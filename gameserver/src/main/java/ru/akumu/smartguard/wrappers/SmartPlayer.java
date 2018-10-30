package ru.akumu.smartguard.wrappers;

import org.mmocore.gameserver.object.Player;
import ru.akumu.smartguard.core.wrappers.ISmartClient;
import ru.akumu.smartguard.core.wrappers.ISmartPlayer;

import java.security.InvalidParameterException;

/**
 * @author Akumu
 * @date 27.03.2016 12:23
 */
public class SmartPlayer extends ISmartPlayer {
    Player player;

    public SmartPlayer(Player player) {
        this(new SmartClient(player.getNetConnection()), player);
    }

    public SmartPlayer(ISmartClient client, Player player) {
        super(client);

        if (player == null)
            throw new InvalidParameterException();

        this.player = player;
    }

    @Override
    public boolean isAdmin() {
        return player.isGM();
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public int getObjId() {
        return player.getObjectId();
    }
}
