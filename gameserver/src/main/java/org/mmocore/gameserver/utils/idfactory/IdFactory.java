package org.mmocore.gameserver.utils.idfactory;

import gnu.trove.list.array.TIntArrayList;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

public abstract class IdFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdFactory.class);

    public static final String[][] EXTRACT_OBJ_ID_TABLES = {
            {"characters", "obj_id"},
            {"items", "object_id"},
            {"clan_data", "clan_id"},
            {"ally_data", "ally_id"},
            {"couples", "id"}
    };

    public static final int FIRST_OID = 0x00000001; // 0x10000000 - для тестов, двинем генерацию на еденичку.
    public static final int LAST_OID = 0x7FFFFFFF;
    public static final int FREE_OBJECT_ID_SIZE = LAST_OID - FIRST_OID;

    protected static final IdFactory INSTANCE = new BitSetIdFactory();

    protected volatile boolean initialized;
    protected long releasedCount = 0;

    protected IdFactory() {
        resetOnlineStatus();
    }

    public static IdFactory getInstance() {
        return INSTANCE;
    }

    private void resetOnlineStatus() {
        Connection con = null;
        Statement st = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            st = con.createStatement();
            st.executeUpdate("UPDATE characters SET online = 0");
            LOGGER.info("IdFactory: Clear characters online status.");
        } catch (SQLException e) {
            LOGGER.error("", e);
        } finally {
            DbUtils.closeQuietly(con, st);
        }
    }

    protected int[] extractUsedObjectIDTable() throws SQLException {
        final TIntArrayList objectIds = new TIntArrayList();

        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            st = con.createStatement();
            for (final String[] table : EXTRACT_OBJ_ID_TABLES) {
                rs = st.executeQuery("SELECT " + table[1] + " FROM " + table[0]);
                int size = objectIds.size();
                while (rs.next()) {
                    objectIds.add(rs.getInt(1));
                }

                DbUtils.close(rs);

                size = objectIds.size() - size;
                if (size > 0) {
                    LOGGER.info("IdFactory: Extracted {} used id's from {}", size, table[0]);
                }
            }
        } finally {
            DbUtils.closeQuietly(con, st, rs);
        }

        final int[] extracted = objectIds.toArray();

        Arrays.sort(extracted);

        LOGGER.info("IdFactory: Extracted total {} used id's.", extracted.length);

        return extracted;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public abstract int getNextId();

    /**
     * return a used Object ID back to the pool
     *
     * @param object ID
     */
    public void releaseId(final int id) {
        releasedCount++;
    }

    public long getReleasedCount() {
        return releasedCount;
    }

    public abstract int size();
}