package dev.koji.koko.common.attachments

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.koji.koko.Loggable
import dev.koji.koko.common.models.SkillData
import net.minecraft.resources.ResourceLocation
import org.jetbrains.annotations.ApiStatus

data class PlayerSkills(private val skillsData: Map<ResourceLocation, SkillData>) : Loggable {
    constructor() : this(emptyMap())

    fun getSkill(skill: ResourceLocation): SkillData? = skillsData[skill]

    fun getAllSkills(): Map<ResourceLocation, SkillData> = skillsData

    @ApiStatus.Internal
    fun put(skill: ResourceLocation, data: SkillData): PlayerSkills {
        val newSkillsData = skillsData.toMutableMap()

        newSkillsData[skill] = data

        return this.copy(skillsData = newSkillsData)
    }

    @ApiStatus.Internal
    fun putIfAbsent(skill: ResourceLocation, data: SkillData): PlayerSkills {
        if (skillsData[skill] != null) return this

        return this.put(skill, data)
    }

    fun updateSkill(skill: ResourceLocation, xp: Double? = null, isUnlocked: Boolean? = false): PlayerSkills {
        val currentData = this.getSkill(skill)

        return this.put(skill, SkillData(
            xp = xp ?: (currentData?.xp ?: 0.0),
            isUnlocked = isUnlocked ?: (currentData?.isUnlocked ?: false)
        ))
    }

    companion object {
        val CODEC: Codec<PlayerSkills> = RecordCodecBuilder.create { instance ->
            instance.group(
                Codec.unboundedMap(ResourceLocation.CODEC, SkillData.CODEC)
                    .fieldOf("skills").forGetter { it.getAllSkills() }
            ).apply(instance, ::PlayerSkills)
        }
    }
}
