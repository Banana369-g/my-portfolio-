import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Teamwork02 {

	public static void main(String[] args) {

		Scanner scan = new Scanner(System.in);
		AccountDatabase db = new AccountDatabase();

		while (true) {
			System.out.println("💵💵💵💵💵💵💵💵💵💵💵");
			System.out.print("Enter your name: ");
			String name = scan.next();
			System.out.println("Welcome, " + name);

			// Look up an existing account, or create a new one if it doesn't exist
			mainAccount account = db.findOrCreate(name);

			boolean loggedIn = true;
			while (loggedIn) {
				System.out.println("------------------------");
				System.out.println("1: Bank Account (check balance)");
				System.out.println("2: Income track (record income)");
				System.out.println("3: Expenses track (record expense)");
				System.out.println("4: Transaction history");
				System.out.println("5: Customer service");
				System.out.println("6: Logout");
				System.out.print("Choose an option (1-6): ");

				// If nextLine() isn't called after nextInt(), the leftover newline
				// character will mess up the next next()/nextLine() call
				int choice;
				if (scan.hasNextInt()) {
					choice = scan.nextInt();
				} else {
					System.out.println("Please enter a number.");
					scan.next(); // discard the invalid input
					continue;
				}
				scan.nextLine(); // discard the leftover newline

				switch (choice) {
				case 1:
					account.printBalance();
					break;

				case 2:
					System.out.print("Income description: ");
					String incomeDesc = scan.nextLine();
					System.out.print("Amount: ");
					double incomeAmt = readDouble(scan);
					account.addIncome(incomeDesc, incomeAmt);
					db.saveToFile();
					System.out.println("Income recorded. Current balance: " + account.getBalance());
					break;

				case 3:
					System.out.print("Expense description: ");
					String expenseDesc = scan.nextLine();
					System.out.print("Amount: ");
					double expenseAmt = readDouble(scan);
					boolean ok = account.addExpense(expenseDesc, expenseAmt);
					if (ok) {
						db.saveToFile();
						System.out.println("Expense recorded. Current balance: " + account.getBalance());
					} else {
						System.out.println("Insufficient balance. Expense was not recorded.");
					}
					break;

				case 4:
					account.printHistory();
					break;

				case 5:
					customerServiceMenu(scan, name);
					break;

				case 6:
					loggedIn = false;
					System.out.println(name + ", you have been logged out.");
					break;

				default:
					System.out.println("Please enter a number from 1 to 6.");
				}
			}
		}
	}

	// Helper that keeps reading input until a valid number is entered,
	// so Scanner doesn't crash on bad input
	private static double readDouble(Scanner scan) {
		while (true) {
			String line = scan.nextLine();
			try {
				return Double.parseDouble(line);
			} catch (NumberFormatException e) {
				System.out.print("Please enter a valid number: ");
			}
		}
	}

	// A "Contact Support" screen similar to what other banking apps have
	private static void customerServiceMenu(Scanner scan, String customerName) {
		CustomerService cs = new CustomerService();

		boolean inMenu = true;
		while (inMenu) {
			System.out.println("--- Customer Service ---");
			System.out.println("1: Report an issue");
			System.out.println("2: View my ticket history");
			System.out.println("3: Back");
			System.out.print("Choose an option (1-3): ");

			int choice;
			if (scan.hasNextInt()) {
				choice = scan.nextInt();
			} else {
				System.out.println("Please enter a number.");
				scan.next();
				continue;
			}
			scan.nextLine();

			switch (choice) {
			case 1:
				cs.reportIssue(scan, customerName);
				break;
			case 2:
				cs.viewMyTickets(customerName);
				break;
			case 3:
				inMenu = false;
				break;
			default:
				System.out.println("Please enter a number from 1 to 3.");
			}
		}
	}
}

// Represents a single transaction (either income or an expense)
class Transaction {
	private String description;
	private double amount;
	private LocalDate date;

	public Transaction(String description, double amount) {
		this.description = description;
		this.amount = amount;
		this.date = LocalDate.now();
	}

	public String getDescription() {
		return description;
	}

	public double getAmount() {
		return amount;
	}

	public LocalDate getDate() {
		return date;
	}

