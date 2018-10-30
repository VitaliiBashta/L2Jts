package org.mmocore.gameserver.network.lineage.serverpackets;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.gameserver.configuration.config.GeodataConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.base.TeamType;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Alliance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.skills.AbnormalEffectType;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Location;

/**
 * @reworked VISTALL
 */
public class NpcInfo extends GameServerPacket {
    private boolean can_writeImpl = false;
    private int npcObjId, npcId, running, incombat, dead, showSpawnAnimation;
    private int runSpd, walkSpd, mAtkSpd, pAtkSpd, rhand, lhand, enchantEffect;
    private int karma, pvp_flag, abnormalEffect, abnormalEffect2, clan_id, clan_crest_id, ally_id, ally_crest_id, formId, titleColor;
    private double colHeight, colRadius, currentColHeight, currentColRadius;
    private boolean isAttackable, isNameAbove, isFlying;
    private Location loc;
    private String name = StringUtils.EMPTY;
    private String title = StringUtils.EMPTY;
    private boolean showName;
    private int state;
    private NpcString nameNpcString = NpcString.NONE;
    private NpcString titleNpcString = NpcString.NONE;
    private TeamType team;

    public NpcInfo(final NpcInstance cha, final Creature attacker) {
        npcId = cha.getDisplayId() != 0 ? cha.getDisplayId() : cha.getTemplate().npcId;
        isAttackable = attacker != null && cha.isAutoAttackable(attacker);
        rhand = cha.getRightHandItem();
        lhand = cha.getLeftHandItem();
        enchantEffect = 0;
        if (ServerConfig.SERVER_SIDE_NPC_NAME || cha.getTemplate().displayId != 0 || cha.getName() != cha.getTemplate().name) {
            name = cha.getName();
        }
        if (ServerConfig.SERVER_SIDE_NPC_TITLE || cha.getTemplate().displayId != 0 || cha.getTitle() != cha.getTemplate().title) {
            title = cha.getTitle();
        }

        showSpawnAnimation = cha.getSpawnAnimation();
        showName = cha.isShowName();
        state = cha.getNpcState();
        nameNpcString = cha.getNameNpcString();
        titleNpcString = cha.getTitleNpcString();

        common(cha);
    }

    public NpcInfo(final Servitor cha, final Creature attacker) {
        if (cha.getPlayer() != null && cha.getPlayer().isInvisible()) {
            return;
        }

        npcId = cha.getTemplate().npcId;
        isAttackable = cha.isAutoAttackable(attacker);
        rhand = 0;
        lhand = 0;
        enchantEffect = 0;
        showName = true;
        name = cha.getName().equalsIgnoreCase(cha.getTemplate().name) ? "" : cha.getName();
        title = cha.getTitle();
        showSpawnAnimation = cha.getSpawnAnimation();

        common(cha);
    }

    public NpcInfo(final Player player) {
        if (player.isInvisible()) {
            return;
        }

        npcId = player.getPolyId();
        final NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);

        isAttackable = false;
        enchantEffect = 0;
        showName = true;
        name = player.getName();
        title = player.getTitle();
        showSpawnAnimation = 0;

        //
        final Clan clan = player.getClan();
        final Alliance alliance = clan == null ? null : clan.getAlliance();
        //
        clan_id = clan == null ? 0 : clan.getClanId();
        clan_crest_id = clan == null ? 0 : clan.getCrestId();
        //
        ally_id = alliance == null ? 0 : alliance.getAllyId();
        ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();

        colHeight = template.getCollisionHeight();
        colRadius = template.getCollisionRadius();
        currentColHeight = colHeight;
        currentColRadius = colRadius;

