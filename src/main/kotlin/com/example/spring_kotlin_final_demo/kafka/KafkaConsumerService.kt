package com.example.spring_kotlin_final_demo.kafka


import com.example.spring_kotlin_final_demo.kafka.event.NoteCreatedEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.kafka.annotation.BackOff
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.stereotype.Service
import org.springframework.retry.annotation.Backoff

//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event

@Service
class KafkaConsumerService(
    private val objectMapper: ObjectMapper
) {

//    @KafkaListener(topics = ["note-created"], groupId = "notes-group")
//    fun consume(message: String) {
//
//        println("Kafka event received: $message")
//    }

//    @KafkaListener(topics = ["note-created"], groupId = "notes-group")
//    fun consume(event: NoteCreatedEvent) {
//
//        println("Kafka event received:")
//        println("NoteId: ${event.noteId}")
//        println("Title: ${event.title}")
//    }



    @RetryableTopic(
        attempts = "3",
        backOff = BackOff(delay = 2000 , multiplier = 2.0),
//        backoffDelay = 2000,

        dltTopicSuffix = "-dlt"
    )
    @KafkaListener(topics = ["note-created"], groupId = "notes-group")
    fun consume(message: String) {

        val event = objectMapper.readValue(message, NoteCreatedEvent::class.java)
        println("Kafka Event Received:")
        println(event)

        if (event.title.contains("fail")) {
            println("Simulated failure triggered")
            throw RuntimeException("Consumer failed")
        }
        println("Message processed successfully")
    }

    @DltHandler
    fun handleDLT(message: String) {

        try {
            val event = objectMapper.readValue(message, NoteCreatedEvent::class.java)
            println("message moved to dlt topic")
            println(event)
        } catch (e: Exception) {
            println("invalid message format : $message")
        }
    }




}