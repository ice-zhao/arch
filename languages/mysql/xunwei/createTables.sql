create table t_ammeter (id integer primary key, hostID integer, devNumber integer, 
deviceType integer, totalCurrent float, timestamp datetime);

insert into t_ammeter values (5, 6, 3, 4, 500, "2019-01-05 17:15:30");
insert into t_ammeter values (4, 5, 3, 4, 400, "2019-01-04 16:15:30");
insert into t_ammeter values (3, 4, 3, 4, 300, "2019-01-03 15:15:30");
insert into t_ammeter values (2, 3, 3, 4, 200, "2019-01-02 14:15:30");
insert into t_ammeter values (1, 2, 3, 4, 100, "2019-01-01 13:15:30");



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
    
    
    
    
    