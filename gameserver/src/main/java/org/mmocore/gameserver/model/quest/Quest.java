package org.mmocore.gameserver.model.quest;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;
import org.apache.commons.lang3.ArrayUtils;
import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.commons.logging.LogUtils;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.TroveUtils;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.data.xml.holder.QuestCustomParamsHolder;
import org.mmocore.gameserver.database.dao.impl.CharacterQuestDAO;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.QuestManager;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.entity.olympiad.OlympiadGame;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.quest.startcondition.ConditionList;
import org.mmocore.gameserver.model.quest.startcondition.ICheckStartCondition;
import org.mmocore.gameserver.model.quest.startcondition.impl.*;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExQuestNpcLogList;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.QuestCustomParams;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.HtmlUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Quest implements OnInitScriptListener {
    public static final String SOUND_ITEMGET = "ItemSound.quest_itemget";
    public static final String SOUND_ACCEPT = "ItemSound.quest_accept";
    public static final String SOUND_MIDDLE = "ItemSound.quest_middle";
    public static final String SOUND_FINISH = "ItemSound.quest_finish";
    public static final String SOUND_GIVEUP = "ItemSound.quest_giveup";
    public static final String SOUND_TUTORIAL = "ItemSound.quest_tutorial";
    public static final String SOUND_JACKPOT = "ItemSound.quest_jackpot";
    public static final String SOUND_SYS_SIREN = "ItemSound3.sys_siren";
    public static final String SOUND_ANTARAS_FEAR = "SkillSound3.antaras_fear";
    public static final String SOUND_HORROR1 = "SkillSound5.horror_01";
    public static final String SOUND_HORROR2 = "SkillSound5.horror_02";
    public static final String SOUND_LIQUID_MIX_01 = "SkillSound5.liquid_mix_01";
    public static final String SOUND_LIQUID_SUCCESS_01 = "SkillSound5.liquid_success_01";
    public static final String SOUND_LIQUID_FAIL_01 = "SkillSound5.liquid_fail_01";
    public static final String SOUND_BEFORE_BATTLE = "Itemsound.quest_before_battle";
    public static final String SOUND_FANFARE_MIDDLE = "ItemSound.quest_fanfare_middle";
    public static final String SOUND_FANFARE1 = "ItemSound.quest_fanfare_1";
    public static final String SOUND_FANFARE2 = "ItemSound.quest_fanfare_2";
    public static final String SOUND_BROKEN_KEY = "ItemSound2.broken_key";
    public static final String SOUND_ENCHANT_SUCESS = "ItemSound3.sys_enchant_sucess";
    public static final String SOUND_ENCHANT_FAILED = "ItemSound3.sys_enchant_failed";
    public static final String SOUND_SYS_SOW_SUCCESS = "ItemSound3.sys_sow_success";
    public static final String SOUND_ED_CHIMES05 = "AmdSound.ed_chimes_05";
    public static final String SOUND_DD_HORROR_01 = "AmbSound.dd_horror_01";
    public static final String SOUND_DD_HORROR_02 = "AmdSound.dd_horror_02";
    public static final String SOUND_D_HORROR_03 = "AmbSound.d_horror_03";
    public static final String SOUND_D_HORROR_15 = "AmbSound.d_horror_15";
    public static final String SOUND_ARMOR_WOOD_3 = "ItemSound.armor_wood_3";
    public static final String SOUND_ITEM_DROP_EQUIP_ARMOR_CLOTH = "ItemSound.item_drop_equip_armor_cloth";
    public static final String SOUND_FDELF_CRY = "ChrSound.FDElf_Cry";
    public static final String SOUND_CHARSTAT_OPEN_01 = "InterfaceSound.charstat_open_01";
    public static final String SOUND_D_WIND_LOOT_02 = "AmdSound.d_wind_loot_02";
    public static final String SOUND_ELCROKI_SONG_FULL = "EtcSound.elcroki_song_full";
    public static final String SOUND_ELCROKI_SONG_1ST = "EtcSound.elcroki_song_1st";
    public static final String SOUND_ELCROKI_SONG_2ND = "EtcSound.elcroki_song_2nd";
    public static final String SOUND_ELCROKI_SONG_3RD = "EtcSound.elcroki_song_3rd";
    public static final String SOUND_CD_CRYSTAL_LOOP = "AmbSound.cd_crystal_loop";
    public static final String SOUND_DT_PERCUSSION_01 = "AmbSound.dt_percussion_01";
    public static final String SOUND_AC_PERCUSSION_02 = "AmbSound.ac_percussion_02";
    public static final String SOUND_ED_DRONE_02 = "AmbSound.ed_drone_02";
    public static final String SOUND_EG_DRON_02 = "AmbSound.eg_dron_02";
    public static final String SOUND_MT_CREAK01 = "AmbSound.mt_creak01";
    public static final String SOUND_ITEMDROP_ARMOR_LEATHER = "ItemSound.itemdrop_armor_leather";
    public static final String SOUND_MHFIGHTER_CRY = "ChrSound.MHFighter_cry";
    public static final String SOUND_ITEMDROP_WEAPON_SPEAR = "ItemSound.itemdrop_weapon_spear";
    public static final String SOUND_T_WINGFLAP_04 = "AmbSound.t_wingflap_04";
    public static final String THUNDER_02 = "AmbSound.thunder_02";
    public static final String NO_QUEST_DIALOG = "no-quest";
    public static final int ADENA_ID = 57;
    public static final int PARTY_NONE = 0;
    public static final int PARTY_ONE = 1;
    public static final int PARTY_ALL = 2;
    public static final int PARTY_CC = 3;
    public static final int CREATED = 1;
    public static final int STARTED = 2;
    public static final int COMPLETED = 3;
    public static final int DELAYED = 4;
    protected static final Logger LOGGER = LoggerFactory.getLogger(Quest.class);
    protected final String name;

    protected final int party;

    protected final int questId;
    //карта с приостановленными квестовыми таймерами для каждого игрока
    private final Map<Integer, Map<String, QuestTimer>> pausedQuestTimers = new ConcurrentHashMap<>();
    private final TIntHashSet questItems = new TIntHashSet();
    private TIntObjectMap<List<QuestNpcLogInfo>> npcLogList = TroveUtils.emptyIntObjectMap();
    private List<ICheckStartCondition> startConditionList;
    private int minLevel = 0;
    private int maxLevel = 99;

    /**
     * Deprecated.
     */
    public Quest(final boolean party) {
        this(party ? 1 : 0);
    }

    /**
     * 0 - по ластхиту, 1 - случайно по пати, 2 - всей пати, 3 - CC.
     */
    public Quest(final int party) {
        name = getClass().getSimpleName();
        questId = Integer.parseInt(name.split("_")[1]);
        this.party = party;
    }

    /**
     * Добавляет спаун с числовым значением разброса - от 50 до randomOffset.
     * Если randomOffset указан мене 50, то координаты не меняются.
     */
    public static NpcInstance addSpawnToInstance(final int npcId, final int x, final int y, final int z, final int heading, final int randomOffset, final int refId) {
        return addSpawnToInstance(npcId, new Location(x, y, z, heading), randomOffset, refId);
    }

    public static NpcInstance addSpawnToInstance(final int npcId, final Location loc, final int randomOffset, final int refId) {
        try {
            final NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
            if (template != null) {
                final NpcInstance npc = NpcHolder.getInstance().getTemplate(npcId).getNewInstance();
                npc.setReflection(refId);
                npc.setSpawnedLoc(randomOffset > 50 ? Location.findPointToStay(loc, 50, randomOffset, npc.getGeoIndex()) : loc);
                npc.spawnMe(npc.getSpawnedLoc());
                return npc;
            }
        } catch (Exception e1) {
            LOGGER.warn("Could not spawn Npc " + npcId);
        }
        return null;
    }

    public static String getStateName(final int state) {
        switch (state) {
            case CREATED:
                return "Start";
            case STARTED:
                return "Started";
            case COMPLETED:
                return "Completed";
            case DELAYED:
                return "Delayed";
        }
        return "Start";
    }

    public static int getStateId(final String state) {
        if ("Start".equalsIgnoreCase(state)) {
            return CREATED;
        } else if ("Started".equalsIgnoreCase(state)) {
            return STARTED;
        } else if ("Completed".equalsIgnoreCase(state)) {
            return COMPLETED;
        } else if ("Delayed".equalsIgnoreCase(state)) {
            return DELAYED;
        }
        return CREATED;
    }

    public List<QuestNpcLogInfo> getNpcLogList(final int cond) {
        return npcLogList.get(cond);
    }

    /**
     * Add this quest to the list of quests that the passed mob will respond to
     * for Attack Events.<BR>
     * <BR>
     *
     * @param attackIds
     */
    public void addAttackId(final int... attackIds) {
        for (final int attackId : attackIds) {
            addEventId(attackId, QuestEventType.ATTACKED_WITH_QUEST);
        }
    }

    /**
     * Add this quest to the list of quests that the passed mob will respond to
     * for the specified Event type.<BR>
     * <BR>
     *
     * @param npcId     : id of the NPC to register
     * @param eventType : type of event being registered
     * @return int : npcId
     */
    public NpcTemplate addEventId(final int npcId, final QuestEventType eventType) {
        try {
            final NpcTemplate t = NpcHolder.getInstance().getTemplate(npcId);
            if (t != null) {
                t.addQuestEvent(eventType, this);
            }
            return t;
        } catch (Exception e) {
            LOGGER.error("", e);
            return null;
        }
    }

    /**
     * Add this quest to the list of quests that the passed mob will respond to
     * for Kill Events.<BR>
     * <BR>
     *
     * @param killIds
     * @return int : killId
     */
    public void addKillId(final int... killIds) {
        for (final int killid : killIds) {
            addEventId(killid, QuestEventType.MOB_KILLED_WITH_QUEST);
        }
    }

    /**
     * Добавляет нпц масив для слушателя при их убийстве, и обновлении пакетом {@link org.mmocore.gameserver.network.lineage.serverpackets.ExQuestNpcLogList}
     *
     * @param cond
     * @param varName
     * @param killIds
     */
    public void addKillNpcWithLog(final int cond, final String varName, final int max, final int... killIds) {
        if (killIds.length == 0) {
            throw new IllegalArgumentException("Npc list cant be empty!");
        }

        addKillId(killIds);
        if (npcLogList.isEmpty()) {
            npcLogList = new TIntObjectHashMap<>(5);
        }

        List<QuestNpcLogInfo> vars = npcLogList.get(cond);
        if (vars == null) {
            npcLogList.put(cond, (vars = new ArrayList<>(5)));
        }

        vars.add(new QuestNpcLogInfo(killIds, varName, max));
    }

    public boolean updateKill(final NpcInstance npc, final QuestState st) {
        final Player player = st.getPlayer();
        if (player == null) {
            return false;
        }
        final List<QuestNpcLogInfo> vars = getNpcLogList(st.getCond());
        if (vars == null) {
            return false;
        }
        boolean done = true;
        boolean find = false;
        for (final QuestNpcLogInfo info : vars) {
            int count = st.getInt(info.getVarName());
            if (!find && ArrayUtils.contains(info.getNpcIds(), npc.getNpcId())) {
                find = true;
                if (count < info.getMaxCount()) {
                    st.setMemoState(info.getVarName(), ++count);
                    player.sendPacket(new ExQuestNpcLogList(st));
                }
            }

            if (count != info.getMaxCount()) {
                done = false;
            }
        }

        return done;
    }

    public void addKillId(final Collection<Integer> killIds) {
        for (final int killid : killIds) {
            addKillId(killid);
        }
    }

    /**
     * Add this quest to the list of quests that the passed npc will respond to
     * for Skill-Use Events.<BR>
     * <BR>
     *
     * @param npcId : ID of the NPC
     * @return int : ID of the NPC
     */
    public NpcTemplate addSkillUseId(final int npcId) {
        return addEventId(npcId, QuestEventType.MOB_TARGETED_BY_SKILL);
    }

    public void addStartNpc(final int... npcIds) {
        for (final int talkId : npcIds) {
            addStartNpc(talkId);
        }
    }

    /**
     * Add the quest to the NPC's startQuest
     * Вызывает addTalkId
     *
     * @param npcId
     * @return L2NpcTemplate : Start NPC
     */
    public NpcTemplate addStartNpc(final int npcId) {
        addTalkId(npcId);
        return addEventId(npcId, QuestEventType.QUEST_START);
    }

    /**
     * Add the quest to the NPC's first-talk (default action dialog)
     *
     * @param npcIds
     * @return L2NpcTemplate : Start NPC
     */
    public void addFirstTalkId(final int... npcIds) {
        for (final int npcId : npcIds) {
            addEventId(npcId, QuestEventType.NPC_FIRST_TALK);
        }
    }

    /**
     * Add this quest to the list of quests that the passed npc will respond to
     * for Talk Events.<BR>
     * <BR>
     *
     * @param talkIds : ID of the NPC
     * @return int : ID of the NPC
     */
    public void addTalkId(final int... talkIds) {
        for (final int talkId : talkIds) {
            addEventId(talkId, QuestEventType.QUEST_TALK);
        }
    }

    public void addTalkId(final Collection<Integer> talkIds) {
        for (final int talkId : talkIds) {
            addTalkId(talkId);
        }
    }

    public int getMinLevel() {
        return minLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Добавляем проверку с логическим ИЛИ, состоящую из нескольких кондишнов.
     * Проверка выполняется если одно из условий верно.
     *
     * @param conds - метод реализуется при двух и более проверок
     */
    public void addOrCond(ICheckStartCondition... conds) {
        if (startConditionList == null)
            startConditionList = new ArrayList<ICheckStartCondition>();
        startConditionList.add(new OrCondition(conds));
    }

    /**
     * Добавлем проверку на содержание итема в инвентаре
     *
     * @param itemIds - id предмета
     */
    public void addHasItemsCheck(int... itemIds) {
        if (startConditionList == null)
            startConditionList = new ArrayList<ICheckStartCondition>();
        startConditionList.add(new HasItemCondition(itemIds));
    }

    /**
     * Добавляет проверку уровня при старте квеста
     *
     * @param min - минимальный уровень
     * @param max - максимальный уровень
     */
    public void addLevelCheck(int min, int max) {
        if (startConditionList == null)
            startConditionList = new ArrayList<ICheckStartCondition>();
        final Optional<QuestCustomParams> param = QuestCustomParamsHolder.getInstance().get(getId());
        if (param.isPresent()) {
            minLevel = param.get().getLevelMin();
            maxLevel = param.get().getLevelMax();
        } else {
            minLevel = min;
            maxLevel = max;
        }
        startConditionList.add(new PlayerLevelCondition(min, max));
    }

    /**
     * Добавляет проверку уровня при старте квеста
     *
     * @param min - минимальный уровень
     */
    public void addLevelCheck(int min) {
        addLevelCheck(min, 99);
        minLevel = min;
    }

    /**
     * Добавляет проверку выполненного квеста
     *
     * @param questId - квеста пишется в формате шаблона без 000. Пример: (23)
     */
    public void addQuestCompletedCheck(int questId) {
        if (startConditionList == null)
            startConditionList = new ArrayList<ICheckStartCondition>();

        startConditionList.add(new QuestCompletedCondition(questId));
    }

    /**
     * Добавляет проверку на расу активного персонажа
     *
     * @param race - Race.ID расы
     */
    public void addRaceCheck(PlayerRace... race) {
        if (startConditionList == null)
            startConditionList = new ArrayList<ICheckStartCondition>();

        startConditionList.add(new RaceCondition(race));
    }

    /**
     * Добавляет проверку уровня профессии
     *
     * @param classLevels - список уровней классов, для которых доступен квест
     */
    public void addClassLevelCheck(int classLevels) {
        if (startConditionList == null)
            startConditionList = new ArrayList<ICheckStartCondition>();

        startConditionList.add(new ClassLevelCondition(classLevels));
    }

    /**
     * Добавляет проверку модели профессии
     *
     * @param classId - список уровней классов, для которых доступен квест
     */
    public void addClassIdCheck(ClassId... classId) {
        if (startConditionList == null)
            startConditionList = new ArrayList<ICheckStartCondition>();

        startConditionList.add(new ClassIdCondition(classId));
    }

    /**
     * Возвращает название квеста (Берется с npcstring-*.dat)
     * state 1 = ""
     * state 2 = "In Progress"
     * state 3 = "Done"
     */
    public String getDescr(final Player player) {
        if (!isVisible()) {
            return null;
        }

        final QuestState qs = player.getQuestState(this);
        int state = 2;
        if (qs == null || qs.isCreated() && qs.isNowAvailable()) {
            state = 1;
        } else if (qs.isCompleted() || !qs.isNowAvailable()) {
            state = 3;
        }

        int fStringId = getId();
        if (fStringId >= 10000) {
            fStringId -= 5000;
        }
        fStringId = fStringId * 100 + state;
        return HtmlUtils.htmlNpcString(fStringId);
    }

    /**
     * Return name of the quest
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Return ID of the quest
     *
     * @return int
     */
    public int getId() {
        return questId;
    }

    /**
     * Return party state of quest
     *
     * @return String
     */
    public int getParty() {
        return party;
    }

    public QuestState newQuestState(final Player player, final int state) {
        final QuestState qs = new QuestState(this, player, state);
        CharacterQuestDAO.getInstance().replace(qs);
        return qs;
    }

    public QuestState newQuestStateAndNotSave(final Player player, final int state) {
        return new QuestState(this, player, state);
    }

    public void notifyAttack(final NpcInstance npc, final QuestState qs) {
        String res;
        try {
            res = onAttack(npc, qs);
        } catch (Exception e) {
            showError(qs.getPlayer(), e);
            return;
        }
        showResult(npc, qs.getPlayer(), res);
    }

    void notifyDeath(final Creature killer, final Creature victim, final QuestState qs) {
        String res;
        try {
            res = onDeath(killer, victim, qs);
        } catch (Exception e) {
            showError(qs.getPlayer(), e);
            return;
        }
        showResult(null, qs.getPlayer(), res);
    }

    public void notifyEvent(final String event, final QuestState qs, final NpcInstance npc) {
        String res;
        try {
            res = onEvent(event, qs, npc);
        } catch (Exception e) {
            showError(qs.getPlayer(), e);
            return;
        }
        showResult(npc, qs.getPlayer(), res);
    }

    public void notifyKill(final NpcInstance npc, final QuestState qs) {
        String res;
        try {
            res = onKill(npc, qs);
        } catch (Exception e) {
            showError(qs.getPlayer(), e);
            return;
        }
        showResult(npc, qs.getPlayer(), res);
    }

    void notifyKill(final Player target, final QuestState qs) {
        String res = null;
        try {
            res = onKill(target, qs);
        } catch (Exception e) {
            showError(qs.getPlayer(), e);
            return;
        }
        showResult(null, qs.getPlayer(), res);
    }

    /**
     * Override the default NPC dialogs when a quest defines this for the given NPC
     */
    public final boolean notifyFirstTalk(final NpcInstance npc, final Player player) {
        String res = null;
        try {
            res = onFirstTalk(npc, player);
        } catch (Exception e) {
            showError(player, e);
            return true;
        }
        // if the quest returns text to display, display it. Otherwise, use the default npc text.
        return showResult(npc, player, res, true);
    }

    public boolean notifyTalk(final NpcInstance npc, final QuestState qs) {
        String res;
        try {
            res = onTalk(npc, qs);
        } catch (Exception e) {
            showError(qs.getPlayer(), e);
            return true;
        }
        return showResult(npc, qs.getPlayer(), res);
    }

    public void notifySkillUse(final NpcInstance npc, final Skill skill, final QuestState qs) {
        String res;
        try {
            res = onSkillUse(npc, skill, qs);
        } catch (Exception e) {
            showError(qs.getPlayer(), e);
            return;
        }
        showResult(npc, qs.getPlayer(), res);
    }

    void notifyCreate(final QuestState qs) {
        try {
            onCreate(qs);
        } catch (Exception e) {
            showError(qs.getPlayer(), e);
        }
    }

    public void onCreate(final QuestState qs) {
    }

    public String onAttack(final NpcInstance npc, final QuestState qs) {
        return null;
    }

    public String onDeath(final Creature killer, final Creature victim, final QuestState qs) {
        return null;
    }

    public String onEvent(final String event, final QuestState qs, final NpcInstance npc) {
        return null;
    }

    public String onKill(final NpcInstance npc, final QuestState qs) {
        return null;
    }

    public String onKill(final Player killed, final QuestState st) {
        return null;
    }

    public String onFirstTalk(final NpcInstance npc, final Player player) {
        return null;
    }

    public String onTalk(final NpcInstance npc, final QuestState qs) {
        return null;
    }

    private String onSkillUse(final NpcInstance npc, final Skill skill, final QuestState qs) {
        return null;
    }

    public void onOlympiadEnd(final OlympiadGame og, final QuestState qs) {
    }

    public void onAbort(final QuestState qs) {
    }

    public boolean canAbortByPacket() {
        return true;
    }

    /**
     * Show message error to player who has an access level greater than 0
     *
     * @param player : L2Player
     * @param t      : Throwable
     */
    private void showError(final Player player, final Throwable t) {
        LOGGER.error("", t);
        if (player != null && player.isGM()) {
            final String res = "<html><body><title>Script error</title>" + LogUtils.dumpStack(t).replace("\n", "<br>") + "</body></html>";
            showResult(null, player, res);
        }
    }

    private void showHtmlFile(final Player player, final String fileName, final boolean showQuestInfo) {
        showHtmlFile(player, fileName, showQuestInfo, ArrayUtils.EMPTY_OBJECT_ARRAY);
    }

    protected void showHtmlFile(final Player player, final String fileName, final boolean showQuestInfo, final Object... arg) {
        if (player == null) {
            return;
        }

        final GameObject target = player.getTarget();
        final HtmlMessage npcReply = new HtmlMessage(target == null ? 5 : target.getObjectId());
        if (showQuestInfo) {
            npcReply.setQuestId(getId());
        }
        npcReply.setFile("quests/" + getClass().getSimpleName() + '/' + fileName);

        if (arg.length % 2 == 0) {
            for (int i = 0; i < arg.length; i += 2) {
                npcReply.replace(String.valueOf(arg[i]), String.valueOf(arg[i + 1]));
            }
        }

        player.sendPacket(npcReply);
    }

    private void showSimpleHtmFile(final Player player, final String fileName) {
        if (player == null) {
            return;
        }

        final HtmlMessage npcReply = new HtmlMessage(5);
        npcReply.setFile(fileName);
        player.sendPacket(npcReply);
    }

    /**
     * Show a message to player.<BR><BR>
     * <U><I>Concept : </I></U><BR>
     * 3 cases are managed according to the value of the parameter "res" :<BR>
     * <LI><U>"res" ends with string ".html" :</U> an HTML is opened in order to be shown in a dialog box</LI>
     * <LI><U>"res" starts with tag "html" :</U> the message hold in "res" is shown in a dialog box</LI>
     * <LI><U>"res" is null :</U> do not show any message</LI>
     * <LI><U>"res" is empty string :</U> show default message</LI>
     * <LI><U>otherwise :</U> the message hold in "res" is shown in chat box</LI>
     *
     * @param res    : String pointing out the message to show at the player
     */
    private boolean showResult(final NpcInstance npc, final Player player, final String res) {
        return showResult(npc, player, res, false);
    }

    private boolean showResult(final NpcInstance npc, final Player player, final String res, final boolean isFirstTalk) {
        boolean showQuestInfo = showQuestInfo(player);
        if (isFirstTalk) {
            showQuestInfo = false;
        }
        if (res == null) // do not show message
        {
            return true;
        }
        if (res.isEmpty()) // show default npc message
        {
            return false;
        }
        if (res.startsWith("no_quest") || "noquest".equalsIgnoreCase(res) || res.equalsIgnoreCase(NO_QUEST_DIALOG)) {
            showSimpleHtmFile(player, "no-quest.htm");
        } else if ("completed".equalsIgnoreCase(res)) {
            showSimpleHtmFile(player, "pts/finishedquest.htm");
        } else if (res.endsWith(".htm")) {
            showHtmlFile(player, res, showQuestInfo);
        } else {
            final HtmlMessage npcReply = new HtmlMessage(npc == null ? 5 : npc.getObjectId());
            npcReply.setHtml(res);
            if (showQuestInfo) {
                npcReply.setQuestId(getId());
            }
            player.sendPacket(npcReply);
        }
        return true;
    }

    // =========================================================
    //  QUEST SPAWNS
    // =========================================================

    // Проверяем, показывать ли информацию о квесте в диалоге.
    private boolean showQuestInfo(final Player player) {
        final QuestState qs = player.getQuestState(this);
        if (qs != null && qs.getState() != CREATED) {
            return false;
        }
        return isVisible();
    }

    // Останавливаем и сохраняем таймеры (при выходе из игры)
    void pauseQuestTimers(final QuestState qs) {
        if (qs.getTimers().isEmpty()) {
            return;
        }

        for (final QuestTimer timer : qs.getTimers().values()) {
            timer.setQuestState(null);
            timer.pause();
        }

        pausedQuestTimers.put(qs.getPlayer().getObjectId(), qs.getTimers());
    }

    // Восстанавливаем таймеры (при входе в игру)
    void resumeQuestTimers(final QuestState qs) {
        final Map<String, QuestTimer> timers = pausedQuestTimers.remove(qs.getPlayer().getObjectId());
        if (timers == null) {
            return;
        }

        qs.getTimers().putAll(timers);

        for (final QuestTimer timer : qs.getTimers().values()) {
            timer.setQuestState(qs);
            timer.start();
        }
    }

    protected String str(final long i) {
        return String.valueOf(i);
    }

    public NpcInstance addSpawn(final int npcId, final int x, final int y, final int z, final int heading, final int randomOffset, final int despawnDelay) {
        return addSpawn(npcId, new Location(x, y, z, heading), randomOffset, despawnDelay);
    }

    public NpcInstance addSpawn(final int npcId, final Location loc, final int randomOffset, final int despawnDelay) {
        final NpcInstance result = NpcUtils.spawnSingle(npcId, randomOffset > 50 ? Location.findPointToStay(loc, 0, randomOffset, ReflectionManager.DEFAULT.getGeoIndex()) : loc);
        if (despawnDelay > 0 && result != null) {
            ThreadPoolManager.getInstance().schedule(new DeSpawnScheduleTimerTask(result), despawnDelay);
        }
        return result;
    }

    /**
     * Этот метод для регистрации квестовых вещей, которые будут удалены
     * при прекращении квеста, независимо от того, был он закончен или
     * прерван. <strong>Добавлять сюда награды нельзя</strong>.
     */
    public void addQuestItem(final int... ids) {
        for (final int id : ids) {
            if (id != 0) {
                ItemTemplate i = ItemTemplateHolder.getInstance().getTemplate(id);

                if (questItems.contains(id)) {
                    LOGGER.warn("Item " + i + " multiple times in quest drop in " + getName());
                }

                questItems.add(id);
            }
        }
    }

    public int[] getItems() {
        return questItems.toArray();
    }

    public boolean isQuestItem(final int id) {
        return questItems.contains(id);
    }

    public boolean isVisible() {
        return true;
    }

    public boolean isUnderLimit() {
        return false;
    }

    /**
     * Метод для проверки квеста на доступность данному персонажу. Используется при показе списка квестов, Доступные квесты выделяются синим,
     * недоступные - красным Также используется для показа соответствующих диалогов
     *
     * @param player - игрок, для которого проверяем условия квеста
     * @return NONE, если игрок соответствует требованиям квеста, иначе значение с кондишена
     */
    public final ConditionList isAvailableFor(final Player player) {
        for (final ICheckStartCondition startCondition : startConditionList)
            if (startCondition.checkCondition(player) != ConditionList.NONE)
                return startCondition.checkCondition(player);
        return ConditionList.NONE;
    }

    @Override
    public void onInit() {
        if (!ServerConfig.DONTLOADQUEST)
            QuestManager.addQuest(this);
    }

    public static class DeSpawnScheduleTimerTask extends RunnableImpl {
        final NpcInstance _npc;

        DeSpawnScheduleTimerTask(final NpcInstance npc) {
            _npc = npc;
        }

        @Override
        public void runImpl() {
            if (_npc != null) {
                if (_npc.getSpawn() != null) {
                    _npc.getSpawn().deleteAll();
                } else {
                    _npc.deleteMe();
                }
            }
        }
    }
}