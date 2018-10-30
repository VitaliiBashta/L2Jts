package org.jts.dataparser.data.holder.skilldata.skillacquire;

/**
 * @author KilRoy
 */
public enum AcquireRace {
    race_human,
    race_elf,
    race_dark_elf,
    race_orc,
    race_dwarf,
    race_kamael;

    public int getId() {
        return ordinal();
    }
}