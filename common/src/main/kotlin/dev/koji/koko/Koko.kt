package dev.koji.koko

import com.mojang.logging.LogUtils
import dev.koji.koko.common.SkillsHandler
import net.minecraft.resources.ResourceLocation

object Koko {
    const val MOD_ID = "koko"

    lateinit var config: KokoConfiguration
    lateinit var skillsHandler: SkillsHandler

    val LOGGER = LogUtils.getLogger()

    fun init(configuration: KokoConfiguration, skillsHandler: SkillsHandler) {
        this.config = configuration
        this.skillsHandler = skillsHandler
    }

    fun toPath(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
    }

    interface KokoConfiguration {
        fun getMessageConfig(message: String): String
    }
}