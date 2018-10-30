package org.mmocore.gameserver.network.lineage.clientpackets;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.data.xml.holder.SkillAcquireHolder;
import org.mmocore.gameserver.model.SkillLearn;
import org.mmocore.gameserver.model.base.AcquireType;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.VillageMasterInstance;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillList;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.SkillEntryType;
import org.mmocore.gameserver.tables.SkillTable;

public class RequestAquireSkill extends L2GameClientPacket {
    private AcquireType _type;
    private int _id, _level, _subUnit;

    /**
     * Изучение следующего возможного уровня скилла
     *
     * @param player
     * @param skillLearn
     * @param skill
     */
    private static void learnSimpleNextLevel(final Player player, final SkillLearn skillLearn, final SkillEntry skill) {
        final int skillLevel = player.getSkillLevel(skillLearn.getId(), 0);
        if (skillLevel != skillLearn.getLevel() - 1) {
            return;
        }

        learnSimple(player, skillLearn, skill);
    }

    private static void learnSimple(final Player player, final SkillLearn skillLearn, final SkillEntry skill) {
        if (player.getSp() < skillLearn.getCost()) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL);
            return;
        }

        if (skillLearn.getItemId() > 0) {
            if (!player.consumeItem(skillLearn.getItemId(), skillLearn.getItemCount())) {
                return;
            }
        }

        player.sendUserInfo();
        player.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.getId(), skill.getLevel()));
        player.setSp(player.getSp() - skillLearn.getCost());
        player.addSkill(skill, true);
        player.updateStats();
        player.sendPacket(new SkillList(player));
        RequestExEnchantSkill.updateSkillShortcuts(player, skill.getId(), skill.getLevel());
    }

    private static void learnClanSkill(final Player player, final SkillLearn skillLearn, final NpcInstance trainer, final SkillEntry skill) {
        if (!(trainer instanceof VillageMasterInstance)) {
            return;
        }

        if (!player.isClanLeader()) {
            player.sendPacket(SystemMsg.ONLY_THE_CLAN_LEADER_IS_ENABLED);
            return;
        }

        final Clan clan = player.getClan();
        final int skillLevel = clan.getSkillLevel(skillLearn.getId(), 0);
        if (skillLevel != skillLearn.getLevel() - 1) // можно выучить только следующий уровень
        {
            return;
        }
        if (clan.getReputationScore() < skillLearn.getCost()) {
            player.sendPacket(SystemMsg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
            return;
        }

        if (skillLearn.getItemId() > 0) {
            if (!player.consumeItem(skillLearn.getItemId(), skillLearn.getItemCount())) {
                return;
            }
        }

        clan.incReputation(-skillLearn.getCost(), false, "AquireSkill: " + skillLearn.getId() + ", lvl " + skillLearn.getLevel());
        clan.addSkill(skill, true);
        clan.broadcastToOnlineMembers(new SystemMessage(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill));

        NpcInstance.showClanSkillList(player);
    }

    private static void learnSubUnitSkill(final Player player, final SkillLearn skillLearn, final NpcInstance trainer, final SkillEntry skill, final int id) {
        final Clan clan = player.getClan();
        if (clan == null) {
            return;
        }
        final SubUnit sub = clan.getSubUnit(id);
        if (sub == null) {
            return;
        }

        if ((player.getClanPrivileges() & Clan.CP_CL_TROOPS_FAME) != Clan.CP_CL_TROOPS_FAME) {
            player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        final int lvl = sub.getSkillLevel(skillLearn.getId(), 0);
        if (lvl >= skillLearn.getLevel()) {
            player.sendPacket(SystemMsg.THIS_SQUAD_SKILL_HAS_ALREADY_BEEN_ACQUIRED);
            return;
        }

        if (lvl != (skillLearn.getLevel() - 1)) {
            player.sendPacket(SystemMsg.THE_PREVIOUS_LEVEL_SKILL_HAS_NOT_BEEN_LEARNED);
            return;
        }

        if (clan.getReputationScore() < skillLearn.getCost()) {
            player.sendPacket(SystemMsg.THE_CLAN_REPUTATION_SCORE_IS_TOO_LOW);
            return;
        }

        if (skillLearn.getItemId() > 0) {
            if (!player.consumeItem(skillLearn.getItemId(), skillLearn.getItemCount())) {
                return;
            }
        }

        clan.incReputation(-skillLearn.getCost(), false, "AquireSkill2: " + skillLearn.getId() + ", lvl " + skillLearn.getLevel());
        sub.addSkill(skill, true);
        player.sendPacket(new SystemMessage(SystemMsg.THE_CLAN_SKILL_S1_HAS_BEEN_ADDED).addSkillName(skill));

        if (trainer != null) {
            NpcInstance.showSubUnitSkillList(player);
        }
    }

    private static boolean checkSpellbook(final Player player, final SkillLearn skillLearn) {
        if (AllSettingsConfig.ALT_DISABLE_SPELLBOOKS) {
            return true;
        }

        if (skillLearn.getItemId() == 0) {
            return true;
        }

        // скилы по клику учатся другим способом
        if (skillLearn.isClicked()) {
            return false;
        }

        return player.getInventory().getCountOf(skillLearn.getItemId()) >= skillLearn.getItemCount();
    }

    @Override
    protected void readImpl() {
        _id = readD();
        _level = readD();
        _type = ArrayUtils.valid(AcquireType.VALUES, readD());
        if (_type == AcquireType.SUB_UNIT) {
            _subUnit = readD();
        }
    }

    @Override
    protected void runImpl() {
        final Player player = getClient().getActiveChar();
        if (player == null || _type == null) {
            return;
        }
        if (player.isTransformed()) {
            player.sendPacket(new SystemMessage(SystemMsg.THIS_ITEM_CANNOT_BE_USED_IN_THE_CURRENT_TRANSFORMATION_STATE)); // TODO[K] - найти подходящий мессадж
            return;
        }
        if (player.isSitting() || player.isInStoreMode()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
            return;
        }

        final NpcInstance trainer = player.getLastNpc();
        if ((trainer == null || player.getDistance(trainer.getX(), trainer.getY()) > player.getInteractDistance(trainer)) && !player.isGM()) {
            return;
        }

        final SkillEntry skillEntry = SkillTable.getInstance().getSkillEntry(_id, _level);
        if (skillEntry == null) {
            return;
        }

        if (!SkillAcquireHolder.getInstance().isSkillPossible(player, skillEntry, _type)) {
            return;
        }

        final SkillLearn skillLearn = SkillAcquireHolder.getInstance().getSkillLearn(player, _id, _level, _type);

        if (skillLearn == null) {
            return;
        }

        if (!checkSpellbook(player, skillLearn)) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_NECESSARY_MATERIALS_OR_PREREQUISITES_TO_LEARN_THIS_SKILL);
            return;
        }

        final int playerPing = getClient().getLastPing() + 100 > 500 ? 500 : getClient().getLastPing() + 100;
        switch (_type) {
            case NORMAL:
                learnSimpleNextLevel(player, skillLearn, skillEntry);
                if (trainer != null) {
                    ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                        @Override
                        public void runImpl() {
                            trainer.showSkillList(player);
                        }
                    }, playerPing);
                }
                break;
            case TRANSFORMATION:
                learnSimpleNextLevel(player, skillLearn, skillEntry);
                if (trainer != null) {
                    ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                        @Override
                        public void runImpl() {
                            trainer.showTransformationSkillList(player, AcquireType.TRANSFORMATION);
                        }
                    }, playerPing);
                }
                break;
            case COLLECTION:
                learnSimpleNextLevel(player, skillLearn, skillEntry);
                if (trainer != null) {
                    ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                        @Override
                        public void runImpl() {
                            NpcInstance.showCollectionSkillList(player);
                        }
                    }, playerPing);
                }
                break;
            case TRANSFER_CARDINAL:
            case TRANSFER_EVA_SAINTS:
            case TRANSFER_SHILLIEN_SAINTS:
                learnSimple(player, skillLearn, skillEntry.copyTo(SkillEntryType.VALUES[_type.ordinal()]));
                if (trainer != null) {
                    ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                        @Override
                        public void runImpl() {
                            trainer.showTransferSkillList(player);
                        }
                    }, playerPing);
                }
                break;
            case FISHING:
            case FISHING_NON_DWARF:
                learnSimpleNextLevel(player, skillLearn, skillEntry);
                if (trainer != null) {
                    ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                        @Override
                        public void runImpl() {
                            NpcInstance.showFishingSkillList(player);
                        }
                    }, playerPing);
                }
                break;
            case CLAN:
                learnClanSkill(player, skillLearn, trainer, skillEntry);
                break;
            case SUB_UNIT:
                learnSubUnitSkill(player, skillLearn, trainer, skillEntry, _subUnit);
                break;
            case CERTIFICATION:
                if (!player.getPlayerClassComponent().getActiveClass().isBase()) {
                    player.sendPacket(SystemMsg.THIS_SKILL_CANNOT_BE_LEARNED_WHILE_IN_THE_SUBCLASS_STATE);
                    return;
                }
                learnSimpleNextLevel(player, skillLearn, skillEntry.copyTo(SkillEntryType.VALUES[_type.ordinal()]));
                if (trainer != null) {
                    ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                        @Override
                        public void runImpl() {
                            trainer.showTransformationSkillList(player, AcquireType.CERTIFICATION);
                        }
                    }, playerPing);
                }
                break;
        }
    }
}