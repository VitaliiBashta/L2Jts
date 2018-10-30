CREATE TABLE `mmotop_month` (
	`year` int(11) unsigned NOT NULL,
	`month` int(11) unsigned NOT NULL,
	`day` int(11) unsigned NOT NULL default '0',
	`voteCount` int(11) unsigned NOT NULL,
	`isOpen` tinyint(4) unsigned NOT NULL default '0',
	PRIMARY KEY (`year`,`month`)
);

CREATE TABLE `mmotop_vote` (
	`id` int(11) unsigned NOT NULL,
	`Date` varchar(45) NOT NULL,
	`ip` varchar(45) default NULL,
	`char_name` varchar(45) NOT NULL,
	`voteCount` int(11) unsigned NOT NULL,
	PRIMARY KEY (`id`)
) ENGINE=MyISAM;