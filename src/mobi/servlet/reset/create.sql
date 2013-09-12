
drop table schema_version;
drop table organisation; 
drop table person; 
drop table account_trans; 
drop table account;

create table schema_version (
  version_number int,
  time_updated timestamp default now()
)
;

create table person (
  person_id int not null auto_increment primary key, 
  account_id int,
  person_name varchar(64), 
  person_email varchar(64),
  password_hash varchar(64),
  password_salt varchar(32),
  known_phrase varchar(64),
  challenge varchar(64),
  answer varchar(64),
  otp varchar(32),
  otp_expiry timestamp,
  last_login timestamp,
  currency varchar(3),
  country varchar(2),
  language_ varchar(2),
  locale varchar(32)
);

create table organisation (
  organisation_id int not null auto_increment primary key, 
  organisation_name varchar(32),
  organisation_url varchar(32)
);

create table account (
  account_id int not null auto_increment primary key, 
  description varchar(64),
  person_id int,
  balance numeric(12, 4),
  balance_currency varchar(3),
  balance_account_trans_id int,
  time_created timestamp default now()
);

create table account_trans (
  account_trans_id int not null auto_increment primary key, 
  debit_account_id int,
  credit_account_id int,
  trans_type varchar(16),
  trans_status varchar(16),
  description varchar(64),
  request_time timestamp default now(),
  trans_time timestamp,
  amount numeric(12, 4),
  currency varchar(3)
);

ALTER TABLE account_trans ADD FOREIGN KEY (debit_account_id) REFERENCES account (account_id);
ALTER TABLE account_trans ADD FOREIGN KEY (credit_account_id) REFERENCES account (account_id);

ALTER TABLE person ADD FOREIGN KEY (account_id) REFERENCES account (account_id);


