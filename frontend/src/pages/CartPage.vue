<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Minus, Plus, Trash2 } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { Separator } from '@/components/ui/separator'
import { Skeleton } from '@/components/ui/skeleton'
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog'
import { api } from '@/api/client'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'
import type { CustomerResponseDto } from '@/api/types/person'
import type { ItemResponseDto } from '@/api/types/item'
import type { CartTotalDto, CompleteCartItem } from '@/api/types/cart'
import type { OrderResponseDto, OrderCreateRequestDto } from '@/api/types/order'

const auth = useAuthStore()
const cart = useCartStore()
const router = useRouter()

const customerId = computed(() => (auth.person as CustomerResponseDto)?.id)
const availableLoyaltyPoints = computed(() => (auth.person as CustomerResponseDto)?.loyaltyPoints ?? 0)

const items = ref<CompleteCartItem[]>([])
const cartTotal = ref(0)
const loading = ref(true)
const error = ref('')

// Checkout dialog state
const checkoutOpen = ref(false)
const deliveryDate = ref('')
const usedLoyaltyPoints = ref(0)
const checkoutError = ref('')
const checkoutLoading = ref(false)

const minDeliveryDate = computed(() => {
  const tomorrow = new Date()
  tomorrow.setDate(tomorrow.getDate() + 1)
  return tomorrow.toISOString().split('T')[0]
})

async function loadCart() {
  loading.value = true
  error.value = ''
  try {
    const [rawItems, totalDto] = await Promise.all([
      api<ItemResponseDto[]>(`/carts/${customerId.value}/items`),
      api<CartTotalDto>(`/carts/${customerId.value}`),
    ])

    cart.items = rawItems
    items.value = rawItems.map(item => ({
      itemID: item.itemID,
      quantity: item.quantity,
      price: item.price,
      clothingVariantID: item.clothingVariantID ?? '',
      modelName: item.modelName ?? 'Unknown',
      color: item.variantColor ?? '-',
      size: item.variantSize ?? '-',
      imagePath: item.variantImagePath ?? '',
    }))

    cartTotal.value = totalDto.cartTotal
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Failed to load cart.'
  } finally {
    loading.value = false
  }
}

async function updateQuantity(item: CompleteCartItem, delta: number) {
  const newQty = item.quantity + delta
  if (newQty < 1) return
  try {
    await api(`/carts/${customerId.value}/items/${item.itemID}`, { method: 'PATCH', body: JSON.stringify({ quantity: newQty }) })
    item.quantity = newQty
    const storeItem = cart.items.find(i => i.itemID === item.itemID)
    if (storeItem) storeItem.quantity = newQty
    const totalDto = await api<CartTotalDto>(`/carts/${customerId.value}`)
    cartTotal.value = totalDto.cartTotal
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Failed to update quantity.'
  }
}

async function removeItem(itemID: string) {
  try {
    await api(`/carts/${customerId.value}/items/${itemID}`, { method: 'DELETE' })
    items.value = items.value.filter(i => i.itemID !== itemID)
    cart.items = cart.items.filter(i => i.itemID !== itemID)
    const totalDto = await api<CartTotalDto>(`/carts/${customerId.value}`)
    cartTotal.value = totalDto.cartTotal
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Failed to remove item.'
  }
}

function openCheckout() {
  deliveryDate.value = minDeliveryDate.value
  usedLoyaltyPoints.value = 0
  checkoutError.value = ''
  checkoutOpen.value = true
}

async function placeOrder() {
  if (!deliveryDate.value) {
    checkoutError.value = 'Please select a delivery date.'
    return
  }
  checkoutLoading.value = true
  checkoutError.value = ''
  try {
    const body: OrderCreateRequestDto = {
      customerID: customerId.value,
      deliveryDate: deliveryDate.value,
      usedLoyaltyPoints: usedLoyaltyPoints.value,
    }
    await api<OrderResponseDto>('/orders', { method: 'POST', body: JSON.stringify(body) })
    // Fetch fresh customer data so loyalty points reflect what the backend computed
    const updated = await api<CustomerResponseDto>(`/persons/customers/${customerId.value}`)
    auth.updatePerson(updated)
    cart.clearCart()
    checkoutOpen.value = false
    router.push({ name: 'shop' })
  } catch (e) {
    checkoutError.value = e instanceof Error ? e.message : 'Failed to place order.'
  } finally {
    checkoutLoading.value = false
  }
}

onMounted(loadCart)
</script>

