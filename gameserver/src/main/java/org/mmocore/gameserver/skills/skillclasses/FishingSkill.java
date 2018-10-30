package org.mmocore.gameserver.skills.skillclasses;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.xml.holder.FishDataHolder;
import org.mmocore.gameserver.geoengine.GeoEngine;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.model.zone.ZoneType;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Inventory;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.Fishing;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.item.WeaponTemplate;
import org.mmocore.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.mmocore.gameserver.templates.item.support.FishGrade;
import org.mmocore.gameserver.templates.item.support.FishGroup;
import org.mmocore.gameserver.templates.item.support.FishTemplate;
import org.mmocore.gameserver.templates.item.support.LureTemplate;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.PositionUtils;
import org.mmocore.gameserver.world.World;

import java.util.ArrayList;
import java.util.List;


public class FishingSkill extends Skill {
    public FishingSkill(final StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(final SkillEntry skillEntry, final Creature activeChar, final Creature target, final boolean forceUse, final boolean dontMove, final boolean first) {
        final Player player = (Player) activeChar;

        if (player.getSkillLevel(SKILL_FISHING_MASTERY) == -1) {
            return false;
        }

        if (player.isFishing()) {
            player.stopFishing();
            player.sendPacket(SystemMsg.YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED);
            return false;
        }

        if (player.isInBoat()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_FISH_WHILE_RIDING_AS_A_PASSENGER_OF_A_BOAT);
            return false;
        }

        if (player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_MANUFACTURE_OR_PRIVATE_STORE);
            return false;
        }

        final Zone fishingZone = player.getZone(ZoneType.FISHING);
        if (fishingZone == null) {
            player.sendPacket(SystemMsg.YOU_CANT_FISH_HERE);
            return false;
        }

        if (player.isInWater()) {
            player.sendPacket(SystemMsg.YOU_CANNOT_FISH_WHILE_UNDER_WATER);
            return false;
        }

        final WeaponTemplate weaponItem = player.getActiveWeaponItem();
        if (weaponItem == null || weaponItem.getItemType() != WeaponType.ROD) {
            //Fishing poles are not installed
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED);
            return false;
        }

        final ItemInstance lure = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
        if (lure == null || lure.getCount() < 1) {
            player.sendPacket(SystemMsg.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
            return false;
        }

        //Вычисляем координаты поплавка
        final int rnd = Rnd.get(50) + 150;
        final double angle = PositionUtils.convertHeadingToDegree(player.getHeading());
        final double radian = Math.toRadians(angle - 90);
        final double sin = Math.sin(radian);
        final double cos = Math.cos(radian);
        final int x1 = -(int) (sin * rnd);
        final int y1 = (int) (cos * rnd);
        final int x = player.getX() + x1;
        final int y = player.getY() + y1;
        //z - уровень карты
        int z = GeoEngine.getHeight(x, y, player.getZ(), player.getGeoIndex()) + 1;

        // Проверяем, что поплавок оказался в воде
        // в зоне типа 2 можно рыбачить без воды, но если вода есть то ставим поплавок на ее поверхность
        boolean isInWater = fishingZone.getParams().getInteger("fishing_place_type") == 2;
        final List<Zone> zones = new ArrayList<>();
        World.getZones(zones, new Location(x, y, z), player.getReflection());
        for (final Zone zone : zones) {
            if (zone.getType() == ZoneType.water) {
                //z - уровень воды
                z = zone.getTerritory().getZmax();
                isInWater = true;
                break;
            }
        }
        zones.clear();

        if (!isInWater) {
            player.sendPacket(SystemMsg.YOU_CANT_FISH_HERE);
            return false;
        }

        player.getFishing().setFishLoc(new Location(x, y, z));

        return super.checkCondition(skillEntry, activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final SkillEntry skillEntry, final Creature caster, final List<Creature> targets) {
        if (caster == null || !caster.isPlayer()) {
            return;
        }

        final Player player = (Player) caster;

        final ItemInstance lure = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
        if (lure == null || lure.getCount() < 1) {
            player.sendPacket(SystemMsg.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
            return;
        }
        final Zone zone = player.getZone(ZoneType.FISHING);
        if (zone == null) {
            return;
        }
        final LureTemplate lureTemplate = FishDataHolder.getInstance().getLure(lure.getItemId());
        if (lureTemplate == null) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_BAIT);
            return;
        }
        final int lvl = Fishing.getRandomFishLvl(player);
        final FishGrade grade = Fishing.getGroupForLure(lure.getItemId());
        final FishGroup type = Fishing.getRandomFishType(grade, lure.getItemId());
        boolean isHotSpring = false;
        if (zone.getName().startsWith("fishing_zone_9")) {
            isHotSpring = Rnd.nextBoolean();
        }

        isHotSpring = isHotSpring && lure.getItemId() == 8548 && player.getSkillLevel(1315) > 19;

        final List<FishTemplate> fishs = FishDataHolder.getInstance().getFish(lvl, type, grade);
        if (fishs == null || fishs.isEmpty()) {
            player.sendPacket(SystemMsg.SYSTEM_ERROR);
            return;
        }

        if (!player.getInventory().destroyItemByObjectId(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND), 1L)) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_BAIT);
            return;
        }

        final int check = Rnd.get(fishs.size());
        final FishTemplate fish = isHotSpring ? FishDataHolder.getInstance().getFish(8547) : fishs.get(check);
        player.startFishing(fish, lureTemplate);
    }
}