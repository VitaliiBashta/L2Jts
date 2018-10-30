package org.mmocore.gameserver.object.components.player;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.serverpackets.ExStorageMaxCount;
import org.mmocore.gameserver.network.lineage.serverpackets.StatusUpdate;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.creatures.recorders.CharStatsChangeRecorder;

import java.util.Objects;

/**
 * @author G1ta0
 */
public final class PlayerStatsChangeRecorder extends CharStatsChangeRecorder<Player> {
    public static final int BROADCAST_KARMA = 1 << 3;
    public static final int SEND_STORAGE_INFO = 1 << 4;
    public static final int SEND_MAX_LOAD = 1 << 5;
    public static final int SEND_CUR_LOAD = 1 << 6;
    private final int[] _attackElement = new int[6];
    private final int[] _defenceElement = new int[6];
    private int _maxCp;
    private int _maxLoad;
    private int _curLoad;
    private long _exp;
    private int _sp;
    private int _karma;
    private int _pk;
    private int _pvp;
    private int _fame;

    private int _inventory;
    private int _warehouse;
    private int _clan;
    private int _trade;
    private int _recipeDwarven;
    private int _recipeCommon;
    private int _partyRoom;

    private String _title = StringUtils.EMPTY;

    private int _cubicsHash;

    public PlayerStatsChangeRecorder(Player activeChar) {
        super(activeChar);
    }

    @Override
    protected void refreshStats() {
        super.refreshStats();

        _maxCp = set(SEND_STATUS_INFO, _maxCp, _activeChar.getMaxCp());

        _maxLoad = set(SEND_CHAR_INFO | SEND_MAX_LOAD, _maxLoad, _activeChar.getMaxLoad());
        _curLoad = set(SEND_CUR_LOAD, _curLoad, _activeChar.getCurrentLoad());

        for (Element e : Element.VALUES) {
            _attackElement[e.getId()] = set(SEND_CHAR_INFO, _attackElement[e.getId()], _activeChar.getAttack(e));
            _defenceElement[e.getId()] = set(SEND_CHAR_INFO, _defenceElement[e.getId()], _activeChar.getDefence(e));
        }

        _exp = set(SEND_CHAR_INFO, _exp, _activeChar.getExp());
        _sp = set(SEND_CHAR_INFO, _sp, _activeChar.getIntSp());
        _pk = set(SEND_CHAR_INFO, _pk, _activeChar.getPkKills());
        _pvp = set(SEND_CHAR_INFO, _pvp, _activeChar.getPvpKills());
        _fame = set(SEND_CHAR_INFO, _fame, _activeChar.getFame());

        _karma = set(BROADCAST_KARMA, _karma, _activeChar.getKarma());

        _inventory = set(SEND_STORAGE_INFO, _inventory, _activeChar.getInventoryLimit());
        _warehouse = set(SEND_STORAGE_INFO, _warehouse, _activeChar.getWarehouseLimit());
        _clan = set(SEND_STORAGE_INFO, _clan, OtherConfig.WAREHOUSE_SLOTS_CLAN);
        _trade = set(SEND_STORAGE_INFO, _trade, _activeChar.getTradeLimit());
        _recipeDwarven = set(SEND_STORAGE_INFO, _recipeDwarven, _activeChar.getDwarvenRecipeLimit());
        _recipeCommon = set(SEND_STORAGE_INFO, _recipeCommon, _activeChar.getCommonRecipeLimit());
        _cubicsHash = set(BROADCAST_CHAR_INFO, _cubicsHash, Objects.hash(_activeChar.getCubics().values()));
        _partyRoom = set(BROADCAST_CHAR_INFO, _partyRoom, _activeChar.getMatchingRoom() != null && _activeChar.getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING && _activeChar.getMatchingRoom().getGroupLeader() == _activeChar ? _activeChar.getMatchingRoom().getId() : 0);
        _title = set(BROADCAST_CHAR_INFO, _title, _activeChar.getTitle());
    }

    @Override
    protected void onSendChanges() {
        super.onSendChanges();

        if ((_changes & BROADCAST_CHAR_INFO) == BROADCAST_CHAR_INFO) {
            _activeChar.broadcastCharInfo();
        } else if ((_changes & SEND_CHAR_INFO) == SEND_CHAR_INFO) {
            _activeChar.sendUserInfo();
        }

        if ((_changes & SEND_CUR_LOAD) == SEND_CUR_LOAD) {
            _activeChar.sendStatusUpdate(false, false, StatusUpdate.CUR_LOAD);
        }

        if ((_changes & SEND_MAX_LOAD) == SEND_MAX_LOAD) {
            _activeChar.sendStatusUpdate(false, false, StatusUpdate.MAX_LOAD);
        }

        if ((_changes & BROADCAST_KARMA) == BROADCAST_KARMA) {
            _activeChar.sendStatusUpdate(true, false, StatusUpdate.KARMA);
        }

        if ((_changes & SEND_STORAGE_INFO) == SEND_STORAGE_INFO) {
            _activeChar.sendPacket(new ExStorageMaxCount(_activeChar));
        }
    }
}