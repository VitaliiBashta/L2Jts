package org.mmocore.gameserver.model.instances;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.database.dao.impl.SummonDAO;
import org.mmocore.gameserver.database.dao.impl.SummonEffectDAO;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SetSummonRemainTime;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.concurrent.Future;

public class SummonInstance extends Servitor {
    public static final int CYCLE = 5000; // in millis
    private static final long serialVersionUID = -7187924244473768812L;
    private final int _itemConsumeIdInTime;
    private final int _itemConsumeCountInTime;
    private final int _itemConsumeDelay;
    private final int _maxLifetime;
    private final int _skillId;
    private double _expPenalty = 0;
    private volatile Future<?> _disappearTask;
    private int _consumeCountdown;
    private int _lifetimeCountdown;
    private boolean _isSiegeSummon;

    public SummonInstance(final int objectId, final NpcTemplate template, final Player owner, final int currentLifeTime, final int maxLifeTime, final int consumeid, final int consumecount,
                          final int consumedelay, final int skillId) {
        super(objectId, template, owner);
        setName(template.name);
        _lifetimeCountdown = currentLifeTime;
        _maxLifetime = maxLifeTime;
        _itemConsumeIdInTime = consumeid;
        _itemConsumeCountInTime = consumecount;
        _consumeCountdown = _itemConsumeDelay = consumedelay;
        _skillId = skillId;
        _disappearTask = ThreadPoolManager.getInstance().schedule(new Lifetime(), CYCLE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public HardReference<SummonInstance> getRef() {
        return (HardReference<SummonInstance>) super.getRef();
    }

    @Override
    public final int getLevel() {
        return getTemplate() != null ? getTemplate().level : 0;
    }

    @Override
    public int getServitorType() {
        return SUMMON_TYPE;
    }

    @Override
    public int getCurrentFed() {
        return _lifetimeCountdown;
    }

    @Override
    public int getMaxFed() {
        return _maxLifetime;
    }

    @Override
    public double getExpPenalty() {
        return _expPenalty;
    }

    public void setExpPenalty(final double expPenalty) {
        _expPenalty = expPenalty;
    }

    @Override
    protected void onDeath(final Creature killer) {
        super.onDeath(killer);

        saveEffects();

        if (_disappearTask != null) {
            _disappearTask.cancel(false);
            _disappearTask = null;
        }
    }

    public int getItemConsumeIdInTime() {
        return _itemConsumeIdInTime;
    }

    public int getItemConsumeCountInTime() {
        return _itemConsumeCountInTime;
    }

    public int getItemConsumeDelay() {
        return _itemConsumeDelay;
    }

    protected void stopDisappear() {
        if (_disappearTask != null) {
            _disappearTask.cancel(false);
            _disappearTask = null;
        }
    }

    @Override
    public void unSummon(final boolean saveEffects, final boolean store) {
        stopDisappear();
        if (store) {
            SummonDAO.getInstance().insert(getPlayer(), this);
        } else if (isSiegeSummon()) {
            final SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
            if (siegeEvent != null) //FIXME [VISTALL] тут не должно быть null? Узнать когда нулл и поправить
            {
                siegeEvent.removeSiegeSummon(getPlayer(), this);
            }
        }

        super.unSummon(saveEffects, store);
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
            getPlayer().sendPacket(new SystemMessage(SystemMsg.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2).addName(this).addName(attacker).addNumber(damage));
            if (reflected > 0) {
                getPlayer().sendPacket(new SystemMessage(SystemMsg.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2).addName(attacker).addName(this).addNumber(reflected));
            }
        }
    }

    public int getCallSkillId() {
        return _skillId;
    }

    @Override
    public boolean isSummon() {
        return true;
    }

    @Override
    public long getWearedMask() {
        return WeaponType.SWORD.mask(); // TODO: читать пассивки и смотреть тип оружия и брони там
    }

    @Override
    public int getSoulshotConsumeCount() {
        return getTemplate().getSoulShotCount();
    }

    @Override
    public int getSpiritshotConsumeCount() {
        return getTemplate().getSpiritShotCount();
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
            SummonEffectDAO.getInstance().insert(this);
        }
    }

    public boolean isSiegeSummon() {
        return _isSiegeSummon;
    }

    public void setSiegeSummon(final boolean siegeSummon) {
        _isSiegeSummon = siegeSummon;
    }

    @Override
    public double getLevelMod() {
        return (89. + getLevel()) / 100.0;
    }

    class Lifetime extends RunnableImpl {
        @Override
        public void runImpl() {
            final Player owner = getPlayer();
            if (owner == null) {
                _disappearTask = null;
                unSummon(false, false);
                return;
            }

            final int usedtime = isInCombat() ? CYCLE : CYCLE / 4;
            _lifetimeCountdown -= usedtime;

            if (_lifetimeCountdown <= 0) {
                owner.sendPacket(SystemMsg.YOUR_SERVITOR_HAS_VANISHED_YOULL_NEED_TO_SUMMON_A_NEW_ONE);
                _disappearTask = null;
                unSummon(false, false);
                return;
            }

            _consumeCountdown -= usedtime;
            if (_itemConsumeIdInTime > 0 && _itemConsumeCountInTime > 0 && _consumeCountdown <= 0) {
                if (owner.getInventory().destroyItemByItemId(getItemConsumeIdInTime(), getItemConsumeCountInTime())) {
                    _consumeCountdown = _itemConsumeDelay;
                    owner.sendPacket(new SystemMessage(SystemMsg.A_SUMMONED_MONSTER_USES_S1).addItemName(getItemConsumeIdInTime()));
                } else {
                    owner.sendPacket(SystemMsg.SINCE_YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_MAINTAIN_THE_SERVITORS_STAY_THE_SERVITOR_HAS_DISAPPEARED);
                    unSummon(false, false);
                }
            }

            owner.sendPacket(new SetSummonRemainTime(SummonInstance.this));

            _disappearTask = ThreadPoolManager.getInstance().schedule(this, CYCLE);
        }
    }
}