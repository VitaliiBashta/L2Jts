package org.mmocore.commons.data.xml;

import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.commons.data.AbstractParser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Author: VISTALL
 * Date:  20:52/30.11.2010
 */
public abstract class AbstractFileParser<H extends AbstractHolder> extends AbstractParser<H> {
    protected AbstractFileParser(final H holder) {
        super(holder);
    }

    public abstract File getXMLFile();

    public abstract String getDTDFileName();

    @Override
    protected final void parse() {
        final File file = getXMLFile();

        if (!file.exists()) {
            warn("file " + file.getAbsolutePath() + " not exists");
            return;
        }

        final File dtd = new File(file.getParent(), getDTDFileName());
        if (!dtd.exists()) {
            info("DTD file: " + dtd.getName() + " not exists.");
            return;
        }

        initDTD(dtd);

        try (InputStream inputStream = new FileInputStream(file); InputStream bufferedInputStream = new BufferedInputStream(inputStream)) {
            parseDocument(bufferedInputStream, file.getName());
        } catch (Exception e) {
            warn("Exception: " + e, e);
        }
    }
}
