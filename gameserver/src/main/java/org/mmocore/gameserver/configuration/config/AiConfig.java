package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

/**
 * Create by Mangol on 28.11.2015.
 */
@Configuration("ai.json")
public class AiConfig {
    @Setting(name = "AiTaskManagers")
    public static int AI_TASK_MANAGER_COUNT;

    @Setting(name = "AiTaskDelay")
    public static long AI_TASK_ATTACK_DELAY;

    @Setting(name = "AiTaskActiveDelay")
    public static long AI_TASK_ACTIVE_DELAY;

    @Setting(name = "BlockActiveTasks")
    public static boolean BLOCK_ACTIVE_TASKS;

    @Setting(name = "AlwaysTeleportHome")
    public static boolean ALWAYS_TELEPORT_HOME;

    @Setting(name = "RndWalk")
    public static boolean RND_WALK;

    @Setting(name = "RndWalkRate")
    public static int RND_WALK_RATE;

    @Setting(name = "MaxDriftRange")
    public static int MAX_DRIFT_RANGE;

    @Setting(name = "RndAnimationRate")
    public static int RND_ANIMATION_RATE;

    @Setting(name = "AggroCheckInterval")
    public static int AGGRO_CHECK_INTERVAL;

    @Setting(name = "NonAggroTimeOnTeleport")
    public static long NONAGGRO_TIME_ONTELEPORT;

    @Setting(name = "MaxPursueUndergoundRange")
    public static int MAX_PURSUE_UNDERGROUND_RANGE;

    @Setting(name = "MaxPursueRange")
    public static int MAX_PURSUE_RANGE;

    @Setting(name = "MaxPursueRangeRaid")
    public static int MAX_PURSUE_RANGE_RAID;

    public static int TotemSummonTime;
}
