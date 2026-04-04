export interface ClothingModelResponseDto {
  clothingModelID: string
  name: string
  price: number
  imagePath: string
  totalStockQuantity: number
}

export interface ClothingVariantResponseDto {
  clothingVariantID: string
  size: string
  color: string
  imagePath: string
  stockQuantity: number
  modelId: string
}
