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
import dev.koji.neoforge.compact.ironspells.modifiers.filters.SpellInscribeSkillEffectFilter
import net.minecraft.world.entity.player.Player

class SpellInscribeSkillEffect(
    val spell: String,
    val filters: List<AbstractSkillModifierFilter>
) : AbstractSkillModifier() {
    override val type: String = IronSpellsCompact.Effects.PLAYER_SPELL_INSCRIBE

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
        val filter = applier.filter as? SpellInscribeSkillEffectFilter ?: return

        val group = Koko.skillsHandler.getGroup(player, MainHelper.safeParseResource(spell))

        if (group != null) {
            for (listedTarget in group.content) {
                IronSpellsCompact.addBlockedSpell(
                    player.uuid, MainHelper.safeParseResource(listedTarget), filter.spellLevel,  IronSpellsCompact.ISSBlockScope.INSCRIBE
                )
            }

            return
        }

        IronSpellsCompact.addBlockedSpell(
            player.uuid, MainHelper.safeParseResource(spell), filter.spellLevel, IronSpellsCompact.ISSBlockScope.INSCRIBE
        )
    }

    override fun unApply(applier: SkillsHandler.SkillModifierApplier, player: Player) {
        IronSpellsCompact.removeBlockedSpell(
            player.uuid, MainHelper.safeParseResource(spell), IronSpellsCompact.ISSBlockScope.INSCRIBE
        )
    }

    companion object {
        val CODEC: MapCodec<SpellInscribeSkillEffect> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("spell").forGetter(SpellInscribeSkillEffect::spell),
                AbstractSkillModifierFilter.CODEC.listOf().fieldOf("filters").forGetter(SpellInscribeSkillEffect::filters)
            ).apply(instance, ::SpellInscribeSkillEffect)
        }
    }
}