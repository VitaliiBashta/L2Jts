package org.mmocore.gameserver.network.lineage.serverpackets;

import com.google.common.primitives.Ints;
import org.jts.dataparser.data.holder.ExpDataHolder;
import org.mmocore.gameserver.configuration.config.EventsConfig;
import org.mmocore.gameserver.configuration.config.GeodataConfig;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.manager.CursedWeaponsManager;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.cubicdata.CubicComponent;
import org.mmocore.gameserver.skills.AbnormalEffectType;
import org.mmocore.gameserver.utils.Location;

import java.util.Map;

public class UserInfo extends GameServerPacket {
    private final boolean partyRoom;
    private final int runSpd;
    private final int walkSpd;
    private final int swimRunSpd;
    private final int swimWalkSpd;
    private final int flyRunSpd;
    private final int flyWalkSpd;
    private final int rideRunSpd;
    private final int rideWalkSpd;
    private final double move_speed;
    private final double attack_speed;
    private final double col_radius;
    private final double col_height;
    private final int[][] inv;
    private final Location loc;
    private final Location fishLoc;
    private final int fishing;
    private final int obj_id;
    private final int vehicle_obj_id;
    private final int race;
    private final int sex;
    private final int base_class;
    private final int level;
    private final int curCp;
    private final int maxCp;
    private final int enchant;
    private final int weaponFlag;
    private final long exp;
    private final int curHp;
    private final int maxHp;
    private final int curMp;
    private final int maxMp;
    private final int curLoad;
    private final int maxLoad;
    private final int rec_left;
    private final int rec_have;
    private final int str;
    private final int con;
    private final int dex;
    private final int _int;
    private final int wit;
    private final int men;
    private final int sp;
    private final int ClanPrivs;
    private final int InventoryLimit;
    private final int patk;
    private final int patkspd;
    private final int pdef;
    private final int evasion;
    private final int accuracy;
    private final int crit;
    private final int matk;
    private final int matkspd;
    private final int mdef;
    private final int pvp_flag;
    private final int karma;
    private final int hair_style;
    private final int hair_color;
    private final int face;
    private final int gm_commands;
    private final int fame;
    private final int vitality;
    private final int private_store;
    private final int can_crystalize;
    private final int pk_kills;
    private final int pvp_kills;
    private final int class_id;
    private final int agathion;
    private final int abnormalEffect;
    private final int abnormalEffect2;
    private final int noble;
    private final int hero;
    private final int mount_id;
    private final int cw_level;
    private final int name_color;
    private final int running;
    private final int pledge_class;
    private final int pledge_type;
    private final int transformation;
    private final int defenceFire;
    private final int defenceWater;
    private final int defenceWind;
    private final int defenceEarth;
    private final int defenceHoly;
    private final int defenceUnholy;
    private final int mount_type;
    private final Map<Integer, CubicComponent> cubics;
    private final Element attackElement;
    private final int attackElementValue;
    private final boolean isInsideWater, isFlying;
    private final boolean activeRelation;
    private final int talisman_count;
    private final boolean cloak;
    private final double expPercent;
    private final TeamType team;
    private boolean can_writeImpl = false;
    private int relation;
    private int clan_id;
    private int clan_crest_id;
    private int ally_id;
    private int ally_crest_id;
    private int large_clan_crest_id;
    private final int title_color;
    private String name;
    private String title;

