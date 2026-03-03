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

class ForgeSkillModifier(
    val recipe: String,
    val filter: AbstractSkillModifierFilter
) : AbstractSkillModifier() {
    override val type: String = Paths.DefaultModifiers.PLAYER_FORGE

    override fun doAnyApplies(level: Int): AbstractSkillModifierFilter? {
        if (filter !is BlockedSkillModifierFilter)
            throw IllegalArgumentException("${this::class.simpleName} only supports ${BlockedSkillModifierFilter::class.simpleName}")

        return filter.takeIf { it.apply(level) }
    }

    override fun apply(applier: SkillsHandler.SkillModifierApplier, player: Player) {
        PlayerEventHandler.addBlockedItem(
            player.uuid, MainHelper.safeParseResource(recipe), PlayerEventHandler.PlayerBlockScope.FORGE
        )
    }

    override fun unApply(
        applier: SkillsHandler.SkillModifierApplier,
        player: Player
    ) {
        PlayerEventHandler.removeBlockedItem(
            player.uuid, MainHelper.safeParseResource(recipe), PlayerEventHandler.PlayerBlockScope.FORGE
        )
    }

    companion object {
        val CODEC: MapCodec<ForgeSkillModifier> = RecordCodecBuilder.mapCodec { instance ->
            instance.group(
                Codec.STRING.fieldOf("recipe").forGetter(ForgeSkillModifier::recipe),
                AbstractSkillModifierFilter.CODEC.fieldOf("filter").forGetter(ForgeSkillModifier::filter)
            ).apply(instance, ::ForgeSkillModifier)
        }
    }
}