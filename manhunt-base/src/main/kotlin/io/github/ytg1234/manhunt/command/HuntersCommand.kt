package io.github.ytg1234.manhunt.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.github.ytg1234.manhunt.base.CONFIG
import io.github.ytg1234.manhunt.base.UserVars.hunters
import io.github.ytg1234.manhunt.base.UserVars.speedrunners
import io.github.ytg1234.manhunt.base.fromCmdContext
import io.github.ytg1234.manhunt.base.ifHasMod
import io.github.ytg1234.manhunt.util.PermedCommand
import io.github.ytg1234.manhunt.util.plus
import io.github.ytg1234.manhunt.util.reset
import mc.aegis.AegisCommandBuilder
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText

/**
 * Used to manage the [hunter list][hunters].
 *
 * @author YTG1234
 */
object HuntersCommand : PermedCommand("hunters", "manhunt.command.hunters", 2) {
    override val cmd: AegisCommandBuilder.() -> AegisCommandBuilder = {
        literal("clear") {
            executes(::executeClear)
        }
        literal("add") {
            custom(CommandManager.argument("target", EntityArgumentType.player())) {
                executes(::executeAdd)
            }
        }
        literal("get") {
            executes(::executeGet)
        }
        this
    }

    /**
     * Clears the hunter list.
     */
    private fun executeClear(context: CommandContext<ServerCommandSource>): Int {
        hunters.clear()
        context.source.sendFeedback(
            ifHasMod(
                context,
                TranslatableText("text.manhunt.command.hunters.clear"),
                LiteralText("Cleared hunter list!")
            ),
            true
        )
        return Command.SINGLE_SUCCESS
    }

    /**
     * Adds a hunter to the hunter list.
     */
    private fun executeAdd(context: CommandContext<ServerCommandSource>): Int {
        val target = EntityArgumentType.getPlayer(context, "target")

        // Target is speedrunner
        if (speedrunners.contains(target.uuid)) {
            context.source.sendError(
                ifHasMod(
                    context,
                    TranslatableText(
                        "text.manhunt.command.hunters.error.speedrunner",
                        target.displayName
                    ),
                    LiteralText("Cannot add ") + target.displayName + LiteralText(" as a hunter because they are a speedrunner!")
                )
            )
            return Command.SINGLE_SUCCESS
        }

        // Check if target is already a hunter
        if (hunters.contains(target.uuid)) {
            context.source.sendError(
                ifHasMod(
                    context,
                    TranslatableText(
                        "text.manhunt.command.hunters.error.hunter",
                        target.displayName
                    ),
                    reset("Cannot add ") + target.displayName + reset(" as a hunter because they are already a hunter!")
                )
            )
            return Command.SINGLE_SUCCESS
        }
        if (CONFIG!!.giveCompassWhenSettingHunters) fromCmdContext(context, target.uuid)!!.equip(
            8,
            ItemStack(Items.COMPASS, 1)
        )
        hunters.add(target.uuid)

        context.source.sendFeedback(
            ifHasMod(
                context,
                TranslatableText("text.manhunt.command.hunters.add", target.displayName),
                reset("Added ") + target.displayName + reset(" to the hunters list!")
            ),
            true
        )
        return Command.SINGLE_SUCCESS
    }

    /**
     * Sends the hunter list.
     */
    private fun executeGet(context: CommandContext<ServerCommandSource>): Int {
        if (hunters.isEmpty()) return 1
        val hunterNames: Collection<String> = hunters.map {
            fromCmdContext(context, it)!!.displayName.asString()
        }

        context.source.sendFeedback(
            ifHasMod(
                context,
                TranslatableText(
                    "text.manhunt.command.hunters.get",
                    java.lang.String.join(", ", hunterNames)
                ),
                LiteralText("Hunters are: " + java.lang.String.join(", ", hunterNames))
            ),
            true
        )
        return Command.SINGLE_SUCCESS
    }
}
