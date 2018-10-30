package org.mmocore.gameserver.handler.voicecommands.impl;

import org.apache.commons.lang3.math.NumberUtils;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;

public class Cfg implements IVoicedCommandHandler {
    private final String[] _commandList = {"lang", "cfg"};

    //	public static final PrintfFormat cfg_row = new PrintfFormat("<table><tr><td width=5></td><td width=120>%s:</td><td width=100>%s</td></tr></table>");
    //	public static final PrintfFormat cfg_button = new PrintfFormat("<button width=%d back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h user_cfg %s\" value=\"%s\">");

    @Override
    public boolean useVoicedCommand(final String command, final Player activeChar, final String args) {
        if ("cfg".equals(command)) {
            if (args != null) {
                final String[] param = args.split(" ");
                if (param.length == 2) {
                    if ("dli".equalsIgnoreCase(param[0])) {
                        if ("on".equalsIgnoreCase(param[1])) {
                            activeChar.getPlayerVariables().set(PlayerVariables.DROPLIST_ICONS, "1", -1);
                        } else if ("of".equalsIgnoreCase(param[1])) {
                            activeChar.getPlayerVariables().remove(PlayerVariables.DROPLIST_ICONS);
                        }
                    }

                    if ("lang".equalsIgnoreCase(param[0])) {
                        if ("en".equalsIgnoreCase(param[1])) {
                            activeChar.getPlayerVariables().set(PlayerVariables.LANG, "en", -1);
                        } else if ("ru".equalsIgnoreCase(param[1])) {
                            activeChar.getPlayerVariables().set(PlayerVariables.LANG, "ru", -1);
                        }
                    }

                    if ("noe".equalsIgnoreCase(param[0])) {
                        if ("on".equalsIgnoreCase(param[1])) {
                            activeChar.getPlayerVariables().set(PlayerVariables.NO_EXP, "1", -1);
                        } else if ("of".equalsIgnoreCase(param[1])) {
                            activeChar.getPlayerVariables().remove(PlayerVariables.NO_EXP);
                        }
                    }

                    if ("notraders".equalsIgnoreCase(param[0])) {
                        if ("on".equalsIgnoreCase(param[1])) {
                            activeChar.setNotShowTraders(true);
                            activeChar.getPlayerVariables().set(PlayerVariables.NO_TRADERS, "1", -1);
                        } else if ("of".equalsIgnoreCase(param[1])) {
                            activeChar.setNotShowTraders(false);
                            activeChar.getPlayerVariables().remove(PlayerVariables.NO_TRADERS);
                        }
                    }

                    if ("notShowBuffAnim".equalsIgnoreCase(param[0])) {
                        if ("on".equalsIgnoreCase(param[1])) {
                            activeChar.setNotShowBuffAnim(true);
                            activeChar.getPlayerVariables().set(PlayerVariables.NO_ANIMATION_OF_CAST, "1", -1);
                        } else if ("of".equalsIgnoreCase(param[1])) {
                            activeChar.setNotShowBuffAnim(false);
                            activeChar.getPlayerVariables().remove(PlayerVariables.NO_ANIMATION_OF_CAST);
                        }
                    }

                    if ("noShift".equalsIgnoreCase(param[0])) {
                        if ("on".equalsIgnoreCase(param[1])) {
                            activeChar.getPlayerVariables().set(PlayerVariables.NO_SHIFT, "1", -1);
                        } else if ("of".equalsIgnoreCase(param[1])) {
                            activeChar.getPlayerVariables().remove(PlayerVariables.NO_SHIFT);
                        }
                    }

                    if ("translit".equalsIgnoreCase(param[0])) {
                        if ("on".equalsIgnoreCase(param[1])) {
                            activeChar.getPlayerVariables().set(PlayerVariables.TRANSLIT, "tl", -1);
                        } else if ("of".equalsIgnoreCase(param[1])) {
                            activeChar.getPlayerVariables().remove(PlayerVariables.TRANSLIT);
                        }
                    }

                    if (AllSettingsConfig.AUTO_LOOT_INDIVIDUAL) {
                        if (AllSettingsConfig.AUTO_LOOT_INDIVIDUAL_ONLY_FOR_PREMIUM && !activeChar.getPremiumAccountComponent().hasBonus()) {
                            activeChar.sendAdminMessage(!activeChar.isLangRus() ? "Allowed only for premium account.)." : "Доступно только для премиум аккаунтов.");
                            activeChar.sendActionFailed();
                            return true;
                        }

                        if (param[0].equalsIgnoreCase("autoloot")) {
                            activeChar.setAutoLoot(NumberUtils.toInt(param[1], Player.AUTO_LOOT_NONE));
                        }

                        if (param[0].equalsIgnoreCase("autolooth")) {
                            activeChar.setAutoLootHerbs(Boolean.parseBoolean(param[1]));
                        }
                    }

                    if (param[0].equalsIgnoreCase("carriertime") && AllSettingsConfig.AltAllowNoCarrier) {
                        try {
                            int time = Integer.parseInt(param[1]);
                            if (time < 0)
                                time = 0;
                            else if (time > 180)
                                time = 180;
                            activeChar.getPlayerVariables().set(PlayerVariables.NO_CARRIER_TIME, time, -1);
                            activeChar.sendMessage("New time for rejoining: " + time + " seconds.");
                        } catch (Exception e) {
                            // ignore
                        }
                    }

                    if (param[0].equalsIgnoreCase("talismanstack")) {
                        if (param[1].equalsIgnoreCase("on")) {
                            activeChar.getPlayerVariables().set(PlayerVariables.TALISMAN_STACK, "on", -1);
                            activeChar.setTalismanStack(true);
                        } else {
                            activeChar.getPlayerVariables().remove(PlayerVariables.TALISMAN_STACK);
                            activeChar.setTalismanStack(false);
                        }
                    }
                }
            }
        }

        final HtmlMessage dialog = new HtmlMessage(5);
        dialog.setFile("command/cfg.htm");

        dialog.replace("%lang%", activeChar.getPlayerVariables().get(PlayerVariables.LANG).toUpperCase());
        dialog.replace("%dli%", activeChar.getPlayerVariables().getBoolean(PlayerVariables.DROPLIST_ICONS) ? "On" : "Off");
        dialog.replace("%noe%", activeChar.getPlayerVariables().getBoolean(PlayerVariables.NO_EXP) ? "On" : "Off");
        dialog.replace("%notraders%", activeChar.getPlayerVariables().getBoolean(PlayerVariables.NO_TRADERS) ? "On" : "Off");
        dialog.replace("%notShowBuffAnim%", activeChar.getPlayerVariables().getBoolean(PlayerVariables.NO_ANIMATION_OF_CAST) ? "On" : "Off");
        dialog.replace("%noShift%", activeChar.getPlayerVariables().getBoolean(PlayerVariables.NO_SHIFT) ? "On" : "Off");
        dialog.replace("%curcarrier%", activeChar.getPlayerVariables().getInt(PlayerVariables.NO_CARRIER_TIME, 60) + "");
        dialog.replace("%talisman%", activeChar.isTalismanStack() ? "On" : "Off");

        final String tl = activeChar.getPlayerVariables().get(PlayerVariables.TRANSLIT);
        if (tl == null) {
            dialog.replace("%translit%", "Off");
        } else if ("tl".equals(tl)) {
            dialog.replace("%translit%", "On");
        }

        if (AllSettingsConfig.AUTO_LOOT_INDIVIDUAL) {
            switch (activeChar.isAutoLootEnabled()) {
                case Player.AUTO_LOOT_ALL:
                    dialog.replace("%autoLoot%", "On");
                    break;
                case Player.AUTO_LOOT_ALL_EXCEPT_ARROWS:
                    if (activeChar.isLangRus()) {
                        dialog.replace("%autoLoot%", "Кроме стрел");
                    } else {
                        dialog.replace("%autoLoot%", "Except Arrows");
                    }
                    break;
                default:
                    dialog.replace("%autoLoot%", "Off");
                    break;
            }
            dialog.replace("%autoLootH%", activeChar.isAutoLootHerbsEnabled() ? "On" : "Off");
        } else {
            dialog.replace("%autoLoot%", "N/A");
            dialog.replace("%autoLootH%", "N/A");
        }

        activeChar.sendPacket(dialog);

        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }
}