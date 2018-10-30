package org.mmocore.gameserver.model.instances;

import javafx.util.Pair;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.data.xml.holder.TalismanEventHolder;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Hack
 * Date: 21.06.2017 23:38
 */
public class TalismanManagerInstance extends NpcInstance {
    public TalismanManagerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (command.startsWith("TalismanManager")) {
            StringTokenizer args = new StringTokenizer(command, " ");
            args.nextToken();
            switch (args.nextToken()) {
                case "use":
                    Choice choice = TalismanEventHolder.getInstance().get(Integer.parseInt(args.nextToken()));
                    try {
                        player.getInventory().writeLock();
                        if (!checkItemsOwn(player, choice.getPay())) {
                            player.sendMessage("Not enough items!");
                            return;
                        }
                        choice.getPay().forEach(pair -> Util.getPay(player, pair.getKey(), pair.getValue()));
                        if (Rnd.chance(choice.getChance())) {
                            player.sendMessage("Success!");
                            choice.getSuccess().forEach(pair -> Util.addItem(player, pair.getKey(), pair.getValue()));
                        } else {
                            player.sendMessage("Fail!");
                            choice.getSuccess().forEach(pair -> Util.addItem(player, pair.getKey(), pair.getValue()));
                        }
                    } finally {
                        player.getInventory().writeUnlock();
                    }
                    break;
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    private boolean checkItemsOwn(Player player, List<Pair<Integer, Long>> items) {
        for (Pair<Integer, Long> pair : items)
            if (player.getInventory().getCountOf(pair.getKey()) < pair.getValue())
                return false;
        return true;
    }

    public static class Choice {
        private int id;
        private int chance;
        private final List<Pair<Integer, Long>> pay = new ArrayList<>();
        private final List<Pair<Integer, Long>> success = new ArrayList<>();
        private final List<Pair<Integer, Long>> fail = new ArrayList<>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getChance() {
            return chance;
        }

        public void setChance(int chance) {
            this.chance = chance;
        }

        public List<Pair<Integer, Long>> getPay() {
            return pay;
        }

        public List<Pair<Integer, Long>> getSuccess() {
            return success;
        }

        public List<Pair<Integer, Long>> getFail() {
            return fail;
        }
    }
}
