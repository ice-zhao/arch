CREATE DEFINER=`root`@`localhost` PROCEDURE `calculate_month_current`()
BEGIN
SELECT 
    MONTH(res.timestamp) AS mounth,
    deviceType,
    (res.maxval - res.minval) AS consumePower
FROM
    (SELECT 
        deviceType,
            MIN(totalCurrent) AS minval,
            MAX(totalCurrent) AS maxval,
            am1.timestamp
    FROM
        t_ammeter am1, (SELECT 
        MAX(timestamp) AS maxdate, MIN(timestamp) AS mindate
    FROM
        t_ammeter) am2
    WHERE
        am1.timestamp = am2.maxdate
            OR am1.timestamp = am2.mindate) res;

END