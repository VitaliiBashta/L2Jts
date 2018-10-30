package org.mmocore.gameserver.data.xml.parser;

import org.jdom2.Element;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.xml.holder.MultiSellHolder;
import org.mmocore.gameserver.model.MultiSellListContainer;

import java.io.File;

public final class CustomMultiSellParser extends MultiSellParser {
    private static final CustomMultiSellParser _instance = new CustomMultiSellParser();

    private int _size = 0;

    private CustomMultiSellParser() {
        super();
    }

    public static CustomMultiSellParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/multisell/custom/");
    }

    @Override
    public boolean isIgnored(final File f) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "../multisell.dtd";
    }

    @Override
    public void load() {
        super.load();
        if (_size > 0) {
            info(String.format("loaded %d custom multi sell(s) count.", _size));
        }
    }

    @Override
    protected void readData(final MultiSellHolder holder, final Element rootElement) throws Exception {
        super.readData(holder, rootElement);
        _size++;
    }

    @Override
    protected void addMultiSellListContainer(final MultiSellHolder holder, final int id, final MultiSellListContainer list) {
        holder.addCustomMultiSellListContainer(id, list);
    }
}
