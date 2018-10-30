package org.mmocore.gameserver.scripts.ai.pts;

import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.serverpackets.MagicSkillUse;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.NpcUtils;
import org.mmocore.gameserver.utils.version.Chronicle;
import org.mmocore.gameserver.utils.version.ChronicleCheck;
import org.mmocore.gameserver.world.World;

/**
 * @author KilRoy
 */
@ChronicleCheck(Chronicle.HIGH_FIVE)
public class event_jackpot_bug extends DefaultAI {
    private static final int s_display_jackpot_firework = 5758;
    private static final int s_g_display_luckpi_a = 23325;
    private static final int s_g_display_luckpi_b = 23326;
    private static final int s_dispaly_soul_unleash1 = 6037;
    private static final int RandRate = 10;
    private int i_ai0;
    private int i_ai1;
    private int i_ai4;
    private int i_ai5;
    private boolean targeted;

    public event_jackpot_bug(NpcInstance actor) {
        super(actor);
    }

    private void seeItem() {
        if (i_ai4 == 0) {
            return;
        }

        final NpcInstance actor = getActor();
        if (actor != null && !actor.isDead()) {
            ItemInstance closestItem = null;
            for (GameObject obj : World.getAroundObjects(actor, 500, 200)) {
                if (obj.isItem() && ((ItemInstance) obj).getItemId() == ItemTemplate.ITEM_ID_ADENA) {
                    closestItem = (ItemInstance) obj;
                }
            }

            if (closestItem != null) {
                actor.moveToLocation(closestItem.getLoc(), 0, true);
            }
        }
    }

    @Override
    protected void onEvtArrived() {
        super.onEvtArrived();
        NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        ItemInstance closestItem = null;
        for (GameObject obj : World.getAroundObjects(actor, 30, 200)) {
            if (obj.isItem() && ((ItemInstance) obj).getItemId() == ItemTemplate.ITEM_ID_ADENA) {
                closestItem = (ItemInstance) obj;
            }
        }
        if (closestItem != null && closestItem.getCount() >= 1) {
            i_ai0 = i_ai0 + 1;
            if (i_ai0 >= 10) {
                ChatUtils.say(actor, NpcString.LUCKY_I_M_FULL_THANKS_FOR_THE_YUMMY_ADENA_OH_I_M_SO_HEAVY);
                actor.doDie(actor);
                actor.deleteMe();
                if (i_ai4 > 0) {
                    if (i_ai1 >= 5) {
                        NpcUtils.createOnePrivateEx(2503, getActor().getX(), getActor().getY(), getActor().getZ() + 20, i_ai1, getActor().getLevel());
                    } else if (i_ai1 >= 2 && i_ai1 < 5) {
                        NpcUtils.createOnePrivateEx(2502, getActor().getX(), getActor().getY(), getActor().getZ() + 20, i_ai1, getActor().getLevel());
                    } else {
                        NpcUtils.createOnePrivateEx(2502, getActor().getX(), getActor().getY(), getActor().getZ() + 20, i_ai1, getActor().getLevel());
                    }
                }
            } else {
                final int i0 = Rnd.get(100);
                if (i0 < 50) {
                    actor.broadcastPacket(new MagicSkillUse(actor, actor, s_display_jackpot_firework, 1, 0, 0));
                    i_ai1++;
                    ChatUtils.say(actor, NpcString.YUMMY_THANKS_LUCKY);
                    if (i_ai1 >= 2 && i_ai1 < 5) {
                        if (i_ai5 == 0) {
                            actor.broadcastPacket(new MagicSkillUse(actor, actor, s_g_display_luckpi_a, 1, 0, 0));
                            i_ai5 = 1;
                        }
                    } else if (i_ai1 >= 5) {
                        if (i_ai5 == 1) {
                            actor.broadcastPacket(new MagicSkillUse(actor, actor, s_g_display_luckpi_a, 1, 0, 0));
                            i_ai5 = 2;
                        }
                    }
                } else if (i0 < 99) {
                    actor.broadcastPacket(new MagicSkillUse(actor, actor, s_dispaly_soul_unleash1, 1, 0, 0));
                    i_ai1--;
                    if (i_ai1 < 0)
                        i_ai1 = 0;
                    ChatUtils.say(actor, NpcString.GRRRR_YUCK);
                    if (i_ai1 >= 2 && i_ai1 < 5) {
                        if (i_ai5 == 2) {
                            actor.broadcastPacket(new MagicSkillUse(actor, actor, s_g_display_luckpi_b, 1, 0, 0));
                            i_ai5 = 1;
                        }
                    } else if (i_ai1 >= 5) {
                    } else if (i_ai5 == 1) {
                        actor.broadcastPacket(new MagicSkillUse(actor, actor, s_g_display_luckpi_b, 1, 0, 0));
                        i_ai5 = 0;
                    }
                }
            }
            closestItem.deleteMe();
        }
        if (!targeted) {
            if (Rnd.chance(RandRate)) {
                for (final Creature creature : actor.getAroundCharacters(500, 200)) {
                    if (creature.isPlayer() && !targeted) {
                        targeted = true;
                        actor.setFollowTarget(creature);
                        AddTimerEx(1003, 15000);
                        break;
                    }
                }

            }
        }
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        i_ai0 = 0;
        i_ai1 = 0;
        i_ai4 = 0;
        i_ai5 = 0;
        targeted = false;
        AddTimerEx(47188, 1000);
    }

