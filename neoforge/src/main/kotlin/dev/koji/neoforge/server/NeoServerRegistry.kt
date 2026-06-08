package dev.koji.neoforge.server

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.koji.koko.Koko
import dev.koji.neoforge.NeoSkillsHandler
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.ResourceLocationArgument
import net.minecraft.network.chat.Component
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent

@EventBusSubscriber(modid = Koko.MOD_ID)
object NeoServerRegistry {
    private val playerArgument = Commands.argument("player", EntityArgument.player()).executes(this::skillCommand)

    private val amountArgument = Commands.argument("amount", DoubleArgumentType.doubleArg())
        .then(playerArgument)

    private val skillArgument = Commands.argument("skill", ResourceLocationArgument.id())
        .suggests { context, builder ->
            val skillModels = NeoSkillsHandler.getSkillsModels(context.source.level) ?: emptySet()

            for (entry in skillModels) {
                builder.suggest(entry.key.location().toString())
            }

            builder.buildFuture()
        }
        .then(amountArgument)

    private val modeArgument = Commands.argument("mode", StringArgumentType.string())
        .suggests { _, builder ->
            builder.suggest("add")
            builder.suggest("remove")

            builder.buildFuture()
        }
        .then(skillArgument)

    private val kokoCommand = Commands.literal("koko")
        .requires { it.hasPermission(2) }
        .then(modeArgument)

    @SubscribeEvent
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        event.dispatcher.register(kokoCommand)
    }

    private fun skillCommand(commandStack: CommandContext<CommandSourceStack>): Int {
        val mode = StringArgumentType.getString(commandStack, "mode")
        val skill = ResourceLocationArgument.getId(commandStack, "skill")
        val amount = DoubleArgumentType.getDouble(commandStack, "amount")
        val player = EntityArgument.getPlayer(commandStack, "player")

        val multiplier = if (mode == "add") 1 else - 1

        val playerSkill = NeoSkillsHandler.getSkill(player, skill)
            ?: return 0

        val beforeXp = playerSkill.xp

        NeoSkillsHandler.updateXp(player, skill, amount * multiplier)

        commandStack.source.sendSuccess({
            Component.literal("Edited ${player.name.string} experience of $skill from $beforeXp to ${beforeXp + amount * multiplier}!")
        }, false)

        return 1
    }
}