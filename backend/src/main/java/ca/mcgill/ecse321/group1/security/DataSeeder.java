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

  private static final String MANAGER_EMAIL = "manager@kloth.com";
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
    Employee emp1 = createEmployee("alice.parker@kloth.com");
    Employee emp2 = createEmployee("bob.builder@kloth.com");
    Employee emp3 = createEmployee("carol.ann@kloth.com");

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

    // Classic T-Shirt — white, black, cream
    ClothingModel tshirt =
        createClothingModel(
            "Classic Boxy T-Shirt",
            "Kloth Essentials",
            ClothingModel.Category.Tops,
            25f,
            "A wardrobe staple crafted from soft 100% cotton. Features a relaxed crew neck and a clean, minimalist cut that pairs effortlessly with anything.");
    ClothingVariant[] tshirtWhiteAll =
        createAllSizes(
            tshirt,
            "#FFFFFF",
            30,
            "https://media.weekday.com/assets/003/79/94/7994ab87c6312ad8dd511d0490ca525609d3a01a_xxl-1.jpg?imwidth=1600");
    ClothingVariant tshirtWhiteS = tshirtWhiteAll[0], tshirtWhiteM = tshirtWhiteAll[1];
    ClothingVariant[] tshirtBlackAll =
        createAllSizes(
            tshirt,
            "#1C1C1C",
            30,
            "https://media.weekday.com/assets/003/52/e8/52e8ebf91e143ff620dbe8c17d3b1c0d5b8a83f0_xxl-1.jpg?imwidth=1600");
    ClothingVariant tshirtBlackM = tshirtBlackAll[1], tshirtBlackL = tshirtBlackAll[2];
    ClothingVariant[] tshirtCreamAll =
        createAllSizes(
            tshirt,
            "#FBFAE9",
            15,
            "https://media.weekday.com/assets/003/a7/e8/a7e809c082e61667af6486917c3416f8c7a23824_xxl-1.jpg?imwidth=1600");
    ClothingVariant tshirtCreamXL = tshirtCreamAll[3];

    // Premium Hoodie — black, gray, white, brown
    ClothingModel hoodie =
        createClothingModel(
            "Premium Hoodie",
            "Kloth Originals",
            ClothingModel.Category.Tops,
            60f,
            "A heavyweight fleece hoodie built for comfort and durability. Features a kangaroo pocket, adjustable drawstring hood, and a cozy brushed interior.");
    ClothingVariant[] hoodieBlackAll =
        createAllSizes(
            hoodie,
            "#1C1C1C",
            15,
            "https://static.zara.net/assets/public/0363/18a9/60e149328a0b/634299cd4403/00761370800-000-e1/00761370800-000-e1.jpg?ts=1754985838610&w=750");
    ClothingVariant hoodieBlackL = hoodieBlackAll[2], hoodieBlackXL = hoodieBlackAll[3];
    ClothingVariant[] hoodieGrayAll =
        createAllSizes(
            hoodie,
            "#c7c8c8",
            20,
            "https://static.zara.net/assets/public/5f99/0231/d3814b679341/3283b69fc286/00761370803-000-e1/00761370803-000-e1.jpg?ts=1754985839053&w=750");
    ClothingVariant hoodieGrayM = hoodieGrayAll[1], hoodieGrayL = hoodieGrayAll[2];
    createAllSizes(
        hoodie,
        "#797491",
        25,
        "https://static.zara.net/assets/public/f3d0/b7a4/e4874e648553/513e8762a2d5/00761370519-000-e1/00761370519-000-e1.jpg?ts=1774604187856&w=750");
    createAllSizes(
        hoodie,
        "#8f9358",
        20,
        "https://static.zara.net/assets/public/f491/a7b1/c4ee43ba9c5a/c8584d6a9759/00761370648-000-e1/00761370648-000-e1.jpg?ts=1774604206634&w=750");

    // Straight fit jeans - blue, dark indigo, navy
    ClothingModel jeans =
        createClothingModel(
            "Straight Fit Jeans",
            "Kloth Denim",
            ClothingModel.Category.Bottoms,
            90f,
            "Classic straight-leg denim with a mid-rise waist and five-pocket construction. Made from durable stretch denim for a comfortable fit throughout the day.");
    ClothingVariant[] jeansBlueAll =
        createAllSizes(
            jeans,
            "#4e6e8f",
            15,
            "https://imagescdn.simons.ca/images/19659/226836/45/A2_1.jpg?__=4");
    ClothingVariant jeansNavyS = jeansBlueAll[0],
        jeansBlueM = jeansBlueAll[1],
        jeansBlueL = jeansBlueAll[2];
    ClothingVariant[] jeansIndigoAll =
        createAllSizes(
            jeans,
            "#373238",
            14,
            "https://imagescdn.simons.ca/images/19659/226837/1/A2_1.jpg?__=3");
    ClothingVariant jeansIndigoM = jeansIndigoAll[1];
    createAllSizes(
        jeans,
        "#1d2a3d",
        20,
        "https://imagescdn.simons.ca/images/6652/26107/40/A2_1.jpg?__=3");

    // Summer Floral Dress — white, brown
    ClothingModel dress =
        createClothingModel(
            "Summer Dress",
            "Kloth Studio",
            ClothingModel.Category.Dresses,
            70f,
            "A breezy summer dress draping to the ankles. Made from lightweight woven fabric, perfect for warm-weather occasions.");
    ClothingVariant[] dressFloralWhite =
        createAllSizes(
            dress,
            "#e4e2df",
            10,
            "https://dam.dynamiteclothing.com/m/43f1a6cf410a6be7/original/100096517_0QO_alt3_1920x2880.jpg?sw=740&sh=1110");
    ClothingVariant dressFloralS = dressFloralWhite[0],
        dressFloralM = dressFloralWhite[1],
        dressFloralL = dressFloralWhite[2];

    createAllSizes(
        dress,
        "#3a2728",
        10,
        "https://dam.dynamiteclothing.com/m/17817c7723843918/original/100096517_8F4_alt3_1920x2880.jpg?sw=740&sh=1110");

    // Braided Belt — brown, olive, beige
    ClothingModel braidedBelt =
        createClothingModel(
            "Braided Belt",
            "Kloth Accessories",
            ClothingModel.Category.Accessories,
            55f,
            "A handcrafted braided leather belt with a smooth silver buckle. Adds a polished, artisanal touch to both casual and smart-casual outfits.");
    createAllSizes(
        braidedBelt,
        "#3a2728",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20175420068?maxW=3840&fmt=auto");
    createAllSizes(
        braidedBelt,
        "#5c5751",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20175413038?maxW=3840&fmt=auto");
    createAllSizes(
        braidedBelt,
        "#E8DCC8",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20175412061?maxW=3840&fmt=auto");

    // Polo Shirt — blue, brown, green, pink
    ClothingModel polo2 =
        createClothingModel(
            "Fresh Polo Shirt",
            "Kloth Essentials",
            ClothingModel.Category.Tops,
            60f,
            "A refined piqué cotton polo with a two-button placket and ribbed collar and cuffs. Effortlessly bridges the gap between casual comfort and polished style.");
    createAllSizes(
        polo2,
        "#334461",
        20,
        "https://www.privatewhitevc.com/cdn/shop/files/TheCivyPolo-Navy.png?v=1750389454");
    createAllSizes(
        polo2,
        "#ebdfd8",
        20,
        "https://www.privatewhitevc.com/cdn/shop/files/TheCivyPolo-FlakeWhite.png?v=1751233163");
    createAllSizes(
        polo2,
        "#e9d4c1",
        20,
        "https://www.privatewhitevc.com/cdn/shop/files/THEJACKSPOLO-ALABASTER.png?v=1751203712");

    // Bomber Jacket — olive, black
    ClothingModel jacket =
        createClothingModel(
            "Polar Fleece Jacket",
            "Kloth Originals",
            ClothingModel.Category.Outerwear,
            270f,
            "With its relaxed fit as well as a new luxurious and comfortable fabric, this Polar Fleece won't leave your side from the moment you put it on.");
    ClothingVariant[] jacketOliveAll =
        createAllSizes(
            jacket,
            "#e2c79e",
            8,
            "https://www.privatewhitevc.com/cdn/shop/files/TheFleeceZipThrough-Pebble.png?v=1756843285");
    ClothingVariant jacketOliveM = jacketOliveAll[1], jacketOliveL = jacketOliveAll[2];
    ClothingVariant[] jacketBlackAll =
        createAllSizes(
            jacket,
            "#1C1C1C",
            8,
            "https://www.privatewhitevc.com/cdn/shop/files/TheFleeceZipThrough-Ink.png?v=1756842774");
    ClothingVariant jacketBlackL = jacketBlackAll[2], jacketBlackXL = jacketBlackAll[3];

    // Shirts
    // Shirt — gray, beige, light blue
    ClothingModel shirt =
        createClothingModel(
            "Shirt",
            "Kloth Essentials",
            ClothingModel.Category.Tops,
            80f,
            "A tailored dress shirt cut from wrinkle-resistant poplin fabric. Features a spread collar and a single-button cuffs for a sharp everyday look.");
    createAllSizes(
        shirt,
        "#9E9E9E",
        20,
        "https://www.privatewhitevc.com/cdn/shop/files/THECIVVYSHIRT-WHITE.png?v=1750879193");
    createAllSizes(
        shirt,
        "#C8B89A",
        20,
        "https://www.privatewhitevc.com/cdn/shop/files/TheSleeperShirt-Ecru.png?v=1751028825");
    createAllSizes(
        shirt,
        "#ADD8E6",
        20,
        "https://www.privatewhitevc.com/cdn/shop/files/THECIVVYSHIRT-AZURE.png?v=1750615162");

    // Classic Polo Shirt — white, navy
    ClothingModel polo =
        createClothingModel(
            "Classic Polo Shirt",
            "Kloth Essentials",
            ClothingModel.Category.Tops,
            40f,
            "A timeless polo shirt in smooth piqué cotton with a classic fit. An iconic silhouette that stands the test of time.");
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
    // Wool Bomber — gray
    ClothingModel woolBomber =
        createClothingModel(
            "Suede Bomber Jacket",
            "Kloth Originals",
            ClothingModel.Category.Outerwear,
            450f,
            "A premium suede-blend bomber with a structured silhouette and satin lining. The ribbed collar, cuffs, and hem give it a sporty edge with a sophisticated finish.");
    createAllSizes(
        woolBomber,
        "#292e46",
        20,
        "https://www.privatewhitevc.com/cdn/shop/files/ThePSReversibleSuedeBomber-Brown-toNavy.png?v=1752082510");

    // Cargo Shorts — khaki, black
    ClothingModel shorts =
        createClothingModel(
            "Cargo Shorts",
            "Kloth Originals",
            ClothingModel.Category.Bottoms,
            35f,
            "Relaxed-fit cargo shorts with multiple utility pockets. Made from durable ripstop fabric, designed for comfort on the move.");
    ClothingVariant[] shortsKhakiAll =
        createAllSizes(
            shorts,
            "#3c3a22",
            18,
            "https://imagescdn.simons.ca/images/15128/138254/40/A2_1.jpg?__=4");
    ClothingVariant shortsKhakiS = shortsKhakiAll[0],
        shortsKhakiM = shortsKhakiAll[1],
        shortsKhakiL = shortsKhakiAll[2];
    ClothingVariant[] shortsBlackAll =
        createAllSizes(
            shorts,
            "#1C1C1C",
            20,
            "https://imagescdn.simons.ca/images/20546/227047/1/A2_1.jpg?__=11");
    ClothingVariant shortsBlackM = shortsBlackAll[1];

    // Limited Edition Tee — white (mostly out-of-stock for UI edge-case testing)
    ClothingModel rareItem =
        createClothingModel(
            "Limited Edition Tee",
            "Kloth Studio",
            ClothingModel.Category.Tops,
            90f,
            "A collector's piece from our limited Kloth Studio drop. Crafted from premium Supima cotton with a subtle tonal graphic, this tee won't be restocked once it's gone.");
    createVariant(
        rareItem,
        Size.S,
        "#d4d0cf",
        0,
        "https://media.weekday.com/assets/003/e7/1f/e71fcaac58a6bb80a055bdb6b40a5cdbae10d39a_xxl-1.jpg?imwidth=1600");
    ClothingVariant rareTeeBlackM =
        createVariant(
            rareItem,
            Size.M,
            "#d4d0cf",
            0,
            "https://media.weekday.com/assets/003/e7/1f/e71fcaac58a6bb80a055bdb6b40a5cdbae10d39a_xxl-1.jpg?imwidth=1600");
    ClothingVariant rareTeeBlackL =
        createVariant(
            rareItem,
            Size.L,
            "#d4d0cf",
            2,
            "https://media.weekday.com/assets/003/e7/1f/e71fcaac58a6bb80a055bdb6b40a5cdbae10d39a_xxl-1.jpg?imwidth=1600");
    createVariant(
        rareItem,
        Size.XL,
        "#d4d0cf",
        0,
        "https://media.weekday.com/assets/003/e7/1f/e71fcaac58a6bb80a055bdb6b40a5cdbae10d39a_xxl-1.jpg?imwidth=1600");

    // Relaxed Cargo Trousers — tan, dark, taupe
    ClothingModel cargoTrousers =
        createClothingModel(
            "Relaxed Cargo Trousers",
            "Kloth Essentials",
            ClothingModel.Category.Bottoms,
            98f,
            "Easy-fitting cargo trousers with a tapered leg and multiple side pockets. The relaxed cut and elasticated waistband make them a versatile go-to for everyday wear.");
    ClothingVariant[] cargoTrousersTanAll =
        createAllSizes(
            cargoTrousers,
            "#C2B49A",
            15,
            "https://imagescdn.simons.ca/images/20546/228072/23/A2_1.jpg?__=10");
    ClothingVariant cargoTrousersTanM = cargoTrousersTanAll[1],
        cargoTrousersTanL = cargoTrousersTanAll[2];
    createAllSizes(
        cargoTrousers,
        "#3B3A32",
        15,
        "https://imagescdn.simons.ca/images/20546/228072/1/A2_1.jpg?__=10");
    ClothingVariant[] cargoTrousersOliveAll =
        createAllSizes(
            cargoTrousers,
            "#6B6455",
            10,
            "https://imagescdn.simons.ca/images/20546/228072/31/A2_1.jpg?__=10");
    ClothingVariant cargoTrousersOliveL = cargoTrousersOliveAll[2];

    // Midi Dress — red, white
    ClothingModel midiDress =
        createClothingModel(
            "Midi Dress",
            "Kloth Studio",
            ClothingModel.Category.Dresses,
            90f,
            "An elegant midi-length dress with a fitted bodice and flowy skirt. Crafted from fluid fabric that drapes beautifully, transitioning seamlessly from day to evening.");
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

    // Pleated Wide-Leg Pants — tan, black, navy
    ClothingModel wideLegPants =
        createClothingModel(
            "Pleated Wide-Leg Pants",
            "Kloth Essentials",
            ClothingModel.Category.Bottoms,
            115f,
            "Sophisticated wide-leg trousers with front pleats and a high-rise waist. Cut from a smooth suiting fabric that gives a structured, elevated look for any occasion.");
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

    // Denim Jacket
    // Indigo Denim Jacket — indigo
    ClothingModel indigoDenimJacket =
        createClothingModel(
            "Indigo Denim Jacket",
            "Kloth Denim",
            ClothingModel.Category.Tops,
            80f,
            "A fashion forward denim jacket in deep indigo selvedge denim. Features chest flap pockets, adjustable side tabs, and a sturdy metal button placket that gets better with age.");
    createAllSizes(
        indigoDenimJacket,
        "#3B4A6B",
        20,
        "https://media.weekday.com/assets/003/f1/14/f11405a369525a4d2a4337807b24f97dcfcafaf6_xxl-1.jpg?imwidth=1600");

    // Linen Blend Camp Shirt — Taupe, cream, black
    ClothingModel campShirt =
        createClothingModel(
            "Linen Blend Camp Shirt",
            "Kloth Studio",
            ClothingModel.Category.Tops,
            75f,
            "A relaxed camp-collar shirt in a breathable linen-cotton blend. The open collar and straight hem give it an effortlessly laid-back resort aesthetic.");
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

    // Nylon Crossbody Bag — black, beige
    ClothingModel crossbodyBag =
        createClothingModel(
            "Nylon Crossbody Bag",
            "Kloth Accessories",
            ClothingModel.Category.Accessories,
            55f,
            "A compact and lightweight nylon crossbody bag with an adjustable strap and multiple zip compartments. Practical enough for daily use, stylish enough for special occasions.");
    ClothingVariant[] crossbodyBlackAll =
        createAllSizes(
            crossbodyBag,
            "#1A1A18",
            15,
            "https://cdn.media.amplience.net/i/harryrosen/20165937075-2?maxW=3840&fmt=auto");
    ClothingVariant crossbodyBlackS = crossbodyBlackAll[0], crossbodyBlackM = crossbodyBlackAll[1];
    ClothingVariant[] crossbodyOliveAll =
        createAllSizes(
            crossbodyBag,
            "#E6DFD1",
            10,
            "https://cdn.media.amplience.net/i/harryrosen/20165957070-2?maxW=3840&fmt=auto");
    ClothingVariant crossbodyOliveS = crossbodyOliveAll[0];
    // Linen Pants — baby blue, cream
    ClothingModel linenPants =
        createClothingModel(
            "Linen Pants",
            "Kloth Essentials",
            ClothingModel.Category.Bottoms,
            125f,
            "Lightweight linen trousers with a relaxed straight cut and elastic waistband. Naturally breathable and perfect for warm-weather dressing, from beach to brunch.");
    createAllSizes(
        linenPants,
        "#B0D4E8",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20157916041?maxW=3840&fmt=auto");
    createAllSizes(
        linenPants,
        "#F5F0E8",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20157911070?maxW=3840&fmt=auto");

    // Puffer Jacket — black
    ClothingModel pufferJacket =
        createClothingModel(
            "Leather Jacket",
            "Kloth Originals",
            ClothingModel.Category.Outerwear,
            500f,
            "A genuine leather jacket that combines style and functionality. Quilted baffling and a high-zip collar protect against the coldest of days.");
    createAllSizes(
        pufferJacket,
        "#502e18",
        20,
        "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fmensflair.com%2Fwp-content%2Fuploads%2F2023%2F07%2Fprivate-white-twin-track-brown.jpg&f=1&nofb=1&ipt=444be83c218c9cc448f22f8ef6140ae23116e36f6d83cbc1445dd9a7d160e16e");

    // Layered Coach Jacket — navy, black
    ClothingModel coachJacket =
        createClothingModel(
            "Layered Coach Jacket",
            "Kloth Originals",
            ClothingModel.Category.Outerwear,
            390f,
            "A refined coach jacket with a snap-button front and a subtle layered construction for added depth. Finished with a woven inner lining and chest pocket for a put-together look.");
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

    // Retro Sunglasses — light blue, light green
    ClothingModel retroSunglasses =
        createClothingModel(
            "Retro Sunglasses",
            "Kloth Accessories",
            ClothingModel.Category.Accessories,
            100f,
            "Vintage-inspired oval sunglasses with tinted lenses and a slim metal frame. UV400 protective lenses and a lightweight build make them a stylish everyday essential.");
    createAllSizes(
        retroSunglasses,
        "#AED6F1",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20179741067?maxW=3840&fmt=auto");
    createAllSizes(
        retroSunglasses,
        "#A8D5B5",
        20,
        "https://cdn.media.amplience.net/i/harryrosen/20179740068?maxW=3840&fmt=auto");

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
    addItemToOrder(order8, jeansIndigoM, 1, jeans.getPrice());
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
    addItemToOrder(order11, jeansBlueM, 1, jeans.getPrice());
    addItemToOrder(order11, hoodieGrayL, 1, hoodie.getPrice());
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
