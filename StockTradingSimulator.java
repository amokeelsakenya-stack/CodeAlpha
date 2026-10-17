
import java.io.*;
import java.util.*;

// Class representing a Stock in the market
class Stock implements Serializable {
    private String symbol;
    private String name;
    private double price;

    public Stock(String symbol, String name, double price) {
        this.symbol = symbol;
        this.name = name;
        this.price = price;
    }

    public String getSymbol() { return symbol; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    @Override
    public String toString() {
        return String.format("%-6s | %-20s | $%-10.2f", symbol, name, price);
    }
}

// Class representing a trade transaction
class Transaction implements Serializable {
    private String symbol;
    private String type; // "BUY" or "SELL"
    private int quantity;
    private double price;
    private String timestamp;

    public Transaction(String symbol, String type, int quantity, double price) {
        this.symbol = symbol;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = new Date().toString();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %d shares of %s @ $%.2f (Total: $%.2f)", 
                timestamp, type, quantity, symbol, price, (quantity * price));
    }
}

// Class representing a User's Portfolio and financial tracking
class Portfolio implements Serializable {
    private double cashBalance;
    private Map<String, Integer> holdings; // Symbol -> Quantity
    private List<Transaction> transactionHistory;

    public Portfolio(double initialCash) {
        this.cashBalance = initialCash;
        this.holdings = new HashMap<>();
        this.transactionHistory = new ArrayList<>();
    }

    public double getCashBalance() { return cashBalance; }
    public Map<String, Integer> getHoldings() { return holdings; }
    public List<Transaction> getTransactionHistory() { return transactionHistory; }

    public void deposit(double amount) {
        if (amount > 0) cashBalance += amount;
    }

    public boolean buyStock(Stock stock, int quantity) {
        double totalCost = stock.getPrice() * quantity;
        if (totalCost > cashBalance) {
            System.out.println("❌ Error: Insufficient funds!");
            return false;
        }

        cashBalance -= totalCost;
        holdings.put(stock.getSymbol(), holdings.getOrDefault(stock.getSymbol(), 0) + quantity);
        transactionHistory.add(new Transaction(stock.getSymbol(), "BUY", quantity, stock.getPrice()));
        return true;
    }

    public boolean sellStock(Stock stock, int quantity) {
        int currentOwned = holdings.getOrDefault(stock.getSymbol(), 0);
        if (quantity > currentOwned) {
            System.out.println("❌ Error: You do not own enough shares to sell!");
            return false;
        }

        double totalRevenue = stock.getPrice() * quantity;
        cashBalance += totalRevenue;
        
        if (currentOwned == quantity) {
            holdings.remove(stock.getSymbol());
        } else {
            holdings.put(stock.getSymbol(), currentOwned - quantity);
        }

        transactionHistory.add(new Transaction(stock.getSymbol(), "SELL", quantity, stock.getPrice()));
        return true;
    }

    public double calculateTotalPortfolioValue(Map<String, Stock> market) {
        double totalValue = cashBalance;
        for (Map.Entry<String, Integer> entry : holdings.entrySet()) {
            Stock stock = market.get(entry.getKey());
            if (stock != null) {
                totalValue += stock.getPrice() * entry.getValue();
            }
        }
        return totalValue;
    }
}

// Main Stock Trading Simulator Class
public class StockTradingSimulator {
    private static Map<String, Stock> market = new HashMap<>();
    private static Portfolio portfolio = new Portfolio(10000.00); // Start with $10,000 virtual cash
    private static final String DATA_FILE = "portfolio_data.ser";

    public static void initializeMarket() {
        market.put("AAPL", new Stock("AAPL", "Apple Inc.", 185.50));
        market.put("TSLA", new Stock("TSLA", "Tesla Inc.", 210.25));
        market.put("GOOGL", new Stock("Alphabet Inc.", "Google", 142.80));
        market.put("MSFT", new Stock("MSFT", "Microsoft Corp.", 415.10));
        market.put("AMZN", new Stock("AMZN", "Amazon.com Inc.", 178.20));
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        initializeMarket();
        loadPortfolio(); // Try loading saved data if available

        int choice;
        do {
            System.out.println("\n========================================");
            System.out.println("      VIRTUAL STOCK TRADING SIMULATOR   ");
            System.out.println("========================================");
            System.out.println("1. View Market Data");
            System.out.println("2. View Portfolio & Performance");
            System.out.println("3. Buy Stock");
            System.out.println("4. Sell Stock");
            System.out.println("5. View Transaction History");
            System.out.println("6. Simulate Market Price Change");
            System.out.println("7. Save & Exit");
            System.out.print("Enter your choice (1-7): ");

            while (!scanner.hasNextInt()) {
                System.out.print("Invalid choice. Enter a number (1-7): ");
                scanner.next();
            }
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    displayMarket();
                    break;
                case 2:
                    displayPortfolio();
                    break;
                case 3:
                    executeBuy(scanner);
                    break;
                case 4:
                    executeSell(scanner);
                    break;
                case 5:
                    displayHistory();
                    break;
                case 6:
                    simulateMarketFluctuation();
                    break;
                case 7:
                    savePortfolio();
                    System.out.println("\nPortfolio saved successfully. Goodbye!");
                    break;
                default:
                    System.out.println("\nInvalid choice. Please pick between 1 and 7.");
            }
        } while (choice != 7);

