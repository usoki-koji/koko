package dev.koji.koko.common.models.modifiers.filters

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.koji.koko.common.content.Paths
import dev.koji.koko.common.models.modifiers.AbstractSkillModifierFilter
import net.minecraft.world.entity.ai.attributes.AttributeModifier

class RangeSkillModifierFilter(
    val from: Int, val to: Int, val value: Double, val operation: AttributeModifier.Operation
): AbstractSkillModifierFilter() {
    override val type: String = Paths.DefaultFilters.RANGE

    override fun apply(level: Int): Boolean = (level in from..to)

    companion object {
        val CODEC: MapCodec<RangeSkillModifierFilter> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.INT.fieldOf("from").forGetter(RangeSkillModifierFilter::from),
                Codec.INT.fieldOf("to").forGetter(RangeSkillModifierFilter::to),
                Codec.DOUBLE.fieldOf("value").forGetter(RangeSkillModifierFilter::value),
                AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(RangeSkillModifierFilter::operation)
            ).apply(instance, ::RangeSkillModifierFilter)
        }
    }
}