import java.util.*;

enum CoffeeType {
    ESPRESSO,
    LATTE,
    CAPPUCCINO
}

enum Ingredient {
    COFFEE,
    MILK,
    SUGAR,
    WATER
}

class Recipe {
    public Map<Ingredient, Integer> ingredients;

    public Recipe(Map<Ingredient, Integer> ingredients) {
        this.ingredients = ingredients;
    }

    public Map<Ingredient, Integer> getIngredients() {
        return ingredients;
    }
}

class Inventory {
    private Map<Ingredient, Integer> stock;

    public boolean hasEnoughIngredients(Recipe recipe) {
        for (Map.Entry<Ingredient, Integer> entry : recipe.getIngredients().entrySet()) {
            Ingredient ingredient = entry.getKey();
            int requiredAmount = entry.getValue();
            // Check if the stock has enough of the required ingredient
            if (stock.getOrDefault(ingredient, 0) < requiredAmount) {
                System.out.println("Not enough " + ingredient + " in stock. Required: " + requiredAmount
                        + ", Available: " + stock.getOrDefault(ingredient, 0));
                return false;
            }
        }
        return true;
    }

    public void useIngredients(Recipe recipe) {
        // Reduce the stock based on the recipe's ingredients
        for (Map.Entry<Ingredient, Integer> entry : recipe.getIngredients().entrySet()) {
            stock.put(entry.getKey(), stock.get(entry.getKey()) - entry.getValue());
        }
    }

    public Map<Ingredient, Integer> getStock() {
        return stock;
    }

