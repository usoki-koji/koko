package dev.koji.neoforge.common

import dev.koji.koko.Koko
import dev.koji.koko.common.content.Paths
import dev.koji.koko.common.events.BlockEventHandler
import net.minecraft.world.entity.player.Player
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import net.neoforged.neoforge.event.level.BlockEvent

@EventBusSubscriber(modid = Koko.MOD_ID)
object NeoBlockEventHandler {
    @SubscribeEvent
    fun onBlockPlace(event: BlockEvent.EntityPlaceEvent) {
        if (event.level.isClientSide) return

        val player = (event.entity as? Player) ?: return

        BlockEventHandler.blockEvaluate(player, Paths.DefaultSources.BLOCK_PLACE, event.state)
    }

    @SubscribeEvent
    fun onBlockInteract(event: PlayerInteractEvent.RightClickBlock) {
        if (event.level.isClientSide) return

        val blockState = event.level.getBlockState(event.pos)

        BlockEventHandler.blockEvaluate(event.entity, Paths.DefaultSources.BLOCK_INTERACT, blockState)
    }

    @SubscribeEvent
    fun onBlockBreak(event: BlockEvent.BreakEvent) {
        if (event.level.isClientSide) return

        BlockEventHandler.blockEvaluate(event.player, Paths.DefaultSources.BLOCK_BREAK, event.state)
    }
}