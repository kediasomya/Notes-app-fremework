package com.example.spring_kotlin_final_demo.database.model

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
//import kotlin.time.Instant
import java.time.Instant


data class Note(
    val title :String ,
    val content: String,
    val color: Long,
   // @Kotilin.time.ExperimentalTime

    //@kotlin.time.ExperimentalTime
    //val Instant = Clock.now()
    val createdAt :Instant ,
    @Indexed
    val ownerId : ObjectId,

    @Id
    val id : ObjectId = ObjectId.get()
)
