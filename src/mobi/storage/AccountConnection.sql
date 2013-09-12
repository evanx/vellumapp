
-- exists
select 1 from account where account_id = ?;

-- find by id 
select * from account where account_id = ?;

-- insert
insert into account (description)
values (?);

-- update balance
update account 
set balance = ?,
balance_currency = ?,
balance_account_trans_id = ?
where account_id = ?
;

