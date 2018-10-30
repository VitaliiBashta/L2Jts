package org.mmocore.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.mmocore.commons.data.AbstractHolder;
import org.mmocore.gameserver.model.SkillLearn;
import org.mmocore.gameserver.model.base.AcquireType;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.model.pledge.SubUnit;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.skills.SkillEntry;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author VISTALL
 * @date 20:55/30.11.2010
 */
public final class SkillAcquireHolder extends AbstractHolder {
    // классовые зависимости
    private final TIntObjectMap<List<SkillLearn>> normalSkillTree = new TIntObjectHashMap<>();
    private final TIntObjectMap<List<SkillLearn>> transferSkillTree = new TIntObjectHashMap<>();
    // расовые зависимости
    private final TIntObjectMap<List<SkillLearn>> transformationSkillTree = new TIntObjectHashMap<>();
    // без зависимостей
    private final List<SkillLearn> fishingSkillTree = new ArrayList<>();
    private final List<SkillLearn> fishingNonDwarfSkillTree = new ArrayList<>();
    private final List<SkillLearn> certificationSkillTree = new ArrayList<>();
    private final List<SkillLearn> collectionSkillTree = new ArrayList<>();
    private final List<SkillLearn> pledgeSkillTree = new ArrayList<>();
    private final List<SkillLearn> subUnitSkillTree = new ArrayList<>();

    public static SkillAcquireHolder getInstance() {
        return LazyHolder.INSTANCE;
    }

    public int getMinLevelForNewSkill(final Player player, final AcquireType type) {
        final List<SkillLearn> skills;
        switch (type) {
            case NORMAL:
                skills = normalSkillTree.get(player.getPlayerClassComponent().getActiveClassId());
                if (skills == null) {
                    info("skill tree for class " + player.getPlayerClassComponent().getActiveClassId() + " is not defined !");
                    return 0;
                }
                break;
            case TRANSFORMATION:
                skills = transformationSkillTree.get(player.getPlayerTemplateComponent().getPlayerRace().ordinal());
                if (skills == null) {
                    info("skill tree for race " + player.getPlayerTemplateComponent().getPlayerRace().ordinal() + " is not defined !");
                    return 0;
                }
                break;
            default:
                return 0;
        }
        int minlevel = 0;
        for (final SkillLearn temp : skills) {
            if (temp.getMinLevel() > player.getLevel()) {
                if (minlevel == 0 || temp.getMinLevel() < minlevel) {
                    minlevel = temp.getMinLevel();
                }
            }
        }
        return minlevel;
    }

    public Collection<SkillLearn> getAvailableSkills(final Player player, final AcquireType type) {
        return getAvailableSkills(player, type, null);
    }

