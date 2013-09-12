
-- validate 
select * from secret where 1 = 0;

-- insert
insert into secret (group_, name_, secret)
values (?, ?, ?)
;

-- update
update secret 
set 
  secret = ?
where secret_id = ?
;

-- exists
select count(1) from secret where group_ = ? and name_ = ?
;

-- find
select * from secret where group_ = ? and name_ = ?
;

-- find_id
select * from secret where secret_id = ?
;

-- list
select * from secret order by group_, name_
;
