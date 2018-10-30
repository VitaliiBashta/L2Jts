package org.mmocore.gameserver.network.lineage.serverpackets;

import org.mmocore.gameserver.model.base.Element;
import org.mmocore.gameserver.model.entity.residence.Residence;
import org.mmocore.gameserver.model.instances.DoorInstance;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.model.instances.StaticObjectInstance;
import org.mmocore.gameserver.network.lineage.components.GameServerPacket;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.Servitor;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.skills.SkillEntry;
import org.mmocore.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @date 14:27/08.03.2011
 */
public abstract class SysMsgContainer<T extends SysMsgContainer<T>> extends GameServerPacket {
    private static final Logger _log = LoggerFactory.getLogger(SysMsgContainer.class);
    protected SystemMsg _message;
    protected List<IArgument> _arguments;
    protected SysMsgContainer(final SystemMsg message) {
        if (message == null) {
            throw new IllegalArgumentException("SystemMsg is null");
        }

        _message = message;
        _arguments = new ArrayList<>(_message.size());
    }

    protected void writeElements() {
        if (_message.size() != _arguments.size()) {
            throw new IllegalArgumentException("Wrong count of arguments: " + _message);
        }

        writeD(_message.getId());
        writeD(_arguments.size());
        for (final IArgument argument : _arguments) {
            argument.write(this);
        }
    }

    @Override
    public L2GameServerPacket packet(final Player player) {
        if (_message.size() != _arguments.size()) {
            _log.warn("Wrong count of arguments: " + _message, new Exception());
            return null;
        } else {
            return this;
        }
    }

    //==================================================================================================
    public T addName(final GameObject object) {
        if (object == null) {
            return add(new StringArgument(null));
        }

        if (object.isNpc()) {
            return add(new NpcNameArgument(((NpcInstance) object).getNpcId() + 1000000));
        } else if (object instanceof Servitor) {
            return add(new NpcNameArgument(((Servitor) object).getNpcId() + 1000000));
        } else if (object.isItem()) {
            return add(new ItemNameArgument(((ItemInstance) object).getItemId()));
        } else if (object.isPlayer()) {
            return add(new PlayerNameArgument((Player) object));
        } else if (object.isDoor()) {
            return add(new StaticObjectNameArgument(((DoorInstance) object).getDoorId()));
        } else if (object instanceof StaticObjectInstance) {
            return add(new StaticObjectNameArgument(((StaticObjectInstance) object).getUId()));
        }

        return add(new StringArgument(object.getName()));
    }

    public T addInstanceName(final int id) {
        return add(new InstanceNameArgument(id));
    }

    public T addSysString(final int id) {
        return add(new SysStringArgument(id));
    }

    public T addNpcString(final NpcString npcString, final String... arg) {
        return add(new NpcStringArgument(npcString, arg));
    }

    public T addSkillName(final SkillEntry skill) {
        return addSkillName(skill.getDisplayId(), skill.getDisplayLevel());
    }

    public T addSkillName(final int id, final int level) {
        return add(new SkillArgument(id, level));
    }

    public T addItemName(final int item_id) {
        return add(new ItemNameArgument(item_id));
    }

    public T addItemNameWithAugmentation(final ItemInstance item) {
        return add(new ItemNameWithAugmentationArgument(item.getItemId(), item.getVariation1Id(), item.getVariation2Id()));
    }

    public T addZoneName(final Creature c) {
        return addZoneName(c.getX(), c.getY(), c.getZ());
    }

    public T addZoneName(final Location loc) {
        return add(new ZoneArgument(loc.x, loc.y, loc.z));
    }

    public T addZoneName(final int x, final int y, final int z) {
        return add(new ZoneArgument(x, y, z));
    }

    public T addResidenceName(final Residence r) {
        return add(new ResidenceArgument(r.getId()));
    }

    public T addResidenceName(final int i) {
        return add(new ResidenceArgument(i));
    }

    public T addElementName(final int i) {
        return add(new ElementNameArgument(i));
    }

    public T addElementName(final Element i) {
        return add(new ElementNameArgument(i.getId()));
    }

    public T addNumber(final int i) {
        return add(new IntegerArgument(i));
    }

    public T addNumber(final long i) {
        return add(new LongArgument(i));
    }

    public T addString(final String t) {
        return add(new StringArgument(t));
    }

    @SuppressWarnings("unchecked")
    protected T add(final IArgument arg) {
        _arguments.add(arg);

        return (T) this;
    }

    public enum Types {
        TEXT, //0
        NUMBER,  //1
        NPC_NAME, //2
        ITEM_NAME, //3
        SKILL_NAME, //4
        RESIDENCE_NAME,   //5
        LONG,         //6
        ZONE_NAME,    //7
        ITEM_NAME_WITH_AUGMENTATION,    //8
        ELEMENT_NAME,    //9
        INSTANCE_NAME,   //10  d
        STATIC_OBJECT_NAME,    //11
        PLAYER_NAME, //12 S
        SYSTEM_STRING, //13 d
        NPC_STRING //14 dsssss
    }
    //==================================================================================================
    // Суппорт классы, собственна реализация (не L2jFree)
    //==================================================================================================

