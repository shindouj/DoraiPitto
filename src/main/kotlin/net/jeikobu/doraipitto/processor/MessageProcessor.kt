package net.jeikobu.doraipitto.processor

import net.jeikobu.doraipitto.model.GuildConfig
import org.springframework.stereotype.Component

private val HOSTNAME = "<hostname>"

@Component
class MessageProcessor {
    private val urlRegex = "https?:\\/\\/(www\\.)?$HOSTNAME(?:\\/.[^\\s]*)?"
    private val domainRegex = "https?:\\/\\/(www\\.)?$HOSTNAME"

    fun getMessagesToSend(
        guildConfig: GuildConfig,
        message: String,
    ): List<String> = findAllUrls(guildConfig, message)

    private fun findAllUrls(
        guildConfig: GuildConfig,
        message: String,
    ): List<String> =
        guildConfig.replacementDefinitions.flatMap { def ->
            def.hostnamesToReplace.flatMap { hostname ->
                findUrls(message, hostname, def.replacement)
            }
        }

    private fun findUrls(
        message: String,
        hostname: String,
        newHostname: String,
    ): List<String> =
        urlRegex
            .replace(HOSTNAME, hostname)
            .toRegex()
            .findAll(message)
            .toList()
            .map { it.value.replace(domainRegex.replace(HOSTNAME, hostname).toRegex(), newHostname) }
}
