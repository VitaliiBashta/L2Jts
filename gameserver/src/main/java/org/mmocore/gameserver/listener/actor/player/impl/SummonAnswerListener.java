package org.mmocore.gameserver.listener.actor.player.impl;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.gameserver.listener.actor.player.OnAnswerListener;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.skillclasses.Call;
import org.mmocore.gameserver.utils.ItemFunctions;

/**
 * @author VISTALL
 * @date 11:28/15.04.2011
 */
public class SummonAnswerListener implements OnAnswerListener {
    private final HardReference<Player> _summonerRef;
    private final HardReference<Player> _playerRef;
    private final int _itemId;
    private final int _count;
    private final long _timeStamp;

    public SummonAnswerListener(final Player summoner, final Player player, final int itemConsumeId, final int itemConsumeCount, final int expiration) {
        _summonerRef = summoner.getRef();
        _playerRef = player.getRef();
        _itemId = itemConsumeId;
        _count = itemConsumeCount;
        _timeStamp = expiration > 0 ? System.currentTimeMillis() + expiration : Long.MAX_VALUE;
    }

    @Override
    public void sayYes() {
        if (System.currentTimeMillis() > expireTime()) {
            return;
        }
        final Player summoner = _summonerRef.get();
        if (summoner == null) {
            return;
        }

        if (Call.canSummonHere(summoner) != null) {
            return;
        }

        final Player player = _playerRef.get();
        if (player == null) {
            return;
        }

        if (Call.canBeSummoned(player) != null) {
            return;
        }

        player.abortAttack(true, true);
        player.abortCast(true, true);
        player.stopMove();

        if (_itemId == 0 || _count == 0) {
            player.teleToLocation(summoner.getLoc());
        } else if (ItemFunctions.removeItem(player, _itemId, _count, true) == _count) {
            player.teleToLocation(summoner.getLoc());
        } else {
            player.sendPacket(SystemMsg.NOT_ENOUGH_ITEMS);
        }
    }

    @Override
    public void sayNo() {
        //
    }

    @Override
    public long expireTime() {
        return _timeStamp;
    }
}
