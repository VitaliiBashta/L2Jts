package org.mmocore.gameserver.scripts.events.RaidBossFarm;

import org.mmocore.gameserver.data.scripts.Functions;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.listener.actor.OnDeathListener;
import org.mmocore.gameserver.listener.actor.player.OnPlayerEnterListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.manager.ServerVariables;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.RequestPlayerGamePointIncrease;
import org.mmocore.gameserver.object.Creature;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.creatures.listeners.CharListenerList;
import org.mmocore.gameserver.utils.AnnouncementUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KilRoy
 */
public class RaidBossFarm extends Functions implements OnInitScriptListener {
    private static final Logger logger = LoggerFactory.getLogger(RaidBossFarm.class);

    private static final int ITEM_COUNT = 1000;

    /**
     * Читает статус эвента из базы.
     *
     * @return
     */
    private static boolean isActive() {
        return ServerVariables.getString("RaidBossFarm", "off").equalsIgnoreCase("on");
    }

    private static boolean isPriseGived() {
        return ServerVariables.getString("RaidBossFarmPrise", "non").equalsIgnoreCase("gived");
    }

    @Override
    public void onInit() {
        CharListenerList.addGlobal(new OnDeathListenerImpl());
        CharListenerList.addGlobal(new OnPlayerEnterListenerImpl());

        if (isActive())
            logger.info("Loaded Event: Raid Boss Farm [state: activated]");
        else
            logger.info("Loaded Event: Raid Boss Farm [state: deactivated]");
    }

    /**
     * Запускает эвент
     */
    @Bypass("events.RaidBossFarm:activateEvent")
    public void activateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (!isActive()) {
            logger.info("Event 'Raid Boss Farm' started.");
            AnnouncementUtils.announceToAll("Event 'Raid Boss Farm' started!");

            ServerVariables.set("RaidBossFarm", "on");
            ServerVariables.set("RaidBossFarmPrise", "non");

            show("admin/events/events.htm", player);
        } else
            player.sendMessage("Event 'Raid Boss Farm' already started.");
    }

    /**
     * Останавливает эвент
     */
    @Bypass("events.RaidBossFarm:deactivateEvent")
    public void deactivateEvent(Player player, NpcInstance lastNpc, String[] args) {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (isActive()) {
            logger.info("Event 'Raid Boss Farm' stopped.");
            AnnouncementUtils.announceToAll("Event 'Raid Boss Farm' stoped!");

            ServerVariables.set("RaidBossFarm", "off");
            ServerVariables.set("RaidBossFarmPrise", "non");

            show("admin/events/events.htm", player);
        } else
            player.sendMessage("Event 'Raid Boss Farm' not started.");
    }

    public boolean CheckDropChamp(Creature mob, Creature killer) {
        return mob != null && mob.isRaid() && killer != null && killer.getPlayer() != null && killer.getLevel() - mob.getLevel() < 10;
    }

    private static class OnPlayerEnterListenerImpl implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(Player player) {
            if (isActive() && !isPriseGived())
                AnnouncementUtils.announceToPlayer("Уважаемый игрок, хотите заработать Премиум Очков? Тогда убейте первым любого Райд Боcса, и получите приз в размере " + ITEM_COUNT + " Премиум Очков.", player);
            else if (isPriseGived())
                AnnouncementUtils.announceToPlayer("Уважаемый игрок, ивент 'Убийство первого Райд Босса' на этой неделе закончен. Победители получили " + ITEM_COUNT + " Премиум Очков. Следующий запуск ивента запланирован на след. неделю.", player);
        }
    }

    /**
     * Обработчик смерти мобов, управляющий эвентовым дропом
     */
    private class OnDeathListenerImpl implements OnDeathListener {
        @Override
        public void onDeath(Creature self, Creature killer) {
            if (isActive() && !isPriseGived() && CheckDropChamp(self, killer)) {
                if (killer.isPlayer()) {
                    final Player player = (Player) killer;
                    if (player.getParty() != null) {
                        final int membersPrize = ITEM_COUNT / player.getParty().getMemberCount();

                        for (final Player member : player.getParty().getPartyMembers()) {
                            AuthServerCommunication.getInstance().sendPacket(new RequestPlayerGamePointIncrease(member, membersPrize, false));
                            member.sendMessage(member.isLangRus() ? "Поздравляем! Вы первые, убившие первого Райд Боcса. Ваша награда(Премиум Очки): " + membersPrize : "Congratulation! You killed first Raid Boss. Your reward(Premium Points): " + membersPrize);
                        }
                        AnnouncementUtils.announceToAll("Поздравляем игрока " + player.getParty().getGroupLeader().getName() + " и его товарищей по группе, с убийством 1го Райд Боcса на сервере!");
                    } else {
                        AuthServerCommunication.getInstance().sendPacket(new RequestPlayerGamePointIncrease(player, ITEM_COUNT, false));
                        player.sendMessage(player.isLangRus() ? "Поздравляем! Вы первые, убившие первого Райд Боcса этой недели. Ваша награда(Премиум Очки): " + ITEM_COUNT : "Congratulation! You killed first Raid Boss on this weekly. Your reward(Premium Points): " + ITEM_COUNT);
                        AnnouncementUtils.announceToAll("Поздравляем игрока " + player.getName() + ", с убийством 1го Райд Боcса на этой неделе!");
                    }
                    ServerVariables.set("RaidBossFarmPrise", "gived");
                }
            }
        }
    }
}