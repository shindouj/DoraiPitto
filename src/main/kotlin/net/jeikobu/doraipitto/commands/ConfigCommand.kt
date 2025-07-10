package net.jeikobu.doraipitto.commands

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent
import discord4j.core.`object`.component.Container
import discord4j.core.`object`.component.TextDisplay
import net.jeikobu.doraipitto.dao.GuildConfigDao
import net.jeikobu.doraipitto.model.DiscordNoValueReturnedException
import net.jeikobu.doraipitto.model.GuildConfig
import net.jeikobu.doraipitto.model.SlashCommand
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import kotlin.jvm.optionals.getOrNull

@Component
class ConfigCommand(
    private val guildConfigDao: GuildConfigDao,
) : SlashCommand {
    override val name = "config"

    override fun handle(event: ChatInputInteractionEvent): Mono<Void> {
        val guildConfig =
            guildConfigDao.getConfigForGuild(
                event.interaction.guildId
                    .getOrNull()
                    ?.asLong() ?: throw DiscordNoValueReturnedException("Guild ID not provided!"),
            )

        if (event.options.isEmpty()) {
            return event
                .reply()
                .withEphemeral(true)
                .withComponents(createConfigContainer(guildConfig, "Current configuration:"))
        }

        val useReply = event.getOptionAsBoolean("use-reply").getOrNull()
        val useSilent = event.getOptionAsBoolean("use-silent").getOrNull()
        val mentionUser = event.getOptionAsBoolean("mention-user").getOrNull()
        val removeOriginalEmbed = event.getOptionAsBoolean("remove-original-embed").getOrNull()

        val newConfig =
            guildConfig.copy(
                useReply = useReply ?: guildConfig.useReply,
                useSilent = useSilent ?: guildConfig.useSilent,
                mentionUser = mentionUser ?: guildConfig.mentionUser,
                removeOriginalEmbed = removeOriginalEmbed ?: guildConfig.removeOriginalEmbed,
            )

        if (guildConfig != newConfig) {
            guildConfigDao.saveConfig(newConfig)
        }

        return event
            .reply()
            .withEphemeral(true)
            .withComponents(createConfigContainer(newConfig, "Configuration updated!"))
    }

    private fun boolToHuman(value: Boolean) = if (value) "**will**" else "**will not**"

    private fun createConfigContainer(
        config: GuildConfig,
        header: String,
    ): Container =
        Container.of(
            TextDisplay.of("**$header**"),
            TextDisplay.of("Bot ${boolToHuman(config.useReply)} reply to user with link."),
            TextDisplay.of("Bot ${boolToHuman(config.useSilent)} send silent messages."),
            TextDisplay.of("Bot ${boolToHuman(config.mentionUser)} mention the user when replying."),
            TextDisplay.of("Bot ${boolToHuman(config.removeOriginalEmbed)} remove the original embed."),
        )
}
