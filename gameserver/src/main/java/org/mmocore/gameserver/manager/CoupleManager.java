package org.mmocore.gameserver.manager;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.entity.Couple;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CoupleManager {
    private static final Logger _log = LoggerFactory.getLogger(CoupleManager.class);

    private List<Couple> _couples;
    private List<Couple> _deletedCouples;

    private CoupleManager() {
        _log.info("Initializing CoupleManager");

        load();
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new StoreTask(), 10 * 60 * 1000, 10 * 60 * 1000);
    }

    public static CoupleManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private void load() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM couples ORDER BY id");
            rs = statement.executeQuery();
            while (rs.next()) {
                final Couple c = new Couple(rs.getInt("id"));
                c.setPlayer1Id(rs.getInt("player1Id"));
                c.setPlayer2Id(rs.getInt("player2Id"));
                c.setMaried(rs.getBoolean("maried"));
                c.setAffiancedDate(rs.getLong("affiancedDate"));
                c.setWeddingDate(rs.getLong("weddingDate"));
                getCouples().add(c);
            }
            _log.info("Loaded: " + getCouples().size() + " couples(s)");
        } catch (Exception e) {
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rs);
        }
    }

    public final Couple getCouple(final int coupleId) {
        for (final Couple c : getCouples()) {
            if (c != null && c.getId() == coupleId) {
                return c;
            }
        }
        return null;
    }

    /**
     * Вызывается при каждом входе персонажа в мир
     *
     * @param cha
     */
    public void engage(final Player cha) {
        final int chaId = cha.getObjectId();

        for (final Couple cl : getCouples()) {
            if (cl != null) {
                if (cl.getPlayer1Id() == chaId || cl.getPlayer2Id() == chaId) {
                    if (cl.getMaried()) {
                        cha.setMaried(true);
                    }

                    cha.setCoupleId(cl.getId());

                    if (cl.getPlayer1Id() == chaId) {
                        cha.setPartnerId(cl.getPlayer2Id());
                    } else {
                        cha.setPartnerId(cl.getPlayer1Id());
                    }
                }
            }
        }
    }

    /**
     * Уведомляет партнера персонажа о его входе в мир.
     *
     * @param cha
     */
    public void notifyPartner(final Player cha) {
        if (cha.getPartnerId() != 0) {
            final Player partner = GameObjectsStorage.getPlayer(cha.getPartnerId());
            if (partner != null) {
                partner.sendMessage(new CustomMessage("org.mmocore.gameserver.instancemanager.CoupleManager.PartnerEntered"));
            }
        }
    }

    public void createCouple(final Player player1, final Player player2) {
        if (player1 != null && player2 != null) {
            if (player1.getPartnerId() == 0 && player2.getPartnerId() == 0) {
                getCouples().add(new Couple(player1, player2));
            }
        }
    }

    public final List<Couple> getCouples() {
        if (_couples == null) {
            _couples = new CopyOnWriteArrayList<>();
        }
        return _couples;
    }

    public List<Couple> getDeletedCouples() {
        if (_deletedCouples == null) {
            _deletedCouples = new CopyOnWriteArrayList<>();
        }
        return _deletedCouples;
    }

    /**
     * Вызывется при шатдауне
     * Сначала очищаем таблицу от ненужных свадеб, потом загоняем в нее все нужные.
     * Обращение происходит только при загрузке/шатдауне сервера, ну или по запросу
     */
    public void store() {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();

            if (_deletedCouples != null && !_deletedCouples.isEmpty()) {
                statement = con.prepareStatement("DELETE FROM couples WHERE id = ?");
                for (final Couple c : _deletedCouples) {
                    statement.setInt(1, c.getId());
                    statement.execute();
                }

                _deletedCouples.clear();
            }

            if (_couples != null && !_couples.isEmpty()) {
                for (final Couple c : _couples) {
                    if (c != null && c.isChanged()) {
                        c.store(con);
                        c.setChanged(false);
                    }
                }
            }
        } catch (Exception e) {
            _log.error("", e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    private static class LazyHolder {
        private static final CoupleManager INSTANCE = new CoupleManager();
    }

    private class StoreTask extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            store();
        }
    }
}