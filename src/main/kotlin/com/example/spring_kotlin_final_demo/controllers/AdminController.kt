package com.example.spring_kotlin_final_demo.controllers

import com.example.spring_kotlin_final_demo.controllers.NoteController.NoteResponse
import com.example.spring_kotlin_final_demo.database.model.Note
import com.example.spring_kotlin_final_demo.database.model.User
import com.example.spring_kotlin_final_demo.database.repository.NoteRepository
import com.example.spring_kotlin_final_demo.database.repository.UserRepository
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



@RestController
@RequestMapping("/admin")
class AdminController(
    private val userRepo: UserRepository,
    private val noterepo: NoteRepository
) {



    @GetMapping("/all-users")
    fun getAllUsers(): List<User> {
        val all = userRepo.findAll()  // Spring Data JPA method

        return if (all.isNotEmpty()) {
            all
        } else {
            emptyList()
        }
    }

    @GetMapping("all-notes")
    fun getAllNotes():List<NoteResponse>{
        val all = noterepo.findAll()

        val responseList = all.map { note ->
            NoteResponse(
                id = id.toHexString(),
                title = title,
                content = content,
                color = color,
                createdAt = createdAt

            )
        }

        return if (all.isNotEmpty()) {
            all
        } else {
            emptyList()
        }

    }
}