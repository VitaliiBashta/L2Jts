CREATE TABLE `clan_data` (
	`clan_id` INT NOT NULL DEFAULT '0',
	`clan_level` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`hasCastle` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`hasFortress` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`hasHideout` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`ally_id` INT NOT NULL DEFAULT '0',
	`crest` VARBINARY(256) NULL DEFAULT NULL,
	`largecrest` VARBINARY(8192) NULL DEFAULT NULL,
	`reputation_score` INT NOT NULL DEFAULT '0',
	`warehouse` INT NOT NULL DEFAULT '0',
	`expelled_member` INT UNSIGNED NOT NULL DEFAULT '0',
	`leaved_ally` INT UNSIGNED NOT NULL DEFAULT '0',
	`dissolved_ally` INT UNSIGNED NOT NULL DEFAULT '0',
	`airship` INT NOT NULL DEFAULT '-1',
	`castle_defend_count` INT NOT NULL DEFAULT '0',
	PRIMARY KEY (`clan_id`),
	KEY `ally_id` (`ally_id`)
) ENGINE=MyISAM;

CREATE TABLE `clan_privs` (
	`clan_id` INT NOT NULL DEFAULT '0',
	`rank` INT NOT NULL DEFAULT '0',
	`privilleges` INT NOT NULL DEFAULT '0',
	PRIMARY KEY  (`clan_id`,`rank`)
) ENGINE=MyISAM;

CREATE TABLE `clan_skills` (
	`clan_id` INT NOT NULL DEFAULT '0',
	`skill_id` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
	`skill_level` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	PRIMARY KEY  (`clan_id`,`skill_id`)
) ENGINE=MyISAM;

CREATE TABLE `clan_subpledges` (
	`clan_id` INT UNSIGNED NOT NULL DEFAULT '0',
	`type` SMALLINT NOT NULL DEFAULT '0',
	`name` VARCHAR(45) CHARACTER SET UTF8 NOT NULL DEFAULT '',
	`leader_id` INT UNSIGNED NOT NULL DEFAULT '0',
	`new_leader_id` INT UNSIGNED NOT NULL DEFAULT '0',
	PRIMARY KEY  (`clan_id`,`type`)
) ENGINE=MyISAM;

CREATE TABLE `clan_subpledges_skills` (
  `clan_id` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL,
  `skill_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `skill_level` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`clan_id`,`type`,`skill_id`)
);

CREATE TABLE `clan_wars` (
	`clan1` INT NOT NULL DEFAULT '0',
	`clan2` INT NOT NULL DEFAULT '0'
) ENGINE=MyISAM;