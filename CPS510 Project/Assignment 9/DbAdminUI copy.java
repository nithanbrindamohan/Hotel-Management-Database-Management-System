import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class DbAdminUI {

    private static final String JDBC_URL  = "jdbc:oracle:thin:@oracle12c.cs.ryerson.ca:1521:orcl12c";
    private static final String DB_USER   = "USER";
    private static final String DB_PASS   = "PASS";

    public static void main(String[] args) {

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Oracle JDBC driver not found.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

         while (running) {
            System.out.println("\n=== Hotel Reservation System ===");
            System.out.println("1. Create all tables");
            System.out.println("2. Drop all tables");
            System.out.println("3. Populate tables");
            System.out.println("4. Create views");
            System.out.println("5. Drop views");
            System.out.println("6. Run advanced queries");
            System.out.println("7. Run search");
            System.out.println("8. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    createAllTables();
                    break;
                case "2":
                    dropAllTables();
                    break;
                case "3":
                    populateTables();
                    break;
                case "4":
                    createViews();
                    break;
                case "5":
                    dropViews();
                    break;
                case "6":
                    runAdvancedQueriesMenu(scanner);
                    break;
                case "7":
                    runSearchMenu(scanner);;
                    break;
                case "8":
                    running = false;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid option, try again.");
            }
        }
        scanner.close();
    }

    private static void dropAllTables() {
        System.out.println("\nDropping all tables...");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {


            try {
                stmt.executeUpdate("DROP TABLE BOOKING CASCADE CONSTRAINTS");
                System.out.println("Dropped table BOOKING");
            } catch (SQLException e) {
                System.out.println("Could not drop BOOKING (maybe it doesn't exist): " + e.getMessage());
            }

            try {
                stmt.executeUpdate("DROP TABLE CUSTOMER CASCADE CONSTRAINTS");
                System.out.println("Dropped table CUSTOMER");
            } catch (SQLException e) {
                System.out.println("Could not drop CUSTOMER: " + e.getMessage());
            }

            try {
                stmt.executeUpdate("DROP TABLE ROOM CASCADE CONSTRAINTS");
                System.out.println("Dropped table ROOM");
            } catch (SQLException e) {
                System.out.println("Could not drop ROOM: " + e.getMessage());
            }

            try {
                stmt.executeUpdate("DROP TABLE EMPLOYEE CASCADE CONSTRAINTS");
                System.out.println("Dropped table EMPLOYEE");
            } catch (SQLException e) {
                System.out.println("Could not drop EMPLOYEE: " + e.getMessage());
            }

            try {
                stmt.executeUpdate("DROP TABLE SERVICE CASCADE CONSTRAINTS");
                System.out.println("Dropped table SERVICE");
            } catch (SQLException e) {
                System.out.println("Could not drop SERVICE: " + e.getMessage());
            }

            try {
                stmt.executeUpdate("DROP TABLE BOOKING_ROOM CASCADE CONSTRAINTS");
                System.out.println("Dropped table BOOKING_ROOM");
            } catch (SQLException e) {
                System.out.println("Could not drop BOOKING_ROOM: " + e.getMessage());
            }

            try {
                stmt.executeUpdate("DROP TABLE BOOKING_CUSTOMER CASCADE CONSTRAINTS");
                System.out.println("Dropped table BOOKING_CUSTOMER");
            } catch (SQLException e) {
                System.out.println("Could not drop BOOKING_CUSTOMER: " + e.getMessage());
            }

            try {
                stmt.executeUpdate("DROP TABLE PAYMENT CASCADE CONSTRAINTS");
                System.out.println("Dropped table PAYMENT");
            } catch (SQLException e) {
                System.out.println("Could not drop PAYMENT: " + e.getMessage());
            }

            try {
                stmt.executeUpdate("DROP TABLE SERVICE_BOOKING CASCADE CONSTRAINTS");
                System.out.println("Dropped table SERVICE_BOOKING");
            } catch (SQLException e) {
                System.out.println("Could not drop SERVICE_BOOKING: " + e.getMessage());
            }

            try {
                stmt.executeUpdate("DROP TABLE ROOM_TYPE CASCADE CONSTRAINTS");
                System.out.println("Dropped table ROOM_TYPE");
            } catch (SQLException e) {
                System.out.println("Could not drop ROOM_TYPE: " + e.getMessage());
            }

            System.out.println("Done attempting to drop tables.");

        } catch (SQLException e) {
            System.out.println("Database error while dropping tables: " + e.getMessage());
        }
    }

 private static void createAllTables() {
        System.out.println("\nCreating all tables...");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
             Statement stmt = conn.createStatement()) {

            // Order matters because of foreign keys:
            // 1) ROOM_TYPE
            stmt.executeUpdate("""
                CREATE TABLE ROOM_TYPE (
                    ROOMTYPEID   NUMBER       NOT NULL,
                    ROOMTYPENAME VARCHAR2(20) NOT NULL,
                    MAXCAPACITY  NUMBER       NOT NULL,
                    CONSTRAINT PK_ROOM_TYPE PRIMARY KEY (ROOMTYPEID)
                )
            """);
            System.out.println("Created table ROOM_TYPE");

            // 2) Customer
            stmt.executeUpdate("""
                CREATE TABLE Customer (
                    CustomerID NUMBER PRIMARY KEY,
                    CusName    VARCHAR2(100) NOT NULL,
                    PhoneNum   VARCHAR2(20)  NOT NULL UNIQUE,
                    Email      VARCHAR2(254) NOT NULL UNIQUE,
                    IDType     VARCHAR2(20)  NOT NULL,

                    CONSTRAINT uq_customer_phone_email UNIQUE (PhoneNum, Email),
                    CONSTRAINT chk_idtype CHECK (IDType IN ('Passport', 'DriverLicense', 'NationalID', 'Other')),
                    CONSTRAINT chk_phone_digits CHECK (REGEXP_LIKE(PhoneNum, '^[0-9]+$')),
                    CONSTRAINT chk_email_format CHECK (
                        REGEXP_LIKE(Email, '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$')
                    )
                )
            """);
            System.out.println("Created table Customer");

            // 3) Employee
            stmt.executeUpdate("""
                CREATE TABLE Employee (
                    EmployeeID NUMBER PRIMARY KEY,
                    EmpName    VARCHAR2(100) NOT NULL,
                    EmpRole    VARCHAR2(30)  NOT NULL,
                    PhoneNum   VARCHAR2(20)  NOT NULL UNIQUE,
                    Email      VARCHAR2(254) NOT NULL UNIQUE,
                    Salary     NUMBER(10,2)  NOT NULL,

                    CONSTRAINT uq_emp_phone_email UNIQUE (PhoneNum, Email),
                    CONSTRAINT chk_emp_role CHECK (
                        EmpRole IN ('Receptionist','Housekeeper','Cook','Manager','Owner')
                    ),
                    CONSTRAINT chk_emp_salary_nonnegative CHECK (Salary >= 0),
                    CONSTRAINT chk_emp_phone_digits CHECK (REGEXP_LIKE(PhoneNum, '^[0-9]+$')),
                    CONSTRAINT chk_emp_email_format CHECK (
                        REGEXP_LIKE(Email, '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$')
                    )
                )
            """);
            System.out.println("Created table Employee");

            // 4) Room (depends on ROOM_TYPE)
            stmt.executeUpdate("""
                CREATE TABLE Room (
                    RoomID   NUMBER PRIMARY KEY,
                    RoomNum  NUMBER       NOT NULL UNIQUE,
                    Floor    NUMBER       NOT NULL,
                    RoomType NUMBER       NOT NULL,
                    Price    NUMBER(10,2) NOT NULL,
                    Status   VARCHAR2(20) NOT NULL,

                    CONSTRAINT chk_floor_nonnegative CHECK (Floor >= 1),
                    CONSTRAINT chk_price_nonnegative CHECK (Price >= 0),
                    CONSTRAINT chk_status CHECK (Status IN ('Available', 'Unavailable', 'Maintenance')),
                    CONSTRAINT FK_ROOM_ROOMTYPE FOREIGN KEY (RoomType)
                        REFERENCES ROOM_TYPE(ROOMTYPEID)
                )
            """);
            System.out.println("Created table Room");

            // 5) Service
            stmt.executeUpdate("""
                CREATE TABLE Service (
                    ServiceID   NUMBER       PRIMARY KEY,
                    ServiceName VARCHAR2(100) NOT NULL UNIQUE,
                    Price       NUMBER(10,2)  NOT NULL,

                    CONSTRAINT chk_service_price_nonnegative CHECK (Price >= 0)
                )
            """);
            System.out.println("Created table Service");

            // 6) Booking (depends on Employee)
            stmt.executeUpdate("""
                CREATE TABLE Booking (
                    BookingID  NUMBER       PRIMARY KEY,
                    EmployeeID NUMBER       NOT NULL,
                    NumGuests  NUMBER       NOT NULL,
                    CheckIn    DATE         NOT NULL,
                    CheckOut   DATE         NOT NULL,
                    Status     VARCHAR2(20) NOT NULL,
                    TotalPrice NUMBER(10,2) NOT NULL,

                    CONSTRAINT fk_booking_emp FOREIGN KEY (EmployeeID)
                        REFERENCES Employee(EmployeeID),
                    CONSTRAINT chk_booking_guests CHECK (NumGuests >= 1),
                    CONSTRAINT chk_booking_dates CHECK (CheckIn < CheckOut),
                    CONSTRAINT chk_booking_status CHECK (
                        Status IN ('Pending','Confirmed','Checkedin','Completed','Cancelled')
                    ),
                    CONSTRAINT chk_booking_total_nonneg CHECK (TotalPrice >= 0)
                )
            """);
            System.out.println("Created table Booking");

            // 7) Booking_Room (depends on Booking, Room)
            stmt.executeUpdate("""
                CREATE TABLE Booking_Room (
                    BookingID NUMBER NOT NULL,
                    RoomID    NUMBER NOT NULL,
                    PRIMARY KEY (BookingID, RoomID),

                    CONSTRAINT fk_br_booking FOREIGN KEY (BookingID)
                        REFERENCES Booking(BookingID) ON DELETE CASCADE,
                    CONSTRAINT fk_br_room FOREIGN KEY (RoomID)
                        REFERENCES Room(RoomID) ON DELETE CASCADE
                )
            """);
            System.out.println("Created table Booking_Room");

            // 8) Booking_Customer (depends on Booking, Customer)
            stmt.executeUpdate("""
                CREATE TABLE Booking_Customer (
                    BookingID  NUMBER NOT NULL,
                    CustomerID NUMBER NOT NULL,
                    PRIMARY KEY (BookingID, CustomerID),

                    CONSTRAINT fk_bc_booking FOREIGN KEY (BookingID)
                        REFERENCES Booking(BookingID) ON DELETE CASCADE,
                    CONSTRAINT fk_bc_customer FOREIGN KEY (CustomerID)
                        REFERENCES Customer(CustomerID) ON DELETE CASCADE
                )
            """);
            System.out.println("Created table Booking_Customer");

            // 9) Payment (depends on Booking)
            stmt.executeUpdate("""
                CREATE TABLE Payment (
                    PaymentID   NUMBER       PRIMARY KEY,
                    BookingID   NUMBER       NOT NULL UNIQUE,
                    Method      VARCHAR2(10) NOT NULL,
                    PaymentDate DATE         NOT NULL,
                    Amount      NUMBER(10,2) NOT NULL,

                    CONSTRAINT fk_payment_booking FOREIGN KEY (BookingID)
                        REFERENCES Booking(BookingID) ON DELETE CASCADE,
                    CONSTRAINT chk_payment_method CHECK (Method IN ('Cash','Credit','Debit')),
                    CONSTRAINT chk_payment_amt CHECK (Amount >= 0)
                )
            """);
            System.out.println("Created table Payment");

            // 10) Service_Booking (depends on Booking, Service)
            stmt.executeUpdate("""
                CREATE TABLE Service_Booking (
                    BookingID NUMBER NOT NULL,
                    ServiceID NUMBER NOT NULL,
                    Quantity  NUMBER NOT NULL,

                    PRIMARY KEY (BookingID, ServiceID),

                    CONSTRAINT fk_sb_booking FOREIGN KEY (BookingID)
                        REFERENCES Booking(BookingID) ON DELETE CASCADE,
                    CONSTRAINT fk_sb_service FOREIGN KEY (ServiceID)
                        REFERENCES Service(ServiceID) ON DELETE CASCADE,
                    CONSTRAINT chk_sb_qty CHECK (Quantity >= 1)
                )
            """);
            System.out.println("Created table Service_Booking");

            System.out.println("All tables created successfully.");

        } catch (SQLException e) {
            System.out.println("Database error while creating tables:");
            System.out.println(e.getMessage());
        }
    }

