package ca.mcgill.ecse321.group1.dto;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import ca.mcgill.ecse321.group1.model.ClothingVariant;

public class ClothingModelResponseDto {

  private String clothingModelID;
  private String name;
  private float price;
  private String imagePath;
  private int totalStockQuantity;

  public ClothingModelResponseDto() {}

  public ClothingModelResponseDto(ClothingModel model) {
    this.clothingModelID = model.getClothingModelID();
    this.name = model.getName();
    this.price = model.getPrice();
    this.imagePath = model.getImagePath();
    this.totalStockQuantity =
        model.getClothingVariants().stream()
            .filter(v -> !v.getArchived())
            .mapToInt(ClothingVariant::getStockQuantity)
            .sum();
  }

  public String getClothingModelID() {
    return clothingModelID;
  }

  public void setClothingModelID(String clothingModelID) {
    this.clothingModelID = clothingModelID;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public float getPrice() {
    return price;
  }

  public void setPrice(float price) {
    this.price = price;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public int getTotalStockQuantity() {
    return totalStockQuantity;
  }

  public void setTotalStockQuantity(int totalStockQuantity) {
    this.totalStockQuantity = totalStockQuantity;
  }
}
