package io.github.ytg1234.manhunt.base

import com.mojang.brigadier.context.CommandContext
import io.github.ytg1234.manhunt.api.event.callback.CompassUpdateCallback
import io.github.ytg1234.manhunt.config.ManhuntConfig
import io.github.ytg1234.manhunt.config.Runners
import io.github.ytg1234.manhunt.util.string
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffect
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.MinecraftServer
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.jetbrains.annotations.Contract
import java.util.Optional
import java.util.UUID

/**
 * Manhunt's mod id.
 */
const val MOD_ID = "manhunt"

/**
 * The name of the mod.
 */
const val MOD_NAME = "Manhunt Fabric"

/**
 * Contains the [Identifier] of the question packet.
 */
@JvmField
val SERVER_QUESTION_PACKET_ID: Identifier = Identifier(MOD_ID, "question")

/**
 * Contains the [Identifier] of the answer packet.
 */
@JvmField
val CLIENT_ANSWER_PACKET_ID: Identifier = Identifier(MOD_ID, "answer")

/**
 * Manhunt's [Log4j logger][org.apache.logging.log4j.Logger]
 */
@JvmField
val LOGGER = LogManager.getLogger(MOD_NAME)!!

/**
 * The one and only Manhunt configuration.
 */
@JvmField
var CONFIG: ManhuntConfig? = null

/**
 * Checks if a player has the Manhunt mod on the client side.
 *
 * @param context the [CommandContext] to get the player from
 *
 * @return if the player has the mod
 */
@Contract(pure = true)
fun playerHasMod(context: CommandContext<ServerCommandSource>): Boolean {
    return context.source.entity != null &&
            context.source.entity is PlayerEntity &&
            UserVars.haveMod.contains(context.source.player)
}

fun <A> ifHasMod(ctx: CommandContext<ServerCommandSource>, has: A, `else`: A): A {
    return if (playerHasMod(ctx)) has else `else`
}

/**
 * Gets a [PlayerEntity] from a [CommandContext] and an optional [UUID].
 *
 * @param ctx the context to get the server from
 * @param uuid the UUID of the player to get
 *
 * @return the nullable [ServerPlayerEntity] object if the optional is present, `null` otherwise.
 */
@Contract(pure = true)
fun fromCmdContext(ctx: CommandContext<ServerCommandSource>, uuid: Optional<UUID>) =
    fromServer(ctx.source.minecraftServer, uuid)

/**
 * Gets a [PlayerEntity] from a [CommandContext] and a [UUID].
 *
 * @param ctx the context to get the server from
 * @param uuid the UUID of the player to get
 *
 * @return a nullable [ServerPlayerEntity] object that will contain the player is found.
 */
@Contract(pure = true)
fun fromCmdContext(ctx: CommandContext<ServerCommandSource>, uuid: UUID) = fromServer(ctx.source.minecraftServer, uuid)

/**
 * Gets a [PlayerEntity] that is connected to a [MinecraftServer]
 * using an optional [UUID].
 *
 * @param server the server to get the player from
 * @param uuid the UUID of the player
 *
 * @return the nullable [ServerPlayerEntity] object if optional is present, otherwise `null`
 */
@Contract(pure = true)
fun fromServer(server: MinecraftServer, uuid: Optional<UUID>): ServerPlayerEntity? {
    return if (!uuid.isPresent) null else fromServer(server, uuid.get())
}

/**
 * Gets a [PlayerEntity] that is connected to a [MinecraftServer]
 * using their [UUID].
 *
 * @param server the server to get the player from
 * @param uuid the UUID of the player
 *
 * @return the [ServerPlayerEntity] object
 */
@Contract(pure = true)
fun fromServer(server: MinecraftServer, uuid: UUID): ServerPlayerEntity? = server.playerManager.getPlayer(uuid)

