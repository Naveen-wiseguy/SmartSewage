insert into treatment_plant values(5,0,'ON');

insert into pump values(1,'04:00:00',200);
insert into pump values(2,'11:00:00',800);
insert into pump values(3,'07:00:00',580);
insert into pump values(4,'02:00:00',100);

insert into pumping_station values(1,1,5,'gents hostel',7000000,5,0,'2000-01-01 00:00:00','00:00:00','00:00:00','OFF');
insert into pumping_station values(2,2,5,'ladies hostel',7000000,5,0,'2000-01-01 00:00:00','00:00:00','00:00:00','OFF');
insert into pumping_station values(3,3,5,'cse dept',3500000,2,0,'2000-01-01 00:00:00','00:00:00','00:00:00','OFF');
insert into pumping_station values(4,4,5,'ece dept',4000000,2,0,'2000-01-01 00:00:00','00:00:00','00:00:00','OFF');

insert into treatment_plant_input(num,TpID) values(1,5);
insert into treatment_plant_input(num,TpID) values(2,5);
insert into treatment_plant_input(num,TpID) values(3,5);


update pumping_station set level=0, minTimeToEmpty='00:00:00', status='OFF';
update treatment_plant_input set PsID=NULL , switchedOnAt=NULL;
update treatment_plant set level=0;
