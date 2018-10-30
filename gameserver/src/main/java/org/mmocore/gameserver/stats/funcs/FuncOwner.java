package org.mmocore.gameserver.stats.funcs;

public interface FuncOwner {
    boolean isFuncEnabled();

    boolean overrideLimits();
}