    public void showInventory() {
        System.out.println("Current Inventory:");
        for (Map.Entry<Ingredient, Integer> entry : stock.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    public void addStock(Ingredient ingredient, int amount) {
        stock.put(ingredient, stock.getOrDefault(ingredient, 0) + amount);
    }

    public boolean isLowOnIngredient(Ingredient ingredient) {
        return stock.getOrDefault(ingredient, 0) < 5; // Example threshold
    }
}

class Payment {
    public boolean processPayment(Double amountPaid, Double coffeePrice) {
        if (amountPaid < coffeePrice) {
            System.out.println("Insufficient payment. Coffee price: " + coffeePrice + ", Amount paid: " + amountPaid);
            return false;
        }
        if (amountPaid > coffeePrice) {
            System.out.println("Change to be returned: " + (amountPaid - coffeePrice));
        }
        return true;
    }
}

class Coffee {
    private CoffeeType type;
    private Recipe recipe;
    private Double price;

    public Coffee(CoffeeType type, Recipe recipe, Double price) {
        this.type = type;
        this.recipe = recipe;
        this.price = price;
    }

    public CoffeeType getType() {
        return type;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public Double getPrice() {
        return price;
    }
}

class CoffeeMachine {
    public Map<CoffeeType, Coffee> menu;
    public Inventory inventory;
    public Payment payment;

    public CoffeeMachine(Map<CoffeeType, Coffee> menu, Inventory inventory, Payment payment) {
        this.menu = menu;
        this.inventory = inventory;
        this.payment = payment;
    }

    public void showMenu() {
        System.out.println("Menu: ");
        for (Coffee coffee : menu.values()) {
            System.out.println(coffee.getType() + " - Price: " + coffee.getPrice());
        }
    }

    public void orderCoffee(CoffeeType type, Double amountPaid) {
        Coffee coffee = menu.get(type);
        if (coffee == null) {
            System.out.println("Invalid selection!");
            return;
        }

        if (!inventory.hasEnoughIngredients(coffee.getRecipe())) {
            System.out.println("Sorry, not enough ingredients for " + type);
            return;
        }

        if (payment.processPayment(amountPaid, coffee.getPrice())) {
            inventory.useIngredients(coffee.getRecipe());
            System.out.println("Dispensing: " + type);
        }
    }
}

public class VendingMachine {

    public static void main() {
        Recipe espressoRecipe = new Recipe(Map.of(
                Ingredient.COFFEE, 5,
                Ingredient.WATER, 10));

        Recipe cappuccinoRecipe = new Recipe(Map.of(
                Ingredient.COFFEE, 4,
                Ingredient.WATER, 8,
                Ingredient.MILK, 5));

        Recipe latteRecipe = new Recipe(Map.of(
                Ingredient.COFFEE, 3,
                Ingredient.WATER, 8,
                Ingredient.MILK, 7,
                Ingredient.SUGAR, 2));

        Coffee espresso = new Coffee(CoffeeType.ESPRESSO, espressoRecipe, 100.0);
        Coffee cappuccina = new Coffee(CoffeeType.CAPPUCCINO, cappuccinoRecipe, 200.0);
        Coffee latte = new Coffee(CoffeeType.LATTE, latteRecipe, 150.0);

        HashMap<CoffeeType, Coffee> menu = new HashMap<>();
        menu.put(CoffeeType.ESPRESSO, espresso);
        menu.put(CoffeeType.CAPPUCCINO, cappuccina);
        menu.put(CoffeeType.LATTE, latte);

        Inventory inventory = new Inventory();
        inventory.addStock(Ingredient.COFFEE, 100);
        inventory.addStock(Ingredient.WATER, 100);
        inventory.addStock(Ingredient.MILK, 50);
        inventory.addStock(Ingredient.SUGAR, 30);

        Payment payment = new Payment();

        CoffeeMachine machine1 = new CoffeeMachine(menu, inventory, payment);

        machine1.showMenu();

        System.out.println("\nUser 1 ordering CAPPUCCINO with $5...");
        machine1.orderCoffee(CoffeeType.CAPPUCCINO, 5.0);

        System.out.println("\nUser 2 ordering LATTE with $2...");
        machine1.orderCoffee(CoffeeType.LATTE, 2.0);

        System.out.println("\nUser 3 ordering ESPRESSO with $2...");
        machine1.orderCoffee(CoffeeType.ESPRESSO, 2.0);

        System.out.println("\nInventory after transactions:");
        inventory.showInventory();
    }
}


/*

+------------------------------------------+
|              VendingMachine              |
|              (Main Class)                |
+------------------------------------------+

+------------------------------------------+
|                CoffeeMachine             |
+------------------------------------------+
| - menu: Map<CoffeeType, Coffee>          |
| - inventory: Inventory                   |
| - payment: Payment                       |
+------------------------------------------+
| + showMenu(): void                       |
| + orderCoffee(type: CoffeeType,          |
|                amountPaid: Double): void |
+------------------------------------------+

                      ▲
                      |
+------------------------------------------+
|                 Coffee                   |
+------------------------------------------+
| - type: CoffeeType                       |
| - recipe: Recipe                         |
| - price: Double                          |
+------------------------------------------+
| + getType(): CoffeeType                  |
| + getRecipe(): Recipe                    |
| + getPrice(): Double                     |
+------------------------------------------+

+------------------------------------------+
|                 Recipe                   |
+------------------------------------------+
| - ingredients: Map<Ingredient, Integer>  |
+------------------------------------------+
| + getIngredients(): Map<Ingredient,      |
|                      Integer>            |
+------------------------------------------+

+------------------------------------------+
|                 Inventory                |
+------------------------------------------+
| - stock: Map<Ingredient, Integer>        |
+------------------------------------------+
| + hasEnoughIngredients(recipe: Recipe):  |
|       boolean                            |
| + useIngredients(recipe: Recipe): void   |
| + showInventory(): void                  |
| + addStock(ingredient: Ingredient,       |
|             amount: int): void           |
| + isLowOnIngredient(ingredient:          |
|             Ingredient): boolean         |
+------------------------------------------+

+------------------------------------------+
|                 Payment                  |
+------------------------------------------+
| + processPayment(amountPaid: Double,     |
|                    coffeePrice: Double): |
|                    boolean                |
+------------------------------------------+

+------------------------------------------+
|              CoffeeType                  |
|                (enum)                    |
+------------------------------------------+
| + ESPRESSO                               |
| + CAPPUCCINO                             |
| + LATTE                                  |
+------------------------------------------+

+------------------------------------------+
|              Ingredient                  |
|                (enum)                    |
+------------------------------------------+
| + COFFEE                                 |
| + WATER                                  |
| + MILK                                   |
| + SUGAR                                  |
+------------------------------------------+

 */