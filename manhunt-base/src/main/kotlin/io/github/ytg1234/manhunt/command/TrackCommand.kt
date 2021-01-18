package io.github.ytg1234.manhunt.command

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import io.github.ytg1234.manhunt.base.CONFIG
import io.github.ytg1234.manhunt.base.UserVars
import io.github.ytg1234.manhunt.base.ifHasMod
import io.github.ytg1234.manhunt.config.Runners
import io.github.ytg1234.manhunt.util.PermedCommand
import mc.aegis.AegisCommandBuilder
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.LiteralText
import net.minecraft.text.TranslatableText

object TrackCommand : PermedCommand("track", "manhunt.command.track", 0) {
    override val cmd: AegisCommandBuilder.() -> AegisCommandBuilder = {
        custom(CommandManager.argument("target", EntityArgumentType.player())) {
            executes(::execute)
        }
        this
    }

    private fun execute(context: CommandContext<ServerCommandSource>): Int {
        if (CONFIG!!.runnerBehaviour != Runners.CompassSwitching) {
            context.source.sendError(
                ifHasMod(
                    context,
                    TranslatableText("text.manhunt.command.track.error.mode"),
                    LiteralText(
                        "The runner mode isn't CompassSwitching!"
                    )
                )
            )

            return 0
        }

        val target = EntityArgumentType.getPlayer(context, "target")!!
        UserVars.trackMap[context.source.player.uuid] = target.uuid

        context.source.sendFeedback(
            ifHasMod(
                context,
                TranslatableText("text.manhunt.command.track.success"),
                LiteralText("Now tracking!")
            ),
            true
        )
        return Command.SINGLE_SUCCESS
    }
}
