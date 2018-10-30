CREATE TABLE `hwid_bans` (
	`hwid` VARCHAR(32) default NULL ,
	`comment` varchar(255) default '',
	`ban_type` enum('PLAYER_BAN','ACCOUNT_BAN','NONE') NOT NULL default 'NONE',
	UNIQUE (`hwid`)
) ENGINE = MYISAM;

CREATE TABLE `hwid_list` (
	`hwid` varchar(32) NOT NULL default '',
	`windows_count` INT UNSIGNED NOT NULL DEFAULT 1,
	`login` varchar(45) NOT NULL default '',
	PRIMARY KEY (`hwid`)
) ENGINE=MyISAM;

ALTER TABLE `characters` ADD `last_hwid` VARCHAR(32) default '';

CREATE TABLE `protection_info_list` (
	`login` varchar(45) NOT NULL default '',
	`buf` varchar(1024) default '',
	PRIMARY KEY (`login`)
) ENGINE=MyISAM;