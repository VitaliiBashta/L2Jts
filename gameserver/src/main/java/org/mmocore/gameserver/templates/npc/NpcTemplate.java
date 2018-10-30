package org.mmocore.gameserver.templates.npc;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.utils.TroveUtils;
import org.mmocore.gameserver.ai.CharacterAI;
import org.mmocore.gameserver.data.scripts.Scripts;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.TeleportLocation;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.base.NpcRace;
import org.mmocore.gameserver.model.buylist.BuyList;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.RaidBossInstance;
import org.mmocore.gameserver.model.instances.ReflectionBossInstance;
import org.mmocore.gameserver.model.quest.Quest;
import org.mmocore.gameserver.model.quest.QuestEventType;
import org.mmocore.gameserver.model.reward.RewardList;
import org.mmocore.gameserver.model.reward.RewardType;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.skills.effects.EffectTemplate;
import org.mmocore.gameserver.templates.CharTemplate;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.utils.idfactory.IdFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.*;

public final class NpcTemplate extends CharTemplate {
    @SuppressWarnings("unchecked")
    public static final Constructor<NpcInstance> DEFAULT_TYPE_CONSTRUCTOR = (Constructor<NpcInstance>) NpcInstance.class.getConstructors()[0];
    @SuppressWarnings("unchecked")
    public static final Constructor<CharacterAI> DEFAULT_AI_CONSTRUCTOR = (Constructor<CharacterAI>) CharacterAI.class.getConstructors()[0];
    private static final Logger LOGGER = LoggerFactory.getLogger(NpcTemplate.class);
    public final int npcId;
    public final String name;
    public final String title;
    // не используется - public final String sex;
    public final int level;
    public final long rewardExp;
    public final int rewardSp;
    public final int rewardRp;
    public final int aggroRange;
    public final int rhand;
    public final int lhand;
    public final double rateHp;
    public final String jClass;
    public final int displayId;
    private final int soulShotCount;
    private final int spiritShotCount;
    private final StatsSet aiParams;
    private final int castleId;
    private final String htmRoot;
    public boolean isRaid;
    private long corpse_time;
    private Faction faction = Faction.NONE;
    /**
     * fixed skills
     */
    private NpcRace race;
    private Map<RewardType, RewardList> rewards = Collections.emptyMap();
    private TIntObjectMap<TeleportLocation[]> teleportList = TroveUtils.emptyIntObjectMap();
    private TIntObjectMap<BuyList> tradeList = TroveUtils.emptyIntObjectMap();
    private List<AbsorbInfo> absorbInfos = Collections.emptyList();
    private List<ClassId> teachInfo = Collections.emptyList();
    private Map<QuestEventType, Quest[]> questEvents = Collections.emptyMap();
    private TIntObjectMap<SkillEntry> skills = TroveUtils.emptyIntObjectMap();
    private SkillEntry[] damageSkills = SkillEntry.EMPTY_ARRAY;
    private SkillEntry[] dotSkills = SkillEntry.EMPTY_ARRAY;
    private SkillEntry[] debuffSkills = SkillEntry.EMPTY_ARRAY;
    private SkillEntry[] buffSkills = SkillEntry.EMPTY_ARRAY;
    private SkillEntry[] stunSkills = SkillEntry.EMPTY_ARRAY;
    private SkillEntry[] healSkills = SkillEntry.EMPTY_ARRAY;
    private Class<NpcInstance> classType = NpcInstance.class;
    private Constructor<NpcInstance> constructorType = DEFAULT_TYPE_CONSTRUCTOR;
    private Class<CharacterAI> aiClass = CharacterAI.class;
    private Constructor<CharacterAI> aiConstructor = DEFAULT_AI_CONSTRUCTOR;

    /**
     * Constructor<?> of L2Character.<BR><BR>
     *
     * @param set The StatsSet object to transfer data to the method
     */
    public NpcTemplate(final StatsSet set) {
        super(set);
        npcId = set.getInteger("npcId");
        displayId = set.getInteger("displayId");

        name = set.getString("name");
        title = set.getString("title");
        // sex = set.getString("sex");
        level = set.getInteger("level");
        rewardExp = set.getLong("rewardExp");
        rewardSp = set.getInteger("rewardSp");
        rewardRp = (int) set.getDouble("rewardRp");
        aggroRange = set.getInteger("aggroRange");
        rhand = set.getInteger("rhand", 0);
        lhand = set.getInteger("lhand", 0);
        rateHp = set.getDouble("baseHpRate");
        jClass = set.getString("texture", null);
        htmRoot = set.getString("htm_root", null);
        castleId = set.getInteger("castle_id", 0);
        soulShotCount = set.getInteger("soulshot_count", 0);
        spiritShotCount = set.getInteger("spiritshot_count", 0);
        aiParams = (StatsSet) set.getObject("aiParams", StatsSet.EMPTY);
        setCorpseTime(set.getLong("corpse_time", 0) * 1000);
        setType(set.getString("type", null));
        setAI(set.getString("ai_type", null));
    }

