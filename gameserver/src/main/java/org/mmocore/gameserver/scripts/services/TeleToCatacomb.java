package org.mmocore.gameserver.scripts.services;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.handler.npcdialog.INpcDialogAppender;
import org.mmocore.gameserver.handler.npcdialog.NpcDialogAppenderHolder;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;

public class TeleToCatacomb implements INpcDialogAppender, OnInitScriptListener {
    private final String TELE_RU;
    private final String FREE_RU;
    private final String TELE_EN;
    private final String FREE_EN;

    public TeleToCatacomb() {
        StringBuilder append = new StringBuilder(300);
        append.append("<br>");
        append.append("За определенную плату, вы можете переместиться в катакомбы или некрополисы.<br1> ");
        append.append("Список доступных локаций:<br>");
        getTeleports(append);
        TELE_RU = append.toString();

        append = new StringBuilder(300);
        append.append("<br>");
        append.append("За определенную плату, вы можете переместиться в катакомбы или некрополисы.<br1> ");
        append.append("Список доступных локаций:<br>");
        getFreeTeleports(append);
        FREE_RU = append.toString();

        append = new StringBuilder(300);
        append.append("<br>");
        append.append("Teleport to catacomb or necropolis.<br1> ");
        append.append("You may teleport to any of the following hunting locations.<br>");
        getTeleports(append);
        TELE_EN = append.toString();

        append = new StringBuilder(300);
        append.append("<br>");
        append.append("Teleport to catacomb or necropolis.<br1> ");
        append.append("You may teleport to any of the following hunting locations.<br>");
        getFreeTeleports(append);
        FREE_EN = append.toString();
    }

    private final void getTeleports(StringBuilder append) {
        append.append("[npc_%objectId%_teleport_request -41567 209463 -5080 10000|Necropolis of Sacrifice (20-30) - 10000 Adena]<br1>");
        append.append("[npc_%objectId%_teleport_request 45248 124223 -5408 20000|The Pilgrim's Necropolis (30-40) - 20000 Adena]<br1>");
        append.append("[npc_%objectId%_teleport_request 110911 174013 -5439 30000|Necropolis of Worship (40-50) - 30000 Adena]<br1>");
        append.append("[npc_%objectId%_teleport_request -22101 77383 -5173 40000|The Patriot's Necropolis (50-60) - 40000 Adena]<br1>");
        append.append("[npc_%objectId%_teleport_request -52654 79149 -4741 50000|Necropolis of Devotion (60-70) - 50000 Adena]<br1>");
        append.append("[npc_%objectId%_teleport_request 117884 132796 -4831 50000|Necropolis of Martyrdom (60-70) - 50000 Adena]<br1>");
        append.append("[npc_%objectId%_teleport_request 82750 209250 -5401 60000|The Saint's Necropolis (70-80) - 60000 Adena]<br1>");
        append.append("[npc_%objectId%_teleport_request 171897 -17606 -4901 60000|Disciples Necropolis(70-80) - 60000 Adena]<br>");

        append.append("[npc_%objectId%_teleport_request 42322 143927 -5381 20000|Catacomb of the Heretic (30-40) - 20000 Adena]<br1>");
        append.append("[npc_%objectId%_teleport_request 45841 170307 -4981 30000|Catacomb of the Branded (40-50) - 30000 Adena]<br1>");
        append.append("[npc_%objectId%_teleport_request 77348 78445 -5125 40000|Catacomb of the Apostate (50-60) - 40000 Adena]<br1>");
        append.append("[npc_%objectId%_teleport_request 139955 79693 -5429 50000|Catacomb of the Witch (60-70) - 50000 Adena]<br1>");
        append.append("[npc_%objectId%_teleport_request -19827 13509 -4901 60000|Catacomb of Dark Omens (70-80) - 60000 Adena]<br1>");
        append.append("[npc_%objectId%_teleport_request 113573 84513 -6541 60000|Catacomb of the Forbidden Path (70-80) - 60000 Adena]");
    }

    private final void getFreeTeleports(StringBuilder append) {
        append.append("[npc_%objectId%_teleport_request -41567 209463 -5080 0|Necropolis of Sacrifice (20-30)]<br1>");
        append.append("[npc_%objectId%_teleport_request 45248 124223 -5408 0|The Pilgrim's Necropolis (30-40)]<br1>");
        append.append("[npc_%objectId%_teleport_request 110911 174013 -5439 0|Necropolis of Worship (40-50)]<br1>");
        append.append("[npc_%objectId%_teleport_request -22101 77383 -5173 0|The Patriot's Necropolis (50-60)]<br1>");
        append.append("[npc_%objectId%_teleport_request -52654 79149 -4741 0|Necropolis of Devotion (60-70)]<br1>");
        append.append("[npc_%objectId%_teleport_request 117884 132796 -4831 0|Necropolis of Martyrdom (60-70)]<br1>");
        append.append("[npc_%objectId%_teleport_request 82750 209250 -5401 0|The Saint's Necropolis (70-80)]<br1>");
        append.append("[npc_%objectId%_teleport_request 171897 -17606 -4901 0|Disciples Necropolis(70-80)]<br>");

        append.append("[npc_%objectId%_teleport_request 42322 143927 -5381 0|Catacomb of the Heretic (30-40)]<br1>");
        append.append("[npc_%objectId%_teleport_request 45841 170307 -4981 0|Catacomb of the Branded (40-50)]<br1>");
        append.append("[npc_%objectId%_teleport_request 77348 78445 -5125 0|Catacomb of the Apostate (50-60)]<br1>");
        append.append("[npc_%objectId%_teleport_request 139955 79693 -5429 0|Catacomb of the Witch (60-70)]<br1>");
        append.append("[npc_%objectId%_teleport_request -19827 13509 -4901 0|Catacomb of Dark Omens (70-80)]<br1>");
        append.append("[npc_%objectId%_teleport_request 113573 84513 -6541 0|Catacomb of the Forbidden Path (70-80)]");
    }

    @Override
    public String getAppend(Player player, NpcInstance npc, int val) {
        if (val != 0 || !AllSettingsConfig.ALT_TELE_TO_CATACOMBS) {
            return "";
        }

        if (player.isLangRus()) {
            if (player.getLevel() <= AllSettingsConfig.GATEKEEPER_FREE) {
                return FREE_RU;
            } else {
                return TELE_RU;
            }
        } else {
            if (player.getLevel() <= AllSettingsConfig.GATEKEEPER_FREE) {
                return FREE_EN;
            } else {
                return TELE_EN;
            }
        }
    }

    @Override
    public void onInit() {
        NpcDialogAppenderHolder.getInstance().register(this);
    }

    @Override
    public int[] getNpcIds() {
        return new int[]{31212, 31213, 31214, 31215, 31216, 31217, 31218, 31219, 31220, 31221, 31222, 31223, 31224, 31767, 31768, 32048};
    }
}