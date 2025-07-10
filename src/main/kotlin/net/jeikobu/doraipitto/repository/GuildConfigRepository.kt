package net.jeikobu.doraipitto.repository

import net.jeikobu.doraipitto.model.GuildConfig
import org.springframework.data.mongodb.repository.MongoRepository

interface GuildConfigRepository : MongoRepository<GuildConfig, Long> {
    fun findByGuildId(guildId: Long): GuildConfig?
}
