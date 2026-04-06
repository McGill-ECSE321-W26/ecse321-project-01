export interface ItemResponseDto {
  itemID: string
  price: number
  quantity: number
  clothingVariantID: string | null
  customerID: string | null
}

export interface ItemCreateRequestDto {
  clothingVariantID: string;
  quantity: number;
}