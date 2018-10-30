package ru.akumu.smartguard.wrappers;

import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.world.GameObjectsStorage;
import ru.akumu.smartguard.core.wrappers.ISmartPlayer;
import ru.akumu.smartguard.core.wrappers.IWorld;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Akumu
 * @date 27.03.2016 12:16
 */
public class World extends IWorld {
    @Override
    public ISmartPlayer getPlayerByObjId(int i) {
        Player player = GameObjectsStorage.getPlayer(i);
        if (player != null)
            return new SmartPlayer(player);
        return null;
    }

    @Override
    public List<ISmartPlayer> getAllPlayers() {
        return GameObjectsStorage.getPlayers().stream().map(SmartPlayer::new).collect(Collectors.toList());
    }
}
