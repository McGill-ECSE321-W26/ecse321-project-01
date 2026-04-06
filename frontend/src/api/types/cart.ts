export interface ItemResponseDto {
  itemID: string
  price: number
  quantity: number
  clothingVariantID: string
  customerID: string
}

export interface CartTotalDto {
  cartTotal: number
}

export interface ClothingVariantResponseDto {
  clothingVariantID: string
  size: string
  color: string
  imagePath: string
  stockQuantity: number
  modelId: string
}

export interface ClothingModelResponseDto {
  clothingModelID: string
  name: string
  price: number
  imagePath: string
  totalStockQuantity: number
}

export interface CompleteCartItem {
  itemID: string
  quantity: number
  price: number
  clothingVariantID: string
  modelName: string
  color: string
  size: string
  imagePath: string
}