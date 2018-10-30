package org.mmocore.gameserver.model.instances;

import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.gameserver.ai.CtrlIntention;
import org.mmocore.gameserver.handler.onshiftaction.OnShiftActionHolder;
import org.mmocore.gameserver.model.reference.L2Reference;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.network.lineage.serverpackets.MyTargetSelected;
import org.mmocore.gameserver.network.lineage.serverpackets.ShowTownMap;
import org.mmocore.gameserver.network.lineage.serverpackets.StaticObject;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.StaticObjectTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.world.World;

import java.util.Collections;
import java.util.List;


public class StaticObjectInstance extends GameObject {
    private final HardReference<StaticObjectInstance> reference;
    private final StaticObjectTemplate _template;
    private int _meshIndex;

    public StaticObjectInstance(final int objectId, final StaticObjectTemplate template) {
        super(objectId);

        _template = template;
        reference = new L2Reference<>(this);
    }

    @Override
    public HardReference<StaticObjectInstance> getRef() {
        return reference;
    }

    public int getUId() {
        return _template.getUId();
    }

    public int getType() {
        return _template.getType();
    }

    @Override
    public void onAction(final Player player, final boolean shift) {
        if (shift && OnShiftActionHolder.getInstance().callShiftAction(player, StaticObjectInstance.class, this, true)) {
            return;
        }

        if (player.getTarget() != this) {
            player.setTarget(this);
            player.sendPacket(new MyTargetSelected(getObjectId(), 0));
            return;
        }

        final MyTargetSelected my = new MyTargetSelected(getObjectId(), 0);
        player.sendPacket(my);

        if (!isInRangeZ(player, (int) (150 + player.getMinDistance(this)))) {
            if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
            }
            return;
        }

        if (_template.getType() == 0) // Arena Board
        {
            player.sendPacket(new HtmlMessage(getUId()).setFile("common/arena.htm"));
        } else if (_template.getType() == 2) // Village map
        {
            player.sendPacket(new ShowTownMap(_template.getFilePath(), _template.getMapX(), _template.getMapY()));
            player.sendActionFailed();
        }
    }

    @Override
    public List<L2GameServerPacket> addPacketList(final Player forPlayer, final Creature dropper) {
        return Collections.<L2GameServerPacket>singletonList(new StaticObject(this));
    }

    @Override
    public boolean isAttackable(final Creature attacker) {
        return false;
    }

    public void broadcastInfo(final boolean force) {
        final StaticObject p = new StaticObject(this);
        for (final Player player : World.getAroundObservers(this)) {
            player.sendPacket(p);
        }
    }

    @Override
    public int getGeoZ(final Location loc)   //FIXME [VISTALL] нужно ли?
    {
        return loc.z;
    }

    public int getMeshIndex() {
        return _meshIndex;
    }

    public void setMeshIndex(final int meshIndex) {
        _meshIndex = meshIndex;
    }
}