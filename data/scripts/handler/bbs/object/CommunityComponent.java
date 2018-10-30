package handler.bbs.object;
import handler.bbs.dao.buffer.CommunityBufferDAO;
import handler.bbs.dao.leaseTransform.CommunityLeaseTransformDAO;
import handler.bbs.dao.teleport.CommunityTeleportDAO;
import org.jts.dataparser.data.holder.transform.TCombat;
import org.jts.dataparser.data.holder.transform.type.TransformType;
import org.mmocore.gameserver.configuration.config.community.CBasicConfig;
import org.mmocore.gameserver.configuration.config.community.CBufferConfig;
import org.mmocore.gameserver.configuration.config.community.CServiceConfig;
import org.mmocore.gameserver.configuration.config.community.CTeleportConfig;
import org.mmocore.gameserver.data.xml.holder.custom.community.CLeaseTransformHolder;
import org.mmocore.gameserver.object.Player;
import org.mmocore.gameserver.object.components.player.community.ICommunityComponent;
import org.mmocore.gameserver.object.components.player.community.Scheme;
import org.mmocore.gameserver.object.components.player.community.TeleportPoint;
import org.mmocore.gameserver.object.components.player.community.enums.DBState;
import org.mmocore.gameserver.templates.custom.community.leaseTransform.BonusTemplate;
import org.mmocore.gameserver.templates.custom.community.leaseTransform.LeaseTransformTemplate;

import java.util.*;

/**
 * Create by Mangol on 13.12.2015.
 */
public class CommunityComponent implements ICommunityComponent
{
	private static final CommunityBufferDAO bufferDao = CommunityBufferDAO.getInstance();
	private static final CommunityTeleportDAO teleportDao = CommunityTeleportDAO.getInstance();
	private static final CommunityLeaseTransformDAO leaseTransformDAO = CommunityLeaseTransformDAO.getInstance();
	private final Player player;
	private final Map<Integer, TeleportPoint> teleportPoints = new LinkedHashMap<>();
	private final Map<String, Scheme> bufferScheme = new LinkedHashMap<>();
	private Stack<String> saveBypassStack;
	private int leaseTransformId;

	public CommunityComponent(final Player player)
	{
		this.player = player;
	}

	public void restore()
	{
		if(!CBasicConfig.COMMUNITYBOARD_ENABLED)
		{
			return;
		}
		loadTeleportPoints();
		loadBufferScheme();
		loadLeaseTransform();
	}


	public void loadBufferScheme()
	{
		if(!CBufferConfig.enableBuffer)
		{
			return;
		}

		bufferDao.select(player);
	}

	public void loadTeleportPoints()
	{
		if(!CTeleportConfig.allowTeleport)
		{
			return;
		}
		teleportDao.selectTeleportPoint(player);
	}

	public void loadLeaseTransform()
	{
		if(!CServiceConfig.allowLeaseTransform)
		{
			return;
		}
		setLeaseTransformId(leaseTransformDAO.select(player), DBState.none);
		if(leaseTransformId > 0)
		{
			refreshLeaseTransformStats();
		}
	}

	public void deleteTeleportId(final int teleportId)
	{
		teleportPoints.remove(teleportId);
	}

	public void addTeleportId(final TeleportPoint teleportPoint)
	{
		teleportPoints.put(teleportPoint.getId(), teleportPoint);
	}

	public Map<Integer, TeleportPoint> getTeleportPoints()
	{
		return teleportPoints;
	}

	public void deleteBuffScheme(final String schemeName)
	{
		bufferScheme.remove(schemeName);
	}

	public void addBuffScheme(final Scheme scheme)
	{
		bufferScheme.put(scheme.getName(), scheme);
	}

	public Map<String, Scheme> getBuffSchemes()
	{
		return bufferScheme;
	}

	public Optional<Scheme> getBuffScheme(final String schemeName)
	{
		return Optional.ofNullable(bufferScheme.get(schemeName));
	}

	public int getLeaseTransformId()
	{
		return leaseTransformId;
	}

	public void setLeaseTransformId(final int leaseTransformId, final DBState db)
	{
		this.leaseTransformId = leaseTransformId;
		switch(db)
		{
			case insert:
				leaseTransformDAO.insert(player, leaseTransformId);
				break;
			case delete:
				leaseTransformDAO.delete(player);
				break;
			case none:
				break;
		}
	}

	public void refreshLeaseTransformStats()
	{
		if(player.isTransformed())
		{
			if(player.getTransformation().getData().type == TransformType.COMBAT)
			{
				final LeaseTransformTemplate template = CLeaseTransformHolder.getInstance().getLeaseTransform(getLeaseTransformId()).get();
				if(template.getId() == player.getTransformationId())
				{
					final TCombat combat = player.getTransformation().getCombat().clone();
					if(template.getBonus().isPresent())
					{
						final BonusTemplate bonusTemplate = template.getBonus().get();
						combat.setStr(bonusTemplate.getStr());
						combat.setCon(bonusTemplate.getCon());
						combat.setDex(bonusTemplate.getDex());
						combat.setInt(bonusTemplate.getInt());
						combat.setMen(bonusTemplate.getMen());
						combat.setWit(bonusTemplate.getWit());
					}
					player.getTransformation().setTCombat(combat);
					player.sendChanges();
				}
				else
				{
					setLeaseTransformId(0, DBState.delete);
				}
			}
		}
		else
		{
			setLeaseTransformId(0, DBState.delete);
		}
	}

	@Override
	public Stack<String> getStackBypass()
	{
		if(saveBypassStack == null)
		{
			saveBypassStack = new Stack<>();
		}
		return saveBypassStack;
	}
}
