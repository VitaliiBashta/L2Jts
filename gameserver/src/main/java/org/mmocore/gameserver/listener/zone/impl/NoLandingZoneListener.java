package org.mmocore.gameserver.listener.zone.impl;

import org.jts.dataparser.data.holder.petdata.PetUtils.PetId;
import org.jts.dataparser.data.holder.transform.type.TransformType;
import org.mmocore.gameserver.data.xml.holder.ResidenceHolder;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.community.ICommunityComponent;

public class NoLandingZoneListener implements OnZoneEnterLeaveListener {
    public static final OnZoneEnterLeaveListener STATIC = new NoLandingZoneListener();

    @Override
    public void onZoneEnter(final Zone zone, final Creature actor) {
        final Player player = actor.getPlayer();
        if (player != null) {
            final ICommunityComponent communityComponent = player.getCommunityComponent();
            if (communityComponent != null) {
                final int transformId = communityComponent.getLeaseTransformId();
                if (transformId > 0 && player.isTransformed() && player.getTransformation().getData().type == TransformType.COMBAT) {
                    player.sendPacket(new CustomMessage("leaseTransform.siegeZone"));
                    player.stopTransformation();
                }
            }
            if (player.isFlying() && player.getMountNpcId() == PetId.WYVERN_ID) {
                final Residence residence = ResidenceHolder.getInstance().getResidence(zone.getParams().getInteger("residence", 0));
                if (residence != null && player.getClan() != null && residence.getOwner() == player.getClan()) {
                    //
                } else {
                    player.stopMove();
                    player.sendPacket(SystemMsg.THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN);
                    player.dismount();
                }
            }
        }
    }

    @Override
    public void onZoneLeave(final Zone zone, final Creature cha) {
    }
}
