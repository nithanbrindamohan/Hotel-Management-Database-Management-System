#!/bin/sh
#export LD_LIBRARY_PATH=/usr/lib/oracle/12.1/client64/lib


sqlplus64 "s53chowd/09172572@(DESCRIPTION=(ADDRESS=(PROTOCOL=TCP)(Host=oracle12c.scs.ryerson.ca)(Port=1521))(CONNECT_DATA=(SID=orcl12c)))" <<EOF

INSERT INTO Customer (CustomerID, CusName, PhoneNum, Email, IDType)
VALUES (1, 'John Smith', '4161234567', 'john.smith@example.com', 'Passport');

INSERT INTO Customer (CustomerID, CusName, PhoneNum, Email, IDType)
VALUES (2, 'Maria Gonzalez', '6479876543', 'maria.gonzalez@example.com', 'DriverLicense');

INSERT INTO Customer (CustomerID, CusName, PhoneNum, Email, IDType)
VALUES (3, 'David Chen', '4375551122', 'david.chen@example.com', 'NationalID');

INSERT INTO Customer (CustomerID, CusName, PhoneNum, Email, IDType)
VALUES (4, 'Aisha Khan', '2893217788', 'aisha.khan@example.com', 'Other');

INSERT INTO Employee (EmployeeID, EmpName, EmpRole, PhoneNum, Email, Salary)
VALUES (1, 'Alice Johnson', 'Receptionist', '4165551001', 'alice.johnson@example.com', 42000.00);

INSERT INTO Employee (EmployeeID, EmpName, EmpRole, PhoneNum, Email, Salary)
VALUES (2, 'Brian Lee', 'Housekeeper', '4165551002', 'brian.lee@example.com', 35000.00);

INSERT INTO Employee (EmployeeID, EmpName, EmpRole, PhoneNum, Email, Salary)
VALUES (3, 'Carlos Rivera', 'Cook', '4165551003', 'carlos.rivera@example.com', 38000.00);

INSERT INTO Employee (EmployeeID, EmpName, EmpRole, PhoneNum, Email, Salary)
VALUES (4, 'Diana Patel', 'Manager', '4165551004', 'diana.patel@example.com', 60000.00);

INSERT INTO Employee (EmployeeID, EmpName, EmpRole, PhoneNum, Email, Salary)
VALUES (5, 'Edward Brown', 'Owner', '4165551005', 'edward.brown@example.com', 80000.00);

INSERT INTO Room (RoomID, RoomNum, Floor, RoomType, MaxCap, Price, Status)
VALUES (1, 101, 1, 'Single', 1, 120.00, 'Available');

INSERT INTO Room (RoomID, RoomNum, Floor, RoomType, MaxCap, Price, Status)
VALUES (2, 102, 1, 'Double', 2, 180.00, 'Available');

INSERT INTO Room (RoomID, RoomNum, Floor, RoomType, MaxCap, Price, Status)
VALUES (3, 201, 2, 'Family', 4, 300.00, 'Unavailable');

INSERT INTO Room (RoomID, RoomNum, Floor, RoomType, MaxCap, Price, Status)
VALUES (4, 202, 2, 'Single', 1, 110.00, 'Maintenance');

INSERT INTO Room (RoomID, RoomNum, Floor, RoomType, MaxCap, Price, Status)
VALUES (5, 301, 3, 'Double', 2, 200.00, 'Available');

INSERT INTO Service (ServiceID, ServiceName, Price)
VALUES (1, 'Breakfast Buffet', 20.00);

INSERT INTO Service (ServiceID, ServiceName, Price)
VALUES (2, 'Airport Shuttle', 45.00);

INSERT INTO Service (ServiceID, ServiceName, Price)
VALUES (3, 'Laundry Service', 15.00);

INSERT INTO Service (ServiceID, ServiceName, Price)
VALUES (4, 'Spa Package', 100.00);

INSERT INTO Service (ServiceID, ServiceName, Price)
VALUES (5, 'Extra Bed', 30.00);

INSERT INTO Booking (BookingID, EmployeeID, NumGuests, CheckIn, CheckOut, Status, TotalPrice)
VALUES (1001, 1, 3, DATE '2025-10-01', DATE '2025-10-05', 'Confirmed', 1075.00);

INSERT INTO Booking_Room (BookingID, RoomID) VALUES (1001, 2);
INSERT INTO Booking_Room (BookingID, RoomID) VALUES (1001, 5);

INSERT INTO Booking_Customer (BookingID, CustomerID) VALUES (1001, 1);
INSERT INTO Booking_Customer (BookingID, CustomerID) VALUES (1001, 2);

INSERT INTO Payment (PaymentID, BookingID, Method, PaymentDate, Amount)
VALUES (5001, 1001, 'Credit', DATE '2025-09-20', 1075.00);

INSERT INTO Booking (BookingID, EmployeeID, NumGuests, CheckIn, CheckOut, Status, TotalPrice)
VALUES (1002, 4, 1, DATE '2025-11-10', DATE '2025-11-12', 'Confirmed', 240.00);

INSERT INTO Booking_Room (BookingID, RoomID) VALUES (1002, 1);  -- Room 101 (Single)

INSERT INTO Booking_Customer (BookingID, CustomerID) VALUES (1002, 3);

INSERT INTO Payment (PaymentID, BookingID, Method, PaymentDate, Amount)
VALUES (5002, 1002, 'Debit', DATE '2025-10-25', 240.00);

INSERT INTO Service_Booking (BookingID, ServiceID, Quantity, TotalPrice)
VALUES (1001, 1, 12, 240.00);

INSERT INTO Service_Booking (BookingID, ServiceID, Quantity, TotalPrice)
VALUES (1001, 2, 1, 45.00);

INSERT INTO Service_Booking (BookingID, ServiceID, Quantity, TotalPrice)
VALUES (1002, 1, 2, 40.00);

INSERT INTO Service_Booking (BookingID, ServiceID, Quantity, TotalPrice)
VALUES (1002, 3, 1, 15.00);

exit;

EOF
