package org.mmocore.gameserver.model.entity.residence;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.database.dao.impl.DominionDAO;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author VISTALL
 * @date 15:15/14.02.2011
 */
public class Dominion extends Residence {
    private final Set<Integer> flags = new TreeSet<>();
    private Castle castle;
    private int lordObjectId;

    public Dominion(final StatsSet set) {
        super(set);
    }

    @Override
    public void init() {
        initEvent();

        castle = ResidenceHolder.getInstance().getResidence(Castle.class, getId() - 80);
        castle.setDominion(this);

        loadData();

        siegeDate = Residence.MIN_SIEGE_DATE;
    }

    @Override
    public void rewardSkills() {
        final Clan owner = getOwner();
        if (owner != null) {
            if (!flags.contains(getId())) {
                return;
            }

            for (final int dominionId : flags) {
                final Dominion dominion = ResidenceHolder.getInstance().getResidence(Dominion.class, dominionId);
                for (final SkillEntry skill : dominion.getSkills()) {
                    owner.addSkill(skill, false);
                    owner.broadcastToOnlineMembers(new SystemMessage(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill));
                }
            }
        }
    }

    @Override
    public void removeSkills() {
        final Clan owner = getOwner();
        if (owner != null) {
            for (final int dominionId : flags) {
                final Dominion dominion = ResidenceHolder.getInstance().getResidence(Dominion.class, dominionId);
                for (final SkillEntry skill : dominion.getSkills()) {
                    owner.removeSkill(skill.getId());
                }
            }
        }
    }

    public void addFlag(final int dominionId) {
        flags.add(dominionId);
    }

    public void removeFlag(final int dominionId) {
        flags.remove(dominionId);
    }

    public void resetDominionFlags() {
        deleteAllFlags();
        restoreDefaultFlag();
        setJdbcState(JdbcEntityState.UPDATED);
        update();
    }

    public void deleteAllFlags() {
        if (!flags.isEmpty()) {
            for (final int dominionId : flags) {
                removeFlag(dominionId);
            }
        }
    }

    public void restoreDefaultFlag() {
        if (!flags.contains(getId())) {
            addFlag(getId());
        }
    }

    public Integer[] getFlags() {
        return flags.toArray(new Integer[flags.size()]);
    }

    public void purgeFlags() {
        flags.clear();
        flags.add(getId());
    }

    @Override
    protected void loadData() {
        DominionDAO.getInstance().select(this);
    }

    @Override
    public void changeOwner(final Clan clan) {
        final int newLordObjectId;
        if (clan == null) {
            if (lordObjectId > 0) {
                newLordObjectId = 0;
            } else {
                return;
            }
        } else {
            newLordObjectId = clan.getLeaderId();

            // разсылаем мессагу
            final SystemMessage message = new SystemMessage(SystemMsg.CLAN_LORD_C2_WHO_LEADS_CLAN_S1_HAS_BEEN_DECLARED_THE_LORD_OF_THE_S3_TERRITORY).addName(clan.getLeader().getPlayer()).addString(clan.getName()).addResidenceName(getCastle());
            for (final Player player : GameObjectsStorage.getPlayers()) {
                player.sendPacket(message);
            }
        }

        lordObjectId = newLordObjectId;

        setJdbcState(JdbcEntityState.UPDATED);
        update();

        // обновляем значки в нпц которые принадлежат територии
        for (final NpcInstance npc : GameObjectsStorage.getNpcs()) {
            if (npc.getDominion() == this) {
                npc.broadcastCharInfoImpl();
            }
        }
    }

    public int getLordObjectId() {
        return lordObjectId;
    }

    public void setLordObjectId(final int lordObjectId) {
        this.lordObjectId = lordObjectId;
    }

    @Override
    public Clan getOwner() {
        return castle.getOwner();
    }

    @Override
    public int getOwnerId() {
        return castle.getOwnerId();
    }

    public Castle getCastle() {
        return castle;
    }

    @Override
    public void update() {
        DominionDAO.getInstance().update(this);
    }
}
