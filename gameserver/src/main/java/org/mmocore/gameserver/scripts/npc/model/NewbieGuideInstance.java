package org.mmocore.gameserver.scripts.npc.model;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.network.lineage.serverpackets.RadarControl;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

import java.util.ArrayList;
import java.util.List;

/**
 * Based on official Freya
 *
 * @author Magister
 * @version 1.2
 * @date 03/02/2015
 * @tested OK
 */
public class NewbieGuideInstance extends NpcInstance {
    private static final long serialVersionUID = 92437418203893683L;
    // Class Id
    private static final int kamael_m_soldier = 0x7b;
    private static final int kamael_f_soldier = 0x7c;
    // dialogs
    private static final String fnCoupon1Ok = "pts/newbie_guide/newbie_guide002.htm";
    private static final String fnCoupon1Not1 = "pts/newbie_guide/newbie_guide003.htm";
    private static final String fnCoupon1Not2 = "pts/newbie_guide/newbie_guide004.htm";
    private static final String fnCoupon2Ok = "pts/newbie_guide/newbie_guide011.htm";
    private static final String fnCoupon2Not1 = "pts/newbie_guide/newbie_guide012.htm";
    private static final String fnCoupon2Not2 = "pts/newbie_guide/newbie_guide013.htm";
    private static final String fnNoSummonCreature = "pts/newbie_guide/blessing_list002b.htm";
    // etcitem
    private static final int q_adventure_coupon1 = 7832;
    private static final int q_adventure_coupon2 = 7833;
    // skills
    private static final int s_wind_walk_for_newbie = 4322;
    private static final int s_shield_for_newbie = 4323;
    private static final int s_magic_barrier_for_adventurer = 5637;
    private static final int s_bless_the_body_for_newbie = 4324;
    private static final int s_vampiric_rage_for_newbie = 4325;
    private static final int s_regeneration_for_newbie = 4326;
    private static final int s_haste_for_newbie = 4327;
    private static final int s_haste_for_adventurer = 5632;
    private static final int s_life_cubic_for_newbie = 4338;
    private static final int s_acumen_for_newbie = 4329;
    private static final int s_concentration_for_newbie = 4330;
    private static final int s_empower_for_newbie = 4331;
    private static final int s_bless_the_soul_for_newbie = 4328;
    private static final int s_pk_protect1 = 5182;

