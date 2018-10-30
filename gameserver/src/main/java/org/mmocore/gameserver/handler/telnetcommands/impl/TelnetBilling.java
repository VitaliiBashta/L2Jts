package org.mmocore.gameserver.handler.telnetcommands.impl;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.database.dao.impl.ItemsDAO;
import org.mmocore.gameserver.handler.telnetcommands.ITelnetCommandHandler;
import org.mmocore.gameserver.handler.telnetcommands.TelnetCommand;
import org.mmocore.gameserver.manager.MapRegionManager;
import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.templates.mapregion.RestartArea;
import org.mmocore.gameserver.templates.mapregion.RestartPoint;
import org.mmocore.gameserver.utils.AdminFunctions;
import org.mmocore.gameserver.utils.AutoBan;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.idfactory.IdFactory;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Laky
 * Date: 23.11.11
 * Time: 20:42
 * The Abyss (c) 2011
 */
public class TelnetBilling implements ITelnetCommandHandler {
    private static final Logger _log = LoggerFactory.getLogger(TelnetBilling.class);
    private final Set<TelnetCommand> _commands = new LinkedHashSet<>();

    public TelnetBilling() {
        _commands.add(new TelnetCommand("billing.items.add") {
            @Override
            public String getUsage() {
                return "billing.items.add <char_obj_id> <item_id> <amount> <enchant>";
            }

            @Override
            public String handle(final String[] args) {
                if (args.length < 4) {
                    return "FAIL";
                }

                try {
                    final int obj_id = Integer.parseInt(args[0]);
                    final int item_id = Integer.parseInt(args[1]);
                    final long amount = Long.parseLong(args[2]);
                    final int enchant = Integer.parseInt(args[3]);

                    if (amount < 1 || enchant < 0 || enchant > 65535) {
                        _log.error("Couldn't add item with ID " + item_id + " for object " + obj_id + " with " + amount + " amount!");
                        return "FAIL";
                    }

                    int isNotDelayed = 0;

                    if (item_id >= 26000 && item_id <= 26004) {
                        if (amount != 1) {
                            _log.error("Couldn't add item with ID " + item_id + " for object " + obj_id + " with " + amount + " amount!");
                            return "FAIL";
                        }

                        isNotDelayed = 1;

                        final int newObjectId = IdFactory.getInstance().getNextId();
                        final ItemInstance newItem = new ItemInstance(newObjectId, item_id);

                        newItem.setOwnerId(obj_id);
                        newItem.setLocation(ItemInstance.ItemLocation.INVENTORY);
                        newItem.setCount(1L);
                        newItem.save();

                        final Player player = GameObjectsStorage.getPlayer(obj_id);
                        if (player != null) {
                            player.getInventory().addItem(newItem);
                            _log.info("billing.items.add: ONLINE owner_id " + obj_id + " object_id " + newObjectId + " item_id " + item_id);
                        } else {
                            _log.info("billing.items.add: OFFLINE owner_id " + obj_id + " object_id " + newObjectId + " item_id " + item_id);
                        }
                    }

                    Connection con = null;
                    PreparedStatement statement = null;
                    try {
                        con = DatabaseFactory.getInstance().getConnection();
                        statement = con.prepareStatement("INSERT INTO `items_delayed` VALUES (DEFAULT,?,?,?,?,-1,-1,0,?,'WEB')");
                        statement.setInt(1, obj_id);
                        statement.setInt(2, item_id);
                        statement.setLong(3, amount);
                        statement.setInt(4, enchant);
                        statement.setInt(5, isNotDelayed);
                        statement.execute();
                    } catch (SQLException e) {
                        _log.error("TelnetBilling: " + e.getMessage());
                        return "FAIL";
                    } finally {
                        DbUtils.closeQuietly(con, statement);
                    }

                    return "SUCCESS";
                } catch (Exception e) {
                    _log.error("TelnetBilling: " + e.getMessage());
                    return "FAIL";
                } finally {
                    _log.info("billing.items.add: " + args[0] + ' ' + args[1] + ' ' + args[2] + ' ' + args[3]);
                }
            }
        });

        _commands.add(new TelnetCommand("billing.players.show") {
            @Override
            public String getUsage() {
                return "billing.players.show";
            }

            @Override
            public String handle(final String[] args) {
                return String.valueOf(GameObjectsStorage.getPlayers().size());
            }
        });

        _commands.add(new TelnetCommand("billing.players.kick") {
            @Override
            public String getUsage() {
                return "billing.players.kick <char_obj_id>";
            }

            @Override
            public String handle(final String[] args) {
                if (AdminFunctions.kick(Integer.parseInt(args[0]), "telnet")) {
                    return "SUCCESS";
                } else {
                    return "FAIL";
                }
            }
        });

        _commands.add(new TelnetCommand("billing.players.send2home") {
            @Override
            public String getUsage() {
                return "billing.players.send2home <char_obj_id>";
            }

            @Override
            public String handle(final String[] args) {
                final int playerId = Integer.parseInt(args[0]);

                Connection con = null;
                PreparedStatement statement = null;
                ResultSet rs = null;
                try {
                    con = DatabaseFactory.getInstance().getConnection();
                    statement = con.prepareStatement("SELECT karma,x,y,z FROM characters WHERE obj_Id=? and online=0");
                    statement.setInt(1, playerId);
                    statement.execute();
                    rs = statement.getResultSet();

                    if (!rs.next()) {
                        return "FAIL";
                    }

                    final int karma = rs.getInt("karma");
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int z = rs.getInt("z");

                    DbUtils.close(statement, rs);

                    if (karma > 0) {
                        Location loc = new Location(x, y, z);
                        final RestartPoint rp = MapRegionManager.getInstance().getRegionData(RestartArea.class, loc).getRestartPoint().get(PlayerRace.human);
                        loc = Rnd.get(rp.getPKrestartPoints());
                        x = loc.getX();
                        y = loc.getY();
                        z = loc.getZ();
                        statement = con.prepareStatement("UPDATE characters SET x=?, y=?, z=? WHERE obj_Id=?");
                        statement.setInt(1, x);
                        statement.setInt(2, y);
                        statement.setInt(3, z);
                        statement.setInt(4, playerId);
                        statement.execute();
                        DbUtils.close(statement);
                    } else {
                        Location loc = new Location(x, y, z);
                        final RestartPoint rp = MapRegionManager.getInstance().getRegionData(RestartArea.class, loc).getRestartPoint().get(PlayerRace.human);
                        loc = Rnd.get(rp.getRestartPoints());
                        x = loc.getX();
                        y = loc.getY();
                        z = loc.getZ();
                        statement = con.prepareStatement("UPDATE characters SET x=?, y=?, z=? WHERE obj_Id=?");
                        statement.setInt(1, x);
                        statement.setInt(2, y);
                        statement.setInt(3, z);
                        statement.setInt(4, playerId);
                        statement.execute();
                        DbUtils.close(statement);

                        final Collection<ItemInstance> items = ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(playerId, ItemInstance.ItemLocation.PAPERDOLL);
                        for (final ItemInstance item : items) {
                            item.setEquipped(false);
                            item.setLocData(0);
                            item.setLocation(item.getTemplate().isStoreable() ? ItemInstance.ItemLocation.WAREHOUSE : ItemInstance.ItemLocation.INVENTORY);
                            item.setJdbcState(JdbcEntityState.UPDATED);
                            item.update();
                        }
                    }

                    statement = con.prepareStatement("DELETE FROM character_variables WHERE obj_id=? AND type='player-var' AND name='"
                            + PlayerVariables.REFLECTION + '\'');
                    statement.setInt(1, playerId);
                    statement.execute();
                    DbUtils.close(statement);
                    return "SUCCESS";
                } catch (Exception e) {
                    return "FAIL";
                } finally {
                    DbUtils.closeQuietly(con, statement, rs);
                }
            }
        });

        _commands.add(new TelnetCommand("billing.players.ban") {
            @Override
            public String getUsage() {
                return "billing.players.ban <char_obj_id> <ban_type> <period>";  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public String handle(final String[] args) {
                final int charId = Integer.parseInt(args[0]);
                final int banType = Integer.parseInt(args[1]);
                final int banPeriod = Integer.parseInt(args[2]);
                final String char_name = CharacterDAO.getInstance().getNameByObjectId(charId);

                if (char_name == null) {
                    return "FAIL";
                }

                switch (banType) {
                    case 0: // chat
                        AutoBan.ChatBan(char_name, banPeriod, "CP.THEABYSS.RU");
                        break;
                    case 1: // char
                        final Player character = World.getPlayer(charId);
                        if (character != null) {
                            character.setAccessLevel(-100);
                            AutoBan.banPlayer(character, banPeriod, "Banned by web-administrator", "CP.THEABYSS.RU");
                            character.kick();
                        } else {
                            AutoBan.banOfflinePlayer(char_name, -100, banPeriod, "Banned by web-administrator", "CP.THEABYSS.RU");
                        }
                        break;
                    default:
                        return "FAIL";
                }

                return "SUCCESS";
            }
        });

        _commands.add(new TelnetCommand("billing.players.unban") {
            @Override
            public String getUsage() {
                return "billing.players.unban <char_obj_id> <ban_type> <period>";
            }

            @Override
            public String handle(final String[] args) {
                final int charId = Integer.parseInt(args[0]);
                final int banType = Integer.parseInt(args[1]);
                final int unbanPeriod = Integer.parseInt(args[2]);
                final String char_name = CharacterDAO.getInstance().getNameByObjectId(charId);

                if (char_name == null) {
                    return "FAIL";
                }

                final Player character = World.getPlayer(charId);

                switch (banType) {
                    case 0: // chat
                        if (character != null) {
                            final long time = character.getNoChannelRemained();
                            if (unbanPeriod * 60000L > time) {
                                AutoBan.ChatBan(char_name, 0, "CP.THEABYSS.RU");
                            } else {
                                character.setNoChannel(time - unbanPeriod * 60000L);
                            }
                        } else {
                            Connection con = null;
                            PreparedStatement statement = null;
                            try {
                                con = DatabaseFactory.getInstance().getConnection();
                                statement = con.prepareStatement("UPDATE characters SET nochannel = IF(nochannel>?,nochannel-?,0) WHERE obj_Id=?");
                                statement.setInt(1, unbanPeriod * 60);
                                statement.setInt(2, unbanPeriod * 60);
                                statement.setInt(3, charId);
                                statement.executeUpdate();
                            } catch (Exception e) {
                                _log.warn("Could not activate nochannel:" + e);
                                return "FAIL";
                            } finally {
                                DbUtils.closeQuietly(con, statement);
                            }
                        }
                        break;
                    case 1: // char
                        AutoBan.banOfflinePlayer(char_name, 0, unbanPeriod, "Unbanned by web-administrator", "CP.THEABYSS.RU");
                        break;
                    default:
                        return "FAIL";
                }

                return "SUCCESS";
            }
        });

        _commands.add(new TelnetCommand("billing.clan.rename") {
            @Override
            public String getUsage() {
                return "billing.clan.rename <clan_id> <new_name>";
            }

            @Override
            public String handle(final String[] args) {
                final int clanId = Integer.parseInt(args[0]);
                final String name = args[1];
                final Clan clan = ClanTable.getInstance().getClan(clanId);
                if (clan == null) {
                    return "FAIL";
                }
                if (ClanTable.getInstance().getClanByName(name) != null) {
                    return "FAIL";
                }
                clan.getSubUnit(0).setName(name, true);
                clan.broadcastClanStatus(false, true, false);
                return "SUCCESS";
            }
        });

        _commands.add(new TelnetCommand("billing.ally.rename") {
            @Override
            public String getUsage() {
                return "billing.ally.rename <ally_id> <new_name>";
            }

            @Override
            public String handle(final String[] args) {
                final int allyId = Integer.parseInt(args[0]);
                final String name = args[1];
                final Alliance ally = ClanTable.getInstance().getAlliance(allyId);
                if (ally == null) {
                    return "FAIL";
                }
                if (ClanTable.getInstance().getAllyByName(name) != null) {
                    return "FAIL";
                }
                ally.setAllyName(name, true);
                return "SUCCESS";
            }
        });

        _commands.add(new TelnetCommand("billing.char.rename") {
            @Override
            public String getUsage() {
                return "billing.char.rename <char_id> <new_name>";
            }

            @Override
            public String handle(final String[] args) {
                final int charId = Integer.parseInt(args[0]);
                final String name = args[1];
                final String oldName = CharacterDAO.getInstance().getNameByObjectId(charId);
                if (oldName == null || oldName != null && oldName.isEmpty()) {
                    return "FAIL";
                }
                if (CharacterDAO.getInstance().getPlayersCountByName(name) > 0) {
                    return "FAIL";
                }
                CharacterDAO.getInstance().updateName(charId, name);
                final Player player = GameObjectsStorage.getPlayer(charId);
                if (player != null) {
                    player.setName(name);
                    player.broadcastCharInfo();
                }
                return "SUCCESS";
            }
        });
    }

    @Override
    public Set<TelnetCommand> getCommands() {
        return _commands;
    }
}
