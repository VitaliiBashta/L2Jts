package org.mmocore.gameserver.scripts.quests;

/**
 * @author pchayka
 */
public class _723_ForTheSakeOfTheTerritoryGoddard extends Dominion_ForTheSakeOfTerritory {
    public _723_ForTheSakeOfTheTerritoryGoddard() {
        super();
        addLevelCheck(40);
    }

    @Override
    public int getDominionId() {
        return 87;
    }

}
