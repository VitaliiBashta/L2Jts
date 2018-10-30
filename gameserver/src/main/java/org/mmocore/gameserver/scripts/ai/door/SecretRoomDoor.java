package org.mmocore.gameserver.scripts.ai.door;

import org.mmocore.gameserver.ai.DoorAI;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.object.Player;

/**
 * Created by [STIGMATED] : 02.08.12 : 15:12
 * GW2World
 */
public class SecretRoomDoor extends DoorAI {

    public SecretRoomDoor(DoorInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtTwiceClick(final Player player) {
        final DoorInstance door = getActor();
        switch (door.getDoorId()) {
            case 23150001: {
                _log.info(player.getName() + '\t' + door.getDoorId());
            }
            break;
            case 23150002: {
                _log.info(player.getName() + '\t' + door.getDoorId());
            }
            break;
        }
    }
}