<template>
  <div class="max-w-3xl mx-auto py-12 px-4">
    <h1 class="text-3xl font-bold mb-8">
      Shopping Cart
    </h1>

    <div
      v-if="loading"
      class="flex flex-col gap-4"
    >
      <div
        v-for="n in 3"
        :key="n"
        class="flex items-center gap-4 py-5"
      >
        <Skeleton class="w-20 h-20 rounded-lg shrink-0" />
        <div class="flex-1 space-y-2">
          <Skeleton class="h-4 w-1/2" />
          <Skeleton class="h-3 w-1/3" />
          <Skeleton class="h-7 w-24 mt-2" />
        </div>
        <Skeleton class="h-4 w-12" />
      </div>
    </div>

    <div
      v-else-if="error"
      class="rounded-md bg-red-50 border border-red-200 p-4 text-sm text-red-800"
    >
      {{ error }}
    </div>

    <div
      v-else-if="items.length === 0"
      class="text-center text-(--text-muted) py-16"
    >
      Your cart is empty.
    </div>

    <div v-else>
      <div class="flex flex-col">
        <template
          v-for="(item, index) in items"
          :key="item.itemID"
        >
          <Separator
            v-if="index > 0"
            class="bg-(--card-hover)"
          />
          <div class="flex items-center gap-4 py-5">
            <img
              :src="item.imagePath"
              :alt="item.modelName"
              class="w-20 h-20 object-cover rounded-lg bg-gray-100 shrink-0"
            >
            <div class="flex-1 min-w-0">
              <p class="font-medium truncate">
                {{ item.modelName }}
              </p>
              <p class="text-sm text-(--text-muted)">
                Color: {{ item.color }} / Size: {{ item.size }}
              </p>

              <div class="flex items-center gap-2 mt-2">
                <Button
                  variant="outline"
                  size="icon"
                  class="h-7 w-7"
                  :disabled="item.quantity <= 1"
                  @click="updateQuantity(item, -1)"
                >
                  <Minus class="w-3 h-3" />
                </Button>
                <span class="w-6 text-center text-sm">{{ item.quantity }}</span>
                <Button
                  variant="outline"
                  size="icon"
                  class="h-7 w-7"
                  @click="updateQuantity(item, 1)"
                >
                  <Plus class="w-3 h-3" />
                </Button>
              </div>
            </div>

            <div class="flex flex-col items-end gap-2 shrink-0">
              <span class="font-medium">${{ item.price.toFixed(2) }}</span>
              <button
                class="text-xs text-(--text-muted) hover:text-red-500 flex items-center gap-1 transition-colors"
                @click="removeItem(item.itemID)"
              >
                <Trash2 class="w-3 h-3" />
                Remove
              </button>
            </div>
          </div>
        </template>
      </div>

      <Separator class="my-4 bg-(--card-hover)" />
      <div class="flex items-center justify-between">
        <span class="text-lg font-semibold">Subtotal</span>
        <span class="text-lg font-semibold">${{ cartTotal.toFixed(2) }}</span>
      </div>

      <Button
        class="w-full mt-6"
        @click="openCheckout"
      >
        Proceed to checkout
      </Button>
    </div>
  </div>

  <Dialog v-model:open="checkoutOpen">
    <DialogContent class="sm:max-w-md">
      <DialogHeader>
        <DialogTitle>Checkout</DialogTitle>
      </DialogHeader>

      <div class="flex flex-col gap-5 py-2">
        <div class="flex flex-col gap-1.5">
          <label class="text-sm font-medium">Delivery date</label>
          <input
            v-model="deliveryDate"
            type="date"
            :min="minDeliveryDate"
            class="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
          >
        </div>

        <div class="flex flex-col gap-1.5">
          <div class="flex items-center justify-between">
            <label class="text-sm font-medium">Loyalty points to use</label>
            <span class="text-xs text-(--text-muted)">{{ availableLoyaltyPoints }} available</span>
          </div>
          <input
            v-model.number="usedLoyaltyPoints"
            type="number"
            min="0"
            :max="availableLoyaltyPoints"
            class="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
          >
        </div>

        <div
          v-if="checkoutError"
          class="rounded-md bg-red-50 border border-red-200 p-3 text-sm text-red-800"
        >
          {{ checkoutError }}
        </div>
      </div>

      <DialogFooter>
        <Button
          variant="outline"
          :disabled="checkoutLoading"
          @click="checkoutOpen = false"
        >
          Cancel
        </Button>
        <Button
          :disabled="checkoutLoading"
          @click="placeOrder"
        >
          {{ checkoutLoading ? 'Placing order...' : 'Place order' }}
        </Button>
      </DialogFooter>
    </DialogContent>
  </Dialog>
</template>
