package dev.koji.koko.common.models.modifiers.filters

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.koji.koko.common.content.Paths
import dev.koji.koko.common.models.modifiers.AbstractSkillModifierFilter
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.entity.ai.attributes.AttributeModifier

class BellowSkillModifierFilter(
    val level: Int, val value: Double, val operation: AttributeModifier.Operation
): AbstractSkillModifierFilter() {
    override val type: String = Paths.DefaultFilters.BELLOW

    override fun apply(level: Int): Boolean = (level <= this.level)

    companion object {
        val CODEC: MapCodec<BellowSkillModifierFilter> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.INT.fieldOf("level").forGetter(BellowSkillModifierFilter::level),
                Codec.DOUBLE.fieldOf("value").forGetter(BellowSkillModifierFilter::value),
                AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(BellowSkillModifierFilter::operation)
            ).apply(instance, ::BellowSkillModifierFilter)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BellowSkillModifierFilter> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, BellowSkillModifierFilter::level,
            ByteBufCodecs.DOUBLE, BellowSkillModifierFilter::value,
            AttributeModifier.Operation.STREAM_CODEC, BellowSkillModifierFilter::operation,
            ::BellowSkillModifierFilter
        )
    }
}