
-- exists
select count(1) from admin_user where username = ?;

-- find by email 
select * from admin_user where username = ?;

-- list all 
select * from admin_user;

-- insert
insert into admin_user (username, email, role_)
values (?, ?, ?);

