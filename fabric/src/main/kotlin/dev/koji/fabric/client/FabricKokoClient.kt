package dev.koji.fabric.client

import dev.koji.fabric.client.FabricClientEventHandler
import dev.koji.fabric.client.FabricClientNetwork
import dev.koji.koko.Koko
import net.fabricmc.api.ClientModInitializer

class FabricKokoClient : ClientModInitializer {
    override fun onInitializeClient() {
        Koko.LOGGER.info("Koko client is loading...")

        FabricClientRegistry.register()
        FabricClientEventHandler.init()
        FabricClientNetwork.init()

        Koko.LOGGER.info("Koko client loaded!")
    }
}