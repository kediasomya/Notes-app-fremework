package com.example.spring_kotlin_final_demo.kafka

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class KafkaDLQConsumer {

    @KafkaListener(topics = ["note-created-dlt"])
    fun consumeDLQ(message: String) {

        println("⚠️ Message moved to DLQ:")
        println(message)
    }
}