package org.jts.dataparser.data.holder.userbasicaction;

import org.jts.dataparser.data.annotations.value.IntValue;
import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * Create by Mangol on 20.10.2015.
 */
public class UserBasicAction {
    @IntValue
    public int id;
    public OptionalInt use_skill = OptionalInt.empty();
    public Optional<IUserBasicActionHandler> handler = Optional.empty();
    public Optional<String> option = Optional.empty();

    public int getId() {
        return id;
    }

    public OptionalInt getUseSkill() {
        return use_skill;
    }

    public Optional<IUserBasicActionHandler> getHandler() {
        return handler;
    }

    public Optional<String> getOption() {
        return option;
    }
}
