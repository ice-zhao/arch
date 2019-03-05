create table t_ammeter (id integer primary key, hostID integer, devNumber integer, 
deviceType integer, totalCurrent float, timestamp datetime);

insert into t_ammeter values (5, 6, 3, 4, 500, "2019-01-05 17:15:30");
insert into t_ammeter values (4, 5, 3, 4, 400, "2019-01-04 16:15:30");
insert into t_ammeter values (3, 4, 3, 4, 300, "2019-01-03 15:15:30");
insert into t_ammeter values (2, 3, 3, 4, 200, "2019-01-02 14:15:30");
insert into t_ammeter values (1, 2, 3, 4, 100, "2019-01-01 13:15:30");

insert into t_ammeter values (6, 7, 6, 4, 600, "2019-02-05 17:15:30");
insert into t_ammeter values (7, 8, 6, 4, 800, "2019-02-06 16:15:30");

insert into t_ammeter values (9, 7, 6, 4, 900, "2019-03-05 17:15:30");
insert into t_ammeter values (8, 8, 6, 4, 1000, "2019-03-06 16:15:30");

create table tl_powerConsume( id int not null auto_increment primary key, mounth int, deviceType int, 
totalConsumePower float);


call calculate_month_current;


select
	*
    from t_ammeter ammeter
    where date(ammeter.timestamp) >= '2019-01-02' and date(ammeter.timestamp) <= '2019-01-08'
    order by ammeter.timestamp;



select
	month(res.timestamp) as mounth,
	deviceType,
    (res.maxval - res.minval) as consumePower
from (
SELECT
    deviceType,
    min(totalCurrent) as minval,
    max(totalCurrent) as maxval,
    am1.timestamp
FROM
    t_ammeter am1,
    (SELECT 
        MAX(timestamp) AS maxdate, MIN(timestamp) AS mindate
    FROM
        t_ammeter) am2
WHERE
    am1.timestamp = am2.maxdate
        OR am1.timestamp = am2.mindate) res;
    

SET @var_month = 1;
SET @var_devtype = 3;
SET @var_power = 400;
insert into tl_powerconsume (mounth, deviceType, totalConsumePower)
select * from (
select @var_month as month,@var_devtype,@var_power) as tmp  where not exists ( select * from tl_powerconsume pc where pc.mounth = tmp.month);


set @today = now();
set @currMonth= month(@today);
set @item = 1;

call autocalculate_month_current;

/* enable and show event scheduler */
set global event_scheduler = on;
show processlist;

create event myevent 
on schedule every 10 second
do
call autocalculate_month_current;

drop event myevent;
show events;







 