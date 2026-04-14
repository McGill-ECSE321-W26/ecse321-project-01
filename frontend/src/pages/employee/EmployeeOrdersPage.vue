<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { api } from '@/api/client'
import type { OrderResponseDto } from '@/api/types/order'
import type { EmployeeResponseDto, CustomerResponseDto } from '@/api/types/person'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()
const employeeID = auth.person?.id ?? ''
const employeePersonId = auth.person?.personId ?? ''

const orders = ref<OrderResponseDto[]>([])
const employees = ref<EmployeeResponseDto[]>([])
const customers = ref<CustomerResponseDto[]>([])
const loading = ref(true)
const activeFilter = ref<'all' | 'available' | 'mine'>('all')
const updatingStatus = ref<string | null>(null)

onMounted(async () => {
  try {
    orders.value = await api<OrderResponseDto[]>('/orders')
  } catch { /* empty */ } finally {
    loading.value = false
  }
  try {
    employees.value = await api<EmployeeResponseDto[]>('/persons/employees')
  } catch { /* empty */ }
  try {
    customers.value = await api<CustomerResponseDto[]>('/persons/customers')
  } catch { /* empty */ }
})

function filteredOrders() {
  if (activeFilter.value === 'available') return orders.value.filter(o => o.orderStatus === 'Preparing' && !o.employeeID)
  if (activeFilter.value === 'mine') return orders.value.filter(o => o.employeeID === employeeID)
  return orders.value
}

async function assignSelf(order: OrderResponseDto) {
  updatingStatus.value = order.orderID
  try {
    const updated = await api<OrderResponseDto>(`/orders/${order.orderID}`, {
      method: 'PATCH',
      body: JSON.stringify({
        employeeID: employeeID,
        orderStatus: 'Preparing',
      }),
    })
    const index = orders.value.findIndex(o => o.orderID === updated.orderID)
    if (index !== -1) orders.value[index] = updated
  } catch { /* empty */ } finally {
    updatingStatus.value = null
  }
}

function canAssignSelf(order: OrderResponseDto) {
  if (order.orderStatus !== 'Preparing' || order.employeeID || !order.customerID) return false
  const customer = customers.value.find(c => c.id === order.customerID)
  if (customer && customer.personId === employeePersonId) return false
  return true
}

function formatDate(d: Date | null) {
  if (!d) return '-'
  return new Date(d).toLocaleDateString('en-CA', { year: 'numeric', month: 'short', day: 'numeric' })
}

function getEmployeeName(id: string | null | undefined) {
  if (!id) return '-'
  const emp = employees.value.find(e => e.id === id)
  if (!emp) return 'Unknown'
  return emp.email.substring(0, emp.email.indexOf('@')).replace('.', ' ')
}
</script>

