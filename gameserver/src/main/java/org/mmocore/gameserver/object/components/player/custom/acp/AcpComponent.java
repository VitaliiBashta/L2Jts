package org.mmocore.gameserver.object.components.player.custom.acp;

import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.database.dao.impl.custom.AcpDAO;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.actor.OnReviveListener;
import org.mmocore.gameserver.listener.actor.player.*;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.player.custom.acp.action.CpAction;
import org.mmocore.gameserver.object.components.player.custom.acp.action.HpAction;
import org.mmocore.gameserver.object.components.player.custom.acp.action.MpAction;
import org.mmocore.gameserver.object.components.player.custom.acp.action.SmallCpAction;
import org.mmocore.gameserver.object.components.player.custom.acp.action.interfaces.IUseAction;
import org.mmocore.gameserver.object.components.player.custom.acp.task.ActionTask;
import org.mmocore.gameserver.templates.custom.AcpTemplate;

import java.util.concurrent.ScheduledFuture;

/**
 * Create by Mangol on 30.12.2015.
 */
public class AcpComponent {
    private static final AcpDAO dao = AcpDAO.getInstance();
    private final Player player;
    private AcpTemplate template;
    private ScheduledFuture<?> autoSmallCpTask = null;
    private ScheduledFuture<?> autoCpTask = null;
    private ScheduledFuture<?> autoHpTask = null;
    private ScheduledFuture<?> autoMpTask = null;
    private IUseAction actionSmallCp;
    private IUseAction actionCp;
    private IUseAction actionHp;
    private IUseAction actionMp;

    public AcpComponent(final Player player) {
        this.player = player;
        this.player.addListener(new OnPlayerEnterListenerImpl());
    }

    public void restore() {
        final AcpTemplate restoreTemplate = dao.selectAcp(getPlayer());
        if (restoreTemplate != null) {
            template = restoreTemplate;
        } else {
            template = new AcpTemplate();
        }
    }

    private void startTask() {
        startAndStopAutoSmallCp(getTemplate().isAutoSmallCp());
        startAndStopAutoCp(getTemplate().isAutoCp());
        startAndStopAutoHp(getTemplate().isAutoHp());
        startAndStopAutoMp(getTemplate().isAutoMp());
    }

