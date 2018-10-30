package org.mmocore.gameserver.model.items.etcitems;

import org.jts.dataparser.data.holder.TransformHolder;
import org.jts.dataparser.data.holder.transform.TransformData;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.Earthquake;
import org.mmocore.gameserver.network.lineage.serverpackets.ExRedSky;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.Optional;

public class CursedWeapon {
    private final String _name;
    private final int _itemId, _skillMaxLevel;
    private final int _skillId;
    private int _dropRate, _disapearChance;
    private int _durationMin, _durationMax, _durationLost;
    private int _transformationId;
    private int _stageKills, _nbKills = 0, _playerKarma = 0, _playerPkKills = 0;

    private CursedWeaponState _state = CursedWeaponState.NONE;
    private Location _loc = null;
    private long _endTime = 0;
    private ItemInstance _item = null;

    private int _playerObjectId = 0;
    private Player _player = null;

    public CursedWeapon(final int itemId, final String name, final int skillId, final int skillMaxLevel) {
        _name = name;
        _itemId = itemId;
        _skillId = skillId;
        _skillMaxLevel = skillMaxLevel;
    }

    public void initWeapon() {
        zeroOwner();
        setState(CursedWeaponState.NONE);
        _endTime = 0;
        _item = null;
        _nbKills = 0;
    }

    /**
     * Выпадение оружия из монстра
     */
    public void create(final NpcInstance attackable, final Player killer) {
        _item = ItemFunctions.createItem(_itemId);
        if (_item != null) {
            zeroOwner();
            setState(CursedWeaponState.DROPPED);

            if (_endTime == 0) {
                _endTime = System.currentTimeMillis() + getRndDuration() * 60000L;
            }

            _item.dropToTheGround(attackable, Location.findPointToStay(attackable, 100));
            _loc = _item.getLoc();
            _item.setDropTime(0);

            // RedSky and Earthquake
            final L2GameServerPacket redSky = new ExRedSky(10);
            final L2GameServerPacket eq = new Earthquake(killer.getLoc(), 30, 12);
            for (final Player player : GameObjectsStorage.getPlayers()) {
                player.sendPacket(redSky, eq);
            }
        }
    }

