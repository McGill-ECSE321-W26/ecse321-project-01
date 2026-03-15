package ca.mcgill.ecse321.group1.dto;

public class ClothingVariantUpdateRequestDto {

  private int stockQuantity;

  public ClothingVariantUpdateRequestDto() {}

  public ClothingVariantUpdateRequestDto(int stockQuantity) {
    this.stockQuantity = stockQuantity;
  }

  public int getStockQuantity() {
    return stockQuantity;
  }

  public void setStockQuantity(int stockQuantity) {
    this.stockQuantity = stockQuantity;
  }
}
