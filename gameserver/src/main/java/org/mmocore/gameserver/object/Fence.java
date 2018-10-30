package org.mmocore.gameserver.object;

import org.mmocore.commons.geometry.Shape;
import org.mmocore.gameserver.configuration.config.GeodataConfig;
import org.mmocore.gameserver.geoengine.GeoCollision;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.network.lineage.serverpackets.ExColosseumFenceInfo;
import org.mmocore.gameserver.network.lineage.serverpackets.L2GameServerPacket;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.templates.FenceTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.utils.Location;

import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Laky
 * Date: 15.03.12
 * Time: 20:06
 * The Abyss (c) 2012
 */
public final class Fence extends Creature implements GeoCollision {
    private final int width;
    private final int height;
    private int type;
    private byte[][] geoAround;

    public Fence(int objectId, Location loc, int type, int width, int height) {
        super(objectId, new FenceTemplate(loc, type, width, height));
        this.type = type;
        this.width = width;
        this.height = height;
    }

    public int getType() {
        return type;
    }

    public void setType(int t) {
        type = t;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return false;
    }

    @Override
    public int getLevel() {
        return 0;
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
    public void broadcastCharInfo() {
        broadcastPacket(new ExColosseumFenceInfo(this));
    }

    @Override
    public void validateLocation(int broadcast) {
    }

    @Override
    public void sendChanges() {
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
        return Collections.<L2GameServerPacket>singletonList(new ExColosseumFenceInfo(this));
    }

    @Override
    public FenceTemplate getTemplate() {
        return (FenceTemplate) super.getTemplate();
    }

    @Override
    public Shape getShape() {
        return getTemplate().getPolygon();
    }

    @Override
    public byte[][] getGeoAround() {
        return geoAround;
    }

    @Override
    public void setGeoAround(byte[][] geo) {
        geoAround = geo;
    }

    @Override
    public boolean isConcrete() {
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

    public void setCollision(boolean m) {
        if (GeodataConfig.ALLOW_GEODATA) {
            if (m) {
                GeoEngine.applyGeoCollision(this, getGeoIndex());
            } else {
                GeoEngine.removeGeoCollision(this, getGeoIndex());
            }
        }
    }
}
