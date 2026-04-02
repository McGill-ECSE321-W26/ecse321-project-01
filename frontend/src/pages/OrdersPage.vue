<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import {
  ShoppingBag, Boxes, Users, UserCog, LayoutDashboard,
  ShieldCheck, ArrowLeft, User,
} from 'lucide-vue-next'
import { api } from '@/api/client'
import type { OrderResponseDto, PersonResponseDto } from '@/api/types'

const route = useRoute()

// ── Data ────────────────────────────────────────────────────────────────────
const orders = ref<OrderResponseDto[]>([])
const employees = ref<PersonResponseDto[]>([])
const loading = ref(true)
const assigningOrder = ref<OrderResponseDto | null>(null)
const assigning = ref(false)
const assignError = ref<string | null>(null)
const activeFilter = ref<'all' | 'unassigned'>('all')
const updatingStatus = ref<string | null>(null)

onMounted(async () => {
  try {
    const [ordersData, employeesData] = await Promise.all([
      api<OrderResponseDto[]>('/orders'),
      api<PersonResponseDto[]>('/persons/employees'),
    ])
    orders.value = ordersData
    employees.value = employeesData
  } catch {
    // leave empty
  } finally {
    loading.value = false
  }
})

// ── Computed ─────────────────────────────────────────────────────────────────
const totalOrders = computed(() => orders.value.length)
const notAssigned = computed(() => orders.value.filter(o => !o.employeeID).length)
const completed = computed(() => orders.value.filter(o => o.orderStatus === 'Delivered').length)

const filteredOrders = computed(() =>
  activeFilter.value === 'unassigned'
    ? orders.value.filter(o => !o.employeeID)
    : orders.value,
)

// ── Actions ──────────────────────────────────────────────────────────────────
function openAssign(order: OrderResponseDto) {
  assigningOrder.value = order
  assignError.value = null
}

function cancelAssign() {
  assigningOrder.value = null
  assignError.value = null
}

async function assignEmployee(employee: PersonResponseDto) {
  if (!assigningOrder.value) return
  assigning.value = true
  assignError.value = null
  try {
    const updated = await api<OrderResponseDto>(`/orders/${assigningOrder.value.orderID}`, {
      method: 'PATCH',
      body: JSON.stringify({ 
        employeeID: employee.employeeRoleId,
        orderStatus: "Preparing",
      }),
    })
    const idx = orders.value.findIndex(o => o.orderID === updated.orderID)
    if (idx !== -1) orders.value[idx] = updated
    assigningOrder.value = null
  } catch (e: any) {
    assignError.value = e?.message ?? 'Failed to assign employee.'
  } finally {
    assigning.value = false
  }
}

async function updateStatus(order: OrderResponseDto, status: string) {
  updatingStatus.value = order.orderID
  console.log(order)
  try {
    const updated = await api<OrderResponseDto>(`/orders/${order.orderID}`, {
      method: 'PATCH',
      body: JSON.stringify({
        employeeID: order.employeeID,
        deliveryDate: order.deliveryDate,
        orderStatus: status,
      }),
    })
    const idx = orders.value.findIndex(o => o.orderID === updated.orderID)
    if (idx !== -1) orders.value[idx] = updated
  } catch {
    // silently ignore — could add error toast here
  } finally {
    updatingStatus.value = null
  }
}

function formatDate(d: Date | null) {
  if (!d) return '—'
  return new Date(d).toLocaleDateString('en-CA', { year: 'numeric', month: 'short', day: 'numeric' })
}

// ── Sidebar ──────────────────────────────────────────────────────────────────
const navItems = [
  { key: 'dashboard', label: 'Dashboard', icon: LayoutDashboard, to: '/manager' },
  { key: 'orders', label: 'Orders', icon: ShoppingBag, to: '/manager/orders' },
  { key: 'inventory', label: 'Inventory', icon: Boxes, to: '/shop' },
  { key: 'customers', label: 'Customers', icon: Users, to: null },
  { key: 'employees', label: 'Employees', icon: UserCog, to: null },
]

const activeSection = computed(() => {
  if (route.path === '/manager') return 'dashboard'
  if (route.path.startsWith('/manager/orders')) return 'orders'
  if (route.path.startsWith('/shop')) return 'inventory'
  return ''
})
</script>

