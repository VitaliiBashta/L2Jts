package org.jts.dataparser.data.holder;

import org.jts.dataparser.data.annotations.Element;
import org.jts.dataparser.data.annotations.factory.IObjectFactory;
import org.jts.dataparser.data.holder.cubicdata.Agathion;
import org.jts.dataparser.data.holder.cubicdata.DefaultCubicData;
import org.jts.dataparser.data.holder.cubicdata.DefaultCubicData.CubicDataOpCond;
import org.jts.dataparser.data.holder.cubicdata.DefaultCubicData.CubicDataSkill;
import org.jts.dataparser.data.holder.cubicdata.DefaultCubicData.CubicDataTargetType;
import org.jts.dataparser.data.holder.cubicdata.DefaultCubicData.TargetType;
import org.mmocore.commons.data.AbstractHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jts.dataparser.data.holder.cubicdata.Agathion.AgathionTimeSkill;

public class CubicDataHolder extends AbstractHolder {
    private static final Pattern cubicDataTargetTypePattern = Pattern.compile("target_type\\s*?=\\s*?\\{?(master|target|by_skill|heal;(\\d+)%;(\\d+)%;(\\d+)%;(\\d+)%)}?");
    private static final Pattern cubicDataOpCondPattern = Pattern.compile("op_cond\\s*?=\\s*?\\{?(debuff|(\\d+);(\\d+)%;(\\d+))}?");
    // skills 1-3
    private static final Pattern cubicSkill1Pattern = Pattern.compile("skill1\\s*?=\\s*?\\{(\\d+)?%?;?\\[(\\S+?)];(\\d+)%;(\\d+);?(master|target|by_skill|heal)?;?\\{?(debuff|(\\d+);(\\d+)%;(\\d+))?}?}");
    private static final Pattern cubicSkill2Pattern = Pattern.compile("skill2\\s*?=\\s*?\\{(\\d+)?%?;?\\[(\\S+?)];(\\d+)%;(\\d+);?(master|target|by_skill|heal)?;?\\{?(debuff|(\\d+);(\\d+)%;(\\d+))?}?}");
    private static final Pattern cubicSkill3Pattern = Pattern.compile("skill3\\s*?=\\s*?\\{(\\d+)?%?;?\\[(\\S+?)];(\\d+)%;(\\d+);?(master|target|by_skill|heal)?;?\\{?(debuff|(\\d+);(\\d+)%;(\\d+))?}?}");
    // timeskills 1-3
    private static final Pattern agathionTimeSkill1Pattern = Pattern.compile("timeskill1\\s*?=\\s*?\\{\\[(\\S+?)];(\\d+);(master|target|by_skill|heal);(\\d+)}");
    private static final Pattern agathionTimeSkill2Pattern = Pattern.compile("timeskill2\\s*?=\\s*?\\{\\[(\\S+?)];(\\d+);(master|target|by_skill|heal);(\\d+)}");
    private static final Pattern agathionTimeSkill3Pattern = Pattern.compile("timeskill3\\s*?=\\s*?\\{\\[(\\S+?)];(\\d+);(master|target|by_skill|heal);(\\d+)}");
    private static final CubicDataHolder ourInstance = new CubicDataHolder();
    private final Map<Integer, DefaultCubicData> t_cubics = new HashMap<>();
    private final Map<Integer, Agathion> t_agathions = new HashMap<>();
    @Element(start = "cubic_begin", end = "cubic_end", objectFactory = CubicDataObjectFactory.class)
    private List<DefaultCubicData> cubics;
    @Element(start = "agathion_begin", end = "agathion_end", objectFactory = CubicDataObjectFactory.class)
    public List<Agathion> agathions;
    private CubicDataHolder() {
    }

    public static CubicDataHolder getInstance() {
        return ourInstance;
    }

    @Override
    public int size() {
        return t_cubics.size() + t_agathions.size();
    }

    @Override
    public void afterParsing() {
        cubics.forEach(cubic -> t_cubics.put(hash(cubic.id, cubic.level), cubic));
        agathions.forEach(agathion -> t_agathions.put(hash(agathion.id, agathion.level), agathion));
        agathions.clear();
        cubics.clear();
    }

    public DefaultCubicData getCubicTemplate(final int id, final int level) {
        return t_cubics.get(hash(id, level));
    }

    public Agathion getAgathionTemplate(final int id, final int level) {
        return t_agathions.get(hash(id, level));
    }

    public int hash(final int id, final int level) {
        return id * 10000 + level;
    }

    public Map<Integer, Agathion> getAgathions() {
        return t_agathions;
    }

    public Map<Integer, DefaultCubicData> getCubics() {
        return t_cubics;
    }

    @Override
    public void clear() {
        // TODO Auto-generated method stub
    }

    public static class CubicDataObjectFactory implements IObjectFactory<DefaultCubicData> {
        private Class<?> clazz;

