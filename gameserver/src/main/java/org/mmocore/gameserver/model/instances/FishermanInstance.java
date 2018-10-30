package org.mmocore.gameserver.model.instances;

import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.manager.games.FishingChampionShipManager;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author n0nam3
 * @date 08/08/2010 16:22
 */
public class FishermanInstance extends MerchantInstance {
    private static final long serialVersionUID = 1L;

    public FishermanInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if ("FishingSkillList".equalsIgnoreCase(command)) {
            showFishingSkillList(player);
        } else if (command.startsWith("FishingReward") && AllSettingsConfig.ALT_FISH_CHAMPIONSHIP_ENABLED) {
            FishingChampionShipManager.getInstance().getReward(player);
        } else if (command.equalsIgnoreCase("menu_select?ask=-404&reply=1")) {
            FishingChampionShipManager.getInstance().showChampScreen(player, this);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}