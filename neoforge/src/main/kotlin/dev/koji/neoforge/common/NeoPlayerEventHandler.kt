package dev.koji.neoforge.common

import dev.koji.koko.Koko
import dev.koji.koko.common.content.Paths
import dev.koji.koko.common.events.PlayerEventHandler
import dev.koji.koko.common.events.PlayerEventHandler.DefaultPlayerMessages
import dev.koji.koko.common.events.PlayerEventHandler.PlayerBlockScope
import dev.koji.koko.common.helpers.MainHelper
import dev.koji.neoforge.NeoSkillsHandler
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.AnvilUpdateEvent
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.event.tick.PlayerTickEvent

@EventBusSubscriber(modid = Koko.MOD_ID)
object NeoPlayerEventHandler {
    @SubscribeEvent
    fun onPlayerJoin(event: PlayerEvent.PlayerLoggedInEvent): Unit = this.doPlayerStuff(event.entity)

    @SubscribeEvent
    fun onPlayerChangedDimension(event: PlayerEvent.PlayerChangedDimensionEvent): Unit = this.doPlayerStuff(event.entity)

    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        val player = event.entity
        val level = player.level()

        if (level.isClientSide || level.gameTime % 20 != 0L) return

        PlayerEventHandler.checkPlayerArmor(player)

        NeoSkillsHandler.syncModifiers(player)
    }

    @SubscribeEvent
    fun onItemUse(event: PlayerInteractEvent.RightClickItem) {
        val player = event.entity
        val item = player.mainHandItem

        if (PlayerEventHandler.isItemBlockedFor(player, item, PlayerBlockScope.USE)){
            event.isCanceled = true

            if (!player.level().isClientSide) return

            MainHelper.sendMessageToPlayer(player, DefaultPlayerMessages.UNABLE_TO_USE)
        }

        if (player.level().isClientSide) return

        PlayerEventHandler.playerEvaluate(player, Paths.DefaultSources.PLAYER_ITEM_USE, item)
    }

    //TODO
    @SubscribeEvent
    fun onItemAttack(event: AttackEntityEvent) {
        val player = event.entity
        val item = player.mainHandItem

        if (PlayerEventHandler.isItemBlockedFor(player, item, PlayerBlockScope.ATTACK)) {
            event.isCanceled = true

            if (!player.level().isClientSide) return

            MainHelper.sendMessageToPlayer(player, DefaultPlayerMessages.UNABLE_TO_ATTACK)
        }

        if (player.level().isClientSide) return

        PlayerEventHandler.playerEvaluate(player, Paths.DefaultSources.PLAYER_ATTACKED, item)
    }

    //TODO
    @SubscribeEvent
    fun onItemConsume(event: LivingEntityUseItemEvent.Start) {
        val player = (event.entity as? Player) ?: return
        val item = event.item

        if (PlayerEventHandler.isItemBlockedFor(player, item, PlayerBlockScope.CONSUME)) {

            event.isCanceled = true

            if (!player.level().isClientSide) return

            MainHelper.sendMessageToPlayer(player, DefaultPlayerMessages.UNABLE_TO_CONSUME)

            return
        }

        if (player.level().isClientSide) return

        PlayerEventHandler.playerEvaluate(player, Paths.DefaultSources.PLAYER_CONSUMED, item)
    }

    //TODO
    @SubscribeEvent
    fun onAnvilRepair(event: AnvilRepairEvent) {
        val player = event.entity

        if (player.level().isClientSide) return

        PlayerEventHandler.playerEvaluate(player, Paths.DefaultSources.PLAYER_FORGED, event.output)
    }

    //TODO
    @SubscribeEvent
    fun onAnvilUpdate(event: AnvilUpdateEvent) {
        val player = event.player

        if (!PlayerEventHandler.isItemBlockedFor(player, event.left, PlayerBlockScope.FORGE)) return

        event.isCanceled = true

        if (!player.level().isClientSide) return

        MainHelper.sendMessageToPlayer(player, DefaultPlayerMessages.UNABLE_TO_FORGE)
    }

    private fun doPlayerStuff(player: Player) {
        NeoSkillsHandler.syncSkills(player)
        NeoSkillsHandler.syncModifiers(player)
    }
}