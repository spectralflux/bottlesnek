package com.meadsteve.bottlesnek

import com.meadsteve.bottlesnek.space.*
import io.javalin.Javalin
import org.slf4j.LoggerFactory


val logger = LoggerFactory.getLogger("bottlesnek")

fun main(_args: Array<String>) {
    val app = Javalin.create().start(getHerokuAssignedPort())
    val snakeConfig = object {
        val color = "#e34234"
        val headType = "silly"
        val tailType = "pixel"
    }

    app.get("/") { ctx -> ctx.result("Hello snek") }
    app.get("/ping") { ctx -> ctx.result("pong") }

    app.post("/start") { ctx -> ctx.json(snakeConfig)}
    app.post("/end") { ctx -> ctx.json("ok")}

    app.post("/move"){ ctx ->
        val game = ctx.body<Game>()
        logger.info("Got a board to make a move on. $game", game)
        ctx.json(object{val move = idealMove(game).value})
    }

    logger.info("snnnnnn")
}

fun idealMove(game: Game): Direction {
    logger.info("Trying to find ideal move")
    if(game.board.food.isEmpty()) {
        logger.info("Empty board so I'm moving at random")
        return randomDirection()
    }
    val firstPieceOfFood = game.board.food.first()
    val heading =
        findHeading(from = game.you.head, to = firstPieceOfFood)
    logger.info("Heading $heading to the food")
    return heading
}

fun getHerokuAssignedPort(): Int {
    val herokuPort = System.getenv("PORT")
    return herokuPort?.toInt() ?: 7000
}

data class BodyPiece(override val x: Int, override val y: Int): Square
data class Food(override val x: Int, override val y: Int): Square

data class Snake(val id: String, val name: String, val health: String, val body: List<BodyPiece>, val shout: String) {
    val head: BodyPiece
        get() = this.body.last()
}

data class Board(val height: Int, val width: Int, val food: List<Food>, val snakes: List<Snake>)

data class Game(val game: Any?, val turn: Int, val board: Board, val you: Snake)