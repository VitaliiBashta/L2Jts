package org.mmocore.gameserver.scripts.quests;

/**
 * @author pchayka
 */
public class _717_ForTheSakeOfTheTerritoryGludio extends Dominion_ForTheSakeOfTerritory {
    public _717_ForTheSakeOfTheTerritoryGludio() {
        super();
        addLevelCheck(40);
    }

    @Override
    public int getDominionId() {
        return 81;
    }
}
