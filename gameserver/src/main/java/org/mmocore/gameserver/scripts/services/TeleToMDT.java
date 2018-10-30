package org.mmocore.gameserver.scripts.services;

import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.utils.Location;

public class TeleToMDT {
    @Bypass("services.TeleToMDT:toMDT")
    public void toMDT(Player player, NpcInstance npc, String[] arg) {
        if (player == null || npc == null) {
            return;
        }

        if (!NpcInstance.canBypassCheck(player, npc)) {
            return;
        }

        player.getPlayerVariables().set(PlayerVariables.BACK_COORDINATES, player.getLoc().toXYZString(), -1);
        player.teleToLocation(12661, 181687, -3560);
    }

    @Bypass("services.TeleToMDT:fromMDT")
    public void fromMDT(Player player, NpcInstance npc, String[] arg) {
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
        player.teleToLocation(Location.parseLoc(var));
    }

    @Bypass("services.TeleToMDT:teleOut")
    public void teleOut(Player player, NpcInstance npc, String[] arg) {
        if (player == null || npc == null) {
            return;
        }
        player.teleToLocation(12902, 181011, -3563);
        Functions.show(player.isLangRus() ? "Я не знаю, как Вы попали сюда, но я могу Вас отправить за ограждение." : "I don't know from where you came here, but I can teleport you the another border side.", player, npc);
    }
}