// API response types matching the backend DTOs
export interface PersonResponseDto {
  id: string
  email: string
  roleTypes: string[]
  address: string | null
  loyaltyPoints: number | null
  employeeRoleId: string | null
  customerRoleId: string | null
}

export interface AuthResponseDto {
  token: string
  person: PersonResponseDto
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