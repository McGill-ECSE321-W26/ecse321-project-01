<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ArrowLeft, User } from 'lucide-vue-next'
import { api } from '@/api/client.ts'
import type { OrderResponseDto } from '@/api/types/order.ts'
import type { EmployeeResponseDto } from '@/api/types/person.ts'
import type { ItemResponseDto } from '@/api/types/item.ts'
import type { ClothingModelResponseDto, ClothingVariantResponseDto } from '@/api/types/clothing.ts'

// ── Data ────────────────────────────────────────────────────────────────────
const orders = ref<OrderResponseDto[]>([])
const employees = ref<EmployeeResponseDto[]>([])
const loading = ref(true)
const activeFilter = ref<'all' | 'unassigned'>('all')
const updatingStatus = ref<string | null>(null)

// view: 'list' | 'detail' | 'assign'
const currentView = ref<'list' | 'detail' | 'assign'>('list')
const selectedOrder = ref<OrderResponseDto | null>(null)

// detail panel state
const detailItems = ref<ItemResponseDto[]>([])
const detailLoading = ref(false)
const detailError = ref<string | null>(null)
const variantMap = ref(new Map<string, { name: string; size: string; color: string }>())

// assign panel state
const assigning = ref(false)
const assignError = ref<string | null>(null)

onMounted(async () => {
  try {
    const [ordersData, employeesData] = await Promise.all([
      api<OrderResponseDto[]>('/orders'),
      api<EmployeeResponseDto[]>('/persons/employees'),
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

// ── View order detail ─────────────────────────────────────────────────────────
async function openDetail(order: OrderResponseDto) {
  selectedOrder.value = order
  currentView.value = 'detail'
  detailLoading.value = true
  detailError.value = null
  detailItems.value = []
  try {
    const [itemsData, models] = await Promise.all([
      api<ItemResponseDto[]>(`/orders/${order.orderID}/items`),
      api<ClothingModelResponseDto[]>('/clothing'),
    ])
    detailItems.value = itemsData

    const variantLists = await Promise.all(
      models.map(m =>
        api<ClothingVariantResponseDto[]>(`/clothing/${m.clothingModelID}/variants`).then(
          variants => ({ modelName: m.name, variants }),
        ),
      ),
    )
    const map = new Map<string, { name: string; size: string; color: string }>()
    for (const { modelName, variants } of variantLists) {
      for (const v of variants) {
        map.set(v.clothingVariantID, { name: modelName, size: v.size, color: v.color })
      }
    }
    variantMap.value = map
  } catch (e: unknown) {
    detailError.value = e instanceof Error ? e.message : 'Failed to load order details.'
  } finally {
    detailLoading.value = false
  }
}

function getVariantInfo(clothingVariantID: string | null) {
  if (!clothingVariantID) return { name: 'Unknown', size: '-', color: '-' }
  return variantMap.value.get(clothingVariantID) ?? { name: 'Unknown', size: '-', color: '-' }
}

// ── Assign employee ───────────────────────────────────────────────────────────
function openAssign() {
  assignError.value = null
  currentView.value = 'assign'
}

function cancelAssign() {
  assignError.value = null
  currentView.value = 'detail'
}

async function assignEmployee(employee: EmployeeResponseDto) {
  if (!selectedOrder.value) return
  assigning.value = true
  assignError.value = null
  try {
    const updated = await api<OrderResponseDto>(`/orders/${selectedOrder.value.orderID}`, {
      method: 'PATCH',
      body: JSON.stringify({
        employeeID: employee.id,
        orderStatus: 'Preparing',
      }),
    })
    const idx = orders.value.findIndex(o => o.orderID === updated.orderID)
    if (idx !== -1) orders.value[idx] = updated
    selectedOrder.value = updated
    currentView.value = 'detail'
  } catch (e: unknown) {
    assignError.value = e instanceof Error ? e.message : 'Failed to assign employee.'
  } finally {
    assigning.value = false
  }
}

// ── Update status ─────────────────────────────────────────────────────────────
async function updateStatus(order: OrderResponseDto, status: string) {
  updatingStatus.value = order.orderID
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
    if (selectedOrder.value?.orderID === updated.orderID) {
      selectedOrder.value = updated
    }
  } catch {
    // silently ignore
  } finally {
    updatingStatus.value = null
  }
}

function formatDate(d: Date | null) {
  if (!d) return '-'
  return new Date(d).toLocaleDateString('en-CA', { year: 'numeric', month: 'short', day: 'numeric' })
}

</script>

<template>
  <main class="flex-1 px-10 pt-16 pb-20">
    <!-- Heading -->
    <h1 class="orders-heading text-[40px] lg:text-[52px] font-normal tracking-tight leading-tight mb-2">
      <span class="text-(--text-muted)">Order</span>
      <em class="text-(--text-light)"> Management</em>
    </h1>
    <div class="flex items-center justify-between mb-10 pb-6 border-b border-(--text-light)">
      <p class="text-sm font-light text-(--text-muted)">
        Assign employees and track every order.
      </p>
      <span class="text-[12px] text-(--text-light) uppercase tracking-widest hidden sm:inline">Orders</span>
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
          Not Assigned
        </p>
        <p class="text-[44px] font-light text-(--text) leading-none">
          {{ loading ? '-' : notAssigned }}
        </p>
      </div>
      <div
        class="stat-card p-7"
        style="animation-delay: 0.2s"
      >
        <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
          Completed
        </p>
        <p class="text-[44px] font-light text-(--text) leading-none">
          {{ loading ? '-' : completed }}
        </p>
      </div>
    </div>

    <!-- Order table view -->
    <div
      v-if="currentView === 'list'"
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
      <div class="grid grid-cols-[2fr_1.5fr_1fr_1.2fr_1.5fr_0.8fr] px-6 py-2.5 border-b border-(--text-light)">
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Order ID</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Date</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Total</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Status</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Employee</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">View</span>
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
        v-else-if="filteredOrders.length === 0"
        class="px-6 py-10 text-[13px] text-(--text-light)"
      >
        No orders found.
      </div>

      <!-- Rows -->
      <div
        v-for="(order, i) in filteredOrders"
        :key="order.orderID"
        class="grid grid-cols-[2fr_1.5fr_1fr_1.2fr_1.5fr_0.8fr] px-6 py-4 border-b border-(--text-light) last:border-b-0 hover:bg-(--card-hover) transition-colors items-center row-card"
        :style="{ animationDelay: `${0.3 + i * 0.04}s` }"
      >
        <span class="text-[13px] text-(--text) font-light tracking-wide">#{{ order.orderID }}</span>
        <span class="text-[13px] text-(--text-muted) font-light">{{ formatDate(order.orderDate) }}</span>
        <span class="text-[13px] text-(--text-muted) font-light">${{ order.totalPrice?.toFixed(2) ?? '-' }}</span>
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
        <span
          v-if="order.employeeID"
          class="text-[12px] text-(--text-muted) tracking-wide"
        >
          #{{ order.employeeID.slice(0, 8) }}…
        </span>
        <span
          v-else
          class="text-[12px] text-(--text-light)"
        >
          -
        </span>
        <button
          class="text-[11px] uppercase tracking-widest text-(--text) border border-(--text-light) px-3 py-1.5 hover:bg-(--text) hover:text-(--bg) transition-colors w-fit"
          @click="openDetail(order)"
        >
          View
        </button>
      </div>
    </div>

    <!-- Order detail panel -->
    <div
      v-else-if="currentView === 'detail' && selectedOrder"
      class="stat-card border border-(--text-light)"
      style="animation-delay: 0.3s"
    >
      <!-- Header -->
      <div class="flex items-center justify-between px-8 py-5 border-b border-(--text-light)">
        <button
          class="flex items-center gap-2 text-[11px] uppercase tracking-widest text-(--text-light) hover:text-(--text-muted) transition-colors"
          @click="currentView = 'list'"
        >
          <ArrowLeft class="w-3.5 h-3.5" /> Back to Orders
        </button>
        <span class="text-[10px] uppercase tracking-[0.2em] text-(--text-light)">
          Order <strong class="text-(--text-muted)">#{{ selectedOrder.orderID }}</strong>
        </span>
      </div>

      <div class="px-8 pt-8 pb-8">
        <!-- Order stats -->
        <div class="grid grid-cols-[2fr_2fr_1fr_1fr] gap-0 border border-(--text-light) mb-8">
          <div
            class="stat-card p-6 border-r border-(--text-light)"
            style="animation-delay: 0s"
          >
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Order ID
            </p>
            <p class="text-[11px] font-light text-(--text) font-mono leading-relaxed break-all">
              {{ selectedOrder.orderID }}
            </p>
          </div>
          <div
            class="stat-card p-6 border-r border-(--text-light)"
            style="animation-delay: 0.07s"
          >
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Address
            </p>
            <p class="text-[13px] font-light text-(--text) leading-snug">
              {{ selectedOrder.address }}
            </p>
          </div>
          <div
            class="stat-card p-6 border-r border-(--text-light)"
            style="animation-delay: 0.14s"
          >
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Total
            </p>
            <p class="text-[32px] font-light text-(--text) leading-none">
              ${{ selectedOrder.totalPrice.toFixed(2) }}
            </p>
          </div>
          <div
            class="stat-card p-6"
            style="animation-delay: 0.21s"
          >
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Money Saved
            </p>
            <p class="text-[32px] font-light text-(--text) leading-none">
              ${{ (selectedOrder.loyaltySaving ?? 0).toFixed(2) }}
            </p>
          </div>
        </div>

        <!-- Status + employee row -->
        <div class="grid grid-cols-[1fr_1fr_auto] gap-0 border border-(--text-light) mb-8">
          <div class="p-6 border-r border-(--text-light)">
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Status
            </p>
            <span
              class="text-[13px] uppercase tracking-widest font-light"
              :class="{
                'text-(--text-muted)': selectedOrder.orderStatus === 'Delivered',
                'text-(--text-light) line-through': selectedOrder.orderStatus === 'Cancelled',
                'text-(--text)': selectedOrder.orderStatus === 'Preparing',
              }"
            >{{ selectedOrder.orderStatus }}</span>
          </div>
          <div class="p-6 border-r border-(--text-light)">
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Employee
            </p>
            <span
              v-if="selectedOrder.employeeID"
              class="text-[13px] font-light text-(--text) font-mono"
            >#{{ selectedOrder.employeeID }}</span>
            <span
              v-else
              class="text-[13px] font-light text-(--text-light)"
            >Unassigned</span>
          </div>
          <div class="p-6 flex items-end gap-2">
            <button
              v-if="!selectedOrder.employeeID && selectedOrder.orderStatus !== 'Cancelled'"
              class="text-[11px] uppercase tracking-widest text-(--text) border border-(--text-light) px-4 py-2 hover:bg-(--text) hover:text-(--bg) transition-colors"
              @click="openAssign"
            >
              Assign Employee
            </button>
            <button
              v-if="selectedOrder.orderStatus === 'Preparing' && selectedOrder.employeeID !== null"
              :disabled="updatingStatus === selectedOrder.orderID"
              class="text-[11px] uppercase tracking-widest border border-(--text-light) px-4 py-2 text-(--text) hover:bg-(--text) hover:text-(--bg) transition-colors disabled:opacity-40"
              @click="updateStatus(selectedOrder, 'Delivered')"
            >
              Mark Delivered
            </button>
            <button
              v-if="selectedOrder.orderStatus !== 'Cancelled' && selectedOrder.orderStatus !== 'Delivered'"
              :disabled="updatingStatus === selectedOrder.orderID"
              class="text-[11px] uppercase tracking-widest border border-(--text-light) px-4 py-2 text-(--text) hover:bg-destructive hover:text-(--bg) transition-colors disabled:opacity-40"
              @click="updateStatus(selectedOrder, 'Cancelled')"
            >
              Cancel Order
            </button>
          </div>
        </div>

        <!-- Items table -->
        <div
          class="stat-card border border-(--text-light)"
          style="animation-delay: 0.28s"
        >
          <div class="px-6 py-3 border-b border-(--text-light)">
            <span class="text-[10px] uppercase tracking-[0.2em] text-(--text-light)">Order Items</span>
          </div>

          <!-- Table header -->
          <div class="grid grid-cols-[3fr_1fr_1.5fr_1fr_0.6fr] px-6 py-2.5 border-b border-(--text-light)">
            <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Name</span>
            <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Size</span>
            <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Color</span>
            <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Unit Price</span>
            <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Qty</span>
          </div>

          <div
            v-if="detailLoading"
            class="px-6 py-10 text-[13px] text-(--text-light)"
          >
            Loading items…
          </div>
          <div
            v-else-if="detailError"
            class="px-6 py-10 text-[13px] text-red-400"
          >
            {{ detailError }}
          </div>
          <div
            v-else-if="detailItems.length === 0"
            class="px-6 py-10 text-[13px] text-(--text-light)"
          >
            No items found.
          </div>

          <div
            v-for="(item, i) in detailItems"
            :key="item.itemID"
            class="grid grid-cols-[3fr_1fr_1.5fr_1fr_0.6fr] px-6 py-4 border-b border-(--text-light) last:border-b-0 hover:bg-(--card-hover) transition-colors items-center row-card"
            :style="{ animationDelay: `${0.28 + i * 0.04}s` }"
          >
            <span class="text-[13px] text-(--text) font-light">{{ getVariantInfo(item.clothingVariantID).name }}</span>
            <span class="text-[13px] text-(--text-muted) font-light uppercase">{{ getVariantInfo(item.clothingVariantID).size }}</span>
            <span class="text-[13px] text-(--text-muted) font-light capitalize">{{ getVariantInfo(item.clothingVariantID).color }}</span>
            <span class="text-[13px] text-(--text-muted) font-light">${{ item.price.toFixed(2) }}</span>
            <span class="text-[13px] text-(--text-muted) font-light">× {{ item.quantity }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Assign employee panel -->
    <div
      v-else-if="currentView === 'assign' && selectedOrder"
      class="stat-card border border-(--text-light)"
      style="animation-delay: 0.3s"
    >
      <!-- Header -->
      <div class="flex items-center gap-4 px-8 py-5 border-b border-(--text-light)">
        <button
          class="flex items-center gap-2 text-[11px] uppercase tracking-widest text-(--text-light) hover:text-(--text-muted) transition-colors"
          @click="cancelAssign"
        >
          <ArrowLeft class="w-3.5 h-3.5" /> Back
        </button>
        <span class="text-[10px] uppercase tracking-[0.2em] text-(--text-light)">
          Assigning order <strong class="text-(--text-muted)">#{{ selectedOrder.orderID }}</strong>
        </span>
      </div>

      <!-- Section label -->
      <div class="px-8 pt-8 pb-4">
        <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-6">
          Available Employees
        </p>

        <!-- Error -->
        <p
          v-if="assignError"
          class="text-[12px] text-red-400 mb-4"
        >
          {{ assignError }}
        </p>

        <!-- Employee grid -->
        <div
          v-if="employees.length === 0"
          class="text-[13px] text-(--text-light) py-6"
        >
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
              <p class="text-[12px] text-(--text) leading-snug break-all">
                {{ emp.email }}
              </p>
              <p class="text-[8px] uppercase tracking-widest text-(--text-light) mt-1">
                Click to select
              </p>
            </div>
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
.row-card,
.employee-card {
  animation: fadeUp 0.6s ease both;
}
</style>
