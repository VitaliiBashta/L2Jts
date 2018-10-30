package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.manager.GameTimeManager;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Location;

public class CharSelected extends GameServerPacket {
    //   SdSddddddddddffddddddddddddddddddddddddddddddddddddddddd d
    private final int sessionId;
    private final int char_id;
    private final int clan_id;
    private final int sex;
    private final int race;
    private final int class_id;
    private final String name;
    private final String title;
    private final Location loc;
    private final double curHp;
    private final double curMp;
    private final int sp;
    private final int level;
    private final int karma;
    private final int _int;
    private final int str;
    private final int con;
    private final int men;
    private final int dex;
    private final int wit;
    private final int pk;
    private final long exp;

    public CharSelected(final Player cha, final int sessionId) {
        this.sessionId = sessionId;

        name = cha.getName();
        char_id = cha.getObjectId(); //FIXME 0x00030b7a ??
        title = cha.getTitle();
        clan_id = cha.getClanId();
        sex = cha.getSex();
        race = cha.getPlayerTemplateComponent().getPlayerRace().ordinal();
        class_id = cha.getPlayerClassComponent().getClassId().getId();
        loc = cha.getLoc();
        curHp = cha.getCurrentHp();
        curMp = cha.getCurrentMp();
        sp = cha.getIntSp();
        exp = cha.getExp();
        level = cha.getLevel();
        karma = cha.getKarma();
        pk = cha.getPkKills();
        _int = cha.getINT();
        str = cha.getSTR();
        con = cha.getCON();
        men = cha.getMEN();
        dex = cha.getDEX();
        wit = cha.getWIT();
    }

    @Override
    protected final void writeData() {
        //SdS dddddddddd ff dQ ddddddddddd
        writeS(name);
        writeD(char_id);
        writeS(title);

        writeD(sessionId);
        writeD(clan_id);
        writeD(0x00); //??
        writeD(sex);
        writeD(race);
        writeD(class_id);
        writeD(0x01); // active ??
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);

        writeF(curHp);
        writeF(curMp);

        writeD(sp);
        writeQ(exp);

        writeD(level);
        writeD(karma); //?
        writeD(pk);
        writeD(_int);
        writeD(str);
        writeD(con);
        writeD(men);
        writeD(dex);
        writeD(wit);
        // extra info
        writeD(GameTimeManager.getInstance().getGameTime()); // in-game time
        writeD(0x00);
        writeD(class_id);
        writeD(0x00);
        writeD(0x00);
        writeD(0x00);
        writeD(0x00);
        writeB(new byte[64]);
        writeD(0x00);
    }
}