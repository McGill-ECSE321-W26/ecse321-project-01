package ca.mcgill.ecse321.group1.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import ca.mcgill.ecse321.group1.model.ClothingVariant;
import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Item;
import ca.mcgill.ecse321.group1.repository.ClothingVariantRepository;
import ca.mcgill.ecse321.group1.repository.CustomerRepository;
import ca.mcgill.ecse321.group1.repository.ItemRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class CartServiceTests {

  @Mock private CustomerRepository customerRepository;
  @Mock private ItemRepository itemRepository;
  @Mock private ClothingVariantRepository clothingVariantRepository;
  @InjectMocks private CartService cartService;

  // ===== Helpers =====

  private ClothingVariant buildVariant(String variantId, float price, int stock) {
    ClothingModel model = new ClothingModel();
    model.setPrice(price);
    ClothingVariant variant = new ClothingVariant();
    variant.setClothingVariantID(variantId);
    variant.setStockQuantity(stock);
    variant.setModel(model);
    return variant;
  }

  private Item buildItem(String itemId, ClothingVariant variant, int quantity) {
    Item item = new Item();
    item.setItemID(itemId);
    item.setClothingVariant(variant);
    item.setQuantity(quantity);
    item.setPrice(variant.getModel().getPrice());
    return item;
  }

  // ===== getItemByID =====

  @Test
  public void testGetItemByID() {
    // Valid get item
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    String itemId = "item1";
    ClothingVariant variant = buildVariant("variant1", 50f, 10);
    Item item = buildItem(itemId, variant, 2);
    item.setCustomer(customer);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByItemID(itemId)).thenReturn(item);

    // Act
    Item result = cartService.getItemByID(customerId, itemId);

    // Assert
    assertNotNull(result);
    assertEquals(itemId, result.getItemID());
    assertEquals(2, result.getQuantity());
  }

  @Test
  public void testGetItemByInvalidID() {
    // Call get Item with invalid ID
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    String itemId = "badItem";
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByItemID(itemId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> cartService.getItemByID(customerId, itemId));
    assertEquals("404 NOT_FOUND \"There is no item with id " + itemId + ".\"", e.getMessage());
  }

  @Test
  public void testGetItemByInvalidCustomer() {
    // Get item using invalid customer id
    String customerId = "badCustomer";
    String itemId = "item1";
    ClothingVariant variant = buildVariant("variant1", 50f, 10);
    Item item = buildItem(itemId, variant, 2);
    when(customerRepository.findByRoleID(customerId)).thenReturn(null);
    when(itemRepository.findByItemID(itemId)).thenReturn(item);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> cartService.getItemByID(customerId, itemId));
    assertEquals(
        "404 NOT_FOUND \"There is no customer with id " + customerId + ".\"", e.getMessage());
  }

  @Test
  public void testGetItemWrongCustomer() {
    // Get item not belong to the customer
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    String itemId = "item1";
    ClothingVariant variant = buildVariant("variant1", 50f, 10);
    Item item = buildItem(itemId, variant, 2);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByItemID(itemId)).thenReturn(item);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> cartService.getItemByID(customerId, itemId));
    assertEquals(
        "400 BAD_REQUEST \"The item with id "
            + itemId
            + "is not part of customer's "
            + customerId
            + "cart.\"",
        e.getMessage());
  }

  // ===== getCartItems =====

  @Test
  public void testGetCartItems() {
    // Get item with valid arguments
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    ClothingVariant variant = buildVariant("variant1", 50f, 10);
    Item item1 = buildItem("item1", variant, 1);
    Item item2 = buildItem("item2", variant, 3);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByCustomer(customer)).thenReturn(List.of(item1, item2));

    // Act
    List<Item> result = cartService.getCartItems(customerId);

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals(item1, result.get(0));
    assertEquals(item2, result.get(1));
  }

  @Test
  public void testGetCartItemsEmptyCart() {
    // Get item of an empty cart
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByCustomer(customer)).thenReturn(List.of());

    // Act
    List<Item> result = cartService.getCartItems(customerId);

    // Assert
    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  public void testGetCartItemsWithInvalidCustomer() {
    // Get item of invalid customer
    // Arrange
    String customerId = "badCustomer";
    when(customerRepository.findByRoleID(customerId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(ResponseStatusException.class, () -> cartService.getCartItems(customerId));
    assertEquals(
        "404 NOT_FOUND \"There is no customer with id " + customerId + ".\"", e.getMessage());
  }

  // ===== addItem =====

  @Test
  public void testAddItem() {
    // Add valid item
    // Arrange
    String variantId = "variant1";
    String customerId = "customer1";
    int quantity = 5;
    ClothingVariant variant = buildVariant(variantId, 100f, 10);
    Customer customer = new Customer();
    when(clothingVariantRepository.findByClothingVariantIDAndArchivedFalse(variantId))
        .thenReturn(variant);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    Item result = cartService.addItem(variantId, customerId, quantity);

    // Assert
    assertNotNull(result);
    assertEquals(quantity, result.getQuantity());
    assertEquals(100, result.getPrice());
    assertEquals(variant, result.getClothingVariant());
    assertEquals(customer, result.getCustomer());
    assertEquals(variant, result.getClothingVariant());
    verify(itemRepository, times(1)).save(any(Item.class));
  }

  @Test
  public void testAddItemAtExactStockQuantity() {
    // Add item with stock quantity
    // Arrange
    String variantId = "variant1";
    String customerId = "customer1";
    int stock = 5;
    ClothingVariant variant = buildVariant(variantId, 50f, stock);
    Customer customer = new Customer();
    when(clothingVariantRepository.findByClothingVariantIDAndArchivedFalse(variantId))
        .thenReturn(variant);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

    Item result = cartService.addItem(variantId, customerId, stock);

    assertNotNull(result);
    assertEquals(stock, result.getQuantity());
  }

  @Test
  public void testAddItemWithInvalidCustomer() {
    // Add item to an invalid customer
    // Arrange
    String variantId = "variant1";
    String customerId = "badCustomer";
    ClothingVariant variant = buildVariant(variantId, 100f, 10);
    when(clothingVariantRepository.findByClothingVariantIDAndArchivedFalse(variantId))
        .thenReturn(variant);
    when(customerRepository.findByRoleID(customerId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> cartService.addItem(variantId, customerId, 1));
    assertEquals(
        "404 NOT_FOUND \"There is no customer with id " + customerId + ".\"", e.getMessage());
  }

  @Test
  public void testAddItemWithInvalidVariant() {
    // Add item of an invalid clothing variant
    // Arrange
    String variantId = "badVariant";
    String customerId = "customer1";
    Customer customer = new Customer();
    when(clothingVariantRepository.findByClothingVariantIDAndArchivedFalse(variantId))
        .thenReturn(null);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> cartService.addItem(variantId, customerId, 1));
    assertEquals(
        "404 NOT_FOUND \"There is no clothing variant with id " + variantId + ".\"",
        e.getMessage());
  }

  @Test
  public void testAddItemWithExcessiveQuantity() {
    // Add item with quantity exceed stock
    // Arrange
    String variantId = "variant1";
    String customerId = "customer1";
    int stock = 3;
    int quantity = 10;
    ClothingVariant variant = buildVariant(variantId, 100f, stock);
    Customer customer = new Customer();
    when(clothingVariantRepository.findByClothingVariantIDAndArchivedFalse(variantId))
        .thenReturn(variant);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> cartService.addItem(variantId, customerId, quantity));
    assertEquals(
        "400 BAD_REQUEST \"The quantity "
            + quantity
            + " is higher than the available stock for this clothing piece ("
            + stock
            + ").\"",
        e.getMessage());
  }

  @Test
  public void testAddItemWithZeroQuantity() {
    // Add item with 0 quantity
    String variantId = "variant1";
    String customerId = "customer1";
    ClothingVariant variant = buildVariant(variantId, 50f, 5);
    Customer customer = new Customer();
    when(clothingVariantRepository.findByClothingVariantIDAndArchivedFalse(variantId))
        .thenReturn(variant);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> cartService.addItem(variantId, customerId, 0));

    assertEquals("400 BAD_REQUEST \"The quantity must be greater than zero.\"", e.getMessage());
  }

  // ===== removeItem =====

  @Test
  public void testRemoveItem() {
    // Remove valid item
    // Arrange
    String itemId = "item1";
    String customerId = "customer1";
    ClothingVariant variant = buildVariant("variant1", 50f, 10);
    Item item = buildItem(itemId, variant, 2);
    Customer customer = new Customer();
    item.setCustomer(customer); // adds item to customer.items
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByItemID(itemId)).thenReturn(item);

    // Act
    cartService.removeItem(itemId, customerId);

    // Assert
    verify(itemRepository, times(1)).delete(item);
  }

  @Test
  public void testRemoveItemWithInvalidCustomer() {
    // remove item of invalid customer
    // Arrange
    String itemId = "item1";
    String customerId = "badCustomer";
    when(customerRepository.findByRoleID(customerId)).thenReturn(null);
    when(itemRepository.findByItemID(itemId)).thenReturn(new Item());

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> cartService.removeItem(itemId, customerId));
    assertEquals(
        "404 NOT_FOUND \"There is no customer with id " + customerId + ".\"", e.getMessage());
  }

  @Test
  public void testRemoveItemWithInvalidItem() {
    // Remove invalid item
    // Arrange
    String itemId = "badItem";
    String customerId = "customer1";
    Customer customer = new Customer();
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByItemID(itemId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> cartService.removeItem(itemId, customerId));
    assertEquals("404 NOT_FOUND \"There is no item with id " + itemId + ".\"", e.getMessage());
  }

  @Test
  public void testRemoveItemNotInCart() {
    // remove item belongs to a different customer
    String itemId = "item1";
    String customerId = "customer1";
    ClothingVariant variant = buildVariant("variant1", 50f, 10);
    Item item = buildItem(itemId, variant, 2); // not added to this customer
    Customer customer = new Customer();
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByItemID(itemId)).thenReturn(item);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> cartService.removeItem(itemId, customerId));
    assertEquals(
        "400 BAD_REQUEST \"The item with ID "
            + itemId
            + "is not in customer's "
            + customerId
            + "cart.\"",
        e.getMessage());
  }

  // ===== removeAllItems =====

  @Test
  public void testRemoveAllItemsEmptyCart() {
    // Remove all item with 0 item
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.deleteByCustomer(customer)).thenReturn(0);

    // Act
    int result = cartService.removeAllItems(customerId);

    // Assert
    verify(itemRepository, times(1)).deleteByCustomer(customer);
    assertEquals(0, result);
  }

  @Test
  public void testRemoveAllItemsWithOneItem() {
    // Remove all with one item
    // Arrange
    String customerId = "customer1";
    String itemId = "item1";
    ClothingVariant variant = buildVariant("variant1", 50f, 10);
    Item item = buildItem(itemId, variant, 2);
    Customer customer = new Customer();
    item.setCustomer(customer);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.deleteByCustomer(customer)).thenReturn(1);

    // Act
    int result = cartService.removeAllItems(customerId);

    // Assert
    verify(itemRepository, times(1)).deleteByCustomer(customer);
    assertEquals(1, result);
  }

  @Test
  public void testRemoveAllItemsWithMultipleItems() {
    // Remove all with multiple item
    // Arrange
    String customerId = "customer1";
    String itemId1 = "item1";
    String itemId2 = "item2";
    Customer customer = new Customer();
    ClothingVariant variant = buildVariant("variant1", 50f, 10);
    Item item1 = buildItem(itemId1, variant, 2);
    Item item2 = buildItem(itemId2, variant, 2);
    customer.addItem(item1);
    customer.addItem(item2);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.deleteByCustomer(customer)).thenReturn(2);

    // Act
    int result = cartService.removeAllItems(customerId);

    // Assert
    verify(itemRepository, times(1)).deleteByCustomer(customer);
    assertEquals(2, result);
  }

  @Test
  public void testRemoveAllItemsWithInvalidCustomer() {
    // Arrange
    String customerId = "badCustomer";
    when(customerRepository.findByRoleID(customerId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(ResponseStatusException.class, () -> cartService.removeAllItems(customerId));
    assertEquals(
        "404 NOT_FOUND \"There is no customer with id " + customerId + ".\"", e.getMessage());
  }

  // ===== changeQuantity =====

  @Test
  public void testChangeQuantity() {
    // Update quantity of an item
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    String itemId = "item1";
    int newQuantity = 3;
    ClothingVariant variant = buildVariant("variant1", 50f, 10);
    Item item = buildItem(itemId, variant, 1);
    item.setCustomer(customer);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByItemID(itemId)).thenReturn(item);
    when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    Item result = cartService.changeQuantity(customerId, itemId, newQuantity);

    // Assert
    assertNotNull(result);
    assertEquals(newQuantity, result.getQuantity());
    verify(itemRepository, times(1)).save(any(Item.class));
  }

  @Test
  public void testChangeQuantityAtExactStock() {
    // update quantity of item to item's stock amount
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    String itemId = "item1";
    int stock = 7;
    ClothingVariant variant = buildVariant("variant1", 50f, stock);
    Item item = buildItem(itemId, variant, 1);
    item.setCustomer(customer);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByItemID(itemId)).thenReturn(item);
    when(itemRepository.save(any(Item.class))).thenAnswer(i -> i.getArgument(0));

    // Act
    Item result = cartService.changeQuantity(customerId, itemId, stock);

    // Assert
    assertEquals(stock, result.getQuantity());
  }

  @Test
  public void testChangeQuantityWithInvalidItem() {
    // update quantity with invalid item
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    String itemId = "badItem";
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByItemID(itemId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> cartService.changeQuantity(customerId, itemId, 1));
    assertEquals("404 NOT_FOUND \"There is no item with id " + itemId + ".\"", e.getMessage());
  }

  @Test
  public void testChangeQuantityWithInvalidCustomer() {
    // Update item stock with invalid customer
    // Arrange
    String customerId = "badCustomer";
    String itemId = "item1";
    int stock = 7;
    ClothingVariant variant = buildVariant("variant1", 50f, stock);
    Item item = buildItem(itemId, variant, 1);
    when(customerRepository.findByRoleID(customerId)).thenReturn(null);
    when(itemRepository.findByItemID(itemId)).thenReturn(item);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> cartService.changeQuantity(customerId, itemId, 1));
    assertEquals(
        "404 NOT_FOUND \"There is no customer with id " + customerId + ".\"", e.getMessage());
  }

  @Test
  public void testChangeQuantityWithWrongCustomer() {
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    String itemId = "item1";
    int stock = 7;
    ClothingVariant variant = buildVariant("variant1", 50f, stock);
    Item item = buildItem(itemId, variant, 1);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByItemID(itemId)).thenReturn(item);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> cartService.changeQuantity(customerId, itemId, 1));
    assertEquals(
        "400 BAD_REQUEST \"The item with id "
            + itemId
            + "is not part of customer's "
            + customerId
            + "cart.\"",
        e.getMessage());
  }

  @Test
  public void testChangeQuantityWithZeroQuantity() {
    // Update quantity to 0
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    String itemId = "item1";
    ClothingVariant variant = buildVariant("variant1", 50f, 10);
    Item item = buildItem(itemId, variant, 2);
    item.setCustomer(customer);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByItemID(itemId)).thenReturn(item);

    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class, () -> cartService.changeQuantity(customerId, itemId, 0));
    assertEquals(
        "400 BAD_REQUEST \"The new quantity has to be a positive number.\"", e.getMessage());
  }

  @Test
  public void testChangeQuantityWithNegativeQuantity() {
    // Update quantity to negative amount
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    String itemId = "item1";
    ClothingVariant variant = buildVariant("variant1", 50f, 10);
    Item item = buildItem(itemId, variant, 2);
    item.setCustomer(customer);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByItemID(itemId)).thenReturn(item);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> cartService.changeQuantity(customerId, itemId, -5));
    assertEquals(
        "400 BAD_REQUEST \"The new quantity has to be a positive number.\"", e.getMessage());
  }

  @Test
  public void testChangeQuantityExceedingStock() {
    // update item with quantity exceed stock
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    String itemId = "item1";
    int stock = 5;
    int newQuantity = 10;
    ClothingVariant variant = buildVariant("variant1", 50f, stock);
    Item item = buildItem(itemId, variant, 2);
    item.setCustomer(customer);
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByItemID(itemId)).thenReturn(item);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(
            ResponseStatusException.class,
            () -> cartService.changeQuantity(customerId, itemId, newQuantity));
    assertEquals(
        "400 BAD_REQUEST \"The quantity "
            + newQuantity
            + "is higher than the available stock for this clothing piece ("
            + stock
            + ").\"",
        e.getMessage());
  }

  // ===== getCartTotal =====

  @Test
  public void testGetCartTotal() {
    // Total = sum of (price * quantity) per item
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    ClothingVariant variant = buildVariant("variant1", 50f, 10);
    Item item1 = buildItem("item1", variant, 2); // 50 * 2 = 100
    Item item2 = buildItem("item2", variant, 3); // 50 * 3 = 150
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByCustomer(customer)).thenReturn(List.of(item1, item2));

    // Act
    float total = cartService.getCartTotal(customerId);

    // Assert
    assertEquals(250f, total);
  }

  @Test
  public void testGetCartTotalWithDifferentPricesAndQuantities() {
    // Calculate total with different price and quantity
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    ClothingVariant variant1 = buildVariant("variant1", 30f, 10);
    ClothingVariant variant2 = buildVariant("variant2", 70f, 10);
    Item item1 = buildItem("item1", variant1, 4); // 30 * 4 = 120
    Item item2 = buildItem("item2", variant2, 1); // 70 * 1 = 70
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByCustomer(customer)).thenReturn(List.of(item1, item2));

    // Act
    float total = cartService.getCartTotal(customerId);

    // Assert
    assertEquals(190f, total);
  }

  @Test
  public void testGetCartTotalEmptyCart() {
    // Arrange
    String customerId = "customer1";
    Customer customer = new Customer();
    when(customerRepository.findByRoleID(customerId)).thenReturn(customer);
    when(itemRepository.findByCustomer(customer)).thenReturn(List.of());

    // Act
    float total = cartService.getCartTotal(customerId);

    // Assert
    assertEquals(0f, total);
  }

  @Test
  public void testGetCartTotalWithInvalidCustomer() {
    // Arrange
    String customerId = "badCustomer";
    when(customerRepository.findByRoleID(customerId)).thenReturn(null);

    // Act & Assert
    ResponseStatusException e =
        assertThrows(ResponseStatusException.class, () -> cartService.getCartTotal(customerId));
    assertEquals(
        "404 NOT_FOUND \"There is no customer with id " + customerId + ".\"", e.getMessage());
  }
}
