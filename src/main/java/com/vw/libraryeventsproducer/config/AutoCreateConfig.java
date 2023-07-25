package com.vw.libraryeventsproducer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Component;

@Component
public class AutoCreateConfig {


    @Value("${spring.kafka.topic}")
    private String topic;

    @Bean
    public NewTopic libraryEvents(){

        return TopicBuilder.name(topic)
                .partitions(3)
                .replicas(3)
                .build();
    }
}
