package ca.mcgill.ecse321.group1.security;

import ca.mcgill.ecse321.group1.model.*;
import ca.mcgill.ecse321.group1.model.ClothingVariant.Size;
import ca.mcgill.ecse321.group1.model.Order.OrderStatus;
import ca.mcgill.ecse321.group1.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.sql.Date;

// Runs on every server startup and seeds the database with a default Manager account
// and dummy data (customers, employees, clothing catalog, orders) for frontend testing.
// If the manager email already exists, this entire seeder is skipped.
@Component
public class DataSeeder implements CommandLineRunner {

  private static final String MANAGER_EMAIL = "manager@admin.com";
  private static final String MANAGER_PASSWORD = "manager123";

  private final PersonRepository personRepository;
  private final ManagerRepository managerRepository;
  private final CustomerRepository customerRepository;
  private final EmployeeRepository employeeRepository;
  private final ClothingModelRepository clothingModelRepository;
  private final ClothingVariantRepository clothingVariantRepository;
  private final OrderRepository orderRepository;
  private final ItemRepository itemRepository;
  private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  public DataSeeder(
      PersonRepository personRepository,
      ManagerRepository managerRepository,
      CustomerRepository customerRepository,
      EmployeeRepository employeeRepository,
      ClothingModelRepository clothingModelRepository,
      ClothingVariantRepository clothingVariantRepository,
      OrderRepository orderRepository,
      ItemRepository itemRepository) {
    this.personRepository = personRepository;
    this.managerRepository = managerRepository;
    this.customerRepository = customerRepository;
    this.employeeRepository = employeeRepository;
    this.clothingModelRepository = clothingModelRepository;
    this.clothingVariantRepository = clothingVariantRepository;
    this.orderRepository = orderRepository;
    this.itemRepository = itemRepository;
  }

