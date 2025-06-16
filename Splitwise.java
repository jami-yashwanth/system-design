import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class SplitwiseUser {
    private final String id;
    private final String name;
    private final String email;
    private final Map<String, Double> balanceMap = new ConcurrentHashMap<>(); // userId -> balance

    public SplitwiseUser(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Map<String, Double> getBalanceMap() {
        return balanceMap;
    }

    public void updateBalance(String otherUserId, double amount) {
        balanceMap.put(otherUserId, balanceMap.getOrDefault(otherUserId, 0.0) + amount);
    }

    public void addBalance(String otherUserId, double amount) {
        if (amount == 0)
            return;
        balanceMap.put(otherUserId, balanceMap.getOrDefault(otherUserId, 0.0) + amount);
    }
    
    public double getBalance(String otherUserId) {
        return balanceMap.getOrDefault(otherUserId, 0.0);
    }

    public Map<String, Double> getBalances() {
        return balanceMap;
    }
}

abstract class Split {
    private final SplitwiseUser user;
    private double amount;

    public Split(SplitwiseUser user) {
        this.user = user;
    }

    public SplitwiseUser getUser() { return user; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}

class EqualSplit extends Split {
    public EqualSplit(SplitwiseUser user) { super(user); }
}

class PercentSplit extends Split {
    private final double percent;

    public PercentSplit(SplitwiseUser user, double percent) {
        super(user);
        this.percent = percent;
    }

    public double getPercent() { return percent; }
}

class ExactSplit extends Split {
    public ExactSplit(SplitwiseUser user, double amount) {
        super(user);
        setAmount(amount);
    }
}

class Expense {
    private final String id;
    private final double amount;
    private final String description;
    private final SplitwiseUser paidBy;
    private final List<Split> splits;

    public Expense(String id, double amount, String description, SplitwiseUser paidBy, List<Split> splits) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.paidBy = paidBy;
        this.splits = splits;
    }

    public List<Split> getSplits() {
        return splits;
    }

    public SplitwiseUser getPaidBy() {
        return paidBy;
    }

    public double getAmount() {
        return amount;
    }
}

class Group {
    private final String id;
    private final String name;
    private final List<SplitwiseUser> members = new ArrayList<>();
    private final List<Expense> expenses = new ArrayList<>();

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addMember(SplitwiseUser user) {
        members.add(user);
    }

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }

    public List<SplitwiseUser> getMembers() {
        return members;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

class Transaction {
    private final String id;
    private final SplitwiseUser sender;
    private final SplitwiseUser receiver;
    private final double amount;

    public Transaction(String id, SplitwiseUser sender, SplitwiseUser receiver, double amount) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public SplitwiseUser getSender() {
        return sender;
    }

    public SplitwiseUser getReceiver() {
        return receiver;
    }

    public double getAmount() {
        return amount;
    }
}

class SplitwiseService {
    private static SplitwiseService instance;

    private Map<String, SplitwiseUser> users;
    private Map<String, Group> groups;
    private List<Transaction> transactions;

    private SplitwiseService() {
        users = new ConcurrentHashMap<>();
        groups = new ConcurrentHashMap<>();
        transactions = new ArrayList<>();
    }

    public static synchronized SplitwiseService getInstance() {
        if (instance == null) {
            instance = new SplitwiseService();
        }
        return instance;
    }

    public void addUser(SplitwiseUser user) {
        users.put(user.getId(), user);
    }

    public void createGroup(Group group) {
        groups.put(group.getId(), group);
    }

    public void addExpense(String groupId, Expense expense) {
        Group group = groups.get(groupId);
        if (group == null) throw new IllegalArgumentException("Group not found");

        group.addExpense(expense);

        SplitwiseUser paidBy = expense.getPaidBy();
        double totalAmount = expense.getAmount();
        List<Split> splits = expense.getSplits();

        // Set split amounts
        if (splits.get(0) instanceof EqualSplit) {
            double splitAmount = Math.round((totalAmount / splits.size()) * 100.0) / 100.0;
            for (Split split : splits) {
                split.setAmount(splitAmount);
            }
        } else if (splits.get(0) instanceof PercentSplit) {
            for (Split split : splits) {
                PercentSplit ps = (PercentSplit) split;
                double calculatedAmount = Math.round((totalAmount * ps.getPercent() / 100.0) * 100.0) / 100.0;
                ps.setAmount(calculatedAmount);
            }
        }
        // ExactSplit already has amounts set

        // Update balances
        for (Split split : splits) {
            if (split.getUser().getId().equals(paidBy.getId())) continue;

            double owed = split.getAmount();
            SplitwiseUser owedUser = split.getUser();

            paidBy.addBalance(owedUser.getId(), owed);
            owedUser.addBalance(paidBy.getId(), -owed);
        }
    }


    public void settleBalance(String fromUserId, String toUserId, double amount) {
        SplitwiseUser from = users.get(fromUserId);
        SplitwiseUser to = users.get(toUserId);

        if (from == null || to == null) throw new IllegalArgumentException("SplitwiseUser not found");

        from.addBalance(toUserId, -amount);
        to.addBalance(fromUserId, amount);

        Transaction txn = new Transaction(UUID.randomUUID().toString(), from, to, amount);
        transactions.add(txn);
    }

    public void showBalances(String userId) {
        SplitwiseUser user = users.get(userId);
        if (user == null) return;
        for (Map.Entry<String, Double> entry : user.getBalances().entrySet()) {
            String otherUserId = entry.getKey();
            double amount = entry.getValue();
            if (amount > 0) {
                System.out.println("  " + users.get(otherUserId).getName() + " owes " + user.getName() + ": ₹" + amount);
            } else if (amount < 0) {
                System.out.println("  " + user.getName() + " owes " + users.get(otherUserId).getName() + ": ₹" + (-amount));
            }
        }
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}

public class Splitwise {
    public static void main(String[] args) {
        SplitwiseService splitwise = SplitwiseService.getInstance();

        // Create Users
        SplitwiseUser u1 = new SplitwiseUser("u1", "Alice", "alice@gmail.com");
        SplitwiseUser u2 = new SplitwiseUser("u2", "Bob", "bob@gmail.com");
        SplitwiseUser u3 = new SplitwiseUser("u3", "Charlie", "charlie@gmail.com");

        splitwise.addUser(u1);
        splitwise.addUser(u2);
        splitwise.addUser(u3);

        // Create Group
        Group tripGroup = new Group("g1", "Goa Trip");
        tripGroup.addMember(u1);
        tripGroup.addMember(u2);
        tripGroup.addMember(u3);

        splitwise.createGroup(tripGroup);

        // Expense 1: ₹300 paid by Alice, split equally
        List<Split> splits1 = new ArrayList<>();
        splits1.add(new EqualSplit(u1));
        splits1.add(new EqualSplit(u2));
        splits1.add(new EqualSplit(u3));

        Expense expense1 = new Expense("e1", 300, "Hotel", u1, splits1);
        splitwise.addExpense("g1", expense1);

        // Show balances
        splitwise.showBalances("u1");
        splitwise.showBalances("u2");
        splitwise.showBalances("u3");

        // Settle: Bob pays Alice ₹100
        splitwise.settleBalance("u2", "u1", 100);

        // Show updated balances
        System.out.println("\n🔁 After settlement:");
        splitwise.showBalances("u1");
        splitwise.showBalances("u2");
        splitwise.showBalances("u3");
    }
}