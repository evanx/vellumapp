
-- exists
select 1 from person where person_email = ?;

-- find by email 
select * from person where person_email = ?;

-- insert
insert into person (account_id, person_email, person_name, password_hash, password_salt)
values (?, ?, ?, ?, ?);

