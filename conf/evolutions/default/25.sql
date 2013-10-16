# T83 Account entities

# --- !Ups

CREATE TABLE `ACCOUNT`
(`ID`        BIGINT  NOT NULL AUTO_INCREMENT PRIMARY KEY,
 `PERSON_ID` BIGINT UNIQUE,
 `ORGANISATION_ID` BIGINT UNIQUE,
 `CURRENCY`  CHAR(3) NOT NULL DEFAULT 'EUR',
  `ACTIVE` BOOLEAN DEFAULT FALSE NOT NULL,
  CHECK (
    (`PERSON_ID` IS NULL AND `ORGANISATION_ID` IS NOT NULL)
    OR  (`PERSON_ID` IS NOT NULL AND `ORGANISATION_ID` IS NULL)
  )
);
ALTER TABLE `ACCOUNT` ADD CONSTRAINT `ACCOUNT_PERSON_FK` FOREIGN KEY (`PERSON_ID`) REFERENCES `PERSON` (`ID`);
ALTER TABLE `ACCOUNT` ADD CONSTRAINT `ACCOUNT_ORGANISATION_FK` FOREIGN KEY(`ORGANISATION_ID`) REFERENCES `ORGANISATION`(`ID`);

INSERT INTO `ACCOUNT` (`PERSON_ID`) SELECT `ID` FROM `PERSON`;
INSERT INTO `ACCOUNT` (`ORGANISATION_ID`) SELECT `ID` FROM `ORGANISATION`;


# --- !Downs

ALTER TABLE `ACCOUNT` DROP FOREIGN KEY `ACCOUNT_PERSON_FK`;
ALTER TABLE `ACCOUNT` DROP FOREIGN KEY `ACCOUNT_ORGANISATION_FK`;
DROP TABLE `ACCOUNT`;