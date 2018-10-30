package org.mmocore.gameserver.data.xml.parser;

import org.apache.commons.lang3.ArrayUtils;
import org.jdom2.Element;
import org.mmocore.commons.collections.MultiValueSet;
import org.mmocore.commons.data.xml.AbstractDirParser;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.data.scripts.Scripts;
import org.mmocore.gameserver.data.xml.holder.EventHolder;
import org.mmocore.gameserver.model.entity.events.Event;
import org.mmocore.gameserver.model.entity.events.EventAction;
import org.mmocore.gameserver.model.entity.events.actions.*;
import org.mmocore.gameserver.model.entity.events.objects.*;
import org.mmocore.gameserver.model.reward.RewardData;
import org.mmocore.gameserver.model.reward.RewardGroup;
import org.mmocore.gameserver.model.reward.RewardList;
import org.mmocore.gameserver.model.reward.RewardType;
import org.mmocore.gameserver.network.lineage.components.ChatType;
import org.mmocore.gameserver.network.lineage.components.NpcString;
import org.mmocore.gameserver.network.lineage.components.SysString;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.PlaySound;
import org.mmocore.gameserver.utils.Location;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @author VISTALL
 * @date 12:56/10.12.2010
 */
public final class EventParser extends AbstractDirParser<EventHolder> {
    private static final EventParser _instance = new EventParser();

    protected EventParser() {
        super(EventHolder.getInstance());
    }

    public static EventParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLDir() {
        return new File(ServerConfig.DATAPACK_ROOT, "data/events/");
    }

    @Override
    public boolean isIgnored(final File f) {
        return false;
    }