        scanner.close();
    }

    private static void displayMarket() {
        System.out.println("\n----------------------------------------");
        System.out.println("              MARKET DATA               ");
        System.out.println("----------------------------------------");
        System.out.printf("%-6s | %-20s | %-10s\n", "Symbol", "Company", "Price");
        System.out.println("----------------------------------------");
        for (Stock stock : market.values()) {
            System.out.println(stock);
        }
        System.out.println("----------------------------------------");
    }

    private static void displayPortfolio() {
        System.out.println("\n----------------------------------------");
        System.out.println("           PORTFOLIO SUMMARY            ");
        System.out.println("----------------------------------------");
        System.out.printf("Cash Balance         : $%.2f\n", portfolio.getCashBalance());
        
        double stockValue = portfolio.calculateTotalPortfolioValue(market) - portfolio.getCashBalance();
        System.out.printf("Total Stock Value    : $%.2f\n", stockValue);
        System.out.printf("Total Net Worth      : $%.2f\n", portfolio.calculateTotalPortfolioValue(market));
        
        System.out.println("\nCurrent Holdings:");
        if (portfolio.getHoldings().isEmpty()) {
            System.out.println(" (None)");
        } else {
            System.out.printf("%-6s | %-10s | %-12s\n", "Symbol", "Shares", "Current Value");
            System.out.println("----------------------------------------");
            for (Map.Entry<String, Integer> entry : portfolio.getHoldings().entrySet()) {
                String symbol = entry.getKey();
                int qty = entry.getValue();
                double currentVal = market.get(symbol).getPrice() * qty;
                System.out.printf("%-6s | %-10d | $%-11.2f\n", symbol, qty, currentVal);
            }
        }
        System.out.println("----------------------------------------");
    }

    private static void executeBuy(Scanner scanner) {
        displayMarket();
        System.out.print("Enter stock symbol to buy: ");
        String symbol = scanner.nextLine().toUpperCase();

        Stock stock = market.get(symbol);
        if (stock == null) {
            System.out.println("❌ Error: Invalid stock symbol.");
            return;
        }

        System.out.print("Enter number of shares to buy: ");
        if (!scanner.hasNextInt()) {
            System.out.println("❌ Invalid quantity.");
            scanner.next();
            return;
        }
        int qty = scanner.nextInt();
        scanner.nextLine();

        if (qty <= 0) {
            System.out.println("❌ Quantity must be greater than zero.");
            return;
        }

        if (portfolio.buyStock(stock, qty)) {
            System.out.println("✔ Successfully purchased " + qty + " shares of " + symbol + "!");
        }
    }

    private static void executeSell(Scanner scanner) {
        if (portfolio.getHoldings().isEmpty()) {
            System.out.println("\nYou don't own any stocks to sell.");
            return;
        }

        displayPortfolio();
        System.out.print("Enter stock symbol to sell: ");
        String symbol = scanner.nextLine().toUpperCase();

        if (!portfolio.getHoldings().containsKey(symbol)) {
            System.out.println("❌ Error: You do not own this stock.");
            return;
        }

        System.out.print("Enter number of shares to sell: ");
        if (!scanner.hasNextInt()) {
            System.out.println("❌ Invalid quantity.");
            scanner.next();
            return;
        }
        int qty = scanner.nextInt();
        scanner.nextLine();

        Stock stock = market.get(symbol);
        if (portfolio.sellStock(stock, qty)) {
            System.out.println("✔ Successfully sold " + qty + " shares of " + symbol + "!");
        }
    }

    private static void displayHistory() {
        System.out.println("\n----------------------------------------");
        System.out.println("          TRANSACTION HISTORY           ");
        System.out.println("----------------------------------------");
        List<Transaction> history = portfolio.getTransactionHistory();
        if (history.isEmpty()) {
            System.out.println("No transactions made yet.");
        } else {
            for (Transaction tx : history) {
                System.out.println(tx);
            }
        }
        System.out.println("----------------------------------------");
    }

    private static void simulateMarketFluctuation() {
        Random rand = new Random();
        System.out.println("\n🔄 Simulating market price changes...");
        for (Stock stock : market.values()) {
            // Random fluctuation between -5% and +5%
            double percentChange = (rand.nextDouble() * 10) - 5; 
            double newPrice = stock.getPrice() * (1 + percentChange / 100.0);
            stock.setPrice(Math.round(newPrice * 100.0) / 100.0);
        }
        System.out.println("✔ Market prices updated successfully! Check Market Data to view updates.");
    }

    private static void savePortfolio() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(portfolio);
        } catch (IOException e) {
            System.out.println("⚠️ Could not save portfolio data to file.");
        }
    }

    private static void loadPortfolio() {
        File file = new File(DATA_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                portfolio = (Portfolio) ois.readObject();
                System.out.println("✔ Previous portfolio data loaded successfully.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("⚠️ Could not load previous portfolio data. Starting fresh.");
            }
        }
    }
}

