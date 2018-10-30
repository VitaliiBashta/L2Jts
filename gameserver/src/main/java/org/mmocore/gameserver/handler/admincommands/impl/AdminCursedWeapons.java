package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.database.dao.impl.CursedWeaponsDAO;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.CursedWeaponsManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.items.etcitems.CursedWeapon;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.world.GameObjectsStorage;

public class AdminCursedWeapons implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().Menu) {
            return false;
        }

        final CursedWeaponsManager cwm = CursedWeaponsManager.getInstance();

        CursedWeapon cw = null;
        switch (command) {
            case admin_cw_remove:
            case admin_cw_goto:
            case admin_cw_add:
            case admin_cw_drop:
                if (wordList.length < 2) {
                    activeChar.sendMessage("Need correct itemID. Usage: //command ITEM_ID");
                    return false;
                }
                for (final CursedWeapon cwp : CursedWeaponsManager.getInstance().getCursedWeapons()) {
                    if (cwp.getItemId() == Integer.parseInt(wordList[1])) {
                        cw = cwp;
                    }
                }
                if (cw == null) {
                    activeChar.sendMessage("Not correct itemId:" + Integer.parseInt(wordList[1]));
                    return false;
                }
                break;
        }

        switch (command) {
            case admin_cw_info:
                activeChar.sendMessage("======= Cursed Weapons: =======");
                for (final CursedWeapon c : cwm.getCursedWeapons()) {
                    activeChar.sendMessage("> " + c.getName() + " (" + c.getItemId() + ')');
                    if (c.isActivated()) {
                        final Player pl = c.getPlayer();
                        activeChar.sendMessage("  Player holding: " + pl.getName());
                        activeChar.sendMessage("  Player karma: " + c.getPlayerKarma());
                        activeChar.sendMessage("  Time Remaining: " + c.getTimeLeft() / 60000 + " min.");
                        activeChar.sendMessage("  Kills : " + c.getNbKills());
                    } else if (c.isDropped()) {
                        activeChar.sendMessage("  Lying on the ground.");
                        activeChar.sendMessage("  Time Remaining: " + c.getTimeLeft() / 60000 + " min.");
                        activeChar.sendMessage("  Kills : " + c.getNbKills());
                    } else {
                        activeChar.sendMessage("  Don't exist in the world.");
                    }
                }
                break;
            case admin_cw_reload:
                activeChar.sendMessage("Cursed weapons can't be reloaded.");
                break;
            case admin_cw_remove:
                if (cw == null) {
                    return false;
                }
                CursedWeaponsDAO.getInstance().endOfLife(cw);
                break;
            case admin_cw_goto:
                if (cw == null) {
                    return false;
                }
                activeChar.teleToLocation(cw.getLoc());
                break;
            case admin_cw_add:
                if (cw == null) {
                    return false;
                }
                if (cw.isActive()) {
                    activeChar.sendMessage("This cursed weapon is already active.");
                } else {
                    final GameObject target = activeChar.getTarget();
                    if (target != null && target.isPlayer() && !((Player) target).isInOlympiadMode()) {
                        final Player player = (Player) target;
                        final ItemInstance item = ItemFunctions.createItem(cw.getItemId());
                        cwm.activate(player, player.getInventory().addItem(item));
                        cwm.showUsageTime(player, cw);
                    }
                }
                break;
            case admin_cw_drop:
                if (cw == null) {
                    return false;
                }
                if (cw.isActive()) {
                    activeChar.sendMessage("This cursed weapon is already active.");
                }
                if (wordList[2] == null && wordList[2].isEmpty()) {
                    activeChar.sendMessage("Usage: //cw_drop ITEM_ID NPC_ID(for drop)");
                } else {
                    final GameObject target = activeChar.getTarget();
                    final NpcInstance npc = GameObjectsStorage.getByNpcId(Integer.parseInt(wordList[2]));
                    if (npc != null && !npc.isDead()) {
                        if (target != null && target.isPlayer() && !((Player) target).isInOlympiadMode()) {
                            final Player player = (Player) target;
                            cw.create(npc, player);
                        }
                    } else {
                        activeChar.sendMessage("Not correct NPC_ID or npc die.");
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_cw_info,
        admin_cw_remove,
        admin_cw_goto,
        admin_cw_reload,
        admin_cw_add,
        admin_cw_drop
    }
}