package org.mmocore.gameserver.model;


import org.mmocore.gameserver.database.dao.impl.ItemsDAO;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;

import java.util.Collection;


/**
 * Used to Store data sent to Client for Character
 * Selection screen.
 *
 * @version $Revision: 1.2.2.2.2.4 $ $Date: 2005/03/27 15:29:33 $
 */
public class CharSelectInfoPackage {
    private final ItemInstance[] _paperdoll;
    public boolean _newChar;
    private String _name;
    private int _objectId = 0;
    private int _charId = 0x00030b7a;
    private long _exp = 0;
    private int _sp = 0;
    private int _clanId = 0;
    private int _race = 0;
    private int _classId = 0;
    private int _baseClassId = 0;
    private int _deleteTimer = 0;
    private long _lastAccess = 0L;
    private int _face = 0;
    private int _hairStyle = 0;
    private int _hairColor = 0;
    private int _sex = 0;
    private int _level = 1;
    private int _karma = 0, _pk = 0, _pvp = 0;
    private int _maxHp = 0;
    private double _currentHp = 0;
    private int _maxMp = 0;
    private double _currentMp = 0;
    private int _accesslevel = 0;
    private int _x = 0, _y = 0, _z = 0;
    private int _vitalityPoints = 20000;
    private boolean _passwordChecked;
    private boolean _passwordEnable;
    private String _password;

    public CharSelectInfoPackage(final int objectId, final String name) {
        setObjectId(objectId);
        _name = name;
        final Collection<ItemInstance> items = ItemsDAO.getInstance().getItemsByOwnerIdAndLoc(objectId, ItemLocation.PAPERDOLL);
        _paperdoll = new ItemInstance[Inventory.PAPERDOLL_MAX];
        items.stream().filter(item -> item.getEquipSlot() < Inventory.PAPERDOLL_MAX).forEach(item -> _paperdoll[item.getEquipSlot()] = item);
    }

    public int getObjectId() {
        return _objectId;
    }

    public void setObjectId(final int objectId) {
        _objectId = objectId;
    }

    public int getCharId() {
        return _charId;
    }

    public void setCharId(final int charId) {
        _charId = charId;
    }

    public int getClanId() {
        return _clanId;
    }

    public void setClanId(final int clanId) {
        _clanId = clanId;
    }

    public int getClassId() {
        return _classId;
    }

    public void setClassId(final int classId) {
        _classId = classId;
    }

    public int getBaseClassId() {
        return _baseClassId;
    }

    public void setBaseClassId(final int baseClassId) {
        _baseClassId = baseClassId;
    }

    public double getCurrentHp() {
        return _currentHp;
    }

    public void setCurrentHp(final double currentHp) {
        _currentHp = currentHp;
    }

    public double getCurrentMp() {
        return _currentMp;
    }

    public void setCurrentMp(final double currentMp) {
        _currentMp = currentMp;
    }

    public int getDeleteTimer() {
        return _deleteTimer;
    }

    public void setDeleteTimer(final int deleteTimer) {
        _deleteTimer = deleteTimer;
    }

    public long getLastAccess() {
        return _lastAccess;
    }

    public void setLastAccess(final long lastAccess) {
        _lastAccess = lastAccess;
    }

    public long getExp() {
        return _exp;
    }

    public void setExp(final long exp) {
        _exp = exp;
    }

    public int getFace() {
        return _face;
    }

    public void setFace(final int face) {
        _face = face;
    }

    public int getHairColor() {
        return _hairColor;
    }

    public void setHairColor(final int hairColor) {
        _hairColor = hairColor;
    }

    public int getHairStyle() {
        return _hairStyle;
    }

    public void setHairStyle(final int hairStyle) {
        _hairStyle = hairStyle;
    }

    public int getPaperdollObjectId(final int slot) {
        final ItemInstance item = _paperdoll[slot];
        if (item != null) {
            return item.getObjectId();
        }
        return 0;
    }

    public int getPaperdollAugmentation1Id(final int slot) {
        final ItemInstance item = _paperdoll[slot];
        if (item != null && item.isAugmented()) {
            return item.getVariation1Id();
        }
        return 0;
    }

    public int getPaperdollAugmentation2Id(final int slot) {
        final ItemInstance item = _paperdoll[slot];
        if (item != null && item.isAugmented()) {
            return item.getVariation2Id();
        }
        return 0;
    }

    public int getPaperdollItemId(final int slot) {
        final ItemInstance item = _paperdoll[slot];
        if (item != null) {
            return item.getItemId();
        }
        return 0;
    }

    public int getPaperdollEnchantEffect(final int slot) {
        final ItemInstance item = _paperdoll[slot];
        if (item != null) {
            return item.getEnchantLevel();
        }
        return 0;
    }

    public int getLevel() {
        return _level;
    }

    public void setLevel(final int level) {
        _level = level;
    }

    public int getMaxHp() {
        return _maxHp;
    }

    public void setMaxHp(final int maxHp) {
        _maxHp = maxHp;
    }

    public int getMaxMp() {
        return _maxMp;
    }

    public void setMaxMp(final int maxMp) {
        _maxMp = maxMp;
    }

    public String getName() {
        return _name;
    }

    public void setName(final String name) {
        _name = name;
    }

    public int getRace() {
        return _race;
    }

    public void setRace(final int race) {
        _race = race;
    }

    public int getSex() {
        return _sex;
    }

    public void setSex(final int sex) {
        _sex = sex;
    }

    public int getSp() {
        return _sp;
    }

    public void setSp(final int sp) {
        _sp = sp;
    }

    public int getKarma() {
        return _karma;
    }

    public void setKarma(final int karma) {
        _karma = karma;
    }

    public int getAccessLevel() {
        return _accesslevel;
    }

    public void setAccessLevel(final int accesslevel) {
        _accesslevel = accesslevel;
    }

    public int getX() {
        return _x;
    }

    public void setX(final int x) {
        _x = x;
    }

    public int getY() {
        return _y;
    }

    public void setY(final int y) {
        _y = y;
    }

    public int getZ() {
        return _z;
    }

    public void setZ(final int z) {
        _z = z;
    }

    public int getPk() {
        return _pk;
    }

    public void setPk(final int pk) {
        _pk = pk;
    }

    public int getPvP() {
        return _pvp;
    }

    public void setPvP(final int pvp) {
        _pvp = pvp;
    }

    public int getVitalityPoints() {
        return _vitalityPoints;
    }

    public void setVitalityPoints(final int points) {
        _vitalityPoints = points;
    }

    public boolean isPasswordEnable() {
        return _passwordEnable;
    }

    public void setPasswordEnable(final boolean passwordEnable) {
        _passwordEnable = passwordEnable;
    }

    public String getPassword() {
        return _password;
    }

    public void setPassword(final String password) {
        _password = password;
    }

    public boolean isPasswordChecked() {
        return _passwordChecked;
    }

    public void setPasswordChecked(final boolean passwordChecked) {
        _passwordChecked = passwordChecked;
    }

    public boolean isNewChar() {
        return _newChar;
    }

    public void setNewChar(final boolean newChar) {
        _newChar = newChar;
    }
}