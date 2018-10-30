package org.mmocore.gameserver.scripts.npc.model;

import org.jts.dataparser.data.holder.setting.common.PlayerRace;
import org.mmocore.gameserver.configuration.config.OlympiadConfig;
import org.mmocore.gameserver.model.entity.Hero;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.serverpackets.ExHeroList;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;

import java.util.StringTokenizer;

/**
 * @author VISTALL
 * @date 17:17/01.09.2011
 */
public class OlympiadObeliskInstance extends NpcInstance {
    private static final long serialVersionUID = 521439086476657130L;

    private static final int[] ITEMS =
            {
                    6611,
                    6612,
                    6613,
                    6614,
                    6615,
                    6616,
                    6617,
                    6618,
                    6619,
                    6620,
                    6621,
                    9388,
                    9389,
                    9390
            };

    public OlympiadObeliskInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (checkForDominionWard(player)) {
            return;
        }

        if (!OlympiadConfig.ENABLE_OLYMPIAD) {
            return;
        }

        StringTokenizer token = new StringTokenizer(command, " ");
        String actualCommand = token.nextToken();
        switch (actualCommand) {
            case "becameHero":
                if (Hero.getInstance().isInactiveHero(player.getObjectId())) {
                    Hero.getInstance().activateHero(player);
                    showChatWindow(player, "olympiad/monument_give_hero.htm");
                } else {
                    showChatWindow(player, "olympiad/monument_dont_hero.htm");
                }
                break;
            case "heroList":
                player.sendPacket(new ExHeroList());
                break;
            case "getCirclet":
                if (player.isHero()) {
                    if (player.getInventory().getItemByItemId(6842) != null) {
                        showChatWindow(player, "olympiad/monument_circlet_have.htm");
                    } else {
                        ItemFunctions.addItem(player, 6842, 1, true); //Wings of Destiny Circlet
                    }
                } else {
                    showChatWindow(player, "olympiad/monument_circlet_no_hero.htm");
                }
                break;
            case "getItem":
                if (!player.isHero() && !player.getCustomPlayerComponent().isTemporalHero()) {
                    showChatWindow(player, "olympiad/monument_weapon_no_hero.htm");
                } else {
                    for (int heroItem : ITEMS) {
                        if (player.getInventory().getItemByItemId(heroItem) != null) {
                            showChatWindow(player, "olympiad/monument_weapon_have.htm");
                            return;
                        }
                    }

                    int val = Integer.parseInt(token.nextToken());

                    if (val < 11) {
                        if (player.getPlayerTemplateComponent().getPlayerRace() == PlayerRace.kamael) {
                            return;
                        }
                    } else if (player.getPlayerTemplateComponent().getPlayerRace() != PlayerRace.kamael) {
                        return;
                    }

                    ItemFunctions.addItem(player, ITEMS[val], 1, true);
                }
                break;
            default:
                super.onBypassFeedback(player, command);
                break;
        }
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        if (checkForDominionWard(player)) {
            return;
        }

        String fileName = "olympiad/monument";
        if (player.isNoble()) {
            fileName += "_n";
        }
        if (val > 0) {
            fileName += "-" + val;
        }
        fileName += ".htm";
        player.sendPacket(new HtmlMessage(this, fileName, val));
    }
}
