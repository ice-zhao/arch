CREATE DEFINER=`root`@`localhost` PROCEDURE `autocalculate_month_current`()
BEGIN
	declare today datetime default now();
    declare currMonth int default month(today);
    declare item int default 1;
    
    declare dec_month int;
	declare dec_deviceType int;
	declare dec_totalPower float;
    
    /*the better method is to only delete the data of current month.*/
    truncate tl_powerconsume;
    
   while item <= currMonth
   do
	SELECT
		MONTH(res.timestamp) AS mounth,
		deviceType,
		(res.maxval - res.minval) AS consumePower
	INTO dec_month , dec_deviceType , dec_totalPower
	from
		(SELECT 
			deviceType,
				MIN(totalCurrent) AS minval,
				MAX(totalCurrent) AS maxval,
				am1.timestamp
		FROM
			t_ammeter am1,
			(SELECT 
				MAX(timestamp) AS maxdate, MIN(timestamp) AS mindate
			from
				(select * from t_ammeter ta where month(ta.timestamp) = item) mon_set) am2
		where
			am1.timestamp = am2.maxdate
            OR am1.timestamp = am2.mindate) res;
            
	insert into tl_powerconsume (mounth, deviceType, totalConsumePower)
	select 
		* 
	from 
		(select 
			dec_month as month, 
            dec_deviceType, 
            dec_totalPower
		) as tmp
	where not exists (select * from tl_powerconsume pc where pc.mounth = tmp.month);
    
	set item = item + 1;
    
	end while;
 
END