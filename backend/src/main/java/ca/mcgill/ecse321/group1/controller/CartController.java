package ca.mcgill.ecse321.group1.controller;

import ca.mcgill.ecse321.group1.dto.AddItemDTO;
import ca.mcgill.ecse321.group1.dto.CartTotalDTO;
import ca.mcgill.ecse321.group1.dto.ItemDTO;
import ca.mcgill.ecse321.group1.dto.UpdateItemQuantityDTO;
import ca.mcgill.ecse321.group1.model.Item;
import ca.mcgill.ecse321.group1.service.CartService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/carts")
@RestController
public class CartController {
  private CartService cartService;

  public CartController(CartService cartService) {
    this.cartService = cartService;
  }

  @GetMapping("/{customerID}/total")
  public CartTotalDTO getCartTotal(@PathVariable String customerID) {
    float total = cartService.getCartTotal(customerID);
    return new CartTotalDTO(total);
  }

  @PostMapping("/{customerID}/items")
  @ResponseStatus(HttpStatus.CREATED)
  public ItemDTO addItem(@PathVariable String customerID, @RequestBody AddItemDTO dto) {
    Item item = cartService.addItem(dto.getClothingVariantID(), customerID, dto.getQuantity());
    return new ItemDTO(item);
  }

  @GetMapping("/{customerID}/items")
  public List<ItemDTO> getCartItems(@PathVariable String customerID) {
    return cartService.getCartItems(customerID).stream().map(ItemDTO::new).toList();
  }

  @DeleteMapping("/{customerID}/items")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeAllItems(@PathVariable String customerID) {
    cartService.removeAllItems(customerID);
  }

  @DeleteMapping("/{customerID}/items/{itemID}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeItem(@PathVariable String customerID, @PathVariable String itemID) {
    cartService.removeItem(itemID, customerID);
  }

  @GetMapping("/{customerID}/items/{itemID}")
  public ItemDTO getItemByID(@PathVariable String itemID) {
    Item item = cartService.getItemByID(itemID);
    return new ItemDTO(item);
  }

  @PatchMapping("/{customerID}/items/{itemID}")
  public ItemDTO modifyQuantity(
      @PathVariable String itemID, @RequestBody UpdateItemQuantityDTO dto) {
    Item item = cartService.changeQuantity(itemID, dto.getQuantity());
    return new ItemDTO(item);
  }
}
