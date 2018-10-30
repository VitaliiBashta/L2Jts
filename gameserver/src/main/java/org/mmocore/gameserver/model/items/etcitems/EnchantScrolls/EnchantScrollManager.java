package org.mmocore.gameserver.model.items.etcitems.EnchantScrolls;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.templates.item.ItemTemplate.Grade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * @author ALF, KilRoy
 * @date 29.08.2012
 * TODO[K] - либо перенести к основным парсерам, либо перепилить на JDom
 */
public class EnchantScrollManager {
    private static final Logger logger = LoggerFactory.getLogger(EnchantScrollManager.class);

    private static TIntObjectHashMap<EnchantScrollInfo> scrolls = new TIntObjectHashMap<>();

    public static void load() {
        int itemId = 0;
        EnchantScrollType type = null;
        EnchantScrollTarget target = null;
        Grade grade = null;
        int min = 0;
        int safe = 0;
        int max = 0;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);

        File file = new File(ServerConfig.DATAPACK_ROOT, "data/model/etcitems/EnchantScroll.xml");
        Document doc = null;

        if (file.exists()) {
            try {
                doc = factory.newDocumentBuilder().parse(file);
            } catch (Exception e) {
                logger.warn("Could not parse EnchantScroll.xml file: " + e.getMessage(), e);
                return;
            }

            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if ("list".equalsIgnoreCase(n.getNodeName())) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        if ("item".equalsIgnoreCase(d.getNodeName())) {
                            NamedNodeMap attrs = d.getAttributes();
                            Node att;

                            att = attrs.getNamedItem("id");
                            if (att != null)
                                itemId = Integer.parseInt(att.getNodeValue());

                            att = attrs.getNamedItem("type");
                            if (att != null)
                                type = EnchantScrollType.valueOf(att.getNodeValue());

                            att = attrs.getNamedItem("target");
                            if (att != null)
                                target = EnchantScrollTarget.valueOf(att.getNodeValue());

                            att = attrs.getNamedItem("grade");
                            if (att != null)
                                grade = Grade.valueOf(att.getNodeValue());

                            att = attrs.getNamedItem("min");
                            if (att != null)
                                min = Integer.parseInt(att.getNodeValue());

                            att = attrs.getNamedItem("safe");
                            if (att != null)
                                safe = Integer.parseInt(att.getNodeValue());

                            att = attrs.getNamedItem("max");
                            if (att != null)
                                max = Integer.parseInt(att.getNodeValue());

                            EnchantScrollInfo esi = new EnchantScrollInfo();
                            esi.setItemId(itemId);
                            esi.setType(type);
                            esi.setTarget(target);
                            esi.setGrade(grade);
                            esi.setMin(min);
                            esi.setSafe(safe);
                            esi.setMax(max);

                            att = attrs.getNamedItem("chance");
                            if (att != null) {
                                final String chance = att.getNodeValue();
                                esi.setChance(chance);
                            }
                            att = attrs.getNamedItem("targetItems");
                            if (att != null) //TODO[K] - переделать
                            {
                                final String targetItems = att.getNodeValue();
                                final String[] args = targetItems.split(";");

                                for (String a : args)
                                    esi.addTargetItems(Integer.parseInt(a));
                            }

                            scrolls.put(itemId, esi);
                        }
                    }
                }
            }
        }
        logger.info("EnchantScrollManager: Loaded " + scrolls.size() + " scrolls data...");
    }

    public static EnchantScrollInfo getScrollInfo(int itemId) {
        return scrolls.get(itemId);
    }

    public static int[] getEnchantScrollIds() {
        return scrolls.keys();
    }
}