private static void populateTables() {
    System.out.println("\nPopulating tables with sample data...");

    try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
         Statement stmt = conn.createStatement()) {

        // --- ROOM_TYPE ---
        stmt.executeUpdate("""
            INSERT INTO ROOM_TYPE VALUES (1, 'Single', 1)
        """);
        stmt.executeUpdate("""
            INSERT INTO ROOM_TYPE VALUES (2, 'Double', 2)
        """);
        stmt.executeUpdate("""
            INSERT INTO ROOM_TYPE VALUES (3, 'Family', 4)
        """);

        // --- CUSTOMERS ---
        stmt.executeUpdate("""
            INSERT INTO Customer (CustomerID, CusName, PhoneNum, Email, IDType)
            VALUES (1, 'John Smith', '4161234567', 'john.smith@example.com', 'Passport')
        """);
        stmt.executeUpdate("""
            INSERT INTO Customer (CustomerID, CusName, PhoneNum, Email, IDType)
            VALUES (2, 'Maria Gonzalez', '6479876543', 'maria.gonzalez@example.com', 'DriverLicense')
        """);
        stmt.executeUpdate("""
            INSERT INTO Customer (CustomerID, CusName, PhoneNum, Email, IDType)
            VALUES (3, 'David Chen', '4375551122', 'david.chen@example.com', 'NationalID')
        """);
        stmt.executeUpdate("""
            INSERT INTO Customer (CustomerID, CusName, PhoneNum, Email, IDType)
            VALUES (4, 'Aisha Khan', '2893217788', 'aisha.khan@example.com', 'Other')
        """);

        // --- EMPLOYEES ---
        stmt.executeUpdate("""
            INSERT INTO Employee (EmployeeID, EmpName, EmpRole, PhoneNum, Email, Salary)
            VALUES (1, 'Alice Johnson', 'Receptionist', '4165551001', 'alice.johnson@example.com', 42000.00)
        """);
        stmt.executeUpdate("""
            INSERT INTO Employee (EmployeeID, EmpName, EmpRole, PhoneNum, Email, Salary)
            VALUES (2, 'Brian Lee', 'Housekeeper', '4165551002', 'brian.lee@example.com', 35000.00)
        """);
        stmt.executeUpdate("""
            INSERT INTO Employee (EmployeeID, EmpName, EmpRole, PhoneNum, Email, Salary)
            VALUES (3, 'Carlos Rivera', 'Cook', '4165551003', 'carlos.rivera@example.com', 38000.00)
        """);
        stmt.executeUpdate("""
            INSERT INTO Employee (EmployeeID, EmpName, EmpRole, PhoneNum, Email, Salary)
            VALUES (4, 'Diana Patel', 'Manager', '4165551004', 'diana.patel@example.com', 60000.00)
        """);
        stmt.executeUpdate("""
            INSERT INTO Employee (EmployeeID, EmpName, EmpRole, PhoneNum, Email, Salary)
            VALUES (5, 'Edward Brown', 'Owner', '4165551005', 'edward.brown@example.com', 80000.00)
        """);

        // --- ROOMS ---
        stmt.executeUpdate("""
            INSERT INTO Room (RoomID, RoomNum, Floor, RoomType, Price, Status)
            VALUES (1, 101, 1, 1, 120.00, 'Available')
        """);
        stmt.executeUpdate("""
            INSERT INTO Room (RoomID, RoomNum, Floor, RoomType, Price, Status)
            VALUES (2, 102, 1, 2, 180.00, 'Available')
        """);
        stmt.executeUpdate("""
            INSERT INTO Room (RoomID, RoomNum, Floor, RoomType, Price, Status)
            VALUES (3, 201, 2, 3, 300.00, 'Unavailable')
        """);
        stmt.executeUpdate("""
            INSERT INTO Room (RoomID, RoomNum, Floor, RoomType, Price, Status)
            VALUES (4, 202, 2, 1, 110.00, 'Maintenance')
        """);
        stmt.executeUpdate("""
            INSERT INTO Room (RoomID, RoomNum, Floor, RoomType, Price, Status)
            VALUES (5, 301, 3, 2, 200.00, 'Available')
        """);

        // --- SERVICES ---
        stmt.executeUpdate("""
            INSERT INTO Service (ServiceID, ServiceName, Price)
            VALUES (1, 'Breakfast Buffet', 20.00)
        """);
        stmt.executeUpdate("""
            INSERT INTO Service (ServiceID, ServiceName, Price)
            VALUES (2, 'Airport Shuttle', 45.00)
        """);
        stmt.executeUpdate("""
            INSERT INTO Service (ServiceID, ServiceName, Price)
            VALUES (3, 'Laundry Service', 15.00)
        """);
        stmt.executeUpdate("""
            INSERT INTO Service (ServiceID, ServiceName, Price)
            VALUES (4, 'Spa Package', 100.00)
        """);
        stmt.executeUpdate("""
            INSERT INTO Service (ServiceID, ServiceName, Price)
            VALUES (5, 'Extra Bed', 30.00)
        """);

        // --- BOOKING 1001 + relations ---
        stmt.executeUpdate("""
            INSERT INTO Booking (BookingID, EmployeeID, NumGuests, CheckIn, CheckOut, Status, TotalPrice)
            VALUES (1001, 1, 3, DATE '2025-10-01', DATE '2025-10-05', 'Confirmed', 1075.00)
        """);

        stmt.executeUpdate("""
            INSERT INTO Booking_Room (BookingID, RoomID)
            VALUES (1001, 2)
        """);
        stmt.executeUpdate("""
            INSERT INTO Booking_Room (BookingID, RoomID)
            VALUES (1001, 5)
        """);

        stmt.executeUpdate("""
            INSERT INTO Booking_Customer (BookingID, CustomerID)
            VALUES (1001, 1)
        """);
        stmt.executeUpdate("""
            INSERT INTO Booking_Customer (BookingID, CustomerID)
            VALUES (1001, 2)
        """);

        stmt.executeUpdate("""
            INSERT INTO Payment (PaymentID, BookingID, Method, PaymentDate, Amount)
            VALUES (5001, 1001, 'Credit', DATE '2025-09-20', 1075.00)
        """);

        // --- BOOKING 1002 + relations ---
        stmt.executeUpdate("""
            INSERT INTO Booking (BookingID, EmployeeID, NumGuests, CheckIn, CheckOut, Status, TotalPrice)
            VALUES (1002, 4, 1, DATE '2025-11-10', DATE '2025-11-12', 'Confirmed', 240.00)
        """);

        stmt.executeUpdate("""
            INSERT INTO Booking_Room (BookingID, RoomID)
            VALUES (1002, 1)
        """);

        stmt.executeUpdate("""
            INSERT INTO Booking_Customer (BookingID, CustomerID)
            VALUES (1002, 3)
        """);

        stmt.executeUpdate("""
            INSERT INTO Payment (PaymentID, BookingID, Method, PaymentDate, Amount)
            VALUES (5002, 1002, 'Debit', DATE '2025-10-25', 240.00)
        """);

        // --- SERVICE_BOOKING ---
        stmt.executeUpdate("""
            INSERT INTO Service_Booking (BookingID, ServiceID, Quantity)
            VALUES (1001, 1, 12)
        """);
        stmt.executeUpdate("""
            INSERT INTO Service_Booking (BookingID, ServiceID, Quantity)
            VALUES (1001, 2, 1)
        """);
        stmt.executeUpdate("""
            INSERT INTO Service_Booking (BookingID, ServiceID, Quantity)
            VALUES (1002, 1, 2)
        """);

        System.out.println("Sample data inserted successfully.");

    } catch (SQLException e) {
        System.out.println("Database error while populating tables:");
        System.out.println(e.getMessage());
    }
}

