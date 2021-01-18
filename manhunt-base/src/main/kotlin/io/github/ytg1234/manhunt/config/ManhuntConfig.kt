package io.github.ytg1234.manhunt.config

import me.sargunvohra.mcmods.autoconfig1u.ConfigData
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry.Gui.EnumHandler
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment
import java.util.ArrayList

/**
 * A class that holds and represents the configuration of the Manhunt mod.
 *
 * @author YTG1234
 */
@Config(name = "manhunt")
class ManhuntConfig : ConfigData {
    /**
     * Sets the behaviour of the compass mechanic, can be either [Update][Compass.Update] or [Use][Compass.Use].
     *
     * [Update][Compass.Update] = Automatically update the compass every tick.
     * [Use][Compass.Use] = Use the compass to update it (more like Dream's manhunt).
     */
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @Comment("Sets the behaviour of the compass mechanic, can be either Update or Use. Update = Automatically update the compass every tick. Use = Use the compass to update it (more like Dream's manhunt).")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @JvmField
    var compassBehaviour = Compass.Use

    /**
     * Sets the behaviour of the damage mechanic. Can be either [Kill][Damage.Kill] or [Damage][Damage.Damage].
     *
     * [Kill][Damage.Kill] = The speedrunner loses when they are killed (like Dream's manhunt).
     * [Damage][Damage.Damage] = The speedrunner loses when they take damage (like dream's assassin).
     */
    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @Comment("Sets the behaviour of the damage mechanic. Can be either Kill or Damage. Kill = The speedrunner loses when they are killed (like Dream's manhunt). Damage = The speedrunner loses when they take damage (like dream's assassin).")
    @ConfigEntry.Gui.Tooltip(count = 3)
    @JvmField
    var damageBehaviour = Damage.Kill

    /**
     * If true, gives players a compass when added to the hunters list.
     */
    @Comment("If true, gives players a compass when added to the hunters list.")
    @ConfigEntry.Gui.Tooltip
    @JvmField
    var giveCompassWhenSettingHunters = true

    // This is a new list because auto config will try to append the values in the Json file.
    /**
     * Dimensions that the compass won't work in.
     */
    @Comment("Dimensions that the compass won't work in.")
    @ConfigEntry.Gui.Tooltip
    @JvmField
    var disabledDimensions: List<String> = ArrayList()

    /**
     * Whether to apply the glowing effect to the speedrunner, similar to Dream's Survivalist
     */
    @Comment("Whether to apply the glowing effect to the speedrunner, similar to Dream's Survivalist.")
    @ConfigEntry.Gui.Tooltip
    @JvmField
    var highlightSpeedrunner = false

    @EnumHandler(option = EnumHandler.EnumDisplayOption.BUTTON)
    @Comment("Multirunner settings. Can be Dream (default), CompassSwitching or ClosestRunner. Dream = Normal Manhunt. CompassSwitching = Hunters can switch compasses using a command, unlimited runners. ClosestRunner = Hunter's compass points at closest runner.")
    @ConfigEntry.Gui.Tooltip(count = 4)
    @JvmField
    var runnerBehaviour = Runners.Dream
}
