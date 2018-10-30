package handler.bbs.custom.classMaster;
import handler.bbs.ScriptBbsHandler;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.configuration.config.community.CClassMasterConfig;
import org.mmocore.gameserver.model.base.ClassId;
import org.mmocore.gameserver.model.base.ClassLevel;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.network.lineage.components.SystemMsg;
import org.mmocore.gameserver.object.Player;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Create by Mangol on 14.12.2015.
 *
 * @author - ?
 */
public class CommunityClassMaster extends ScriptBbsHandler {
	private static final CommunityClassMaster INSTANCE = new CommunityClassMaster();

	public static CommunityClassMaster getInstance() {
		return INSTANCE;
	}

	@Override
	public String[] getBypassCommands() {
		return new String[] { "_bbscareer" };
	}

	@Override
	public void onBypassCommand(Player player, String bypass) {
		if(!CClassMasterConfig.allowClassMaster) {
			player.sendMessage(new CustomMessage("scripts.services.off"));
			useCommand(player, "_bbshome");
			return;
		}
		if(bypass.equals("_bbscareer")) {
			showClassPage(player);
		}
		else if(bypass.startsWith("_bbscareer:change_class")) {
			final String[] data = bypass.split(":");
			changeClass(player, Integer.parseInt(data[2]), Integer.parseInt(data[3]), 0);
		}
	}

	private String page(final String text) {
		final StringBuilder html = new StringBuilder();
		html.append("<tr>");
		html.append("<td width=20></td>");
		html.append("<td width=690 height=15 align=left valign=top>");
		html.append(text);
		html.append("</td>");
		html.append("</tr>");
		return html.toString();
	}

	public void showClassPage(final Player player) {
		if(!CClassMasterConfig.allowClassMaster) {
			player.sendMessage(new CustomMessage("scripts.services.off"));
			useCommand(player, "_bbshome");
			return;
		}
		final ClassId classId = player.getPlayerClassComponent().getClassId();
		ClassLevel jobLevel = classId.getLevel();
		final int level = player.getLevel();
		String html = "";
		if(CClassMasterConfig.classLevel.isEmpty() || !CClassMasterConfig.classLevel.contains(jobLevel.ordinal())) {
			jobLevel = ClassLevel.Fourth;
		}
		String content = getCache().getHtml(CBasicConfig.BBS_PATH + "/classMaster/index.htm", player);
		String template = getCache().getHtml(CBasicConfig.BBS_PATH + "/classMaster/template.htm", player);
		String block;
		if(level >= 20 && (jobLevel == ClassLevel.First) || (level >= 40 && jobLevel == ClassLevel.Second) || (level >= 76 && jobLevel == ClassLevel.Third) && (CClassMasterConfig.classLevel.contains(jobLevel.ordinal()))) {
			html = html + "<table width=755>";
			html = html + page("<center>" + new CustomMessage("classMaster.choiceClass").toString(player) + "</center>");
			html = html + "</table>";
			int id = jobLevel.ordinal() - 1;
			final List<ClassId> classIds = getAvailClasses(classId);
			if(classIds.isEmpty()) {
				content = content.replace("{info}", "");
			}
			else {
				for(ClassId cid : classIds) {
					block = template;
					block = block.replace("{icon}", "icon.etc_royal_membership_i00");
					block = block.replace("{name}", className(player, cid.getId()));
					switch(payType(id)) {
						case 0:
							block = block.replace("{action_name}", new CustomMessage("classMaster.cost").addString(formatPay(player, CClassMasterConfig.priceCount[id], CClassMasterConfig.itemId[id])).toString(player));
							block = block.replace("{link}", "bypass _bbscareer:change_class:" + cid.getId() + ":" + id + ":0");
							break;
						case 1:
							block = block.replace("{action_name}", new CustomMessage("classMaster.cost").addString(formatPay(player, CClassMasterConfig.secondPrice[id], CClassMasterConfig.secondItem[id])).toString(player));
							block = block.replace("{link}", "bypass _bbscareer:change_class:" + cid.getId() + ":" + id + ":1");
							break;
						case 2:
							block = block.replace("{action_name}", new CustomMessage("classMaster.costChoice").toString(player));
							block = block.replace("{link}", "bypass _bbsbypass:services.Class:choice " + cid.getId() + " " + id + ";_bbscareer");
					}
					html += block.replace("{value}", new CustomMessage("classMaster.change").toString(player));
				}
				content = content.replace("{info}", "");
			}
		}
		else {
			String info = getCache().getHtml(CBasicConfig.BBS_PATH + "/classMaster/info.htm", player);
			info = info.replace("{current}", className(player, player.getPlayerClassComponent().getClassId().getId()));
			html = html + "<table width=755>";
			String need;
			switch(jobLevel) {
				case First: {
					need = new CustomMessage("classMaster.needLevel").addNumber(20).toString(player);
					info = info.replace("{info}", need);
					break;
				}
				case Second: {
					need = new CustomMessage("classMaster.needLevel").addNumber(40).toString(player);
					info = info.replace("{info}", need);
					break;
				}
				case Third: {
					need = new CustomMessage("classMaster.needLevel").addNumber(76).toString(player);
					info = info.replace("{info}", need);
					break;
				}
				case Fourth: {
					info = info.replace("{info}", "You have learned all available professions");
					break;
				}
			}
			content = content.replace("{info}", info);
			html = html + "</table>";
		}
		content = content.replace("{classmaster}", html);
		separateAndSend(content, player);
	}

