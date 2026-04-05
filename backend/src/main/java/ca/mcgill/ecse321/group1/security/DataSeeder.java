package ca.mcgill.ecse321.group1.security;

import ca.mcgill.ecse321.group1.model.*;
import ca.mcgill.ecse321.group1.model.ClothingVariant.Size;
import ca.mcgill.ecse321.group1.model.Order.OrderStatus;
import ca.mcgill.ecse321.group1.repository.*;
import java.sql.Date;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

// Seeds the database with a default Manager account and dummy data for frontend testing
// Run via: ./gradlew seedDatabase
@Component
public class DataSeeder {

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

  public void seed() {
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
    Customer cust1 =
        createCustomer("john.doe@example.com", "123 Maple St, Montreal, QC H3A 1B1", 150);
    Customer cust2 =
        createCustomer("jane.smith@example.com", "456 Oak Ave, Montreal, QC H2X 2C3", 320);
    Customer cust3 = createCustomer("mike.chen@example.com", "789 Pine Rd, Laval, QC H7N 4E5", 75);
    Customer cust4 =
        createCustomer("sara.lee@example.com", "321 Elm Blvd, Longueuil, QC J4K 2F7", 500);
    Customer cust5 =
        createCustomer("alex.tremblay@example.com", "654 Cedar Dr, Brossard, QC J4Y 1M8", 0);

    // Clothing Models & Variants
    // Classic T-Shirt multiple colors and sizes
    ClothingModel tshirt =
        createClothingModel(
            "Classic T-Shirt", "Generic", ClothingModel.Category.Tops, 25f);
    ClothingVariant tshirtWhiteS = createVariant(tshirt, Size.S, "#FFFFFF", 30, "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant tshirtWhiteM = createVariant(tshirt, Size.M, "#FFFFFF", 45, "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant tshirtBlackM = createVariant(tshirt, Size.M, "#1C1C1C", 40, "https://images.unsplash.com/photo-1503341504253-dff4815485f1?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant tshirtBlackL = createVariant(tshirt, Size.L, "#1C1C1C", 25, "https://images.unsplash.com/photo-1503341504253-dff4815485f1?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant tshirtNavyXL = createVariant(tshirt, Size.XL, "#1F305E", 15, "https://images.unsplash.com/photo-1618354691373-d851c5c3a990?w=800&auto=format&fit=crop&q=60&fm=jpg");

    // Premium Hoodie
    ClothingModel hoodie =
        createClothingModel(
            "Premium Hoodie", "Generic", ClothingModel.Category.Tops, 60f);
    ClothingVariant hoodieGrayM = createVariant(hoodie, Size.M, "#808080", 20, "https://images.unsplash.com/photo-1509942774463-acf339cf87d5?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant hoodieGrayL = createVariant(hoodie, Size.L, "#808080", 18, "https://images.unsplash.com/photo-1509942774463-acf339cf87d5?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant hoodieBlackL = createVariant(hoodie, Size.L, "#1C1C1C", 22, "https://images.unsplash.com/photo-1556821840-3a63f15732ce?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant hoodieBlackXL = createVariant(hoodie, Size.XL, "#1C1C1C", 10, "https://images.unsplash.com/photo-1556821840-3a63f15732ce?w=800&auto=format&fit=crop&q=60&fm=jpg");

    // Slim Fit Jeans
    ClothingModel jeans =
        createClothingModel(
            "Slim Fit Jeans", "Generic", ClothingModel.Category.Bottoms, 80f);
    ClothingVariant jeansBlueS = createVariant(jeans, Size.S, "#3B5998", 12, "https://images.unsplash.com/photo-1542272604-787c3835535d?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant jeansBlueM = createVariant(jeans, Size.M, "#3B5998", 20, "https://images.unsplash.com/photo-1542272604-787c3835535d?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant jeansBlueL = createVariant(jeans, Size.L, "#3B5998", 16, "https://images.unsplash.com/photo-1542272604-787c3835535d?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant jeansBlackM = createVariant(jeans, Size.M, "#1C1C1C", 14, "https://images.unsplash.com/photo-1475178626620-a4d074967452?w=800&auto=format&fit=crop&q=60&fm=jpg");

    // Summer Floral Dress
    ClothingModel dress =
        createClothingModel(
            "Summer Floral Dress", "Generic", ClothingModel.Category.Dresses, 50f);
    ClothingVariant dressFloralS = createVariant(dress, Size.S, "#E8A0BF", 8, "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant dressFloralM = createVariant(dress, Size.M, "#E8A0BF", 12, "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant dressFloralL = createVariant(dress, Size.L, "#E8A0BF", 6, "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=800&auto=format&fit=crop&q=60&fm=jpg");

    // Bomber Jacket
    ClothingModel jacket =
        createClothingModel(
            "Bomber Jacket", "Generic", ClothingModel.Category.Outerwear, 120f);
    ClothingVariant jacketOliveM = createVariant(jacket, Size.M, "#556B2F", 7, "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant jacketOliveL = createVariant(jacket, Size.L, "#556B2F", 9, "https://images.unsplash.com/photo-1591047139829-d91aecb6caea?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant jacketBlackL = createVariant(jacket, Size.L, "#1C1C1C", 11, "https://images.unsplash.com/photo-1548126032-079a0fb0099d?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant jacketBlackXL = createVariant(jacket, Size.XL, "#1C1C1C", 5, "https://images.unsplash.com/photo-1548126032-079a0fb0099d?w=800&auto=format&fit=crop&q=60&fm=jpg");

    // Classic Polo Shirt
    ClothingModel polo =
        createClothingModel(
            "Classic Polo Shirt", "Generic", ClothingModel.Category.Tops, 40f);
    ClothingVariant poloWhiteM = createVariant(polo, Size.M, "#FFFFFF", 25, "https://images.unsplash.com/photo-1586363104862-3a5e2ab60d99?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant poloNavyM = createVariant(polo, Size.M, "#1F305E", 20, "https://images.unsplash.com/photo-1598032895397-b9472444bf93?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant poloNavyL = createVariant(polo, Size.L, "#1F305E", 18, "https://images.unsplash.com/photo-1598032895397-b9472444bf93?w=800&auto=format&fit=crop&q=60&fm=jpg");

    // Cargo Shorts
    ClothingModel shorts =
        createClothingModel(
            "Cargo Shorts", "Generic", ClothingModel.Category.Bottoms, 35f);
    ClothingVariant shortsKhakiS = createVariant(shorts, Size.S, "#C3B091", 15, "https://images.unsplash.com/photo-1562886520-8a50978b4655?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant shortsKhakiM = createVariant(shorts, Size.M, "#C3B091", 22, "https://images.unsplash.com/photo-1562886520-8a50978b4655?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant shortsKhakiL = createVariant(shorts, Size.L, "#C3B091", 18, "https://images.unsplash.com/photo-1562886520-8a50978b4655?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant shortsBlackM = createVariant(shorts, Size.M, "#1C1C1C", 20, "https://images.unsplash.com/photo-1591195853828-11db59a44f43?w=800&auto=format&fit=crop&q=60&fm=jpg");

    // Limited Edition Tee mostly out-of-stock for UI edge-case testing
    ClothingModel rareItem =
        createClothingModel(
            "Limited Edition Tee", "Generic", ClothingModel.Category.Tops, 90f);
    ClothingVariant rareTeeBlackM = createVariant(rareItem, Size.M, "#1C1C1C", 0, "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=800&auto=format&fit=crop&q=60&fm=jpg");
    ClothingVariant rareTeeBlackL = createVariant(rareItem, Size.L, "#1C1C1C", 2, "https://images.unsplash.com/photo-1583743814966-8936f5b7be1a?w=800&auto=format&fit=crop&q=60&fm=jpg");

    // Orders
    // cust1: 2 delivered, 1 preparing
    Order order1 =
        createOrder(
            cust1,
            emp1,
            OrderStatus.Delivered,
            date("2026-01-10"),
            date("2026-01-15"),
            "123 Maple St, Montreal, QC H3A 1B1",
            0f);
    addItemToOrder(order1, tshirtBlackM, 2, tshirt.getPrice());
    addItemToOrder(order1, hoodieGrayM, 1, hoodie.getPrice());

    Order order2 =
        createOrder(
            cust1,
            emp2,
            OrderStatus.Delivered,
            date("2026-02-05"),
            date("2026-02-12"),
            "123 Maple St, Montreal, QC H3A 1B1",
            5f);
    addItemToOrder(order2, jeansBlueL, 1, jeans.getPrice());
    addItemToOrder(order2, poloNavyM, 2, polo.getPrice());

    Order order3 =
        createOrder(
            cust1,
            emp1,
            OrderStatus.Preparing,
            date("2026-03-20"),
            date("2026-07-02"),
            "123 Maple St, Montreal, QC H3A 1B1",
            0f);
    addItemToOrder(order3, jacketOliveM, 1, jacket.getPrice());

    // cust2: 1 delivered, 1 cancelled, 1 preparing
    Order order4 =
        createOrder(
            cust2,
            emp2,
            OrderStatus.Delivered,
            date("2026-01-22"),
            date("2026-01-28"),
            "456 Oak Ave, Montreal, QC H2X 2C3",
            10f);
    addItemToOrder(order4, dressFloralM, 1, dress.getPrice());
    addItemToOrder(order4, shortsKhakiM, 2, shorts.getPrice());

    Order order5 =
        createOrder(
            cust2,
            emp3,
            OrderStatus.Cancelled,
            date("2026-02-14"),
            null,
            "456 Oak Ave, Montreal, QC H2X 2C3",
            0f);
    addItemToOrder(order5, rareTeeBlackL, 1, rareItem.getPrice());

    Order order6 =
        createOrder(
            cust2,
            emp1,
            OrderStatus.Preparing,
            date("2026-03-22"),
            date("2026-07-02"),
            "456 Oak Ave, Montreal, QC H2X 2C3",
            15f);
    addItemToOrder(order6, jacketBlackL, 1, jacket.getPrice());
    addItemToOrder(order6, tshirtNavyXL, 3, tshirt.getPrice());

    // cust3: 1 delivered
    Order order7 =
        createOrder(
            cust3,
            emp3,
            OrderStatus.Delivered,
            date("2026-02-28"),
            date("2026-03-05"),
            "789 Pine Rd, Laval, QC H7N 4E5",
            0f);
    addItemToOrder(order7, tshirtWhiteS, 2, tshirt.getPrice());
    addItemToOrder(order7, shortsKhakiS, 1, shorts.getPrice());
    addItemToOrder(order7, poloWhiteM, 1, polo.getPrice());

    // cust4: 1 delivered, 1 preparing
    Order order8 =
        createOrder(
            cust4,
            emp3,
            OrderStatus.Delivered,
            date("2026-01-05"),
            date("2026-01-09"),
            "321 Elm Blvd, Longueuil, QC J4K 2F7",
            20f);
    addItemToOrder(order8, jeansBlackM, 1, jeans.getPrice());
    addItemToOrder(order8, hoodieBlackL, 1, hoodie.getPrice());

    Order order9 =
        createOrder(
            cust4,
            emp3,
            OrderStatus.Preparing,
            date("2026-03-24"),
            date("2026-07-02"),
            "321 Elm Blvd, Longueuil, QC J4K 2F7",
            25f);
    addItemToOrder(order9, dressFloralS, 2, dress.getPrice());
    addItemToOrder(order9, tshirtWhiteM, 1, tshirt.getPrice());

    // cust5: 1 preparing (first order)
    Order order10 =
        createOrder(
            cust5,
            null,
            OrderStatus.Preparing,
            date("2026-03-25"),
            date("2026-07-02"),
            "654 Cedar Dr, Brossard, QC J4Y 1M8",
            0f);
    addItemToOrder(order10, jacketOliveL, 1, jacket.getPrice());
    addItemToOrder(order10, shortsBlackM, 2, shorts.getPrice());
    addItemToOrder(order10, jacketBlackXL, 1, jacket.getPrice());

    // Spread remaining variants across extra orders for emp3 load
    Order order11 =
        createOrder(
            cust3,
            emp3,
            OrderStatus.Delivered,
            date("2026-03-10"),
            date("2026-03-15"),
            "789 Pine Rd, Laval, QC H7N 4E5",
            0f);
    addItemToOrder(order11, jeansBlueM, 1, jeans.getPrice());
    addItemToOrder(order11, hoodieGrayL, 1, hoodie.getPrice());
    addItemToOrder(order11, shortsKhakiL, 1, shorts.getPrice());
    addItemToOrder(order11, dressFloralL, 1, dress.getPrice());
    addItemToOrder(order11, poloNavyL, 1, polo.getPrice());
    addItemToOrder(order11, tshirtBlackL, 1, tshirt.getPrice());

    // Cart items (in-cart, not yet ordered)
    createCartItem(cust3, hoodieBlackXL, 1, hoodie.getPrice());
    createCartItem(cust5, tshirtBlackM, 2, tshirt.getPrice());
    createCartItem(cust5, jeansBlueS, 1, jeans.getPrice());
    // rareTeeBlackM is 0-stock useful for testing "out of stock" display in catalog
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

  private ClothingModel createClothingModel(
      String name, String brand, ClothingModel.Category category, float price) {
    ClothingModel model = new ClothingModel();
    model.setName(name);
    model.setBrand(brand);
    model.setCategory(category);
    model.setPrice(price);
    return clothingModelRepository.save(model);
  }

  private ClothingVariant createVariant(
      ClothingModel model, Size size, String color, int stock, String imagePath) {
    ClothingVariant variant = new ClothingVariant();
    variant.setModel(model);
    variant.setSize(size);
    variant.setColor(color);
    variant.setStockQuantity(stock);
    variant.setImagePath(imagePath);
    return clothingVariantRepository.save(variant);
  }

  private Order createOrder(
      Customer customer,
      Employee employee,
      OrderStatus status,
      Date orderDate,
      Date deliveryDate,
      String address,
      float loyaltySaving) {
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

  private void createCartItem(
      Customer customer, ClothingVariant variant, int quantity, float unitPrice) {
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
