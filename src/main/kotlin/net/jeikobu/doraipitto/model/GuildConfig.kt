package net.jeikobu.doraipitto.model

import org.springframework.data.annotation.Id

data class GuildConfig(
    @Id
    private val guildId: Long,
    val useReply: Boolean = true,
    val useSilent: Boolean = true,
    val mentionUser: Boolean = false,
    val removeOriginalEmbed: Boolean = true,
    val replacementDefinitions: List<ReplacementDefinition> = emptyList(),
)
