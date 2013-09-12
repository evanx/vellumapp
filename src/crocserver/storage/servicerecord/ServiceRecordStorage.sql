
-- insert
insert into service_record (
  org_id,
  cert_name,
  service_name,
  status,
  time_,
  dispatched_time,
  notified_time,
  exit_code,
  out_,
  err_
)
values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
;

-- find_latest
select sr.* 
from (
  select service_name, max(time_) as max_time
  from service_record 
  where cert_name = ? and service_name = ?
  group by service_name
) msr
inner join service_record sr on (
  sr.service_name = msr.service_name and sr.time_ = max_time
)
;

-- find_id
select * from service_record 
where service_record_id = ?
;

-- delete_id
delete from service_record where service_record_id = ?
;

-- list
select * from service_record
order by time_ desc
;


