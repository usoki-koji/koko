package dev.koji.neoforge

import dev.koji.koko.Koko
import dev.koji.neoforge.common.CommonRegistry
import dev.koji.koko.common.SkillsHandler
import dev.koji.koko.common.attachments.PlayerSkills
import dev.koji.koko.common.models.SkillData
import dev.koji.koko.common.models.SkillModel
import dev.koji.koko.common.models.sources.SkillSourceFilter
import dev.koji.koko.common.network.payloads.IncomingXpPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.neoforged.neoforge.network.PacketDistributor

object NeoSkillsHandler : SkillsHandler {
    override fun syncSkills(player: Player) {
        val level = player.level()

        if (level.isClientSide) return

        val skillModels = this.getSkillsModels(level)
            ?: return Koko.LOGGER.error("Unable to find SkillsModels in level context!")

        val playerSkills = this.getSkills(player)

        for (entry in skillModels)
            playerSkills.putIfAbsent(entry.key.location(), SkillData(entry.value.defaultXp, false))
    }

    override fun syncModifiers(player: Player) {
        if (player.level().isClientSide) return

        val pastAppliedEffects = this.getPlayerPastModifiers(player)
        val toApplyEffects = this.getPlayerModifiers(player)

        for (applier in pastAppliedEffects.filter { !toApplyEffects.contains(it) })
            applier.modifierData.unApply(applier, player)

        for (applier in toApplyEffects) applier.modifierData.apply(applier, player)
    }

    override fun <T> skillCheckup(
        player: Player, source: String, predicate: (`object`: T, target: String) -> Boolean, `object`: T
    ) {
        val listeners = this.getListenersFor(source, player.level())

        for (listener in listeners) {
            val skillSource = listener.sourceData

            val xp = if (skillSource.alwaysApply)
                skillSource.alwaysValue
            else
                this.evaluate(listener.sourceData.filters, predicate, `object`)

            if (xp == 0.0) continue

            this.updateXp(player, listener.skill, xp)
        }
    }

    override fun <T> evaluate(
        filters: List<SkillSourceFilter>,
        predicate: (`object`: T, target: String) -> Boolean,
        `object`: T
    ): Double {
        if (filters.isEmpty()) return 0.0

        val whitelists = filters.filter { it.type == SkillSourceFilter.FilterType.WHITELIST }
            .sortedByDescending { it.priority }

        val blacklists = filters.filter { it.type == SkillSourceFilter.FilterType.BLACKLIST }
            .sortedByDescending { it.priority }

        for (blacklist in blacklists) {
            if (predicate(`object`, blacklist.target)) return 0.0
        }

        if (whitelists.isEmpty()) return 0.0

        for (whitelist in whitelists) {
            if (predicate(`object`, whitelist.target)) {
                val xp = whitelist.xp

                return if (whitelist.inverse) -xp else xp
            }
        }

        return 0.0
    }

    override fun updateXp(player: Player, skill: ResourceLocation, amount: Double) {
        if (player.level().isClientSide) return

        val playerSkills = player.getData(CommonRegistry.PLAYER_SKILLS)
        val playerSkill = playerSkills.getSkill(skill)
            ?: return Koko.LOGGER.warn("Unable to find skill with location $skill!")

        val currentXp = playerSkill.xp

        Koko.LOGGER.debug("Updating {} xp of skill {} from {} to {}.", player.name.string, currentXp, skill, currentXp + amount)

        val skillModel = this.getSkillModel(player.level(), skill)
            ?: return Koko.LOGGER.warn("Unable to find skill model for location $skill!")

        val maxLevel = if (playerSkill.isUnlocked) skillModel.unlockedMaxLevel else skillModel.maxLevel
        val maxXp = getXpToLevelUp(maxLevel).toDouble()

        playerSkill.xp = (playerSkill.xp + amount).coerceIn(0.0, maxXp)

        PacketDistributor.sendToPlayer(player as ServerPlayer, IncomingXpPayload(skill, playerSkill.xp))

        //TODO
        //this.syncEffects(player)
    }

