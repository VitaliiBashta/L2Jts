package org.jts.protection.handler.commands;

import org.jts.protection.Protection;
import org.jts.protection.database.ProtectionDAO;
import org.jts.protection.manager.HWIDBanManager;
import org.jts.protection.manager.HWIDBanManager.BanType;
import org.jts.protection.manager.HWIDListManager;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.AutoBan;
import org.mmocore.gameserver.world.World;

import java.util.StringTokenizer;

public class AdminHWID implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        if (!Protection.isProtectEnabled()) {
            return false;
        }

        final StringTokenizer st = new StringTokenizer(fullString);
        final Commands command = (Commands) comm;

        if (activeChar.getPlayerAccess().CanBan) {
            switch (command) {
                case admin_hwid_info:
                    if (activeChar.getTarget() != null) {
                        final GameObject playerTarger = activeChar.getTarget();
                        if (!(playerTarger instanceof Player)) {
                            activeChar.sendAdminMessage("Target is not player!");
                            return false;
                        }
                        final Player target = (Player) playerTarger;
                        if (target != null)
                            activeChar.sendAdminMessage("Player name: " + target.getName() + " HWID: " + target.getNetConnection().getHWID());
                    } else {
                        st.nextToken();
                        final String player = st.nextToken();
                        final Player targetPlayer = World.getPlayer(player);
                        if (targetPlayer != null)
                            activeChar.sendAdminMessage("Player name: " + targetPlayer.getName() + " HWID: " + targetPlayer.getNetConnection().getHWID());
                        else {
                            activeChar.sendAdminMessage(player + " char name not found.");
                            return false;
                        }
                    }
                    break;
                case admin_hwid_ban_char:
                    banThisPlayer(activeChar, st, false);
                    break;
                case admin_hwid_ban_acc:
                    banThisPlayer(activeChar, st, true);
                    break;
                case admin_hwid_reload:
                    HWIDBanManager.getInstance().load();
                    HWIDListManager.getInstance().load();
                    break;
            }
        }
        return true;
    }

    @SuppressWarnings("unused")
    private boolean banThisPlayer(final Player activeChar, final StringTokenizer st, final boolean account) {
        if (activeChar.getTarget() != null) {
            final GameObject playerTarger = activeChar.getTarget();
            if (!playerTarger.isPlayer()) {
                activeChar.sendAdminMessage("Target is not player!");
                return false;
            }
            final Player target = (Player) playerTarger;
            if (target != null) {
                ProtectionDAO.getInstance().storeHWIDBanned(target.getNetConnection(), "PERMANENT_HWID_BAN_FROM_GM_" + activeChar.getName(), account == true ? BanType.ACCOUNT_BAN : BanType.PLAYER_BAN);
                HWIDBanManager.getInstance().systemBan(target.getNetConnection(), "Admin ban from: " + activeChar.getName(), BanType.PLAYER_BAN);
                target.sendMessage(new CustomMessage("admincommandhandlers.YoureBannedByGM"));
                AutoBan.banPlayer(target, 365, "PERMANENT_HWID_BAN", activeChar.getName());
                final String playerAcc = account ? " account" : " character";
                final String name = account ? target.getAccountName() : target.getName();
                activeChar.sendAdminMessage(name + playerAcc + " banned on HWID");
                if (account)
                    target.setAccessLevel(-100);
                target.kick();
                return true;
            }
        } else {
            st.nextToken();
            final String player = st.nextToken();
            final Player targetPlayer = World.getPlayer(player);
            if (targetPlayer != null) {
                ProtectionDAO.getInstance().storeHWIDBanned(targetPlayer.getNetConnection(), "PERMANENT_HWID_BAN_FROM_GM_" + activeChar.getName(), account == true ? BanType.ACCOUNT_BAN : BanType.PLAYER_BAN);
                targetPlayer.sendMessage(new CustomMessage("admincommandhandlers.YoureBannedByGM"));
                AutoBan.banPlayer(targetPlayer, 365, "PERMANENT_HWID_BAN", activeChar.getName());
                final String playerAcc = account ? " account" : " character";
                final String name = account ? targetPlayer.getAccountName() : targetPlayer.getName();
                activeChar.sendAdminMessage(name + playerAcc + " banned on HWID");
                if (account)
                    targetPlayer.setAccessLevel(-100);
                targetPlayer.kick();
                return true;
            } else {
                activeChar.sendAdminMessage(player + " char name not found.");
                return false;
            }
        }
        return false;
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
        admin_hwid_info,
        admin_hwid_ban_char,
        admin_hwid_ban_acc,
        admin_hwid_reload
    }

}