    private abstract static class IArgument {
        void write(final SysMsgContainer<?> m) {
            m.writeD(getType().ordinal());

            writeData(m);
        }

        abstract Types getType();

        abstract void writeData(SysMsgContainer<?> message);
    }


    private static class IntegerArgument extends IArgument {
        private final int _data;

        public IntegerArgument(final int da) {
            _data = da;
        }

        @Override
        public void writeData(final SysMsgContainer<?> message) {
            message.writeD(_data);
        }

        @Override
        Types getType() {
            return Types.NUMBER;
        }
    }

    private static class NpcNameArgument extends IntegerArgument {
        public NpcNameArgument(final int da) {
            super(da);
        }

        @Override
        Types getType() {
            return Types.NPC_NAME;
        }
    }

    private static class ItemNameArgument extends IntegerArgument {
        public ItemNameArgument(final int da) {
            super(da);
        }

        @Override
        Types getType() {
            return Types.ITEM_NAME;
        }
    }

    private static class ItemNameWithAugmentationArgument extends IArgument {
        private final int _itemId;
        private final int _variation1Id;
        private final int _variation2Id;

        public ItemNameWithAugmentationArgument(final int itemId, final int variation1Id, final int variation2Id) {
            _itemId = itemId;
            _variation1Id = variation1Id;
            _variation2Id = variation2Id;
        }

        @Override
        Types getType() {
            return Types.ITEM_NAME_WITH_AUGMENTATION;
        }

        @Override
        void writeData(final SysMsgContainer<?> message) {
            message.writeD(_itemId);
            message.writeH(_variation1Id);
            message.writeH(_variation2Id);
        }
    }

    private static class InstanceNameArgument extends IntegerArgument {
        public InstanceNameArgument(final int da) {
            super(da);
        }

        @Override
        Types getType() {
            return Types.INSTANCE_NAME;
        }
    }

    private static class NpcStringArgument extends IntegerArgument {
        private final String[] _arguments = new String[5];

        public NpcStringArgument(final NpcString npcString, final String... arg) {
            super(npcString.getId());
            System.arraycopy(arg, 0, _arguments, 0, arg.length);
        }

        @Override
        public void writeData(final SysMsgContainer<?> message) {
            super.writeData(message);
            for (final String a : _arguments) {
                message.writeS(a);
            }
        }

        @Override
        Types getType() {
            return Types.NPC_STRING;
        }
    }

    private static class SysStringArgument extends IntegerArgument {
        public SysStringArgument(final int da) {
            super(da);
        }

        @Override
        Types getType() {
            return Types.SYSTEM_STRING;
        }
    }

    private static class ResidenceArgument extends IntegerArgument {
        public ResidenceArgument(final int da) {
            super(da);
        }

        @Override
        Types getType() {
            return Types.RESIDENCE_NAME;
        }
    }

    private static class StaticObjectNameArgument extends IntegerArgument {
        public StaticObjectNameArgument(final int da) {
            super(da);
        }

        @Override
        Types getType() {
            return Types.STATIC_OBJECT_NAME;
        }
    }

    private static class LongArgument extends IArgument {
        private final long _data;

        public LongArgument(final long da) {
            _data = da;
        }

        @Override
        void writeData(final SysMsgContainer<?> message) {
            message.writeQ(_data);
        }

        @Override
        Types getType() {
            return Types.LONG;
        }
    }

    private static class StringArgument extends IArgument {
        private final String _data;

        public StringArgument(final String da) {
            _data = da == null ? "null" : da;
        }

        @Override
        void writeData(final SysMsgContainer<?> message) {
            message.writeS(_data);
        }

        @Override
        Types getType() {
            return Types.TEXT;
        }
    }

    private static class SkillArgument extends IArgument {
        private final int _skillId;
        private final int _skillLevel;

        public SkillArgument(final int t1, final int t2) {
            _skillId = t1;
            _skillLevel = t2;
        }

        @Override
        void writeData(final SysMsgContainer<?> message) {
            message.writeD(_skillId);
            message.writeD(_skillLevel);
        }

        @Override
        Types getType() {
            return Types.SKILL_NAME;
        }
    }

    private static class ZoneArgument extends IArgument {
        private final int _x;
        private final int _y;
        private final int _z;

        public ZoneArgument(final int t1, final int t2, final int t3) {
            _x = t1;
            _y = t2;
            _z = t3;
        }

        @Override
        void writeData(final SysMsgContainer<?> message) {
            message.writeD(_x);
            message.writeD(_y);
            message.writeD(_z);
        }

        @Override
        Types getType() {
            return Types.ZONE_NAME;
        }
    }

    private static class ElementNameArgument extends IntegerArgument {
        public ElementNameArgument(final int type) {
            super(type);
        }

        @Override
        Types getType() {
            return Types.ELEMENT_NAME;
        }
    }

    private static class PlayerNameArgument extends StringArgument {
        public PlayerNameArgument(final Player player) {
            super(player.getName());
        }

        @Override
        Types getType() {
            return Types.PLAYER_NAME;
        }
    }
}
