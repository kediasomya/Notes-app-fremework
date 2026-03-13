package com.example.spring_kotlin_final_demo.security
import com.example.spring_kotlin_final_demo.database.model.RefreshToken
import com.example.spring_kotlin_final_demo.database.model.User
import com.example.spring_kotlin_final_demo.database.repository.RefreshTokenRepository
import com.example.spring_kotlin_final_demo.database.repository.UserRepository
import org.bson.types.ObjectId
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.*

@Service
class AuthService (
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
){



    data class TokenPair(
        val accessToken: String,
        val refreshToken:String
    )



    fun register(email: String, password: String): User {

        val existingUser = userRepository.findByEmail(email)

        if (existingUser != null) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "Email already registered"
            )
        }

        return userRepository.save(
            User(
                email = email,
                hashedPassword = hashEncoder.encode(password)
            )
        )
    }


    fun login(email:String ,password:String ):TokenPair{
        val user = userRepository.findByEmail(email)
            ?:throw BadCredentialsException("invalid credentials")

            //if email exists , checking password
            if(!hashEncoder.matches(password,user.hashedPassword)){
                throw BadCredentialsException("invalid credentials")

            }
        //valid creadentials , generate token

        val newAccessToken = jwtService.generateAccessToken(user.id.toHexString())
        val newRefreshToken = jwtService.generateRefreshToken(user.id.toHexString())

        storeRefreshToken(user.id ,newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )

        //we new dto save our refresh token in the database so that when
        //the access token expires , user can reqister a new access token
        //through the refresh token , and we can validate the refresh token is correct or not
    }



    @Transactional
    fun refresh(refreshToken: String):TokenPair{

        if(!jwtService.validateRefreshToken(refreshToken)){
            throw ResponseStatusException(HttpStatusCode.valueOf(401),"Invalid refresh token")

        }
        val userId = jwtService.getUserIdFromToken(refreshToken)
        val user = userRepository.findById(ObjectId(userId)).orElseThrow {
            ResponseStatusException(HttpStatusCode.valueOf(401),"Invalid refresh token")        }

        val hashed = hashToken(refreshToken)
        refreshTokenRepository.findByUserIdAndHashedToken(user.id , hashed)
            ?:throw ResponseStatusException(HttpStatusCode.valueOf(401),
                "Refresh token not authorized(used or expired)")
        //deleting the old token
        refreshTokenRepository.deleteByUserIdAndHashedToken(user.id , hashed)

        val newAccessToken = jwtService.generateAccessToken(userId)
        val newRefreshToken = jwtService.generateRefreshToken(userId)

        //what if the old is deleted and new id not stored
        //we need to set up transaction ..ie do it all or nothing- @transactional
        //storing the new token
        storeRefreshToken(user.id , newRefreshToken)

        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )

    }



//    fun deleteUser(userId: String) {
//
//        val objectId = ObjectId(userId)
//
//        val user = userRepository.findById(objectId)
//            .orElseThrow {
//                ResponseStatusException(
//                    HttpStatus.NOT_FOUND,
//                    "User not found"
//                )
//            }
//
//        // delete refresh tokens first
//        refreshTokenRepository.deleteByUserId(objectId)
//
//        // delete user
//        userRepository.delete(user)
//    }



    private fun storeRefreshToken(userId: ObjectId, rawRefreshToken: String) {
        val hashed = hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)

        refreshTokenRepository.save(
            RefreshToken(
                userId = userId,
                expiresAt = expiresAt,
                hashedToken = hashed
            )
        )
    }



    private fun hashToken(token:String):String{

        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }
}

