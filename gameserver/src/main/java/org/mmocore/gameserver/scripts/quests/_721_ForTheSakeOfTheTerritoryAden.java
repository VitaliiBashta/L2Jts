package org.mmocore.gameserver.scripts.quests;

/**
 * @author pchayka
 */
public class _721_ForTheSakeOfTheTerritoryAden extends Dominion_ForTheSakeOfTerritory {
    public _721_ForTheSakeOfTheTerritoryAden() {
        super();
        addLevelCheck(40);
    }

    @Override
    public int getDominionId() {
        return 85;
    }
}
