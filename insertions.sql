insert into treatment_plant values(5,0,'ON');

insert into pump values(1,'04:00:00',200);
insert into pump values(2,'11:00:00',800);
insert into pump values(3,'07:00:00',580);
insert into pump values(4,'02:00:00',100);

insert into pumping_station values(1,1,'gents hostel',7000000,5,0,'1970-01-01 00:00:00','00:00:00','00:00:00',5,'OFF');
insert into pumping_station values(2,2,'ladies hostel',7000000,5,0,'1970-01-01 00:00:00','00:00:00','00:00:00',5,'OFF');
insert into pumping_station values(3,3,'cse dept',3500000,2,0,'1970-01-01 00:00:00','00:00:00','00:00:00',5,'OFF');
insert into pumping_station values(4,4,'ece dept',4000000,2,0,'1970-01-01 00:00:00','00:00:00','00:00:00',5,'OFF');

insert into treatment_plant_input(num,TpID,status) values(1,5,'OFF');
insert into treatment_plant_input(num,TpID,status) values(2,5,'OFF');
insert into treatment_plant_input(num,TpID,status) values(3,5,'OFF');
