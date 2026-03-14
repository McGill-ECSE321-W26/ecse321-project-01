package ca.mcgill.ecse321.group1.service;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import ca.mcgill.ecse321.group1.model.ClothingVariant;
import ca.mcgill.ecse321.group1.repository.ClothingModelRepository;
import ca.mcgill.ecse321.group1.repository.ClothingVariantRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClothingService {

  private final ClothingModelRepository clothingModelRepository;
  private final ClothingVariantRepository clothingVariantRepository;

  public ClothingService(
      ClothingModelRepository clothingModelRepository,
      ClothingVariantRepository clothingVariantRepository) {
    this.clothingModelRepository = clothingModelRepository;
    this.clothingVariantRepository = clothingVariantRepository;
  }

  @Transactional(readOnly = true)
  public List<ClothingModel> getAllClothingModels() {
    return clothingModelRepository.findAll();
  }

  @Transactional(readOnly = true)
  public ClothingModel getClothingModel(String modelId) throws ResponseStatusException {
    ClothingModel model = clothingModelRepository.findByClothingModelID(modelId);
    if (model == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("Clothing model with ID %s not found", modelId));
    }
    return model;
  }

  @Transactional
  public ClothingModel createClothingModel(String name, float price)
      throws ResponseStatusException {
    if (name == null || name.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name must not be blank");
    }
    if (price <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be greater than 0");
    }

    // Leave ID field as null so CRUD repository can fill with UUID
    ClothingModel model = new ClothingModel(null, name, price);
    return clothingModelRepository.save(model);
  }

  @Transactional
  public ClothingModel updateClothingModel(String modelId, String name, float price) {
    ClothingModel model = getClothingModel(modelId);
    if (name == null || name.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name must not be blank");
    }
    if (price <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be > 0");
    }

    model.setName(name);
    model.setPrice(price);
    return clothingModelRepository.save(model);
  }

  @Transactional
  public void deleteClothingModel(String modelId) throws ResponseStatusException {
    int deletedCount = clothingModelRepository.deleteByClothingModelID(modelId);
    if (deletedCount == 0) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("Clothing model with ID %s not found", modelId));
    }
  }

  @Transactional(readOnly = true)
  public ClothingVariant getVariant(String variantId) throws ResponseStatusException {
    ClothingVariant variant = clothingVariantRepository.findByClothingVariantID(variantId);
    if (variant == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("Clothing variant with ID %s not found", variantId));
    }
    return variant;
  }

  @Transactional(readOnly = true)
  public List<ClothingVariant> getVariantsByModel(String modelId) {
    ClothingModel model = getClothingModel(modelId);
    return model.getClothingVariants();
  }

  @Transactional
  public ClothingVariant createVariant(
      String modelId, ClothingVariant.Size size, String color, int stockQuantity)
      throws ResponseStatusException {
    ClothingModel model = getClothingModel(modelId);
    if (size == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size must be specified");
    }
    if (color == null || color.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Color must not be blank");
    }
    if (stockQuantity < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock quantity must be >= 0");
    }

    // No 2 variants of the same model can have the same color/size combination
    if (model.getClothingVariants().stream()
        .anyMatch(variant -> variant.getSize() == size && variant.getColor().equals(color))) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          String.format(
              "A variant with size %s and color %s already exists for this clothing model",
              size, color));
    }

    // Leave ID field as null so CRUD repository can fill with UUID
    ClothingVariant variant = new ClothingVariant(null, size, color, stockQuantity, model);
    return clothingVariantRepository.save(variant);
  }

  @Transactional
  public ClothingVariant updateVariant(String variantId, int stockQuantity)
      throws ResponseStatusException {
    ClothingVariant variant = getVariant(variantId);
    if (stockQuantity < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock quantity must be >= 0");
    }

    variant.setStockQuantity(stockQuantity);
    return clothingVariantRepository.save(variant);
  }

  @Transactional
  public void deleteVariant(String variantId) throws ResponseStatusException {
    int deleteCount = clothingVariantRepository.deleteByClothingVariantID(variantId);
    if (deleteCount == 0) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("Clothing variant with ID %s not found", variantId));
    }
  }
}
