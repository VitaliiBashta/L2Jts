package org.mmocore.gameserver.object.components.player.recomendation;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.RecommendationConfig;
import org.mmocore.gameserver.network.lineage.serverpackets.ExVoteSystemInfo;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.tasks.RecomBonusTask;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author KilRoy
 * Подсистема рекомендаций персонажа
 * TODO[K] - перенести store/restore инфы по рекам из объекта игрока!
 */
public class RecommendationComponent {
    private final Player playerRef;
    private int _recomHave, _recomLeftToday;
    private int _recomLeft = 20;
    private int _recomBonusTime = 3600;
    private boolean _isHourglassEffected, _isRecomTimerActive;
    private ScheduledFuture<?> _recomBonusTask;

    public RecommendationComponent(final Player player) {
        playerRef = player;
    }

    public Player getPlayer() {
        return playerRef;
    }

    public void sendVoteSystemInfo(final Player player) {
        if (player == null)
            return;
        player.sendPacket(new ExVoteSystemInfo(this));
    }

    public int getRecomHave() {
        return _recomHave;
    }

    public void setRecomHave(final int value) {
        if (value > 255)
            _recomHave = 255;
        else if (value < 0)
            _recomHave = 0;
        else
            _recomHave = value;
    }

    public int getRecomBonusTime() {
        if (_recomBonusTask != null)
            return (int) Math.max(0, _recomBonusTask.getDelay(TimeUnit.SECONDS));
        return _recomBonusTime;
    }

    public void setRecomBonusTime(final int val) {
        _recomBonusTime = val;
    }

    public int getRecomLeft() {
        return _recomLeft;
    }

    public void setRecomLeft(final int value) {
        _recomLeft = value;
    }

    public boolean isHourglassEffected() {
        return _isHourglassEffected;
    }

    public void setHourlassEffected(final boolean val) {
        _isHourglassEffected = val;
    }

    public void startHourglassEffect() {
        setHourlassEffected(true);
        stopRecomBonusTask(true);
        sendVoteSystemInfo(getPlayer());
    }

    public void stopHourglassEffect() {
        setHourlassEffected(false);
        startRecomBonusTask();
        sendVoteSystemInfo(getPlayer());
    }

    public int addRecomLeft() {
        int recoms;
        if (getRecomLeftToday() < 20)
            recoms = 10;
        else
            recoms = 1;
        setRecomLeft(getRecomLeft() + recoms);
        setRecomLeftToday(getRecomLeftToday() + recoms);
        getPlayer().sendUserInfo(true);
        return recoms;
    }

    public int getRecomLeftToday() {
        return _recomLeftToday;
    }

    public void setRecomLeftToday(final int value) {
        _recomLeftToday = value;
        getPlayer().getPlayerVariables().set(PlayerVariables.REC_LEFT_TODAY, String.valueOf(_recomLeftToday), -1);
    }

    public void giveRecom(final Player target) {
        final int targetRecom = target.getRecommendationComponent().getRecomHave();
        if (targetRecom < 255)
            target.getRecommendationComponent().addRecomHave(1);
        if (getRecomLeft() > 0)
            setRecomLeft(getRecomLeft() - 1);

        getPlayer().sendUserInfo(true);
    }

    public void addRecomHave(final int val) {
        setRecomHave(getRecomHave() + val);
        getPlayer().broadcastUserInfo(true);
        sendVoteSystemInfo(getPlayer());
    }

    public int getRecomBonus() {
        if (getRecomBonusTime() > 0 || isHourglassEffected())
            return RecomBonus.getRecoBonus(getPlayer());
        return 0;
    }

    public double getRecomBonusMul() {
        if (getRecomBonusTime() > 0 || isHourglassEffected())
            return RecomBonus.getRecoMultiplier(getPlayer());
        return 1;
    }

    public boolean isRecomTimerActive() {
        return _isRecomTimerActive;
    }

    public void setRecomTimerActive(final boolean val) {
        if (_isRecomTimerActive == val)
            return;

        _isRecomTimerActive = val;

        if (val)
            startRecomBonusTask();
        else
            stopRecomBonusTask(true);

        sendVoteSystemInfo(getPlayer());
    }

    public void startRecomBonusTask() {
        if (_recomBonusTask == null && getRecomBonusTime() > 0 && isRecomTimerActive() && !isHourglassEffected())
            _recomBonusTask = ThreadPoolManager.getInstance().schedule(new RecomBonusTask(getPlayer()), getRecomBonusTime() * 1000);
    }

    public void stopRecomBonusTask(final boolean saveTime) {
        if (_recomBonusTask != null) {
            if (saveTime)
                setRecomBonusTime((int) Math.max(0, _recomBonusTask.getDelay(TimeUnit.SECONDS)));
            _recomBonusTask.cancel(false);
            _recomBonusTask = null;
        }
    }

    public void checkRecom() {
        final Instant temp = Instant.ofEpochMilli(RecommendationConfig.reuseTime.getNextValidTimeAfter(new Date()).getTime());
        long count = (System.currentTimeMillis() / 1000 - getPlayer().getLastAccess()) / 86400;
        if (count == 0 && getPlayer().getLastAccess() < temp.toEpochMilli() / 1000 && System.currentTimeMillis() > temp.toEpochMilli()) {
            count++;
        }

        for (int i = 1; i < count; i++) {
            setRecomHave(getRecomHave() - 20);
        }

        if (count > 0) {
            restartRecom();
        }
    }

    public void restartRecom() {
        setRecomBonusTime(3600);
        setRecomLeftToday(0);
        setRecomLeft(20);
        setRecomHave(getRecomHave() - 20);
        stopRecomBonusTask(false);
        startRecomBonusTask();
        getPlayer().sendUserInfo(true);
        sendVoteSystemInfo(getPlayer());
    }

    public void restoreRecom() {

    }

    public void storeRecom() {

    }
}