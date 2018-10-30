package org.mmocore.gameserver.scripts.ai.fantasy_island;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.ai.DefaultAI;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.utils.ChatUtils;
import org.mmocore.gameserver.utils.Location;

/**
 * @author PaInKiLlEr - AI для MC (32433). - Кричит в чат. - AI проверен и работает.
 */
public class MCManager extends DefaultAI {
    public MCManager(final NpcInstance actor) {
        super(actor);
    }

    @Override
    protected void onEvtSpawn() {
        final NpcInstance actor = getActor();
        if (actor == null)
            return;

        ChatUtils.shout(actor, NpcString.HOW_COME_PEOPLE_ARE_NOT_HERE_WE_ARE_ABOUT_TO_START_THE_SHOW_HMM);
        ThreadPoolManager.getInstance().schedule(new ScheduleStart(1, actor), 30000);
        super.onEvtSpawn();
    }

    @Override
    protected boolean randomWalk() {
        return false;
    }

    @Override
    protected boolean randomAnimation() {
        return false;
    }

    private class ScheduleStart extends RunnableImpl {
        private int _taskId;
        private NpcInstance _actor;

        public ScheduleStart(int taskId, final NpcInstance actor) {
            _taskId = taskId;
            _actor = actor;
        }

        @Override
        public void runImpl() {
            switch (_taskId) {
                case 1:
                    ChatUtils.shout(_actor, NpcString.UGH_I_HAVE_BUTTERFILIES_IN_MY_STOMATCH_THE_SHOW_STARTS_SOON);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(2, _actor), 1000);
                    break;
                case 2:
                    ChatUtils.shout(_actor, NpcString.THANK_YOU_ALL_FOR_COMING_HERE_TONIGHT);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(3, _actor), 6000);
                    break;
                case 3:
                    ChatUtils.shout(_actor, NpcString.IT_IS_AN_HONOR_TO_HAVE_THE_SPECIAL_SHOW_TODAY);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(4, _actor), 4000);
                    break;
                case 4:
                    ChatUtils.shout(_actor, NpcString.FANTASY_ISLE_IS_FULLY_COMITTED_TO_YOUR_HAPPINESS);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(5, _actor), 5000);
                    break;
                case 5:
                    ChatUtils.shout(_actor, NpcString.NOW_ID_LIKE_TO_INTRODUCE_THE_MOST_BEAUTIFUL_SINGER_IN_ADEN_PLEASE_WELCOME_LEYLA_MIRA);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(6, _actor), 3000);
                    addTaskMove(new Location(-56511, -56647, -2008), true);
                    doTask();
                    break;
                case 6:
                    ChatUtils.shout(_actor, NpcString.HERE_SHE_COMES);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(7, _actor), 220000);
                    break;
                case 7:
                    ChatUtils.shout(_actor, NpcString.THANK_YOU_VERY_MUCH_LEYLA);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(8, _actor), 12000);
                    addTaskMove(new Location(-56698, -56430, -2008), true);
                    doTask();
                    break;
                case 8:
                    ChatUtils.shout(_actor, NpcString.JUST_BACK_FROM_THEIR_WORLD_TOUR_PUT_YOUR_HANDS_TOGETHER_FOR_THE_FANTAST_ISLE_CIRCUS);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(9, _actor), 3000);
                    addTaskMove(new Location(-56511, -56647, -2008), true);
                    doTask();
                    break;
                case 9:
                    ChatUtils.shout(_actor, NpcString.COME_ON_EVERYONE);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(10, _actor), 102000);
                    break;
                case 10:
                    ChatUtils.shout(_actor, NpcString.DID_YOU_lIKE_IT_THAT_WAS_SO_AMAZING);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(11, _actor), 5000);
                    addTaskMove(new Location(-56698, -56430, -2008), true);
                    doTask();
                    break;
                case 11:
                    ChatUtils.shout(_actor, NpcString.NOW_WE_ALSO_INVITED_INDIVIDUALS_WITH_SPECIAL_TALENTS);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(12, _actor), 3000);
                    break;
                case 12:
                    ChatUtils.shout(_actor, NpcString.LETS_WELCOME_THE_FIRST_PERSON_HERE);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(13, _actor), 3000);
                    break;
                case 13:
                    ChatUtils.shout(_actor, NpcString.OH);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(14, _actor), 2000);
                    break;
                case 14:
                    ChatUtils.shout(_actor, NpcString.OKAY_NOW_HERE_CONES_THE_NEXT_PERSON_COME_ON_UP_PLEASE);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(15, _actor), 1000);
                    break;
                case 15:
                    ChatUtils.shout(_actor, NpcString.OH_IT_LOOKS_LIKE_SOMETHING_GREAT_IS_GOING_TO_HAPPEN_RIGHT);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(16, _actor), 2000);
                    break;
                case 16:
                    ChatUtils.shout(_actor, NpcString.OH_MY);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(17, _actor), 2000);
                    break;
                case 17:
                    ChatUtils.shout(_actor, NpcString.THATS_GREAT_NOW_HERE_CONES_THE_LAST_PERSON);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(18, _actor), 3000);
                    break;
                case 18:
                    ChatUtils.shout(_actor, NpcString.NOW_THIS_IS_THE_END_OF_TODAY_SHOW);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(19, _actor), 5000);
                    break;
                case 19:
                    ChatUtils.shout(_actor, NpcString.HOW_WAS_IT_I_HOPE_YOU_ALL_ENJOYED_IT);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(20, _actor), 10000);
                    addTaskMove(new Location(-56698, -56340, -2008), true);
                    doTask();
                    break;
                case 20:
                    ChatUtils.shout(_actor, NpcString.PLEASE_REMEMBER_THAT_FANTASY_ISLE_IS_ALWAYS_PLANNING_A_LOT_OF_GREAT_SHOWS_FOR_YOU);
                    ThreadPoolManager.getInstance().schedule(new ScheduleStart(21, _actor), 10000);
                    break;
                case 21:
                    ChatUtils.shout(_actor, NpcString.WELL_I_WISH_I_COULD_CONTINUE_ALL_NIGHT_LONG_BUT_THIS_IS_IT_FOR_TODAY_THANK_YOU);
                    break;
            }
        }
    }
}