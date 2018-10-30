package org.mmocore.gameserver.scripts.quests;

/**
 * @author pchayka
 */
public class _718_ForTheSakeOfTheTerritoryDion extends Dominion_ForTheSakeOfTerritory {
    public _718_ForTheSakeOfTheTerritoryDion() {
        super();
        addLevelCheck(40);
    }

    @Override
    public int getDominionId() {
        return 82;
    }
}
