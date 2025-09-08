package org.example.ticket.menus;

import org.example.ticket.entities.User;
import org.example.ticket.services.LoginService;
import org.example.ticket.utils.ColorUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Scanner;

public class UserMenu {

    private  final Scanner scanner = new Scanner(System.in);
    private final LoginService loginService;

    public UserMenu(LoginService loginService) {
        this.loginService = loginService;
    }

    /**
     * Displays the User Menu with current user details on top and returns the selected option.
     * Dynamically adds "Add Phone" and "Add Aadhaar" options if not already set.
     *
     * @param currentUser the current logged-in user
     * @return the selected option as int (0 for Back, -1 for invalid input)
     */
    public int showMenu(User currentUser) {
        int phoneOption = -1, aadhaarOption = -1;
        int lastOption = 3; // Base options: 1=Name, 2=Email, 3=Password

        // ---------------- USER DETAILS ----------------
        System.out.println(ColorUtils.CYAN + "\n====================================================" + ColorUtils.RESET);
        System.out.println(ColorUtils.BLUE + "             👤 Logged-in User Details            " + ColorUtils.RESET);
        System.out.println(ColorUtils.CYAN + "----------------------------------------------------" + ColorUtils.RESET);
        System.out.println(ColorUtils.YELLOW + "Name: " + ColorUtils.RESET + currentUser.getName());
        System.out.println(ColorUtils.YELLOW + "Email: " + ColorUtils.RESET + currentUser.getEmail());
        System.out.println(ColorUtils.YELLOW + "Phone: " + ColorUtils.RESET +
                (currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "Not set"));
        System.out.println(ColorUtils.YELLOW + "Aadhaar: " + ColorUtils.RESET +
                (currentUser.getAadhaarNumber() != null ? currentUser.getAadhaarNumber() : "Not set"));
        System.out.println(ColorUtils.CYAN + "====================================================" + ColorUtils.RESET);

        // ---------------- MENU OPTIONS ----------------
        System.out.println(ColorUtils.YELLOW + "   [1] ✏️  Change Name" + ColorUtils.RESET);
        System.out.println(ColorUtils.YELLOW + "   [2] ✏️  Change Email" + ColorUtils.RESET);
        System.out.println(ColorUtils.YELLOW + "   [3] 🔑 Change Password" + ColorUtils.RESET);

        // Add dynamic options
        if (currentUser.getPhoneNumber() == null) {
            phoneOption = ++lastOption;
            System.out.println(ColorUtils.YELLOW + "   [" + phoneOption + "] ➕ Add Phone Number" + ColorUtils.RESET);
        }
        if (currentUser.getAadhaarNumber() == null) {
            aadhaarOption = ++lastOption;
            System.out.println(ColorUtils.YELLOW + "   [" + aadhaarOption + "] ➕ Add Aadhaar Number" + ColorUtils.RESET);
        }

        System.out.println(ColorUtils.YELLOW + "   [0] 🔙 Back" + ColorUtils.RESET);
        System.out.println(ColorUtils.CYAN + "----------------------------------------------------" + ColorUtils.RESET);
        System.out.print("👉 " + ColorUtils.GREEN + "Enter your option: " + ColorUtils.RESET);

        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            return choice;
        } catch (NumberFormatException e) {
            System.out.println(ColorUtils.RED + "⚠️ Invalid input! Please enter a number." + ColorUtils.RESET);
            return -1;
        }
    }



    // --------------------- HANDLE ACTION ---------------------
    public void handleAction(int option, User currentUser) {
        int dynamicOption = 3;
        int phoneOption = -1, aadhaarOption = -1;

        // Map dynamic options
        if (currentUser.getPhoneNumber() == null) {
            phoneOption = ++dynamicOption;
        }
        if (currentUser.getAadhaarNumber() == null) {
            aadhaarOption = ++dynamicOption;
        }

        if (option == 1) changeName(currentUser);
        else if (option == 2) changeEmail(currentUser);
        else if (option == 3) changePassword(currentUser);
        else if (option == phoneOption) addPhoneNumber(currentUser);
        else if (option == aadhaarOption) addAadhaar(currentUser);
        else if (option == 0) return;
        else System.out.println(ColorUtils.RED + "⚠️ Invalid option! Try again." + ColorUtils.RESET);
    }

    // ===================== HANDLER FUNCTIONS =====================
    private  void changeName(User user) {
        System.out.print(ColorUtils.GREEN + "👉 Enter your new name: " + ColorUtils.RESET);
        String newName = scanner.nextLine().trim();
        if (newName.isEmpty()) {
            System.out.println(ColorUtils.RED + "⚠️ Name cannot be empty!" + ColorUtils.RESET);
            return;
        }
        user.setName(newName);
        loginService.updateUser(user);
        System.out.println(ColorUtils.CYAN + "✅ Name updated successfully! New Name: "
                + ColorUtils.YELLOW + user.getName() + ColorUtils.RESET);
    }

    private  void changeEmail(User user) {
        System.out.print(ColorUtils.GREEN + "👉 Enter your new email: " + ColorUtils.RESET);
        String newEmail = scanner.nextLine().trim();
        if (!newEmail.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            System.out.println(ColorUtils.RED + "⚠️ Invalid email format!" + ColorUtils.RESET);
            return;
        }
        user.setEmail(newEmail);
        loginService.updateUser(user);
        System.out.println(ColorUtils.CYAN + "✅ Email updated successfully! New Email: "
                + ColorUtils.YELLOW + user.getEmail() + ColorUtils.RESET);
    }

    private  void changePassword(User user) {
        System.out.print(ColorUtils.GREEN + "👉 Enter your new password (min 6 chars): " + ColorUtils.RESET);
        String newPassword = scanner.nextLine().trim();
        if (newPassword.length() < 6) {
            System.out.println(ColorUtils.RED + "⚠️ Password too short!" + ColorUtils.RESET);
            return;
        }
        System.out.print(ColorUtils.GREEN + "👉 Confirm your password: " + ColorUtils.RESET);
        String confirmPassword = scanner.nextLine().trim();
        if (!newPassword.equals(confirmPassword)) {
            System.out.println(ColorUtils.RED + "⚠️ Passwords do not match!" + ColorUtils.RESET);
            return;
        }
        String hashed = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        user.setPasswordHash(hashed);
        loginService.updateUser(user);
        System.out.println(ColorUtils.CYAN + "✅ Password updated successfully!" + ColorUtils.RESET);
    }

    private  void addPhoneNumber(User user) {
        System.out.print(ColorUtils.GREEN + "👉 Enter your phone number (10 digits): " + ColorUtils.RESET);
        String phone = scanner.nextLine().trim();
        if (!phone.matches("^[0-9]{10}$")) {
            System.out.println(ColorUtils.RED + "⚠️ Invalid phone number!" + ColorUtils.RESET);
            return;
        }
        user.setPhoneNumber(phone);
        loginService.updateUser(user);
        System.out.println(ColorUtils.CYAN + "✅ Phone number added: " + ColorUtils.YELLOW + user.getPhoneNumber() + ColorUtils.RESET);
    }

    private  void addAadhaar(User user) {
        System.out.print(ColorUtils.GREEN + "👉 Enter your Aadhaar number (12 digits): " + ColorUtils.RESET);
        String aadhaar = scanner.nextLine().trim();
        if (!aadhaar.matches("^[0-9]{12}$")) {
            System.out.println(ColorUtils.RED + "⚠️ Invalid Aadhaar number!" + ColorUtils.RESET);
            return;
        }
        user.setAadhaarUid(aadhaar);
        loginService.updateUser(user);
        System.out.println(ColorUtils.CYAN + "✅ Aadhaar number added: " + ColorUtils.YELLOW + user.getAadhaarNumber() + ColorUtils.RESET);
    }
}
