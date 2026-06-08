package dev.koji.fabric.client

import dev.koji.fabric.FabricSkillsHandler
import dev.koji.koko.Loggable
import dev.koji.koko.client.ui.SkillsScreen
import dev.koji.koko.common.network.payloads.IncomingXpPayload
import dev.koji.koko.common.network.payloads.StatsResponsePayload
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import kotlin.math.log

object FabricClientNetwork : Loggable {
    fun init() {
        PayloadTypeRegistry.playS2C().register(StatsResponsePayload.TYPE, StatsResponsePayload.STREAM_CODEC)
        PayloadTypeRegistry.playS2C().register(IncomingXpPayload.TYPE, IncomingXpPayload.STREAM_CODEC)

        ClientPlayNetworking.registerGlobalReceiver(StatsResponsePayload.TYPE) { payload, context ->
            val minecraft = context.client()

            minecraft.execute {
                FabricSkillsHandler.replaceSkills(context.player(), payload.skillData)

                minecraft.setScreen(SkillsScreen())
            }
        }

        ClientPlayNetworking.registerGlobalReceiver(IncomingXpPayload.TYPE) { payload, context ->
            val minecraft = context.client()

            minecraft.execute {
                logger.info("Received xp packet: ${payload.skill} +${payload.xp}xp")
            }
        }
    }
}