package dev.koji.fabric

import com.mojang.logging.LogUtils
import net.fabricmc.api.ModInitializer

class FabricKoko : ModInitializer {
    override fun onInitialize() {
        val logger = LogUtils.getLogger()

        logger.info("Koko started!")
    }
}