package org.mmocore.gameserver.network.lineage.serverpackets;

import org.jts.dataparser.data.holder.petdata.PetUtils;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.skills.AbnormalEffectType;
import org.mmocore.gameserver.utils.Location;

public class PetInfo extends GameServerPacket {
    private final int runSpd;
    private final int walkSpd;
    private final int MAtkSpd;
    private final int PAtkSpd;
    private final int pvp_flag;
    private final int karma;
    private final int rideable;
    private final int type;
    private final int obj_id;
    private final int npc_id;
    private final int runing;
    private final int incombat;
    private final int dead;
    private final int sp;
    private final int level;
    private final int abnormalEffect;
    private final int abnormalEffect2;
    private final int curFed;
    private final int maxFed;
    private final int curHp;
    private final int maxHp;
    private final int curMp;
    private final int maxMp;
    private final int curLoad;
    private final int maxLoad;
    private final int PAtk;
    private final int PDef;
    private final int MAtk;
    private final int MDef;
    private final int Accuracy;
    private final int Evasion;
    private final int Crit;
    private final int sps;
    private final int ss;
    private final int types;
    private final Location loc;
    private final double col_redius;
    private final double col_height;
    private final long exp;
    private final long exp_this_lvl;
    private final long exp_next_lvl;
    private final String name;
    private final String title;
    private final TeamType team;
    private int showSpawnAnimation;

    public PetInfo(final Servitor summon) {
        type = summon.getServitorType();
        obj_id = summon.getObjectId();
        npc_id = summon.getTemplate().npcId;
        loc = summon.getLoc();
        MAtkSpd = summon.getMAtkSpd();
        PAtkSpd = summon.getPAtkSpd();
        runSpd = summon.getRunSpeed();
        walkSpd = summon.getWalkSpeed();
        col_redius = summon.getColRadius();
        col_height = summon.getColHeight();
        runing = summon.isRunning() ? 1 : 0;
        incombat = summon.isInCombat() ? 1 : 0;
        dead = summon.isAlikeDead() ? 1 : 0;
        name = summon.getName().equalsIgnoreCase(summon.getTemplate().name) ? "" : summon.getName();
        title = summon.getTitle();
        pvp_flag = summon.getPvpFlag();
        karma = summon.getKarma();
        curFed = summon.getCurrentFed();
        maxFed = summon.getMaxFed();
        curHp = (int) summon.getCurrentHp();
        maxHp = (int) summon.getMaxHp();
        curMp = (int) summon.getCurrentMp();
        maxMp = summon.getMaxMp();
        sp = summon.getSp();
        level = summon.getLevel();
        exp = summon.getExp();
        exp_this_lvl = summon.getExpForThisLevel();
        exp_next_lvl = summon.getExpForNextLevel();
        curLoad = summon.isPet() ? summon.getInventory().getTotalWeight() : 0;
        maxLoad = summon.getMaxLoad();
        PAtk = summon.getPAtk(null);
        PDef = summon.getPDef(null);
        MAtk = summon.getMAtk(null, null);
        MDef = summon.getMDef(null, null);
        Accuracy = summon.getAccuracy();
        Evasion = summon.getEvasionRate(null);
        Crit = summon.getCriticalHit(null, null);
        abnormalEffect = summon.getAbnormalEffect(AbnormalEffectType.FIRST);
        abnormalEffect2 = summon.getAbnormalEffect(AbnormalEffectType.SECOND);
        // Значек mount/dismount не отображается у саммонов или в режиме трансформации
        if (!summon.isPet() || summon.getPlayer().isTransformed()) {
            rideable = 0; //not rideable
        } else {
            rideable = PetUtils.isMountable(npc_id) ? 1 : 0;
        }
        team = summon.getTeam();
        ss = summon.getSoulshotConsumeCount();
        sps = summon.getSpiritshotConsumeCount();
        showSpawnAnimation = summon.getSpawnAnimation();
        types = summon.getFormId();
    }

    public PetInfo update() {
        showSpawnAnimation = 1;
        return this;
    }

    @Override
    protected void writeData() {
        writeD(type);
        writeD(obj_id);
        writeD(npc_id + 1000000);
        writeD(0); // 1=attackable
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z);
        writeD(loc.h);
        writeD(0);
        writeD(MAtkSpd);
        writeD(PAtkSpd);
        writeD(runSpd);
        writeD(walkSpd);
        writeD(runSpd/*_swimRunSpd*/);
        writeD(walkSpd/*_swimWalkSpd*/);
        writeD(runSpd/*_flRunSpd*/);
        writeD(walkSpd/*_flWalkSpd*/);
        writeD(runSpd/*_flyRunSpd*/);
        writeD(walkSpd/*_flyWalkSpd*/);
        writeF(1/*_cha.getProperMultiplier()*/);
        writeF(1/*_cha.getAttackSpeedMultiplier()*/);
        writeF(col_redius);
        writeF(col_height);
        writeD(0); // right hand weapon
        writeD(0);
        writeD(0); // left hand weapon
        writeC(1); // name above char 1=true ... ??
        writeC(runing); // running=1
        writeC(incombat); // attacking 1=true
        writeC(dead); // dead 1=true
        writeC(showSpawnAnimation); // invisible ?? 0=false  1=true   2=summoned (only works if model has a summon animation)
        writeD(-1);
        writeS(name);
        writeD(-1);
        writeS(title);
        writeD(1);
        writeD(pvp_flag); //0=white, 1=purple, 2=purpleblink, if its greater then karma = purple
        writeD(karma); // hmm karma ??
        writeD(curFed); // how fed it is
        writeD(maxFed); //max fed it can be
        writeD(curHp); //current hp
        writeD(maxHp); // max hp
        writeD(curMp); //current mp
        writeD(maxMp); //max mp
        writeD(sp); //sp
        writeD(level);// lvl
        writeQ(exp);
        writeQ(exp_this_lvl); // 0%  absolute value
        writeQ(exp_next_lvl); // 100% absoulte value
        writeD(curLoad); //weight
        writeD(maxLoad); //max weight it can carry
        writeD(PAtk);//patk
        writeD(PDef);//pdef
        writeD(MAtk);//matk
        writeD(MDef);//mdef
        writeD(Accuracy);//accuracy
        writeD(Evasion);//evasion
        writeD(Crit);//critical
        writeD(runSpd);//speed
        writeD(PAtkSpd);//atkspeed
        writeD(MAtkSpd);//casting speed
        writeD(abnormalEffect); //c2  abnormal visual effect... bleed=1; poison=2; bleed?=4;
        writeH(rideable);
        writeC(0); // c2
        writeH(0); // ??
        writeC(team.ordinal()); // team aura (1 = blue, 2 = red)
        writeD(ss);
        writeD(sps);
        writeD(types);
        writeD(abnormalEffect2);
    }
}