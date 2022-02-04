package com.example.botconstructor.api

import com.example.botconstructor.TelegramTemplate
import com.example.botconstructor.model.Bot
import com.example.botconstructor.repositories.BotAnswerRepositories
import com.example.botconstructor.services.AnswerTemplateService
import com.example.botconstructor.services.ClientApi
import com.example.botconstructor.services.LocalServiceRegistry
import com.example.botconstructor.services.Service
import org.springframework.http.MediaType
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.annotation.ConnectMapping
import org.springframework.stereotype.Controller
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Controller
@MessageMapping("bot.api")
class BotController(
        val telegramClient: WebClient,
        val instagramClient: WebClient,
        val clientApi: ClientApi,
        val botAnswerRepositories: BotAnswerRepositories,
        val localServiceRegistry: LocalServiceRegistry
) {

    @ConnectMapping("connect")
    fun connect(@Payload  service: Service) = localServiceRegistry.add(service)


    @MessageMapping("create.telegram")
    fun telegramAuthorize(@Payload template: TelegramTemplate): Mono<String> {
        return telegramClient.get()
                .uri {
                    it.path("bot${template.token}/getMe").build()
                }
                .accept(MediaType.APPLICATION_JSON)
                .exchangeToMono {
                    if (it.statusCode().is2xxSuccessful) {
                        botAnswerRepositories.register(Bot(template.token))
                        return@exchangeToMono bodyExtractTelegram(it)
                    }
                    return@exchangeToMono bodyExtractTelegram(it)
                }
    }

    fun bodyExtractTelegram(clientResponse: ClientResponse): Mono<String> =
            clientResponse.bodyToMono(String::class.java)


}
