// Matches backend ClothingModel.Category enum
export type ClothingCategory = 'Tops' | 'Bottoms' | 'Dresses' | 'Outerwear' | 'Accessories'

export interface VariantSummaryDto {
  imagePath: string
  color: string
}

// Matches backend ClothingModelListResponseDto (used for GET /api/clothing)
export interface ClothingModelListResponseDto {
  clothingModelID: string
  name: string
  description: string
  brand: string
  category: ClothingCategory
  price: number
  totalStockQuantity: number
  variants: VariantSummaryDto[]
}

// Matches backend ClothingModelResponseDto (used for GET /api/clothing/{modelId})
export interface ClothingModelResponseDto {
  clothingModelID: string
  name: string
  description: string
  brand: string
  category: ClothingCategory
  price: number
  totalStockQuantity: number
}

// Matches backend ClothingVariantResponseDto
export interface ClothingVariantResponseDto {
  clothingVariantID: string
  size: string
  color: string
  imagePath: string
  stockQuantity: number
  modelId: string
}

// Request DTOs

export interface ClothingModelRequestDto {
  name: string
  description: string
  brand: string
  category: ClothingCategory
  price: number
}

export interface ClothingVariantCreateRequestDto {
  size: string
  color: string
  imagePath: string
  stockQuantity: number
}

export interface ClothingVariantStockUpdateDto {
  stockQuantity: number
}
