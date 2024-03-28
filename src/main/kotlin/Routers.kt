package org.example.interviewtemplate

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class Routers(
    private val userHandler: UserHandler,
    private val spaceRoverHandler: SpaceRoverHandler
) {

    @Bean
    fun userRoutes() = coRouter {
        accept(MediaType.APPLICATION_JSON).nest {
            POST("api/users", userHandler::register)
            GET("api/users/{id}", userHandler::findById)

            //  -- dirs --

            POST("api/rover/{dir}", spaceRoverHandler::move)
            GET("api/rover", spaceRoverHandler::getPosition)
        }
    }
}