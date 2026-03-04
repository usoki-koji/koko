package dev.koji.neoforge.compact.ironspells.modifiers.filters

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.koji.koko.common.models.modifiers.AbstractSkillModifierFilter
import dev.koji.neoforge.compact.ironspells.IronSpellsCompact

class SpellCastSkillEffectFilter(
    val spellLevel: Int, val level: Int
): AbstractSkillModifierFilter() {
    override val type: String = IronSpellsCompact.Filters.PLAYER_SPELL_CAST_FILTER

    override fun apply(level: Int): Boolean = (this.level > level)

    companion object {
        val CODEC: MapCodec<SpellCastSkillEffectFilter> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.INT.fieldOf("spellLevel").forGetter(SpellCastSkillEffectFilter::spellLevel),
                Codec.INT.fieldOf("level").forGetter(SpellCastSkillEffectFilter::level),
            ).apply(instance, ::SpellCastSkillEffectFilter)
        }
    }
}