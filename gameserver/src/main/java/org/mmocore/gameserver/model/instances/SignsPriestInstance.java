package org.mmocore.gameserver.model.instances;

import org.mmocore.commons.utils.QuartzUtils;
import org.mmocore.gameserver.configuration.config.AllSettingsConfig;
import org.mmocore.gameserver.model.entity.SevenSigns;
import org.mmocore.gameserver.model.pledge.Clan;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.network.lineage.serverpackets.SystemMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.items.ItemInstance;
import org.mmocore.gameserver.object.components.variables.PlayerVariables;
import org.mmocore.gameserver.tables.ClanTable;
import org.mmocore.gameserver.templates.npc.NpcTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.StringTokenizer;

/**
 * Dawn/Dusk Seven Signs Priest Instance
 */
public class SignsPriestInstance extends NpcInstance {
    private static final Logger _log = LoggerFactory.getLogger(SignsPriestInstance.class);

    public SignsPriestInstance(final int objectId, final NpcTemplate template) {
        super(objectId, template);
    }

    private void showChatWindow(final Player player, final int val, final String suffix, final boolean isDescription) {
        String filename = SevenSigns.SEVEN_SIGNS_HTML_PATH;
        filename += isDescription ? "desc_" + val : "signs_" + val;
        filename += suffix != null ? '_' + suffix + ".htm" : ".htm";
        showChatWindow(player, filename);
    }

    private boolean getPlayerAllyHasCastle(final Player player) {
        final Clan playerClan = player.getClan();

        if (playerClan == null) {
            return false;
        }

        // If castle ownage check is clan-based rather than ally-based,
        // check if the player's clan has a castle and return the result.
        if (!AllSettingsConfig.ALT_GAME_REQUIRE_CLAN_CASTLE) {
            final int allyId = playerClan.getAllyId();

            // The player's clan is not in an alliance, so return false.
            if (allyId != 0) {
                // Check if another clan in the same alliance owns a castle,
                // by traversing the list of clans and act accordingly.
                final Clan[] clanList = ClanTable.getInstance().getClans();

                for (final Clan clan : clanList) {
                    if (clan.getAllyId() == allyId) {
                        if (clan.getCastle() > 0) {
                            return true;
                        }
                    }
                }
            }
        }
        return playerClan.getCastle() > 0;
    }

