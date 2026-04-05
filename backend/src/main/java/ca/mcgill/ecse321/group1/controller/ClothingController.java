package ca.mcgill.ecse321.group1.controller;

import ca.mcgill.ecse321.group1.dto.ClothingModelCreateRequestDto;
import ca.mcgill.ecse321.group1.dto.ClothingModelListResponseDto;
import ca.mcgill.ecse321.group1.dto.ClothingModelResponseDto;
import ca.mcgill.ecse321.group1.dto.ClothingVariantCreateRequestDto;
import ca.mcgill.ecse321.group1.dto.ClothingVariantResponseDto;
import ca.mcgill.ecse321.group1.dto.ClothingVariantUpdateRequestDto;
import ca.mcgill.ecse321.group1.service.ClothingService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clothing")
public class ClothingController {

  private final ClothingService clothingService;

  public ClothingController(ClothingService clothingService) {
    this.clothingService = clothingService;
  }

  @GetMapping
  public List<ClothingModelListResponseDto> getAllClothingModels() {
    return clothingService.getAllClothingModels().stream()
        .map(ClothingModelListResponseDto::new)
        .toList();
  }

  @GetMapping("/{modelId}")
  public ClothingModelResponseDto getClothingModel(@PathVariable String modelId) {
    return new ClothingModelResponseDto(clothingService.getClothingModel(modelId));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ClothingModelResponseDto createClothingModel(
      @RequestBody ClothingModelCreateRequestDto request) {
    return new ClothingModelResponseDto(
        clothingService.createClothingModel(
            request.getName(),
            request.getDescription(),
            request.getBrand(),
            request.getCategory(),
            request.getPrice()));
  }

  @PutMapping("/{modelId}")
  public ClothingModelResponseDto updateClothingModel(
      @PathVariable String modelId, @RequestBody ClothingModelCreateRequestDto request) {
    return new ClothingModelResponseDto(
        clothingService.updateClothingModel(
            modelId,
            request.getName(),
            request.getDescription(),
            request.getBrand(),
            request.getCategory(),
            request.getPrice()));
  }

  @DeleteMapping("/{modelId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteClothingModel(@PathVariable String modelId) {
    clothingService.deleteClothingModel(modelId);
  }

  @GetMapping("/{modelId}/variants")
  public List<ClothingVariantResponseDto> getAllVariantsForModel(@PathVariable String modelId) {
    return clothingService.getVariantsByModel(modelId).stream()
        .map(ClothingVariantResponseDto::new)
        .toList();
  }

  @GetMapping("/{modelId}/variants/{variantId}")
  public ClothingVariantResponseDto getVariant(
      @PathVariable String modelId, @PathVariable String variantId) {
    return new ClothingVariantResponseDto(clothingService.getVariant(modelId, variantId));
  }

  @PostMapping("/{modelId}/variants")
  @ResponseStatus(HttpStatus.CREATED)
  public ClothingVariantResponseDto addVariantToModel(
      @PathVariable String modelId, @RequestBody ClothingVariantCreateRequestDto request) {
    return new ClothingVariantResponseDto(
        clothingService.createVariant(
            modelId,
            request.getSize(),
            request.getColor(),
            request.getImagePath(),
            request.getStockQuantity()));
  }

  @PatchMapping("/{modelId}/variants/{variantId}")
  public ClothingVariantResponseDto updateVariantStock(
      @PathVariable String modelId,
      @PathVariable String variantId,
      @RequestBody ClothingVariantUpdateRequestDto request) {
    return new ClothingVariantResponseDto(
        clothingService.updateVariantStock(modelId, variantId, request.getStockQuantity()));
  }

  @DeleteMapping("/{modelId}/variants/{variantId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteVariant(@PathVariable String modelId, @PathVariable String variantId) {
    clothingService.deleteVariant(modelId, variantId);
  }
}
