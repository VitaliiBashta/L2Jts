package org.mmocore.gameserver.model.pledge;

import com.google.common.collect.Iterators;
import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.cache.CrestCache;
import org.mmocore.gameserver.cache.CrestCache.CrestType;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.ClanDataDAO;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.entity.residence.ClanHall;
import org.mmocore.gameserver.model.entity.residence.Fortress;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.network.lineage.components.IBroadcastPacket;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.ClanAirShip;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.warehouse.ClanWarehouse;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.SkillEntryType;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.stream.Collectors;

public class Clan implements Iterable<UnitMember> {
    public static final RankPrivs[] EMPTY_RANK_PRIVSES_ARRAY = new RankPrivs[0];
    // all these in milliseconds
    public static final long EXPELLED_MEMBER_PENALTY = AllSettingsConfig.clanPenaltyForClan * 60 * 1000L;
    public static final long LEAVED_ALLY_PENALTY = 24 * 60 * 60 * 1000L;
    public static final long DISSOLVED_ALLY_PENALTY = 24 * 60 * 60 * 1000L;
    public static final long DISBAND_TIME = 2 * 24 * 60 * 60 * 1000L;
    public static final long CHANGE_LEADER_TIME = 2 * 24 * 60 * 60 * 1000L;
    public static final long DISBAND_PENALTY = 7 * 24 * 60 * 60 * 1000L;
    // for player
    public static final long JOIN_PLEDGE_PENALTY = AllSettingsConfig.clanPenaltyForPlayer * 60 * 1000L;
    public static final long CREATE_PLEDGE_PENALTY = 10 * 24 * 60 * 60 * 1000L;
    //	Clan Privileges: system
    public static final int CP_NOTHING = 0;
    public static final int CP_CL_INVITE_CLAN = 2; // Join clan
    public static final int CP_CL_MANAGE_TITLES = 4; // Give a title
    public static final int CP_CL_WAREHOUSE_SEARCH = 8; // View warehouse content
    public static final int CP_CL_MANAGE_RANKS = 16; // manage clan ranks
    public static final int CP_CL_CLAN_WAR = 32;
    public static final int CP_CL_DISMISS = 64;
    public static final int CP_CL_EDIT_CREST = 128; // Edit clan crest
    public static final int CP_CL_APPRENTICE = 256;
    public static final int CP_CL_TROOPS_FAME = 512;
    public static final int CP_CL_SUMMON_AIRSHIP = 1024;
    //	Clan Privileges: clan hall
    public static final int CP_CH_ENTRY_EXIT = 2048; // open a door
    public static final int CP_CH_USE_FUNCTIONS = 4096;
    public static final int CP_CH_AUCTION = 8192;
    public static final int CP_CH_DISMISS = 16384; // Выгнать чужаков из КХ
    public static final int CP_CH_SET_FUNCTIONS = 32768;
    //	Clan Privileges: castle/fotress
    public static final int CP_CS_ENTRY_EXIT = 65536;
    public static final int CP_CS_MANOR_ADMIN = 131072;
    public static final int CP_CS_MANAGE_SIEGE = 262144;
    public static final int CP_CS_USE_FUNCTIONS = 524288;
    public static final int CP_CS_DISMISS = 1048576; // Выгнать чужаков из замка/форта
    public static final int CP_CS_TAXES = 2097152;
    public static final int CP_CS_MERCENARIES = 4194304;
    public static final int CP_CS_SET_FUNCTIONS = 8388606;
    public static final int CP_ALL = 16777214;
    public static final int RANK_FIRST = 1;
    public static final int RANK_LAST = 9;
    // Sub-unit types
    public static final int SUBUNIT_NONE = Byte.MIN_VALUE;
    public static final int SUBUNIT_ACADEMY = -1;
    public static final int SUBUNIT_MAIN_CLAN = 0;
    public static final int SUBUNIT_ROYAL1 = 100;
    public static final int SUBUNIT_ROYAL2 = 200;
    public static final int SUBUNIT_KNIGHT1 = 1001;
    public static final int SUBUNIT_KNIGHT2 = 1002;
    public static final int SUBUNIT_KNIGHT3 = 2001;
    public static final int SUBUNIT_KNIGHT4 = 2002;
    private static final Logger _log = LoggerFactory.getLogger(Clan.class);
    private static final ClanReputationComparator REPUTATION_COMPARATOR = new ClanReputationComparator();
    /**
     * Количество мест в таблице рангов кланов
     */
    private static final int REPUTATION_PLACES = 100;
    protected final Map<Integer, SkillEntry> skills = new ConcurrentSkipListMap<>();
    protected final Map<Integer, RankPrivs> privileges = new ConcurrentSkipListMap<>();
    protected final Map<Integer, SubUnit> subUnits = new ConcurrentSkipListMap<>();
    private final int clanId;
    private final ClanWarehouse _warehouse;
    private final List<Clan> _atWarWith = new ArrayList<>();
    private final List<Clan> _underAttackFrom = new ArrayList<>();
    private int _allyId;
    private int _level;
    private int _hasCastle;
    private int _castleDefendCount;
    private int _hasFortress;
    private int _hasHideout;
    private int _warDominion;
    private int _crestId;
    private int _crestLargeId;
    private long _expelledMemberTime;
    private long _leavedAllyTime;
    private long _dissolvedAllyTime;
    private ClanAirShip _airship;
    private boolean _airshipLicense;
    private int _airshipFuel;
    private int warehouseBonus = -1;
    private String _notice = null;
    private int _reputation = 0;

