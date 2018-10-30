CREATE TABLE `character_access` (
  `object_id` int(11) NOT NULL,
  `password_enable` int(11) NOT NULL,
  `password` varchar(16) DEFAULT NULL,
  PRIMARY KEY (`object_id`)
);

CREATE TABLE `character_blocklist` (
	`obj_Id` INT NOT NULL,
	`target_Id` INT NOT NULL,
	PRIMARY KEY  (`obj_Id`,`target_Id`)
) ENGINE=MyISAM;

CREATE TABLE `character_effects` (
	`object_id` INT NOT NULL,
	`skill_id` INT NOT NULL,
	`skill_level` INT NOT NULL,
	`effect_count` INT NOT NULL,
	`effect_cur_time` INT NOT NULL,
	`duration` INT NOT NULL,
	`order` INT NOT NULL,
	`class_index` INT NOT NULL,
	PRIMARY KEY (`object_id`,`skill_id`,`class_index`)
) ENGINE=MyISAM;

CREATE TABLE `character_friends` (
	`char_id` INT NOT NULL DEFAULT '0',
	`friend_id` INT NOT NULL DEFAULT '0',
	PRIMARY KEY  (`char_id`,`friend_id`)
) ENGINE=MyISAM;

CREATE TABLE `character_group_reuse` (
  `object_id` int(11) NOT NULL,
  `reuse_group` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `end_time` bigint(20) NOT NULL,
  `reuse` bigint(20) NOT NULL,
  PRIMARY KEY (`object_id`,`reuse_group`)
);

CREATE TABLE `character_dyes` (
	`char_obj_id` INT NOT NULL DEFAULT '0',
	`symbol_id` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`slot` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`class_index` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	INDEX (`char_obj_id`)
) ENGINE=MyISAM;

CREATE TABLE `character_instances` (
	`obj_id` INT NOT NULL DEFAULT '0',
	`id` INT NOT NULL DEFAULT '0',
	`reuse` BIGINT(20) NOT NULL DEFAULT '0',
	UNIQUE KEY `prim` (`obj_id`,`id`),
	KEY `obj_id` (`obj_id`)
) ENGINE=MyISAM;

CREATE TABLE `character_macroses` (
	`char_obj_id` INT NOT NULL DEFAULT '0',
	`id` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
	`icon` TINYINT UNSIGNED,
	`name` VARCHAR(40) CHARACTER SET UTF8 DEFAULT NULL,
	`descr` VARCHAR(80) CHARACTER SET UTF8 DEFAULT NULL,
	`acronym` VARCHAR(4) CHARACTER SET UTF8 DEFAULT NULL,
	`commands` VARCHAR(1024) CHARACTER SET UTF8 DEFAULT NULL,
	PRIMARY KEY  (`char_obj_id`,`id`)
) ENGINE=MyISAM;

CREATE TABLE `character_mail` (
  `char_id` int(11) NOT NULL,
  `message_id` int(11) NOT NULL,
  `is_sender` tinyint(1) NOT NULL,
  PRIMARY KEY (`char_id`,`message_id`),
  KEY `message_id` (`message_id`),
  FOREIGN KEY (`message_id`) REFERENCES `mail` (`message_id`) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE `character_minigame_score` (
	`object_id` int(11) NOT NULL,
	`score` int(11) NOT NULL,
	PRIMARY KEY (`object_id`)
);

CREATE TABLE `character_post_friends`(
  `object_id` INT(11) NOT NULL,
  `post_friend` INT(11) NOT NULL,
  PRIMARY KEY (`object_id`,`post_friend`)
);

CREATE TABLE `character_premium_items` (
	`charId` INT(11) NOT NULL,
	`itemNum` INT(11) NOT NULL,
	`itemId` INT(11) NOT NULL,
	`itemCount` BIGINT(20) UNSIGNED NOT NULL,
	`itemSender` VARCHAR(50) NOT NULL,
	KEY `charId` (`charId`),
	KEY `itemNum` (`itemNum`)
);

CREATE TABLE `character_products` (
  `char_Id` int(11) NOT NULL,
  `product_Id` int(11) NOT NULL,
  KEY `char_Id` (`char_Id`),
  KEY `product_Id` (`product_Id`)
);

CREATE TABLE `character_quests` (
  `char_id` int(11) NOT NULL,
  `quest_id` int(11) NOT NULL,
  `var` varchar(100) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`char_id`,`quest_id`,`var`),
  KEY `char_id` (`char_id`),
  KEY `quest_id` (`quest_id`)
);

