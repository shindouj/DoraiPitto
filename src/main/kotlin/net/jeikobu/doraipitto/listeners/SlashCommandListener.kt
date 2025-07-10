package net.jeikobu.doraipitto.listeners

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import net.jeikobu.doraipitto.model.EventListener
import net.jeikobu.doraipitto.model.SlashCommand
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class SlashCommandListener(
    private val commands: List<SlashCommand>,
) : EventListener<ChatInputInteractionEvent> {
    override fun getEventType(): Class<ChatInputInteractionEvent> = ChatInputInteractionEvent::class.java

    override fun execute(event: ChatInputInteractionEvent): Mono<Void> =
        Flux
            .fromIterable(commands)
            .filter { it.name == event.commandName }
            .next()
            .flatMap { it.handle(event) }
}