    @Override
    protected void onEvtTimerFiredEx(int timer_id, Object arg1, Object arg2) {
        final NpcInstance actor = getActor();
        if (actor != null && !actor.isDead()) {
            if (timer_id == 47188) {
                AddTimerEx(2222, 5000);
                AddTimerEx(47188, 60 * 1000);
            } else if (timer_id == 2222) {
                i_ai4 = 1;
                ChatUtils.shout(actor, NpcString.LUCKY_I_M_LUCKY_THE_SPIRIT_THAT_LOVES_ADENA);
                addTaskMove(Location.findAroundPosition(actor, 100, 200), true);
                AddTimerEx(7777, 1000 * 10);
                AddTimerEx(7778, 1000 * 10);
                AddTimerEx(7779, (1000 * 60) * 10);
            } else if (timer_id == 7777) {
                seeItem();
                AddTimerEx(7777, 10 * 1000);
            } else if (timer_id == 7778) {
                ChatUtils.say(actor, NpcString.LUCKY_I_WANT_TO_EAT_ADENA_GIVE_IT_TO_ME);
            } else if (timer_id == 7779) {
                actor.deleteMe();
            } else if (timer_id == 1003) {
                targeted = false;
            }
        }
    }

    @Override
    protected void onEvtMenuSelected(final Player player, final int ask, final int reply) {
        final NpcInstance actor = getActor();
        if (actor == null) {
            return;
        }

        if (ask == -3000) {
            if (reply == 1 && i_ai0 >= 3) {
                ChatUtils.say(actor, NpcString.LUCKY_NO_MORE_ADENA_OH_I_M_SO_HEAVY);
                actor.doDie(actor);
                actor.deleteMe();
                if (i_ai4 > 0) {
                    if (i_ai1 >= 5) {
                        NpcUtils.createOnePrivateEx(2503, getActor().getX(), getActor().getY(), getActor().getZ() + 20, i_ai1, getActor().getLevel());
                    } else if (i_ai1 >= 2 && i_ai1 < 5) {
                        NpcUtils.createOnePrivateEx(2502, getActor().getX(), getActor().getY(), getActor().getZ() + 20, i_ai1, getActor().getLevel());
                    } else {
                        NpcUtils.createOnePrivateEx(2502, getActor().getX(), getActor().getY(), getActor().getZ() + 20, i_ai1, getActor().getLevel());
                    }
                }
            } else {
                actor.showChatWindow(player, "pts/default/luckpi_003.htm");
            }
        }
    }

    @Override
    protected void onEvtDead(Creature killer) {

        super.onEvtDead(killer);
        getActor().endDecayTask();
    }

    @Override
    protected void onEvtAttacked(final Creature attacker, final SkillEntry skill, final int damage) {
    }

    @Override
    protected void onEvtAggression(Creature attacker, int aggro) {
    }

    @Override
    protected boolean randomWalk() {
        return true;
    }
}