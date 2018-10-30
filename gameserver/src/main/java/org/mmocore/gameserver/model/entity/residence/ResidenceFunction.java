package org.mmocore.gameserver.model.entity.residence;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.TeleportLocation;
import org.mmocore.gameserver.tables.SkillTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListMap;

public class ResidenceFunction {
    // residence functions
    public static final int TELEPORT = 1;
    public static final int ITEM_CREATE = 2;
    public static final int RESTORE_HP = 3;
    public static final int RESTORE_MP = 4;
    public static final int RESTORE_EXP = 5;
    public static final int SUPPORT = 6;
    public static final int CURTAIN = 7;
    public static final int PLATFORM = 8;
    public static final String A = "";
    public static final String W = "W";
    public static final String M = "M";
    private static final Logger _log = LoggerFactory.getLogger(ResidenceFunction.class);
    private static final Object[][][] buffs_template = {
            {
                    // level 0 - no buff
            },
            {
                    // level 1
                    {SkillTable.getInstance().getSkillEntry(4342, 1), A},
                    {SkillTable.getInstance().getSkillEntry(4343, 1), A},
                    {SkillTable.getInstance().getSkillEntry(4344, 1), A},
                    {SkillTable.getInstance().getSkillEntry(4346, 1), A},
                    {SkillTable.getInstance().getSkillEntry(4345, 1), W},
            },
            {
                    // level 2
                    {SkillTable.getInstance().getSkillEntry(4342, 2), A},
                    {SkillTable.getInstance().getSkillEntry(4343, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4344, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4346, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4345, 3), W},
            },
            {
                    // level 3
                    {SkillTable.getInstance().getSkillEntry(4342, 2), A},
                    {SkillTable.getInstance().getSkillEntry(4343, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4344, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4346, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4345, 3), W},
            },
            {
                    // level 4
                    {SkillTable.getInstance().getSkillEntry(4342, 2), A},
                    {SkillTable.getInstance().getSkillEntry(4343, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4344, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4346, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4345, 3), W},
                    {SkillTable.getInstance().getSkillEntry(4347, 2), A},
                    {SkillTable.getInstance().getSkillEntry(4349, 1), A},
                    {SkillTable.getInstance().getSkillEntry(4350, 1), W},
                    {SkillTable.getInstance().getSkillEntry(4348, 2), A},
            },
            {
                    // level 5
                    {SkillTable.getInstance().getSkillEntry(4342, 2), A},
                    {SkillTable.getInstance().getSkillEntry(4343, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4344, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4346, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4345, 3), W},
                    {SkillTable.getInstance().getSkillEntry(4347, 2), A},
                    {SkillTable.getInstance().getSkillEntry(4349, 1), A},
                    {SkillTable.getInstance().getSkillEntry(4350, 1), W},
                    {SkillTable.getInstance().getSkillEntry(4348, 2), A},
                    {SkillTable.getInstance().getSkillEntry(4351, 2), M},
                    {SkillTable.getInstance().getSkillEntry(4352, 1), A},
                    {SkillTable.getInstance().getSkillEntry(4353, 2), W},
                    {SkillTable.getInstance().getSkillEntry(4358, 1), W},
                    {SkillTable.getInstance().getSkillEntry(4354, 1), W},
            },
            {
                    // level 6 - unused
            },
            {
                    // level 7
                    {SkillTable.getInstance().getSkillEntry(4342, 2), A},
                    {SkillTable.getInstance().getSkillEntry(4343, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4344, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4346, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4345, 3), W},
                    {SkillTable.getInstance().getSkillEntry(4347, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4349, 2), A},
                    {SkillTable.getInstance().getSkillEntry(4350, 4), W},
                    {SkillTable.getInstance().getSkillEntry(4348, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4351, 6), M},
                    {SkillTable.getInstance().getSkillEntry(4352, 2), A},
                    {SkillTable.getInstance().getSkillEntry(4353, 6), W},
                    {SkillTable.getInstance().getSkillEntry(4358, 3), W},
                    {SkillTable.getInstance().getSkillEntry(4354, 4), W},
            },
            {
                    // level 8
                    {SkillTable.getInstance().getSkillEntry(4342, 2), A},
                    {SkillTable.getInstance().getSkillEntry(4343, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4344, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4346, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4345, 3), W},
                    {SkillTable.getInstance().getSkillEntry(4347, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4349, 2), A},
                    {SkillTable.getInstance().getSkillEntry(4350, 4), W},
                    {SkillTable.getInstance().getSkillEntry(4348, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4351, 6), M},
                    {SkillTable.getInstance().getSkillEntry(4352, 2), A},
                    {SkillTable.getInstance().getSkillEntry(4353, 6), W},
                    {SkillTable.getInstance().getSkillEntry(4358, 3), W},
                    {SkillTable.getInstance().getSkillEntry(4354, 4), W},
                    {SkillTable.getInstance().getSkillEntry(4355, 1), M},
                    {SkillTable.getInstance().getSkillEntry(4356, 1), M},
                    {SkillTable.getInstance().getSkillEntry(4357, 1), W},
                    {SkillTable.getInstance().getSkillEntry(4359, 1), W},
                    {SkillTable.getInstance().getSkillEntry(4360, 1), W},
            },
            {
                    // level 9 - unused
            },
            {
                    // level 10 - unused
            },
            {
                    // level 11
                    {SkillTable.getInstance().getSkillEntry(4342, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4343, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4344, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4346, 5), A},
                    {SkillTable.getInstance().getSkillEntry(4345, 4), W},
            },
            {
                    // level 12
                    {SkillTable.getInstance().getSkillEntry(4342, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4343, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4344, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4346, 8), A},
                    {SkillTable.getInstance().getSkillEntry(4345, 6), W},
            },
            {
                    // level 13
                    {SkillTable.getInstance().getSkillEntry(4342, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4343, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4344, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4346, 8), A},
                    {SkillTable.getInstance().getSkillEntry(4345, 6), W},
            },
            {
                    // level 14
                    {SkillTable.getInstance().getSkillEntry(4342, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4343, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4344, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4346, 8), A},
                    {SkillTable.getInstance().getSkillEntry(4345, 6), W},
                    {SkillTable.getInstance().getSkillEntry(4347, 8), A},
                    {SkillTable.getInstance().getSkillEntry(4349, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4350, 5), W},
                    {SkillTable.getInstance().getSkillEntry(4348, 8), A},
            },
            {
                    // level 15
                    {SkillTable.getInstance().getSkillEntry(4342, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4343, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4344, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4346, 8), A},
                    {SkillTable.getInstance().getSkillEntry(4345, 6), W},
                    {SkillTable.getInstance().getSkillEntry(4347, 8), A},
                    {SkillTable.getInstance().getSkillEntry(4349, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4350, 5), W},
                    {SkillTable.getInstance().getSkillEntry(4348, 8), A},
                    {SkillTable.getInstance().getSkillEntry(4351, 8), M},
                    {SkillTable.getInstance().getSkillEntry(4352, 3), A},
                    {SkillTable.getInstance().getSkillEntry(4353, 8), W},
                    {SkillTable.getInstance().getSkillEntry(4358, 4), W},
                    {SkillTable.getInstance().getSkillEntry(4354, 5), W},
            },
            {
                    // level 16 - unused
            },
            {
                    // level 17
                    {SkillTable.getInstance().getSkillEntry(4342, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4343, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4344, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4346, 8), A},
                    {SkillTable.getInstance().getSkillEntry(4345, 6), W},
                    {SkillTable.getInstance().getSkillEntry(4347, 12), A},
                    {SkillTable.getInstance().getSkillEntry(4349, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4350, 8), W},
                    {SkillTable.getInstance().getSkillEntry(4348, 12), A},
                    {SkillTable.getInstance().getSkillEntry(4351, 12), M},
                    {SkillTable.getInstance().getSkillEntry(4352, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4353, 12), W},
                    {SkillTable.getInstance().getSkillEntry(4358, 6), W},
                    {SkillTable.getInstance().getSkillEntry(4354, 8), W},
            },
            {
                    // level 18
                    {SkillTable.getInstance().getSkillEntry(4342, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4343, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4344, 6), A},
                    {SkillTable.getInstance().getSkillEntry(4346, 8), A},
                    {SkillTable.getInstance().getSkillEntry(4345, 6), W},
                    {SkillTable.getInstance().getSkillEntry(4347, 12), A},
                    {SkillTable.getInstance().getSkillEntry(4349, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4350, 8), W},
                    {SkillTable.getInstance().getSkillEntry(4348, 12), A},
                    {SkillTable.getInstance().getSkillEntry(4351, 12), M},
                    {SkillTable.getInstance().getSkillEntry(4352, 4), A},
                    {SkillTable.getInstance().getSkillEntry(4353, 12), W},
                    {SkillTable.getInstance().getSkillEntry(4358, 6), W},
                    {SkillTable.getInstance().getSkillEntry(4354, 8), W},
                    {SkillTable.getInstance().getSkillEntry(4355, 4), M},
                    {SkillTable.getInstance().getSkillEntry(4356, 4), M},
                    {SkillTable.getInstance().getSkillEntry(4357, 3), W},
                    {SkillTable.getInstance().getSkillEntry(4359, 4), W},
                    {SkillTable.getInstance().getSkillEntry(4360, 4), W},
            },
    };
    private final int _id;
    private final int _type;
    private final Map<Integer, Integer> _leases = new ConcurrentSkipListMap<>();
    private final Map<Integer, TeleportLocation[]> _teleports = new ConcurrentSkipListMap<>();
    private final Map<Integer, int[]> _buylists = new ConcurrentSkipListMap<>();
    private final Map<Integer, Object[][]> _buffs = new ConcurrentSkipListMap<>();
    private int _level;
    private Instant endDate;
    private boolean _inDebt;
    private boolean _active;

