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
public class ai_br_friendly_sno_dw extends DefaultAI {
    public ai_br_friendly_sno_dw(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        if (ask == 50005) {
            if (reply == 1) {
                player.teleToLocation(115538, -178062, -912);
            } else if (reply == 2) {
                player.teleToLocation(115393, -182601, -1440);
            } else if (reply == 3) {
                player.teleToLocation(114812, -179662, -872);
            } else if (reply == 4) {
                player.teleToLocation(116014, -174628, -968);
            } else if (reply == 5) {
                player.teleToLocation(125455, -180665, -1768);
            } else if (reply == 6) {
                player.teleToLocation(116129, -200225, -3488);
            } else if (reply == 7) {
                player.teleToLocation(137861, -203740, -3720);
            } else if (reply == 8) {
                player.teleToLocation(163444, -206198, -3352);
            } else if (reply == 9) {
                player.teleToLocation(20891, 140881, -3464);
            } else if (reply == 10) {
                player.teleToLocation(-50576, 145859, -2784);
            } else if (reply == 11) {
                player.teleToLocation(109217, -157614, -1992);
            } else if (reply == 12) {
                player.teleToLocation(83507, 90117, -3104);
            } else if (reply == 13) {
                player.teleToLocation(115726, -183314, -1472);
            } else if (reply == 14) {
                player.teleToLocation(115393, -182601, -1440);
            } else if (reply == 15) {
                player.teleToLocation(83159, 146605, -3464);
            }
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}