    public Class<? extends NpcInstance> getInstanceClass() {
        return classType;
    }

    public Constructor<? extends NpcInstance> getInstanceConstructor() {
        return constructorType;
    }

    public boolean isInstanceOf(final Class<?> _class) {
        return _class.isAssignableFrom(classType);
    }

    /**
     * Создает новый инстанс NPC. Для него следует вызывать (именно в этом порядке):
     * <br> setSpawnedLoc (обязательно)
     * <br> setReflection (если reflection не базовый)
     * <br> setChampion (опционально)
     * <br> setCurrentHpMp (если вызывался setChampion)
     * <br> spawnMe (в качестве параметра брать getSpawnedLoc)
     */
    public NpcInstance getNewInstance() {
        try {
            return constructorType.newInstance(IdFactory.getInstance().getNextId(), this);
        } catch (Exception e) {
            LOGGER.error("Unable to create instance of NPC " + npcId, e);
        }

        return null;
    }

    public CharacterAI getNewAI(final NpcInstance npc) {
        try {
            return aiConstructor.newInstance(npc);
        } catch (Exception e) {
            LOGGER.error("Unable to create ai of NPC " + npcId, e);
        }

        return new CharacterAI(npc);
    }

    @SuppressWarnings("unchecked")
    private void setType(final String type) {
        Class<NpcInstance> classType;
        try {
            classType = (Class<NpcInstance>) Class.forName("org.mmocore.gameserver.model.instances." + type + "Instance");
        } catch (ClassNotFoundException e) {
            classType = (Class<NpcInstance>) Scripts.getInstance().getClasses().get("npc.model." + type + "Instance");
        }

        if (classType == null) {
            LOGGER.error("Not found type class for type: " + type + ". NpcId: " + npcId);
        } else {
            this.classType = classType;
            constructorType = (Constructor<NpcInstance>) this.classType.getConstructors()[0];
        }

        if (this.classType.isAnnotationPresent(Deprecated.class)) {
            LOGGER.error("Npc type: " + type + ", is deprecated. NpcId: " + npcId);
        }

        //TODO [G1ta0] сделать поле в соотвествующих классах
        isRaid = isInstanceOf(RaidBossInstance.class) && !isInstanceOf(ReflectionBossInstance.class);
    }

    @SuppressWarnings("unchecked")
    private void setAI(final String ai) {
        Class<CharacterAI> classAI;
        try {
            classAI = (Class<CharacterAI>) Class.forName("org.mmocore.gameserver.ai." + ai);
        } catch (ClassNotFoundException e) {
            classAI = (Class<CharacterAI>) Scripts.getInstance().getClasses().get("ai." + ai);
        }

        if (classAI == null) {
            LOGGER.error("Not found ai class for ai: " + ai + ". NpcId: " + npcId);
        } else {
            aiClass = classAI;
            aiConstructor = (Constructor<CharacterAI>) aiClass.getConstructors()[0];
        }

        if (aiClass.isAnnotationPresent(Deprecated.class)) {
            LOGGER.error("Ai type: " + ai + ", is deprecated. NpcId: " + npcId);
        }
    }

    public void addTeachInfo(final ClassId classId) {
        if (teachInfo.isEmpty()) {
            teachInfo = new ArrayList<>(1);
        }
        teachInfo.add(classId);
    }

    public List<ClassId> getTeachInfo() {
        return teachInfo;
    }

    public boolean canTeach(final ClassId classId) {
        return teachInfo.contains(classId);
    }

    public void addTeleportList(final int id, final TeleportLocation[] list) {
        if (teleportList.isEmpty()) {
            teleportList = new TIntObjectHashMap<>(1);
        }

        teleportList.put(id, list);
    }

    public TeleportLocation[] getTeleportList(final int id) {
        return teleportList.get(id);
    }

    public TIntObjectMap<TeleportLocation[]> getTeleportList() {
        return teleportList;
    }

    public void addTradeList(final int id, final BuyList list) {
        if (tradeList.isEmpty()) {
            tradeList = new TIntObjectHashMap<>(1);
        }

        tradeList.put(id, list);
    }

    public BuyList getTradeList(final int id) {
        return tradeList.get(id);
    }

    public TIntObjectMap<BuyList> getTradeList() {
        return tradeList;
    }

    public void putRewardList(final RewardType rewardType, final RewardList list) {
        if (rewards.isEmpty()) {
            rewards = new EnumMap<>(RewardType.class);
        }
        rewards.put(rewardType, list);
    }

    public RewardList getRewardList(final RewardType t) {
        return rewards.get(t);
    }

    public Map<RewardType, RewardList> getRewards() {
        return rewards;
    }

    public void addAbsorbInfo(final AbsorbInfo absorbInfo) {
        if (absorbInfos.isEmpty()) {
            absorbInfos = new ArrayList<>(1);
        }

        absorbInfos.add(absorbInfo);
    }

    public Faction getFaction() {
        return faction;
    }

    public void setFaction(final Faction faction) {
        this.faction = faction;
    }

