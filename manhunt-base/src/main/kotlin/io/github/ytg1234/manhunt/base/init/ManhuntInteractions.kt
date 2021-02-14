package io.github.ytg1234.manhunt.base.init

import io.github.ytg1234.manhunt.base.CONFIG
import io.github.ytg1234.manhunt.base.fromServer
import io.github.ytg1234.manhunt.base.hunters
import io.github.ytg1234.manhunt.base.speedrunnera
import io.github.ytg1234.manhunt.base.speedrunnerb
import io.github.ytg1234.manhunt.base.updateCompass
import io.github.ytg1234.manhunt.config.Compass
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.CompassItem
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.world.World

/**
 * Handles Manhunt's interaction events.
 *
 * @author YTG1234
 */
object ManhuntInteractions {
    /**
     * Turns a hunters' compass when clicked.
     *
     * @param user the hunter
     * @param world the world the user is in
     * @param hand the hand the user used to click
     *
     * @see net.fabricmc.fabric.api.event.player.UseItemCallback.interact
     */
    fun pointCompassA(user: PlayerEntity, world: World, hand: Hand): TypedActionResult<ItemStack?>? {
        if (user.getStackInHand(hand).item !is CompassItem) return TypedActionResult.pass(user.getStackInHand(hand))

        // If user is not sneaking we don't need
        if (CONFIG!!.compassBehaviour == Compass.USE &&
            hunters.contains(user.uuid)
        ) {
            // On the client we'll just return
            if (!world.isClient()) {
                val stack = user.getStackInHand(hand)
                if (stack.item == Items.COMPASS) {
                    user.equip(
                        8,
                        updateCompass(stack, fromServer(user.server!!, speedrunnera))
                    )
                }
            }
            return TypedActionResult.success(user.getStackInHand(hand), world.isClient())
        }
        return TypedActionResult.pass(user.getStackInHand(hand))
    }

    fun pointCompassB(user: PlayerEntity, world: World, hand: Hand, blockPos: BlockPos, direction: Direction): ActionResult? {
        if (user.getStackInHand(hand).item !is CompassItem) return ActionResult.PASS

        // If user is not sneaking we don't need
        if (CONFIG!!.compassBehaviour == Compass.USE &&
            hunters.contains(user.uuid)
        ) {
            // On the client we'll just return
            if (!world.isClient()) {
                val stack = user.getStackInHand(hand)
                if (stack.item == Items.COMPASS) {
                    user.equip(
                        8,
                        updateCompass(stack, fromServer(user.server!!, speedrunnerb))
                    )
                }
            }
            return ActionResult.SUCCESS
        }
        return ActionResult.PASS
    }
}
