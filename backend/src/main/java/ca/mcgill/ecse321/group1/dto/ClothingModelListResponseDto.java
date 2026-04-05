package ca.mcgill.ecse321.group1.dto;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import ca.mcgill.ecse321.group1.model.ClothingVariant;
import java.util.List;
import java.util.stream.Collectors;

public class ClothingModelListResponseDto {

  private String clothingModelID;
  private String name;
  private String description;
  private String brand;
  private ClothingModel.Category category;
  private float price;
  private int totalStockQuantity;
  private List<VariantSummaryDto> variants;

  public ClothingModelListResponseDto() {}

  public ClothingModelListResponseDto(ClothingModel model) {
    this.clothingModelID = model.getClothingModelID();
    this.name = model.getName();
    this.description = model.getDescription();
    this.brand = model.getBrand();
    this.category = model.getCategory();
    this.price = model.getPrice();
    this.totalStockQuantity =
        model.getClothingVariants().stream()
            .filter(v -> !v.getArchived())
            .mapToInt(ClothingVariant::getStockQuantity)
            .sum();
    // We want to get a list of variants, but only their image and color which will be used in the
    // main clothing shop page. It should not return variants with duplicate colors, as only 1 image
    // is needed per unique color, so we perform filtering using Collectors.toMap
    this.variants =
        model.getClothingVariants().stream()
            .filter(v -> !v.getArchived())
            .collect(
                Collectors.toMap(
                    ClothingVariant::getColor, // key: color
                    v -> v, // value: variant
                    (v1, v2) -> v1)) // merge function: keep 1st
            .values()
            .stream()
            .map(v -> new VariantSummaryDto(v.getImagePath(), v.getColor()))
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

  public List<VariantSummaryDto> getVariants() {
    return variants;
  }

  public void setVariants(List<VariantSummaryDto> variants) {
    this.variants = variants;
  }

  public static class VariantSummaryDto {
    private String imagePath;
    private String color;

    public VariantSummaryDto() {}

    public VariantSummaryDto(String imagePath, String color) {
      this.imagePath = imagePath;
      this.color = color;
    }

    public String getImagePath() {
      return imagePath;
    }

    public void setImagePath(String imagePath) {
      this.imagePath = imagePath;
    }

    public String getColor() {
      return color;
    }

    public void setColor(String color) {
      this.color = color;
    }
  }
}
