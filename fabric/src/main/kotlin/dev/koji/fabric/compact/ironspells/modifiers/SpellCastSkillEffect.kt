package dev.koji.neoforge.compact.ironspells.modifiers

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.koji.koko.Koko
import dev.koji.koko.common.SkillsHandler
import dev.koji.koko.common.helpers.MainHelper
import dev.koji.koko.common.models.modifiers.AbstractSkillModifier
import dev.koji.koko.common.models.modifiers.AbstractSkillModifierFilter
import dev.koji.neoforge.compact.ironspells.IronSpellsCompact
import dev.koji.neoforge.compact.ironspells.modifiers.filters.SpellCastSkillEffectFilter
import net.minecraft.world.entity.player.Player

class SpellCastSkillEffect(
    val spell: String,
    val filters: List<AbstractSkillModifierFilter>
) : AbstractSkillModifier() {
    override val type: String = IronSpellsCompact.Effects.PLAYER_SPELL_CAST

    override fun doAnyApplies(level: Int): AbstractSkillModifierFilter? {
        val applicableFilters = filters.filter { it.apply(level) }

        return when (applicableFilters.size) {
            0 -> null
            1 -> applicableFilters.first()
            else -> {
                Koko.LOGGER.warn("Filter overlapping is not supported.")

                applicableFilters.first()
            }
        }
    }

    override fun apply(applier: SkillsHandler.SkillModifierApplier, player: Player) {
        val filter = applier.filter as? SpellCastSkillEffectFilter ?: return

        IronSpellsCompact.addBlockedSpell(
            player.uuid, MainHelper.safeParseResource(spell), filter.spellLevel,  IronSpellsCompact.ISSBlockScope.CAST
        )
    }

    override fun unApply(applier: SkillsHandler.SkillModifierApplier, player: Player) {
        IronSpellsCompact.removeBlockedSpell(
            player.uuid, MainHelper.safeParseResource(spell), IronSpellsCompact.ISSBlockScope.CAST
        )
    }

    companion object {
        val CODEC: MapCodec<SpellCastSkillEffect> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("spell").forGetter(SpellCastSkillEffect::spell),
                AbstractSkillModifierFilter.CODEC.listOf().fieldOf("filters").forGetter(SpellCastSkillEffect::filters)
            ).apply(instance, ::SpellCastSkillEffect)
        }
    }
}