// Enums

import java.util.*;

enum SeatStatus {
    AVAILABLE,
    BOOKED,
    RESERVED
}

enum BookingStatus {
    CONFIRMED,
    CANCELLED,
    PENDING
}

enum PaymentStatus {
    PENDING,
    SUCCESSFUL,
    FAILED
}

// Abstract User class
abstract class User {
    String id;
    String name;
    String email;
    String phoneNumber;
}

// Passenger (extends User)
class Passenger extends User {
    List<Booking> bookings;

    Passenger(String id, String name, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.bookings = new ArrayList<>();
    }
}

// Staff (extends User)
class Staff extends User {
    String role;

    Staff(String id, String name, String email, String phoneNumber, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }
}

// Admin (extends User)
class Admin extends User {
    Admin(String id, String name, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}

// Aircraft
class Aircraft {
    String tailNumber;
    String model;
    int totalSeats;

    Aircraft(String tailNumber, String model, int totalSeats) {
        this.tailNumber = tailNumber;
        this.model = model;
        this.totalSeats = totalSeats;
    }
}

// Flight
class Flight {
    String flightNumber;
    String source;
    String destination;
    Date departureTime;
    Date arrivalTime;
    Aircraft aircraft;
    List<Seat> seats;

    Flight(String flightNumber, String source, String destination, Date departureTime, Date arrivalTime, Aircraft aircraft) {
        this.flightNumber = flightNumber;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.aircraft = aircraft;
        this.seats = new ArrayList<>();
    }
}

// Seat
class Seat {
    String seatNumber;
    String seatType;
    SeatStatus status;

    Seat(String seatNumber, String seatType) {
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.status = SeatStatus.AVAILABLE;
    }
}

// Booking
class Booking {
    String bookingNumber;
    Flight flight;
    Passenger passenger;
    Seat seat;
    double price;
    BookingStatus status;

    Booking(String bookingNumber, Flight flight, Passenger passenger, Seat seat, double price) {
        this.bookingNumber = bookingNumber;
        this.flight = flight;
        this.passenger = passenger;
        this.seat = seat;
        this.price = price;
        this.status = BookingStatus.CONFIRMED;
    }
}

// Payment
class Payment {
    String paymentId;
    String paymentMethod;
    double amount;
    PaymentStatus status;

    Payment(String paymentId, String paymentMethod, double amount) {
        this.paymentId = paymentId;
        this.paymentMethod = paymentMethod;
        this.amount = amount;
        this.status = PaymentStatus.PENDING;
    }
}

// FlightSearch
class FlightSearch {
    public List<Flight> searchFlights(List<Flight> flights, String source, String destination, Date date) {
        List<Flight> results = new ArrayList<>();
        for (Flight flight : flights) {
            if (flight.source.equals(source) && flight.destination.equals(destination) && flight.departureTime.equals(date)) {
                results.add(flight);
            }
        }
        return results;
    }
}

// BookingManager (Singleton)
class BookingManager {
    private static BookingManager instance;

    private BookingManager() {}

    public static BookingManager getInstance() {
        if (instance == null) {
            instance = new BookingManager();
        }
        return instance;
    }

    public Booking createBooking(Passenger passenger, Flight flight, Seat seat, double price) {
        String bookingNumber = "B" + System.currentTimeMillis();
        Booking booking = new Booking(bookingNumber, flight, passenger, seat, price);
        passenger.bookings.add(booking);
        seat.status = SeatStatus.BOOKED;
        System.out.println("Booking created: " + bookingNumber);
        return booking;
    }

    public void cancelBooking(Booking booking) {
        booking.status = BookingStatus.CANCELLED;
        booking.seat.status = SeatStatus.AVAILABLE;
        System.out.println("Booking " + booking.bookingNumber + " cancelled.");
    }
}

// PaymentProcessor (Singleton)
class PaymentProcessor {
    private static PaymentProcessor instance;

    private PaymentProcessor() {}

