<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ArrowLeft, User } from 'lucide-vue-next'
import { api } from '@/api/client.ts'
import type { EmployeeResponseDto } from '@/api/types/person.ts'
import type { OrderResponseDto } from '@/api/types/order.ts'
import { useToastStore } from '@/stores/toast.ts'

const toast = useToastStore()


// ── Data ─────────────────────────────────────────────────────────────────────
const employees = ref<EmployeeResponseDto[]>([])
const loading = ref(true)


const currentView = ref<'list' | 'detail'>('list')
const selectedEmployee = ref<EmployeeResponseDto | null>(null)
const employeeOrders = ref<OrderResponseDto[]>([])
const detailLoading = ref(false)
const detailError = ref<string | null>(null)
const actionId = ref<string | null>(null)

async function fireEmployee(employee: EmployeeResponseDto) {
  actionId.value = employee.personId
  try {
    await api(`/persons/${employee.personId}/roles/employee`, { method: 'DELETE' })
    employees.value = employees.value.filter(e => e.personId !== employee.personId)
    toast.success(`Fired ${employee.email}.`)
  } catch (e: unknown) {
    toast.error(e instanceof Error ? e.message : 'Failed to remove employee role.')
  } finally {
    actionId.value = null
  }
}


onMounted(async () => {
  try {
    employees.value = await api<EmployeeResponseDto[]>('/persons/employees')
  } catch {
    // leave empty
  } finally {
    loading.value = false
  }
})


// ── Computed ──────────────────────────────────────────────────────────────────
const totalEmployees = computed(() => employees.value.length)


const employeeOrderCount = computed(() => employeeOrders.value.length)
const employeeDelivered = computed(() =>
    employeeOrders.value.filter(o => o.orderStatus === 'Delivered').length,
)
const employeePreparing = computed(() =>
    employeeOrders.value.filter(o => o.orderStatus === 'Preparing').length,
)
const employeeCancelled = computed(() =>
    employeeOrders.value.filter(o => o.orderStatus === 'Cancelled').length,
)


