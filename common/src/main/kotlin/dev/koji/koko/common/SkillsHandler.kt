package dev.koji.koko.common

import dev.koji.koko.common.attachments.PlayerSkills
import dev.koji.koko.common.models.SkillData
import dev.koji.koko.common.models.SkillModel
import dev.koji.koko.common.models.modifiers.AbstractSkillModifier
import dev.koji.koko.common.models.modifiers.AbstractSkillModifierFilter
import dev.koji.koko.common.models.sources.AbstractSkillSource
import dev.koji.koko.common.models.sources.SkillSourceFilter
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

interface SkillsHandler {
    fun syncSkills(player: Player)
    fun syncModifiers(player: Player)

    fun <T> skillCheckup(
        player: Player, source: String, predicate: (`object`: T, target: String) -> Boolean, `object`: T
    )

    fun <T> evaluate(
        filters: List<SkillSourceFilter>,
        predicate: (`object`: T, target: String) -> Boolean,
        `object`: T
    ): Double

    fun updateXp(player: Player, skill: ResourceLocation, amount: Double)

    fun replaceSkill(player: Player, skill: ResourceLocation, data: SkillData)
    fun replaceSkills(player: Player, skills: Map<ResourceLocation, SkillData>)

    fun getXpToLevelUp(level: Int): Int
    fun getLevel(player: Player, skill: ResourceLocation): Int

    fun getSkill(player: Player, skill: ResourceLocation): SkillData?
    fun getSkills(player: Player): PlayerSkills

    fun getSkillModel(player: Player, skill: ResourceLocation): SkillModel?
    fun getSkillModel(level: Level, skill: ResourceLocation): SkillModel?
    fun getSkillsModels(player: Player): Set<Map.Entry<ResourceKey<SkillModel>, SkillModel>>?
    fun getSkillsModels(level: Level): Set<Map.Entry<ResourceKey<SkillModel>, SkillModel>>?

    fun getListenersFor(skillSourceType: String, level: Level): Set<SkillSourceApplier>

    fun getPlayerModifiers(player: Player): Set<SkillModifierApplier>

    fun getPlayerPastModifiers(player: Player): Set<SkillModifierApplier>

    data class SkillSourceApplier(val skill: ResourceLocation, val sourceData: AbstractSkillSource)

    data class SkillModifierApplier(
        val skill: ResourceLocation, val modifierData: AbstractSkillModifier, val filter: AbstractSkillModifierFilter
    )
}