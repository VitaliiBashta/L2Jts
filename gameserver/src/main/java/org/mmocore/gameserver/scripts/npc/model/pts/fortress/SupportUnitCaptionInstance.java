package org.mmocore.gameserver.scripts.npc.model.pts.fortress;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author : Mangol
 */
public class SupportUnitCaptionInstance extends NpcInstance {
    protected static final int COND_ALL_FALSE = 0;
    protected static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
    protected static final int COND_OWNER = 2;
    private static final int shoulder_strap_of_knight = 9912;
    private static final int blue_talisman_mp_relax = 9931;
    private static final int blue_talisman_celestial_shield = 9932;
    private static final int red_talisman_detonation = 10416;
    private static final int red_talisman_m_detonation = 10417;
    private static final int red_talisman_clarity = 9917;
    private static final int red_talisman_clarity_special = 9918;
    private static final int blue_talisman_mp_regen_up = 9928;
    private static final int blue_talisman_shield_rate_up = 9929;
    private static final int blue_talisman_silent_move = 9920;
    private static final int red_talisman_berserker_spirit = 9916;
    private static final int blue_talisman_cure_hold = 9923;
    private static final int blue_talisman_heal_empower = 9924;
    private static final int blue_talisman_heal = 9925;
    private static final int blue_talisman_shield = 9926;
    private static final int blue_talisman_magic_barrier = 9927;
    private static final int blue_talisman_vatality = 10518;
    private static final int blue_talisman_great_healing = 10424;
    private static final int red_talisman_might = 9914;
    private static final int red_talisman_wild_magic = 9915;
    private static final int blue_talisman_shield_defence_up = 9921;
    private static final int blue_talisman_cure_bleeding = 9922;
    private static final int yellow_talisman_pa_up = 9933;
    private static final int yellow_talisman_attack_speed_up = 9934;
    private static final int yellow_talisman_md_up = 9935;
    private static final int yellow_talisman_ma_up = 9936;
    private static final int yellow_talisman_magic_speed_up = 9937;
    private static final int yellow_talisman_hit_up = 9938;
    private static final int yellow_talisman_pd_up = 9939;
    private static final int yellow_talisman_all_speed_up = 9940;
    private static final int yellow_talisman_speed_up = 9941;
    private static final int yellow_talisman_critical_damage_down = 9942;
    private static final int yellow_talisman_critical_damage_up = 9943;
    private static final int yellow_talisman_critical_rate_down = 9944;
    private static final int yellow_talisman_avoid_up = 9945;
    private static final int yellow_talisman_heal_up = 9946;
    private static final int yellow_talisman_cp_regen_up = 9947;
    private static final int yellow_talisman_hp_regen_up = 9948;
    private static final int yellow_talisman_mp_regen_up = 9949;
    private static final int gray_talisman_weight_up = 9950;
    private static final int orange_talisman_create_healing_potion = 9952;
    private static final int orange_talisman_create_elixir_of_mental_c = 9953;
    private static final int orange_talisman_create_elixir_of_mental_b = 9954;
    private static final int black_talisman_cure_m_silence = 9955;
    private static final int black_talisman_cure_m_hold = 9956;
    private static final int black_talisman_cure_p_hold = 9957;
    private static final int black_talisman_cure_p_silence = 9958;
    private static final int black_talisman_cure_silence = 9959;
    private static final int white_talisman_derangement_resist_up = 9960;
    private static final int white_talisman_paralyze_resist_up = 9961;
    private static final int white_talisman_shock_resist_up = 9962;
    private static final int white_talisman_sleep_resist_up = 9963;
    private static final int white_talisman_bleed_resist_up = 9964;
    private static final int white_talisman_cancel_resist_up = 9965;
    private static final int white_talisman_storm = 10418;
    private static final int white_talisman_water = 10420;
    private static final int white_talisman_earth = 10519;
    private static final int white_talisman_light = 10422;
    private static final int blue_talisman_explosion = 10423;
    private static final int white_talisman_darkness = 10419;
    private static final int white_talisman_fire = 10421;
    private static final String fnHi = "pts/fortress/fortress_wizard001.htm";
    private static final String fnHi2 = "pts/fortress/fortress_wizard002.htm";
    private static final String fnHi3 = "pts/fortress/fortress_wizard003.htm";
    private static final String fnHi4 = "pts/fortress/fortress_wizard004.htm";
    private static final String fnHi5 = "pts/fortress/fortress_wizard005.htm";
    private static final String fnHi6 = "pts/fortress/fortress_wizard006.htm";