private static void createViews() {
    System.out.println("\nCreating views...");

    try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
         Statement stmt = conn.createStatement()) {

        // --- VIEW 1: VIEW_BOOKING_WITH_CUSTOMERS ---
        try {
            stmt.executeUpdate("""
                CREATE OR REPLACE VIEW VIEW_BOOKING_WITH_CUSTOMERS AS
                SELECT
                    b.BookingID,
                    TO_CHAR(b.CheckIn,  'YYYY-MM-DD') AS CheckIn,
                    TO_CHAR(b.CheckOut, 'YYYY-MM-DD') AS CheckOut,
                    b.Status,
                    b.NumGuests,
                    b.TotalPrice,
                    e.EmpName AS HandledBy,
                    LISTAGG(c.CusName, ', ') 
                        WITHIN GROUP (ORDER BY c.CusName) AS Customers
                FROM Booking b
                JOIN Employee e
                    ON e.EmployeeID = b.EmployeeID
                LEFT JOIN Booking_Customer bc
                    ON bc.BookingID = b.BookingID
                LEFT JOIN Customer c
                    ON c.CustomerID = bc.CustomerID
                GROUP BY
                    b.BookingID, b.CheckIn, b.CheckOut, b.Status,
                    b.NumGuests, b.TotalPrice, e.EmpName
            """);
            System.out.println("Created VIEW_BOOKING_WITH_CUSTOMERS");
        } catch (SQLException ex) {
            System.out.println("Could not create VIEW_BOOKING_WITH_CUSTOMERS: " + ex.getMessage());
        }

        // --- VIEW 2: VIEW_ACTIVE_CUSTOMERS ---
        try {
            stmt.executeUpdate("""
                CREATE OR REPLACE VIEW VIEW_ACTIVE_CUSTOMERS AS
                SELECT DISTINCT
                    c.CustomerID,
                    c.CusName,
                    c.Email
                FROM Customer c
                JOIN Booking_Customer bc
                    ON bc.CustomerID = c.CustomerID
            """);
            System.out.println("Created VIEW_ACTIVE_CUSTOMERS");
        } catch (SQLException ex) {
            System.out.println("Could not create VIEW_ACTIVE_CUSTOMERS: " + ex.getMessage());
        }

        // --- VIEW 3: VIEW_ACTIVE_CUSTOMERS ---
        try {
            stmt.executeUpdate("""
                CREATE OR REPLACE VIEW VIEW_BOOKING_SERVICES AS
                SELECT
                    b.BookingID,
                    TO_CHAR(b.CheckIn,  'YYYY-MM-DD') AS CheckIn,
                    TO_CHAR(b.CheckOut, 'YYYY-MM-DD') AS CheckOut,
                    e.EmpName AS EmployeeName,
                    c.CusName AS CustomerName,
                    s.ServiceName,
                    sb.Quantity,
                    (s.Price * sb.Quantity) AS TotalPrice
                FROM Booking b
                JOIN Employee e
                    ON e.EmployeeID = b.EmployeeID
                JOIN Booking_Customer bc
                    ON bc.BookingID = b.BookingID
                JOIN Customer c
                    ON c.CustomerID = bc.CustomerID
                JOIN Service_Booking sb
                    ON sb.BookingID = b.BookingID
                JOIN Service s
                    ON s.ServiceID = sb.ServiceID;

            """);
            System.out.println("Created VIEW_BOOKING_SERVICES");
        } catch (SQLException ex) {
            System.out.println("Could not create VIEW_BOOKING_SERVICES: " + ex.getMessage());
        }

        System.out.println("All views created successfully.");

    } catch (SQLException e) {
        System.out.println("Error creating views:");
        System.out.println(e.getMessage());
    }
}

