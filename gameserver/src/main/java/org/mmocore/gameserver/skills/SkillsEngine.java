package org.mmocore.gameserver.skills;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.mmocore.commons.utils.TroveUtils;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.model.Skill;
import org.mmocore.gameserver.tables.SkillTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collection;
import java.util.List;

public class SkillsEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(SkillsEngine.class);

    private static final SkillsEngine INSTANCE = new SkillsEngine();

    private SkillsEngine() {
    }

    public static SkillsEngine getInstance() {
        return INSTANCE;
    }

    public List<Skill> loadSkills(final File file) {
        if (file == null) {
            LOGGER.warn("SkillsEngine: File not found!");
            return null;
        }
        final DocumentSkill doc = new DocumentSkill(file);
        doc.parse();
        return doc.getSkills();
    }

    public TIntObjectMap<SkillEntry> loadAllSkills() {
        final File dir = new File(ServerConfig.DATAPACK_ROOT, "data/stats/skills");
        if (!dir.exists()) {
            LOGGER.info("Dir " + dir.getAbsolutePath() + " not exists");
            return TroveUtils.emptyIntObjectMap();
        }

        final Collection<File> files = FileUtils.listFiles(dir, FileFilterUtils.suffixFileFilter(".xml"), FileFilterUtils.directoryFileFilter());
        final TIntObjectMap<SkillEntry> result = new TIntObjectHashMap<>();
        int maxId = 0, maxLvl = 0;

        for (final File file : files) {
            final List<Skill> s = loadSkills(file);
            if (s == null) {
                continue;
            }

            for (final Skill skill : s) {
                result.put(SkillTable.getSkillHashCode(skill), new SkillEntry(SkillEntryType.NONE, skill));
                if (skill.getId() > maxId) {
                    maxId = skill.getId();
                }
                if (skill.getLevel() > maxLvl) {
                    maxLvl = skill.getLevel();
                }
            }

            s.clear();
        }

        LOGGER.info("SkillsEngine: Loaded " + result.size() + " skill templates from XML files. Max id: " + maxId + ", max level: " + maxLvl);
        return result;
    }
}