    public UserInfo(final Player player) {
        if (player.isTransformed() && player.isCursedWeaponEquipped()) {
            name = player.getTransformation().getNameTransform();
            clan_crest_id = 0;
            ally_crest_id = 0;
            large_clan_crest_id = 0;
            cw_level = CursedWeaponsManager.getInstance().getLevel(player.getCursedWeaponEquippedId());
        } else {
            name = player.getName();

            final Clan clan = player.getClan();
            final Alliance alliance = clan == null ? null : clan.getAlliance();
            //
            clan_id = clan == null ? 0 : clan.getClanId();
            clan_crest_id = clan == null ? 0 : clan.getCrestId();
            large_clan_crest_id = clan == null ? 0 : clan.getCrestLargeId();
            //
            ally_id = alliance == null ? 0 : alliance.getAllyId();
            ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();

            cw_level = 0;
            title = player.getTitle();
        }

        if (player.isPolymorphed()) {
            if (NpcHolder.getInstance().getTemplate(player.getPolyId()) != null) {
                title += " - " + NpcHolder.getInstance().getTemplate(player.getPolyId()).name;
            } else {
                title += " - Polymorphed";
            }
        }

        if (player.getPlayerAccess().GodMode && player.isInvisible())
            title += "[I]";

        if (player.isMounted()) {
            enchant = 0;
            mount_id = player.getMountNpcId() + 1000000;
            mount_type = player.getMountType();
        } else {
            enchant = player.getEnchantEffect();
            mount_id = 0;
            mount_type = 0;
        }

        weaponFlag = player.getActiveWeaponInstance() == null ? 0x14 : 0x28;

        attack_speed = player.getAttackSpeedMultiplier();
        move_speed = player.getMovementSpeedMultiplier();
        matkspd = player.getMAtkSpd();
        patkspd = player.getPAtkSpd();
        runSpd = (int) (player.getRunSpeed() / move_speed);
        walkSpd = (int) (player.getWalkSpeed() / move_speed);
        flyRunSpd = player.isFlying() ? runSpd : Ints.checkedCast(Math.round(player.getPlayerTemplateComponent().getMovingSpeed()[5]));
        flyWalkSpd = player.isFlying() ? walkSpd : Ints.checkedCast(Math.round(player.getPlayerTemplateComponent().getMovingSpeed()[4]));
        rideRunSpd = player.isMounted() ? runSpd : Ints.checkedCast(Math.round(player.getPlayerTemplateComponent().getMovingSpeed()[7]));
        rideWalkSpd = player.isMounted() ? walkSpd : Ints.checkedCast(Math.round(player.getPlayerTemplateComponent().getMovingSpeed()[6]));
        swimRunSpd = (int) Math.round(player.getSwimRunSpeed() / player.getSwimSpeedMultiplier());
        swimWalkSpd = (int) Math.round(player.getSwimWalkSpeed() / player.getSwimSpeedMultiplier());

        inv = new int[Inventory.PAPERDOLL_MAX][4];
        for (final int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER) {
            inv[PAPERDOLL_ID][0] = player.getInventory().getPaperdollObjectId(PAPERDOLL_ID);
            inv[PAPERDOLL_ID][1] = player.getInventory().getPaperdollItemId(PAPERDOLL_ID);
            inv[PAPERDOLL_ID][2] = player.getInventory().getPaperdollVariation1Id(PAPERDOLL_ID);
            inv[PAPERDOLL_ID][3] = player.getInventory().getPaperdollVariation2Id(PAPERDOLL_ID);
        }

        if (player.getClan() != null) {
            relation |= RelationChanged.USER_RELATION_CLAN_MEMBER;
            if (player.isClanLeader()) {
                relation |= RelationChanged.USER_RELATION_CLAN_LEADER;
            }
        }

        for (final Event e : player.getEvents()) {
            relation = e.getUserRelation(player, relation);
        }
        title = player.getCustomPlayerComponent().getTitleName().isEmpty() ? title : player.getCustomPlayerComponent().getTitleName();
        loc = player.getLoc();
        obj_id = player.getObjectId();
        vehicle_obj_id = player.isInBoat() ? player.getBoat().getObjectId() : 0x00;
        race = player.getPlayerTemplateComponent().getPlayerRace().ordinal();
        sex = player.getSex();
        base_class = player.getPlayerClassComponent().getBaseClassId();
        level = player.getLevel();
        exp = player.getExp();
        expPercent = ExpDataHolder.getInstance().getExpPercent(player.getLevel(), player.getExp());
        str = player.getSTR();
        dex = player.getDEX();
        con = player.getCON();
        _int = player.getINT();
        wit = player.getWIT();
        men = player.getMEN();
        curHp = (int) player.getCurrentHp();
        maxHp = (int) player.getMaxHp();
        curMp = (int) player.getCurrentMp();
        maxMp = player.getMaxMp();
        curLoad = player.getCurrentLoad();
        maxLoad = player.getMaxLoad();
        sp = player.getIntSp();
        patk = player.getPAtk(null);
        pdef = player.getPDef(null);
        evasion = player.getEvasionRate(null);
        accuracy = player.getAccuracy();
        crit = player.getCriticalHit(null, null);
        matk = player.getMAtk(null, null);
        mdef = player.getMDef(null, null);
        pvp_flag = player.getPvpFlag(); // 0=white, 1=purple, 2=purpleblink
        karma = player.getKarma();
        col_radius = player.getColRadius();
        col_height = player.getColHeight();
        hair_style = player.getCustomPlayerComponent().getHairStyleWear() > -1 ? player.getCustomPlayerComponent().getHairStyleWear() : player.getAppearanceComponent().getHairStyle();
        hair_color = player.getCustomPlayerComponent().getHairColorWear() > -1 ? player.getCustomPlayerComponent().getHairColorWear() : player.getAppearanceComponent().getHairColor();
        face = player.getCustomPlayerComponent().getFaceWear() > -1 ? player.getCustomPlayerComponent().getFaceWear() : player.getAppearanceComponent().getFace();
        gm_commands = player.isGM() || player.getPlayerAccess().CanUseGMCommand ? 1 : 0;
        // builder level активирует в клиенте админские команды
        clan_id = player.getClanId();
        ally_id = player.getAllyId();
        private_store = player.getPrivateStoreType();
        can_crystalize = player.getSkillLevel(Skill.SKILL_CRYSTALLIZE) > 0 ? 1 : 0;
        pk_kills = player.getPkKills();
        pvp_kills = player.getPvpKills();
        cubics = player.getCubics();
        abnormalEffect = player.getAbnormalEffect(AbnormalEffectType.FIRST);
        abnormalEffect2 = player.getAbnormalEffect(AbnormalEffectType.SECOND);
        ClanPrivs = player.getClanPrivileges();
        rec_left = player.getRecommendationComponent().getRecomLeft(); //c2 recommendations remaining
        rec_have = player.getRecommendationComponent().getRecomHave(); //c2 recommendations received
        InventoryLimit = player.getInventoryLimit();
        class_id = player.getPlayerClassComponent().getClassId().getId();
        maxCp = player.getMaxCp();
        curCp = (int) player.getCurrentCp();
        team = player.getTeam();
        noble = player.isNoble() || player.isGM() && OtherConfig.GM_HERO_AURA ? 1 : 0; //0x01: symbol on char menu ctrl+I
        hero = (player.isHero() || player.getCustomPlayerComponent().isTemporalHero()) || player.isGM() && OtherConfig.GM_HERO_AURA ? 1 : 0; //0x01: Hero Aura and symbol
        fishing = player.isFishing() ? 1 : 0;
        fishLoc = player.getFishLoc();
        name = player.getCustomPlayerComponent().getName().isEmpty() ? name : player.getCustomPlayerComponent().getName();
        name_color = player.getCustomPlayerComponent().getNameColor() == -1 ? player.getAppearanceComponent().getNameColor() : player.getCustomPlayerComponent().getNameColor();
        running = player.isRunning() ? 0x01 : 0x00; //changes the Speed display on Status Window
        pledge_class = player.getPledgeClass();
        pledge_type = player.getPledgeType();
        title_color = player.getCustomPlayerComponent().getTitleColor() == -1 ? player.getAppearanceComponent().getTitleColor() : player.getCustomPlayerComponent().getTitleColor();
        transformation = player.isTransformed() ? player.getTransformation().getData().id : 0;
        attackElement = player.getAttackElement();
        attackElementValue = player.getAttack(attackElement);
        defenceFire = player.getDefence(Element.FIRE);
        defenceWater = player.getDefence(Element.WATER);
        defenceWind = player.getDefence(Element.WIND);
        defenceEarth = player.getDefence(Element.EARTH);
        defenceHoly = player.getDefence(Element.HOLY);
        defenceUnholy = player.getDefence(Element.UNHOLY);
        agathion = player.getAgathionNpcId();
        fame = player.getFame();
        vitality = (int) player.getVitalityComponent().getVitality();
        partyRoom = player.getMatchingRoom() != null && player.getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING && player.getMatchingRoom().getGroupLeader() == player;
        isInsideWater = player.isInWater();
        isFlying = player.isInFlyingTransform();
        talisman_count = player.getTalismanCount();
        cloak = player.getOpenCloak();
        activeRelation = player.isUserRelationActive();

        can_writeImpl = true;

        if (player.isInLastHero()) {
            name = !EventsConfig.LhCustomName.isEmpty() ? EventsConfig.LhCustomName : name;
            title = !EventsConfig.LhCustomTitle.isEmpty() ? EventsConfig.LhCustomTitle : title;
            if (EventsConfig.LhHideClanCrests) {
                clan_crest_id = 0;
                ally_crest_id = 0;
                large_clan_crest_id = 0;
            }
        }
    }