private static void dropViews() {
    System.out.println("\nDropping all views...");

    try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
         Statement stmt = conn.createStatement()) {

        try {
            stmt.executeUpdate("DROP VIEW VIEW_BOOKING_WITH_CUSTOMERS");
            System.out.println("Dropped VIEW_BOOKING_WITH_CUSTOMERS");
        } catch (SQLException e) {
            System.out.println("Skip VIEW_BOOKING_WITH_CUSTOMERS: " + e.getMessage());
        }

        try {
            stmt.executeUpdate("DROP VIEW VIEW_ACTIVE_CUSTOMERS");
            System.out.println("Dropped VIEW_ACTIVE_CUSTOMERS");
        } catch (SQLException e) {
            System.out.println("Skip VIEW_ACTIVE_CUSTOMERS: " + e.getMessage());
        }

        try {
            stmt.executeUpdate("DROP VIEW VIEW_BOOKING_SERVICES");
            System.out.println("Dropped VIEW_BOOKING_SERVICES");
        } catch (SQLException e) {
            System.out.println("Skip VIEW_BOOKING_SERVICES: " + e.getMessage());
        }

        System.out.println("Done attempting to drop views.");

    } catch (SQLException e) {
        System.out.println("Database error while dropping views:");
        System.out.println(e.getMessage());
    }
}

