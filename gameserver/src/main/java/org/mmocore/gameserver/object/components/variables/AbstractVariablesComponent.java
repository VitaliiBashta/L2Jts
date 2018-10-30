package org.mmocore.gameserver.object.components.variables;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.database.dao.impl.variables.AbstractVariablesDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Java-man
 */
abstract class AbstractVariablesComponent<T extends Enum<?> & Variables> {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractVariablesComponent.class);

    private final int objectId;
    private final AbstractVariablesDAO variablesDAO;
    private final MultiValueSet<Variables> variables = new MultiValueSet<>();
    private final boolean isCanUseDao;

    protected AbstractVariablesComponent(final int objectId, final AbstractVariablesDAO variablesDAO, boolean isCanUseDao) {
        this.objectId = objectId;
        this.variablesDAO = variablesDAO;
        this.isCanUseDao = isCanUseDao;
        final MultiValueSet<Variables> loadedVariables;
        if (isCanUseDao) {
            loadedVariables = variablesDAO.loadVariables(objectId);
            variables.putAll(loadedVariables);
            loadedVariables.clear();
        }
        onStart();
    }

    protected AbstractVariablesComponent(final int objectId, final AbstractVariablesDAO variablesDAO) {
        this(objectId, variablesDAO, true);
    }

    protected void onStart() {
    }

    public void set(final T variable, final String value, final long expirationTime) {
        variables.put(variable, value);

        if (variable.isKeepInDatabase() && isCanUseDao)
            variablesDAO.save(objectId, variable.name(), value, expirationTime);
    }

    public void set(final T variable, final int value, final long expirationTime) {
        set(variable, String.valueOf(value), expirationTime);
    }

    public void set(final T variable, final long value, final long expirationTime) {
        set(variable, String.valueOf(value), expirationTime);
    }

    public String get(final T variable) {
        return variables.getString(variable, null);
    }

    public boolean getBoolean(final T variable, final boolean defaultVal) {
        final String var = variables.getString(variable, null);

        if (var == null)
            return defaultVal;

        return !("0".equals(var) || "false".equalsIgnoreCase(var));
    }

    public boolean getBoolean(final T variable) {
        final String var = variables.getString(variable, null);
        return !(var == null || "0".equals(var) || "false".equalsIgnoreCase(var));
    }

    public long getLong(final T variable) {
        return getLong(variable, 0L);
    }

    public long getLong(final T variable, final long defaultVal) {
        long result = defaultVal;
        final String var = get(variable);

        if (var != null)
            result = Long.parseLong(var);

        return result;
    }

    public int getInt(final T variable) {
        return getInt(variable, 0);
    }

    public int getInt(final T variable, final int defaultVal) {
        int result = defaultVal;
        final String var = get(variable);

        if (var != null)
            result = Integer.parseInt(var);

        return result;
    }

    public Object remove(final T variable) {
        final Object variableValue = variables.remove(variable);

        if (variableValue != null) {
            if (variable.isKeepInDatabase() && isCanUseDao)
                variablesDAO.remove(objectId, variable.name());

            return variableValue;
        }

        return null;
    }
}
