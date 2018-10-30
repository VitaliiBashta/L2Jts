package org.mmocore.gameserver.model.pledge;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.cache.CrestCache;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.ClanTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Alliance {
    public static final long EXPELLED_MEMBER_PENALTY = 24 * 60 * 60 * 1000L;
    private static final Logger _log = LoggerFactory.getLogger(Alliance.class);
    private final Map<Integer, Clan> _members = new ConcurrentHashMap<>();
    private String _allyName;
    private int _allyId;
    private Clan _leader = null;
    private int _allyCrestId;
    private long _expelledMemberTime;

    public Alliance(final int allyId) {
        _allyId = allyId;
        restore();
    }

    public Alliance(final int allyId, final String allyName, final Clan leader) {
        _allyId = allyId;
        _allyName = allyName;
        setLeader(leader);
    }

    public int getLeaderId() {
        return _leader != null ? _leader.getClanId() : 0;
    }

    public Clan getLeader() {
        return _leader;
    }

    public void setLeader(final Clan leader) {
        _leader = leader;
        _members.put(leader.getClanId(), leader);
    }

    public String getAllyLeaderName() {
        return _leader != null ? _leader.getLeaderName() : "";
    }

    public void addAllyMember(final Clan member, final boolean storeInDb) {
        _members.put(member.getClanId(), member);

        if (storeInDb) {
            storeNewMemberInDatabase(member);
        }
    }

    public Clan getAllyMember(final int id) {
        return _members.get(id);
    }

    public void removeAllyMember(final int id) {
        if (_leader != null && _leader.getClanId() == id) {
            return;
        }
        final Clan exMember = _members.remove(id);
        if (exMember == null) {
            _log.warn("Clan " + id + " not found in alliance while trying to remove");
            return;
        }
        removeMemberInDatabase(exMember);
    }

    public Clan[] getMembers() {
        return _members.values().toArray(new Clan[_members.size()]);
    }

    public int getMembersCount() {
        return _members.size();
    }

    public int getAllyId() {
        return _allyId;
    }

    public void setAllyId(final int allyId) {
        _allyId = allyId;
    }

    public String getAllyName() {
        return _allyName;
    }

    public int getAllyCrestId() {
        return _allyCrestId;
    }

    public void setAllyCrestId(final int allyCrestId) {
        _allyCrestId = allyCrestId;
    }

    public void setAllyName(final String allyName, final boolean updateDB) {
        _allyName = allyName;
        if (updateDB) {
            Connection con = null;
            PreparedStatement statement = null;
            try {
                con = DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement("UPDATE ally_data SET ally_name=? WHERE ally_id=?");
                statement.setString(1, _allyName);
                statement.setInt(2, _allyId);
                statement.execute();
            } catch (Exception e) {
                _log.error("Exception: " + e, e);
            } finally {
                DbUtils.closeQuietly(con, statement);
            }
        }
    }

    public boolean isMember(final int id) {
        return _members.containsKey(id);
    }

    public long getExpelledMemberTime() {
        return _expelledMemberTime;
    }

    public void setExpelledMemberTime(final long time) {
        _expelledMemberTime = time;
    }

    public void setExpelledMember() {
        _expelledMemberTime = System.currentTimeMillis();
        updateAllyInDB();
    }

    public boolean canInvite() {
        return System.currentTimeMillis() - _expelledMemberTime >= EXPELLED_MEMBER_PENALTY;
    }

    public void updateAllyInDB() {
        if (getLeaderId() == 0) {
            _log.warn("updateAllyInDB with empty LeaderId");
            Thread.dumpStack();
            return;
        }

        if (getAllyId() == 0) {
            _log.warn("updateAllyInDB with empty AllyId");
            Thread.dumpStack();
            return;
        }

        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE ally_data SET leader_id=?,expelled_member=? WHERE ally_id=?");
            statement.setInt(1, getLeaderId());
            statement.setLong(2, getExpelledMemberTime() / 1000);
            statement.setInt(3, getAllyId());
            statement.execute();
        } catch (Exception e) {
            _log.warn("error while updating ally '" + getAllyId() + "' data in db: " + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void store() {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("INSERT INTO ally_data (ally_id,ally_name,leader_id) values (?,?,?)");
            statement.setInt(1, getAllyId());
            statement.setString(2, getAllyName());
            statement.setInt(3, getLeaderId());
            statement.execute();
            DbUtils.close(statement);

            statement = con.prepareStatement("UPDATE clan_data SET ally_id=? WHERE clan_id=?");
            statement.setInt(1, getAllyId());
            statement.setInt(2, getLeaderId());
            statement.execute();
        } catch (Exception e) {
            _log.warn("error while saving new ally to db " + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    private void storeNewMemberInDatabase(final Clan member) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE clan_data SET ally_id=? WHERE clan_id=?");
            statement.setInt(1, getAllyId());
            statement.setInt(2, member.getClanId());
            statement.execute();
        } catch (Exception e) {
            _log.warn("error while saving new alliance member to db " + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    private void removeMemberInDatabase(final Clan member) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE clan_data SET ally_id=0 WHERE clan_id=?");
            statement.setInt(1, member.getClanId());
            statement.execute();
        } catch (Exception e) {
            _log.warn("error while removing ally member in db " + e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    private void restore() {
        if (getAllyId() == 0) // no ally
        {
            return;
        }

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            Clan member;

            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT ally_name,leader_id FROM ally_data where ally_id=?");
            statement.setInt(1, getAllyId());
            rset = statement.executeQuery();

            if (rset.next()) {
                setAllyName(rset.getString("ally_name"), false);
                final int leaderId = rset.getInt("leader_id");

                DbUtils.close(statement, rset);
                statement = con.prepareStatement("SELECT clan_id FROM clan_data WHERE ally_id=?");
                statement.setInt(1, getAllyId());
                rset = statement.executeQuery();

                while (rset.next()) {
                    member = ClanTable.getInstance().getClan(rset.getInt("clan_id"));
                    if (member != null) {
                        if (member.getClanId() == leaderId) {
                            setLeader(member);
                        } else {
                            addAllyMember(member, false);
                        }
                    }
                }
            }

            setAllyCrestId(CrestCache.getInstance().getCrestId(CrestCache.CrestType.ALLY, getAllyId()));
        } catch (Exception e) {
            _log.warn("error while restoring ally");
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void broadcastToOnlineMembers(final L2GameServerPacket packet) {
        for (final Clan member : _members.values()) {
            if (member != null) {
                member.broadcastToOnlineMembers(packet);
            }
        }
    }

    public void broadcastToOtherOnlineMembers(final L2GameServerPacket packet, final Player player) {
        for (final Clan member : _members.values()) {
            if (member != null) {
                member.broadcastToOtherOnlineMembers(packet, player);
            }
        }
    }

    @Override
    public String toString() {
        return getAllyName();
    }

    public boolean hasAllyCrest() {
        return _allyCrestId > 0;
    }

    public void broadcastAllyStatus() {
        for (final Clan member : getMembers()) {
            member.broadcastClanStatus(false, true, false);
        }
    }
}
