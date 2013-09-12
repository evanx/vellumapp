

-- validate 
select * from admin_user where 1 = 0;

-- insert
insert into admin_user (user_name, email, cert_subject, role_)
values (?, ?, ?, ?);

-- update_subject
update admin_user 
set 
  cert_subject = ?
where user_name = ?
;

-- update_display_name
update admin_user 
set 
  display_name = ?
where user_name = ?
;

-- update_secret
update admin_user 
set 
  secret = ?
where user_name = ?
;

-- exists_username
select count(1) from admin_user where user_name = ?
;

-- exists_email
select count(1) from admin_user where email = ?
;

-- exists_subject
select count(1) from admin_user where cert_subject = ?
;

-- find_username
select * from admin_user where user_name = ?
;

-- find_email
select * from admin_user where email = ?
;

-- find_subject
select * from admin_user where cert_subject = ?
;

-- list
select * from admin_user order by user_name
;


