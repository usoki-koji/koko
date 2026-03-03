package dev.koji.koko.common.events

import dev.koji.koko.Koko
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.boss.enderdragon.EnderDragon
import net.minecraft.world.entity.boss.wither.WitherBoss
import net.minecraft.world.entity.monster.ElderGuardian
import net.minecraft.world.entity.monster.warden.Warden
import net.minecraft.world.entity.player.Player

object EntityEventHandler {
    fun entityEvaluate(
        player: Player,
        source: String,
        entity: Entity
    ) = Koko.skillsHandler.skillCheckup(player, source, this::entityMatches, entity)

    private fun entityMatches(entity: Entity, target: String): Boolean {
        val resourceLocation = if (target.contains(":")) {
            ResourceLocation.parse(target)
        } else {
            ResourceLocation.fromNamespaceAndPath("minecraft", target)
        }

        val entityTypeKey = ResourceKey.create(Registries.ENTITY_TYPE, resourceLocation)
        val entityType = BuiltInRegistries.ENTITY_TYPE.get(entityTypeKey)

        if (entity.type == entityType) return true

        val tagKey = TagKey.create(Registries.ENTITY_TYPE, resourceLocation)

        if (entity.type.`is`(tagKey)) return true

        return when (target) {
            "boss" -> (entity is EnderDragon || entity is WitherBoss || entity is ElderGuardian || entity is Warden)
            "hostile" -> entity is Mob && entity.isAggressive
            "animal" -> entity is Animal
            "player" -> entity is Player
            "living" -> entity is LivingEntity
            else -> false
        }
    }
}