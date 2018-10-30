package org.mmocore.gameserver.object.components.player.premium;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.configuration.config.community.CServiceConfig;
import org.mmocore.gameserver.database.dao.impl.AccountBonusDAO;
import org.mmocore.gameserver.database.dao.impl.CharacterProductDAO;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.BonusRequest;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBR_PremiumState;
import org.mmocore.gameserver.network.lineage.serverpackets.ExPCCafePointInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.PremiumItem;
import org.mmocore.gameserver.taskmanager.LazyPrecisionTaskManager;
import org.mmocore.gameserver.templates.item.ProductItemTemplate;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Future;

/**
 * @author KilRoy
 */
public class PremiumAccountComponent {
    private static final Logger LOGGER = LogManager.getLogger(PremiumAccountComponent.class);
    private final Player playerRef;
    private PremiumBonus bonus;
    private Future<?> bonusExpiration;
    private Future<?> pcCafePointsTask;
    private Map<Integer, PremiumItem> premiumItems = null;
    private TreeMap<Integer, ProductItemTemplate> boughtProducts = new TreeMap<>();
    private long premiumPoints;
    private int privateStore = 0;
    private int expandInventory = 0;
    private int expandWarehouse = 0;
    private int pcBangPoints;

    public PremiumAccountComponent(final Player player) {
        playerRef = player;
    }

    public Player getPlayer() {
        return playerRef;
    }

    public void addBoughtProduct(final ProductItemTemplate product, final boolean db) {
        if (product == null)
            return;

        if (boughtProducts.containsKey(product.getProductId()))
            return;

        boughtProducts.put(product.getProductId(), product);

        if (boughtProducts.size() > ServicesConfig.SERVICES_PREMIUMSHOP_PLAYER_PRODUCT_COUNT) {
            final Integer last = boughtProducts.lastKey();
            boughtProducts.remove(last);

            CharacterProductDAO.getInstance().update(getPlayer(), last, product.getProductId());
        } else {
            if (db)
                CharacterProductDAO.getInstance().insert(getPlayer(), product.getProductId());
        }
    }

    public Collection<ProductItemTemplate> getBoughtProducts() {
        return boughtProducts.values();
    }

    public int getPcBangPoints() {
        return pcBangPoints;
    }

    public void setPcBangPoints(final int val) {
        pcBangPoints = val;
    }

    public void addPcBangPoints(int count, final boolean doublePoints) {
        if (doublePoints)
            count *= 2;

        pcBangPoints += count;

        getPlayer().sendPacket(new SystemMessage(doublePoints ? SystemMsg.DOUBLE_POINTS_YOU_ACQUIRED_S1_PC_BANG_POINT : SystemMsg.YOU_ACQUIRED_S1_PC_BANG_POINT).addNumber(count));
        getPlayer().sendPacket(new ExPCCafePointInfo(getPlayer(), count, 1, 2, 12));
    }

    public boolean reducePcBangPoints(final int count) {
        if (pcBangPoints < count)
            return false;

        pcBangPoints -= count;
        getPlayer().sendPacket(new SystemMessage(SystemMsg.YOU_ARE_USING_S1_POINT).addNumber(count));
        getPlayer().sendPacket(new ExPCCafePointInfo(getPlayer(), 0, 1, 2, 12));
        return true;
    }

    public int getExpandInventory() {
        return expandInventory;
    }

    public void setExpandInventory(final int inventory) {
        expandInventory = inventory;
    }

    public int getExpandWarehouse() {
        return expandWarehouse;
    }

    public void setExpandWarehouse(final int warehouse) {
        expandWarehouse = warehouse;
    }

    public int getPrivateStore() {
        return privateStore;
    }

    public void setPrivateStore(final int privateStore) {
        this.privateStore = privateStore;
    }

    public void loadPremiumItemList() {
        premiumItems = AccountBonusDAO.getInstance().loadPremiumItemList(getPlayer().getObjectId());
    }

    public Map<Integer, PremiumItem> getPremiumItemList() {
        return premiumItems;
    }

    public PremiumBonus getPremiumBonus() {
        return bonus;
    }

    public void setPremiumBonus(PremiumBonus premiumBonus) {
        this.bonus = premiumBonus;
    }

    public boolean hasBonus() {
        return getPremiumBonus().getBonusExpire() > System.currentTimeMillis() / 1000L;
    }

    public long getPremiumPoints() {
        return premiumPoints;
    }

    public void setPremiumPoints(final long premiumPoints) {
        this.premiumPoints = premiumPoints;
    }

    public void startBonusTask() {
        if (CServiceConfig.rateBonusType != PremiumBonus.NO_BONUS) {
            if (bonus == null) {
                if (playerRef.getNetConnection() != null && playerRef.getNetConnection().getPremiumBonus() != null) {
                    bonus = playerRef.getNetConnection().getPremiumBonus().clone();
                } else {
                    bonus = new PremiumBonus();
                }
            }
            final long bonusExpire = bonus.getBonusExpire();
            if (bonusExpire > System.currentTimeMillis() / 1000L) {
                if (bonusExpiration == null) {
                    bonusExpiration = LazyPrecisionTaskManager.getInstance().startBonusExpirationTask(getPlayer());
                }
            } else if (bonusExpire > 0 && CServiceConfig.rateBonusType == PremiumBonus.BONUS_GLOBAL_ON_GAMESERVER) {
                bonus.setDefault();
                getPlayer().sendPacket(new ExBR_PremiumState(getPlayer(), false));
                AccountBonusDAO.getInstance().delete(getPlayer().getAccountName());
                stopBonusTask();
            } else if (bonusExpire > 0 && !AuthServerCommunication.getInstance().isShutdown() && CServiceConfig.rateBonusType == PremiumBonus.BONUS_GLOBAL_ON_AUTHSERVER) {
                bonus.setDefault();
                getPlayer().sendPacket(new ExBR_PremiumState(getPlayer(), false));
                AuthServerCommunication.getInstance().sendPacket(new BonusRequest(getPlayer().getAccountName(), 0));
                stopBonusTask();
            }
        }
    }

    public void stopBonusTask() {
        if (bonusExpiration != null) {
            bonusExpiration.cancel(false);
            bonusExpiration = null;
        }
    }

    public void startPcBangPointsTask() {
        if (!AllSettingsConfig.ALT_PCBANG_POINTS_ENABLED || AllSettingsConfig.ALT_PCBANG_POINTS_DELAY <= 0)
            return;
        if (pcCafePointsTask == null)
            pcCafePointsTask = LazyPrecisionTaskManager.getInstance().addPCCafePointsTask(getPlayer());
    }

    public void stopPcBangPointsTask() {
        if (pcCafePointsTask != null)
            pcCafePointsTask.cancel(false);
        pcCafePointsTask = null;
    }
}