        @Override
        public DefaultCubicData createObjectFor(StringBuilder data) throws IllegalAccessException, InstantiationException {
            DefaultCubicData cubicData = (DefaultCubicData) clazz.newInstance();
            // target_type
            Matcher matcher = cubicDataTargetTypePattern.matcher(data);
            if (matcher.find()) {
                int group = 1;
                String target_type = matcher.group(group++);
                if (target_type.startsWith("heal")) {
                    int[] params = new int[4];
                    params[0] = Integer.valueOf(matcher.group(group++));
                    params[1] = Integer.valueOf(matcher.group(group++));
                    params[2] = Integer.valueOf(matcher.group(group++));
                    params[3] = Integer.valueOf(matcher.group(group));
                    cubicData.target_type = new CubicDataTargetType(TargetType.heal, params);
                } else {
                    cubicData.target_type = new CubicDataTargetType(TargetType.valueOf(target_type));
                }
                data.replace(matcher.start(), matcher.end(), "");
            }
            // op_cond
            matcher = cubicDataOpCondPattern.matcher(data);
            if (matcher.find()) {
                int group = 1;
                String type = matcher.group(group++);
                if (type.equals("debuff")) {
                    cubicData.op_cond = new CubicDataOpCond();
                } else {
                    int[] cond = new int[3];
                    cond[0] = Integer.valueOf(matcher.group(group++));
                    cond[1] = Integer.valueOf(matcher.group(group++));
                    cond[2] = Integer.valueOf(matcher.group(group));
                    cubicData.op_cond = new CubicDataOpCond(cond);
                }
                data.replace(matcher.start(), matcher.end(), "");
            }
            // skill1
            matcher = cubicSkill1Pattern.matcher(data);
            if (matcher.find()) {
                cubicData.skill1 = doSkill(matcher);
                data.replace(matcher.start(), matcher.end(), "");
            }
            // skill2
            matcher = cubicSkill2Pattern.matcher(data);
            if (matcher.find()) {
                cubicData.skill2 = doSkill(matcher);
                data.replace(matcher.start(), matcher.end(), "");
            }
            // skill3
            matcher = cubicSkill3Pattern.matcher(data);
            if (matcher.find()) {
                cubicData.skill3 = doSkill(matcher);
                data.replace(matcher.start(), matcher.end(), "");
            }
            // Agathion timeskills 1-3
            if (clazz == Agathion.class) {
                matcher = agathionTimeSkill1Pattern.matcher(data);
                if (matcher.find()) {
                    ((Agathion) cubicData).timeskill1 = doTimeSkill(matcher);
                    data.replace(matcher.start(), matcher.end(), "");
                }
                matcher = agathionTimeSkill2Pattern.matcher(data);
                if (matcher.find()) {
                    ((Agathion) cubicData).timeskill2 = doTimeSkill(matcher);
                    data.replace(matcher.start(), matcher.end(), "");
                }
                matcher = agathionTimeSkill3Pattern.matcher(data);
                if (matcher.find()) {
                    ((Agathion) cubicData).timeskill3 = doTimeSkill(matcher);
                    data.replace(matcher.start(), matcher.end(), "");
                }
            }
            return cubicData;
        }

        private AgathionTimeSkill doTimeSkill(Matcher matcher) {
            AgathionTimeSkill skill = new AgathionTimeSkill();
            skill.skill_name = matcher.group(1);
            skill.targetStaticObject = Integer.valueOf(matcher.group(2));
            skill.skill_target_type = TargetType.valueOf(matcher.group(3));
            skill.time = Integer.valueOf(matcher.group(4));
            return skill;
        }

        private CubicDataSkill doSkill(Matcher matcher) {
            CubicDataSkill skill = new CubicDataSkill();
            int group = 1;
            String group1 = matcher.group(group++);
            if (group1 != null) {
                skill.skillChance = Integer.valueOf(group1);
            }
            // Эта информация всегда есть в скиле
            skill.skill_name = matcher.group(group++);
            skill.useChance = Integer.valueOf(matcher.group(group++));
            skill.targetStaticObject = Integer.valueOf(matcher.group(group++));
            // Присутствует, например, для smart кубиков
            if (matcher.group(group) != null) {
                skill.skill_target_type = TargetType.valueOf(matcher.group(group++));
                String opCond = matcher.group(group++);
                if (opCond.equals("debuff")) {
                    skill.skill_op_cond = new CubicDataOpCond();
                } else {
                    int[] cond = new int[3];
                    cond[0] = Integer.valueOf(matcher.group(group++));
                    cond[1] = Integer.valueOf(matcher.group(group++));
                    cond[2] = Integer.valueOf(matcher.group(group));
                    skill.skill_op_cond = new CubicDataOpCond(cond);
                }
            }
            return skill;
        }

        @Override
        public void setFieldClass(Class<?> clazz) {
            this.clazz = clazz;
        }
    }
}