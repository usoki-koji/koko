package dev.koji.fabric.common

import dev.koji.koko.common.content.Paths
import dev.koji.koko.common.events.EntityEventHandler
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player

object FabricEntityEventHandler {
    fun init() {
        UseEntityCallback.EVENT.register { player, _, _, entity, _ ->
            if (entity.level().isClientSide) return@register InteractionResult.PASS

            EntityEventHandler.entityEvaluate(player, Paths.DefaultSources.ENTITY_INTERACT, entity)

            return@register InteractionResult.PASS
        }

        ServerLivingEntityEvents.AFTER_DEATH.register { entity, source ->
            if (entity.level().isClientSide) return@register

            val attacker = source.entity

            if (attacker as? Player == null) return@register

            EntityEventHandler.entityEvaluate(attacker, Paths.DefaultSources.ENTITY_KILL, entity)
        }
    }
}