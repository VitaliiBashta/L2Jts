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
public class ai_br_friendly_sno_h extends DefaultAI {
    public ai_br_friendly_sno_h(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        if (ask == 50003) {
            if (reply == 1) {
                player.teleToLocation(-83925, 243416, -3728);
            } else if (reply == 2) {
                player.teleToLocation(-83141, 242806, -3720);
            } else if (reply == 3) {
                player.teleToLocation(-84903, 245094, -3720);
            } else if (reply == 4) {
                player.teleToLocation(-81788, 243677, -3712);
            } else if (reply == 5) {
                player.teleToLocation(-81797, 240198, -3680);
            } else if (reply == 6) {
                player.teleToLocation(-93490, 242721, -3464);
            } else if (reply == 7) {
                player.teleToLocation(-102727, 238718, -3496);
            } else if (reply == 8) {
                player.teleToLocation(-113854, 244875, -3344);
            } else if (reply == 9) {
                player.teleToLocation(-48029, 199525, -3712);
            } else if (reply == 10) {
                player.teleToLocation(20891, 140881, -3464);
            } else if (reply == 11) {
                player.teleToLocation(-50576, 145859, -2784);
            } else if (reply == 12) {
                player.teleToLocation(109217, -157614, -1992);
            } else if (reply == 13) {
                player.teleToLocation(83507, 90117, -3104);
            } else if (reply == 14) {
                player.teleToLocation(-81891, 149296, -3120);
            } else if (reply == 15) {
                player.teleToLocation(-82523, 150435, -3120);
            } else if (reply == 16) {
                player.teleToLocation(-85103, 152782, -3160);
            } else if (reply == 17) {
                player.teleToLocation(-80491, 150743, -3040);
            } else if (reply == 18) {
                player.teleToLocation(-79372, 150765, -3040);
            } else if (reply == 19) {
                player.teleToLocation(19953, 144495, -3096);
            } else if (reply == 20) {
                player.teleToLocation(85544, 146521, -3400);
            } else if (reply == 21) {
                player.teleToLocation(85345, 146606, -3400);
            } else if (reply == 22) {
                player.teleToLocation(16387, 142227, -2688);
            } else if (reply == 23) {
                player.teleToLocation(-85006, 105843, -3592);
            }
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}