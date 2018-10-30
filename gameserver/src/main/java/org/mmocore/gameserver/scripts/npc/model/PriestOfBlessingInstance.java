package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.AccountVariables;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author Unknown
 * @author KilRoy
 */
public class PriestOfBlessingInstance extends NpcInstance {
    private static final String fnHi = "pts/vote_manager/vote_manager001.htm";
    private static final String fnHi1 = "pts/vote_manager/vote_manager002.htm";
    private static final String fnHi2 = "pts/vote_manager/vote_manager003.htm";
    private static final String fnHi3 = "pts/vote_manager/vote_manager004.htm";
    private static final String fnHi4 = "pts/vote_manager/vote_manager005.htm";
    private static final String fnHi5 = "pts/vote_manager/vote_manager006.htm";
    private static final String fnHi6 = "pts/vote_manager/vote_manager007.htm";
    private static final String fnHi7 = "pts/vote_manager/vote_manager008.htm";
    private static final int vote_fee1 = 4000;
    private static final int vote_fee2 = 30000;
    private static final int vote_fee3 = 110000;
    private static final int vote_fee4 = 310000;
    private static final int vote_fee5 = 970000;
    private static final int vote_fee6 = 2160000;
    private static final int vote_fee7 = 5000000;
    private static final int vote_count_fee = 100000;
    private static final int vote_limit_time = 20;
    private static final int vote_package10 = 17094;
    private static final int vote_package11 = 17095;
    private static final int vote_package12 = 17096;
    private static final int vote_package13 = 17097;
    private static final int vote_package14 = 17098;
    private static final int vote_package15 = 17099;
    private static final int vote_package21 = 17100;
    private static final int vote_package22 = 17101;
    private static final int vote_package23 = 17102;
    private static final int vote_package24 = 17103;
    private static final int vote_package25 = 17104;
    private static final int vote_package31 = 17105;
    private static final int vote_package32 = 17106;
    private static final int vote_package33 = 17107;
    private static final int vote_package34 = 17108;
    private static final int vote_package35 = 17109;
    private static final int vote_package41 = 17110;
    private static final int vote_package42 = 17111;
    private static final int vote_package43 = 17112;
    private static final int vote_package44 = 17113;
    private static final int vote_package45 = 17114;
    private static final int vote_package51 = 17115;
    private static final int vote_package52 = 17116;
    private static final int vote_package53 = 17117;
    private static final int vote_package54 = 17118;
    private static final int vote_package55 = 17119;
    private static final int vote_package61 = 17120;
    private static final int vote_package62 = 17121;
    private static final int vote_package63 = 17122;
    private static final int vote_package64 = 17123;
    private static final int vote_package65 = 17124;
    private static final int vote_package71 = 17125;
    private static final int vote_package72 = 17126;
    private static final int vote_package73 = 17127;
    private static final int vote_package74 = 17128;
    private static final int vote_package75 = 17129;

