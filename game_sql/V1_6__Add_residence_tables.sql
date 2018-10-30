CREATE TABLE `castle` (
  `id` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `name` varchar(25) NOT NULL,
  `tax_percent` int(11) NOT NULL,
  `treasury` bigint(20) unsigned NOT NULL DEFAULT '0',
  `town_id` int(11) NOT NULL,
  `last_siege_date` bigint(20) NOT NULL,
  `own_date` bigint(20) NOT NULL,
  `siege_date` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO castle VALUES ('5', 'Aden', '0', '0', '11', '0', '0', '0');
INSERT INTO castle VALUES ('2', 'Dion', '0', '0', '8', '0', '0', '0');
INSERT INTO castle VALUES ('3', 'Giran', '0', '0', '9', '0', '0', '0');
INSERT INTO castle VALUES ('1', 'Gludio', '0', '0', '6', '0', '0', '0');
INSERT INTO castle VALUES ('7', 'Goddard', '0', '0', '15', '0', '0', '0');
INSERT INTO castle VALUES ('6', 'Innadril', '0', '0', '13', '0', '0', '0');
INSERT INTO castle VALUES ('4', 'Oren', '0', '0', '10', '0', '0', '0');
INSERT INTO castle VALUES ('8', 'Rune', '0', '0', '14', '0', '0', '0');
INSERT INTO castle VALUES ('9', 'Schuttgart', '0', '0', '16', '0', '0', '0');



CREATE TABLE `clanhall` (
  `id` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `name` varchar(40) NOT NULL DEFAULT '',
  `last_siege_date` bigint(20) NOT NULL,
  `own_date` bigint(20) NOT NULL,
  `siege_date` bigint(20) NOT NULL,
  `auction_min_bid` bigint(20) NOT NULL,
  `auction_length` int(11) NOT NULL,
  `auction_desc` text,
  `cycle` int(11) NOT NULL,
  `paid_cycle` int(11) NOT NULL,
  PRIMARY KEY (`id`,`name`)
);

INSERT INTO clanhall VALUES ('21', 'Fortress of Resistance', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('22', 'Moonstone Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('23', 'Onyx Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('24', 'Topaz Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('25', 'Ruby Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('26', 'Crystal Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('27', 'Onyx Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('28', 'Sapphire Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('29', 'Moonstone Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('30', 'Emerald Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('31', 'The Atramental Barracks', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('32', 'The Scarlet Barracks', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('33', 'The Viridian Barracks', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('34', 'Devastated Castle', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('35', 'Bandit Stronghold', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('36', 'The Golden Chamber', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('37', 'The Silver Chamber', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('38', 'The Mithril Chamber', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('39', 'Silver Manor', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('40', 'Gold Manor', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('41', 'The Bronze Chamber', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('42', 'The Golden Chamber', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('43', 'The Silver Chamber', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('44', 'The Mithril Chamber', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('45', 'The Bronze Chamber', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('46', 'Silver Manor', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('47', 'Moonstone Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('48', 'Onyx Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('49', 'Emerald Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('50', 'Sapphire Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('51', 'Mont Chamber', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('52', 'Astaire Chamber', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('53', 'Aria Chamber', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('54', 'Yiana Chamber', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('55', 'Roien Chamber', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('56', 'Luna Chamber', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('57', 'Traban Chamber', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('58', 'Eisen Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('59', 'Heavy Metal Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('60', 'Molten Ore Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('61', 'Titan Hall', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('62', 'Rainbow Springs', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('63', 'Wild Beast Reserve', '0', '0', '0', '0', '0', null, '0', '0');
INSERT INTO clanhall VALUES ('64', 'Fortress of the Dead', '0', '0', '0', '0', '0', null, '0', '0');



CREATE TABLE`dominion` (
  `id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `lord_object_id` int(11) NOT NULL DEFAULT '0',
  `wards` varchar(255) NOT NULL,
  `siege_date` bigint(255) NOT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO dominion VALUES ('81', 'Gludio Territory', '0', '81;', '0');
INSERT INTO dominion VALUES ('82', 'Dion Territory', '0', '82;', '0');
INSERT INTO dominion VALUES ('83', 'Giran Territory', '0', '83;', '0');
INSERT INTO dominion VALUES ('84', 'Oren Territory', '0', '84;', '0');
INSERT INTO dominion VALUES ('85', 'Aden Territory', '0', '85;', '0');
INSERT INTO dominion VALUES ('86', 'Innadril Territory', '0', '86;', '0');
INSERT INTO dominion VALUES ('87', 'Goddard Territory', '0', '87;', '0');
INSERT INTO dominion VALUES ('88', 'Rune Territory', '0', '88;', '0');
INSERT INTO dominion VALUES ('89', 'Schuttgart Territory', '0', '89;', '0');

CREATE TABLE  `dominion_rewards` (
  `id` int(11) NOT NULL,
  `object_id` int(11) NOT NULL,
  `static_badges` int(11) NOT NULL,
  `kill_reward` int(11) NOT NULL,
  `online_reward` int(11) NOT NULL,
  PRIMARY KEY (`id`,`object_id`)
);



CREATE TABLE `fortress` (
  `id` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `name` varchar(45) NOT NULL,
  `state` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `castle_id` int(11) NOT NULL,
  `last_siege_date` bigint(20) NOT NULL,
  `own_date` bigint(20) NOT NULL,
  `siege_date` bigint(20) NOT NULL,
  `blood_oath_count` bigint(20) NOT NULL,
  `supply_count` bigint(20) NOT NULL,
  `facility_0` int(20) NOT NULL,
  `facility_1` int(11) NOT NULL,
  `facility_2` int(11) NOT NULL,
  `facility_3` int(11) NOT NULL,
  `facility_4` int(11) NOT NULL,
  `cycle` int(11) NOT NULL,
  `paid_cycle` int(11) NOT NULL,
  `reward_count` int(11) NOT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO `fortress` VALUES ('101', 'Shanty Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('102', 'Southern Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('103', 'Hive Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('104', 'Valley Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('105', 'Ivory Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('106', 'Narsell Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('107', 'Bayou Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('108', 'White Sands Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('109', 'Borderland Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('110', 'Swamp Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('111', 'Archaic Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('112', 'Floran Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('113', 'Cloud Mountain Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('114', 'Tanor Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('115', 'Dragonspine Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('116', 'Antharas Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('117', 'Western Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('118', 'Hunters Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('119', 'Aaru Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('120', 'Demon Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');
INSERT INTO `fortress` VALUES ('121', 'Monastic Fortress', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0');




CREATE TABLE `residence_functions` (
	`id` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`type` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`lvl` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
	`lease` INT NOT NULL DEFAULT '0',
	`rate` bigint NOT NULL DEFAULT '0',
	`endTime` INT NOT NULL DEFAULT '0',
	`inDebt` TINYINT NOT NULL DEFAULT '0',
	PRIMARY KEY  (`id`,`type`)
) ENGINE=MyISAM;
