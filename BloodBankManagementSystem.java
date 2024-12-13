import java.util.*;

// ANSI escape codes for text colors
class TextColor {
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String CYAN = "\u001B[36m";
}

// Abstract class
abstract class BloodDonation {
    private String donorName;
    private String bloodType;
    private int donationAmount;

    public BloodDonation(String donorName, String bloodType, int donationAmount) {
        this.donorName = donorName;
        this.bloodType = bloodType;
        this.donationAmount = donationAmount;
    }

    public abstract boolean isEligibleToDonate();

    public String getDonorName() { return donorName; }
    public void setDonorName(String donorName) { this.donorName = donorName; }

    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }

    public int getDonationAmount() { return donationAmount; }
    public void setDonationAmount(int donationAmount) { this.donationAmount = donationAmount; }

    @Override
    public String toString() {
        return "Donor: " + donorName + "\n\t\t\t\t    Blood Type: " + bloodType + "\n\t\t\t\t    Amount: " + donationAmount + "ml";
    }
}
// =================================================== Regular Donation Class ===================================================
class RegularDonation extends BloodDonation {
    private int age;

    public RegularDonation(String donorName, String bloodType, int donationAmount, int age) {
        super(donorName, bloodType, donationAmount);
        this.age = age;
    }

    @Override
    public boolean isEligibleToDonate() {
        return age >= 18 && age <= 65 && getDonationAmount() <= 470;
    }

    public void displayEligibilityMessage() {
        System.out.println(isEligibleToDonate() 
            ? "\t\t\t\tDonor is eligible for donation" 
            : TextColor.RED + "\t\t\t\tDonor is not eligible for donation" + TextColor.RESET);
    }
}
// =================================================== Emergency Donation Class ===================================================
class EmergencyDonation extends BloodDonation {
    private boolean isUrgent;

    public EmergencyDonation(String donorName, String bloodType, int donationAmount, boolean isUrgent) {
        super(donorName, bloodType, donationAmount);
        this.isUrgent = isUrgent;
    }

    @Override
    public boolean isEligibleToDonate() {
        return isUrgent && getDonationAmount() <= 500;
    }
}

interface BloodBankOperations {
    void addDonation(BloodDonation donation);
    void listDonations();
    void findCompatibleDonations(String requiredBloodType);
}
// =================================================== Blood bank Class ===================================================
class BloodBank implements BloodBankOperations { // Interface 
    private ArrayList<BloodDonation> regularDonations;
    private ArrayList<BloodDonation> emergencyDonations;
    private Scanner scanner;

    public BloodBank() {
        regularDonations = new ArrayList<>();
        emergencyDonations = new ArrayList<>();
        scanner = new Scanner(System.in);
    }

    @Override
    public void addDonation(BloodDonation donation) {
        if (donation.isEligibleToDonate()) {
            if (donation instanceof RegularDonation) {
                regularDonations.add(donation);
            } else if (donation instanceof EmergencyDonation) {
                emergencyDonations.add(donation);
            }
            System.out.println(TextColor.GREEN + "\n\t\t\t\t--- Donation added successfully! ---" + TextColor.RESET);
        } else {
            System.out.println(TextColor.RED + "\n\t\t\t\t--- Donation failed ---");
            System.out.println("\n\t\t\t\tRequirements:");
            System.out.println("\t\t\t\t- Must Be 18+ Years old to donate");
            System.out.println("\t\t\t\t- Donation Amount Limit is 470 mL" + TextColor.RESET);
        }
    }