  @Override
  public void run(String... args) {
    // Skip entirely if the database already has any data
    if (personRepository.count() > 0) {
      return;
    }

    // Manager
    Person managerPerson = createPerson(MANAGER_EMAIL, MANAGER_PASSWORD);
    Manager manager = new Manager();
    manager.setPerson(managerPerson);
    managerRepository.save(manager);

    // Employees
    Employee emp1 = createEmployee("alice.employee@store.com");
    Employee emp2 = createEmployee("bob.employee@store.com");
    Employee emp3 = createEmployee("carol.employee@store.com");

    // Customers
    Customer cust1 = createCustomer("john.doe@example.com",     "123 Maple St, Montreal, QC H3A 1B1",  150);
    Customer cust2 = createCustomer("jane.smith@example.com",   "456 Oak Ave, Montreal, QC H2X 2C3",   320);
    Customer cust3 = createCustomer("mike.chen@example.com",    "789 Pine Rd, Laval, QC H7N 4E5",      75);
    Customer cust4 = createCustomer("sara.lee@example.com",     "321 Elm Blvd, Longueuil, QC J4K 2F7", 500);
    Customer cust5 = createCustomer("alex.tremblay@example.com","654 Cedar Dr, Brossard, QC J4Y 1M8",  0);

    // Clothing Models & Variants
    // Classic T-Shirt — multiple colors and sizes
    ClothingModel tshirt = createClothingModel("Classic T-Shirt", 24.99f,
        "https://picsum.photos/seed/tshirt/400/400.jpg");
    ClothingVariant tshirtWhiteS  = createVariant(tshirt, Size.S,  "White", 30);
    ClothingVariant tshirtWhiteM  = createVariant(tshirt, Size.M,  "White", 45);
    ClothingVariant tshirtBlackM  = createVariant(tshirt, Size.M,  "Black", 40);
    ClothingVariant tshirtBlackL  = createVariant(tshirt, Size.L,  "Black", 25);
    ClothingVariant tshirtNavyXL  = createVariant(tshirt, Size.XL, "Navy",  15);

    // Premium Hoodie
    ClothingModel hoodie = createClothingModel("Premium Hoodie", 59.99f,
        "https://picsum.photos/seed/hoodie/400/400.jpg");
    ClothingVariant hoodieGrayM   = createVariant(hoodie, Size.M,  "Gray",  20);
    ClothingVariant hoodieGrayL   = createVariant(hoodie, Size.L,  "Gray",  18);
    ClothingVariant hoodieBlackL  = createVariant(hoodie, Size.L,  "Black", 22);
    ClothingVariant hoodieBlackXL = createVariant(hoodie, Size.XL, "Black", 10);

    // Slim Fit Jeans
    ClothingModel jeans = createClothingModel("Slim Fit Jeans", 79.99f,
        "https://picsum.photos/seed/jeans/400/400.jpg");
    ClothingVariant jeansBlueS  = createVariant(jeans, Size.S,  "Blue",  12);
    ClothingVariant jeansBlueM  = createVariant(jeans, Size.M,  "Blue",  20);
    ClothingVariant jeansBlueL  = createVariant(jeans, Size.L,  "Blue",  16);
    ClothingVariant jeansBlackM = createVariant(jeans, Size.M,  "Black", 14);

    // Summer Floral Dress
    ClothingModel dress = createClothingModel("Summer Floral Dress", 49.99f,
        "https://picsum.photos/seed/dress/400/400.jpg");
    ClothingVariant dressFloralS = createVariant(dress, Size.S, "Floral", 8);
    ClothingVariant dressFloralM = createVariant(dress, Size.M, "Floral", 12);
    ClothingVariant dressFloralL = createVariant(dress, Size.L, "Floral", 6);

    // Bomber Jacket
    ClothingModel jacket = createClothingModel("Bomber Jacket", 119.99f,
        "https://picsum.photos/seed/jacket/400/400.jpg");
    ClothingVariant jacketOliveM  = createVariant(jacket, Size.M,  "Olive", 7);
    ClothingVariant jacketOliveL  = createVariant(jacket, Size.L,  "Olive", 9);
    ClothingVariant jacketBlackL  = createVariant(jacket, Size.L,  "Black", 11);
    ClothingVariant jacketBlackXL = createVariant(jacket, Size.XL, "Black", 5);

    // Classic Polo Shirt
    ClothingModel polo = createClothingModel("Classic Polo Shirt", 39.99f,
        "https://picsum.photos/seed/polo/400/400.jpg");
    ClothingVariant poloWhiteM = createVariant(polo, Size.M, "White", 25);
    ClothingVariant poloNavyM  = createVariant(polo, Size.M, "Navy",  20);
    ClothingVariant poloNavyL  = createVariant(polo, Size.L, "Navy",  18);

    // Cargo Shorts
    ClothingModel shorts = createClothingModel("Cargo Shorts", 34.99f,
        "https://picsum.photos/seed/shorts/400/400.jpg");
    ClothingVariant shortsKhakiS = createVariant(shorts, Size.S, "Khaki", 15);
    ClothingVariant shortsKhakiM = createVariant(shorts, Size.M, "Khaki", 22);
    ClothingVariant shortsKhakiL = createVariant(shorts, Size.L, "Khaki", 18);
    ClothingVariant shortsBlackM = createVariant(shorts, Size.M, "Black", 20);

    // Limited Edition Tee — mostly out-of-stock for UI edge-case testing
    ClothingModel rareItem = createClothingModel("Limited Edition Tee", 89.99f,
        "https://picsum.photos/seed/limited/400/400.jpg");
    ClothingVariant rareTeeBlackM = createVariant(rareItem, Size.M, "Black", 0);
    ClothingVariant rareTeeBlackL = createVariant(rareItem, Size.L, "Black", 2);

    // Orders
    // cust1: 2 delivered, 1 preparing
    Order order1 = createOrder(cust1, emp1, OrderStatus.Delivered,
        date("2026-01-10"), date("2026-01-15"), "123 Maple St, Montreal, QC H3A 1B1", 0f);
    addItemToOrder(order1, tshirtBlackM, 2, tshirt.getPrice());
    addItemToOrder(order1, hoodieGrayM,  1, hoodie.getPrice());

    Order order2 = createOrder(cust1, emp2, OrderStatus.Delivered,
        date("2026-02-05"), date("2026-02-12"), "123 Maple St, Montreal, QC H3A 1B1", 5f);
    addItemToOrder(order2, jeansBlueL, 1, jeans.getPrice());
    addItemToOrder(order2, poloNavyM,  2, polo.getPrice());

    Order order3 = createOrder(cust1, emp1, OrderStatus.Preparing,
        date("2026-03-20"), null, "123 Maple St, Montreal, QC H3A 1B1", 0f);
    addItemToOrder(order3, jacketOliveM, 1, jacket.getPrice());

    // cust2: 1 delivered, 1 cancelled, 1 preparing
    Order order4 = createOrder(cust2, emp2, OrderStatus.Delivered,
        date("2026-01-22"), date("2026-01-28"), "456 Oak Ave, Montreal, QC H2X 2C3", 10f);
    addItemToOrder(order4, dressFloralM, 1, dress.getPrice());
    addItemToOrder(order4, shortsKhakiM, 2, shorts.getPrice());

    Order order5 = createOrder(cust2, emp3, OrderStatus.Cancelled,
        date("2026-02-14"), null, "456 Oak Ave, Montreal, QC H2X 2C3", 0f);
    addItemToOrder(order5, rareTeeBlackL, 1, rareItem.getPrice());

    Order order6 = createOrder(cust2, emp1, OrderStatus.Preparing,
        date("2026-03-22"), null, "456 Oak Ave, Montreal, QC H2X 2C3", 15f);
    addItemToOrder(order6, jacketBlackL, 1, jacket.getPrice());
    addItemToOrder(order6, tshirtNavyXL, 3, tshirt.getPrice());

    // cust3: 1 delivered
    Order order7 = createOrder(cust3, emp3, OrderStatus.Delivered,
        date("2026-02-28"), date("2026-03-05"), "789 Pine Rd, Laval, QC H7N 4E5", 0f);
    addItemToOrder(order7, tshirtWhiteS, 2, tshirt.getPrice());
    addItemToOrder(order7, shortsKhakiS, 1, shorts.getPrice());
    addItemToOrder(order7, poloWhiteM,   1, polo.getPrice());

    // cust4: 1 delivered, 1 preparing
    Order order8 = createOrder(cust4, emp2, OrderStatus.Delivered,
        date("2026-01-05"), date("2026-01-09"), "321 Elm Blvd, Longueuil, QC J4K 2F7", 20f);
    addItemToOrder(order8, jeansBlackM,  1, jeans.getPrice());
    addItemToOrder(order8, hoodieBlackL, 1, hoodie.getPrice());

    Order order9 = createOrder(cust4, emp3, OrderStatus.Preparing,
        date("2026-03-24"), null, "321 Elm Blvd, Longueuil, QC J4K 2F7", 25f);
    addItemToOrder(order9, dressFloralS, 2, dress.getPrice());
    addItemToOrder(order9, tshirtWhiteM, 1, tshirt.getPrice());

    // cust5: 1 preparing (first order)
    Order order10 = createOrder(cust5, emp2, OrderStatus.Preparing,
        date("2026-03-25"), null, "654 Cedar Dr, Brossard, QC J4Y 1M8", 0f);
    addItemToOrder(order10, jacketOliveL,  1, jacket.getPrice());
    addItemToOrder(order10, shortsBlackM,  2, shorts.getPrice());
    addItemToOrder(order10, jacketBlackXL, 1, jacket.getPrice());

    // Spread remaining variants across extra orders for emp3 load
    Order order11 = createOrder(cust3, emp3, OrderStatus.Delivered,
        date("2026-03-10"), date("2026-03-15"), "789 Pine Rd, Laval, QC H7N 4E5", 0f);
    addItemToOrder(order11, jeansBlueM,   1, jeans.getPrice());
    addItemToOrder(order11, hoodieGrayL,  1, hoodie.getPrice());
    addItemToOrder(order11, shortsKhakiL, 1, shorts.getPrice());
    addItemToOrder(order11, dressFloralL, 1, dress.getPrice());
    addItemToOrder(order11, poloNavyL,    1, polo.getPrice());
    addItemToOrder(order11, tshirtBlackL, 1, tshirt.getPrice());

    // Cart items (in-cart, not yet ordered)
    createCartItem(cust3, hoodieBlackXL, 1, hoodie.getPrice());
    createCartItem(cust5, tshirtBlackM,  2, tshirt.getPrice());
    createCartItem(cust5, jeansBlueS,    1, jeans.getPrice());
    // rareTeeBlackM is 0-stock — useful for testing "out of stock" display in catalog
    createCartItem(cust4, rareTeeBlackM, 1, rareItem.getPrice());
  }

