package org.mmocore.authserver.configuration.parser;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.mmocore.authserver.configuration.config.ServerNamesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Create by Mangol on 09.12.2015.
 */
@Deprecated
public class ServerNamesConfigParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerNamesConfigParser.class);

    public static void loadServerNames() {
        final String SERVER_NAMES_FILE = "configuration/servername/servername.xml";
        ServerNamesConfig.SERVER_NAMES.clear();
        try {
            final SAXBuilder reader = new SAXBuilder();
            reader.setXMLReaderFactory(XMLReaders.DTDVALIDATING);
            final Document document = reader.build(new File(SERVER_NAMES_FILE));

            final Element root = document.getRootElement();

            for (final Element serverElement : root.getChildren("server")) {
                Integer id = Integer.valueOf(serverElement.getAttributeValue("id"));
                String name = serverElement.getAttributeValue("name");
                ServerNamesConfig.SERVER_NAMES.put(id, name);
            }

            LOGGER.info("Loaded " + ServerNamesConfig.SERVER_NAMES.size() + " server names");
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }
}
