use smartsewage;
insert into treatment_plant values(5,0,'ON');

insert into pump values(1,220,40);
insert into pump values(2,300,50);
insert into pump values(3,140,20);
insert into pump values(4,120,20);

insert into pumping_station values(1,1,5,'gents hostel',7000,5,0,'2000-01-01 00:00:00','00:00:00','00:00:00','OFF');
insert into pumping_station values(2,2,5,'ladies hostel',7000,5,0,'2000-01-01 00:00:00','00:00:00','00:00:00','OFF');
insert into pumping_station values(3,3,5,'cse dept',3500,2,0,'2000-01-01 00:00:00','00:00:00','00:00:00','OFF');
insert into pumping_station values(4,4,5,'ece dept',4000,3,0,'2000-01-01 00:00:00','00:00:00','00:00:00','OFF');

insert into treatment_plant_input(num,TpID) values(1,5);
insert into treatment_plant_input(num,TpID) values(2,5);
insert into treatment_plant_input(num,TpID) values(3,5);


update pumping_station set level=0, minTimeToEmpty='00:00:00', status='OFF',durationLastOn='00:00:00';
update treatment_plant_input set PsID=NULL , switchedOnAt=NULL;
update treatment_plant set level=0, status='ON';
