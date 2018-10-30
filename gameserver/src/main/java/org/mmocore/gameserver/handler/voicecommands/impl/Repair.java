package org.mmocore.gameserver.handler.voicecommands.impl;

import gnu.trove.iterator.TIntObjectIterator;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.ItemsDAO;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;
import org.mmocore.gameserver.object.components.player.AccountPlayerInfo;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

public class Repair implements IVoicedCommandHandler {
    private static final Logger _log = LoggerFactory.getLogger(Repair.class);

    private final String[] _commandList = {"repair"};

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }

    @Override
    public boolean useVoicedCommand(final String command, final Player activeChar, final String target) {
        if (!target.isEmpty()) {
            if (activeChar.getName().equalsIgnoreCase(target)) {
                activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.YouCantRepairYourself"));
                return false;
            }

            int objId = 0;

            final TIntObjectIterator<AccountPlayerInfo> iterator = activeChar.getAccountChars().iterator();
            for (int i = activeChar.getAccountChars().size(); i > 0; i--) {
                iterator.advance();
                if (iterator.value().getName().equalsIgnoreCase(target)) {
                    objId = iterator.key();
                    break;
                }
            }

            if (objId == 0) {
                activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.YouCanRepairOnlyOnSameAccount"));
                return false;
            } else if (World.getPlayer(objId) != null) {
                activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.CharIsOnline"));
                return false;
            }

            Connection con = null;
            PreparedStatement statement = null;
            ResultSet rs = null;
            try {
                con = DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement("SELECT karma FROM characters WHERE obj_Id=?");
                statement.setInt(1, objId);
                statement.execute();
                rs = statement.getResultSet();

                int karma = 0;

                rs.next();

                karma = rs.getInt("karma");

                DbUtils.close(statement, rs);

                if (karma > 0) {
                    statement = con.prepareStatement("UPDATE characters SET x=17144, y=170156, z=-3502 WHERE obj_Id=?");
                    statement.setInt(1, objId);
                    statement.execute();
                    DbUtils.close(statement);
                } else {
                    statement = con.prepareStatement("UPDATE characters SET x=17864, y=170312, z=-3504 WHERE obj_Id=?");
                    statement.setInt(1, objId);
                    statement.execute();
                    DbUtils.close(statement);

                    final Collection<ItemInstance> items = ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(objId, ItemLocation.PAPERDOLL);
                    for (final ItemInstance item : items) {
                        item.setEquipped(false);
                        item.setLocData(0);
                        item.setLocation(item.getTemplate().isStoreable() ? ItemLocation.WAREHOUSE : ItemLocation.INVENTORY);
                        item.setJdbcState(JdbcEntityState.UPDATED);
                        item.update();
                    }
                }

                statement = con.prepareStatement("DELETE FROM character_variables WHERE obj_id=? AND type='player-var' AND name='"
                        + PlayerVariables.REFLECTION + '\'');
                statement.setInt(1, objId);
                statement.execute();
                DbUtils.close(statement);

                activeChar.sendMessage(new CustomMessage("voicedcommandhandlers.Repair.RepairDone"));
                return true;
            } catch (Exception e) {
                _log.error("", e);
                return false;
            } finally {
                DbUtils.closeQuietly(con, statement, rs);
            }
        } else {
            activeChar.sendMessage(".repair <name>");
        }

        return false;
    }
}