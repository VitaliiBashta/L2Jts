package org.mmocore.gameserver.manager.games;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author n0nam3
 * @date 08/08/2010 15:11
 */
public class FishingChampionShipManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(FishingChampionShipManager.class);
    private final List<String> _playersName = new ArrayList<>();
    private final List<String> _fishLength = new ArrayList<>();
    private final List<String> _winPlayersName = new ArrayList<>();
    private final List<String> _winFishLength = new ArrayList<>();
    private final List<Fisher> _tmpPlayers = new ArrayList<>();
    private final List<Fisher> _winPlayers = new ArrayList<>();
    private long _enddate = 0;
    private double _minFishLength = 0;
    private boolean _needRefresh = true;

    private FishingChampionShipManager() {
        restoreData();
        refreshWinResult();
        recalculateMinLength();
        if (_enddate <= System.currentTimeMillis()) {
            _enddate = System.currentTimeMillis();
            new finishChamp().run();
        } else {
            ThreadPoolManager.getInstance().schedule(new finishChamp(), _enddate - System.currentTimeMillis());
        }
    }

    public static FishingChampionShipManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private void setEndOfChamp() {
        final Calendar finishtime = Calendar.getInstance();
        finishtime.setTimeInMillis(_enddate);
        finishtime.set(Calendar.MINUTE, 0);
        finishtime.set(Calendar.SECOND, 0);
        finishtime.add(Calendar.DAY_OF_MONTH, 6);
        finishtime.set(Calendar.DAY_OF_WEEK, 3);
        finishtime.set(Calendar.HOUR_OF_DAY, 19);
        _enddate = finishtime.getTimeInMillis();
    }

    private void restoreData() {
        _enddate = ServerVariables.getLong("fishChampionshipEnd", 0);
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT `PlayerName`, `fishLength`, `rewarded` FROM fishing_championship");
            final ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                final int rewarded = rs.getInt("rewarded");
                if (rewarded == 0) // Текущий участник
                {
                    _tmpPlayers.add(new Fisher(rs.getString("PlayerName"), rs.getDouble("fishLength"), 0));
                }
                if (rewarded > 0) // Победитель прошлой недели
                {
                    _winPlayers.add(new Fisher(rs.getString("PlayerName"), rs.getDouble("fishLength"), rewarded));
                }
            }
            rs.close();
        } catch (SQLException e) {
            LOGGER.warn("Exception: can't get fishing championship info: " + e.getMessage());
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public synchronized void newFish(final Player pl, final int lureId) {
        if (!AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_ENABLED) {
            return;
        }
        double p1 = Rnd.get(60, 80);
        if (p1 < 90 && lureId > 8484 && lureId < 8486) {
            final long diff = Math.round(90 - p1);
            if (diff > 1) {
                p1 += Rnd.get(1, diff);
            }
        }
        final double len = Rnd.get(100, 999) / 1000. + p1;
        if (_tmpPlayers.size() < 5) {
            for (final Fisher fisher : _tmpPlayers) {
                if (fisher.getName().equalsIgnoreCase(pl.getName())) {
                    if (fisher.getLength() < len) {
                        fisher.setLength(len);
                        pl.sendMessage(new CustomMessage("org.mmocore.gameserver.instancemanager.games.FishingChampionShipManager.ResultImproveOn"));
                        recalculateMinLength();
                    }
                    return;
                }
            }
            _tmpPlayers.add(new Fisher(pl.getName(), len, 0));
            pl.sendMessage(new CustomMessage("org.mmocore.gameserver.instancemanager.games.FishingChampionShipManager.YouInAPrizeList"));
            recalculateMinLength();
        } else if (_minFishLength < len) {
            for (final Fisher fisher : _tmpPlayers) {
                if (fisher.getName().equalsIgnoreCase(pl.getName())) {
                    if (fisher.getLength() < len) {
                        fisher.setLength(len);
                        pl.sendMessage(new CustomMessage("org.mmocore.gameserver.instancemanager.games.FishingChampionShipManager.ResultImproveOn"));
                        recalculateMinLength();
                    }
                    return;
                }
            }
            Fisher minFisher = null;
            double minLen = 99999.;
            for (final Fisher fisher : _tmpPlayers) {
                if (fisher.getLength() < minLen) {
                    minFisher = fisher;
                    minLen = minFisher.getLength();
                }
            }
            _tmpPlayers.remove(minFisher);
            _tmpPlayers.add(new Fisher(pl.getName(), len, 0));
            pl.sendMessage(new CustomMessage("org.mmocore.gameserver.instancemanager.games.FishingChampionShipManager.YouInAPrizeList"));
            recalculateMinLength();
        }
    }

    private void recalculateMinLength() {
        double minLen = 99999.;
        for (final Fisher fisher : _tmpPlayers) {
            if (fisher.getLength() < minLen) {
                minLen = fisher.getLength();
            }
        }
        _minFishLength = minLen;
    }

    public long getTimeRemaining() {
        return (_enddate - System.currentTimeMillis()) / 60000;
    }

    public String getWinnerName(final int par) {
        if (_winPlayersName.size() >= par) {
            return _winPlayersName.get(par - 1);
        }
        return "—";
    }

    public String getCurrentName(final int par) {
        if (_playersName.size() >= par) {
            return _playersName.get(par - 1);
        }
        return "—";
    }

    public String getFishLength(final int par) {
        if (_winFishLength.size() >= par) {
            return _winFishLength.get(par - 1);
        }
        return "0";
    }

    public String getCurrentFishLength(final int par) {
        if (_fishLength.size() >= par) {
            return _fishLength.get(par - 1);
        }
        return "0";
    }

    public void getReward(final Player pl) {
        final String filename = "pts/fisherman/championship/getReward.htm";
        final HtmlMessage html = new HtmlMessage(pl.getObjectId());
        html.setFile(filename);
        pl.sendPacket(html);
        for (final Fisher fisher : _winPlayers) {
            if (fisher._name.equalsIgnoreCase(pl.getName())) {
                if (fisher.getRewardType() != 2) {
                    int rewardCnt = 0;
                    for (int x = 0; x < _winPlayersName.size(); x++) {
                        if (_winPlayersName.get(x).equalsIgnoreCase(pl.getName())) {
                            switch (x) {
                                case 0:
                                    rewardCnt = AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_1;
                                    break;
                                case 1:
                                    rewardCnt = AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_2;
                                    break;
                                case 2:
                                    rewardCnt = AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_3;
                                    break;
                                case 3:
                                    rewardCnt = AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_4;
                                    break;
                                case 4:
                                    rewardCnt = AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_5;
                                    break;
                            }
                        }
                    }
                    fisher.setRewardType(2);
                    if (rewardCnt > 0) {
                        final SystemMessage smsg = new SystemMessage(SystemMsg.YOU_HAVE_EARNED_S2_S1S)
                                .addItemName(AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM).addNumber(rewardCnt);
                        pl.sendPacket(smsg);
                        ItemFunctions.addItem(pl, AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM, rewardCnt);
                    }
                }
            }
        }
    }

    public void showMidResult(final Player pl) {
        if (_needRefresh) {
            refreshResult();
            ThreadPoolManager.getInstance().schedule(new needRefresh(), 60000);
        }
        final HtmlMessage html = new HtmlMessage(pl.getObjectId());
        final String filename = "pts/fisherman/championship/MidResult.htm";
        html.setFile(filename);
        String str = null;
        for (int x = 1; x <= 5; x++) {
            str += "<tr><td width=70 align=center>" + x + (pl.isLangRus() ? " Место:" : " Position:") + "</td>";
            str += "<td width=110 align=center>" + getCurrentName(x) + "</td>";
            str += "<td width=80 align=center>" + getCurrentFishLength(x) + "</td></tr>";
        }
        html.replace("%TABLE%", str);
        html.replace("%prizeItem%", ItemTemplateHolder.getInstance().getTemplate(AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM).getName());
        html.replace("%prizeFirst%", String.valueOf(AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_1));
        html.replace("%prizeTwo%", String.valueOf(AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_2));
        html.replace("%prizeThree%", String.valueOf(AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_3));
        html.replace("%prizeFour%", String.valueOf(AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_4));
        html.replace("%prizeFive%", String.valueOf(AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_5));
        pl.sendPacket(html);
    }

    public void showChampScreen(final Player pl, final NpcInstance npc) {
        final HtmlMessage html = new HtmlMessage(pl.getObjectId());
        final String filename = "pts/fisherman/championship/champScreen.htm";
        html.setFile(filename);
        String str = null;
        for (int x = 1; x <= 5; x++) {
            str += "<tr><td width=70 align=center>" + x + (pl.isLangRus() ? " Место:" : " Position:") + "</td>";
            str += "<td width=110 align=center>" + getWinnerName(x) + "</td>";
            str += "<td width=80 align=center>" + getFishLength(x) + "</td></tr>";
        }
        html.replace("%TABLE%", str);
        html.replace("%prizeItem%", ItemTemplateHolder.getInstance().getTemplate(AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_ITEM).getName());
        html.replace("%prizeFirst%", String.valueOf(AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_1));
        html.replace("%prizeTwo%", String.valueOf(AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_2));
        html.replace("%prizeThree%", String.valueOf(AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_3));
        html.replace("%prizeFour%", String.valueOf(AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_4));
        html.replace("%prizeFive%", String.valueOf(AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_REWARD_5));
        html.replace("%refresh%", String.valueOf(getTimeRemaining()));
        html.replace("%objectId%", String.valueOf(npc.getObjectId()));
        pl.sendPacket(html);
    }

    public void shutdown() {
        ServerVariables.set("fishChampionshipEnd", _enddate);
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM fishing_championship");
            statement.execute();
            statement.close();

            for (final Fisher fisher : _winPlayers) {
                statement = con.prepareStatement("INSERT INTO fishing_championship(PlayerName,fishLength,rewarded) VALUES (?,?,?)");
                statement.setString(1, fisher.getName());
                statement.setDouble(2, fisher.getLength());
                statement.setInt(3, fisher.getRewardType());
                statement.execute();
                statement.close();
            }
            for (final Fisher fisher : _tmpPlayers) {
                statement = con.prepareStatement("INSERT INTO fishing_championship(PlayerName,fishLength,rewarded) VALUES (?,?,?)");
                statement.setString(1, fisher.getName());
                statement.setDouble(2, fisher.getLength());
                statement.setInt(3, 0);
                statement.execute();
                statement.close();
            }
        } catch (SQLException e) {
            LOGGER.warn("Exception: can't update player vitality: " + e.getMessage());
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    private synchronized void refreshResult() {
        _needRefresh = false;
        _playersName.clear();
        _fishLength.clear();
        Fisher fisher1;
        Fisher fisher2;
        for (int x = 0; x <= _tmpPlayers.size() - 1; x++) {
            for (int y = 0; y <= _tmpPlayers.size() - 2; y++) {
                fisher1 = _tmpPlayers.get(y);
                fisher2 = _tmpPlayers.get(y + 1);
                if (fisher1.getLength() < fisher2.getLength()) {
                    _tmpPlayers.set(y, fisher2);
                    _tmpPlayers.set(y + 1, fisher1);
                }
            }
        }
        for (int x = 0; x <= _tmpPlayers.size() - 1; x++) {
            _playersName.add(_tmpPlayers.get(x)._name);
            _fishLength.add(String.valueOf(_tmpPlayers.get(x).getLength()));
        }
    }

    private void refreshWinResult() {
        _winPlayersName.clear();
        _winFishLength.clear();
        Fisher fisher1;
        Fisher fisher2;
        for (int x = 0; x <= _winPlayers.size() - 1; x++) {
            for (int y = 0; y <= _winPlayers.size() - 2; y++) {
                fisher1 = _winPlayers.get(y);
                fisher2 = _winPlayers.get(y + 1);
                if (fisher1.getLength() < fisher2.getLength()) {
                    _winPlayers.set(y, fisher2);
                    _winPlayers.set(y + 1, fisher1);
                }
            }
        }
        for (int x = 0; x <= _winPlayers.size() - 1; x++) {
            _winPlayersName.add(_winPlayers.get(x)._name);
            _winFishLength.add(String.valueOf(_winPlayers.get(x).getLength()));
        }
    }

    private static class Fisher {
        private double _length = 0.;
        private String _name;
        private int _reward = 0;

        public Fisher(final String name, final double length, final int rewardType) {
            setName(name);
            setLength(length);
            setRewardType(rewardType);
        }

        public String getName() {
            return _name;
        }

        public void setName(final String value) {
            _name = value;
        }

        public int getRewardType() {
            return _reward;
        }

        public void setRewardType(final int value) {
            _reward = value;
        }

        public double getLength() {
            return _length;
        }

        public void setLength(final double value) {
            _length = value;
        }
    }

    private static class LazyHolder {
        private static final FishingChampionShipManager INSTANCE = new FishingChampionShipManager();
    }

    private class finishChamp extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            _winPlayers.clear();
            for (final Fisher fisher : _tmpPlayers) {
                fisher.setRewardType(1);
                _winPlayers.add(fisher);
            }
            _tmpPlayers.clear();
            refreshWinResult();
            setEndOfChamp();
            shutdown();
            LOGGER.info("Fishing Championship Manager : start new event period.");
            ThreadPoolManager.getInstance().schedule(new finishChamp(), _enddate - System.currentTimeMillis());
        }
    }

    private class needRefresh extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            _needRefresh = true;
        }
    }
}