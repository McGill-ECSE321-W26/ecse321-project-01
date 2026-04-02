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

