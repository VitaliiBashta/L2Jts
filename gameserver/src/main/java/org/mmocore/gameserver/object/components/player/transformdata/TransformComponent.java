package org.mmocore.gameserver.object.components.player.transformdata;

import org.jts.dataparser.data.holder.pcparameter.common.LevelBonus;
import org.jts.dataparser.data.holder.pcparameter.common.LevelParameter;
import org.jts.dataparser.data.holder.setting.common.PlayerSex;
import org.jts.dataparser.data.holder.transform.*;
import org.jts.dataparser.data.holder.transform.type.TransformType;
import org.jts.dataparser.data.pch.LinkerFactory;
import org.mmocore.gameserver.data.client.holder.NpcNameLineHolder;
import org.mmocore.gameserver.data.client.holder.TransformDataLineHolder;
import org.mmocore.gameserver.data.xml.holder.SkillAcquireHolder;
import org.mmocore.gameserver.database.dao.impl.CharacterSkillDAO;
import org.mmocore.gameserver.model.Effect;
import org.mmocore.gameserver.network.lineage.serverpackets.SkillList;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.LockType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.client.NpcNameLine;
import org.mmocore.gameserver.templates.client.TransformDataLine;
import org.mmocore.gameserver.utils.Language;
import org.mmocore.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * Create by Mangol on 02.10.2015.
 */
