package org.mmocore.gameserver.model.entity;

import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.TimeUtils;

import java.time.Instant;
import java.util.AbstractMap;
import java.util.Map;

/**
 * @author VISTALL
 * @author Java-man
 * @date 18:13/21.04.2011
 */
public class HeroDiary {
    public static final int ACTION_RAID_KILLED = 1;
    public static final int ACTION_HERO_GAINED = 2;
    public static final int ACTION_CASTLE_TAKEN = 3;

    private final int id;
    private final Instant timestamp;
    private final int parameter;

    public HeroDiary(final int id, final Instant timestamp, final int parameter) {
        this.id = id;
        this.timestamp = timestamp;
        this.parameter = parameter;
    }

    public Map.Entry<String, String> toString(final Player player) {
        final CustomMessage message;
        switch (id) {
            case ACTION_RAID_KILLED:
                message = new CustomMessage("org.mmocore.gameserver.model.entity.Hero.RaidBossKilled").addString(HtmlUtils.htmlNpcName(parameter));
                break;
            case ACTION_HERO_GAINED:
                message = new CustomMessage("org.mmocore.gameserver.model.entity.Hero.HeroGained");
                break;
            case ACTION_CASTLE_TAKEN:
                message = new CustomMessage("org.mmocore.gameserver.model.entity.Hero.CastleTaken").addString(HtmlUtils.htmlResidenceName(parameter));
                break;
            default:
                return null;
        }

        return new AbstractMap.SimpleEntry<>(TimeUtils.dateTimeFormat(timestamp), message.toString(player));
    }
}
