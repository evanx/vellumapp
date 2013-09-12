

CREATE TABLE person_role (
  person_role_id serial,
  role_label text
);

CREATE TABLE person_role_membership (
  person_role_id,
  person_id
);

ALTER TABLE person ADD COLUMN date_created timestamp;
ALTER TABLE person ADD COLUMN known_phrase varchar(64);
ALTER TABLE person ADD COLUMN challenge varchar(64);
ALTER TABLE person ADD COLUMN answer varchar(64);

CREATE TABLE schema_ (
  schema_id serial,
  table_ text,
  column_ text,
  type_ text,
  size_ int
);

