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