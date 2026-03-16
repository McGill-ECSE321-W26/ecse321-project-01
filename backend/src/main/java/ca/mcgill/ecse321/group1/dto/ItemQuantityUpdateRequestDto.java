package ca.mcgill.ecse321.group1.dto;

public class ItemQuantityUpdateRequestDto {
  private int quantity;

  public int getQuantity() {
    return this.quantity;
  }

  public void setQuantity(int newQuantity) {
    this.quantity = newQuantity;
  }
}
