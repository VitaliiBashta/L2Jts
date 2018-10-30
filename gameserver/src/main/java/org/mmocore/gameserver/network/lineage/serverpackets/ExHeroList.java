package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.templates.StatsSet;

import java.util.Map;


/**
 * Format: (ch) d [SdSdSdd]
 * d: size
 * [
 * S: hero name
 * d: hero class ID
 * S: hero clan name
 * d: hero clan crest id
 * S: hero ally name
 * d: hero Ally id
 * d: count
 * ]
 */
public class ExHeroList extends GameServerPacket {
    private final Map<Integer, StatsSet> heroList;

    public ExHeroList() {
        heroList = Hero.getInstance().getHeroes();
    }

    @Override
    protected final void writeData() {
        writeD(heroList.size());
        for (final StatsSet hero : heroList.values()) {
            writeS(hero.getString(Olympiad.CHAR_NAME));
            writeD(hero.getInteger(Olympiad.CLASS_ID));
            writeS(hero.getString(Hero.CLAN_NAME, StringUtils.EMPTY));
            writeD(hero.getInteger(Hero.CLAN_CREST, 0));
            writeS(hero.getString(Hero.ALLY_NAME, StringUtils.EMPTY));
            writeD(hero.getInteger(Hero.ALLY_CREST, 0));
            writeD(hero.getInteger(Hero.COUNT));
        }
    }
}