    override fun replaceSkill(player: Player, skill: ResourceLocation, data: SkillData) {
        val playerSkills = this.getSkills(player)

        playerSkills.put(skill, data)
    }

    override fun replaceSkills(player: Player, skills: Map<ResourceLocation, SkillData>) {
        val playerSkills = this.getSkills(player)

        playerSkills.replace(skills)
    }

    override fun getXpToLevelUp(level: Int): Int = (100 + 25 * level + 5 * level * level)

    override fun getLevel(player: Player, skill: ResourceLocation): Int {
        val skills = this.getSkills(player)
        val skillData = skills.getSkill(skill)
            ?: return 0

        val skillModel = this.getSkillModel(player.level(), skill)
            ?: return 0

        val xp = skillData.xp
        val maxLevel = if (skillData.isUnlocked) skillModel.unlockedMaxLevel else skillModel.maxLevel
        var total: Int

        for (level in 1..maxLevel) {
            total = this.getXpToLevelUp(level)

            if (xp <= total) return level
        }

        return maxLevel
    }

    override fun getSkill(player: Player, skill: ResourceLocation): SkillData? =
        this.getSkills(player).getSkill(skill)

    override fun getSkills(player: Player): PlayerSkills = player.getData(CommonRegistry.PLAYER_SKILLS)

    override fun getSkillModel(player: Player, skill: ResourceLocation): SkillModel? =
        this.getSkillModel(player.level(), skill)

    override fun getSkillModel(level: Level, skill: ResourceLocation): SkillModel? {
        val registry = level.registryAccess().registry(CommonRegistry.SKILL_REGISTRY)

        return registry.map { it.get(skill) }.orElse(null)
    }

    override fun getSkillsModels(player: Player): Set<Map.Entry<ResourceKey<SkillModel>, SkillModel>>? =
        this.getSkillsModels(player.level())

    override fun getSkillsModels(level: Level): Set<Map.Entry<ResourceKey<SkillModel>, SkillModel>>? {
        val registry = level.registryAccess().registry(CommonRegistry.SKILL_REGISTRY)

        return registry.map { it.entrySet() }.orElse(null)
    }

    override fun getListenersFor(skillSourceType: String, level: Level): Set<SkillsHandler.SkillSourceApplier> {
        val skillsModels = this.getSkillsModels(level)
            ?: emptySet()

        return skillsModels.asSequence()
            .flatMap { (key, model) ->
                model.skillSources.asSequence()
                    .filter { it.type == skillSourceType }
                    .map { SkillsHandler.SkillSourceApplier(key.location(), it) }
            }
            .toSet()
    }

    override fun getPlayerModifiers(player: Player): Set<SkillsHandler.SkillModifierApplier> {
        val skillsModels = this.getSkillsModels(player)
            ?: return emptySet()

        return skillsModels.mapNotNull { (location, model) ->
            val playerLevel = this.getLevel(player, location.location())

            model.effects.mapNotNull { effect ->
                effect.doAnyApplies(playerLevel)?.let { applies ->
                    SkillsHandler.SkillModifierApplier(location.location(), effect, applies)
                }
            }.takeIf { it.isNotEmpty() }
        }.flatten().toSet()
    }

    override fun getPlayerPastModifiers(player: Player): Set<SkillsHandler.SkillModifierApplier> {
        val skillsModels = this.getSkillsModels(player)
            ?: return emptySet()

        return skillsModels.mapNotNull { (location, model) ->
            val playerLevel = this.getLevel(player, location.location())

            model.effects.mapNotNull { effect ->
                effect.doAnyApplies((playerLevel - 1).coerceAtMost(0))?.let { applies ->
                    SkillsHandler.SkillModifierApplier(location.location(), effect, applies)
                }
            }.takeIf { it.isNotEmpty() }
        }.flatten().toSet()
    }
}