    @Override
    protected final void writeData() {
        if (!can_writeImpl) {
            return;
        }
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z + GeodataConfig.CLIENT_Z_SHIFT);
        writeD(vehicle_obj_id);
        writeD(obj_id);
        writeS(name);
        writeD(race);
        writeD(sex);
        writeD(base_class);
        writeD(level);
        writeQ(exp);
        writeF(expPercent);
        writeD(str);
        writeD(dex);
        writeD(con);
        writeD(_int);
        writeD(wit);
        writeD(men);
        writeD(maxHp);
        writeD(curHp);
        writeD(maxMp);
        writeD(curMp);
        writeD(sp);
        writeD(curLoad);
        writeD(maxLoad);
        writeD(weaponFlag);

        for (final int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER) {
            writeD(inv[PAPERDOLL_ID][0]);
        }

        for (final int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER) {
            writeD(inv[PAPERDOLL_ID][1]);
        }

        for (final int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER) {
            writeH(inv[PAPERDOLL_ID][2]);
            writeH(inv[PAPERDOLL_ID][3]);
        }

        writeD(talisman_count);
        writeD(cloak ? 0x01 : 0x00);

        writeD(patk);
        writeD(patkspd);
        writeD(pdef);
        writeD(evasion);
        writeD(accuracy);
        writeD(crit);
        writeD(matk);
        writeD(matkspd);
        writeD(patkspd);
        writeD(mdef);
        writeD(pvp_flag);
        writeD(karma);

