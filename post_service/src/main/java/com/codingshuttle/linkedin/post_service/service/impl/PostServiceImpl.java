package com.codingshuttle.linkedin.post_service.service.impl;
import com.codingshuttle.linkedin.post_service.auth.AuthContextHolder;
import com.codingshuttle.linkedin.post_service.client.ConnectionServiceClient;
import com.codingshuttle.linkedin.post_service.client.UploaderServiceClient;
import com.codingshuttle.linkedin.post_service.dto.*;
import com.codingshuttle.linkedin.post_service.entity.Comment;
import com.codingshuttle.linkedin.post_service.entity.Post;
import com.codingshuttle.linkedin.post_service.event.PostCommented;
import com.codingshuttle.linkedin.post_service.event.PostCreated;
import com.codingshuttle.linkedin.post_service.event.PostReposted;
import com.codingshuttle.linkedin.post_service.exceptions.BadRequestException;
import com.codingshuttle.linkedin.post_service.exceptions.ResourceNotFoundException;
import com.codingshuttle.linkedin.post_service.repository.CommentRepository;
import com.codingshuttle.linkedin.post_service.repository.PostLikeRepository;
import com.codingshuttle.linkedin.post_service.repository.PostRepository;
import com.codingshuttle.linkedin.post_service.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final ConnectionServiceClient connectionServiceClient;
    private final KafkaTemplate<String, PostCreated> postCreatedKafkaTemplate;
    private final UploaderServiceClient uploaderServiceClient;
    private final CommentRepository commentRepository;
    private final KafkaTemplate<String, PostCommented> postCommentedKafkaTemplate;
    private final PostLikeRepository postLikeRepository;
    private final KafkaTemplate<String, PostReposted> postRepostedKafkaTemplate;

    @Override
    public PostResponseDto createPost(PostRequestDto postRequestDto) {
        log.info("Testing 1");
        Long userId = AuthContextHolder.getCurrrentUserId();
        String uploadedUrl = null;
        if (postRequestDto.getFile() != null && !postRequestDto.getFile().isEmpty()) {
            uploadedUrl = uploaderServiceClient
                    .uploadFile(postRequestDto.getFile())
                    .getBody()
                    .getData()
                    .getFileUrl();
        }

        Post post = new Post();
        post.setContent(postRequestDto.getContent());
        post.setUserId(userId);
        post.setMediaUrl(uploadedUrl);

        Post savedPost = postRepository.save(post);

        List<PersonDto> personDtos = connectionServiceClient.getFirstDegreeConnections(userId).getData();
        for (PersonDto personDto : personDtos) {
            PostCreated postCreated = PostCreated.builder()
                    .postId(savedPost.getId())
                    .authorId(userId)
                    .receiverUserId(personDto.getId())
                    .content(savedPost.getContent())
                    .createdAt(savedPost.getCreatedAt())
                    .build();
            postCreatedKafkaTemplate.send("post_created_topic", postCreated);
        }
        PostResponseDto dto = modelMapper.map(savedPost, PostResponseDto.class);
        dto.setOwnPost(true);
        dto.setAuthorName("You");
        return dto;
    }

    @Override
    public PostResponseDto getPostById(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() ->
                        new ResourceNotFoundException("Post not found with id: " + postId));
        return modelMapper.map(post, PostResponseDto.class);
    }

    @Override
    public List<PostResponseDto> getAllPostsOfUser(Long userId) {
        return postRepository.findByUserId(userId)
                .stream()
                .map(post -> modelMapper.map(post, PostResponseDto.class))
                .toList();
    }

    @Override
    public List<PostResponseDto> getFeed(int page, int size){
        Long currentUserId = AuthContextHolder.getCurrrentUserId();
        List<PersonDto> connections = connectionServiceClient.getFirstDegreeConnections(currentUserId).getData();
        List<Long> userIds = connections.stream().map(PersonDto::getId).collect(Collectors.toList());
        userIds.add(currentUserId);
        Pageable pageable = PageRequest.of(page, size);
        List<Post> feedPosts = postRepository.findByUserIdInOrderByCreatedAtDesc(userIds, pageable).getContent();
        return feedPosts.stream()
                .map(post -> {
                    PostResponseDto dto = modelMapper.map(post, PostResponseDto.class);
                    boolean isOwnPost = post.getUserId().equals(currentUserId);
                    dto.setOwnPost(isOwnPost);
                    dto.setLikeCount(postLikeRepository.countByPostId(post.getId()));
                    dto.setLiked(postLikeRepository.existsByuserIdAndPostId(currentUserId, post.getId()));
                    if (isOwnPost) {
                        dto.setAuthorName("You");
                        PersonDto currentUser = connectionServiceClient.getUserById(currentUserId).getData();
                        dto.setAuthorProfileImageUrl(currentUser.getProfileImageUrl());
                    } else {
                        PersonDto author = connectionServiceClient.getUserById(post.getUserId()).getData();
                        dto.setAuthorName(author.getName());
                        dto.setAuthorProfileImageUrl(author.getProfileImageUrl());
                    }
                    if (post.getRepostedPostId() != null) {
                        Post originalPost = postRepository.findById(post.getRepostedPostId()).orElse(null);
                        if (originalPost != null) {
                            dto.setOriginalContent(originalPost.getContent());
                            dto.setOriginalMediaUrl(originalPost.getMediaUrl());
                            if (originalPost.getUserId().equals(currentUserId)) {
                                dto.setOriginalAuthorName("You");
                            } else {
                                connections.stream()
                                        .filter(person -> person.getId().equals(originalPost.getUserId()))
                                        .findFirst()
                                        .ifPresent(person -> dto.setOriginalAuthorName(person.getName()));
                            }
                        } else {
                            dto.setOriginalAuthorName("Original Post");
                            dto.setOriginalContent("This original post is no longer available");
                            dto.setOriginalMediaUrl(null);
                        }
                    }
                    return dto;
                })
                .toList();
    }

    @Override
    public List<PostResponseDto> getUserPosts(Long userId) {

        Long currentUserId = AuthContextHolder.getCurrrentUserId();

        List<Post> posts = postRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return posts.stream()
                .map(post -> {
                    PostResponseDto dto = new PostResponseDto();

                    dto.setId(post.getId());
                    dto.setContent(post.getContent());
                    dto.setMediaUrl(post.getMediaUrl());
                    dto.setCreatedAt(post.getCreatedAt());
                    dto.setUserId(post.getUserId());
                    dto.setRepostedPostId(post.getRepostedPostId());

                    dto.setOwnPost(post.getUserId().equals(currentUserId));

                    dto.setLikeCount(postLikeRepository.countByPostId(post.getId()));
                    dto.setLiked(postLikeRepository.existsByuserIdAndPostId(currentUserId, post.getId()));

                    PersonDto author = connectionServiceClient.getUserById(post.getUserId()).getData();

                    dto.setAuthorName(post.getUserId().equals(currentUserId) ? "You" : author.getName());

                    dto.setAuthorProfileImageUrl(author.getProfileImageUrl());

                    // Repost details
                    if (post.getRepostedPostId() != null) {

                        Post originalPost = postRepository.findById(post.getRepostedPostId()).orElse(null);

                        if (originalPost != null) {

                            dto.setOriginalContent(originalPost.getContent());
                            dto.setOriginalMediaUrl(originalPost.getMediaUrl());

                            if (originalPost.getUserId().equals(currentUserId)) {
                                dto.setOriginalAuthorName("You");
                            } else {

                                PersonDto originalAuthor = connectionServiceClient.getUserById(originalPost.getUserId()).getData();
                                dto.setOriginalAuthorName(originalAuthor.getName());
                            }

                        } else {

                            dto.setOriginalAuthorName("Original Post");
                            dto.setOriginalContent("This original post is no longer available");
                            dto.setOriginalMediaUrl(null);
                        }
                    }

                    return dto;
                })
                .toList();
    }

    @Override
    public CommentResponseDto addComment(Long postId, CommentRequestDto requestDto) {
        Long currentUserId = AuthContextHolder.getCurrrentUserId();
        List<PersonDto> connections = connectionServiceClient.getFirstDegreeConnections(currentUserId).getData();
        Post post = postRepository.findById(postId).orElseThrow(() ->
                        new ResourceNotFoundException("Post not found"));
        if (requestDto.getContent() == null || requestDto.getContent().trim().isEmpty()) {
            throw new BadRequestException("Comment content cannot be empty");
        }
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(currentUserId);
        comment.setContent(requestDto.getContent());
        Comment savedComment = commentRepository.save(comment);
        PostCommented event = PostCommented.builder()
                .postId(postId)
                .commenterUserId(currentUserId)
                .ownerUserId(post.getUserId())
                .comment(savedComment.getContent())
                .build();
        if (!currentUserId.equals(post.getUserId())) {
            postCommentedKafkaTemplate.send("post_commented_topic", event);
        }
        CommentResponseDto dto = modelMapper.map(savedComment, CommentResponseDto.class);
        dto.setAuthorName("You");
        connections.stream()
                .filter(person ->
                        person.getId().equals(currentUserId))
                        .findFirst()
                        .ifPresent(person ->
                        dto.setAuthorProfileImageUrl(
                                person.getProfileImageUrl()
                        ));
        return dto;
    }

    @Override
    public List<CommentResponseDto> getComments(Long postId) {
        Long currentUserId = AuthContextHolder.getCurrrentUserId();
        postRepository.findById(postId).orElseThrow(() ->
                        new ResourceNotFoundException("Post not found with id: " + postId));
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(comment -> {
                    CommentResponseDto dto = modelMapper.map(comment, CommentResponseDto.class);
                    try {
                        PersonDto author = connectionServiceClient.getUserById(comment.getUserId()).getData();
                        if (comment.getUserId().equals(currentUserId)) {
                            dto.setAuthorName("You");
                        } else {
                            dto.setAuthorName(author.getName());
                        }
                        dto.setAuthorProfileImageUrl(author.getProfileImageUrl());

                    } catch (Exception e) {
                        dto.setAuthorName(comment.getUserId().equals(currentUserId) ? "You" : "Unknown User");
                    }
                    return dto;
                })
                .toList();
    }

    @Override
    public PostResponseDto updatePost(Long postId, UpdatePostRequestDto updatePostRequestDto) {
        Long currentUserId = AuthContextHolder.getCurrrentUserId();
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getUserId().equals(currentUserId)) {throw new BadRequestException("You can only edit your own posts");}

        post.setContent(updatePostRequestDto.getContent());
        Post updatedPost = postRepository.save(post);
        PostResponseDto dto = modelMapper.map(updatedPost, PostResponseDto.class);
        dto.setOwnPost(true);
        dto.setAuthorName("You");
        dto.setLikeCount(postLikeRepository.countByPostId(updatedPost.getId()));
        dto.setLiked(postLikeRepository.existsByuserIdAndPostId(currentUserId, updatedPost.getId()));
        return dto;
    }

    @Override
    public void deletePost(Long postId) {
        Long currentUserId = AuthContextHolder.getCurrrentUserId();
        Post post = postRepository.findById(postId).orElseThrow(() ->
                        new ResourceNotFoundException("Post not found"));
        if (!post.getUserId().equals(currentUserId)) {throw new BadRequestException(
                "You can only delete your own posts");
        }
        postRepository.delete(post);
    }

    @Override
    public PostResponseDto repostPost(Long postId, RepostRequestDto repostRequestDto) {

        Long currentUserId = AuthContextHolder.getCurrrentUserId();
        Post originalPost = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        String uploadedUrl = null;

        if (repostRequestDto.getFile() != null && !repostRequestDto.getFile().isEmpty()) {
            uploadedUrl = uploaderServiceClient
                    .uploadFile(repostRequestDto.getFile())
                    .getBody()
                    .getData()
                    .getFileUrl();
        }
        Post repost = new Post();
        repost.setUserId(currentUserId);
        repost.setContent(repostRequestDto.getContent() != null ? repostRequestDto.getContent().trim() : "");
        repost.setMediaUrl(uploadedUrl);
        repost.setRepostedPostId(originalPost.getId());

        Post savedRepost = postRepository.save(repost);
        if (!originalPost.getUserId().equals(currentUserId)) {
            PostReposted event = PostReposted.builder()
                            .postId(savedRepost.getId())
                            .ownerUserId(originalPost.getUserId())
                            .repostedByUserId(currentUserId)
                            .build();
            postRepostedKafkaTemplate.send("post_reposted_topic", event);
        }
        PostResponseDto dto = modelMapper.map(savedRepost, PostResponseDto.class);
        dto.setOwnPost(true);
        dto.setAuthorName("You");
        dto.setRepostedPostId(originalPost.getId());
        dto.setOriginalContent(originalPost.getContent());
        dto.setOriginalMediaUrl(originalPost.getMediaUrl());
        if (originalPost.getUserId().equals(currentUserId)) {
            dto.setOriginalAuthorName("You");
        } else {
            PersonDto originalAuthor = connectionServiceClient.getUserById(originalPost.getUserId()).getData();
            dto.setOriginalAuthorName(originalAuthor.getName());
        }
        return dto;
    }
}