package io.github.ytg1234.manhunt.config

/**
 * Decides the behaviour of the hunters' compass.
 *
 * @author YTG1234
 */
enum class Compass {
    /**
     * Compass Updates automatically every tick.
     */
    Update,

    /**
     * Hunter has to use compass to update it.
     */
    Use
}

/**
 * Decides the "damage mode", aka when the speedrunner dies.
 *
 * @author YTG1234
 */
enum class Damage {
    /**
     * The speedrunner dies when they take damage.
     */
    Damage,

    /**
     * The speedrunner dies when they die, like normal Minecraft.
     */
    Kill
}

/**
 * Speedrunner mode, support for multiple runners.
 *
 * @author YTG1234
 */
enum class Runners {
    /**
     * One speedrunner, Dream-like.
     */
    Dream,

    /**
     * Hunters can switch compasses using a
     * command, unlimited runners.
     */
    CompassSwitching,

    /**
     * Hunter's compass points at closest runner.
     */
    ClosestRunner
}
