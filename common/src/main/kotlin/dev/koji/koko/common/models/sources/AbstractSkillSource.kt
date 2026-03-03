package dev.koji.koko.common.models.sources

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import dev.koji.koko.common.content.Paths
import dev.koji.koko.common.models.sources.block.BlockBreakSource
import dev.koji.koko.common.models.sources.block.BlockInteractSource
import dev.koji.koko.common.models.sources.block.BlockPlaceSource
import dev.koji.koko.common.models.sources.entity.EntityInteractSource
import dev.koji.koko.common.models.sources.entity.EntityKillSource
import dev.koji.koko.common.models.sources.entity.EntityTameSource
import dev.koji.koko.common.models.sources.player.*

abstract class AbstractSkillSource {
    abstract val filters: List<SkillSourceFilter>
    abstract val alwaysApply: Boolean

    abstract val alwaysValue: Double
    abstract val type: String

    companion object {
        val CODEC: Codec<AbstractSkillSource> = Codec.STRING.dispatch(
            { source -> source.type },
            { type -> codecMapper.getOrElse(type) { throw IllegalArgumentException("$type is not supported") } }
        )

        val codecMapper = mutableMapOf<String, MapCodec<out AbstractSkillSource>>(
            Paths.DefaultSources.BLOCK_PLACE to BlockPlaceSource.CODEC,
            Paths.DefaultSources.BLOCK_INTERACT to BlockBreakSource.CODEC,
            Paths.DefaultSources.BLOCK_BREAK to BlockInteractSource.CODEC,
            Paths.DefaultSources.ENTITY_INTERACT to EntityInteractSource.CODEC,
            Paths.DefaultSources.ENTITY_TAME to EntityTameSource.CODEC,
            Paths.DefaultSources.ENTITY_KILL to EntityKillSource.CODEC,
            Paths.DefaultSources.PLAYER_RUN to PlayerRunSource.CODEC,
            Paths.DefaultSources.PLAYER_JUMP to PlayerJumpSource.CODEC,
            Paths.DefaultSources.PLAYER_CRAFTED to PlayerCraftedSource.CODEC,
            Paths.DefaultSources.PLAYER_TRADE to PlayerTradeSource.CODEC,
            Paths.DefaultSources.PLAYER_STACK_DROP to PlayerStackDropSource.CODEC,
            Paths.DefaultSources.PLAYER_DAMAGED to PlayerDamagedSource.CODEC,
            Paths.DefaultSources.PLAYER_DIED to PlayerDiedSource.CODEC
        )

        fun registerCodec(path: String, codec: MapCodec<out AbstractSkillSource>) { codecMapper[path] = codec }
    }
}