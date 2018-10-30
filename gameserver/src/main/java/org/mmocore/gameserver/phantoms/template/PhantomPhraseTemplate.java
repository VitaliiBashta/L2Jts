package org.mmocore.gameserver.phantoms.template;

import org.mmocore.gameserver.network.lineage.components.ChatType;

/**
 * Created by Hack
 * Date: 23.08.2016 17:50
 */
public class PhantomPhraseTemplate {
    private String phrase;
    private int chance = 100;
    private ChatType type;

    public ChatType getType() {
        return type;
    }

    public void setType(ChatType type) {
        this.type = type;
    }

    public String getPhrase() {
        return phrase;
    }

    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }

    public int getChance() {
        return chance;
    }

    public void setChance(int chance) {
        this.chance = chance;
    }
}
