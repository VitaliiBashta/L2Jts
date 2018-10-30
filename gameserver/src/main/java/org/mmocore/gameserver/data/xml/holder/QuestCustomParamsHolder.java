package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.templates.QuestCustomParams;

import java.util.Optional;

/**
 * Created by Hack
 * Date: 06.06.2016 20:09
 */
public class QuestCustomParamsHolder extends AbstractHolder {
    public static QuestCustomParamsHolder instance;
    private final TIntObjectHashMap<QuestCustomParams> holder = new TIntObjectHashMap<>();

    public static QuestCustomParamsHolder getInstance() {
        return instance == null ? instance = new QuestCustomParamsHolder() : instance;
    }

    public void add(QuestCustomParams params) {
        holder.put(params.getId(), params);
    }

    public Optional<QuestCustomParams> get(int questId) {
        return Optional.ofNullable(holder.get(questId));
    }

    public TIntObjectHashMap<QuestCustomParams> getHolder() {
        return holder;
    }

    public int size() {
        return holder.size();
    }

    @Override
    public void clear() {
        holder.clear();
    }
}
