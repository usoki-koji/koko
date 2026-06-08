package dev.koji.fabric.common

import dev.koji.koko.Koko
import dev.koji.koko.common.attachments.PlayerSkills
import dev.koji.koko.common.models.GroupData
import dev.koji.koko.common.models.SkillModel
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.fabricmc.fabric.api.event.registry.DynamicRegistries
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

object FabricCommonRegistry {
    val SKILL_REGISTRY: ResourceKey<Registry<SkillModel>> = ResourceKey.createRegistryKey(Koko.toPath("skills"))
    val GROUPS_REGISTRY: ResourceKey<Registry<GroupData>> = ResourceKey.createRegistryKey(Koko.toPath("groups"))

    val PLAYER_SKILLS: AttachmentType<PlayerSkills> = AttachmentRegistry.createPersistent(
        Koko.toPath("player_skills"),
        PlayerSkills.CODEC
    )

    fun register() {
        DynamicRegistries.registerSynced(SKILL_REGISTRY, SkillModel.CODEC, SkillModel.CODEC)
        DynamicRegistries.registerSynced(GROUPS_REGISTRY, GroupData.CODEC, GroupData.CODEC)
    }
}