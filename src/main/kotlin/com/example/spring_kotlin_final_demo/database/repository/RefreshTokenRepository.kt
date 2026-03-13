package com.example.spring_kotlin_final_demo.database.repository

import com.example.spring_kotlin_final_demo.database.model.RefreshToken
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface RefreshTokenRepository: MongoRepository <RefreshToken, ObjectId>{

    //find a token with user id and tooken combination
    //have attached a alresy refresh token to be verified

    fun findByUserIdAndHashedToken(userId: ObjectId , hashedToken : String ):RefreshToken?

    //if a user re loggs in , before the refresh token expires
    //we need to make sure that the old token is deleted
    fun deleteByUserIdAndHashedToken(userId: ObjectId , hashedToken: String)

   // fun deleteByUserId(userId: ObjectId)



}