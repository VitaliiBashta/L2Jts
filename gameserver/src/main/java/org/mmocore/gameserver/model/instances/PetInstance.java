package org.mmocore.gameserver.model.instances;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jts.dataparser.data.holder.PetDataHolder;
import org.jts.dataparser.data.holder.petdata.PetData;
import org.jts.dataparser.data.holder.petdata.PetUtils;
import org.jts.dataparser.data.holder.petdata.PetUtils.PetId;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.database.dao.impl.PetDAO;
import org.mmocore.gameserver.database.dao.impl.PetEffectDAO;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExChangeNpcState;
import org.mmocore.gameserver.network.lineage.serverpackets.InventoryUpdate;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;
import org.mmocore.gameserver.object.components.items.attachments.FlagItemAttachment;
import org.mmocore.gameserver.object.components.items.warehouse.PetInventory;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.stats.Stats;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.PlayerUtils;

import java.util.concurrent.Future;
import java.util.stream.IntStream;

//TODO: Переписать этот бред.
public class PetInstance extends Servitor {
    protected final PetInventory _inventory;
    private final int _controlItemObjId;
    private int _curFed;
    private PetData _data;
    private Future<?> _feedTask;
    private int _level;
    private int lostExp;
    private int npcState;
    private boolean _existsInDatabase;
    public PetInstance(final int objectId, final NpcTemplate template, final Player owner, final ItemInstance control) {
        this(objectId, template, owner, control, 0);
    }

    public PetInstance(final int objectId, final NpcTemplate template, final Player owner, final ItemInstance control, final long exp) {
        super(objectId, template, owner);
        _controlItemObjId = control.getObjectId();
        _exp = exp;
        _level = control.getEnchantLevel();
        _data = PetDataHolder.getInstance().getPetData(template.npcId);
        final int minLevel = PetUtils.getMinLevel(template.npcId);
        if (_level < minLevel) {
            _level = minLevel;
        }
        _inventory = new PetInventory(this);
    }

    public static PetInstance restore(final ItemInstance control, final NpcTemplate template, final Player owner) {
        return PetDAO.getInstance().select(owner, control, template);
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        final int old_level = _level;
        if (getControlItem().getEnchantLevel() == 0) {
            //Нужен квест аи
            if (getTemplate().npcId == PetId.SIN_EATER_ID) {
                _level = getPlayer().getLevel();
            }
            addExpAndSp(getExpForThisLevel(), 0);
            updateControlItem();
        } else if (getData().isSyncLevel()) {
            if (getLevel() != getPlayer().getLevel()) {
                setLevel(getPlayer().getLevel());
                setExp(getExpForNextLevel());
                updateControlItem();
                if (getLevel() > old_level) {
                    broadcastPacket(new SocialAction(getObjectId(), SocialAction.LEVEL_UP));
                    setCurrentHpMp(getMaxHp(), getMaxMp());
                }
            }
        }
        startFeed();
        if (isVeryHungryPet()) {
            setNpcState(100);
        } else {
            setNpcState(101);
        }
    }

    @Override
    protected void onDespawn() {
        super.onSpawn();
        stopFeed();
    }

    @Override
    public void addExpAndSp(final long addToExp, final long addToSp) {
        final Player owner = getPlayer();
        final int old_level = _level;
        if (getData().isSyncLevel()) {
            if (getLevel() != owner.getLevel()) {
                setLevel(owner.getLevel());
                setExp(getExpForNextLevel());
                updateControlItem();
                if (getLevel() > old_level) {
                    broadcastPacket(new SocialAction(getObjectId(), SocialAction.LEVEL_UP));
                    setCurrentHpMp(getMaxHp(), getMaxMp());
                }
            }
            return;
        }

        _exp += addToExp;
        _sp += addToSp;

        if (_exp > getMaxExp()) {
            _exp = getMaxExp();
        }

        if (addToExp > 0 || addToSp > 0) {
            owner.sendPacket(new SystemMessage(SystemMsg.YOUR_PET_GAINED_S1_EXPERIENCE_POINTS).addNumber(addToExp));
        }

        while (_exp >= getExpForNextLevel() && _level < PlayerUtils.getPetMaxLevel()) {
            _level++;
        }

        while (_exp < getExpForThisLevel() && _level > getMinLevel()) {
            _level--;
        }

        if (old_level < _level) {
            broadcastPacket(new SocialAction(getObjectId(), SocialAction.LEVEL_UP));
            setCurrentHpMp(getMaxHp(), getMaxMp());
        }

        if (old_level != _level) {
            updateControlItem();
            updateData();
        }

        if (addToExp > 0 || addToSp > 0) {
            sendStatusUpdate();
        }
    }

