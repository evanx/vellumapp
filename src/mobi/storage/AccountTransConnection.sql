
-- find by id 
select * from account_trans where account_trans_id = ?;

-- insert
insert into account_trans (
  debit_account_id, 
  credit_account_id, 
  description,
  trans_type, 
  trans_status,
  currency,
  amount
) values (?, ?, ?, ?, ?, ?, ?);