<template>
  <div class="flex min-h-screen bg-(--bg)">
    <!-- Sidebar -->
    <aside class="w-52 shrink-0 border-r border-(--text-light) flex flex-col pt-8 gap-1">
      <div class="flex flex-col items-center gap-2 pb-6 border-b border-(--text-light) px-4">
        <div class="w-12 h-12 border border-(--text-light) flex items-center justify-center">
          <ShieldCheck class="w-5 h-5 text-(--text-muted)" />
        </div>
        <span class="text-[10px] text-(--text-light) uppercase tracking-[0.2em]">Manager</span>
      </div>

      <nav class="flex flex-col gap-0 px-0 pt-2">
        <template v-for="item in navItems" :key="item.key">
          <component
            :is="item.to ? RouterLink : 'button'"
            v-bind="item.to ? { to: item.to } : {}"
            class="flex items-center gap-3 px-5 py-3 text-[13px] tracking-wide border-b border-(--text-light) transition-colors w-full text-left"
            :class="
              activeSection === item.key
                ? 'bg-(--card-hover) text-(--text) font-medium'
                : 'text-(--text-muted) hover:bg-(--card-hover) hover:text-(--text)'
            "
          >
            <component :is="item.icon" class="w-4 h-4 shrink-0" />
            {{ item.label }}
          </component>
        </template>
      </nav>
    </aside>

    <!-- Main -->
    <main class="flex-1 px-10 pt-16 pb-20">

      <!-- Heading -->
      <h1 class="orders-heading text-[40px] lg:text-[52px] font-normal tracking-tight leading-tight mb-2">
        <span class="text-(--text-muted)">Order</span>
        <em class="text-(--text-light)"> Management</em>
      </h1>
      <div class="flex items-center justify-between mb-10 pb-6 border-b border-(--text-light)">
        <p class="text-sm font-light text-(--text-muted)">Assign employees and track every order.</p>
        <span class="text-[12px] text-(--text-light) uppercase tracking-widest hidden sm:inline">Orders</span>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-3 gap-0 border border-(--text-light) mb-10">
        <div class="stat-card p-7 border-r border-(--text-light)" style="animation-delay: 0s">
          <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">Total Orders</p>
          <p class="text-[44px] font-light text-(--text) leading-none">{{ loading ? '—' : totalOrders }}</p>
        </div>
        <div class="stat-card p-7 border-r border-(--text-light)" style="animation-delay: 0.1s">
          <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">Not Assigned</p>
          <p class="text-[44px] font-light text-(--text) leading-none">{{ loading ? '—' : notAssigned }}</p>
        </div>
        <div class="stat-card p-7" style="animation-delay: 0.2s">
          <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">Completed</p>
          <p class="text-[44px] font-light text-(--text) leading-none">{{ loading ? '—' : completed }}</p>
        </div>
      </div>

      <!-- Order table view -->
      <div v-if="!assigningOrder" class="stat-card border border-(--text-light)" style="animation-delay: 0.3s">
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
            :class="activeFilter === 'unassigned' ? 'bg-(--card-hover) text-(--text) font-medium' : 'text-(--text-muted) hover:bg-(--card-hover)'"
            @click="activeFilter = 'unassigned'"
          >
            Not Assigned
          </button>
          <span class="ml-auto pr-6 text-[11px] text-(--text-light) uppercase tracking-widest">
            {{ filteredOrders.length }} orders
          </span>
        </div>

        <!-- Table header -->
        <div class="grid grid-cols-[2fr_1.5fr_1fr_1.2fr_1.5fr_1.5fr] px-6 py-2.5 border-b border-(--text-light)">
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Order ID</span>
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Date</span>
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Total</span>
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Status</span>
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Employee</span>
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Actions</span>
        </div>

        <!-- Loading -->
        <div v-if="loading" class="px-6 py-10 text-[13px] text-(--text-light)">Loading orders…</div>

        <!-- Empty -->
        <div v-else-if="filteredOrders.length === 0" class="px-6 py-10 text-[13px] text-(--text-light)">
          No orders found.
        </div>

        <!-- Rows -->
        <div
          v-for="(order, i) in filteredOrders"
          :key="order.orderID"
          class="grid grid-cols-[2fr_1.5fr_1fr_1.2fr_1.5fr_1.5fr] px-6 py-4 border-b border-(--text-light) last:border-b-0 hover:bg-(--card-hover) transition-colors items-center row-card"
          :style="{ animationDelay: `${0.3 + i * 0.04}s` }"
        >
          <span class="text-[13px] text-(--text) font-light tracking-wide">#{{ order.orderID }}</span>
          <span class="text-[13px] text-(--text-muted) font-light">{{ formatDate(order.orderDate) }}</span>
          <span class="text-[13px] text-(--text-muted) font-light">${{ order.totalPrice?.toFixed(2) ?? '—' }}</span>
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
          <div>
            <span v-if="order.employeeID" class="text-[12px] text-(--text-muted) tracking-wide">
              #{{ order.employeeID.slice(0, 8) }}…
            </span>
            <button
              v-else-if="order.orderStatus !== 'Cancelled'"
              class="text-[11px] uppercase tracking-widest text-(--text) border border-(--text-light) px-3 py-1.5 hover:bg-(--text) hover:text-(--bg) transition-colors"
              @click="openAssign(order)"
            >
              Assign
            </button>
          </div>
          <div class="flex items-center gap-2">
            <button
              v-if="order.orderStatus === 'Preparing' && order.employeeID !== null"
              :disabled="updatingStatus === order.orderID"
              class="text-[11px] uppercase tracking-widest border border-(--text-light) px-3 py-1.5 text-(--text) hover:bg-(--text) hover:text-(--bg) transition-colors disabled:opacity-40"
              @click="updateStatus(order, 'Delivered')"
            >
              Delivered
            </button>
            <button
              v-if="order.orderStatus !== 'Cancelled' && order.orderStatus !== 'Delivered'"
              :disabled="updatingStatus === order.orderID"
              class="text-[11px] uppercase tracking-widest border border-(--text-light) px-3 py-1.5 text-(--text-light) hover:border-red-400 hover:text-red-400 transition-colors disabled:opacity-40"
              @click="updateStatus(order, 'Cancelled')"
            >
              Cancel
            </button>
          </div>
        </div>
      </div>

      <!-- Assign employee panel -->
      <div v-else class="stat-card border border-(--text-light)" style="animation-delay: 0.3s">
        <!-- Header -->
        <div class="flex items-center gap-4 px-8 py-5 border-b border-(--text-light)">
          <button
            class="flex items-center gap-2 text-[11px] uppercase tracking-widest text-(--text-light) hover:text-(--text-muted) transition-colors"
            @click="cancelAssign"
          >
            <ArrowLeft class="w-3.5 h-3.5" /> Back
          </button>
          <span class="text-[10px] uppercase tracking-[0.2em] text-(--text-light)">
            Assigning order <strong class="text-(--text-muted)">#{{ assigningOrder.orderID }}</strong>
          </span>
        </div>

        <!-- Section label -->
        <div class="px-8 pt-8 pb-4">
          <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-6">Available Employees</p>

          <!-- Error -->
          <p v-if="assignError" class="text-[12px] text-red-400 mb-4">{{ assignError }}</p>

          <!-- Employee grid -->
          <div v-if="employees.length === 0" class="text-[13px] text-(--text-light) py-6">
            No employees found.
          </div>
          <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4 pb-8">
            <button
              v-for="(emp, i) in employees"
              :key="emp.id"
              :disabled="assigning"
              class="employee-card group border border-(--text-light) p-5 flex flex-col items-center gap-3 text-center hover:bg-(--card-hover) hover:border-(--text-muted) transition-colors disabled:opacity-50"
              :style="{ animationDelay: `${0.35 + i * 0.05}s` }"
              @click="assignEmployee(emp)"
            >
              <div class="w-14 h-14 border border-(--text-light) group-hover:border-(--text-muted) flex items-center justify-center transition-colors">
                <User class="w-6 h-6 text-(--text-muted)" />
              </div>
              <div>
                <p class="text-[12px] text-(--text) leading-snug break-all">{{ emp.email }}</p>
                <p class="text-[10px] uppercase tracking-widest text-(--text-light) mt-1">Employee</p>
              </div>
              <span
                class="text-[10px] uppercase tracking-widest border border-(--text-light) px-2.5 py-1 group-hover:border-(--text-muted) group-hover:text-(--text-muted) text-(--text-light) transition-colors"
              >
                Select
              </span>
            </button>
          </div>
        </div>
      </div>
    </main>
  </div>
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
.row-card,
.employee-card {
  animation: fadeUp 0.6s ease both;
}
</style>