    public PriestOfBlessingInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(final Player player, final int val, final Object... arg) {
        if (player.getLevel() <= 19) {
            showChatWindow(player, fnHi);
        } else if (player.getLevel() <= 39) {
            showChatWindow(player, fnHi1);
        } else if (player.getLevel() <= 51) {
            showChatWindow(player, fnHi2);
        } else if (player.getLevel() <= 60) {
            showChatWindow(player, fnHi3);
        } else if (player.getLevel() <= 75) {
            showChatWindow(player, fnHi4);
        } else if (player.getLevel() <= 79) {
            showChatWindow(player, fnHi5);
        } else if (player.getLevel() <= 85) {
            showChatWindow(player, fnHi6);
        } else {
            super.showChatWindow(player, val);
        }
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        final int i0 = Rnd.get(4);

        if (command.startsWith("menu_select?ask=20002&")) {
            if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - (player.getInventoryLimit() * 0.20)) {
                player.sendPacket(SystemMsg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                return;
            } else {
                if (command.endsWith("reply=1")) {
                    if (ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= vote_count_fee) {
                        buyLimitedItem(player, AccountVariables.BUY_LIMITED_NEVITS_VOICE, vote_package10, vote_count_fee);
                    } else {
                        showChatWindow(player, fnHi7);
                    }
                } else if (command.endsWith("reply=2")) {
                    if (ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= vote_fee1) {
                        switch (i0) {
                            case 4:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_20, vote_package11, vote_fee1);
                                break;
                            case 3:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_20, vote_package12, vote_fee1);
                                break;
                            case 2:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_20, vote_package13, vote_fee1);
                                break;
                            case 1:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_20, vote_package14, vote_fee1);
                                break;
                            case 0:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_20, vote_package15, vote_fee1);
                                break;
                        }
                    } else {
                        showChatWindow(player, fnHi7);
                    }
                }
            }
        } else if (command.startsWith("menu_select?ask=20003&")) {
            if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - (player.getInventoryLimit() * 0.20)) {
                player.sendPacket(SystemMsg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                return;
            } else {
                if (command.endsWith("reply=1")) {
                    if (ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= vote_count_fee) {
                        buyLimitedItem(player, AccountVariables.BUY_LIMITED_NEVITS_VOICE, vote_package10, vote_count_fee);
                    } else {
                        showChatWindow(player, fnHi7);
                    }
                } else if (command.endsWith("reply=2")) {
                    if (ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= vote_fee2) {
                        switch (i0) {
                            case 4:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_59, vote_package21, vote_fee2);
                                break;
                            case 3:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_59, vote_package22, vote_fee2);
                                break;
                            case 2:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_59, vote_package23, vote_fee2);
                                break;
                            case 1:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_59, vote_package24, vote_fee2);
                                break;
                            case 0:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_59, vote_package25, vote_fee2);
                                break;
                        }
                    } else {
                        showChatWindow(player, fnHi7);
                    }
                }
            }
        } else if (command.startsWith("menu_select?ask=20004&")) {
            if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - (player.getInventoryLimit() * 0.20)) {
                player.sendPacket(SystemMsg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                return;
            } else {
                if (command.endsWith("reply=1")) {
                    if (ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= vote_count_fee) {
                        buyLimitedItem(player, AccountVariables.BUY_LIMITED_NEVITS_VOICE, vote_package10, vote_count_fee);
                    } else {
                        showChatWindow(player, fnHi7);
                    }
                } else if (command.endsWith("reply=2")) {
                    if (ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= vote_fee3) {
                        switch (i0) {
                            case 4:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_91, vote_package31, vote_fee3);
                                break;
                            case 3:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_91, vote_package32, vote_fee3);
                                break;
                            case 2:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_91, vote_package33, vote_fee3);
                                break;
                            case 1:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_91, vote_package34, vote_fee3);
                                break;
                            case 0:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_91, vote_package35, vote_fee3);
                                break;
                        }
                    } else {
                        showChatWindow(player, fnHi7);
                    }
                }
            }
        } else if (command.startsWith("menu_select?ask=20005&")) {
            if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - (player.getInventoryLimit() * 0.20)) {
                player.sendPacket(SystemMsg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                return;
            } else {
                if (command.endsWith("reply=1")) {
                    if (ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= vote_count_fee) {
                        buyLimitedItem(player, AccountVariables.BUY_LIMITED_NEVITS_VOICE, vote_package10, vote_count_fee);
                    } else {
                        showChatWindow(player, fnHi7);
                    }
                } else if (command.endsWith("reply=2")) {
                    if (ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= vote_fee4) {
                        switch (i0) {
                            case 4:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_112, vote_package41, vote_fee4);
                                break;
                            case 3:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_112, vote_package42, vote_fee4);
                                break;
                            case 2:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_112, vote_package43, vote_fee4);
                                break;
                            case 1:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_112, vote_package44, vote_fee4);
                                break;
                            case 0:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_112, vote_package45, vote_fee4);
                                break;
                        }
                    } else {
                        showChatWindow(player, fnHi7);
                    }
                }
            }
        } else if (command.startsWith("menu_select?ask=20006&")) {
            if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - (player.getInventoryLimit() * 0.20)) {
                player.sendPacket(SystemMsg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                return;
            } else {
                if (command.endsWith("reply=1")) {
                    if (ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= vote_count_fee) {
                        buyLimitedItem(player, AccountVariables.BUY_LIMITED_NEVITS_VOICE, vote_package10, vote_count_fee);
                    } else {
                        showChatWindow(player, fnHi7);
                    }
                } else if (command.endsWith("reply=2")) {
                    if (ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= vote_fee5) {
                        switch (i0) {
                            case 4:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_136, vote_package51, vote_fee5);
                                break;
                            case 3:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_136, vote_package52, vote_fee5);
                                break;
                            case 2:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_136, vote_package53, vote_fee5);
                                break;
                            case 1:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_136, vote_package54, vote_fee5);
                                break;
                            case 0:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_136, vote_package55, vote_fee5);
                                break;
                        }
                    } else {
                        showChatWindow(player, fnHi7);
                    }
                }
            }
        } else if (command.startsWith("menu_select?ask=20007&")) {
            if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - (player.getInventoryLimit() * 0.20)) {
                player.sendPacket(SystemMsg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                return;
            } else {
                if (command.endsWith("reply=1")) {
                    if (ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= vote_count_fee) {
                        buyLimitedItem(player, AccountVariables.BUY_LIMITED_NEVITS_VOICE, vote_package10, vote_count_fee);
                    } else {
                        showChatWindow(player, fnHi7);
                    }
                } else if (command.endsWith("reply=2")) {
                    if (ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= vote_fee6) {
                        switch (i0) {
                            case 4:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_155, vote_package61, vote_fee6);
                                break;
                            case 3:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_155, vote_package62, vote_fee6);
                                break;
                            case 2:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_155, vote_package63, vote_fee6);
                                break;
                            case 1:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_155, vote_package64, vote_fee6);
                                break;
                            case 0:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_155, vote_package65, vote_fee6);
                                break;
                        }
                    } else {
                        showChatWindow(player, fnHi7);
                    }
                }
            }
        } else if (command.startsWith("menu_select?ask=20008&")) {
            if (player.getWeightPenalty() >= 3 || player.getInventory().getSize() > player.getInventoryLimit() - (player.getInventoryLimit() * 0.20)) {
                player.sendPacket(SystemMsg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
                return;
            } else {
                if (command.endsWith("reply=1")) {
                    if (ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= vote_count_fee) {
                        buyLimitedItem(player, AccountVariables.BUY_LIMITED_NEVITS_VOICE, vote_package10, vote_count_fee);
                    } else {
                        showChatWindow(player, fnHi7);
                    }
                } else if (command.endsWith("reply=2")) {
                    if (ItemFunctions.getItemCount(player, ItemTemplate.ITEM_ID_ADENA) >= vote_fee7) {
                        switch (i0) {
                            case 4:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_165, vote_package71, vote_fee7);
                                break;
                            case 3:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_165, vote_package72, vote_fee7);
                                break;
                            case 2:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_165, vote_package73, vote_fee7);
                                break;
                            case 1:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_165, vote_package74, vote_fee7);
                                break;
                            case 0:
                                buyLimitedItem(player, PlayerVariables.BUY_LIMITED_HOURGLASS_165, vote_package75, vote_fee7);
                                break;
                        }
                    } else {
                        showChatWindow(player, fnHi7);
                    }
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    private void buyLimitedItem(final Player player, final AccountVariables variable, final int itemId, final int price) {
        final long _remaining_time;
        final long _reuse_time = vote_limit_time * 60 * 60 * 1000;
        final long _curr_time = System.currentTimeMillis();

        if (player.getAccountVariables().get(variable) != null) {
            _remaining_time = _curr_time - player.getAccountVariables().getLong(variable);
        } else {
            _remaining_time = _reuse_time;
        }

        if (_remaining_time >= _reuse_time) {
            if (player.reduceAdena(price, true)) {
                ItemFunctions.addItem(player, itemId, 1);
                player.getAccountVariables().set(variable, String.valueOf(_curr_time), -1);
            } else {
                player.sendPacket(new SystemMessage(SystemMsg.S2_UNITS_OF_THE_ITEM_S1_ISARE_REQUIRED).addItemName(57).addNumber(price));
            }
        } else {
            final int hours = (int) (_reuse_time - _remaining_time) / 3600000;
            final int minutes = (int) (_reuse_time - _remaining_time) % 3600000 / 60000;
            if (hours > 0) {
                player.sendPacket(new SystemMessage(SystemMsg.THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(hours).addNumber(minutes));
            } else if (minutes > 0) {
                player.sendPacket(new SystemMessage(SystemMsg.THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(minutes));
            } else if (player.reduceAdena(price, true)) {
                ItemFunctions.addItem(player, itemId, 1);
                player.getAccountVariables().set(variable, String.valueOf(_curr_time), -1);
            } else {
                player.sendPacket(new SystemMessage(SystemMsg.S2_UNITS_OF_THE_ITEM_S1_ISARE_REQUIRED).addItemName(57).addNumber(price));
            }
        }
    }

    private void buyLimitedItem(final Player player, final PlayerVariables variable, final int itemId, final int price) {
        final long _remaining_time;
        final long _reuse_time = vote_limit_time * 60 * 60 * 1000;
        final long _curr_time = System.currentTimeMillis();
        String _last_use_time = player.getPlayerVariables().get(variable);

        if (_last_use_time != null) {
            _remaining_time = _curr_time - Long.parseLong(_last_use_time);
        } else {
            _remaining_time = _reuse_time;
        }

        if (_remaining_time >= _reuse_time) {
            if (player.reduceAdena(price, true)) {
                ItemFunctions.addItem(player, itemId, 1);
                player.getPlayerVariables().set(variable, String.valueOf(_curr_time), -1);
            } else {
                player.sendPacket(new SystemMessage(SystemMsg.S2_UNITS_OF_THE_ITEM_S1_ISARE_REQUIRED).addItemName(57).addNumber(price));
            }
        } else {
            final int hours = (int) (_reuse_time - _remaining_time) / 3600000;
            final int minutes = (int) (_reuse_time - _remaining_time) % 3600000 / 60000;
            if (hours > 0) {
                player.sendPacket(new SystemMessage(SystemMsg.THERE_ARE_S1_HOURSS_AND_S2_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(hours).addNumber(minutes));
            } else if (minutes > 0) {
                player.sendPacket(new SystemMessage(SystemMsg.THERE_ARE_S1_MINUTES_REMAINING_UNTIL_THE_TIME_WHEN_THE_ITEM_CAN_BE_PURCHASED).addNumber(minutes));
            } else if (player.reduceAdena(price, true)) {
                ItemFunctions.addItem(player, itemId, 1);
                player.getPlayerVariables().set(variable, String.valueOf(_curr_time), -1);
            } else {
                player.sendPacket(new SystemMessage(SystemMsg.S2_UNITS_OF_THE_ITEM_S1_ISARE_REQUIRED).addItemName(57).addNumber(price));
            }
        }
    }
}