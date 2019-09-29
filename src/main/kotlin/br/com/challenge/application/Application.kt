package br.com.challenge.application

import br.com.challenge.application.city.CityController
import br.com.challenge.application.city.cityModules
import br.com.challenge.application.city.cityRoutes
import br.com.challenge.application.config.configModules
import br.com.challenge.application.config.objectMapper
import br.com.challenge.application.health.HealthController
import br.com.challenge.application.health.health
import br.com.challenge.application.health.healthModules
import br.com.challenge.application.playlist.playlistModules
import br.com.challenge.infrastructure.gateway.exceptions.GatewayException
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.jackson.JacksonConverter
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject

fun Application.main() {

    install(Koin) {
        modules(listOf(healthModules, cityModules, configModules, playlistModules))
    }

    install(StatusPages) {
        exception(GatewayException::class.java) { call.respond(it.statusCode(), it.response()) }
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, JacksonConverter(objectMapper))
    }

    val healthController by inject<HealthController>()
    val cityController by inject<CityController>()

    routing {
        health(healthController)
        cityRoutes(cityController)
    }
}

fun main(args: Array<String>) {
    embeddedServer(Netty, 8080) {
        main()
    }.start(wait = true)
}

