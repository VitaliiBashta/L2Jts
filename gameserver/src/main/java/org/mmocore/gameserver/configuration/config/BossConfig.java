package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;

@Configuration("boss.json")
public class BossConfig {
    public static int AntharasFixedRespawn;
    public static int AntharasRandomRespawn;
    public static int AntharasTimeUntilSleep; // 900
    public static int AntharasUptime; // 1800
    public static int AntharasLimit; // 200

    public static int ValakasFixedRespawn;
    public static int ValakasRandomRespawn;
    public static int ValakasTimeUntilSleep; // 900
    public static int ValakasUptime; // 1800
    public static int ValakasLimit; // 200

    public static int BaiumFixedRespawn;
    public static int BaiumRandomRespawn;
    public static int BaiumTimeUntilSleep; // 1800

    public static int BelethMinCcSize; // 36
    public static boolean disableBelethQuestChecks;
}
