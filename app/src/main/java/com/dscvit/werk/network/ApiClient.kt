package com.dscvit.werk.network

import com.dscvit.werk.models.auth.SendVerificationRequest
import com.dscvit.werk.models.auth.SignInRequest
import com.dscvit.werk.models.auth.SignUpRequest
import com.dscvit.werk.models.participants.AssignRequest
import com.dscvit.werk.models.sessions.CreateSessionRequest
import com.dscvit.werk.models.sessions.JoinSessionRequest
import com.dscvit.werk.models.task.CreateTaskRequest
import com.dscvit.werk.models.task.TaskRequest
import javax.inject.Inject

class ApiClient @Inject constructor(
    private val api: ApiInterface
) : BaseApiClient() {

    suspend fun signUpUser(signUpRequest: SignUpRequest) = processResponse {
        api.signUpUser(signUpRequest)
    }

    suspend fun signInUser(signInRequest: SignInRequest) = processResponse {
        api.signInUser(signInRequest)
    }

    suspend fun getSessions() = processResponse {
        api.getSessions()
    }

    suspend fun createSession(createSessionRequest: CreateSessionRequest) = processResponse {
        api.createSession(createSessionRequest)
    }

    suspend fun joinSession(joinSessionRequest: JoinSessionRequest) = processResponse {
        api.joinSession(joinSessionRequest)
    }

    suspend fun sendVerificationEmail(sendVerificationRequest: SendVerificationRequest) =
        processResponse {
            api.sendVerificationEmail(sendVerificationRequest)
        }

    suspend fun getTasks(sessionID: Int) = processResponse { api.getTasks(sessionID) }

    suspend fun getParticipants(sessionID: Int) = processResponse { api.getParticipants(sessionID) }

    suspend fun createTask(createTaskRequest: CreateTaskRequest) = processResponse {
        api.createTask(createTaskRequest)
    }

    suspend fun assignTask(assignRequest: AssignRequest, taskID: Int) = processResponse {
        api.assignTask(assignRequest, taskID)
    }

}