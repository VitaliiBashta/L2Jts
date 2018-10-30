package org.mmocore.gameserver.configuration.config;

import org.mmocore.commons.configuration.annotations.Configuration;
import org.mmocore.commons.configuration.annotations.Setting;

/**
 * Create by Mangol on 28.11.2015.
 */
@Configuration("ext.json")
public class ExtConfig {
    @Setting(name = "NewPetitionSystem")
    public static boolean EX_NEW_PETITION_SYSTEM;

    @Setting(name = "JapanMinigame")
    public static boolean EX_JAPAN_MINIGAME;

    @Setting(name = "LectureMark")
    public static boolean EX_LECTURE_MARK;

    @Setting(name = "UseTeleportFlag")
    public static boolean EX_USE_TELEPORT_FLAG;

    @Setting(name = "ClientLanguageCheck")
    public static boolean ALT_ENABLE_CLIENT_LANGUAGE_CHECK;

    @Setting(name = "2ndPasswordCheck")
    public static boolean EX_2ND_PASSWORD_CHECK;

    @Setting(name = "EnableBotReporting")
    public static boolean EX_ENABLE_AUTO_HUNTING_REPORT;

    @Setting(name = "DefaultReportPoints")
    public static int DEFAULT_COUNT_REPORT_POINTS;

    @Setting(name = "ChangeNameDialogIfIncorrect")
    public static boolean EX_CHANGE_NAME_DIALOG;

    @Setting(name = "GoodsInventoryEnable")
    public static boolean EX_GOODS_INVENTORY_ENABLED;
}
