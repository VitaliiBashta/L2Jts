package services.vote;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.mmocore.commons.database.dbutils.DbUtils;
import org.mmocore.gameserver.ThreadPoolManager;
import org.mmocore.gameserver.configuration.config.L2TopConfig;
import org.mmocore.gameserver.database.DatabaseFactory;
import org.mmocore.gameserver.database.dao.impl.CharacterDAO;
import org.mmocore.gameserver.listener.script.OnInitScriptListener;
import org.mmocore.gameserver.network.authcomm.AuthServerCommunication;
import org.mmocore.gameserver.network.authcomm.gs2as.RequestPlayerGamePointIncrease;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.templates.item.ItemTemplate;
import org.mmocore.gameserver.utils.ItemFunctions;
import org.mmocore.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

/**
 * @author Visor123, KilRoy
 */
public class MMOTopVote implements OnInitScriptListener
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MMOTopVote.class);
	private static final String SELECT_QUERY = "SELECT isOpen FROM mmotop_month WHERE year=? AND month=?";
	private static final String SELECT_QUERY_1 = "SELECT id FROM mmotop_vote";
	private static final String SELECT_QUERY_2 = "SELECT COUNT(voteCount) as cvc FROM mmotop_vote";
	private static final String UPDATE_QUERY = "UPDATE mmotop_month SET isOpen = 0, voteCount =?, day=? WHERE year=? AND month=?";
	private static final String INSERT_QUERY = "INSERT INTO items_delayed (owner_id, item_id, count, description) VALUES(?,?,?,?)";
	private static final String REPLACE_QUERY = "REPLACE INTO mmotop_month(year,month,day,voteCount,isOpen) VALUES(?,?,?,0,1)";
	private static final String REPLACE_QUERY_1 = "REPLACE INTO mmotop_vote(id,Date,ip,char_name,voteCount) VALUES(?,?,?,?,?)";
	private static final String TRUNCATE_QUERY = "TRUNCATE mmotop_month";

	@Override
	public void onInit()
	{
		if(L2TopConfig.MMOTOP_ENABLED)
			ThreadPoolManager.getInstance().scheduleAtFixedRate(new AutoBonusTask(), 60000, L2TopConfig.MMOTOP_PARSER_DELAY * 60 * 1000);
	}
	
	private class AutoBonusTask implements Runnable
	{
		@Override
		public void run()
		{
			Connection con = null;
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			try
			{
				con = DatabaseFactory.getInstance().getConnection();
				
				boolean isOpen = getVoteIsOpen(cal);
				int voteCount = 0;
				
				int day = Integer.valueOf(DateFormatUtils.format(cal, "dd")).intValue();
				if(day == 1)
				{
					Calendar cal1 = Calendar.getInstance();
					cal1.setTimeInMillis(cal.getTimeInMillis());
					cal1.add(Calendar.DAY_OF_MONTH, -1);
					boolean oldIsOpen = getVoteIsOpen(cal1);
					LOGGER.info("AutoBonusTask FIRST day of month, old day="+day+" oldIsOpen="+oldIsOpen);
					if(oldIsOpen)
						closeVoteMonth(cal1);
				}
				
				if(isOpen)
					voteCount = checkAndGetNewVote(cal);
				if(voteCount != 0)
					LOGGER.info("AutoBonusTask MMOTop recive bonus for voteCount="+voteCount);
			}
			catch(Exception se)
			{
				LOGGER.warn("SQLException AutoBonusTask MMOTop "+se);
			}
			finally
			{
				DbUtils.closeQuietly(con);
			}
		}
	}

	private static boolean getVoteIsOpen(final Calendar cal)
	{
		Connection con = null;
		PreparedStatement statement = null;
		PreparedStatement statement1 = null;
		ResultSet rset = null;
		boolean isExists = false;
		boolean isOpen = true;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			
			int year = Integer.valueOf(DateFormatUtils.format(cal, "yyyy")).intValue();
			int month = Integer.valueOf(DateFormatUtils.format(cal, "MM")).intValue();
			int day = Integer.valueOf(DateFormatUtils.format(cal, "dd")).intValue();
			statement = con.prepareStatement(SELECT_QUERY);
			statement.setInt(1, year);
			statement.setInt(2, month);
			rset = statement.executeQuery();
			while(rset.next()) 
			{
				isExists = true;
				isOpen = rset.getBoolean("isOpen");
			}
			if(!isExists)
			{
				statement1 = con.prepareStatement(REPLACE_QUERY);
				statement1.setInt(1, year);
				statement1.setInt(2, month);
				statement1.setInt(3, day);
				statement1.execute();
				DbUtils.closeQuietly(statement1);
			}
			
		}
		catch(SQLException se)
		{
			LOGGER.info("SQLException getVoteIsOpen: "+se.getMessage());
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return isOpen;
	}

	private static void closeVoteMonth(final Calendar cal)
	{
		Connection con = null;
		PreparedStatement statement = null;
		PreparedStatement statement1 = null;
		PreparedStatement statement2 = null;
		PreparedStatement statement3 = null;
		ResultSet rset = null;
		ResultSet rset1 = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			int year = Integer.valueOf(DateFormatUtils.format(cal, "yyyy")).intValue();
			int month = Integer.valueOf(DateFormatUtils.format(cal, "MM")).intValue();
			int day = Integer.valueOf(DateFormatUtils.format(cal, "dd")).intValue();
			final TIntList t = new TIntArrayList();
			statement = con.prepareStatement(SELECT_QUERY_1);
			rset = statement.executeQuery();
			while(rset.next()) 
			{
				t.add(rset.getInt("id"));
			}

			URL u = new URL(L2TopConfig.MMOTOP_VOTE_FILE_URL);
			BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));
			String inputLine;

			int hitCount = 0;
			int vc = 0;
			while((inputLine = in.readLine()) != null)
			{
			    String s = "";
				if(inputLine.indexOf("\t") != -1)
				{
					int idx = inputLine.indexOf("\t");
					s = inputLine.substring(0, idx);
					if(s != "")
					{
						int id = Integer.valueOf(s).intValue();
						if (t.contains(id))
						{
							hitCount ++;
						}
					}
				}
				vc ++;
			}
			in.close();
			
			if(hitCount < Math.round(vc/2)) // vote log not update
				return;

			statement1 = con.prepareStatement(SELECT_QUERY_2);
			rset1 = statement1.executeQuery();
			int voteCount = 0;
			while(rset1.next())
			{
				voteCount = rset1.getInt("cvc");
			}
			DbUtils.closeQuietly(statement1, rset1);

			statement2 = con.prepareStatement(TRUNCATE_QUERY);
			statement2.execute();
			DbUtils.closeQuietly(statement2);

			statement3 = con.prepareStatement(UPDATE_QUERY);
			statement3.setInt(1, voteCount);
			statement3.setInt(2, day);
			statement3.setInt(3, year);
			statement3.setInt(4, month);
			statement3.execute();
			DbUtils.closeQuietly(statement3);
		}
		catch(Exception se)
		{
			LOGGER.info("Exception closeVoteMonth:"+se.getMessage());
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

	private static int checkAndGetNewVote(final Calendar cal)
	{
		Connection con = null;
		PreparedStatement statement = null;
		PreparedStatement statement1 = null;
		PreparedStatement statement2 = null;
		ResultSet rset = null;
		final TIntList t = new TIntArrayList();
		int voteCount = 0;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();

			statement = con.prepareStatement(SELECT_QUERY_1);
			rset = statement.executeQuery();
			while(rset.next()) 
			{
				t.add(rset.getInt("id"));
			}

			URL u = new URL(L2TopConfig.MMOTOP_VOTE_FILE_URL);
			BufferedReader in = new BufferedReader(new InputStreamReader(u.openStream()));
			String inputLine;

			while((inputLine = in.readLine()) != null)
			{
			    String s = "";
				if(inputLine.indexOf("\t") != -1)
				{
					int idx = inputLine.indexOf("\t");
					s = inputLine.substring(0, idx);
					if(s != "")
					{
						int id = Integer.valueOf(s).intValue();
						if(!t.contains(id))
						{
							inputLine = inputLine.substring(idx+1);
							idx = inputLine.indexOf("\t");
							String d = inputLine.substring(0, idx);
							inputLine = inputLine.substring(idx+1);
							idx = inputLine.indexOf("\t");
							String ip = inputLine.substring(0, idx);
							inputLine = inputLine.substring(idx+1);
							idx = inputLine.indexOf("\t");
							String cn = inputLine.substring(0, idx);
							inputLine = inputLine.substring(idx+1);
							int vc = Integer.valueOf(inputLine).intValue();
							if(vc <= 0)
								vc = 1;

							statement1 = con.prepareStatement(REPLACE_QUERY_1);
							statement1.setInt(1, id);
							statement1.setString(2, d);
							statement1.setString(3, ip);
							statement1.setString(4, cn);
							statement1.setInt(5, vc);
							statement1.execute();
							DbUtils.closeQuietly(statement1);

							Player player = World.getPlayer(cn);
							if(player != null)
							{
								if(L2TopConfig.rewardItem == ItemTemplate.PREMIUM_POINTS)
									AuthServerCommunication.getInstance().sendPacket(new RequestPlayerGamePointIncrease(player, L2TopConfig.rewardCount, false));
								else
									ItemFunctions.addItem(player, L2TopConfig.rewardItem, L2TopConfig.rewardCount);
								player.sendMessage(player.isLangRus() ? "Вам было начислено вознаграждение за голосование в рейтинге MMOTop.ru" : "You were charged a fee for voting rating MMOTop.ru");
								voteCount += vc;
							}
							else
							{
								int charId = CharacterDAO.getInstance().getObjectIdByName(cn);
								
								if(charId > 0)
								{
									statement2 = con.prepareStatement(INSERT_QUERY);
									statement2.setInt(1, charId);
									statement2.setInt(2, L2TopConfig.rewardItem);
									statement2.setInt(3, L2TopConfig.rewardCount);
									statement2.setString(4, "MMOTop vote");
									statement2.execute();
									DbUtils.closeQuietly(statement2);
								}
								else
								{
									//LOGGER.info("checkAndGetNewVote id="+id+" char name="+cn+" not found in base");
								}
							}
						}
					}
				}
			}
			in.close();
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
		return voteCount;
	}
}