private static void runAdvancedQueriesMenu(Scanner scanner) {
    boolean back = false;

    while (!back) {
        System.out.println("\n=== Advanced Queries Menu ===");
        System.out.println("1. Customers with NO bookings");
        System.out.println("2. Employees with NO bookings OR only bookings with TotalPrice < 300");
        System.out.println("3. For each booking, count unique services (only where count >= 2)");
        System.out.println("4. Total quantity of each service across all bookings");
        System.out.println("5. Employees with bookings, excluding those earning >= 50000");
        System.out.println("6. Number of bookings per room type (only where > 0)");
        System.out.println("7. Back to main menu");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                queryCustomersWithNoBookings();
                break;
            case "2":
                queryEmployeesNoOrLowPriceBookings();
                break;
            case "3":
                queryBookingsWithServiceCounts();
                break;
            case "4":
                queryServiceTotalQuantities();
                break;
            case "5":
                queryEmployeesWithBookingsButLowSalary();
                break;
            case "6":
                queryRoomTypeBookingCounts();
                break;
            case "7":
                back = true;
                break;
            default:
                System.out.println("Invalid option, try again.");
        }
    }
}

private static void queryCustomersWithNoBookings() {
    System.out.println("\n[Q1] Customers that have NOT made any bookings:");

    String sql = """
        SELECT C.CUSTOMERID, C.CUSNAME
        FROM CUSTOMER C
        WHERE NOT EXISTS (
            SELECT 1
            FROM BOOKING_CUSTOMER BC
            WHERE C.CUSTOMERID = BC.CUSTOMERID
        )
    """;

    try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        boolean any = false;
        while (rs.next()) {
            any = true;
            int id = rs.getInt("CUSTOMERID");
            String name = rs.getString("CUSNAME");
            System.out.printf("CustomerID=%d, Name=%s%n", id, name);
        }
        if (!any) {
            System.out.println("No such customers found.");
        }

    } catch (SQLException e) {
        System.out.println("Error in Q1: " + e.getMessage());
    }
}

