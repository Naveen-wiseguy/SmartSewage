use smartsewage;

create table treatment_plant(
  TpID int not null,
  level int,
  status varchar(3),
  primary key (TpID)
);

create table pump(
  PumpID int not null,
  MaxRunTime time,
  OpRate int,
  primary key (PumpID)
);

create table pumping_station(
  PsID int not null,
  PumpID int not null,
  TpID int not null
  location text,
  capacity int,
  priority int,
  level int,
  lastSwitchedOff timestamp,
  durationLastOn time,
  minTimeToEmpty time,
  status varchar(10),
  primary key (PsID),
  foreign key (PumpID) references pump(PumpID),
  foreign key (TpID) references treatment_plant(TpID)
);


create table treatment_plant_input(
  num int not null,
  PsID int,
  TpID int not null,
  switchedOnAt timestamp,
  foreign key (TpID) references treatment_plant(TpID),
  foreign key (PsID) references pumping_station(PsID),
  primary key (num,TpID)
);

create table sensor_log(
seqnum int not null auto_increment,
PsID int not null,
level int,
time datetime,
primary key (seqnum),
);

create table command_log(
  seqnum int not null auto_increment,
  PsID int not null,
  pumpOn int,
  primary key (seqnum)
);
