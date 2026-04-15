import { test, expect } from '@playwright/test'
import {
  mockAuth,
  mockGet,
  measureLoad,
  CLOTHING_MODEL,
  CLOTHING_VARIANT,
  CART_ITEM,
  CART_TOTAL,
  ORDER,
  ORDER_ITEM,
  EMPLOYEE,
  CUSTOMER,
} from './helpers'

const LIMIT_MS = 2000

test('HomePage loads within 2 seconds', async ({ page }) => {
  const elapsed = await measureLoad(page, '/')
  expect(elapsed, `HomePage took ${elapsed}ms`).toBeLessThan(LIMIT_MS)
})

test('LoginPage loads within 2 seconds', async ({ page }) => {
  const elapsed = await measureLoad(page, '/login')
  expect(elapsed, `LoginPage took ${elapsed}ms`).toBeLessThan(LIMIT_MS)
})

test('RegisterPage loads within 2 seconds', async ({ page }) => {
  const elapsed = await measureLoad(page, '/register')
  expect(elapsed, `RegisterPage took ${elapsed}ms`).toBeLessThan(LIMIT_MS)
})

test('ShopPage loads within 2 seconds', async ({ page }) => {
  await mockAuth(page, 'Customer')
  await mockGet(page, '/clothing', [CLOTHING_MODEL])
  const elapsed = await measureLoad(page, '/shop')
  expect(elapsed, `ShopPage took ${elapsed}ms`).toBeLessThan(LIMIT_MS)
})

test('ItemPage loads within 2 seconds', async ({ page }) => {
  await mockAuth(page, 'Customer')
  await mockGet(page, '/clothing/model-1', CLOTHING_MODEL)
  await mockGet(page, '/clothing/model-1/variants', [CLOTHING_VARIANT])
  const elapsed = await measureLoad(page, '/item/model-1')
  expect(elapsed, `ItemPage took ${elapsed}ms`).toBeLessThan(LIMIT_MS)
})

test('CartPage loads within 2 seconds', async ({ page }) => {
  await mockAuth(page, 'Customer')
  await mockGet(page, '/carts/customer-1/items', [CART_ITEM])
  await mockGet(page, '/carts/customer-1', CART_TOTAL)
  const elapsed = await measureLoad(page, '/cart')
  expect(elapsed, `CartPage took ${elapsed}ms`).toBeLessThan(LIMIT_MS)
})

test('CustomerOrdersPage loads within 2 seconds', async ({ page }) => {
  await mockAuth(page, 'Customer')
  await mockGet(page, '/orders/customer/customer-1', [ORDER])
  const elapsed = await measureLoad(page, '/orders')
  expect(elapsed, `CustomerOrdersPage took ${elapsed}ms`).toBeLessThan(LIMIT_MS)
})

test('OrderDetailPage loads within 2 seconds', async ({ page }) => {
  await mockAuth(page, 'Customer')
  await mockGet(page, '/orders/order-1', ORDER)
  await mockGet(page, '/orders/order-1/items', [ORDER_ITEM])
  const elapsed = await measureLoad(page, '/orders/order-1')
  expect(elapsed, `OrderDetailPage took ${elapsed}ms`).toBeLessThan(LIMIT_MS)
})

test('AccountPage loads within 2 seconds', async ({ page }) => {
  await mockAuth(page, 'Customer')
  const elapsed = await measureLoad(page, '/account')
  expect(elapsed, `AccountPage took ${elapsed}ms`).toBeLessThan(LIMIT_MS)
})

test('EmployeeOrdersPage loads within 2 seconds', async ({ page }) => {
  await mockAuth(page, 'Employee')
  await mockGet(page, '/orders', [ORDER])
  await mockGet(page, '/persons/employees', [EMPLOYEE])
  const elapsed = await measureLoad(page, '/employee/orders')
  expect(elapsed, `EmployeeOrdersPage took ${elapsed}ms`).toBeLessThan(LIMIT_MS)
})

test('ManagerDashboardPage loads within 2 seconds', async ({ page }) => {
  await mockAuth(page, 'Manager')
  await mockGet(page, '/persons/customers', [CUSTOMER])
  await mockGet(page, '/persons/employees', [EMPLOYEE])
  await mockGet(page, '/orders', [ORDER])
  const elapsed = await measureLoad(page, '/manager')
  expect(elapsed, `ManagerDashboardPage took ${elapsed}ms`).toBeLessThan(LIMIT_MS)
})

test('ManagerOrdersDashboardPage loads within 2 seconds', async ({ page }) => {
  await mockAuth(page, 'Manager')
  await mockGet(page, '/orders', [ORDER])
  await mockGet(page, '/persons/employees', [EMPLOYEE])
  const elapsed = await measureLoad(page, '/manager/orders')
  expect(elapsed, `ManagerOrdersDashboardPage took ${elapsed}ms`).toBeLessThan(LIMIT_MS)
})

test('ManagerCustomersDashboardPage loads within 2 seconds', async ({ page }) => {
  await mockAuth(page, 'Manager')
  await mockGet(page, '/persons/customers', [CUSTOMER])
  const elapsed = await measureLoad(page, '/manager/customers')
  expect(elapsed, `ManagerCustomersDashboardPage took ${elapsed}ms`).toBeLessThan(LIMIT_MS)
})

test('ManagerEmployeesDashboardPage loads within 2 seconds', async ({ page }) => {
  await mockAuth(page, 'Manager')
  await mockGet(page, '/persons/employees', [EMPLOYEE])
  const elapsed = await measureLoad(page, '/manager/employees')
  expect(elapsed, `ManagerEmployeesDashboardPage took ${elapsed}ms`).toBeLessThan(LIMIT_MS)
})

test('ManagerInventoryPage loads within 2 seconds', async ({ page }) => {
  await mockAuth(page, 'Manager')
  await mockGet(page, '/clothing', [CLOTHING_MODEL])
  const elapsed = await measureLoad(page, '/manager/inventory')
  expect(elapsed, `ManagerInventoryPage took ${elapsed}ms`).toBeLessThan(LIMIT_MS)
})
