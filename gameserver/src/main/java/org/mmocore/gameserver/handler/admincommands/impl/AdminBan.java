package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.ChangeAccessLevel;
import org.mmocore.gameserver.network.lineage.GameClient;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ManufactureItem;
import org.mmocore.gameserver.object.components.items.TradeItem;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.*;
import org.mmocore.gameserver.world.World;

import java.util.Collection;
import java.util.StringTokenizer;

public class AdminBan implements IAdminCommandHandler {
    private static String tradeToString(final Player targ, final int trade) {
        final StringBuilder ret = new StringBuilder();

        switch (trade) {
            case Player.STORE_PRIVATE_BUY: {
                final Collection<TradeItem> list = targ.getBuyList();
                if (list == null || list.isEmpty()) {
                    return "";
                }
                ret.append(":buy:");
                for (final TradeItem i : list) {
                    ret.append(i.getItemId()).append(';').append(i.getCount()).append(';').append(i.getOwnersPrice()).append(':');
                }
                return ret.toString();
            }
            case Player.STORE_PRIVATE_SELL:
            case Player.STORE_PRIVATE_SELL_PACKAGE: {
                final Collection<TradeItem> list = targ.getSellList();
                if (list == null || list.isEmpty()) {
                    return "";
                }
                ret.append(":sell:");
                for (final TradeItem i : list) {
                    ret.append(i.getItemId()).append(';').append(i.getCount()).append(';').append(i.getOwnersPrice()).append(':');
                }
                return ret.toString();
            }
            case Player.STORE_PRIVATE_MANUFACTURE: {
                final Collection<ManufactureItem> list = targ.getCreateList();
                if (list == null || list.isEmpty()) {
                    return "";
                }
                ret.append(":mf:");
                for (final ManufactureItem i : list) {
                    ret.append(i.getRecipeId()).append(';').append(i.getCost()).append(':');
                }
                return ret.toString();
            }
            default:
                return "";
        }
    }

    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        final StringTokenizer st = new StringTokenizer(fullString);

        if (activeChar.getPlayerAccess().CanTradeBanUnban) {
            switch (command) {
                case admin_trade_ban:
                    return tradeBan(st, activeChar);
                case admin_trade_unban:
                    return tradeUnban(st, activeChar);
            }
        }

        if (command == Commands.admin_chatban && activeChar.getPlayerAccess().CanBanChat) {
            try {
                st.nextToken();
                final String player = st.nextToken();
                final String period = st.nextToken();
                final String bmsg = "admin_chatban " + player + ' ' + period + ' ';
                final String msg = fullString.substring(bmsg.length(), fullString.length());

                if (AutoBan.ChatBan(player, Integer.parseInt(period), activeChar.getName())) {
                    activeChar.sendAdminMessage("You ban chat for " + player + '.');
                } else {
                    activeChar.sendAdminMessage("Can't find char " + player + '.');
                }
            } catch (Exception e) {
                activeChar.sendAdminMessage("Command syntax: //chatban char_name period reason");
            }
        }
        if (command == Commands.admin_chatunban && activeChar.getPlayerAccess().CanUnBanChat) {
            try {
                st.nextToken();
                final String player = st.nextToken();

                if (AutoBan.ChatBan(player, 0, activeChar.getName())) {
                    activeChar.sendAdminMessage("You unban chat for " + player + '.');
                } else {
                    activeChar.sendAdminMessage("Can't find char " + player + '.');
                }
            } catch (Exception e) {
                activeChar.sendAdminMessage("Command syntax: //chatunban char_name");
            }
        }

