package org.mmocore.gameserver.model.entity.events.impl.reflection.object;

import org.mmocore.commons.database.dao.JdbcEntityState;
import org.mmocore.gameserver.model.entity.events.impl.ReflectionEvent;
import org.mmocore.gameserver.model.entity.events.impl.reflection.RBaseController;
import org.mmocore.gameserver.model.entity.events.impl.reflection.REventState;
import org.mmocore.gameserver.model.entity.events.impl.reflection.RTeamType;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.ExShowScreenMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.GetItem;
import org.mmocore.gameserver.network.lineage.serverpackets.RadarControl;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.items.attachments.FlagItemAttachment;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.mmocore.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Mangol
 * @since 08.10.2016
 */
public class RFlagObject implements FlagItemAttachment {
    private static final Logger LOGGER = LoggerFactory.getLogger(RFlagObject.class);
    private final Location baseLocation;
    private ItemInstance item;
    private ReflectionEvent event;

    public RFlagObject(final ReflectionEvent event) {
        this.event = event;
        this.baseLocation = new Location(142056, 142840, -15264);
    }

    public Location getBaseLocation() {
        return baseLocation;
    }

    @Override
    public void onLogout(final Player player) {
        onDeath(player, null);
    }

    @Override
    public void onDeath(Player owner, Creature killer) {
        owner.sendPacket(new RadarControl(RadarControl.RadarState.DELETE_FLAG, RadarControl.RadarType.FLAG_ON_MAP, 0, 0, 0));
        owner.getInventory().removeItem(item);
        item.setJdbcState(JdbcEntityState.UPDATED);
        item.setLocation(ItemInstance.ItemLocation.VOID);
        item.update();
        owner.sendPacket(new SystemMessage(SystemMsg.YOU_HAVE_DROPPED_S1).addItemName(item.getItemId()));
        World.removeVisibleObject(item);
        item.dropMe(null, baseLocation, false);
        item.setReflection(event.getReflection());
        item.setDropTime(0);
        World.addVisibleObject(item, null);
        if (event != null) {
            for (RBaseController controller : event.getBaseControllerMap().values()) {
                if (controller.getPlayers().isEmpty())
                    continue;
                controller.getPlayers().stream().filter(p -> p.getPlayer() != null).forEach(p -> p.getPlayer().sendPacket(new ExShowScreenMessage(new CustomMessage("event.r.flagHome").toString(p.getPlayer()), 1500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false)));
            }
        }
    }

    public void goToBase(Player owner) {
        onDeath(owner, null);
    }

    @Override
    public boolean canAttack(Player player) {
        player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_PERFORM_ANY_ATTACKS);
        return false;
    }

    @Override
    public boolean canCast(Player player, SkillEntry skillEntry) {
        return false;
    }

    @Override
    public boolean canPickUp(Player player) {
        if (event.getEventState() == REventState.RAID_BOSS) {
            return false;
        }
        if (player.getActiveWeaponFlagAttachment() != null || player.isMounted()) {
            return false;
        }
        final ReflectionEvent event = player.getEvent(ReflectionEvent.class);
        if (this.event != event) {
            return false;
        }
        return true;
    }

    @Override
    public void pickUp(final Player player) {
        RTeamType teamType = player.getEventComponent().getTeam(ReflectionEvent.class);
        if (teamType == null)
            return;
        final RBaseController controller = event.getBaseControllerMap().get(teamType);
        if (controller != null) {
            player.sendPacket(new RadarControl(RadarControl.RadarState.DELETE_FLAG, RadarControl.RadarType.FLAG_ON_MAP, 0, 0, 0));
            player.sendPacket(new RadarControl(RadarControl.RadarState.SHOW_RADAR, RadarControl.RadarType.ARROW, controller.getBaseLocation()));
        }
        player.getInventory().equipItem(item);
        player.broadcastPacket(new GetItem(item, player.getObjectId()));
        for (final RBaseController base : event.getBaseControllerMap().values()) {
            if (base.getPlayers().isEmpty()) {
                continue;
            }
            if (base.getTeamType() == teamType) {
                base.getPlayers().stream().filter(p -> p.getPlayer() != null).forEach(p -> p.getPlayer().sendPacket(new ExShowScreenMessage(new CustomMessage("event.r.flagPickUpC").toString(player), 1500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false)));
            } else {
                base.getPlayers().stream().filter(p -> p.getPlayer() != null).forEach(p -> p.getPlayer().sendPacket(new ExShowScreenMessage(new CustomMessage("event.r.flagPickUp").addString(teamType.getNameTeam()).toString(player), 1500, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false)));
            }
        }
        item.decayMe();
    }

    public GameObject getOwner() {
        return item == null ? null : GameObjectsStorage.getPlayer(item.getOwnerId());
    }

    public ItemInstance getItem() {
        return item;
    }

    @Override
    public void setItem(ItemInstance item) {

    }

    public void spawnObject() {
        if (item != null) {
            LOGGER.info("RFlagObject: can't spawn twice: " + event);
            return;
        }
        item = ItemFunctions.createItem(9819);
        item.setAttachment(this);
        item.dropMe(null, baseLocation, false);
        item.setReflection(event.getReflection());
        item.setDropTime(0);
    }

    public void despawnObject() {
        if (item == null) {
            return;
        }
        final Player owner = GameObjectsStorage.getPlayer(item.getOwnerId());
        if (owner != null) {
            owner.getInventory().destroyItem(item);
            owner.sendDisarmMessage(item);
        }
        item.setAttachment(null);
        item.setJdbcState(JdbcEntityState.UPDATED);
        item.delete();
        item.deleteMe();
        item = null;
        event = null;
    }
}
