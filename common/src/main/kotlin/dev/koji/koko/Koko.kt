package dev.koji.koko

import com.mojang.logging.LogUtils
import dev.koji.koko.common.SkillsHandler
import net.minecraft.resources.ResourceLocation
import org.slf4j.Logger

object Koko {
    const val MOD_ID = "koko"

    val LOGGER: Logger = LogUtils.getLogger()

    lateinit var config: KokoConfig
    lateinit var skillsHandler: SkillsHandler

    fun init(skillsHandler: SkillsHandler) {
        this.config = KokoConfig.HANDLER.instance()
        this.skillsHandler = skillsHandler

        this.config.init()
    }

    fun toPath(path: String): ResourceLocation {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path)
    }
}