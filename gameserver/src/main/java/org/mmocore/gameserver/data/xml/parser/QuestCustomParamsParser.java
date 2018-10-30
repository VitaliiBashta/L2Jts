package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.data.xml.holder.QuestCustomParamsHolder;
import org.mmocore.gameserver.templates.QuestCustomParams;

import java.io.File;

/**
 * Created by Hack
 * Date: 06.06.2016 20:41
 */
public class QuestCustomParamsParser extends AbstractFileParser<QuestCustomParamsHolder> {
    private static QuestCustomParamsParser instance;

    protected QuestCustomParamsParser() {
        super(QuestCustomParamsHolder.getInstance());
    }

    public static QuestCustomParamsParser getInstance() {
        return instance == null ? instance = new QuestCustomParamsParser() : instance;
    }

    @Override
    public String getDTDFileName() {
        return "../dtd/quest_custom_params.dtd";
    }

    @Override
    public File getXMLFile() {
        return new File("data/xmlscript/static/quest_custom_params.xml");
    }

    @Override
    protected void readData(QuestCustomParamsHolder holder, Element rootElement) throws Exception {
        for (Element quest : rootElement.getChildren("quest")) {
            int id = Integer.parseInt(quest.getAttributeValue("id"));
            int levelMin = Integer.parseInt(quest.getAttributeValue("levelMin"));
            int levelMax = Integer.parseInt(quest.getAttributeValue("levelMax"));
            holder.add(new QuestCustomParams(id, levelMin, levelMax));
        }
    }
}