    @Override
    public void listDonations() {
        while (true) {
            System.out.print(TextColor.BLUE);
            System.out.println("\n\t\t\t\t=========================================");
            System.out.println("\t\t\t\t---        List Donations Menu        ---");
            System.out.println("\t\t\t\t=========================================");
            System.out.print(TextColor.CYAN);
            System.out.println("\t\t\t\t1. View Regular Donations");
            System.out.println("\t\t\t\t2. View Emergency Donations");
            System.out.println("\t\t\t\t3. Return to Donor Menu");
            System.out.print(TextColor.YELLOW);
            System.out.print("\t\t\t\tEnter your choice: ");
            // Error Handling
            int choice = -1;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println(TextColor.RED + "\n\t\t\t\t--- Invalid input. Please enter a valid number ---" + TextColor.RESET);
                scanner.nextLine();
                continue;
            }
            switch (choice) {
                case 1:
                    System.out.println("\n\t\t\t\tRegular Donations:\n");
                    if (regularDonations.isEmpty()) {
                        System.out.println(TextColor.RED + "\n\t\t\t\t--- No regular donations recorded ---" + TextColor.RESET);
                    } else {
                        for (BloodDonation donation : regularDonations) {
                            System.out.println("\t\t\t\t" + donation);
                        }
                    }
                    break;
                case 2:
                    System.out.println("\n\t\t\t\tEmergency Donations:\n");
                    if (emergencyDonations.isEmpty()) {
                        System.out.println(TextColor.RED + "\n\t\t\t\t--- No emergency donations recorded ---" + TextColor.RESET);
                    } else {
                        for (BloodDonation donation : emergencyDonations) {
                            System.out.println("\t\t\t\t" + donation);
                        }
                    }
                    break;
                case 3:
                    return;
                default:
                    System.out.println(TextColor.RED + "\n\t\t\t\t--- Invalid choice. Please try again ---" + TextColor.RESET);
            }
        }
    }

    @Override
    public void findCompatibleDonations(String requiredBloodType) {
        while (!isValidBloodType(requiredBloodType)) {
            System.out.println(TextColor.RED + "\n\t\t\t\t--- Invalid blood type. Blood type (A, B, AB, or O) ---" + TextColor.RESET);
            requiredBloodType = scanner.nextLine();
        }
        System.out.println("\n\t\t\t\tCompatible Donations for " + requiredBloodType + ":");
        boolean found = false;
        for (BloodDonation donation : regularDonations) {
            if (isBloodTypeCompatible(donation.getBloodType(), requiredBloodType)) {
                System.out.println("\n\t\t\t\t" + donation);
                found = true;
            }
        }
        for (BloodDonation donation : emergencyDonations) {
            if (isBloodTypeCompatible(donation.getBloodType(), requiredBloodType)) {
                System.out.println("\n\t\t\t\t" + donation);
                found = true;
            }
        }
        if (!found) {
            System.out.println(TextColor.RED + "\n\t\t\t\t--- No compatible donations found ---" + TextColor.RESET);
        }
    }

    private boolean isBloodTypeCompatible(String donorType, String recipientType) {
        // Simplified compatibility logic
        return donorType.equals(recipientType);
    }

    private boolean isValidBloodType(String bloodType) {
        return bloodType.equalsIgnoreCase("A") || bloodType.equalsIgnoreCase("B") || 
                bloodType.equalsIgnoreCase("AB") || bloodType.equalsIgnoreCase("O");
    }

    // Close the scanner when done (e.g., in a destructor or main program exit)
    public void closeScanner() {
        scanner.close();
    }
}
// =================================================== Main System ===================================================
public class BloodBankManagementSystem {
    private static HashMap<String, String> donorAccounts = new HashMap<>();
    // ======================================== Entry Point of program =======================================`
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BloodBank bloodBank = new BloodBank(); 

