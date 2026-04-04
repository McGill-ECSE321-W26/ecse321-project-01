<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Calendar, X } from 'lucide-vue-next'
import { api } from '@/api/client'
import type { OrderResponseDto } from '@/api/types/order'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()

// ── Data ─────────────────────────────────────────────────────────────────────
const orders = ref<OrderResponseDto[]>([])
const loading = ref(true)
const updatingOrder = ref<string | null>(null)
const editingDelivery = ref<string | null>(null)
const editDeliveryValue = ref<string>('')
const saveDeliveryError = ref<string | null>(null)

onMounted(async () => {
  try {
    const customerID = auth.person?.id
    if (!customerID) return
    orders.value = await api<OrderResponseDto[]>(`/orders/customer/${customerID}`)
  } catch { /* empty */ } finally {
    loading.value = false
  }
})

const totalOrders = computed(() => orders.value.length)
const activeOrders = computed(() =>
  orders.value.filter(o => o.orderStatus !== 'Delivered' && o.orderStatus !== 'Cancelled').length,
)
const totalSaved = computed(() =>
  orders.value.reduce((sum, o) => sum + (o.loyaltySaving ?? 0), 0),
)

async function cancelOrder(order: OrderResponseDto) {
  updatingOrder.value = order.orderID
  try {
    const updated = await api<OrderResponseDto>(`/orders/${order.orderID}`, {
      method: 'PATCH',
      body: JSON.stringify({
        employeeID: order.employeeID,
        deliveryDate: order.deliveryDate,
        orderStatus: 'Cancelled',
      }),
    })
    const idx = orders.value.findIndex(o => o.orderID === updated.orderID)
    if (idx !== -1) orders.value[idx] = updated
  } catch { /* empty */ } finally {
    updatingOrder.value = null
  }
}

function startEditDelivery(order: OrderResponseDto) {
  editingDelivery.value = order.orderID
  editDeliveryValue.value = order.deliveryDate
    ? new Date(order.deliveryDate).toISOString().slice(0, 10)
    : ''
}

function cancelEditDelivery() {
  editingDelivery.value = null
  editDeliveryValue.value = ''
  saveDeliveryError.value = null
}

async function saveDeliveryDate(order: OrderResponseDto) {
  if (!editDeliveryValue.value) return
  updatingOrder.value = order.orderID
  saveDeliveryError.value = null
  try {
    // Send as epoch ms to work with java.sql.Date consistently
    const deliveryTimestamp = new Date(editDeliveryValue.value).getTime()
    const updated = await api<OrderResponseDto>(`/orders/${order.orderID}/`, {
      method: 'PATCH',
      body: JSON.stringify({
        employeeID: order.employeeID,
        deliveryDate: deliveryTimestamp,
        orderStatus: order.orderStatus,
      }),
    })
    const idx = orders.value.findIndex(o => o.orderID === updated.orderID)
    if (idx !== -1) orders.value[idx] = updated
    editingDelivery.value = null
  } catch (e: unknown) {
    saveDeliveryError.value = e instanceof Error ? e.message : 'Failed to update delivery date.'
  } finally {
    updatingOrder.value = null
  }
}

function formatDate(d: Date | null) {
  if (!d) return '-'
  return new Date(d).toLocaleDateString('en-CA', { year: 'numeric', month: 'short', day: 'numeric' })
}

function canModify(order: OrderResponseDto) {
  return order.orderStatus !== 'Cancelled' && order.orderStatus !== 'Delivered'
}
</script>

