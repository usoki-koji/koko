package dev.koji.koko.common.models

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.koji.koko.common.models.modifiers.AbstractSkillModifier
import dev.koji.koko.common.models.sources.AbstractSkillSource

data class SkillModel(
    val displayName: String, val icon: String, val description: String,
    val defaultXp: Double, val maxLevel: Int, val unlockedMaxLevel: Int,
    val skillSources: List<AbstractSkillSource>, val effects: List<AbstractSkillModifier>
) {
    companion object {
        val CODEC: Codec<SkillModel> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.STRING.fieldOf("display_name").forGetter(SkillModel::displayName),
                Codec.STRING.fieldOf("icon").forGetter(SkillModel::icon),
                Codec.STRING.fieldOf("description").forGetter(SkillModel::description),
                Codec.DOUBLE.fieldOf("default_xp").forGetter(SkillModel::defaultXp),
                Codec.INT.fieldOf("max_level").forGetter(SkillModel::maxLevel),
                Codec.INT.fieldOf("unlocked_max_level").forGetter(SkillModel::unlockedMaxLevel),
                AbstractSkillSource.CODEC.listOf().optionalFieldOf("sources", listOf()).forGetter(SkillModel::skillSources),
                AbstractSkillModifier.CODEC.listOf().optionalFieldOf("effects", listOf()).forGetter(SkillModel::effects)
            ).apply(instance, ::SkillModel)
        }
    }
}
