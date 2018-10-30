package org.mmocore.gameserver.object.components.player.custom;

import org.apache.logging.log4j.util.Strings;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.custom.CustomConfig;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerExitListener;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.scripts.events.LastHero.LastHero;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Mangol
 * @since 23.02.2016
 */
public final class CustomPlayerComponent {
    public static final int DEFAULT = -1;
    private final Player player;
    private AtomicLong timeSubscription = new AtomicLong(0);
    private int hairStyleWear = DEFAULT;
    private int hairColor = DEFAULT;
    private int face = DEFAULT;
    private boolean temporalHero;
    private String name = Strings.EMPTY;
    private int nameColor = DEFAULT;
    private int titleColor = DEFAULT;
    private String titleName = Strings.EMPTY;
    private ScheduledFuture<?> _temporalHeroTask;
    private ScheduledFuture<?> _faceTask;
    private ScheduledFuture<?> _hairStyleTask;
    private ScheduledFuture<?> _hairColorTask;
    private ScheduledFuture<?> _subscriptionTask;

    private CustomPlayerComponent(final Player player) {
        this.player = player;
        this.player.addListener(new OnPlayerEnterImpl());
        this.player.addListener(new OnPlayerExitImpl());
    }

    public static CustomPlayerComponent create(final Player player) {
        return new CustomPlayerComponent(player);
    }

