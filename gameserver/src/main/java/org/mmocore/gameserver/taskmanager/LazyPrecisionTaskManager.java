package org.mmocore.gameserver.taskmanager;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.threading.SteppingRunnableQueueManager;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.configuration.config.community.CServiceConfig;
import org.mmocore.gameserver.data.StringHolder;
import org.mmocore.gameserver.database.dao.impl.AccountBonusDAO;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.BonusRequest;
import org.mmocore.gameserver.network.lineage.serverpackets.ExBR_PremiumState;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.premium.PremiumBonus;

import java.util.concurrent.Future;

/**
 * Менеджер задач вспомогательных задач, шаг выполенния задач 1с.
 *
 * @author G1ta0
 */
public class LazyPrecisionTaskManager extends SteppingRunnableQueueManager {
    private static final LazyPrecisionTaskManager _instance = new LazyPrecisionTaskManager();

    private LazyPrecisionTaskManager() {
        super(1000L);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(this, 1000L, 1000L);
        //Очистка каждые 60 секунд
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() {
                LazyPrecisionTaskManager.this.purge();
            }

        }, 60000L, 60000L);
    }

    public static LazyPrecisionTaskManager getInstance() {
        return _instance;
    }

    public Future<?> addPCCafePointsTask(final Player player) {
        final long delay = AllSettingsConfig.ALT_PCBANG_POINTS_DELAY * 60000L;

        return scheduleAtFixedRate(new RunnableImpl() {

            @Override
            public void runImpl() {
                if (player.isInOfflineMode() || player.getLevel() < AllSettingsConfig.ALT_PCBANG_POINTS_MIN_LVL) {
                    return;
                }

                player.getPremiumAccountComponent().addPcBangPoints(AllSettingsConfig.ALT_PCBANG_POINTS_BONUS, AllSettingsConfig.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE > 0 && Rnd.chance(AllSettingsConfig.ALT_PCBANG_POINTS_BONUS_DOUBLE_CHANCE));
            }

        }, delay, delay);
    }

    public Future<?> addVitalityRegenTask(final Player player) {
        final long delay = 60000L;

        return scheduleAtFixedRate(new RunnableImpl() {

            @Override
            public void runImpl() {
                if (player.isInOfflineMode() || !player.isInPeaceZone()) {
                    return;
                }

                player.getVitalityComponent().setVitality(player.getVitalityComponent().getVitality() + 1); // одно очко раз в минуту
            }

        }, delay, delay);
    }

    public Future<?> startBonusExpirationTask(final Player player) {
        final long delay = player.getPremiumAccountComponent().getPremiumBonus().getBonusExpire() * 1000L - System.currentTimeMillis();

        return schedule(new RunnableImpl() {

            @Override
            public void runImpl() {
                player.getPremiumAccountComponent().getPremiumBonus().setDefault();
                if (player.getParty() != null) {
                    player.getParty().recalculatePartyData();
                }
                final String msg = StringHolder.getInstance().getString(player, "premium.end");
                player.sendPacket(new ExShowScreenMessage(msg, 10000, ScreenMessageAlign.TOP_CENTER, true), new ExBR_PremiumState(player, false));
                player.sendMessage(msg);
                if (CServiceConfig.rateBonusType == PremiumBonus.BONUS_GLOBAL_ON_GAMESERVER) {
                    AccountBonusDAO.getInstance().delete(player.getAccountName());
                } else if (!AuthServerCommunication.getInstance().isShutdown() && CServiceConfig.rateBonusType == PremiumBonus.BONUS_GLOBAL_ON_AUTHSERVER) {
                    AuthServerCommunication.getInstance().sendPacket(new BonusRequest(player.getAccountName(), 0));
                }
            }

        }, delay);
    }

    public Future<?> addNpcAnimationTask(final NpcInstance npc) {
        return scheduleAtFixedRate(new RunnableImpl() {

            @Override
            public void runImpl() {
                if (npc.isVisible() && !npc.isActionsDisabled() && !npc.isMoving && !npc.isInCombat()) {
                    npc.onRandomAnimation();
                }
            }

        }, 1000L, Rnd.get(ServerConfig.MIN_NPC_ANIMATION, ServerConfig.MAX_NPC_ANIMATION) * 1000L);
    }
}
