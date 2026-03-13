package com.example.spring_kotlin_final_demo.config


import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class KafkaConfig {

    @Bean
    fun noteCreatedTopic(): NewTopic {
        return NewTopic("note-created", 1, 1)
    }
}