package org.mmocore.gameserver.scripts.ai.pts.events;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_br_friendly_sno_k extends DefaultAI {
    public ai_br_friendly_sno_k(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        if (ask == 50008) {
            if (reply == 1) {
                player.teleToLocation(-119902, 44293, 360);
            } else if (reply == 2) {
                player.teleToLocation(-118848, 43229, 544);
            } else if (reply == 3) {
                player.teleToLocation(-117347, 43260, 544);
            } else if (reply == 4) {
                player.teleToLocation(-114979, 44930, 512);
            } else if (reply == 5) {
                player.teleToLocation(-121244, 54282, -1464);
            } else if (reply == 6) {
                player.teleToLocation(-124630, 78822, -3520);
            } else if (reply == 7) {
                player.teleToLocation(-113830, 65181, -2640);
            } else if (reply == 8) {
                player.teleToLocation(-92738, 38872, -2464);
            } else if (reply == 9) {
                player.teleToLocation(-89340, 47096, -2856);
            } else if (reply == 10) {
                player.teleToLocation(20891, 140881, -3464);
            } else if (reply == 11) {
                player.teleToLocation(-50576, 145859, -2784);
            } else if (reply == 12) {
                player.teleToLocation(109217, -157614, -1992);
            } else if (reply == 13) {
                player.teleToLocation(83507, 90117, -3104);
            } else if (reply == 14) {
                player.teleToLocation(-13256, 125395, -3128);
            } else if (reply == 15) {
                player.teleToLocation(-84537, 153288, -3168);
            } else if (reply == 16) {
                player.teleToLocation(83359, 54492, -1520);
            } else if (reply == 17) {
                player.teleToLocation(80765, 150452, -3528);
            } else if (reply == 18) {
                player.teleToLocation(16613, 146327, -3112);
            }
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}