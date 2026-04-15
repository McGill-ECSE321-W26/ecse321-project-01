package ca.mcgill.ecse321.group1.controller;

import ca.mcgill.ecse321.group1.dto.CartTotalDto;
import ca.mcgill.ecse321.group1.dto.ItemCreateRequestDto;
import ca.mcgill.ecse321.group1.dto.ItemQuantityUpdateRequestDto;
import ca.mcgill.ecse321.group1.dto.ItemResponseDto;
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
  private final CartService cartService;

  public CartController(CartService cartService) {
    this.cartService = cartService;
  }

  @GetMapping("/{customerID}")
  public CartTotalDto getCartTotal(@PathVariable String customerID) {
    float total = cartService.getCartTotal(customerID);
    return new CartTotalDto(total);
  }

  @PostMapping("/{customerID}/items")
  @ResponseStatus(HttpStatus.CREATED)
  public ItemResponseDto addItem(
      @PathVariable String customerID, @RequestBody ItemCreateRequestDto dto) {
    Item item = cartService.addItem(dto.getClothingVariantID(), customerID, dto.getQuantity());
    return new ItemResponseDto(item);
  }

  @GetMapping("/{customerID}/items")
  public List<ItemResponseDto> getCartItems(@PathVariable String customerID) {
    return cartService.getCartItems(customerID).stream().map(ItemResponseDto::new).toList();
  }

  @DeleteMapping("/{customerID}/items/{itemID}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeItem(@PathVariable String customerID, @PathVariable String itemID) {
    cartService.removeItem(itemID, customerID);
  }

  @PatchMapping("/{customerID}/items/{itemID}")
  public ItemResponseDto modifyQuantity(
      @PathVariable String customerID,
      @PathVariable String itemID,
      @RequestBody ItemQuantityUpdateRequestDto dto) {
    Item item = cartService.changeQuantity(customerID, itemID, dto.getQuantity());
    return new ItemResponseDto(item);
  }
}
