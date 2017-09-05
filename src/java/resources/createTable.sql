create table conversation (conversation_id int(11) not null auto_increment,
 date_	date not null, time_ varchar(50) not null,
 sender varchar(50) not null, message text,
 containsSmiley boolean,
 primary key (conversation_id)); 