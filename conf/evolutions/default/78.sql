# --- !Ups
CREATE TABLE IF NOT EXISTS MEMBER (
  ID BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  OBJECT_ID BIGINT,
  PERSON TINYINT(1) NOT NULL,
  FUNDER TINYINT(1) NOT NULL,
  FEE_CURRENCY CHAR(3) NOT NULL,
  FEE DECIMAL(13,3) NOT NULL,
  SINCE DATE NOT NULL,
  CREATED DATETIME NOT NULL,
  CREATED_BY BIGINT NOT NULL,
  UPDATED DATETIME NOT NULL,
  UPDATED_BY BIGINT NOT NULL);

# --- !Downs
DROP TABLE IF EXISTS MEMBER;
