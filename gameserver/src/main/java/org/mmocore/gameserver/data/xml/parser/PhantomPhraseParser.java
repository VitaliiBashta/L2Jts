package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.PhantomPhraseHolder;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.phantoms.template.PhantomPhraseTemplate;

import java.io.File;

/**
 * Created by Hack
 * Date: 23.08.2016 18:15
 */
public class PhantomPhraseParser extends AbstractFileParser<PhantomPhraseHolder> {
    public static final PhantomPhraseParser instance = new PhantomPhraseParser();

    public PhantomPhraseParser() {
        super(PhantomPhraseHolder.getInstance());
    }

    public static PhantomPhraseParser getInstance() {
        return instance;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/xmlscript/phantoms/phrases.xml");
    }

    @Override
    public String getDTDFileName() {
        return "phrases.dtd";
    }

    @Override
    protected void readData(PhantomPhraseHolder holder, Element rootElement) {
        for (Element phrase : rootElement.getChildren("phrase")) {
            PhantomPhraseTemplate template = new PhantomPhraseTemplate();
            template.setPhrase(phrase.getAttributeValue("text"));
            template.setType(ChatType.valueOf(phrase.getAttributeValue("type")));
            String chance = phrase.getAttributeValue("chance");
            if (chance != null)
                template.setChance(Integer.parseInt(chance));
            holder.addPhrase(template.getType(), template);
        }
    }
}
