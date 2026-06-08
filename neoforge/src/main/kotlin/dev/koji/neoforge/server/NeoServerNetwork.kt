package dev.koji.neoforge.server

import dev.koji.koko.Koko
import dev.koji.koko.Loggable
import dev.koji.koko.client.ui.SkillsScreen
import dev.koji.koko.common.network.payloads.IncomingXpPayload
import dev.koji.koko.common.network.payloads.StatsRequestPayload
import dev.koji.koko.common.network.payloads.StatsResponsePayload
import dev.koji.neoforge.NeoSkillsHandler
import net.minecraft.client.Minecraft
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

@EventBusSubscriber(modid = Koko.MOD_ID)
object NeoServerNetwork : Loggable {
    @SubscribeEvent
    fun registerPayloadHandlers(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(Koko.toPath("payloaders").toString())

        registrar.playToServer(StatsRequestPayload.TYPE, StatsRequestPayload.STREAM_CODEC) { payload, context ->
            val minecraft = Minecraft.getInstance()

            minecraft.execute {
                val player = context.player()

                PacketDistributor.sendToPlayer(
                    player as ServerPlayer,
                    StatsResponsePayload(NeoSkillsHandler.getSkills(player).getAllSkills())
                )
            }
        }
    }
}