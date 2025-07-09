package net.jeikobu.doraipitto.listeners

import discord4j.common.util.Snowflake
import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.Message
import discord4j.core.spec.MessageCreateSpec
import discord4j.core.spec.MessageEditSpec
import discord4j.rest.util.AllowedMentions
import io.github.oshai.kotlinlogging.KotlinLogging
import net.jeikobu.doraipitto.model.EventListener
import net.jeikobu.doraipitto.processor.MessageProcessor
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

@Component
class MessageListener(
    private val processor: MessageProcessor,
): EventListener<MessageCreateEvent> {
    override fun getEventType(): Class<MessageCreateEvent> = MessageCreateEvent::class.java

    override fun execute(event: MessageCreateEvent): Mono<Void> {
        val messagesToSend = processor.getMessagesToSend(event.message.content)
        messagesToSend.map { message ->
            event.message.channel.flatMap {
                it.createMessage(MessageCreateSpec.builder()
                    .messageReferenceId(event.message.id)
                    .flags(listOf(Message.Flag.SUPPRESS_NOTIFICATIONS))
                    .allowedMentions(AllowedMentions.builder().build())
                    .content(message)
                    .build())
            }.subscribe()
        }

        if (messagesToSend.isNotEmpty()) {
            event.message.edit().withFlags(Message.Flag.SUPPRESS_EMBEDS).subscribe()
        }

        return Mono.empty()
    }
}