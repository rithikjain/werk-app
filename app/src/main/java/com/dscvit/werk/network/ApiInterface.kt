package com.dscvit.werk.network

import com.dscvit.werk.models.auth.*
import com.dscvit.werk.models.chat.ChatResponse
import com.dscvit.werk.models.participants.AssignRequest
import com.dscvit.werk.models.participants.ParticipantsResponse
import com.dscvit.werk.models.sessions.*
import com.dscvit.werk.models.task.CreateTaskRequest
import com.dscvit.werk.models.task.Task
import com.dscvit.werk.models.task.TaskRequest
import com.dscvit.werk.models.task.TaskResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiInterface {
    @POST("/auth/register")
    suspend fun signUpUser(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>

    @POST("/auth/login")
    suspend fun signInUser(@Body signInRequest: SignInRequest): Response<SignInResponse>

    @GET("/session")
    suspend fun getSessions(): Response<SessionsResponse>

    @POST("/session/create")
    suspend fun createSession(@Body createSessionRequest: CreateSessionRequest): Response<CreateSessionResponse>

    @POST("/session/join")
    suspend fun joinSession(@Body joinSessionRequest: JoinSessionRequest): Response<JoinSessionResponse>

    @POST("/auth/verifyEmail")
    suspend fun sendVerificationEmail(@Body sendVerificationRequest: SendVerificationRequest): Response<Any>

    @GET("/task/session/{sessionID}")
    suspend fun getTasks(@Path("sessionID") sessionID: Int): Response<TaskResponse>

    @GET("/session/{sessionID}/participants")
    suspend fun getParticipants(@Path("sessionID") sessionID: Int): Response<ParticipantsResponse>

    @POST("/task/create")
    suspend fun createTask(@Body createTaskRequest: CreateTaskRequest): Response<Task>

    @POST("/task/{taskID}/assign")
    suspend fun assignTask(
        @Body assignRequest: AssignRequest,
        @Path("taskID") taskID: Int
    ): Response<Any>

    @GET("/chats/{sessionID}")
    suspend fun getChats(@Path("sessionID") sessionID: Int): Response<ChatResponse>

    @POST("/task/{taskID}/terminate")
    suspend fun terminateTask(@Path("taskID") taskID: Int): Response<Any>
}