    public NewbieGuideInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        switch (getNpcId()) {
            case 30598:
                showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot001.htm");
                break;
            case 30599:
                showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios001.htm");
                break;
            case 30600:
                showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia001.htm");
                break;
            case 30601:
                showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin001.htm");
                break;
            case 30602:
                showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai001.htm");
                break;
            case 31076:
                showChatWindow(player, "pts/newbie_guide/guide_gludin_nina/guide_gludin_nina001.htm");
                break;
            case 31077:
                showChatWindow(player, "pts/newbie_guide/guide_gludio_euria/guide_gludio_euria001.htm");
                break;
            case 32135:
                showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk001.htm");
                break;
            case 32327:
                showChatWindow(player, "pts/newbie_guide/blessing_benefector/blessing_benefector001.htm");
                break;
            default:
                super.showChatWindow(player, val, arg);
                break;
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (player == null) {
            return;
        }
        if (command.startsWith("menu_select?ask=-7&")) {
            List<Creature> target = new ArrayList<Creature>();
            if (command.endsWith("reply=1")) {
                switch (getNpcId()) {
                    case 30598:
                        if (player.getPlayerTemplateComponent().getPlayerRace() != PlayerRace.human)
                            showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot003.htm");
                        else if (player.getLevel() > 20 || player.getPlayerClassComponent().getClassId().level() != 0)
                            showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot002.htm");
                        else if (player.getPlayerClassComponent().getClassId().level() == 0) {
                            if (!player.isMageClass()) {
                                if (player.getLevel() <= 5)
                                    showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_f05.htm");
                                else if (player.getLevel() <= 10)
                                    showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_f10.htm");
                                else if (player.getLevel() <= 15)
                                    showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_f15.htm");
                                else
                                    showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_f20.htm");
                            } else {
                                if (player.getLevel() <= 7)
                                    showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_m07.htm");
                                else if (player.getLevel() <= 14)
                                    showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_m14.htm");
                                else
                                    showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_m20.htm");
                            }
                        }
                        break;
                    case 30599:
                        if (player.getPlayerTemplateComponent().getPlayerRace() != PlayerRace.elf)
                            showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios003.htm");
                        else if (player.getLevel() > 20 || player.getPlayerClassComponent().getClassId().level() != 0)
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_041_GuideMission/guide_elf_roios/guide_elf_roios002.htm");
                        else if (player.getPlayerClassComponent().getClassId().level() == 0) {
                            if (!player.isMageClass()) {
                                if (player.getLevel() <= 5)
                                    showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_f05.htm");
                                else if (player.getLevel() <= 10)
                                    showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_f10.htm");
                                else if (player.getLevel() <= 15)
                                    showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_f15.htm");
                                else
                                    showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_f20.htm");
                            } else {
                                if (player.getLevel() <= 7)
                                    showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_m07.htm");
                                else if (player.getLevel() <= 14)
                                    showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_m14.htm");
                                else
                                    showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_m20.htm");
                            }
                        }
                        break;
                    case 30600:
                        if (player.getPlayerTemplateComponent().getPlayerRace() != PlayerRace.darkelf)
                            showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia003.htm");
                        else if (player.getLevel() > 20 || player.getPlayerClassComponent().getClassId().level() != 0)
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_041_GuideMission/guide_delf_frankia/guide_delf_frankia002.htm");
                        else if (player.getPlayerClassComponent().getClassId().level() == 0) {
                            if (!player.isMageClass()) {
                                if (player.getLevel() <= 5)
                                    showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_f05.htm");
                                else if (player.getLevel() <= 10)
                                    showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_f10.htm");
                                else if (player.getLevel() <= 15)
                                    showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_f15.htm");
                                else
                                    showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_f20.htm");
                            } else {
                                if (player.getLevel() <= 7)
                                    showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_m07.htm");
                                else if (player.getLevel() <= 14)
                                    showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_m14.htm");
                                else
                                    showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_m20.htm");
                            }
                        }
                        break;
                    case 30601:
                        if (player.getPlayerTemplateComponent().getPlayerRace() != PlayerRace.dwarf)
                            showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin003.htm");
                        else if (player.getLevel() > 20 || player.getPlayerClassComponent().getClassId().level() != 0)
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_041_GuideMission/guide_dwarf_gullin/guide_dwarf_gullin002.htm");
                        else if (player.getPlayerClassComponent().getClassId().level() == 0) {
                            if (player.getLevel() <= 5)
                                showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_f05.htm");
                            else if (player.getLevel() <= 10)
                                showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_f10.htm");
                            else if (player.getLevel() <= 15)
                                showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_f15.htm");
                            else
                                showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_f20.htm");
                        }
                        break;
                    case 30602:
                        if (player.getPlayerTemplateComponent().getPlayerRace() != PlayerRace.orc)
                            showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai003.htm");
                        else if (player.getLevel() > 20 || player.getPlayerClassComponent().getClassId().level() != 0)
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_041_GuideMission/guide_orc_tanai/guide_orc_tanai002.htm");
                        else if (player.getPlayerClassComponent().getClassId().level() == 0) {
                            if (!player.isMageClass()) {
                                if (player.getLevel() <= 5)
                                    showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_f05.htm");
                                else if (player.getLevel() <= 10)
                                    showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_f10.htm");
                                else if (player.getLevel() <= 15)
                                    showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_f15.htm");
                                else
                                    showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_f20.htm");
                            } else {
                                if (player.getLevel() <= 7)
                                    showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_m07.htm");
                                else if (player.getLevel() <= 14)
                                    showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_m14.htm");
                                else
                                    showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_m20.htm");
                            }
                        }
                        break;
                    case 32135:
                        if (player.getPlayerTemplateComponent().getPlayerRace() != PlayerRace.kamael)
                            showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk003.htm");
                        else if (player.getLevel() > 20 || player.getPlayerClassComponent().getClassId().level() != 0)
                            showChatWindow(player, "org/mmocore/gameserver/scripts/quests/_041_GuideMission/guide_krenisk/guide_krenisk002.htm");
                        else if (player.getPlayerClassComponent().getClassId().getId() == kamael_m_soldier) {
                            if (player.getLevel() <= 5)
                                showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_kmf05.htm");
                            else if (player.getLevel() <= 10)
                                showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_kmf10.htm");
                            else if (player.getLevel() <= 15)
                                showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_kmf15.htm");
                            else
                                showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_kmf20.htm");
                        } else if (player.getPlayerClassComponent().getClassId().getId() == kamael_f_soldier) {
                            if (player.getLevel() <= 5)
                                showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_kff05.htm");
                            else if (player.getLevel() <= 10)
                                showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_kff10.htm");
                            else if (player.getLevel() <= 15)
                                showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_kff15.htm");
                            else
                                showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_kff20.htm");
                        }
                        break;
                }
            } else if (command.endsWith("reply=2")) {
                switch (getNpcId()) {
                    case 30598:
                    case 30599:
                    case 30600:
                    case 30601:
                    case 30602:
                    case 31076:
                    case 31077:
                    case 32135:
                    case 32327:
                        target.add(player);
                        if (player.getLevel() <= 75) {
                            if (player.getLevel() < 6)
                                showChatWindow(player, "pts/newbie_guide/guide_for_newbie002.htm");
                            else if (!player.isMageClass()) {
                                if (player.getLevel() >= 16 && player.getLevel() <= 34) {
                                    broadcastPacket(new MagicSkillUse(this, player, s_life_cubic_for_newbie, 1, 0, 0));
                                    player.doCast(SkillTable.getInstance().getSkillEntry(s_life_cubic_for_newbie, 1), player, true);
                                }
                                broadcastPacket(new MagicSkillUse(this, player, s_wind_walk_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_wind_walk_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player, s_shield_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_shield_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player, s_magic_barrier_for_adventurer, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_magic_barrier_for_adventurer, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player, s_bless_the_body_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_bless_the_body_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player, s_vampiric_rage_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_vampiric_rage_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player, s_regeneration_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_regeneration_for_newbie, 1), target, true);
                                if (player.getLevel() >= 6 && player.getLevel() <= 39) {
                                    broadcastPacket(new MagicSkillUse(this, player, s_haste_for_newbie, 1, 0, 0));
                                    callSkill(SkillTable.getInstance().getSkillEntry(s_haste_for_newbie, 1), target, true);
                                }
                                if (player.getLevel() >= 40 && player.getLevel() <= 75) {
                                    broadcastPacket(new MagicSkillUse(this, player, s_haste_for_adventurer, 1, 0, 0));
                                    callSkill(SkillTable.getInstance().getSkillEntry(s_haste_for_adventurer, 1), target, true);
                                }
                                target.clear();
                            } else if (player.isMageClass()) {
                                broadcastPacket(new MagicSkillUse(this, player, s_wind_walk_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_wind_walk_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player, s_shield_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_shield_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player, s_magic_barrier_for_adventurer, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_magic_barrier_for_adventurer, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player, s_bless_the_soul_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_bless_the_soul_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player, s_acumen_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_acumen_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player, s_concentration_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_concentration_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player, s_empower_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_empower_for_newbie, 1), target, true);
                                if (player.getLevel() >= 16 && player.getLevel() <= 34) {
                                    broadcastPacket(new MagicSkillUse(this, player, s_life_cubic_for_newbie, 1, 0, 0));
                                    player.doCast(SkillTable.getInstance().getSkillEntry(s_life_cubic_for_newbie, 1), player, true);
                                }
                                target.clear();
                            }
                        } else
                            showChatWindow(player, "pts/newbie_guide/guide_for_newbie003.htm");
                        break;
                }
            } else if (command.endsWith("reply=3")) {
                switch (getNpcId()) {
                    case 30598:
                    case 30599:
                    case 30600:
                    case 30601:
                    case 30602:
                    case 31076:
                    case 31077:
                    case 32135:
                    case 32327:
                        target.add(player);
                        if (player.getLevel() <= 39 && (player.getPlayerClassComponent().getClassId().level() == 0 || player.getPlayerClassComponent().getClassId().level() == 1)) {
                            broadcastPacket(new MagicSkillUse(this, player, s_pk_protect1, 1, 0, 0));
                            callSkill(SkillTable.getInstance().getSkillEntry(s_pk_protect1, 1), target, true);
                            target.clear();
                        } else
                            showChatWindow(player, "pts/newbie_guide/pk_protect002.htm");
                        break;
                }
            } else if (command.endsWith("reply=4")) {
                switch (getNpcId()) {
                    case 30598:
                    case 30599:
                    case 30600:
                    case 30601:
                    case 30602:
                    case 31076:
                    case 31077:
                    case 32135:
                    case 32327:
                        target.add(player.getServitor());
                        if (player.getServitor() != null && !player.getServitor().isSummon()) {
                            if (player.getLevel() < 6 || player.getLevel() > 75)
                                showChatWindow(player, "pts/newbie_guide/guide_for_newbie003.htm");
                            else {
                                broadcastPacket(new MagicSkillUse(this, player.getServitor(), s_wind_walk_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_wind_walk_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player.getServitor(), s_shield_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_shield_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player.getServitor(), s_magic_barrier_for_adventurer, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_magic_barrier_for_adventurer, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player.getServitor(), s_bless_the_body_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_bless_the_body_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player.getServitor(), s_vampiric_rage_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_vampiric_rage_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player.getServitor(), s_regeneration_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_regeneration_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player.getServitor(), s_bless_the_soul_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_bless_the_soul_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player.getServitor(), s_acumen_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_acumen_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player.getServitor(), s_concentration_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_concentration_for_newbie, 1), target, true);
                                broadcastPacket(new MagicSkillUse(this, player.getServitor(), s_empower_for_newbie, 1, 0, 0));
                                callSkill(SkillTable.getInstance().getSkillEntry(s_empower_for_newbie, 1), target, true);
                                if (player.getLevel() >= 6 && player.getLevel() <= 39) {
                                    broadcastPacket(new MagicSkillUse(this, player.getServitor(), s_haste_for_newbie, 1, 0, 0));
                                    callSkill(SkillTable.getInstance().getSkillEntry(s_haste_for_newbie, 1), target, true);
                                }
                                if (player.getLevel() >= 40 && player.getLevel() <= 75) {
                                    broadcastPacket(new MagicSkillUse(this, player.getServitor(), s_haste_for_adventurer, 1, 0, 0));
                                    callSkill(SkillTable.getInstance().getSkillEntry(s_haste_for_adventurer, 1), target, true);
                                }
                                target.clear();
                            }
                        } else
                            showChatWindow(player, fnNoSummonCreature);
                        break;
                }
            }
        } else if (command.startsWith("menu_select?ask=255&")) {
            if (command.endsWith("reply=10")) {
                switch (getNpcId()) {
                    case 30598:
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_04.htm");
                        break;
                    case 30599:
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_04.htm");
                        break;
                    case 30600:
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_04.htm");
                        break;
                    case 30601:
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04.htm");
                        break;
                    case 30602:
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_04.htm");
                    case 32135:
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_04.htm");
                        break;
                }
            } else if (command.endsWith("reply=11")) {
                switch (getNpcId()) {
                    case 30598:
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_04a.htm");
                        break;
                    case 30599:
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_04a.htm");
                        break;
                    case 30600:
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_04a.htm");
                        break;
                    case 30601:
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04a.htm");
                        break;
                    case 30602:
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_04a.htm");
                    case 32135:
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_04a.htm");
                        break;
                }
            } else if (command.endsWith("reply=12")) {
                switch (getNpcId()) {
                    case 30598:
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_04b.htm");
                        break;
                    case 30599:
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_04b.htm");
                        break;
                    case 30600:
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_04b.htm");
                        break;
                    case 30601:
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04b.htm");
                        break;
                    case 30602:
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_04b.htm");
                    case 32135:
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_04b.htm");
                        break;
                }
            } else if (command.endsWith("reply=13")) {
                switch (getNpcId()) {
                    case 30598:
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_04c.htm");
                        break;
                    case 30599:
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_04c.htm");
                        break;
                    case 30600:
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_04c.htm");
                        break;
                    case 30601:
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04c.htm");
                        break;
                    case 30602:
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_04c.htm");
                    case 32135:
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_04c.htm");
                        break;
                }
            } else if (command.endsWith("reply=14")) {
                switch (getNpcId()) {
                    case 30598:
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_04d.htm");
                        break;
                    case 30599:
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_04d.htm");
                        break;
                    case 30600:
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_04d.htm");
                        break;
                    case 30601:
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04d.htm");
                        break;
                    case 30602:
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_04d.htm");
                    case 32135:
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_04d.htm");
                        break;
                }
            } else if (command.endsWith("reply=15")) {
                switch (getNpcId()) {
                    case 30598:
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_04e.htm");
                        break;
                    case 30599:
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_04e.htm");
                        break;
                    case 30600:
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_04e.htm");
                        break;
                    case 30601:
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04e.htm");
                        break;
                    case 30602:
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_04e.htm");
                    case 32135:
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_04e.htm");
                        break;
                }
            } else if (command.endsWith("reply=16")) {
                switch (getNpcId()) {
                    case 30598:
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_04f.htm");
                        break;
                    case 30599:
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_04f.htm");
                        break;
                    case 30600:
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_04f.htm");
                        break;
                    case 30601:
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04f.htm");
                        break;
                    case 30602:
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_04f.htm");
                    case 32135:
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_04f.htm");
                        break;
                }
            } else if (command.endsWith("reply=17")) {
                switch (getNpcId()) {
                    case 30598:
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_04g.htm");
                        break;
                    case 30599:
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_04g.htm");
                        break;
                    case 30600:
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_04g.htm");
                        break;
                    case 30601:
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04g.htm");
                        break;
                    case 30602:
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_04g.htm");
                    case 32135:
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_04g.htm");
                        break;
                }
            } else if (command.endsWith("reply=18")) {
                switch (getNpcId()) {
                    case 30598:
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_04h.htm");
                        break;
                    case 30599:
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_04h.htm");
                        break;
                    case 30600:
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_04h.htm");
                        break;
                    case 30601:
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_04h.htm");
                        break;
                    case 30602:
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_04h.htm");
                    case 32135:
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_04h.htm");
                        break;
                }
            } else if (command.endsWith("reply=19")) {
                switch (getNpcId()) {
                    case 30598:
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_04i.htm");
                        break;
                    case 30602:
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_04i.htm");
                    case 32135:
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_04i.htm");
                        break;
                }
            } else if (command.endsWith("reply=20")) {
                switch (getNpcId()) {
                    case 32135:
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_04j.htm");
                        break;
                }
            } else if (command.endsWith("reply=21")) {
                switch (getNpcId()) {
                    case 32135:
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_04k.htm");
                        break;
                }
            } else if (command.endsWith("reply=22")) {
                switch (getNpcId()) {
                    case 32135:
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_04l.htm");
                        break;
                }
            } else if (command.endsWith("reply=31")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84108, 244604, -3729, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(46926, 51511, -2977, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(9670, 15537, -4574, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115072, -178176, -906, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45264, -112512, -235, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-116879, 46591, 380, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=32")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-82236, 241573, -3728, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(44995, 51706, -2803, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(15120, 15656, -4376, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(117847, -182339, -1537, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-46576, -117311, -242, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-119378, 49242, 22, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=33")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-82515, 241221, -3728, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45727, 51721, -2803, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(17306, 13592, -3724, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116617, -184308, -1569, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-47360, -113791, -237, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-119774, 49245, 22, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=34")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-82319, 244709, -3727, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(42812, 51138, -2996, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(15272, 16310, -4377, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(117826, -182576, -1537, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-47360, -113424, -235, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-119830, 51860, -787, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=35")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-82659, 244992, -3717, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45487, 46511, -2996, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(6449, 19619, -3694, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116378, -184308, -1571, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45744, -117165, -236, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-119362, 51862, -780, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=36")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-86114, 244682, -3727, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(47401, 51764, -2996, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-15404, 71131, -3445, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115183, -176728, -791, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-46528, -109968, -250, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-112872, 46850, 68, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=37")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-86328, 244448, -3724, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(42971, 51372, -2996, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(7496, 17388, -4377, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(114969, -176752, -790, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45808, -110055, -255, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-112352, 47392, 68, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=38")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-86322, 241215, -3727, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(47595, 51569, -2996, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(17102, 13002, -3743, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(117366, -178725, -1118, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45731, -113844, -237, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-110544, 49040, -1124, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=39")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-85964, 240947, -3727, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45778, 46534, -2996, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(6532, 19903, -3693, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(117378, -178914, -1120, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45728, -113360, -237, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-110536, 45162, -1132, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=40")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-85026, 242689, -3729, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(44476, 47153, -2984, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-15648, 71405, -3451, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116226, -178529, -948, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45952, -114784, -199, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-115888, 43568, 524, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=41")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-83789, 240799, -3717, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(42700, 50057, -2984, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(7644, 18048, -4377, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116190, -178441, -948, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45952, -114496, -199, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-115486, 43567, 525, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=42")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84204, 240403, -3717, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(42766, 50037, -2984, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-1301, 75883, -3566, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116016, -178615, -948, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45863, -112621, -200, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-116920, 47792, 464, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=43")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-86385, 243267, -3717, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(44683, 46952, -2981, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-1152, 76125, -3566, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116190, -178615, -948, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45864, -112540, -199, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-116749, 48077, 462, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=44")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-86733, 242918, -3717, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(44667, 46896, -2982, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10580, 17574, -4554, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116103, -178407, -948, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-43264, -112532, -220, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-117153, 48075, 463, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=45")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84516, 245449, -3714, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45725, 52105, -2795, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(12009, 15704, -4554, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116103, -178653, -948, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-43910, -115518, -194, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-119104, 43280, 559, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=46")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84729, 245001, -3726, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(44823, 52414, -2795, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(11951, 15661, -4554, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115468, -182446, -1434, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-43950, -115457, -194, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-119104, 43152, 559, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=47")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84965, 245222, -3726, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45000, 52101, -2795, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10761, 17970, -4554, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115315, -182155, -1444, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-44416, -111486, -222, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-117056, 43168, 559, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=48")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84981, 244764, -3726, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45919, 52414, -2795, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10823, 18013, -4554, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115271, -182692, -1445, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-43926, -111794, -222, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-117060, 43296, 559, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=49")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-85186, 245001, -3726, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(44692, 52261, -2795, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(11283, 14226, -4242, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115900, -177316, -915, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-43109, -113770, -221, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-118192, 42384, 838, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=50")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-83326, 242964, -3718, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(47780, 49568, -2983, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10447, 14620, -4242, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116268, -177524, -914, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-43114, -113404, -221, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-117968, 42384, 838, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=51")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-83020, 242553, -3718, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(47912, 50170, -2983, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(11258, 14431, -4242, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115741, -181645, -1344, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-46768, -113610, -3, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-118132, 42788, 723, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=52")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-83175, 243065, -3718, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(47868, 50167, -2983, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10344, 14445, -4242, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116192, -181072, -1344, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-46802, -114011, -112, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-118028, 42788, 720, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=53")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-82809, 242751, -3718, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(28928, 74248, -3773, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10315, 14293, -4242, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115205, -180024, -870, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-46247, -113866, -21, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-114802, 44821, 524, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=54")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-81895, 243917, -3721, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(43673, 49683, -3046, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(10775, 14190, -4242, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(114716, -180018, -871, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-46808, -113184, -112, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-114975, 44658, 524, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=55")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-81840, 243534, -3721, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45610, 49008, -3059, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(11235, 14078, -4242, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(114832, -179520, -871, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-45328, -114736, -237, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-114801, 45031, 525, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=56")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-81512, 243424, -3720, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(50592, 54986, -3376, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(11012, 14128, -4242, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115717, -183488, -1483, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 30602:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-44624, -111873, -238, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_orc_tanai/guide_orc_tanai_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-120432, 45296, 416, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=57")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84436, 242793, -3729, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(42978, 49115, -2994, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(13380, 17430, -4542, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115618, -183265, -1483, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-120706, 45079, 419, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=58")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-78939, 240305, -3443, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(46475, 50495, -3058, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(13464, 17751, -4541, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(114348, -178537, -813, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-120356, 45293, 416, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=59")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-85301, 244587, -3725, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(45859, 50827, -3058, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(13763, 17501, -4542, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(114990, -177294, -854, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-120604, 44960, 423, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=60")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-83163, 243560, -3728, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(51210, 82474, -3283, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-44225, 79721, -3652, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(114426, -178672, -812, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-120294, 46013, 384, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=61")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-97131, 258946, -3622, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30599:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(49262, 53607, -3216, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_elf_roios/guide_elf_roios_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-44015, 79683, -3652, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(114409, -178415, -812, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-120157, 45813, 355, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=62")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-114685, 222291, -2925, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(25856, 10832, -3724, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(117061, -181867, -1413, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-120158, 46221, 354, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=63")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-84057, 242832, -3729, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(12328, 14947, -4574, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116164, -184029, -1507, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-120400, 46921, 415, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=64")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-100332, 238019, -3573, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(13081, 18444, -4573, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(115563, -182923, -1448, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-120407, 46755, 423, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=65")) {
                switch (getNpcId()) {
                    case 30598:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-82041, 242718, -3725, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_human_cnacelot/guide_human_cnacelot_q0255_05.htm");
                        break;
                    case 30600:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(12311, 17470, -4574, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_delf_frankia/guide_delf_frankia_q0255_05.htm");
                        break;
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(112656, -174864, -611, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-120442, 47125, 422, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=66")) {
                switch (getNpcId()) {
                    case 30601:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(116852, -183595, -1566, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_dwarf_gullin/guide_dwarf_gullin_q0255_05.htm");
                        break;
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-118720, 48062, 473, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=67")) {
                switch (getNpcId()) {
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-118918, 47956, 474, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=68")) {
                switch (getNpcId()) {
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-118527, 47955, 473, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=69")) {
                switch (getNpcId()) {
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-117605, 48079, 472, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=70")) {
                switch (getNpcId()) {
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-117824, 48080, 476, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=71")) {
                switch (getNpcId()) {
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-118030, 47930, 465, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=72")) {
                switch (getNpcId()) {
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-119221, 46981, 380, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            } else if (command.endsWith("reply=73")) {
                switch (getNpcId()) {
                    case 32135:
                        ThreadPoolManager.getInstance().schedule(new RadarTask(-118080, 42835, 720, player), 200L);
                        showChatWindow(player, "pts/newbie_guide/guide_krenisk/guide_krenisk_q0209_05.htm");
                        break;
                }
            }
        } else if (command.startsWith("menu_select?ask=-1000&")) {
            if (command.endsWith("reply=1")) {
                switch (getNpcId()) {
                    case 30598:
                    case 30599:
                    case 30600:
                    case 30601:
                    case 30602:
                    case 31076:
                    case 31077:
                    case 32135:
                        if (player.getLevel() > 5) {
                            if (player.getLevel() < 20 && player.getPlayerClassComponent().getClassId().level() == 0) {
                                QuestState GuideMission = player.getQuestState(41);
                                if (!player.getPlayerVariables().getBoolean(PlayerVariables.NG_207) && GuideMission != null && !GuideMission.isCompleted()) {
                                    ItemFunctions.addItem(player, q_adventure_coupon1, 5);
                                    player.getPlayerVariables().set(PlayerVariables.NG_207, "207", -1);
                                    showChatWindow(player, fnCoupon1Ok);
                                    GuideMission.setMemoState("guide_mission", String.valueOf(GuideMission.getInt("guide_mission") + 100), true);
                                    player.sendPacket(new ExShowScreenMessage(NpcString.ACQUISITION_OF_WEAPON_EXCHANGE_COUPON_FOR_BEGINNERS_COMPLETE_GO_SPEAK_WITH_THE_NEWBIE_GUIDE, 5000, ScreenMessageAlign.TOP_CENTER, true));
                                } else
                                    showChatWindow(player, fnCoupon1Not2);
                            } else
                                showChatWindow(player, fnCoupon1Not1);
                        } else
                            showChatWindow(player, fnCoupon1Not1);
                        break;
                }
            } else if (command.endsWith("reply=2")) {
                switch (getNpcId()) {
                    case 30598:
                    case 30599:
                    case 30600:
                    case 30601:
                    case 30602:
                    case 31076:
                    case 31077:
                    case 32135:
                        if (player.getPlayerClassComponent().getClassId().level() == 1) {
                            if (player.getLevel() < 40) {
                                if (!player.getPlayerVariables().getBoolean(PlayerVariables.NG_208)) {
                                    ItemFunctions.addItem(player, q_adventure_coupon2, 1);
                                    player.getPlayerVariables().set(PlayerVariables.NG_208, "208", -1);
                                    showChatWindow(player, fnCoupon2Ok);
                                } else
                                    showChatWindow(player, fnCoupon2Not2);
                            } else
                                showChatWindow(player, fnCoupon2Not1);
                        } else
                            showChatWindow(player, fnCoupon2Not1);
                        break;
                }
            }
        } else if (command.startsWith("Teleport")) {
            if (player.getLevel() >= 20)
                showChatWindow(player, "pts/newbie_guide/guide_teleport_over001.htm");
            else if (player.getTransformationId() == 111 || player.getTransformationId() == 112 || player.getTransformationId() == 124)
                showChatWindow(player, "pts/newbie_guide/q194_noteleport.htm");
            else
                super.onBypassFeedback(player, command);
        } else
            super.onBypassFeedback(player, command);
    }

    private class RadarTask extends RunnableImpl {
        private final int x, y, z;
        private final Player player;

        RadarTask(final int x, final int y, final int z, final Player player) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.player = player;
        }

        @Override
        public void runImpl() throws Exception {
            player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, x, y, z));
        }
    }
}
