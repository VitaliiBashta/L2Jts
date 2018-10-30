package org.mmocore.gameserver.scripts.npc.model.monas;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.model.entity.events.impl.MonasteryOfSilenceMiniGameEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date 16:51/30.04.2012
 */
public class SumielInstance extends NpcInstance {
    public SumielInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        final MonasteryOfSilenceMiniGameEvent event = getEvent(MonasteryOfSilenceMiniGameEvent.class);

        switch (command) {
            case "start":
                if (event.isInProgress()) {
                    if (player != event.getPlayer()) {
                        showChatWindow(player, "pts/events/monastery_of_silence/minigame_instructor004.htm");
                        return;
                    }

                    if (event.isFailed()) {
                        if (event.getFailCount() == 1) {
                            showChatWindow(player, "pts/events/monastery_of_silence/minigame_instructor002.htm");
                        } else {
                            showChatWindow(player, "pts/events/monastery_of_silence/minigame_instructor003.htm");
                        }

                        return;
                    }
                }

                if (event.getLastWinTime() > System.currentTimeMillis()) {
                    showChatWindow(player, "pts/events/monastery_of_silence/minigame_instructor008.htm");
                    return;
                }

                if (ItemFunctions.getItemCount(player, MonasteryOfSilenceMiniGameEvent.NEED_ITEM_ID) == 0) {
                    showChatWindow(player, "pts/events/monastery_of_silence/minigame_instructor005.htm");
                    return;
                }

                event.setPlayer(player);
                event.reCalcNextTime(false);
                break;
            case "restart":
                if (!event.isInProgress() || event.getPlayer() != player || event.getLastWinTime() > System.currentTimeMillis()) {
                    return;
                }

                event.restart();
                break;
            case "outLoc":
                player.teleToLocation(Location.parseLoc(getParameter("outLoc", StringUtils.EMPTY)));
                break;
            case "grymore":
                player.teleToLocation(118833, -80589, -2688);
                break;
            default:
                super.onBypassFeedback(player, command);
                break;
        }
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        final MonasteryOfSilenceMiniGameEvent event = getEvent(MonasteryOfSilenceMiniGameEvent.class);
        if (event.isInProgress()) {
            if (player != event.getPlayer()) {
                showChatWindow(player, "pts/events/monastery_of_silence/minigame_instructor004.htm");
            } else {
                if (event.isFailed()) {
                    if (event.getFailCount() == 1) {
                        showChatWindow(player, "pts/events/monastery_of_silence/minigame_instructor002.htm");
                    } else {
                        showChatWindow(player, "pts/events/monastery_of_silence/minigame_instructor003.htm");
                    }
                } else {
                    showChatWindow(player, "pts/events/monastery_of_silence/minigame_instructor007.htm");
                }
            }
        } else {
            if (event.getLastWinTime() > System.currentTimeMillis()) {
                showChatWindow(player, "pts/events/monastery_of_silence/minigame_instructor008.htm");
            } else {
                showChatWindow(player, "pts/events/monastery_of_silence/minigame_instructor001.htm");
            }
        }
    }
}