    /**
     * Выдаем временного героя, выдаем скилы, запускаем таск на снятие временного героя.
     */
    public void startTemporalHero() {
        final ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.systemDefault());
        long millis = player.getPlayerVariables().getLong(PlayerVariables.TEMPORAL_HERO);
        final ZonedDateTime endHeroTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
        if (endHeroTime.toInstant().toEpochMilli() > dateTime.toInstant().toEpochMilli()) {
            final Duration duration = Duration.between(dateTime, endHeroTime);
            final long startTimer = duration.toMillis();
            setTemporalHero(true);
            player.setHero(true);
            player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.GIVE_HERO));
            //Выдаем скилы игроку.Если игрок на базовом классе.
            if (!player.isHero() && player.getPlayerClassComponent().isBaseExactlyActiveId()) {
                Hero.addSkills(player);
//				player.broadcastUserInfo(true);
            }
            player.broadcastUserInfo(true);
            //Останавливаем на всякий случай таск.
            stopTemporalHeroTask();
            _temporalHeroTask = ThreadPoolManager.getInstance().schedule(() -> {
                stopTemporalHero(false);
                LastHero.removeWinner(player);
                player.setHero(false);
                player.broadcastUserInfo(true);
            }, startTimer);
        } else {
            if (millis > 0 && !player.isHero()) {
                final PcInventory inventory = player.getInventory();
                inventory.writeLock();
                try {
                    for (final ItemInstance item : player.getInventory().getItems()) {
                        if (item.isHeroWeapon()) {
                            player.getInventory().destroyItem(item);
                        }
                    }
                } finally {
                    inventory.writeUnlock();
                }
            }
        }
    }

    /**
     * Забираем временного героя, чистим базу, удаляем скилы, останавливаем таск если нужно.
     */
    private void stopTemporalHero(final boolean stopTask) {
        setTemporalHero(false);
        if (stopTask) {
            stopTemporalHeroTask();
        }
        player.getPlayerVariables().remove(PlayerVariables.TEMPORAL_HERO);
        if (!player.isHero()) {
            Hero.removeSkills(player);
        }
        player.broadcastUserInfo(true);
    }

    /**
     * Останавливаем таск на временного геройство.
     */
    private void stopTemporalHeroTask() {
        if (_temporalHeroTask != null) {
            _temporalHeroTask.cancel(true);
            _temporalHeroTask = null;
        }
    }

    public void saveSubscriptionTimeTask(final boolean saveDb) {
        if (_subscriptionTask != null) {
            if (_subscriptionTask.getDelay(TimeUnit.MILLISECONDS) > 0) {
                timeSubscription.set(_subscriptionTask.getDelay(TimeUnit.MILLISECONDS));
            }
        }
        if (saveDb) {
            if (_subscriptionTask != null && !_subscriptionTask.isDone()) {
                if (_subscriptionTask.getDelay(TimeUnit.MILLISECONDS) > 0) {
                    player.getPlayerVariables().set(PlayerVariables.SUBSCRIPTION, _subscriptionTask.getDelay(TimeUnit.MILLISECONDS), 0);
                }
            } else {
                player.getPlayerVariables().set(PlayerVariables.SUBSCRIPTION, timeSubscription.get(), -1);
            }
            timeSubscription.set(0);
        }
    }

    /**
     * Останавливаем таск на подписки
     */
    public void stopSubscriptionTask() {
        if (_subscriptionTask != null) {
            _subscriptionTask.cancel(true);
            _subscriptionTask = null;
        }
    }

    public void startSubscription() {
        if (!CustomConfig.subscriptionAllow) {
            return;
        }
        final long time = timeSubscription.get();
        if (time > 0) {
            if (_subscriptionTask != null) {
                _subscriptionTask.cancel(true);
                _subscriptionTask = null;
            }
            _subscriptionTask = ThreadPoolManager.getInstance().schedule(() -> {
                timeSubscription.set(0);
                player.sendChanges();
                player.getPlayerVariables().remove(PlayerVariables.SUBSCRIPTION);
            }, time);
        }
    }

    /**
     * @return - статус временного героя, верно или ложно.
     */
    public boolean isTemporalHero() {
        return temporalHero;
    }

    public void setTemporalHero(final boolean temporalHero) {
        this.temporalHero = temporalHero;
    }

    public void startTimers() {
        startTemporalHero();
        if (!player.isInZone(ZoneType.peace_zone)) {
            startSubscription();
        }
    }

    public void stopAllTimers() {
        stopTemporalHeroTask();
        stopRemoveHairStyleTask();
        saveSubscriptionTimeTask(true);
    }

    public int getHairStyleWear() {
        return hairStyleWear;
    }

    /**
     * Устанавливает стиль волос для просмотра, используется из коммунити боард.
     *
     * @param hairStyleWear
     * @param sendUserInfo  - обновить вид юзера?
     */
    public void setHairStyleWear(final int hairStyleWear, final boolean sendUserInfo) {
        this.hairStyleWear = hairStyleWear;
        if (sendUserInfo) {
            player.sendUserInfo(true);
        }
    }

    public int getHairColorWear() {
        return hairColor;
    }

    /**
     * Устанавливает цвет волос для просмотра, используется из коммунити боард.
     *
     * @param hairColor
     * @param sendUserInfo - обновить вид юзера?
     */
    public void setHairColorWear(final int hairColor, final boolean sendUserInfo) {
        this.hairColor = hairColor;
        if (sendUserInfo) {
            player.sendUserInfo(true);
        }
    }

    public int getFaceWear() {
        return face;
    }

    /**
     * Устанавливает лицо для просмотра, используется из коммунити боард.
     *
     * @param face
     * @param sendUserInfo - обновить вид юзера?
     */
    public void setFaceWear(final int face, final boolean sendUserInfo) {
        this.face = face;
        if (sendUserInfo) {
            player.sendUserInfo(true);
        }
    }

    /**
     * Останавливает таск примерки стиля волос.
     */
    public void stopRemoveHairStyleTask() {
        if (_hairStyleTask != null) {
            setHairStyleWear(-1, false);
            _hairStyleTask.cancel(true);
            _hairStyleTask = null;
        }
    }

    /**
     * Останавливает таск примерки цвета волос.
     */
    public void stopRemoveHairColorTask() {
        if (_hairColorTask != null) {
            setHairColorWear(-1, false);
            _hairColorTask.cancel(true);
            _hairColorTask = null;
        }
    }

    /**
     * Останавливает таск подборки лица.
     */
    public void stopRemoveFaceTask() {
        if (_faceTask != null) {
            setFaceWear(-1, false);
            _faceTask.cancel(true);
            _faceTask = null;
        }
    }

    /**
     * Запускает таск примерки стиля волос, вызывается из сервиса коммунити боард.
     */
    public void startRemoveHairStyleTask() {
        stopRemoveHairStyleTask();
        _hairStyleTask = ThreadPoolManager.getInstance().schedule(() -> setHairStyleWear(-1, true), 10000);
    }

    /**
     * Запускает таск примерки цвета волос, вызывается из сервиса коммунити боард.
     */
    public void startRemoveHairColorTask() {
        stopRemoveHairColorTask();
        _hairColorTask = ThreadPoolManager.getInstance().schedule(() -> setHairColorWear(-1, true), 10000);
    }

    /**
     * Запускает таск подборки лица, вызывается из сервиса коммунити боард.
     */
    public void startRemoveFaceTask() {
        stopRemoveHairColorTask();
        _faceTask = ThreadPoolManager.getInstance().schedule(() -> setFaceWear(-1, true), 10000);
    }

    public boolean isSubscriptionActive() {
        if (_subscriptionTask != null && !_subscriptionTask.isDone()) {
            return _subscriptionTask.getDelay(TimeUnit.MILLISECONDS) > 0;
        }
        return timeSubscription.get() > 0;
    }

    public void addTimeSubscription(final long timeMillis) {
        timeSubscription.addAndGet(timeMillis);
    }

    public long getTimeSubscription() {
        if (_subscriptionTask != null && !_subscriptionTask.isDone()) {
            return _subscriptionTask.getDelay(TimeUnit.MILLISECONDS);
        }
        return timeSubscription.get();
    }

    public void setTimeSubscription(final long timeMillis) {
        timeSubscription.set(timeMillis);
    }

    public int getNameColor() {
        return nameColor;
    }

    public void setNameColor(int nameColor) {
        this.nameColor = nameColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(TitleType titleType) {
        this.titleName = titleType.getLangTitle(player);
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    private class OnPlayerEnterImpl implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(Player player) {
            if (CustomConfig.subscriptionAllow) {
                final long time = player.getPlayerVariables().getLong(PlayerVariables.SUBSCRIPTION);
                if (time <= 0) {
                    player.getPlayerVariables().remove(PlayerVariables.SUBSCRIPTION);
                } else {
                    setTimeSubscription(player.getPlayerVariables().getLong(PlayerVariables.SUBSCRIPTION));
                }
                if (CustomConfig.subscriptionAllow && !player.getPlayerVariables().getBoolean(PlayerVariables.SUBSCRIPTION_DAY_GIFT)) {
                    stopSubscriptionTask();
                    player.getPlayerVariables().set(PlayerVariables.SUBSCRIPTION_DAY_GIFT, "true", -1);
                    addTimeSubscription(TimeUnit.HOURS.toMillis(CustomConfig.subscriptionHourBonus));
                }
                player.sendChanges();
            }
            startTimers();
        }
    }

    private class OnPlayerExitImpl implements OnPlayerExitListener {
        @Override
        public void onPlayerExit(Player player) {
            stopAllTimers();
        }
    }
}
