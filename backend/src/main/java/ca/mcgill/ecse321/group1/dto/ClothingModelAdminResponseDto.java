package ca.mcgill.ecse321.group1.dto;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import ca.mcgill.ecse321.group1.model.ClothingVariant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClothingModelAdminResponseDto {

  private String clothingModelID;
  private String name;
  private String description;
  private String brand;
  private ClothingModel.Category category;
  private float price;
  private int totalStockQuantity;
  private List<ClothingModelListResponseDto.VariantSummaryDto> variants;
  private boolean archived;

  public ClothingModelAdminResponseDto() {}

  public ClothingModelAdminResponseDto(ClothingModel model) {
    this.clothingModelID = model.getClothingModelID();
    this.name = model.getName();
    this.description = model.getDescription();
    this.brand = model.getBrand();
    this.category = model.getCategory();
    this.price = model.getPrice();
    this.archived = model.getArchived();
    this.totalStockQuantity =
        model.getClothingVariants().stream()
            .mapToInt(ClothingVariant::getStockQuantity)
            .sum();
    Set<String> seenColors = new HashSet<>();
    this.variants =
        model.getClothingVariants().stream()
            .filter(v -> seenColors.add(v.getColor()))
            .map(v -> new ClothingModelListResponseDto.VariantSummaryDto(v.getImagePath(), v.getColor()))
            .toList();
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public ClothingModel.Category getCategory() {
    return category;
  }

  public void setCategory(ClothingModel.Category category) {
    this.category = category;
  }

  public float getPrice() {
    return price;
  }

  public void setPrice(float price) {
    this.price = price;
  }

  public int getTotalStockQuantity() {
    return totalStockQuantity;
  }

  public void setTotalStockQuantity(int totalStockQuantity) {
    this.totalStockQuantity = totalStockQuantity;
  }

  public List<ClothingModelListResponseDto.VariantSummaryDto> getVariants() {
    return variants;
  }

  public void setVariants(List<ClothingModelListResponseDto.VariantSummaryDto> variants) {
    this.variants = variants;
  }

  public boolean isArchived() {
    return archived;
  }

  public void setArchived(boolean archived) {
    this.archived = archived;
  }
}
