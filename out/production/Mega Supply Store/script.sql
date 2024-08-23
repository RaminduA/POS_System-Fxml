DROP DATABASE IF EXISTS MSSWholesale;
CREATE DATABASE IF NOT EXISTS MSSWholesale;
SHOW DATABASES ;
USE MSSWholesale;
SHOW TABLES ;
#===================================================================================================
DROP TABLE IF EXISTS Customer;
CREATE TABLE IF NOT EXISTS Customer(
    custId VARCHAR(8),
    custTitle VARCHAR(8),
    custName VARCHAR(30) NOT NULL DEFAULT 'Unknown',
    custAddress VARCHAR(30),
    city VARCHAR(20),
    province VARCHAR(20),
    postalCode VARCHAR(9),
    CONSTRAINT PRIMARY KEY (custId)
);
#===================================================================================================
DROP TABLE IF EXISTS `Order`;
CREATE TABLE IF NOT EXISTS `Order`(
    orderId VARCHAR(6),
    custId VARCHAR(6),
    orderDate DATE,
    time VARCHAR(15),
    cost DOUBLE,
    CONSTRAINT PRIMARY KEY (orderId),
    CONSTRAINT FOREIGN KEY (custId) REFERENCES Customer(custId) ON DELETE CASCADE ON UPDATE CASCADE
);
#===================================================================================================
DROP TABLE IF EXISTS Item;
CREATE TABLE IF NOT EXISTS Item(
    itemCode VARCHAR(8),
    description VARCHAR(50) NOT NULL DEFAULT 'Empty',
    packSize VARCHAR(20),
    qtyOnHand INT NOT NULL DEFAULT 0,
    unitPrice DOUBLE NOT NULL DEFAULT 0.00,
    discountPercent DOUBLE DEFAULT 0.00,
    CONSTRAINT PRIMARY KEY (itemCode)
);
#===================================================================================================
DROP TABLE IF EXISTS `Order Detail`;
CREATE TABLE IF NOT EXISTS `Order Detail`(
    itemCode VARCHAR(6),
    orderId VARCHAR(6),
    orderQty INT NOT NULL DEFAULT 0,
    price DOUBLE,
    discount DOUBLE DEFAULT 0.00,
    CONSTRAINT PRIMARY KEY (itemCode, orderId),
    CONSTRAINT FOREIGN KEY (itemCode) REFERENCES Item(itemCode) ON DELETE CASCADE ON UPDATE CASCADE ,
    CONSTRAINT FOREIGN KEY (orderId) REFERENCES `Order`(orderId) ON DELETE CASCADE ON UPDATE CASCADE
);
#===================================================================================================
SHOW TABLES ;
DESCRIBE Customer;
DESCRIBE `Order`;
DESCRIBE Item;
DESCRIBE `Order Detail`;

