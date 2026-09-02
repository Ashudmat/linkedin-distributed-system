package com.codingshuttle.linkedin.post_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic postCreated(){
        return new NewTopic("post_created_topic", 3, (short) 1);
    }

    @Bean
    public NewTopic postLiked(){
        return new NewTopic("post_liked_topic", 3, (short) 1);
    }

    @Bean
    public NewTopic postCommented(){
        return new NewTopic("post_commented_topic", 3, (short) 1);
    }

    @Bean
    public NewTopic postReposted(){
        return new NewTopic("post_reposted_topic", 3, (short) 1);
    }
}