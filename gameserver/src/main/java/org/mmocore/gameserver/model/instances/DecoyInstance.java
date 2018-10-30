package org.mmocore.gameserver.model.instances;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.network.lineage.serverpackets.AutoAttackStart;
import org.mmocore.gameserver.network.lineage.serverpackets.CharInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.MyTargetSelected;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public class DecoyInstance extends NpcInstance {
    private final HardReference<Player> _playerRef;
    private int _lifeTime, _timeRemaining;
    private ScheduledFuture<?> _decoyLifeTask, _hateSpam;

    public DecoyInstance(final int objectId, final NpcTemplate template, final Player owner, final int lifeTime) {
        super(objectId, template);

        setUndying(false);
        _playerRef = owner.getRef();
        _lifeTime = lifeTime;
        _timeRemaining = _lifeTime;
        final int skilllevel = getNpcId() < 13257 ? getNpcId() - 13070 : getNpcId() - 13250;
        _decoyLifeTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new DecoyLifetime(), 1000, 1000);
        _hateSpam = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HateSpam(SkillTable.getInstance().getSkillEntry(5272, skilllevel)), 1000, 3000);
    }

    @Override
    protected void onDeath(final Creature killer) {
        super.onDeath(killer);
        if (_hateSpam != null) {
            _hateSpam.cancel(false);
            _hateSpam = null;
        }
        _lifeTime = 0;
    }

    public void unSummon() {
        if (_decoyLifeTask != null) {
            _decoyLifeTask.cancel(false);
            _decoyLifeTask = null;
        }
        if (_hateSpam != null) {
            _hateSpam.cancel(false);
            _hateSpam = null;
        }
        deleteMe();
    }

    public void decTimeRemaining(final int value) {
        _timeRemaining -= value;
    }

    public int getTimeRemaining() {
        return _timeRemaining;
    }

    public int getLifeTime() {
        return _lifeTime;
    }

    @Override
    public Player getPlayer() {
        return _playerRef.get();
    }

    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        final Player owner = getPlayer();
        return owner != null && owner.isAutoAttackable(attacker);
    }

    @Override
    public boolean isAttackable(final Creature attacker) {
        final Player owner = getPlayer();
        return owner != null && owner.isAttackable(attacker);
    }

    @Override
    protected void onDelete() {
        final Player owner = getPlayer();
        if (owner != null) {
            owner.setDecoy(null);
        }
        super.onDelete();
    }

    @Override
    public void onAction(final Player player, final boolean shift) {
        if (player.getTarget() != this) {
            player.setTarget(this);
            player.sendPacket(new MyTargetSelected(getObjectId(), 0));
        } else if (isAutoAttackable(player)) {
            player.getAI().Attack(this, false, shift);
        }
    }

    @Override
    public List<L2GameServerPacket> addPacketList(final Player forPlayer, final Creature dropper) {
        if (!isInCombat()) {
            return Collections.<L2GameServerPacket>singletonList(new CharInfo(this));
        } else {
            final List<L2GameServerPacket> list = new ArrayList<>(2);
            list.add(new CharInfo(this));
            list.add(new AutoAttackStart(objectId));
            return list;
        }
    }

    class DecoyLifetime extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            try {
                final double newTimeRemaining;
                decTimeRemaining(1000);
                newTimeRemaining = getTimeRemaining();
                if (newTimeRemaining < 0) {
                    unSummon();
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    class HateSpam extends RunnableImpl {
        private final SkillEntry _skill;

        HateSpam(final SkillEntry skill) {
            _skill = skill;
        }

        @Override
        public void runImpl() throws Exception {
            try {
                setTarget(DecoyInstance.this);
                doCast(_skill, DecoyInstance.this, true);
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }
}