package org.mmocore.gameserver.handler.userbasicaction.impl;

import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Create by Mangol on 20.10.2015.
 */
public class TRADE implements IUserBasicActionHandler {
    @Override
    public void useAction(final Player player, final int id, final Optional<String> option, final OptionalInt useSkill, final Optional<GameObject> target, final boolean ctrlPressed, final boolean shiftPressed) {
    }
}
