package org.mmocore.gameserver.object.components.player.vitality;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExVitalityPointInfo;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.taskmanager.LazyPrecisionTaskManager;

import java.util.concurrent.Future;

/**
 * @author KilRoy
 * TODO[K] - в будущем перенести систему виталити с ребеллиона
 */
public class VitalityComponent {
    private final Player playerRef;

    private int _vitalityLevel = -1;
    private double _vitality = AllSettingsConfig.VITALITY_LEVELS[4];
    private Future<?> _vitalityTask;


    public VitalityComponent(final Player player) {
        playerRef = player;
    }

    public Player getPlayer() {
        return playerRef;
    }

    public int getVitalityLevel(boolean blessActive) {
        return AllSettingsConfig.ALT_VITALITY_ENABLED ? (blessActive ? 4 : _vitalityLevel) : 0;
    }

    public double getVitality() {
        return AllSettingsConfig.ALT_VITALITY_ENABLED ? _vitality : 0;
    }

    public void setVitality(double newVitality) {
        final Player player = getPlayer();
        if (!AllSettingsConfig.ALT_VITALITY_ENABLED)
            return;

        newVitality = Math.max(Math.min(newVitality, AllSettingsConfig.VITALITY_LEVELS[4]), 0);

        if (newVitality >= _vitality || player.getLevel() >= 10) {
            if (newVitality != _vitality) {
                if (newVitality == 0)
                    player.sendPacket(SystemMsg.YOUR_VITALITY_IS_FULLY_EXHAUSTED);
                else if (newVitality == AllSettingsConfig.VITALITY_LEVELS[4])
                    player.sendPacket(SystemMsg.YOUR_VITALITY_IS_AT_MAXIMUM);
            }
            _vitality = newVitality;
        }

        int newLevel = 0;

        if (_vitality >= AllSettingsConfig.VITALITY_LEVELS[3])
            newLevel = 4;
        else if (_vitality >= AllSettingsConfig.VITALITY_LEVELS[2])
            newLevel = 3;
        else if (_vitality >= AllSettingsConfig.VITALITY_LEVELS[1])
            newLevel = 2;
        else if (_vitality >= AllSettingsConfig.VITALITY_LEVELS[0])
            newLevel = 1;

        if (_vitalityLevel > newLevel)
            player.getNevitComponent().addPoints(1500); //TODO: Количество от балды.

        if (_vitalityLevel != newLevel) {
            if (_vitalityLevel != -1) // при ините чара сообщения не шлём
                player.sendPacket(newLevel < _vitalityLevel ? SystemMsg.YOUR_VITALITY_HAS_DECREASED : SystemMsg.YOUR_VITALITY_HAS_INCREASED);
            _vitalityLevel = newLevel;
        }

        player.sendPacket(new ExVitalityPointInfo((int) _vitality));
    }

    public void addVitality(double val) {
        setVitality(getVitality() + val);
    }

    public void startVitalityTask() {
        if (!AllSettingsConfig.ALT_VITALITY_ENABLED)
            return;
        if (_vitalityTask == null)
            _vitalityTask = LazyPrecisionTaskManager.getInstance().addVitalityRegenTask(getPlayer());
    }

    public void stopVitalityTask() {
        if (_vitalityTask != null)
            _vitalityTask.cancel(false);
        _vitalityTask = null;
    }
}