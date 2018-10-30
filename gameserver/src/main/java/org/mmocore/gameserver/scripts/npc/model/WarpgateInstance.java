package org.mmocore.gameserver.scripts.npc.model;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.commons.threading.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.manager.HellboundManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author pchayka
 */
public class WarpgateInstance extends NpcInstance {
    private static final int confidenceAdd = (int) (500 * AllSettingsConfig.HELLBOUND_RATE);

    public WarpgateInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() {
                final int i2 = HellboundManager.getHellboundLevel();
                final long confidence = HellboundManager.getConfidence();
                if (i2 == 1) {
                    if (confidence < 400000) {
                        HellboundManager.addConfidence(confidenceAdd);
                    }
                } else if (i2 == 2) {
                    if (confidence < 492000) {
                        HellboundManager.addConfidence(confidenceAdd);
                    }
                } else if (i2 == 3) {
                    if (confidence < 584000) {
                        HellboundManager.addConfidence(confidenceAdd);
                    }
                } else if (i2 == 4) {
                    if (confidence < 676000) {
                        HellboundManager.addConfidence(confidenceAdd);
                    }
                } else if (i2 == 5) {
                    if (confidence < 768000) {
                        HellboundManager.addConfidence(confidenceAdd);
                    }
                } else if (i2 == 6) {
                    if (confidence < 860000) {
                        HellboundManager.addConfidence(confidenceAdd);
                    }
                } else if (i2 == 7) {
                    if (confidence < 952000) {
                        HellboundManager.addConfidence(confidenceAdd);
                    }
                } else if (i2 == 8) {
                    if (confidence < 1044000) {
                        HellboundManager.addConfidence(confidenceAdd);
                    }
                } else if (i2 == 9) {
                    if (confidence < 1136000) {
                        HellboundManager.addConfidence(confidenceAdd);
                    }
                } else if (i2 == 10) {
                    if (confidence < 1228000) {
                        HellboundManager.addConfidence(confidenceAdd);
                    }
                } else if (i2 == 11) {
                    if (confidence < 1320000) {
                        HellboundManager.addConfidence(confidenceAdd);
                    }
                }
            }
        }, 60 * 60 * 1000L, 60 * 60 * 1000L);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        if (HellboundManager.getHellboundLevel() == 0 && !AllSettingsConfig.ALLOW_CLOSED_HELLBOUND) {
            showChatWindow(player, "pts/heine/warp_gate001.htm");
        } else {
            showChatWindow(player, "pts/heine/warp_gate001a.htm");
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (command.startsWith("enter_hellbound")) {
            if (HellboundManager.getHellboundLevel() != 0 && (player.isQuestCompleted(130) || player.isQuestCompleted(133))
                    || HellboundManager.getHellboundLevel() != 0 && AllSettingsConfig.HELLBOUND_TELE_WITHOUT_QUEST
                    || AllSettingsConfig.ALLOW_CLOSED_HELLBOUND && AllSettingsConfig.HELLBOUND_TELE_WITHOUT_QUEST) {
                player.teleToLocation(-11272, 236464, -3248);
            } else {
                showChatWindow(player, "pts/heine/warp_gate001b.htm");
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}