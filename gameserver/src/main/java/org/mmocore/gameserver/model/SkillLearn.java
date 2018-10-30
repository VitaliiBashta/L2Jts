package org.mmocore.gameserver.model;

/**
 * @author VISTALL
 */
public final class SkillLearn implements Comparable<SkillLearn> {
    private final int _id;
    private final int _level;
    private final int _minLevel;
    private final int _cost;
    private final int _itemId;
    private final long _itemCount;
    private final boolean _clicked;

    public SkillLearn(final int id, final int lvl, final int minLvl, final int cost, final int itemId, final long itemCount, final boolean clicked) {
        _id = id;
        _level = lvl;
        _minLevel = minLvl;
        _cost = cost;

        _itemId = itemId;
        _itemCount = itemCount;
        _clicked = clicked;
    }

    public int getId() {
        return _id;
    }

    public int getLevel() {
        return _level;
    }

    public int getMinLevel() {
        return _minLevel;
    }

    public int getCost() {
        return _cost;
    }

    public int getItemId() {
        return _itemId;
    }

    public long getItemCount() {
        return _itemCount;
    }

    public boolean isClicked() {
        return _clicked;
    }

    @Override
    public int compareTo(final SkillLearn o) {
        if (getId() == o.getId()) {
            return getLevel() - o.getLevel();
        } else {
            return getId() - o.getId();
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final SkillLearn that = (SkillLearn) o;

        if (_id != that._id) {
            return false;
        }
        if (_level != that._level) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = _id;
        result = 31 * result + _level;
        return result;
    }
}