package org.mmocore.gameserver.object.components.player.tasks;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.database.dao.impl.CharacterServitorDAO;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.utils.Location;

import java.util.List;

/**
 * @author VISTALL
 * @date 14:48/19.09.2011
 */
public class ServitorSummonTask extends RunnableImpl {
    private final Player _player;

    public ServitorSummonTask(Player player) {
        _player = player;
    }

    @Override
    public void runImpl() {
        List<int[]> saveServitors = _player.getSavedServitors();
        CharacterServitorDAO.getInstance().delete(_player.getObjectId());

        for (int[] ar : saveServitors) {
            switch (ar[0]) {
                case Servitor.PET_TYPE:
                    ItemInstance item = _player.getInventory().getItemByObjectId(ar[1]);
                    if (item == null) {
                        continue;
                    }

                    _player.summonPet(item, Location.findPointToStay(_player, 10, 20));
                    break;
                case Servitor.SUMMON_TYPE:
                    _player.summonSummon(ar[1]);
                    break;
            }
        }
    }
}