    public void startAndStopAutoSmallCp(boolean autoSmallCp) {
        if (autoSmallCp) {
            if (getPlayer().getCurrentCpPercents() <= getTemplate().getCpPercent()) {
                if (actionSmallCp == null) {
                    actionSmallCp = new SmallCpAction(this);
                }
                if (autoSmallCpTask != null) {
                    autoSmallCpTask.cancel(true);
                    autoSmallCpTask = null;
                }
                autoSmallCpTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ActionTask(actionSmallCp), 1000L, getTemplate().getReuseSmallCp());
            }
            return;
        }
        stopAutoSmallCp();
    }

    public void startAndStopAutoCp(final boolean autoCp) {
        if (autoCp) {
            if (getPlayer().getCurrentCpPercents() <= getTemplate().getCpPercent()) {
                if (actionCp == null) {
                    actionCp = new CpAction(this);
                }
                if (autoCpTask != null) {
                    autoCpTask.cancel(true);
                    autoCpTask = null;
                }
                autoCpTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ActionTask(actionCp), 1000L, getTemplate().getReuseCp());
            }
            return;
        }
        stopAutoCp();
    }

    public void startAndStopAutoHp(final boolean autoHp) {
        if (autoHp) {
            if (getPlayer().getCurrentHpPercents() <= getTemplate().getHpPercent()) {
                if (actionHp == null) {
                    actionHp = new HpAction(this);
                }
                if (autoHpTask != null) {
                    autoHpTask.cancel(true);
                    autoHpTask = null;
                }
                autoHpTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ActionTask(actionHp), 1000L, getTemplate().getReuseHp());
            }
            return;
        }
        stopAutoHp();
    }

    public void startAndStopAutoMp(final boolean autoMp) {
        if (autoMp) {
            if (getPlayer().getCurrentMpPercents() <= getTemplate().getMpPercent()) {
                if (actionMp == null) {
                    actionMp = new MpAction(this);
                }
                if (autoMpTask != null) {
                    autoMpTask.cancel(true);
                    autoMpTask = null;
                }
                autoMpTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new ActionTask(actionMp), 1000L, getTemplate().getReuseMp());
            }
            return;
        }
        stopAutoMp();
    }

    public void stopAllTask() {
        stopAutoSmallCp();
        stopAutoCp();
        stopAutoHp();
        stopAutoMp();
    }

    public void stopAutoSmallCp() {
        if (autoSmallCpTask != null) {
            autoSmallCpTask.cancel(true);
            autoSmallCpTask = null;
        }
        if (actionSmallCp != null) {
            actionSmallCp = null;
        }
    }

    public void stopAutoCp() {
        if (autoCpTask != null) {
            autoCpTask.cancel(true);
            autoCpTask = null;
        }
        if (actionCp != null) {
            actionCp = null;
        }
    }

    public void stopAutoHp() {
        if (autoHpTask != null) {
            autoHpTask.cancel(true);
            autoHpTask = null;
        }
        if (actionHp != null) {
            actionHp = null;
        }
    }

    public void stopAutoMp() {
        if (autoMpTask != null) {
            autoMpTask.cancel(true);
            autoMpTask = null;
        }
        if (actionMp != null) {
            actionMp = null;
        }
    }

    public AcpTemplate getTemplate() {
        return template;
    }

    public Player getPlayer() {
        return player;
    }

    private class OnPlayerEnterListenerImpl implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(final Player player) {
            if (player == null) {
                return;
            }
            restore();
            getPlayer().addListener(new onDeathListenerImpl());
            getPlayer().addListener(new OnReviveListenerImpl());
            getPlayer().addListener(new onPlayerCurrentCpImpl());
            getPlayer().addListener(new onPlayerCurrentHpImpl());
            getPlayer().addListener(new onPlayerCurrentMpImpl());
            getPlayer().addListener(new onPlayerAddItemImpl());
            getPlayer().addListener(new onPlayerExitImpl());
            startTask();
        }
    }

    private class onPlayerAddItemImpl implements OnPlayerAddItemListener {
        @Override
        public void onPlayerAddItem(final Player player, final ItemInstance itemInstance) {
            if (player == null) {
                return;
            }
            if (getTemplate().isAutoCp()) {
                if (autoCpTask == null) {
                    if (itemInstance.getItemId() == getTemplate().getCpItemId()) {
                        startAndStopAutoCp(getTemplate().isAutoCp());
                    }
                }
            }
            if (getTemplate().isAutoSmallCp()) {
                if (autoSmallCpTask == null) {
                    if (itemInstance.getItemId() == getTemplate().getSmallCpItemId()) {
                        startAndStopAutoSmallCp(getTemplate().isAutoSmallCp());
                    }
                }
            }
            if (getTemplate().isAutoHp()) {
                if (autoHpTask == null) {
                    if (itemInstance.getItemId() == getTemplate().getHpItemId()) {
                        startAndStopAutoHp(getTemplate().isAutoHp());
                    }
                }
            }
            if (getTemplate().isAutoMp()) {
                if (autoMpTask == null) {
                    if (itemInstance.getItemId() == getTemplate().getMpItemId()) {
                        startAndStopAutoMp(getTemplate().isAutoMp());
                    }
                }
            }
        }
    }

    private class onPlayerExitImpl implements OnPlayerExitListener {
        @Override
        public void onPlayerExit(final Player player) {
            if (player == null) {
                return;
            }
            dao.insertUpdateAcp(getPlayer(), getTemplate());
        }
    }

    private class onPlayerCurrentCpImpl implements OnPlayerCurrentCpListener {
        @Override
        public void onPlayerCurrentCp(final Player player, final double currentCp, final double currentCpPercents) {
            if (player == null) {
                return;
            }
            if (autoCpTask == null) {
                startAndStopAutoCp(getTemplate().isAutoCp());
            }
            if (autoSmallCpTask == null) {
                startAndStopAutoSmallCp(getTemplate().isAutoSmallCp());
            }
        }
    }

    private class onPlayerCurrentHpImpl implements OnPlayerCurrentHpListener {
        @Override
        public void onPlayerCurrentHp(final Player player, final double currentHp, final double currentHpPercents) {
            if (player == null) {
                return;
            }
            if (autoHpTask != null) {
                return;
            }
            startAndStopAutoHp(getTemplate().isAutoHp());
        }
    }

    private class onPlayerCurrentMpImpl implements OnPlayerCurrentMpListener {
        @Override
        public void onPlayerCurrentMp(final Player player, final double currentMp, final double currentMpPercents) {
            if (player == null) {
                return;
            }
            if (autoMpTask != null) {
                return;
            }
            startAndStopAutoMp(getTemplate().isAutoMp());
        }
    }

    private class onDeathListenerImpl implements OnDeathListener {
        @Override
        public void onDeath(final Creature self, final Creature killer) {
            if (self == null) {
                return;
            }
            stopAllTask();
        }
    }

    private class OnReviveListenerImpl implements OnReviveListener {
        @Override
        public void onRevive(final Creature actor) {
            if (actor == null) {
                return;
            }
            startTask();
        }
    }
}