public class TransformComponent {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransformComponent.class);
    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicBoolean stateUpdate = new AtomicBoolean(true);
    private final Map<Integer, SkillEntry> tSkills = new HashMap<>();
    private final Map<Integer, SkillEntry> tAdditionalSkill = new HashMap<>();
    private final Map<Integer, SkillEntry> allSkill = new HashMap<>();
    private final Player player;
    private final TransformData data;
    private final int id;
    private TCombat combat;
    private TCommon common;
    private String name_transform;
    private int giveLevelSkill;

    private TransformComponent(final Player player, final TransformData data) {
        this.player = player;
        this.data = data;
        this.id = data.id;
        if (isCombatTransformed()) {
            this.combat = getSex().stream().findFirst().get().combat_begin.stream().findFirst().get();
        }
        this.common = getSex().stream().findFirst().get().common_begin.stream().findFirst().get();
        startTransform();
    }

    public static TransformComponent createStartTransform(final Player player, final TransformData data) {
        return new TransformComponent(player, data);
    }

    private void startTransform() {
        //Останавливаем тугл скилы.
        player.getEffectList().getAllEffects().stream().filter(e -> e.getSkill().getTemplate().isToggle()).forEach(Effect::exit);
        // Заполняем мап скилов которые не зависят от уровня игрока. Заполняется при выдаче трансформы.
        fillSkills();
        // Генерируем скилы трансформации которые(зависят, не зависят от уровня игрока) и выдаем скилы игроку.
        addGiveAllSkills(player.getLevel());
        final Optional<TItemCheck> item_check = getCommon().item_check;
        if (item_check.isPresent()) {
            final LinkerFactory linker = LinkerFactory.getInstance();
            final int[] item_check_arrays = new int[item_check.get().item_name.length];
            for (int i = 0; i < item_check.get().item_name.length; i++) {
                item_check_arrays[i] = linker.findClearValue(item_check.get().item_name[i]);
            }
            if (item_check.get().name.equalsIgnoreCase("allow")) {
                player.getInventory().lockItems(LockType.allow, item_check_arrays);
            }
        }
        if (data.type == TransformType.RIDING_MODE) {
            player.setRiding(true);
        } else if (data.type == TransformType.FLYING) {
            final boolean isVisible = player.isVisible();
            if (player.getServitor() != null) {
                player.getServitor().unSummon(false, false);
            }
            player.decayMe();
            player.setFlying(true);
            player.setLoc(new Location(player.getX(), player.getY(), player.getZ() + getData().spawn_height, player.getHeading()));
            if (isVisible) {
                player.spawnMe();
            }
        } else if (data.type == TransformType.CURSED) {
            //Выдаем имя трансформации для курсед трансформ.
            final PlayerSex sex = player.getPlayerTemplateComponent().getPlayerSex();
            final Optional<TransformDataLine> transformLine = Optional.ofNullable(TransformDataLineHolder.getInstance().get(sex, getId()));
            final int npcId = transformLine.isPresent() ? transformLine.get().getNpcId() : 0;
            final Optional<NpcNameLine> npcnameLine = Optional.ofNullable(NpcNameLineHolder.getInstance().get(Language.ENGLISH, npcId));
            if (npcnameLine.isPresent()) {
                name_transform = npcnameLine.get().getName();
            } else {
                LOGGER.warn("npc name null - transform id " + getId() + " npc_id " + npcId);
                name_transform = player.getName();
            }
        }
    }

    public void stopTransform() {
        //Останавливаем тугл скилы.
        player.getEffectList().getAllEffects().stream().filter(e -> e.getSkill().getTemplate().isToggle()).forEach(Effect::exit);
        final Optional<TItemCheck> itemCheck = getCommon().item_check;
        if (itemCheck.isPresent()) {
            player.getInventory().unlock();
        }
        if (data.type == TransformType.RIDING_MODE) {
            player.setRiding(false);
        } else if (data.type == TransformType.FLYING) {
            final boolean isVisible = player.isVisible();
            player.decayMe();
            player.setFlying(false);
            player.setLoc(new Location(player.getX(), player.getY(), player.getZ(), player.getHeading()).correctGeoZ());
            if (isVisible) {
                player.spawnMe();
            }
        }
        if (tSkills.size() > 0) {
            tSkills.values().stream().filter(s -> !SkillAcquireHolder.getInstance().isSkillPossible(player, s)).forEach(player::removeSkill);
        }
        if (tAdditionalSkill.size() > 0) {
            tAdditionalSkill.values().stream().filter(s -> !SkillAcquireHolder.getInstance().isSkillPossible(player, s)).forEach(player::removeSkill);
        }
        tSkills.clear();
        tAdditionalSkill.clear();
        allSkill.clear();
        CharacterSkillDAO.getInstance().select(player);
        player.sendPacket(new SkillList(player));
    }

    public int getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public TransformData getData() {
        return data;
    }

    public TCombat getCombat() {
        return combat;
    }

    public void setTCombat(final TCombat combat) {
        this.combat = combat;
    }

    public TCommon getCommon() {
        return common;
    }

    public List<TOptionsSex> getSex() {
        if (player.getPlayerTemplateComponent().getPlayerSex() == PlayerSex.FEMALE) {
            return data.female_begin;
        } else if (player.getPlayerTemplateComponent().getPlayerSex() == PlayerSex.MALE) {
            return data.male_begin;
        }
        return null;
    }

    public double getRegen(final LevelParameter type) {
        if (type == LevelParameter.cp) {
            return getCombat().org_cp_regen[player.getLevel() - 1];
        } else if (type == LevelParameter.hp) {
            return getCombat().org_hp_regen[player.getLevel() - 1];
        } else if (type == LevelParameter.mp) {
            return getCombat().org_mp_regen[player.getLevel() - 1];
        } else {
            LOGGER.warn("Incorect regen type: " + type.name());
        }
        return 0;
    }

    public double getTable(final LevelParameter type) {
        if (type == LevelParameter.cp) {
            return getCombat().cp_table[player.getLevel() - 1];
        } else if (type == LevelParameter.hp) {
            return getCombat().hp_table[player.getLevel() - 1];
        } else if (type == LevelParameter.mp) {
            return getCombat().mp_table[player.getLevel() - 1];
        } else {
            LOGGER.warn("Incorect table type: " + type.name());
        }
        return 0;
    }

    public double getBonus(final LevelBonus type) {
        final TCombat combat = getCombat();
        if (type == LevelBonus.str) {
            final int str = player.getSTR();
            return (double) (100 + combat.str_bonus[str - 1]) / 100;
        } else if (type == LevelBonus.con) {
            final int con = player.getCON();
            return (double) (100 + combat.con_bonus[con - 1]) / 100;
        } else if (type == LevelBonus.dex) {
            final int dex = player.getDEX();
            return (double) (100 + combat.dex_bonus[dex - 1]) / 100;
        } else if (type == LevelBonus.men) {
            final int men = player.getMEN();
            return (double) (100 + combat.men_bonus[men - 1]) / 100;
        } else if (type == LevelBonus.wit) {
            final int wit = player.getWIT();
            return (double) (100 + combat.wit_bonus[wit - 1]) / 100;
        } else if (type == LevelBonus._int) {
            final int _int = player.getINT();
            return (double) (100 + combat.int_bonus[_int - 1]) / 100;
        } else {
            LOGGER.warn("Incorect bonus type: " + type.name());
        }
        return 0;
    }

    public double getLevelBonus() {
        final double[] level_bonus = getCombat().level_bonus;
        if (level_bonus.length > 0) {
            return level_bonus[player.getLevel() - 1];
        } else {
            LOGGER.warn("Incorect arrays level_bonus");
        }
        return 0;
    }

    /**
     * Заполняет мап списком скилов трансформации
     * Заполнять 1 раз при выдаче трансформы.
     */
    private void fillSkills() {
        final LinkerFactory linker = LinkerFactory.getInstance();
        final String[] skills = getCommon().skill;
        if (skills.length > 0) {
            for (final String s : skills) {
                final int skill_id = linker.skillPchIdfindClearValue(s)[0];
                final int skill_lvl = linker.skillPchIdfindClearValue(s)[1];
                final SkillEntry skill = SkillTable.getInstance().getSkillEntry(skill_id, skill_lvl);
                tSkills.put(skill.getId(), skill);
            }
        }
    }

    /**
     * Заполняет мап списком скилов трансформации которые зависят от уровня игрока.
     *
     * @param clear       - Очистить мап от скилов трансформации которые зависят от уровня игрока?
     * @param playerLevel - Уровень игрока
     */
    private void fillAdditionalSkills(final int playerLevel, final boolean clear) {
        if (clear) {
            tAdditionalSkill.clear();
        }
        final TAdditionalSkill[] additional_skills = getCommon().additional_skill;
        if (additional_skills.length > 0) {
            final LinkerFactory linker = LinkerFactory.getInstance();
            Stream.of(additional_skills).filter(s -> playerLevel >= s.level).forEach(s -> Stream.of(s.skill_name).forEach(skill -> {
                final int skill_id = linker.skillPchIdfindClearValue(skill)[0];
                final int skill_lvl = linker.skillPchIdfindClearValue(skill)[1];
                final SkillEntry skill_entry = SkillTable.getInstance().getSkillEntry(skill_id, skill_lvl);
                tAdditionalSkill.put(skill_entry.getId(), skill_entry);
            }));
        }
    }

    /**
     * @param skillEntry       - Добавляет скил к текущему листу скилов трансформации,
     *                         которые не зависят от уровня трансформации.
     *                         Так же обновляя список всех скилов трансформации.
     * @param giveSkill        - добавить игроку новый скил?
     * @param refreshSkillList - отправлять ли пакет игроку о списке новых скилов ?
     */
    public void addSkill(final SkillEntry skillEntry, final boolean giveSkill, final boolean refreshSkillList) {
        tSkills.put(skillEntry.getId(), skillEntry);
        allSkill.put(skillEntry.getId(), skillEntry);
        if (giveSkill) {
            player.addSkill(skillEntry, false);
        }
        if (refreshSkillList) {
            player.sendPacket(new SkillList(player));
        }
    }

    public Map<Integer, SkillEntry> getSkills() {
        return tSkills;
    }

    /**
     * Возвращает коллекцию скилов трансформации которые не зависят от уровня игрока.
     */
    public Collection<SkillEntry> getSkillsValues() {
        return tSkills.values();
    }

    /**
     * Возвращает коллекцию скилов трансформации которые зависят от уровня игрока.
     */
    public Collection<SkillEntry> getAdditionalSkills() {
        return tAdditionalSkill.values();
    }

    /**
     * Возвращает коллекцию скилов трансформации.
     */
    public Collection<SkillEntry> getAllSkills() {
        lock.lock();
        try {
            if (stateUpdate.compareAndSet(true, false)) {
                allSkill.clear();
                player.getSkills().stream().filter(s -> s != null && s.getTemplate().isPassive()).forEach(s -> allSkill.put(s.getId(), s));
                allSkill.putAll(tSkills);
                allSkill.putAll(tAdditionalSkill);
                return allSkill.values();
            }
            return allSkill.values();
        } finally {
            lock.unlock();
        }
    }

    /**
     * @param level - уровень игрока.
     */
    public void addGiveAllSkills(final int level) {
        lock.lock();
        try {
            if (level > giveLevelSkill)
                giveLevelSkill = level;
            else if (allSkill.size() > 0)
                allSkill.values().stream().filter(s -> tSkills.containsKey(s.getId()) || tAdditionalSkill.containsKey(s.getId())).forEach(player::removeSkill);
            //Обновим мап скилов которые зависят от уровня игрока.
            fillAdditionalSkills(level, true);
            //Тут полюбому нужно зачистить мап.
            allSkill.clear();
            player.getSkills().stream().filter(s -> s != null && s.getTemplate().isPassive()).forEach(s -> allSkill.put(s.getId(), s));
            //Добавляем все скилы трансформации которые не зависят от уровня игрока.
            allSkill.putAll(tSkills);
            // Добавляем все скилы трансформации которые зависят от уровня игрока.
            allSkill.putAll(tAdditionalSkill);
            // Теперь добавим все скилы игроку.
            allSkill.values().stream().forEach(s -> player.addSkill(s, false));
            stateUpdate.set(false);
        } finally {
            lock.unlock();
        }
    }

    private boolean isCombatTransformed() {
        final TransformType type = getData().type;
        return type == TransformType.COMBAT || type == TransformType.CURSED || type == TransformType.FLYING || type == TransformType.PURE_STAT;
    }

    public String getNameTransform() {
        return name_transform;
    }

    public void setStateUpdate(boolean update) {
        stateUpdate.set(update);
    }
}
