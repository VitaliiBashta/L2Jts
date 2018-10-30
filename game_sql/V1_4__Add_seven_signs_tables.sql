CREATE TABLE `seven_signs` (
	`char_obj_id` INT NOT NULL DEFAULT '0',
	`cabal` enum('dawn','dusk','No Cabal') NOT NULL DEFAULT 'No Cabal',
	`seal` TINYINT NOT NULL DEFAULT '0',
	`dawn_red_stones` INT NOT NULL DEFAULT '0',
	`dawn_green_stones` INT NOT NULL DEFAULT '0',
	`dawn_blue_stones` INT NOT NULL DEFAULT '0',
	`dawn_ancient_adena_amount` INT NOT NULL DEFAULT '0',
	`dawn_contribution_score` INT NOT NULL DEFAULT '0',
	`dusk_red_stones` INT NOT NULL DEFAULT '0',
	`dusk_green_stones` INT NOT NULL DEFAULT '0',
	`dusk_blue_stones` INT NOT NULL DEFAULT '0',
	`dusk_ancient_adena_amount` INT NOT NULL DEFAULT '0',
	`dusk_contribution_score` INT NOT NULL DEFAULT '0',
	PRIMARY KEY  (`char_obj_id`)
) ENGINE=MyISAM;

CREATE TABLE `seven_signs_festival` (
	`festivalId` TINYINT(4) NOT NULL DEFAULT '0',
	`cabal` VARCHAR(4) NOT NULL,
	`cycle` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0',
	`date` BIGINT(20) NULL DEFAULT '0',
	`score` MEDIUMINT(8) UNSIGNED NOT NULL DEFAULT '0',
	`members` VARCHAR(255) CHARACTER SET UTF8 NOT NULL,
	`names` TINYTEXT CHARACTER SET UTF8 NOT NULL,
	PRIMARY KEY (`festivalId`, `cabal`, `cycle`)
) ENGINE=MyISAM;

REPLACE INTO `seven_signs_festival` VALUES
(0,"dawn",1,0,0,"",""),
(1,"dawn",1,0,0,"",""),
(2,"dawn",1,0,0,"",""),
(3,"dawn",1,0,0,"",""),
(4,"dawn",1,0,0,"",""),
(0,"dusk",1,0,0,"",""),
(1,"dusk",1,0,0,"",""),
(2,"dusk",1,0,0,"",""),
(3,"dusk",1,0,0,"",""),
(4,"dusk",1,0,0,"","");

CREATE TABLE `seven_signs_status` (
	`current_cycle` INT NOT NULL DEFAULT '1',
	`festival_cycle` INT NOT NULL DEFAULT '1',
	`active_period` INT NOT NULL DEFAULT '1',
	`date` INT(11) NOT NULL DEFAULT '1',
	`previous_winner` INT NOT NULL DEFAULT '0',
	`dawn_stone_score` BIGINT NOT NULL DEFAULT '0',
	`dawn_festival_score` BIGINT NOT NULL DEFAULT '0',
	`dusk_stone_score` BIGINT NOT NULL DEFAULT '0',
	`dusk_festival_score` BIGINT NOT NULL DEFAULT '0',
	`avarice_owner` INT NOT NULL DEFAULT '0',
	`gnosis_owner` INT NOT NULL DEFAULT '0',
	`strife_owner` INT NOT NULL DEFAULT '0',
	`avarice_dawn_score` INT NOT NULL DEFAULT '0',
	`gnosis_dawn_score` INT NOT NULL DEFAULT '0',
	`strife_dawn_score` INT NOT NULL DEFAULT '0',
	`avarice_dusk_score` INT NOT NULL DEFAULT '0',
	`gnosis_dusk_score` INT NOT NULL DEFAULT '0',
	`strife_dusk_score` INT NOT NULL DEFAULT '0',
	`accumulated_bonus0` BIGINT NOT NULL DEFAULT '0',
	`accumulated_bonus1` BIGINT NOT NULL DEFAULT '0',
	`accumulated_bonus2` BIGINT NOT NULL DEFAULT '0',
	`accumulated_bonus3` BIGINT NOT NULL DEFAULT '0',
	`accumulated_bonus4` BIGINT NOT NULL DEFAULT '0'
) ENGINE=MyISAM;

REPLACE INTO `seven_signs_status` VALUES
(1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0);