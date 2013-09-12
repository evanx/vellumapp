
-- insert
insert into service (
  org_id,
  host_name,
  service_name,
  subject,
  cert,
  updated_by
) values (?, ?, ?, ?, ?, ?)
;

-- update_cert
update service 
set 
  subject = ?,
  cert = ?, 
  updated_by = ?,
  updated = now()
where service_id = ?
;

-- exists_org_host_service
select count(1) from service  
where org_id = ? and host_name = ? and service_name = ?
;

-- find_org_host_client
select * from service 
where org_id = ? and host_name = ? and service_name = ?
;

-- find_id
select * from service where service_id = ?
;

-- find_subject
select * from service where subject = ?
;

-- delete_id
delete from service where service_id = ?
;

-- list_org
select * from service where org_id = ? order by host_name, service_name
;

-- list
select * from service order by org_id, host_name, service_name
;

