package kg.automoika.repository

import io.ktor.server.application.*
import kg.automoika.data.body.*
import kg.automoika.data.remote.ReviewSenderModel
import kg.automoika.data.response.LoginResponse
import kg.automoika.data.response.ReviewResponse
import kg.automoika.data.response.UserResponse

interface UserRepository {

    suspend fun loginUser(body : LoginBody, call: ApplicationCall) : UserResponse?
    suspend fun createUser(body : UserBody) : UserResponse?
    suspend fun getUserById(id : String?) : UserResponse?
    suspend fun sendReview(body : ReviewBody) : ReviewSenderModel?
    suspend fun getCarWashReviews(id : String?) : List<ReviewResponse>
}