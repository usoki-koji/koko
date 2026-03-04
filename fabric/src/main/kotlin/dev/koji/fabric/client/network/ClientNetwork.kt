package dev.koji.neoforge.client.network

import dev.koji.koko.Koko
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent

@EventBusSubscriber(modid = Koko.MOD_ID)
object ClientNetwork {
    @SubscribeEvent
    fun registerPayloadHandlers(event: RegisterPayloadHandlersEvent) {
        val registrar = event.registrar(Koko.toPath("payloaders").toString())

//        registrar.playToClient(SyncSkillsPayload.Companion.TYPE, SyncSkillsPayload.Companion.STREAM_CODEC) { payload, _ ->
//            val minecraft = Minecraft.getInstance()
//            minecraft.execute {
//                val player = minecraft.player ?: return@execute
//
//                SkillsHandler.replaceSkills(player, payload.skillData)
//            }
//        }
//
//        registrar.playToClient(SyncSkillPayload.Companion.TYPE, SyncSkillPayload.Companion.STREAM_CODEC) { payload, _ ->
//            val minecraft = Minecraft.getInstance()
//
//            minecraft.execute {
//                val player = minecraft.player ?: return@execute
//
//                SkillsHandler.replaceSkill(player, payload.skill, payload.skillData)
//            }
//        }
    }
}