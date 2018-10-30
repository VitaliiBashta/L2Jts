package org.mmocore.gameserver.data.xml.parser;

import javafx.util.Pair;
import org.jdom2.Element;
import org.mmocore.commons.data.xml.AbstractFileParser;
import org.mmocore.gameserver.data.xml.holder.TalismanEventHolder;
import org.mmocore.gameserver.model.instances.TalismanManagerInstance;

import java.io.File;
import java.util.List;

/**
 * Created by Hack
 * Date: 22.06.2017 0:03
 */
public class TalismanEventParser extends AbstractFileParser<TalismanEventHolder> {
    private static final TalismanEventParser instance = new TalismanEventParser();

    public TalismanEventParser() {
        super(TalismanEventHolder.getInstance());
    }

    public static TalismanEventParser getInstance() {
        return instance;
    }

    @Override
    public File getXMLFile() {
        return new File("data/xmlscript/items/talisman_event.xml");
    }

    @Override
    public String getDTDFileName() {
        return "../dtd/talisman_event.dtd";
    }

    @Override
    protected void readData(TalismanEventHolder holder, Element rootElement) {
        rootElement.getChildren("choice").forEach(choiceNode -> {
            TalismanManagerInstance.Choice choice = new TalismanManagerInstance.Choice();
            choice.setId(Integer.parseInt(choiceNode.getAttributeValue("id")));
            choice.setChance(Integer.parseInt(choiceNode.getAttributeValue("chance")));
            choiceNode.getChildren("pay").forEach(payNode -> parseItems(payNode, choice.getPay()));
            choiceNode.getChildren("success").forEach(successNode -> parseItems(successNode, choice.getSuccess()));
            choiceNode.getChildren("fail").forEach(failNode -> parseItems(failNode, choice.getFail()));
            holder.add(choice);
        });
    }

    private void parseItems(Element node, List<Pair<Integer, Long>> list) {
        node.getChildren("item").forEach(itemNode ->
                list.add(new Pair<>(Integer.parseInt(itemNode.getAttributeValue("id")),
                        Long.parseLong(itemNode.getAttributeValue("count")))));
    }
}
