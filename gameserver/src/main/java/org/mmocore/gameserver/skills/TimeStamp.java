package org.mmocore.gameserver.skills;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;

/**
 * Simple class containing all neccessary information to maintain
 * valid timestamps and reuse for skills upon relog. Filter this
 * carefully as it becomes redundant to store reuse for small delays.
 *
 * @author Yesod
 */
public class TimeStamp {
    private final int _id;
    private final long _reuse;
    private final boolean _decreaseReuse;
    private int _level;
    private long _endTime;

    public TimeStamp(final int id, final long endTime, final long reuse) {
        _id = id;
        _level = 0;
        _reuse = reuse;
        _endTime = endTime;
        _decreaseReuse = false;
    }

    public TimeStamp(final SkillEntry skill, final long reuse) {
        this(skill, System.currentTimeMillis() + reuse, reuse);
    }

    public TimeStamp(final SkillEntry skill, final long endTime, final long reuse) {
        _id = skill.getId();
        _level = skill.getLevel();
        _reuse = reuse;
        _endTime = endTime;
        _decreaseReuse = !skill.getTemplate().isHandler() && !skill.getTemplate().isItemSkill();
    }

    public long getReuseBasic() {
        if (_reuse == 0) {
            return getReuseCurrent();
        }
        return _reuse;
    }

    public long getReuseCurrent() {
        return Math.max(_endTime - System.currentTimeMillis(), 0);
    }

    public long getEndTime() {
        return _endTime;
    }

    public void setEndTime(final long endTime) {
        _endTime = endTime;
    }

    public boolean hasNotPassed() {
        return _endTime - System.currentTimeMillis() >= (_decreaseReuse ? AllSettingsConfig.ALT_REUSE_CORRECTION : 0);
    }

    public int getId() {
        return _id;
    }

    public int getLevel() {
        return _level;
    }

    public void setLevel(final int level) {
        _level = level;
    }
}