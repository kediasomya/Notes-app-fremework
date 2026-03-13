package com.example.spring_kotlin_final_demo.database.repository

import com.example.spring_kotlin_final_demo.database.model.Note
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface NoteRepository: MongoRepository<Note, ObjectId> {

    fun findByOwnerId(ownerId: ObjectId): List<Note>
 //  fun findById(Id: ObjectId): Note

}
//    fun temp(repository: NoteRepository)
//    {
//        repository.
//    }