	public void changeClass(final Player player, final int classID, final int id, final int pay) {
		if(player == null) {
			return;
		}
		if(!CClassMasterConfig.allowClassMaster) {
			player.sendMessage(new CustomMessage("scripts.services.off"));
			useCommand(player, "_bbshome");
			return;
		}
		int item = 0;
		long count = -1L;
		switch(pay) {
			case 0:
				item = CClassMasterConfig.itemId[id];
				count = CClassMasterConfig.priceCount[id];
				break;
			case 1:
				item = CClassMasterConfig.secondItem[id];
				count = CClassMasterConfig.secondPrice[id];
				break;
		}
		if(getPay(player, item, count, true)) {
			Optional<ClassId> RequestClassId = Optional.empty();
			List<ClassId> availClasses = getAvailClasses(player.getPlayerClassComponent().getClassId());
			for(ClassId _class : availClasses) {
				if(_class.getId() == classID) {
					RequestClassId = Optional.of(_class);
					break;
				}
			}
			if(!RequestClassId.isPresent())
				return;
			if(player.getPlayerClassComponent().getClassId().isOfLevel(ClassLevel.Third)) {
				player.sendPacket(SystemMsg.YOU_HAVE_COMPLETED_THE_QUEST_FOR_3RD_OCCUPATION_CHANGE_AND_MOVED_TO_ANOTHER_CLASS_CONGRATULATIONS);
			}
			else {
				player.sendPacket(SystemMsg.CONGRATULATIONS__YOUVE_COMPLETED_A_CLASS_TRANSFER);
			}
			player.getPlayerClassComponent().setClassId(classID, false, false);
			player.broadcastCharInfo();
		}
		showClassPage(player);
	}

	private int payType(final int id) {
		if(CClassMasterConfig.itemId[id] != 0 && CClassMasterConfig.secondItem[id] == 0) {
			return 0;
		}
		if(CClassMasterConfig.itemId[id] == 0 && CClassMasterConfig.secondItem[id] != 0) {
			return 1;
		}
		if(CClassMasterConfig.itemId[id] != 0 && CClassMasterConfig.secondItem[id] != 0) {
			return 2;
		}
		return 0;
	}

	private static List<ClassId> getAvailClasses(ClassId playerClass) {
		return Stream.of(ClassId.values()).
				filter(_class -> _class.level() == playerClass.level() + 1 && _class.childOf(playerClass) && _class != ClassId.inspector).
				collect(Collectors.toList());
	}

	@Override
	public void onWriteCommand(final Player player, final String bypass, final String arg1, final String arg2, final String arg3, final String arg4, final String arg5) {

	}
}
