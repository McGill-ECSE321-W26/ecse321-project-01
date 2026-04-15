import type { Page } from '@playwright/test'

const API_BASE = 'http://localhost:8080/api'



export const CUSTOMER_PERSON = {
  personId: 'person-1',
  id: 'customer-1',
  email: 'customer@test.com',
  address: '123 Main St',
  loyaltyPoints: 0,
}

export const EMPLOYEE_PERSON = {
  personId: 'person-2',
  id: 'employee-1',
  email: 'employee@test.com',
}

export const MANAGER_PERSON = {
  personId: 'person-3',
  id: 'manager-1',
  email: 'manager@test.com',
}

export const CLOTHING_MODEL = {
  clothingModelID: 'model-1',
  name: 'Test Shirt',
  brand: 'TestBrand',
  category: 'Tops',
  price: 29.99,
  description: 'A test shirt',
  variants: [
    { clothingVariantID: 'variant-1', color: '#FF0000', size: 'M', stockQuantity: 10, imagePath: '' },
  ],
}

export const CLOTHING_VARIANT = {
  clothingVariantID: 'variant-1',
  clothingModelID: 'model-1',
  color: '#FF0000',
  size: 'M',
  stockQuantity: 10,
  imagePath: '',
}

export const CART_ITEM = {
  itemID: 'item-1',
  quantity: 1,
  price: 29.99,
  clothingVariantID: 'variant-1',
  modelName: 'Test Shirt',
  variantColor: '#FF0000',
  variantSize: 'M',
  variantImagePath: '',
}

export const CART_TOTAL = { cartTotal: 29.99 }

export const ORDER = {
  orderID: 'order-1',
  customerID: 'customer-1',
  employeeID: null,
  orderDate: '2026-01-01T00:00:00',
  deliveryDate: null,
  orderStatus: 'Preparing',
  totalPrice: 29.99,
  loyaltySaving: 0,
}

export const ORDER_ITEM = {
  itemID: 'item-1',
  quantity: 1,
  price: 29.99,
  clothingVariantID: 'variant-1',
  modelName: 'Test Shirt',
  variantColor: '#FF0000',
  variantSize: 'M',
  variantImagePath: '',
}

export const EMPLOYEE = EMPLOYEE_PERSON

export const CUSTOMER = CUSTOMER_PERSON




// Setup auth state for localStorage before the page loads

export async function mockAuth(page: Page, role: 'Customer' | 'Employee' | 'Manager') {
  const personMap = { Customer: CUSTOMER_PERSON, Employee: EMPLOYEE_PERSON, Manager: MANAGER_PERSON }
  const person = personMap[role]
  await page.addInitScript(({ token, personJson, roleStr }) => {
    localStorage.setItem('token', token)
    localStorage.setItem('person', personJson)
    localStorage.setItem('role', roleStr)
  }, { token: 'fake-jwt-token', personJson: JSON.stringify(person), roleStr: role })
}


 // Mock a GET endpoint to return JSON data

export async function mockGet(page: Page, path: string, body: unknown) {
  await page.route(`${API_BASE}${path}`, route => {
    route.fulfill({ status: 200, contentType: 'application/json', body: JSON.stringify(body) })
  })
}

 // Navigate to path and return time until the load event fires

export async function measureLoad(page: Page, path: string): Promise<number> {
  const start = Date.now()
  await page.goto(path, { waitUntil: 'load' })
  const elapsed = Date.now() - start
  console.log(`Load time: ${elapsed}ms`)
  return elapsed
}