  // Helpers

  private Person createPerson(String email, String rawPassword) {
    Person person = new Person();
    person.setEmail(email);
    person.setPassword(passwordEncoder.encode(rawPassword));
    return personRepository.save(person);
  }

  private Employee createEmployee(String email) {
    Person person = createPerson(email, "password123");
    Employee emp = new Employee();
    emp.setPerson(person);
    return employeeRepository.save(emp);
  }

  private Customer createCustomer(String email, String address, int loyaltyPoints) {
    Person person = createPerson(email, "password123");
    Customer cust = new Customer();
    cust.setPerson(person);
    cust.setAddress(address);
    cust.setLoyaltyPoints(loyaltyPoints);
    return customerRepository.save(cust);
  }

  private ClothingModel createClothingModel(String name, float price, String imagePath) {
    ClothingModel model = new ClothingModel();
    model.setName(name);
    model.setPrice(price);
    model.setImagePath(imagePath);
    return clothingModelRepository.save(model);
  }

  private ClothingVariant createVariant(ClothingModel model, Size size, String color, int stock) {
    ClothingVariant variant = new ClothingVariant();
    variant.setModel(model);
    variant.setSize(size);
    variant.setColor(color);
    variant.setStockQuantity(stock);
    return clothingVariantRepository.save(variant);
  }

  private Order createOrder(Customer customer, Employee employee, OrderStatus status,
      Date orderDate, Date deliveryDate, String address, float loyaltySaving) {
    Order order = new Order();
    order.setCustomer(customer);
    order.setEmployee(employee);
    order.setOrderStatus(status);
    order.setOrderDate(orderDate);
    order.setDeliveryDate(deliveryDate);
    order.setAddress(address);
    order.setLoyaltySaving(loyaltySaving);
    return orderRepository.save(order);
  }

  private void addItemToOrder(Order order, ClothingVariant variant, int quantity, float unitPrice) {
    Item item = new Item();
    item.setClothingVariant(variant);
    item.setQuantity(quantity);
    item.setPrice(unitPrice * quantity);
    item.setOrder(order);
    itemRepository.save(item);
  }

  private void createCartItem(Customer customer, ClothingVariant variant, int quantity, float unitPrice) {
    Item item = new Item();
    item.setClothingVariant(variant);
    item.setQuantity(quantity);
    item.setPrice(unitPrice * quantity);
    item.setCustomer(customer);
    itemRepository.save(item);
  }

  private Date date(String iso) {
    return Date.valueOf(iso);
  }
}
