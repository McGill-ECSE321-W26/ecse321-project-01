export interface CartTotalDto {
  cartTotal: number
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