package net.jeikobu.doraipitto.model

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import reactor.core.publisher.Mono

interface SlashCommand {
    val name: String

    fun handle(event: ChatInputInteractionEvent): Mono<Void>
}