/**
 * Receives an [ItemStack] and a [ServerPlayerEntity], assuming
 * the [ItemStack] is a compass updates it to point to the [ServerPlayerEntity].
 *
 * @param compass the compass to update
 * @param target the target that the compass shall point to
 *
 * @return the updated [ItemStack], without mutating the first param
 */
@Contract(pure = true)
fun updateCompass(compass: ItemStack, target: ServerPlayerEntity?): ItemStack {
    // Is target null?
    if (target == null) {
        LOGGER.warn("Compass target is null, cannot update compass! Please fix!")
        return compass.copy()
    }
    // Is dimension disabled?
    if (CONFIG!!.disabledDimensions.contains(target.serverWorld.registryKey.value.string)) return compass.copy()

    // Continue Updating
    val oldCompass = compass.copy()
    var newCompass = compass.copy()
    val itemTag = newCompass.orCreateTag.copy()
    itemTag.putBoolean("LodestoneTracked", false)
    itemTag.putString("LodestoneDimension", target.serverWorld.registryKey.value.string)
    val lodestonePos = CompoundTag()
    lodestonePos.putInt("X", target.x.toInt())
    lodestonePos.putInt("Y", target.y.toInt())
    lodestonePos.putInt("Z", target.z.toInt())
    itemTag.put("LodestonePos", lodestonePos)
    newCompass.tag = itemTag
    newCompass = CompassUpdateCallback.EVENT.invoker().onCompassUpdate(oldCompass, newCompass)
    return newCompass
}

fun decideUpdate(hunter: ServerPlayerEntity, compass: ItemStack): ItemStack {
    return when (CONFIG!!.runnerBehaviour) {
        Runners.Dream -> updateCompass(compass, fromServer(hunter.server, UserVars.speedrunner))
        Runners.CompassSwitching -> updateCompass(
            compass,
            if (UserVars.trackMap[hunter.uuid] != null) fromServer(hunter.server, UserVars.trackMap[hunter.uuid]!!) else null
        )
        Runners.ClosestRunner -> updateCompass(compass, getClosestPlayer(hunter))
    }
}

fun getClosestPlayer(player: ServerPlayerEntity): ServerPlayerEntity {
    TODO()
}

/**
 * Applies the specified status effect to a player for 2 ticks.
 *
 * @param player the player to apply the effect to
 * @param effect the effect to apply
 *
 * @return whether the effect applied
 *
 * @see LivingEntity.addStatusEffect
 */
@Contract(mutates = "param1")
fun applyStatusEffectToPlayer(player: PlayerEntity, effect: StatusEffect?): Boolean {
    return player.addStatusEffect(StatusEffectInstance(effect, 2, 0, false, false))
}

object UserVars {
    /**
     * Contains all the currently active hunters.
     */
    @JvmField
    val hunters: MutableList<UUID> = mutableListOf()

    /**
     * Contains the currently active speedrunners.
     */
    @JvmField
    var speedrunners: MutableList<UUID> = mutableListOf()

    /**
     * Gets the currently active speedrunner (or empty) if singular,
     * throws an exception otherwise.
     */
    @JvmStatic
    val speedrunner: Optional<UUID>
        get() = when (CONFIG!!.runnerBehaviour) {
            Runners.Dream -> if (speedrunners.isEmpty()) Optional.empty() else Optional.of(speedrunners[0])
            else -> throw IllegalStateException("Cannot get singular speedrunner when mode is not Dream!")
        }

    private val whosTracking: MutableMap<UUID, UUID> = mutableMapOf()
    val trackMap: MutableMap<UUID, UUID>
        get() = when (CONFIG!!.runnerBehaviour) {
            Runners.CompassSwitching -> whosTracking
            else -> throw IllegalStateException("Cannot get tracking map when not in switching mode!")
        }


    /**
     * Contains every [player][PlayerEntity] that has the mod
     * on their client.
     */
    @JvmField
    val haveMod: MutableList<PlayerEntity> = mutableListOf()
}
