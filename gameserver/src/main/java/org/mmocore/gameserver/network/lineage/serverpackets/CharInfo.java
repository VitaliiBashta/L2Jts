package org.mmocore.gameserver.network.lineage.serverpackets;

import com.google.common.primitives.Ints;
import org.jts.dataparser.data.holder.setting.common.PlayerSex;
import org.mmocore.gameserver.configuration.config.EventsConfig;
import org.mmocore.gameserver.configuration.config.GeodataConfig;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.data.client.holder.NpcNameLineHolder;
import org.mmocore.gameserver.data.client.holder.TransformDataLineHolder;
import org.mmocore.gameserver.manager.CursedWeaponsManager;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.instances.DecoyInstance;
import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.team.matching.MatchingRoom;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.cubicdata.CubicComponent;
import org.mmocore.gameserver.object.components.player.inventory.PcInventory;
import org.mmocore.gameserver.skills.AbnormalEffectType;
import org.mmocore.gameserver.templates.client.NpcNameLine;
import org.mmocore.gameserver.templates.client.TransformDataLine;
import org.mmocore.gameserver.utils.Language;
import org.mmocore.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public class CharInfo extends GameServerPacket {
    public static final int[] PAPERDOLL_ORDER = {Inventory.PAPERDOLL_UNDER, Inventory.PAPERDOLL_HEAD, Inventory.PAPERDOLL_RHAND, Inventory.PAPERDOLL_LHAND, Inventory.PAPERDOLL_GLOVES, Inventory.PAPERDOLL_CHEST, Inventory.PAPERDOLL_LEGS, Inventory.PAPERDOLL_FEET, Inventory.PAPERDOLL_BACK, Inventory.PAPERDOLL_LRHAND, Inventory.PAPERDOLL_HAIR, Inventory.PAPERDOLL_DHAIR, Inventory.PAPERDOLL_RBRACELET, Inventory.PAPERDOLL_LBRACELET, Inventory.PAPERDOLL_DECO1, Inventory.PAPERDOLL_DECO2, Inventory.PAPERDOLL_DECO3, Inventory.PAPERDOLL_DECO4, Inventory.PAPERDOLL_DECO5, Inventory.PAPERDOLL_DECO6, Inventory.PAPERDOLL_BELT};
    private static final Logger logger = LoggerFactory.getLogger(CharInfo.class);
    private int[][] inv;
    private int mAtkSpd, pAtkSpd;
    private int runSpd, walkSpd, swimRunSpd, swimWalkSpd, flyRunSpd, flyWalkSpd, rideRunSpd, rideWalkSpd;
    private Location loc, fishLoc;
    private String name, title;
    private int objId, race, sex, base_class, pvp_flag, karma, rec_have, talisman_count;
    private double speed_move, speed_atack, col_radius, col_height;
    private int hair_style, hair_color, face, abnormalEffect, abnormalEffect2;
    private int clan_id, clan_crest_id, large_clan_crest_id, ally_id, ally_crest_id, class_id;
    private int sit, run, combat, dead, private_store, enchant;
    private int noble, hero, fishing, mount_type;
    private int plg_class, pledge_type, clan_rep_score, cw_level, mount_id;
    private int nameColor, title_color, transform, agathion, clanBoatObjectId;
    private Map<Integer, CubicComponent> cubics;
    private boolean isPartyRoomLeader, isInsideWater, isFlying, activeRelation, cloak, invisible;
    private TeamType team;

    public CharInfo(final Player cha) {
        this((Creature) cha);
    }

    public CharInfo(final DecoyInstance cha) {
        this((Creature) cha);
    }

    public CharInfo(final Creature cha) {
        if (cha == null) {
            logger.error("CharInfo: cha is null!");
            Thread.dumpStack();
            return;
        }

        if (cha.isInvisible()) {
            return;
        }

        if (cha.isDeleted()) {
            return;
        }

        final Player player = cha.getPlayer();
        if (player == null) {
            return;
        }

        if (player.isInBoat()) {
            loc = player.getInBoatPosition();
            if (player.isClanAirShipDriver()) {
                clanBoatObjectId = player.getBoat().getObjectId();
            }
        }

        if (loc == null) {
            loc = cha.getLoc();
        }

        objId = cha.getObjectId();
        name = player.getName();
        if ((player.isTransformed() && (player.isDominionTransform() || player.isCursedWeaponEquipped())) || (player.getReflection() == ReflectionManager.GIRAN_HARBOR) || player.getReflection() == ReflectionManager.PARNASSUS && player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE) {
            if (player.isDominionTransform()) {
                final PlayerSex sex = player.getPlayerTemplateComponent().getPlayerSex();
                final int idTransform = player.getTransformationId();
                final Optional<TransformDataLine> transform_line = Optional.ofNullable(TransformDataLineHolder.getInstance().get(sex, idTransform));
                final int npcId = transform_line.isPresent() ? transform_line.get().getNpcId() : 0;
                final Optional<NpcNameLine> npcnameLine = Optional.ofNullable(NpcNameLineHolder.getInstance().get(Language.ENGLISH, npcId));
                if (npcnameLine.isPresent()) {
                    name = npcnameLine.get().getName();
                } else {
                    logger.warn("npc name null - transform id " + npcnameLine + " npc_id " + npcId);
                }
            } else if (player.isCursedWeaponEquipped()) {
                name = player.getTransformation().getNameTransform();
                cw_level = CursedWeaponsManager.getInstance().getLevel(player.getCursedWeaponEquippedId());
            }
            title = "";
            clan_id = 0;
            clan_crest_id = 0;
            ally_id = 0;
            ally_crest_id = 0;
            large_clan_crest_id = 0;
        } else {
            if (!player.isConnected() && player.getPrivateStoreType() == Player.STORE_PRIVATE_NONE && !player.isPhantom() && player.isNoCarrier()) {
                title = "NO CARRIER";
                title_color = 3355647;
            } else if (player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE) {
                title = "";
            } else {
                title = player.getTitle();
                title_color = player.getAppearanceComponent().getTitleColor();
            }

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
        }
        name = player.getCustomPlayerComponent().getName().isEmpty() ? name : player.getCustomPlayerComponent().getName();
        nameColor = player.getCustomPlayerComponent().getNameColor() == -1 ? player.getAppearanceComponent().getNameColor() : player.getCustomPlayerComponent().getNameColor();

        if (player.isMounted()) {
            enchant = 0;
            mount_id = player.getMountNpcId() + 1000000;
            mount_type = player.getMountType();
        } else {
            enchant = player.getEnchantEffect();
            mount_id = 0;
            mount_type = 0;
        }

        inv = new int[PcInventory.PAPERDOLL_MAX][3];
        for (final int PAPERDOLL_ID : PAPERDOLL_ORDER) {
            inv[PAPERDOLL_ID][0] = player.getInventory().getPaperdollItemId(PAPERDOLL_ID);
            inv[PAPERDOLL_ID][1] = player.getInventory().getPaperdollVariation1Id(PAPERDOLL_ID);
            inv[PAPERDOLL_ID][2] = player.getInventory().getPaperdollVariation2Id(PAPERDOLL_ID);
        }
        talisman_count = player.getTalismanCount();
        cloak = player.getOpenCloak();

        mAtkSpd = player.getMAtkSpd();
        pAtkSpd = player.getPAtkSpd();
        speed_move = player.getMovementSpeedMultiplier();
        runSpd = (int) (player.getRunSpeed() / speed_move);
        walkSpd = (int) (player.getWalkSpeed() / speed_move);
        flyRunSpd = player.isFlying() ? runSpd : Ints.checkedCast(Math.round(player.getPlayerTemplateComponent().getMovingSpeed()[5]));
        flyWalkSpd = player.isFlying() ? walkSpd : Ints.checkedCast(Math.round(player.getPlayerTemplateComponent().getMovingSpeed()[4]));
        rideRunSpd = player.isMounted() ? runSpd : Ints.checkedCast(Math.round(player.getPlayerTemplateComponent().getMovingSpeed()[7]));
        rideWalkSpd = player.isMounted() ? walkSpd : Ints.checkedCast(Math.round(player.getPlayerTemplateComponent().getMovingSpeed()[6]));
        swimRunSpd = (int) Math.round(player.getSwimRunSpeed() / player.getSwimSpeedMultiplier());
        swimWalkSpd = (int) Math.round(player.getSwimWalkSpeed() / player.getSwimSpeedMultiplier());

        race = player.getPlayerTemplateComponent().getPlayerRace().ordinal();
        sex = player.getSex();
        base_class = player.getPlayerClassComponent().getBaseClassId();
        pvp_flag = player.getPvpFlag();
        karma = player.getKarma();

        speed_atack = player.getAttackSpeedMultiplier();
        col_radius = player.getColRadius();
        col_height = player.getColHeight();
        hair_style = player.getAppearanceComponent().getHairStyle();
        hair_color = player.getAppearanceComponent().getHairColor();
        face = player.getAppearanceComponent().getFace();
        if (clan_id > 0 && player.getClan() != null) {
            clan_rep_score = player.getClan().getReputationScore();
        } else {
            clan_rep_score = 0;
        }
        sit = player.isSitting() ? 0 : 1; // standing = 1 sitting = 0
        run = player.isRunning() ? 1 : 0; // running = 1 walking = 0
        combat = player.isInCombat() ? 1 : 0;
        dead = player.isAlikeDead() ? 1 : 0;
        private_store = player.isInObserverMode() ? Player.STORE_OBSERVING_GAMES : player.getPrivateStoreType();
        cubics = player.getCubics();
        abnormalEffect = player.getAbnormalEffect(AbnormalEffectType.FIRST);
        abnormalEffect2 = player.getAbnormalEffect(AbnormalEffectType.SECOND);
        rec_have = player.isGM() ? 0 : player.getRecommendationComponent().getRecomHave();
        class_id = player.getPlayerClassComponent().getClassId().getId();
        team = player.getTeam();

        invisible = player.isInvisible();
        noble = player.isNoble() ? 1 : 0; // 0x01: symbol on char menu ctrl+I
        hero = (player.isHero() || player.getCustomPlayerComponent().isTemporalHero()) || player.isGM() && OtherConfig.GM_HERO_AURA ? 1 : 0; // 0x01: Hero Aura
        fishing = player.isFishing() ? 1 : 0;
        fishLoc = player.getFishLoc();
        plg_class = player.getPledgeClass();
        pledge_type = player.getPledgeType();
        transform = player.isTransformed() ? player.getTransformation().getData().id : 0;
        agathion = player.getAgathionNpcId();
        isPartyRoomLeader = player.getMatchingRoom() != null && player.getMatchingRoom().getType() == MatchingRoom.PARTY_MATCHING && player.getMatchingRoom().getGroupLeader() == player;
        isInsideWater = player.isInWater();
        isFlying = player.isInFlyingTransform();
        activeRelation = player.isUserRelationActive();

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
        final Player activeChar = getClient().getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (objId == 0) {
            return;
        }

        if (activeChar.getObjectId() == objId) {
            logger.error("You cant send CharInfo about his character to active user!!!");
            return;
        }

        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z + GeodataConfig.CLIENT_Z_SHIFT);
        writeD(clanBoatObjectId);
        writeD(objId);
        writeS(name);
        writeD(race);
        writeD(sex);
        writeD(base_class);

        for (final int PAPERDOLL_ID : PAPERDOLL_ORDER) {
            writeD(inv[PAPERDOLL_ID][0]);
        }

        for (final int PAPERDOLL_ID : PAPERDOLL_ORDER) {
            writeH(inv[PAPERDOLL_ID][1]);
            writeH(inv[PAPERDOLL_ID][2]);
        }

        writeD(talisman_count);
        writeD(cloak ? 0x01 : 0x00);

        writeD(pvp_flag);
        writeD(karma);

        writeD(mAtkSpd);
        writeD(pAtkSpd);

        writeD(0); // FIXME

        writeD(runSpd);
        writeD(walkSpd);
        writeD(swimRunSpd);
        writeD(swimWalkSpd);
        writeD(rideRunSpd);
        writeD(rideWalkSpd);
        writeD(flyRunSpd);
        writeD(flyWalkSpd);
        writeF(speed_move); // _cha.getProperMultiplier()
        writeF(speed_atack); // _cha.getAttackSpeedMultiplier()

        writeF(col_radius);
        writeF(col_height);
        writeD(hair_style);
        writeD(hair_color);
        writeD(face);
        writeS(title);
        writeD(clan_id);
        writeD(clan_crest_id);
        writeD(ally_id);
        writeD(ally_crest_id);

        writeC(sit);
        writeC(run);
        writeC(combat);
        writeC(dead);
        writeC(invisible ? 0x01 : 0x00); // is invisible
        writeC(mount_type); // 1-on Strider, 2-on Wyvern, 3-on Great Wolf, 0-no mount
        writeC(private_store);
        writeH(cubics.size());
        for (final CubicComponent cubic : cubics.values()) {
            writeH(cubic == null ? 0 : cubic.getId());
        }
        writeC(isPartyRoomLeader ? 0x01 : 0x00); // find party members
        writeD(abnormalEffect);
        writeC(isInsideWater ? 0x01 : isFlying ? 0x02 : 0x00);
        writeH(rec_have);
        writeD(mount_id);
        writeD(class_id);
        writeD(0); // FIXME
        writeC(enchant);

        writeC(team.ordinal()); // team circle around feet 1 = Blue, 2 = red

        writeD(large_clan_crest_id);
        writeC(noble);
        writeC(hero);

        writeC(fishing);
        writeD(fishLoc.x);
        writeD(fishLoc.y);
        writeD(fishLoc.z);

        writeD(nameColor);
        writeD(loc.h);
        writeD(plg_class);
        writeD(pledge_type);
        writeD(title_color);
        writeD(cw_level);
        writeD(clan_rep_score);
        writeD(transform);
        writeD(agathion);
        writeD(activeRelation);
        writeD(abnormalEffect2);
    }
}