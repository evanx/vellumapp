
drop table admin_history; 
drop table admin_user; 
drop table app;
drop table cert; 
drop table client; 
drop table schema_revision;
drop table secret; 
drop table service; 
drop table secret; 

create table admin_history (
  admin_log_id int auto_increment primary key not null,
  admin_user_id int,
  second_admin_user_id int,
  requested_time timestamp,
  updated_time timestamp,
  action_ varchar(32),
  entity_id int,
  entity_table varchar(32),
  entity_column varchar(32),
  old_value varchar(255),
  new_value varchar(255),
  description varchar(255)
);

create table admin_user (
  admin_user_id int auto_increment primary key not null,
  user_name varchar(64),
  display_name varchar(64),
  email varchar(64),
  role_ varchar(32),
  cert_subject varchar(255),
  otp_secret varchar(16),
  password_hash varchar(255),
  enabled boolean default false,
  unique uniq_user_email (email),
  unique uniq_user_subject (cert_subject)
);

create table app (
  app_id int primary key not null,
  log_level varchar(16),
  keystore_file varchar(64),
  server_key_alias varchar(32),
  truststore_file varchar(64),
  http_server_port int,
  http_server_enabled boolean,
  http_server_url char(64),
  https_server_port int,
  https_server_enabled boolean,
  https_server_url char(64),
  httpsa_server_port int,
  httpsa_server_enabled boolean,
  httpsa_server_url char(64),
  unique uniq_app (app_id)
);

create table cert (
  cert_id int auto_increment primary key,
  cert_subject varchar(255) not null,
  cert_pem varchar(8192),
  enabled boolean default false,  
  unique uniq_cert_subject (cert_subject)
);

create table schema_revision (
  revision_number int,
  updated timestamp default now()
);

create table secret (
  secret_id int auto_increment primary key not null,
  group_ varchar(32),
  name_ varchar(32),
  secret varchar(255),
  key_alias varchar(32),
  unique uniq_secret (group_, name_)
);

create table client (
  client_id int auto_increment primary key, 
  client_ip varchar(32)
);
