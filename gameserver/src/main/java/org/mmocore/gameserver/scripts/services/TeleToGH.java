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

public class TeleToGH implements OnInitScriptListener, OnReloadScriptListener, INpcDialogAppender {
    private static final Logger _log = LoggerFactory.getLogger(TeleToGH.class);
    private static final String en = "<br>[npc_%objectId%_services.TeleToGH:toGH @811;Giran Harbor|\"I want free admission to Giran Harbor.\"]";
    private static final String ru = "<br>[npc_%objectId%_services.TeleToGH:toGH @811;Giran Harbor|\"Я хочу бесплатно попасть в Giran Harbor.\"]";
    private static final String en2 = "<br>[npc_%objectId%_services.ManaRegen:DoManaRegen|Full MP Regeneration. (1 MP for 5 Adena)]<br>[npc_%objectId%_services.TeleToGH:fromGH @811;From Giran Harbor|\"Exit the Giran Harbor.\"]<br>";
    private static final String ru2 = "<br>[npc_%objectId%_services.ManaRegen:DoManaRegen|Полное восстановление MP. (1 MP за 5 Adena)]<br>[npc_%objectId%_services.TeleToGH:fromGH @811;From Giran Harbor|\"Покинуть Giran Harbor.\"]<br>";
    private static List<NpcInstance> _spawns = new ArrayList<NpcInstance>();
    private static Zone _zone = ReflectionUtils.getZone("[giran_harbor_offshore]");
    private static ZoneListener _zoneListener;

    @Override
    public void onInit() {
        if (!ServicesConfig.SERVICES_GIRAN_HARBOR_ENABLED) {
            return;
        }

        NpcDialogAppenderHolder.getInstance().register(this);
        ReflectionManager.GIRAN_HARBOR.setCoreLoc(new Location(47416, 186568, -3480));

        // spawn wh keeper
        _spawns.add(NpcUtils.spawnSingle(30086, new Location(48059, 186791, -3512, 42000), ReflectionManager.GIRAN_HARBOR));
        // spawn grocery trader
        _spawns.add(NpcUtils.spawnSingle(32169, new Location(48146, 186753, -3512, 42000), ReflectionManager.GIRAN_HARBOR));
        // spawn gk
        _spawns.add(NpcUtils.spawnSingle(13129, new Location(47984, 186832, -3512, 42000), ReflectionManager.GIRAN_HARBOR));
        // spawn Orion the Cat
        _spawns.add(NpcUtils.spawnSingle(31860, new Location(48129, 186828, -3512, 45452), ReflectionManager.GIRAN_HARBOR));
        // spawn blacksmith (Pushkin)
        _spawns.add(NpcUtils.spawnSingle(30300, new Location(48102, 186772, -3512, 42000), ReflectionManager.GIRAN_HARBOR));
        // spawn Item Broker
        _spawns.add(NpcUtils.spawnSingle(32320, new Location(47772, 186905, -3480, 42000), ReflectionManager.GIRAN_HARBOR));
        _spawns.add(NpcUtils.spawnSingle(32320, new Location(46360, 187672, -3480, 42000), ReflectionManager.GIRAN_HARBOR));
        _spawns.add(NpcUtils.spawnSingle(32320, new Location(49016, 185960, -3480, 42000), ReflectionManager.GIRAN_HARBOR));

        _zoneListener = new ZoneListener();
        _zone.addListener(_zoneListener);
        _zone.setReflection(ReflectionManager.GIRAN_HARBOR);
        _zone.setActive(true);
        Zone zone = ReflectionUtils.getZone("[giran_harbor_peace_alt]");
        zone.setReflection(ReflectionManager.GIRAN_HARBOR);
        zone.setActive(true);
        zone = ReflectionUtils.getZone("[giran_harbor_no_trade]");
        zone.setReflection(ReflectionManager.GIRAN_HARBOR);
        zone.setActive(true);

        _log.info("Loaded Service: Teleport to Giran Harbor");
    }

    @Override
    public void onReload() {
        _zone.removeListener(_zoneListener);
        for (NpcInstance spawn : _spawns) {
            spawn.deleteMe();
        }
        _spawns.clear();
    }

    @Bypass("services.TeleToGH:toGH")
    public void toGH(Player player, NpcInstance npc, String... arg) {
        if (player == null || npc == null) {
            return;
        }

        if (!NpcInstance.canBypassCheck(player, npc)) {
            return;
        }

        player.getPlayerVariables().set(PlayerVariables.BACK_COORDINATES, player.getLoc().toXYZString(), -1);
        player.teleToLocation(Location.findPointToStay(_zone.getSpawn(), 30, 200, ReflectionManager.GIRAN_HARBOR.getGeoIndex()), ReflectionManager.GIRAN_HARBOR);
    }

    @Bypass("services.TeleToGH:fromGH")
    public void fromGH(Player player, NpcInstance npc, String... arg) {
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

    @Bypass("services.TeleToGH:teleOut")
    public void teleOut(Player player, NpcInstance npc, String... arg) {
        if (player == null || npc == null) {
            return;
        }
        player.teleToLocation(46776, 185784, -3528, 0);
        Functions.show(player.isLangRus() ? "Я не знаю, как Вы попали сюда, но я могу Вас отправить за ограждение." : "I don't know from where you came here, but I can teleport you the another border side.", player, npc);
    }

    @Override
    public String getAppend(Player player, NpcInstance npc, int val) {
        if (val != 0 || !ServicesConfig.SERVICES_GIRAN_HARBOR_ENABLED) {
            return "";
        }

        if (npc.getNpcId() == 13129) {
            if (player.getReflection() != ReflectionManager.GIRAN_HARBOR) {
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
                if (ServicesConfig.SERVICES_GIRAN_HARBOR_ENABLED && player.getReflection() == ReflectionManager.GIRAN_HARBOR && player.isVisible()) {
                    double angle = PositionUtils.convertHeadingToDegree(cha.getHeading()); // угол в градусах
                    double radian = Math.toRadians(angle - 90); // угол в радианах
                    cha.teleToLocation((int) (cha.getX() + 50 * Math.sin(radian)), (int) (cha.getY() - 50 * Math.cos(radian)), cha.getZ());
                }
            }
        }
    }
}