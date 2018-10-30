package services;

import org.mmocore.commons.threading.RunnableImpl;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.ServicesConfig;
import org.mmocore.gameserver.handler.bypass.Bypass;
import org.mmocore.gameserver.listener.actor.player.OnMailActivationListener;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.model.instances.NpcInstance;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.CheckEmailRequest;
import org.mmocore.gameserver.network.authcomm.gs2as.UpdateEmailRequest;
import org.mmocore.gameserver.network.lineage.components.HtmlMessage;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.PlayerListenerList;
import org.mmocore.gameserver.utils.Location;
import org.mmocore.gameserver.utils.Log;
import org.mmocore.gameserver.world.GameObjectsStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: STIGMATED   16.12.11
 */
public class MailActivation implements OnInitScriptListener
{

	private static final Logger _log = LoggerFactory.getLogger(MailActivation.class);
	private static final long _reTask = 4800000;        // 4800000 = 1 час 20 мин
	private static final OnMailActivationListener listener = player -> {
		sendHtml(player);
		if(!player.entering || !player.getReflection().isDefault() || player.isTerritoryFlagEquipped() || player.isInOlympiadMode() || player.isInObserverMode())
		{
			return;
		}
		player.teleToLocation(Location.findPointToStay(-14376, 123592, -3142, 100, 100, player.getGeoIndex()));

	};

	@Override
	public void onInit()
	{
		if(ServicesConfig.SERVICE_ENABLED_MAIL_ACTIVATION_SERVICE)
		{
			_log.info("Loaded Mail Activation Services");
			PlayerListenerList.addGlobal(listener);
			rescheduleCheck();
		}
	}

	public static void checkPlayers()
	{
		GameObjectsStorage.getPlayers().stream().filter(player -> !player.isInOfflineMode()).forEach(services.MailActivation::checkPlayer);
		rescheduleCheck();
	}

	// отправляем запрос на логин-сервер
	private static void checkPlayer(Player player)
	{
		AuthServerCommunication.getInstance().sendPacket(
				new CheckEmailRequest(player.getAccountName()));
	}

	private static void rescheduleCheck()
	{
		ThreadPoolManager.getInstance().schedule(new RunnableImpl()
		{
			@Override
			public void runImpl() throws Exception
			{
				checkPlayers();
			}
		}, _reTask);
	}

	private static void sendHtml(Player player)
	{
		HtmlMessage notice = new HtmlMessage(5);
		StringBuilder msg = new StringBuilder("<html><body>");
		msg.append("<table border=0 cellpadding=10 cellspacing=3 width=292 height=358 background=\"L2UI_CH3.refinewnd_back_Pattern\">"
		           + "<tr><td valign=\"top\" align=\"center\">"
		           + "Здравствуйте, <font color=\"LEVEL\">"
		           + player.getName()
		           + "</font><br>"
		           + "<font color=\"aadd77\">Возможно сейчас ваш аккаунт взламывают</font> !<br>"
		           + "У вас не установлен почтовый адрес (EMAIL).<br>"
		           + "Введите свой адрес электронной почты.<br>"
		           + "Вам будет отправлено письмо. Прочтите его и в течении 30 минут подтвердите.<br>"
		           + "<br>"
		           + "Пример: user@gmail.com<br>"
		           + "<table><tr>"
		           + "<td><multiedit var=\"acc\" width=200 height=12></td>"
		           + "</tr></table><br>"
		           + "<button value=\"Установить Почту\" action=\"bypass -h htmbypass_services.MailActivation:activate $acc\" width=200 height=31 back=\"L2UI_CT1.OlympiadWnd_DF_Info_Down\" fore=\"L2UI_CT1.OlympiadWnd_DF_Info\">"
		           + "<br>"
		           + "Установите почту и сообщение не будет появляться.<br>"
		           + "</td></tr></table>"
		           + "</body></html>");

		notice.setHtml(msg.toString());
		player.sendPacket(notice);
	}

	@Bypass("services.MailActivation:activate")
	public void activate(Player player, NpcInstance npc, String[] param)
	{
		String _acc = param[0];
		if(_acc.length() < 41)
		{
			String mail = String.valueOf(param[0]);
			setAccountMail(player, mail);
		}
		else
		{
			sendHtml(player);
			player.sendMessage("Адрес эл. почты не должен быть длиннее 40 символов");
		}
	}

	private void setAccountMail(Player player, String email)
	{
		if(player == null)
		{
			return;
		}

		if(email == null || email.isEmpty())
		{
			sendHtml(player);
			return;
		}

		String _account = player.getAccountName();
		//LOGGER.info(_account + " " + email);
		Log.add("Character: " + player.getName() + " acc:" + _account + " set email " + email, "MailActivation");

		activationPage(player, email);
		AuthServerCommunication.getInstance().sendPacket(new UpdateEmailRequest(_account, email, 1));
	}

	private void activationPage(Player player, String email)
	{
		HtmlMessage notice = new HtmlMessage(5);
		StringBuilder msg = new StringBuilder("<html><body>");
		msg.append("<table border=0 cellpadding=10 cellspacing=3 width=292 height=358 background=\"L2UI_CH3.refinewnd_back_Pattern\">\n" +
		           "<tr><td valign=\"top\" align=\"center\">\n" +
		           "Cпасибо, <font color=\"LEVEL\">");
		msg.append(player.getName());
		msg.append("</font> !<br>\n" +
		           "Теперь зайдите на почту в течении 30 минут и подтвердите Ваш EMail<br>");
		msg.append(email);
		msg.append("<br>\n" +
		           "Установите почту и сообщение не будет появляться.\n" +
		           "</td></tr></table>\n" +
		           "</body></html>");
		notice.setHtml(msg.toString());
		player.sendPacket(notice);
	}

}
