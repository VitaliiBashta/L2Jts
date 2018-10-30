package org.mmocore.gameserver.tables;

import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.model.SkillLearn;
import org.mmocore.gameserver.object.Servitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author OverWorld
 * @author KilRoy
 */
public class PetSkillsTable {
    private static final Logger _log = LoggerFactory.getLogger(PetSkillsTable.class);
    private static PetSkillsTable _instance = new PetSkillsTable();
    private final TIntObjectHashMap<List<SkillLearn>> _skillTrees = new TIntObjectHashMap<>();

    private PetSkillsTable() {
        load();
    }

    public static PetSkillsTable getInstance() {
        return _instance;
    }

    public void reload() {
        _instance = new PetSkillsTable();
    }

    private void load() {
        int npcId;
        int skillId;
        int skillLvl;
        int minLvl;
        int count = 0;

        try {
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);

            final File file = new File(ServerConfig.DATAPACK_ROOT, "data/skill_tree/servitor_skill_tree.xml");

            final Document doc = factory.newDocumentBuilder().parse(file);
            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if ("list".equalsIgnoreCase(n.getNodeName())) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        if ("pet".equalsIgnoreCase(d.getNodeName())) {
                            NamedNodeMap attrs = d.getAttributes();
                            npcId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
                            skillId = 0;
                            skillLvl = 0;
                            minLvl = 0;
                            for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                                if ("data".equalsIgnoreCase(cd.getNodeName())) {
                                    attrs = cd.getAttributes();
                                    Node node;

                                    node = attrs.getNamedItem("minLevel");
                                    if (node != null)
                                        minLvl = Integer.parseInt(node.getNodeValue());

                                    node = attrs.getNamedItem("skillId");
                                    if (node != null)
                                        skillId = Integer.parseInt(node.getNodeValue());

                                    node = attrs.getNamedItem("skillLevel");
                                    if (node != null)
                                        skillLvl = Integer.parseInt(node.getNodeValue());

                                    List<SkillLearn> list = _skillTrees.get(npcId);
                                    if (list == null)
                                        _skillTrees.put(npcId, (list = new ArrayList<>()));

                                    final SkillLearn skillLearn = new SkillLearn(skillId, skillLvl, minLvl, 0, 0, 0, false);
                                    list.add(skillLearn);
                                    count++;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            _log.error("Error parsing servitor_skill_tree.xml {}", e);
        }

        _log.info("PetSkillsTable: Loaded " + count + " skills.");
    }

    public int getAvailableLevel(final Servitor cha, final int skillId) {
        final List<SkillLearn> skills = _skillTrees.get(cha.getNpcId());
        if (skills == null) {
            return 0;
        }

        int lvl = 0;
        for (final SkillLearn temp : skills) {
            if (temp.getId() != skillId) {
                continue;
            }
            if (temp.getLevel() == 0) {
                if (cha.getLevel() < 70) {
                    lvl = cha.getLevel() / 10;
                    if (lvl <= 0) {
                        lvl = 1;
                    }
                } else {
                    lvl = 7 + (cha.getLevel() - 70) / 5;
                }

                // formula usable for skill that have 10 or more skill levels
                final int maxLvl = SkillTable.getInstance().getMaxLevel(temp.getId());
                if (lvl > maxLvl) {
                    lvl = maxLvl;
                }
                break;
            } else if (temp.getMinLevel() <= cha.getLevel()) {
                if (temp.getLevel() > lvl) {
                    lvl = temp.getLevel();
                }
            }
        }
        return lvl;
    }
}