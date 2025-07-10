package net.jeikobu.doraipitto.model

import discord4j.core.event.domain.Event
import io.github.oshai.kotlinlogging.KotlinLogging
import reactor.core.publisher.Mono

private val log = KotlinLogging.logger {}

interface EventListener<T : Event> {
    fun getEventType(): Class<T>

    fun execute(event: T): Mono<Void>

    fun handleError(error: Throwable): Mono<Void> {
        log.error(error) { "Unable to process " + getEventType().getSimpleName() }
        return Mono.empty()
    }
}
