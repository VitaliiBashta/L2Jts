package org.mmocore.gameserver.templates;

import org.mmocore.commons.geometry.CustomPolygon;
import org.mmocore.gameserver.ai.CharacterAI;
import org.mmocore.gameserver.ai.DoorAI;
import org.mmocore.gameserver.data.scripts.Scripts;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

/**
 * @author VISTALL
 * @date 8:35/15.04.2011
 */
public class DoorTemplate extends CharTemplate {
    @SuppressWarnings("unchecked")
    public static final Constructor<DoorAI> DEFAULT_AI_CONSTRUCTOR = (Constructor<DoorAI>) CharacterAI.class.getConstructors()[0];
    private static final Logger _log = LoggerFactory.getLogger(DoorTemplate.class);
    private final int _id;
    private final String _name;
    private final DoorType _doorType;
    private final boolean _unlockable;
    private final boolean _isHPVisible;
    private final boolean _opened;
    private final boolean _targetable;
    private final CustomPolygon _polygon;
    private final Location _loc;
    private final int _key;
    private final int _openTime;
    private final int _rndTime;
    private final int _closeTime;
    private final int _masterDoor;
    private final StatsSet _aiParams;
    private Class<DoorAI> _classAI = DoorAI.class;
    private Constructor<DoorAI> _constructorAI = DEFAULT_AI_CONSTRUCTOR;
    public DoorTemplate(final StatsSet set) {
        super(set);
        _id = set.getInteger("uid");
        _name = set.getString("name");
        _doorType = set.getEnum("door_type", DoorType.class, DoorType.DOOR);
        _unlockable = set.getBool("unlockable", false);
        _isHPVisible = set.getBool("show_hp", false);
        _opened = set.getBool("opened", false);
        _targetable = set.getBool("targetable", true);
        _loc = (Location) set.get("pos");
        _polygon = (CustomPolygon) set.get("shape");
        _key = set.getInteger("key", 0);
        _openTime = set.getInteger("open_time", 0);
        _rndTime = set.getInteger("random_time", 0);
        _closeTime = set.getInteger("close_time", 0);
        _masterDoor = set.getInteger("master_door", 0);
        _aiParams = (StatsSet) set.getObject("ai_params", StatsSet.EMPTY);

        setAI(set.getString("ai", "DoorAI"));
    }

    @SuppressWarnings("unchecked")
    private void setAI(final String ai) {
        Class<DoorAI> classAI = null;
        try {
            classAI = (Class<DoorAI>) Class.forName("org.mmocore.gameserver.ai." + ai);
        } catch (ClassNotFoundException e) {
            classAI = (Class<DoorAI>) Scripts.getInstance().getClasses().get("ai.door." + ai);
        }

        if (classAI == null) {
            _log.error("Not found ai class for ai: " + ai + ". DoorId: " + _id);
        } else {
            _classAI = classAI;
            _constructorAI = (Constructor<DoorAI>) _classAI.getConstructors()[0];
        }

        if (_classAI.isAnnotationPresent(Deprecated.class)) {
            _log.error("Ai type: " + ai + ", is deprecated. DoorId: " + _id);
        }
    }

    public CharacterAI getNewAI(final DoorInstance door) {
        try {
            return _constructorAI.newInstance(door);
        } catch (Exception e) {
            _log.error("Unable to create ai of doorId " + _id, e);
        }

        return new DoorAI(door);
    }

    @Override
    public int getNpcId() {
        return _id;
    }

    public String getName() {
        return _name;
    }

    public DoorType getDoorType() {
        return _doorType;
    }

    public boolean isUnlockable() {
        return _unlockable;
    }

    public boolean isHPVisible() {
        return _isHPVisible;
    }

    public CustomPolygon getPolygon() {
        return _polygon;
    }

    public int getKey() {
        return _key;
    }

    public boolean isOpened() {
        return _opened;
    }

    public Location getLoc() {
        return _loc;
    }

    public int getOpenTime() {
        return _openTime;
    }

    public int getRandomTime() {
        return _rndTime;
    }

    public int getCloseTime() {
        return _closeTime;
    }

    public boolean isTargetable() {
        return _targetable;
    }

    public int getMasterDoor() {
        return _masterDoor;
    }

    public StatsSet getAIParams() {
        return _aiParams;
    }

    public enum DoorType {
        DOOR,
        WALL
    }
}
