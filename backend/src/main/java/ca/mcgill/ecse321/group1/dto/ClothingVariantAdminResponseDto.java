package ca.mcgill.ecse321.group1.dto;

import ca.mcgill.ecse321.group1.model.ClothingVariant;

public class ClothingVariantAdminResponseDto {

  private String clothingVariantID;
  private ClothingVariant.Size size;
  private String color;
  private String imagePath;
  private int stockQuantity;
  private String modelId;
  private boolean archived;

  public ClothingVariantAdminResponseDto() {}

  public ClothingVariantAdminResponseDto(ClothingVariant variant) {
    this.clothingVariantID = variant.getClothingVariantID();
    this.size = variant.getSize();
    this.color = variant.getColor();
    this.imagePath = variant.getImagePath();
    this.stockQuantity = variant.getStockQuantity();
    this.modelId = variant.getModel().getClothingModelID();
    this.archived = variant.getArchived();
  }

  public String getClothingVariantID() {
    return clothingVariantID;
  }

  public void setClothingVariantID(String clothingVariantID) {
    this.clothingVariantID = clothingVariantID;
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

  public String getModelId() {
    return modelId;
  }

  public void setModelId(String modelId) {
    this.modelId = modelId;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }
}
