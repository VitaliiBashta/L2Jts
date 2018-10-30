package org.mmocore.gameserver.listener.actor.player.impl;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.model.instances.PetInstance;
import org.mmocore.gameserver.object.Player;

/**
 * @author VISTALL
 * @date 11:35/15.04.2011
 */
public class ReviveAnswerListener implements OnAnswerListener {
    private final HardReference<Player> _playerRef;
    private final double _power;
    private final boolean _forPet;
    private final long _expireTime;

    public ReviveAnswerListener(final Player player, final double power, final boolean forPet, final long expireTime) {
        _playerRef = player.getRef();
        _forPet = forPet;
        _power = power;
        _expireTime = expireTime > 0L ? System.currentTimeMillis() + expireTime : 0L;
    }

    @Override
    public void sayYes() {
        final Player player = _playerRef.get();
        if (player == null) {
            return;
        }
        if (!player.isDead() && !_forPet || _forPet && player.getServitor() != null && !player.getServitor().isDead()) {
            return;
        }

        if (!_forPet) {
            player.doRevive(_power);
        } else if (player.getServitor() != null) {
            ((PetInstance) player.getServitor()).doRevive(_power);
        }
    }

    @Override
    public void sayNo() {

    }

    @Override
    public long expireTime() {
        return _expireTime;
    }

    public double getPower() {
        return _power;
    }

    public boolean isForPet() {
        return _forPet;
    }
}
