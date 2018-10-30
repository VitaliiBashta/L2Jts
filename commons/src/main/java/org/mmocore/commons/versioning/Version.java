package org.mmocore.commons.versioning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

public class Version {
    private static final Logger _log = LoggerFactory.getLogger(Version.class);

    private String _revisionNumber = "exported";
    private String _versionNumber = "-1";
    private String _buildDate = "";
    private String _buildJdk = "";

    public Version(final Class<?> c) {
        final File jarName = Locator.getClassSource(c);
        try (JarFile jarFile = new JarFile(jarName)) {
            final Attributes attrs = jarFile.getManifest().getMainAttributes();

            setBuildJdk(attrs);

            setBuildDate(attrs);

            setRevisionNumber(attrs);

            setVersionNumber(attrs);
        } catch (IOException e) {
            _log.error("Unable to get soft information\nFile name '" + (jarName == null ? "null" : jarName.getAbsolutePath()) + "' isn't a valid jar");
        }
    }

    public String getRevisionNumber() {
        return _revisionNumber;
    }

    /**
     * @param attrs
     */
    private void setRevisionNumber(final Attributes attrs) {
        final String revisionNumber = attrs.getValue("Implementation-Build");
        if (revisionNumber != null) {
            _revisionNumber = revisionNumber;
        } else {
            _revisionNumber = "-1";
        }
    }

    public String getVersionNumber() {
        return _versionNumber;
    }

    /**
     * @param attrs
     */
    private void setVersionNumber(final Attributes attrs) {
        final String versionNumber = attrs.getValue("Implementation-Version");
        if (versionNumber != null) {
            _versionNumber = versionNumber;
        } else {
            _versionNumber = "-1";
        }
    }

    public String getBuildDate() {
        return _buildDate;
    }

    /**
     * @param attrs
     */
    private void setBuildDate(final Attributes attrs) {
        final String buildDate = attrs.getValue("Build-Date");
        if (buildDate != null) {
            _buildDate = buildDate;
        } else {
            _buildDate = "-1";
        }
    }

    public String getBuildJdk() {
        return _buildJdk;
    }

    /**
     * @param attrs
     */
    private void setBuildJdk(final Attributes attrs) {
        String buildJdk = attrs.getValue("Build-Jdk");
        if (buildJdk != null) {
            _buildJdk = buildJdk;
        } else {
            buildJdk = attrs.getValue("Created-By");
            if (buildJdk != null) {
                _buildJdk = buildJdk;
            } else {
                _buildJdk = "-1";
            }
        }
    }
}
