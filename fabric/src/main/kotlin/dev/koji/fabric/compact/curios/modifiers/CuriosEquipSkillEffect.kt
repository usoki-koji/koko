package dev.koji.neoforge.compact.curios.modifiers

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.koji.koko.common.SkillsHandler
import dev.koji.koko.common.events.PlayerEventHandler
import dev.koji.koko.common.helpers.MainHelper
import dev.koji.koko.common.models.modifiers.AbstractSkillModifier
import dev.koji.koko.common.models.modifiers.AbstractSkillModifierFilter
import dev.koji.koko.common.models.modifiers.filters.BlockedSkillModifierFilter
import dev.koji.neoforge.compact.curios.CuriosCompact
import net.minecraft.world.entity.player.Player

class CuriosEquipSkillEffect(
    val curio: String,
    val filter: AbstractSkillModifierFilter
) : AbstractSkillModifier() {
    override val type: String = CuriosCompact.Modifiers.PLAYER_CURIOS_EQUIP

    override fun doAnyApplies(level: Int): AbstractSkillModifierFilter? {
        if (filter !is BlockedSkillModifierFilter)
            throw IllegalArgumentException("${this::class.simpleName} only supports ${BlockedSkillModifierFilter::class.simpleName}")

        return filter.takeIf { it.apply(level) }
    }

    override fun apply(applier: SkillsHandler.SkillModifierApplier, player: Player) {
        PlayerEventHandler.addBlockedItem(
            player.uuid, MainHelper.safeParseResource(curio), PlayerEventHandler.PlayerBlockScope.CURIOS
        )
    }

    override fun unApply(
        applier: SkillsHandler.SkillModifierApplier,
        player: Player
    ) {
        PlayerEventHandler.removeBlockedItem(
            player.uuid, MainHelper.safeParseResource(curio), PlayerEventHandler.PlayerBlockScope.CURIOS
        )
    }

    companion object {
        val CODEC: MapCodec<CuriosEquipSkillEffect> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("curio").forGetter(CuriosEquipSkillEffect::curio),
                AbstractSkillModifierFilter.CODEC.fieldOf("filter").forGetter(CuriosEquipSkillEffect::filter)
            ).apply(instance, ::CuriosEquipSkillEffect)
        }
    }
}