// This is the Auth store which is used to manage JWT token, user profile, and role (Persisted to localStorage)
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/api/client'
import type { AuthResponseDto, RoleResponseDto } from '@/api/types/types.ts'
import type { CustomerCreateRequestDto } from '@/api/types/person.ts'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const person = ref<RoleResponseDto | null>(
    JSON.parse(localStorage.getItem('person') ?? 'null'),
  )
  const role = ref<string | null>(localStorage.getItem('role'))

  // Reactive computed values based on 3 refs above (https://vuejs.org/guide/essentials/computed)
  const isAuthenticated = computed(() => !!token.value)
  const currentRole = computed(() => role.value)
  const personId = computed(() => person.value?.personId ?? null)

  // Sends POST /api/persons/sessions and updates local storage
  async function login(email: string, password: string, selectedRole: string) {
    const data = await api<AuthResponseDto>('/persons/sessions', {
      method: 'POST',
      body: JSON.stringify({ email, password, role: selectedRole }),
    })

    token.value = data.token
    person.value = data.person
    role.value = selectedRole

    localStorage.setItem('token', data.token)
    localStorage.setItem('person', JSON.stringify(data.person))
    localStorage.setItem('role', selectedRole)
  }

  // Sends POST /api/persons/customers
  async function register(email: string, password: string, address: string) {
    await api<CustomerCreateRequestDto>('/persons/customers', {
      method: 'POST',
      body: JSON.stringify({ email, password, address }),
    })
  }

  // Sends POST /api/persons/employees (also creates a customer role with the given address)
  async function registerEmployee(email: string, password: string, address: string) {
    await api('/persons/employees', {
      method: 'POST',
      body: JSON.stringify({ email, password, address }),
    })
  }

  // Logout is simple, just
  function logout() {
    token.value = null
    person.value = null
    role.value = null

    localStorage.removeItem('token')
    localStorage.removeItem('person')
    localStorage.removeItem('role')
  }

  function updatePerson(newPerson: RoleResponseDto) {
    person.value = newPerson
    localStorage.setItem('person', JSON.stringify(newPerson))
  }

  return {
    token,
    person,
    role,
    isAuthenticated,
    currentRole,
    personId,
    login,
    register,
    registerEmployee,
    logout,
    updatePerson,
  }
})
