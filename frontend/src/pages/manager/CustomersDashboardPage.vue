<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ArrowLeft, Star } from 'lucide-vue-next'
import { api } from '@/api/client.ts'
import type { CustomerResponseDto } from '@/api/types/person.ts'
import type { OrderResponseDto } from '@/api/types/order.ts'


// ── Data ─────────────────────────────────────────────────────────────────────
const customers = ref<CustomerResponseDto[]>([])
const loading = ref(true)


const currentView = ref<'list' | 'detail'>('list')
const selectedCustomer = ref<CustomerResponseDto | null>(null)
const customerOrders = ref<OrderResponseDto[]>([])
const detailLoading = ref(false)
const detailError = ref<string | null>(null)


onMounted(async () => {
  try {
    customers.value = await api<CustomerResponseDto[]>('/persons/customers')
  } catch {
    // leave empty
  } finally {
    loading.value = false
  }
})


// ── Computed ──────────────────────────────────────────────────────────────────
const totalCustomers = computed(() => customers.value.length)

const customerOrderCount = computed(() =>
    customerOrders.value.length,
)
const customerTotalSpent = computed(() =>
    customerOrders.value.reduce((sum, o) => sum + (o.totalPrice ?? 0), 0),
)
const customerOrdersDelivered = computed(() =>
    customerOrders.value.filter(o => o.orderStatus === 'Delivered').length,
)


