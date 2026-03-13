package com.example.spring_kotlin_final_demo.controllers

import com.example.spring_kotlin_final_demo.database.model.Note
import com.example.spring_kotlin_final_demo.database.repository.NoteRepository
import com.example.spring_kotlin_final_demo.kafka.KafkaProducerService
import com.example.spring_kotlin_final_demo.kafka.event.NoteCreatedEvent
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

import java.time.Instant
import kotlin.String


@RestController
@RequestMapping("/notes")
class NoteController(
    private val repository: NoteRepository,
    private val kafkaProducerService: KafkaProducerService

) {
    data class NoteRequest(
        val id: String?,
        @field:NotBlank(message = "Title can't be blank.")
        val title: String,
        val content: String,
        val color: Long
    )

    data class NoteResponse(
        val id: String,
        val title: String,
        val content: String,
        val color: Long,
        val createdAt: Instant
    )

    @PostMapping
    fun save(
        @RequestBody @Valid body: NoteRequest
    ): NoteResponse {
        val ownerId = SecurityContextHolder
            .getContext()
            .authentication
            ?.name
            ?: throw IllegalStateException("User not authenticated")
            val note =  repository.save(
            Note(
                id =body.id?.let{ObjectId(it)}?:ObjectId.get(),
                //if id exists , converts the existing object id of it(body.id) to string
                //if it doesn't exist then , gets a new random object id through .get()

                title =body.title ,
                content= body.content,
                createdAt = Instant.now(),
                color = body.color,
                ownerId = ObjectId(ownerId)

        ) )

        val NoteEvent = NoteCreatedEvent(
            noteId = note.id.toHexString(),
            title = note.title,
            ownerId = note.ownerId.toHexString(),
            createdAt = note.createdAt.toString()
        )

        //after saving , kafka call for producing message
        kafkaProducerService.sendNoteCreatedEvent(NoteEvent)


        return note.toResponse()

    }


    @GetMapping
    fun getNoteByOwnerId(): List<NoteResponse>
    {

        val ownerId = SecurityContextHolder
            .getContext()
            .authentication
            ?.name
            ?: throw IllegalStateException("User not authenticated")
            return repository.findByOwnerId(
            ObjectId(ownerId)).map {
            it.toResponse()
        }

    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String) {
         val note = repository.findById(ObjectId(id)).orElseThrow {
             IllegalArgumentException("Note not found")
         }
        val ownerId = SecurityContextHolder
            .getContext()
            .authentication
            ?.name
            ?: throw IllegalStateException("User not authenticated")
        if (note.ownerId.toHexString() != ownerId) {
            throw IllegalStateException("You are not allowed to delete this note")
        }

        repository.deleteById(ObjectId(id))
    }



    private fun Note.toResponse(): NoteController.NoteResponse
    {

        return NoteResponse(
            id = id.toHexString(),
            title = title,
            content = content,
            color = color,
            createdAt = createdAt

        )
    }

}