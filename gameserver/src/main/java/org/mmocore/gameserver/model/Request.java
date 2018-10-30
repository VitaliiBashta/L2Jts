package org.mmocore.gameserver.model;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.lang.reference.HardReference;
import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;

import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Класс обмена между игроками запросами и ответами.
 */
public class Request extends MultiValueSet<String> {
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger nextId = new AtomicInteger();
    private final int id;
    private final L2RequestType type;
    private final HardReference<Player> requester;
    private final HardReference<Player> receiver;
    private boolean isRequesterConfirmed;
    private boolean isReceiverConfirmed;
    private boolean isCancelled;
    private boolean isDone;
    private long timeout;
    private Future<?> timeoutTask;
    /**
     * Создает запрос
     */
    public Request(final L2RequestType type, final Player requester, final Player receiver) {
        id = nextId.incrementAndGet();
        this.requester = requester.getRef();
        this.receiver = receiver.getRef();
        this.type = type;
        requester.setRequest(this);
        receiver.setRequest(this);
    }

    public Request setTimeout(final long timeout) {
        this.timeout = timeout > 0 ? System.currentTimeMillis() + timeout : 0;
        timeoutTask = ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
            @Override
            public void runImpl() throws Exception {
                timeout();
            }
        }, timeout);
        return this;
    }

    public int getId() {
        return id;
    }

    /**
     * Отменяет запрос и очищает соответствующее поле у участников.
     */
    public void cancel() {
        isCancelled = true;
        if (timeoutTask != null) {
            timeoutTask.cancel(false);
        }
        timeoutTask = null;
        Player player = getRequester();
        if (player != null && player.getRequest() == this) {
            player.setRequest(null);
        }
        player = getReceiver();
        if (player != null && player.getRequest() == this) {
            player.setRequest(null);
        }
    }

    /**
     * Заканчивает запрос и очищает соответствующее поле у участников.
     */
    public void done() {
        isDone = true;
        if (timeoutTask != null) {
            timeoutTask.cancel(false);
        }
        timeoutTask = null;
        Player player = getRequester();
        if (player != null && player.getRequest() == this) {
            player.setRequest(null);
        }
        player = getReceiver();
        if (player != null && player.getRequest() == this) {
            player.setRequest(null);
        }
    }

    /**
     * Действие при таймауте.
     */
    public void timeout() {
        final Player player = getReceiver();
        if (player != null) {
            if (player.getRequest() == this) {
                player.sendPacket(SystemMsg.TIME_EXPIRED);
            }
        }
        cancel();
    }

    public Player getOtherPlayer(final Player player) {
        if (player == getRequester()) {
            return getReceiver();
        }
        if (player == getReceiver()) {
            return getRequester();
        }
        return null;
    }

    public Player getRequester() {
        return requester.get();
    }

    public Player getReceiver() {
        return receiver.get();
    }

    /**
     * Проверяет не просрочен ли запрос.
     */
    public boolean isInProgress() {
        if (isCancelled) {
            return false;
        }
        if (isDone) {
            return false;
        }
        if (timeout == 0) {
            return true;
        }
        if (timeout > System.currentTimeMillis()) {
            return true;
        }
        return false;
    }

    /**
     * Проверяет тип запроса.
     */
    public boolean isTypeOf(final L2RequestType type) {
        return this.type == type;
    }

    /**
     * Помечает участника как согласившегося.
     */
    public void confirm(final Player player) {
        if (player == getRequester()) {
            isRequesterConfirmed = true;
        } else if (player == getReceiver()) {
            isReceiverConfirmed = true;
        }
    }

    /**
     * Проверяет согласился ли игрок с запросом.
     */
    public boolean isConfirmed(final Player player) {
        if (player == getRequester()) {
            return isRequesterConfirmed;
        } else if (player == getReceiver()) {
            return isReceiverConfirmed;
        }
        return false; // WTF???
    }

    public enum L2RequestType {
        CUSTOM,
        PARTY,
        PARTY_ROOM,
        CLAN,
        ALLY,
        TRADE,
        TRADE_REQUEST,
        FRIEND,
        CHANNEL,
        DUEL,
        COUPLE_ACTION
    }
}