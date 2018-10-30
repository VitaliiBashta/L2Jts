package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.factory.IObjectFactory;
import org.jts.dataparser.data.holder.userbasicaction.UserBasicAction;
import org.jts.dataparser.data.holder.userbasicaction.UserBasicActionHandlerType;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.handler.userbasicaction.IUserBasicActionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by Mangol on 20.10.2015.
 */
public class UserBasicActionHolder extends AbstractHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserBasicActionHolder.class);
    private static final UserBasicActionHolder ourInstance = new UserBasicActionHolder();
    private static final Map<UserBasicActionHandlerType, IUserBasicActionHandler> handlers = new HashMap<>();
    private static final Pattern useSkillPattern = Pattern.compile("use_skill\\s*=\\s*(\\S*)");
    private static final Pattern handlerPattern = Pattern.compile("handler\\s*=\\s*\\[(\\S*?)]");
    private static final Pattern optionPattern = Pattern.compile("option\\s*=\\s*(\\S*)");
    @Element(start = "action_begin", end = "action_end", objectFactory = UserBasicActionFactory.class)
    private static List<UserBasicAction> actionList;
    private final Map<Integer, UserBasicAction> actionMap = new HashMap<>();
    private int[] actionIds;

    public static UserBasicActionHolder getInstance() {
        return ourInstance;
    }

    private static Optional<IUserBasicActionHandler> getHandlerClass(final UserBasicActionHandlerType type) {
        try {
            if (handlers.containsKey(type)) {
                return Optional.of(handlers.get(type));
            }
            final Class<?> aClass = Class.forName("org.mmocore.gameserver.handler.userbasicaction.impl." + type.name());
            final IUserBasicActionHandler handler = (IUserBasicActionHandler) aClass.newInstance();
            handlers.put(type, handler);
            return Optional.of(handler);
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public void afterParsing() {
        actionList.stream().forEach(a -> actionMap.put(a.getId(), a));
        actionList.clear();
        handlers.clear();
        actionIds = actionMap.keySet().stream().mapToInt(Integer::intValue).toArray();
    }

    public Optional<UserBasicAction> getAction(final int id) {
        return Optional.ofNullable(actionMap.get(id));
    }

    public int[] getActionIds() {
        return actionIds;
    }

    @Override
    public int size() {
        return actionMap.size();
    }

    @Override
    public void clear() {
    }

    public static class UserBasicActionFactory implements IObjectFactory<UserBasicAction> {
        private Class<?> clazz;

        @Override
        public UserBasicAction createObjectFor(final StringBuilder data) throws IllegalAccessException, InstantiationException, NoSuchFieldException {
            final UserBasicAction action = (UserBasicAction) clazz.newInstance();
            Optional<IUserBasicActionHandler> handler;
            Matcher matcher = handlerPattern.matcher(data);
            if (matcher.find()) {
                final UserBasicActionHandlerType type = UserBasicActionHandlerType.valueOf(matcher.group(1));
                handler = getHandlerClass(type);
                if (!handler.isPresent()) {
                    LOGGER.warn("handler type isNull " + type.name());
                }
                action.handler = Optional.of(handler.get());
            }
            matcher = optionPattern.matcher(data);
            if (matcher.find()) {
                action.option = Optional.of(matcher.group(1));
            }
            matcher = useSkillPattern.matcher(data);
            if (matcher.find()) {
                action.use_skill = OptionalInt.of(Integer.parseInt(matcher.group(1)));
            }
            return action;
        }

        @Override
        public void setFieldClass(Class<?> clazz) {
            this.clazz = clazz;
        }
    }
}
