package net.jeikobu.doraipitto.config

import com.fasterxml.jackson.module.kotlin.readValue
import discord4j.common.JacksonResources
import discord4j.core.GatewayDiscordClient
import discord4j.discordjson.json.ApplicationCommandRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import net.jeikobu.doraipitto.model.DiscordNoValueReturnedException
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.stereotype.Component
import java.io.IOException
import java.util.function.Consumer

private val log = KotlinLogging.logger {}

@Component
class GlobalCommandRegistrar(
    private val client: GatewayDiscordClient,
) : ApplicationRunner {
    @Throws(IOException::class)
    override fun run(args: ApplicationArguments) {
        val restClient = client.restClient
        val d4jMapper = JacksonResources.create()
        val matcher = PathMatchingResourcePatternResolver()

        val commands: List<ApplicationCommandRequest> =
            matcher.getResources("commands/*.json").map {
                d4jMapper.objectMapper.readValue(it.inputStream)
            }

        val applicationId: Long = restClient.applicationId.block() ?: throw DiscordNoValueReturnedException("No application ID provided!")
        restClient.applicationService
            .bulkOverwriteGlobalApplicationCommand(applicationId, commands)
            .doOnNext(Consumer { log.debug { "Successfully registered Global Commands" } })
            .doOnError(Consumer { e: Throwable -> log.error(e) { "Failed to register global commands" } })
            .subscribe()
    }
}
