import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/api/client'
import type { ItemResponseDto } from '@/api/types/item'

export const useCartStore = defineStore('cart', () => {
  const items = ref<ItemResponseDto[]>([])

  const itemCount = computed(() => items.value.length)

  async function fetchCart(customerId: string) {
    items.value = await api<ItemResponseDto[]>(`/carts/${customerId}/items`)
  }

  function clearCart() {
    items.value = []
  }

  return {
    items,
    itemCount,
    fetchCart,
    clearCart
  }
})