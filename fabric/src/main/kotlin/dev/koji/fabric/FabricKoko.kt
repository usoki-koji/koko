package dev.koji.fabric

import dev.koji.fabric.common.FabricBlockEventHandler
import dev.koji.fabric.common.FabricCommonRegistry
import dev.koji.fabric.common.FabricEntityEventHandler
import dev.koji.fabric.common.FabricPlayerEventHandler
import dev.koji.fabric.server.FabricServerNetwork
import dev.koji.fabric.server.FabricServerRegistry
import dev.koji.koko.Koko
import net.fabricmc.api.ModInitializer

class FabricKoko : ModInitializer {
    override fun onInitialize() {
        Koko.LOGGER.info("Koko is loading...")

        Koko.init(FabricSkillsHandler)

        FabricCommonRegistry.register()
        FabricServerRegistry.register()
        FabricServerNetwork.init()

        FabricBlockEventHandler.init()
        FabricPlayerEventHandler.init()
        FabricEntityEventHandler.init()

        Koko.LOGGER.info("Koko has been successfully loaded.")
    }
}