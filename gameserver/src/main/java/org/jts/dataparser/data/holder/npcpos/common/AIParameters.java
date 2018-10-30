package org.jts.dataparser.data.holder.npcpos.common;

import java.util.Collections;
import java.util.Map;

public class AIParameters {
    private Map<String, Object> params;

    public AIParameters(Map<String, Object> params) // Конструктор для NpcAIObjectFactory
    {
        this.params = params;
    }

    public AIParameters() {
        this.params = Collections.emptyMap();
    }

    public Map<String, Object> getParams() {
        return params;
    }
}