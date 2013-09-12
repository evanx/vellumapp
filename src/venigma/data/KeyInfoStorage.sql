
-- exists
select count(1) from key_info 
where key_alias = ? and revision_number = ?
;

-- find
select * from key_info 
where key_alias = ? and revision_number = ?
;

-- list
select * from key_info
;

-- delete
delete from key_info where key_alias = ?
;

-- insert
insert into key_info (key_alias, revision_number, key_size, salt, iv, data_)
values (?, ?, ?, ?, ?, ?)
;




