package io.github.ytg1234.manhunt.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.github.ytg1234.manhunt.base.fromCmdContext
import io.github.ytg1234.manhunt.base.hunters
import io.github.ytg1234.manhunt.base.playerHasMod
import io.github.ytg1234.manhunt.base.speedrunnera
import io.github.ytg1234.manhunt.base.speedrunnerb
import io.github.ytg1234.manhunt.util.PermedCommand
import io.github.ytg1234.manhunt.util.plus
import io.github.ytg1234.manhunt.util.reset
import mc.aegis.AegisCommandBuilder
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.TranslatableText

/**
 * Used to control the [speedrunner].
 *
 * @author YTG1234
 */
object SpeedrunnerCommand : PermedCommand("speedrunner", "manhunt.command.speedrunner", 2) {
    override val cmd: AegisCommandBuilder.() -> AegisCommandBuilder = {
        literal("set") {
            custom(CommandManager.argument("targeta", EntityArgumentType.player())) {
                custom(CommandManager.argument("targetb", EntityArgumentType.player())) {
                    executes(::executeSet)
                }
            }
        }
        literal("get") {
            executes(::executeGet)
        }
        literal("clear") {
            executes(::executeClear)
        }
        this
    }

    /**
     * Changes the [speedrunner] to another player.
     */
    private fun executeSet(context: CommandContext<ServerCommandSource>): Int {
        val playerHasMod: Boolean = playerHasMod(context)
        val targeta = EntityArgumentType.getPlayer(context, "targeta")
        val targetb = EntityArgumentType.getPlayer(context, "targetb")
        if (hunters.contains(targeta.uuid) || hunters.contains(targetb.uuid)) {
            if (playerHasMod) {
                context.source.sendError(
                    TranslatableText(
                        "text.manhunt.command.speedrunner.error.hunter",
                        targeta.displayName
                    )
                )
            } else {
                context.source
                    .sendError(
                        reset("Cannot set speedrunner to ") + targeta.displayName + reset(" because they are a hunter!")
                    )
            }
            return Command.SINGLE_SUCCESS
        }
        speedrunnera = targeta.uuid
        speedrunnerb = targetb.uuid
        if (playerHasMod) {
            context.source
                .sendFeedback(
                    TranslatableText(
                        "text.manhunt.command.speedrunner.set",
                        fromCmdContext(context, speedrunnera)!!.displayName
                    ), true
                )
        } else {
            context.source
                .sendFeedback(
                    reset("Set speedrunner to ") + targeta.displayName + reset("!"),
                    true
                )
        }
        return Command.SINGLE_SUCCESS
    }

    /**
     * Sends the [speedrunner]'s name.
     */
    private fun executeGet(context: CommandContext<ServerCommandSource>): Int {
        val playerHasMod: Boolean = playerHasMod(context)
        if (speedrunnera == null) return 1
        if (speedrunnerb == null) return 1
        if (playerHasMod) {
            context.source
                .sendFeedback(
                    TranslatableText(
                        "text.manhunt.command.speedrunner.get",
                        fromCmdContext(context, speedrunnera)!!.displayName
                    ), false
                )
        } else {
            context.source
                .sendFeedback(
                    reset("Speedrunner is currently: ") + fromCmdContext(context, speedrunnera)!!.displayName,
                    true
                )
        }
        return Command.SINGLE_SUCCESS
    }

    /**
     * Sets the [speedrunner] to `null`
     */
    private fun executeClear(context: CommandContext<ServerCommandSource>): Int {
        val playerHasMod: Boolean = playerHasMod(context)
        speedrunnera = null
        speedrunnerb = null
        if (playerHasMod) {
            context.source.sendFeedback(TranslatableText("text.manhunt.command.speedrunner.clear"), true)
        } else {
            context.source.sendFeedback(reset("Speedrunner Cleared!"), true)
        }
        return Command.SINGLE_SUCCESS
    }
}