    /**
     * Конструктор используется только внутри для восстановления из базы
     */
    public Clan(final int clanId) {
        this.clanId = clanId;
        initializePrivs();
        _warehouse = new ClanWarehouse(this);
        _warehouse.restore();
    }

    public static Clan restore(final int clanId) {
        if (clanId == 0) // no clan
        {
            return null;
        }

        Clan clan = null;

        Connection con1 = null;
        PreparedStatement statement1 = null;
        ResultSet clanData = null;
        try {
            con1 = DatabaseFactory.getInstance().getConnection();
            statement1 = con1.prepareStatement("SELECT clan_level,hasCastle,hasFortress,hasHideout,ally_id,reputation_score,expelled_member,leaved_ally,dissolved_ally,warehouse,airship,castle_defend_count FROM clan_data where clan_id=?");
            statement1.setInt(1, clanId);
            clanData = statement1.executeQuery();

            if (clanData.next()) {
                clan = new Clan(clanId);
                clan.setLevel(clanData.getInt("clan_level"));
                clan.setHasCastle(clanData.getInt("hasCastle"));
                clan.setHasFortress(clanData.getInt("hasFortress"));
                clan.setHasHideout(clanData.getInt("hasHideout"));
                clan.setAllyId(clanData.getInt("ally_id"));
                clan._reputation = clanData.getInt("reputation_score");
                clan.setExpelledMemberTime(clanData.getLong("expelled_member") * 1000L);
                clan.setLeavedAllyTime(clanData.getLong("leaved_ally") * 1000L);
                clan.setDissolvedAllyTime(clanData.getLong("dissolved_ally") * 1000L);
                clan.setWarehouseBonus(clanData.getInt("warehouse"));
                clan.setCastleDefendCount(clanData.getInt("castle_defend_count"));
                clan.setAirshipLicense(clanData.getInt("airship") != -1);
                if (clan.isHaveAirshipLicense()) {
                    clan.setAirshipFuel(clanData.getInt("airship"));
                }
            } else {
                _log.warn("Clan " + clanId + " doesnt exists!");
                return null;
            }
        } catch (Exception e) {
            _log.error("Error while restoring clan!", e);
        } finally {
            DbUtils.closeQuietly(con1, statement1, clanData);
        }

        if (clan == null) {
            _log.warn("Clan " + clanId + " does't exist");
            return null;
        }

        clan.restoreSkills();
        clan.restoreSubPledges();

        for (final SubUnit unit : clan.getAllSubUnits()) {
            unit.restore();
            unit.restoreSkills();
            if (unit.getType() == SUBUNIT_MAIN_CLAN && unit.getNewLeaderObjectId() != 0 && unit.getLeaderObjectId() != 0) {
                final UnitMember newLeader = unit.getUnitMember(unit.getNewLeaderObjectId());
                unit.setLeader(newLeader, false);
                unit.setNewLeaderObjectId(0);
                Connection con = null;
                PreparedStatement statement = null;
                try {
                    con = DatabaseFactory.getInstance().getConnection();
                    statement = con.prepareStatement("UPDATE clan_subpledges SET leader_id=?, new_leader_id=? WHERE clan_id=? and type=?");
                    statement.setInt(1, unit.getLeaderObjectId());
                    statement.setInt(2, unit.getNewLeaderObjectId());
                    statement.setInt(3, clan.getClanId());
                    statement.setInt(4, unit.getType());
                    statement.execute();
                } catch (Exception e) {
                    _log.error("Exception: " + e, e);
                } finally {
                    DbUtils.closeQuietly(con, statement);
                }
            }
        }

        clan.restoreRankPrivs();
        clan.setCrestId(CrestCache.getInstance().getCrestId(CrestType.PLEDGE, clanId));
        clan.setCrestLargeId(CrestCache.getInstance().getCrestId(CrestType.PLEDGE_LARGE, clanId));

        return clan;
    }

    public static boolean isAcademy(final int pledgeType) {
        return pledgeType == SUBUNIT_ACADEMY;
    }

    public static boolean isRoyalGuard(final int pledgeType) {
        return pledgeType == SUBUNIT_ROYAL1 || pledgeType == SUBUNIT_ROYAL2;
    }

    public static boolean isOrderOfKnights(final int pledgeType) {
        return pledgeType == SUBUNIT_KNIGHT1 || pledgeType == SUBUNIT_KNIGHT2 || pledgeType == SUBUNIT_KNIGHT3 || pledgeType == SUBUNIT_KNIGHT4;
    }

    public int getClanId() {
        return clanId;
    }

    public int getLeaderId() {
        return getLeaderId(SUBUNIT_MAIN_CLAN);
    }

    public UnitMember getLeader() {
        return getLeader(SUBUNIT_MAIN_CLAN);
    }

    public String getLeaderName() {
        return getLeaderName(SUBUNIT_MAIN_CLAN);
    }

    public String getName() {
        return getUnitName(SUBUNIT_MAIN_CLAN);
    }

