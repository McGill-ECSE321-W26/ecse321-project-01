package ca.mcgill.ecse321.group1.dto;

public class CartTotalDto {
  private float cartTotal;

  @SuppressWarnings("unused")
  private CartTotalDto() {}

  public CartTotalDto(float total) {
    this.cartTotal = total;
  }

  public float getCartTotal() {
    return this.cartTotal;
  }

  public void setCartTotal(float total) {
    this.cartTotal = total;
  }
}