        if (command == Commands.admin_jail) {
            if (activeChar.getPlayerAccess().CanBan) {
                try {
                    st.nextToken();
                    final String player = st.nextToken();
                    final String period = st.nextToken(); // 43200 for moders
                    final String reason = st.nextToken();

                    final Player target = World.getPlayer(player);

                    if (target != null) {
                        target.getPlayerVariables().set(PlayerVariables.JAILED_FROM, target.getX() + ";" + target.getY() + ';' + target.getZ() + ';' + target.getReflectionId(), -1);
                        target.getPlayerVariables().set(PlayerVariables.JAILED, period, -1);
                        target.startUnjailTask(target, Integer.parseInt(period));
                        target.teleToLocation(Location.findPointToStay(target, AdminFunctions.JAIL_SPAWN, 50, 200), ReflectionManager.JAIL);
                        if (activeChar.isInStoreMode()) {
                            activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
                        }
                        target.sitDown(null);
                        target.block();
                        target.sendMessage("You moved to jail, time to escape - " + period + " minutes, reason - " + reason + " .");
                        activeChar.sendAdminMessage("You jailed " + player + '.');
                    } else {
                        activeChar.sendAdminMessage("Can't find char " + player + '.');
                    }
                } catch (Exception e) {
                    activeChar.sendAdminMessage("Command syntax: //jail char_name period reason");
                }
            } else if (activeChar.getPlayerAccess().isGameModerator) {
                try {
                    st.nextToken();
                    final String player = st.nextToken();

                    final Player target = World.getPlayer(player);

                    if (target != null) {
                        target.getPlayerVariables().set(PlayerVariables.JAILED_FROM, target.getX() + ";" + target.getY() + ';' + target.getZ() + ';' + target.getReflectionId(), -1);
                        target.getPlayerVariables().set(PlayerVariables.JAILED, 43200, -1);
                        target.startUnjailTask(target, 43200);
                        if (activeChar.isInStoreMode()) {
                            activeChar.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
                        }
                        target.teleToLocation(Location.findPointToStay(target, AdminFunctions.JAIL_SPAWN, 50, 200), ReflectionManager.JAIL);
                        target.updateNoChannel(2678400000L);
                        target.sitDown(null);
                        target.block();
                        if (target.getLanguage() == Language.ENGLISH) {
                            target.sendMessage("You are banned by Game Moderator. Welcome to support");
                        } else {
                            target.sendMessage("Вы заблокированы Модератором чата. Обратитесь в support");
                        }
                        activeChar.sendAdminMessage("You jailed " + player + '.');
                    } else {
                        // check offline and ban
                        activeChar.sendAdminMessage("Can't find char " + player + '.');
                    }
                } catch (Exception e) {
                    activeChar.sendAdminMessage("Command syntax: //jail char_name");
                }
            }
        }

