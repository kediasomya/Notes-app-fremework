package com.example.spring_kotlin_final_demo.database.repository

import com.example.spring_kotlin_final_demo.database.model.User
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface UserRepository: MongoRepository<User , ObjectId> {
    fun findByEmail(email:String):User?

  //  fun deleteByUserId(userId: ObjectId)

}
