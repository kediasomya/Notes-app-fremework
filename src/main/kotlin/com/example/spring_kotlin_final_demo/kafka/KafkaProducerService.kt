package com.example.spring_kotlin_final_demo.kafka


import com.example.spring_kotlin_final_demo.database.model.Note
import com.example.spring_kotlin_final_demo.kafka.event.NoteCreatedEvent
import org.springframework.kafka.core.KafkaTemplate
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Service

@Service
class KafkaProducerService(
    private val kafkaTemplate: KafkaTemplate<String,String>,
    private val objectMapper: ObjectMapper

) {

    fun sendNoteCreatedEvent(event: NoteCreatedEvent) {
        val json = objectMapper.writeValueAsString(event)
        kafkaTemplate.send("note-created", json)
    }
}