    public SupportUnitCaptionInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        final Castle castle = getCastle();
        int condition = validateCondition(player);
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (condition == COND_BUSY_BECAUSE_OF_SIEGE) {
            return;
        }
        if (condition != COND_OWNER) {
            showChatWindow(player, fnHi5);
            return;
        }
		/*
		//TODO: Надо ли?
		else if((player.getClanPrivileges() & Clan.CP_CS_USE_FUNCTIONS) != Clan.CP_CS_USE_FUNCTIONS)
		{
			player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
			return;
		}
		*/
        if (command.startsWith("menu_select?ask=-301&")) {
            int i1 = 0;
            if (command.endsWith("reply=4")) {
                showChatWindow(player, fnHi2);
            } else if (command.endsWith("reply=5")) {
                if (!player.isWeightLimit(true)) {
                    return;
                }
                if (!player.isInventoryLimit(true)) {
                    return;
                }
                int i0 = Rnd.get(100);
                if (ItemFunctions.getItemCount(player, shoulder_strap_of_knight) >= 10) {
                    ItemFunctions.removeItem(player, shoulder_strap_of_knight, 10);
                    if (i0 <= 5) {
                        i1 = Rnd.get(100);
                        if (Rnd.get(100) < 25) {
                            ItemFunctions.addItem(player, blue_talisman_mp_relax, 1);
                        } else if (i1 <= 50) {
                            ItemFunctions.addItem(player, blue_talisman_celestial_shield, 1);
                        } else if (i1 <= 75) {
                            ItemFunctions.addItem(player, red_talisman_detonation, 1);
                        } else {
                            ItemFunctions.addItem(player, red_talisman_m_detonation, 1);
                        }
                    } else if (i0 <= 15) {
                        i1 = Rnd.get(100);
                        if (i1 <= 20) {
                            ItemFunctions.addItem(player, red_talisman_clarity, 1);
                        } else if (i1 <= 40) {
                            ItemFunctions.addItem(player, red_talisman_clarity_special, 1);
                        } else if (i1 <= 60) {
                            ItemFunctions.addItem(player, blue_talisman_mp_regen_up, 1);
                        } else if (i1 <= 80) {
                            ItemFunctions.addItem(player, blue_talisman_shield_rate_up, 1);
                        } else {
                            ItemFunctions.addItem(player, blue_talisman_silent_move, 1);
                        }
                    } else if (i0 <= 30) {
                        i1 = Rnd.get(100);
                        if (i1 <= 12) {
                            ItemFunctions.addItem(player, red_talisman_berserker_spirit, 1);
                        } else if (i1 <= 25) {
                            ItemFunctions.addItem(player, blue_talisman_cure_hold, 1);
                        } else if (i1 <= 37) {
                            ItemFunctions.addItem(player, blue_talisman_heal_empower, 1);
                        } else if (i1 <= 50) {
                            ItemFunctions.addItem(player, blue_talisman_heal, 1);
                        } else if (i1 <= 62) {
                            ItemFunctions.addItem(player, blue_talisman_shield, 1);
                        } else if (i1 <= 75) {
                            ItemFunctions.addItem(player, blue_talisman_magic_barrier, 1);
                        } else if (i1 <= 87) {
                            ItemFunctions.addItem(player, blue_talisman_vatality, 1);
                        } else {
                            ItemFunctions.addItem(player, blue_talisman_great_healing, 1);
                        }
                    } else {
                        i1 = Rnd.get(46);
                        if (i1 == 0) {
                            ItemFunctions.addItem(player, red_talisman_might, 1);
                        } else if (i1 == 1) {
                            ItemFunctions.addItem(player, red_talisman_wild_magic, 1);
                        } else if (i1 == 2) {
                            ItemFunctions.addItem(player, blue_talisman_silent_move, 1);
                        } else if (i1 == 3) {
                            ItemFunctions.addItem(player, blue_talisman_silent_move, 1);
                        } else if (i1 == 4) {
                            ItemFunctions.addItem(player, blue_talisman_shield_defence_up, 1);
                        } else if (i1 == 5) {
                            ItemFunctions.addItem(player, blue_talisman_cure_bleeding, 1);
                        } else if (i1 == 6) {
                            ItemFunctions.addItem(player, yellow_talisman_pa_up, 1);
                        } else if (i1 == 7) {
                            ItemFunctions.addItem(player, yellow_talisman_attack_speed_up, 1);
                        } else if (i1 == 8) {
                            ItemFunctions.addItem(player, yellow_talisman_md_up, 1);
                        } else if (i1 == 9) {
                            ItemFunctions.addItem(player, yellow_talisman_ma_up, 1);
                        } else if (i1 == 10) {
                            ItemFunctions.addItem(player, yellow_talisman_magic_speed_up, 1);
                        } else if (i1 == 11) {
                            ItemFunctions.addItem(player, yellow_talisman_hit_up, 1);
                        } else if (i1 == 12) {
                            ItemFunctions.addItem(player, yellow_talisman_pd_up, 1);
                        } else if (i1 == 13) {
                            ItemFunctions.addItem(player, yellow_talisman_all_speed_up, 1);
                        } else if (i1 == 14) {
                            ItemFunctions.addItem(player, yellow_talisman_speed_up, 1);
                        } else if (i1 == 15) {
                            ItemFunctions.addItem(player, yellow_talisman_critical_damage_down, 1);
                        } else if (i1 == 16) {
                            ItemFunctions.addItem(player, yellow_talisman_critical_damage_up, 1);
                        } else if (i1 == 17) {
                            ItemFunctions.addItem(player, yellow_talisman_critical_rate_down, 1);
                        } else if (i1 == 18) {
                            ItemFunctions.addItem(player, yellow_talisman_avoid_up, 1);
                        } else if (i1 == 19) {
                            ItemFunctions.addItem(player, yellow_talisman_heal_up, 1);
                        } else if (i1 == 20) {
                            ItemFunctions.addItem(player, yellow_talisman_cp_regen_up, 1);
                        } else if (i1 == 21) {
                            ItemFunctions.addItem(player, yellow_talisman_hp_regen_up, 1);
                        } else if (i1 == 22) {
                            ItemFunctions.addItem(player, yellow_talisman_mp_regen_up, 1);
                        } else if (i1 == 23) {
                            ItemFunctions.addItem(player, gray_talisman_weight_up, 1);
                        } else if (i1 == 25) {
                            ItemFunctions.addItem(player, orange_talisman_create_healing_potion, 1);
                        } else if (i1 == 26) {
                            ItemFunctions.addItem(player, orange_talisman_create_elixir_of_mental_c, 1);
                        } else if (i1 == 27) {
                            ItemFunctions.addItem(player, orange_talisman_create_elixir_of_mental_b, 1);
                        } else if (i1 == 28) {
                            ItemFunctions.addItem(player, black_talisman_cure_m_silence, 1);
                        } else if (i1 == 29) {
                            ItemFunctions.addItem(player, black_talisman_cure_m_hold, 1);
                        } else if (i1 == 30) {
                            ItemFunctions.addItem(player, black_talisman_cure_p_hold, 1);
                        } else if (i1 == 31) {
                            ItemFunctions.addItem(player, black_talisman_cure_p_silence, 1);
                        } else if (i1 == 32) {
                            ItemFunctions.addItem(player, black_talisman_cure_silence, 1);
                        } else if (i1 == 33) {
                            ItemFunctions.addItem(player, white_talisman_derangement_resist_up, 1);
                        } else if (i1 == 34) {
                            ItemFunctions.addItem(player, white_talisman_paralyze_resist_up, 1);
                        } else if (i1 == 35) {
                            ItemFunctions.addItem(player, white_talisman_shock_resist_up, 1);
                        } else if (i1 == 36) {
                            ItemFunctions.addItem(player, white_talisman_sleep_resist_up, 1);
                        } else if (i1 == 37) {
                            ItemFunctions.addItem(player, white_talisman_bleed_resist_up, 1);
                        } else if (i1 == 24) {
                            ItemFunctions.addItem(player, white_talisman_cancel_resist_up, 1);
                        } else if (i1 == 38) {
                            ItemFunctions.addItem(player, white_talisman_storm, 1);
                        } else if (i1 == 39) {
                            ItemFunctions.addItem(player, white_talisman_water, 1);
                        } else if (i1 == 40) {
                            ItemFunctions.addItem(player, white_talisman_earth, 1);
                        } else if (i1 == 41) {
                            ItemFunctions.addItem(player, white_talisman_light, 1);
                        } else if (i1 == 42) {
                            ItemFunctions.addItem(player, blue_talisman_explosion, 1);
                        } else if (i1 == 43) {
                            ItemFunctions.addItem(player, white_talisman_darkness, 1);
                        } else {
                            ItemFunctions.addItem(player, white_talisman_fire, 1);
                        }
                    }
                    showChatWindow(player, fnHi3);
                } else {
                    showChatWindow(player, fnHi6);
                }
            } else if (command.endsWith("reply=6")) {
                if (condition == COND_OWNER && (player.getClanPrivileges() & Clan.CP_CS_USE_FUNCTIONS) == Clan.CP_CS_USE_FUNCTIONS) {
                    showSubUnitSkillList(player);
                } else {
                    showChatWindow(player, fnHi4);
                }
            } else {
                showChatWindow(player, fnHi4);
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        player.sendActionFailed();
        int condition = validateCondition(player);
        if (condition == COND_BUSY_BECAUSE_OF_SIEGE) {
            return;
        } else if (condition == COND_OWNER) {
            showChatWindow(player, fnHi);
        } else {
            showChatWindow(player, fnHi5);
        }
    }

    protected int validateCondition(Player player) {
        if (player.isGM()) {
            return COND_OWNER;
        }
        if (getFortress() != null && getFortress().getId() > 0) {
            if (player.getClan() != null) {
                if (getFortress().getSiegeEvent().isInProgress()) {
                    return COND_BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
                } else if (getFortress().getOwnerId() == player.getClanId()) // Clan owns fortress
                {
                    return COND_OWNER;
                }
            }
        }
        return COND_ALL_FALSE;
    }
}