    @Override
    public boolean consumeItem(final int itemConsumeId, final long itemCount) {
        return getInventory().destroyItemByItemId(itemConsumeId, itemCount);
    }

    private void deathPenalty() {
        if (isInZoneBattle()) {
            return;
        }
        final int lvl = getLevel();
        final double percentLost = -0.07 * lvl + 6.5;
        // Calculate the Experience loss
        lostExp = (int) Math.round((getExpForNextLevel() - getExpForThisLevel()) * percentLost / 100);
        addExpAndSp(-lostExp, 0);
    }

    /**
     * Remove the Pet from DB and its associated item from the player inventory
     */
    private void destroyControlItem() {
        final Player owner = getPlayer();
        if (getControlItemObjId() == 0) {
            return;
        }
        if (!owner.getInventory().destroyItemByObjectId(getControlItemObjId(), 1L)) {
            return;
        }

        PetDAO.getInstance().delete(getControlItemObjId());
    }

    @Override
    protected void onDeath(final Creature killer) {
        super.onDeath(killer);

        final Player owner = getPlayer();

        owner.sendPacket(SystemMsg.THE_PET_HAS_BEEN_KILLED);
        startDecay(getTemplate().getCorpseTime());
        if (getData().isSyncLevel()) {
            return;
        }

        stopFeed();
        deathPenalty();
    }

