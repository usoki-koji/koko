package dev.koji.neoforge.client

import dev.koji.koko.Koko
import dev.koji.koko.client.ui.SkillsScreen
import dev.koji.koko.common.network.payloads.StatsRequestPayload
import net.minecraft.client.Minecraft
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.network.PacketDistributor

@EventBusSubscriber(modid = Koko.MOD_ID)
object NeoClientEventHandler {
    @SubscribeEvent
    fun onClientTick(event: ClientTickEvent.Post) {
        while (NeoClientRegistry.OPEN_SKILLS.consumeClick()) {
            PacketDistributor.sendToServer(StatsRequestPayload())
        }
    }
}