private static void queryEmployeesNoOrLowPriceBookings() {
    System.out.println("\n[Q2] Employees with NO bookings OR only bookings with TotalPrice < 300:");

    String sql = """
        SELECT E.EMPLOYEEID, E.EMPNAME
        FROM EMPLOYEE E
        WHERE NOT EXISTS (
            SELECT 1 FROM BOOKING B WHERE E.EMPLOYEEID = B.EMPLOYEEID
        )
        UNION
        SELECT E.EMPLOYEEID, E.EMPNAME
        FROM EMPLOYEE E
        WHERE EXISTS (
            SELECT 1 FROM BOOKING B
            WHERE B.EMPLOYEEID = E.EMPLOYEEID
        )
        AND EXISTS (
            SELECT 1 FROM BOOKING B
            WHERE B.EMPLOYEEID = E.EMPLOYEEID
              AND B.TOTALPRICE < 300
        )
        ORDER BY EMPLOYEEID
    """;

    try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        boolean any = false;
        while (rs.next()) {
            any = true;
            int id = rs.getInt("EMPLOYEEID");
            String name = rs.getString("EMPNAME");
            System.out.printf("EmployeeID=%d, Name=%s%n", id, name);
        }
        if (!any) {
            System.out.println("No such employees found.");
        }

    } catch (SQLException e) {
        System.out.println("Error in Q2: " + e.getMessage());
    }
}

