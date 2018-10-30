package org.jts.dataparser.data.holder.npcpos;

import org.jts.dataparser.data.annotations.array.StringArray;
import org.jts.dataparser.data.annotations.value.IntValue;
import org.jts.dataparser.data.annotations.value.StringValue;

/**
 * @author : Camelion
 * @date : 30.08.12 20:23
 */
public class DefaultMaker {
    @StringArray(withoutName = true, bounds = {"", ""})
    private String[] territory_names;
    @StringValue
    private String name;
    @StringArray
    private String[] banned_territory;
    @IntValue
    private int maximum_npc;
    @IntValue
    private int flying;

    public String[] getTerritoryNames() {
        return territory_names;
    }

    public String getName() {
        return name;
    }

    public String[] getBannedTerritory() {
        return banned_territory;
    }

    public int getMaximumNpc() {
        return maximum_npc;
    }

    public boolean isFlying() {
        return flying > 0 ? true : false;
    }
}