package net.jeikobu.doraipitto.processor

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

private val HOSTNAME = "<hostname>"

@Component
class MessageProcessor(
    @param:Value("\${doraipitto.twitter.host}") private val twitterHost: String,
    @param:Value("\${doraipitto.instagram.host}") private val instagramHost: String,
    @param:Value("\${doraipitto.reddit.host}") private val redditHost: String,
) {
    private val originalTwitterHostnames = listOf("twitter.com", "x.com")
    private val originalInstagramHostnames = listOf("instagram.com")
    private val originalRedditHostnames = listOf("reddit.com", "redditmedia.com", "redd.it")

    private val urlRegex = "https?:\\/\\/(www\\.)?$HOSTNAME(?:\\/.[^\\s]*)?"
    private val domainRegex = "https?:\\/\\/(www\\.)?$HOSTNAME"

    fun getMessagesToSend(message: String): List<String> = findAllUrls(message)

    private fun findAllUrls(message: String): List<String> {
        val twitterUrls = mutableListOf<String>()
        for (hostname in originalTwitterHostnames) {
            twitterUrls += findUrls(message, hostname, twitterHost)
        }

        val instagramUrls = mutableListOf<String>()
        for (hostname in originalInstagramHostnames) {
            instagramUrls += findUrls(message, hostname, instagramHost)
        }

        val redditUrls = mutableListOf<String>()
        for (hostname in originalRedditHostnames) {
            redditUrls += findUrls(message, hostname, redditHost)
        }

        return twitterUrls + instagramUrls + redditUrls
    }

    private fun findUrls(message: String, hostname: String, newHostname: String): List<String> =
        urlRegex.replace(HOSTNAME, hostname)
            .toRegex()
            .findAll(message)
            .toList()
            .map { it.value.replace(domainRegex.replace(HOSTNAME, hostname).toRegex(), newHostname) }

}