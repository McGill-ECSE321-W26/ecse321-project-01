export interface EmployeeCreateRequestDto{
    email: string
    password: string
}

export interface EmployeeResponseDto {
  id: string
  email: string
  personId: string
}