    @Override
    public void onBypassFeedback(final Player player, final String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if (getNpcId() == 31113 || getNpcId() == 31126) {
            if (SevenSigns.getInstance().getPlayerCabal(player) == SevenSigns.CABAL_NULL && !player.isGM()) {
                return;
            }
        }

        if (command.startsWith("SevenSignsDesc")) {
            final int val = Integer.parseInt(command.substring(15));

            showChatWindow(player, val, null, true);
        } else if (command.startsWith("SevenSigns")) {
            final SystemMessage sm;
            //String path;
            int cabal = SevenSigns.CABAL_NULL;
            int stoneType = 0;
            //      int inventorySize = player.getInventory().getSize() + 1;
            ItemInstance ancientAdena = player.getInventory().getItemByItemId(SevenSigns.ANCIENT_ADENA_ID);
            final long ancientAdenaAmount = ancientAdena == null ? 0 : ancientAdena.getCount();
            int val = Integer.parseInt(command.substring(11, 12).trim());

            if (command.length() > 12) // SevenSigns x[x] x [x..x]
            {
                val = Integer.parseInt(command.substring(11, 13).trim());
            }

            if (command.length() > 13) {
                try {
                    cabal = Integer.parseInt(command.substring(14, 15).trim());
                } catch (Exception e) {
                    try {
                        cabal = Integer.parseInt(command.substring(13, 14).trim());
                    } catch (Exception e2) {
                    }
                }
            }

            switch (val) {
                case 2: // Purchase Record of the Seven Signs
                    if (!player.getInventory().validateCapacity(1)) {
                        player.sendPacket(SystemMsg.YOUR_INVENTORY_IS_FULL);
                        return;
                    }

                    if (player.getAdena() < SevenSigns.RECORD_SEVEN_SIGNS_COST) {
                        player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                        return;
                    }

                    player.reduceAdena(SevenSigns.RECORD_SEVEN_SIGNS_COST, true);
                    player.getInventory().addItem(ItemFunctions.createItem(SevenSigns.RECORD_SEVEN_SIGNS_ID));
                    player.sendPacket(SystemMessage.obtainItems(SevenSigns.RECORD_SEVEN_SIGNS_ID, 1, 0));

                    break;
                case 3: // Join Cabal Intro 1
                case 8: // Festival of Darkness Intro - SevenSigns x [0]1
                    cabal = SevenSigns.getInstance().getPriestCabal(getNpcId());
                    showChatWindow(player, val, SevenSigns.getCabalShortName(cabal), false);
                    break;
                case 10: // Teleport Locations List
                    cabal = SevenSigns.getInstance().getPriestCabal(getNpcId());
                    if (SevenSigns.getInstance().isSealValidationPeriod()) {
                        showChatWindow(player, val, "", false);
                    } else {
                        showChatWindow(player, val, this.getParameters().getString("town", "no"), false);
                    }
                    break;
                case 4: // Join a Cabal - SevenSigns 4 [0]1 x
                    final int newSeal = Integer.parseInt(command.substring(15));
                    final int oldCabal = SevenSigns.getInstance().getPlayerCabal(player);

                    if (oldCabal != SevenSigns.CABAL_NULL) {
                        player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2SignsPriestInstance.AlreadyMember").addString(SevenSigns
                                .getCabalName(cabal)));
                        return;
                    }
                    if (player.getPlayerClassComponent().getClassId().level() == 0) {
                        player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2SignsPriestInstance.YouAreNewbie"));
                        break;
                    } else if (player.getPlayerClassComponent().getClassId().level() >= 2) {
                        if (AllSettingsConfig.ALT_GAME_REQUIRE_CASTLE_DAWN) {
                            if (getPlayerAllyHasCastle(player)) {
                                if (cabal == SevenSigns.CABAL_DUSK) {
                                    player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2SignsPriestInstance.CastleOwning"));
                                    return;
                                }
                            } else
                                /*
                                 * If the player is trying to join the Lords of Dawn, check if they are
                                 * carrying a Lord's certificate.
                                 *
                                 * If not then try to take the required amount of adena instead.
                                 */
                                if (cabal == SevenSigns.CABAL_DAWN) {
                                    boolean allowJoinDawn = false;

                                    if (ItemFunctions.getItemCount(player, SevenSigns.CERTIFICATE_OF_APPROVAL_ID) > 0) {
                                        ItemFunctions.removeItem(player, SevenSigns.CERTIFICATE_OF_APPROVAL_ID, 1);
                                        allowJoinDawn = true;
                                    } else if (AllSettingsConfig.ALT_GAME_ALLOW_ADENA_DAWN && player.getAdena() >= SevenSigns.ADENA_JOIN_DAWN_COST) {
                                        player.reduceAdena(SevenSigns.ADENA_JOIN_DAWN_COST, true);
                                        allowJoinDawn = true;
                                    }

                                    if (!allowJoinDawn) {
                                        if (AllSettingsConfig.ALT_GAME_ALLOW_ADENA_DAWN) {
                                            player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2SignsPriestInstance.CastleOwningCertificate"));
                                        } else {
                                            player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2SignsPriestInstance.CastleOwningCertificate2"));
                                        }
                                        return;
                                    }
                                }
                        }
                    }

                    SevenSigns.getInstance().setPlayerInfo(player.getObjectId(), cabal, newSeal);
                    if (cabal == SevenSigns.CABAL_DAWN) {
                        player.sendPacket(SystemMsg.YOU_WILL_PARTICIPATE_IN_THE_SEVEN_SIGNS_AS_A_MEMBER_OF_THE_LORDS_OF_DAWN); // Joined Dawn
                    } else {
                        player.sendPacket(SystemMsg.YOU_WILL_PARTICIPATE_IN_THE_SEVEN_SIGNS_AS_A_MEMBER_OF_THE_REVOLUTIONARIES_OF_DUSK); // Joined Dusk
                    }

                    //Show a confirmation message to the user, indicating which seal they chose.
                    switch (newSeal) {
                        case SevenSigns.SEAL_AVARICE:
                            player.sendPacket(SystemMsg.YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_AVARICE_DURING_THIS_QUEST_EVENT_PERIOD);
                            break;
                        case SevenSigns.SEAL_GNOSIS:
                            player.sendPacket(SystemMsg.YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_GNOSIS_DURING_THIS_QUEST_EVENT_PERIOD);
                            break;
                        case SevenSigns.SEAL_STRIFE:
                            player.sendPacket(SystemMsg.YOUVE_CHOSEN_TO_FIGHT_FOR_THE_SEAL_OF_STRIFE_DURING_THIS_QUEST_EVENT_PERIOD);
                            break;
                    }
                    showChatWindow(player, 4, SevenSigns.getCabalShortName(cabal), false);
                    break;
                case 6: // Contribute Seal Stones - SevenSigns 6 x
                    stoneType = Integer.parseInt(command.substring(13));
                    final ItemInstance redStones = player.getInventory().getItemByItemId(SevenSigns.SEAL_STONE_RED_ID);
                    final long redStoneCount = redStones == null ? 0 : redStones.getCount();
                    final ItemInstance greenStones = player.getInventory().getItemByItemId(SevenSigns.SEAL_STONE_GREEN_ID);
                    final long greenStoneCount = greenStones == null ? 0 : greenStones.getCount();
                    final ItemInstance blueStones = player.getInventory().getItemByItemId(SevenSigns.SEAL_STONE_BLUE_ID);
                    final long blueStoneCount = blueStones == null ? 0 : blueStones.getCount();
                    long contribScore = SevenSigns.getInstance().getPlayerContribScore(player);
                    boolean stonesFound = false;

                    if (contribScore == SevenSigns.MAXIMUM_PLAYER_CONTRIB) {
                        player.sendPacket(SystemMsg.CONTRIBUTION_LEVEL_HAS_EXCEEDED_THE_LIMIT);
                    } else {
                        long redContribCount = 0;
                        long greenContribCount = 0;
                        long blueContribCount = 0;

                        switch (stoneType) {
                            case 1:
                                blueContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - contribScore) / SevenSigns.BLUE_CONTRIB_POINTS;
                                if (blueContribCount > blueStoneCount) {
                                    blueContribCount = blueStoneCount;
                                }
                                break;
                            case 2:
                                greenContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - contribScore) / SevenSigns.GREEN_CONTRIB_POINTS;
                                if (greenContribCount > greenStoneCount) {
                                    greenContribCount = greenStoneCount;
                                }
                                break;
                            case 3:
                                redContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - contribScore) / SevenSigns.RED_CONTRIB_POINTS;
                                if (redContribCount > redStoneCount) {
                                    redContribCount = redStoneCount;
                                }
                                break;
                            case 4:
                                long tempContribScore = contribScore;
                                redContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - tempContribScore) / SevenSigns.RED_CONTRIB_POINTS;
                                if (redContribCount > redStoneCount) {
                                    redContribCount = redStoneCount;
                                }
                                tempContribScore += redContribCount * SevenSigns.RED_CONTRIB_POINTS;
                                greenContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - tempContribScore) / SevenSigns.GREEN_CONTRIB_POINTS;
                                if (greenContribCount > greenStoneCount) {
                                    greenContribCount = greenStoneCount;
                                }
                                tempContribScore += greenContribCount * SevenSigns.GREEN_CONTRIB_POINTS;
                                blueContribCount = (SevenSigns.MAXIMUM_PLAYER_CONTRIB - tempContribScore) / SevenSigns.BLUE_CONTRIB_POINTS;
                                if (blueContribCount > blueStoneCount) {
                                    blueContribCount = blueStoneCount;
                                }
                                break;
                        }
                        if (redContribCount > 0) {
                            if (player.getInventory().destroyItemByItemId(SevenSigns.SEAL_STONE_RED_ID, redContribCount)) {
                                stonesFound = true;
                            }
                        }
                        if (greenContribCount > 0) {
                            if (player.getInventory().destroyItemByItemId(SevenSigns.SEAL_STONE_GREEN_ID, greenContribCount)) {
                                stonesFound = true;
                            }
                        }
                        if (blueContribCount > 0) {
                            //ItemInstance temp = player.getInventory().getItemByItemId(SevenSigns.SEAL_STONE_BLUE_ID);
                            if (player.getInventory().destroyItemByItemId(SevenSigns.SEAL_STONE_BLUE_ID, blueContribCount)) {
                                stonesFound = true;
                            }
                        }

                        if (!stonesFound) {
                            player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2SignsPriestInstance.DontHaveAnySSType"));
                            return;
                        }

                        contribScore = SevenSigns.getInstance().addPlayerStoneContrib(player, blueContribCount, greenContribCount, redContribCount);
                        sm = new SystemMessage(SystemMsg.YOUR_CONTRIBUTION_SCORE_HAS_INCREASED_BY_S1);
                        sm.addNumber(contribScore);
                        player.sendPacket(sm);

                        showChatWindow(player, 6, null, false);
                    }
                    break;
                case 7: // Exchange Ancient Adena for Adena - SevenSigns 7 xxxxxxx
                    long ancientAdenaConvert = 0;
                    try {
                        ancientAdenaConvert = Long.parseLong(command.substring(13).trim());
                    } catch (Exception e) {
                        player.sendPacket(SystemMsg.SYSTEM_ERROR);
                        return;
                    }

                    if (ancientAdenaAmount < ancientAdenaConvert || ancientAdenaConvert < 1) {
                        player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                        return;
                    }

                    if (player.getInventory().destroyItemByItemId(SevenSigns.ANCIENT_ADENA_ID, ancientAdenaConvert)) {
                        player.addAdena(ancientAdenaConvert);
                        player.sendPacket(SystemMessage.removeItems(5575, ancientAdenaConvert));
                        player.sendPacket(SystemMessage.obtainItems(57, ancientAdenaConvert, 0));
                    }
                    break;
                case 9: // Receive Contribution Rewards
                    final int playerCabal = SevenSigns.getInstance().getPlayerCabal(player);
                    final int winningCabal = SevenSigns.getInstance().getCabalHighestScore();

                    if (SevenSigns.getInstance().isSealValidationPeriod() && playerCabal == winningCabal) {
                        final int ancientAdenaReward = SevenSigns.getInstance().getAncientAdenaReward(player, true);

                        if (ancientAdenaReward < 3) {
                            showChatWindow(player, 9, "b", false);
                            return;
                        }

                        ancientAdena = ItemFunctions.createItem(SevenSigns.ANCIENT_ADENA_ID);
                        ancientAdena.setCount(ancientAdenaReward);
                        player.getInventory().addItem(ancientAdena);
                        player.sendPacket(SystemMessage.obtainItems(SevenSigns.ANCIENT_ADENA_ID, ancientAdenaReward, 0));
                        showChatWindow(player, 9, "a", false);
                    }
                    break;
                case 11: // Teleport to Hunting Grounds - deprecated, instead use scripts_Util:QuestGatekeeper x y x 5575 price
                    try {
                        final String portInfo = command.substring(14).trim();

                        final StringTokenizer st = new StringTokenizer(portInfo);
                        final int x = Integer.parseInt(st.nextToken());
                        final int y = Integer.parseInt(st.nextToken());
                        final int z = Integer.parseInt(st.nextToken());
                        final long ancientAdenaCost = Long.parseLong(st.nextToken());

                        if (ancientAdenaCost > 0) {
                            if (!player.getInventory().destroyItemByItemId(SevenSigns.ANCIENT_ADENA_ID, ancientAdenaCost)) {
                                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                                return;
                            }
                        }
                        player.teleToLocation(x, y, z);
                    } catch (Exception e) {
                        _log.warn("SevenSigns: Error occurred while teleporting player: " + e);
                    }
                    break;
                case 17: // Exchange Seal Stones for Ancient Adena (Type Choice) - SevenSigns 17 x
                    stoneType = Integer.parseInt(command.substring(14));
                    int stoneId = 0;
                    long stoneCount = 0;
                    int stoneValue = 0;
                    String stoneColor = null;
                    //String content;

                    //FIXME [G1ta0] item-API
                    if (stoneType == 4) {
                        final ItemInstance BlueStoneInstance = player.getInventory().getItemByItemId(SevenSigns.SEAL_STONE_BLUE_ID);
                        final long bcount = BlueStoneInstance != null ? BlueStoneInstance.getCount() : 0;
                        final ItemInstance GreenStoneInstance = player.getInventory().getItemByItemId(SevenSigns.SEAL_STONE_GREEN_ID);
                        final long gcount = GreenStoneInstance != null ? GreenStoneInstance.getCount() : 0;
                        final ItemInstance RedStoneInstance = player.getInventory().getItemByItemId(SevenSigns.SEAL_STONE_RED_ID);
                        final long rcount = RedStoneInstance != null ? RedStoneInstance.getCount() : 0;
                        final long ancientAdenaReward = SevenSigns.calcAncientAdenaReward(bcount, gcount, rcount);
                        if (ancientAdenaReward > 0) {
                            if (BlueStoneInstance != null) {
                                player.getInventory().destroyItem(BlueStoneInstance, bcount);
                                player.sendPacket(SystemMessage.removeItems(SevenSigns.SEAL_STONE_BLUE_ID, bcount));
                            }
                            if (GreenStoneInstance != null) {
                                player.getInventory().destroyItem(GreenStoneInstance, gcount);
                                player.sendPacket(SystemMessage.removeItems(SevenSigns.SEAL_STONE_GREEN_ID, gcount));
                            }
                            if (RedStoneInstance != null) {
                                player.getInventory().destroyItem(RedStoneInstance, rcount);
                                player.sendPacket(SystemMessage.removeItems(SevenSigns.SEAL_STONE_RED_ID, rcount));
                            }

                            ancientAdena = ItemFunctions.createItem(SevenSigns.ANCIENT_ADENA_ID);
                            ancientAdena.setCount(ancientAdenaReward);
                            player.getInventory().addItem(ancientAdena);
                            player.sendPacket(SystemMessage.obtainItems(SevenSigns.ANCIENT_ADENA_ID, ancientAdenaReward, 0));
                        } else {
                            player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2SignsPriestInstance.DontHaveAnySS"));
                        }
                        break;
                    }

                    switch (stoneType) {
                        case 1:
                            stoneColor = "blue";
                            stoneId = SevenSigns.SEAL_STONE_BLUE_ID;
                            stoneValue = SevenSigns.SEAL_STONE_BLUE_VALUE;
                            break;
                        case 2:
                            stoneColor = "green";
                            stoneId = SevenSigns.SEAL_STONE_GREEN_ID;
                            stoneValue = SevenSigns.SEAL_STONE_GREEN_VALUE;
                            break;
                        case 3:
                            stoneColor = "red";
                            stoneId = SevenSigns.SEAL_STONE_RED_ID;
                            stoneValue = SevenSigns.SEAL_STONE_RED_VALUE;
                            break;
                    }
                    final ItemInstance stoneInstance = player.getInventory().getItemByItemId(stoneId);

                    if (stoneInstance != null) {
                        stoneCount = stoneInstance.getCount();
                    }

                    final HtmlMessage html = new HtmlMessage(this).setFile(SevenSigns.SEVEN_SIGNS_HTML_PATH + "signs_17.htm");
                    html.replace("%stoneColor%", stoneColor);
                    html.replace("%stoneValue%", String.valueOf(stoneValue));
                    html.replace("%stoneCount%", String.valueOf(stoneCount));
                    html.replace("%stoneItemId%", String.valueOf(stoneId));
                    player.sendPacket(html);

                    break;
                case 18: // Exchange Seal Stones for Ancient Adena - SevenSigns 18 xxxx xxxxxx
                    final int convertStoneId = Integer.parseInt(command.substring(14, 18));
                    long convertCount = 0;

                    try {
                        convertCount = Long.parseLong(command.substring(19).trim());
                    } catch (Exception exception) {
                        player.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
                        break;
                    }

                    final ItemInstance convertItem = player.getInventory().getItemByItemId(convertStoneId);
                    if (convertItem == null) {
                        player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2SignsPriestInstance.DontHaveAnySSType"));
                        break;
                    }

                    final long totalCount = convertItem.getCount();
                    long ancientAdenaReward = 0;
                    if (convertCount <= totalCount && convertCount > 0) {
                        switch (convertStoneId) {
                            case SevenSigns.SEAL_STONE_BLUE_ID:
                                ancientAdenaReward = SevenSigns.calcAncientAdenaReward(convertCount, 0, 0);
                                break;
                            case SevenSigns.SEAL_STONE_GREEN_ID:
                                ancientAdenaReward = SevenSigns.calcAncientAdenaReward(0, convertCount, 0);
                                break;
                            case SevenSigns.SEAL_STONE_RED_ID:
                                ancientAdenaReward = SevenSigns.calcAncientAdenaReward(0, 0, convertCount);
                                break;
                        }

                        if (player.getInventory().destroyItemByItemId(convertStoneId, convertCount)) {
                            ancientAdena = ItemFunctions.createItem(SevenSigns.ANCIENT_ADENA_ID);
                            ancientAdena.setCount(ancientAdenaReward);
                            player.getInventory().addItem(ancientAdena);
                            player.sendPacket(SystemMessage.removeItems(convertStoneId, convertCount), SystemMessage
                                    .obtainItems(SevenSigns.ANCIENT_ADENA_ID, ancientAdenaReward, 0));
                        }
                    } else {
                        player.sendMessage(new CustomMessage("org.mmocore.gameserver.model.instances.L2SignsPriestInstance.DontHaveSSAmount"));
                    }
                    break;
                case 19: // Seal Information (for when joining a cabal)
                    final int chosenSeal = Integer.parseInt(command.substring(16));
                    final String fileSuffix = SevenSigns.getSealName(chosenSeal, true) + '_' + SevenSigns.getCabalShortName(cabal);

                    showChatWindow(player, val, fileSuffix, false);
                    break;
                case 20: // Seal Status (for when joining a cabal)
                    final StringBuilder contentBuffer = new StringBuilder("<html><body><font color=\"LEVEL\">[Seal Status]</font><br>");

                    for (int i = 1; i < 4; i++) {
                        final int sealOwner = SevenSigns.getInstance().getSealOwner(i);
                        if (sealOwner != SevenSigns.CABAL_NULL) {
                            contentBuffer.append('[').append(SevenSigns.getSealName(i, false)).append(": ").append(SevenSigns.getCabalName(sealOwner))
                                    .append("]<br>");
                        } else {
                            contentBuffer.append('[').append(SevenSigns.getSealName(i, false)).append(": Nothingness]<br>");
                        }
                    }

                    contentBuffer.append("<a action=\"bypass -h npc_").append(getObjectId()).append("_SevenSigns 3 ").append(cabal).append(
                            "\">Go back.</a></body></html>");

                    final HtmlMessage html2 = new HtmlMessage(this);
                    html2.setHtml(contentBuffer.toString());
                    player.sendPacket(html2);
                    break;
                case 21: // Exchange Adena for Ancient Adena - High Five
                    if (player.getLevel() < 60) {
                        showChatWindow(player, 20, null, false);
                        return;
                    }
                    if (player.getPlayerVariables().getInt(PlayerVariables.B_MARKET_ADENA, 0) >= 500000) {
                        showChatWindow(player, 21, null, false);
                        return;
                    }
                    final Calendar sh = Calendar.getInstance();
                    sh.set(Calendar.HOUR_OF_DAY, 20);
                    sh.set(Calendar.MINUTE, 00);
                    sh.set(Calendar.SECOND, 00);
                    final Calendar eh = Calendar.getInstance();
                    eh.set(Calendar.HOUR_OF_DAY, 23);
                    eh.set(Calendar.MINUTE, 59);
                    eh.set(Calendar.SECOND, 59);
                    if (System.currentTimeMillis() > sh.getTimeInMillis() && System.currentTimeMillis() < eh.getTimeInMillis()) {
                        showChatWindow(player, 23, null, false);
                    } else {
                        showChatWindow(player, 22, null, false);
                    }
                    break;
                case 22:
                    final long adenaConvert;
                    final int tradeMult = 4;
                    final int limit = 500000;
                    try {
                        adenaConvert = Long.parseLong(command.substring(14).trim());
                    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                        player.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
                        return;
                    }
                    final long adenaAmount = ItemFunctions.getItemCount(player, 57);
                    final int amountLimit = player.getPlayerVariables().getInt(PlayerVariables.B_MARKET_ADENA, 0);
                    final long result = adenaConvert / tradeMult;
                    if (adenaAmount < adenaConvert || adenaConvert < tradeMult) // adenaConvert < tradeMult i.e. can't exchange if no AA will be given
                    {
                        player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                        return;
                    }
                    if (result > limit - amountLimit) {
                        player.sendMessage(new CustomMessage("common.LimitedAmount").addNumber(500000));
                        return;
                    }
                    if (ItemFunctions.removeItem(player, 57, adenaConvert, true) == adenaConvert) {
                        final CronExpression reset = QuartzUtils.createCronExpression("0 30 6 * * ?");

                        player.getPlayerVariables().set(PlayerVariables.B_MARKET_ADENA,
                                player.getPlayerVariables().getInt(PlayerVariables.B_MARKET_ADENA) + result,
                                reset.getNextValidTimeAfter(new Date()).getTime());
                        ItemFunctions.addItem(player, SevenSigns.ANCIENT_ADENA_ID, result, true);
                        showChatWindow(player, 24, null, false);
                    }
                    break;
                default:
                    // 1 = Purchase Record Intro
                    // 5 = Contrib Seal Stones Intro
                    // 16 = Choose Type of Seal Stones to Convert

                    showChatWindow(player, val, null, false);
                    break;
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public void showChatWindow(final Player player, final int val, final Object... arg) {
        final int npcId = getTemplate().npcId;

        String filename = SevenSigns.SEVEN_SIGNS_HTML_PATH;

        final int sealAvariceOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_AVARICE);
        final int sealGnosisOwner = SevenSigns.getInstance().getSealOwner(SevenSigns.SEAL_GNOSIS);
        final int playerCabal = SevenSigns.getInstance().getPlayerCabal(player);
        final boolean isSealValidationPeriod = SevenSigns.getInstance().isSealValidationPeriod();
        final int compWinner = SevenSigns.getInstance().getCabalHighestScore();

        switch (npcId) {
            case 31078:
            case 31079:
            case 31080:
            case 31081:
            case 31082: // Dawn Priests
            case 31083:
            case 31084:
            case 31168:
            case 31692:
            case 31694:
            case 31997:
                switch (playerCabal) {
                    case SevenSigns.CABAL_DAWN:
                        if (isSealValidationPeriod) {
                            if (compWinner == SevenSigns.CABAL_DAWN) {
                                if (compWinner != sealGnosisOwner) {
                                    filename += "dawn_priest_2c.htm";
                                } else {
                                    filename += "dawn_priest_2a.htm";
                                }
                            } else {
                                filename += "dawn_priest_2b.htm";
                            }
                        } else {
                            filename += "dawn_priest_1b.htm";
                        }
                        break;
                    case SevenSigns.CABAL_DUSK:
                        if (isSealValidationPeriod) {
                            filename += "dawn_priest_3b.htm";
                        } else {
                            filename += "dawn_priest_3a.htm";
                        }
                        break;
                    default:
                        if (isSealValidationPeriod) {
                            if (compWinner == SevenSigns.CABAL_DAWN) {
                                filename += "dawn_priest_4.htm";
                            } else {
                                filename += "dawn_priest_2b.htm";
                            }
                        } else {
                            filename += "dawn_priest_1a.htm";
                        }
                        break;
                }
                break;
            case 31085:
            case 31086:
            case 31087:
            case 31088: // Dusk Priest
            case 31089:
            case 31090:
            case 31091:
            case 31169:
            case 31693:
            case 31695:
            case 31998:
                switch (playerCabal) {
                    case SevenSigns.CABAL_DUSK:
                        if (isSealValidationPeriod) {
                            if (compWinner == SevenSigns.CABAL_DUSK) {
                                if (compWinner != sealGnosisOwner) {
                                    filename += "dusk_priest_2c.htm";
                                } else {
                                    filename += "dusk_priest_2a.htm";
                                }
                            } else {
                                filename += "dusk_priest_2b.htm";
                            }
                        } else {
                            filename += "dusk_priest_1b.htm";
                        }
                        break;
                    case SevenSigns.CABAL_DAWN:
                        if (isSealValidationPeriod) {
                            filename += "dusk_priest_3b.htm";
                        } else {
                            filename += "dusk_priest_3a.htm";
                        }
                        break;
                    default:
                        if (isSealValidationPeriod) {
                            if (compWinner == SevenSigns.CABAL_DUSK) {
                                filename += "dusk_priest_4.htm";
                            } else {
                                filename += "dusk_priest_2b.htm";
                            }
                        } else {
                            filename += "dusk_priest_1a.htm";
                        }
                        break;
                }
                break;
            case 31092: // Black Marketeer of Mammon
                filename += "blkmrkt_1.htm";
                break;
            case 31113: // Merchant of Mammon
                if (!player.isGM()) {
                    switch (compWinner) {
                        case SevenSigns.CABAL_DAWN:
                            if (playerCabal != compWinner || playerCabal != sealAvariceOwner) {
                                filename += "mammmerch_2.htm";
                                return;
                            }
                            break;
                        case SevenSigns.CABAL_DUSK:
                            if (playerCabal != compWinner || playerCabal != sealAvariceOwner) {
                                filename += "mammmerch_2.htm";
                                return;
                            }
                            break;
                    }
                }
                filename += "mammmerch_1.htm";
                break;
            case 31126: // Blacksmith of Mammon
                if (!player.isGM()) {
                    switch (compWinner) {
                        case SevenSigns.CABAL_DAWN:
                            if (playerCabal != compWinner || playerCabal != sealGnosisOwner) {
                                filename += "mammblack_2.htm";
                                return;
                            }
                            break;
                        case SevenSigns.CABAL_DUSK:
                            if (playerCabal != compWinner || playerCabal != sealGnosisOwner) {
                                filename += "mammblack_2.htm";
                                return;
                            }
                            break;
                    }
                }
                filename += "mammblack_1.htm";
                break;
            default:
                filename = getHtmlPath(npcId, val, player);
        }

        player.sendPacket(new HtmlMessage(this, filename, val));
    }
}
