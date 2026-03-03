package dev.koji.koko.common.models.sources.block

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.koji.koko.common.content.Paths
import dev.koji.koko.common.models.sources.AbstractSkillSource
import dev.koji.koko.common.models.sources.SkillSourceFilter

class BlockInteractSource(
    override val filters: List<SkillSourceFilter>, override val alwaysApply: Boolean, override val alwaysValue: Double
) : AbstractSkillSource() {
    override val type: String = Paths.DefaultSources.BLOCK_INTERACT

    companion object {
        val CODEC: MapCodec<BlockInteractSource> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                SkillSourceFilter.CODEC.listOf().fieldOf("filters").forGetter { it.filters },
                Codec.BOOL.optionalFieldOf("alwaysApply", false).forGetter { it.alwaysApply },
                Codec.DOUBLE.optionalFieldOf("alwaysValue", 0.0).forGetter { it.alwaysValue }
            ).apply(instance, ::BlockInteractSource)
        }
    }
}