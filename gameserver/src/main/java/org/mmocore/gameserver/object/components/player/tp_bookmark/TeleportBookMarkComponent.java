package org.mmocore.gameserver.object.components.player.tp_bookmark;

import org.mmocore.gameserver.database.dao.impl.CharacterTeleportBookmarkDAO;
import org.mmocore.gameserver.object.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * @author KilRoy
 */
public class TeleportBookMarkComponent {
    public static final String TELEPORT_BOOKMARK = "teleport_bookmark";
    public static final int MAX_TELEPORT_BOOKMARK_SIZE = 9;
    private static final Logger logger = LoggerFactory.getLogger(TeleportBookMarkComponent.class);
    private final Player playerRef;
    private int _tpBookmarkSize;
    private List<TeleportBookMark> _tpBookMarks = Collections.emptyList();
    private TeleportBookMark activatedTeleportBookMark;

    public TeleportBookMarkComponent(final Player player) {
        playerRef = player;
    }

    public Player getPlayer() {
        return playerRef;
    }

    public List<TeleportBookMark> getTpBookMarks() {
        return _tpBookMarks;
    }

    public int getTpBookmarkSize() {
        return _tpBookmarkSize;
    }

    public void setTpBookmarkSize(int teleportBookmarkSize) {
        _tpBookmarkSize = teleportBookmarkSize;
    }

    public TeleportBookMark getActivatedTeleportBookMark() {
        return activatedTeleportBookMark;
    }

    public void setActivatedTeleportBookMark(final TeleportBookMark activatedTeleportBookMark) {
        this.activatedTeleportBookMark = activatedTeleportBookMark;
    }

    public void restoreMarks() {
        try {
            _tpBookMarks = CharacterTeleportBookmarkDAO.getInstance().select(getPlayer());
        } catch (Exception e) {
            logger.warn("TpBookMarkSystem: error in select section from playerID: " + getPlayer().getObjectId());
        }
    }
}