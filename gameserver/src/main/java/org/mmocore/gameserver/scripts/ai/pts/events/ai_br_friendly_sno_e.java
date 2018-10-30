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
public class ai_br_friendly_sno_e extends DefaultAI {
    public ai_br_friendly_sno_e(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        if (ask == 50007) {
            if (reply == 1) {
                player.teleToLocation(45473, 48113, -3056);
            } else if (reply == 2) {
                player.teleToLocation(45148, 52247, -2792);
            } else if (reply == 3) {
                player.teleToLocation(47912, 49713, -2984);
            } else if (reply == 4) {
                player.teleToLocation(42606, 42302, -3488);
            } else if (reply == 5) {
                player.teleToLocation(39379, 51175, -3448);
            } else if (reply == 6) {
                player.teleToLocation(34551, 46756, -3688);
            } else if (reply == 7) {
                player.teleToLocation(11979, 58683, -3400);
            } else if (reply == 8) {
                player.teleToLocation(3227, 74645, -3840);
            } else if (reply == 9) {
                player.teleToLocation(20891, 140881, -3464);
            } else if (reply == 10) {
                player.teleToLocation(-50576, 145859, -2784);
            } else if (reply == 11) {
                player.teleToLocation(109217, -157614, -1992);
            } else if (reply == 12) {
                player.teleToLocation(83507, 90117, -3104);
            } else if (reply == 13) {
                player.teleToLocation(-13501, 122750, -3104);
            } else if (reply == 14) {
                player.teleToLocation(43758, 49674, -3048);
            } else if (reply == 15) {
                player.teleToLocation(-13480, 121675, -2968);
            } else if (reply == 16) {
                player.teleToLocation(85544, 146521, -3400);
            } else if (reply == 17) {
                player.teleToLocation(19953, 144495, -3096);
            } else if (reply == 18) {
                player.teleToLocation(85345, 146606, -3400);
            } else if (reply == 19) {
                player.teleToLocation(16387, 142227, -2688);
            } else if (reply == 20) {
                player.teleToLocation(-85006, 105843, -3592);
            }
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}