package net.jeikobu.doraipitto.listeners

import discord4j.core.event.domain.message.MessageCreateEvent
import discord4j.core.`object`.entity.Message
import discord4j.core.spec.MessageCreateSpec
import discord4j.rest.util.AllowedMentions
import io.github.oshai.kotlinlogging.KotlinLogging
import net.jeikobu.doraipitto.dao.GuildConfigDao
import net.jeikobu.doraipitto.model.EventListener
import net.jeikobu.doraipitto.processor.MessageProcessor
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import kotlin.jvm.optionals.getOrNull

private val log = KotlinLogging.logger {}

@Component
class MessageListener(
    private val processor: MessageProcessor,
    private val guildConfigDao: GuildConfigDao,
) : EventListener<MessageCreateEvent> {
    override fun getEventType(): Class<MessageCreateEvent> = MessageCreateEvent::class.java

    override fun execute(event: MessageCreateEvent): Mono<Void> {
        val guildId = event.guildId.getOrNull()?.asLong() ?: return Mono.empty()

        val guildConfig = guildConfigDao.getConfigForGuild(guildId)
        val messagesToSend = processor.getMessagesToSend(guildConfig, event.message.content)
        messagesToSend.map { message ->
            event.message.channel
                .flatMap { channel ->
                    channel.createMessage(
                        MessageCreateSpec
                            .builder()
                            .content(message)
                            .also {
                                if (guildConfig.useReply) it.messageReferenceId(event.message.id)
                                if (guildConfig.useSilent) it.flags(listOf(Message.Flag.SUPPRESS_NOTIFICATIONS))
                                if (!guildConfig.mentionUser) it.allowedMentions(AllowedMentions.builder().build())
                            }.build(),
                    )
                }.subscribe()
        }

        if (messagesToSend.isNotEmpty() && guildConfig.removeOriginalEmbed) {
            return event.message
                .edit()
                .withFlags(Message.Flag.SUPPRESS_EMBEDS)
                .then()
        }

        return Mono.empty()
    }
}
