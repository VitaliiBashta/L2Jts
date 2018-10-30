package org.mmocore.gameserver.handler.admincommands.impl;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.ServerConfig;
import org.mmocore.gameserver.configuration.config.gmAccess.GmAccessConfig;
import org.mmocore.gameserver.configuration.loader.ConfigLoader;
import org.mmocore.gameserver.configuration.parser.chatFilter.AbuseWorldsConfigParser;
import org.mmocore.gameserver.configuration.parser.gmAccess.GmAccessConfigParser;
import org.mmocore.gameserver.data.StringHolder;
import org.mmocore.gameserver.data.htm.HtmCache;
import org.mmocore.gameserver.data.xml.holder.ProductItemHolder;
import org.mmocore.gameserver.data.xml.parser.FishDataParser;
import org.mmocore.gameserver.data.xml.parser.MultiSellParser;
import org.mmocore.gameserver.data.xml.parser.NpcParser;
import org.mmocore.gameserver.database.dao.impl.CharacterQuestDAO;
import org.mmocore.gameserver.database.dao.impl.OlympiadNobleDAO;
import org.mmocore.gameserver.handler.admincommands.IAdminCommandHandler;
import org.mmocore.gameserver.manager.SpawnManager;
import org.mmocore.gameserver.model.entity.olympiad.OlympiadDatabase;
import org.mmocore.gameserver.model.quest.QuestState;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.GameObject;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.tables.SkillTable;
import org.mmocore.gameserver.utils.Strings;
import org.mmocore.gameserver.world.GameObjectsStorage;

public class AdminReload implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(final Enum<?> comm, final String[] wordList, final String fullString, final Player activeChar) {
        final Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanReload) {
            return false;
        }

        switch (command) {
            case admin_reload:
                break;
            case admin_reload_config: {
                try {
                    ConfigLoader.reloading();
                } catch (Exception e) {
                    activeChar.sendAdminMessage("Error: " + e.getMessage() + '!');
                    return false;
                }
                activeChar.sendAdminMessage("Config reloaded!");
                break;
            }
            case admin_reload_multisell: {
                MultiSellParser.getInstance().reload();
                activeChar.sendAdminMessage("Multisell list reloaded!");
                break;
            }
            case admin_reload_gmaccess: {
                try {
                    GmAccessConfigParser.loadGMAccess();
                    for (final Player player : GameObjectsStorage.getPlayers()) {
                        if (!ServerConfig.EVERYBODY_HAS_ADMIN_RIGHTS) {
                            player.setPlayerAccess(GmAccessConfig.gmlist.get(player.getObjectId()));
                        } else {
                            player.setPlayerAccess(GmAccessConfig.gmlist.get(0));
                        }
                    }
                } catch (Exception e) {
                    return false;
                }
                activeChar.sendAdminMessage("GMAccess reloaded!");
                break;
            }
            case admin_reload_htm: {
                HtmCache.getInstance().clear();
                activeChar.sendAdminMessage("HTML cache clearned.");
                break;
            }
            case admin_reload_qs: {
                if (fullString.endsWith("all")) {
                    GameObjectsStorage.getPlayers().forEach(this::reloadQuestStates);
                } else {
                    final GameObject t = activeChar.getTarget();

                    if (t != null && t.isPlayer()) {
                        final Player p = (Player) t;
                        reloadQuestStates(p);
                    } else {
                        reloadQuestStates(activeChar);
                    }
                }
                break;
            }
            case admin_reload_qs_help: {
                activeChar.sendAdminMessage("");
                activeChar.sendAdminMessage("Quest Help:");
                activeChar.sendAdminMessage("reload_qs_help - This Message.");
                activeChar.sendAdminMessage("reload_qs <selected target> - reload all quest states for target.");
                activeChar.sendAdminMessage("reload_qs <no target or target is not player> - reload quests for self.");
                activeChar.sendAdminMessage("reload_qs all - reload quests for all players in world.");
                activeChar.sendAdminMessage("");
                break;
            }
            case admin_reload_skills: {
                SkillTable.getInstance().reload();
                break;
            }
            case admin_reload_npc: {
                NpcParser.getInstance().reload();
                break;
            }
            case admin_reload_spawn: {
                ThreadPoolManager.getInstance().execute(new RunnableImpl() {
                    @Override
                    public void runImpl() {
                        SpawnManager.getInstance().reloadAll();
                    }
                });
                break;
            }
            case admin_reload_fish: {
                FishDataParser.getInstance().reload();
                break;
            }
            case admin_reload_abuse: {
                AbuseWorldsConfigParser.getInstance().reload();
                break;
            }
            case admin_reload_translit: {
                Strings.reload();
                break;
            }
            case admin_reload_static: {
                //StaticObjectsTable.getInstance().reloadStaticObjects();
                break;
            }
            case admin_reload_pets: {
                //PetDataHolder.getInstance().reload();
                activeChar.sendAdminMessage("TODO this reload");
                break;
            }
            case admin_reload_locale: {
                StringHolder.getInstance().reload();
                break;
            }
            case admin_reload_nobles: {
                OlympiadNobleDAO.getInstance().select();
                OlympiadDatabase.loadNoblesRank();
                break;
            }
            case admin_reload_im: {
                ProductItemHolder.getInstance().reload();
                break;
            }
        }
        activeChar.sendPacket(new HtmlMessage(5).setFile("admin/reload.htm"));
        return true;
    }

    private void reloadQuestStates(final Player p) {
        for (final QuestState qs : p.getAllQuestsStates()) {
            p.removeQuestState(qs.getQuest().getId());
        }
        CharacterQuestDAO.getInstance().select(p);
    }

    @Override
    public Enum<?>[] getAdminCommandEnum() {
        return Commands.values();
    }

    @Override
    public String[] getAdminCommandString() {
        // TODO Auto-generated method stub
        return null;
    }

    private enum Commands {
        admin_reload,
        admin_reload_config,
        admin_reload_multisell,
        admin_reload_gmaccess,
        admin_reload_htm,
        admin_reload_qs,
        admin_reload_qs_help,
        admin_reload_skills,
        admin_reload_npc,
        admin_reload_spawn,
        admin_reload_fish,
        admin_reload_abuse,
        admin_reload_translit,
        admin_reload_shops,
        admin_reload_static,
        admin_reload_pets,
        admin_reload_locale,
        admin_reload_nobles,
        admin_reload_im
    }
}