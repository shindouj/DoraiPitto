package net.jeikobu.doraipitto.model

data class ReplacementDefinition(
    val enabled: Boolean = true,
    val hostnamesToReplace: List<String>,
    val replacement: String,
)
