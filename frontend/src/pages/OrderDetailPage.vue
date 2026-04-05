<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowLeft } from 'lucide-vue-next'
import { api } from '@/api/client'
import type { OrderResponseDto } from '@/api/types/order'
import type { ItemResponseDto } from '@/api/types/item'
import type { ClothingModelListResponseDto, ClothingVariantResponseDto } from '@/api/types/clothing'

const route = useRoute()
const router = useRouter()
const orderID = route.params.orderID as string

const order = ref<OrderResponseDto | null>(null)
const items = ref<ItemResponseDto[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

// variantID: { name, size, color, imagePath }
const variantMap = ref(new Map<string, { name: string; size: string; color: string; imagePath: string }>())

onMounted(async () => {
  try {
    const [orderData, itemsData, models] = await Promise.all([
      api<OrderResponseDto>(`/orders/${orderID}`),
      api<ItemResponseDto[]>(`/orders/${orderID}/items`),
      api<ClothingModelListResponseDto[]>('/clothing'),
    ])
    order.value = orderData
    items.value = itemsData

    const variantLists = await Promise.all(
      models.map(m =>
        api<ClothingVariantResponseDto[]>(`/clothing/${m.clothingModelID}/variants`).then(
          variants => ({ modelName: m.name, variants }),
        ),
      ),
    )

    const map = new Map<string, { name: string; size: string; color: string; imagePath: string }>()
    for (const { modelName, variants } of variantLists) {
      for (const v of variants) {
        map.set(v.clothingVariantID, { name: modelName, size: v.size, color: v.color, imagePath: v.imagePath })
      }
    }
    variantMap.value = map
  }
  catch (e: unknown) {
    error.value = e instanceof Error ? e.message : 'Failed to load order.'
  }
  finally {
    loading.value = false
  }
})

function getVariantInfo(clothingVariantID: string | null) {
  if (!clothingVariantID) return { name: 'Unknown', size: '-', color: '-', imagePath: '' }
  return variantMap.value.get(clothingVariantID) ?? { name: 'Unknown', size: '-', color: '-', imagePath: '' }
}
</script>

<template>
  <main class="flex-1 px-10 pt-16 pb-20">
    <!-- Back -->
    <button
      class="flex items-center gap-2 text-[12px] uppercase tracking-widest text-(--text-light) hover:text-(--text-muted) transition-colors mb-8"
      @click="router.push({ name: 'orders' })"
    >
      <ArrowLeft class="w-3.5 h-3.5" />
      Back to Orders
    </button>

    <!-- Heading -->
    <h1 class="detail-heading text-[40px] lg:text-[52px] font-normal tracking-tight leading-tight mb-2">
      <span class="text-(--text-muted)">Order</span>
      <em class="text-(--text-light)"> Details</em>
    </h1>
    <div class="flex items-center justify-between mb-10 pb-6 border-b border-(--text-light)">
      <p class="text-sm font-light text-(--text-muted)">
        Full breakdown of your order contents.
      </p>
    </div>

    <!-- Loading / Error -->
    <div
      v-if="loading"
      class="text-[13px] text-(--text-light)"
    >
      Loading order…
    </div>
    <div
      v-else-if="error"
      class="text-[13px] text-red-400"
    >
      {{ error }}
    </div>

    <template v-else-if="order">
      <!-- Order header stats -->
      <div class="grid grid-cols-[2fr_2fr_1fr_1fr] gap-0 border border-(--text-light) mb-10">
        <div
          class="stat-card p-7 border-r border-(--text-light) flex flex-col gap-5"
          style="animation-delay: 0s"
        >
          <div>
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Order ID
            </p>
            <p class="text-[11px] font-light text-(--text) font-mono leading-relaxed break-all">
              {{ order.orderID }}
            </p>
          </div>
          <div>
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Address
            </p>
            <p class="text-[13px] font-light text-(--text) leading-snug">
              {{ order.address }}
            </p>
          </div>
        </div>
        <div
          class="stat-card p-7 border-r border-(--text-light) flex flex-col gap-5"
          style="animation-delay: 0.07s"
        >
          <div>
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Order Date
            </p>
            <p class="text-[13px] font-light text-(--text) leading-snug">
              {{ order.orderDate ? new Date(order.orderDate).toLocaleDateString('en-CA', { year: 'numeric', month: 'short', day: 'numeric' }) : '-' }}
            </p>
          </div>
          <div>
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Delivery Date
            </p>
            <p class="text-[13px] font-light text-(--text) leading-snug">
              {{ order.deliveryDate ? new Date(order.deliveryDate).toLocaleDateString('en-CA', { year: 'numeric', month: 'short', day: 'numeric' }) : '-' }}
            </p>
          </div>
        </div>
        <div
          class="stat-card p-7 border-r border-(--text-light) flex flex-col justify-center"
          style="animation-delay: 0.14s"
        >
          <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
            Total
          </p>
          <p class="text-[36px] font-light text-(--text) leading-none">
            ${{ order.totalPrice.toFixed(2) }}
          </p>
        </div>
        <div
          class="stat-card p-7 flex flex-col justify-center"
          style="animation-delay: 0.21s"
        >
          <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
            Money Saved
          </p>
          <p class="text-[36px] font-light text-(--text) leading-none">
            ${{ (order.loyaltySaving ?? 0).toFixed(2) }}
          </p>
        </div>
      </div>

      <!-- Items table -->
      <div
        class="stat-card border border-(--text-light)"
        style="animation-delay: 0.28s"
      >
        <!-- Table header -->
        <div class="grid grid-cols-[2.5rem_3fr_1fr_1.5fr_1fr_0.6fr] px-6 py-2.5 border-b border-(--text-light)">
          <span />
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Name</span>
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Size</span>
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Color</span>
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Unit Price</span>
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Qty</span>
        </div>

        <!-- Empty -->
        <div
          v-if="items.length === 0"
          class="px-6 py-10 text-[13px] text-(--text-light)"
        >
          No items found.
        </div>

        <!-- Item rows -->
        <div
          v-for="(item, i) in items"
          :key="item.itemID"
          class="grid grid-cols-[2.5rem_3fr_1fr_1.5fr_1fr_0.6fr] px-6 py-4 border-b border-(--text-light) last:border-b-0 hover:bg-(--card-hover) transition-colors items-center row-card"
          :style="{ animationDelay: `${0.28 + i * 0.04}s` }"
        >
          <div class="w-9 h-9 shrink-0 overflow-hidden border border-(--text-light)">
            <img
              v-if="getVariantInfo(item.clothingVariantID).imagePath"
              :src="getVariantInfo(item.clothingVariantID).imagePath"
              :alt="getVariantInfo(item.clothingVariantID).name"
              class="w-full h-full object-cover"
            >
            <div
              v-else
              class="w-full h-full bg-(--card-hover)"
            />
          </div>
          <span class="text-[13px] text-(--text) font-light">{{ getVariantInfo(item.clothingVariantID).name }}</span>
          <span class="text-[13px] text-(--text-muted) font-light uppercase">{{ getVariantInfo(item.clothingVariantID).size }}</span>
          <span class="text-[13px] text-(--text-muted) font-light capitalize">{{ getVariantInfo(item.clothingVariantID).color }}</span>
          <span class="text-[13px] text-(--text-muted) font-light">${{ item.price.toFixed(2) }}</span>
          <span class="text-[13px] text-(--text-muted) font-light">× {{ item.quantity }}</span>
        </div>
      </div>
    </template>
  </main>
</template>

<style scoped>
.detail-heading {
  font-family: 'Playfair Display', serif;
  letter-spacing: -1.5px;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

.stat-card,
.row-card {
  animation: fadeUp 0.6s ease both;
}
</style>
