export interface CustomerResponseDto{
    id: string
    personId: string
    email: string
    address: string
    loyaltyPoints: number
}

export interface CustomerCreateRequestDto{
    email: string
    password: string
    address: string
}

export interface EmployeeCreateRequestDto{
    email: string
    password: string
}

export interface EmployeeResponseDto {
  id: string
  email: string
  personId: string
}
