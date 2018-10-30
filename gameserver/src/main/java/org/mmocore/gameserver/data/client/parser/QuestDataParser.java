package org.mmocore.gameserver.data.client.parser;

import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.client.holder.QuestDataHolder;
import org.mmocore.gameserver.templates.client.QuestLine;

import java.io.File;

/**
 * @author KilRoy
 */
public class QuestDataParser extends AbstractFileParser<QuestDataHolder> {
    private static final QuestDataParser INSTANCE = new QuestDataParser();

    protected QuestDataParser() {
        super(QuestDataHolder.getInstance());
    }

    public static QuestDataParser getInstance() {
        return INSTANCE;
    }

    @Override
    public File getXMLFile() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/client/quest/quest_data.xml");
    }

    @Override
    public String getDTDFileName() {
        return "quest_data.dtd";
    }

    @Override
    protected void readData(final QuestDataHolder holder, final Element rootElement) throws Exception {
        for (final Element questElement : rootElement.getChildren("data")) {
            final int questId = Integer.parseInt(questElement.getAttributeValue("quest_id"));
            final String questName = questElement.getAttributeValue("main_name");
            final int minLevel = Integer.parseInt(questElement.getAttributeValue("lvl_min"));
            final int maxLevel = Integer.parseInt(questElement.getAttributeValue("lvl_max"));
            final String reward = questElement.getAttributeValue("reward");
            final QuestLine questTestInfo = new QuestLine(questId, questName, minLevel, maxLevel, reward);
            holder.addTestQuest(questId, questTestInfo);
        }
    }
}