private static void queryBookingsWithServiceCounts() {
    System.out.println("\n[Q3] Bookings with COUNT(DISTINCT ServiceID) >= 2:");

    String sql = """
        SELECT SB.BOOKINGID, COUNT(DISTINCT SB.SERVICEID) AS NUMSERVICES
        FROM SERVICE_BOOKING SB
        GROUP BY SB.BOOKINGID
        HAVING COUNT(DISTINCT SB.SERVICEID) >= 2
        ORDER BY SB.BOOKINGID
    """;

    try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        boolean any = false;
        while (rs.next()) {
            any = true;
            int bookingId = rs.getInt("BOOKINGID");
            int numServices = rs.getInt("NUMSERVICES");
            System.out.printf("BookingID=%d, NumServices=%d%n", bookingId, numServices);
        }
        if (!any) {
            System.out.println("No bookings found with >= 2 distinct services.");
        }

    } catch (SQLException e) {
        System.out.println("Error in Q3: " + e.getMessage());
    }
}

private static void queryServiceTotalQuantities() {
    System.out.println("\n[Q4] Total quantity of each service across all bookings:");

    String sql = """
        SELECT S.SERVICEID, S.SERVICENAME, SUM(SB.QUANTITY) AS TOTALQUANTITY
        FROM SERVICE S, SERVICE_BOOKING SB
        WHERE S.SERVICEID = SB.SERVICEID
        GROUP BY S.SERVICEID, S.SERVICENAME
        ORDER BY TOTALQUANTITY DESC
    """;

    try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        boolean any = false;
        while (rs.next()) {
            any = true;
            int serviceId = rs.getInt("SERVICEID");
            String name = rs.getString("SERVICENAME");
            int totalQty = rs.getInt("TOTALQUANTITY");
            System.out.printf("ServiceID=%d, Name=%s, TotalQuantity=%d%n",
                    serviceId, name, totalQty);
        }
        if (!any) {
            System.out.println("No service quantities found.");
        }

    } catch (SQLException e) {
        System.out.println("Error in Q4: " + e.getMessage());
    }
}

