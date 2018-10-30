package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_g_herald_of_navit extends DefaultAI {
    private static final int s_g_the_fall_of_dragon1 = 23312;

    public ai_g_herald_of_navit(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        AddTimerEx(1000, (180 * 60) * 1000);
        AddTimerEx(1001, (5 * 60) * 1000);
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        if (ask == -77) {
            if (player.getEffectList().containEffectFromSkills(s_g_the_fall_of_dragon1)) {
                actor.showChatWindow(player, "pts/default/g_herald_of_navit002.htm");
            } else {
                actor.broadcastPacket(new MagicSkillUse(actor, player, s_g_the_fall_of_dragon1, 1, 0, 0));
                actor.altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(s_g_the_fall_of_dragon1, 1));
            }
        }
    }

    @Override
    protected void onEvtTimerFiredEx(final int timer_id, final Object arg1, final Object arg2) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        if (timer_id == 1000) {
            getActor().deleteMe();
        } else if (timer_id == 1001) {
            if (Rnd.get(3) < 1) {
                ChatUtils.shout(actor, NpcString.SHOW_RESPECT_TO_THE_HEROES_WHO_DEFEATED_THE_EVIL_DRAGON_AND_PROTECTED_THIS_ADEN_WORLD);
            } else if (Rnd.get(2) < 1) {
                ChatUtils.shout(actor, NpcString.SHOUT_TO_CELEBRATE_THE_VICTORY_OF_THE_HEROES);
            } else {
                ChatUtils.shout(actor, NpcString.PRAISE_THE_ACHIVEMENT_OF_THE_HEROES_AND_RECEIVE_NEVITS_BLESSING);
            }
            AddTimerEx(1001, (5 * 60) * 1000);
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}