    public static PaymentProcessor getInstance() {
        if (instance == null) {
            instance = new PaymentProcessor();
        }
        return instance;
    }

    public void processPayment(Payment payment) {
        payment.status = PaymentStatus.SUCCESSFUL;
        System.out.println("Payment " + payment.paymentId + " successful.");
    }

    public void refundPayment(Payment payment) {
        payment.status = PaymentStatus.FAILED;
        System.out.println("Payment " + payment.paymentId + " refunded.");
    }
}

// AirlineManagementSystem (Main Entry Point)
public class AirlineManagement {
    public static void main(String[] args) {
        // Example initialization
        Aircraft aircraft = new Aircraft("N12345", "Boeing 737", 180);
        Flight flight = new Flight("AI101", "Delhi", "Mumbai", new Date(), new Date(), aircraft);
        Seat seat = new Seat("1A", "Window");
        flight.seats.add(seat);

        Passenger passenger = new Passenger("P1", "John Doe", "john@example.com", "1234567890");

        // Booking
        BookingManager bookingManager = BookingManager.getInstance();
        bookingManager.createBooking(passenger, flight, seat, 5000);

        // Payment
        Payment payment = new Payment("PAY123", "Credit Card", 5000);
        PaymentProcessor paymentProcessor = PaymentProcessor.getInstance();
        paymentProcessor.processPayment(payment);

        // Flight search example
        FlightSearch flightSearch = new FlightSearch();
        List<Flight> searchResults = flightSearch.searchFlights(List.of(flight), "Delhi", "Mumbai", new Date());
        System.out.println("Flights found: " + searchResults.size());
    }
}

/*

UML Diagram

+---------------------------+
|        <<abstract>>      |
|           User           |
+---------------------------+
| - id: String              |
| - name: String            |
| - email: String           |
| - phoneNumber: String     |
+---------------------------+
| + getId(): String         |
| + getName(): String       |
| + getEmail(): String      |
| + getPhoneNumber(): String|
+---------------------------+
             ▲
   ┌─────────┼────────────┐
   ▼                      ▼
+-------------------+   +--------------------+
|     Passenger     |   |       Staff        |
+-------------------+   +--------------------+
| - bookings: List  |   | - role: String     |
+-------------------+   +--------------------+
| + getBookings()   |   | + getRole()        |
+-------------------+   +--------------------+

             ▼
     +------------------+
     |      Admin       |
     +------------------+
     | (inherits User)  |
     +------------------+

+-------------------------------+
|          Aircraft            |
+-------------------------------+
| - tailNumber: String          |
| - model: String               |
| - totalSeats: int             |
+-------------------------------+
| + getModel(): String          |
| + getTotalSeats(): int        |
+-------------------------------+

+-------------------------------------------+
|                 Flight                    |
+-------------------------------------------+
| - flightNumber: String                    |
| - source: String                          |
| - destination: String                     |
| - departureTime: Date                     |
| - arrivalTime: Date                       |
| - aircraft: Aircraft                      |
| - seats: List<Seat>                       |
+-------------------------------------------+
| + getFlightInfo(): String                 |
| + assignAircraft(aircraft: Aircraft): void|
+-------------------------------------------+

+-----------------------------+
|            Seat            |
+-----------------------------+
| - seatNumber: String        |
| - seatType: String          |
| - status: SeatStatus        |
+-----------------------------+
| + getSeatInfo(): String     |
+-----------------------------+

+-------------------------------------+
|              Booking                |
+-------------------------------------+
| - bookingNumber: String             |
| - flight: Flight                    |
| - passenger: Passenger              |
| - seat: Seat                        |
| - price: double                     |
| - status: BookingStatus             |
+-------------------------------------+
| + getBookingDetails(): String       |
+-------------------------------------+

+-------------------------------+
|           Payment            |
+-------------------------------+
| - paymentId: String           |
| - paymentMethod: String       |
| - amount: double              |
| - status: PaymentStatus       |
+-------------------------------+
| + process(): boolean          |
| + refund(): void              |
+-------------------------------+

+-----------------------------------------------+
|              FlightSearch                     |
+-----------------------------------------------+
| + searchFlights(source: String,               |
|   destination: String, date: Date): List<Flight>|
+-----------------------------------------------+

+-----------------------------------------------+
|         <<singleton>> BookingManager          |
+-----------------------------------------------+
| + getInstance(): BookingManager               |
| + createBooking(passenger, flight, seat):     |
|   Booking                                     |
| + cancelBooking(bookingNumber): boolean       |
+-----------------------------------------------+

+-----------------------------------------------+
|        <<singleton>> PaymentProcessor         |
+-----------------------------------------------+
| + getInstance(): PaymentProcessor             |
| + processPayment(payment: Payment): boolean   |
| + refundPayment(payment: Payment): void       |
+-----------------------------------------------+

+------------------------------------------------+
|          AirlineManagementSystem               |
+------------------------------------------------+
| + main(args: String[]): void                   |
| + initializeData(): void                       |
| + displayMenu(): void                          |
+------------------------------------------------+

==================================================

Database 

┌──────────────────────────────┐
│          users              │
├──────────────────────────────┤
│ + user_id: VARCHAR (PK)      │
│ + name: VARCHAR              │
│ + email: VARCHAR (UNIQUE)    │
│ + phone_number: VARCHAR      │
│ + user_type: ENUM            │
│   ['PASSENGER','STAFF','ADMIN'] │
└──────────────────────────────┘
             ▲
             │
             │
┌──────────────────────────────────────────────────────────────┐
│                          bookings                            │
├──────────────────────────────────────────────────────────────┤
│ + booking_id: VARCHAR (PK)                                   │
│ + passenger_id: VARCHAR (FK → users.user_id)                 │
│ + flight_number: VARCHAR (FK → flights.flight_number)        │
│ + seat_id: VARCHAR (FK → seats.seat_id)                      │
│ + price: DECIMAL                                              │
│ + booking_status: ENUM ['CONFIRMED', 'CANCELED']             │
└──────────────────────────────────────────────────────────────┘
             ▲
             │
             │
┌──────────────────────────────────────────────────────────────┐
│                          payments                            │
├──────────────────────────────────────────────────────────────┤
│ + payment_id: VARCHAR (PK)                                   │
│ + booking_id: VARCHAR (FK → bookings.booking_id)             │
│ + payment_method: VARCHAR ['CARD', 'UPI', 'NETBANKING']      │
│ + amount: DECIMAL                                            │
│ + payment_status: ENUM ['SUCCESS', 'FAILED', 'REFUNDED']     │
└──────────────────────────────────────────────────────────────┘

┌──────────────────────────────┐
│         aircrafts           │
├──────────────────────────────┤
│ + tail_number: VARCHAR (PK)  │
│ + model: VARCHAR             │
│ + total_seats: INT           │
└──────────────────────────────┘
             ▲
             │
             │
┌────────────────────────────────────────────┐
│                  flights                  │
├────────────────────────────────────────────┤
│ + flight_number: VARCHAR (PK)              │
│ + source: VARCHAR                          │
│ + destination: VARCHAR                     │
│ + departure_time: DATETIME                 │
│ + arrival_time: DATETIME                   │
│ + aircraft_id: VARCHAR (FK → aircrafts)    │
└────────────────────────────────────────────┘
             ▲
             │
             │
┌────────────────────────────────────────────────────┐
│                     seats                          │
├────────────────────────────────────────────────────┤
│ + seat_id: VARCHAR (PK)                             │
│ + flight_number: VARCHAR (FK → flights.flight_number)│
│ + seat_number: VARCHAR                              │
│ + seat_type: ENUM ['ECONOMY', 'BUSINESS', 'FIRST']  │
│ + seat_status: ENUM ['AVAILABLE', 'BOOKED']         │
└────────────────────────────────────────────────────┘

 */