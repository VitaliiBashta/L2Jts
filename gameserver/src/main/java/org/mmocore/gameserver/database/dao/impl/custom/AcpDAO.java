package org.mmocore.gameserver.database.dao.impl.custom;

import org.mmocore.commons.jdbchelper.ResultSetHandler;
import org.mmocore.gameserver.database.dao.AbstractGameServerDAO;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.custom.AcpTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Create by Mangol on 30.12.2015.
 */
public class AcpDAO extends AbstractGameServerDAO {
    private static final AcpDAO INSTANCE = new AcpDAO();
    private static final String SQL_SELECT_ACP = "SELECT * FROM custom_acp WHERE player_obj_id=?";
    private static final String SQL_SELECT_ACP_COUNT = "SELECT COUNT(*) FROM custom_acp WHERE player_obj_id=?";
    private static final String SQL_INSERT_ACP = "INSERT INTO custom_acp (player_obj_id, auto_cp, cp_percent, cp_item_id, reuse_cp, auto_small_cp, small_cp_percent, small_cp_item_id, reuse_small_cp, auto_hp, hp_percent, hp_item_id, reuse_hp, auto_mp, mp_percent, mp_item_id, reuse_mp) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String SQL_UPDATE_ACP = "UPDATE custom_acp SET auto_cp=?, cp_percent=?, cp_item_id=?, reuse_cp=?, auto_small_cp=?, small_cp_percent=?, small_cp_item_id=?, reuse_small_cp=?, auto_hp=?, hp_percent=?, hp_item_id=?, reuse_hp=?, auto_mp=?, mp_percent=?, mp_item_id=?, reuse_mp=? WHERE player_obj_id=?";

    public static AcpDAO getInstance() {
        return INSTANCE;
    }

    public AcpTemplate selectAcp(final Player player) {
        final AcpTemplate[] template = new AcpTemplate[1];
        jdbcHelper.query(SQL_SELECT_ACP, new ResultSetHandler() {
            @Override
            public void processRow(final ResultSet rs) throws SQLException {
                final boolean autoCp = rs.getBoolean("auto_cp");
                final double cpPercent = rs.getDouble("cp_percent");
                final int cpItemId = rs.getInt("cp_item_id");
                final int reuseCp = rs.getInt("reuse_cp");
                final boolean autoSmallCp = rs.getBoolean("auto_small_cp");
                final double smallCpPercent = rs.getDouble("small_cp_percent");
                final int smallCpItemId = rs.getInt("small_cp_item_id");
                final int reuseSmallCp = rs.getInt("reuse_small_cp");
                final boolean autoHp = rs.getBoolean("auto_hp");
                final double hpPercent = rs.getDouble("hp_percent");
                final int hpItemId = rs.getInt("hp_item_id");
                final int reuseHp = rs.getInt("reuse_hp");
                final boolean autoMp = rs.getBoolean("auto_mp");
                final double mpPercent = rs.getDouble("mp_percent");
                final int mpItemId = rs.getInt("mp_item_id");
                final int reuseMp = rs.getInt("reuse_mp");
                template[0] = new AcpTemplate(autoCp, autoSmallCp, autoHp, autoMp,
                        cpPercent, smallCpPercent, hpPercent, mpPercent, cpItemId, smallCpItemId, hpItemId, mpItemId, reuseCp, reuseSmallCp, reuseHp, reuseMp);
            }
        }, rs -> rs.setInt(1, player.getObjectId()));
        return template[0];
    }

    public void insertUpdateAcp(final Player player, final AcpTemplate template) {
        final int count = jdbcHelper.queryForInt(SQL_SELECT_ACP_COUNT, player.getObjectId());
        jdbcHelper.execute(count == 0 ? SQL_INSERT_ACP : SQL_UPDATE_ACP, stmt -> {
            if (count == 0) {
                stmt.setInt(1, player.getObjectId());
                stmt.setBoolean(2, template.isAutoCp());
                stmt.setDouble(3, template.getCpPercent());
                stmt.setInt(4, template.getCpItemId());
                stmt.setInt(5, template.getReuseCp());
                stmt.setBoolean(6, template.isAutoSmallCp());
                stmt.setDouble(7, template.getSmallCpPercent());
                stmt.setInt(8, template.getSmallCpItemId());
                stmt.setInt(9, template.getReuseSmallCp());
                stmt.setBoolean(10, template.isAutoHp());
                stmt.setDouble(11, template.getHpPercent());
                stmt.setInt(12, template.getHpItemId());
                stmt.setInt(13, template.getReuseHp());
                stmt.setBoolean(14, template.isAutoMp());
                stmt.setDouble(15, template.getMpPercent());
                stmt.setInt(16, template.getMpItemId());
                stmt.setInt(17, template.getReuseMp());
            } else if (count > 0) {
                stmt.setBoolean(1, template.isAutoCp());
                stmt.setDouble(2, template.getCpPercent());
                stmt.setInt(3, template.getCpItemId());
                stmt.setInt(4, template.getReuseCp());
                stmt.setBoolean(5, template.isAutoSmallCp());
                stmt.setDouble(6, template.getSmallCpPercent());
                stmt.setInt(7, template.getSmallCpItemId());
                stmt.setInt(8, template.getReuseSmallCp());
                stmt.setBoolean(9, template.isAutoHp());
                stmt.setDouble(10, template.getHpPercent());
                stmt.setInt(11, template.getHpItemId());
                stmt.setInt(12, template.getReuseHp());
                stmt.setBoolean(13, template.isAutoMp());
                stmt.setDouble(14, template.getMpPercent());
                stmt.setInt(15, template.getMpItemId());
                stmt.setInt(16, template.getReuseMp());
                stmt.setInt(17, player.getObjectId());
            }
        });
    }
}
