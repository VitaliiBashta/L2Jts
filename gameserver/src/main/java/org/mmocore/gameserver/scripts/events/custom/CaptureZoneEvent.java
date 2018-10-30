package org.mmocore.gameserver.scripts.events.custom;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.utils.QuartzUtils;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.ItemTemplateHolder;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.team.Party;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.*;
import org.quartz.CronExpression;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CaptureZoneEvent extends Event {
    public int itemId;
    public long itemCount;
    public int tickTime;
    private boolean isEventStarted = false;
    private CronExpression _pattern;
    private Location _spawnLoc;
    private Instant _startTime;
    private final Map<Party, Double> damageList = new HashMap<>();
    private final OnZoneEnterLeaveListener _zoneListener = new ZoneEnterLeaveListener();
    private Party _winParty = null;
    private ScheduledFuture<?> _rewardTask;
    private int _npcId;
    private Zone _eventZone;
    private String _itemName;
    private NpcInstance _npc = null;

    public CaptureZoneEvent(MultiValueSet<String> set) {
        super(set);
        try {
            _pattern = QuartzUtils.createCronExpression(set.getString("pattern"));
            _spawnLoc = Location.parseLoc(set.getString("location"));
            _npcId = set.getInteger("npc_id");
            _eventZone = ReflectionUtils.getZone(set.getString("event_zone"));
            itemId = set.getInteger("rewardItemId");
            itemCount = set.getLong("rewardItemCount");
            _itemName = ItemTemplateHolder.getInstance().getTemplate(itemId).getName();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startEvent() {
        clearZone();
        _npc = NpcUtils.spawnSingle(_npcId, _spawnLoc, ReflectionManager.DEFAULT);
        _eventZone.setType(ZoneType.battle_zone);
        _eventZone.addListener(_zoneListener);
        isEventStarted = true;
    }

    @Override
    public void stopEvent() {
        _eventZone.removeListener(_zoneListener);
        _eventZone.setType(_eventZone.getType());
        clearZone();
        _npc.deleteMe();
        if (_rewardTask != null) {
            _rewardTask.cancel(false);
        }
        isEventStarted = false;
    }

    @Override
    public void reCalcNextTime(boolean onInit) {
        clearActions();
        _startTime = Instant.ofEpochMilli(_pattern.getNextValidTimeAfter(new Date()).getTime() + 60000L);
        registerActions();
    }

    @Override
    public EventType getType() {
        return EventType.FUN_EVENT;
    }

    @Override
    protected Instant startTime() {
        return _startTime;
    }

    private void clearZone() {
        if (_eventZone != null)
            _eventZone.getInsidePlayers().forEach(org.mmocore.gameserver.object.Player::teleToCastle);
    }

    public boolean isOwner(Creature creature) {
        if (!isEventStarted) {
            return true;
        }
        Player player = null;
        if (creature.isPlayer()) {
            player = creature.getPlayer();
        } else if (creature.isSummon() && creature.getServitor() != null) {
            player = creature.getServitor().getPlayer();
        }
        return player == null || !player.isInParty() || (_winParty != null && player.getParty() == _winParty);
    }

    public void damage(double damage, Creature attacker) {
        if (!isEventStarted) {
            return;
        }
        Player player = null;
        Party party;
        if (attacker.isPlayer()) {
            player = attacker.getPlayer();
        } else if (attacker.isSummon() && attacker.getServitor().getPlayer() != null) {
            player = attacker.getServitor().getPlayer();
        }
        if (player == null || !player.isInParty()) {
            return;
        }
        party = player.getParty();
        if (damageList.get(party) == null) {
            damageList.put(party, 0.0D);
            return;
        }
        double d = damageList.get(party);
        d = d + damage;
        damageList.put(party, d);
    }

    public void calcWinner() {
        if (!isEventStarted) {
            return;
        }
        double damage = 0.0D;
        Party party = null;
        for (Party p : damageList.keySet()) {
            double tmpDamage = damageList.get(p);
            if (tmpDamage > damage && p.getMemberCount() != 0) {
                damage = tmpDamage;
                party = p;
            }
        }
        _winParty = party;
        damageList.clear();
        if (_rewardTask != null) {
            _rewardTask.cancel(false);
        }
        _rewardTask = ThreadPoolManager.getInstance().scheduleAtFixedDelay(new RewardTask(), 0L, tickTime, TimeUnit.SECONDS);
        if (_winParty != null) {
            CustomMessage cm = new CustomMessage("events.CaptureZone.PartyCapture");
            cm.addString(_winParty.getGroupLeader().getName());
            cm.addString(itemCount + " " + _itemName);
            AnnouncementUtils.announceToAll(cm);
        }
    }

    public void respawnObject() {
        _npc.deleteMe();
        _npc = NpcUtils.spawnSingle(_npcId, _spawnLoc);
    }

    @Override
    public void announceFromHolder(final String string) {
        AnnouncementUtils.announceToAll(new CustomMessage(string), ChatType.ANNOUNCEMENT);
    }

    private final class RewardTask extends RunnableImpl {
        @Override
        public void runImpl() {
            if (!isEventStarted || _winParty == null) {
                _rewardTask.cancel(false);
                return;
            }
            for (Player player : _winParty) {
                ItemFunctions.addItem(player, itemId, itemCount);
            }
        }
    }

    private final class ZoneEnterLeaveListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature actor) {
            if (actor.isPlayer() && zone == _eventZone && isEventStarted) {
                actor.getPlayer().addRadar(_npc.getX(), _npc.getY(), _npc.getZ());
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature actor) {
        }
    }
}
