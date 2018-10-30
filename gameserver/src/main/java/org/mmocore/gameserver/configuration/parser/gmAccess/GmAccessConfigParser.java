package org.mmocore.gameserver.configuration.parser.gmAccess;

import org.mmocore.gameserver.configuration.config.gmAccess.GmAccessConfig;
import org.mmocore.gameserver.model.base.PlayerAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.lang.reflect.Field;

/**
 * Create by Mangol on 08.12.2015.
 */
@Deprecated
public class GmAccessConfigParser {
    private static final Logger _log = LoggerFactory.getLogger(GmAccessConfigParser.class);

    public static void loadGMAccess() {
        GmAccessConfig.gmlist.clear();
        loadGMAccess(new File(GmAccessConfig.GM_PERSONAL_ACCESS_FILE));
        final File dir = new File(GmAccessConfig.GM_ACCESS_FILES_DIR);
        if (!dir.exists() || !dir.isDirectory()) {
            _log.info("Dir " + dir.getAbsolutePath() + " not exists.");
            return;
        }
        for (final File f : dir.listFiles())
        // hidden файлы НЕ игнорируем
        {
            if (!f.isDirectory() && f.getName().endsWith(".xml")) {
                loadGMAccess(f);
            }
        }
    }

    public static void loadGMAccess(final File file) {
        try {
            Field fld;
            final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            final Document doc = factory.newDocumentBuilder().parse(file);
            _log.info(file.getName());
            for (Node z = doc.getFirstChild(); z != null; z = z.getNextSibling()) {
                for (Node n = z.getFirstChild(); n != null; n = n.getNextSibling()) {
                    if (!"char".equalsIgnoreCase(n.getNodeName())) {
                        continue;
                    }

                    final PlayerAccess pa = new PlayerAccess();
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        final Class<?> cls = pa.getClass();
                        final String node = d.getNodeName();

                        if ("#text".equalsIgnoreCase(node)) {
                            continue;
                        }
                        try {
                            fld = cls.getField(node);
                        } catch (NoSuchFieldException e) {
                            _log.info("Not found desclarate ACCESS name: " + node + " in XML Player access Object");
                            continue;
                        }

                        if ("boolean".equalsIgnoreCase(fld.getType().getName())) {
                            fld.setBoolean(pa, Boolean.parseBoolean(d.getAttributes().getNamedItem("set").getNodeValue()));
                        } else if ("int".equalsIgnoreCase(fld.getType().getName())) {
                            fld.setInt(pa, Integer.parseInt(d.getAttributes().getNamedItem("set").getNodeValue()));
                        }
                    }
                    GmAccessConfig.gmlist.put(pa.PlayerID, pa);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
