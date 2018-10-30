package org.mmocore.gameserver.object.components.player.player_class;

import org.mmocore.commons.logging.LogUtils;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.SkillAcquireHolder;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.database.dao.impl.CharacterDyeDAO;
import org.mmocore.gameserver.database.dao.impl.CharacterEffectDAO;
import org.mmocore.gameserver.database.dao.impl.CharacterSkillDAO;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.model.SkillLearn;
import org.mmocore.gameserver.model.SubClass;
import org.mmocore.gameserver.model.base.AcquireType;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.base.ClassLevel;
import org.mmocore.gameserver.model.entity.olympiad.Olympiad;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.DeathPenalty;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.skills.EffectType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author KilRoy
 */
public class PlayerClassComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerClassComponent.class);

    private final Player playerRef;

    private final Map<Integer, SubClass> classList = new HashMap<>(4);
    private int baseClass = -1;
    private SubClass activeClass;

    public PlayerClassComponent(final Player player) {
        playerRef = player;
    }

    public Player getPlayer() {
        return playerRef;
    }

    public void setBaseClass(final int baseClass) {
        this.baseClass = baseClass;
    }

    public int getBaseClassId() {
        return baseClass;
    }

    public int getActiveClassId() {
        return activeClass == null ? 0 : activeClass.getClassId();
    }

    public SubClass getActiveClass() {
        return activeClass;
    }

    public void setActiveClass(final SubClass activeClass) {
        this.activeClass = activeClass;
    }

    public Map<Integer, SubClass> getSubClasses() {
        return classList;
    }

    public boolean isSubClassActive() {
        return getBaseClassId() != getActiveClassId();
    }

    public boolean isBaseExactlyActiveId() {
        return getBaseClassId() == getActiveClassId();
    }

    public ClassId getClassId() {
        return ClassId.VALUES[getActiveClassId()];
    }

    /**
     * Добавить класс, используется только для сабклассов
     *
     * @param storeOld
     * @param certification
     */
    public boolean addSubClass(final int classId, final boolean storeOld, final int certification) {
        if (classList.size() >= 4) {
            return false;
        }

        final ClassId newId = ClassId.VALUES[classId];

        final SubClass newClass = new SubClass();
        newClass.setBase(false);
        if (newId.getRace() == null) {
            return false;
        }

        newClass.setClassId(classId);
        newClass.setCertification(certification);

        classList.put(classId, newClass);

        final Player player = getPlayer();

        if (!CharacterDAO.getInstance().addSubClass(player, certification, newClass)) {
            LOGGER.error("Could not add character sub-class. Player objId: {} player new sub: {}", player.getObjectId(), newClass);
            return false;
        }
        setActiveSubClass(classId, storeOld);

        if (AllSettingsConfig.startLevelSub > 0)
            player.setLevel(AllSettingsConfig.startLevelSub);


        boolean countUnlearnable = true;
        int unLearnable = 0;

        Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.NORMAL);
        while (skills.size() > unLearnable) {
            for (final SkillLearn s : skills) {
                final SkillEntry sk = SkillTable.getInstance().getSkillEntry(s.getId(), s.getLevel());
                if (sk == null || !sk.getTemplate().getCanLearn(newId)) {
                    if (countUnlearnable) {
                        unLearnable++;
                    }
                    continue;
                }
                player.addSkill(sk, true);
            }
            countUnlearnable = false;
            skills = SkillAcquireHolder.getInstance().getAvailableSkills(player, AcquireType.NORMAL);
        }

        player.sendPacket(new SkillList(player));
        player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp(), true);
        player.setCurrentCp(player.getMaxCp());
        return true;
    }

    /**
     * Удаляет всю информацию о классе и добавляет новую, только для сабклассов
     */
    public boolean modifySubClass(final int oldClassId, final int newClassId) {
        final SubClass originalClass = classList.get(oldClassId);
        if (originalClass == null || originalClass.isBase()) {
            return false;
        }

        final int certification = originalClass.getCertification();

        final Player player = getPlayer();

        switch (ClassId.VALUES[oldClassId]) {
            case cardinal:
                ItemFunctions.removeItem(player, 15307, 1, true);
                break;
            case eva_saint:
                ItemFunctions.removeItem(player, 15308, 1, true);
                break;
            case shillien_saint:
                ItemFunctions.removeItem(player, 15309, 4, true);
                break;
        }

        if (!CharacterDAO.getInstance().modifySubClass(player, oldClassId)) {
            LOGGER.error("Could not delete char sub-class. Player objId: {} oldClassId: {}", player.getObjectId(), oldClassId);
        }

        classList.remove(oldClassId);

        return newClassId <= 0 || addSubClass(newClassId, false, certification);
    }

    /**
     * Устанавливает активный сабкласс
     * <p/>
     * <li>Retrieve from the database all skills of this L2Player and add them to skills </li>
     * <li>Retrieve from the database all macroses of this L2Player and add them to _macroses</li>
     * <li>Retrieve from the database all shortCuts of this L2Player and add them to _shortCuts</li><BR><BR>
     */
    public void setActiveSubClass(final int subId, final boolean store) {
        final Player player = getPlayer();

        final SubClass sub = classList.get(subId);
        if (sub == null) {
            return;
        }

        if (getActiveClass() != null) {
            CharacterEffectDAO.getInstance().insert(player);
            player.storeDisableSkills();

            final QuestState qs = player.getQuestState(422);
            if (qs != null) {
                qs.exitQuest(true);
            }
        }

        if (store) {
            final SubClass oldsub = getActiveClass();
            if (oldsub != null) {
                oldsub.setCp(player.getCurrentCp());
                oldsub.setExp(player.getExp());
                oldsub.setSp(player.getSp());
                oldsub.setHp(player.getCurrentHp());
                oldsub.setMp(player.getCurrentMp());
                oldsub.setActive(false);
                getSubClasses().put(getActiveClassId(), oldsub);
            }
        }

        sub.setActive(true);
        setActiveClass(sub);
        getSubClasses().put(getActiveClassId(), sub);

        setClassId(subId, false, false);

        player.removeAllSkills();

        player.getEffectList().getAllEffects().stream().filter(effect -> effect.getEffectType() != EffectType.ReportBlock).forEach(Effect::exit);

        if (player.getServitor() != null) {
            player.getServitor().unSummon(false, true);
        }
        //cubicdata
        player.deleteCubics();
        player.deleteAgathion();

        CharacterSkillDAO.getInstance().select(player);
        player.rewardSkills(false);
        player.checkSkills();
        player.sendPacket(new ExStorageMaxCount(player));

        player.refreshExpertisePenalty();

        player.sendPacket(new SkillList(player));

        player.getInventory().refreshEquip();
        player.getInventory().validateItems();

        for (int i = 0; i < 3; i++) {
            player.getDyes()[i] = null;
        }

        CharacterDyeDAO.getInstance().select(player);
        player.recalcDyeStats();
        player.sendPacket(new HennaInfo(player));

        CharacterEffectDAO.getInstance().select(player);
        player.restoreDisableSkills();

        player.setCurrentHpMp(sub.getHp(), sub.getMp());
        player.setCurrentCp(sub.getCp());

        player.getShortCutComponent().restore();
        player.sendPacket(new ShortCutInit(player));
        for (final int shotId : player.getAutoSoulShot()) {
            player.sendPacket(new ExAutoSoulShot(shotId, true));
        }
        player.sendPacket(new SkillCoolTime(player));

        player.broadcastPacket(new SocialAction(player.getObjectId(), SocialAction.LEVEL_UP));

        player.getDeathPenalty().restore(player);

        player.setIncreasedForce(0);

        player.broadcastCharInfo();
        player.updateEffectIcons();
        player.updateStats();
    }

    /**
     * Changing index of class in DB, used for changing class when finished professional quests
     *
     * @param oldclass
     * @param newclass
     */
    public synchronized void changeClassInDb(final int oldclass, final int newclass) {
        CharacterDAO.getInstance().changeClassInDb(getPlayer(), oldclass, newclass);
    }

    /**
     * Сохраняет информацию о классах в БД
     */
    public void storeCharSubClasses() {
        final Player player = getPlayer();
        final SubClass main = getActiveClass();
        if (main != null) {
            if (player == null) {
                LOGGER.error(LogUtils.dumpStack());
                return;
            }

            main.setCp(player.getCurrentCp());
            main.setExp(player.getExp());
            main.setSp(player.getSp());
            main.setHp(player.getCurrentHp());
            main.setMp(player.getCurrentMp());
            main.setActive(true);
            getSubClasses().put(getActiveClassId(), main);
        } else {
            LOGGER.error("Could not store char sub data, main class: {} not found for: {}", getActiveClassId(), player);
        }

        CharacterDAO.getInstance().storeCharSubClasses(player);
    }

    public void restoreCharSubClasses() {
        final Player player = getPlayer();
        final Iterator<StatsSet> subClasses = CharacterDAO.getInstance().restoreCharSubClasses(player).iterator();

        SubClass activeSubclass = null;
        while (subClasses.hasNext()) {
            final StatsSet subClassStatsSet = subClasses.next();

            final SubClass subClass = new SubClass();
            subClass.setBase(subClassStatsSet.getInteger("isBase") != 0);
            subClass.setClassId(subClassStatsSet.getInteger("class_id"));
            subClass.setExp(subClassStatsSet.getLong("exp"));
            subClass.setSp(subClassStatsSet.getInteger("sp"));
            subClass.setHp(subClassStatsSet.getDouble("curHp"));
            subClass.setMp(subClassStatsSet.getDouble("curMp"));
            subClass.setCp(subClassStatsSet.getDouble("curCp"));
            subClass.setDeathPenalty(new DeathPenalty(player, subClassStatsSet.getInteger("death_penalty")));
            subClass.setCertification(subClassStatsSet.getInteger("certification"));

            final boolean active = subClassStatsSet.getInteger("active") != 0;
            if (active) {
                activeSubclass = subClass;
            }
            getSubClasses().put(subClass.getClassId(), subClass);
        }

        if (getSubClasses().isEmpty()) {
            LOGGER.error("There are no one subclass for player: " + player, new Exception());
        }

        final int baseClassId = getBaseClassId();
        if (baseClassId == -1) {
            LOGGER.error("There are no base subclass for player: " + player, new Exception());
        }

        if (activeSubclass != null) {
            setActiveSubClass(activeSubclass.getClassId(), false);
        }

        if (getActiveClass() == null) {
            //если из-за какого-либо сбоя ни один из сабкласов не отмечен как активный помечаем базовый как активный
            final SubClass subClass = getSubClasses().get(baseClassId);
            subClass.setActive(true);
            setActiveSubClass(subClass.getClassId(), false);
        }
    }

    /**
     * Set the template of the L2Player.
     *
     * @param id The Identifier of the L2PlayerTemplate to set to the L2Player
     */
    public synchronized void setClassId(final int id, final boolean noban, final boolean fromQuest) {
        final Player player = getPlayer();
        if (!noban && !(ClassId.VALUES[id].equalsOrChildOf(ClassId.VALUES[getActiveClassId()]) || player.getPlayerAccess().CanChangeClass || ServerConfig.EVERYBODY_HAS_ADMIN_RIGHTS)) {
            Thread.dumpStack();
            return;
        }

        //Если новый ID не принадлежит имеющимся классам значит это новая профа
        if (!getSubClasses().containsKey(id)) {
            final SubClass cclass = getActiveClass();
            getSubClasses().remove(getActiveClassId());
            changeClassInDb(cclass.getClassId(), id);
            if (cclass.isBase()) {
                setBaseClass(id);
                Olympiad.changeNobleClass(player.getObjectId(), getBaseClassId());
                player.addClanPointsOnProfession(id);
                ItemInstance coupons = null;
                if (ClassId.VALUES[id].isOfLevel(ClassLevel.Second)) {
                    if (fromQuest && AllSettingsConfig.ALT_ALLOW_SHADOW_WEAPONS) // FIXME[K] - перенести в квесты!
                    {
                        coupons = ItemFunctions.createItem(8869);
                    }
                    player.getPlayerVariables().remove(PlayerVariables.NEWBIE_WEAPON);
                    player.getPlayerVariables().remove(PlayerVariables.profession_145);
                } else if (ClassId.VALUES[id].isOfLevel(ClassLevel.Third)) {
                    if (fromQuest && AllSettingsConfig.ALT_ALLOW_SHADOW_WEAPONS) // FIXME[K] - перенести в квесты!
                    {
                        coupons = ItemFunctions.createItem(8870);
                    }
                    player.getPlayerVariables().remove(PlayerVariables.NEWBIE_ARMOR);
                    player.getPlayerVariables().remove(PlayerVariables.DD1); // удаляем отметки о выдаче дименшен даймондов
                    player.getPlayerVariables().remove(PlayerVariables.DD2);
                    player.getPlayerVariables().remove(PlayerVariables.DD3);
                    player.getPlayerVariables().remove(PlayerVariables.PROF2_1);
                    player.getPlayerVariables().remove(PlayerVariables.PROF2_2);
                    player.getPlayerVariables().remove(PlayerVariables.PROF2_3);
                }

                if (coupons != null) {
                    coupons.setCount(15);
                    player.sendPacket(SystemMessage.obtainItems(coupons));
                    player.getInventory().addItem(coupons);
                }
            }

            // Выдача Holy Pomander
            switch (ClassId.VALUES[id]) // FIXME[K] - перенести в квесты!
            {
                case cardinal:
                    ItemFunctions.addItem(player, 15307, 1, true);
                    break;
                case eva_saint:
                    ItemFunctions.addItem(player, 15308, 1, true);
                    break;
                case shillien_saint:
                    ItemFunctions.addItem(player, 15309, 4, true);
                    break;
            }

            cclass.setClassId(id);
            getSubClasses().put(id, cclass);
            player.rewardSkills(true);
            storeCharSubClasses();

            if (fromQuest) {
                // Социалка при получении профы
                player.broadcastPacket(new MagicSkillUse(player, player, 5103, 1, 1000, 0));
                player.broadcastPacket(new SocialAction(player.getObjectId(), 3));
                player.sendPacket(new PlaySound("ItemSound.quest_fanfare_2"));
            }
            player.broadcastCharInfo();
        }

        // Update class icon in party and clan
        if (player.isInParty()) {
            player.getParty().broadCast(new PartySmallWindowUpdate(player));
        }
        if (player.getClan() != null) {
            player.getClan().broadcastToOnlineMembers(new PledgeShowMemberListUpdate(player));
        }
        if (player.getMatchingRoom() != null) {
            player.getMatchingRoom().broadcastPlayerUpdate(player);
        }
    }
}