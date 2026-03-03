package dev.koji.koko.common.events

import dev.koji.koko.Koko
import dev.koji.koko.common.content.Translatable
import dev.koji.koko.common.helpers.MainHelper
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.TagKey
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import java.util.*

object PlayerEventHandler {
    private val BLOCKED_PLAYER_INSTANCES = mutableSetOf<BlockedPlayerInstance>()

    fun playerEvaluate(
        player: Player, source: String, item: ItemStack
    ) = Koko.skillsHandler.skillCheckup(player, source, this::itemMatches, item)

    fun isItemBlockedFor(player: Player, item: ItemStack, scope: PlayerBlockScope): Boolean =
        this.isItemBlockedFor(player.uuid, item, scope)

    fun isItemBlockedFor(uuid: UUID, item: ItemStack, scope: PlayerBlockScope): Boolean {
        if (item.isEmpty) return false

        val blockedRecipes = this.getBlockedItemsInScope(uuid, scope)

        val isBlocked = blockedRecipes.find { this.itemMatches(item, it.location) }

        return (isBlocked != null)
    }

    fun isResourceBlockedFor(player: Player, resource: ResourceLocation, scope: PlayerBlockScope): Boolean =
        this.isResourceBlockedFor(player.uuid, resource, scope)

    fun isResourceBlockedFor(uuid: UUID, resource: ResourceLocation, scope: PlayerBlockScope): Boolean {
        val blockedRecipes = this.getBlockedItemsInScope(uuid, scope)

        val isBlocked = blockedRecipes.find { resource == it.location }

        return (isBlocked != null)
    }

    fun getBlockedItemsInScope(uuid: UUID, scope: PlayerBlockScope): Set<BlockedItem> {
        val blockedItems = BLOCKED_PLAYER_INSTANCES.find { it.uuid == uuid }
            ?.blockedItems
            ?.filter { it.scopes.contains(scope) }
            ?.toSet()

        return blockedItems ?: emptySet()
    }

    fun getBlockedPlayerInstance(player: Player) =
        this.getBlockedPlayerInstance(player.uuid)

    fun getBlockedPlayerInstance(uuid: UUID): BlockedPlayerInstance {
        var foundInstance = BLOCKED_PLAYER_INSTANCES.find {
            it.uuid == uuid
        }

        if (foundInstance == null) {
            foundInstance = BlockedPlayerInstance(uuid, mutableSetOf())

            BLOCKED_PLAYER_INSTANCES.add(foundInstance)
        }

        return foundInstance
    }

    fun checkPlayerArmor(player: Player) {
        val playerInventory = player.inventory

        for (i in 36..39) {
            val armor = playerInventory.getItem(i)

            if (!this.isItemBlockedFor(player, armor, PlayerBlockScope.ARMOR)) continue

            player.addEffect(MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                20, 3
            ))

            MainHelper.sendMessageToPlayer(player, DefaultPlayerMessages.UNABLE_TO_ARMOR)
        }
    }

    fun addBlockedItem(uuid: UUID, item: ResourceLocation, scope: PlayerBlockScope) {
        val blockedItems = this.getBlockedPlayerInstance(uuid).blockedItems
        var blockedItem = blockedItems.find { it.location == item }

        if (blockedItem == null) {
            blockedItem = BlockedItem(item, mutableSetOf())

            blockedItems.add(blockedItem)
        }

        blockedItem.scopes.add(scope)
    }

    fun removeBlockedItem(uuid: UUID, item: ResourceLocation, scope: PlayerBlockScope) {
        val blockedItem = this.getBlockedPlayerInstance(uuid).blockedItems.find { it.location == item }

        if (blockedItem == null) return

        blockedItem.scopes.removeIf { it == scope }
    }

    fun clearBlockedItemsFor(uuid: UUID, scope: PlayerBlockScope) {
        for (blockedItem in this.getBlockedPlayerInstance(uuid).blockedItems) {
            blockedItem.scopes.removeIf { it == scope }
        }
    }

    fun clearAllBlockedItemsFor(uuid: UUID) = this.getBlockedPlayerInstance(uuid).blockedItems.clear()

    fun itemMatches(item: ItemStack, targetLocation: String): Boolean =
        this.itemMatches(item, MainHelper.safeParseResource(targetLocation))

    fun itemMatches(item: ItemStack, resourceLocation: ResourceLocation): Boolean {
        if (item.`is`(TagKey.create(Registries.ITEM, resourceLocation))) return true

        val keyOptional = item.itemHolder.unwrapKey()

        if (keyOptional.isEmpty) return false

        return keyOptional.get().location() == resourceLocation
    }

    private fun spawnDenyParticles(player: Player) {
        val level = player.level() as ServerLevel

        level.sendParticles(
            ParticleTypes.SMOKE, player.x, player.y + 1, player.z, 20, 0.5, 0.5, 0.5, 0.05
        )
    }

    private fun playDenySound(player: Player) {
        val level = player.level()

        level.playSound(null, player.blockPosition(), SoundEvents.ANVIL_DESTROY, SoundSource.PLAYERS)
        level.playSound(null, player.blockPosition(), SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS)
    }

    data class BlockedPlayerInstance(
        val uuid: UUID,
        val blockedItems: MutableSet<BlockedItem>,
    ) {
        override fun equals(other: Any?): Boolean {
            return other is BlockedPlayerInstance && other.uuid == this.uuid
        }

        override fun hashCode(): Int = uuid.hashCode()
    }

    data class BlockedItem(val location: ResourceLocation, val scopes: MutableSet<PlayerBlockScope>)

    enum class PlayerBlockScope { USE, ATTACK, CONSUME, CRAFT, FORGE, ARMOR, CURIOS, ISPELL }

    object DefaultPlayerMessages {
        val UNABLE_TO_USE = Koko.config.getMessageConfig(Translatable.MESSAGES_UNABLE_TO_USE)
        val UNABLE_TO_ATTACK = Koko.config.getMessageConfig(Translatable.MESSAGES_UNABLE_TO_ATTACK)
        val UNABLE_TO_CONSUME = Koko.config.getMessageConfig(Translatable.MESSAGES_UNABLE_TO_CONSUME)
        val UNABLE_TO_CRAFT = Koko.config.getMessageConfig(Translatable.MESSAGES_UNABLE_TO_CRAFT)
        val UNABLE_TO_FORGE = Koko.config.getMessageConfig(Translatable.MESSAGES_UNABLE_TO_FORGE)
        val UNABLE_TO_ARMOR = Koko.config.getMessageConfig(Translatable.MESSAGES_UNABLE_TO_ARMOR)
    }
}