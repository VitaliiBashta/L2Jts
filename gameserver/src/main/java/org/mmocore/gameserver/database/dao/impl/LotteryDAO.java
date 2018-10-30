package org.mmocore.gameserver.database.dao.impl;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.mmocore.commons.jdbchelper.ResultSetHandler;
import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;
import org.mmocore.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Java-man
 */
public class LotteryDAO extends AbstractGameServerDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(LotteryDAO.class);
    private static final LotteryDAO INSTANCE = new LotteryDAO();
    private static final String INSERT_LOTTERY = "INSERT INTO games(id, idnr, enddate, prize, newprize) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_PRICE = "UPDATE games SET prize=?, newprize=? WHERE id = 1 AND idnr = ?";
    private static final String UPDATE_LOTTERY = "UPDATE games SET finished=1, prize=?, newprize=?, number1=?, number2=?, prize1=?, prize2=?, prize3=? WHERE id=1 AND idnr=?";
    private static final String SELECT_LAST_LOTTERY = "SELECT idnr, prize, newprize, enddate, finished FROM games WHERE id = 1 ORDER BY idnr DESC LIMIT 1";
    private static final String SELECT_LOTTERY_ITEM = "SELECT enchant_level, custom_type2 FROM items WHERE item_id = 4442 AND custom_type1 = ?";
    private static final String SELECT_LOTTERY_TICKET = "SELECT number1, number2, prize1, prize2, prize3 FROM games WHERE id = 1 AND idnr = ?";

    private LotteryDAO() {
    }

    public static LotteryDAO getInstance() {
        return INSTANCE;
    }

    public StatsSet restoreLotteryData() {
        final StatsSet result = new StatsSet();
        jdbcHelper.query(SELECT_LAST_LOTTERY, new ResultSetHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                result.set("idnr", rs.getInt("idnr"));
                result.set("finished", rs.getInt("finished"));
                result.set("newprize", rs.getInt("newprize"));
                result.set("prize", rs.getInt("prize"));
                result.set("enddate", rs.getLong("enddate"));
            }
        });
        return result;
    }

    public void createNewLottery(final int lotteryId, final long endTimestamp, final int prize) {
        jdbcHelper.execute(INSERT_LOTTERY, stmt -> {
            stmt.setInt(1, 1);
            stmt.setInt(2, lotteryId);
            stmt.setLong(3, endTimestamp);
            stmt.setInt(4, prize);
            stmt.setInt(5, prize);
        });
    }

    public void updateLottery(final int lotteryId, final int prize, final int newPrize, final int prize1, final int prize2, final int prize3, final int enchant, final int type2) {
        jdbcHelper.execute(UPDATE_LOTTERY, stmt -> {
            stmt.setInt(1, prize);
            stmt.setInt(2, newPrize);
            stmt.setInt(3, enchant);
            stmt.setInt(4, type2);
            stmt.setInt(5, prize1);
            stmt.setInt(6, prize2);
            stmt.setInt(7, prize3);
            stmt.setInt(8, lotteryId);
        });
    }

    public StatsSet selectLotteryTicket(final int id) {
        final StatsSet result = new StatsSet();
        jdbcHelper.query(SELECT_LOTTERY_TICKET, new ResultSetHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                result.set("number1", rs.getInt("number1"));
                result.set("number2", rs.getInt("number2"));
                result.set("prize1", rs.getInt("prize1"));
                result.set("prize2", rs.getInt("prize2"));
                result.set("prize3", rs.getInt("prize3"));
            }
        }, stmt -> stmt.setInt(1, id));
        return result;
    }

    public List<ImmutablePair<Integer, Integer>> selectLotteryItems(final int lotteryId) {
        final List<ImmutablePair<Integer, Integer>> result = new ArrayList<>();
        jdbcHelper.query(SELECT_LOTTERY_ITEM, new ResultSetHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                result.add(new ImmutablePair<>(rs.getInt("enchant_level"), rs.getInt("custom_type2")));
            }
        }, stmt -> stmt.setInt(1, lotteryId));
        return result;
    }

    public void updatePrize(final int lotteryId, final int newPrize) {
        jdbcHelper.execute(UPDATE_PRICE, stmt ->
        {
            stmt.setInt(1, newPrize);
            stmt.setInt(2, newPrize);
            stmt.setInt(3, lotteryId);
        });
    }
}
