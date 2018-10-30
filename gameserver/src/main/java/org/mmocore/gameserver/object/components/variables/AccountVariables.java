package org.mmocore.gameserver.object.components.variables;

/**
 * @author Java-man
 */
public enum AccountVariables implements Variables {
    BUY_LIMITED_NEVITS_VOICE,
    BUY_LIMITED_YOGI_SCROLL;

    @Override
    public boolean isKeepInDatabase() {
        return true;
    }
}