<template>
  <main class="flex-1 px-10 pt-16 pb-20">
    <!-- Heading -->
    <h1 class="orders-heading text-[40px] lg:text-[52px] font-normal tracking-tight leading-tight mb-2">
      <span class="text-(--text-muted)">My</span>
      <em class="text-(--text-light)"> Orders</em>
    </h1>
    <div class="flex items-center justify-between mb-10 pb-6 border-b border-(--text-light)">
      <p class="text-sm font-light text-(--text-muted)">
        Track and manage your current and past orders.
      </p>
    </div>

    <!-- Stats -->
    <div class="grid grid-cols-3 gap-0 border border-(--text-light) mb-10">
      <div
        class="stat-card p-7 border-r border-(--text-light)"
        style="animation-delay: 0s"
      >
        <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
          Total Orders
        </p>
        <p class="text-[44px] font-light text-(--text) leading-none">
          {{ loading ? '-' : totalOrders }}
        </p>
      </div>
      <div
        class="stat-card p-7 border-r border-(--text-light)"
        style="animation-delay: 0.1s"
      >
        <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
          Active
        </p>
        <p class="text-[44px] font-light text-(--text) leading-none">
          {{ loading ? '-' : activeOrders }}
        </p>
      </div>
      <div
        class="stat-card p-7"
        style="animation-delay: 0.2s"
      >
        <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
          Money Saved
        </p>
        <p class="text-[44px] font-light text-(--text) leading-none">
          {{ loading ? '-' : `$${totalSaved.toFixed(2)}` }}
        </p>
      </div>
    </div>

    <!-- Order table -->
    <div
      class="stat-card border border-(--text-light)"
      style="animation-delay: 0.3s"
    >
      <!-- Table header -->
      <div class="grid grid-cols-[2fr_1.5fr_2fr_1fr_1fr_1.2fr_1.8fr] px-6 py-2.5 border-b border-(--text-light)">
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Order ID</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Order Date</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Delivery Date</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Total</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Saved</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Status</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Actions</span>
      </div>

      <!-- Loading -->
      <div
        v-if="loading"
        class="px-6 py-10 text-[13px] text-(--text-light)"
      >
        Loading orders…
      </div>

      <!-- Empty -->
      <div
        v-else-if="orders.length === 0"
        class="px-6 py-10 text-[13px] text-(--text-light)"
      >
        No orders found.
      </div>

      <!-- Rows -->
      <div
        v-for="(order, i) in orders"
        :key="order.orderID"
        class="grid grid-cols-[2fr_1.5fr_2fr_1fr_1fr_1.2fr_1.8fr] px-6 py-4 border-b border-(--text-light) last:border-b-0 hover:bg-(--card-hover) transition-colors items-center row-card"
        :style="{ animationDelay: `${0.3 + i * 0.04}s` }"
      >
        <!-- Order ID -->
        <span class="text-[13px] text-(--text) font-light tracking-wide font-mono">#{{ order.orderID.slice(0, 13) }}…</span>

        <!-- Order Date -->
        <span class="text-[13px] text-(--text-muted) font-light">{{ formatDate(order.orderDate) }}</span>

        <!-- Delivery Date (inline edit) -->
        <div>
          <div
            v-if="editingDelivery === order.orderID"
            class="flex flex-col gap-1"
          >
            <div class="flex items-center gap-2">
              <input
                v-model="editDeliveryValue"
                type="date"
                class="text-[12px] border border-(--text-light) bg-(--bg) text-(--text) px-2 py-1 focus:outline-none focus:border-(--text-muted)"
              >
              <button
                :disabled="updatingOrder === order.orderID"
                class="text-[10px] uppercase tracking-widest border border-(--text-light) px-2 py-1 text-(--text) hover:bg-(--text) hover:text-(--bg) transition-colors disabled:opacity-40"
                @click="saveDeliveryDate(order)"
              >
                Save
              </button>
              <button
                class="text-(--text-light) hover:text-(--text-muted) transition-colors"
                @click="cancelEditDelivery"
              >
                <X class="w-3.5 h-3.5" />
              </button>
            </div>
            <p
              v-if="saveDeliveryError"
              class="text-[11px] text-red-400"
            >
              {{ saveDeliveryError }}
            </p>
          </div>
          <span
            v-else
            class="text-[13px] text-(--text-muted) font-light"
          >{{ formatDate(order.deliveryDate) }}</span>
        </div>

        <!-- Total Price -->
        <span class="text-[13px] text-(--text-muted) font-light">${{ order.totalPrice?.toFixed(2) ?? '-' }}</span>

        <!-- Loyalty Savings -->
        <span class="text-[13px] text-(--text-muted) font-light">${{ (order.loyaltySaving ?? 0).toFixed(2) }}</span>

        <!-- Status -->
        <span
          class="text-[11px] uppercase tracking-widest"
          :class="{
            'text-(--text-muted)': order.orderStatus === 'Delivered',
            'text-(--text-light) line-through': order.orderStatus === 'Cancelled',
            'text-(--text)': order.orderStatus === 'Preparing',
          }"
        >
          {{ order.orderStatus }}
        </span>

        <!-- Actions -->
        <div class="flex items-center gap-2">
          <button
            v-if="canModify(order) && editingDelivery !== order.orderID"
            :disabled="updatingOrder === order.orderID"
            class="text-[11px] uppercase tracking-widest border border-(--text-light) px-3 py-1.5 text-(--text) hover:bg-(--text) hover:text-(--bg) transition-colors disabled:opacity-40 flex items-center gap-1.5"
            @click="startEditDelivery(order)"
          >
            <Calendar class="w-3 h-3" />
            Edit
          </button>
          <button
            v-if="canModify(order)"
            :disabled="updatingOrder === order.orderID"
            class="text-[11px] uppercase tracking-widest border border-(--text-light) px-3 py-1.5 text-(--text) hover:bg-destructive hover:text-(--bg) transition-colors disabled:opacity-40"
            @click="cancelOrder(order)"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.orders-heading {
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