	@Override
	public String toString() {
		return "[" + date + "] " + description + ": " + amount;
	}
}

// Represents a single user's bank account
class mainAccount {
	private String name;
	private double balance;
	private ArrayList<Transaction> incomeList;
	private ArrayList<Transaction> expenseList;

	public mainAccount(String name) {
		this(name, 0.0);
	}

	// Used to restore a saved balance when loading from account.csv
	public mainAccount(String name, double balance) {
		this.name = name;
		this.balance = balance;
		this.incomeList = new ArrayList<>();
		this.expenseList = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public double getBalance() {
		return balance;
	}

	public void addIncome(String description, double amount) {
		Transaction t = new Transaction(description, amount);
		incomeList.add(t);
		balance += amount;
	}

	// Returns false and does not record the transaction if balance is insufficient
	public boolean addExpense(String description, double amount) {
		if (amount > balance) {
			return false;
		}
		Transaction t = new Transaction(description, amount);
		expenseList.add(t);
		balance -= amount;
		return true;
	}

	public void printBalance() {
		System.out.println(name + "'s current balance: " + balance);
	}

	public void printHistory() {
		System.out.println("=== " + name + "'s Income History ===");
		if (incomeList.isEmpty()) {
			System.out.println("(No records yet)");
		}
		for (Transaction t : incomeList) {
			System.out.println(t);
		}

		System.out.println("=== " + name + "'s Expense History ===");
		if (expenseList.isEmpty()) {
			System.out.println("(No records yet)");
		}
		for (Transaction t : expenseList) {
			System.out.println(t);
		}

		System.out.println("Current balance: " + balance);
	}
}

// Manages all user accounts by name, and saves/restores them via account.csv
class AccountDatabase {
	private static final String FILE_PATH = "account.csv";
	private HashMap<String, mainAccount> accounts;

	public AccountDatabase() {
		accounts = new HashMap<>();
		loadFromFile();
	}

	// Returns the existing account if found, otherwise creates a new one
	public mainAccount findOrCreate(String name) {
		if (accounts.containsKey(name)) {
			return accounts.get(name);
		}
		mainAccount newAccount = new mainAccount(name);
		accounts.put(name, newAccount);
		saveToFile(); // immediately reflect the new account in the CSV
		return newAccount;
	}

	public mainAccount getAccount(String name) {
		return accounts.get(name);
	}

	public int getAccountCount() {
		return accounts.size();
	}

	// Reads account.csv (name,balance) and restores accounts.
	// If the file doesn't exist yet (e.g. first run), just continue with an empty state.
	private void loadFromFile() {
		File file = new File(FILE_PATH);
		if (!file.exists()) {
			return;
		}

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			boolean firstLine = true;
			while ((line = reader.readLine()) != null) {
				if (firstLine) {
					firstLine = false;
					if (line.startsWith("name,balance")) {
						continue; // skip the header row
					}
				}
				if (line.isBlank()) {
					continue;
				}
				String[] parts = line.split(",");
				if (parts.length < 2) {
					continue;
				}
				String name = parts[0].trim();
				try {
					double balance = Double.parseDouble(parts[1].trim());
					accounts.put(name, new mainAccount(name, balance));
				} catch (NumberFormatException e) {
					System.out.println("Skipped a line in account.csv that couldn't be read: " + line);
				}
			}
		} catch (IOException e) {
			System.out.println("Error while reading account.csv: " + e.getMessage());
		}
	}

	// Writes all current accounts (name, balance) out to account.csv
	public void saveToFile() {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
			writer.write("name,balance");
			writer.newLine();
			for (mainAccount account : accounts.values()) {
				writer.write(account.getName() + "," + account.getBalance());
				writer.newLine();
			}
		} catch (IOException e) {
			System.out.println("Error while saving account.csv: " + e.getMessage());
		}
	}
}

// Represents a single customer service ticket
class SupportTicket {
	private String ticketId;
	private String customerName;
	private String category;
	private String message;
	private LocalDateTime createdAt;

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public SupportTicket(String ticketId, String customerName, String category, String message) {
		this.ticketId = ticketId;
		this.customerName = customerName;
		this.category = category;
		this.message = message;
		this.createdAt = LocalDateTime.now();
	}

