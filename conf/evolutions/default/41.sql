# --- !Ups

create table EVENT (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  BRAND_CODE VARCHAR(5) NOT NULL,
  TITLE VARCHAR(254) NOT NULL,
  SPOKEN_LANGUAGE VARCHAR(254) NOT NULL,
  MATERIALS_LANGUAGE VARCHAR(254),
  CITY VARCHAR(254) NOT NULL,
  COUNTRY_CODE CHAR(2) NOT NULL,
  DESCRIPTION TEXT,
  SPECIAL_ATTENTION TEXT,
  START_DATE DATE NOT NULL,
  END_DATE DATE NOT NULL,
  HOURS_PER_DAY SMALLINT NOT NULL,
  WEB_SITE VARCHAR(254),
  REGISTRATION_PAGE VARCHAR(254),
  NOT_PUBLIC BOOLEAN NOT NULL DEFAULT 0,
  ARCHIVED BOOLEAN NOT NULL DEFAULT 0,
  CREATED TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CREATED_BY VARCHAR(254) NOT NULL DEFAULT 'Sergey Kotlov',
  UPDATED TIMESTAMP NOT NULL,
  UPDATED_BY VARCHAR(254) NOT NULL DEFAULT 'Sergey Kotlov'
);

# --- !Downs

drop table EVENT;
