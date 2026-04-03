// API response types matching the backend DTOs
import type { CustomerResponseDto, EmployeeResponseDto, ManagerResponseDto } from './person'

export type { CustomerResponseDto, EmployeeResponseDto, ManagerResponseDto }

export type RoleResponseDto = CustomerResponseDto | EmployeeResponseDto | ManagerResponseDto

export interface AuthResponseDto {
  token: string
  person: RoleResponseDto
}
