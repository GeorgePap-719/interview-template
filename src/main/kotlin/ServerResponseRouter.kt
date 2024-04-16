package org.example.interviewtemplate

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class ServerResponseRouter(private val handler: ServerResponseHandler) {

    @Bean
    fun testRouter() = coRouter {
        accept(MediaType.APPLICATION_JSON).nest {
            GET("api/response", handler::returnBody)
        }
    }
}

@Component
class ServerResponseHandler {

    suspend fun returnBody(request: ServerRequest): ServerResponse {
        return ServerResponse
            .ok()
            // Note: `fun ServerResponse.BodyBuilder.bodyValueAndAwait(body: Any)`
            // is not a reified function and is subject to type erasure.
            // In cases where response is a collection, the object is serialized as `Any`,
            // and the response does not have the proper format, which is unintended.
            .bodyValueAndAwait(listOf(SomeBody(1, "name")))
    }
}

@Serializable
data class SomeBody(@SerialName("user_id") val userId: Int, val name: String)