    @Override
    public void doPickupItem(final GameObject object) {
        final Player owner = getPlayer();

        stopMove();

        if (!object.isItem()) {
            return;
        }

        final ItemInstance item = (ItemInstance) object;

        if (item.isCursed()) {
            owner.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_FAILED_TO_PICK_UP_S1).addItemName(item.getItemId()));
            return;
        }

        synchronized (item) {
            if (!item.isVisible()) {
                return;
            }

            if (item.isHerb()) {
                final SkillEntry[] skills = item.getTemplate().getAttachedSkills();
                if (skills.length > 0) {
                    for (final SkillEntry skill : skills) {
                        altUseSkill(skill, this);
                    }
                }
                item.deleteMe();
                return;
            }

            if (!getInventory().validateWeight(item)) {
                sendPacket(SystemMsg.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS_);
                return;
            }

            if (!getInventory().validateCapacity(item)) {
                sendPacket(SystemMsg.YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS);
                return;
            }

            if (!item.getTemplate().getHandler().pickupItem(this, item)) {
                return;
            }

            final FlagItemAttachment attachment = item.getAttachment() instanceof FlagItemAttachment ? (FlagItemAttachment) item.getAttachment() : null;
            if (attachment != null) {
                return;
            }

            item.pickupMe();
        }

        if (owner.getParty() == null || owner.getParty().getLootDistribution() == Party.ITEM_LOOTER) {
            getInventory().addItem(item);
            sendChanges();
        } else {
            owner.getParty().distributeItem(owner, item, null);
        }
        broadcastPickUpMsg(item);
        moveToOwner();
    }

    public void doRevive(final double percent) {
        restoreExp(percent);
        doRevive();
    }

    @Override
    public void doRevive() {
        stopDecay();
        super.doRevive();
        startFeed();
        setRunning();
    }

    @Override
    public ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getActiveWeaponItem() {
        return null;
    }

    public ItemInstance getControlItem() {
        final Player owner = getPlayer();
        if (owner == null) {
            return null;
        }
        final int item_obj_id = getControlItemObjId();
        if (item_obj_id == 0) {
            return null;
        }
        return owner.getInventory().getItemByObjectId(item_obj_id);
    }

    @Override
    public int getControlItemObjId() {
        return _controlItemObjId;
    }

    @Override
    public int getCurrentFed() {
        return _curFed;
    }

    public void setCurrentFed(final int num) {
        _curFed = Math.min(getMaxFed(), Math.max(0, num));
    }

    @Override
    public int getCriticalHit(final Creature target, final SkillEntry skill) {
        return (int) calcStat(Stats.CRITICAL_BASE, getTemplate().getBaseCritRate(), target, skill);
    }

    @Override
    public int getEvasionRate(final Creature target) {
        return (int) calcStat(Stats.EVASION_RATE, 0, target, null);
    }

    @Override
    public int getAccuracy() {
        return (int) calcStat(Stats.ACCURACY_COMBAT, 0, null, null);
    }

    @Override
    public long getExpForNextLevel() {
        return getData().getLevelStatForLevel(getLevel() + 1).getExp();
    }

    @Override
    public long getExpForThisLevel() {
        return getData().getLevelStatForLevel(getLevel()).getExp();
    }

    public int[] getFoodId() {
        return _data.getLevelStatForLevel(getLevel()).getFood();
    }

    public PetData getData() {
        return _data;
    }

    @Override
    public PetInventory getInventory() {
        return _inventory;
    }

    @Override
    public long getWearedMask() {
        return _inventory.getWearedMask();
    }

    @Override
    public final int getLevel() {
        return _level;
    }

    public void setLevel(final int level) {
        _level = level;
    }

    @Override
    public double getLevelMod() {
        return (89. + getLevel()) / 100.0;
    }

    public long getMaxExp() {
        return _data.getLevelStatForLevel(PlayerUtils.getPetMaxLevel() + 1).getExp();
    }

    @Override
    public int getMaxFed() {
        return _data.getLevelStatForLevel(getLevel()).getMaxMeal();
    }

    @Override
    public int getInventoryLimit() {
        return AllSettingsConfig.ALT_PET_INVENTORY_LIMIT;
    }

    @Override
    public double getMaxHp() {
        return (int) calcStat(Stats.MAX_HP, _data.getLevelStatForLevel(getLevel()).getMaxHp(), null, null);
    }

    @Override
    public int getMaxMp() {
        return (int) calcStat(Stats.MAX_MP, _data.getLevelStatForLevel(getLevel()).getMaxMp(), null, null);
    }

    @Override
    public int getPAtk(final Creature target) {
        return (int) calcStat(Stats.POWER_ATTACK, _data.getLevelStatForLevel(getLevel()).getPAtk(), target, null);
    }

    @Override
    public int getPDef(final Creature target) {
        return (int) calcStat(Stats.POWER_DEFENCE, _data.getLevelStatForLevel(getLevel()).getPDef(), target, null);
    }

    @Override
    public int getMAtk(final Creature target, final SkillEntry skill) {
        return (int) calcStat(Stats.MAGIC_ATTACK, _data.getLevelStatForLevel(getLevel()).getMAtk(), target, skill);
    }

    @Override
    public int getMDef(final Creature target, final SkillEntry skill) {
        return (int) calcStat(Stats.MAGIC_DEFENCE, _data.getLevelStatForLevel(getLevel()).getMDef(), target, skill);
    }

    @Override
    public int getPAtkSpd() {
        final int init = (int) calcStat(Stats.ATK_BASE, getTemplate().getBasePAtkSpd(), null, null);
        return (int) calcStat(Stats.POWER_ATTACK_SPEED, init, null, null);
    }

    @Override
    public int getMAtkSpd() {
        return (int) calcStat(Stats.MAGIC_ATTACK_SPEED, getTemplate().getBaseMAtkSpd(), null, null);
    }

    @Override
    public int getSoulshotConsumeCount() {
        return getData().getLevelStatForLevel(getLevel()).getSoulShotCount();
    }

    @Override
    public int getSpiritshotConsumeCount() {
        return getData().getLevelStatForLevel(getLevel()).getSpiritShotCount();
    }

    @Override
    public ItemInstance getSecondaryWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getSecondaryWeaponItem() {
        return null;
    }

    @Override
    public int getServitorType() {
        return PET_TYPE;
    }

    @Override
    public NpcTemplate getTemplate() {
        return (NpcTemplate) _template;
    }

    @Override
    public boolean isMountable() {
        return PetUtils.isMountable(getNpcId());
    }

    public void restoreExp(final double percent) {
        if (lostExp != 0) {
            addExpAndSp((long) (lostExp * percent / 100.), 0);
            lostExp = 0;
        }
    }

    @Override
    public void setSp(final int sp) {
        _sp = sp;
    }

    public void startFeed() {
        final boolean first = _feedTask == null;
        stopFeed();
        if (!isDead()) {
            final int feedTime;
            if (getData().isSyncLevel()) {
                feedTime = 10000;
            } else {
                feedTime = first ? 15000 : 10000;
            }
            _feedTask = ThreadPoolManager.getInstance().schedule(new FeedTask(), feedTime);
        }
    }

    private void stopFeed() {
        if (_feedTask != null) {
            _feedTask.cancel(false);
            _feedTask = null;
        }
    }

    public void destroy(final boolean deleteMe) {
        getInventory().store();
        destroyControlItem();
        stopFeed();
        if (deleteMe)
            deleteMe();
    }

    @Override
    protected void onDecay() {
        destroy(false);
        super.onDecay();
    }

    @Override
    public void unSummon(final boolean saveEffects, final boolean store) {
        stopFeed();

        getInventory().store();

        if (getControlItemObjId() == 0) {
            return;
        }

        if (saveEffects) {
            saveEffects();
        }

        if (isExistsInDatabase()) {
            PetDAO.getInstance().update(this);
        } else {
            PetDAO.getInstance().insert(this);
        }

        deleteMe();
    }

    public void updateControlItem() {
        final ItemInstance controlItem = getControlItem();
        if (controlItem == null) {
            return;
        }
        controlItem.setEnchantLevel(_level);
        controlItem.setCustomType2(isDefaultName() ? 0 : 1);
        controlItem.setJdbcState(JdbcEntityState.UPDATED);
        controlItem.update();
        final Player owner = getPlayer();
        owner.sendPacket(new InventoryUpdate().addModifiedItem(controlItem));
    }

    private void updateData() {
        _data = PetDataHolder.getInstance().getPetData(getNpcId());
    }

    public int getMinLevel() {
        return PetUtils.getMinLevel(getNpcId());
    }

    @Override
    public double getExpPenalty() {
        final double expP = 100 - getData().getLevelStatForLevel(getLevel()).getGetExpType();
        return expP / 100;
    }

    @Override
    public void displayGiveDamageMessage(final Creature target, final int damage, final boolean crit, final boolean miss, final boolean shld, final boolean magic) {
        if (getPlayer() != null) {
            if (crit) {
                getPlayer().sendPacket(SystemMsg.SUMMONED_MONSTERS_CRITICAL_HIT);
            }
            if (miss) {
                getPlayer().sendPacket(new SystemMessage(SystemMsg.C1S_ATTACK_WENT_ASTRAY).addName(this));
            }
        }
    }

    @Override
    public void displayReceiveDamageMessage(final Creature attacker, final int damage, final int toPet, final int reflected) {
        super.displayReceiveDamageMessage(attacker, damage, toPet, reflected);

        if (attacker != this && !isDead() && getPlayer() != null) {
            getPlayer().sendPacket(new SystemMessage(SystemMsg.YOUR_PET_RECEIVED_S2_DAMAGE_BY_C1).addName(attacker).addNumber(damage));
            if (reflected > 0) {
                getPlayer().sendPacket(new SystemMessage(SystemMsg.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2).addName(attacker).addName(this).addNumber(reflected));
            }
        }
    }

    @Override
    public int getFormId() {
        final int[] form = {0};
        switch (getNpcId()) {
            case PetId.GREAT_WOLF_ID:
            case PetId.WGREAT_WOLF_ID:
            case PetId.FENRIR_WOLF_ID:
            case PetId.WFENRIR_WOLF_ID:
                IntStream.rangeClosed(0, getData().getPetEvolve().length).forEach(i ->
                        {
                            if (getLevel() >= 60) {
                                form[0] = 1;
                            } else if (getLevel() >= 65) {
                                form[0] = 2;
                            } else if (getLevel() >= 70) {
                                form[0] = 3;
                            }
                        }
                );
                break;
        }
        return form[0];
    }

    @Override
    public boolean isPet() {
        return true;
    }

    public boolean isDefaultName() {
        return StringUtils.isEmpty(_name) || getName().equalsIgnoreCase(getTemplate().name);
    }

    public boolean isVeryHungryPet() {
        return getCurrentFed() <= 0.01 * getMaxFed();
    }

    public boolean isHungryPet() {
        final int hungryLimit = getData().getLevelStatForLevel(getLevel()).getHungryLimit();
        return getCurrentFed() * 100 / getMaxFed() <= hungryLimit;
    }

    public int getNpcState() {
        return npcState;
    }

    public void setNpcState(final int state) {
        npcState = state;
        getPlayer().sendPacket(new ExChangeNpcState(getObjectId(), state));
    }

    @Override
    public void saveEffects() {
        final Player owner = getPlayer();
        if (owner == null) {
            return;
        }

        if (owner.isInOlympiadMode()) {
            getEffectList().stopAllEffects();  //FIXME [VISTALL] нужно ли
        } else {
            PetEffectDAO.getInstance().insert(this);
        }
    }

    public boolean isExistsInDatabase() {
        return _existsInDatabase;
    }

    public void setExistsInDatabase(final boolean existsInDatabase) {
        _existsInDatabase = existsInDatabase;
    }

    class FeedTask extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            final Player owner = getPlayer();
            if (!isMovementDisabled() && isHungryPet()) {
                for (final ItemInstance food : getInventory().getItems()) {
                    if (!ArrayUtils.contains(getFoodId(), food.getItemId())) {
                        continue;
                    }

                    if (food.getLocation() != ItemLocation.PET_INVENTORY) {
                        continue;
                    }

                    if (!food.getTemplate().testCondition(PetInstance.this, food)) {
                        continue;
                    }

                    if (food.getTemplate().getHandler().useItem(PetInstance.this, food, false)) {
                        getPlayer().sendPacket(new SystemMessage(SystemMsg.YOUR_PET_WAS_HUNGRY_SO_IT_ATE_S1).addItemName(food.getItemId()));
                        break;
                    }
                }
            }

            if (getData().isSyncLevel() && getCurrentFed() <= 0) {
                owner.sendPacket(SystemMsg.THE_HUNTING_HELPER_PET_IS_NOW_LEAVING);
                destroy(true);
                return;
            } else if (getCurrentFed() <= 0.10 * getMaxFed() && getCurrentFed() > 0.01 * getMaxFed()) {
                if (getData().isSyncLevel()) {
                    owner.sendPacket(SystemMsg.THERE_IS_NOT_MUCH_TIME_REMAINING_UNTIL_THE_HUNTING_HELPER_PET_LEAVES);
                } else {
                    owner.sendPacket(SystemMsg.WHEN_YOUR_PETS_HUNGER_GAUGE_IS_AT_0_PERCENT_YOU_CANNOT_USE_YOUR_PET);
                }
            } else if (isVeryHungryPet()) {
                if (getData().isSyncLevel()) {
                    owner.sendPacket(SystemMsg.THERE_IS_NOT_MUCH_TIME_REMAINING_UNTIL_THE_HUNTING_HELPER_PET_LEAVES);
                } else {
                    owner.sendPacket(SystemMsg.YOUR_PET_IS_STARVING_AND_WILL_NOT_OBEY_UNTIL_IT_GETS_ITS_FOOD_FEED_YOUR_PET);
                }
                if (getNpcState() == 101) {
                    setNpcState(100);
                }
            }

            if (getCurrentFed() > 0) {
                final int consumeNormal = getData().getLevelStatForLevel(getLevel()).getConsumeMealInNormal();
                final int consumeBattle = getData().getLevelStatForLevel(getLevel()).getConsumeMealInBattle();
                setCurrentFed(getCurrentFed() - (isInCombat() ? consumeBattle : consumeNormal));
            }

            sendStatusUpdate();
            broadcastCharInfo();
            sendChanges();
            startFeed();
        }
    }
}