package io.github.ytg1234.manhunt.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.github.ytg1234.manhunt.base.CONFIG
import io.github.ytg1234.manhunt.base.UserVars.hunters
import io.github.ytg1234.manhunt.base.UserVars.speedrunners
import io.github.ytg1234.manhunt.base.fromCmdContext
import io.github.ytg1234.manhunt.base.ifHasMod
import io.github.ytg1234.manhunt.config.Runners
import io.github.ytg1234.manhunt.util.PermedCommand
import io.github.ytg1234.manhunt.util.plus
import io.github.ytg1234.manhunt.util.reset
import mc.aegis.AegisCommandBuilder
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.TranslatableText

/**
 * Used to control the [speedrunners].
 *
 * @author YTG1234
 */
object SpeedrunnerCommand : PermedCommand("speedrunners", "manhunt.command.speedrunner", 2) {
    override val cmd: AegisCommandBuilder.() -> AegisCommandBuilder = {
        literal("add") {
            custom(CommandManager.argument("target", EntityArgumentType.player())) {
                executes(::executeAdd)
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

    private fun executeAdd(context: CommandContext<ServerCommandSource>): Int {
        val target = EntityArgumentType.getPlayer(context, "target")

        if (hunters.contains(target.uuid)) {
            context.source.sendError(
                ifHasMod(
                    context,
                    TranslatableText(
                        "text.manhunt.command.speedrunner.error.hunter",
                        target.displayName
                    ),
                    reset("Cannot add speedrunner ") + target.displayName + reset(" to the list because they are a hunter!")
                )
            )
        }

        if (CONFIG!!.runnerBehaviour == Runners.Dream) speedrunners.clear()
        speedrunners.add(target.uuid)
        context.source.sendFeedback(
            ifHasMod(
                context,
                TranslatableText(
                    "text.manhunt.command.speedrunner.set",
                    target.displayName
                ),
                reset("Added speedrunner ") + target.displayName + reset("!"),
            ),
            true
        )
        return Command.SINGLE_SUCCESS
    }

    /**
     * Sends the [speedrunners]'s name.
     */
    private fun executeGet(context: CommandContext<ServerCommandSource>): Int {
        if (speedrunners.isEmpty()) return 1
        val speedrunnerNames: List<String> = speedrunners.map {
            fromCmdContext(context, it)!!.displayName.asString()!!
        }

        context.source.sendFeedback(
            ifHasMod(
                context,
                TranslatableText(
                    "text.manhunt.command.speedrunner.get",
                    java.lang.String.join(", ", speedrunnerNames)
                ),
                reset("Speedrunners are currently: ") + reset(java.lang.String.join(", ", speedrunnerNames)),
            ),
            true
        )

        return Command.SINGLE_SUCCESS
    }

    /**
     * Sets the [speedrunners] to an empty list
     */
    private fun executeClear(context: CommandContext<ServerCommandSource>): Int {
        speedrunners = mutableListOf()
        context.source.sendFeedback(
            ifHasMod(
                context,
                TranslatableText("text.manhunt.command.speedrunner.clear"),
                reset("Speedrunner Cleared!")
            ),
            true
        )
        return Command.SINGLE_SUCCESS
    }
}
