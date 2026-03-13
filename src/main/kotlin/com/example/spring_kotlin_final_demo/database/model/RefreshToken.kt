package com.example.spring_kotlin_final_demo.database.model

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant


@Document
data class RefreshToken(
    val userId : ObjectId,
    @Indexed(expireAfter = "0s")
    val expiresAt : Instant,
    val hashedToken :String,
    val createdAt: Instant = Instant.now()

)
