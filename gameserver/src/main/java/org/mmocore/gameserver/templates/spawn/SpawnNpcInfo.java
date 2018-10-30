package org.mmocore.gameserver.templates.spawn;

import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 4:33/19.05.2011
 */
public class SpawnNpcInfo {
    private final NpcTemplate _template;
    private final int _max;
    private final MultiValueSet<String> _parameters;

    public SpawnNpcInfo(final int npcId, final int max, final MultiValueSet<String> set) {
        _template = NpcHolder.getInstance().getTemplate(npcId);
        _max = max;
        _parameters = set;
    }

    public NpcTemplate getTemplate() {
        return _template;
    }

    public int getMax() {
        return _max;
    }

    public MultiValueSet<String> getParameters() {
        return _parameters;
    }
}
