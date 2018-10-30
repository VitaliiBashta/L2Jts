package org.mmocore.gameserver.manager;

import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.SpoilConfig;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.model.Manor;
import org.mmocore.gameserver.model.Manor.SeedData;
import org.mmocore.gameserver.model.entity.residence.Castle;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.components.items.warehouse.Warehouse;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.templates.manor.CropProcure;
import org.mmocore.gameserver.templates.manor.SeedProduction;
import org.mmocore.gameserver.utils.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CastleManorManager {
    public static final int PERIOD_CURRENT = 0;
    public static final int PERIOD_NEXT = 1;
    protected static final String var_name = "ManorApproved";
    protected static final long MAINTENANCE_PERIOD = SpoilConfig.MANOR_MAINTENANCE_PERIOD / 60000; // 6 mins
    private static final Logger _log = LoggerFactory.getLogger(CastleManorManager.class);
    private static final String CASTLE_MANOR_LOAD_PROCURE = "SELECT * FROM castle_manor_procure WHERE castle_id=?";
    private static final String CASTLE_MANOR_LOAD_PRODUCTION = "SELECT * FROM castle_manor_production WHERE castle_id=?";
    private static final int NEXT_PERIOD_APPROVE = SpoilConfig.MANOR_APPROVE_TIME; // 6:00
    private static final int NEXT_PERIOD_APPROVE_MIN = SpoilConfig.MANOR_APPROVE_MIN; //
    private static final int MANOR_REFRESH = SpoilConfig.MANOR_REFRESH_TIME; // 20:00
    private static final int MANOR_REFRESH_MIN = SpoilConfig.MANOR_REFRESH_MIN; //
    private boolean _underMaintenance;
    private boolean _disabled;

    private CastleManorManager() {
        _log.info("Manor System: Initializing...");

        load(); // load data from database
        init(); // schedule all manor related events
        _underMaintenance = false;
        _disabled = !SpoilConfig.ALLOW_MANOR;
        final List<Castle> castleList = ResidenceHolder.getInstance().getResidenceList(Castle.class);
        for (final Castle c : castleList) {
            c.setNextPeriodApproved(ServerVariables.getBool(var_name));
        }
    }

    public static CastleManorManager getInstance() {
        return LazyHolder.INSTANCE;
    }

    private void load() {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            // Get Connection
            con = DatabaseFactory.getInstance().getConnection();
            final List<Castle> castleList = ResidenceHolder.getInstance().getResidenceList(Castle.class);
            for (final Castle castle : castleList) {
                final List<SeedProduction> production = new ArrayList<>();
                final List<SeedProduction> productionNext = new ArrayList<>();
                final List<CropProcure> procure = new ArrayList<>();
                final List<CropProcure> procureNext = new ArrayList<>();

                // restore seed production info
                statement = con.prepareStatement(CASTLE_MANOR_LOAD_PRODUCTION);
                statement.setInt(1, castle.getId());
                rs = statement.executeQuery();
                while (rs.next()) {
                    final int seedId = rs.getInt("seed_id");
                    final long canProduce = rs.getLong("can_produce");
                    final long startProduce = rs.getLong("start_produce");
                    final long price = rs.getLong("seed_price");
                    final int period = rs.getInt("period");
                    if (period == PERIOD_CURRENT) {
                        production.add(new SeedProduction(seedId, canProduce, price, startProduce));
                    } else {
                        productionNext.add(new SeedProduction(seedId, canProduce, price, startProduce));
                    }
                }

                DbUtils.close(statement, rs);

                castle.setSeedProduction(production, PERIOD_CURRENT);
                castle.setSeedProduction(productionNext, PERIOD_NEXT);

                // restore procure info
                statement = con.prepareStatement(CASTLE_MANOR_LOAD_PROCURE);
                statement.setInt(1, castle.getId());
                rs = statement.executeQuery();
                while (rs.next()) {
                    final int cropId = rs.getInt("crop_id");
                    final long canBuy = rs.getLong("can_buy");
                    final long startBuy = rs.getLong("start_buy");
                    final int rewardType = rs.getInt("reward_type");
                    final long price = rs.getLong("price");
                    final int period = rs.getInt("period");
                    if (period == PERIOD_CURRENT) {
                        procure.add(new CropProcure(cropId, canBuy, rewardType, startBuy, price));
                    } else {
                        procureNext.add(new CropProcure(cropId, canBuy, rewardType, startBuy, price));
                    }
                }

                castle.setCropProcure(procure, PERIOD_CURRENT);
                castle.setCropProcure(procureNext, PERIOD_NEXT);

                if (!procure.isEmpty() || !procureNext.isEmpty() || !production.isEmpty() || !productionNext.isEmpty()) {
                    _log.info("Manor System: Loaded data for " + castle.getName() + " castle");
                }

                DbUtils.close(statement, rs);
            }
        } catch (Exception e) {
            _log.error("Manor System: Error restoring manor data!", e);
        } finally {
            DbUtils.closeQuietly(con, statement, rs);
        }
    }

    protected void init() {
        if (ServerVariables.getString(var_name, "").isEmpty()) {
            final Calendar manorRefresh = Calendar.getInstance();
            manorRefresh.set(Calendar.HOUR_OF_DAY, MANOR_REFRESH);
            manorRefresh.set(Calendar.MINUTE, MANOR_REFRESH_MIN);
            manorRefresh.set(Calendar.SECOND, 0);
            manorRefresh.set(Calendar.MILLISECOND, 0);

            final Calendar periodApprove = Calendar.getInstance();
            periodApprove.set(Calendar.HOUR_OF_DAY, NEXT_PERIOD_APPROVE);
            periodApprove.set(Calendar.MINUTE, NEXT_PERIOD_APPROVE_MIN);
            periodApprove.set(Calendar.SECOND, 0);
            periodApprove.set(Calendar.MILLISECOND, 0);
            final boolean isApproved = periodApprove.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() && manorRefresh.getTimeInMillis() > Calendar.getInstance().getTimeInMillis();
            ServerVariables.set(var_name, isApproved);
        }

        final Calendar FirstDelay = Calendar.getInstance();
        FirstDelay.set(Calendar.SECOND, 0);
        FirstDelay.set(Calendar.MILLISECOND, 0);
        FirstDelay.add(Calendar.MINUTE, 1);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new ManorTask(), FirstDelay.getTimeInMillis() - Calendar.getInstance().getTimeInMillis(), 60000);
    }

    public void setNextPeriod() {
        final List<Castle> castleList = ResidenceHolder.getInstance().getResidenceList(Castle.class);
        for (final Castle c : castleList) {
            if (c.getOwnerId() <= 0) {
                continue;
            }

            final Clan clan = ClanTable.getInstance().getClan(c.getOwnerId());
            if (clan == null) {
                continue;
            }

            final Warehouse cwh = clan.getWarehouse();

            for (final CropProcure crop : c.getCropProcure(PERIOD_CURRENT)) {
                if (crop.getStartAmount() == 0) {
                    continue;
                }

                // adding bought crops to clan warehouse
                if (crop.getStartAmount() > crop.getAmount()) {
                    _log.info("Manor System [" + c.getName() + "]: Start Amount of Crop " + crop.getStartAmount() + " > Amount of current " + crop.getAmount());
                    long count = crop.getStartAmount() - crop.getAmount();

                    count = count * 90 / 100;
                    if (count < 1 && Rnd.get(99) < 90) {
                        count = 1;
                    }

                    if (count >= 1) {
                        final int id = Manor.getInstance().getMatureCrop(crop.getId());
                        cwh.addItem(id, count);
                    }
                }

                // reserved and not used money giving back to treasury
                if (crop.getAmount() > 0) {
                    c.addToTreasuryNoTax(crop.getAmount() * crop.getPrice(), false, false);
                    Log.add(c.getName() + '|' + crop.getAmount() * crop.getPrice() + "|ManorManager|" + crop.getAmount() + '*' + crop.getPrice(), "treasury");
                }

                c.setCollectedShops(0);
                c.setCollectedSeed(0);
            }

            c.setSeedProduction(c.getSeedProduction(PERIOD_NEXT), PERIOD_CURRENT);
            c.setCropProcure(c.getCropProcure(PERIOD_NEXT), PERIOD_CURRENT);

            final long manor_cost = c.getManorCost(PERIOD_CURRENT);
            if (c.getTreasury() < manor_cost) {
                c.setSeedProduction(getNewSeedsList(c.getId()), PERIOD_NEXT);
                c.setCropProcure(getNewCropsList(c.getId()), PERIOD_NEXT);
                Log.add(c.getName() + '|' + manor_cost + "|ManorManager Error@setNextPeriod", "treasury");
            } else {
                final List<SeedProduction> production = new ArrayList<>();
                final List<CropProcure> procure = new ArrayList<>();
                for (final SeedProduction s : c.getSeedProduction(PERIOD_CURRENT)) {
                    s.setCanProduce(s.getStartProduce());
                    production.add(s);
                }
                for (final CropProcure cr : c.getCropProcure(PERIOD_CURRENT)) {
                    cr.setAmount(cr.getStartAmount());
                    procure.add(cr);
                }
                c.setSeedProduction(production, PERIOD_NEXT);
                c.setCropProcure(procure, PERIOD_NEXT);
            }

            c.saveCropData();
            c.saveSeedData();

            // Sending notification to a clan leader
            PlayerMessageStack.getInstance().mailto(clan.getLeaderId(), SystemMsg.THE_MANOR_INFORMATION_HAS_BEEN_UPDATED);

            c.setNextPeriodApproved(false);
        }
    }

    public void approveNextPeriod() {
        final List<Castle> castleList = ResidenceHolder.getInstance().getResidenceList(Castle.class);
        for (final Castle c : castleList) {
            // Castle has no owner
            if (c.getOwnerId() <= 0) {
                continue;
            }

            long manor_cost = c.getManorCost(PERIOD_NEXT);

            if (c.getTreasury() < manor_cost) {
                c.setSeedProduction(getNewSeedsList(c.getId()), PERIOD_NEXT);
                c.setCropProcure(getNewCropsList(c.getId()), PERIOD_NEXT);
                manor_cost = c.getManorCost(PERIOD_NEXT);
                if (manor_cost > 0) {
                    Log.add(c.getName() + '|' + -manor_cost + "|ManorManager Error@approveNextPeriod", "treasury");
                }
                final Clan clan = c.getOwner();
                PlayerMessageStack.getInstance().mailto(clan.getLeaderId(), SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_FUNDS_IN_THE_CLAN_WAREHOUSE_FOR_THE_MANOR_TO_OPERATE);
            } else {
                c.addToTreasuryNoTax(-manor_cost, false, false);
                Log.add(c.getName() + '|' + -manor_cost + "|ManorManager", "treasury");
            }
            c.setNextPeriodApproved(true);
        }
    }

    private List<SeedProduction> getNewSeedsList(final int castleId) {
        final List<SeedProduction> seeds = new ArrayList<>();
        final List<SeedData> seedsIds = Manor.getInstance().getSeedsForCastle(castleId);
        for (final SeedData sd : seedsIds) {
            seeds.add(new SeedProduction(sd.getId()));
        }
        return seeds;
    }

    private List<CropProcure> getNewCropsList(final int castleId) {
        final List<CropProcure> crops = new ArrayList<>();
        final List<Integer> cropsIds = Manor.getInstance().getCropsForCastle(castleId);
        for (final int cr : cropsIds) {
            crops.add(new CropProcure(cr));
        }
        return crops;
    }

    public boolean isUnderMaintenance() {
        return _underMaintenance;
    }

    public void setUnderMaintenance(final boolean mode) {
        _underMaintenance = mode;
    }

    public boolean isDisabled() {
        return _disabled;
    }

    public void setDisabled(final boolean mode) {
        _disabled = mode;
    }

    public SeedProduction getNewSeedProduction(final int id, final long amount, final long price, final long sales) {
        return new SeedProduction(id, amount, price, sales);
    }

    public CropProcure getNewCropProcure(final int id, final long amount, final int type, final long price, final long buy) {
        return new CropProcure(id, amount, type, buy, price);
    }

    public void save() {
        final List<Castle> castleList = ResidenceHolder.getInstance().getResidenceList(Castle.class);
        for (final Castle c : castleList) {
            c.saveSeedData();
            c.saveCropData();
        }
    }

    private static class LazyHolder {
        private static final CastleManorManager INSTANCE = new CastleManorManager();
    }

    private class ManorTask extends RunnableImpl {
        @Override
        public void runImpl() {
            final int H = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            final int M = Calendar.getInstance().get(Calendar.MINUTE);

            if (ServerVariables.getBool(var_name)) // 06:00 - 20:00
            {
                if (H < NEXT_PERIOD_APPROVE || H > MANOR_REFRESH || H == MANOR_REFRESH && M >= MANOR_REFRESH_MIN) {
                    ServerVariables.set(var_name, false);
                    setUnderMaintenance(true);
                    LOGGER.info("Manor System: Under maintenance mode started");
                }
            } else if (isUnderMaintenance()) // 20:00 - 20:06
            {
                if (H != MANOR_REFRESH || M >= MANOR_REFRESH_MIN + MAINTENANCE_PERIOD) {
                    setUnderMaintenance(false);
                    LOGGER.info("Manor System: Next period started");
                    if (isDisabled()) {
                        return;
                    }
                    setNextPeriod();
                    try {
                        save();
                    } catch (Exception e) {
                        LOGGER.info("Manor System: Failed to save manor data: " + e);
                    }
                }
            } else if (H > NEXT_PERIOD_APPROVE && H < MANOR_REFRESH || H == NEXT_PERIOD_APPROVE && M >= NEXT_PERIOD_APPROVE_MIN) {
                ServerVariables.set(var_name, true);
                LOGGER.info("Manor System: Next period approved");
                if (isDisabled()) {
                    return;
                }
                approveNextPeriod();
            }
        }
    }
}