private static void queryEmployeesWithBookingsButLowSalary() {
    System.out.println("\n[Q5] Employees with at least one booking, but salary < 50000:");

    String sql = """
        SELECT E.EMPLOYEEID, E.EMPNAME
        FROM EMPLOYEE E, BOOKING B
        WHERE E.EMPLOYEEID = B.EMPLOYEEID
        MINUS
        SELECT E.EMPLOYEEID, E.EMPNAME
        FROM EMPLOYEE E
        WHERE E.SALARY >= 50000
    """;

    try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        boolean any = false;
        while (rs.next()) {
            any = true;
            int id = rs.getInt("EMPLOYEEID");
            String name = rs.getString("EMPNAME");
            System.out.printf("EmployeeID=%d, Name=%s%n", id, name);
        }
        if (!any) {
            System.out.println("No such employees found.");
        }

    } catch (SQLException e) {
        System.out.println("Error in Q5: " + e.getMessage());
    }
}

private static void queryRoomTypeBookingCounts() {
    System.out.println("\n[Q6] Number of bookings associated with each room type (only > 0):");

    String sql = """
        SELECT R.ROOMTYPE, COUNT(BR.BOOKINGID) AS NUMBOOKINGS
        FROM ROOM R, BOOKING_ROOM BR
        WHERE R.ROOMID = BR.ROOMID
        GROUP BY R.ROOMTYPE
        ORDER BY NUMBOOKINGS DESC
    """;

    try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {

        boolean any = false;
        while (rs.next()) {
            any = true;
            int roomTypeId = rs.getInt("ROOMTYPE");
            int numBookings = rs.getInt("NUMBOOKINGS");
            System.out.printf("RoomTypeID=%d, NumBookings=%d%n", roomTypeId, numBookings);
        }
        if (!any) {
            System.out.println("No room types with bookings found.");
        }

    } catch (SQLException e) {
        System.out.println("Error in Q6: " + e.getMessage());
    }
}

private static void runSearchMenu(Scanner scanner) {
    boolean back = false;

    while (!back) {
        System.out.println("\n=== Search Menu ===");
        System.out.println("1. Search Customer by ID");
        System.out.println("2. Search Employee by ID");
        System.out.println("3. Search Room by ID");
        System.out.println("4. Search Booking by ID");
        System.out.println("5. Back to main menu");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.print("Enter CustomerID: ");
                int custId = Integer.parseInt(scanner.nextLine());
                searchById("Customer", "CustomerID", custId);
                break;

            case "2":
                System.out.print("Enter EmployeeID: ");
                int empId = Integer.parseInt(scanner.nextLine());
                searchById("Employee", "EmployeeID", empId);
                break;

            case "3":
                System.out.print("Enter RoomID: ");
                int roomId = Integer.parseInt(scanner.nextLine());
                searchById("Room", "RoomID", roomId);
                break;

            case "4":
                System.out.print("Enter BookingID: ");
                int bookingId = Integer.parseInt(scanner.nextLine());
                searchById("Booking", "BookingID", bookingId);
                break;

            case "5":
                back = true;
                break;

            default:
                System.out.println("Invalid option, try again.");
        }
    }
}

private static void searchById(String tableName, String idColumn, int idValue) {
    System.out.println("\nSearching " + tableName + " for " + idColumn + " = " + idValue);

    String sql = "SELECT * FROM " + tableName + " WHERE " + idColumn + " = ?";

    try (Connection conn = DriverManager.getConnection(JDBC_URL, DB_USER, DB_PASS);
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idValue);

        try (ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                System.out.println("No record found.");
                return;
            }

            // Print each column dynamically
            int columnCount = rs.getMetaData().getColumnCount();
            do {
                System.out.println("------------------------------");
                for (int i = 1; i <= columnCount; i++) {
                    String colName = rs.getMetaData().getColumnName(i);
                    String value = rs.getString(i);
                    System.out.printf("%s: %s%n", colName, value);
                }
            } while (rs.next());
        }

    } catch (SQLException e) {
        System.out.println("Error searching " + tableName + ":        " + e.getMessage());
    }
}

}
