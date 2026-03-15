package ca.mcgill.ecse321.group1.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.group1.dto.AddItemDTO;
import ca.mcgill.ecse321.group1.dto.CartTotalDTO;
import ca.mcgill.ecse321.group1.dto.ItemDTO;
import ca.mcgill.ecse321.group1.dto.UpdateItemQuantityDTO;
import ca.mcgill.ecse321.group1.model.Item;
import ca.mcgill.ecse321.group1.service.CartService;

@RequestMapping("api/cart")
@RestController
public class CartController {
    @Autowired
    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("{customerID}/total")
    public CartTotalDTO getCartTotal(@PathVariable String customerID) {
        float total = cartService.getCartTotal(customerID);
        return new CartTotalDTO(total);
    }

    @PostMapping("{customerID}/add")
    public ItemDTO addItem(@PathVariable String customerID, @RequestBody AddItemDTO dto) {
        Item item = cartService.addItem(dto.getClothingVariantID(), customerID, dto.getQuantity());
        return new ItemDTO(item);
    }

    @GetMapping("{customerID}")
    public List<ItemDTO> getCartItems(@PathVariable String customerID) {
        List<Item> items = cartService.getCartItems(customerID);

        // Convert List<Item> into List<ItemDTO>
        List<ItemDTO> itemsDTOs = new ArrayList<>();
        for (Item item : items) {
            itemsDTOs.add(new ItemDTO(item));
        }

        return itemsDTOs;
    }

    @DeleteMapping("{customerID}/remove")
    public void removeAllItems(@PathVariable String customerID) {
        cartService.removeAllItems(customerID);
    }

    @DeleteMapping("{customerID}/remove/{itemID}")
    public void removeItem(@PathVariable String customerID, @PathVariable String itemID) {
        cartService.removeItem(itemID, customerID);
    }

    @GetMapping("{itemID}")
    public ItemDTO getItemByID(@PathVariable String itemID) {
        Item item = cartService.getItemByID(itemID);
        return new ItemDTO(item);
    }

    @PutMapping("{itemID}/modify-quantity")
    public ItemDTO modifyQuantity(@PathVariable String itemID, @RequestBody UpdateItemQuantityDTO dto) {
        Item item = cartService.changeQuantity(itemID, dto.getQuantity());
        return new ItemDTO(item);
    }
}
