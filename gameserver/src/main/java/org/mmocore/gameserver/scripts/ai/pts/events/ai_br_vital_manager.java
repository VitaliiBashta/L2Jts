package org.mmocore.gameserver.scripts.ai.pts.events;

import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

import java.util.ArrayList;
import java.util.List;

/**
 * @author KilRoy
 * TODO[K]
 * Разобраться, по какой причине, не работают НпсСтринги, в которых принимается
 * "не описанная" переменная, как $s. С $s1, etc - все в норме. Проверил в том
 * числе и NpcSay - так же не передает параметры...мистика О_О
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class ai_br_vital_manager extends DefaultAI {
    private static final long reuse_time = 2 * 60 * 60 * 1000;
    private static final int s_wind_walk_for_newbie = 4322;
    private static final int s_shield_for_newbie = 4323;
    private static final int s_magic_barrier_for_adventurer = 5637;
    private static final int s_bless_the_body_for_newbie = 4324;
    private static final int s_vampiric_rage_for_newbie = 4325;
    private static final int s_regeneration_for_newbie = 4326;
    private static final int s_haste_for_adventurer = 5632;
    private static final int s_acumen_for_newbie = 4329;
    private static final int s_concentration_for_newbie = 4330;
    private static final int s_empower_for_newbie = 4331;
    private static final int s_bless_the_soul_for_newbie = 4328;
    private static final int s_br_vitality_day_1 = 23179;
    private static final int s_br_vitality_day_2 = 23180;

    public ai_br_vital_manager(NpcInstance actor) {
        super(actor);
    }

    private boolean canGiveEventData(final Player player) {
        final long current_time = System.currentTimeMillis();
        final long remaining_time;

        if (player.getPlayerVariables().get(PlayerVariables.BUY_LIMITED_VITALITY_BUFF) != null) {
            remaining_time = current_time - player.getPlayerVariables().getLong(PlayerVariables.BUY_LIMITED_VITALITY_BUFF);
        } else {
            remaining_time = reuse_time;
        }

        final int hours = (int) (reuse_time - remaining_time) / 3600000;
        final int minutes = (int) (reuse_time - remaining_time) % 3600000 / 60000;
        final int seconds = (int) ((reuse_time - remaining_time) % 3600000 - minutes * 60000) / 1000;
        if (player.isLangRus()) {
            player.sendMessage("До следующей выдачи подарка Энергии осталось " + hours + " ч. " + minutes + " мин. " + seconds + " сек.");
        } else {
            player.sendMessage("The resupply time of " + hours + " hour(s) " + minutes + " minute(s) " + seconds + " second(s) remain for the Gift of Energy.");
        }

        if (remaining_time >= reuse_time) {
            getActor().altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(s_br_vitality_day_1, 1));
            getActor().altOnMagicUseTimer(player, SkillTable.getInstance().getSkillEntry(s_br_vitality_day_2, 1)); //TODO[K] - Мангольчику на заметку ( .|. (c)Mangol):)
            player.getPlayerVariables().set(PlayerVariables.BUY_LIMITED_VITALITY_BUFF, String.valueOf(current_time), -1);
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final List<Creature> target = new ArrayList<Creature>();
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        if (ask == 50021) {
            switch (reply) {
                case 1:
                    if (canGiveEventData(player)) {
                        actor.showChatWindow(player, "pts/events/gift_of_vitality/br_vi_stevu002.htm");
                    } else {
                        actor.showChatWindow(player, "pts/events/gift_of_vitality/br_vi_stevu003.htm");
                    }
                    break;
                case 2:
                    if (player.getLevel() <= 75) {
                        actor.showChatWindow(player, "pts/events/gift_of_vitality/br_vi_stevu005.htm");
                    } else if (player.isMageClass()) {
                        target.add(player);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_wind_walk_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_wind_walk_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_shield_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_shield_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_magic_barrier_for_adventurer, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_magic_barrier_for_adventurer, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_bless_the_soul_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_bless_the_soul_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_acumen_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_acumen_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_concentration_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_concentration_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_empower_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_empower_for_newbie, 1), target, true);
                        actor.showChatWindow(player, "pts/events/gift_of_vitality/br_vi_stevu007.htm");
                        target.clear();
                    } else {
                        target.add(player);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_wind_walk_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_wind_walk_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_shield_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_shield_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_magic_barrier_for_adventurer, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_magic_barrier_for_adventurer, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_bless_the_body_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_bless_the_body_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_vampiric_rage_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_vampiric_rage_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_regeneration_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_regeneration_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player, s_haste_for_adventurer, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_haste_for_adventurer, 1), target, true);
                        actor.showChatWindow(player, "pts/events/gift_of_vitality/br_vi_stevu006.htm");
                        target.clear();
                    }
                    break;
                case 3:
                    if (player.getLevel() <= 75) {
                        actor.showChatWindow(player, "pts/events/gift_of_vitality/br_vi_stevu011.htm");
                    } else if (player.getServitor() != null && !player.getServitor().isSummon()) {
                        target.add(player.getServitor());
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player.getServitor(), s_wind_walk_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_wind_walk_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player.getServitor(), s_shield_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_shield_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player.getServitor(), s_magic_barrier_for_adventurer, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_magic_barrier_for_adventurer, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player.getServitor(), s_bless_the_body_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_bless_the_body_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player.getServitor(), s_vampiric_rage_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_vampiric_rage_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player.getServitor(), s_regeneration_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_regeneration_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player.getServitor(), s_bless_the_soul_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_bless_the_soul_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player.getServitor(), s_acumen_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_acumen_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player.getServitor(), s_concentration_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_concentration_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player.getServitor(), s_empower_for_newbie, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_empower_for_newbie, 1), target, true);
                        getActor().broadcastPacket(new MagicSkillUse(getActor(), player.getServitor(), s_haste_for_adventurer, 1, 0, 0));
                        getActor().callSkill(SkillTable.getInstance().getSkillEntry(s_haste_for_adventurer, 1), target, true);
                        actor.showChatWindow(player, "pts/events/gift_of_vitality/br_vi_stevu009.htm");
                        target.clear();
                    } else {
                        actor.showChatWindow(player, "pts/events/gift_of_vitality/br_vi_stevu010.htm");
                    }
                    break;
            }
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}