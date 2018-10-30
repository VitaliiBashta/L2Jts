package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;

public class PledgeShowInfoUpdate extends GameServerPacket {
    private final int clan_id;
    private final int clan_level;
    private final int clan_rank;
    private final int clan_rep;
    private final int crest_id;
    private final int ally_id;
    private final int atwar;
    private final int territorySide;
    private final int HasCastle;
    private final int HasHideout;
    private final int HasFortress;
    private int ally_crest;
    private String ally_name = StringUtils.EMPTY;

    public PledgeShowInfoUpdate(final Clan clan) {
        clan_id = clan.getClanId();
        clan_level = clan.getLevel();
        HasCastle = clan.getCastle();
        HasHideout = clan.getHasHideout();
        HasFortress = clan.getHasFortress();
        clan_rank = clan.getRank();
        clan_rep = clan.getReputationScore();
        crest_id = clan.getCrestId();
        ally_id = clan.getAllyId();
        atwar = clan.isAtWar();
        territorySide = clan.getWarDominion();
        final Alliance ally = clan.getAlliance();
        if (ally != null) {
            ally_name = ally.getAllyName();
            ally_crest = ally.getAllyCrestId();
        }
    }

    @Override
    protected final void writeData() {
        //sending empty data so client will ask all the info in response ;)
        writeD(clan_id);
        writeD(crest_id);
        writeD(clan_level);
        writeD(HasCastle);
        writeD(HasHideout);
        writeD(HasFortress);
        writeD(clan_rank);// displayed in the "tree" view (with the clan skills)
        writeD(clan_rep);
        writeD(0);
        writeD(0);
        writeD(ally_id); //c5
        writeS(ally_name); //c5
        writeD(ally_crest); //c5
        writeD(atwar); //c5

        writeD(0x00);
        writeD(territorySide);
    }
}