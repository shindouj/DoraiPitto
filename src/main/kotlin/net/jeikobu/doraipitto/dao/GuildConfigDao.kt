package net.jeikobu.doraipitto.dao

import net.jeikobu.doraipitto.model.GuildConfig
import net.jeikobu.doraipitto.model.ReplacementDefinition
import net.jeikobu.doraipitto.repository.GuildConfigRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GuildConfigDao(
    private val guildConfigRepository: GuildConfigRepository,
    @param:Value("\${doraipitto.twitter.host}") private val twitterHost: String,
    @param:Value("\${doraipitto.instagram.host}") private val instagramHost: String,
    @param:Value("\${doraipitto.reddit.host}") private val redditHost: String,
) {
    fun getConfigForGuild(guildId: Long): GuildConfig =
        guildConfigRepository.findByGuildId(guildId) ?: getDefaultConfigForGuild(guildId).also {
            guildConfigRepository.save(it)
        }

    fun saveConfig(config: GuildConfig) {
        guildConfigRepository.save(config)
    }

    private fun getDefaultConfigForGuild(guildId: Long) =
        GuildConfig(
            guildId = guildId,
            useReply = true,
            useSilent = true,
            mentionUser = false,
            removeOriginalEmbed = true,
            replacementDefinitions =
                listOf(
                    ReplacementDefinition(hostnamesToReplace = listOf("twitter.com", "x.com"), replacement = twitterHost),
                    ReplacementDefinition(
                        hostnamesToReplace = listOf("reddit.com", "redditmedia.com", "redd.it"),
                        replacement = redditHost,
                    ),
                    ReplacementDefinition(hostnamesToReplace = listOf("instagram.com"), replacement = instagramHost),
                ),
        )
}
