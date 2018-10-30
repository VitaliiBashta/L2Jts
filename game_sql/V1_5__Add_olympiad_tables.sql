CREATE TABLE `olympiad_nobles` (
  `char_id` int(11) NOT NULL DEFAULT '0',
  `class_id` smallint(6) unsigned NOT NULL DEFAULT '0',
  `olympiad_points` smallint(6) NOT NULL DEFAULT '0',
  `olympiad_points_past` smallint(6) NOT NULL DEFAULT '0',
  `olympiad_points_past_static` smallint(6) NOT NULL DEFAULT '0',
  `competitions_done` smallint(6) unsigned NOT NULL DEFAULT '0',
  `competitions_win` smallint(6) unsigned NOT NULL DEFAULT '0',
  `competitions_loose` smallint(6) unsigned NOT NULL DEFAULT '0',
  `game_classes_count` int(11) NOT NULL,
  `game_noclasses_count` int(11) NOT NULL,
  `game_team_count` int(11) NOT NULL,
	PRIMARY KEY  (`char_id`)
) ENGINE=MyISAM;

CREATE TABLE `olympiad_history` (
  `object_id_1` INT(11) NOT NULL,
  `object_id_2` INT(11) NOT NULL,
  `class_id_1` INT(11) NOT NULL,
  `class_id_2` INT(11) NOT NULL,
  `name_1` VARCHAR(255) CHARACTER SET UTF8 NOT NULL DEFAULT '',
  `name_2` VARCHAR(255) CHARACTER SET UTF8 NOT NULL DEFAULT '',
  `game_start_time` BIGINT(20) NOT NULL,
  `game_time` INT(11) NOT NULL,
  `game_status` INT(11) NOT NULL,
  `game_type` INT(11) NOT NULL,
  `old` INT(11) NOT NULL
);

CREATE TABLE `heroes` (
	`char_id` INT NOT NULL DEFAULT '0',
	`count` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`played` TINYINT NOT NULL DEFAULT '0',
	`active` TINYINT NOT NULL DEFAULT '0',
	`message` varchar(300) NOT NULL default '',
	PRIMARY KEY  (`char_id`)
) ENGINE=MyISAM;

CREATE TABLE `heroes_diary` (
  `charId` int(10) unsigned NOT NULL,
  `time` decimal(20,0) unsigned NOT NULL,
  `action` tinyint(2) unsigned NOT NULL default '0',
  `param` int(11) unsigned NOT NULL default '0',
  KEY `charId` (`charId`)
);