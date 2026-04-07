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

    // Clothing Models & Variants — each model gets a variant for every size (S M L XL) per color

    // Classic T-Shirt — white, black, navy
    ClothingModel tshirt =
        createClothingModel(
            "Classic T-Shirt", "Kloth Essentials", ClothingModel.Category.Tops, 25f);
    ClothingVariant[] tshirtWhiteAll =
        createAllSizes(
            tshirt,
            "#FFFFFF",
            30,
            "https://i.pinimg.com/1200x/06/7d/03/067d0313c3e9482aef3804296aadd53c.jpg");
    ClothingVariant tshirtWhiteS = tshirtWhiteAll[0], tshirtWhiteM = tshirtWhiteAll[1];
    ClothingVariant[] tshirtBlackAll =
        createAllSizes(
            tshirt,
            "#1C1C1C",
            30,
            "https://i.pinimg.com/1200x/fb/7f/6a/fb7f6a6963520f0ff5b638f73404a176.jpg");
    ClothingVariant tshirtBlackM = tshirtBlackAll[1], tshirtBlackL = tshirtBlackAll[2];
    ClothingVariant[] tshirtCreamAll =
        createAllSizes(
            tshirt,
            "#D4C8B8",
            15,
            "https://i.pinimg.com/1200x/2d/7b/21/2d7b21a072cd538589f63358fbd35520.jpg");
    ClothingVariant tshirtCreamXL = tshirtCreamAll[3];

    // Premium Hoodie — gray, black
    ClothingModel hoodie =
        createClothingModel("Premium Hoodie", "Kloth Originals", ClothingModel.Category.Tops, 60f);
    ClothingVariant[] hoodieTealAll =
        createAllSizes(
            hoodie,
            "#738C89",
            20,
            "https://i.pinimg.com/1200x/fe/5e/0d/fe5e0d2891819e1981e996b5e7670516.jpg");
    ClothingVariant hoodieTealM = hoodieTealAll[1], hoodieTealL = hoodieTealAll[2];
    ClothingVariant[] hoodieBlackAll =
        createAllSizes(
            hoodie,
            "#1C1C1C",
            15,
            "https://i.pinimg.com/736x/f7/67/9c/f7679c84bd40669b7b66c021244205cd.jpg");
    ClothingVariant hoodieBlackL = hoodieBlackAll[2], hoodieBlackXL = hoodieBlackAll[3];
    createAllSizes(
        hoodie,
        "#F0F0F0",
        25,
        "https://i.pinimg.com/1200x/12/db/1e/12db1e73ed55afba605c3a4f2fa846c1.jpg");
    createAllSizes(
        hoodie,
        "#BA8C5D",
        20,
        "https://i.pinimg.com/736x/6c/84/18/6c841808f0bce9dbdead9b03cd4e95ef.jpg");

    // Straight fit jeans - blue indigo navy
    ClothingModel jeans =
        createClothingModel(
            "Straight Fit Jeans", "Kloth Essentials", ClothingModel.Category.Bottoms, 90f);
    ClothingVariant[] jeansBlueAll =
        createAllSizes(
            jeans,
            "#1E2646",
            15,
            "https://cdn.media.amplience.net/i/harryrosen/20178920041?maxW=3840&fmt=auto");
    ClothingVariant jeansNavyS = jeansBlueAll[0],
        jeansNavyM = jeansBlueAll[1],
        jeansNavyL = jeansBlueAll[2];
    ClothingVariant[] jeansBlackAll =
        createAllSizes(
            jeans,
            "#1C1C1C",
            14,
            "https://cdn.media.amplience.net/i/harryrosen/20178921047?maxW=3840&fmt=auto");
    ClothingVariant jeansBlackM = jeansBlackAll[1];
    createAllSizes(
        jeans,
        "#7D91B2",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20178922047?maxW=3840&fmt=auto");

    // Summer Floral Dress — pink, brown
    ClothingModel dress =
        createClothingModel(
            "Summer Floral Dress", "Kloth Studio", ClothingModel.Category.Dresses, 70f);
    ClothingVariant[] dressFloralWhite =
        createAllSizes(
            dress,
            "#E8A0BF",
            10,
            "https://dam.dynamiteclothing.com/m/43f1a6cf410a6be7/original/100096517_0QO_alt3_1920x2880.jpg?sw=740&sh=1110");
    ClothingVariant dressFloralS = dressFloralWhite[0],
        dressFloralM = dressFloralWhite[1],
        dressFloralL = dressFloralWhite[2];

    ClothingVariant[] dressFloralBrown =
        createAllSizes(
            dress,
            "#A0522D",
            10,
            "https://dam.dynamiteclothing.com/m/17817c7723843918/original/100096517_8F4_alt3_1920x2880.jpg?sw=740&sh=1110");
    ClothingVariant dressFloralBrownS = dressFloralBrown[0],
        dressFloralBrownM = dressFloralBrown[1],
        dressFloralBrownL = dressFloralBrown[2];

    //Polos
    // Polo Shirt — blue, brown, green, pink
    ClothingModel polo2 =
        createClothingModel(
            "Polo Shirt", "Kloth Essentials", ClothingModel.Category.Tops, 40f);
    createAllSizes(
        polo2,
        "#1E3A5F",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20177733046?maxW=3840&fmt=auto");
    createAllSizes(
        polo2,
        "#6B3A2A",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20177745038?maxW=3840&fmt=auto");
    createAllSizes(
        polo2,
        "#2D5A27",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20177790035?maxW=3840&fmt=auto");
    createAllSizes(
        polo2,
        "#E8A0BF",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20177799055?maxW=3840&fmt=auto");

    // Bomber Jacket — olive, black
    ClothingModel jacket =
        createClothingModel(
            "Bomber Jacket", "Kloth Originals", ClothingModel.Category.Outerwear, 120f);
    ClothingVariant[] jacketOliveAll =
        createAllSizes(
            jacket,
            "#556B2F",
            8,
            "https://cdn.media.amplience.net/i/harryrosen/20179677035?maxW=3840&fmt=auto");
    ClothingVariant jacketOliveM = jacketOliveAll[1], jacketOliveL = jacketOliveAll[2];
    ClothingVariant[] jacketBlackAll =
        createAllSizes(
            jacket,
            "#1C1C1C",
            8,
            "https://cdn.media.amplience.net/i/harryrosen/20182499047?maxW=3840&fmt=auto");
    ClothingVariant jacketBlackL = jacketBlackAll[2], jacketBlackXL = jacketBlackAll[3];

    //Shirts
    // Shirt — gray, beige, light blue
    ClothingModel shirt =
        createClothingModel(
            "Shirt", "Kloth Essentials", ClothingModel.Category.Tops, 40f);
    createAllSizes(
        shirt,
        "#9E9E9E",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20181297072?maxW=3840&fmt=auto");
    createAllSizes(
        shirt,
        "#C8B89A",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20181299061?maxW=3840&fmt=auto");
    createAllSizes(
        shirt,
        "#ADD8E6",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20181305046?maxW=3840&fmt=auto");

    // Classic Polo Shirt — white, navy
    ClothingModel polo =
        createClothingModel(
            "Classic Polo Shirt", "Kloth Essentials", ClothingModel.Category.Tops, 40f);
    ClothingVariant[] poloWhiteAll =
        createAllSizes(
            polo,
            "#FFFFFF",
            20,
            "https://i.pinimg.com/1200x/7f/63/8c/7f638cdad4844d451b0c5e064900d826.jpg");
    ClothingVariant poloWhiteM = poloWhiteAll[1];
    ClothingVariant[] poloNavyAll =
        createAllSizes(
            polo,
            "#1F305E",
            18,
            "https://i.pinimg.com/1200x/a5/7b/5c/a57b5c3af8531b36e6be94d7afa5f581.jpg");
    ClothingVariant poloNavyM = poloNavyAll[1], poloNavyL = poloNavyAll[2];
    createAllSizes(
        polo,
        "#1C564A",
        20,
        "https://i.pinimg.com/1200x/da/b4/ff/dab4ffbd58f51266b8fb4edc2687b443.jpg");
    createAllSizes(
        polo,
        "#641826",
        25,
        "https://i.pinimg.com/1200x/21/03/d2/2103d260cb11f09909454ad4731bac47.jpg");
    createAllSizes(
        polo,
        "#8E6E57",
        20,
        "https://i.pinimg.com/736x/9e/cb/7b/9ecb7bc67d9960862250ce997d5dd738.jpg");
    createAllSizes(
        polo,
        "#93928E",
        35,
        "https://i.pinimg.com/1200x/35/31/3e/35313ea8a71b08015b84a8e459a64614.jpg");
    createAllSizes(
        polo,
        "#19181C",
        15,
        "https://i.pinimg.com/1200x/5d/13/f4/5d13f484d8f6a06f5f754edb9fa6bc81.jpg");
    // Cargo Shorts — khaki, black
    ClothingModel shorts =
        createClothingModel("Cargo Shorts", "Kloth Originals", ClothingModel.Category.Bottoms, 35f);
    ClothingVariant[] shortsKhakiAll =
        createAllSizes(
            shorts,
            "#928D79",
            18,
            "https://i.pinimg.com/1200x/34/1a/bc/341abc0a9fdebd4539b312d48af5b961.jpg");
    ClothingVariant shortsKhakiS = shortsKhakiAll[0],
        shortsKhakiM = shortsKhakiAll[1],
        shortsKhakiL = shortsKhakiAll[2];
    ClothingVariant[] shortsBlackAll =
        createAllSizes(
            shorts,
            "#1C1C1C",
            20,
            "https://i.pinimg.com/736x/6a/89/7f/6a897f9ee86159a8b8272a6b4164c09c.jpg");
    ClothingVariant shortsBlackM = shortsBlackAll[1];

    // Limited Edition Tee — black (mostly out-of-stock for UI edge-case testing)
    ClothingModel rareItem =
        createClothingModel(
            "Limited Edition Tee", "Kloth Studio", ClothingModel.Category.Tops, 90f);
    createVariant(
        rareItem,
        Size.S,
        "#1C1C1C",
        0,
        "https://cdn.media.amplience.net/i/harryrosen/20181317070?maxW=3840&fmt=auto");
    ClothingVariant rareTeeBlackM =
        createVariant(
            rareItem,
            Size.M,
            "#1C1C1C",
            0,
            "https://cdn.media.amplience.net/i/harryrosen/20181317070?maxW=3840&fmt=auto");
    ClothingVariant rareTeeBlackL =
        createVariant(
            rareItem,
            Size.L,
            "#1C1C1C",
            2,
            "https://cdn.media.amplience.net/i/harryrosen/20181317070?maxW=3840&fmt=auto");
    createVariant(
        rareItem,
        Size.XL,
        "#1C1C1C",
        0,
        "https://cdn.media.amplience.net/i/harryrosen/20181317070?maxW=3840&fmt=auto");

    // Relaxed Cargo Trousers — tan, dark, taupe
    ClothingModel cargoTrousers =
        createClothingModel(
            "Relaxed Cargo Trousers", "Kloth Essentials", ClothingModel.Category.Bottoms, 98f);
    ClothingVariant[] cargoTrousersTanAll =
        createAllSizes(
            cargoTrousers,
            "#C2B49A",
            15,
            "https://i.pinimg.com/736x/2f/0c/02/2f0c02c0dc68e1fd7da4a58b6249e7d1.jpg");
    ClothingVariant cargoTrousersTanM = cargoTrousersTanAll[1],
        cargoTrousersTanL = cargoTrousersTanAll[2];
    createAllSizes(
        cargoTrousers,
        "#3B3A32",
        15,
        "https://i.pinimg.com/736x/04/45/b8/0445b8b4567a01b3acee022f3c34e92a.jpg");
    ClothingVariant[] cargoTrousersOliveAll =
        createAllSizes(
            cargoTrousers,
            "#6B6455",
            10,
            "https://i.pinimg.com/1200x/c6/54/eb/c654eb35700ce012b0a3e5b49b996aec.jpg");
    ClothingVariant cargoTrousersOliveL = cargoTrousersOliveAll[2];

    // Midi Dress — red, white
    ClothingModel midiDress =
        createClothingModel(
            "Midi Dress", "Kloth Studio", ClothingModel.Category.Dresses, 60f);
    createAllSizes(
        midiDress,
        "#C0392B",
        20,
        "https://static.massimodutti.net/assets/public/7ac0/1373/72b34dbaa2b0/41e48e8fdf26/06620748599-o1/06620748599-o1.jpg?ts=1772463171413&w=1440&f=auto");
    createAllSizes(
        midiDress,
        "#FFFFFF",
        20,
        "https://static.massimodutti.net/assets/public/84c3/c721/24da4a49b9f9/f82ebee55677/06620748250-o1/06620748250-o1.jpg?ts=1774953540327&w=1440&f=auto");

    // Pleated Wide-Leg Pants — tan, black, taupe
    ClothingModel wideLegPants =
        createClothingModel(
            "Pleated Wide-Leg Pants", "Kloth Essentials", ClothingModel.Category.Bottoms, 115f);
    ClothingVariant[] wideLegTanAll =
        createAllSizes(
            wideLegPants,
            "#C2B49A",
            12,
            "https://cdn.media.amplience.net/i/harryrosen/20162692066?maxW=3840&fmt=auto");
    ClothingVariant wideLegTanS = wideLegTanAll[0], wideLegTanM = wideLegTanAll[1];
    ClothingVariant[] wideLegBlackAll =
        createAllSizes(
            wideLegPants,
            "#1A1A18",
            10,
            "https://cdn.media.amplience.net/i/harryrosen/20162690047?maxW=3840&fmt=auto");
    ClothingVariant wideLegBlackM = wideLegBlackAll[1];
    createAllSizes(
        wideLegPants,
        "#1E2646",
        8,
        "https://cdn.media.amplience.net/i/harryrosen/20162668047?maxW=3840&fmt=auto");

    //Denim Jacket
    // Indigo Denim Jacket — indigo
    ClothingModel indigoDenimJacket =
        createClothingModel(
            "Indigo Denim Jacket", "Naked & Famous Denim", ClothingModel.Category.Tops, 80f);
    createAllSizes(
        indigoDenimJacket,
        "#3B4A6B",
        20,
        "https://nakedandfamousdenim.com/cdn/shop/files/164056664_01.jpg?v=1761623683&width=1800");

    // Linen Blend Camp Shirt — cream, tan
    ClothingModel campShirt =
        createClothingModel(
            "Linen Blend Camp Shirt", "Kloth Studio", ClothingModel.Category.Tops, 78f);
    ClothingVariant[] campShirtCreamAll =
        createAllSizes(
            campShirt,
            "#78685B",
            12,
            "https://i.pinimg.com/1200x/df/da/a7/dfdaa721dd42d5270cdab1cbdb4f7551.jpg");
    ClothingVariant campShirtWhiteS = campShirtCreamAll[0], campShirtWhiteM = campShirtCreamAll[1];
    ClothingVariant[] campShirtTanAll =
        createAllSizes(
            campShirt,
            "#EDEDEB",
            10,
            "https://i.pinimg.com/1200x/1b/db/6c/1bdb6ccae4a4f417cee45cee454258e9.jpg");
    ClothingVariant campShirtTanM = campShirtTanAll[1], campShirtTanL = campShirtTanAll[2];

    createAllSizes(
        campShirt,
        "#201E1F",
        15,
        "https://i.pinimg.com/1200x/aa/2f/e9/aa2fe9071a9e77654f4b9ac97d8ad2ec.jpg");

    // Nylon Crossbody Bag — black, olive
    ClothingModel crossbodyBag =
        createClothingModel(
            "Nylon Crossbody Bag", "Kloth Accessories", ClothingModel.Category.Accessories, 65f);
    ClothingVariant[] crossbodyBlackAll =
        createAllSizes(
            crossbodyBag,
            "#1A1A18",
            15,
            "https://i.pinimg.com/1200x/4b/9e/e3/4b9ee37cf344ddc311721688b4e6e735.jpg");
    ClothingVariant crossbodyBlackS = crossbodyBlackAll[0], crossbodyBlackM = crossbodyBlackAll[1];
    ClothingVariant[] crossbodyOliveAll =
        createAllSizes(
            crossbodyBag,
            "#E6DFD1",
            10,
            "https://i.pinimg.com/1200x/38/59/59/385959b4ac8b1645520c6c5f75dc9fb3.jpg");
    ClothingVariant crossbodyOliveS = crossbodyOliveAll[0];

    // Puffer Jacket — black
    ClothingModel pufferJacket =
        createClothingModel(
            "Puffer Jacket", "Kloth Originals", ClothingModel.Category.Outerwear, 40f);
    createAllSizes(
        pufferJacket,
        "#1C1C1C",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20167404075?maxW=3840&fmt=auto");

    // Layered Coach Jacket — navy, black
    ClothingModel coachJacket =
        createClothingModel(
            "Layered Coach Jacket", "Kloth Originals", ClothingModel.Category.Outerwear, 165f);
    ClothingVariant[] coachNavyAll =
        createAllSizes(
            coachJacket,
            "#2C3040",
            8,
            "https://static.massimodutti.net/assets/public/3e91/8f31/7b3441b0b869/5e2532e63359/03127433401-o1/03127433401-o1.jpg?ts=1775050466011&w=1440&f=auto");
    ClothingVariant coachNavyM = coachNavyAll[1], coachNavyL = coachNavyAll[2];
    ClothingVariant[] coachBlackAll =
        createAllSizes(
            coachJacket,
            "#3B3A32",
            8,
            "https://static.massimodutti.net/assets/public/6e82/4d4c/a2cb4feda5c8/5dade8157a53/03127433717-o1/03127433717-o1.jpg?ts=1775050514551&w=1440&f=auto");
    ClothingVariant coachBlackM = coachBlackAll[1], coachBlackL = coachBlackAll[2];

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
    addItemToOrder(order1, hoodieTealM, 1, hoodie.getPrice());

    Order order2 =
        createOrder(
            cust1,
            emp2,
            OrderStatus.Delivered,
            date("2026-02-05"),
            date("2026-02-12"),
            "123 Maple St, Montreal, QC H3A 1B1",
            5f);
    addItemToOrder(order2, jeansNavyL, 1, jeans.getPrice());
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
    addItemToOrder(order3, campShirtWhiteM, 2, campShirt.getPrice());

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
    addItemToOrder(order4, crossbodyBlackM, 1, crossbodyBag.getPrice());

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
    addItemToOrder(order6, tshirtCreamXL, 3, tshirt.getPrice());

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
    addItemToOrder(order8, wideLegBlackM, 1, wideLegPants.getPrice());
    addItemToOrder(order8, coachNavyL, 1, coachJacket.getPrice());

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
    addItemToOrder(order10, cargoTrousersTanM, 1, cargoTrousers.getPrice());

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
    addItemToOrder(order11, jeansNavyM, 1, jeans.getPrice());
    addItemToOrder(order11, hoodieTealL, 1, hoodie.getPrice());
    addItemToOrder(order11, shortsKhakiL, 1, shorts.getPrice());
    addItemToOrder(order11, dressFloralL, 1, dress.getPrice());
    addItemToOrder(order11, poloNavyL, 1, polo.getPrice());
    addItemToOrder(order11, tshirtBlackL, 1, tshirt.getPrice());
    addItemToOrder(order11, campShirtTanM, 1, campShirt.getPrice());

    // cust2: new accessories order
    Order order12 =
        createOrder(
            cust2,
            emp2,
            OrderStatus.Delivered,
            date("2026-03-01"),
            date("2026-03-06"),
            "456 Oak Ave, Montreal, QC H2X 2C3",
            0f);
    addItemToOrder(order12, crossbodyOliveS, 1, crossbodyBag.getPrice());
    addItemToOrder(order12, coachBlackM, 1, coachJacket.getPrice());
    addItemToOrder(order12, cargoTrousersOliveL, 1, cargoTrousers.getPrice());

    // cust1: new order with wide-leg pants
    Order order13 =
        createOrder(
            cust1,
            emp2,
            OrderStatus.Delivered,
            date("2026-03-12"),
            date("2026-03-18"),
            "123 Maple St, Montreal, QC H3A 1B1",
            10f);
    addItemToOrder(order13, wideLegTanS, 1, wideLegPants.getPrice());
    addItemToOrder(order13, campShirtWhiteS, 1, campShirt.getPrice());
    addItemToOrder(order13, coachNavyM, 1, coachJacket.getPrice());

    // Cart items (in-cart, not yet ordered)
    createCartItem(cust3, hoodieBlackXL, 1, hoodie.getPrice());
    createCartItem(cust3, wideLegTanM, 1, wideLegPants.getPrice());
    createCartItem(cust5, tshirtBlackM, 2, tshirt.getPrice());
    createCartItem(cust5, jeansNavyS, 1, jeans.getPrice());
    createCartItem(cust5, crossbodyBlackS, 1, crossbodyBag.getPrice());
    createCartItem(cust1, coachBlackL, 1, coachJacket.getPrice());
    createCartItem(cust1, cargoTrousersTanL, 2, cargoTrousers.getPrice());
    createCartItem(cust2, campShirtTanL, 1, campShirt.getPrice());
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
    return createClothingModel(name, brand, category, price, "");
  }

  private ClothingModel createClothingModel(
      String name, String brand, ClothingModel.Category category, float price, String description) {
    ClothingModel model = new ClothingModel();
    model.setName(name);
    model.setBrand(brand);
    model.setCategory(category);
    model.setPrice(price);
    model.setDescription(description);
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

  private ClothingVariant[] createAllSizes(
      ClothingModel model, String color, int stock, String imagePath) {
    Size[] sizes = {Size.S, Size.M, Size.L, Size.XL};
    ClothingVariant[] variants = new ClothingVariant[4];
    for (int i = 0; i < sizes.length; i++) {
      variants[i] = createVariant(model, sizes[i], color, stock, imagePath);
    }
    return variants; // index: 0=S, 1=M, 2=L, 3=XL
  }

  private Date date(String iso) {
    return Date.valueOf(iso);
  }
}
