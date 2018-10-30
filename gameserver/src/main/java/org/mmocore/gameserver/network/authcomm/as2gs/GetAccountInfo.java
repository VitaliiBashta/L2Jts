package org.mmocore.gameserver.network.authcomm.as2gs;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.utils.TroveUtils;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.ReceivablePacket;
import org.mmocore.gameserver.network.authcomm.gs2as.SetAccountInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author VISTALL
 * @date 21:05/25.03.2011
 */
public class GetAccountInfo extends ReceivablePacket {
    private static final Logger _log = LoggerFactory.getLogger(GetAccountInfo.class);
    private String _account;

    @Override
    protected void readImpl() {
        _account = readS();
    }

    @Override
    protected void runImpl() {
        int playerSize = 0;
        TIntList deleteChars = TroveUtils.EMPTY_INT_LIST;

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT deletetime FROM characters WHERE account_name=?");
            statement.setString(1, _account);
            rset = statement.executeQuery();
            while (rset.next()) {
                playerSize++;
                final int d = rset.getInt("deletetime");
                if (d > 0) {
                    if (deleteChars.isEmpty()) {
                        deleteChars = new TIntArrayList(3);
                    }

                    deleteChars.add(d + ServerConfig.DELETE_DAYS * 24 * 60 * 60);
                }
            }
        } catch (Exception e) {
            _log.error("GetAccountInfo:runImpl():" + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }

        AuthServerCommunication.getInstance().sendPacket(new SetAccountInfo(_account, playerSize, deleteChars.toArray()));
    }
}
