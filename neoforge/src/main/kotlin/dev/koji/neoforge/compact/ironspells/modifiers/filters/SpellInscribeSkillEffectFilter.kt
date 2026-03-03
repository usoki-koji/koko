package dev.koji.neoforge.compact.ironspells.modifiers.filters

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.koji.koko.common.models.modifiers.AbstractSkillModifierFilter
import dev.koji.neoforge.compact.ironspells.IronSpellsCompact

class SpellInscribeSkillEffectFilter(
    val spellLevel: Int, val level: Int
): AbstractSkillModifierFilter() {
    override val type: String = IronSpellsCompact.Filters.PLAYER_SPELL_INSCRIBE_FILTER

    override fun apply(level: Int): Boolean = (this.level > level)

    companion object {
        val CODEC: MapCodec<SpellInscribeSkillEffectFilter> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.INT.fieldOf("spellLevel").forGetter(SpellInscribeSkillEffectFilter::spellLevel),
                Codec.INT.fieldOf("level").forGetter(SpellInscribeSkillEffectFilter::level),
            ).apply(instance, ::SpellInscribeSkillEffectFilter)
        }
    }
}