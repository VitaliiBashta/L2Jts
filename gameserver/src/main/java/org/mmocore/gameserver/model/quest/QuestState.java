package org.mmocore.gameserver.model.quest;

import org.apache.commons.lang3.StringUtils;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.configuration.config.OtherConfig;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.database.dao.impl.CharacterQuestDAO;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.actor.OnKillListener;
import org.mmocore.gameserver.manager.SpawnManager;
import org.mmocore.gameserver.model.Spawner;
import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.serverpackets.*;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.templates.spawn.PeriodOfDay;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class QuestState {
    public static final int RESTART_HOUR = 6;
    public static final int RESTART_MINUTES = 30;
    public static final String VAR_COND = "cond";
    public static final String VAR_STATE = "<state>";
    public static final QuestState[] EMPTY_ARRAY = new QuestState[0];
    private static final Logger _log = LoggerFactory.getLogger(QuestState.class);
    private final Player _player;
    private final Quest _quest;
    private final Map<String, String> _vars = new ConcurrentHashMap<>();
    private final Map<String, QuestTimer> _timers = new ConcurrentHashMap<>();
    private int _state;
    private Integer _cond = null;
    private OnKillListener _onKillListener = null;
    /**
     * Constructor<?> of the QuestState : save the quest in the list of quests of the player.<BR/><BR/>
     * <p/>
     * <U><I>Actions :</U></I><BR/>
     * <LI>Save informations in the object QuestState created (Quest, Player, Completion, State)</LI>
     * <LI>Add the QuestState in the player's list of quests by using setQuestState()</LI>
     * <LI>Add drops gotten by the quest</LI>
     * <BR/>
     *
     * @param quest  : quest associated with the QuestState
     * @param player : L2Player pointing out the player
     * @param state  : state of the quest
     */
    public QuestState(final Quest quest, final Player player, final int state) {
        _quest = quest;
        _player = player;

        // Save the state of the quest for the player in the player's list of quest onwed
        player.addQuestState(this);

        // set the state of the quest
        _state = state;
        quest.notifyCreate(this);
    }

    /**
     * Add XP and SP as quest reward
     * <br><br>
     * Метод учитывает рейты!
     * 3-ий параметр true/false показывает является ли квест на профессию
     * и рейты учитываются в завимисомти от параметра RateQuestsRewardOccupationChange
     */
    public void addExpAndSp(final long exp, final long sp) {
        final Player player = getPlayer();
        if (player == null) {
            return;
        }
        if (exp > 0) {
            player.addExpAndSp((long) (exp * getRateQuestsReward()), 0);
        }
        if (sp > 0) {
            player.addExpAndSp(0, (long) (sp * getRateQuestsReward()));
        }
    }

    /**
     * Add player to get notification of characters death
     *
     * @param player : L2Character of the character to get notification of death
     */
    public void addNotifyOfDeath(final Player player, final boolean withPet) {
        final OnDeathListenerImpl listener = new OnDeathListenerImpl();
        player.addListener(listener);
        if (withPet) {
            final Servitor summon = player.getServitor();
            if (summon != null) {
                summon.addListener(listener);
            }
        }
    }

    public void addPlayerOnKillListener() {
        if (_onKillListener != null) {
            throw new IllegalArgumentException("Cant add twice kill listener to player");
        }

        _onKillListener = new PlayerOnKillListenerImpl();
        _player.addListener(_onKillListener);
    }

    public void removePlayerOnKillListener() {
        if (_onKillListener != null) {
            _player.removeListener(_onKillListener);
        }

        _onKillListener = null;
    }

    public void addRadar(final int x, final int y, final int z) {
        final Player player = getPlayer();
        if (player != null) {
            player.addRadar(x, y, z);
        }
    }

    public void addRadarWithMap(final int x, final int y, final int z) {
        final Player player = getPlayer();
        if (player != null) {
            player.sendPacket(new ShowMiniMap(player, 1665));
            player.addRadarWithMap(x, y, z);
        }
    }

    /**
     * Используется для однодневных квестов
     */
    public void exitQuest(final Quest quest) {
        final Player player = getPlayer();
        exitQuest(true);
        quest.newQuestState(player, Quest.DELAYED);
        final QuestState qs = player.getQuestState(quest);
        qs.setRestartTime();
    }

    /**
     * Destroy element used by quest when quest is exited
     *
     * @param repeatable
     * @return QuestState
     */
    public QuestState exitQuest(final boolean repeatable) {
        final Player player = getPlayer();
        if (player == null) {
            return this;
        }

        removePlayerOnKillListener();
        // Clean drops
        for (final int itemId : _quest.getItems()) {
            // Get [item from] / [presence of the item in] the inventory of the player
            final ItemInstance item = player.getInventory().getItemByItemId(itemId);
            if (item == null || itemId == 57) {
                continue;
            }
            final long count = item.getCount();
            // If player has the item in inventory, destroy it (if not gold)
            player.getInventory().destroyItemByItemId(itemId, count);
            player.getWarehouse().destroyItemByItemId(itemId, count);//TODO [G1ta0] analyze this
        }

        // If quest is repeatable, delete quest from list of quest of the player and from database (quest CAN be created again => repeatable)
        if (repeatable) {
            player.removeQuestState(_quest.getId());
            CharacterQuestDAO.getInstance().delete(getPlayer().getObjectId(), _quest.getId());
            _vars.clear();
        } else { // Otherwise, delete variables for quest and update database (quest CANNOT be created again => not repeatable)
            for (final String var : _vars.keySet()) {
                if (var != null) {
                    removeMemo(var);
                }
            }
            setState(Quest.COMPLETED);
        }
        player.sendPacket(new QuestList(player));
        return this;
    }

    public void abortQuest() {
        _quest.onAbort(this);
        exitQuest(true);
    }

    /**
     * <font color=red>Не использовать для получения кондов!</font><br><br>
     * <p/>
     * Return the value of the variable of quest represented by "var"
     *
     * @param var : name of the variable of quest
     * @return Object
     */
    public String get(final String var) {
        return _vars.get(var);
    }

    public Map<String, String> getVars() {
        return _vars;
    }

    /**
     * Возвращает переменную в виде целого числа.
     *
     * @param var : String designating the variable for the quest
     * @return int
     */
    public int getInt(final String var) {
        int varint = 0;
        try {
            final String val = get(var);
            if (val == null) {
                return 0;
            }
            varint = Integer.parseInt(val);
        } catch (Exception e) {
            _log.error(getPlayer().getName() + ": variable " + var + " isn't an integer: " + varint, e);
        }
        return varint;
    }

    public long getLong(final String var) {
        long varint = 0;
        try {
            final String val = get(var);
            if (val == null) {
                return 0;
            }
            varint = Long.parseLong(val);
        } catch (Exception e) {
            _log.error(getPlayer().getName() + ": variable " + var + " isn't an long: " + varint, e);
        }
        return varint;
    }

    /**
     * Return item number which is equipped in selected slot
     *
     * @return int
     */
    public int getItemEquipped(final int loc) {
        return getPlayer().getInventory().getPaperdollItemId(loc);
    }

    /**
     * @return L2Player
     */
    public Player getPlayer() {
        return _player;
    }

    /**
     * Return the quest
     *
     * @return Quest
     */
    public Quest getQuest() {
        return _quest;
    }

    public boolean checkQuestItemsCount(final int... itemIds) {
        final Player player = getPlayer();
        if (player == null) {
            return false;
        }
        for (final int itemId : itemIds) {
            if (player.getInventory().getCountOf(itemId) <= 0) {
                return false;
            }
        }
        return true;
    }

    public long getSumQuestItemsCount(final int... itemIds) {
        final Player player = getPlayer();
        if (player == null) {
            return 0;
        }
        long count = 0;
        for (final int itemId : itemIds) {
            count += player.getInventory().getCountOf(itemId);
        }
        return count;
    }

    /**
     * Return the quantity of one sort of item hold by the player
     *
     * @param itemId : ID of the item wanted to be count
     * @return int
     */
    public long ownItemCount(final int itemId) {
        final Player player = getPlayer();
        return player == null ? 0 : player.getInventory().getCountOf(itemId);
    }

    public long ownItemCount(final int... itemsIds) {
        long result = 0;
        for (final int id : itemsIds) {
            result += ownItemCount(id);
        }
        return result;
    }

    public boolean haveQuestItem(final int itemId, final int count) {
        return ownItemCount(itemId) >= count;
    }

    public boolean haveQuestItem(final int itemId) {
        return haveQuestItem(itemId, 1);
    }

    public int getState() {
        return _state == Quest.DELAYED ? Quest.CREATED : _state;
    }

    public String getStateName() {
        return Quest.getStateName(_state);
    }

    /**
     * Добавить предмет игроку с задержкой
     *
     * @param itemId
     * @param count
     */
    public void giveItemsDelay(final int itemId, final long count) {
        ThreadPoolManager.getInstance().schedule(() -> {
            if (itemId == ItemTemplate.ITEM_ID_ADENA)
                giveItems(itemId, count, true);
            else
                giveItems(itemId, count, false);
            Log.add("Added itemId: " + itemId + " count: " + count, "QuestState:giveItems(int, long, int) QuestId: " + getQuest().getId(), getPlayer());
        }, 4000L);
    }

    /**
     * Добавить предмет игроку
     * By default if item is adena rates 'll be applyed, else no
     *
     * @param itemId
     * @param count
     */
    public void giveItems(final int itemId, final long count) {
        final ItemTemplate template = ItemTemplateHolder.getInstance().getTemplate(itemId);
        if (template == null) {
            return;
        }
        giveItems(itemId, count, template.isStackable());
    }

    /**
     * Добавить предмет игроку
     *
     * @param itemId
     * @param count
     * @param rate   - учет квестовых рейтов
     */
    public void giveItems(final int itemId, long count, final boolean rate) {
        final Player player = getPlayer();
        final ItemTemplate template = ItemTemplateHolder.getInstance().getTemplate(itemId);
        if (player == null || template == null) {
            return;
        }

        if (count <= 0) {
            count = 1;
        }

        if (rate) {
            if (template.isQuest())
                count *= getRateQuestsDrop();
            else
                count *= getRateQuestsReward();
        }

        ItemFunctions.addItem(player, itemId, count, true);
        player.sendChanges();
    }

    public void giveItems(final int itemId, long count, final Element element, final int power) {
        final Player player = getPlayer();
        if (player == null) {
            return;
        }

        if (count <= 0) {
            count = 1;
        }

        // Get template of item
        final ItemTemplate template = ItemTemplateHolder.getInstance().getTemplate(itemId);
        if (template == null) {
            return;
        }

        for (int i = 0; i < count; i++) {
            final ItemInstance item = ItemFunctions.createItem(itemId);

            if (element != Element.NONE) {
                item.setAttributeElement(element, power);
            }

            // Add items to player's inventory
            player.getInventory().addItem(item);
        }

        player.sendPacket(SystemMessage.obtainItems(template.getItemId(), count, 0));
        player.sendChanges();
    }

    public void dropItem(final NpcInstance npc, final int itemId, final long count) {
        final Player player = getPlayer();
        if (player == null) {
            return;
        }

        final ItemInstance item = ItemFunctions.createItem(itemId);
        item.setCount(count);
        item.dropToTheGround(player, npc);
    }

    public void dropItemDelay(final NpcInstance npc, final int itemId, final long count) {
        ThreadPoolManager.getInstance().schedule(() -> {
            final Player player = getPlayer();
            if (player == null)
                return;

            final ItemInstance item = ItemFunctions.createItem(itemId);
            item.setCount(count);
            item.dropToTheGround(player, npc);
            Log.add("Added itemId: " + itemId + " count: " + count, "QuestState:giveItems(int, long, int) QuestId: " + getQuest().getId(), getPlayer());
        }, 4000L);
    }

    /**
     * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов.
     * <br><br>
     * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
     * нужно набить определенное количество предметов не осуществляется.
     * <br><br>
     * Ни один из передаваемых параметров не должен быть равен 0
     *
     * @param count      количество при рейтах 1х
     * @param calcChance шанс при рейтах 1х, в процентах
     * @return количество вещей для дропа, может быть 0
     */
    public int rollDrop(final int count, final double calcChance) {
        if (calcChance <= 0 || count <= 0) {
            return 0;
        }
        return rollDrop(count, count, calcChance);
    }

    /**
     * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов.
     * <br><br>
     * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
     * нужно набить определенное количество предметов не осуществляется.
     * <br><br>
     * Ни один из передаваемых параметров не должен быть равен 0
     *
     * @param min        минимальное количество при рейтах 1х
     * @param max        максимальное количество при рейтах 1х
     * @param calcChance шанс при рейтах 1х, в процентах
     * @return количество вещей для дропа, может быть 0
     */
    public int rollDrop(final int min, final int max, double calcChance) {
        if (calcChance <= 0 || min <= 0 || max <= 0) {
            return 0;
        }
        int dropmult = 1;
        calcChance *= getRateQuestsDrop();
        if (getQuest().getParty() > Quest.PARTY_NONE) {
            final Player player = getPlayer();
            if (player.getParty() != null) {
                calcChance *= AllSettingsConfig.ALT_PARTY_BONUS[player.getParty().getMemberCountInRange(player, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE) - 1];
            }
        }
        if (calcChance > 100) {
            if ((int) Math.ceil(calcChance / 100) <= calcChance / 100) {
                calcChance = Math.nextUp(calcChance);
            }
            dropmult = (int) Math.ceil(calcChance / 100);
            calcChance /= dropmult;
        }
        return Rnd.chance(calcChance) ? Rnd.get(min * dropmult, max * dropmult) : 0;
    }

    public double getRateQuestsDrop() {
        final Player player = getPlayer();
        final double Bonus = player == null ? 1. : player.getPremiumAccountComponent().getPremiumBonus().getQuestDropRate();
        return ServerConfig.RATE_QUESTS_DROP * Bonus;
    }

    public double getRateQuestsReward() {
        final Player player = getPlayer();
        final double Bonus = player == null ? 1. : player.getPremiumAccountComponent().getPremiumBonus().getQuestRewardRate();
        return ServerConfig.RATE_QUESTS_REWARD * Bonus;
    }

    /**
     * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов и дает их,
     * проверяет максимум, а так же проигрывает звук получения вещи.
     * <br><br>
     * Ни один из передаваемых параметров не должен быть равен 0
     *
     * @param itemId     id вещи
     * @param min        минимальное количество при рейтах 1х
     * @param max        максимальное количество при рейтах 1х
     * @param limit      максимум таких вещей
     * @param calcChance
     * @return true если после выполнения количество достигло лимита
     */
    public boolean rollAndGive(final int itemId, final int min, final int max, final int limit, final double calcChance) {
        if (calcChance <= 0 || min <= 0 || max <= 0 || limit <= 0 || itemId <= 0) {
            return false;
        }
        long count = rollDrop(min, max, calcChance);
        if (count > 0) {
            final long alreadyCount = ownItemCount(itemId);
            if (alreadyCount + count > limit) {
                count = limit - alreadyCount;
            }
            if (count > 0) {
                giveItems(itemId, count, false);
                if (count + alreadyCount < limit) {
                    soundEffect(Quest.SOUND_ITEMGET);
                } else {
                    soundEffect(Quest.SOUND_MIDDLE);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов и дает их,
     * а так же проигрывает звук получения вещи.
     * <br><br>
     * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
     * нужно набить определенное количество предметов не осуществляется.
     * <br><br>
     * Ни один из передаваемых параметров не должен быть равен 0
     *
     * @param itemId     id вещи
     * @param min        минимальное количество при рейтах 1х
     * @param max        максимальное количество при рейтах 1х
     * @param calcChance
     */
    public void rollAndGive(final int itemId, final int min, final int max, final double calcChance) {
        if (calcChance <= 0 || min <= 0 || max <= 0 || itemId <= 0) {
            return;
        }
        final int count = rollDrop(min, max, calcChance);
        if (count > 0) {
            giveItems(itemId, count, false);
            soundEffect(Quest.SOUND_ITEMGET);
        }
    }

    /**
     * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов и дает их,
     * а так же проигрывает звук получения вещи.
     * <br><br>
     * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
     * нужно набить определенное количество предметов не осуществляется.
     * <br><br>
     * Ни один из передаваемых параметров не должен быть равен 0
     *
     * @param itemId     id вещи
     * @param count      количество при рейтах 1х
     * @param calcChance
     */
    public boolean rollAndGive(final int itemId, final int count, final double calcChance) {
        if (calcChance <= 0 || count <= 0 || itemId <= 0) {
            return false;
        }
        final int countToDrop = rollDrop(count, calcChance);
        if (countToDrop > 0) {
            giveItems(itemId, countToDrop, false);
            soundEffect(Quest.SOUND_ITEMGET);
            return true;
        }
        return false;
    }

    /**
     * Return true if quest completed, false otherwise
     *
     * @return boolean
     */
    public boolean isCompleted() {
        return getState() == Quest.COMPLETED;
    }

    /**
     * Return true if quest started, false otherwise
     *
     * @return boolean
     */
    public boolean isStarted() {
        return getState() == Quest.STARTED;
    }

    /**
     * Return true if quest created, false otherwise
     *
     * @return boolean
     */
    public boolean isCreated() {
        return getState() == Quest.CREATED;
    }

    public void killNpcByObjectId(final int _objId) {
        final NpcInstance npc = GameObjectsStorage.getNpc(_objId);
        if (npc != null) {
            npc.doDie(null);
        } else {
            _log.warn("Attemp to kill object that is not npc in quest " + getQuest().getId());
        }
    }

    public String setMemoState(final String var, final String val) {
        return setMemoState(var, val, true);
    }

    public String setMemoState(final String var, final int intval) {
        return setMemoState(var, String.valueOf(intval), true);
    }

    /**
     * <font color=red>Использовать осторожно! Служебная функция!</font><br><br>
     * <p/>
     * Устанавливает переменную и сохраняет в базу, если установлен флаг. Если получен cond обновляет список квестов игрока (только с флагом).
     *
     * @param var   : String pointing out the name of the variable for quest
     * @param val   : String pointing out the value of the variable for quest
     * @param store : Сохраняет в базу и если var это cond обновляет список квестов игрока.
     * @return String (equal to parameter "val")
     */
    public String setMemoState(final String var, String val, final boolean store) {
        if (val == null) {
            val = StringUtils.EMPTY;
        }

        _vars.put(var, val);

        if (store) {
            CharacterQuestDAO.getInstance().replace(this, var, val);
        }

        return val;
    }

    /**
     * Return state of the quest after its initialization.<BR><BR>
     * <U><I>Actions :</I></U>
     * <LI>Remove drops from previous state</LI>
     * <LI>Set new state of the quest</LI>
     * <LI>Add drop for new state</LI>
     * <LI>Update information in database</LI>
     * <LI>Send packet QuestList to client</LI>
     *
     * @param state
     * @return object
     */
    public Object setState(final int state) {
        final Player player = getPlayer();
        if (player == null) {
            return null;
        }

        _state = state;

        if (getQuest().isVisible() && isStarted()) {
            player.sendPacket(new ExShowQuestMark(getQuest().getId()));
        }

        CharacterQuestDAO.getInstance().replace(this);
        player.sendPacket(new QuestList(player));
        return state;
    }

    public Object setStateAndNotSave(final int state) {
        final Player player = getPlayer();
        if (player == null) {
            return null;
        }

        _state = state;

        if (getQuest().isVisible() && isStarted()) {
            player.sendPacket(new ExShowQuestMark(getQuest().getId()));
        }

        player.sendPacket(new QuestList(player));
        return state;
    }

    /**
     * Send a packet in order to play sound at client terminal
     *
     * @param sound
     */
    public void soundEffect(final String sound) {
        final Player player = getPlayer();
        if (player != null) {
            player.sendPacket(new PlaySound(sound));
        }
    }

    public void playTutorialVoice(final String voice) {
        final Player player = getPlayer();
        if (player != null) {
            player.sendPacket(new PlaySound(PlaySound.Type.VOICE, voice, 0, 0, player.getLoc()));
        }
    }

    public void onTutorialClientEvent(final int number) {
        final Player player = getPlayer();
        if (player != null) {
            player.sendPacket(new TutorialEnableClientEvent(number));
        }
    }

    public void showQuestionMark(final int number) {
        final Player player = getPlayer();
        if (player != null) {
            player.sendPacket(new TutorialShowQuestionMark(number));
        }
    }

    public void showTutorialHTML(final String html) {
        final Player player = getPlayer();
        if (player == null) {
            return;
        }
        final String text = HtmCache.getInstance().getHtml("quests/_255_Tutorial/" + html, player);
        player.sendPacket(new TutorialShowHtml(text));
    }

    /**
     * Start a timer for quest.<BR><BR>
     *
     * @param name<BR> The name of the timer. Will also be the value for event of onEvent
     * @param time<BR> The milisecond value the timer will elapse
     */
    public void startQuestTimer(final String name, final long time) {
        startQuestTimer(name, time, null);
    }

    /**
     * Add a timer to the quest.<BR><BR>
     *
     * @param name: name of the timer (also passed back as "event" in notifyEvent)
     * @param time: time in ms for when to fire the timer
     * @param npc:  npc associated with this timer (can be null)
     */
    public void startQuestTimer(final String name, final long time, final NpcInstance npc) {
        final QuestTimer timer = new QuestTimer(name, time, npc);
        timer.setQuestState(this);
        final QuestTimer oldTimer = getTimers().put(name, timer);
        if (oldTimer != null) {
            oldTimer.stop();
        }
        timer.start();
    }

    public boolean isRunningQuestTimer(final String name) {
        return getTimers().get(name) != null;
    }

    public boolean cancelQuestTimer(final String name) {
        final QuestTimer timer = removeQuestTimer(name);
        if (timer != null) {
            timer.stop();
        }
        return timer != null;
    }

    QuestTimer removeQuestTimer(final String name) {
        final QuestTimer timer = getTimers().remove(name);
        if (timer != null) {
            timer.setQuestState(null);
        }
        return timer;
    }

    public void pauseQuestTimers() {
        getQuest().pauseQuestTimers(this);
    }

    public void stopQuestTimers() {
        for (final QuestTimer timer : getTimers().values()) {
            timer.setQuestState(null);
            timer.stop();
        }
        _timers.clear();
    }

    public void resumeQuestTimers() {
        getQuest().resumeQuestTimers(this);
    }

    Map<String, QuestTimer> getTimers() {
        return _timers;
    }

    /**
     * Удаляет указанные предметы из инвентаря игрока, и обновляет инвентарь
     *
     * @param itemId : id удаляемого предмета
     * @param count  : число удаляемых предметов<br>
     *               Если count передать -1, то будут удалены все указанные предметы.
     * @return Количество удаленных предметов
     */
    public long takeItems(final int itemId, long count) {
        final Player player = getPlayer();
        if (player == null) {
            return 0;
        }

        // Get object item from player's inventory list
        final ItemInstance item = player.getInventory().getItemByItemId(itemId);
        if (item == null) {
            return 0;
        }
        // Tests on count value in order not to have negative value
        if (count < 0 || count > item.getCount()) {
            count = item.getCount();
        }

        // Destroy the quantity of items wanted
        player.getInventory().destroyItemByItemId(itemId, count);
        // Send message of destruction to client
        player.sendPacket(SystemMessage.removeItems(itemId, count));

        return count;
    }

    public long takeAllItems(final int itemId) {
        return takeItems(itemId, -1);
    }

    public long takeAllItems(final int... itemsIds) {
        long result = 0;
        for (final int id : itemsIds) {
            result += takeAllItems(id);
        }
        return result;
    }

    public long takeAllItems(final Collection<Integer> itemsIds) {
        long result = 0;
        for (final int id : itemsIds) {
            result += takeAllItems(id);
        }
        return result;
    }

    /**
     * Remove the variable of quest from the list of variables for the quest.<BR><BR>
     * <U><I>Concept : </I></U>
     * Remove the variable of quest represented by "var" from the class variable FastMap "vars" and from the database.
     *
     * @param var : String designating the variable for the quest to be deleted
     * @return String pointing out the previous value associated with the variable "var"
     */
    public String removeMemo(final String var) {
        if (var == null) {
            return null;
        }
        final String old = _vars.remove(var);
        if (old != null) {
            CharacterQuestDAO.getInstance().delete(getPlayer().getObjectId(), _quest.getId(), var);
        }
        return old;
    }

    private boolean checkPartyMember(final Player member, final int state, final int maxrange, final GameObject rangefrom) {
        if (member == null) {
            return false;
        }
        if (rangefrom != null && maxrange > 0 && !member.isInRange(rangefrom, maxrange)) {
            return false;
        }
        final QuestState qs = member.getQuestState(getQuest());
        if (qs == null || qs.getState() != state) {
            return false;
        }
        return true;
    }

    public List<Player> getPartyMembers(final int state, final int maxrange, final GameObject rangefrom) {
        final List<Player> result = new ArrayList<>();
        final Party party = getPlayer().getParty();
        if (party == null) {
            if (checkPartyMember(getPlayer(), state, maxrange, rangefrom)) {
                result.add(getPlayer());
            }
            return result;
        }

        for (final Player _member : party.getPartyMembers()) {
            if (checkPartyMember(_member, state, maxrange, rangefrom)) {
                result.add(getPlayer());
            }
        }

        return result;
    }

    public Player getRandomPartyMember(final int state, final int maxrangefromplayer) {
        return getRandomPartyMember(state, maxrangefromplayer, getPlayer());
    }

    public Player getRandomPartyMember(final int state, final int maxrange, final GameObject rangefrom) {
        final List<Player> list = getPartyMembers(state, maxrange, rangefrom);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(Rnd.get(list.size()));
    }

    /**
     * Add spawn for player instance
     * Return object id of newly spawned npc
     */
    public NpcInstance addSpawn(final int npcId) {
        return addSpawn(npcId, getPlayer().getX(), getPlayer().getY(), getPlayer().getZ(), 0, 0, 0);
    }

    public NpcInstance addSpawn(final int npcId, final int despawnDelay) {
        return addSpawn(npcId, getPlayer().getX(), getPlayer().getY(), getPlayer().getZ(), 0, 0, despawnDelay);
    }

    public NpcInstance addSpawn(final int npcId, final int x, final int y, final int z) {
        return addSpawn(npcId, x, y, z, 0, 0, 0);
    }

    /**
     * Add spawn for player instance
     * Will despawn after the spawn length expires
     * Return object id of newly spawned npc
     */
    public NpcInstance addSpawn(final int npcId, final int x, final int y, final int z, final int despawnDelay) {
        return addSpawn(npcId, x, y, z, 0, 0, despawnDelay);
    }

    /**
     * Add spawn for player instance
     * Return object id of newly spawned npc
     */
    public NpcInstance addSpawn(final int npcId, final int x, final int y, final int z, final int heading, final int randomOffset, final int despawnDelay) {
        return getQuest().addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay);
    }

    public NpcInstance findTemplate(final int npcId) {
        for (final Spawner spawn : SpawnManager.getInstance().getSpawners(PeriodOfDay.NONE.name())) {
            if (spawn != null && spawn.getCurrentNpcId() == npcId) {
                return spawn.getLastSpawn();
            }
        }
        return null;
    }

    public int calculateLevelDiffForDrop(final int mobLevel, final int player) {
        if (!OtherConfig.DEEPBLUE_DROP_RULES) {
            return 0;
        }
        return Math.max(player - mobLevel - OtherConfig.DEEPBLUE_DROP_MAXDIFF, 0);
    }

    public int getCond() {
        if (_cond == null) {
            int val = getInt(VAR_COND);
            if ((val & 0x80000000) != 0) {
                val &= 0x7fffffff;
                for (int i = 1; i < 32; i++) {
                    val >>= 1;
                    if (val == 0) {
                        val = i;
                        break;
                    }
                }
            }
            _cond = val;
        }

        return _cond;
    }

    public String setCond(final int newCond) {
        return setCond(newCond, true);
    }

    public String setCond(int newCond, final boolean store) {
        if (newCond == getCond()) {
            return String.valueOf(newCond);
        }

        int oldCond = getInt(VAR_COND);
        _cond = newCond;

        if ((oldCond & 0x80000000) != 0) {
            // уже используется второй формат
            if (newCond > 2) // Если этап меньше 3 то возвращаемся к первому варианту.
            {
                oldCond &= 0x80000001 | ((1 << newCond) - 1);
                newCond = oldCond | (1 << (newCond - 1));
            }
        } else {
            // Второй вариант теперь используется всегда если этап больше 2
            if (newCond > 2) {
                newCond = 0x80000001 | (1 << (newCond - 1)) | ((1 << oldCond) - 1);
            }
        }

        final String sVal = String.valueOf(newCond);
        final String result = setMemoState(VAR_COND, sVal, false);
        if (store) {
            CharacterQuestDAO.getInstance().replace(this, VAR_COND, sVal);
        }

        final Player player = getPlayer();
        if (player != null) {
            player.sendPacket(new QuestList(player));
            if (newCond != 0 && getQuest().isVisible() && isStarted()) {
                player.sendPacket(new ExShowQuestMark(getQuest().getId()));
            }
        }
        return result;
    }

    /**
     * Устанавлевает время, когда квест будет доступен персонажу.
     * Метод используется для квестов, которые проходятся один раз в день.
     */
    public void setRestartTime() {
        final Calendar reDo = Calendar.getInstance();
        if (reDo.get(Calendar.HOUR_OF_DAY) >= RESTART_HOUR) {
            reDo.add(Calendar.DATE, 1);
        }
        reDo.set(Calendar.HOUR_OF_DAY, RESTART_HOUR);
        reDo.set(Calendar.MINUTE, RESTART_MINUTES);
        setMemoState("restartTime", String.valueOf(reDo.getTimeInMillis()));
    }

    /**
     * Проверяет, наступило ли время для выполнения квеста.
     * Метод используется для квестов, которые проходятся один раз в день.
     *
     * @return boolean
     */
    public boolean isNowAvailable() {
        final String val = get("restartTime");
        if (val == null) {
            return true;
        }

        final long restartTime = Long.parseLong(val);
        return restartTime <= System.currentTimeMillis();
    }

    public class OnDeathListenerImpl implements OnDeathListener {
        @Override
        public void onDeath(final Creature actor, final Creature killer) {
            final Player player = actor.getPlayer();
            if (player == null) {
                return;
            }

            player.removeListener(this);

            _quest.notifyDeath(killer, actor, QuestState.this);
        }
    }

    public class PlayerOnKillListenerImpl implements OnKillListener {
        @Override
        public void onKill(final Creature actor, final Creature victim) {
            if (!victim.isPlayer()) {
                return;
            }

            final Player actorPlayer = (Player) actor;
            final List<Player> players;
            switch (_quest.getParty()) {
                case Quest.PARTY_NONE:
                    players = Collections.singletonList(actorPlayer);
                    break;
                case Quest.PARTY_ALL:
                    if (actorPlayer.getParty() == null) {
                        players = Collections.singletonList(actorPlayer);
                    } else {
                        players = new ArrayList<>(actorPlayer.getParty().getMemberCount());
                        players.addAll(actorPlayer.getParty().getPartyMembers().stream().filter($member -> $member.isInRange(actorPlayer, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE)).collect(Collectors.toList()));
                    }
                    break;
                case Quest.PARTY_CC:
                    if (actorPlayer.isInParty())
                        players = Collections.singletonList(actorPlayer);
                    else if (!actorPlayer.getParty().isInCommandChannel()) {
                        players = new ArrayList<>(actorPlayer.getParty().getMemberCount());
                        players.addAll(actorPlayer.getParty().getPartyMembers().stream().filter($member -> $member.isInRange(actorPlayer, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE)).collect(Collectors.toList()));
                    } else {
                        players = new ArrayList<>(actorPlayer.getParty().getCommandChannel().getMemberCount());
                        for (Party $party : actorPlayer.getParty().getCommandChannel().getParties()) {
                            players.addAll($party.getPartyMembers().stream().filter($member -> $member.isInRange(actorPlayer, AllSettingsConfig.ALT_PARTY_DISTRIBUTION_RANGE * 2)).collect(Collectors.toList()));
                        }
                    }
                    break;
                default:
                    players = Collections.emptyList();
                    break;
            }

            for (final Player player : players) {
                final QuestState questState = player.getQuestState(_quest);
                if (questState != null && !questState.isCompleted()) {
                    _quest.notifyKill((Player) victim, questState);
                }
            }
        }

        @Override
        public boolean ignorePetOrSummon() {
            return true;
        }
    }
}