    public UnitMember getAnyMember(final int id) {
        for (final SubUnit unit : getAllSubUnits()) {
            final UnitMember m = unit.getUnitMember(id);
            if (m != null) {
                return m;
            }
        }
        return null;
    }

    public UnitMember getAnyMember(final String name) {
        for (final SubUnit unit : getAllSubUnits()) {
            final UnitMember m = unit.getUnitMember(name);
            if (m != null) {
                return m;
            }
        }
        return null;
    }

    public int getAllSize() {
        int size = 0;

        for (final SubUnit unit : getAllSubUnits()) {
            size += unit.size();
        }

        return size;
    }

    public String getUnitName(final int unitType) {
        if (unitType == SUBUNIT_NONE || !subUnits.containsKey(unitType)) {
            return StringUtils.EMPTY;
        }

        return getSubUnit(unitType).getName();
    }

    public String getLeaderName(final int unitType) {
        if (unitType == SUBUNIT_NONE || !subUnits.containsKey(unitType)) {
            return StringUtils.EMPTY;
        }

        return getSubUnit(unitType).getLeaderName();
    }

    public int getLeaderId(final int unitType) {
        if (unitType == SUBUNIT_NONE || !subUnits.containsKey(unitType)) {
            return 0;
        }

        return getSubUnit(unitType).getLeaderObjectId();
    }

    public UnitMember getLeader(final int unitType) {
        if (unitType == SUBUNIT_NONE || !subUnits.containsKey(unitType)) {
            return null;
        }

        return getSubUnit(unitType).getLeader();
    }

    public void flush() {
        for (final UnitMember member : this) {
            removeClanMember(member.getObjectId());
        }
        _warehouse.writeLock();
        try {
            for (final ItemInstance item : _warehouse.getItems()) {
                _warehouse.destroyItem(item);
            }
        } finally {
            _warehouse.writeUnlock();
        }
        if (_hasCastle != 0) {
            ResidenceHolder.getInstance().getResidence(Castle.class, _hasCastle).changeOwner(null);
        }
        if (_hasFortress != 0) {
            ResidenceHolder.getInstance().getResidence(Fortress.class, _hasFortress).changeOwner(null);
        }
    }

    public void removeClanMember(final int id) {
        if (id == getLeaderId(SUBUNIT_MAIN_CLAN)) {
            return;
        }

        for (final SubUnit unit : getAllSubUnits()) {
            if (unit.isUnitMember(id)) {
                removeClanMember(unit.getType(), id);
                break;
            }
        }
    }

    public void removeClanMember(final int subUnitId, final int objectId) {
        final SubUnit subUnit = getSubUnit(subUnitId);
        if (subUnit == null) {
            return;
        }

        subUnit.removeUnitMember(objectId);
    }

    public List<UnitMember> getAllMembers() {
        final Collection<SubUnit> units = getAllSubUnits();
        int size = 0;

        for (final SubUnit unit : units) {
            size += unit.size();
        }
        final List<UnitMember> members = new ArrayList<>(size);

        for (final SubUnit unit : units) {
            members.addAll(unit.getUnitMembers());
        }
        return members;
    }

    public List<Player> getOnlineMembers() {
        return getOnlineMembers(0);
    }

    public List<Player> getOnlineMembers(final int exclude) {
        final List<Player> result = new ArrayList<>(getAllSize() - 1);

        for (final UnitMember temp : this) {
            if (temp != null && temp.isOnline() && temp.getObjectId() != exclude) {
                result.add(temp.getPlayer());
            }
        }

        return result;
    }

    public int getAllyId() {
        return _allyId;
    }

    public void setAllyId(final int allyId) {
        _allyId = allyId;
    }

    public int getLevel() {
        return _level;
    }

    public void setLevel(final int level) {
        _level = level;
    }

    /**
     * Возвращает замок, которым владеет клан
     *
     * @return ID замка
     */
    public int getCastle() {
        return _hasCastle;
    }

    /**
     * Возвращает крепость, которой владеет клан
     *
     * @return ID крепости
     */
    public int getHasFortress() {
        return _hasFortress;
    }

    /**
     * Устанавливает крепость, которой владеет клан.<BR>
     * Одновременно владеть и крепостью и замком нельзя
     *
     * @param fortress ID крепости
     */
    public void setHasFortress(final int fortress) {
        if (_hasCastle == 0) {
            _hasFortress = fortress;
        }
    }

    /**
     * Возвращает кланхолл, которым владеет клан
     *
     * @return ID кланхолла
     */
    public int getHasHideout() {
        return _hasHideout;
    }

    public void setHasHideout(final int hasHideout) {
        _hasHideout = hasHideout;
    }

    public <R extends Residence> int getResidenceId(final Class<R> r) {
        if (r.equals(Castle.class)) {
            return _hasCastle;
        } else if (r.equals(Fortress.class)) {
            return _hasFortress;
        } else if (r.equals(ClanHall.class)) {
            return _hasHideout;
        } else {
            return 0;
        }
    }

    /**
     * Устанавливает замок, которым владеет клан.<BR>
     * Одновременно владеть и замком и крепостью нельзя
     *
     * @param castle ID замка
     */
    public void setHasCastle(final int castle) {
        if (_hasFortress == 0) {
            _hasCastle = castle;
        }
    }

    public boolean isAnyMember(final int id) {
        for (final SubUnit unit : getAllSubUnits()) {
            if (unit.isUnitMember(id)) {
                return true;
            }
        }
        return false;
    }

