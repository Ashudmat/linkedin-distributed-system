package com.codingshuttle.linkedin.post_service.service.impl;

import com.codingshuttle.linkedin.post_service.auth.AuthContextHolder;
import com.codingshuttle.linkedin.post_service.client.ConnectionServiceClient;
import com.codingshuttle.linkedin.post_service.client.UploaderServiceClient;
import com.codingshuttle.linkedin.post_service.dto.*;
import com.codingshuttle.linkedin.post_service.entity.Comment;
import com.codingshuttle.linkedin.post_service.entity.Post;
import com.codingshuttle.linkedin.post_service.event.PostCommented;
import com.codingshuttle.linkedin.post_service.event.PostCreated;
import com.codingshuttle.linkedin.post_service.exceptions.ResourceNotFoundException;
import com.codingshuttle.linkedin.post_service.repository.CommentRepository;
import com.codingshuttle.linkedin.post_service.repository.PostRepository;
import com.codingshuttle.linkedin.post_service.service.PostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper modelMapper;
    private final ConnectionServiceClient connectionServiceClient;
    private final KafkaTemplate<String, PostCreated> postCreatedKafkaTemplate;
    private final UploaderServiceClient uploaderServiceClient;
    private final CommentRepository commentRepository;
    private final KafkaTemplate<String, PostCommented> postCommentedKafkaTemplate;

    @Override
    public PostResponseDto createPost(PostRequestDto postRequestDto, MultipartFile file) {
        Long userId = AuthContextHolder.getCurrrentUserId();
        String uploadedUrl = null;
        if(file != null && !file.isEmpty()){
            uploadedUrl = uploaderServiceClient.uploadFile(file).getBody();
        }
        Post post = modelMapper.map(postRequestDto, Post.class);
        post.setUserId(userId);
        post.setMediaUrl(uploadedUrl);
        Post savedPost = postRepository.save(post);
        List<PersonDto> personDtos =
                connectionServiceClient.getFirstDegreeConnections(userId);
        for(PersonDto personDto : personDtos){
            PostCreated postCreated = PostCreated.builder()
                    .postId(savedPost.getId())
                    .authorId(userId)
                    .receiverUserId(personDto.getId())
                    .content(savedPost.getContent())
                    .build();
            postCreatedKafkaTemplate.send(
                    "post_created_topic",
                    postCreated
            );
        }
        PostResponseDto dto = modelMapper.map(savedPost, PostResponseDto.class);
        dto.setOwnPost(true);
        dto.setAuthorName("You");
        return dto;
    }

    @Override
    public PostResponseDto getPostById(Long postId) {
        //Long userId = AuthContextHolder.getCurrrentUserId();
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new ResourceNotFoundException("Post not found with id: " + postId));
        return modelMapper.map(post, PostResponseDto.class);
    }

    @Override
    public List<PostResponseDto> getAllPostsOfUser(Long userId) {
        List<Post> postList = postRepository.findByUserId(userId);
        return postList.stream()
                .map(post -> modelMapper.map(post, PostResponseDto.class))
                .toList();
    }

    @Override
    public List<PostResponseDto> getFeed() {
        Long currentUserId = AuthContextHolder.getCurrrentUserId();
        List<PersonDto> connections = connectionServiceClient.getFirstDegreeConnections(currentUserId);
        List<Long> userIds = connections.stream()
                .map(PersonDto::getId)
                .collect(Collectors.toList());
        userIds.add(currentUserId);
        List<Post> feedPosts = postRepository.findByUserIdInOrderByCreatedAtDesc(userIds);
        return feedPosts.stream()
                .map(post -> {
                    PostResponseDto dto = modelMapper.map(post, PostResponseDto.class);
                    boolean isOwnPost = post.getUserId().equals(currentUserId);
                    dto.setOwnPost(isOwnPost);
                    if(isOwnPost){
                        dto.setAuthorName("You");
                    }
                    else{
                        connections.stream()
                                .filter(person -> person.getId().equals(post.getUserId()))
                                .findFirst()
                                .ifPresent(person -> dto.setAuthorName(person.getName()));
                    }
                    return dto;
                }).toList();
    }

    @Override
    public CommentResponseDto addComment(Long postId, CommentRequestDto requestDto) {

        Long currentUserId = AuthContextHolder.getCurrrentUserId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if(requestDto.getContent() == null || requestDto.getContent().trim().isEmpty()){
            throw new RuntimeException("Comment content cannot be empty");
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

        if(!currentUserId.equals(post.getUserId())){
            postCommentedKafkaTemplate.send("post_commented_topic", event);
        }

        return modelMapper.map(savedComment, CommentResponseDto.class);
    }
    @Override
    public List<CommentResponseDto> getComments(Long postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(comment -> modelMapper.map(comment, CommentResponseDto.class))
                .toList();
    }

}
