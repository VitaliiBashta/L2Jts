package handler.bbs.custom.privateOffice.engine.enums;
import handler.bbs.custom.privateOffice.engine.interfaces.IEnumComponent;
import org.mmocore.gameserver.configuration.config.community.CServiceConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * @author Mangol
 * @since 10.02.2016
 */
public enum Services implements IEnumComponent {
	none,
	nick_rename(ServiceType.player, "_bbsservice:service:nick_rename:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowNickRename;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.nickRenameAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.nickRenameItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.nickRenameItemCount;
		}

	},
	clear_pk(ServiceType.player, "_bbsservice:service:clear_pk:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowClearPK;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.clearPKAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.clearPKItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.clearPKItemCount;
		}
	},
	clear_karma(ServiceType.player, "_bbsservice:service:clear_karma:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowClearKarma;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.clearKarmaAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.clearKarmaItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.clearKarmaItemCount;
		}
	},
	buy_nobless(ServiceType.player, "_bbsservice:service:buy_nobless:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowNobless;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.noblessAsk;
		}

		@Override
		public int[] getItemIds() {
			return CServiceConfig.noblessItemId;
		}

		@Override
		public long[] getItemCounts() {
			return CServiceConfig.noblessItemCount;
		}
	},
	change_sex(ServiceType.player, "_bbsservice:service:change_sex:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowChangeSex;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.changeSexAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.changeSexItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.changeSexItemCount;
		}
	},
	change_race(ServiceType.player, "_bbsservice:service:change_race:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowChangeRace;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.changeRaceAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.changeRaceItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.changeRaceItemCount;
		}
	},
	change_hair_style(ServiceType.player, "_bbsservice:service:change_hair_style:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowChangeHairStyle;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.changeHairStyleAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.changeHairStyleItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.changeHairStyleItemCount;
		}

		@Override
		public int getWearItemId() {
			return CServiceConfig.changeHairStyleWearItemId;
		}

		@Override
		public long getWearItemCount() {
			return CServiceConfig.changeHairStyleWearItemCount;
		}
	},
	change_hair_color(ServiceType.player, "_bbsservice:service:change_hair_color:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowChangeHairColor;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.changeHairColorAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.changeHairColorItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.changeHairColorItemCount;
		}

		@Override
		public int getWearItemId() {
			return CServiceConfig.changeHairColorWearItemId;
		}

		@Override
		public long getWearItemCount() {
			return CServiceConfig.changeHairColorWearItemCount;
		}
	},
	change_face(ServiceType.player, "_bbsservice:service:change_face:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowChangeFace;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.changeFaceAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.changeFaceItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.changeFaceItemCount;
		}

		@Override
		public int getWearItemId() {
			return CServiceConfig.changeFaceWearItemId;
		}

		@Override
		public long getWearItemCount() {
			return CServiceConfig.changeFaceWearItemCount;
		}
	},
	buy_fame(ServiceType.player, "_bbsservice:service:buy_fame:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowBuyFame;
		}
		@Override
		public boolean isAnswer() {
			return CServiceConfig.buyFameAsk;
		}

		@Override
		public int[] getCounts() {
			return CServiceConfig.fameCounts;
		}

		@Override
		public int[] getItemIds() {
			return CServiceConfig.fameItemIds;
		}

		@Override
		public long[] getItemCounts() {
			return CServiceConfig.famePriceCounts;
		}
	},
	buy_sp(ServiceType.player, "_bbsservice:service:buy_sp:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowBuySP;
		}
		@Override
		public boolean isAnswer() {
			return CServiceConfig.buySPAsk;
		}

		@Override
		public long[] getCountsL() {
			return CServiceConfig.spCounts;
		}

		@Override
		public int[] getItemIds() {
			return CServiceConfig.spItemIds;
		}

		@Override
		public long[] getItemCounts() {
			return CServiceConfig.spPriceCounts;
		}
	},
	buy_recommendation(ServiceType.player, "_bbsservice:service:buy_recommendation:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowBuyRec;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.buyRecAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.buyRecItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.buyRecItemCount;
		}
	},
	buy_level(ServiceType.player, "_bbsservice:service:buy_level:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowBuyLevel;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.buyLevelAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.buyLevelItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.buyLevelItemCount;
		}
	},
	name_color(ServiceType.player, "_bbsservice:service:name_color:content:1") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowNameColor;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.nameColorAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.nameColorItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.nameColorItemCount;
		}
	},
	title_color(ServiceType.player, "_bbsservice:service:title_color:content:1") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowTitleColor;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.titleColorAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.titleColorItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.titleColorItemCount;
		}
	},
	unban_chat(ServiceType.player, "_bbsservice:service:unban_chat:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowUnBanChat;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.unBanChatAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.unBanChatItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.unBanChatItemCount;
		}
	},
	expand_inventory(ServiceType.player, "_bbsservice:service:expand_inventory:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowExpandInventory;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.expandInventoryAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.expandInventoryItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.expandInventoryItemCount;
		}
	},
	expand_warehouse(ServiceType.player, "_bbsservice:service:expand_warehouse:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowExpandWarehouse;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.expandWarehouseAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.expandWarehouseItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.expandWarehouseItemCount;
		}
	},
	expand_private_store(ServiceType.player, "_bbsservice:service:expand_private_store:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowExpandPrivateStore;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.expandPrivateStoreAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.expandPrivateStoreItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.expandPrivateStoreItemCount;
		}
	},
	no_drop_pk(ServiceType.player, "_bbsservice:service:no_drop_pk:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowNoDropPK;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.noDropPKAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.noDropPKItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.noDropPKItemCount;
		}
	},
	restore_pet_name(ServiceType.player, "_bbsservice:service:restore_pet_name:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowRestorePetName;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.restorePetNameAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.restorePetNameItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.restorePetNameItemCount;
		}
	},
	temporal_hero(ServiceType.player, "_bbsservice:service:temporal_hero:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowTemporalHero;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.temporalHeroAsk;
		}

		@Override
		public int[] getDays() {
			return CServiceConfig.temporalHeroDays;
		}

		@Override
		public int[] getItemIds() {
			return CServiceConfig.temporalHeroItemIds;
		}

		@Override
		public long[] getItemCounts() {
			return CServiceConfig.temporalHeroItemCounts;
		}
	},
	clan_rename(ServiceType.clan, "_bbsservice:service:clan_rename:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowClanRename;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.clanRenameAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.clanRenameItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.clanRenameItemCount;
		}
	},
	buy_clan_rep(ServiceType.clan, "_bbsservice:service:buy_clan_rep:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowClanReputation;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.clanReputationAsk;
		}

		@Override
		public int[] getCounts() {
			return CServiceConfig.clanReputationCounts;
		}

		@Override
		public int[] getItemIds() {
			return CServiceConfig.clanReputationItemIds;
		}

		@Override
		public long[] getItemCounts() {
			return CServiceConfig.clanReputationItemCounts;
		}
	},
	buy_clan_level(ServiceType.clan, "_bbsservice:service:buy_clan_level:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowBuyClanLevel;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.buyClanLevelAsk;
		}

		@Override
		public int[] getLevels() {
			return CServiceConfig.buyClanLevels;
		}

		@Override
		public int[] getItemIds() {
			return CServiceConfig.buyClanLevelItemIds;
		}

		@Override
		public long[] getItemCounts() {
			return CServiceConfig.buyClanLevelItemCounts;
		}
	},
	buy_clan_skill(ServiceType.clan, "_bbsservice:service:buy_clan_skill:content:1") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowClanSkill;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.clanSkillAsk;
		}
	},
	premium_account(ServiceType.player, "_bbsservice:service:premium_account:content:home") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.rateBonusType > 0;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.paAsk;
		}
	},
	subscription(ServiceType.player, "_bbsservice:service:subscription:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowSubscription;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.subscriptionAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.subscriptionItemId;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.subscriptionItemCount;
		}

		@Override
		public long getMaxCount() {
			return CServiceConfig.subscriptionMaxBuyHour;
		}
	},
	change_password(ServiceType.player, "_bbsservice:service:change_password:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowChangePassword;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.changePasswordAsk;
		}
	},
	remove_clan_penalty(ServiceType.clan, "_bbsservice:service:remove_clan_penalty:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowClanPenaltyRemove;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.clanPenaltyRemoveAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.clanPenaltyPriceItem;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.clanPenaltyPrice;
		}
	},
	remove_player_penalty(ServiceType.clan, "_bbsservice:service:remove_player_penalty:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowPlayerPenaltyRemove;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.playerPenaltyRemoveAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.playerPenaltyPriceItem;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.playerPenaltyPrice;
		}
	},
	olympiad_reset(ServiceType.player, "_bbsservice:service:olympiad_reset:content") {
		@Override
		public boolean isIncluded() {
			return CServiceConfig.allowOlyReset;
		}

		@Override
		public boolean isAnswer() {
			return CServiceConfig.olyResetAsk;
		}

		@Override
		public int getItemId() {
			return CServiceConfig.olyResetItem;
		}

		@Override
		public long getItemCount() {
			return CServiceConfig.olyResetItemCount;
		}
	};
	private String bypass;
	private ServiceType serviceType;

	Services(final ServiceType serviceType, final String bypass) {
		this.serviceType = serviceType;
		this.bypass = bypass;
	}

	Services() {
	}

	public ServiceType getServiceType() {
		return serviceType;
	}

	public String getContentBypass() {
		return bypass;
	}

	public static List<Services> listServiceType(final ServiceType serviceType) {
		final List<Services> values = new ArrayList<>();
		IntStream.range(0, Services.values().length).filter(s -> Services.values()[s].getServiceType() == serviceType).forEach(i -> values.add(Services.values()[i]));
		return values;
	}

	public static Optional<Services> value(final String name) {
		for(final Services service : Services.values()) {
			if(Objects.equals(service.name(), name)) {
				return Optional.of(service);
			}
		}
		return Optional.empty();
	}
}
