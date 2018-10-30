package org.mmocore.gameserver.scripts.quests;

/**
 * @author pchayka
 */
public class _719_ForTheSakeOfTheTerritoryGiran extends Dominion_ForTheSakeOfTerritory {
    public _719_ForTheSakeOfTheTerritoryGiran() {
        super();
        addLevelCheck(40);
    }

    @Override
    public int getDominionId() {
        return 83;
    }
}