<template>
  <main class="flex-1 px-10 pt-16 pb-20">
    <!-- Heading -->
    <h1 class="orders-heading text-[40px] lg:text-[52px] font-normal tracking-tight leading-tight mb-2">
      <span class="text-(--text-muted)">Order</span>
      <span class="text-(--button-hover)"> Dashboard</span>
    </h1>
    <div class="flex items-center justify-between mb-10 pb-6 border-b border-(--text-light)">
      <p class="text-md font-light text-(--text-muted)">
        Claim and fulfill customer orders.
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
          {{ loading ? '-' : orders.length }}
        </p>
      </div>
      <div
        class="stat-card p-7 border-r border-(--text-light)"
        style="animation-delay: 0.1s"
      >
        <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
          Assigned to Me
        </p>
        <p class="text-[44px] font-light text-(--text) leading-none">
          {{ loading ? '-' : orders.filter(o => o.employeeID === employeeID).length }}
        </p>
      </div>
      <div
        class="stat-card p-7"
        style="animation-delay: 0.2s"
      >
        <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
          Available
        </p>
        <p class="text-[44px] font-light text-(--text) leading-none">
          {{ loading ? '-' : orders.filter(o => o.orderStatus === 'Preparing' && !o.employeeID).length }}
        </p>
      </div>
    </div>

    <!-- Order table -->
    <div
      class="stat-card border border-(--text-light)"
      style="animation-delay: 0.3s"
    >
      <!-- Filter tabs -->
      <div class="flex items-center border-b border-(--text-light)">
        <button
          class="px-6 py-3.5 text-[11px] uppercase tracking-widest border-r border-(--text-light) transition-colors"
          :class="activeFilter === 'all' ? 'bg-(--card-hover) text-(--text) font-medium' : 'text-(--text-muted) hover:bg-(--card-hover)'"
          @click="activeFilter = 'all'"
        >
          All
        </button>
        <button
          class="px-6 py-3.5 text-[11px] uppercase tracking-widest border-r border-(--text-light) transition-colors"
          :class="activeFilter === 'available' ? 'bg-(--card-hover) text-(--text) font-medium' : 'text-(--text-muted) hover:bg-(--card-hover)'"
          @click="activeFilter = 'available'"
        >
          Available
        </button>
        <button
          class="px-6 py-3.5 text-[11px] uppercase tracking-widest border-r border-(--text-light) transition-colors"
          :class="activeFilter === 'mine' ? 'bg-(--card-hover) text-(--text) font-medium' : 'text-(--text-muted) hover:bg-(--card-hover)'"
          @click="activeFilter = 'mine'"
        >
          Mine
        </button>
        <span class="ml-auto pr-6 text-[11px] text-(--text-light) uppercase tracking-widest">
          {{ filteredOrders().length }} orders
        </span>
      </div>

      <!-- Table header -->
      <div class="grid grid-cols-[2fr_1.5fr_1fr_1.2fr_1.5fr_1.8fr] px-6 py-2.5 border-b border-(--text-light)">
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Order ID</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Date</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Total</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Status</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Assigned To</span>
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
        v-else-if="filteredOrders().length === 0"
        class="px-6 py-10 text-[13px] text-(--text-light)"
      >
        No orders found.
      </div>

      <!-- Rows -->
      <div
        v-for="(order, i) in filteredOrders()"
        :key="order.orderID"
        class="grid grid-cols-[2fr_1.5fr_1fr_1.2fr_1.5fr_1.8fr] px-6 py-4 border-b border-(--text-light) last:border-b-0 hover:bg-(--card-hover) transition-colors items-center row-card"
        :style="{ animationDelay: `${0.3 + i * 0.04}s` }"
      >
        <span class="text-[13px] text-(--text) font-light tracking-wide font-mono">#{{ order.orderID.slice(0, 13) }}…</span>
        <span class="text-[13px] text-(--text-muted) font-light">{{ formatDate(order.orderDate) }}</span>
        <span class="text-[13px] text-(--text-muted) font-light">${{ order.totalPrice?.toFixed(2) ?? '-' }}</span>
        <span
          class="text-[11px] uppercase tracking-widest"
          :class="{
            'text-(--button-hover) font-bold': order.orderStatus === 'Delivered',
            'text-(--text-light) line-through': order.orderStatus === 'Cancelled',
            'text-(--text)': order.orderStatus === 'Preparing',
          }"
        >{{ order.orderStatus }}</span>
        <span
          v-if="order.employeeID"
          class="text-[12px] text-(--text-muted) tracking-wide capitalize"
        >{{ getEmployeeName(order.employeeID) }}</span>
        <span
          v-else
          class="text-[12px] text-(--text-light)"
        >-</span>
        <div class="flex items-center gap-2">
          <button
            class="text-[11px] uppercase tracking-widest border border-(--text-light) px-3 py-1.5 text-(--text) hover:bg-(--button-hover) hover:text-(--bg) transition-colors"
            @click="router.push({ name: 'order-detail', params: { orderID: order.orderID } })"
          >
            View
          </button>
          <button
            v-if="canAssignSelf(order)"
            :disabled="updatingStatus === order.orderID"
            class="text-[11px] uppercase tracking-widest border border-(--text-light) px-3 py-1.5 text-(--text) hover:bg-(--button-hover) hover:text-(--bg) transition-colors disabled:opacity-40"
            @click="assignSelf(order)"
          >
            Assign Me
          </button>
        </div>
      </div>
    </div>
  </main>
</template>

<style scoped>
.orders-heading {
  font-family: 'Lexend Deca', sans-serif;
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
