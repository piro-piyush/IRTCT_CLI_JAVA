# 🚆 IRCTC Java CLI Booking App

A full-featured **Command-Line Interface (CLI)** Java application simulating IRCTC train booking operations with persistent data storage and a beautiful terminal UI.

---

## ✨ Features

- **Login & Logout:** Secure authentication with hashed passwords (BCrypt).  
- **User Management:** Update profile details, view account info.  
- **Persistent Data:** Stores users, tickets, and trains in JSON files.  
- **Train Search:** List and search available trains.  
- **Book Tickets:** Book multiple passengers per train.  
- **Cancel Tickets:** Cancel with verification using a security number.  
- **Booking History:** Track all previous bookings.  
- **ASCII/Colored CLI Output:** Enhanced visual experience in terminal.  

---

## 🏗 Architecture

- **Entities:**  
  - `User` – user details and ticket list  
  - `Ticket` – train ticket with passengers and status  
  - `Passenger` – individual passenger details  
  - `Train` – train information and availability  

- **Services:**  
  - `LoginService` – handles authentication & registration  
  - `UserService` – manages user operations  
  - `TicketService` – manages ticket booking & cancellations  
  - `TrainService` – manages trains and seat availability  

- **Persistence:**  
  - JSON-based storage using Jackson for serialization/deserialization  
  - Handles `LocalDate` & complex objects  

- **Security:**  
  - Passwords stored with **BCrypt**  
  - Ticket cancellation secured by a **unique security number**  

- **CLI Design:**  
  - Colored & ASCII-styled tables  
  - Intuitive menu-driven interface  
  - Clear prompts and error messages  

---

## ⚙️ Installation & Running

1. Clone the repository:

```bash
git clone https://github.com/piro-piyush/IRTCT_CLI_JAVA.git>
cd IRTCT_CLI_JAVA
```