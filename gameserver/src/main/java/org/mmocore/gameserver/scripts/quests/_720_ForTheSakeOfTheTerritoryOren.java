package org.mmocore.gameserver.scripts.quests;

/**
 * @author pchayka
 */
public class _720_ForTheSakeOfTheTerritoryOren extends Dominion_ForTheSakeOfTerritory {
    public _720_ForTheSakeOfTheTerritoryOren() {
        super();
        addLevelCheck(40);
    }

    @Override
    public int getDominionId() {
        return 84;
    }
}
