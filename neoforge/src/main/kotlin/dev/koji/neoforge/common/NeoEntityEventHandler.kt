package dev.koji.neoforge.common

import dev.koji.koko.Koko
import dev.koji.koko.common.content.Paths
import dev.koji.koko.common.events.EntityEventHandler
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent
import net.neoforged.neoforge.event.entity.living.AnimalTameEvent
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent

@EventBusSubscriber(modid = Koko.MOD_ID)
object NeoEntityEventHandler {
    @SubscribeEvent
    fun onEntityInteract(event: PlayerInteractEvent.EntityInteract) {
        if (event.entity.level().isClientSide) return

        val player = event.entity
        val targetEntity = event.target

        EntityEventHandler.entityEvaluate(player, Paths.DefaultSources.ENTITY_INTERACT, targetEntity)
    }

    @SubscribeEvent
    fun onItemTrade(event: ItemStackedOnOtherEvent) {
        val player = event.player

        if (player.level().isClientSide) return

        val container = event.slot.container

        if (container !is Entity) return

        EntityEventHandler.entityEvaluate(player, Paths.DefaultSources.PLAYER_TRADE, container)
    }

    @SubscribeEvent
    fun onEntityTame(event: AnimalTameEvent) {
        val player = event.tamer

        if (player.level().isClientSide) return

        val tamedEntity = event.entity

        EntityEventHandler.entityEvaluate(player, Paths.DefaultSources.ENTITY_TAME, tamedEntity)
    }

    @SubscribeEvent
    fun onEntityDeath(event: LivingDeathEvent) {
        if (event.entity.level().isClientSide) return

        val source = event.source
        val attacker = source.entity
        val killedEntity = event.entity

        if (attacker as? Player == null) return

        EntityEventHandler.entityEvaluate(attacker, Paths.DefaultSources.ENTITY_KILL, killedEntity)
    }
}