// ── Open detail ───────────────────────────────────────────────────────────────
async function openDetail(customer: CustomerResponseDto) {
  selectedCustomer.value = customer
  currentView.value = 'detail'
  detailLoading.value = true
  detailError.value = null
  customerOrders.value = []
  try {
    customerOrders.value = await api<OrderResponseDto[]>(`/orders/customer/${customer.id}`)
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
      <span class="text-(--text-muted)">Customer</span>
      <span class="text-(--button-hover)"> Directory</span>
    </h1>
    <div class="flex items-center justify-between mb-10 pb-6 border-b border-(--text-light)">
      <p class="text-md font-light text-(--text-muted)">
        Browse registered customers and review their order history.
      </p>
      <span class="text-[12px] text-(--text-light) uppercase tracking-widest hidden sm:inline">Customers</span>
    </div>


    <!-- Stats -->
    <div class="grid grid-cols-3 gap-0 border border-(--text-light) mb-10">
      <div
        class="stat-card p-7"
        style="animation-delay: 0s"
      >
        <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
          Total Customers
        </p>
        <p class="text-[44px] font-light text-(--text) leading-none">
          {{ loading ? '-' : totalCustomers }}
        </p>
      </div>
    </div>


    <!-- ── Customer list ── -->
    <div
      v-if="currentView === 'list'"
      class="stat-card border border-(--text-light)"
      style="animation-delay: 0.3s"
    >
      <!-- Table header -->
      <div class="grid grid-cols-[2.5fr_2fr_1fr_0.6fr] px-6 py-2.5 border-b border-(--text-light)">
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Email</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Address</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Loyalty Pts</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">View</span>
      </div>


      <div
        v-if="loading"
        class="px-6 py-10 text-[13px] text-(--text-light)"
      >
        Loading customers…
      </div>
      <div
        v-else-if="customers.length === 0"
        class="px-6 py-10 text-[13px] text-(--text-light)"
      >
        No customers found.
      </div>


      <div
        v-for="(customer, i) in customers"
        :key="customer.id"
        class="grid grid-cols-[2.5fr_2fr_1fr_0.6fr] px-6 py-4 border-b border-(--text-light) last:border-b-0 hover:bg-(--card-hover) transition-colors items-center row-card"
        :style="{ animationDelay: `${0.3 + i * 0.04}s` }"
      >
        <span class="text-[13px] text-(--text) font-light">{{ customer.email }}</span>
        <span class="text-[13px] text-(--text-muted) font-light">{{ customer.address || '-' }}</span>
        <div class="flex items-center gap-1.5">
          <Star class="w-3 h-3 text-(--button-hover)" />
          <span class="text-[13px] text-(--button-hover) font-light">{{ customer.loyaltyPoints ?? 0 }}</span>
        </div>
        <button
          class="text-[11px] uppercase tracking-widest text-(--text) border border-(--text-light) px-3 py-1.5 hover:bg-(--text) hover:text-(--bg) transition-colors w-fit"
          @click="openDetail(customer)"
        >
          View
        </button>
      </div>
    </div>


    <!-- ── Customer detail ── -->
    <div
      v-else-if="currentView === 'detail' && selectedCustomer"
      class="stat-card border border-(--text-light)"
      style="animation-delay: 0.3s"
    >
      <!-- Header -->
      <div class="flex items-center justify-between px-8 py-5 border-b border-(--text-light)">
        <button
          class="flex items-center gap-2 text-[11px] uppercase tracking-widest text-(--text-light) hover:text-(--text-muted) transition-colors"
          @click="currentView = 'list'"
        >
          <ArrowLeft class="w-3.5 h-3.5" /> Back to Customers
        </button>
        <span class="text-[10px] uppercase tracking-[0.2em] text-(--text-light)">
          Customer <strong class="text-(--text-muted)">{{ selectedCustomer.email }}</strong>
        </span>
      </div>


      <div class="px-8 pt-8 pb-8">
        <!-- Profile info -->
        <div class="grid grid-cols-[2fr_2fr_1fr_1fr] gap-0 border border-(--text-light) mb-8">
          <div
            class="stat-card p-6 border-r border-(--text-light)"
            style="animation-delay: 0s"
          >
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Email
            </p>
            <p class="text-[13px] font-light text-(--text) break-all">
              {{ selectedCustomer.email }}
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
              {{ selectedCustomer.address || '-' }}
            </p>
          </div>
          <div
            class="stat-card p-6 border-r border-(--text-light)"
            style="animation-delay: 0.14s"
          >
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Loyalty Points
            </p>
            <p class="text-[32px] font-light text-(--button-hover) leading-none">
              {{ selectedCustomer.loyaltyPoints ?? 0 }}
            </p>
          </div>
          <div
            class="stat-card p-6"
            style="animation-delay: 0.21s"
          >
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Orders
            </p>
            <p class="text-[32px] font-light text-(--text) leading-none">
              {{ detailLoading ? '-' : customerOrderCount }}
            </p>
          </div>
        </div>


        <!-- Order stats -->
        <div class="grid grid-cols-2 gap-0 border border-(--text-light) mb-8">
          <div
            class="stat-card p-6 border-r border-(--text-light)"
            style="animation-delay: 0.28s"
          >
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Total Spent
            </p>
            <p class="text-[32px] font-light text-(--text) leading-none">
              ${{ detailLoading ? '-' : customerTotalSpent.toFixed(2) }}
            </p>
          </div>
          <div
            class="stat-card p-6"
            style="animation-delay: 0.35s"
          >
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
              Delivered Orders
            </p>
            <p class="text-[32px] font-light text-(--text) leading-none">
              {{ detailLoading ? '-' : customerOrdersDelivered }}
            </p>
          </div>
        </div>


        <!-- Orders table -->
        <div
          class="stat-card border border-(--text-light)"
          style="animation-delay: 0.42s"
        >
          <div class="px-6 py-3 border-b border-(--text-light)">
            <span class="text-[10px] uppercase tracking-[0.2em] text-(--text-light)">Order History</span>
          </div>


          <div class="grid grid-cols-[2fr_1.5fr_1fr_1.2fr_1.5fr] px-6 py-2.5 border-b border-(--text-light)">
            <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Order ID</span>
            <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Date</span>
            <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Total</span>
            <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Status</span>
            <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Loyalty Saved</span>
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
            v-else-if="customerOrders.length === 0"
            class="px-6 py-10 text-[13px] text-(--text-light)"
          >
            No orders found.
          </div>


          <div
            v-for="(order, i) in customerOrders"
            :key="order.orderID"
            class="grid grid-cols-[2fr_1.5fr_1fr_1.2fr_1.5fr] px-6 py-4 border-b border-(--text-light) last:border-b-0 hover:bg-(--card-hover) transition-colors items-center row-card"
            :style="{ animationDelay: `${0.42 + i * 0.04}s` }"
          >
            <span class="text-[13px] text-(--text) font-light tracking-wide font-mono">#{{ order.orderID.slice(0, 12) }}…</span>
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
            <span class="text-[13px] text-(--text-muted) font-light">
              ${{ (order.loyaltySaving ?? 0).toFixed(2) }}
            </span>
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

