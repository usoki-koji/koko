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

class AboveSkillModifierFilter(
    val level: Int, val value: Double, val operation: AttributeModifier.Operation
): AbstractSkillModifierFilter() {
    override val type: String = Paths.DefaultFilters.ABOVE

    override fun apply(level: Int): Boolean = (level >= this.level)

    companion object {
        val CODEC: MapCodec<AboveSkillModifierFilter> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.INT.fieldOf("level").forGetter(AboveSkillModifierFilter::level),
                Codec.DOUBLE.fieldOf("value").forGetter(AboveSkillModifierFilter::value),
                AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(AboveSkillModifierFilter::operation)
            ).apply(instance, ::AboveSkillModifierFilter)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, AboveSkillModifierFilter> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, AboveSkillModifierFilter::level,
            ByteBufCodecs.DOUBLE, AboveSkillModifierFilter::value,
            AttributeModifier.Operation.STREAM_CODEC, AboveSkillModifierFilter::operation,
            ::AboveSkillModifierFilter
        )
    }
}