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
public class ai_br_friendly_sno_d extends DefaultAI {
    public ai_br_friendly_sno_d(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        if (ask == 50006) {
            if (reply == 1) {
                player.teleToLocation(12364, 16622, -4584);
            } else if (reply == 2) {
                player.teleToLocation(10561, 14358, -4240);
            } else if (reply == 3) {
                player.teleToLocation(13432, 17634, -4536);
            } else if (reply == 4) {
                player.teleToLocation(26291, 14678, -3648);
            } else if (reply == 5) {
                player.teleToLocation(17863, 8339, -3648);
            } else if (reply == 6) {
                player.teleToLocation(-10222, 23074, -3712);
            } else if (reply == 7) {
                player.teleToLocation(-23044, 39945, -2960);
            } else if (reply == 8) {
                player.teleToLocation(-56988, 72153, -3192);
            } else if (reply == 9) {
                player.teleToLocation(20891, 140881, -3464);
            } else if (reply == 10) {
                player.teleToLocation(-50576, 145859, -2784);
            } else if (reply == 11) {
                player.teleToLocation(109217, -157614, -1992);
            } else if (reply == 12) {
                player.teleToLocation(83507, 90117, -3104);
            } else if (reply == 13) {
                player.teleToLocation(-12769, 123176, -3096);
            } else if (reply == 14) {
                player.teleToLocation(-12489, 123462, -3104);
            } else if (reply == 15) {
                player.teleToLocation(-44181, 79654, -3648);
            } else if (reply == 16) {
                player.teleToLocation(-12542, 123093, -3096);
            } else if (reply == 17) {
                player.teleToLocation(85544, 146521, -3400);
            } else if (reply == 18) {
                player.teleToLocation(19953, 144495, -3096);
            } else if (reply == 19) {
                player.teleToLocation(85345, 146606, -3400);
            } else if (reply == 20) {
                player.teleToLocation(16387, 142227, -2688);
            } else if (reply == 21) {
                player.teleToLocation(-85006, 105843, -3592);
            }
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}