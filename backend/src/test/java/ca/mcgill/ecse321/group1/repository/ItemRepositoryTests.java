package ca.mcgill.ecse321.group1.repository;

import ca.mcgill.ecse321.group1.model.Employee;
import ca.mcgill.ecse321.group1.model.Item;
import ca.mcgill.ecse321.group1.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class ItemRepositoryTests {
    @Autowired
    private ItemRepository itemRepository;

    // used to test interactions between item class and many-to-one association with clothing variant
    @Autowired
    private ClothingVariantRepository clothingVariantRepository;

    // used to test interactions between item class association with order
    @Autowired
    private OrderRepository orderRepository;

    // used to test interactions with customer
    @Autowired
    private CustomerRepository customerRepository;

    // used to test interactions with employee
    @Autowired
    private EmployeeRepository employeeRepository;

    // used to test interactions with person
    @Autowired
    private PersonRepository personRepository;

    // used to test interactions with clothing model
    @Autowired
    private ClothingModelRepository clothingModelRepository;

    @AfterEach
    public void clearDatabase() {
        itemRepository.deleteAll();
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        employeeRepository.deleteAll();
        personRepository.deleteAll();
        clothingVariantRepository.deleteAll();
        clothingModelRepository.deleteAll();
    }

    @Test
    public void testPersistAndLoadItem() {
        // Create person

        int quantity = 20;
        float price = 199.99f;
        Item itemTest = new Item();
        itemTest.setQuantity(quantity);
        itemTest.setPrice(price);

        // Save Item
        itemTest = itemRepository.save(itemTest);
        String id = itemTest.getItemID();

        // Read item from database
        Item itemTestFromDb = itemRepository.findItemByItemID(id);

        // Assert correct response
        assertNotNull(itemTestFromDb);
        assertEquals(price, itemTestFromDb.getPrice());
        assertEquals(itemTestFromDb.getQuantity(), quantity);
    }

    @Test
    public void testFindItemByInvalidId() {
        Item result = itemRepository.findItemByItemID("nonexistent-id");
        assertNull(result);
    }

    @Test
    public void testUpdateItemQuantity() {
        // Create and save
        Item item = new Item();
        item.setQuantity(5);
        item.setPrice(199.99f);
        item = itemRepository.save(item);
        String id = item.getItemID();
        // Update quantity
        item.setQuantity(99);
        itemRepository.save(item);

        // Read back and assert
        Item updatedItem = itemRepository.findItemByItemID(id);
        assertNotNull(updatedItem);
        assertEquals(99, updatedItem.getQuantity());
    }

    @Test
    public void testPersistItemWithPrice() {
        float price = 49.99f;
        int quantity = 3;

        Item item = new Item();
        item.setQuantity(quantity);
        item.setPrice(price);
        item = itemRepository.save(item);
        String id = item.getItemID();

        Item fromDb = itemRepository.findItemByItemID(id);
        assertNotNull(fromDb);
        assertEquals(quantity, fromDb.getQuantity());
        assertEquals(price, fromDb.getPrice());
    }

    @Test
    public void testDeleteItem() {
        Item item = new Item();
        item.setQuantity(3);
        item.setPrice(29.99f);
        item = itemRepository.save(item);
        String id = item.getItemID();

        itemRepository.delete(item);

        Item deletedItem = itemRepository.findItemByItemID(id);
        assertNull(deletedItem);
    }

    @Test
    public void testFindAllItems() {
        Item item1 = new Item();
        item1.setQuantity(1);
        item1.setPrice(94.99f);

        Item item2 = new Item();
        item2.setQuantity(2);
        item2.setPrice(1.99f);

        Item item3 = new Item();
        item3.setQuantity(3);
        item3.setPrice(10.99f);

        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);

        List<Item> allItems = (List<Item>) itemRepository.findAll();
        assertNotNull(allItems);
        assertEquals(3, allItems.size());
    }

    @Test
    public void testPersistItemWithZeroQuantity() {
        // Edge case: quantity of 0 should remain in DB unless explicitly removed from the inventory list.
        Item item = new Item();
        item.setQuantity(0);
        item.setPrice(54.99f);
        item = itemRepository.save(item);
        String id = item.getItemID();

        Item fromDb = itemRepository.findItemByItemID(id);
        assertNotNull(fromDb);
        assertEquals(0, fromDb.getQuantity());
    }

    @Test
    public void testAutoGeneratedIdIsUnique() {
        Item item1 = new Item();
        item1.setQuantity(10);
        item1.setPrice(999.99f);

        Item item2 = new Item();
        item2.setQuantity(20);
        item2.setPrice(899.99f);

        item1 = itemRepository.save(item1);
        item2 = itemRepository.save(item2);

        assertNotNull(item1.getItemID());
        assertNotNull(item2.getItemID());
        assertNotEquals(item1.getItemID(), item2.getItemID());
    }

    @Test
    public void testItemPersistsWithClothingVariant() {
        ClothingVariant variant = new ClothingVariant();
        variant.setSize(ClothingVariant.Size.M);
        variant.setColor("Black");
        variant.setStockQuantity(10);
        variant = clothingVariantRepository.save(variant); // required before linking


        Item item = new Item();
        item.setQuantity(4);
        item.setPrice(9.99f);
        item.setClothingVariant(variant);
        item = itemRepository.save(item);
        String id = item.getItemID();

        Item fromDb = itemRepository.findItemByItemID(id);
        assertNotNull(fromDb);
        assertNotNull(fromDb.getClothingVariant());
        assertEquals(variant.getClothingVariantID(), fromDb.getClothingVariant().getClothingVariantID());
        assertEquals("Black", fromDb.getClothingVariant().getColor());
        assertEquals(ClothingVariant.Size.M, fromDb.getClothingVariant().getSize());
    }

    @Test
    public void testItemPersistsWithOrder() {
        // Person is required for both Customer and Employee (via PersonRole)
        Person customerPerson = new Person("personTemp", "customer@example.com", "pass1");
        personRepository.save(customerPerson);

        Person employeePerson = new Person("person-2", "employee@example.com", "pass2");
        personRepository.save(employeePerson);

        // Customer and Employee both require a Person
        Customer customer = new Customer();
        customer.setAddress("123 Main St");
        customer.setLoyaltyPoints(0);
        customer.setPerson(customerPerson);
        customerRepository.save(customer);

        Employee employee = new Employee();
        employee.setPerson(employeePerson);
        employeeRepository.save(employee);

        // Order requires both a Customer and an Employee
        Order order = new Order();
        order.setOrderStatus(Order.OrderStatus.Preparing);
        order.setOrderDate(Date.valueOf("2024-01-01"));
        order.setDeliveryDate(Date.valueOf("2024-01-10"));
        order.setAddress("123 Main St");
        order.setCustomer(customer);
        order.setEmployee(employee);
        order = orderRepository.save(order);
        String orderID = order.getOrderID();

        // Link order to item
        Item item = new Item();
        item.setQuantity(2);
        item.setPrice(39.99f);
        item.setOrder(order);
        item = itemRepository.save(item);
        String itemID = item.getItemID();

        // Read back and assert
        Item fromDb = itemRepository.findItemByItemID(itemID);
        assertNotNull(fromDb);
        assertTrue(fromDb.hasOrder());
        assertEquals(orderID, fromDb.getOrder().getOrderID());
        assertEquals(Order.OrderStatus.Preparing, fromDb.getOrder().getOrderStatus());
        assertEquals("123 Main St", fromDb.getOrder().getAddress());
    }

    @Test
    public void testItemPersistsWithCustomer() {
        // Person is required for Customer (via PersonRole)
        //parameter: String aPersonID, String aEmail, String aPassword
        Person person = new Person("person-3","shopper@example.com", "pass3");
        personRepository.save(person);

        // Customer requires a Person
        Customer customer = new Customer();
        customer.setAddress("456 Elm St");
        customer.setLoyaltyPoints(100);
        customer.setPerson(person);
        customer = customerRepository.save(customer);
        String customerRoleID = customer.getRoleID();

        // Link customer to item
        Item item = new Item();
        item.setQuantity(6);
        item.setPrice(59.99f);
        item.setCustomer(customer);
        item = itemRepository.save(item);
        String itemID = item.getItemID();

        // Read back and assert
        Item fromDb = itemRepository.findItemByItemID(itemID);
        assertNotNull(fromDb);
        assertTrue(fromDb.hasCustomer());
        assertEquals(customerRoleID, fromDb.getCustomer().getRoleID());
        assertEquals("456 Elm St", fromDb.getCustomer().getAddress());
        assertEquals(100, fromDb.getCustomer().getLoyaltyPoints());
    }

    @Test
    public void testItemWithNoOrderOrCustomer() {
        // Optional associations should default to null
        Item item = new Item();
        item.setQuantity(1);
        item.setPrice(79.99f);
        item = itemRepository.save(item);
        String id = item.getItemID();

        Item fromDb = itemRepository.findItemByItemID(id);
        assertNotNull(fromDb);
        assertFalse(fromDb.hasOrder());
        assertFalse(fromDb.hasCustomer());
        assertNull(fromDb.getOrder());
        assertNull(fromDb.getCustomer());
    }
}