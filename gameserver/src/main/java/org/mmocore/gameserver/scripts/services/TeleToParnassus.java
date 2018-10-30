package org.mmocore.gameserver.scripts.services;

import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.handler.npcdialog.INpcDialogAppender;
import org.mmocore.gameserver.handler.npcdialog.NpcDialogAppenderHolder;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.listener.script.OnReloadScriptListener;
import org.mmocore.gameserver.listener.zone.OnZoneEnterLeaveListener;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.zone.Zone;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.PositionUtils;
import org.mmocore.gameserver.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class TeleToParnassus implements OnInitScriptListener, OnReloadScriptListener, INpcDialogAppender {
    private static final Logger _log = LoggerFactory.getLogger(TeleToParnassus.class);
    private static final String en = "<br>[npc_%objectId%_services.TeleToParnassus:toParnassus @811;Parnassus|\"Move to Parnassus (offshore zone) - " + ServicesConfig.SERVICES_PARNASSUS_PRICE + " Adena.\"]";
    private static final String ru = "<br>[npc_%objectId%_services.TeleToParnassus:toParnassus @811;Parnassus|\"Parnassus (торговая зона без налогов) - " + ServicesConfig.SERVICES_PARNASSUS_PRICE + " Adena.\"]";
    private static final String en2 = "<br>[npc_%objectId%_services.ManaRegen:DoManaRegen|Full MP Regeneration. (1 MP for 5 Adena)]<br>[npc_%objectId%_services.TeleToParnassus:fromParnassus @811;From Parnassus|\"Exit the Parnassus.\"]<br>";
    private static final String ru2 = "<br>[npc_%objectId%_services.ManaRegen:DoManaRegen|Полное восстановление MP. (1 MP за 5 Adena)]<br>[npc_%objectId%_services.TeleToParnassus:fromParnassus @811;From Parnassus|\"Покинуть Parnassus.\"]<br>";
    private static List<NpcInstance> _spawns = new ArrayList<NpcInstance>();
    private static Zone _zone = ReflectionUtils.getZone("[parnassus_offshore]");
    private static ZoneListener _zoneListener;

    @Override
    public void onInit() {
        if (!ServicesConfig.SERVICES_PARNASSUS_ENABLED) {
            return;
        }

        NpcDialogAppenderHolder.getInstance().register(this);
        ReflectionManager.PARNASSUS.setCoreLoc(new Location(149384, 171896, -952));

        // spawn wh keeper
        _spawns.add(NpcUtils.spawnSingle(30086, new Location(149960, 174136, -920, 32768), ReflectionManager.PARNASSUS));
        // spawn grocery trader (Helvetia)
        _spawns.add(NpcUtils.spawnSingle(30839, new Location(149368, 174264, -896, 49152), ReflectionManager.PARNASSUS));
        // spawn gk
        _spawns.add(NpcUtils.spawnSingle(13129, new Location(149368, 172568, -952, 49152), ReflectionManager.PARNASSUS));
        // spawn Orion the Cat
        _spawns.add(NpcUtils.spawnSingle(31860, new Location(148904, 173656, -952, 49152), ReflectionManager.PARNASSUS));
        // spawn blacksmith (Pushkin)
        _spawns.add(NpcUtils.spawnSingle(30300, new Location(148760, 174136, -952, 0), ReflectionManager.PARNASSUS));
        // spawn Item Broker
        _spawns.add(NpcUtils.spawnSingle(32320, new Location(149368, 173064, -952, 16384), ReflectionManager.PARNASSUS));

        _zoneListener = new ZoneListener();
        _zone.addListener(_zoneListener);
        _zone.setReflection(ReflectionManager.PARNASSUS);
        _zone.setActive(true);
        Zone zone = ReflectionUtils.getZone("[parnassus_peace]");
        zone.setReflection(ReflectionManager.PARNASSUS);
        zone.setActive(true);
        zone = ReflectionUtils.getZone("[parnassus_no_trade]");
        zone.setReflection(ReflectionManager.PARNASSUS);
        zone.setActive(true);

        _log.info("Loaded Service: Teleport to Parnassus");
    }

    @Override
    public void onReload() {
        _zone.removeListener(_zoneListener);
        for (NpcInstance spawn : _spawns) {
            spawn.deleteMe();
        }
        _spawns.clear();
    }

    @Bypass("services.TeleToParnassus:toParnassus")
    public void toParnassus(Player player, NpcInstance npc, String... arg) {
        if (player == null || npc == null) {
            return;
        }

        if (!NpcInstance.canBypassCheck(player, npc)) {
            return;
        }

        if (player.getAdena() < ServicesConfig.SERVICES_PARNASSUS_PRICE) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        player.reduceAdena(ServicesConfig.SERVICES_PARNASSUS_PRICE, true);
        player.getPlayerVariables().set(PlayerVariables.BACK_COORDINATES, player.getLoc().toXYZString(), -1);
        player.teleToLocation(Location.findPointToStay(_zone.getSpawn(), 30, 200, ReflectionManager.PARNASSUS.getGeoIndex()), ReflectionManager.PARNASSUS);
    }

    @Bypass("services.TeleToParnassus:fromParnassus")
    public void fromParnassus(Player player, NpcInstance npc, String... arg) {
        if (player == null || npc == null) {
            return;
        }

        if (!NpcInstance.canBypassCheck(player, npc)) {
            return;
        }

        String var = player.getPlayerVariables().get(PlayerVariables.BACK_COORDINATES);
        if (var == null || var.isEmpty()) {
            teleOut(player, npc, arg);
            return;
        }
        player.teleToLocation(Location.parseLoc(var), 0);
    }

    @Bypass("services.TeleToParnassus:teleOut")
    public void teleOut(Player player, NpcInstance npc, String... arg) {
        if (player == null || npc == null) {
            return;
        }
        player.teleToLocation(46776, 185784, -3528, 0);
        Functions.show(player.isLangRus() ? "Я не знаю, как Вы попали сюда, но я могу Вас отправить за ограждение." : "I don't know from where you came here, but I can teleport you the another border side.", player, npc);
    }

    @Override
    public String getAppend(Player player, NpcInstance npc, int val) {
        if (val != 0 || !ServicesConfig.SERVICES_PARNASSUS_ENABLED) {
            return null;
        }

        if (npc.getNpcId() == 13129) {
            if (player.getReflection() != ReflectionManager.PARNASSUS) {
                return null;
            }
            return player.isLangRus() ? ru2 : en2;
        } else {
            return player.isLangRus() ? ru : en;
        }
    }

    @Override
    public int[] getNpcIds() {
        return new int[]{13129, 30059, 30080, 30177, 30233, 30256, 30320, 30848, 30899, 31210, 31275, 31320, 31964, 30006, 30134, 30146, 32163, 30576, 30540};
    }

    public static class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
            // обрабатывать вход в зону не надо, только выход
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
            Player player = cha.getPlayer();
            if (player != null) {
                if (ServicesConfig.SERVICES_PARNASSUS_ENABLED && player.getReflection() == ReflectionManager.PARNASSUS && player.isVisible()) {
                    double angle = PositionUtils.convertHeadingToDegree(cha.getHeading()); // угол в градусах
                    double radian = Math.toRadians(angle - 90); // угол в радианах
                    cha.teleToLocation((int) (cha.getX() + 50 * Math.sin(radian)), (int) (cha.getY() - 50 * Math.cos(radian)), cha.getZ());
                }
            }
        }
    }
}