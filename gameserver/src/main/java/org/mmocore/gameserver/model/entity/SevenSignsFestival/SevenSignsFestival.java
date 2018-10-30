package org.mmocore.gameserver.model.entity.SevenSignsFestival;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PledgeShowInfoUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.PlayerUtils;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class SevenSignsFestival {
    public static final int FESTIVAL_MANAGER_START = 120000; // 2 mins
    public static final int FESTIVAL_LENGTH = 1080000; // 18 mins
    public static final int FESTIVAL_CYCLE_LENGTH = 2280000; // 38 mins
    public static final int FESTIVAL_SIGNUP_TIME = FESTIVAL_CYCLE_LENGTH - FESTIVAL_LENGTH;
    public static final int FESTIVAL_FIRST_SPAWN = 120000; // 2 mins
    public static final int FESTIVAL_FIRST_SWARM = 300000; // 5 mins
    public static final int FESTIVAL_SECOND_SPAWN = 540000; // 9 mins
    public static final int FESTIVAL_SECOND_SWARM = 720000; // 12 mins
    public static final int FESTIVAL_CHEST_SPAWN = 900000; // 15 mins
    public static final int FESTIVAL_COUNT = 5;
    public static final int FESTIVAL_LEVEL_MAX_31 = 0;
    public static final int FESTIVAL_LEVEL_MAX_42 = 1;
    public static final int FESTIVAL_LEVEL_MAX_53 = 2;
    public static final int FESTIVAL_LEVEL_MAX_64 = 3;
    public static final int FESTIVAL_LEVEL_MAX_NONE = 4;
    public static final int[] FESTIVAL_LEVEL_SCORES = {60, 70, 100, 120, 150}; // 500 maximum possible score
    public static final int FESTIVAL_BLOOD_OFFERING = 5901;
    public static final int FESTIVAL_OFFERING_VALUE = 1;
    private static final Logger _log = LoggerFactory.getLogger(SevenSignsFestival.class);
    private static final SevenSigns _signsInstance = SevenSigns.getInstance();
    private static SevenSignsFestival _instance;
    private static boolean _festivalInitialized;
    private static long[] _accumulatedBonuses; // The total bonus available (in Ancient Adena)
    private static Map<Integer, Long> _dawnFestivalScores, _duskFestivalScores;

    /**
     * _festivalData is essentially an instance of the seven_signs_festival table and
     * should be treated as such.
     * Data is initially accessed by the related Seven Signs cycle, with _signsCycle representing data for the current round of Festivals.
     * The actual table data is stored as a series of StatsSet constructs. These are accessed by the use of an offset based on the number of festivals, thus:
     * offset = FESTIVAL_COUNT + festivalId
     * (Data for Dawn is always accessed by offset > FESTIVAL_COUNT)
     */
    private final Map<Integer, Map<Integer, StatsSet>> _festivalData;

    public SevenSignsFestival() {
        _accumulatedBonuses = new long[FESTIVAL_COUNT];
        _dawnFestivalScores = new ConcurrentHashMap<>();
        _duskFestivalScores = new ConcurrentHashMap<>();
        _festivalData = new ConcurrentHashMap<>();
        restoreFestivalData();
    }

    public static SevenSignsFestival getInstance() {
        if (_instance == null) {
            _instance = new SevenSignsFestival();
        }
        return _instance;
    }

    /**
     * Returns the associated name (level range) to a given festival ID.
     *
     * @param int festivalID
     * @return String festivalName
     */
    public static String getFestivalName(final int festivalID) {
        switch (festivalID) {
            case FESTIVAL_LEVEL_MAX_31:
                return "31";
            case FESTIVAL_LEVEL_MAX_42:
                return "42";
            case FESTIVAL_LEVEL_MAX_53:
                return "53";
            case FESTIVAL_LEVEL_MAX_64:
                return "64";
            default:
                return "No Level Limit";
        }
    }

    /**
     * Returns the maximum allowed player level for the given festival type.
     *
     * @param festivalId
     * @return int maxLevel
     */
    public static int getMaxLevelForFestival(final int festivalId) {
        switch (festivalId) {
            case FESTIVAL_LEVEL_MAX_31:
                return 31;
            case FESTIVAL_LEVEL_MAX_42:
                return 42;
            case FESTIVAL_LEVEL_MAX_53:
                return 53;
            case FESTIVAL_LEVEL_MAX_64:
                return 64;
            default:
                return PlayerUtils.getMaxLevel();
        }
    }

    public static int getStoneCount(final int festivalId, final int stoneId) {
        switch (festivalId) {
            case FESTIVAL_LEVEL_MAX_31:
                if (stoneId == 6360) {
                    return 900;
                } else if (stoneId == 6361) {
                    return 520;
                } else {
                    return 270;
                }
            case FESTIVAL_LEVEL_MAX_42:
                if (stoneId == 6360) {
                    return 1500;
                } else if (stoneId == 6361) {
                    return 900;
                } else {
                    return 450;
                }
            case FESTIVAL_LEVEL_MAX_53:
                if (stoneId == 6360) {
                    return 3000;
                } else if (stoneId == 6361) {
                    return 1500;
                } else {
                    return 900;
                }
            case FESTIVAL_LEVEL_MAX_64:
                if (stoneId == 6360) {
                    return 1500;
                } else if (stoneId == 6361) {
                    return 2700;
                } else {
                    return 1350;
                }
            case FESTIVAL_LEVEL_MAX_NONE:
                if (stoneId == 6360) {
                    return 6000;
                } else if (stoneId == 6361) {
                    return 3600;
                } else {
                    return 1800;
                }
        }

        return 0;
    }

    public static String implodeString(final List<?> strArray) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strArray.size(); ) {
            final Object o = strArray.get(i);
            if (o instanceof Player) {
                sb.append(((Player) o).getName());
            } else {
                sb.append(o);
            }
            if (++i < strArray.size()) {
                sb.append(',');
            }
        }
        return sb.toString();
    }

    /**
     * Restores saved festival data, basic settings from the properties file
     * and past high score data from the database.
     */
    private void restoreFestivalData() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT festivalId, cabal, cycle, date, score, members, names FROM seven_signs_festival");
            rset = statement.executeQuery();
            while (rset.next()) {
                final int cycle = _signsInstance.getCurrentCycle();
                int festivalId = rset.getInt("festivalId");
                final int cabal = SevenSigns.getCabalNumber(rset.getString("cabal"));
                final StatsSet festivalDat = new StatsSet();
                festivalDat.set("festivalId", festivalId);
                festivalDat.set("cabal", cabal);
                festivalDat.set("cycle", cycle);
                festivalDat.set("date", rset.getString("date"));
                festivalDat.set("score", rset.getInt("score"));
                festivalDat.set("members", rset.getString("members"));
                festivalDat.set("names", rset.getString("names"));
                if (cabal == SevenSigns.CABAL_DAWN) {
                    festivalId += FESTIVAL_COUNT;
                }
                Map<Integer, StatsSet> tempData = _festivalData.get(cycle);
                if (tempData == null) {
                    tempData = new TreeMap<>();
                }
                tempData.put(festivalId, festivalDat);
                _festivalData.put(cycle, tempData);
            }
            DbUtils.close(statement, rset);

            final StringBuilder query = new StringBuilder("SELECT festival_cycle, ");
            for (int i = 0; i < FESTIVAL_COUNT - 1; i++) {
                query.append("accumulated_bonus").append(i).append(", ");
            }
            query.append("accumulated_bonus").append((FESTIVAL_COUNT - 1)).append(' ');
            query.append("FROM seven_signs_status");

            statement = con.prepareStatement(query.toString());
            rset = statement.executeQuery();
            while (rset.next()) {
                for (int i = 0; i < FESTIVAL_COUNT; i++) {
                    _accumulatedBonuses[i] = rset.getInt("accumulated_bonus" + i);
                }
            }
        } catch (SQLException e) {
            _log.error("SevenSignsFestival: Failed to load configuration: " + e);
        } finally {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    /**
     * Stores current festival data, basic settings to the properties file
     * and past high score data to the database.
     *
     * @param updateSettings
     * @throws Exception
     */
    public synchronized void saveFestivalData(final boolean updateSettings) {
        Connection con = null;
        PreparedStatement statement = null;
        PreparedStatement statement2 = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE seven_signs_festival SET date=?, score=?, members=?, names=? WHERE cycle=? AND cabal=? AND festivalId=?");
            statement2 = con.prepareStatement("INSERT INTO seven_signs_festival (festivalId, cabal, cycle, date, score, members, names) VALUES (?,?,?,?,?,?,?)");
            for (final Map<Integer, StatsSet> currCycleData : _festivalData.values()) {
                for (final StatsSet festivalDat : currCycleData.values()) {
                    final int festivalCycle = festivalDat.getInteger("cycle");
                    final int festivalId = festivalDat.getInteger("festivalId");
                    final String cabal = SevenSigns.getCabalShortName(festivalDat.getInteger("cabal"));
                    // Try to update an existing record.
                    statement.setLong(1, Long.parseLong(festivalDat.getString("date")));
                    statement.setInt(2, festivalDat.getInteger("score"));
                    statement.setString(3, festivalDat.getString("members"));
                    statement.setString(4, festivalDat.getString("names", StringUtils.EMPTY));
                    statement.setInt(5, festivalCycle);
                    statement.setString(6, cabal);
                    statement.setInt(7, festivalId);
                    final boolean update = statement.executeUpdate() > 0;

                    // If there was no record to update, assume it doesn't exist and add a new one, otherwise continue with the next record to store.
                    if (update) {
                        continue;
                    }

                    statement2.setInt(1, festivalId);
                    statement2.setString(2, cabal);
                    statement2.setInt(3, festivalCycle);
                    statement2.setLong(4, Long.parseLong(festivalDat.getString("date")));
                    statement2.setInt(5, festivalDat.getInteger("score"));
                    statement2.setString(6, festivalDat.getString("members"));
                    statement2.setString(7, festivalDat.getString("names", StringUtils.EMPTY));
                    statement2.execute();
                }
            }
        } catch (Exception e) {
            _log.error("SevenSignsFestival: Failed to save configuration!", e);
        } finally {
            DbUtils.closeQuietly(statement2);
            DbUtils.closeQuietly(con, statement);
        }
        // Updates Seven Signs DB data also, so call only if really necessary.
        if (updateSettings) {
            _signsInstance.saveSevenSignsData(0, true);
        }
    }

    /**
     * If a clan member is a member of the highest-ranked party in the Festival of Darkness, 100 points are added per member
     */
    public void rewardHighestRanked() {
        String[] partyMembers;
        for (int i = 0; i < FESTIVAL_COUNT; i++) {
            final StatsSet overallData = getOverallHighestScoreData(i);
            if (overallData != null) {
                partyMembers = overallData.getString("members").split(",");
                for (final String partyMemberId : partyMembers) {
                    addReputationPointsForPartyMemberClan(partyMemberId);
                }
            }
        }
    }

    private void addReputationPointsForPartyMemberClan(final String playerId) {
        final Player player = GameObjectsStorage.getPlayer(Integer.parseInt(playerId));
        if (player != null) {
            if (player.getClan() != null) {
                player.getClan().incReputation(100, true, "SevenSignsFestival");
                final SystemMessage sm = new SystemMessage(SystemMsg.CLAN_MEMBER_S1_WAS_AN_ACTIVE_MEMBER_OF_THE_HIGHEST_RANKED_PARTY_IN_THE_FESTIVAL_OF_DARKNESS_S2_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE);
                sm.addName(player);
                sm.addNumber(100);
                player.getClan().broadcastToOnlineMembers(sm);
            }
        } else {
            Connection con = null;
            PreparedStatement statement = null;
            ResultSet rset = null;
            try {
                con = DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement("SELECT char_name, clanid FROM characters WHERE obj_Id = ?");
                statement.setString(1, playerId);
                rset = statement.executeQuery();
                if (rset.next()) {
                    final int clanId = rset.getInt("clanid");
                    if (clanId > 0) {
                        final Clan clan = ClanTable.getInstance().getClan(clanId);
                        if (clan != null) {
                            clan.incReputation(100, true, "SevenSignsFestival");
                            clan.broadcastToOnlineMembers(new PledgeShowInfoUpdate(clan));
                            final SystemMessage sm = new SystemMessage(SystemMsg.CLAN_MEMBER_S1_WAS_AN_ACTIVE_MEMBER_OF_THE_HIGHEST_RANKED_PARTY_IN_THE_FESTIVAL_OF_DARKNESS_S2_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION_SCORE);
                            sm.addString(rset.getString("char_name"));
                            sm.addNumber(100);
                            clan.broadcastToOnlineMembers(sm);
                        }
                    }
                }
            } catch (Exception e) {
                _log.warn("could not get clan name of " + playerId + ": " + e);
                _log.error("", e);
            } finally {
                DbUtils.closeQuietly(con, statement, rset);
            }
        }
    }

    /**
     * Раздает призы за текущий период и очищает всю информацию для нового.
     */
    public void resetFestivalData(final boolean updateSettings) {
        // Set all accumulated bonuses back to 0.
        for (int i = 0; i < FESTIVAL_COUNT; i++) {
            _accumulatedBonuses[i] = 0;
        }
        _dawnFestivalScores.clear();
        _duskFestivalScores.clear();
        // Set up a new data set for the current cycle of festivals
        final Map<Integer, StatsSet> newData = new TreeMap<>();
        for (int i = 0; i < FESTIVAL_COUNT * 2; i++) {
            int festivalId = i;
            if (i >= FESTIVAL_COUNT) {
                festivalId -= FESTIVAL_COUNT;
            }
            // Create a new StatsSet with "default" data for Dusk
            final StatsSet tempStats = new StatsSet();
            tempStats.set("festivalId", festivalId);
            tempStats.set("cycle", _signsInstance.getCurrentCycle());
            tempStats.set("date", "0");
            tempStats.set("score", 0);
            tempStats.set("members", "");
            if (i >= FESTIVAL_COUNT) {
                tempStats.set("cabal", SevenSigns.CABAL_DAWN);
            } else {
                tempStats.set("cabal", SevenSigns.CABAL_DUSK);
            }
            newData.put(i, tempStats);
        }
        // Add the newly created cycle data to the existing festival data, and subsequently save it to the database.
        _festivalData.put(_signsInstance.getCurrentCycle(), newData);
        saveFestivalData(updateSettings);
        // Remove any unused blood offerings from online players.
        for (final Player onlinePlayer : GameObjectsStorage.getPlayers()) {
            ItemFunctions.removeItem(onlinePlayer, FESTIVAL_BLOOD_OFFERING, ItemFunctions.getItemCount(onlinePlayer, FESTIVAL_BLOOD_OFFERING));
        }
        _log.info("SevenSignsFestival: Reinitialized engine for next competition period.");
    }

    public boolean isFestivalInitialized() {
        return _festivalInitialized;
    }

    public static void setFestivalInitialized(final boolean isInitialized) {
        _festivalInitialized = isInitialized;
    }

    public String getTimeToNextFestivalStr() {
        if (_signsInstance.isSealValidationPeriod()) {
            return "<font color=\"FF0000\">This is the Seal Validation period. Festivals will resume next week.</font>";
        }
        return "<font color=\"FF0000\">The next festival is ready to start.</font>";
    }

    public long getHighestScore(final int oracle, final int festivalId) {
        return getHighestScoreData(oracle, festivalId).getLong("score");
    }

    /**
     * Returns a stats set containing the highest score <b>this cycle</b> for the
     * the specified cabal and associated festival ID.
     *
     * @param oracle
     * @param festivalId
     * @return StatsSet festivalDat
     */
    public StatsSet getHighestScoreData(final int oracle, final int festivalId) {
        int offsetId = festivalId;
        if (oracle == SevenSigns.CABAL_DAWN) {
            offsetId += 5;
        }
        // Attempt to retrieve existing score data (if found), otherwise create a new blank data set and display a console warning.
        StatsSet currData = null;
        try {
            currData = _festivalData.get(_signsInstance.getCurrentCycle()).get(offsetId);
        } catch (Exception e) {
            _log.info("SSF: Error while getting scores");
            _log.info("oracle=" + oracle + " festivalId=" + festivalId + " offsetId" + offsetId + " _signsCycle" + _signsInstance.getCurrentCycle());
            _log.info("_festivalData=" + _festivalData);
            _log.error("", e);
        }
        if (currData == null) {
            currData = new StatsSet();
            currData.set("score", 0);
            currData.set("members", "");
            _log.warn("SevenSignsFestival: Data missing for " + SevenSigns.getCabalName(oracle) + ", FestivalID = " + festivalId + " (Current Cycle " + _signsInstance.getCurrentCycle() + ')');
        }
        return currData;
    }

    /**
     * Returns a stats set containing the highest ever recorded
     * score data for the specified festival.
     *
     * @param festivalId
     * @return StatsSet result
     */
    public StatsSet getOverallHighestScoreData(final int festivalId) {
        StatsSet result = null;
        int highestScore = 0;
        for (final Map<Integer, StatsSet> currCycleData : _festivalData.values()) {
            for (final StatsSet currFestData : currCycleData.values()) {
                final int currFestID = currFestData.getInteger("festivalId");
                final int festivalScore = currFestData.getInteger("score");
                if (currFestID != festivalId) {
                    continue;
                }
                if (festivalScore > highestScore) {
                    highestScore = festivalScore;
                    result = currFestData;
                }
            }
        }
        return result;
    }

    /**
     * Set the final score details for the last participants of the specified festival data.
     * Returns <b>true</b> if the score is higher than that previously recorded <b>this cycle</b>.
     *
     * @param player
     * @param oracle
     * @param festivalId
     * @param offeringScore
     * @return boolean isHighestScore
     */
    public boolean setFinalScore(final Party party, final int oracle, final int festivalId, final long offeringScore) {
        final List<Integer> partyMemberIds = party.getPartyMembersObjIds();
        final List<Player> partyMembers = party.getPartyMembers();
        final long currDawnHighScore = getHighestScore(SevenSigns.CABAL_DAWN, festivalId);
        final long currDuskHighScore = getHighestScore(SevenSigns.CABAL_DUSK, festivalId);
        long thisCabalHighScore = 0;
        long otherCabalHighScore = 0;
        if (oracle == SevenSigns.CABAL_DAWN) {
            thisCabalHighScore = currDawnHighScore;
            otherCabalHighScore = currDuskHighScore;
            _dawnFestivalScores.put(festivalId, offeringScore);
        } else {
            thisCabalHighScore = currDuskHighScore;
            otherCabalHighScore = currDawnHighScore;
            _duskFestivalScores.put(festivalId, offeringScore);
        }
        final StatsSet currFestData = getHighestScoreData(oracle, festivalId);
        // Check if this is the highest score for this level range so far for the player's cabal.
        if (offeringScore > thisCabalHighScore) {
            // If the current score is greater than that for the other cabal, then they already have the points from this festival.
            //if(thisCabalHighScore > otherCabalHighScore)
            //	return false;
            // Update the highest scores and party list.
            currFestData.set("date", String.valueOf(System.currentTimeMillis()));
            currFestData.set("score", offeringScore);
            currFestData.set("members", implodeString(partyMemberIds));
            currFestData.set("names", implodeString(partyMembers));
            // Only add the score to the cabal's overall if it's higher than the other cabal's score.
            if (offeringScore > otherCabalHighScore) {
                _signsInstance.updateFestivalScore();
            }
            saveFestivalData(true);
            return true;
        }
        return false;
    }

    public long getAccumulatedBonus(final int festivalId) {
        return _accumulatedBonuses[festivalId];
    }

    public void addAccumulatedBonus(final int festivalId, final int stoneType, final long stoneAmount) {
        int eachStoneBonus = 0;
        switch (stoneType) {
            case SevenSigns.SEAL_STONE_BLUE_ID:
                eachStoneBonus = SevenSigns.SEAL_STONE_BLUE_VALUE;
                break;
            case SevenSigns.SEAL_STONE_GREEN_ID:
                eachStoneBonus = SevenSigns.SEAL_STONE_GREEN_VALUE;
                break;
            case SevenSigns.SEAL_STONE_RED_ID:
                eachStoneBonus = SevenSigns.SEAL_STONE_RED_VALUE;
                break;
        }
        _accumulatedBonuses[festivalId] += stoneAmount * eachStoneBonus;
    }

    /**
     * Calculate and return the proportion of the accumulated bonus for the festival
     * where the player was in the winning party, if the winning party's cabal won the event.
     * The accumulated bonus is then updated, with the player's share deducted.
     *
     * @param player
     * @return playerBonus (the share of the bonus for the party)
     */
    public void distribAccumulatedBonus() {
        final long[][] result = new long[FESTIVAL_COUNT][];
        long draw_count = 0;
        long draw_score = 0;

        // предварительный подсчет, определение ничьих
        for (int i = 0; i < FESTIVAL_COUNT; i++) {
            final long dawnHigh = getHighestScore(SevenSigns.CABAL_DAWN, i);
            final long duskHigh = getHighestScore(SevenSigns.CABAL_DUSK, i);
            if (dawnHigh > duskHigh) {
                result[i] = new long[]{SevenSigns.CABAL_DAWN, dawnHigh};
            } else if (duskHigh > dawnHigh) {
                result[i] = new long[]{SevenSigns.CABAL_DUSK, duskHigh};
            } else {
                result[i] = new long[]{SevenSigns.CABAL_NULL, dawnHigh};
                draw_count++;
                draw_score += _accumulatedBonuses[i];
            }
        }

        for (int i = 0; i < FESTIVAL_COUNT; i++) {
            if (result[i][0] != SevenSigns.CABAL_NULL) {
                final StatsSet high = getHighestScoreData((int) result[i][0], i);
                final String membersString = high.getString("members");
                final long add = draw_count > 0 ? draw_score / draw_count : 0;
                final String[] members = membersString.split(",");
                final long count = (_accumulatedBonuses[i] + add) / members.length;
                for (final String pIdStr : members) {
                    SevenSigns.getInstance().addPlayerStoneContrib(Integer.parseInt(pIdStr), 0, 0, count / 10);
                }
            }
        }
    }
}