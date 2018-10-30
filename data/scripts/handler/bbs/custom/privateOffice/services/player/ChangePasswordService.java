package handler.bbs.custom.privateOffice.services.player;
import handler.bbs.abstracts.CommunityBoardService;
import handler.bbs.custom.privateOffice.engine.enums.Services;
import org.mmocore.commons.crypt.PBKDF2Hash;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.RequestAccountUpdate;
import org.mmocore.gameserver.network.lineage.components.CustomMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.utils.Log;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Objects;

/**
 * @author Mangol
 * @since 11.09.2016
 */
public class ChangePasswordService extends CommunityBoardService {
	@Override
	public Services getService() {
		return Services.change_password;
	}

	@Override
	public void content(Player player, String bypass, Object... params) {
		String htm = getCache().getHtml(CBasicConfig.BBS_PATH + "/cabinet/service/player/change_password.htm", player);
		separateAndSend(htm, player);
	}

	@Override
	public void request(Player player, String bypass, Object... params) {
		if(Objects.isNull(player)) {
			return;
		}
		if(AuthServerCommunication.getInstance().isShutdown()) {
			player.sendPacket(new CustomMessage("bbs.service.changePassword.error"));
			useSaveCommand(player);
			return;
		}
		final String[] str = bypass.split(":");
		if(str.length != 5) {
			player.sendPacket(new CustomMessage("bbs.service.changePassword.error"));
			useSaveCommand(player);
			return;
		}
		final String[] passwordStr = str[4].trim().split(" ");
		if(passwordStr.length != 3) {
			player.sendPacket(new CustomMessage("bbs.service.changePassword.error"));
			useSaveCommand(player);
			return;
		}
		final String oldPassword = passwordStr[0];
		final String password1 = passwordStr[1];
		final String password2 = passwordStr[2];
		if(!Objects.equals(password1, password2)) {
			player.sendPacket(new CustomMessage("bbs.service.changePassword.noEqualsP1P2"));
			useSaveCommand(player);
			return;
		}
		String passwordUpdate;
		try {
			if(!PBKDF2Hash.validatePassword(oldPassword, player.getPasswordHash())) {
				player.sendPacket(new CustomMessage("bbs.service.changePassword.noCorrectOldPassword"));
				useSaveCommand(player);
				return;
			}
			passwordUpdate = PBKDF2Hash.createHash(password1);
		}
		catch(NoSuchAlgorithmException | InvalidKeySpecException e) {
			player.sendPacket(new CustomMessage("bbs.service.changePassword.error"));
			useSaveCommand(player);
			return;
		}
		reply(player, passwordUpdate);
	}

	@Override
	public void reply(Player player, Object... params) {
		player.setPasswordHash((String) params[0]);
		AuthServerCommunication.getInstance().sendPacket(RequestAccountUpdate.create(player.getAccountName(), player.getLastIp(), player.getPasswordHash()));
		useSaveCommand(player);
		player.sendPacket(new CustomMessage("bbs.service.successfully"));
		Log.service("ChangePasswordService", player, "change password! Last Ip = " + player.getLastIp());
	}
}
