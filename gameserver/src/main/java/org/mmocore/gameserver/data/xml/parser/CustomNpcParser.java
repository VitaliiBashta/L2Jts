package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.NpcHolder;
import org.mmocore.gameserver.templates.npc.NpcTemplate;

import java.io.File;

public final class CustomNpcParser extends NpcParser {
    private static final CustomNpcParser _instance = new CustomNpcParser();

    private int _size = 0;

    private CustomNpcParser() {
        super();
    }

    public static CustomNpcParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/stats/npc/custom/");
    }

    @Override
    public boolean isIgnored(final File f) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "../npc.dtd";
    }

    @Override
    public void load() {
        super.load();
        if (_size > 0) {
            info(String.format("loaded %d custom npc template(s) count.", _size));
        }
    }

    @Override
    protected void readData(final NpcHolder holder, final Element rootElement) throws Exception {
        super.readData(holder, rootElement);
        _size++;
    }

    @Override
    protected void addTemplate(final NpcHolder holder, final NpcTemplate template) {
        holder.addTemplate(template);
    }
}
