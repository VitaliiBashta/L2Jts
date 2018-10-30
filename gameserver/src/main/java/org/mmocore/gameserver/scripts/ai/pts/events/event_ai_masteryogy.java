package org.mmocore.gameserver.scripts.ai.pts.events;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.scripts.events.NcSoft.MasterOfEnchanting.MasterOfEnchanting;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class event_ai_masteryogy extends DefaultAI {
    private static final int yogy_staff = 13539;
    private static final int yogy_scroll = 13540;
    private static final int small_ultra_bomb = 6406;
    private static final int ultra_bomb = 6407;
    private static final int event_magicial_cap_bytime = 13074;
    private static final int event_jjoro_mask_bytime = 13075;
    private static final int event_dragonfly_goggle_bytime = 13076;
    private static final int scrl_of_ench_wp_d = 955;
    private static final int scrl_of_ench_am_d = 956;
    private static final int scrl_of_ench_wp_c = 951;
    private static final int scrl_of_ench_am_c = 952;
    private static final int scrl_of_ench_wp_b = 947;
    private static final int scrl_of_ench_wp_a = 729;
    private static final int event_magicial_cap = 13518;
    private static final int event_jjoro_mask = 13519;
    private static final int event_dragonfly_goggle = 13522;
    private static final int event_s_accessory_box = 13992;
    private static final int unique_75_s = 8762;
    private static final int rare_75_s = 8752;
    private static final int scrl_of_ench_wp_s = 959;
    private static final int event_s_weapon_box = 13990;
    private static final int event_s_armor_box = 13991;
    private static final int event_s80_weapon_box = 13988;
    private static final int event_s80_armor_box = 13989;
    private static final int red_soul_crystal_14 = 9570;
    private static final int green_soul_crystal_14 = 9572;
    private static final int blue_soul_crystal_14 = 9571;

    public event_ai_masteryogy(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        AddTimerEx(1000, 60000);
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        if (timer_id == 1000) {
            if (Rnd.get(5) < 1) {
                if (Rnd.get(2) < 1) {
                    ChatUtils.say(getActor(), NpcString.CARE_TO_CHALLENGE_FATE_AND_TEST_YOUR_LUCK);
                } else {
                    ChatUtils.say(getActor(), NpcString.DONT_PASS_UP_THE_CHANCE_TO_WIN_AN_S80_WEAPON);
                }
            }
            AddTimerEx(1000, 60000);
        }
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final NpcInstance actor = getActor();
        if (ask == 1000) {
            if (reply == 1) {
                if (ItemFunctions.getItemCount(player, 57) >= 1000 && ItemFunctions.getItemCount(player, yogy_staff) == 0) {
                    if (player.isWeightLimit(true) && player.isInventoryLimit(true)) {
                        actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_q01_05.htm");
                        ItemFunctions.addItem(player, yogy_staff, 1);
                        ItemFunctions.removeItem(player, 57, 1000);
                    }
                } else {
                    actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_q01_05f.htm");
                }
            } else if (reply == 2) {
                if (MasterOfEnchanting.isActive()) {
                    if (ItemFunctions.getItemCount(player, 57) >= 6000) {
                        if (player.isWeightLimit(true) && player.isInventoryLimit(true)) {
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_q01_06.htm");
                            if (!player.getPlayerVariables().getBoolean(PlayerVariables.BUY_LIMITED_YOGI_SCROLL)) {
                                ItemFunctions.addItem(player, yogy_scroll, 1);
                                player.getPlayerVariables().set(PlayerVariables.BUY_LIMITED_YOGI_SCROLL, System.currentTimeMillis(), System.currentTimeMillis() + 21600000);
                            } else {
                                final long reuseTime = 21600000;
                                final long remaingTime = System.currentTimeMillis() - player.getPlayerVariables().getLong(PlayerVariables.BUY_LIMITED_YOGI_SCROLL);
                                final int hours = (int) (reuseTime - remaingTime) / 3600000;
                                final int minutes = (int) (reuseTime - remaingTime) % 3600000 / 60000;
                                if (hours > 0) {
                                    player.sendPacket(new SystemMessage(SystemMsg.THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(hours).addNumber(minutes));
                                } else {
                                    player.sendPacket(new SystemMessage(SystemMsg.THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(minutes));
                                }
                            }
                        }
                    } else {
                        actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_q01_06f.htm");
                    }
                } else {
                    actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi027.htm");
                }
            } else if (reply == 3) {
                if (MasterOfEnchanting.isActive()) {
                    if (ItemFunctions.getItemCount(player, 57) >= 77777) {
                        if (player.isWeightLimit(true) && player.isInventoryLimit(true)) {
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_q01_07.htm");
                            ItemFunctions.addItem(player, yogy_scroll, 1);
                            ItemFunctions.removeItem(player, 57, 77777);
                        }
                    } else {
                        actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_q01_07f.htm");
                    }
                } else {
                    actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi027.htm");
                }
            } else if (reply == 4) {
                if (MasterOfEnchanting.isActive()) {
                    if (ItemFunctions.getItemCount(player, 57) >= 777770) {
                        if (player.isWeightLimit(true) && player.isInventoryLimit(true)) {
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_q01_07.htm");
                            ItemFunctions.addItem(player, yogy_scroll, 10);
                            ItemFunctions.removeItem(player, 57, 777770);
                        }
                    } else {
                        actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_fail_q01_26.htm");
                    }
                } else {
                    actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi027.htm");
                }
            }
        } else if (ask == 2000) {
            if (player.getActiveWeaponInstance() != null && player.getActiveWeaponInstance().getItemId() == yogy_staff) {
                final ItemInstance staff = player.getActiveWeaponInstance();
                if (reply == 1) {
                    switch (staff.getEnchantLevel()) {
                        case 1:
                        case 2:
                        case 3:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_fail_q01_08.htm");
                            break;
                        case 4:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_1_4_7_q01_09.htm");
                            ItemFunctions.addItem(player, small_ultra_bomb, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        case 5:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_1_4_7_q01_09.htm");
                            ItemFunctions.addItem(player, small_ultra_bomb, 2);
                            ItemFunctions.addItem(player, ultra_bomb, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        case 6:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_1_4_7_q01_09.htm");
                            ItemFunctions.addItem(player, small_ultra_bomb, 3);
                            ItemFunctions.addItem(player, ultra_bomb, 2);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        case 7:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_1_4_7_q01_09.htm");
                            int i0 = Rnd.get(3);
                            if (i0 == 0) {
                                ItemFunctions.addItem(player, event_magicial_cap_bytime, 1);
                            } else if (i0 == 1) {
                                ItemFunctions.addItem(player, event_jjoro_mask_bytime, 1);
                            } else {
                                ItemFunctions.addItem(player, event_dragonfly_goggle_bytime, 1);
                            }
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        case 8:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_1_4_7_q01_09.htm");
                            ItemFunctions.addItem(player, scrl_of_ench_wp_d, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        case 9:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_2_8_15_q01_10.htm");
                            ItemFunctions.addItem(player, scrl_of_ench_wp_d, 1);
                            ItemFunctions.addItem(player, scrl_of_ench_am_d, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        case 10:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_2_8_15_q01_10.htm");
                            ItemFunctions.addItem(player, scrl_of_ench_wp_c, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        case 11:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_2_8_15_q01_10.htm");
                            ItemFunctions.addItem(player, scrl_of_ench_wp_c, 1);
                            ItemFunctions.addItem(player, scrl_of_ench_am_c, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        case 12:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_2_8_15_q01_10.htm");
                            ItemFunctions.addItem(player, scrl_of_ench_wp_b, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        case 13:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_2_8_15_q01_10.htm");
                            ItemFunctions.addItem(player, scrl_of_ench_wp_a, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        case 14:
                            i0 = Rnd.get(3);
                            if (i0 == 0) {
                                ItemFunctions.addItem(player, event_magicial_cap, 1);
                            } else if (i0 == 1) {
                                ItemFunctions.addItem(player, event_jjoro_mask, 1);
                            } else {
                                ItemFunctions.addItem(player, event_dragonfly_goggle, 1);
                            }
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_3_16_24_q01_11.htm");
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        case 15:
                            ItemFunctions.addItem(player, event_s_accessory_box, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_fail_q01_08.htm");
                            break;
                        case 16:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_fail_q01_08.htm");
                            ItemFunctions.addItem(player, unique_75_s, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        case 17:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_3_16_24_q01_11.htm");
                            ItemFunctions.addItem(player, scrl_of_ench_wp_s, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        case 18:
                            ItemFunctions.addItem(player, event_s_armor_box, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_4_26_q01_13.htm");
                            break;
                        case 19:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_coax_25_q01_19.htm");
                            break;
                        case 20:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_coax_26_q01_20.htm");
                            break;
                        case 21:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_coax_27_q01_21.htm");
                            break;
                        case 22:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_coax_28_q01_22.htm");
                            break;
                        case 23:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_5_29_q01_16.htm");
                            ItemFunctions.addItem(player, event_s80_weapon_box, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        default:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_5_30_q01_17.htm");
                            ItemFunctions.addItem(player, event_s80_weapon_box, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                    }
                }
            } else {
                actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_fail_q01_25.htm");
            }
        } else if (ask == 3000) {
            if (reply == 1) {
                if (player.getActiveWeaponInstance() != null && player.getActiveWeaponInstance().getItemId() == yogy_staff) {
                    final ItemInstance staff = player.getActiveWeaponInstance();
                    switch (staff.getEnchantLevel()) {
                        case 19:
                            ItemFunctions.addItem(player, event_s_weapon_box, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_4_27_q01_14.htm");
                            break;
                        case 20:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_3_16_24_q01_11.htm");
                            int i0 = Rnd.get(3);
                            if (i0 == 0) {
                                ItemFunctions.addItem(player, red_soul_crystal_14, 1);
                            } else if (i0 == 1) {
                                ItemFunctions.addItem(player, green_soul_crystal_14, 1);
                            } else {
                                ItemFunctions.addItem(player, blue_soul_crystal_14, 1);
                            }
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        case 21:
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_3_16_24_q01_11.htm");
                            ItemFunctions.addItem(player, unique_75_s, 1);
                            ItemFunctions.addItem(player, rare_75_s, 1);
                            i0 = Rnd.get(3);
                            if (i0 == 0) {
                                ItemFunctions.addItem(player, red_soul_crystal_14, 1);
                            } else if (i0 == 1) {
                                ItemFunctions.addItem(player, green_soul_crystal_14, 1);
                            } else {
                                ItemFunctions.addItem(player, blue_soul_crystal_14, 1);
                            }
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            break;
                        case 22:
                            ItemFunctions.addItem(player, event_s80_armor_box, 1);
                            ItemFunctions.removeItem(player, yogy_staff, 1);
                            actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_success_4_28_q01_15.htm");
                            break;
                    }
                } else {
                    actor.showChatWindow(player, "pts/events/master_of_enchanting/event_master_yogi_fail_q01_25.htm");
                }
            }
        }
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}