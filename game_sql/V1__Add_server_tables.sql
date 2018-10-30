CREATE TABLE  `account_bonus` (
  `account` varchar(255) NOT NULL,
  `rate_xp` double NOT NULL,
  `rate_sp` double NOT NULL,
  `rate_adena` double NOT NULL,
  `rate_drop` double NOT NULL,
  `rate_spoil` double NOT NULL,
  `rate_epaulette` double NOT NULL,
  `rate_enchant` double NOT NULL,
  `rate_attribute` double NOT NULL,
  `rate_craft` double NOT NULL,
  `bonus_expire` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`account`)
);

CREATE TABLE `account_lecture_mark` (
	`account` varchar(255) NOT NULL,
	`lecture_mark` int(11) NOT NULL,
	PRIMARY KEY (`account`)
);

CREATE TABLE `ally_data` (
	`ally_id` INT NOT NULL DEFAULT '0',
	`ally_name` VARCHAR(45) CHARACTER SET UTF8 DEFAULT NULL,
	`leader_id` INT NOT NULL DEFAULT '0',
	`expelled_member` INT UNSIGNED NOT NULL DEFAULT '0',
	`crest` VARBINARY(192) NULL DEFAULT NULL,
	PRIMARY KEY  (`ally_id`),
	KEY `leader_id` (`leader_id`)
) ENGINE=MyISAM;

CREATE TABLE `bans` (
	`account_name` VARCHAR(45) DEFAULT NULL,
	`obj_Id` INT UNSIGNED NOT NULL DEFAULT '0',
	`startBanDate` INT UNSIGNED DEFAULT NULL,
	`endBanDate` INT UNSIGNED DEFAULT NULL,
	`reason` VARCHAR(200) CHARACTER SET UTF8 DEFAULT NULL,
    `gmName` VARCHAR(35) CHARACTER SET UTF8 DEFAULT NULL
) ENGINE=MyISAM;

