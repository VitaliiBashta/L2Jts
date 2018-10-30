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
public class ai_br_friendly_sno_o extends DefaultAI {
    public ai_br_friendly_sno_o(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        if (ask == 50004) {
            if (reply == 1) {
                player.teleToLocation(-45402, -113599, -240);
            } else if (reply == 2) {
                player.teleToLocation(-46290, -114661, -200);
            } else if (reply == 3) {
                player.teleToLocation(-46219, -112570, -200);
            } else if (reply == 4) {
                player.teleToLocation(-43334, -113585, -216);
            } else if (reply == 5) {
                player.teleToLocation(-51107, -110527, -240);
            } else if (reply == 6) {
                player.teleToLocation(-27405, -128781, -1080);
            } else if (reply == 7) {
                player.teleToLocation(-8022, -139095, -1264);
            } else if (reply == 8) {
                player.teleToLocation(11763, -131081, -1512);
            } else if (reply == 9) {
                player.teleToLocation(414, -114398, -3536);
            } else if (reply == 10) {
                player.teleToLocation(20891, 140881, -3464);
            } else if (reply == 11) {
                player.teleToLocation(-50576, 145859, -2784);
            } else if (reply == 12) {
                player.teleToLocation(109217, -157614, -1992);
            } else if (reply == 13) {
                player.teleToLocation(83507, 90117, -3104);
            } else if (reply == 14) {
                player.teleToLocation(-46017, -114489, -200);
            } else if (reply == 15) {
                player.teleToLocation(-44706, -112008, -240);
            } else if (reply == 16) {
                player.teleToLocation(-44931, -115055, -240);
            } else if (reply == 17) {
                player.teleToLocation(20012, 144554, -3096);
            } else if (reply == 18) {
                player.teleToLocation(-85006, 105843, -3592);
            }
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}