    public ResidenceFunction(final int id, final int type) {
        _id = id;
        _type = type;
        endDate = Instant.now();
    }

    public int getResidenceId() {
        return _id;
    }

    public int getType() {
        return _type;
    }

    public int getLevel() {
        return _level;
    }

    public void setLvl(final int lvl) {
        _level = lvl;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndTime(final Instant instant) {
        endDate = instant;
    }

    public boolean isInDebt() {
        return _inDebt;
    }

    public void setInDebt(final boolean inDebt) {
        _inDebt = inDebt;
    }

    public boolean isActive() {
        return _active;
    }

    public void setActive(final boolean active) {
        _active = active;
    }

    public void updateRentTime(final boolean inDebt) {
        setEndTime(Instant.now().plusMillis(86400000));

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE residence_functions SET endTime=?, inDebt=? WHERE type=? AND id=?");
            statement.setInt(1, (int) getEndDate().getEpochSecond());
            statement.setInt(2, inDebt ? 1 : 0);
            statement.setInt(3, getType());
            statement.setInt(4, getResidenceId());
            statement.executeUpdate();
        } catch (Exception e) {
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public TeleportLocation[] getTeleports() {
        return getTeleports(_level);
    }

    public TeleportLocation[] getTeleports(final int level) {
        return _teleports.get(level);
    }

    public void addTeleports(final int level, final TeleportLocation[] teleports) {
        _teleports.put(level, teleports);
    }

    public int getLease() {
        if (_level == 0) {
            return 0;
        }
        return getLease(_level);
    }

    public int getLease(final int level) {
        return _leases.get(level);
    }

    public void addLease(final int level, final int lease) {
        _leases.put(level, lease);
    }

    public int[] getBuylist() {
        return getBuylist(_level);
    }

    public int[] getBuylist(final int level) {
        return _buylists.get(level);
    }

    public void addBuylist(final int level, final int[] buylist) {
        _buylists.put(level, buylist);
    }

    public Object[][] getBuffs() {
        return getBuffs(_level);
    }

    public Object[][] getBuffs(final int level) {
        return _buffs.get(level);
    }

    public void addBuffs(final int level) {
        _buffs.put(level, buffs_template[level]);
    }

    public Set<Integer> getLevels() {
        return _leases.keySet();
    }
}