package net.jeikobu.doraipitto.config

import discord4j.core.DiscordClientBuilder
import discord4j.core.event.domain.Event
import net.jeikobu.doraipitto.model.EventListener
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DiscordConfiguration {
    @Value("\${doraipitto.discord.token}")
    private lateinit var token: String

    @Bean
    fun <T : Event> discordClient(eventListeners: List<EventListener<T>>) =
        DiscordClientBuilder.create(token).build().gateway().login().block().also { client ->
            for (listener in eventListeners) {
                client
                    ?.on(listener.getEventType(), listener::execute)
                    ?.onErrorResume(listener::handleError)
                    ?.subscribe()
            }
        }
}