CREATE TABLE `character_recipebook` (
	`char_id` int(11) NOT NULL,
	`id` SMALLINT UNSIGNED NOT NULL,
	PRIMARY KEY  (`id`,`char_id`)
) ENGINE=MyISAM;

CREATE TABLE `character_servitors` (
  `object_id` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `servitor_id` int(11) NOT NULL,
  PRIMARY KEY (`object_id`,`servitor_id`)
);

CREATE TABLE `character_shortcuts` (
	`object_id` INT(11) NOT NULL DEFAULT '0',
	`slot` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`page` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`type` TINYINT UNSIGNED,
	`shortcut_id` int,
	`level` SMALLINT,
	`class_index` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`character_type` INT(11) NOT NULL DEFAULT '1',
	PRIMARY KEY (`object_id`,`slot`,`page`,`class_index`),
	KEY `shortcut_id` (`shortcut_id`)
) ENGINE=MyISAM;

CREATE TABLE `character_skills` (
	`char_obj_id` INT NOT NULL DEFAULT '0',
	`skill_id` INT NOT NULL DEFAULT '0',
	`skill_level` INT NOT NULL DEFAULT '0',
	`class_index` INT NOT NULL DEFAULT '0',
	`entry_type` INT NOT NULL DEFAULT '0',
	PRIMARY KEY  (`char_obj_id`,`skill_id`,`class_index`)
) ENGINE=MyISAM;

CREATE TABLE `character_skills_save` (
	`char_obj_id` INT NOT NULL DEFAULT '0',
	`skill_id` INT UNSIGNED NOT NULL DEFAULT '0',
	`skill_level` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
	`class_index` SMALLINT NOT NULL DEFAULT '0',
	`end_time` bigint NOT NULL DEFAULT '0',
	`reuse_delay_org` INT NOT NULL DEFAULT '0',
	PRIMARY KEY  (`char_obj_id`,`skill_id`,`class_index`)
) ENGINE=MyISAM;

CREATE TABLE `character_subclasses` (
	`char_obj_id` INT NOT NULL,
	`class_id` TINYINT UNSIGNED NOT NULL,
	`level` TINYINT UNSIGNED NOT NULL DEFAULT '1',
	`exp` bigint UNSIGNED NOT NULL DEFAULT '0',
	`sp` bigint UNSIGNED NOT NULL DEFAULT '0',
	`curHp` DECIMAL(9,4)	UNSIGNED NOT NULL DEFAULT '0',
	`curMp` DECIMAL(9,4)	UNSIGNED NOT NULL DEFAULT '0',
	`curCp` DECIMAL(11,4) UNSIGNED NOT NULL DEFAULT '0',
	`maxHp` MEDIUMINT	UNSIGNED NOT NULL DEFAULT '0',
	`maxMp` MEDIUMINT	UNSIGNED NOT NULL DEFAULT '0',
	`maxCp` MEDIUMINT	UNSIGNED NOT NULL DEFAULT '0',
	`active` BOOLEAN NOT NULL DEFAULT '0',
	`isBase` BOOLEAN NOT NULL DEFAULT '0',
	`death_penalty` TINYINT NOT NULL DEFAULT '0',
	`certification` INT NOT NULL,
	PRIMARY KEY  (`char_obj_id`,`class_id`)
) ENGINE=MyISAM;

