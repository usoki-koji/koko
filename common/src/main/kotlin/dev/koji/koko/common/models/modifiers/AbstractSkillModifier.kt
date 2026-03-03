package dev.koji.koko.common.models.modifiers

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import dev.koji.koko.common.SkillsHandler
import dev.koji.koko.common.content.Paths
import dev.koji.koko.common.models.modifiers.player.*
import net.minecraft.world.entity.player.Player

abstract class AbstractSkillModifier {
    abstract val type: String

    abstract fun doAnyApplies(level: Int): AbstractSkillModifierFilter?
    abstract fun apply(applier: SkillsHandler.SkillModifierApplier, player: Player)
    abstract fun unApply(applier: SkillsHandler.SkillModifierApplier, player: Player)

    companion object {
        val CODEC: Codec<AbstractSkillModifier> = Codec.STRING.dispatch(
            { source -> source.type },
            { type -> codecMapper.getOrElse(type) { throw IllegalArgumentException("$type is not supported") } }
        )

        val codecMapper = mutableMapOf<String, MapCodec<out AbstractSkillModifier>>(
            Paths.DefaultModifiers.PLAYER_ATTRIBUTE to AttributeSkillModifier.CODEC,
            Paths.DefaultModifiers.PLAYER_CRAFT to CraftingSkillModifier.CODEC,
            Paths.DefaultModifiers.PLAYER_FORGE to ForgeSkillModifier.CODEC,
            Paths.DefaultModifiers.PLAYER_ATTACK to ItemAttackSkillModifier.CODEC,
            Paths.DefaultModifiers.PLAYER_USE to ItemUseSkillModifier.CODEC,
            Paths.DefaultModifiers.PLAYER_CONSUME to ItemConsumeSkillModifier.CODEC,
            Paths.DefaultModifiers.PLAYER_ARMOR to ArmorEquipSkillModifier.CODEC
        )

        fun registerCodec(path: String, codec: MapCodec<out AbstractSkillModifier>) { codecMapper[path] = codec }
    }
}