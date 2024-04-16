package org.example.interviewtemplate

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ServerResponseTest(
    @Autowired
    private val webClient: WebClient,
    @LocalServerPort
    private val port: Int
) {

    private val baseApiUrl = defaultUrl(port)

    @Test
    fun testServerResponseRouter(): Unit = runBlocking {
        webClient.get()
            .uri("$baseApiUrl/response")
            .accept(MediaType.APPLICATION_JSON)
            .awaitExchange {
                // Incoming Json:
                // actual: [{"userId":1,"name":"name"}]
                // expected: [{"user_id":1,"name":"name"}]
                println(it.awaitBody<String>())
                // val body = it.awaitBody<List<SomeBody>>() <-- will throw unknown key userId
                // val body = it.awaitEntityList<SomeBody>() <-- will throw unknown key userId
                // println(body)
            }
    }
}