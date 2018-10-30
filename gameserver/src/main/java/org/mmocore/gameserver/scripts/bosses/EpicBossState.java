package org.mmocore.gameserver.scripts.bosses;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.utils.TimeUtils;
import org.mmocore.gameserver.utils.Util;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;

public class EpicBossState {
    private static final Logger _log = LoggerFactory.getLogger(EpicBossState.class);
    private int bossId;
    private Instant respawnDate;
    private State state;
    public EpicBossState(final int bossId) {
        this(bossId, true);
    }
    public EpicBossState(final int bossId, final boolean isDoLoad) {
        this.bossId = bossId;
        if (isDoLoad) {
            load();
        }
    }

    public int getBossId() {
        return bossId;
    }

    public void setBossId(final int newId) {
        bossId = newId;
    }

    public State getState() {
        return state;
    }

    public void setState(final State newState) {
        state = newState;
    }

    public Instant getRespawnDate() {
        return respawnDate;
    }

    public void setRespawnDate(final long interval) {
        respawnDate = Instant.now().plusMillis(interval);
    }

    public void setWeaklyRespawnDate(CronExpression cron, int randomMinutes) {
        respawnDate = Instant.ofEpochMilli(Util.getCronMillis(cron) + Rnd.get(0, randomMinutes * 60000));
    }

    public void load() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement("SELECT * FROM epic_boss_spawn WHERE bossId = ? LIMIT 1");
            statement.setInt(1, bossId);
            rset = statement.executeQuery();

            if (rset.next()) {
                respawnDate = Instant.ofEpochSecond(rset.getLong("respawnDate"));

                final long diffInMs = Duration.between(Instant.now(), respawnDate).toMillis();
                if (diffInMs <= 0) {
                    state = State.NOTSPAWN;
                } else {
                    final int tempState = rset.getInt("state");
                    state = tempState < 1 || tempState > 3 ? State.NOTSPAWN : State.values()[tempState];
                }
            }
        } catch (final Exception e) {
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void save() {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("REPLACE INTO epic_boss_spawn (bossId,respawnDate,state) VALUES(?,?,?)");
            statement.setInt(1, bossId);
            statement.setInt(2, (int) respawnDate.getEpochSecond());
            statement.setInt(3, state.ordinal());
            statement.execute();
        } catch (final Exception e) {
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void update() {
        Connection con = null;
        Statement statement = null;

        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.createStatement();
            statement.executeUpdate("UPDATE epic_boss_spawn SET respawnDate=" + respawnDate.getEpochSecond() + ", state=" + state.ordinal() + " WHERE bossId=" + bossId);
            _log.info("update EpicBossState: ID:{}, RespawnDate:{}, State:{}", bossId, TimeUtils.dateTimeFormat(respawnDate), state);
        } catch (final Exception e) {
            _log.error("Exception on update EpicBossState: ID {}, RespawnDate:{}, State:{}", bossId, respawnDate.getEpochSecond(), state, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void setNextRespawnDate(final Instant newRespawnDate) {
        respawnDate = newRespawnDate;
    }

    public long getInterval() {
        final long interval = Duration.between(Instant.now(), respawnDate).toMillis();
        return interval > 0 ? interval : 0;
    }

    public static enum State {
        NOTSPAWN,
        ALIVE,
        DEAD,
        INTERVAL
    }

    public static enum NestState {
        ALLOW,
        LIMIT_EXCEEDED,
        ALREADY_ATTACKED,
        NOT_AVAILABLE
    }
}