    public Collection<SkillLearn> getAvailableSkills(final Player player, final AcquireType type, final SubUnit subUnit) {
        final Collection<SkillLearn> skills;
        switch (type) {
            case NORMAL:
                skills = normalSkillTree.get(player.getPlayerClassComponent().getActiveClassId());
                if (skills == null) {
                    info("skill tree for class " + player.getPlayerClassComponent().getActiveClassId() + " is not defined !");
                    return Collections.emptyList();
                }
                return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel());
            case COLLECTION:
                skills = collectionSkillTree;
                if (skills == null) {
                    info("skill tree for class " + player.getPlayerClassComponent().getActiveClassId() + " is not defined !");
                    return Collections.emptyList();
                }
                return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel());
            case TRANSFORMATION:
                skills = transformationSkillTree.get(player.getPlayerTemplateComponent().getPlayerRace().ordinal());
                if (skills == null) {
                    info("skill tree for race " + player.getPlayerTemplateComponent().getPlayerRace().ordinal() + " is not defined !");
                    return Collections.emptyList();
                }
                return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel());
            case TRANSFER_EVA_SAINTS:
            case TRANSFER_SHILLIEN_SAINTS:
            case TRANSFER_CARDINAL:
                skills = transferSkillTree.get(type.transferClassId());
                if (skills == null) {
                    info("skill tree for class " + type.transferClassId() + " is not defined !");
                    return Collections.emptyList();
                }
                if (player == null) {
                    return skills;
                } else {
                    final Map<Integer, SkillLearn> skillLearnMap = new TreeMap<>();
                    skills.stream().filter(temp -> temp.getMinLevel() <= player.getLevel()).forEach(temp -> {
                        final int knownLevel = player.getSkillLevel(temp.getId());
                        if (knownLevel == -1) {
                            skillLearnMap.put(temp.getId(), temp);
                        }
                    });

                    return skillLearnMap.values();
                }
            case FISHING:
                skills = fishingSkillTree;

                return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel());
            case FISHING_NON_DWARF:
                skills = fishingNonDwarfSkillTree;

                return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel());
            case CLAN:
                skills = pledgeSkillTree;
                final Collection<SkillEntry> skls = player.getClan().getSkills(); //TODO [VISTALL] придумать другой способ

                return getAvaliableList(skills, skls.toArray(new SkillEntry[skls.size()]), player.getClan().getLevel());
            case SUB_UNIT:
                skills = subUnitSkillTree;
                final Collection<SkillEntry> st = subUnit.getSkills(); //TODO [VISTALL] придумать другой способ

                return getAvaliableList(skills, st.toArray(new SkillEntry[st.size()]), player.getClan().getLevel());
            case CERTIFICATION:
                skills = certificationSkillTree;
                if (player == null) {
                    return skills;
                } else {
                    return getAvaliableList(skills, player.getAllSkillsArray(), player.getLevel());
                }
            default:
                return Collections.emptyList();
        }
    }

    private Collection<SkillLearn> getAvaliableList(final Collection<SkillLearn> skillLearns, final SkillEntry[] skills, final int level) {
        final Map<Integer, SkillLearn> skillLearnMap = new HashMap<>();
        for (final SkillLearn temp : skillLearns) {
            if (temp.getMinLevel() <= level) {
                boolean knownSkill = false;
                for (int j = 0; j < skills.length && !knownSkill; j++) {
                    if (skills[j].getId() == temp.getId()) {
                        knownSkill = true;
                        if (skills[j].getLevel() == temp.getLevel() - 1) {
                            skillLearnMap.put(temp.getId(), temp);
                        }
                    }
                }
                if (!knownSkill && temp.getLevel() == 1) {
                    skillLearnMap.put(temp.getId(), temp);
                }
            }
        }
        return skillLearnMap.values();
    }

    public Map<Integer, SkillLearn> getNormalSkillsClassId(final Player player) {
        final Collection<SkillLearn> skills = normalSkillTree.get(player.getPlayerClassComponent().getActiveClassId());
        final Map<Integer, SkillLearn> ps = new HashMap<>();
        skills.stream().filter(temp -> temp.getMinLevel() <= player.getLevel()).forEach(temp -> {
            SkillLearn skill = ps.get(temp.getId());
            if (skill == null || temp.getLevel() > skill.getLevel())
                ps.put(temp.getId(), temp);
        });
        return ps;
    }

    public SkillLearn getSkillLearn(final Player player, final int id, final int level, final AcquireType type) {
        final List<SkillLearn> skills;
        switch (type) {
            case NORMAL:
                skills = normalSkillTree.get(player.getPlayerClassComponent().getActiveClassId());
                break;
            case COLLECTION:
                skills = collectionSkillTree;
                break;
            case TRANSFORMATION:
                skills = transformationSkillTree.get(player.getPlayerTemplateComponent().getPlayerRace().ordinal());
                break;
            case TRANSFER_CARDINAL:
            case TRANSFER_SHILLIEN_SAINTS:
            case TRANSFER_EVA_SAINTS:
                skills = transferSkillTree.get(player.getPlayerClassComponent().getActiveClassId());
                break;
            case FISHING:
                skills = fishingSkillTree;
                break;
            case FISHING_NON_DWARF:
                skills = fishingNonDwarfSkillTree;
                break;
            case CLAN:
                skills = pledgeSkillTree;
                break;
            case SUB_UNIT:
                skills = subUnitSkillTree;
                break;
            case CERTIFICATION:
                skills = certificationSkillTree;
                break;
            default:
                return null;
        }

        if (skills == null) {
            return null;
        }

        for (final SkillLearn temp : skills) {
            if (temp.getLevel() == level && temp.getId() == id) {
                return temp;
            }
        }

        return null;
    }

    public boolean isSkillPossible(final Player player, final SkillEntry skill, final AcquireType type) {
        Clan clan = null;
        final List<SkillLearn> skills;
        switch (type) {
            case NORMAL:
                skills = normalSkillTree.get(player.getPlayerClassComponent().getActiveClassId());
                break;
            case COLLECTION:
                skills = collectionSkillTree;
                break;
            case TRANSFORMATION:
                skills = transformationSkillTree.get(player.getPlayerTemplateComponent().getPlayerRace().ordinal());
                break;
            case FISHING:
                skills = fishingSkillTree;
                break;
            case FISHING_NON_DWARF:
                skills = fishingNonDwarfSkillTree;
                break;
            case TRANSFER_CARDINAL:
            case TRANSFER_EVA_SAINTS:
            case TRANSFER_SHILLIEN_SAINTS:
                final int transferId = type.transferClassId();
                if (player.getPlayerClassComponent().getActiveClassId() != transferId) {
                    return false;
                }

                skills = transferSkillTree.get(transferId);
                break;
            case CLAN:
                clan = player.getClan();
                if (clan == null) {
                    return false;
                }
                skills = pledgeSkillTree;
                break;
            case SUB_UNIT:
                clan = player.getClan();
                if (clan == null) {
                    return false;
                }

                skills = subUnitSkillTree;
                break;
            case CERTIFICATION:
                skills = certificationSkillTree;
                break;
            default:
                return false;
        }

        return isSkillPossible(skills, skill);
    }

    private boolean isSkillPossible(final Collection<SkillLearn> skills, final SkillEntry skill) {
        for (final SkillLearn learn : skills) {
            if (learn.getId() == skill.getId() && learn.getLevel() <= skill.getLevel()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSkillPossible(final Player player, final SkillEntry skill) {
        for (final AcquireType aq : AcquireType.VALUES) {
            if (isSkillPossible(player, skill, aq)) {
                return true;
            }
        }

        return false;
    }

    public List<SkillLearn> getSkillLearnListByItemId(final Player player, final int itemId) {
        final List<SkillLearn> learns = normalSkillTree.get(player.getPlayerClassComponent().getActiveClassId());
        if (learns == null) {
            return Collections.emptyList();
        }

        return learns.stream().filter($i -> $i.getItemId() == itemId).collect(Collectors.toList());
    }

    public List<SkillLearn> getAllNormalSkillTreeWithForgottenScrolls() {
        final List<SkillLearn> a = new ArrayList<>();
        for (final List<SkillLearn> list : normalSkillTree.valueCollection()) {
            a.addAll(list.stream().filter(learn -> learn.getItemId() > 0 && learn.isClicked()).collect(Collectors.toList()));
        }

        return a;
    }

    public void addNormalSkillLearns(final int classIdInt, final List<SkillLearn> learns) {
        normalSkillTree.put(classIdInt, learns);

        ClassId classId = ClassId.VALUES[classIdInt];

        ClassId secondparent = classId.getParent(1);
        if (secondparent == classId.getParent(0)) {
            secondparent = null;
        }

        classId = classId.getParent(0);

        while (classId != null) {
            final List<SkillLearn> parentList = normalSkillTree.get(classId.getId());
            learns.addAll(parentList);

            classId = classId.getParent(0);
            if (classId == null && secondparent != null) {
                classId = secondparent;
                secondparent = secondparent.getParent(1);
            }
        }
    }

    public void addAllFishingLearns(final List<SkillLearn> s) {
        fishingSkillTree.addAll(s);
    }

    public void addAllFishingNonDwarfLearns(final List<SkillLearn> s) {
        fishingNonDwarfSkillTree.addAll(s);
    }

    public void addAllTransferLearns(final int classId, final List<SkillLearn> s) {
        transferSkillTree.put(classId, s);
    }

    public void addAllTransformationLearns(final int race, final List<SkillLearn> s) {
        transformationSkillTree.put(race, s);
    }

    public void addAllCertificationLearns(final List<SkillLearn> s) {
        certificationSkillTree.addAll(s);
    }

    public void addAllCollectionLearns(final List<SkillLearn> s) {
        collectionSkillTree.addAll(s);
    }

    public void addAllSubUnitLearns(final List<SkillLearn> s) {
        subUnitSkillTree.addAll(s);
    }

    public void addAllPledgeLearns(final List<SkillLearn> s) {
        pledgeSkillTree.addAll(s);
    }

    @Override
    public void log() {
        // проверяем для всех-ли классов есть дерево скиллов
        checkNormalSkillTree();

        info("load " + sizeOfMapList(normalSkillTree) + " normal learns for " + normalSkillTree.size() + " classes.");
        info("load " + sizeOfMapList(transferSkillTree) + " transfer learns for " + transferSkillTree.size() + " classes.");
        //
        info("load " + sizeOfMapList(transformationSkillTree) + " transformation learns for " + transformationSkillTree.size() + " races.");
        //
        info("load " + fishingSkillTree.size() + " fishing learns.");
        info("load " + fishingNonDwarfSkillTree.size() + " fishing non dwarf learns.");
        info("load " + certificationSkillTree.size() + " certification learns.");
        info("load " + collectionSkillTree.size() + " collection learns.");
        info("load " + pledgeSkillTree.size() + " pledge learns.");
        info("load " + subUnitSkillTree.size() + " sub unit learns.");
    }

    @Override
    public void clear() {
        normalSkillTree.clear();
        fishingSkillTree.clear();
        transferSkillTree.clear();
        certificationSkillTree.clear();
        collectionSkillTree.clear();
        pledgeSkillTree.clear();
        subUnitSkillTree.clear();
    }

    private int sizeOfMapList(final TIntObjectMap<List<SkillLearn>> a) {
        int i = 0;
        for (final List<SkillLearn> l : a.valueCollection()) {
            i += l.size();
        }
        return i;
    }

    private void checkNormalSkillTree() {
        for (final ClassId classId : ClassId.VALUES) {
            if (classId.name().startsWith("dummyEntry"))
                continue;

            final int classID = classId.getId();

            final List<SkillLearn> temp = normalSkillTree.get(classID);
            if (temp == null)
                error("Not found NORMAL skill learn for class " + classID);
        }
    }

    private static class LazyHolder {
        private static final SkillAcquireHolder INSTANCE = new SkillAcquireHolder();
    }
}
