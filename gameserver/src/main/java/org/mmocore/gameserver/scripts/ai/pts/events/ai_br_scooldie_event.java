package org.mmocore.gameserver.scripts.ai.pts.events;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.scripts.events.NcSoft.JackGame.JackGame;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_br_scooldie_event extends DefaultAI {
    private static final int br_halloween_candy = 20706;
    private static final int br_scroll_of_scooldie = 20709;
    private static final int br_box_halloween_mask = 20710;
    private static final int br_jack0_mask = 20711;

    public ai_br_scooldie_event(NpcInstance actor) {
        super(actor);
        if (actor.getParameter("nickname", "") != null && !actor.getParameter("nickname", "").isEmpty()) {
            final String newTitle = actor.getParameter("nickname", "");
            actor.setTitle(newTitle);
            actor.broadcastCharInfo();
        }
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        final String newTitle = getActor().getParameter("nickname", "");
        getActor().setTitle(newTitle);
        getActor().broadcastCharInfo();
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        if (ask == 50001) {
            if (reply == 1) {
                if (ItemFunctions.getItemCount(player, br_halloween_candy) >= 10) {
                    ItemFunctions.addItem(player, br_box_halloween_mask, 1);
                    ItemFunctions.removeItem(player, br_halloween_candy, 10);
                    actor.showChatWindow(player, "pts/events/jack_game/br_halloween_scooldie002.htm");
                } else {
                    actor.showChatWindow(player, "pts/events/jack_game/br_halloween_scooldie003.htm");
                }
            } else if (reply == 2) {
                JackGame.getEventRankerInfo(player, 10);
            } else if (reply == 3) {
                if (ItemFunctions.getItemCount(player, br_jack0_mask) > 0 && ItemFunctions.getItemCount(player, br_scroll_of_scooldie) > 0) {
                    actor.showChatWindow(player, "pts/events/jack_game/br_halloween_scooldie008.htm");
                } else {
                    if (ItemFunctions.getItemCount(player, br_jack0_mask) == 0) {
                        ItemFunctions.addItem(player, br_jack0_mask, 1);
                    }
                    if (ItemFunctions.getItemCount(player, br_scroll_of_scooldie) == 0) {
                        ItemFunctions.addItem(player, br_scroll_of_scooldie, 1);
                    }
                    actor.showChatWindow(player, "pts/events/jack_game/br_halloween_scooldie004.htm");
                }
            }
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}