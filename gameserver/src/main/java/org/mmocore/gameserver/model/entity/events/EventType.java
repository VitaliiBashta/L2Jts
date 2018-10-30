package org.mmocore.gameserver.model.entity.events;


public enum EventType {
    MAIN_EVENT, // 1 - Dominion Siege Runner, 2 - Underground Coliseum Event Runner, 3 - Kratei Cube Runner
    SIEGE_EVENT,// Siege of Castle, Fortress, Clan Hall, Dominion
    PVP_EVENT, // Kratei Cube, Cleft, Underground Coliseum
    BOAT_EVENT, //
    NC_SOFT_EVENT,
    FUN_EVENT; //

    private final int step;

    EventType() {
        step = ordinal() * 1000;
    }

    public int step() {
        return step;
    }
}