        while (true) {
            System.out.print(TextColor.BLUE);
            System.out.println("\n\t\t\t\t================================================");
            System.out.println("\t\t\t\t--     BBBB      AAA     N   N    K     K     --");
            System.out.println("\t\t\t\t--     B   B    A   A    NN  N    K    K      --");
            System.out.println("\t\t\t\t--     BBBB     AAAAA    N N N    K K K       --");
            System.out.println("\t\t\t\t--     B   B    A   A    N  NN    K    K      --");
            System.out.println("\t\t\t\t--     BBBB     A   A    N   N    K     K     --");
            System.out.println("\t\t\t\t================================================");
            System.out.print(TextColor.CYAN);
            System.out.println("\t\t\t\t\t----- Blood Bank Login Menu -----\n");
            System.out.println("\t\t\t\t\t1. Login");
            System.out.println("\t\t\t\t\t2. Create New Account");  
            System.out.println("\t\t\t\t\t3. Exit");
            System.out.print(TextColor.YELLOW + "\n\t\t\t\t\tEnter Your Choice: " + TextColor.RESET);
            // Error Handling for user input
            int choice = -1;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println(TextColor.RED + "\n\t\t\t\t--- Invalid input. Please enter a valid number ---" + TextColor.RESET);
                scanner.nextLine();
                continue;
            }
            switch (choice) {
                case 1:
                    clearScreen();
                    if (donorLogin(scanner)) {
                        handleDonorMenu(scanner, bloodBank);
                    } else {
                        System.out.println(TextColor.RED + "\n\t\t\t\t\tAccount Does Not Exist!" + TextColor.RESET);
                    }
                    break;
                case 2:
                    clearScreen();
                    createDonorAccount(scanner);
                    break;
                case 3:
                    System.out.println(TextColor.GREEN + "\n\t\t\t\t\tExiting the system...\n" + TextColor.RESET);
                    scanner.close();
                    System.exit(0);  // Exit the program
                    break;
                default:
                    System.out.println(TextColor.RED + "\n\t\t\t\t\t--- Invalid choice. Please try again ---" + TextColor.RESET);
            }
        }
    }
    // ========================================= CLear screen Method =========================================
    private static void clearScreen() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();
    }
    // ============================== Log In Account Method inside main class ==============================
    private static boolean donorLogin(Scanner scanner) {
        System.out.print(TextColor.BLUE);
        System.out.println("\n\t\t\t\t\t======================");
        System.out.println("\t\t\t\t\t---     Log In     ---");
        System.out.println("\t\t\t\t\t======================");
        System.out.print(TextColor.YELLOW);
        System.out.print("\n\t\t\t\t\tEnter Donor Username: ");
        String username = scanner.nextLine();
        System.out.print("\t\t\t\t\tEnter Donor Password: ");
        String password = scanner.nextLine();
        // Check if username and password is valid
        return donorAccounts.containsKey(username) && donorAccounts.get(username).equals(password);
    }
    // ============================== Create New Account Method inside main class ==============================
    private static void createDonorAccount(Scanner scanner) {
        System.out.print(TextColor.BLUE);
        System.out.println("\n\t\t\t\t\t==================================");
        System.out.println("\t\t\t\t\t---     Create New Account     ---");
        System.out.println("\t\t\t\t\t==================================");
        System.out.print(TextColor.YELLOW);
        System.out.print("\n\t\t\t\t\tEnter New Username: ");
        String username = scanner.nextLine();
        if (donorAccounts.containsKey(username)) {
            System.out.println(TextColor.RED + "\t\t\t\t--- Username already exists. Please try a different one ---" + TextColor.RESET);
            return;
        }
        System.out.print("\t\t\t\t\tEnter New Password: ");
        String password = scanner.nextLine();

        donorAccounts.put(username, password);
        System.out.println(TextColor.GREEN + "\n\t\t\t\t\t--- Account created successfully! ---" + TextColor.RESET);
    }
    // ============================== Donor Menu Mehod inside main class ============================== 
    private static void handleDonorMenu(Scanner scanner, BloodBank bloodBank) {
        clearScreen();
        while (true) {
            System.out.print(TextColor.BLUE);
            System.out.println("\n\t\t\t\t==================================================");
            System.out.println("\t\t\t\t--     M     M    EEEEE    N     N    U   U     --");
            System.out.println("\t\t\t\t--     M M M M    E        N N   N    U   U     --");
            System.out.println("\t\t\t\t--     M  M  M    EEEE     N  N  N    U   U     --");
            System.out.println("\t\t\t\t--     M     M    E        N   N N    U   U     --");
            System.out.println("\t\t\t\t--     M     M    EEEEE    N     N    UUUUU     --");
            System.out.println("\t\t\t\t==================================================");
            System.out.print(TextColor.CYAN);
            System.out.println("\t\t\t\t\t-------- Donor Menu --------\n");
            System.out.println("\t\t\t\t\t1. Add Regular Donation");
            System.out.println("\t\t\t\t\t2. Add Emergency Donation");
            System.out.println("\t\t\t\t\t3. List Donations");
            System.out.println("\t\t\t\t\t4. Find Compatible Donations");
            System.out.println("\t\t\t\t\t5. Log out");
            System.out.print(TextColor.YELLOW + "\n\t\t\t\t\tEnter your choice: " + TextColor.RESET);
            // Error Handling
            int choice = -1;
            try {
                choice = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.out.println(TextColor.RED + "\n\t\t\t\t\t--- Invalid input. Please enter a valid number ---" + TextColor.RESET);
                scanner.nextLine();
                continue;
            }
            switch (choice) {
                case 1:
                    clearScreen();
                    System.out.print(TextColor.BLUE);
                    System.out.println("\n\t\t\t\t===========================================");
                    System.out.println("\t\t\t\t---      Add Regular Donation Menu      ---");
                    System.out.println("\t\t\t\t===========================================");
                    addRegularDonation(scanner, bloodBank);
                    break;
                    case 2:
                    clearScreen();
                    System.out.print(TextColor.BLUE);
                    System.out.println("\n\t\t\t\t=============================================");
                    System.out.println("\t\t\t\t---      Add Emergency Donation Menu      ---");
                    System.out.println("\t\t\t\t=============================================");
                    addEmergencyDonation(scanner, bloodBank);
                    break;
                case 3:
                    clearScreen();
                    bloodBank.listDonations();
                    break;
                case 4:
                    clearScreen();
                    System.out.print(TextColor.BLUE);
                    System.out.println("\n\t\t\t\t===========================================");
                    System.out.println("\t\t\t\t---      Find Compatible Donations      ---");
                    System.out.println("\t\t\t\t===========================================");
                    findCompatibleDonations(scanner, bloodBank);
                    break;
                case 5:
                    clearScreen();
                    return;
                default:
                    System.out.println(TextColor.RED + "\t\t\t\t\t--- Invalid choice. Please try again ---" + TextColor.RESET);
            }
        }
    }
    // ============================================ Add Regulator Donation Method =========================================== 
    private static void addRegularDonation(Scanner scanner, BloodBank bloodBank) {
        String bloodType;
        while (true) {
            System.out.print(TextColor.CYAN + "\n\t\t\t\tBlood Type: ");
            bloodType = scanner.nextLine().toUpperCase();
            if (bloodType.matches("A|B|AB|O")) {
                break;
            } else {
                System.out.println(TextColor.RED + "\t\t\t\t--- Invalid Blood Type (Blood Type: A, B, AB, O) ---" + TextColor.RESET);
            }
        }
        // Enter Donor Name
        System.out.print("\t\t\t\tDonor Name: ");
        String name = scanner.nextLine();
        // Donation Amount with error handling
        System.out.print("\t\t\t\tDonation Amount (ml): ");
        int amount;
        while (true) {
            try {
                amount = Integer.parseInt(scanner.nextLine());
                if (amount > 0) {
                    break;
                } else {
                    System.out.println(TextColor.RED + "\n\t\t\t\t--- Amount must be positive ---\n" + TextColor.RESET);
                    System.out.print(TextColor.CYAN + "\t\t\t\tDonation Amount (ml): ");
                }
            } catch (NumberFormatException e) {
                System.out.println(TextColor.RED + "\n\t\t\t\t--- Invalid input. Please enter a numeric value ---\n" + TextColor.RESET);
                System.out.print(TextColor.CYAN + "\t\t\t\tDonation Amount (ml): ");
            }
        }
        // Age with error handling
        System.out.print("\t\t\t\tAge: ");
        int age;
        while (true) {
            try {
                age = Integer.parseInt(scanner.nextLine());
                if (age >= 0) {
                    break;
                } else {
                    System.out.println(TextColor.RED + "\t\t\t\t--- Age must be positive ---" + TextColor.RESET);
                    System.out.print(TextColor.CYAN + "\t\t\t\tAge: ");
                }
            } catch (NumberFormatException e) {
                System.out.println(TextColor.RED + "\t\t\t\t--- Invalid input. Please enter a numeric value ---\n" + TextColor.RESET);
                    System.out.print(TextColor.CYAN + "\t\t\t\tAge: ");
            }
        }
        // Add inputted data
        RegularDonation donation = new RegularDonation(name, bloodType, amount, age);
        bloodBank.addDonation(donation);
    }
    // ============================================ Add Emergency Donation Method =========================================== 
    private static void addEmergencyDonation(Scanner scanner, BloodBank bloodBank) {
        String bloodType;
        while (true) {
            System.out.print(TextColor.CYAN + "\n\t\t\t\tBlood Type: ");
            bloodType = scanner.nextLine().toUpperCase();
            if (bloodType.matches("A|B|AB|O")) {
                break;
            } else {
                System.out.println(TextColor.RED + "\n\t\t\t\t--- Invalid Blood Type (Blood Type: A, B, AB, O) ---" + TextColor.RESET);
            }
        }
        // Donont Name
        System.out.print("\t\t\t\tDonor Name: ");
        String name = scanner.nextLine();
        // Donation Amount with error handling
        System.out.print("\t\t\t\tDonation Amount (ml): ");
        int amount;
        while (true) {
            try {
                amount = Integer.parseInt(scanner.nextLine());
                if (amount > 0) {
                    break;
                } else {
                    System.out.println(TextColor.RED + "\n\t\t\t\t--- Amount must be positive ---\n" + TextColor.RESET);
                    System.out.print(TextColor.CYAN + "\t\t\t\tDonation Amount (ml): ");
                }
            } catch (NumberFormatException e) {
                System.out.println(TextColor.RED + "\n\t\t\t\t--- Invalid input. Please enter a numeric value ---\n" + TextColor.RESET);
                System.out.print(TextColor.CYAN + "\t\t\t\tDonation Amount (ml): ");
            }
        }
        // Urgent with error handling
        System.out.print("\t\t\t\tIs Urgent? (true/false): ");
        boolean isUrgent;
        while (true) {
            String input = scanner.nextLine().toLowerCase();
            if (input.equals("true") || input.equals("false")) {
                isUrgent = Boolean.parseBoolean(input);
                break;
            } else {
                System.out.println(TextColor.RED + "\n\t\t\t\t--- Invalid input. Please enter true or false ---\n" + TextColor.RESET);
                System.out.print(TextColor.CYAN + "\t\t\t\tIs Urgent? (true/false): ");
            }
        }
        // Add Emergency donation
        EmergencyDonation donation = new EmergencyDonation(name, bloodType, amount, isUrgent);
        bloodBank.addDonation(donation);
    }
    // ============================================ Find Compatible Method =========================================== 
    private static void findCompatibleDonations(Scanner scanner, BloodBank bloodBank) {
        String bloodType;
        while (true) {
            System.out.print(TextColor.CYAN + "\n\t\t\t\tBlood Type: ");
            bloodType = scanner.nextLine().toUpperCase();
            if (bloodType.matches("A|B|AB|O")) {
                break;
            } else {
                System.out.println(TextColor.RED + "\n\t\t\t\t--- Invalid Blood Type (Blood Type: A, B, AB, O) ---" + TextColor.RESET);
            }
        }
        bloodBank.findCompatibleDonations(bloodType);
    }
}
