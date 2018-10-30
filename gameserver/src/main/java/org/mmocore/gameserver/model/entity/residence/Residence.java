package org.mmocore.gameserver.model.entity.residence;

import org.mmocore.commons.database.dao.JdbcEntity;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @reworked VISTALL
 */
public abstract class Residence implements JdbcEntity {
    public static final long CYCLE_TIME = 60 * 60 * 1000L; // 1 час
    public static final ZonedDateTime MIN_SIEGE_DATE = ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.systemDefault());
    private static final Logger _log = LoggerFactory.getLogger(Residence.class);
    protected final int _id;
    protected final String _name;
    protected final List<ResidenceFunction> _functions = new ArrayList<>();
    protected final List<SkillEntry> _skills = new ArrayList<>();
    // points
    protected final List<Location> _banishPoints = new ArrayList<>();
    protected final List<Location> _ownerRestartPoints = new ArrayList<>();
    protected final List<Location> _otherRestartPoints = new ArrayList<>();
    protected final List<Location> _chaosRestartPoints = new ArrayList<>();
    protected Clan _owner;
    protected Zone _zone;
    protected SiegeEvent<?, ?> _siegeEvent;
    protected ZonedDateTime siegeDate = ZonedDateTime.now();
    protected ZonedDateTime lastSiegeDate = ZonedDateTime.now();
    protected ZonedDateTime ownDate = ZonedDateTime.now();
    // rewards
    protected ScheduledFuture<?> _cycleTask;
    protected JdbcEntityState _jdbcEntityState = JdbcEntityState.CREATED;
    private int _cycle;
    private int _rewardCount;
    private int _paidCycle;
    private long _prisonReuseTime;

    public Residence(final StatsSet set) {
        _id = set.getInteger("id");
        _name = set.getString("name");
    }

    public void init() {
        initZone();
        initEvent();

        loadData();
        loadFunctions();
        rewardSkills();
        startCycleTask();
    }

    protected void initZone() {
        _zone = ReflectionUtils.getZone("residence_" + _id);
        _zone.setParam("residence", this);
    }

    protected void initEvent() {
        _siegeEvent = EventHolder.getInstance().getEvent(EventType.SIEGE_EVENT, _id);
    }

    @SuppressWarnings("unchecked")
    public <E extends SiegeEvent<?, ?>> E getSiegeEvent() {
        return (E) _siegeEvent;
    }

    public boolean isInSiege() {
        return _siegeEvent != null && _siegeEvent.isInProgress();
    }

    public int getId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public int getOwnerId() {
        return _owner == null ? 0 : _owner.getClanId();
    }

    public Clan getOwner() {
        return _owner;
    }

    public Zone getZone() {
        return _zone;
    }

    protected abstract void loadData();

    public abstract void changeOwner(Clan clan);

    public ZonedDateTime getOwnDate() {
        return ownDate;
    }

    public void setOwnDate(final ZonedDateTime ownDate) {
        this.ownDate = ownDate;
    }

    public ZonedDateTime getSiegeDate() {
        return siegeDate;
    }

    public void setSiegeDate(final ZonedDateTime siegeDate) {
        this.siegeDate = siegeDate;
    }

    public ZonedDateTime getLastSiegeDate() {
        return lastSiegeDate;
    }

    public void setLastSiegeDate(final ZonedDateTime lastSiegeDate) {
        this.lastSiegeDate = lastSiegeDate;
    }

    public void setSiegeDateOfInstant(final Instant siegeDateOfInstant) {
        this.siegeDate = ZonedDateTime.ofInstant(siegeDateOfInstant, ZoneId.systemDefault());
    }

    public void addSkill(final SkillEntry skill) {
        _skills.add(skill);
    }

    public void addFunction(final ResidenceFunction function) {
        _functions.add(function);
    }

    public boolean checkIfInZone(final Location loc, final Reflection ref) {
        return checkIfInZone(loc.x, loc.y, loc.z, ref);
    }

    public boolean checkIfInZone(final int x, final int y, final int z, final Reflection ref) {
        return getZone() != null && getZone().checkIfInZone(x, y, z, ref);
    }

    public void banishForeigner() {
        for (final Player player : _zone.getInsidePlayers()) {
            if (player.getClanId() == getOwnerId()) {
                continue;
            }

            player.teleToLocation(getBanishPoint());
        }
    }

    /**
     * Выдает клану-владельцу скилы резиденции
     */
    public void rewardSkills() {
        final Clan owner = getOwner();
        if (owner != null) {
            for (final SkillEntry skill : _skills) {
                owner.addSkill(skill, false);
                owner.broadcastToOnlineMembers(new SystemMessage(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill));
            }
        }
    }

    /**
     * Удаляет у клана-владельца скилы резиденции
     */
    public void removeSkills() {
        final Clan owner = getOwner();
        if (owner != null) {
            for (final SkillEntry skill : _skills) {
                owner.removeSkill(skill.getId());
            }
        }
    }

    protected void loadFunctions() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM residence_functions WHERE id = ?");
            statement.setInt(1, getId());
            rs = statement.executeQuery();
            while (rs.next()) {
                final ResidenceFunction function = getFunction(rs.getInt("type"));
                function.setLvl(rs.getInt("lvl"));
                function.setEndTime(Instant.ofEpochSecond(rs.getInt("endTime")));
                function.setInDebt(rs.getBoolean("inDebt"));
                function.setActive(true);
                startAutoTaskForFunction(function);
            }
        } catch (final Exception e) {
            _log.warn("Residence: loadFunctions(): " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rs);
        }
    }

    public boolean isFunctionActive(final int type) {
        final ResidenceFunction function = getFunction(type);
        if (function != null && function.isActive() && function.getLevel() > 0) {
            return true;
        }
        return false;
    }

    public ResidenceFunction getFunction(final int type) {
        for (final ResidenceFunction rf : _functions) {
            if (rf.getType() == type) {
                return rf;
            }
        }
        return null;
    }

    public boolean updateFunctions(final int type, final int level) {
        final Clan clan = getOwner();
        if (clan == null) {
            return false;
        }

        final long count = clan.getAdenaCount();

        final ResidenceFunction function = getFunction(type);
        if (function == null) {
            return false;
        }

        if (function.isActive() && function.getLevel() == level) {
            return true;
        }

        final int lease = level == 0 ? 0 : getFunction(type).getLease(level);

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();

            if (!function.isActive()) {
                if (count >= lease) {
                    clan.getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, lease);
                } else {
                    return false;
                }

                final Instant endTime = Instant.now().plusMillis(86400000);

                statement = con.prepareStatement("REPLACE residence_functions SET id=?, type=?, lvl=?, endTime=?");
                statement.setInt(1, getId());
                statement.setInt(2, type);
                statement.setInt(3, level);
                statement.setInt(4, (int) endTime.getEpochSecond());
                statement.execute();

                function.setLvl(level);
                function.setEndTime(endTime);
                function.setActive(true);
                startAutoTaskForFunction(function);
            } else {
                if (count >= lease - getFunction(type).getLease()) {
                    if (lease > getFunction(type).getLease()) {
                        clan.getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, lease - getFunction(type).getLease());
                    }
                } else {
                    return false;
                }

                statement = con.prepareStatement("REPLACE residence_functions SET id=?, type=?, lvl=?");
                statement.setInt(1, getId());
                statement.setInt(2, type);
                statement.setInt(3, level);
                statement.execute();

                function.setLvl(level);
            }
        } catch (final Exception e) {
            _log.warn("Exception: Residence.updateFunctions(int type, int lvl, int lease, long rate, long time, boolean addNew): " + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
        return true;
    }

    public void removeFunction(final int type) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM residence_functions WHERE id=? AND type=?");
            statement.setInt(1, getId());
            statement.setInt(2, type);
            statement.execute();
        } catch (final Exception e) {
            _log.warn("Exception: removeFunctions(int type): " + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    private void startAutoTaskForFunction(final ResidenceFunction function) {
        if (getOwnerId() == 0) {
            return;
        }

        final Clan clan = getOwner();

        if (clan == null) {
            return;
        }

        final Instant now = Instant.now();
        if (function.getEndDate().isAfter(now)) {
            final long diffInMillis = Duration.between(now, function.getEndDate()).toMillis();
            ThreadPoolManager.getInstance().schedule(new AutoTaskForFunctions(function), diffInMillis);
        } else if (function.isInDebt() && clan.getAdenaCount() >= function.getLease()) // if player didn't pay before add extra fee
        {
            clan.getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, function.getLease());
            function.updateRentTime(false);

            final long diffInMillis = Duration.between(now, function.getEndDate()).toMillis();
            ThreadPoolManager.getInstance().schedule(new AutoTaskForFunctions(function), diffInMillis);
        } else if (!function.isInDebt()) {
            function.setInDebt(true);
            function.updateRentTime(true);

            final long diffInMillis = Duration.between(now, function.getEndDate()).toMillis();
            ThreadPoolManager.getInstance().schedule(new AutoTaskForFunctions(function), diffInMillis);
        } else {
            function.setLvl(0);
            function.setActive(false);
            removeFunction(function.getType());
        }
    }

    @Override
    public JdbcEntityState getJdbcState() {
        return _jdbcEntityState;
    }

    @Override
    public void setJdbcState(final JdbcEntityState state) {
        _jdbcEntityState = state;
    }

    @Override
    public void save() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException();
    }

    public void cancelCycleTask() {
        _cycle = 0;
        _paidCycle = 0;
        _rewardCount = 0;
        if (_cycleTask != null) {
            _cycleTask.cancel(false);
            _cycleTask = null;
        }

        setJdbcState(JdbcEntityState.UPDATED);
    }

    public void startCycleTask() {
        if (_owner == null)
            return;

        if (getOwnDate().isEqual(MIN_SIEGE_DATE))
            return;

        long diff = Duration.between(ZonedDateTime.now(), getOwnDate()).toMillis();
        while (diff >= CYCLE_TIME)
            diff -= CYCLE_TIME;

        _cycleTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ResidenceCycleTask(), diff, CYCLE_TIME);
    }

    public void chanceCycle() {
        setCycle(getCycle() + 1);

        setJdbcState(JdbcEntityState.UPDATED);
    }

    public List<SkillEntry> getSkills() {
        return _skills;
    }

    public void addBanishPoint(final Location loc) {
        _banishPoints.add(loc);
    }

    public void addOwnerRestartPoint(final Location loc) {
        _ownerRestartPoints.add(loc);
    }

    public void addOtherRestartPoint(final Location loc) {
        _otherRestartPoints.add(loc);
    }

    public void addChaosRestartPoint(final Location loc) {
        _chaosRestartPoints.add(loc);
    }

    public Location getBanishPoint() {
        if (_banishPoints.isEmpty()) {
            return null;
        }
        return Rnd.get(_banishPoints);
    }

    public Location getOwnerRestartPoint() {
        if (_ownerRestartPoints.isEmpty()) {
            return null;
        }
        return Rnd.get(_ownerRestartPoints);
    }

    public Location getOtherRestartPoint() {
        if (_otherRestartPoints.isEmpty()) {
            return null;
        }
        return Rnd.get(_otherRestartPoints);
    }

    public Location getChaosRestartPoint() {
        if (_chaosRestartPoints.isEmpty()) {
            return null;
        }
        return Rnd.get(_chaosRestartPoints);
    }

    public Location getNotOwnerRestartPoint(final Player player) {
        return player.getKarma() > 0 ? getChaosRestartPoint() : getOtherRestartPoint();
    }

    public int getCycle() {
        return _cycle;
    }

    public void setCycle(final int cycle) {
        _cycle = cycle;
    }

    public long getCycleDelay() {
        if (_cycleTask == null) {
            return 0;
        }
        return _cycleTask.getDelay(TimeUnit.SECONDS);
    }

    public int getPaidCycle() {
        return _paidCycle;
    }

    public void setPaidCycle(final int paidCycle) {
        _paidCycle = paidCycle;
    }

    public int getRewardCount() {
        return _rewardCount;
    }

    public void setRewardCount(final int rewardCount) {
        _rewardCount = rewardCount;
    }

    public long getPrisonReuseTime() {
        return _prisonReuseTime;
    }

    public void setPrisonReuseTime(final long time) {
        _prisonReuseTime = time;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Residence))
            return false;

        final Residence residence = (Residence) o;

        if (_id != residence._id)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return _id;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '[' + getName() + ':' + getId() + ']';
    }

    public class ResidenceCycleTask extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            chanceCycle();

            update();
        }
    }

    private class AutoTaskForFunctions extends RunnableImpl {
        final ResidenceFunction _function;

        public AutoTaskForFunctions(final ResidenceFunction function) {
            _function = function;
        }

        @Override
        public void runImpl() throws Exception {
            startAutoTaskForFunction(_function);
        }
    }
}