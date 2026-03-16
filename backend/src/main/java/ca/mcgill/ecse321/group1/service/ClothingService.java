package ca.mcgill.ecse321.group1.service;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import ca.mcgill.ecse321.group1.model.ClothingVariant;
import ca.mcgill.ecse321.group1.model.Item;
import ca.mcgill.ecse321.group1.repository.ClothingModelRepository;
import ca.mcgill.ecse321.group1.repository.ClothingVariantRepository;
import ca.mcgill.ecse321.group1.repository.ItemRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClothingService {

  private final ClothingModelRepository clothingModelRepository;
  private final ClothingVariantRepository clothingVariantRepository;
  private final ItemRepository itemRepository;

  public ClothingService(
      ClothingModelRepository clothingModelRepository,
      ClothingVariantRepository clothingVariantRepository,
      ItemRepository itemRepository) {
    this.clothingModelRepository = clothingModelRepository;
    this.clothingVariantRepository = clothingVariantRepository;
    this.itemRepository = itemRepository;
  }

  private ClothingModel findModel(String modelId) {
    ClothingModel model = clothingModelRepository.findByClothingModelID(modelId);
    if (model == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("Clothing model with ID %s not found", modelId));
    }
    return model;
  }

  private ClothingVariant findVariant(String modelId, String variantId) {
    ClothingVariant variant = clothingVariantRepository.findByClothingVariantID(variantId);
    if (variant == null || !variant.getModel().getClothingModelID().equals(modelId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format(
              "Clothing variant with ID %s not found under model %s", variantId, modelId));
    }
    return variant;
  }

  private void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name must not be blank");
    }
  }

  private void validatePrice(float price) {
    if (price <= 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be > 0");
    }
  }

  private void validateNameUniqueness(String name, String excludeModelId) {
    ClothingModel existing = clothingModelRepository.findByName(name);
    if (existing != null
        && (excludeModelId == null || !existing.getClothingModelID().equals(excludeModelId))) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          String.format("A clothing model with name '%s' already exists", name));
    }
  }

  private void validateVariantFields(ClothingVariant.Size size, String color, int stockQuantity) {
    if (size == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size must be specified");
    }
    if (color == null || color.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Color must not be blank");
    }
    if (stockQuantity < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock quantity must be >= 0");
    }
  }

  private void validateStockQuantity(int stockQuantity) {
    if (stockQuantity < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock quantity must be >= 0");
    }
  }

  private void validateVariantUniqueness(
      ClothingModel model, ClothingVariant.Size size, String color) {
    if (model.getClothingVariants().stream()
        .anyMatch(variant -> variant.getSize() == size && variant.getColor().equals(color))) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          String.format(
              "A variant with size %s and color %s already exists for this clothing model",
              size, color));
    }
  }

  private void updateCartItemPrices(String modelId, float price) {
    List<Item> cartItems =
        itemRepository.findByClothingVariant_Model_ClothingModelIDAndOrderIsNull(modelId);
    cartItems.forEach(item -> item.setPrice(price));
    itemRepository.saveAll(cartItems);
  }

  @Transactional(readOnly = true)
  public List<ClothingModel> getAllClothingModels() {
    return clothingModelRepository.findAll();
  }

  @Transactional(readOnly = true)
  public ClothingModel getClothingModel(String modelId) throws ResponseStatusException {
    return findModel(modelId);
  }

  @Transactional
  public ClothingModel createClothingModel(String name, float price)
      throws ResponseStatusException {
    validateName(name);
    validatePrice(price);
    validateNameUniqueness(name, null);

    ClothingModel model = new ClothingModel(null, name, price);
    return clothingModelRepository.save(model);
  }

  @Transactional
  public ClothingModel updateClothingModel(String modelId, String name, float price) {
    ClothingModel model = findModel(modelId);
    validateName(name);
    validatePrice(price);

    if (!name.equals(model.getName())) {
      validateNameUniqueness(name, modelId);
      model.setName(name);
    }

    if (price != model.getPrice()) {
      model.setPrice(price);
      updateCartItemPrices(modelId, price);
    }

    clothingModelRepository.save(model);
    return model;
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
  public ClothingVariant getVariant(String modelId, String variantId)
      throws ResponseStatusException {
    return findVariant(modelId, variantId);
  }

  @Transactional(readOnly = true)
  public List<ClothingVariant> getVariantsByModel(String modelId) {
    ClothingModel model = findModel(modelId);
    return model.getClothingVariants();
  }

  @Transactional
  public ClothingVariant createVariant(
      String modelId, ClothingVariant.Size size, String color, int stockQuantity)
      throws ResponseStatusException {
    ClothingModel model = findModel(modelId);
    validateVariantFields(size, color, stockQuantity);
    validateVariantUniqueness(model, size, color);

    ClothingVariant variant = new ClothingVariant(null, size, color, stockQuantity, model);
    return clothingVariantRepository.save(variant);
  }

  @Transactional
  public ClothingVariant updateVariantStock(String modelId, String variantId, int stockQuantity)
      throws ResponseStatusException {
    ClothingVariant variant = findVariant(modelId, variantId);
    validateStockQuantity(stockQuantity);

    variant.setStockQuantity(stockQuantity);
    return clothingVariantRepository.save(variant);
  }

  @Transactional
  public void deleteVariant(String modelId, String variantId) throws ResponseStatusException {
    int deletedCount = clothingVariantRepository.deleteByClothingVariantID(variantId);
    if (deletedCount == 0) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format(
              "Clothing variant with ID %s not found under model %s", variantId, modelId));
    }
  }
}
