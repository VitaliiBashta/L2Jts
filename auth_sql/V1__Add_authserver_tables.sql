CREATE TABLE  `account_bonus` (
  `account` varchar(255) NOT NULL,
  `rate_xp` double NOT NULL,
  `rate_sp` double NOT NULL,
  `rate_adena` double NOT NULL,
  `rate_drop` double NOT NULL,
  `rate_spoil` double NOT NULL,
  `rate_epaulette` double NOT NULL,
  `bonus_expire` BIGINT(20) UNSIGNED NOT NULL,
  PRIMARY KEY (`account`)
);

CREATE TABLE `account_block` (
  `account_id` BIGINT(10) UNSIGNED NOT NULL,
  `block_type` ENUM('','email','sms') NOT NULL DEFAULT '',
  `phone_sms` VARCHAR(20) NULL DEFAULT NULL,
  `phone_hash` VARCHAR(32) NULL DEFAULT NULL,
  `want_block` ENUM('','email','sms') NOT NULL DEFAULT '',
  `send_email` TINYINT(1) UNSIGNED NOT NULL DEFAULT '0',
  `send_time` INT(4) UNSIGNED NULL DEFAULT NULL,
  `send_ip` VARCHAR(15) NULL DEFAULT NULL,
  `email_lang` CHAR(2) NOT NULL DEFAULT 'RU',
  `want_key` VARCHAR(32) NULL DEFAULT NULL,
  PRIMARY KEY (account_id)
)
COMMENT='Block IP'
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE `account_log` (
  `time` int(11) NOT NULL,
  `login` varchar(32) NOT NULL,
  `ip` varchar(15) NOT NULL,
  KEY `login` (`login`),
  KEY `ip` (`ip`)
) DEFAULT CHARSET=utf8;

CREATE TABLE `accounts` (
	`id` BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT,
	`login` VARCHAR(32) NOT NULL,
	`password` VARCHAR(255) NOT NULL,
	`creationDate` INT(11) NOT NULL DEFAULT '-1',
	`last_access` INT(11) NOT NULL DEFAULT '0',
	`access_level` INT(11) NOT NULL DEFAULT '0',
	`last_ip` VARCHAR(15) NULL DEFAULT NULL,
	`last_server` INT(11) NOT NULL DEFAULT '0',
	`l2email` varchar(50) NOT NULL DEFAULT 'null@null',
	`ban_expire` INT(11) NOT NULL DEFAULT '0',
	`allow_ip` VARCHAR(255) NOT NULL DEFAULT '',
	`points` INT(11) NOT NULL DEFAULT '0',
	`checkemail` smallint(1) NOT NULL DEFAULT '0',
	`report_points` TINYINT UNSIGNED NOT NULL DEFAULT '10',
	INDEX `last_ip` (`last_ip`),
	PRIMARY KEY (`id`),
	UNIQUE INDEX `login` (`login`)
) DEFAULT CHARSET=utf8;

CREATE TABLE auth_log (
	id INT(10) UNSIGNED NOT NULL AUTO_INCREMENT,
	act_time INT(10) NOT NULL,
	ip INT(15) UNSIGNED NOT NULL DEFAULT '0',
	failed TINYINT(10) UNSIGNED NOT NULL DEFAULT '0',
	login TINYINT(10) UNSIGNED NOT NULL DEFAULT '1',
	account_id BIGINT(20) UNSIGNED NOT NULL DEFAULT '0',
	PRIMARY KEY (id),
	INDEX account_id (account_id),
	INDEX act_time (act_time),
	INDEX ip (ip)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;

CREATE TABLE `gameservers` (
  `server_id` int(11) NOT NULL,
  `host` varchar(255) NOT NULL,
  PRIMARY KEY (`server_id`)
) DEFAULT CHARSET=utf8;