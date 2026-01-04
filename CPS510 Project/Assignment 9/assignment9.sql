-- Assignment#9

BEGIN EXECUTE IMMEDIATE 'DROP VIEW VIEW_BOOKING_SERVICES'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP VIEW VIEW_ACTIVE_CUSTOMERS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP VIEW VIEW_BOOKING_WITH_CUSTOMERS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Service_Booking CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Payment CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Booking_Customer CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Booking_Room CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Booking CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Service CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Room CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Room_Type CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Employee CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/
BEGIN EXECUTE IMMEDIATE 'DROP TABLE Customer CASCADE CONSTRAINTS'; EXCEPTION WHEN OTHERS THEN NULL; END;
/

-- 1) BCNF schema (matches Assignment8)
CREATE TABLE Customer (
  CustomerID NUMBER PRIMARY KEY,
  CusName    VARCHAR2(100) NOT NULL,
  PhoneNum   VARCHAR2(20)  NOT NULL UNIQUE,
  Email      VARCHAR2(254) NOT NULL UNIQUE,
  IDType     VARCHAR2(20)  NOT NULL
);

CREATE TABLE Employee (
  EmployeeID NUMBER PRIMARY KEY,
  EmpName    VARCHAR2(100) NOT NULL,
  EmpRole    VARCHAR2(30)  NOT NULL,
  PhoneNum   VARCHAR2(20)  NOT NULL UNIQUE,
  Email      VARCHAR2(254) NOT NULL UNIQUE,
  Salary     NUMBER(10,2)  NOT NULL
);

CREATE TABLE Room_Type (
  RoomTypeID   NUMBER PRIMARY KEY,
  RoomTypeName VARCHAR2(30) NOT NULL UNIQUE,
  MaxCapacity  NUMBER       NOT NULL
);

CREATE TABLE Room (
  RoomID     NUMBER PRIMARY KEY,
  RoomNum    NUMBER NOT NULL UNIQUE,
  Floor      NUMBER NOT NULL,
  RoomTypeID NUMBER NOT NULL,
  Price      NUMBER(10,2) NOT NULL,
  Status     VARCHAR2(20) NOT NULL,
  CONSTRAINT fk_room_type FOREIGN KEY (RoomTypeID)
    REFERENCES Room_Type(RoomTypeID)
);

CREATE TABLE Service (
  ServiceID   NUMBER PRIMARY KEY,
  ServiceName VARCHAR2(100) NOT NULL UNIQUE,
  Price       NUMBER(10,2)  NOT NULL
);

CREATE TABLE Booking (
  BookingID  NUMBER PRIMARY KEY,
  EmployeeID NUMBER NOT NULL,
  NumGuests  NUMBER NOT NULL,
  CheckIn    DATE   NOT NULL,
  CheckOut   DATE   NOT NULL,
  Status     VARCHAR2(20) NOT NULL,
  TotalPrice NUMBER(10,2) NOT NULL,
  CONSTRAINT fk_booking_emp FOREIGN KEY (EmployeeID)
    REFERENCES Employee(EmployeeID),
  CONSTRAINT chk_booking_dates CHECK (CheckIn < CheckOut)
);

CREATE TABLE Booking_Room (
  BookingID NUMBER NOT NULL,
  RoomID    NUMBER NOT NULL,
  PRIMARY KEY (BookingID, RoomID),
  CONSTRAINT fk_br_b FOREIGN KEY (BookingID) REFERENCES Booking(BookingID) ON DELETE CASCADE,
  CONSTRAINT fk_br_r FOREIGN KEY (RoomID)    REFERENCES Room(RoomID)     ON DELETE CASCADE
);

CREATE TABLE Booking_Customer (
  BookingID  NUMBER NOT NULL,
  CustomerID NUMBER NOT NULL,
  PRIMARY KEY (BookingID, CustomerID),
  CONSTRAINT fk_bc_b FOREIGN KEY (BookingID)  REFERENCES Booking(BookingID)  ON DELETE CASCADE,
  CONSTRAINT fk_bc_c FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID) ON DELETE CASCADE
);

CREATE TABLE Payment (
  PaymentID   NUMBER PRIMARY KEY,
  BookingID   NUMBER NOT NULL UNIQUE,
  Method      VARCHAR2(10) NOT NULL,
  PaymentDate DATE NOT NULL,
  Amount      NUMBER(10,2) NOT NULL,
  CONSTRAINT fk_payment_b FOREIGN KEY (BookingID) REFERENCES Booking(BookingID) ON DELETE CASCADE
);

-- Derives total removed from this table based on assignment 7
CREATE TABLE Service_Booking (
  BookingID NUMBER NOT NULL,
  ServiceID NUMBER NOT NULL,
  Quantity  NUMBER NOT NULL,
  PRIMARY KEY (BookingID, ServiceID),
  CONSTRAINT fk_sb_b FOREIGN KEY (BookingID) REFERENCES Booking(BookingID) ON DELETE CASCADE,
  CONSTRAINT fk_sb_s FOREIGN KEY (ServiceID) REFERENCES Service(ServiceID) ON DELETE CASCADE
);

-- 2) Test data used for demo
INSERT INTO Room_Type VALUES (1,'Single',1);
INSERT INTO Room_Type VALUES (2,'Double',2);
INSERT INTO Room_Type VALUES (3,'Family',4);

