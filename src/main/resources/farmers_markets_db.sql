-- MySQL Script generated by MySQL Workbench
-- Wed Aug 14 15:16:18 2024
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema farmers_markets
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema farmers_markets
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `farmers_markets` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `farmers_markets` ;

-- -----------------------------------------------------
-- Table `farmers_markets`.`markets`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `farmers_markets`.`markets` (
  `fmid` INT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `street` VARCHAR(255) NOT NULL,
  `city` VARCHAR(255) NOT NULL,
  `state` VARCHAR(255) NOT NULL,
  `zip_code` VARCHAR(255) NOT NULL,
  `county` VARCHAR(255) NOT NULL,
  `website` VARCHAR(255) NOT NULL,
  `facebook` VARCHAR(255) NOT NULL,
  `twitter` VARCHAR(255) NOT NULL,
  `youtube` VARCHAR(255) NOT NULL,
  `other_media` VARCHAR(255) NOT NULL,
  `s1_date` VARCHAR(255) NOT NULL,
  `s1_time` VARCHAR(255) NOT NULL,
  `s2_date` VARCHAR(255) NOT NULL,
  `s2_time` VARCHAR(255) NOT NULL,
  `s3_date` VARCHAR(255) NOT NULL,
  `s3_time` VARCHAR(255) NOT NULL,
  `s4_date` VARCHAR(255) NOT NULL,
  `s4_time` VARCHAR(255) NOT NULL,
  `location` VARCHAR(255) NOT NULL,
  `updated` VARCHAR(255) NOT NULL,
  `credit` BIT(1) NOT NULL,
  `WIC` BIT(1) NOT NULL,
  `WICcash` BIT(1) NOT NULL,
  `SFMNP` BIT(1) NOT NULL,
  `SNAP` BIT(1) NOT NULL,
  `Organic` BIT(1) NOT NULL,
  `Bakedgood` BIT(1) NOT NULL,
  `Cheese` BIT(1) NOT NULL,
  `Crafts` BIT(1) NOT NULL,
  `Flowers` BIT(1) NOT NULL,
  `Eggs` BIT(1) NOT NULL,
  `Seafood` BIT(1) NOT NULL,
  `Herbs` BIT(1) NOT NULL,
  `Vegtables` BIT(1) NOT NULL,
  `Honey` BIT(1) NOT NULL,
  `Jams` BIT(1) NOT NULL,
  `Maple` BIT(1) NOT NULL,
  `Meat` BIT(1) NOT NULL,
  `Nursery` BIT(1) NOT NULL,
  `Nuts` BIT(1) NOT NULL,
  `Plants` BIT(1) NOT NULL,
  `Poultry` BIT(1) NOT NULL,
  `Prepared` BIT(1) NOT NULL,
  `Soap` BIT(1) NOT NULL,
  `Trees` BIT(1) NOT NULL,
  `Wine` BIT(1) NOT NULL,
  `Coffee` BIT(1) NOT NULL,
  `Beans` BIT(1) NOT NULL,
  `Fruits` BIT(1) NOT NULL,
  `Grains` BIT(1) NOT NULL,
  `Juices` BIT(1) NOT NULL,
  `Mushrooms` BIT(1) NOT NULL,
  `PetFood` BIT(1) NOT NULL,
  `Tofu` BIT(1) NOT NULL,
  `WildHarvested` BIT(1) NOT NULL,
  `average_rating` FLOAT NOT NULL DEFAULT -1,
  PRIMARY KEY (`fmid`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `farmers_markets`.`reviews`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `farmers_markets`.`reviews` (
  `fmid` INT NOT NULL,
  `username` VARCHAR(255) NOT NULL DEFAULT 'Anonymous',
  `comment` VARCHAR(500) NULL DEFAULT NULL,
  `rating` INT NOT NULL,
  `posted` VARCHAR(255) NOT NULL,
  CONSTRAINT `fk_reviews_markets`
    FOREIGN KEY (`fmid`)
    REFERENCES `farmers_markets`.`markets` (`fmid`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `farmers_markets`.`locations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `farmers_markets`.`locations` (
  `city` VARCHAR(255) NOT NULL,
  `state` VARCHAR(255) NOT NULL,
  `zip_code` INT NOT NULL,
  `longitude` DOUBLE NOT NULL,
  `latitude` DOUBLE NOT NULL)
ENGINE = InnoDB;

USE `farmers_markets` ;

-- -----------------------------------------------------
-- Placeholder table for view `farmers_markets`.`browse_markets`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `farmers_markets`.`browse_markets` (`fmid` INT, `name` INT, `street` INT, `city` INT, `state` INT, `zip_code` INT);

-- -----------------------------------------------------
-- View `farmers_markets`.`browse_markets`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `farmers_markets`.`browse_markets`;
USE `farmers_markets`;
CREATE  OR REPLACE ALGORITHM=UNDEFINED DEFINER=`root`@`localhost` SQL SECURITY DEFINER VIEW `farmers_markets`.`browse_markets` AS select `farmers_markets`.`markets`.`fmid` AS `fmid`,`farmers_markets`.`markets`.`name` AS `name`,`farmers_markets`.`markets`.`street` AS `street`,`farmers_markets`.`markets`.`city` AS `city`,`farmers_markets`.`markets`.`state` AS `state`,`farmers_markets`.`markets`.`zip_code` AS `zip_code` from `farmers_markets`.`markets`;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