    public void updateClanInDB() {
        if (getLeaderId() == 0) {
            _log.warn("updateClanInDB with empty LeaderId");
            Thread.dumpStack();
            return;
        }

        if (getClanId() == 0) {
            _log.warn("updateClanInDB with empty ClanId");
            Thread.dumpStack();
            return;
        }
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(
                    "UPDATE clan_data SET ally_id=?,reputation_score=?,expelled_member=?,leaved_ally=?,dissolved_ally=?,clan_level=?,warehouse=?,airship=?,castle_defend_count=? WHERE clan_id=?");
            statement.setInt(1, getAllyId());
            statement.setInt(2, getReputationScore());
            statement.setLong(3, getExpelledMemberTime() / 1000);
            statement.setLong(4, getLeavedAllyTime() / 1000);
            statement.setLong(5, getDissolvedAllyTime() / 1000);
            statement.setInt(6, _level);
            statement.setInt(7, getWarehouseBonus());
            statement.setInt(8, isHaveAirshipLicense() ? getAirshipFuel() : -1);
            statement.setInt(9, getCastleDefendCount());
            statement.setInt(10, getClanId());
            statement.execute();
        } catch (Exception e) {
            _log.warn("error while updating clan '" + getClanId() + "' data in db");
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void store() {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(
                    "INSERT INTO clan_data (clan_id,clan_level,hasCastle,hasFortress,hasHideout,ally_id,expelled_member,leaved_ally,dissolved_ally,airship) values (?,?,?,?,?,?,?,?,?,?)");
            statement.setInt(1, clanId);
            statement.setInt(2, _level);
            statement.setInt(3, _hasCastle);
            statement.setInt(4, _hasFortress);
            statement.setInt(5, _hasHideout);
            statement.setInt(6, _allyId);
            statement.setLong(7, getExpelledMemberTime() / 1000);
            statement.setLong(8, getLeavedAllyTime() / 1000);
            statement.setLong(9, getDissolvedAllyTime() / 1000);
            statement.setInt(10, isHaveAirshipLicense() ? getAirshipFuel() : -1);
            statement.execute();
            DbUtils.close(statement);

            final SubUnit mainSubUnit = subUnits.get(SUBUNIT_MAIN_CLAN);

            statement = con.prepareStatement("INSERT INTO clan_subpledges (clan_id, type, leader_id, name) VALUES (?,?,?,?)");
            statement.setInt(1, clanId);
            statement.setInt(2, mainSubUnit.getType());
            statement.setInt(3, mainSubUnit.getLeaderObjectId());
            statement.setString(4, mainSubUnit.getName());
            statement.execute();
            DbUtils.close(statement);

            statement = con.prepareStatement("UPDATE characters SET clanid=?,pledge_type=? WHERE obj_Id=?");
            statement.setInt(1, getClanId());
            statement.setInt(2, mainSubUnit.getType());
            statement.setInt(3, getLeaderId());
            statement.execute();
        } catch (Exception e) {
            _log.warn("Exception: " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void broadcastToOnlineMembers(final IBroadcastPacket... packets) {
        for (final UnitMember member : this) {
            if (member.isOnline()) {
                member.getPlayer().sendPacket(packets);
            }
        }
    }

    public void broadcastToOnlineMembers(final L2GameServerPacket... packets) {
        for (final UnitMember member : this) {
            if (member.isOnline()) {
                member.getPlayer().sendPacket(packets);
            }
        }
    }

    public void broadcastToOtherOnlineMembers(final IBroadcastPacket packet, final Player player) {
        for (final UnitMember member : this) {
            if (member.isOnline() && member.getPlayer() != player) {
                member.getPlayer().sendPacket(packet);
            }
        }
    }

    @Override
    public String toString() {
        return getName();
    }

    public int getCrestId() {
        return _crestId;
    }

    public void setCrestId(final int newcrest) {
        _crestId = newcrest;
    }

    public boolean hasCrest() {
        return _crestId > 0;
    }

    public int getCrestLargeId() {
        return _crestLargeId;
    }

    public void setCrestLargeId(final int newcrest) {
        _crestLargeId = newcrest;
    }

    public boolean hasCrestLarge() {
        return _crestLargeId > 0;
    }

    public long getAdenaCount() {
        return _warehouse.getCountOfAdena();
    }

    public ClanWarehouse getWarehouse() {
        return _warehouse;
    }

    public int isAtWar() {
        if (_atWarWith != null && !_atWarWith.isEmpty()) {
            return 1;
        }
        return 0;
    }

    public int isAtWarOrUnderAttack() {
        if (_atWarWith != null && !_atWarWith.isEmpty() || _underAttackFrom != null && !_underAttackFrom.isEmpty()) {
            return 1;
        }
        return 0;
    }

    public boolean isAtWarWith(final int id) {
        final Clan clan = ClanTable.getInstance().getClan(id);
        if (_atWarWith != null && !_atWarWith.isEmpty()) {
            if (_atWarWith.contains(clan)) {
                return true;
            }
        }
        return false;
    }

    public boolean isUnderAttackFrom(final int id) {
        final Clan clan = ClanTable.getInstance().getClan(id);
        if (_underAttackFrom != null && !_underAttackFrom.isEmpty()) {
            if (_underAttackFrom.contains(clan)) {
                return true;
            }
        }
        return false;
    }

    public void setEnemyClan(final Clan clan) {
        _atWarWith.add(clan);
    }

    public void deleteEnemyClan(final Clan clan) {
        _atWarWith.remove(clan);
    }

    // clans that are attacking this clan
    public void setAttackerClan(final Clan clan) {
        _underAttackFrom.add(clan);
    }

    public void deleteAttackerClan(final Clan clan) {
        _underAttackFrom.remove(clan);
    }

    public List<Clan> getEnemyClans() {
        return _atWarWith;
    }

    public int getWarsCount() {
        return _atWarWith.size();
    }

    public List<Clan> getAttackerClans() {
        return _underAttackFrom;
    }

    public void broadcastClanStatus(final boolean updateList, final boolean needUserInfo, final boolean relation) {
        final List<L2GameServerPacket> listAll = updateList ? listAll() : null;
        final PledgeShowInfoUpdate update = new PledgeShowInfoUpdate(this);

        for (final UnitMember member : this) {
            if (member.isOnline()) {
                if (updateList) {
                    member.getPlayer().sendPacket(PledgeShowMemberListDeleteAll.STATIC);
                    member.getPlayer().sendPacket(listAll);
                }
                member.getPlayer().sendPacket(update);
                if (needUserInfo) {
                    member.getPlayer().broadcastCharInfo();
                }
                if (relation) {
                    member.getPlayer().broadcastRelationChanged();
                }
            }
        }
    }

    public Alliance getAlliance() {
        return _allyId == 0 ? null : ClanTable.getInstance().getAlliance(_allyId);
    }

    public long getExpelledMemberTime() {
        return _expelledMemberTime;
    }

    public void setExpelledMemberTime(final long time) {
        _expelledMemberTime = time;
    }

    public void setExpelledMember() {
        _expelledMemberTime = System.currentTimeMillis();
        updateClanInDB();
    }

    public long getLeavedAllyTime() {
        return _leavedAllyTime;
    }

    public void setLeavedAllyTime(final long time) {
        _leavedAllyTime = time;
    }

    public void setLeavedAlly() {
        _leavedAllyTime = System.currentTimeMillis();
        updateClanInDB();
    }

    public long getDissolvedAllyTime() {
        return _dissolvedAllyTime;
    }

    public void setDissolvedAllyTime(final long time) {
        _dissolvedAllyTime = time;
    }

    public void setDissolvedAlly() {
        _dissolvedAllyTime = System.currentTimeMillis();
        updateClanInDB();
    }

    public boolean canInvite() {
        return System.currentTimeMillis() - _expelledMemberTime >= EXPELLED_MEMBER_PENALTY;
    }

    public boolean canJoinAlly() {
        return System.currentTimeMillis() - _leavedAllyTime >= LEAVED_ALLY_PENALTY;
    }

    public boolean canCreateAlly() {
        return System.currentTimeMillis() - _dissolvedAllyTime >= DISSOLVED_ALLY_PENALTY;
    }

    public boolean canDisband() {
        return true;
    }

    public int getRank() {
        final Clan[] clans = ClanTable.getInstance().getClans();
        Arrays.parallelSort(clans, REPUTATION_COMPARATOR);

        final int place = 1;
        for (int i = 0; i < clans.length; i++) {
            if (i == REPUTATION_PLACES) {
                return 0;
            }

            final Clan clan = clans[i];
            if (clan == this) {
                return place + i;
            }
        }

        return 0;
    }

    /* ============================ clan skills stuff ============================ */

    public int getReputationScore() {
        return _reputation;
    }

    private void setReputationScore(final int rep) {
        if (_reputation >= 0 && rep < 0) {
            broadcastToOnlineMembers(SystemMsg.SINCE_THE_CLAN_REPUTATION_SCORE_HAS_DROPPED_TO_0_OR_LOWER_YOUR_CLAN_SKILLS_WILL_BE_DEACTIVATED);
            for (final UnitMember member : this) {
                if (member.isOnline() && member.getPlayer() != null) {
                    member.getPlayer().disableSkillsByEntryType(SkillEntryType.CLAN);
                }
            }
        } else if (_reputation < 0 && rep >= 0) {
            broadcastToOnlineMembers(SystemMsg.CLAN_SKILLS_WILL_NOW_BE_ACTIVATED_SINCE_THE_CLANS_REPUTATION_SCORE_IS_0_OR_HIGHER);
            for (final UnitMember member : this) {
                if (member.isOnline() && member.getPlayer() != null) {
                    member.getPlayer().enableSkillsByEntryType(SkillEntryType.CLAN);
                }
            }
        }

        if (_reputation != rep) {
            _reputation = rep;
            broadcastToOnlineMembers(new PledgeShowInfoUpdate(this));
        }

        updateClanInDB();
    }

    public int incReputation(int inc, final boolean rate, final String source) {
        if (_level < 5) {
            return 0;
        }

        if (rate && Math.abs(inc) <= ServerConfig.RATE_CLAN_REP_SCORE_MAX_AFFECTED) {
            inc = (int) Math.round(inc * ServerConfig.RATE_CLAN_REP_SCORE);
        }

        setReputationScore(_reputation + inc);
        Log.add(getName() + '|' + inc + '|' + _reputation + '|' + source, "clan_reputation");

        return inc;
    }

    public Collection<SkillEntry> getSkills() {
        return skills.values();
    }

    private void restoreSkills() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            // Retrieve all skills of this L2Player from the database
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT skill_id,skill_level FROM clan_skills WHERE clan_id=?");
            statement.setInt(1, getClanId());
            rset = statement.executeQuery();

            // Go though the recordset of this SQL query
            while (rset.next()) {
                final int id = rset.getInt("skill_id");
                final int level = rset.getInt("skill_level");
                // Create a L2Skill object for each record
                final SkillEntry skill = SkillTable.getInstance().getSkillEntry(id, level);
                // Add the L2Skill object to the L2Clan skills
                skills.put(skill.getId(), skill);
            }
        } catch (Exception e) {
            _log.warn("Could not restore clan skills: " + e);
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    /**
     * used to add a new skill to the list, send a packet to all online clan members, update their stats and store it in db
     */
    public SkillEntry addSkill(final SkillEntry newSkill, final boolean store) {
        SkillEntry oldSkill = null;
        if (newSkill != null) {
            // Replace oldSkill by newSkill or Add the newSkill
            oldSkill = skills.put(newSkill.getId(), newSkill);

            if (store) {
                Connection con = null;
                PreparedStatement statement = null;

                try {
                    con = DatabaseFactory.getInstance().getConnection();

                    if (oldSkill != null) {
                        statement = con.prepareStatement("UPDATE clan_skills SET skill_level=? WHERE skill_id=? AND clan_id=?");
                        statement.setInt(1, newSkill.getLevel());
                        statement.setInt(2, oldSkill.getId());
                        statement.setInt(3, getClanId());
                        statement.execute();
                    } else {
                        statement = con.prepareStatement("INSERT INTO clan_skills (clan_id,skill_id,skill_level) VALUES (?,?,?)");
                        statement.setInt(1, getClanId());
                        statement.setInt(2, newSkill.getId());
                        statement.setInt(3, newSkill.getLevel());
                        statement.execute();
                    }
                } catch (Exception e) {
                    _log.warn("Error could not store char skills: " + e);
                    _log.error("", e);
                } finally {
                    DbUtils.closeQuietly(con, statement);
                }
            }

            final PledgeSkillListAdd p = new PledgeSkillListAdd(newSkill.getId(), newSkill.getLevel());
            final PledgeSkillList p2 = new PledgeSkillList(this);
            for (final UnitMember temp : this) {
                if (temp.isOnline()) {
                    final Player player = temp.getPlayer();
                    if (player != null) {
                        addSkill(player, newSkill);
                        player.sendPacket(p, p2, new SkillList(player));
                    }
                }
            }
        }

        return oldSkill;
    }

    /* ============================ clan subpledges stuff ============================ */

    public void addSkillsQuietly(final Player player) {
        final SubUnit subUnit = getSubUnit(player.getPledgeType());
        if (subUnit == null) {
            return;
        }

        final Iterator<SkillEntry> iterator = Iterators.concat(skills.values().iterator(), subUnit.getSkills().iterator());
        while (iterator.hasNext()) {
            addSkill(player, iterator.next());
        }
    }

    protected void addSkill(final Player player, final SkillEntry skill) {
        if (skill.getTemplate().getMinRank() <= player.getPledgeClass()) {
            final SkillEntry entry = skill.copyTo(SkillEntryType.CLAN);
            entry.setDisabled(getReputationScore() < 0 || player.isInOlympiadMode());

            player.addSkill(entry);
        }
    }

    /**
     * Удаляет скилл у клана, без удаления из базы. Используется для удаления скилов резиденций.
     * После удаления скила(ов) необходимо разослать boarcastSkillListToOnlineMembers()
     *
     * @param skill
     */
    public void removeSkill(final int skill) {
        skills.remove(skill);
        final PledgeSkillListAdd p = new PledgeSkillListAdd(skill, 0);
        for (final UnitMember temp : this) {
            final Player player = temp.getPlayer();
            if (player != null && player.isOnline()) {
                player.removeSkillById(skill);
                player.sendPacket(p, new SkillList(player));
            }
        }
    }

    public int getAffiliationRank(final int pledgeType) {
        if (isAcademy(pledgeType)) {
            return 9;
        } else if (isOrderOfKnights(pledgeType)) {
            return 8;
        } else if (isRoyalGuard(pledgeType)) {
            return 7;
        } else {
            return 6;
        }
    }

    public final SubUnit getSubUnit(final int pledgeType) {
        return subUnits.get(pledgeType);
    }

    public final void addSubUnit(final SubUnit sp, final boolean updateDb) {
        subUnits.put(sp.getType(), sp);

        if (updateDb) {
            broadcastToOnlineMembers(new PledgeReceiveSubPledgeCreated(sp));

            Connection con = null;
            PreparedStatement statement = null;
            try {
                con = DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement("INSERT INTO `clan_subpledges` (clan_id,type,leader_id,name) VALUES (?,?,?,?)");
                statement.setInt(1, getClanId());
                statement.setInt(2, sp.getType());
                statement.setInt(3, sp.getLeaderObjectId());
                statement.setString(4, sp.getName());
                statement.execute();
            } catch (Exception e) {
                _log.warn("Could not store clan Sub pledges: " + e);
                _log.error("", e);
            } finally {
                DbUtils.closeQuietly(con, statement);
            }
        }
    }

    public int createSubPledge(final Player player, int pledgeType, final UnitMember leader, final String name) {
        final int temp = pledgeType;
        pledgeType = getAvailablePledgeTypes(pledgeType);

        if (pledgeType == SUBUNIT_NONE) {
            if (temp == SUBUNIT_ACADEMY) {
                player.sendPacket(SystemMsg.YOUR_CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY);
            } else {
                player.sendMessage("You can't create any more sub-units of this type");
            }
            return SUBUNIT_NONE;
        }

        switch (pledgeType) {
            case SUBUNIT_ACADEMY:
                break;
            case SUBUNIT_ROYAL1:
            case SUBUNIT_ROYAL2:
                if (getReputationScore() < 5000) {
                    player.sendPacket(SystemMsg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
                    return SUBUNIT_NONE;
                }
                incReputation(-5000, false, "SubunitCreate");
                break;
            case SUBUNIT_KNIGHT1:
            case SUBUNIT_KNIGHT2:
            case SUBUNIT_KNIGHT3:
            case SUBUNIT_KNIGHT4:
                if (getReputationScore() < 10000) {
                    player.sendPacket(SystemMsg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
                    return SUBUNIT_NONE;
                }
                incReputation(-10000, false, "SubunitCreate");
                break;
        }

        addSubUnit(new SubUnit(this, pledgeType, leader, name), true);
        return pledgeType;
    }

    public int getAvailablePledgeTypes(int pledgeType) {
        if (pledgeType == SUBUNIT_MAIN_CLAN) {
            return SUBUNIT_NONE;
        }

        if (subUnits.get(pledgeType) != null) {
            switch (pledgeType) {
                case SUBUNIT_ACADEMY:
                    return SUBUNIT_NONE;
                case SUBUNIT_ROYAL1:
                    pledgeType = getAvailablePledgeTypes(SUBUNIT_ROYAL2);
                    break;
                case SUBUNIT_ROYAL2:
                    return SUBUNIT_NONE;
                case SUBUNIT_KNIGHT1:
                    pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT2);
                    break;
                case SUBUNIT_KNIGHT2:
                    pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT3);
                    break;
                case SUBUNIT_KNIGHT3:
                    pledgeType = getAvailablePledgeTypes(SUBUNIT_KNIGHT4);
                    break;
                case SUBUNIT_KNIGHT4:
                    return SUBUNIT_NONE;
            }
        }
        return pledgeType;
    }

    private void restoreSubPledges() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM clan_subpledges WHERE clan_id=?");
            statement.setInt(1, getClanId());
            rset = statement.executeQuery();

            // Go though the recordset of this SQL query
            while (rset.next()) {
                final int type = rset.getInt("type");
                final int leaderId = rset.getInt("leader_id");
                final String name = rset.getString("name");
                final int newLeaderId = rset.getInt("new_leader_id");
                final SubUnit pledge = new SubUnit(this, type, leaderId, name, newLeaderId);
                addSubUnit(pledge, false);
            }
        } catch (Exception e) {
            _log.warn("Could not restore clan SubPledges: " + e, e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public int getSubPledgeLimit(final int pledgeType) {
        int limit;
        switch (_level) {
            case 0:
                limit = 10;
                break;
            case 1:
                limit = 15;
                break;
            case 2:
                limit = 20;
                break;
            case 3:
                limit = 30;
                break;
            default:
                limit = 40;
                break;
        }
        switch (pledgeType) {
            case SUBUNIT_ACADEMY:
            case SUBUNIT_ROYAL1:
            case SUBUNIT_ROYAL2:
                if (getLevel() >= 11) {
                    limit = 30;
                } else {
                    limit = 20;
                }
                break;
            case SUBUNIT_KNIGHT1:
            case SUBUNIT_KNIGHT2:
                if (getLevel() >= 9) {
                    limit = 25;
                } else {
                    limit = 10;
                }
                break;
            case SUBUNIT_KNIGHT3:
            case SUBUNIT_KNIGHT4:
                if (getLevel() >= 10) {
                    limit = 25;
                } else {
                    limit = 10;
                }
                break;
        }
        return limit;
    }

    public int getUnitMembersSize(final int pledgeType) {
        if (pledgeType == SUBUNIT_NONE || !subUnits.containsKey(pledgeType)) {
            return 0;
        }
        return getSubUnit(pledgeType).size();
    }

    /* ============================ clan privilege ranks stuff ============================ */

    public Map<Integer, RankPrivs> getPrivileges() {
        return privileges;
    }

    public void restoreRankPrivs() {
        if (privileges == null) {
            initializePrivs();
        }
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            // Retrieve all skills of this L2Player from the database
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT privilleges,rank FROM clan_privs WHERE clan_id=?");
            statement.setInt(1, getClanId());
            rset = statement.executeQuery();

            // Go though the recordset of this SQL query
            while (rset.next()) {
                final int rank = rset.getInt("rank");
                //int party = rset.getInt("party"); - unused?
                final int privileges = rset.getInt("privilleges");
                //noinspection ConstantConditions
                final RankPrivs p = this.privileges.get(rank);
                if (p != null) {
                    p.setPrivs(privileges);
                } else {
                    _log.warn("Invalid rank value (" + rank + "), please check clan_privs table");
                }
            }
        } catch (Exception e) {
            _log.warn("Could not restore clan privs by rank: " + e);
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void initializePrivs() {
        for (int i = RANK_FIRST; i <= RANK_LAST; i++) {
            privileges.put(i, new RankPrivs(i, 0, CP_NOTHING));
        }
    }

    public void updatePrivsForRank(final int rank) {
        for (final UnitMember member : this) {
            if (member.isOnline() && member.getPlayer() != null && member.getPlayer().getPowerGrade() == rank) {
                if (member.getPlayer().isClanLeader()) {
                    continue;
                }
                member.getPlayer().sendUserInfo();
            }
        }
    }

    public RankPrivs getRankPrivs(final int rank) {
        if (rank < RANK_FIRST || rank > RANK_LAST) {
            _log.warn("Requested invalid rank value: " + rank);
            Thread.dumpStack();
            return null;
        }
        if (privileges.get(rank) == null) {
            _log.warn("Request of rank before init: " + rank);
            Thread.dumpStack();
            setRankPrivs(rank, CP_NOTHING);
        }
        return privileges.get(rank);
    }

    public int countMembersByRank(final int rank) {
        int ret = 0;
        for (final UnitMember m : this) {
            if (m.getPowerGrade() == rank) {
                ret++;
            }
        }
        return ret;
    }

    public void setRankPrivs(final int rank, final int privs) {
        if (rank < RANK_FIRST || rank > RANK_LAST) {
            _log.warn("Requested set of invalid rank value: " + rank);
            Thread.dumpStack();
            return;
        }

        if (privileges.get(rank) != null) {
            privileges.get(rank).setPrivs(privs);
        } else {
            privileges.put(rank, new RankPrivs(rank, countMembersByRank(rank), privs));
        }
        Connection con = null;
        PreparedStatement statement = null;
        try {
            //LOGGER.warn("requested store clan privs in db for rank: " + rank + ", privs: " + privs);
            // Retrieve all skills of this L2Player from the database
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("REPLACE INTO clan_privs (clan_id,rank,privilleges) VALUES (?,?,?)");
            statement.setInt(1, getClanId());
            statement.setInt(2, rank);
            statement.setInt(3, privs);
            statement.execute();
        } catch (Exception e) {
            _log.warn("Could not store clan privs for rank: " + e);
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    /**
     * used to retrieve all privilege ranks
     */
    public final RankPrivs[] getAllRankPrivs() {
        if (privileges == null) {
            return EMPTY_RANK_PRIVSES_ARRAY;
        }
        return privileges.values().toArray(new RankPrivs[privileges.values().size()]);
    }

    public int getWarehouseBonus() {
        return warehouseBonus;
    }

    public void setWarehouseBonus(final int warehouseBonus) {
        if (this.warehouseBonus != -1)
            ClanDataDAO.getInstance().updateWarehouseBonus(getClanId(), warehouseBonus);

        this.warehouseBonus = warehouseBonus;
    }

    public void setAirshipLicense(final boolean val) {
        _airshipLicense = val;
    }

    public boolean isHaveAirshipLicense() {
        return _airshipLicense;
    }

    public ClanAirShip getAirship() {
        return _airship;
    }

    public void setAirship(final ClanAirShip airship) {
        _airship = airship;
    }

    public int getAirshipFuel() {
        return _airshipFuel;
    }

    public void setAirshipFuel(final int fuel) {
        _airshipFuel = fuel;
    }

    public final Collection<SubUnit> getAllSubUnits() {
        return subUnits.values();
    }

    public List<L2GameServerPacket> listAll() {
        return getAllSubUnits().stream().map(unit -> new PledgeShowMemberListAll(this, unit)).collect(Collectors.toList());
    }

    public String getNotice() {
        return _notice;
    }

    /**
     * Назначить новое сообщение
     */
    public void setNotice(final String notice) {
        _notice = notice;
    }

    public int getSkillLevel(final int id, final int def) {
        final SkillEntry skill = skills.get(id);
        return skill == null ? def : skill.getLevel();
    }

    public int getSkillLevel(final int id) {
        return getSkillLevel(id, -1);
    }

    public int getWarDominion() {
        return _warDominion;
    }

    public void setWarDominion(final int warDominion) {
        _warDominion = warDominion;
    }

    @Override
    public Iterator<UnitMember> iterator() {
        final List<UnitMember> units = new ArrayList<>(subUnits.size());

        for (final SubUnit subUnit : subUnits.values())
            units.addAll(subUnit.getUnitMembers());

        return Iterators.unmodifiableIterator(units.iterator());
    }

    public int getCastleDefendCount() {
        return _castleDefendCount;
    }

    public void setCastleDefendCount(final int castleDefendCount) {
        _castleDefendCount = castleDefendCount;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final Clan that = (Clan) o;

        if (clanId != that.clanId) {
            return false;
        }
        if (getLeaderId() != that.getLeaderId()) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return clanId;
    }

    private static class ClanReputationComparator implements Comparator<Clan>, Serializable {
        @Override
        public int compare(final Clan o1, final Clan o2) {
            if (o1 == null || o2 == null) {
                return 0;
            }
            return o2.getReputationScore() - o1.getReputationScore();
        }
    }
}