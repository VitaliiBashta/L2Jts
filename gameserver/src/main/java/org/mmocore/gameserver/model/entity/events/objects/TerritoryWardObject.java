package org.mmocore.gameserver.model.entity.events.objects;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventType;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeEvent;
import org.mmocore.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.TerritoryWardInstance;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;
import org.mmocore.gameserver.object.components.items.attachments.FlagItemAttachment;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.idfactory.IdFactory;
import org.mmocore.gameserver.world.GameObjectsStorage;

import java.util.concurrent.ScheduledFuture;

/**
 * @author VISTALL
 * @date 13:24/08.04.2011
 */
public class TerritoryWardObject implements SpawnableObject, FlagItemAttachment {
    private final int _itemId;
    private final NpcTemplate _template;
    private final Location _location;
    private NpcInstance _wardNpcInstance;
    private ItemInstance _wardItemInstance;
    private ScheduledFuture<?> returnToBase;

    public TerritoryWardObject(final int itemId, final int npcId, final Location location) {
        _itemId = itemId;
        _template = NpcHolder.getInstance().getTemplate(npcId);
        _location = location;
    }

    @Override
    public void spawnObject(final Event event) {
        _wardItemInstance = ItemFunctions.createItem(_itemId);
        _wardItemInstance.setAttachment(this);
        _wardNpcInstance = new TerritoryWardInstance(IdFactory.getInstance().getNextId(), _template, this);
        _wardNpcInstance.addEvent(event);
        _wardNpcInstance.setCurrentHpMp(_wardNpcInstance.getMaxHp(), _wardNpcInstance.getMaxMp());
        _wardNpcInstance.spawnMe(_location);
    }

    @Override
    public void despawnObject(final Event event) {
        if (_wardItemInstance == null || _wardNpcInstance == null) {
            return;
        }
        final Player owner = GameObjectsStorage.getPlayer(_wardItemInstance.getOwnerId());
        if (owner != null) {
            owner.getInventory().destroyItem(_wardItemInstance);
            owner.sendDisarmMessage(_wardItemInstance);
        }
        _wardItemInstance.setAttachment(null);
        _wardItemInstance.setJdbcState(JdbcEntityState.UPDATED);
        _wardItemInstance.delete();
        _wardItemInstance.deleteMe();
        _wardItemInstance = null;
        if (returnToBase != null) {
            returnToBase.cancel(true);
        }
        _wardNpcInstance.deleteMe();
        _wardNpcInstance = null;
    }

    @Override
    public void respawnObject(Event event) {
    }

    @Override
    public void refreshObject(final Event event) {
        //
    }

    @Override
    public void onLogout(final Player player) {
        Location loc = player.getLoc();
        player.getInventory().removeItem(_wardItemInstance);
        //_wardItemInstance.setOwnerId(0);
        _wardItemInstance.setLocation(ItemLocation.VOID);
        _wardItemInstance.setJdbcState(JdbcEntityState.UPDATED);
        _wardItemInstance.update();
        _wardNpcInstance.setCurrentHpMp(_wardNpcInstance.getMaxHp(), _wardNpcInstance.getMaxMp(), true);
        _wardNpcInstance.spawnMe(loc);
        scheduleReturnToBase();
        DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
        runnerEvent.broadcastTo(new SystemMessage(SystemMsg.THE_CHARACTER_THAT_ACQUIRED_S1S_WARD_HAS_BEEN_KILLED).addResidenceName(getDominionId()));
    }

    @Override
    public void onDeath(final Player owner, final Creature killer) {
        final Location loc = owner.getLoc();
        owner.getInventory().removeItem(_wardItemInstance);
        owner.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_DROPPED_S1).addName(_wardItemInstance));
        //_wardItemInstance.setOwnerId(0);
        _wardItemInstance.setLocation(ItemLocation.VOID);
        _wardItemInstance.setJdbcState(JdbcEntityState.UPDATED);
        _wardItemInstance.update();
        _wardNpcInstance.setCurrentHpMp(_wardNpcInstance.getMaxHp(), _wardNpcInstance.getMaxMp(), true);
        _wardNpcInstance.spawnMe(loc);
        scheduleReturnToBase();
        final DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
        runnerEvent.broadcastTo(new SystemMessage(SystemMsg.THE_CHARACTER_THAT_ACQUIRED_S1S_WARD_HAS_BEEN_KILLED).addResidenceName(getDominionId()));
    }

    @Override
    public boolean canPickUp(final Player player) {
        return player.getActiveWeaponFlagAttachment() == null && !player.isMounted();
    }

    @Override
    public void pickUp(final Player player) {
        player.getInventory().addItem(_wardItemInstance);
        player.getInventory().equipItem(_wardItemInstance);
        player.sendPacket(SystemMsg.YOU_VE_ACQUIRED_THE_WARD_MOVE_QUICKLY_TO_YOUR_FORCES_OUTPOST);
        final DominionSiegeRunnerEvent runnerEvent = EventHolder.getInstance().getEvent(EventType.MAIN_EVENT, 1);
        runnerEvent.broadcastTo(new SystemMessage(SystemMsg.THE_S1_WARD_HAS_BEEN_DESTROYED_C2_NOW_HAS_THE_TERRITORY_WARD).addResidenceName(getDominionId()).addName(player));
        if (returnToBase != null) {
            returnToBase.cancel(true);
        }
    }

    @Override
    public boolean canAttack(final Player player) {
        player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_PERFORM_ANY_ATTACKS);
        return false;
    }

    @Override
    public boolean canCast(final Player player, final SkillEntry skill) {
        final SkillEntry[] skills = player.getActiveWeaponItem().getAttachedSkills();
        if (!ArrayUtils.contains(skills, skill)) {
            player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPONS_SKILL);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setItem(final ItemInstance item) {
    }

    public Location getWardLocation() {
        if (_wardItemInstance == null || _wardNpcInstance == null) {
            return null;
        }
        if (_wardItemInstance.getOwnerId() > 0 && _wardItemInstance.getLocation() != ItemLocation.VOID) {
            final Player player = GameObjectsStorage.getPlayer(_wardItemInstance.getOwnerId());
            if (player != null) {
                return player.getLoc();
            }
        }
        return _wardNpcInstance.getLoc();
    }

    public NpcInstance getWardNpcInstance() {
        return _wardNpcInstance;
    }

    public ItemInstance getWardItemInstance() {
        return _wardItemInstance;
    }

    public int getDominionId() {
        return _itemId - 13479;
    }

    public DominionSiegeEvent getEvent() {
        return _wardNpcInstance.getEvent(DominionSiegeEvent.class);
    }

    private void scheduleReturnToBase() {
        returnToBase = ThreadPoolManager.getInstance().schedule(new AutoReturnFlagTask(), _wardNpcInstance.isInZonePeace() ? 250 : 5 * 60 * 1000);
    }

    private class AutoReturnFlagTask extends RunnableImpl {
        @Override
        public void runImpl() throws Exception {
            if (_wardNpcInstance != null) {
                _wardNpcInstance.teleToLocation(_location);
            }
        }
    }
}
