package org.mmocore.gameserver.utils;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.components.creatures.tasks.DeleteTask;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 11:02/24.05.2011
 */
public class NpcUtils {
    public static NpcInstance spawnSingleStablePoint(final int npcId, final int x, final int y, final int z, final int offset) {
        Location loc = Location.findPointToStay(new Location(x, y, z), offset, ReflectionManager.DEFAULT.getGeoIndex());
        return spawnSingle(npcId, loc, ReflectionManager.DEFAULT, 0, null);
    }


    //[Hack]: В жопу ваш оффлайк и мобов под картой
    public static NpcInstance spawnSingleStablePoint(final int npcId, final int x, final int y, final int z) {
        Location loc = Location.findPointToStay(new Location(x, y, z), 80, ReflectionManager.DEFAULT.getGeoIndex());
        return spawnSingle(npcId, loc, ReflectionManager.DEFAULT, 0, null);
    }

    public static NpcInstance spawnSingleStablePoint(final int npcId, final int x, final int y, final int z, final long despawnTime) {
        Location loc = Location.findPointToStay(new Location(x, y, z), 80, ReflectionManager.DEFAULT.getGeoIndex());
        return spawnSingle(npcId, loc, ReflectionManager.DEFAULT, despawnTime, null);
    }

    public static NpcInstance spawnSingleStablePoint(final int npcId, final Location location, final long despawnTime) {
        Location loc = Location.findPointToStay(location, 80, ReflectionManager.DEFAULT.getGeoIndex());
        return spawnSingle(npcId, loc, ReflectionManager.DEFAULT, despawnTime, null);
    }

    public static NpcInstance spawnSingleStablePoint(final int npcId, final Location location) {
        Location loc = Location.findPointToStay(location, 80, ReflectionManager.DEFAULT.getGeoIndex());
        return spawnSingle(npcId, loc, ReflectionManager.DEFAULT, 0, null);
    }

    public static NpcInstance spawnSingle(final int npcId, final int x, final int y, final int z) {
        return spawnSingle(npcId, new Location(x, y, z, -1), ReflectionManager.DEFAULT, 0, null);
    }

    public static NpcInstance spawnSingle(final int npcId, final int x, final int y, final int z, final long despawnTime) {
        return spawnSingle(npcId, new Location(x, y, z, -1), ReflectionManager.DEFAULT, despawnTime, null);
    }

    public static NpcInstance spawnSingle(final int npcId, final int x, final int y, final int z, final int h, final long despawnTime) {
        return spawnSingle(npcId, new Location(x, y, z, h), ReflectionManager.DEFAULT, despawnTime, null);
    }

    public static NpcInstance spawnSingle(final int npcId, final Location loc) {
        return spawnSingle(npcId, loc, ReflectionManager.DEFAULT, 0, null);
    }

    public static NpcInstance spawnSingle(final int npcId, final Location loc, final long despawnTime) {
        return spawnSingle(npcId, loc, ReflectionManager.DEFAULT, despawnTime, null);
    }

    public static NpcInstance spawnSingle(final int npcId, final Location loc, final Reflection reflection) {
        return spawnSingle(npcId, loc, reflection, 0, null);
    }

    public static NpcInstance spawnSingle(final int npcId, final Location loc, final Reflection reflection, final long despawnTime) {
        return spawnSingle(npcId, loc, reflection, despawnTime, null);
    }

    public static NpcInstance spawnSingle(final int npcId, final Location loc, final Reflection reflection, final long despawnTime, final String title) {
        final NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
        if (template == null) {
            throw new NullPointerException("Npc template id : " + npcId + " not found!");
        }
        final NpcInstance npc = template.getNewInstance();
        npc.setHeading(loc.h < 0 ? Rnd.get(0xFFFF) : loc.h);
        npc.setSpawnedLoc(loc);
        npc.setReflection(reflection);
        npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
        if (title != null) {
            npc.setTitle(title);
        }
        npc.spawnMe(npc.getSpawnedLoc());
        if (despawnTime > 0) {
            ThreadPoolManager.getInstance().schedule(new DeleteTask(npc), despawnTime);
        }
        return npc;
    }

    public static NpcInstance createOnePrivateEx(final int npcId, final int x, final int y, final int z, final int i0, final int i1) {
        return createOnePrivateEx(npcId, new Location(x, y, z, -1), ReflectionManager.DEFAULT, 0, null, null, i0, i1);
    }

    public static NpcInstance createOnePrivateEx(final int npcId, final Location loc, final Reflection reflection, final Creature arg, final int i0, final int i1) {
        return createOnePrivateEx(npcId, loc, reflection, 0, null, arg, i0, i1);
    }

    public static NpcInstance createOnePrivateEx(final int npcId, final int x, final int y, final int z, final Creature arg, final int i0, final int i1) {
        return createOnePrivateEx(npcId, new Location(x, y, z, -1), ReflectionManager.DEFAULT, 0, null, arg, i0, i1);
    }

    public static NpcInstance createOnePrivateEx(final int npcId, final Location loc, final Reflection reflection, final long despawnTime, final String title, final Creature arg, final int i0, final int i1) {
        final NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
        if (template == null) {
            throw new NullPointerException("Npc template id : " + npcId + " not found!");
        }
        final NpcInstance npc = template.getNewInstance();
        npc.setHeading(loc.h < 0 ? Rnd.get(0xFFFF) : loc.h);
        npc.setSpawnedLoc(loc);
        npc.setReflection(reflection);
        npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
        if (title != null) {
            npc.setTitle(title);
        }
        npc.spawnMe(npc.getSpawnedLoc());
        npc.setParam2(i0);
        npc.setParam3(i1);
        if (arg != null) {
            npc.setParam4(arg);
        }
        if (despawnTime > 0) {
            ThreadPoolManager.getInstance().schedule(new DeleteTask(npc), despawnTime);
        }
        return npc;
    }
}
