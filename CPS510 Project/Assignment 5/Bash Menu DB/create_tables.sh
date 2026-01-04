#!/bin/sh
#export LD_LIBRARY_PATH=/usr/lib/oracle/12.1/client64/lib


sqlplus64 "s53chowd/09172572@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(Host=oracle12c.scs.ryerson.ca)(Port=1521))(CONNECT_DATA=(SID=orcl12c)))" <<EOF

CREATE TABLE Customer (
    CustomerID NUMBER PRIMARY KEY,
    CusName VARCHAR2(100) NOT NULL,
    PhoneNum VARCHAR2(20) NOT NULL UNIQUE,
    Email VARCHAR2(254) NOT NULL UNIQUE,
    IDType VARCHAR2(20) NOT NULL,
    CONSTRAINT uq_customer_phone_email UNIQUE (PhoneNum, Email),
    CONSTRAINT chk_idtype CHECK (IDType IN ('Passport', 'DriverLicense', 'NationalID', 'Other')),
    CONSTRAINT chk_phone_digits CHECK (REGEXP_LIKE(PhoneNum, '^[0-9]+$')),
    CONSTRAINT chk_email_format CHECK (REGEXP_LIKE(Email, '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'))
);

CREATE TABLE Room (
    RoomID NUMBER PRIMARY KEY,
    RoomNum NUMBER NOT NULL UNIQUE,
    Floor NUMBER NOT NULL,
    RoomType VARCHAR2(20) NOT NULL,
    MaxCap NUMBER NOT NULL,
    Price NUMBER(10,2) NOT NULL,
    Status VARCHAR2(20) NOT NULL,
    CONSTRAINT chk_floor_nonnegative CHECK (Floor >= 1),
    CONSTRAINT chk_maxcap_positive CHECK (MaxCap >= 1),
    CONSTRAINT chk_price_nonnegative CHECK (Price >= 0),
    CONSTRAINT chk_roomtype CHECK (RoomType IN ('Single', 'Double', 'Family')),
    CONSTRAINT chk_status CHECK (Status IN ('Available', 'Unavailable', 'Maintenance'))
);

CREATE TABLE Employee (
    EmployeeID NUMBER PRIMARY KEY,
    EmpName VARCHAR2(100) NOT NULL,
    EmpRole VARCHAR2(30) NOT NULL,
    PhoneNum VARCHAR2(20) NOT NULL UNIQUE,
    Email VARCHAR2(254) NOT NULL UNIQUE,
    Salary NUMBER(10,2) NOT NULL,
    CONSTRAINT uq_emp_phone_email UNIQUE (PhoneNum, Email),
    CONSTRAINT chk_emp_role CHECK (EmpRole IN ('Receptionist','Housekeeper','Cook','Manager','Owner')),
    CONSTRAINT chk_emp_salary_nonnegative CHECK (Salary >= 0),
    CONSTRAINT chk_emp_phone_digits CHECK (REGEXP_LIKE(PhoneNum, '^[0-9]+$')),
    CONSTRAINT chk_emp_email_format CHECK (REGEXP_LIKE(Email, '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'))
);

CREATE TABLE Service (
    ServiceID NUMBER PRIMARY KEY,
    ServiceName VARCHAR2(100) NOT NULL UNIQUE,
    Price NUMBER(10,2) NOT NULL,
    CONSTRAINT chk_service_price_nonnegative CHECK (Price >= 0)
);


CREATE TABLE Booking (
    BookingID NUMBER PRIMARY KEY,
    EmployeeID NUMBER NOT NULL,
    NumGuests NUMBER NOT NULL,
    CheckIn DATE NOT NULL,
    CheckOut DATE NOT NULL,
    Status VARCHAR2(20) NOT NULL,
    TotalPrice NUMBER(10,2) NOT NULL,
    CONSTRAINT fk_booking_emp FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID),
    CONSTRAINT chk_booking_guests CHECK (NumGuests >= 1),
    CONSTRAINT chk_booking_dates  CHECK (CheckIn < CheckOut),
    CONSTRAINT chk_booking_status CHECK (Status IN ('Pending','Confirmed','Checkedin','Completed','Cancelled')),
    CONSTRAINT chk_booking_total_nonneg CHECK (TotalPrice >= 0)
);

CREATE TABLE Booking_Room (
    BookingID NUMBER NOT NULL,
    RoomID NUMBER NOT NULL,
    PRIMARY KEY (BookingID, RoomID),
    CONSTRAINT fk_br_booking FOREIGN KEY (BookingID) REFERENCES Booking(BookingID) ON DELETE CASCADE,
    CONSTRAINT fk_br_room FOREIGN KEY (RoomID) REFERENCES Room(RoomID) ON DELETE CASCADE
);

CREATE TABLE Booking_Customer (
    BookingID  NUMBER NOT NULL,
    CustomerID NUMBER NOT NULL,
    PRIMARY KEY (BookingID, CustomerID),
    CONSTRAINT fk_bc_booking FOREIGN KEY (BookingID) REFERENCES Booking(BookingID) ON DELETE CASCADE,
    CONSTRAINT fk_bc_customer FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID) ON DELETE CASCADE
);

CREATE TABLE Payment (
    PaymentID NUMBER PRIMARY KEY,
    BookingID NUMBER NOT NULL UNIQUE,
    Method VARCHAR2(10) NOT NULL,
    PaymentDate DATE NOT NULL,
    Amount NUMBER(10,2) NOT NULL,
    CONSTRAINT fk_payment_booking FOREIGN KEY (BookingID) REFERENCES Booking(BookingID) ON DELETE CASCADE,
    CONSTRAINT chk_payment_method CHECK (Method IN ('Cash','Credit','Debit')),
    CONSTRAINT chk_payment_amt_ CHECK (Amount >= 0)
);

CREATE TABLE Service_Booking (
    BookingID NUMBER NOT NULL,
    ServiceID NUMBER NOT NULL,
    Quantity NUMBER NOT NULL,
    TotalPrice NUMBER(10,2) NOT NULL,
    PRIMARY KEY (BookingID, ServiceID),
    CONSTRAINT fk_sb_booking FOREIGN KEY (BookingID)REFERENCES Booking(BookingID) ON DELETE CASCADE,
    CONSTRAINT fk_sb_service FOREIGN KEY (ServiceID)REFERENCES Service(ServiceID) ON DELETE CASCADE,
    CONSTRAINT chk_sb_qty CHECK (Quantity >= 1),
    CONSTRAINT chk_sb_total CHECK (TotalPrice >= 0)
);


exit;

EOF
