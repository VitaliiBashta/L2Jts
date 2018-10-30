package org.mmocore.gameserver.model.items.etcitems.AttributeStone;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.model.base.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * @author ALF
 * @data 26.06.2012
 */
public class AttributeStoneManager {
    private static final Logger logger = LoggerFactory.getLogger(AttributeStoneManager.class);

    private static TIntObjectHashMap<AttributeStoneInfo> stones = new TIntObjectHashMap<>();

    public static void load() {
        int id, min_arm, max_arm, min_weap, max_weap, inc_arm, inc_weap, inc_weap_arm;
        Element element;
        int chance;

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setIgnoringComments(true);

        File file = new File(ServerConfig.DATAPACK_ROOT, "data/model/etcitems/AttributeStone.xml");
        Document doc = null;

        if (file.exists()) {
            try {
                doc = factory.newDocumentBuilder().parse(file);
            } catch (Exception e) {
                logger.warn("Could not parse AttributeStone.xml file: " + e.getMessage(), e);
                return;
            }

            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if ("list".equalsIgnoreCase(n.getNodeName())) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        if ("item".equalsIgnoreCase(d.getNodeName())) {
                            NamedNodeMap attrs = d.getAttributes();
                            Node att;

                            id = 0;
                            min_arm = 0;
                            max_arm = 0;
                            min_weap = 0;
                            max_weap = 0;
                            inc_arm = 0;
                            inc_weap = 0;
                            inc_weap_arm = 0;
                            element = Element.NONE;
                            chance = 0;

                            att = attrs.getNamedItem("id");
                            if (att != null)
                                id = Integer.parseInt(att.getNodeValue());

                            att = attrs.getNamedItem("min_arm");
                            if (att != null)
                                min_arm = Integer.parseInt(att.getNodeValue());

                            att = attrs.getNamedItem("max_arm");
                            if (att != null)
                                max_arm = Integer.parseInt(att.getNodeValue());

                            att = attrs.getNamedItem("min_weap");
                            if (att != null)
                                min_weap = Integer.parseInt(att.getNodeValue());

                            att = attrs.getNamedItem("max_weap");
                            if (att != null)
                                max_weap = Integer.parseInt(att.getNodeValue());

                            att = attrs.getNamedItem("inc_arm");
                            if (att != null)
                                inc_arm = Integer.parseInt(att.getNodeValue());

                            att = attrs.getNamedItem("inc_weap");
                            if (att != null)
                                inc_weap = Integer.parseInt(att.getNodeValue());

                            att = attrs.getNamedItem("inc_weap_arm");
                            if (att != null)
                                inc_weap_arm = Integer.parseInt(att.getNodeValue());

                            att = attrs.getNamedItem("element");
                            if (att != null)
                                element = Element.getElementByName(att.getNodeValue());

                            att = attrs.getNamedItem("chance");
                            if (att != null)
                                chance = Integer.parseInt(att.getNodeValue());

                            AttributeStoneInfo asi = new AttributeStoneInfo();
                            asi.setItemId(id);

                            asi.setMinArmor(min_arm);
                            asi.setMaxArmor(max_arm);

                            asi.setMaxWeapon(min_weap);
                            asi.setMaxWeapon(max_weap);

                            asi.setIncArmor(inc_arm);
                            asi.setIncWeapon(inc_weap);
                            asi.setInсWeaponArmor(inc_weap_arm);

                            asi.setElement(element);

                            asi.setChance(chance);

                            stones.put(id, asi);
                        }
                    }
                }
            }
        }

        logger.info("AttributeStoneManager: Loaded " + stones.size() + " stone data...");

    }

    public static AttributeStoneInfo getStoneInfo(int itemId) {
        return stones.get(itemId);
    }

    public static int[] getAttributeStoneIds() {
        return stones.keys();

    }
}