        writeD(runSpd);
        writeD(walkSpd);
        writeD(swimRunSpd);
        writeD(swimWalkSpd);
        writeD(rideRunSpd);
        writeD(rideWalkSpd);
        writeD(flyRunSpd);
        writeD(flyWalkSpd);
        writeF(move_speed);
        writeF(attack_speed);

        writeF(col_radius);
        writeF(col_height);
        writeD(hair_style);
        writeD(hair_color);
        writeD(face);
        writeD(gm_commands);
        writeS(title);
        writeD(clan_id);
        writeD(clan_crest_id);
        writeD(ally_id);
        writeD(ally_crest_id);
        writeD(relation);
        writeC(mount_type); // mount type
        writeC(private_store);
        writeC(can_crystalize);
        writeD(pk_kills);
        writeD(pvp_kills);
        writeH(cubics.size());
        for (final CubicComponent cubic : cubics.values()) {
            writeH(cubic == null ? 0 : cubic.getId());
        }
        writeC(partyRoom ? 0x01 : 0x00); //1-find party members
        writeD(abnormalEffect);
        writeC(isInsideWater ? 0x01 : isFlying ? 0x02 : 0x00);
        writeD(ClanPrivs);
        writeH(rec_left);
        writeH(rec_have);
        writeD(mount_id);
        writeH(InventoryLimit);
        writeD(class_id);
        writeD(0x00); // FIXME special effects? circles around player...
        writeD(maxCp);
        writeD(curCp);
        writeC(enchant);
        writeC(team.ordinal());
        writeD(large_clan_crest_id);
        writeC(noble);
        writeC(hero);
        writeC(fishing);
        writeD(fishLoc.x);
        writeD(fishLoc.y);
        writeD(fishLoc.z);
        writeD(name_color);
        writeC(running);
        writeD(pledge_class);
        writeD(pledge_type);
        writeD(title_color);
        writeD(cw_level);
        writeD(transformation); // Transformation id
        writeH(attackElement.getId());   // AttackElement (0 - Fire, 1 - Water, 2 - Wind, 3 - Earth, 4 - Holy, 5 - Dark, -2 - None)
        writeH(attackElementValue); // AttackElementValue
        writeH(defenceFire); // DefAttrFire
        writeH(defenceWater); // DefAttrWater
        writeH(defenceWind); // DefAttrWind
        writeH(defenceEarth); // DefAttrEarth
        writeH(defenceHoly); // DefAttrHoly
        writeH(defenceUnholy); // DefAttrUnholy
        writeD(agathion);
        writeD(fame); // Fame
        writeD(activeRelation);  // if 1 the relation flag is grey
        writeD(vitality); // Vitality Points
        writeD(abnormalEffect2);
    }
}