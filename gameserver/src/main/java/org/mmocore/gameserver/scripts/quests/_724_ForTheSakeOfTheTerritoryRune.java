package org.mmocore.gameserver.scripts.quests;

/**
 * @author pchayka
 */
public class _724_ForTheSakeOfTheTerritoryRune extends Dominion_ForTheSakeOfTerritory {
    public _724_ForTheSakeOfTheTerritoryRune() {
        super();
        addLevelCheck(40);
    }

    @Override
    public int getDominionId() {
        return 88;
    }


}
