package org.example.interviewtemplate

import kotlinx.serialization.Serializable
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait

@Component
class SpaceRoverHandler(private val service: SpaceRoverService) {
    private val logger = logger()

    suspend fun move(request: ServerRequest): ServerResponse {
        logger.info("request: api/rover/dir")
        val dir = request.pathVariableOrNull("dir")
            ?: return ServerResponse.badRequest().buildAndAwait()
        logger.info("-- after dir --")
        val response = service.move(Action.valueOf(dir))
        return ServerResponse.ok().bodyValueAndAwait(response)
    }

    suspend fun getPosition(request: ServerRequest): ServerResponse {
        return ServerResponse.ok().bodyValueAndAwait(service.getPosition())
    }
}

@Serializable
enum class Action {
    FORWARD,
    BACK,

    // Actions without movement.
    RIGHT,
    LEFT
}

@Serializable
data class Response(val position: Position, val direction: Direction)

@Service
class SpaceRoverService {
    private var dir = Direction.RIGHT
    private var curPos = Position(0, 0)

    fun move(action: Action): Response {
        when (action) {
            Action.FORWARD, Action.BACK -> movePos(action)
            Action.RIGHT -> {
                when (dir) {
                    Direction.UP -> dir = Direction.RIGHT
                    Direction.DOWN -> dir = Direction.LEFT
                    Direction.RIGHT -> dir = Direction.DOWN
                    Direction.LEFT -> dir = Direction.UP
                }
            }

            Action.LEFT -> {
                when (dir) {
                    Direction.UP -> dir = Direction.LEFT
                    Direction.DOWN -> dir = Direction.RIGHT
                    Direction.RIGHT -> dir = Direction.UP
                    Direction.LEFT -> dir = Direction.DOWN
                }
            }
        }
        return Response(curPos, dir)
    }

    private fun movePos(action: Action) {
        val step = if (action == Action.FORWARD) 1 else -1
        curPos = when (dir) {
            Direction.UP -> curPos.copy(y = curPos.y - step)
            Direction.DOWN -> curPos.copy(y = curPos.y + step)
            Direction.RIGHT -> curPos.copy(x = curPos.x + step)
            Direction.LEFT -> curPos.copy(x = curPos.x - step)
        }
    }

    fun getPosition(): Response {
        return Response(curPos, dir)
    }
}

@Serializable
data class Position(val x: Int, val y: Int)

enum class Direction {
    UP,
    DOWN,
    RIGHT,
    LEFT
}