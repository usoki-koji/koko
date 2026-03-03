package dev.koji.koko.common.models.modifiers.player

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.koji.koko.common.SkillsHandler
import dev.koji.koko.common.content.Paths
import dev.koji.koko.common.events.PlayerEventHandler
import dev.koji.koko.common.helpers.MainHelper
import dev.koji.koko.common.models.modifiers.AbstractSkillModifier
import dev.koji.koko.common.models.modifiers.AbstractSkillModifierFilter
import dev.koji.koko.common.models.modifiers.filters.BlockedSkillModifierFilter
import net.minecraft.world.entity.player.Player

class ArmorEquipSkillModifier(
    val item: String,
    val filter: AbstractSkillModifierFilter
) : AbstractSkillModifier() {
    override val type: String = Paths.DefaultModifiers.PLAYER_ARMOR

    override fun doAnyApplies(level: Int): AbstractSkillModifierFilter? {
        if (filter !is BlockedSkillModifierFilter)
            throw IllegalArgumentException("${this::class.simpleName} only supports ${BlockedSkillModifierFilter::class.simpleName}")

        return filter.takeIf { it.apply(level) }
    }

    override fun apply(applier: SkillsHandler.SkillModifierApplier, player: Player) {
        PlayerEventHandler.addBlockedItem(
            player.uuid, MainHelper.safeParseResource(item), PlayerEventHandler.PlayerBlockScope.ARMOR
        )
    }

    override fun unApply(
        applier: SkillsHandler.SkillModifierApplier,
        player: Player
    ) {
        PlayerEventHandler.removeBlockedItem(
            player.uuid, MainHelper.safeParseResource(item), PlayerEventHandler.PlayerBlockScope.ARMOR
        )
    }

    companion object {
        val CODEC: MapCodec<ArmorEquipSkillModifier> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("item").forGetter(ArmorEquipSkillModifier::item),
                AbstractSkillModifierFilter.CODEC.fieldOf("filter").forGetter(ArmorEquipSkillModifier::filter)
            ).apply(instance, ::ArmorEquipSkillModifier)
        }
    }
}