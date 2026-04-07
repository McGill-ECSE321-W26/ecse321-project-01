package ca.mcgill.ecse321.group1.service;

import ca.mcgill.ecse321.group1.model.ClothingModel;
import ca.mcgill.ecse321.group1.model.ClothingModel.Category;
import ca.mcgill.ecse321.group1.model.ClothingVariant;
import ca.mcgill.ecse321.group1.model.Item;
import ca.mcgill.ecse321.group1.repository.ClothingModelRepository;
import ca.mcgill.ecse321.group1.repository.ClothingVariantRepository;
import ca.mcgill.ecse321.group1.repository.ItemRepository;
import java.util.List;
import java.util.Set;
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
    ClothingModel model = clothingModelRepository.findByClothingModelIDAndArchivedFalse(modelId);
    if (model == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("Clothing model with ID %s not found", modelId));
    }
    return model;
  }

  private ClothingVariant findVariant(String modelId, String variantId) {
    ClothingVariant variant =
        clothingVariantRepository.findByClothingVariantIDAndArchivedFalse(variantId);
    if (variant == null || !variant.getModel().getClothingModelID().equals(modelId)) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND,
          String.format(
              "Clothing variant with ID %s not found under model %s", variantId, modelId));
    }
    return variant;
  }

  // Unfiltered lookups — include archived records (for manager restore/admin operations)
  private ClothingModel findModelIncludingArchived(String modelId) {
    ClothingModel model = clothingModelRepository.findByClothingModelID(modelId);
    if (model == null) {
      throw new ResponseStatusException(
          HttpStatus.NOT_FOUND, String.format("Clothing model with ID %s not found", modelId));
    }
    return model;
  }

  private ClothingVariant findVariantIncludingArchived(String modelId, String variantId) {
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

  private void validateBrand(String brand) {
    if (brand == null || brand.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Brand must not be blank");
    }
  }

  // Checks name uniqueness across ALL models (archived or not), optionally excluding a model ID
  // (used during updates/restores to allow keeping the same name)
  private void validateNameUniqueness(String name, String excludeModelId) {
    ClothingModel existing = clothingModelRepository.findByName(name);
    if (existing != null
        && (excludeModelId == null || !existing.getClothingModelID().equals(excludeModelId))) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          String.format("A clothing model with name '%s' already exists", name));
    }
  }

  private void validateVariantFields(
      ClothingVariant.Size size, String color, int stockQuantity, String imagePath) {
    if (size == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size must be specified");
    }
    if (color == null || color.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Color must not be blank");
    }
    if (!color.matches("^#[0-9A-Fa-f]{6}$")) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST, "Color must be a valid hex color (e.g. #FF5733)");
    }
    validateStockQuantity(stockQuantity);
    validateImagePath(imagePath);
  }

  private void validateStockQuantity(int stockQuantity) {
    if (stockQuantity < 0) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Stock quantity must be >= 0");
    }
  }

  private static final java.util.Set<String> ALLOWED_IMAGE_EXTENSIONS =
      Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp", ".svg");

  private void validateImagePath(String imagePath) {
    if (imagePath == null || imagePath.isBlank()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Image path must not be blank");
    }
    String lower = imagePath.toLowerCase();
    boolean valid = ALLOWED_IMAGE_EXTENSIONS.stream().anyMatch(lower::endsWith);
    if (!valid) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          "Image path must end with a valid image extension (.jpg, .jpeg, .png, .gif, .webp, .svg)");
    }
  }

  // Checks size+color uniqueness across ALL variants (archived or not), optionally excluding a
  // variant ID (used during restores to avoid a self-conflict)
  private void validateVariantUniqueness(
      ClothingModel model, ClothingVariant.Size size, String color, String excludeVariantId) {
    if (model.getClothingVariants().stream()
        .anyMatch(
            variant ->
                (excludeVariantId == null
                        || !variant.getClothingVariantID().equals(excludeVariantId))
                    && variant.getSize() == size
                    && variant.getColor().equals(color))) {
      throw new ResponseStatusException(
          HttpStatus.CONFLICT,
          String.format(
              "A variant with size %s and color %s already exists for this clothing model",
              size, color));
    }
  }

  private void validateVariantUniqueness(
      ClothingModel model, ClothingVariant.Size size, String color) {
    validateVariantUniqueness(model, size, color, null);
  }

  // Propagates a model price change to all items sitting in customer carts (not yet ordered)
  private void updateCartItemPrices(String modelId, float price) {
    List<Item> cartItems =
        itemRepository.findByClothingVariant_Model_ClothingModelIDAndOrderIsNull(modelId);
    cartItems.forEach(item -> item.setPrice(price));
    itemRepository.saveAll(cartItems);
  }

  @Transactional(readOnly = true)
  public List<ClothingModel> getAllClothingModels() {
    return clothingModelRepository.findByArchivedFalse();
  }

  @Transactional(readOnly = true)
  public ClothingModel getClothingModel(String modelId) throws ResponseStatusException {
    return findModel(modelId);
  }

  @Transactional
  public ClothingModel createClothingModel(
      String name, String description, String brand, Category category, float price)
      throws ResponseStatusException {
    validateName(name);
    validateBrand(brand);
    validatePrice(price);
    validateNameUniqueness(name, null);

    ClothingModel model = new ClothingModel(null, name, description, brand, category, price);
    return clothingModelRepository.save(model);
  }

  @Transactional
  public ClothingModel updateClothingModel(
      String modelId,
      String name,
      String description,
      String brand,
      Category category,
      float price) {
    ClothingModel model = findModel(modelId);
    validateName(name);
    validateBrand(brand);
    validatePrice(price);

    // Only validate uniqueness and update if the name actually changed
    if (!name.equals(model.getName())) {
      validateNameUniqueness(name, modelId);
      model.setName(name);
    }

    // Sync price across existing cart items when the model price changes
    if (price != model.getPrice()) {
      model.setPrice(price);
      updateCartItemPrices(modelId, price);
    }

    model.setDescription(description);
    model.setBrand(brand);
    model.setCategory(category);

    clothingModelRepository.save(model);
    return model;
  }

  @Transactional
  public void deleteClothingModel(String modelId) throws ResponseStatusException {
    ClothingModel model = findModel(modelId);
    // Archive model and all its variants
    model.setArchived(true);
    for (ClothingVariant variant : model.getClothingVariants()) {
      variant.setArchived(true);
    }
    // Must delete call Items that are in cart (not in order, since it was already checked out and
    // paid for)
    List<Item> cartItems =
        itemRepository.findByClothingVariant_Model_ClothingModelIDAndOrderIsNull(modelId);
    itemRepository.deleteAll(cartItems);
    clothingModelRepository.save(model);
  }

  @Transactional(readOnly = true)
  public ClothingVariant getVariant(String modelId, String variantId)
      throws ResponseStatusException {
    return findVariant(modelId, variantId);
  }

  @Transactional(readOnly = true)
  public List<ClothingVariant> getVariantsByModel(String modelId) {
    ClothingModel model = findModel(modelId);
    return model.getClothingVariants().stream().filter(v -> !v.getArchived()).toList();
  }

  @Transactional
  public ClothingVariant createVariant(
      String modelId, ClothingVariant.Size size, String color, String imagePath, int stockQuantity)
      throws ResponseStatusException {
    ClothingModel model = findModel(modelId);
    validateVariantFields(size, color, stockQuantity, imagePath);
    validateVariantUniqueness(model, size, color);

    ClothingVariant variant =
        new ClothingVariant(null, size, color, imagePath, stockQuantity, model);
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
    ClothingVariant variant = findVariant(modelId, variantId);
    variant.setArchived(true);
    // Must delete call Items that are in cart (not in order, since it was already checked out and
    // paid for)
    List<Item> cartItems = itemRepository.findByClothingVariantAndOrderIsNull(variant);
    itemRepository.deleteAll(cartItems);
    clothingVariantRepository.save(variant);
  }

  // ── Manager-only operations (archived + non-archived) ───────────────────────

  @Transactional(readOnly = true)
  public List<ClothingModel> getAllClothingModelsIncludingArchived() {
    return clothingModelRepository.findAll();
  }

  @Transactional(readOnly = true)
  public List<ClothingVariant> getAllVariantsByModelIncludingArchived(String modelId) {
    ClothingModel model = findModelIncludingArchived(modelId);
    return model.getClothingVariants().stream().toList();
  }

  @Transactional
  public ClothingModel restoreClothingModel(String modelId) throws ResponseStatusException {
    ClothingModel model = findModelIncludingArchived(modelId);
    if (!model.getArchived()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          String.format("Clothing model with ID %s is not archived", modelId));
    }
    validateNameUniqueness(model.getName(), modelId);
    model.setArchived(false);
    return clothingModelRepository.save(model);
  }

  @Transactional
  public ClothingVariant restoreVariant(String modelId, String variantId)
      throws ResponseStatusException {
    ClothingVariant variant = findVariantIncludingArchived(modelId, variantId);
    if (!variant.getArchived()) {
      throw new ResponseStatusException(
          HttpStatus.BAD_REQUEST,
          String.format("Clothing variant with ID %s is not archived", variantId));
    }
    validateVariantUniqueness(variant.getModel(), variant.getSize(), variant.getColor(), variantId);
    variant.setArchived(false);
    return clothingVariantRepository.save(variant);
  }
}
