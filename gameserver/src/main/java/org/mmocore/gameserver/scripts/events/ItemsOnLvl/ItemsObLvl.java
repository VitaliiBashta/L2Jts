package org.mmocore.gameserver.scripts.events.ItemsOnLvl;

import org.mmocore.gameserver.configuration.config.EventsConfig;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.listener.actor.player.OnLevelUpListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ConfirmDlg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.PlayerListenerList;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Трик on 16.05.2017.
 */
public class ItemsObLvl extends Functions implements OnInitScriptListener {

    private static final Logger logger = LoggerFactory.getLogger(ItemsObLvl.class);
    private static final OnLevelUpListenerImpl playerOnLevelUp = new OnLevelUpListenerImpl();
    private static String[] LEVELS_REWARD_DIAPAZON_STR = EventsConfig.EVENT_IOLU_LvlsForReward;
    private static List<Integer> LEVELS_REWARD_DIAPASON_MIN = new ArrayList<>();
    private static List<Integer> LEVELS_REWARD_DIAPASON_MAX = new ArrayList<>();
    private static int[] LEVELS_REWARD_ITEMS = EventsConfig.EVENT_IOLU_LvlsForRewardItems;
    private static int[] LEVELS_REWARD_COUNT = EventsConfig.EVENT_IOLU_LvlsForRewardCount;
    private static String[] LEVELS_REWARD_HTM = EventsConfig.EVENT_IOLU_LvlsForRewardHtm;
    private static String[] LEVELS_TELEPORT_DIAPAZON_STR = EventsConfig.EVENT_IOLU_LvlsForTeleport;
    private static List<Integer> LEVELS_TELEPORT_DIAPASON_MIN = new ArrayList<>();
    private static List<Integer> LEVELS_TELEPORT_DIAPASON_MAX = new ArrayList<>();
    private static String[] LOC_TELEPORT = EventsConfig.EVENT_IOLU_LocForTeleport;
    private static String[] LOC_NAME_TELEPORT = EventsConfig.EVENT_IOLU_LocNameForTeleport;
    private static boolean active = EventsConfig.EVENT_ItemOnLevelUpActive;
    private static Functions functions = new Functions();

    /**
     * Читает статус эвента из конфига.
     *
     * @return
     */
    private static boolean isActive() {
        return active;
    }

    /**
     * Окно с предложением телепортироваться
     *
     * @param player
     */

    private static void teleportByAsk(Player player, String str_loc, int lvl_min, int lvl_max) {
        ConfirmDlg ask = new ConfirmDlg(SystemMsg.S1, 30000);
        ask.addString(player.isLangRus() ? "Желаете ли вы телепортироваться в Зона " + lvl_min + "-" + lvl_max + "?" : "Do you want to teleport in  Zone " + lvl_min + "-" + lvl_max + "?");
        int[] loc = Arrays.stream(str_loc.split(":")).mapToInt(Integer::parseInt).toArray();
        player.ask(ask, new OnAnswerListener() {
            @Override
            public void sayYes() {
                ServerVariables.set("IOLU_TP." + lvl_min + "-" + lvl_max + "." + player.getObjectId(), "on");
                player.teleToLocation(loc[0], loc[1], loc[2]);
                player.sendMessage(new CustomMessage("teleport.point.success.location").addString("Зона " + lvl_min + "-" + lvl_max));
            }
        });
    }

    @Override
    public void onInit() {
        if (isActive()) {
            PlayerListenerList.addGlobal(playerOnLevelUp);
            for (String str : LEVELS_REWARD_DIAPAZON_STR) {
                String[] arr_str = str.split("-");
                LEVELS_REWARD_DIAPASON_MIN.add(Integer.parseInt(arr_str[0]));
                LEVELS_REWARD_DIAPASON_MAX.add(Integer.parseInt(arr_str[1]));
            }
            for (String str : LEVELS_TELEPORT_DIAPAZON_STR) {
                String[] arr_str = str.split("-");
                LEVELS_TELEPORT_DIAPASON_MIN.add(Integer.parseInt(arr_str[0]));
                LEVELS_TELEPORT_DIAPASON_MAX.add(Integer.parseInt(arr_str[1]));
            }
            logger.info("Loaded Event: Item On Level [state: activated]");
        } else {
            logger.info("Loaded Event: Item On Level [state: deactivated]");
        }
    }

    /**
     * Обработка при получении лвла
     */
    private static class OnLevelUpListenerImpl implements OnLevelUpListener {
        @Override
        public void onLevelUp(Player player, int level) {
            boolean forbidden = false;
            for (int i = 0; i < LEVELS_REWARD_DIAPAZON_STR.length; i++) {
                forbidden = ServerVariables.getString("IOLU_ITEM." + LEVELS_REWARD_DIAPASON_MIN.get(i) + "-" + LEVELS_REWARD_DIAPASON_MAX.get(i) + "." + player.getObjectId(), "off").equalsIgnoreCase("on");
                if (LEVELS_REWARD_DIAPASON_MIN.get(i) <= level && LEVELS_REWARD_DIAPASON_MAX.get(i) >= level && !forbidden) {
                    ItemFunctions.addItem(player, LEVELS_REWARD_ITEMS[i], LEVELS_REWARD_COUNT[i]);
                    functions.show(LEVELS_REWARD_HTM[i], player);
                    ServerVariables.set("IOLU_ITEM." + LEVELS_REWARD_DIAPASON_MIN.get(i) + "-" + LEVELS_REWARD_DIAPASON_MAX.get(i) + "." + player.getObjectId(), "on");
                    break;
                }
            }

            for (int i = 0; i < LEVELS_TELEPORT_DIAPAZON_STR.length; i++) {
                forbidden = ServerVariables.getString("IOLU_TP." + LEVELS_TELEPORT_DIAPASON_MIN.get(i) + "-" + LEVELS_TELEPORT_DIAPASON_MAX.get(i) + "." + player.getObjectId(), "off").equalsIgnoreCase("on");
                if (LEVELS_TELEPORT_DIAPASON_MIN.get(i) <= level && LEVELS_TELEPORT_DIAPASON_MAX.get(i) >= level && !forbidden) {
                    teleportByAsk(player, LOC_TELEPORT[i], LEVELS_TELEPORT_DIAPASON_MIN.get(i), LEVELS_TELEPORT_DIAPASON_MAX.get(i));
                    return;
                }
            }
        }
    }
}
