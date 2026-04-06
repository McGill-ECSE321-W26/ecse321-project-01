export interface OrderCreateRequestDto {
  customerID: string
  deliveryDate: string
  usedLoyaltyPoints: number
}

export interface OrderResponseDto {
  orderID: string
  orderStatus: string
  orderDate: Date
  deliveryDate: Date
  loyaltySaving: number
  totalPrice: number
  address: string
  customerID: string
  employeeID: string | null
  itemIDs: string[]
}

export interface OrderRequestUpdateDto{
  employeeID: string
  orderStatus: string
  deliveryDate: Date
}