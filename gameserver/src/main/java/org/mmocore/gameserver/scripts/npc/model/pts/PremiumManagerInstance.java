package org.mmocore.gameserver.scripts.npc.model.pts;

import org.mmocore.gameserver.model.instances.MerchantInstance;
import org.mmocore.gameserver.network.lineage.serverpackets.PackageToList;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.WarehouseFunctions;

import java.util.Calendar;

/**
 * @author Mangol
 */
public class PremiumManagerInstance extends MerchantInstance {
    private static final int ticket_5h = 13273;
    private static final int ticket_5h_ev = 13383;
    private static final int ticket_5h_br = 20914;
    private static final int ticket_5h_br_ev = 22240;
    private static final int ticket_pt = 14065;
    private static final int ticket_pt_ev = 14074;
    private static final int warrior_buf_con_5h = 13017;
    private static final int mage_buf_con_5h = 13018;
    private static final int warrior_con_5h = 13019;
    private static final int mage_con_5h = 13020;
    private static final int toy_knight_con = 14061;
    private static final int spirit_mage_con = 14062;
    private static final int turtle_con = 14064;
    private static final int owl_mage_con = 14063;
    private static final int m_knight_pet = 20915;
    private static final int m_mage_pet = 20916;
    private static final int m_warsmith_pet = 20917;
    private static final int f_knight_pet = 20918;
    private static final int f_mage_pet = 20919;
    private static final int f_warsmith_pet = 20920;
    private static final String NotHavePaper = "pts/premium_manager/e_premium_manager014.htm";
    private static final String NotYetTime = "pts/premium_manager/e_premium_manager015.htm";
    private static final int RcPaper = 15279;
    private static final int RcPresent = 15278;

