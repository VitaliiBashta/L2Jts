package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.manager.MapRegionManager;
import org.mmocore.gameserver.manager.ReflectionManager;
import org.mmocore.gameserver.model.entity.Reflection;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.scripts.instances.KamalokaNightmare;
import org.mmocore.gameserver.templates.InstantZone;
import org.mmocore.gameserver.templates.StatsSet;
import org.mmocore.gameserver.templates.mapregion.DomainArea;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.utils.ReflectionUtils;

public class PathfinderInstance extends NpcInstance {
    //Instance's ID
    private static final int INSTANCE_75LVL_ID = 56;

    private int _rank = -1;
    private boolean _rewarded = false;

    public PathfinderInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    private static int[][] getRewardList(int rank, InstantZone iz) {
        if (iz == null) {
            return null;
        }

        StatsSet params = iz.getAddParams();
        String rewards = null;
        for (int i = rank; i >= 0; i--) {
            rewards = params.getString("reward_lvl_" + i, null);
            if (rewards != null) {
                break;
            }
        }

        String[] items_list = rewards.split(";");
        int rewards_count = items_list.length;
        int[][] result = new int[rewards_count][];
        for (int i = 0; i < rewards_count; i++) {
            String[] item_s = items_list[i].split("-");
            if (item_s.length != 2) {
                continue;
            }

            int[] item = new int[2];
            item[0] = Integer.parseInt(item_s[0]);
            item[1] = Integer.parseInt(item_s[1]);
            result[i] = item;
        }
        return result;
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String pom;
        if (val == 0) {
            pom = String.valueOf(npcId);
        } else {
            pom = npcId + "-" + val;
        }

        return "instance/soloKamaloka/" + pom + ".htm";
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("ExitSoloKama")) {
            Reflection r = getReflection();
            if (r.getReturnLoc() != null) {
                player.teleToLocation(r.getReturnLoc(), ReflectionManager.DEFAULT);
            } else {
                player.setReflection(ReflectionManager.DEFAULT);
            }
            player.getPlayerVariables().remove(PlayerVariables.BACK_COORDINATES);
            r.startCollapseTimer(1000);
        } else if (command.startsWith("enter_dc")) {
            player.getPlayerVariables().set(PlayerVariables.DC_BACK_COORDS, player.getLoc().toXYZString(), -1);
            player.teleToLocation(-114582, -152635, -6742);
        } else if (command.startsWith("ListPossible")) {
            if (getNpcId() == 32484 && AllSettingsConfig.ALT_KAMALOKA_NIGHTMARES_PREMIUM_ONLY && !player.getPremiumAccountComponent().hasBonus()) {
                showChatWindow(player, "pts/kamaloka/npc_rim_maker003.htm");
                return;
            }
            //FIXME [G1ta0] исправить, когда будет реализованы индивидуальные параметры спавна
            DomainArea domain = MapRegionManager.getInstance().getRegionData(DomainArea.class, this);
            String htmlpath = "instance/soloKamaloka/" + getNpcId();
            if (domain != null) {
                switch (domain.getId()) {
                    case 1: // Town of Gludio
                        htmlpath += "-gludio";
                        break;
                    case 2: // Town of Dion
                        htmlpath += "-dion";
                        break;
                    case 4: // Town of Oren
                        htmlpath += "-oren";
                        break;
                    case 6: // Heine
                        htmlpath += "-heine";
                        break;
                    case 8: // Rune Township
                        htmlpath += "-rune";
                        break;
                    case 9: // Town of Schuttgart
                        htmlpath += "-schuttgart";
                        break;
                }
            }
            htmlpath += ".htm";
            showChatWindow(player, htmlpath);
        } else if (command.startsWith("ShowResults")) {
            String htmlpath = "instance/soloKamaloka/" + getNpcId();
            switch (getRewardRank()) {
                case 0:
                    htmlpath += "-F";
                    break;
                case 1:
                    htmlpath += "-D";
                    break;
                case 2:
                    htmlpath += "-C";
                    break;
                case 3:
                    htmlpath += "-B";
                    break;
                case 4:
                    htmlpath += "-A";
                    break;
                case 5:
                    htmlpath += "-S";
                    break;
                case 6:
                    if (getReflection().getInstancedZoneId() == INSTANCE_75LVL_ID) // G-Grade награда доступна только в соло камалоке 70-80 уровней.
                    {
                        htmlpath += "-G";
                    } else {
                        htmlpath += "-S";
                    }
                    break;
            }
            htmlpath += ".htm";
            showChatWindow(player, htmlpath);
        } else if (command.startsWith("SoloKamaReward")) {
            if (!_rewarded) {
                int[][] rewards = getRewardList(getRewardRank(), getReflection().getInstancedZone());
                if (rewards != null) {
                    for (int[] item : rewards) {
                        if (item != null) {
                            int id = item[0];
                            int count = item[1];
                            if (id > 0 && count > 0) {
                                ItemFunctions.addItem(player, id, count);
                            }
                        }
                    }
                }

                _rewarded = true;
            }

            showChatWindow(player, 1);
        } else if (command.startsWith("Chat")) {
            try {
                int val = Integer.parseInt(command.substring(5));
                showChatWindow(player, val);
            } catch (NumberFormatException nfe) {
                String filename = command.substring(5).trim();
                if (filename.length() == 0) {
                    showChatWindow(player, "npcdefault.htm");
                } else {
                    showChatWindow(player, filename);
                }
            }
        } else if (command.startsWith("solo_kamaloka")) {
            int val = Integer.parseInt(command.substring(14));
            if (val >= 46 && val <= 56) // sanity check
            {
                if (player.isInParty()) {
                    showChatWindow(player, "pts/kamaloka/npc_rim_maker005.htm");
                } else {
                    final Reflection newInstance = ReflectionUtils.simpleEnterInstancedZone(player, KamalokaNightmare.class, val);
                    if (newInstance != null) {
                        ((KamalokaNightmare) newInstance).setPlayerId(player.getObjectId());
                    } else if (player.getReflection().getInstancedZoneId() != val) {
                        showChatWindow(player, "pts/kamaloka/npc_rim_maker004.htm");
                    }
                }
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    private int getRewardRank() {
        if (_rank >= 0) {
            return _rank;
        }

        Reflection r = getReflection();
        if (r instanceof KamalokaNightmare) {
            _rank = ((KamalokaNightmare) r).getRank();
        } else {
            _rank = 0;
        }

        return _rank;
    }
}