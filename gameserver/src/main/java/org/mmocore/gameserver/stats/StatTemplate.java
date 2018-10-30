package org.mmocore.gameserver.stats;

import org.mmocore.commons.lang.ArrayUtils;
import org.mmocore.gameserver.stats.funcs.Func;
import org.mmocore.gameserver.stats.funcs.FuncTemplate;
import org.mmocore.gameserver.stats.triggers.TriggerInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author VISTALL
 * @date 23:05/22.01.2011
 */
public class StatTemplate {
    protected FuncTemplate[] _funcTemplates = FuncTemplate.EMPTY_ARRAY;
    protected List<TriggerInfo> _triggerList = Collections.emptyList();

    public List<TriggerInfo> getTriggerList() {
        return _triggerList;
    }

    public void addTrigger(final TriggerInfo f) {
        if (_triggerList.isEmpty()) {
            _triggerList = new ArrayList<>(4);
        }
        _triggerList.add(f);
    }

    public void attachFunc(final FuncTemplate f) {
        _funcTemplates = ArrayUtils.add(_funcTemplates, f);
    }

    public FuncTemplate[] getAttachedFuncs() {
        return _funcTemplates;
    }

    public Func[] getStatFuncs(final Object owner) {
        if (_funcTemplates.length == 0) {
            return Func.EMPTY_FUNC_ARRAY;
        }

        final Func[] funcs = new Func[_funcTemplates.length];
        for (int i = 0; i < funcs.length; i++) {
            funcs[i] = _funcTemplates[i].getFunc(owner);
        }
        return funcs;
    }
}
