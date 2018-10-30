package org.mmocore.gameserver.model.entity.events.objects;

import org.apache.commons.lang3.ArrayUtils;
import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.impl.FortressSiegeEvent;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.ItemInstance.ItemLocation;
import org.mmocore.gameserver.object.components.items.attachments.FlagItemAttachment;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author VISTALL
 * @date 11:59/24.03.2011
 * Item ID: 9819
 */
public class FortressCombatFlagObject implements SpawnableObject, FlagItemAttachment {
    private static final Logger _log = LoggerFactory.getLogger(FortressCombatFlagObject.class);
    private final Location _location;
    private ItemInstance _item;
    private Event _event;

    public FortressCombatFlagObject(final Location location) {
        _location = location;
    }

    @Override
    public void spawnObject(final Event event) {
        if (_item != null) {
            _log.info("FortressCombatFlagObject: can't spawn twice: " + event);
            return;
        }
        _item = ItemFunctions.createItem(9819);
        _item.setAttachment(this);
        _item.dropMe(null, _location);
        _item.setDropTime(0);

        _event = event;
    }

    @Override
    public void despawnObject(final Event event) {
        if (_item == null) {
            return;
        }

        final Player owner = GameObjectsStorage.getPlayer(_item.getOwnerId());
        if (owner != null) {
            owner.getInventory().destroyItem(_item);
            owner.sendDisarmMessage(_item);
        }

        _item.setAttachment(null);
        _item.setJdbcState(JdbcEntityState.UPDATED);
        _item.delete();

        _item.deleteMe();
        _item = null;

        _event = null;
    }

    @Override
    public void respawnObject(Event event) {

    }

    @Override
    public void refreshObject(final Event event) {

    }

    @Override
    public void onLogout(final Player player) {
        onDeath(player, null);
    }

    @Override
    public void onDeath(final Player owner, final Creature killer) {
        owner.getInventory().removeItem(_item);

        //_item.setOwnerId(0);
        _item.setLocation(ItemLocation.VOID);
        _item.setJdbcState(JdbcEntityState.UPDATED);
        _item.update();

        owner.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_DROPPED_S1).addItemName(_item.getItemId()));

        _item.dropMe(null, _location);
        _item.setDropTime(0);
    }

    @Override
    public boolean canPickUp(final Player player) {
        if (player.getActiveWeaponFlagAttachment() != null || player.isMounted()) {
            return false;
        }
        final FortressSiegeEvent event = player.getEvent(FortressSiegeEvent.class);
        if (event == null) {
            return false;
        }
        final SiegeClanObject object = event.getSiegeClan(FortressSiegeEvent.ATTACKERS, player.getClan());
        if (object == null) {
            return false;
        }
        if (player.isTransformed())
            return false;
        return true;
    }

    @Override
    public void pickUp(final Player player) {
        player.getInventory().equipItem(_item);

        final FortressSiegeEvent event = player.getEvent(FortressSiegeEvent.class);
        event.broadcastTo(new SystemMessage(SystemMsg.C1_HAS_ACQUIRED_THE_FLAG).addName(player), FortressSiegeEvent.ATTACKERS, FortressSiegeEvent.DEFENDERS);
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
        // ignored
    }

    public Event getEvent() {
        return _event;
    }
}