        rhand = player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_RHAND);
        lhand = player.getInventory().getPaperdollItemId(Inventory.PAPERDOLL_LHAND);

        npcObjId = player.getObjectId();
        loc = player.getLoc();
        mAtkSpd = player.getMAtkSpd();

        runSpd = player.getRunSpeed();
        walkSpd = player.getWalkSpeed();
        karma = player.getKarma();
        pvp_flag = player.getPvpFlag();
        pAtkSpd = player.getPAtkSpd();
        running = player.isRunning() ? 1 : 0;
        incombat = player.isInCombat() ? 1 : 0;
        dead = player.isAlikeDead() ? 1 : 0;
        abnormalEffect = player.getAbnormalEffect(AbnormalEffectType.FIRST);
        abnormalEffect2 = player.getAbnormalEffect(AbnormalEffectType.SECOND);
        isFlying = player.isFlying();
        team = player.getTeam();
        formId = player.getFormId();
        isNameAbove = player.isNameAbove();
        titleColor = (player.isSummon() || player.isPet()) ? 1 : 0;

        can_writeImpl = true;
    }

    private void common(final Creature cha) {
        colHeight = cha.getTemplate().getCollisionHeight();
        colRadius = cha.getTemplate().getCollisionRadius();
        currentColHeight = cha.getColHeight();
        currentColRadius = cha.getColRadius();
        npcObjId = cha.getObjectId();
        loc = cha.getLoc();
        mAtkSpd = cha.getMAtkSpd();
        //
        final Clan clan = cha.getClan();
        final Alliance alliance = clan == null ? null : clan.getAlliance();
        //
        clan_id = clan == null ? 0 : clan.getClanId();
        clan_crest_id = clan == null ? 0 : clan.getCrestId();
        //
        ally_id = alliance == null ? 0 : alliance.getAllyId();
        ally_crest_id = alliance == null ? 0 : alliance.getAllyCrestId();

        runSpd = cha.getRunSpeed();
        walkSpd = cha.getWalkSpeed();
        karma = cha.getKarma();
        pvp_flag = cha.getPvpFlag();
        pAtkSpd = cha.getPAtkSpd();
        running = cha.isRunning() ? 1 : 0;
        incombat = cha.isInCombat() ? 1 : 0;
        dead = cha.isAlikeDead() ? 1 : 0;
        abnormalEffect = cha.getAbnormalEffect(AbnormalEffectType.FIRST);
        abnormalEffect2 = cha.getAbnormalEffect(AbnormalEffectType.SECOND);
        isFlying = cha.isFlying();
        team = cha.getTeam();
        formId = cha.getFormId();
        isNameAbove = cha.isNameAbove();
        titleColor = (cha.isSummon() || cha.isPet()) ? 1 : 0;

        can_writeImpl = true;
    }

    public NpcInfo update() {
        showSpawnAnimation = 1;
        return this;
    }

    @Override
    protected void writeData() {
        if (!can_writeImpl) {
            return;
        }
        //ddddddddddddddddddffffdddcccccSSddddddddccffddddccd
        writeD(npcObjId);
        writeD(npcId + 1000000); // npctype id c4
        writeD(isAttackable ? 1 : 0);
        writeD(loc.x);
        writeD(loc.y);
        writeD(loc.z + GeodataConfig.CLIENT_Z_SHIFT);
        writeD(loc.h);
        writeD(0x00);
        writeD(mAtkSpd);
        writeD(pAtkSpd);
        writeD(runSpd);
        writeD(walkSpd);
        writeD(runSpd /*_swimRunSpd*//*0x32*/); // swimspeed
        writeD(walkSpd/*_swimWalkSpd*//*0x32*/); // swimspeed
        writeD(runSpd/*_flRunSpd*/);
        writeD(walkSpd/*_flWalkSpd*/);
        writeD(runSpd/*_flyRunSpd*/);
        writeD(walkSpd/*_flyWalkSpd*/);
        writeF(1.100000023841858); // взято из клиента
        writeF(pAtkSpd / 277.478340719);
        writeF(colRadius);
        writeF(colHeight);
        writeD(rhand); // right hand weapon
        writeD(0); //TODO chest
        writeD(lhand); // left hand weapon
        writeC(isNameAbove ? 1 : 0); // 2.2: name above char 1=true ... ??; 2.3: 1 - normal, 2 - dead
        writeC(running);
        writeC(incombat);
        writeC(dead);
        writeC(showSpawnAnimation); // invisible ?? 0=false  1=true   2=summoned (only works if model has a summon animation)
        writeD(nameNpcString.getId());
        writeS(name);
        writeD(titleNpcString.getId());
        writeS(title);
        writeD(titleColor); // 0- светло зеленый титул(моб), 1 - светло синий(пет)/отображение текущего МП
        writeD(pvp_flag);
        writeD(karma); // hmm karma ??
        writeD(abnormalEffect); // C2
        writeD(clan_id);
        writeD(clan_crest_id);
        writeD(ally_id);
        writeD(ally_crest_id);
        writeC(isFlying ? 2 : 0); // C2
        writeC(team.ordinal()); // team aura 1-blue, 2-red
        writeF(currentColRadius); // тут что-то связанное с colRadius
        writeF(currentColHeight); // тут что-то связанное с colHeight
        writeD(enchantEffect); // C4
        writeD(0x00); // writeD(_npc.isFlying() ? 1 : 0); // C6
        writeD(0x00);
        writeD(formId);// great wolf type
        writeC(showName ? 0x01 : 0x00); // show name
        writeC(showName ? 0x01 : 0x00); // show title
        writeD(abnormalEffect2);
        writeD(state);
    }
}