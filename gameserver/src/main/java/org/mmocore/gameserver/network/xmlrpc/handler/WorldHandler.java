package org.mmocore.gameserver.network.xmlrpc.handler;

import org.mmocore.commons.jdbchelper.NoResultException;
import org.mmocore.commons.net.xmlrpc.handler.Handler;
import org.mmocore.gameserver.world.GameObjectsStorage;

/**
 * @author KilRoy
 */
public class WorldHandler extends Handler {
    public String getWorldOnline() {
        String response;
        try {
            response = String.valueOf(GameObjectsStorage.getAllPlayersSize());
        } catch (NoResultException nre) {
            response = NOT_FIND;
        } catch (Exception e) {
            LOGGER.error("WorldHandler.getWorldOnline(String) return exception: ", e);
            response = ERROR;
        }
        return response;
    }
}