	public String getTicketId() {
		return ticketId;
	}

	public String getCustomerName() {
		return customerName;
	}

	// Formats this ticket as an email-style block for writing to support_tickets.txt
	public String toEmailFormat() {
		StringBuilder sb = new StringBuilder();
		sb.append("====================================\n");
		sb.append("Ticket ID: ").append(ticketId).append("\n");
		sb.append("Date: ").append(createdAt.format(FORMATTER)).append("\n");
		sb.append("From: ").append(customerName).append("\n");
		sb.append("To: support@yourbank.co.jp\n");
		sb.append("Subject: [Support Request] ").append(category).append("\n");
		sb.append("Status: Open\n");
		sb.append("------------------------------------\n");
		sb.append(message).append("\n");
		sb.append("====================================\n");
		return sb.toString();
	}
}

// Manages a "Contact Support" feature similar to other banking apps.
// Instead of actually sending an email, it logs an email-style entry to support_tickets.txt
class CustomerService {
	private static final String FILE_PATH = "support_tickets.txt";

	// Reports an issue: collects a category and details, then issues and saves a ticket
	public void reportIssue(Scanner scan, String customerName) {
		System.out.println("--- Select the type of issue ---");
		System.out.println("1: Account issue");
		System.out.println("2: Transaction error");
		System.out.println("3: Security concern");
		System.out.println("4: Other");
		System.out.print("Choose an option (1-4): ");

		int categoryChoice;
		if (scan.hasNextInt()) {
			categoryChoice = scan.nextInt();
		} else {
			categoryChoice = 4;
			scan.next();
		}
		scan.nextLine();

		String category;
		switch (categoryChoice) {
		case 1:
			category = "Account issue";
			break;
		case 2:
			category = "Transaction error";
			break;
		case 3:
			category = "Security concern";
			break;
		default:
			category = "Other";
		}

		System.out.print("Please describe your issue: ");
		String message = scan.nextLine();

		String ticketId = generateNextTicketId();
		SupportTicket ticket = new SupportTicket(ticketId, customerName, category, message);
		saveTicket(ticket);

		System.out.println("Thank you for contacting us.");
		System.out.println("Your ticket " + ticketId + " has been sent to our support team.");
		System.out.println("A representative will typically respond within 24 hours.");
	}

	// Lists this customer's past support tickets
	public void viewMyTickets(String customerName) {
		File file = new File(FILE_PATH);
		if (!file.exists()) {
			System.out.println("You have no support ticket history yet.");
			return;
		}

		String delimiter = "====================================";
		StringBuilder fullText = new StringBuilder();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				fullText.append(line).append("\n");
			}
		} catch (IOException e) {
			System.out.println("Error while reading support_tickets.txt: " + e.getMessage());
			return;
		}

		// Splitting on the delimiter leaves only the content of each ticket
		String[] chunks = fullText.toString().split(delimiter);
		boolean found = false;

		for (String chunk : chunks) {
			if (chunk.isBlank()) {
				continue;
			}
			if (chunk.contains("From: " + customerName + "\n")) {
				System.out.println(delimiter);
				System.out.print(chunk.strip());
				System.out.println();
				System.out.println(delimiter);
				found = true;
			}
		}

		if (!found) {
			System.out.println(customerName + " has no support ticket history yet.");
		}
	}

	// Determines the next ticket number based on how many tickets already exist in the file
	private String generateNextTicketId() {
		int count = 0;
		File file = new File(FILE_PATH);
		if (file.exists()) {
			try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
				String line;
				while ((line = reader.readLine()) != null) {
					if (line.startsWith("Ticket ID:")) {
						count++;
					}
				}
			} catch (IOException e) {
				System.out.println("Error while reading ticket count: " + e.getMessage());
			}
		}
		return String.format("TCK-%04d", count + 1);
	}

	// Appends the ticket to the file in email format
	private void saveTicket(SupportTicket ticket) {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
			writer.write(ticket.toEmailFormat());
		} catch (IOException e) {
			System.out.println("Error while saving to support_tickets.txt: " + e.getMessage());
		}
	}
}