INSERT INTO Room VALUES (101,101,1,1,120,'Available');
INSERT INTO Room VALUES (102,102,1,2,180,'Available');
INSERT INTO Room VALUES (201,201,2,3,260,'Maintenance');

INSERT INTO Employee VALUES (1,'Alice Smith','Receptionist','4161111111','alice@hotel.com',42000);
INSERT INTO Employee VALUES (2,'Bob Chen','Manager','4162222222','bob@hotel.com',78000);

INSERT INTO Customer VALUES (1,'John Doe','6471111111','john@example.com','Passport');
INSERT INTO Customer VALUES (2,'Jane Roe','6472222222','jane@example.com','DriverLicense');

INSERT INTO Service VALUES (1,'Breakfast',20);
INSERT INTO Service VALUES (2,'Airport Pickup',50);

INSERT INTO Booking VALUES (1001,1,2,DATE '2025-11-20', DATE '2025-11-22','Confirmed',320);
INSERT INTO Booking_Customer VALUES (1001,1);
INSERT INTO Booking_Customer VALUES (1001,2);
INSERT INTO Booking_Room     VALUES (1001,101);
INSERT INTO Payment          VALUES (5001,1001,'Credit',SYSDATE,320);
INSERT INTO Service_Booking  VALUES (1001,1,2); -- 2 breakfasts
INSERT INTO Service_Booking  VALUES (1001,2,1); -- 1 pickup

COMMIT;

-- 3) Views based on assignment 4 part 2 
CREATE OR REPLACE VIEW VIEW_BOOKING_WITH_CUSTOMERS AS
SELECT
  b.BookingID,
  TO_CHAR(b.CheckIn,'YYYY-MM-DD')  AS CheckIn,
  TO_CHAR(b.CheckOut,'YYYY-MM-DD') AS CheckOut,
  b.Status,
  b.NumGuests,
  b.TotalPrice,
  e.EmpName AS HandledBy,
  LISTAGG(c.CusName, ', ') WITHIN GROUP (ORDER BY c.CusName) AS Customers
FROM Booking b
JOIN Employee e ON e.EmployeeID = b.EmployeeID
LEFT JOIN Booking_Customer bc ON bc.BookingID = b.BookingID
LEFT JOIN Customer c ON c.CustomerID = bc.CustomerID
GROUP BY b.BookingID, b.CheckIn, b.CheckOut, b.Status, b.NumGuests, b.TotalPrice, e.EmpName;

CREATE OR REPLACE VIEW VIEW_ACTIVE_CUSTOMERS AS
SELECT DISTINCT c.CustomerID, c.CusName, c.Email
FROM Customer c
JOIN Booking_Customer bc ON bc.CustomerID = c.CustomerID;

CREATE OR REPLACE VIEW VIEW_BOOKING_SERVICES AS
SELECT
  b.BookingID,
  TO_CHAR(b.CheckIn,'YYYY-MM-DD')  AS CheckIn,
  TO_CHAR(b.CheckOut,'YYYY-MM-DD') AS CheckOut,
  e.EmpName  AS EmployeeName,
  c.CusName  AS CustomerName,
  s.ServiceName,
  sb.Quantity
FROM Booking b
JOIN Employee e          ON e.EmployeeID = b.EmployeeID
JOIN Booking_Customer bc ON bc.BookingID = b.BookingID
JOIN Customer c          ON c.CustomerID = bc.CustomerID
JOIN Service_Booking sb  ON sb.BookingID = b.BookingID
JOIN Service s           ON s.ServiceID = sb.ServiceID;

-- 4) Demo queries
-- A) List customers
SELECT CustomerID, CusName, Email FROM Customer ORDER BY CusName;

-- B) Available rooms
SELECT r.RoomNum, rt.RoomTypeName, r.Price, r.Status
FROM Room r JOIN Room_Type rt ON r.RoomTypeID=rt.RoomTypeID
WHERE r.Status='Available' ORDER BY r.Price;

-- C) Booking summary (view)
SELECT BookingID, CheckIn, CheckOut, Status, NumGuests, TotalPrice, HandledBy, Customers
FROM VIEW_BOOKING_WITH_CUSTOMERS ORDER BY BookingID;

-- D) Active customers (view)
SELECT * FROM VIEW_ACTIVE_CUSTOMERS ORDER BY CusName;

-- E) Count distinct services per booking (>=2)
SELECT sb.BookingID, COUNT(DISTINCT sb.ServiceID) AS NumServices
FROM Service_Booking sb
GROUP BY sb.BookingID
HAVING COUNT(DISTINCT sb.ServiceID) >= 2
ORDER BY sb.BookingID;

-- F) total quantity per service
SELECT s.ServiceID, s.ServiceName, SUM(sb.Quantity) AS TotalQuantity
FROM Service s JOIN Service_Booking sb ON s.ServiceID = sb.ServiceID
GROUP BY s.ServiceID, s.ServiceName
ORDER BY TotalQuantity DESC;

-- G) Advanced: employees with no bookings OR only bookings < 300
SELECT E.EmployeeID, E.EmpName
FROM Employee E
WHERE NOT EXISTS (SELECT 1 FROM Booking B WHERE B.EmployeeID = E.EmployeeID)
UNION
SELECT E.EmployeeID, E.EmpName
FROM Employee E
WHERE EXISTS (SELECT 1 FROM Booking B WHERE B.EmployeeID = E.EmployeeID)
  AND EXISTS (SELECT 1 FROM Booking B WHERE B.EmployeeID = E.EmployeeID AND B.TotalPrice < 300);
