package org.mmocore.gameserver.model.instances;

import org.mmocore.commons.geometry.Shape;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.CtrlEvent;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.ai.DoorAI;
import org.mmocore.gameserver.configuration.config.GeodataConfig;
import org.mmocore.gameserver.geoengine.GeoCollision;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.handler.onshiftaction.OnShiftActionHolder;
import org.mmocore.gameserver.listener.actor.door.OnOpenCloseListener;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.SiegeEvent;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.Moving.ValidateLocation;
import org.mmocore.gameserver.network.lineage.serverpackets.MyTargetSelected;
import org.mmocore.gameserver.network.lineage.serverpackets.StaticObject;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.DoorTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.world.World;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class DoorInstance extends Creature implements GeoCollision {
    private static final long serialVersionUID = 8916212513177171448L;
    private final Lock _openLock = new ReentrantLock();
    protected ScheduledFuture<?> _autoActionTask;
    private boolean _open = true;
    private boolean _geoOpen = true;
    private int upgradeHp;

    private byte[][] _geoAround;

    public DoorInstance(final int objectId, final DoorTemplate template) {
        super(objectId, template);

        // ai initialization
        getAI();
    }

    public boolean isUnlockable() {
        return getTemplate().isUnlockable();
    }

    @Override
    public String getName() {
        return getTemplate().getName();
    }

    @Override
    public int getLevel() {
        return 1;
    }

    public int getDoorId() {
        return getTemplate().getNpcId();
    }

    public boolean isOpen() {
        return _open;
    }

    private boolean setOpen(final boolean open) {
        if (_open == open) {
            return false;
        }
        _open = open;
        return true;
    }

    /**
     * Запланировать открытие/закрытие двери
     *
     * @param open        - открытие/закрытие
     * @param actionDelay - время до открытие/закрытие
     */
    public void scheduleAutoAction(final boolean open, final long actionDelay) {
        if (_autoActionTask != null) {
            _autoActionTask.cancel(false);
            _autoActionTask = null;
        }

        _autoActionTask = ThreadPoolManager.getInstance().schedule(new AutoOpenClose(open), actionDelay);
    }

    public int getDamage() {
        final int dmg = 6 - (int) Math.ceil(getCurrentHpRatio() * 6);
        return Math.max(0, Math.min(6, dmg));
    }

    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return isAttackable(attacker);
    }

    @Override
    public boolean isAttackable(final Creature attacker) {
        if (attacker == null || isOpen()) {
            return false;
        }

        final SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);

        switch (getDoorType()) {
            case WALL:
                if (!attacker.isSummon() || siegeEvent == null || !siegeEvent.containsSiegeSummon((SummonInstance) attacker)) {
                    return false;
                }
                break;
            case DOOR:
                final Player player = attacker.getPlayer();
                if (player == null) {
                    return false;
                }
                if (siegeEvent != null && siegeEvent.isInProgress()) {
                    if (siegeEvent.getSiegeClan(SiegeEvent.DEFENDERS, player.getClan()) != null) {
                        return false;
                    }
                    if (siegeEvent.getObjects(DominionSiegeEvent.DEFENDER_PLAYERS).contains(player.getObjectId())) {
                        return false;
                    }
                }
                break;
        }

        return !isInvul();
    }

    @Override
    public void sendChanges() {
    }

    @Override
    public ItemInstance getActiveWeaponInstance() {
        return null;
    }

    @Override
    public WeaponTemplate getActiveWeaponItem() {
        return null;
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
    public void onAction(final Player player, final boolean shift) {
        if (shift && OnShiftActionHolder.getInstance().callShiftAction(player, DoorInstance.class, this, true)) {
            return;
        }

        if (player.getTarget() != this) {
            player.setTarget(this);
            player.sendPacket(new MyTargetSelected(getObjectId(), player.getLevel()));

            if (isAutoAttackable(player)) {
                player.sendPacket(new StaticObject(this, player));
            }

            player.sendPacket(new ValidateLocation(this));
        } else {
            player.sendPacket(new MyTargetSelected(getObjectId(), 0));

            if (isAutoAttackable(player)) {
                player.getAI().Attack(this, false, shift);
                return;
            }

            if (!isInRangeZ(player, getInteractDistance(player))) {
                if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT) {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
                }
                return;
            }

            getAI().notifyEvent(CtrlEvent.EVT_DBLCLICK, player);
        }
    }

    @Override
    public DoorAI getAI() {
        if (_ai == null) {
            synchronized (this) {
                if (_ai == null)
                    _ai = getTemplate().getNewAI(this);
            }
        }

        return (DoorAI) _ai;
    }

    @Override
    public void broadcastStatusUpdate() {
        for (final Player player : World.getAroundObservers(this)) {
            player.sendPacket(new StaticObject(this, player));
        }
    }

    public boolean openMe() {
        return openMe(null, true);
    }

    public boolean openMe(final Player opener, final boolean autoClose) {
        _openLock.lock();
        try {
            if (!setOpen(true)) {
                return false;
            }

            setGeoOpen(true);
        } finally {
            _openLock.unlock();
        }

        broadcastStatusUpdate();

        if (autoClose && getTemplate().getCloseTime() > 0) {
            scheduleAutoAction(false, this.getTemplate().getCloseTime() * 1000L);
        }

        getAI().notifyEvent(CtrlEvent.EVT_OPEN, opener);

        for (final OnOpenCloseListener listener : getListeners().getListeners(OnOpenCloseListener.class))
            listener.onOpen(this);

        return true;
    }

    public boolean closeMe() {
        return closeMe(null, true);
    }

    public boolean closeMe(final Player closer, final boolean autoOpen) {
        if (isDead()) {
            return false;
        }

        _openLock.lock();
        try {
            if (!setOpen(false)) {
                return false;
            }

            setGeoOpen(false);
        } finally {
            _openLock.unlock();
        }

        broadcastStatusUpdate();

        if (autoOpen && getTemplate().getOpenTime() > 0) {
            long openDelay = getTemplate().getOpenTime() * 1000L;
            if (getTemplate().getRandomTime() > 0) {
                openDelay += Rnd.get(0, getTemplate().getRandomTime()) * 1000L;
            }

            scheduleAutoAction(true, openDelay);
        }

        getAI().notifyEvent(CtrlEvent.EVT_CLOSE, closer);

        for (final OnOpenCloseListener listener : getListeners().getListeners(OnOpenCloseListener.class))
            listener.onClose(this);

        return true;
    }

    @Override
    public String toString() {
        return "[Door " + getDoorId() + ']';
    }

    @Override
    protected void onDeath(final Creature killer) {
        _openLock.lock();
        try {
            setGeoOpen(true);
        } finally {
            _openLock.unlock();
        }

        final SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
        if (siegeEvent != null && siegeEvent.isInProgress()) {
            Log.add(this.toString(), getDoorType() + " destroyed by " + killer + ", " + siegeEvent);
        }

        super.onDeath(killer);
    }

    @Override
    protected void onRevive() {
        super.onRevive();

        _openLock.lock();
        try {
            if (!isOpen()) {
                setGeoOpen(false);
            }
        } finally {
            _openLock.unlock();
        }
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();

        setCurrentHpMp(getMaxHp(), getMaxMp(), true);

        closeMe(null, true);
    }

    @Override
    protected void onDespawn() {
        if (_autoActionTask != null) {
            _autoActionTask.cancel(false);
            _autoActionTask = null;
        }

        super.onDespawn();
    }

    public boolean isHPVisible() {
        return getTemplate().isHPVisible();
    }

    @Override
    public double getMaxHp() {
        return super.getMaxHp() + upgradeHp;
    }

    public int getUpgradeHp() {
        return upgradeHp;
    }

    public void setUpgradeHp(final int hp) {
        upgradeHp = hp;
    }

    @Override
    public int getPDef(final Creature target) {
        switch (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE)) {
            case SevenSigns.CABAL_DAWN:
                return (int) (super.getPDef(target) * 1.2);
            case SevenSigns.CABAL_DUSK:
                return (int) (super.getPDef(target) * 0.3);
            default:
                return super.getPDef(target);
        }
    }

    @Override
    public int getMDef(final Creature target, final SkillEntry skill) {
        switch (SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_STRIFE)) {
            case SevenSigns.CABAL_DAWN:
                return (int) (super.getMDef(target, skill) * 1.2);
            case SevenSigns.CABAL_DUSK:
                return (int) (super.getMDef(target, skill) * 0.3);
            default:
                return super.getMDef(target, skill);
        }
    }

    /**
     * Двери на осадах уязвимы во время осады.
     * Остальные двери не уязвимы вообще.
     *
     * @return инвульная ли дверь.
     */
    @Override
    public boolean isInvul() {
        if (!getTemplate().isHPVisible()) {
            return true;
        } else {
            final SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
            if (siegeEvent != null && siegeEvent.isInProgress()) {
                return false;
            }

            return super.isInvul();
        }
    }

    /**
     * Устанавливает значение закрытости\открытости в геодате<br>
     *
     * @param open новое значение
     */
    protected boolean setGeoOpen(final boolean open) {
        if (_geoOpen == open) {
            return false;
        }

        _geoOpen = open;

        if (GeodataConfig.ALLOW_GEODATA) {
            if (open) {
                GeoEngine.removeGeoCollision(this, getGeoIndex());
            } else {
                GeoEngine.applyGeoCollision(this, getGeoIndex());
            }
        }

        return true;
    }

    @Override
    public boolean isMovementDisabled() {
        return true;
    }

    @Override
    public boolean isActionsDisabled() {
        return true;
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public boolean isLethalImmune() {
        return true;
    }

    @Override
    public boolean isConcrete() {
        return true;
    }

    @Override
    public boolean isHealBlocked() {
        return true;
    }

    @Override
    public boolean isEffectImmune() {
        return true;
    }

    @Override
    public List<L2GameServerPacket> addPacketList(final Player forPlayer, final Creature dropper) {
        return Collections.<L2GameServerPacket>singletonList(new StaticObject(this, forPlayer));
    }

    @Override
    public boolean isDoor() {
        return true;
    }

    @Override
    public Shape getShape() {
        return getTemplate().getPolygon();
    }

    @Override
    public byte[][] getGeoAround() {
        return _geoAround;
    }

    @Override
    public void setGeoAround(final byte[][] geo) {
        _geoAround = geo;
    }

    @Override
    public DoorTemplate getTemplate() {
        return (DoorTemplate) super.getTemplate();
    }

    public DoorTemplate.DoorType getDoorType() {
        return getTemplate().getDoorType();
    }

    public int getKey() {
        return getTemplate().getKey();
    }

    private class AutoOpenClose extends RunnableImpl {
        private final boolean _open;

        public AutoOpenClose(final boolean open) {
            _open = open;
        }

        @Override
        public void runImpl() throws Exception {
            if (_open) {
                openMe(null, true);
            } else {
                closeMe(null, true);
            }
        }
    }
}