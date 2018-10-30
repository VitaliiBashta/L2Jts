package org.mmocore.gameserver.model.base;

public enum ClassType {
    Fighter, Mystic, Priest, Aditional;

    public boolean isMagician() {
        return this != Fighter;
    }
}