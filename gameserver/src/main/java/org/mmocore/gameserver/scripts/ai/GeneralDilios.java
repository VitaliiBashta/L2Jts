package org.mmocore.gameserver.scripts.ai;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.manager.SoDManager;
import org.mmocore.gameserver.manager.SoIManager;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.SocialAction;

import java.util.List;


/**
 * @author pchayka, KilRoy
 */
public class GeneralDilios extends DefaultAI {
    private static final int GUARD_ID = 32619;
    private long _wait_timeout = 0;

    public GeneralDilios(NpcInstance actor) {
        super(actor);
        AI_TASK_ATTACK_DELAY = 10000;
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();

        if (System.currentTimeMillis() > _wait_timeout) {
            _wait_timeout = System.currentTimeMillis() + 300000;
            int i0 = Rnd.get(1);
            switch (i0) {
                case 0:
                    final int stage = SoIManager.getCurrentStage();
                    if (stage == 1) {
                        Functions.npcSay(actor, NpcString.MESSENGER_INFORM_THE_BROTHERS_IN_KUCEREUS_CLAN_OUTPOST_BRAVE_ADVENTURERS_WHO_HAVE_CHALLENGED_THE_SEED_OF_INFINITY_ARE_CURRENTLY_INFILTRATING_THE_HALL_OF_EROSION_THROUGH_THE_DEFENSIVELY_WEAK_HALL_OF_SUFFERING);
                    } else if (stage == 2) {
                        Functions.npcSay(actor, NpcString.MESSENGER_INFORM_THE_BROTHERS_IN_KUCEREUS_CLAN_OUTPOST_SWEEPING_THE_SEED_OF_INFINITY_IS_CURRENTLY_COMPLETE_TO_THE_HEART_OF_THE_SEED);
                    } else if (stage == 3) {
                        Functions.npcSay(actor, NpcString.MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_THE_SEED_OF_INFINITY_IS_CURRENTLY_SECURED_UNDER_THE_FLAG_OF_THE_KEUCEREUS_ALLIANCE);
                    } else if (stage == 4) {
                        Functions.npcSay(actor, NpcString.MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_THE_RESURRECTED_UNDEAD_IN_THE_SEED_OF_INFINITY_ARE_POURING_INTO_THE_HALL_OF_SUFFERING_AND_THE_HALL_OF_EROSION);
                    } else if (stage == 5) {
                        Functions.npcSay(actor, NpcString.MESSENGER_INFORM_THE_BROTHERS_IN_KUCEREUS_CLAN_OUTPOST_EKIMUS_IS_ABOUT_TO_BE_REVIVED_BY_THE_RESURRECTED_UNDEAD_IN_SEED_OF_INFINITY);
                        final List<NpcInstance> around = actor.getAroundNpc(1500, 100);
                        if (around != null && !around.isEmpty()) {
                            for (NpcInstance guard : around) {
                                if (!guard.isMonster() && guard.getNpcId() == GUARD_ID) {
                                    guard.broadcastPacket(new SocialAction(guard.getObjectId(), 4));
                                }
                            }
                        }
                    }
                    break;
                case 1:
                    final int kill = SoDManager.getTiatKills();
                    if (kill == 0) {
                        Functions.npcSay(actor, NpcString.MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_WERE_GATHERING_BRAVE_ADVENTURERS_TO_ATTACK_TIATS_MOUNTED_TROOP_THATS_ROOTED_IN_THE_SEED_OF_DESTRUCTION);
                    } else if (kill > 0 && kill < 9) {
                        Functions.npcSay(actor, NpcString.MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_THE_SEED_OF_DESTRUCTION_IS_CURRENTLY_SECURED_UNDER_THE_FLAG_OF_THE_KEUCEREUS_ALLIANCE);
                    } else if (kill > 9) {
                        Functions.npcSay(actor, NpcString.MESSENGER_INFORM_THE_PATRONS_OF_THE_KEUCEREUS_ALLIANCE_BASE_TIATS_MOUNTED_TROOP_IS_CURRENTLY_TRYING_TO_RETAKE_SEED_OF_DESTRUCTION_COMMIT_ALL_THE_AVAILABLE_REINFORCEMENTS_INTO_SEED_OF_DESTRUCTION);
                        final List<NpcInstance> around = actor.getAroundNpc(1500, 100);
                        if (around != null && !around.isEmpty()) {
                            for (NpcInstance guard : around) {
                                if (!guard.isMonster() && guard.getNpcId() == GUARD_ID) {
                                    guard.broadcastPacket(new SocialAction(guard.getObjectId(), 4));
                                }
                            }
                        }
                    }
                    break;
            }
        }
        return false;
    }
}