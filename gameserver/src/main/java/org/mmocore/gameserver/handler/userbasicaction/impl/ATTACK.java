package org.mmocore.gameserver.handler.userbasicaction.impl;

import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Create by Mangol on 20.10.2015.
 */
public class ATTACK implements IUserBasicActionHandler {
    @Override
    public void useAction(Player player, int id, Optional<String> option, final OptionalInt useSkill, Optional<GameObject> target, boolean ctrlPressed, boolean shiftPressed) {
    }
}
