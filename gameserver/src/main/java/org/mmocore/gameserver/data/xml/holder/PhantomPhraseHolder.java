package org.mmocore.gameserver.data.xml.holder;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.commons.utils.Rnd;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.phantoms.template.PhantomPhraseTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Hack
 * Date: 23.08.2016 17:45
 */
public class PhantomPhraseHolder extends AbstractHolder {
    private static final PhantomPhraseHolder instance = new PhantomPhraseHolder();
    private static final Logger log = LoggerFactory.getLogger(PhantomPhraseHolder.class);
    private final Map<ChatType, List<PhantomPhraseTemplate>> phrases = new HashMap<>();

    public PhantomPhraseHolder() {
        for (ChatType type : ChatType.values()) {
            phrases.put(type, new ArrayList<>(0));
        }
    }

    public static PhantomPhraseHolder getInstance() {
        return instance;
    }

    public void addPhrase(ChatType type, PhantomPhraseTemplate phrase) {
        phrases.get(type).add(phrase);
    }

    public String getRandomPhrase(ChatType type) {
        List<PhantomPhraseTemplate> phList = phrases.get(type);
        if (phList.size() == 0) {
            log.warn("Can't find phrases for chat type: " + type);
            return null;
        }
        for (int i = 0; i < 20; i++) {
            PhantomPhraseTemplate phrase = phList.get(Rnd.get(phList.size()));
            if (Rnd.chance(phrase.getChance()))
                return phrase.getPhrase();
        }
        log.warn("Can't find phrase for chat type: " + type + "! Please add more phrases or correct chance.");
        return null;
    }

    @Override
    public int size() {
        return phrases.size();
    }

    @Override
    public void clear() {
        phrases.clear();
    }
}
