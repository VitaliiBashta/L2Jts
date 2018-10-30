package org.mmocore.gameserver.database.dao.impl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;
import org.mmocore.commons.jdbchelper.ResultSetHandler;
import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import static org.mmocore.gameserver.cache.CrestCache.CrestType;

/**
 * @author Java-man
 */
public class CrestCacheDAO extends AbstractGameServerDAO {
    private static final CrestCacheDAO INSTANCE = new CrestCacheDAO();

    private static final String LOAD_PLEDGE_CRESTS = "SELECT `clan_id`,`crest` FROM `clan_data` WHERE `crest` IS NOT NULL";
    private static final String LOAD_PLEDGE_LARGE_CRESTS = "SELECT `clan_id`,`largecrest` FROM `clan_data` WHERE `largecrest` IS NOT NULL";
    private static final String LOAD_ALLY_CRESTS = "SELECT `ally_id`,`crest` FROM `ally_data` WHERE `crest` IS NOT NULL";

    private static final String UPDATE_PLEDGE_CREST = "UPDATE clan_data SET crest=? WHERE clan_id=?";
    private static final String UPDATE_PLEDGE_LARGE_CREST = "UPDATE clan_data SET largecrest=? WHERE clan_id=?";
    private static final String UPDATE_ALLY_CREST = "UPDATE ally_data SET crest=? WHERE ally_id=?";

    public static CrestCacheDAO getInstance() {
        return INSTANCE;
    }

    public ImmutableTable<CrestType, Integer, byte[]> selectAllCrests() {
        final Table<CrestType, Integer, byte[]> result = HashBasedTable.create();
        jdbcHelper.query(LOAD_PLEDGE_CRESTS, new ResultSetHandler() {
            @Override
            public void processRow(final ResultSet rs) throws SQLException {
                result.put(CrestType.PLEDGE, rs.getInt(1), rs.getBytes(2));
            }
        });
        jdbcHelper.query(LOAD_PLEDGE_LARGE_CRESTS, new ResultSetHandler() {
            @Override
            public void processRow(final ResultSet rs) throws SQLException {
                result.put(CrestType.PLEDGE_LARGE, rs.getInt(1), rs.getBytes(2));
            }
        });
        jdbcHelper.query(LOAD_ALLY_CRESTS, new ResultSetHandler() {
            @Override
            public void processRow(final ResultSet rs) throws SQLException {
                result.put(CrestType.ALLY, rs.getInt(1), rs.getBytes(2));
            }
        });
        return ImmutableTable.copyOf(result);
    }

    public void save(final CrestType crestType, final int pledgeId, final byte[] crestHash) {
        String query = null;
        switch (crestType) {
            case PLEDGE:
                query = UPDATE_PLEDGE_CREST;
                break;
            case PLEDGE_LARGE:
                query = UPDATE_PLEDGE_LARGE_CREST;
                break;
            case ALLY:
                query = UPDATE_ALLY_CREST;
                break;
        }
        jdbcHelper.execute(query, stmt -> {
            stmt.setBytes(1, crestHash);
            stmt.setInt(2, pledgeId);
        });
    }

    public void remove(final CrestType crestType, final int pledgeId) {
        String query = null;
        switch (crestType) {
            case PLEDGE:
                query = UPDATE_PLEDGE_CREST;
                break;
            case PLEDGE_LARGE:
                query = UPDATE_PLEDGE_LARGE_CREST;
                break;
            case ALLY:
                query = UPDATE_ALLY_CREST;
                break;
        }
        jdbcHelper.execute(query, stmt -> {
            stmt.setNull(1, Types.VARBINARY);
            stmt.setInt(2, pledgeId);
        });
    }
}