        if (activeChar.getPlayerAccess().CanBan) {
            switch (command) {
                case admin_ban:
                    ban(st, activeChar);
                    break;
                case admin_ba:
                    ban(st, activeChar);
                    break;
                case admin_accban: {
                    st.nextToken();

                    int level = 0;
                    int banExpire = 0;

                    final String account = st.nextToken();

                    if (st.hasMoreTokens()) {
                        banExpire = (int) (System.currentTimeMillis() / 1000L) + Integer.parseInt(st.nextToken()) * 60;
                    } else {
                        level = -100;
                    }

                    AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel(account, level, banExpire));
                    final GameClient client = AuthServerCommunication.getInstance().getAuthedClient(account);
                    if (client != null) {
                        final Player player = client.getActiveChar();
                        if (player != null) {
                            player.kick();
                            activeChar.sendAdminMessage("Player " + player.getName() + " kicked.");
                        }
                    }
                    break;
                }
                case admin_accunban: {
                    st.nextToken();
                    final String account = st.nextToken();
                    AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel(account, 0, 0));
                    break;
                }
                case admin_trade_ban:
                    return tradeBan(st, activeChar);
                case admin_trade_unban:
                    return tradeUnban(st, activeChar);
                case admin_unjail:
                    try {
                        st.nextToken();
                        final String player = st.nextToken();

                        final Player target = World.getPlayer(player);

                        if (target != null && target.getPlayerVariables().get(PlayerVariables.JAILED) != null) {
                            final String[] re = target.getPlayerVariables().get(PlayerVariables.JAILED_FROM).split(";");
                            target.teleToLocation(Integer.parseInt(re[0]), Integer.parseInt(re[1]), Integer.parseInt(re[2]));
                            target.setReflection(re.length > 3 ? Integer.parseInt(re[3]) : 0);
                            target.stopUnjailTask();
                            target.getPlayerVariables().remove(PlayerVariables.JAILED_FROM);
                            target.getPlayerVariables().remove(PlayerVariables.JAILED);
                            target.unblock();
                            target.standUp();
                            activeChar.sendAdminMessage("You unjailed " + player + '.');
                        } else {
                            activeChar.sendAdminMessage("Can't find char " + player + '.');
                        }
                    } catch (Exception e) {
                        activeChar.sendAdminMessage("Command syntax: //unjail char_name");
                    }
                    break;
                case admin_cban:
                    activeChar.sendPacket(new HtmlMessage(5).setFile("admin/cban.htm"));
                    break;
                case admin_permaban:
                    if (activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()) {
                        activeChar.sendAdminMessage("Target should be set and be a player instance");
                        return false;
                    }
                    final Player banned = activeChar.getTarget().getPlayer();
                    final String banaccount = banned.getAccountName();
                    AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel(banaccount, -100, 0));
                    if (banned.isInOfflineMode()) {
                        banned.setOfflineMode(false);
                    }
                    banned.kick();
                    activeChar.sendAdminMessage("Player account " + banaccount + " is banned, player " + banned.getName() + " kicked.");
                    break;
            }
        }

        return true;
    }

    private boolean tradeBan(final StringTokenizer st, final Player activeChar) {
        if (activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()) {
            return false;
        }
        st.nextToken();
        final Player targ = (Player) activeChar.getTarget();
        long days = -1;
        long time = -1;
        if (st.hasMoreTokens()) {
            days = Long.parseLong(st.nextToken());
            time = days * 24 * 60 * 60 * 1000L + System.currentTimeMillis();
        }
        targ.getPlayerVariables().set(PlayerVariables.TRADE_BAN, String.valueOf(time), -1);
        final String msg = activeChar.getName() + " заблокировал торговлю персонажу " + targ.getName() + (days == -1 ? " на бессрочный период." : " на " + days + " дней.");

        Log.add(targ.getName() + ':' + days + tradeToString(targ, targ.getPrivateStoreType()), "tradeBan", activeChar);

        if (targ.isInOfflineMode()) {
            targ.setOfflineMode(false);
            targ.kick();
        } else if (targ.isInStoreMode()) {
            targ.setPrivateStoreType(Player.STORE_PRIVATE_NONE);
            targ.standUp();
            targ.broadcastCharInfo();
            targ.getBuyList().clear();
        }

        if (ServerConfig.BANCHAT_ANNOUNCE_FOR_ALL_WORLD) {
            AnnouncementUtils.announceToAll(msg);
        } else {
            AnnouncementUtils.shout(activeChar, msg, ChatType.CRITICAL_ANNOUNCE);
        }
        return true;
    }

    private boolean tradeUnban(final StringTokenizer st, final Player activeChar) {
        if (activeChar.getTarget() == null || !activeChar.getTarget().isPlayer()) {
            return false;
        }
        final Player targ = (Player) activeChar.getTarget();

        targ.getPlayerVariables().remove(PlayerVariables.TRADE_BAN);

        if (ServerConfig.BANCHAT_ANNOUNCE_FOR_ALL_WORLD) {
            AnnouncementUtils.announceToAll(activeChar + " разблокировал торговлю персонажу " + targ + '.');
        } else {
            AnnouncementUtils.shout(activeChar, activeChar + " разблокировал торговлю персонажу " + targ + '.', ChatType.CRITICAL_ANNOUNCE);
        }

        Log.add(activeChar + " разблокировал торговлю персонажу " + targ + '.', "tradeBan", activeChar);
        return true;
    }

    private boolean ban(final StringTokenizer st, final Player activeChar) {
        System.out.println(st.nextToken() + '\t' + activeChar.getName());
        try {
            st.nextToken();

            final String player = st.nextToken();

            int time = 0;
            String msg = "";

            if (st.hasMoreTokens()) {
                time = Integer.parseInt(st.nextToken());
            }

            if (st.hasMoreTokens()) {
                msg = "admin_ban " + player + ' ' + time + ' ';
                while (st.hasMoreTokens()) {
                    msg += st.nextToken() + ' ';
                }
                msg = msg.trim();
            }

            final Player plyr = World.getPlayer(player);
            if (plyr != null) {
                plyr.sendMessage(new CustomMessage("admincommandhandlers.YoureBannedByGM"));
                plyr.setAccessLevel(-100);
                AutoBan.banPlayer(plyr, time, msg, activeChar.getName());
                plyr.kick();
                activeChar.sendAdminMessage("You banned " + plyr.getName());
            } else if (AutoBan.banOfflinePlayer(player, -100, time, msg, activeChar.getName())) {
                activeChar.sendAdminMessage("You banned " + player);
            } else {
                activeChar.sendAdminMessage("Can't find char: " + player);
            }
        } catch (Exception e) {
            activeChar.sendAdminMessage("Command syntax: //ban char_name days reason");
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
        admin_ban,
        admin_ba,
        admin_unban,
        admin_cban,
        admin_chatban,
        admin_chatunban,
        admin_accban,
        admin_accunban,
        admin_trade_ban,
        admin_trade_unban,
        admin_jail,
        admin_unjail,
        admin_permaban
    }
}
