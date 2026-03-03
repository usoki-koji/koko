package dev.koji.koko.common.models.modifiers.filters

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.koji.koko.common.content.Paths
import dev.koji.koko.common.models.modifiers.AbstractSkillModifierFilter

class BlockedSkillModifierFilter(val until: Int): AbstractSkillModifierFilter() {
    override val type: String = Paths.DefaultFilters.BLOCKED

    override fun apply(level: Int): Boolean = (level < until)

    companion object {
        val CODEC: MapCodec<BlockedSkillModifierFilter> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.INT.fieldOf("until").forGetter(BlockedSkillModifierFilter::until),
            ).apply(instance, ::BlockedSkillModifierFilter)
        }
    }
}