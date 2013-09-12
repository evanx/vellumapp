

-- validate 
select * from user_ where 1 = 0;

-- insert
insert into user_ (user_name, first_name, last_name, display_name, email, subject, secret, login)
values (?, ?, ?, ?, ?, ?, ?, ?);

-- update_display_name_subject_cert
update user_ 
set 
  display_name = ?, 
  subject = ?,  
  cert = ?,
  updated = now()
where user_name = ?
;

-- update_display_name
update user_ 
set 
  display_name = ?, 
  updated = now()
where user_name = ?
;

-- update_login
update user_ 
set 
  login = ?,
  updated = now()
where user_name = ?
;

-- update_logout
update user_ 
set 
  logout = ?,
  updated = now()
where user_name = ?
;

-- update_secret
update user_ 
set 
  secret = ?,
  updated = now()
where user_name = ?
;

-- update_cert
update user_ 
set 
  subject = ?,
  cert = ?,
  updated = now()
where user_name = ?
;

-- exists_username
select count(1) from user_ where user_name = ?
;

-- exists_email
select count(1) from user_ where email = ?
;

-- find_username
select * from user_ where user_name = ?
;

-- find_email
select * from user_ where email = ?
;

-- list
select * from user_ order by user_name
;


