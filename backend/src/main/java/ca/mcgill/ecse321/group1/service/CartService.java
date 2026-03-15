package ca.mcgill.ecse321.group1.service;

import java.util.List;

import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.group1.exception.InvalidInputException;
import ca.mcgill.ecse321.group1.exception.NotFoundException;
import ca.mcgill.ecse321.group1.model.ClothingVariant;
import ca.mcgill.ecse321.group1.model.Customer;
import ca.mcgill.ecse321.group1.model.Item;
import ca.mcgill.ecse321.group1.repository.ClothingVariantRepository;
import ca.mcgill.ecse321.group1.repository.CustomerRepository;
import ca.mcgill.ecse321.group1.repository.ItemRepository;
import jakarta.transaction.Transactional;

@Service
public class CartService {
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;
    private final ClothingVariantRepository clothingVariantRepository;

    public CartService(
            CustomerRepository customerRepository, 
            ItemRepository itemRepository, 
            ClothingVariantRepository clothingVariantRepository) {
        this.customerRepository = customerRepository;
        this.itemRepository = itemRepository;
        this.clothingVariantRepository = clothingVariantRepository;
    }

    @Transactional
    public Item getItemByID(String itemID) {
        Item item = itemRepository.findItemByItemID(itemID);

        if (item == null) {
            throw new NotFoundException("There is no item with id " + itemID + ".");
        }

        return item;
    }

    @Transactional
    public List<Item> getCartItems(String customerID) {
        Customer customer = customerRepository.findByRoleID(customerID);
        if (customer == null) {
            throw new NotFoundException("There is no customer with id " + customerID + ".");
        }

        return itemRepository.findItemsByCustomer(customer);
    }

    @Transactional
    public Item addItem(String clothingVariantID, String customerID, int quantity) {
        ClothingVariant clothingVariant = clothingVariantRepository.findClothingVariantByClothingVariantID(clothingVariantID);
        Customer customer = customerRepository.findByRoleID(customerID);

        if (customer == null) {
            throw new NotFoundException("There is no customer with id " + customerID + ".");
        }

        if (clothingVariant == null) {
            throw new NotFoundException("There is no clothing variant with id " + clothingVariantID + ".");
        }

        if (quantity > clothingVariant.getStockQuantity()) {
            throw new InvalidInputException("The quantity " + quantity + "is higher than the available stock for this clothing piece (" + clothingVariant.getStockQuantity() + ").");
        }
        
        Item item = new Item();
        item.setQuantity(quantity);
        item.setPrice(clothingVariant.getModel().getPrice());
        item.setClothingVariant(clothingVariant);
        customer.addItem(item);

        return itemRepository.save(item);
    }

    @Transactional
    public void removeItem(String itemID, String customerID) {
        Item item = itemRepository.findItemByItemID(itemID);
        Customer customer = customerRepository.findByRoleID(customerID);

        if (customer == null) {
            throw new NotFoundException("There is no customer with id " + customerID + ".");
        }

        if (item == null) {
            throw new NotFoundException("There is no item with id " + itemID + ".");
        }

        if (!customer.getItems().contains(item)) {
            throw new InvalidInputException("The item with ID " + itemID + "is not in customer's " + customerID + "cart.");
        }

        customer.removeItem(item);

        itemRepository.deleteByItemID(itemID);
        item.delete();
    }

    @Transactional
    public void removeAllItems(String customerID) {
        Customer customer = customerRepository.findByRoleID(customerID);

        if (customer == null) {
            throw new NotFoundException("There is no customer with id " + customerID + ".");
        }

        List<Item> items = customer.getItems();

        if (items != null) {
            for (Item item : items) {
                customer.removeItem(item);
                itemRepository.deleteByItemID(item.getItemID());
                item.delete();
            }
        }
    }

    @Transactional
    public Item changeQuantity(String itemID, int newQuantity) {
        Item item = itemRepository.findItemByItemID(itemID);

        if (item == null) {
            throw new NotFoundException("There is no item with id " + itemID + ".");
        }

        // I assume that we can only change quantity to 1 <= newQuantity <= variantStockQuantity
        // If we want to be able to change quantity to 0, then it would be a special case where 
        if (newQuantity < 1) {
            throw new InvalidInputException("The new quantity has to be a positive number.");
        }
        if (newQuantity > item.getClothingVariant().getStockQuantity()) {
            throw new InvalidInputException("The quantity " + newQuantity + "is higher than the available stock for this clothing piece (" + item.getClothingVariant().getStockQuantity() + ").");
        }

        item.setQuantity(newQuantity);
        itemRepository.save(item); // save new quantity in DB
        return item;
    }

    @Transactional
    public float getCartTotal(String customerID) {
        List<Item> items = getCartItems(customerID);
        float total = 0.00f;

        if (!items.isEmpty()) {
            for (Item item : items) {
                total += item.getPrice() * item.getQuantity();
            }
        }

        return total;
    }
}
