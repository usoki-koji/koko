package dev.koji.neoforge.compact.curios

import dev.koji.neoforge.NeoKokoConfig
import dev.koji.koko.common.content.Translatable
import dev.koji.koko.common.events.PlayerEventHandler
import dev.koji.koko.common.helpers.MainHelper
import dev.koji.koko.common.models.modifiers.AbstractSkillModifier
import dev.koji.koko.common.models.sources.AbstractSkillSource
import dev.koji.neoforge.compact.curios.modifiers.CuriosEquipSkillEffect
import dev.koji.neoforge.compact.curios.sources.CuriosTickSource
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.neoforge.common.util.TriState
import net.neoforged.neoforge.event.tick.PlayerTickEvent
import top.theillusivec4.curios.api.CuriosApi
import top.theillusivec4.curios.api.event.CurioCanEquipEvent

object CuriosCompact {
    private val BLOCKED_PLAYER_INSTANCES = mutableSetOf<PlayerEventHandler.BlockedPlayerInstance>()

    //TODO
    @SubscribeEvent
    fun onPlayerTick(event: PlayerTickEvent.Post) {
        val player = event.entity

        if (player.level().isClientSide) return

        val optional = CuriosApi.getCuriosInventory(player)

        if (optional.isEmpty) return

        val inventory = optional.get()
        val equippedCurios = inventory.equippedCurios

        for (i in 1..equippedCurios.slots) {
            val curioAt = equippedCurios.getStackInSlot(i)

            if (curioAt == ItemStack.EMPTY) continue

            if (!PlayerEventHandler.isItemBlockedFor(player, curioAt, PlayerEventHandler.PlayerBlockScope.CURIOS)) continue

            player.addEffect(MobEffectInstance(
                MobEffects.MOVEMENT_SLOWDOWN,
                20, 10
            ))

            MainHelper.sendMessageToPlayer(player, DefaultCuriosMessages.UNABLE_TO_WEAR)

            break
        }
    }
    //TODO
    @SubscribeEvent
    fun onCuriosSlotEquipTry(event: CurioCanEquipEvent) {
        val player = (event.entity as? Player) ?: return

        if (!PlayerEventHandler.isItemBlockedFor(player, event.stack, PlayerEventHandler.PlayerBlockScope.CURIOS)) return

        event.equipResult = TriState.FALSE

        if (!player.level().isClientSide) return

        MainHelper.sendMessageToPlayer(player, DefaultCuriosMessages.UNABLE_TO_WEAR)
    }
    //TODO
    fun register() {
        AbstractSkillSource.registerCodec(Sources.PLAYER_CURIOUS_USE, CuriosTickSource.Companion.CODEC)
        AbstractSkillModifier.registerCodec(Modifiers.PLAYER_CURIOS_EQUIP, CuriosEquipSkillEffect.Companion.CODEC)
    }

    object Sources {
        const val PLAYER_CURIOUS_USE = "player/curios_use"
    }

    object Modifiers {
        const val PLAYER_CURIOS_EQUIP = "player/curios_equip"
    }

    object DefaultCuriosMessages {
        val UNABLE_TO_WEAR = NeoKokoConfig.getMessageConfig(Translatable.MESSAGES_CURIOS_UNABLE_TO_WEAR)
    }
}