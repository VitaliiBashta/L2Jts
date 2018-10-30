package org.mmocore.gameserver.scripts.ai.pts.events;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_white_freya_event extends DefaultAI {
    private static final int[] GIFT_SKILLS = {9150, 9151, 9152, 9153, 9154, 9155, 9156, 9157};
    private static final long reuse_time = 20 * 60 * 60 * 1000;
    private static final int event_present_skill = 15440;
    private static final int event_present = 17138;
    private static final int idle_massage = 1801190;
    private static final int love_massage = 1801195;
    private int i_ai0;
    private int i_ai1;

    public ai_white_freya_event(NpcInstance actor) {
        super(actor);
        i_ai0 = 0;
        i_ai1 = 0;
        AddTimerEx(200001, 60000);
        AddTimerEx(200002, 10000);
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        if (ask == 20001 && reply == 1) {
            final long current_time = System.currentTimeMillis();
            final long remaining_time;
            if (player.getPlayerVariables().get(PlayerVariables.BUY_LIMITED_FREYA_PRESENT) != null) {
                remaining_time = current_time - player.getPlayerVariables().getLong(PlayerVariables.BUY_LIMITED_FREYA_PRESENT);
            } else {
                remaining_time = reuse_time;
            }

            if (remaining_time >= reuse_time) {
                if (player.reduceAdena(1, true)) {
                    ItemFunctions.addItem(player, event_present_skill, 1);
                    player.getPlayerVariables().set(PlayerVariables.BUY_LIMITED_FREYA_PRESENT, String.valueOf(current_time), -1);
                }
            } else {
                final int hours = (int) (reuse_time - remaining_time) / 3600000;
                final int minutes = (int) (reuse_time - remaining_time) % 3600000 / 60000;
                if (hours > 0) {
                    player.sendPacket(new SystemMessage(SystemMsg.THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(hours).addNumber(minutes));
                } else if (minutes > 0) {
                    player.sendPacket(new SystemMessage(SystemMsg.THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(minutes));
                }
            }
        }
    }

    @Override
    protected void onEvtSeeSpell(SkillEntry skill, Creature caster) {
        final NpcInstance actor = getActor();

        if (caster == null || !caster.isPlayer()) {
            return;
        }

        final GameObject casterTarget = caster.getCastingTarget();
        if (casterTarget == null || casterTarget.getObjectId() != actor.getObjectId()) {
            return;
        }

        final Player player = caster.getPlayer();
        final int i0 = Rnd.get(10000);
        final int i1 = Rnd.get(2);
        if (ArrayUtils.contains(GIFT_SKILLS, skill.getId())) {
            if (i0 >= 9500) {
                if (i_ai0 == 1) {
                    ItemFunctions.addItem(player, event_present, 1);
                    ChatUtils.say(actor, NpcString.valueOf(love_massage + i1), player.getName());
                    i_ai0 = 0;
                } else if (i_ai1 == 1) {
                    final int i2 = Rnd.get(5);
                    ChatUtils.say(actor, NpcString.valueOf(idle_massage + i2));
                    i_ai1 = 0;
                }
            } else if (i_ai1 == 1) {
                final int i2 = Rnd.get(5);
                ChatUtils.say(actor, NpcString.valueOf(idle_massage + i2));
                i_ai1 = 0;
            }
        }
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        if (timer_id == 200001) {
            i_ai0 = 1;
        }
        if (timer_id == 200002) {
            i_ai1 = 1;
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}