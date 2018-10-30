CREATE TABLE `items` (
  `object_id` int(11) NOT NULL,
  `owner_id` int(11) NOT NULL,
  `item_id` int(7) NOT NULL,
  `count` bigint(20) NOT NULL,
  `enchant_level` int(11) NOT NULL,
  `loc` varchar(32) NOT NULL,
  `loc_data` int(11) NOT NULL,
  `life_time` int(11) NOT NULL,
  `variation_stone_id` int(7) NOT NULL,
  `variation1_id` int(7) NOT NULL,
  `variation2_id` int(7) NOT NULL,
  `attribute_fire` int(11) NOT NULL,
  `attribute_water` int(11) NOT NULL,
  `attribute_wind` int(11) NOT NULL,
  `attribute_earth` int(11) NOT NULL,
  `attribute_holy` int(11) NOT NULL,
  `attribute_unholy` int(11) NOT NULL,
  `custom_type1` int(5) NOT NULL,
  `custom_type2` int(5) NOT NULL,
  `custom_flags` int(11) NOT NULL,
  `agathion_energy` int(11) NOT NULL,
  `agathion_max_energy` int(11) NOT NULL,
  `visual_id` int(11) NOT NULL,
  `is_costume` int(11) NOT NULL,
  PRIMARY KEY  (`object_id`),
  KEY `owner_id` (`owner_id`),
  KEY `loc` (`loc`),
  KEY `item_id` (`item_id`)
) ENGINE=InnoDB;

CREATE TABLE `item_auction` (
  `auctionId` int(11) NOT NULL,
  `instanceId` int(11) NOT NULL,
  `auctionItemId` int(11) NOT NULL,
  `startingTime` bigint(20) NOT NULL,
  `endingTime` bigint(20) NOT NULL,
  `auctionStateId` tinyint(1) NOT NULL,
  PRIMARY KEY (`auctionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `item_auction_bid` (
  `auctionId` int(11) NOT NULL,
  `playerObjId` int(11) NOT NULL,
  `playerBid` bigint(20) NOT NULL,
  PRIMARY KEY (`auctionId`,`playerObjId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `items_delayed` (
	`payment_id` INT NOT NULL auto_increment,
	`owner_id` INT NOT NULL,
	`item_id` INT NOT NULL,
	`count` INT UNSIGNED NOT NULL DEFAULT '1',
	`enchant_level` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
	`attribute` SMALLINT NOT NULL DEFAULT '-1',
	`attribute_level` SMALLINT NOT NULL DEFAULT '-1',
	`flags` INT NOT NULL DEFAULT '0',
	`payment_status` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`description` VARCHAR(255) DEFAULT NULL,
	PRIMARY KEY (`payment_id`),
	KEY `key_owner_id` (`owner_id`),
	KEY `key_item_id` (`item_id`)
) ENGINE=MyISAM;