CREATE TABLE `character_tp_bookmarks` (
  `object_id` int(11) NOT NULL,
  `name` varchar(32) CHARACTER SET utf8 NOT NULL,
  `acronym` varchar(4) CHARACTER SET utf8 NOT NULL,
  `icon` int(3) unsigned NOT NULL,
  `x` int(11) NOT NULL,
  `y` int(11) NOT NULL,
  `z` int(11) NOT NULL,
  PRIMARY KEY (`object_id`,`name`,`x`,`y`,`z`)
);

CREATE TABLE `character_variables` (
	`obj_id` INT NOT NULL DEFAULT '0',
	`type` VARCHAR(86) NOT NULL DEFAULT '0',
	`name` VARCHAR(86) CHARACTER SET UTF8 NOT NULL DEFAULT '0',
	`value` VARCHAR(255) CHARACTER SET UTF8 NOT NULL DEFAULT '0',
	`expire_time` bigint(20) NOT NULL DEFAULT '0',
	UNIQUE KEY `prim` (`obj_id`,`type`,`name`),
	KEY `obj_id` (`obj_id`),
	KEY `type` (`type`),
	KEY `name` (`name`),
	KEY `value` (`value`),
	KEY `expire_time` (`expire_time`)
) ENGINE=MyISAM;

CREATE TABLE `characters` (
	`account_name` VARCHAR(45) NOT NULL DEFAULT '',
	`obj_Id` INT NOT NULL DEFAULT '0',
	`char_name` VARCHAR(35) CHARACTER SET UTF8 NOT NULL DEFAULT '',
	`face` TINYINT UNSIGNED DEFAULT NULL,
	`race` SMALLINT NOT NULL DEFAULT '0',
	`hairStyle` TINYINT UNSIGNED DEFAULT NULL,
	`hairColor` TINYINT UNSIGNED DEFAULT NULL,
	`sex` BOOLEAN DEFAULT NULL,
	`heading` mediumint DEFAULT NULL,
	`x` mediumint DEFAULT NULL,
	`y` mediumint DEFAULT NULL,
	`z` mediumint DEFAULT NULL,
	`karma` INT DEFAULT NULL,
	`pvpkills` INT DEFAULT NULL,
	`pkkills` INT DEFAULT NULL,
	`clanid` INT DEFAULT NULL,
	`createtime` INT UNSIGNED NOT NULL DEFAULT '0',
	`deletetime` INT UNSIGNED NOT NULL DEFAULT '0',
	`title` VARCHAR(16) CHARACTER SET UTF8 DEFAULT NULL,
	`rec_have` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`rec_left` TINYINT UNSIGNED NOT NULL DEFAULT '20',
	`rec_bonus_time` INT NOT NULL DEFAULT '3600',
	`hunt_points` INT NOT NULL DEFAULT '0',
	`hunt_time` INT NOT NULL DEFAULT '14400',
	`accesslevel` TINYINT DEFAULT NULL,
	`online` BOOLEAN DEFAULT NULL,
	`onlinetime` INT UNSIGNED NOT NULL DEFAULT '0',
	`lastAccess` INT UNSIGNED NOT NULL DEFAULT '0',
	`leaveclan`  INT UNSIGNED NOT NULL DEFAULT '0',
	`deleteclan` INT UNSIGNED NOT NULL DEFAULT '0',
	`nochannel` INT NOT NULL DEFAULT '0', -- not UNSIGNED, negative value means 'forever'
	`pledge_type` SMALLINT NOT NULL DEFAULT '-128',
	`pledge_rank` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`lvl_joined_academy` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`apprentice` INT UNSIGNED NOT NULL DEFAULT '0',
	`key_bindings` varbinary(8192) DEFAULT NULL,
	`pcBangPoints` INT NOT NULL DEFAULT '0',
	`vitality` SMALLINT UNSIGNED NOT NULL DEFAULT '20000',
	`fame` INT NOT NULL DEFAULT '0',
	`bookmarks` TINYINT UNSIGNED NOT NULL DEFAULT '9',
	PRIMARY KEY (obj_Id),
	UNIQUE KEY `char_name` (`char_name`),
	KEY `account_name` (`account_name`),
	KEY `clanid` (`clanid`)
) ENGINE=MyISAM;