    public void addSkill(final SkillEntry skill) {
        if (skills.isEmpty())
            skills = new TIntObjectHashMap<>();

        skills.put(skill.getId(), skill);

        //TODO [G1ta0] перенести в AI
        if (skill.getTemplate().isNotUsedByAI() || skill.getTemplate().getTargetType() == Skill.SkillTargetType.TARGET_NONE || skill.getSkillType() == Skill.SkillType.NOTDONE || !skill.getTemplate().isActive()) {
            return;
        }

        switch (skill.getSkillType()) {
            case PDAM:
            case MANADAM:
            case MDAM:
            case DRAIN:
            case DRAIN_SOUL:
                boolean added = false;

                if (skill.getTemplate().hasEffects()) {
                    for (final EffectTemplate eff : skill.getTemplate().getEffectTemplates()) {
                        switch (eff.getEffectType()) {
                            case Stun:
                                stunSkills = ArrayUtils.add(stunSkills, skill);
                                added = true;
                                break;
                            case DamOverTime:
                            case DamOverTimeLethal:
                            case ManaDamOverTime:
                            case LDManaDamOverTime:
                                dotSkills = ArrayUtils.add(dotSkills, skill);
                                added = true;
                                break;
                        }
                    }
                }

                if (!added) {
                    damageSkills = ArrayUtils.add(damageSkills, skill);
                }

                break;
            case DOT:
            case MDOT:
            case POISON:
            case BLEED:
                dotSkills = ArrayUtils.add(dotSkills, skill);
                break;
            case DEBUFF:
            case SLEEP:
            case ROOT:
            case PARALYZE:
            case MUTE:
            case TELEPORT_NPC:
            case AGGRESSION:
                debuffSkills = ArrayUtils.add(debuffSkills, skill);
                break;
            case BUFF:
                buffSkills = ArrayUtils.add(buffSkills, skill);
                break;
            case STUN:
                stunSkills = ArrayUtils.add(stunSkills, skill);
                break;
            case HEAL:
            case HEAL_PERCENT:
            case HOT:
                healSkills = ArrayUtils.add(healSkills, skill);
                break;
            default:

                break;
        }
    }

    public SkillEntry[] getDamageSkills() {
        return damageSkills;
    }

    public SkillEntry[] getDotSkills() {
        return dotSkills;
    }

    public SkillEntry[] getDebuffSkills() {
        return debuffSkills;
    }

    public SkillEntry[] getBuffSkills() {
        return buffSkills;
    }

    public SkillEntry[] getStunSkills() {
        return stunSkills;
    }

    public SkillEntry[] getHealSkills() {
        return healSkills;
    }

    public TIntObjectMap<SkillEntry> getSkills() {
        return skills;
    }

    public void addQuestEvent(final QuestEventType EventType, final Quest q) {
        if (questEvents.isEmpty()) {
            questEvents = new EnumMap<>(QuestEventType.class);
        }

        if (questEvents.get(EventType) == null) {
            questEvents.put(EventType, new Quest[]{q});
        } else {
            final Quest[] _quests = questEvents.get(EventType);
            final int len = _quests.length;

            final Quest[] tmp = new Quest[len + 1];
            for (int i = 0; i < len; i++) {
                if (_quests[i].getName().equals(q.getName())) {
                    _quests[i] = q;
                    return;
                }
                tmp[i] = _quests[i];
            }
            tmp[len] = q;

            questEvents.put(EventType, tmp);
        }
    }

    public Quest[] getEventQuests(final QuestEventType EventType) {
        return questEvents.get(EventType);
    }

    public NpcRace getRace() {
        return race;
    }

    public void setRace(final NpcRace newrace) {
        race = newrace;
    }

    public boolean isUndead() {
        return race == NpcRace.UNDEAD;
    }

    @Override
    public String toString() {
        return "Npc template " + name + '[' + npcId + ']';
    }

    @Override
    public int getNpcId() {
        return npcId;
    }

    public String getName() {
        return name;
    }

    public final String getJClass() {
        return jClass;
    }

    public final StatsSet getAIParams() {
        return aiParams;
    }

    public List<AbsorbInfo> getAbsorbInfo() {
        return absorbInfos;
    }

    public int getCastleId() {
        return castleId;
    }

    public Map<QuestEventType, Quest[]> getQuestEvents() {
        return questEvents;
    }

    public String getHtmRoot() {
        return htmRoot;
    }

    public int getSoulShotCount() {
        return soulShotCount;
    }

    public int getSpiritShotCount() {
        return spiritShotCount;
    }

    /**
     * Возвращает время которое моб лежит на земле после смерти (милисекунды)
     *
     * @return Возвращает время которое моб лежит на земле после смерти (милисекунды)
     */
    public long getCorpseTime() {
        return corpse_time;
    }

    /**
     * Указывает время которое моб лежит на земле после смерти (указывать в милисекундах)
     *
     * @param _corpse_time - время которое моб еще лежит на земле после смерти указываеться в милисекундах;
     */
    public void setCorpseTime(final long _corpse_time) {
        corpse_time = _corpse_time;
    }
}