// ── Open detail ───────────────────────────────────────────────────────────────
async function openDetail(employee: EmployeeResponseDto) {
  selectedEmployee.value = employee
  currentView.value = 'detail'
  detailLoading.value = true
  detailError.value = null
  employeeOrders.value = []
  try {
    employeeOrders.value = await api<OrderResponseDto[]>(`/orders/employee/${employee.id}`)
  } catch (e: unknown) {
    detailError.value = e instanceof Error ? e.message : 'Failed to load orders.'
  } finally {
    detailLoading.value = false
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
    <h1 class="page-heading text-[40px] lg:text-[52px] font-normal tracking-tight leading-tight mb-2">
      <span class="text-(--text-muted)">Employee</span>
      <span class="text-(--button-hover)"> Directory</span>
    </h1>
    <div class="flex items-center justify-between mb-10 pb-6 border-b border-(--text-light)">
      <p class="text-md font-light text-(--text-muted)">
        View all employees and their assigned order history.
      </p>
      <span class="text-[12px] text-(--text-light) uppercase tracking-widest hidden sm:inline">Employees</span>
    </div>


    <!-- Stats -->
    <div class="grid grid-cols-1 gap-0 border border-(--text-light) mb-10">
      <div
        class="stat-card p-7"
        style="animation-delay: 0s"
      >
        <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
          Total Employees
        </p>
        <p class="text-[44px] font-light text-(--text) leading-none">
          {{ loading ? '-' : totalEmployees }}
        </p>
      </div>
    </div>


    <!-- ── Employee list ── -->
    <div
      v-if="currentView === 'list'"
      class="stat-card border border-(--text-light)"
      style="animation-delay: 0.1s"
    >
      <!-- Table header -->
      <div class="grid grid-cols-[3fr_2fr_1.5fr] px-6 py-2.5 border-b border-(--text-light)">
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Email</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Person ID</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Actions</span>
      </div>


      <div
        v-if="loading"
        class="px-6 py-10 text-[13px] text-(--text-light)"
      >
        Loading employees…
      </div>
      <div
        v-else-if="employees.length === 0"
        class="px-6 py-10 text-[13px] text-(--text-light)"
      >
        No employees found.
      </div>


      <div
        v-for="(employee, i) in employees"
        :key="employee.id"
        class="grid grid-cols-[3fr_2fr_1.5fr] px-6 py-4 border-b border-(--text-light) last:border-b-0 hover:bg-(--card-hover) transition-colors items-center row-card"
        :style="{ animationDelay: `${0.1 + i * 0.04}s` }"
      >
        <div class="flex items-center gap-3">
          <div class="w-7 h-7 border border-(--text-light) flex items-center justify-center shrink-0">
            <User class="w-3.5 h-3.5 text-(--text-muted)" />
          </div>
          <span class="text-[13px] text-(--text) font-light">{{ employee.email }}</span>
        </div>
        <span class="text-[12px] text-(--text-light) font-mono">{{ employee.personId }}</span>
        <div class="flex items-center gap-2">
          <button
            class="text-[11px] uppercase tracking-widest text-(--text) border border-(--text-light) px-3 py-1.5 hover:bg-(--text) hover:text-(--bg) transition-colors"
            @click="openDetail(employee)"
          >
            View
          </button>
          <button
            class="text-[11px] uppercase tracking-widest text-red-400 border border-red-400 px-3 py-1.5 hover:bg-red-400 hover:text-white transition-colors disabled:opacity-40"
            :disabled="actionId === employee.personId"
            @click="fireEmployee(employee)"
          >
            {{ actionId === employee.personId ? '…' : 'Fire' }}
          </button>
        </div>
      </div>
    </div>


    <!-- ── Employee detail ── -->
    <div
      v-else-if="currentView === 'detail' && selectedEmployee"
      class="stat-card border border-(--text-light)"
      style="animation-delay: 0.1s"
    >
      <!-- Header -->
      <div class="flex items-center justify-between px-8 py-5 border-b border-(--text-light)">
        <button
          class="flex items-center gap-2 text-[11px] uppercase tracking-widest text-(--text-light) hover:text-(--text-muted) transition-colors"
          @click="currentView = 'list'"
        >
          <ArrowLeft class="w-3.5 h-3.5" /> Back to Employees
        </button>
        <span class="text-[10px] uppercase tracking-[0.2em] text-(--text-light)">
          Employee <strong class="text-(--text-muted)">{{ selectedEmployee.email }}</strong>
        </span>
      </div>


      <div class="px-8 pt-8 pb-8">
        <!-- Profile info -->
        <div class="grid grid-cols-2 gap-0 border border-(--text-light) mb-8">
          <div
            class="stat-card p-6 border-r border-(--text-light)"
            style="animation-delay: 0s"
          >
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Email
            </p>
            <p class="text-[13px] font-light text-(--text) break-all">
              {{ selectedEmployee.email }}
            </p>
          </div>
          <div
            class="stat-card p-6"
            style="animation-delay: 0.07s"
          >
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Person ID
            </p>
            <p class="text-[13px] font-light text-(--text) font-mono break-all">
              {{ selectedEmployee.personId }}
            </p>
          </div>
        </div>


        <!-- Order stats -->
        <div class="grid grid-cols-4 gap-0 border border-(--text-light) mb-8">
          <div
            class="stat-card p-6 border-r border-(--text-light)"
            style="animation-delay: 0.14s"
          >
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Total Assigned
            </p>
            <p class="text-[32px] font-light text-(--text) leading-none">
              {{ detailLoading ? '-' : employeeOrderCount }}
            </p>
          </div>
          <div
            class="stat-card p-6 border-r border-(--text-light)"
            style="animation-delay: 0.21s"
          >
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Preparing
            </p>
            <p class="text-[32px] font-light text-(--text) leading-none">
              {{ detailLoading ? '-' : employeePreparing }}
            </p>
          </div>
          <div
            class="stat-card p-6 border-r border-(--text-light)"
            style="animation-delay: 0.28s"
          >
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Delivered
            </p>
            <p class="text-[32px] font-light text-(--text) leading-none">
              {{ detailLoading ? '-' : employeeDelivered }}
            </p>
          </div>
          <div
            class="stat-card p-6"
            style="animation-delay: 0.35s"
          >
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Cancelled
            </p>
            <p class="text-[32px] font-light text-(--text) leading-none">
              {{ detailLoading ? '-' : employeeCancelled }}
            </p>
          </div>
        </div>


        <!-- Orders table -->
        <div
          class="stat-card border border-(--text-light)"
          style="animation-delay: 0.42s"
        >
          <div class="px-6 py-3 border-b border-(--text-light)">
            <span class="text-[10px] uppercase tracking-[0.2em] text-(--text-light)">Assigned Orders</span>
          </div>


          <div class="grid grid-cols-[2fr_1.5fr_1fr_1.2fr_2fr] px-6 py-2.5 border-b border-(--text-light)">
            <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Order ID</span>
            <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Date</span>
            <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Total</span>
            <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Status</span>
            <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Delivery Date</span>
          </div>


          <div
            v-if="detailLoading"
            class="px-6 py-10 text-[13px] text-(--text-light)"
          >
            Loading orders…
          </div>
          <div
            v-else-if="detailError"
            class="px-6 py-10 text-[13px] text-red-400"
          >
            {{ detailError }}
          </div>
          <div
            v-else-if="employeeOrders.length === 0"
            class="px-6 py-10 text-[13px] text-(--text-light)"
          >
            No orders assigned.
          </div>


          <div
            v-for="(order, i) in employeeOrders"
            :key="order.orderID"
            class="grid grid-cols-[2fr_1.5fr_1fr_1.2fr_2fr] px-6 py-4 border-b border-(--text-light) last:border-b-0 hover:bg-(--card-hover) transition-colors items-center row-card"
            :style="{ animationDelay: `${0.42 + i * 0.04}s` }"
          >
            <span class="text-[13px] text-(--text) font-light font-mono">#{{ order.orderID.slice(0, 12) }}…</span>
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
            <span class="text-[13px] text-(--text-muted) font-light">{{ formatDate(order.deliveryDate) }}</span>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>


<style scoped>
.page-heading {
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

