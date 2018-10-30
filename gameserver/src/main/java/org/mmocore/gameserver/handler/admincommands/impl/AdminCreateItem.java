package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.InventoryUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.world.GameObjectsStorage;


public class AdminCreateItem implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().UseGMShop) {
            return false;
        }

        switch (command) {
            case admin_itemcreate:
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;
            case admin_ci:
            case admin_create_item:
                try {
                    if (wordList.length < 2) {
                        activeChar.sendAdminMessage("USAGE: create_item id [count]");
                        return false;
                    }

                    final int item_id = Integer.parseInt(wordList[1]);
                    final long item_count = wordList.length < 3 ? 1 : Long.parseLong(wordList[2]);
                    createItem(activeChar, item_id, item_count);
                } catch (NumberFormatException nfe) {
                    activeChar.sendAdminMessage("USAGE: create_item id [count]");
                }
                activeChar.sendPacket(new HtmlMessage(5).setFile("admin/itemcreation.htm"));
                break;
            case admin_spreaditem:
                try {
                    final int id = Integer.parseInt(wordList[1]);
                    final int num = wordList.length > 2 ? Integer.parseInt(wordList[2]) : 1;
                    final long count = wordList.length > 3 ? Long.parseLong(wordList[3]) : 1;
                    for (int i = 0; i < num; i++) {
                        final ItemInstance createditem = ItemFunctions.createItem(id);
                        createditem.setCount(count);
                        createditem.dropMe(activeChar, Location.findPointToStay(activeChar, 100));
                    }
                } catch (NumberFormatException nfe) {
                    activeChar.sendAdminMessage("Specify a valid number.");
                } catch (StringIndexOutOfBoundsException e) {
                    activeChar.sendAdminMessage("Can't create this item.");
                }
                break;
            case admin_create_item_element:
                try {
                    if (wordList.length < 4) {
                        activeChar.sendAdminMessage("USAGE: create_item_attribue [id] [element id] [value]");
                        return false;
                    }

                    final int item_id = Integer.parseInt(wordList[1]);
                    final int elementId = Integer.parseInt(wordList[2]);
                    final int value = Integer.parseInt(wordList[3]);
                    if (elementId > 5 || elementId < 0) {
                        activeChar.sendAdminMessage("Improper element Id");
                        return false;
                    }
                    if (value < 1 || value > 300) {
                        activeChar.sendAdminMessage("Improper element value");
                        return false;
                    }

                    final ItemInstance item = createItem(activeChar, item_id, 1);
                    final Element element = Element.getElementById(elementId);
                    item.setAttributeElement(element, item.getAttributeElementValue(element, false) + value);
                    item.setJdbcState(JdbcEntityState.UPDATED);
                    item.update();
                    activeChar.sendPacket(new InventoryUpdate().addModifiedItem(item));
                } catch (NumberFormatException nfe) {
                    activeChar.sendAdminMessage("USAGE: create_item id [count]");
                }
                activeChar.sendPacket(new HtmlMessage(5).setFile("data/html/admin/itemcreation.htm"));
                break;
            case admin_remote_create:
                try {
                    if (wordList.length < 4) {
                        activeChar.sendAdminMessage("Please specify all fields!");
                        return false;
                    }
                    final Player player = GameObjectsStorage.getPlayer(wordList[1]);
                    if (player == null) {
                        activeChar.sendAdminMessage("Player not found in the game!");
                        break;
                    }
                    final int item_id = Integer.parseInt(wordList[2]);
                    final long item_count = Integer.parseInt(wordList[3]);
                    ItemInstance item = createItem(player, item_id, item_count);
                    activeChar.sendAdminMessage(item_count + " " + item.getName() + " successfully added to " + player.getName() + ".");
                } catch (Exception e) {
                    activeChar.sendAdminMessage("Incorrect data!");
                }
                break;
            case admin_remote_delete:
                try {
                    if (wordList.length < 4) {
                        activeChar.sendAdminMessage("Please specify all fields!");
                        return false;
                    }
                    final Player player = GameObjectsStorage.getPlayer(wordList[1]);
                    if (player == null)
                        activeChar.sendAdminMessage("Player not found in the game!");

                    final int item_id = Integer.parseInt(wordList[2]);
                    final long item_count = Integer.parseInt(wordList[3]);
                    boolean isDeleted = deleteItem(player, item_id, item_count);
                    activeChar.sendAdminMessage((isDeleted ? "S" : "Uns") + "uccessfully deleted.");
                } catch (Exception e) {
                    activeChar.sendAdminMessage("Incorrect data!");
                }
                break;
        }

        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    private ItemInstance createItem(final Player activeChar, final int itemId, final long count) {
        ItemInstance createditem = ItemFunctions.createItem(itemId);
        createditem.setCount(count);
        Log.items(activeChar, Log.Create, createditem);
        activeChar.getInventory().addItem(createditem);
        if (!createditem.isStackable()) {
            for (long i = 0; i < count - 1; i++) {
                createditem = ItemFunctions.createItem(itemId);
                Log.items(activeChar, Log.Create, createditem);
                activeChar.getInventory().addItem(createditem);
            }
        }
        activeChar.sendPacket(SystemMessage.obtainItems(itemId, count, 0));
        return createditem;
    }

    private boolean deleteItem(Player activeChar, int itemId, long count) {
        PcInventory inventory = activeChar.getInventory();
        long realCount = Math.min(inventory.getCountOf(itemId), count);
        boolean isDeleted = inventory.destroyItemByItemId(itemId, count);
        activeChar.sendPacket(new InventoryUpdate());
        return isDeleted;
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_itemcreate,
        admin_create_item,
        admin_ci,
        admin_spreaditem,
        admin_create_item_element,
        admin_remote_create,
        admin_remote_delete
    }
}