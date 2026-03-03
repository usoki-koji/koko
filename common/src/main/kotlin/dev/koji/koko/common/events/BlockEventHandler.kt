package dev.koji.koko.common.events

import dev.koji.koko.Koko
import dev.koji.koko.common.helpers.MainHelper
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.state.BlockState

object BlockEventHandler {
    fun blockEvaluate(
        player: Player,
        source: String,
        blockState: BlockState
    ) = Koko.skillsHandler.skillCheckup(player, source, this::blockMatches, blockState)

    private fun blockMatches(blockState: BlockState, targetLocation: String): Boolean {
        val resourceLocation = MainHelper.safeParseResource(targetLocation)
        val tagKey = TagKey.create(Registries.BLOCK, resourceLocation)

        return (blockState.`is`(tagKey) || blockState.`is`(ResourceKey.create(Registries.BLOCK, resourceLocation)))
    }
}
