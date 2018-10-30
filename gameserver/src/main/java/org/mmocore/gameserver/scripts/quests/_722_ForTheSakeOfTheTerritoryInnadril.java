package org.mmocore.gameserver.scripts.quests;

/**
 * @author pchayka
 */
public class _722_ForTheSakeOfTheTerritoryInnadril extends Dominion_ForTheSakeOfTerritory {
    public _722_ForTheSakeOfTheTerritoryInnadril() {
        super();
        addLevelCheck(40);
    }

    @Override
    public int getDominionId() {
        return 86;
    }
}
