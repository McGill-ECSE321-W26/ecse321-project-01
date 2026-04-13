export interface CustomerResponseDto {
  role: 'Customer'
  id: string
  personId: string
  email: string
  address: string
  loyaltyPoints: number
}

export interface CustomerCreateRequestDto {
  email: string
  password: string
  address: string
}

export interface EmployeeCreateRequestDto {
  email: string
  password: string
}

export interface EmployeeResponseDto {
  role: 'Employee'
  id: string
  email: string
  personId: string
}

export interface ManagerResponseDto {
  role: 'Manager'
  id: string
  personId: string
  email: string
}