CREATE TABLE `bbs_clannotice` (
`clan_id` INT UNSIGNED NOT NULL,
`type` SMALLINT NOT NULL DEFAULT '0',
`notice` text NOT NULL,
PRIMARY KEY(`clan_id`,`type`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `bbs_favorites` (
`fav_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
`object_id` INT(15) UNSIGNED NOT NULL,
`fav_bypass` VARCHAR(35) NOT NULL,
`fav_title` VARCHAR(100) NOT NULL,
`add_date` INT(15) UNSIGNED NOT NULL,
PRIMARY KEY(`fav_id`),
INDEX(`object_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
ALTER TABLE bbs_favorites ADD UNIQUE INDEX ix_obj_id_bypass (object_id, fav_bypass);

CREATE TABLE `bbs_mail` (
`message_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
`to_name` VARCHAR(35) NOT NULL,
`to_object_id` INT UNSIGNED NOT NULL,
`from_name` VARCHAR(35) NOT NULL,
`from_object_id` INT UNSIGNED NOT NULL,
`title` VARCHAR(128) NOT NULL,
`message` TEXT NOT NULL,
`post_date` INT(15) UNSIGNED NOT NULL,
`read` SMALLINT NOT NULL DEFAULT '0',
`box_type` SMALLINT NOT NULL DEFAULT '0',
PRIMARY KEY(`message_id`),
INDEX(`to_object_id`),
INDEX(`from_object_id`),
INDEX(`read`),
INDEX(`box_type`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `bbs_memo` (
`memo_id` int(11) NOT NULL auto_increment,
`account_name` varchar(45) NOT NULL,
`char_name` varchar(35) NOT NULL,
`ip` varchar(16) NOT NULL,
`title` varchar(128) NOT NULL,
`memo` text NOT NULL,
`post_date` INT(15) UNSIGNED NOT NULL,
PRIMARY KEY(`memo_id`),
INDEX(account_name)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `bot_report` (
	`report_id` int(10) NOT NULL auto_increment,
	`reported_name` varchar(45) DEFAULT NULL,
	`reported_objectId` int(10) DEFAULT NULL,
	`reporter_name` varchar(45) DEFAULT NULL,
	`reporter_objectId` int(10) DEFAULT NULL,
	`date` DECIMAL(20,0) NOT NULL default 0,
	`read` enum('true','false') DEFAULT 'false' NOT NULL,
PRIMARY KEY (`report_id`)
);

CREATE TABLE `bot_reported_punish` (
	`charId` int(11) NOT NULL DEFAULT '0',
	`punish_type` varchar(45) DEFAULT NULL,
	`time_left` bigint(20) DEFAULT NULL,
PRIMARY KEY (`charId`)
) ENGINE=MyISAM;

CREATE TABLE `castle_damage_zones` (
  `residence_id` int(11) NOT NULL,
  `zone` varchar(255) NOT NULL,
  PRIMARY KEY (`residence_id`,`zone`)
);

CREATE TABLE `castle_door_upgrade` (
  `door_id` int(11) NOT NULL,
  `hp` int(11) NOT NULL,
  PRIMARY KEY (`door_id`)
);

CREATE TABLE `castle_hired_guards` (
  `residence_id` int(11) NOT NULL DEFAULT '0',
  `item_id` int(11) NOT NULL DEFAULT '0',
  `x` int(11) NOT NULL DEFAULT '0',
  `y` int(11) NOT NULL DEFAULT '0',
  `z` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`residence_id`,`item_id`,`x`,`y`, `z`),
  KEY `id` (`residence_id`)
);

CREATE TABLE `castle_manor_procure` (
	`castle_id` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`crop_id` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
	`can_buy` BIGINT NOT NULL DEFAULT '0',
	`start_buy` BIGINT NOT NULL DEFAULT '0',
	`price` BIGINT NOT NULL DEFAULT '0',
	`reward_type` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`period` INT NOT NULL DEFAULT '1',
	PRIMARY KEY  (`castle_id`,`crop_id`,`period`)
) ENGINE=MyISAM;

CREATE TABLE `castle_manor_production` (
	`castle_id` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`seed_id` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
	`can_produce` BIGINT NOT NULL DEFAULT '0',
	`start_produce` BIGINT NOT NULL DEFAULT '0',
	`seed_price` BIGINT NOT NULL DEFAULT '0',
	`period` INT NOT NULL DEFAULT '1',
	PRIMARY KEY  (`castle_id`,`seed_id`,`period`)
) ENGINE=MyISAM;

CREATE TABLE `couples` (
	`id` INT NOT NULL,
	`player1Id` INT NOT NULL DEFAULT '0',
	`player2Id` INT NOT NULL DEFAULT '0',
	`maried` VARCHAR(5) DEFAULT NULL,
	`affiancedDate` bigint DEFAULT '0',
	`weddingDate` bigint DEFAULT '0',
	PRIMARY KEY  (`id`)
) ENGINE=MyISAM;

CREATE TABLE `cursed_weapons` (
	`item_id` SMALLINT UNSIGNED NOT NULL,
	`player_id` INT UNSIGNED NOT NULL DEFAULT '0',
	`player_karma` INT UNSIGNED NOT NULL DEFAULT '0',
	`player_pkkills` INT UNSIGNED NOT NULL DEFAULT '0',
	`nb_kills` INT UNSIGNED NOT NULL DEFAULT '0',
	`x` INT NOT NULL DEFAULT '0',
	`y` INT NOT NULL DEFAULT '0',
	`z` INT NOT NULL DEFAULT '0',
	`end_time` INT UNSIGNED NOT NULL DEFAULT '0',
	PRIMARY KEY (`item_id`)
) ENGINE=MyISAM;

CREATE TABLE `epic_boss_spawn` (
	`bossId` SMALLINT UNSIGNED NOT NULL,
	`respawnDate` INT NOT NULL,
	`state` INT NOT NULL,
	PRIMARY KEY  (`bossId`)
) ENGINE=MyISAM;

REPLACE INTO `epic_boss_spawn` (`bossId`, `respawnDate`, `state`) VALUES
(29068,'0',0),
(29020,'0',0),
(29028,'0',0),
(29062,'0',0),
(29065,'0',0),
(29099,'0',0);

CREATE TABLE `event_data` (
  `charId` int(15) NOT NULL,
  `score` int(5) DEFAULT NULL,
  PRIMARY KEY (`charId`)
) ENGINE=MyISAM;

CREATE TABLE `fishing_championship` (
  `PlayerName` varchar(35) CHARACTER SET utf8 NOT NULL,
  `fishLength` double(10,3) NOT NULL,
  `rewarded` int(1) NOT NULL
) ENGINE=MyISAM;

CREATE TABLE `games` (
	`id` INT NOT NULL DEFAULT '0',
	`idnr` INT NOT NULL DEFAULT '0',
	`number1` INT NOT NULL DEFAULT '0',
	`number2` INT NOT NULL DEFAULT '0',
	`prize` INT NOT NULL DEFAULT '0',
	`newprize` INT NOT NULL DEFAULT '0',
	`prize1` INT NOT NULL DEFAULT '0',
	`prize2` INT NOT NULL DEFAULT '0',
	`prize3` INT NOT NULL DEFAULT '0',
	`enddate` decimal(20,0) NOT NULL DEFAULT '0',
	`finished` INT NOT NULL DEFAULT '0',
	PRIMARY KEY (id, idnr)
) ENGINE=MyISAM;

CREATE TABLE `pet_effects` (
  `object_id` int(11) NOT NULL,
  `skill_id` int(11) NOT NULL,
  `skill_level` int(11) NOT NULL,
  `effect_count` int(11) NOT NULL,
  `effect_cur_time` int(11) NOT NULL,
  `duration` int(11) NOT NULL,
  `order` int(11) NOT NULL,
  PRIMARY KEY (`object_id`,`skill_id`)
);

CREATE TABLE `petitions` (
	`serv_id` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0',
	`act_time` int(10) UNSIGNED NOT NULL DEFAULT '0',
	`petition_type` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0',
	`actor` int(10) UNSIGNED NOT NULL DEFAULT '0',
	`location_x` mediumint(9) DEFAULT NULL,
	`location_y` mediumint(9) DEFAULT NULL,
	`location_z` SMALLINT(6) DEFAULT NULL,
	`petition_text` text CHARACTER SET UTF8 NOT NULL,
	`STR_actor` VARCHAR(50) CHARACTER SET UTF8 DEFAULT NULL,
	`STR_actor_account` VARCHAR(50) CHARACTER SET UTF8 DEFAULT NULL,
	`petition_status` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0',
	KEY `actor` (`actor`),
	KEY `petition_status` (`petition_status`),
	KEY `petition_type` (`petition_type`),
	KEY `serv_id` (`serv_id`)
) ENGINE=MyISAM;

CREATE TABLE `pets` (
  `object_id` int(11) NOT NULL,
  `name` varchar(20) CHARACTER SET utf8 DEFAULT NULL,
  `current_hp` int(11) NOT NULL,
  `current_mp` int(11) NOT NULL,
  `exp` bigint(20) DEFAULT NULL,
  `sp` int(10) unsigned DEFAULT NULL,
  `current_life` int(11) NOT NULL,
  PRIMARY KEY (`object_id`)
);

CREATE TABLE `summon_effects` (
  `object_id` int(11) NOT NULL,
  `call_skill_id` int(11) NOT NULL,
  `skill_id` int(11) NOT NULL,
  `skill_level` int(11) NOT NULL,
  `effect_count` int(11) NOT NULL,
  `effect_cur_time` int(11) NOT NULL,
  `duration` int(11) NOT NULL,
  `order` int(11) NOT NULL,
  PRIMARY KEY (`object_id`,`call_skill_id`,`skill_id`)
);

CREATE TABLE `summons` (
  `object_id` int(11) NOT NULL,
  `call_skill_id` int(11) NOT NULL DEFAULT '0',
  `current_hp` int(11) NOT NULL,
  `current_mp` int(11) NOT NULL,
  `current_life` int(11) NOT NULL,
  `max_life` int(11) NOT NULL DEFAULT '0',
  `npc_id` int(11) NOT NULL DEFAULT '0',
  `exp_penalty` double NOT NULL DEFAULT '0',
  `item_consume_id` int(11) NOT NULL DEFAULT '0',
  `item_consume_count` int(11) NOT NULL DEFAULT '0',
  `item_consume_delay` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`object_id`,`call_skill_id`)
);

CREATE TABLE `mail` (
  `message_id` int(11) NOT NULL AUTO_INCREMENT,
  `sender_id` int(11) NOT NULL,
  `sender_name` varchar(32) CHARACTER SET utf8 NOT NULL,
  `receiver_id` int(10) NOT NULL,
  `receiver_name` varchar(32) CHARACTER SET utf8 NOT NULL,
  `expire_time` int(11) NOT NULL,
  `topic` tinytext CHARACTER SET utf8 NOT NULL,
  `body` text CHARACTER SET utf8 NOT NULL,
  `price` bigint(20) NOT NULL,
  `type` int(10) NOT NULL ,
  `unread` tinyint(4) NOT NULL DEFAULT '1',
  PRIMARY KEY (`message_id`),
  KEY `sender_id` (`sender_id`),
  KEY `receiver_id` (`receiver_id`)
) ENGINE=InnoDB;

CREATE TABLE `mail_attachments` (
  `message_id` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  UNIQUE KEY `item_id` (`item_id`),
  KEY `messageId` (`message_id`),
  FOREIGN KEY (`message_id`) REFERENCES `mail` (`message_id`) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE `raidboss_points` (
	`owner_id` INT NOT NULL,
	`boss_id` SMALLINT UNSIGNED NOT NULL,
	`points` INT NOT NULL DEFAULT '0',
	KEY `owner_id` (`owner_id`),
	KEY `boss_id` (`boss_id`)
) ENGINE=MyISAM;

CREATE TABLE `raidboss_status` (
	`id` INT NOT NULL,
	`current_hp` INT DEFAULT NULL,
	`current_mp` INT DEFAULT NULL,
	`respawn_delay` INT NOT NULL DEFAULT '0',
	PRIMARY KEY  (`id`)
) ENGINE=MyISAM;

CREATE TABLE `server_variables` (
	`name` VARCHAR(86) NOT NULL DEFAULT '',
	`value` VARCHAR(255) CHARACTER SET UTF8 NOT NULL DEFAULT '',
	PRIMARY KEY (`name`)
) ENGINE=MyISAM;

CREATE TABLE `siege_clans` (
  `residence_id` int(11) NOT NULL DEFAULT '0',
  `clan_id` int(11) NOT NULL DEFAULT '0',
  `type` varchar(255) NOT NULL,
  `param` bigint(20) NOT NULL,
  `date` bigint(20) NOT NULL,
  PRIMARY KEY (`residence_id`,`clan_id`)
);

CREATE TABLE `siege_players` (
  `residence_id` int(11) NOT NULL,
  `object_id` int(11) NOT NULL,
  `clan_id` int(11) NOT NULL,
  PRIMARY KEY (`residence_id`,`object_id`,`clan_id`)
);

CREATE TABLE `vote` (
	`id` INT(10) NOT NULL DEFAULT '0',
	`HWID` VARCHAR(32) NOT NULL DEFAULT '',
	`vote` INT(10) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`, `HWID`, `vote`),
	INDEX `Index 2` (`id`, `vote`),
	INDEX `Index 3` (`id`),
	INDEX `Index 4` (`HWID`)
) ENGINE=MyISAM;

CREATE TABLE `hwid_locks` (
  `account_name` varchar(20) NOT NULL,
  `hwid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`account_name`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

