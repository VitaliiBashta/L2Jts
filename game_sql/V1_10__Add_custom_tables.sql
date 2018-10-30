CREATE TABLE `bbs_buffer` (
  `name` varchar(45) NOT NULL,
  `player_id` int(11) NOT NULL DEFAULT '0',
  `buffs` varchar(355) NOT NULL DEFAULT '0',
  PRIMARY KEY (`name`,`player_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `bbs_teleport` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `player_id` int(11) NOT NULL DEFAULT '0',
  `x` int(9) NOT NULL DEFAULT '0',
  `y` int(9) NOT NULL DEFAULT '0',
  `z` int(9) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `custom_acp` (
  `player_obj_id` int(11) NOT NULL,
  `auto_cp` tinyint(1) NOT NULL,
  `cp_percent` double NOT NULL,
  `cp_item_id` int(11) NOT NULL,
  `reuse_cp` int(11) NOT NULL,
  `auto_small_cp` tinyint(1) NOT NULL,
  `small_cp_percent` double NOT NULL,
  `small_cp_item_id` int(11) NOT NULL,
  `reuse_small_cp` int(11) NOT NULL,
  `auto_hp` tinyint(1) NOT NULL,
  `hp_percent` double NOT NULL,
  `hp_item_id` int(11) NOT NULL,
  `reuse_hp` int(11) NOT NULL,
  `auto_mp` tinyint(1) NOT NULL,
  `mp_percent` double NOT NULL,
  `mp_item_id` int(11) NOT NULL,
  `reuse_mp` int(11) NOT NULL,
  PRIMARY KEY (`player_obj_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE `custom_lease_transform` (
  `player_obj_id` int(11) NOT NULL,
  `id_transform` int(10) NOT NULL,
  PRIMARY KEY (`player_obj_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
