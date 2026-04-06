export interface ItemResponseDto {
  itemID: string
  price: number
  quantity: number
  clothingVariantID: string | null
  customerID: string | null
  modelName: string | null
  variantSize: string | null
  variantColor: string | null
  variantImagePath: string | null
}