    @Override
    public String getDTDFileName() {
        return "events.dtd";
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void readData(final EventHolder holder, final Element rootElement) throws Exception {
        for (final Element eventElement : rootElement.getChildren("event")) {
            final int id = Integer.parseInt(eventElement.getAttributeValue("id"));
            final String name = eventElement.getAttributeValue("name");
            final String impl = eventElement.getAttributeValue("impl");
            Class<Event> eventClass;
            try {
                eventClass = (Class<Event>) Class.forName("org.mmocore.gameserver.model.entity.events.impl." + impl + "Event");
            } catch (ClassNotFoundException e) {
                eventClass = (Class<Event>) Scripts.getInstance().getClasses().get("events.custom." + impl + "Event");
            }

            if (eventClass == null) {
                error("Not found impl class: " + impl + "; File: " + getCurrentFileName());
                continue;
            }

            final Constructor<Event> constructor = eventClass.getConstructor(MultiValueSet.class);

            final MultiValueSet<String> set = new MultiValueSet<>();
            set.set("id", id);
            set.set("name", name);

            for (final Element parameterElement : eventElement.getChildren("parameter")) {
                set.set(parameterElement.getAttributeValue("name"), parameterElement.getAttributeValue("value"));
            }

            final Event event = constructor.newInstance(set);

            event.addOnStartActions(parseActions(eventElement.getChild("on_start"), Integer.MAX_VALUE));
            event.addOnStopActions(parseActions(eventElement.getChild("on_stop"), Integer.MAX_VALUE));
            event.addOnInitActions(parseActions(eventElement.getChild("on_init"), Integer.MAX_VALUE));

            final Element onTime = eventElement.getChild("on_time");
            if (onTime != null) {
                for (final Element onElement : onTime.getChildren("on")) {
                    final int time = Integer.parseInt(onElement.getAttributeValue("time"));
                    final List<EventAction> actions = parseActions(onElement, time);
                    event.addOnTimeActions(time, actions);
                }
            }

            for (final Element objectElement : eventElement.getChildren("objects")) {
                final String objectsName = objectElement.getAttributeValue("name");
                final List<Serializable> objects = parseObjects(objectElement);

                event.addObjects(objectsName, objects);
            }
            holder.addEvent(event);
        }
    }

    private List<Serializable> parseObjects(final Element element) {
        if (element == null) {
            return Collections.emptyList();
        }

        final List<Serializable> objects = new ArrayList<>(2);
        for (final Element objectsElement : element.getChildren()) {
            final String nodeName = objectsElement.getName();
            if ("boat_point".equalsIgnoreCase(nodeName)) {
                objects.add(BoatPoint.parse(objectsElement));
            } else if ("point".equalsIgnoreCase(nodeName)) {
                objects.add(Location.parse(objectsElement));
            } else if ("spawn_ex".equalsIgnoreCase(nodeName)) {
                objects.add(new SpawnExObject(objectsElement.getAttributeValue("name")));
            } else if ("door".equalsIgnoreCase(nodeName)) {
                objects.add(new DoorObject(Integer.parseInt(objectsElement.getAttributeValue("id"))));
            } else if ("static_object".equalsIgnoreCase(nodeName)) {
                objects.add(new StaticObjectObject(Integer.parseInt(objectsElement.getAttributeValue("id"))));
            } else if (nodeName.equalsIgnoreCase("spawn_npc")) {
                int id = Integer.parseInt(objectsElement.getAttributeValue("id"));
                int x = Integer.parseInt(objectsElement.getAttributeValue("x"));
                int y = Integer.parseInt(objectsElement.getAttributeValue("y"));
                int z = Integer.parseInt(objectsElement.getAttributeValue("z"));
                objects.add(new SpawnSimpleObject(id, new Location(x, y, z)));
            } else if ("combat_flag".equalsIgnoreCase(nodeName)) {
                final int x = Integer.parseInt(objectsElement.getAttributeValue("x"));
                final int y = Integer.parseInt(objectsElement.getAttributeValue("y"));
                final int z = Integer.parseInt(objectsElement.getAttributeValue("z"));
                objects.add(new FortressCombatFlagObject(new Location(x, y, z)));
            } else if ("territory_ward".equalsIgnoreCase(nodeName)) {
                final int x = Integer.parseInt(objectsElement.getAttributeValue("x"));
                final int y = Integer.parseInt(objectsElement.getAttributeValue("y"));
                final int z = Integer.parseInt(objectsElement.getAttributeValue("z"));
                final int itemId = Integer.parseInt(objectsElement.getAttributeValue("item_id"));
                final int npcId = Integer.parseInt(objectsElement.getAttributeValue("npc_id"));
                objects.add(new TerritoryWardObject(itemId, npcId, new Location(x, y, z)));
            } else if ("siege_toggle_npc".equalsIgnoreCase(nodeName)) {
                final int id = Integer.parseInt(objectsElement.getAttributeValue("id"));
                final int fakeId = Integer.parseInt(objectsElement.getAttributeValue("fake_id"));
                final int x = Integer.parseInt(objectsElement.getAttributeValue("x"));
                final int y = Integer.parseInt(objectsElement.getAttributeValue("y"));
                final int z = Integer.parseInt(objectsElement.getAttributeValue("z"));
                final int hp = Integer.parseInt(objectsElement.getAttributeValue("hp"));
                Set<String> set = Collections.emptySet();
                for (final Element sub : objectsElement.getChildren()) {
                    if (set.isEmpty()) {
                        set = new HashSet<>();
                    }
                    set.add(sub.getAttributeValue("name"));
                }
                objects.add(new SiegeToggleNpcObject(id, fakeId, new Location(x, y, z), hp, set));
            } else if ("castle_zone".equalsIgnoreCase(nodeName)) {
                final long price = Long.parseLong(objectsElement.getAttributeValue("price"));
                objects.add(new CastleDamageZoneObject(objectsElement.getAttributeValue("name"), price));
            } else if ("zone".equalsIgnoreCase(nodeName)) {
                objects.add(new ZoneObject(objectsElement.getAttributeValue("name")));
            } else if ("ctb_team".equalsIgnoreCase(nodeName)) {
                final int mobId = Integer.parseInt(objectsElement.getAttributeValue("mob_id"));
                final int flagId = Integer.parseInt(objectsElement.getAttributeValue("id"));
                final Location loc = Location.parse(objectsElement);

                objects.add(new CTBTeamObject(mobId, flagId, loc));
            } else if (nodeName.equalsIgnoreCase("rewardlist"))
                objects.add(parseReward(objectsElement));
        }

        return objects;
    }

    private List<EventAction> parseActions(final Element element, final int time) {
        if (element == null) {
            return Collections.emptyList();
        }

        IfElseAction lastIf = null;
        final List<EventAction> actions = new ArrayList<>(0);
        for (final Element actionElement : element.getChildren()) {
            if ("start".equalsIgnoreCase(actionElement.getName())) {
                final String name = actionElement.getAttributeValue("name");
                final StartStopAction startStopAction = new StartStopAction(name, true);
                actions.add(startStopAction);
            } else if ("stop".equalsIgnoreCase(actionElement.getName())) {
                final String name = actionElement.getAttributeValue("name");
                final StartStopAction startStopAction = new StartStopAction(name, false);
                actions.add(startStopAction);
            } else if ("spawn".equalsIgnoreCase(actionElement.getName())) {
                final String name = actionElement.getAttributeValue("name");
                final SpawnDespawnAction spawnDespawnAction = new SpawnDespawnAction(name, true);
                actions.add(spawnDespawnAction);
            } else if ("despawn".equalsIgnoreCase(actionElement.getName())) {
                final String name = actionElement.getAttributeValue("name");
                final SpawnDespawnAction spawnDespawnAction = new SpawnDespawnAction(name, false);
                actions.add(spawnDespawnAction);
            } else if ("respawn".equalsIgnoreCase(actionElement.getName())) {
                final String name = actionElement.getAttributeValue("name");
                final RespawnAction respawnAction = new RespawnAction(name);
                actions.add(respawnAction);
            } else if ("open".equalsIgnoreCase(actionElement.getName())) {
                final String name = actionElement.getAttributeValue("name");
                final OpenCloseAction a = new OpenCloseAction(true, name);
                actions.add(a);
            } else if ("close".equalsIgnoreCase(actionElement.getName())) {
                final String name = actionElement.getAttributeValue("name");
                final OpenCloseAction a = new OpenCloseAction(false, name);
                actions.add(a);
            } else if ("active".equalsIgnoreCase(actionElement.getName())) {
                final String name = actionElement.getAttributeValue("name");
                final ActiveDeactiveAction a = new ActiveDeactiveAction(true, name);
                actions.add(a);
            } else if ("deactive".equalsIgnoreCase(actionElement.getName())) {
                final String name = actionElement.getAttributeValue("name");
                final ActiveDeactiveAction a = new ActiveDeactiveAction(false, name);
                actions.add(a);
            } else if ("refresh".equalsIgnoreCase(actionElement.getName())) {
                final String name = actionElement.getAttributeValue("name");
                final RefreshAction a = new RefreshAction(name);
                actions.add(a);
            } else if ("init".equalsIgnoreCase(actionElement.getName())) {
                final String name = actionElement.getAttributeValue("name");
                final InitAction a = new InitAction(name);
                actions.add(a);
            } else if ("npc_say".equalsIgnoreCase(actionElement.getName())) {
                final int npc = Integer.parseInt(actionElement.getAttributeValue("npc"));
                final ChatType chat = ChatType.valueOf(actionElement.getAttributeValue("chat"));
                final int range = Integer.parseInt(actionElement.getAttributeValue("range"));
                final NpcString string = NpcString.valueOf(actionElement.getAttributeValue("text"));
                final NpcSayAction action = new NpcSayAction(npc, range, chat, string);
                actions.add(action);
            } else if ("play_sound".equalsIgnoreCase(actionElement.getName())) {
                final int range = Integer.parseInt(actionElement.getAttributeValue("range"));
                final String sound = actionElement.getAttributeValue("sound");
                final PlaySound.Type type = PlaySound.Type.valueOf(actionElement.getAttributeValue("type"));

                final PlaySoundAction action = new PlaySoundAction(range, sound, type);
                actions.add(action);
            } else if ("give_item".equalsIgnoreCase(actionElement.getName())) {
                final int itemId = Integer.parseInt(actionElement.getAttributeValue("id"));
                final long count = Integer.parseInt(actionElement.getAttributeValue("count"));

                final GiveItemAction action = new GiveItemAction(itemId, count);
                actions.add(action);
            } else if ("announce".equalsIgnoreCase(actionElement.getName())) {
                final String val = actionElement.getAttributeValue("val");
                if (val == null && time == Integer.MAX_VALUE) {
                    info("Can't get announce time." + getCurrentFileName());
                    continue;
                }

                final int val2 = val == null ? time : Integer.parseInt(val);
                final EventAction action = new AnnounceAction(val2);
                actions.add(action);
            } else if ("announceFromHolder".equalsIgnoreCase(actionElement.getName())) {
                final String val = actionElement.getAttributeValue("val");
                if (val == null && time == Integer.MAX_VALUE) {
                    info("Can't get announce time." + getCurrentFileName());
                    continue;
                }
                final EventAction action = new AnnounceFromHolderAction(val);
                actions.add(action);
            } else if ("if".equalsIgnoreCase(actionElement.getName())) {
                final String name = actionElement.getAttributeValue("name");
                final IfElseAction action = new IfElseAction(name, false);

                action.setIfList(parseActions(actionElement, time));
                actions.add(action);

                lastIf = action;
            } else if ("ifnot".equalsIgnoreCase(actionElement.getName())) {
                final String name = actionElement.getAttributeValue("name");
                final IfElseAction action = new IfElseAction(name, true);

                action.setIfList(parseActions(actionElement, time));
                actions.add(action);

                lastIf = action;
            } else if ("else".equalsIgnoreCase(actionElement.getName())) {
                if (lastIf == null) {
                    info("Not find <if> for <else> tag");
                } else {
                    lastIf.setElseList(parseActions(actionElement, time));
                }
            } else if ("say".equalsIgnoreCase(actionElement.getName())) {
                final ChatType chat = ChatType.valueOf(actionElement.getAttributeValue("chat"));
                final int range = Integer.parseInt(actionElement.getAttributeValue("range"));

                final String how = actionElement.getAttributeValue("how");
                final String text = actionElement.getAttributeValue("text");

                final SysString sysString = SysString.valueOf2(how);

                SayAction sayAction = null;
                if (sysString != null) {
                    sayAction = new SayAction(range, chat, sysString, SystemMsg.valueOf(text));
                } else {
                    sayAction = new SayAction(range, chat, how, NpcString.valueOf(text));
                }

                actions.add(sayAction);
            } else if ("teleport_players".equalsIgnoreCase(actionElement.getName())) {
                final String name = actionElement.getAttributeValue("id");
                final TeleportPlayersAction a = new TeleportPlayersAction(name);
                actions.add(a);
            }
        }

        return actions.isEmpty() ? Collections.<EventAction>emptyList() : actions;
    }

    private RewardList parseReward(final Element secondElement) {
        final boolean autoLoot = secondElement.getAttributeValue("auto_loot") != null && Boolean.parseBoolean(secondElement.getAttributeValue("auto_loot"));
        final RewardList list = new RewardList(RewardType.EVENT, autoLoot);
        for (final Element nextElement : secondElement.getChildren()) {
            final String nextName = nextElement.getName();
            if ("group".equalsIgnoreCase(nextName)) {
                final double enterChance = nextElement.getAttributeValue("chance") == null ? RewardList.MAX_CHANCE : Double.parseDouble(nextElement.getAttributeValue("chance")) * 10000;
                final RewardGroup group = new RewardGroup(enterChance);
                for (final Element rewardElement : nextElement.getChildren()) {
                    final RewardData data = reward(rewardElement);
                    group.addData(data);
                }
                if (group != null) {
                    list.add(group);
                }
            } else if ("reward".equalsIgnoreCase(nextName)) {
                final RewardData data = reward(nextElement);
                final RewardGroup g = new RewardGroup(RewardList.MAX_CHANCE);
                g.addData(data);
                list.add(g);
            }
        }
        return list;
    }

    private RewardData reward(final Element rewardElement) {
        final int itemId = Integer.parseInt(rewardElement.getAttributeValue("item_id"));
        final RewardData data = new RewardData(itemId);

        long min = Long.parseLong(rewardElement.getAttributeValue("min"));
        long max = Long.parseLong(rewardElement.getAttributeValue("max"));

        if (data.getItem().isCommonItem()) {
            min *= ServerConfig.RATE_DROP_COMMON_ITEMS;
            max *= ServerConfig.RATE_DROP_COMMON_ITEMS;
        }

        // переводим в системный вид
        final int chance = (int) (Double.parseDouble(rewardElement.getAttributeValue("chance")) * 10000);
        data.setChance(chance);

        if (data.getItem().isArrow() // стрелы не рейтуются
                || (ServerConfig.NO_RATE_EQUIPMENT && data.getItem().isEquipment()) // отключаемая рейтовка эквипа
                || (ServerConfig.NO_RATE_KEY_MATERIAL && data.getItem().isKeyMatherial()) // отключаемая рейтовка ключевых материалов
                || (ServerConfig.NO_RATE_RECIPES && data.getItem().isRecipe()) // отключаемая рейтовка рецептов
                || ArrayUtils.contains(ServerConfig.NO_RATE_ITEMS, itemId)) // индивидаульная отключаемая рейтовка для списка предметов
        {
            data.setNotRate(true);
        }

        data.setMinDrop(min);
        data.setMaxDrop(max);

        return data;
    }
}
