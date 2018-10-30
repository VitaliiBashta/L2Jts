package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.ItemFunctions;

import java.time.ZonedDateTime;

/**
 * @author KilRoy
 * Freya: 13184, 13185
 */
public class br_xmas_santa extends DefaultAI {
    private static final NpcString[] fString = {NpcString.HO_HO_HO_HAPPY_HOLIDAYS, NpcString.I_MUST_RAISE_THE_REINDEER_QUICKLY_THIS_YEARS_CHRISTMAS_GIFTS_HAVE_TO_BE_BELIVERED};
    private static final int THIS_YEAR = ZonedDateTime.now().getYear();

    public br_xmas_santa(NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final NpcInstance actor = getActor();
        if (actor == null)
            return;

        int i2 = 0, i1 = 0;
        if (ask == 50010) {
            switch (reply) {
                case 1: {
                    if (player.isQuestContinuationPossible(false)) {
                        if (ItemFunctions.getItemCount(player, 20763) >= 1) {
                            actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa004.htm");
                        } else if (ItemFunctions.getItemCount(player, 57) < 1000) {
                            actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa032.htm");
                        } else {
                            ItemFunctions.addItem(player, 20760, 3);
                            ItemFunctions.addItem(player, 20769, 3);
                            ItemFunctions.addItem(player, 20763, 1);
                            ItemFunctions.removeItem(player, 57, 1000);
                            actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa003.htm");
                        }
                    } else {
                        player.sendPacket(SystemMsg.THE_ITEM_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHT_QUANTITY_LIMIT_HAS_BEEN_EXCEEDED);
                    }
                    break;
                }
                case 2: {
                    if (ItemFunctions.getItemCount(player, 20756) < 1) {
                        actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa007.htm");
                    } else {
                        final int i3 = 4 + Rnd.get(13);
                        ItemFunctions.addItem(player, 20759, i3);
                        ItemFunctions.removeItem(player, 20756, 1);
                        actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa006.htm");
                    }
                    break;
                }
                case 3: {
                    if (ItemFunctions.getItemCount(player, 20757) > 0) {
                        actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa035.htm");
                    } else if (ItemFunctions.getItemCount(player, 20755) > 0) {
                        actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa009.htm");
                    } else {
                        actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa024.htm");
                    }
                    break;
                }
                case 4: {
                    if (ItemFunctions.getItemCount(player, 20762) < 1) {
                        actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa031.htm");
                    } else {
                        ItemFunctions.addItem(player, 20761, 1);
                        ItemFunctions.removeItem(player, 20762, 1);
                        actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa030.htm");
                    }
                    break;
                }
                case 5: {
                    if (ItemFunctions.getItemCount(player, 57) < 500) {
                        actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa034.htm");
                    } else {
                        ItemFunctions.removeItem(player, 57, 500);
                        ItemFunctions.addItem(player, 20769, 1);
                        actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa033.htm");
                    }
                    break;
                }
                case 6: {
                    if (ItemFunctions.getItemCount(player, 57) < THIS_YEAR || player.getPlayerVariables().getBoolean(PlayerVariables.BUY_LIMITED_XMAS_PACK)) {
                        actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa038.htm");
                    } else {
                        player.getPlayerVariables().set(PlayerVariables.BUY_LIMITED_XMAS_PACK, "true", System.currentTimeMillis() + 86400000);
                        ItemFunctions.addItem(player, 20901, 1);
                        ItemFunctions.removeItem(player, 57, THIS_YEAR);
                        actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa037.htm");
                    }
                    break;
                }
                case 10: {
                    i1 = 20109;
                    i2 = 2500;
                    break;
                }
                case 11: {
                    i1 = 20123;
                    i2 = 2500;
                    break;
                }
                case 12: {
                    i1 = 20137;
                    i2 = 2500;
                    break;
                }
                case 13: {
                    i1 = 20151;
                    i2 = 2500;
                    break;
                }
                case 14: {
                    i1 = 20165;
                    i2 = 2500;
                    break;
                }
                case 15: {
                    i1 = 20110;
                    i2 = 81;
                    break;
                }
                case 16: {
                    i1 = 20124;
                    i2 = 81;
                    break;
                }
                case 17: {
                    i1 = 20138;
                    i2 = 81;
                    break;
                }
                case 18: {
                    i1 = 20152;
                    i2 = 81;
                    break;
                }
                case 19: {
                    i1 = 20166;
                    i2 = 81;
                    break;
                }
                case 20: {
                    i1 = 20111;
                    i2 = 236;
                    break;
                }
                case 21: {
                    i1 = 20125;
                    i2 = 236;
                    break;
                }
                case 22: {
                    i1 = 20139;
                    i2 = 236;
                    break;
                }
                case 23: {
                    i1 = 20153;
                    i2 = 236;
                    break;
                }
                case 24: {
                    i1 = 20167;
                    i2 = 236;
                    break;
                }
                case 25: {
                    i1 = 20112;
                    i2 = 164;
                    break;
                }
                case 26: {
                    i1 = 20126;
                    i2 = 164;
                    break;
                }
                case 27: {
                    i1 = 20140;
                    i2 = 164;
                    break;
                }
                case 28: {
                    i1 = 20154;
                    i2 = 164;
                    break;
                }
                case 29: {
                    i1 = 20168;
                    i2 = 164;
                    break;
                }
                case 30: {
                    i1 = 20113;
                    i2 = 7902;
                    break;
                }
                case 31: {
                    i1 = 20127;
                    i2 = 7902;
                    break;
                }
                case 32: {
                    i1 = 20141;
                    i2 = 7902;
                    break;
                }
                case 33: {
                    i1 = 20155;
                    i2 = 7902;
                    break;
                }
                case 34: {
                    i1 = 20169;
                    i2 = 7902;
                    break;
                }
                case 35: {
                    i1 = 20114;
                    i2 = 7895;
                    break;
                }
                case 36: {
                    i1 = 20128;
                    i2 = 7895;
                    break;
                }
                case 37: {
                    i1 = 20142;
                    i2 = 7895;
                    break;
                }
                case 38: {
                    i1 = 20156;
                    i2 = 7895;
                    break;
                }
                case 39: {
                    i1 = 20170;
                    i2 = 7895;
                    break;
                }
                case 40: {
                    i1 = 20115;
                    i2 = 213;
                    break;
                }
                case 41: {
                    i1 = 20129;
                    i2 = 213;
                    break;
                }
                case 42: {
                    i1 = 20143;
                    i2 = 213;
                    break;
                }
                case 43: {
                    i1 = 20157;
                    i2 = 213;
                    break;
                }
                case 44: {
                    i1 = 20171;
                    i2 = 213;
                    break;
                }
                case 45: {
                    i1 = 20116;
                    i2 = 270;
                    break;
                }
                case 46: {
                    i1 = 20130;
                    i2 = 270;
                    break;
                }
                case 47: {
                    i1 = 20144;
                    i2 = 270;
                    break;
                }
                case 48: {
                    i1 = 20158;
                    i2 = 270;
                    break;
                }
                case 49: {
                    i1 = 20172;
                    i2 = 270;
                    break;
                }
                case 50: {
                    i1 = 20117;
                    i2 = 289;
                    break;
                }
                case 51: {
                    i1 = 20131;
                    i2 = 289;
                    break;
                }
                case 52: {
                    i1 = 20145;
                    i2 = 289;
                    break;
                }
                case 53: {
                    i1 = 20159;
                    i2 = 289;
                    break;
                }
                case 54: {
                    i1 = 20173;
                    i2 = 289;
                    break;
                }
                case 55: {
                    i1 = 20118;
                    i2 = 305;
                    break;
                }
                case 56: {
                    i1 = 20132;
                    i2 = 305;
                    break;
                }
                case 57: {
                    i1 = 20146;
                    i2 = 305;
                    break;
                }
                case 58: {
                    i1 = 20160;
                    i2 = 305;
                    break;
                }
                case 59: {
                    i1 = 20174;
                    i2 = 305;
                    break;
                }
                case 60: {
                    i1 = 20119;
                    i2 = 5706;
                    break;
                }
                case 61: {
                    i1 = 20133;
                    i2 = 5706;
                    break;
                }
                case 62: {
                    i1 = 20147;
                    i2 = 5706;
                    break;
                }
                case 63: {
                    i1 = 20161;
                    i2 = 5706;
                    break;
                }
                case 64: {
                    i1 = 20175;
                    i2 = 5706;
                    break;
                }
                case 65: {
                    i1 = 20120;
                    i2 = 9340;
                    break;
                }
                case 66: {
                    i1 = 20134;
                    i2 = 9340;
                    break;
                }
                case 67: {
                    i1 = 20148;
                    i2 = 9340;
                    break;
                }
                case 68: {
                    i1 = 20162;
                    i2 = 9340;
                    break;
                }
                case 69: {
                    i1 = 20176;
                    i2 = 9340;
                    break;
                }
                case 70: {
                    i1 = 20121;
                    i2 = 9344;
                    break;
                }
                case 71: {
                    i1 = 20135;
                    i2 = 9344;
                    break;
                }
                case 72: {
                    i1 = 20149;
                    i2 = 9344;
                    break;
                }
                case 73: {
                    i1 = 20163;
                    i2 = 9344;
                    break;
                }
                case 74: {
                    i1 = 20177;
                    i2 = 9344;
                    break;
                }
                case 75: {
                    i1 = 20122;
                    i2 = 9348;
                    break;
                }
                case 76: {
                    i1 = 20136;
                    i2 = 9348;
                    break;
                }
                case 77: {
                    i1 = 20150;
                    i2 = 9348;
                    break;
                }
                case 78: {
                    i1 = 20164;
                    i2 = 9348;
                    break;
                }
                case 79: {
                    i1 = 20178;
                    i2 = 9348;
                    break;
                }
            }
            if (reply >= 10 && reply <= 79) {
                if (ItemFunctions.getItemCount(player, 20757) > 0) {
                    ItemFunctions.removeItem(player, 20757, 1);
                    final ItemInstance enchantItem = ItemFunctions.createItem(i2);
                    if (enchantItem != null) {
                        enchantItem.setEnchantLevel(12);
                        player.getInventory().addItem(enchantItem);
                    }
                    actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa026.htm");
                } else if (ItemFunctions.getItemCount(player, 20755) > 0) {
                    ItemFunctions.removeItem(player, 20755, 1);
                    final ItemInstance enchantItem = ItemFunctions.createItem(i1);
                    if (enchantItem != null) {
                        enchantItem.setEnchantLevel(8 + Rnd.get(9));
                        player.getInventory().addItem(enchantItem);
                    }
                    actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa025.htm");
                } else {
                    actor.showChatWindow(player, "pts/br_xmas_event/br_xmas_2009_santa024.htm");
                }
            }
        }
    }

    @Override
    protected void onEvtSpawn() {
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        ThreadPoolManager.getInstance().schedule(new Runnable() {
            @Override
            public void run() {
                Functions.npcSay(actor, Rnd.get(fString));
                ThreadPoolManager.getInstance().schedule(this, 60000 + Rnd.get(30000));
            }
        }, 1000L);
        super.onEvtSpawn();
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }
}