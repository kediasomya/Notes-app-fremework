package com.example.spring_kotlin_final_demo.kafka.event

data class NoteCreatedEvent(
    val noteId: String,
    val ownerId: String,
    val title: String,
    val createdAt: String
)