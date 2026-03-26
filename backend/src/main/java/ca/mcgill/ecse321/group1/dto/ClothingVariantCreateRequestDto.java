package ca.mcgill.ecse321.group1.dto;

import ca.mcgill.ecse321.group1.model.ClothingVariant;

public class ClothingVariantCreateRequestDto {

  private ClothingVariant.Size size;
  private String color;
  private String imagePath;
  private int stockQuantity;

  public ClothingVariantCreateRequestDto() {}

  public ClothingVariantCreateRequestDto(
      ClothingVariant.Size size, String color, String imagePath, int stockQuantity) {
    this.size = size;
    this.color = color;
    this.imagePath = imagePath;
    this.stockQuantity = stockQuantity;
  }

  public ClothingVariant.Size getSize() {
    return size;
  }

  public void setSize(ClothingVariant.Size size) {
    this.size = size;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public int getStockQuantity() {
    return stockQuantity;
  }

  public void setStockQuantity(int stockQuantity) {
    this.stockQuantity = stockQuantity;
  }
}
