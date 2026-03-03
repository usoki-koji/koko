package dev.koji.koko.common.attachments

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import dev.koji.koko.common.models.SkillData
import net.minecraft.resources.ResourceLocation

class PlayerSkills(newSkillsData: Map<ResourceLocation, SkillData>) {
    private val skillsData = newSkillsData.toMutableMap()

    constructor() : this(emptyMap())

    fun getSkill(skill: ResourceLocation): SkillData? = skillsData[skill]

    fun getAllSkills(): Map<ResourceLocation, SkillData> = skillsData

    fun put(skill: ResourceLocation, data: SkillData) {
        skillsData[skill] = data
    }

    fun putIfAbsent(skill: ResourceLocation, data: SkillData) {
        if (skillsData[skill] != null) return

        put(skill, data)
    }

    fun replace(newSkillsData: Map<ResourceLocation, SkillData>) {
        skillsData.clear()

        skillsData.putAll(newSkillsData)
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
