package ca.mcgill.ecse321.group1.dto;

public class AddItemDTO {
  private String clothingVariantID;
  private int quantity;

  public String getClothingVariantID() {
    return this.clothingVariantID;
  }

  public void setClothingVariantID(String clothingVariantID) {
    this.clothingVariantID = clothingVariantID;
  }

  public int getQuantity() {
    return this.quantity;
  }

  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }
}
