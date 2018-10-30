package org.mmocore.gameserver.templates.client;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.templates.StatsSet;

/**
 * Create by Mangol on 20.12.2015.
 */
public class EtcitemgrpLine {
    private final int id;
    private final String icon0;
    private final String icon1;
    private final String icon2;
    private final String icon3;
    private final String icon4;

    public EtcitemgrpLine(final int id, final StatsSet statsSet) {
        this.id = id;
        icon0 = statsSet.getString("icon0", StringUtils.EMPTY);
        icon1 = statsSet.getString("icon1", StringUtils.EMPTY);
        icon2 = statsSet.getString("icon2", StringUtils.EMPTY);
        icon3 = statsSet.getString("icon3", StringUtils.EMPTY);
        icon4 = statsSet.getString("icon4", StringUtils.EMPTY);
    }

    public int getId() {
        return id;
    }

    public String getIcon0() {
        return icon0;
    }

    public String getIcon1() {
        return icon1;
    }

    public String getIcon2() {
        return icon2;
    }

    public String getIcon3() {
        return icon3;
    }

    public String getIcon4() {
        return icon4;
    }
}