    /**
     * Выпадение оружия из владельца, или исчезновение с определенной вероятностью.
     * Вызывается при смерти игрока.
     */
    public boolean dropIt(final NpcInstance attackable, final Player killer, final Player owner) {
        if (Rnd.chance(_disapearChance)) {
            return false;
        }

        Player player = getOnlineOwner();
        if (player == null) {
            if (owner == null) {
                return false;
            }
            player = owner;
        }

        final ItemInstance oldItem;
        if ((oldItem = player.getInventory().removeItemByItemId(_itemId, 1L)) == null) {
            return false;
        }

        player.setKarma(_playerKarma);
        player.setPkKills(_playerPkKills);
        player.setCursedWeaponEquippedId(0);
        player.stopTransformation(false);
        player.validateLocation(0);
        player.abortAttack(true, false);
        zeroOwner();
        setState(CursedWeaponState.DROPPED);

        oldItem.dropToTheGround(player, Location.findPointToStay(player, 100));
        _loc = oldItem.getLoc();

        oldItem.setDropTime(0);
        _item = oldItem;

        player.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_DROPPED_S1).addItemName(oldItem.getItemId()));
        player.broadcastUserInfo(true);
        player.broadcastPacket(new Earthquake(player.getLoc(), 30, 12));

        return true;
    }

    private void giveSkill(final Player player) {

        int level = 1 + _nbKills / _stageKills;
        if (level > _skillMaxLevel) {
            level = _skillMaxLevel;
        }
        final SkillEntry skill = SkillTable.getInstance().getSkillEntry(_skillId, level);
        player.getTransformation().addSkill(skill, true, true);
    }

    /**
     * вызывается при загрузке оружия
     */
    public boolean reActivate() {
        if (getTimeLeft() <= 0) {
            if (getPlayerId() != 0) // to be sure, that cursed weapon will deleted in right way
            {
                setState(CursedWeaponState.ACTIVATED);
            }
            return false;
        }

        if (getPlayerId() == 0) {
            if (_loc == null || (_item = ItemFunctions.createItem(_itemId)) == null) {
                return false;
            }

            _item.dropMe(null, _loc);
            _item.setDropTime(0);

            setState(CursedWeaponState.DROPPED);
        } else {
            setState(CursedWeaponState.ACTIVATED);
        }
        return true;
    }

    public void activate(final Player player, final ItemInstance item) {
        if (isDropped() || getPlayerId() != player.getObjectId()) // оружие уже в руках игрока или новый игрок
        {
            setPlayerId(player.getObjectId());
            setPlayerKarma(player.getKarma());
            setPlayerPkKills(player.getPkKills());
        }

        setPlayer(player);
        setState(CursedWeaponState.ACTIVATED);

        player.leaveParty();
        if (player.isMounted()) {
            player.dismount();
        }

        _item = item;

        player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
        player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_RHAND, null);
        player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_RHAND, _item);

        player.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_EQUIPPED_YOUR_S1).addItemName(_item.getItemId()));

        player.stopTransformation();
        player.setCursedWeaponEquippedId(_itemId);
        player.setLoc(player.getLoc().changeZ(30));
        final Optional<TransformData> transform = TransformHolder.getInstance().getTransformId(getTransformationId());
        player.setTransformation(transform.get());
        //player.setTransformation(_transformationId);
        //player.setTransformationName(_transformationName);
        //player.setTransformationTemplate(_transformationTemplateId);
        player.setKarma(9999999);
        player.setPkKills(_nbKills);

        if (_endTime == 0) {
            _endTime = System.currentTimeMillis() + getRndDuration() * 60000L;
        }

        giveSkill(player);

        player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
        player.setCurrentCp(player.getMaxCp());
        player.broadcastUserInfo(true);
    }

    public void increaseKills() {
        final Player player = getOnlineOwner();
        if (player == null) {
            return;
        }

        _nbKills++;
        player.setPkKills(_nbKills);
        player.updateStats();
        if (_nbKills % _stageKills == 0 && _nbKills <= _stageKills * (_skillMaxLevel - 1)) {
            giveSkill(player);
        }
        _endTime -= _durationLost * 60000L; // Reduce time-to-live
    }

    public void setDisapearChance(final int disapearChance) {
        _disapearChance = disapearChance;
    }

    public void setDurationMin(final int duration) {
        _durationMin = duration;
    }

    public void setDurationMax(final int duration) {
        _durationMax = duration;
    }

    public void setDurationLost(final int durationLost) {
        _durationLost = durationLost;
    }

    public int getTransformationId() {
        return _transformationId;
    }

    public void setTransformationId(final int transformationId) {
        _transformationId = transformationId;
    }

    private void zeroOwner() {
        _player = null;
        _playerObjectId = 0;
        _playerKarma = 0;
        _playerPkKills = 0;
    }

    public CursedWeaponState getState() {
        return _state;
    }

    public void setState(final CursedWeaponState state) {
        _state = state;
    }

    public boolean isActivated() {
        return getState() == CursedWeaponState.ACTIVATED;
    }

    public boolean isDropped() {
        return getState() == CursedWeaponState.DROPPED;
    }

    public long getEndTime() {
        return _endTime;
    }

    public void setEndTime(final long endTime) {
        _endTime = endTime;
    }

    public String getName() {
        return _name;
    }

    public int getItemId() {
        return _itemId;
    }

    public ItemInstance getItem() {
        return _item;
    }

    public void setItem(final ItemInstance item) {
        _item = item;
    }

    public int getSkillId() {
        return _skillId;
    }

    public int getDropRate() {
        return _dropRate;
    }

    public void setDropRate(final int dropRate) {
        _dropRate = dropRate;
    }

    public int getPlayerId() {
        return _playerObjectId;
    }

    public void setPlayerId(final int playerId) {
        _playerObjectId = playerId;
    }

    public Player getPlayer() {
        return _player;
    }

    public void setPlayer(final Player player) {
        _player = player;
    }

    public int getPlayerKarma() {
        return _playerKarma;
    }

    public void setPlayerKarma(final int playerKarma) {
        _playerKarma = playerKarma;
    }

    public int getPlayerPkKills() {
        return _playerPkKills;
    }

    public void setPlayerPkKills(final int playerPkKills) {
        _playerPkKills = playerPkKills;
    }

    public int getNbKills() {
        return _nbKills;
    }

    public void setNbKills(final int nbKills) {
        _nbKills = nbKills;
    }

    public int getStageKills() {
        return _stageKills;
    }

    public void setStageKills(final int stageKills) {
        _stageKills = stageKills;
    }

    /**
     * Возвращает позицию (x, y, z)
     *
     * @return Location
     */
    public Location getLoc() {
        return _loc;
    }

    public void setLoc(final Location loc) {
        _loc = loc;
    }

    public int getRndDuration() {
        if (_durationMin > _durationMax) {
            _durationMax = 2 * _durationMin;
        }
        return Rnd.get(_durationMin, _durationMax);
    }

    public boolean isActive() {
        return isActivated() || isDropped();
    }

    public int getLevel() {
        return Math.min(1 + _nbKills / _stageKills, _skillMaxLevel);
    }

    public long getTimeLeft() {
        return _endTime - System.currentTimeMillis();
    }

    public Location getWorldPosition() {
        if (isActivated()) {
            final Player player = getOnlineOwner();
            if (player != null) {
                return player.getLoc();
            }
        } else if (isDropped()) {
            if (_item != null) {
                return _item.getLoc();
            }
        }

        return null;
    }

    public Player getOnlineOwner() {
        final Player player = getPlayer();
        return player != null && player.isOnline() ? player : null;
    }

    public enum CursedWeaponState {
        NONE,
        ACTIVATED,
        DROPPED,
    }
}