    public PremiumManagerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.startsWith("menu_select?ask=2&")) {
            if (command.endsWith("reply=1")) {
                showChatWindow(player, "pts/premium_manager/e_premium_manager002.htm");
            }
        } else if (command.startsWith("menu_select?ask=3&")) {
            if (command.endsWith("reply=1")) {
                showChatWindow(player, "pts/premium_manager/e_premium_manager003.htm");
            } else if (command.endsWith("reply=2")) {
                if (ItemFunctions.getItemCount(player, ticket_5h) > 0 || ItemFunctions.getItemCount(player, ticket_5h_ev) > 0) {
                    if (ItemFunctions.getItemCount(player, ticket_5h_ev) > 0) {
                        ItemFunctions.removeItem(player, ticket_5h_ev, 1);
                    } else {
                        ItemFunctions.removeItem(player, ticket_5h, 1);
                    }
                    ItemFunctions.addItem(player, warrior_buf_con_5h, 1);
                    showChatWindow(player, "pts/premium_manager/e_premium_manager008.htm");
                } else {
                    showChatWindow(player, "pts/premium_manager/e_premium_manager007.htm");
                }
            }
        } else if (command.startsWith("menu_select?ask=4&")) {
            if (command.endsWith("reply=1")) {
                showChatWindow(player, "pts/premium_manager/e_premium_manager004.htm");
            } else if (command.endsWith("reply=2")) {
                if (ItemFunctions.getItemCount(player, ticket_5h) > 0 || ItemFunctions.getItemCount(player, ticket_5h_ev) > 0) {
                    if (ItemFunctions.getItemCount(player, ticket_5h_ev) > 0) {
                        ItemFunctions.removeItem(player, ticket_5h_ev, 1);
                    } else {
                        ItemFunctions.removeItem(player, ticket_5h, 1);
                    }
                    ItemFunctions.addItem(player, mage_buf_con_5h, 1);
                    showChatWindow(player, "pts/premium_manager/e_premium_manager008.htm");
                } else {
                    showChatWindow(player, "pts/premium_manager/e_premium_manager007.htm");
                }
            }
        } else if (command.startsWith("menu_select?ask=5&")) {
            if (command.endsWith("reply=1")) {
                showChatWindow(player, "pts/premium_manager/e_premium_manager005.htm");
            } else if (command.endsWith("reply=2")) {
                if (ItemFunctions.getItemCount(player, ticket_5h) > 0 || ItemFunctions.getItemCount(player, ticket_5h_ev) > 0) {
                    if (ItemFunctions.getItemCount(player, ticket_5h_ev) > 0) {
                        ItemFunctions.removeItem(player, ticket_5h_ev, 1);
                    } else {
                        ItemFunctions.removeItem(player, ticket_5h, 1);
                    }
                    ItemFunctions.addItem(player, warrior_con_5h, 1);
                    showChatWindow(player, "pts/premium_manager/e_premium_manager008.htm");
                } else {
                    showChatWindow(player, "pts/premium_manager/e_premium_manager007.htm");
                }
            }
        } else if (command.startsWith("menu_select?ask=6&")) {
            if (command.endsWith("reply=1")) {
                showChatWindow(player, "pts/premium_manager/e_premium_manager006.htm");
            } else if (command.endsWith("reply=2")) {
                if (ItemFunctions.getItemCount(player, ticket_5h) > 0 || ItemFunctions.getItemCount(player, ticket_5h_ev) > 0) {
                    if (ItemFunctions.getItemCount(player, ticket_5h_ev) > 0) {
                        ItemFunctions.removeItem(player, ticket_5h_ev, 1);
                    } else {
                        ItemFunctions.removeItem(player, ticket_5h, 1);
                    }
                    ItemFunctions.addItem(player, mage_con_5h, 1);
                    showChatWindow(player, "pts/premium_manager/e_premium_manager008.htm");
                } else {
                    showChatWindow(player, "pts/premium_manager/e_premium_manager007.htm");
                }
            }
        } else if (command.startsWith("menu_select?ask=-2271&")) {
            if (command.endsWith("reply=1")) {
                if (!player.isWeightLimit(true)) {
                    return;
                }
                if (!player.isInventoryLimit(true)) {
                    return;
                }
                if (ItemFunctions.getItemCount(player, RcPaper) > 0) {
                    final long restartTime = player.getPlayerVariables().getLong(PlayerVariables.ITEM_922);
                    if (restartTime <= System.currentTimeMillis()) {
                        ItemFunctions.removeItem(player, RcPaper, 1);
                        ItemFunctions.addItem(player, RcPresent, 1);
                        final Calendar reDo = Calendar.getInstance();
                        if (reDo.get(Calendar.HOUR_OF_DAY) >= 6) {
                            reDo.add(Calendar.DATE, 1);
                        }
                        reDo.set(Calendar.HOUR_OF_DAY, 6);
                        reDo.set(Calendar.MINUTE, 30);
                        player.getPlayerVariables().set(PlayerVariables.ITEM_922, reDo.getTimeInMillis(), -1);
                    } else {
                        showChatWindow(player, NotYetTime);
                    }
                } else {
                    showChatWindow(player, NotHavePaper);
                }
            }
        } else if (command.startsWith("menu_select?ask=21000&")) {
            if (command.endsWith("reply=11")) {
                if (ItemFunctions.getItemCount(player, ticket_pt) > 0 || ItemFunctions.getItemCount(player, ticket_pt_ev) > 0) {
                    if (ItemFunctions.getItemCount(player, ticket_pt_ev) > 0) {
                        ItemFunctions.removeItem(player, ticket_pt_ev, 1);
                    } else {
                        ItemFunctions.removeItem(player, ticket_pt, 1);
                    }
                    ItemFunctions.addItem(player, toy_knight_con, 1);
                    showChatWindow(player, "pts/premium_manager/e_premium_manager008.htm");
                } else {
                    showChatWindow(player, "pts/premium_manager/e_premium_manager007.htm");
                }
            } else if (command.endsWith("reply=21")) {
                if (ItemFunctions.getItemCount(player, ticket_pt) > 0 || ItemFunctions.getItemCount(player, ticket_pt_ev) > 0) {
                    if (ItemFunctions.getItemCount(player, ticket_pt_ev) > 0) {
                        ItemFunctions.removeItem(player, ticket_pt_ev, 1);
                    } else {
                        ItemFunctions.removeItem(player, ticket_pt, 1);
                    }
                    ItemFunctions.addItem(player, spirit_mage_con, 1);
                    showChatWindow(player, "pts/premium_manager/e_premium_manager008.htm");
                } else {
                    showChatWindow(player, "pts/premium_manager/e_premium_manager007.htm");
                }
            } else if (command.endsWith("reply=31")) {
                if (ItemFunctions.getItemCount(player, ticket_pt) > 0 || ItemFunctions.getItemCount(player, ticket_pt_ev) > 0) {
                    if (ItemFunctions.getItemCount(player, ticket_pt_ev) > 0) {
                        ItemFunctions.removeItem(player, ticket_pt_ev, 1);
                    } else {
                        ItemFunctions.removeItem(player, ticket_pt, 1);
                    }
                    ItemFunctions.addItem(player, owl_mage_con, 1);
                    showChatWindow(player, "pts/premium_manager/e_premium_manager008.htm");
                } else {
                    showChatWindow(player, "pts/premium_manager/e_premium_manager007.htm");
                }
            } else if (command.endsWith("reply=41")) {
                if (ItemFunctions.getItemCount(player, ticket_pt) > 0 || ItemFunctions.getItemCount(player, ticket_pt_ev) > 0) {
                    if (ItemFunctions.getItemCount(player, ticket_pt_ev) > 0) {
                        ItemFunctions.removeItem(player, ticket_pt_ev, 1);
                    } else {
                        ItemFunctions.removeItem(player, ticket_pt, 1);
                    }
                    ItemFunctions.addItem(player, turtle_con, 1);
                    showChatWindow(player, "pts/premium_manager/e_premium_manager008.htm");
                } else {
                    showChatWindow(player, "pts/premium_manager/e_premium_manager007.htm");
                }
            }
            //Пригодиться в будущем :D (с)Mangol
			/*
			else if(command.endsWith("reply=51"))
			{
				if(ItemFunctions.getItemCount(player, ticket_5h_br) > 0 || ItemFunctions.getItemCount(player, ticket_5h_br_ev) > 0)
				{
					if(ItemFunctions.getItemCount(player, ticket_5h_br_ev) > 0)
					{
						ItemFunctions.removeItem(player, ticket_5h_br_ev, 1);
					}
					else
					{
						ItemFunctions.removeItem(player, ticket_5h_br, 1);
					}
					ItemFunctions.addItem(player, m_knight_pet, 1);
					showChatWindow(player, "pts/premium_manager/e_premium_manager008.htm");
				}
				else
				{
					showChatWindow(player, "pts/premium_manager/e_premium_manager025.htm");
				}
			}
			else if(command.endsWith("reply=61"))
			{
				if(ItemFunctions.getItemCount(player, ticket_5h_br) > 0 || ItemFunctions.getItemCount(player, ticket_5h_br_ev) > 0)
				{
					if(ItemFunctions.getItemCount(player, ticket_5h_br_ev) > 0)
					{
						ItemFunctions.removeItem(player, ticket_5h_br_ev, 1);
					}
					else
					{
						ItemFunctions.removeItem(player, ticket_5h_br, 1);
					}
					ItemFunctions.addItem(player, m_mage_pet, 1);
					showChatWindow(player, "pts/premium_manager/e_premium_manager008.htm");
				}
				else
				{
					showChatWindow(player, "pts/premium_manager/e_premium_manager025.htm");
				}
			}
			else if(command.endsWith("reply=71"))
			{
				if(ItemFunctions.getItemCount(player, ticket_5h_br) > 0 || ItemFunctions.getItemCount(player, ticket_5h_br_ev) > 0)
				{
					if(ItemFunctions.getItemCount(player, ticket_5h_br_ev) > 0)
					{
						ItemFunctions.removeItem(player, ticket_5h_br_ev, 1);
					}
					else
					{
						ItemFunctions.removeItem(player, ticket_5h_br, 1);
					}
					ItemFunctions.addItem(player, m_warsmith_pet, 1);
					showChatWindow(player, "pts/premium_manager/e_premium_manager008.htm");
				}
				else
				{
					showChatWindow(player, "pts/premium_manager/e_premium_manager025.htm");
				}
			}
			else if(command.endsWith("reply=81"))
			{
				if(ItemFunctions.getItemCount(player, ticket_5h_br) > 0 || ItemFunctions.getItemCount(player, ticket_5h_br_ev) > 0)
				{
					if(ItemFunctions.getItemCount(player, ticket_5h_br_ev) > 0)
					{
						ItemFunctions.removeItem(player, ticket_5h_br_ev, 1);
					}
					else
					{
						ItemFunctions.removeItem(player, ticket_5h_br, 1);
					}
					ItemFunctions.addItem(player, f_knight_pet, 1);
					showChatWindow(player, "pts/premium_manager/e_premium_manager008.htm");
				}
				else
				{
					showChatWindow(player, "pts/premium_manager/e_premium_manager025.htm");
				}
			}
			else if(command.endsWith("reply=91"))
			{
				if(ItemFunctions.getItemCount(player, ticket_5h_br) > 0 || ItemFunctions.getItemCount(player, ticket_5h_br_ev) > 0)
				{
					if(ItemFunctions.getItemCount(player, ticket_5h_br_ev) > 0)
					{
						ItemFunctions.removeItem(player, ticket_5h_br_ev, 1);
					}
					else
					{
						ItemFunctions.removeItem(player, ticket_5h_br, 1);
					}
					ItemFunctions.addItem(player, f_mage_pet, 1);
					showChatWindow(player, "pts/premium_manager/e_premium_manager008.htm");
				}
				else
				{
					showChatWindow(player, "pts/premium_manager/e_premium_manager025.htm");
				}
			}
			else if(command.endsWith("reply=101"))
			{
				if(ItemFunctions.getItemCount(player, ticket_5h_br) > 0 || ItemFunctions.getItemCount(player, ticket_5h_br_ev) > 0)
				{
					if(ItemFunctions.getItemCount(player, ticket_5h_br_ev) > 0)
					{
						ItemFunctions.removeItem(player, ticket_5h_br_ev, 1);
					}
					else
					{
						ItemFunctions.removeItem(player, ticket_5h_br, 1);
					}
					ItemFunctions.addItem(player, f_warsmith_pet, 1);
					showChatWindow(player, "pts/premium_manager/e_premium_manager008.htm");
				}
				else
				{
					showChatWindow(player, "pts/premium_manager/e_premium_manager025.htm");
				}
			}
			*/
        } else if (command.startsWith("package_deposit")) {
            player.sendPacket(new PackageToList(player));
        } else if (command.startsWith("package_withdraw")) {
            WarehouseFunctions.showFreightWindow(player);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}
