<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import { ShoppingBag, Boxes, Users, UserCog, LayoutDashboard, ShieldCheck, ArrowRight } from 'lucide-vue-next'
import { api } from '@/api/client'
import type { CustomerResponseDto, EmployeeResponseDto } from '@/api/types/person'
import type { OrderResponseDto } from '@/api/types/order'

const activeSection = ref('dashboard')

const totalOrders = ref<number | null>(null)
const totalCustomers = ref<number | null>(null)
const totalEmployees = ref<number | null>(null)
const completedOrders = ref(0)
const pendingOrders = ref(0)
const cancelOrders = ref(0)
const recentOrders = ref<OrderResponseDto[]>([])

onMounted(async () => {
  try {
    const [customers, employees] = await Promise.all([
      api<CustomerResponseDto[]>('/persons/customers'),
      api<EmployeeResponseDto[]>('/persons/employees'),
    ])
    totalCustomers.value = customers.length
    totalEmployees.value = employees.length
  } catch {
    totalCustomers.value = 0
    totalEmployees.value = 0
  }

  try {
    const orders = await api<OrderResponseDto[]>('/orders')
    totalOrders.value = orders.length
    console.log(orders)
    
    completedOrders.value = orders.filter(o => o.orderStatus === 'Delivered').length
    cancelOrders.value = orders.filter(o => o.orderStatus === 'Cancelled').length
    pendingOrders.value = orders.filter(o => o.orderStatus === 'Preparing').length
    recentOrders.value = orders.slice(-6).reverse()
  } catch {
    totalOrders.value = 0
  }
})

const PIE_R = 60
const PIE_CX = 80
const PIE_CY = 80

const totalOrdersForChart = computed(() =>
  completedOrders.value + pendingOrders.value + cancelOrders.value
)

const completedFraction = computed(() =>
  totalOrdersForChart.value === 0 ? 0 : completedOrders.value / totalOrdersForChart.value
)

const cancelledFraction = computed(() =>
  totalOrdersForChart.value === 0 ? 0 : cancelOrders.value / totalOrdersForChart.value
)

function polarToCartesian(cx: number, cy: number, r: number, angleDeg: number) {
  const rad = ((angleDeg - 90) * Math.PI) / 180
  return { x: cx + r * Math.cos(rad), y: cy + r * Math.sin(rad) }
}

function slicePath(cx: number, cy: number, r: number, startFraction: number, endFraction: number) {
  const span = endFraction - startFraction
  if (span <= 0) return ''
  if (span >= 1) return `M ${cx},${cy - r} A ${r},${r} 0 1,1 ${cx - 0.001},${cy - r} Z`
  const startAngle = startFraction * 360
  const endAngle = endFraction * 360
  const start = polarToCartesian(cx, cy, r, startAngle)
  const end = polarToCartesian(cx, cy, r, endAngle)
  const large = (endAngle - startAngle) > 180 ? 1 : 0
  return `M ${cx},${cy} L ${start.x},${start.y} A ${r},${r} 0 ${large},1 ${end.x},${end.y} Z`
}

const navItems = [
  { key: 'dashboard', label: 'Dashboard', icon: LayoutDashboard, to: null },
  { key: 'orders', label: 'Orders', icon: ShoppingBag, to: '/manager/orders' },
  { key: 'inventory', label: 'Inventory', icon: Boxes, to: '/shop' },
  { key: 'customers', label: 'Customers', icon: Users, to: null },
  { key: 'employees', label: 'Employees', icon: UserCog, to: null },
]

const statCards = computed(() => [
  { label: 'Total Orders', value: totalOrders.value, index: 0 },
  { label: 'Employees', value: totalEmployees.value, index: 1 },
  { label: 'Customers', value: totalCustomers.value, index: 2 },
])
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
        <template
          v-for="item in navItems"
          :key="item.key"
        >
          <component
            :is="item.to ? RouterLink : 'button'"
            v-bind="item.to ? { to: item.to } : {}"
            class="flex items-center gap-3 px-5 py-3 text-[13px] tracking-wide border-b border-(--text-light) transition-colors w-full text-left"
            :class="
              activeSection === item.key
                ? 'bg-(--card-hover) text-(--text) font-medium'
                : 'text-(--text-muted) hover:bg-(--card-hover) hover:text-(--text)'
            "
            @click="activeSection = item.key"
          >
            <component
              :is="item.icon"
              class="w-4 h-4 shrink-0"
            />
            {{ item.label }}
          </component>
        </template>
      </nav>
    </aside>

    <!-- Main content -->
    <main class="flex-1 px-10 pt-16 pb-20">
      <!-- Page heading -->
      <h1 class="dashboard-heading text-[40px] lg:text-[52px] font-normal tracking-tight leading-tight mb-2">
        <span class="text-(--text-muted)">Welcome Back,</span>
        <em class="text-(--text-light)">  Manager </em>
      </h1>
      <div class="flex items-center justify-between mb-10 pb-6 border-b border-(--text-light)">
        <p class="text-sm font-light text-(--text-muted)">
          Here's what's happening in your store today.
        </p>
        <span class="text-[12px] text-(--text-light) uppercase tracking-widest hidden sm:inline">
          Overview
        </span>
      </div>

      <!-- Stats row -->
      <div class="grid grid-cols-3 gap-0 border border-(--text-light) mb-10">
        <div
          v-for="stat in statCards"
          :key="stat.label"
          class="stat-card p-7 border-r border-(--text-light) last:border-r-0"
          :style="{ animationDelay: `${stat.index * 0.1}s` }"
        >
          <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
            {{ stat.label }}
          </p>
          <p class="text-[44px] font-light text-(--text) leading-none">
            {{ stat.value ?? '—' }}
          </p>
        </div>
      </div>

      <!-- Charts + Actions row -->
      <div class="grid grid-cols-2 gap-0 border border-(--text-light)">
        <!-- Pie chart -->
        <div
          class="stat-card p-8 border-r border-(--text-light)"
          style="animation-delay: 0.3s"
        >
          <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-6">
            Order Completion
          </p>
          <div class="flex items-center gap-8">
            <svg
              width="160"
              height="160"
              viewBox="0 0 160 160"
            >
              <circle
                :cx="PIE_CX"
                :cy="PIE_CY"
                :r="PIE_R"
                fill="transparent"
                stroke="var(--text-light)"
                stroke-width="1"
              />
              <path
                v-if="completedFraction > 0"
                :d="slicePath(PIE_CX, PIE_CY, PIE_R, 0, completedFraction)"
                fill="var(--chart-4)"
                opacity="0.15"
              />
              <path
                v-if="completedFraction > 0"
                :d="slicePath(PIE_CX, PIE_CY, PIE_R, 0, completedFraction)"
                fill="var(--chart-4)"
                opacity="0.75"
                style="mix-blend-mode: multiply"
              />
              <path
                v-if="cancelledFraction > 0"
                :d="slicePath(PIE_CX, PIE_CY, PIE_R, completedFraction, completedFraction + cancelledFraction)"
                fill="var(--destructive)"
                opacity="0.15"
              />
              <path
                v-if="cancelledFraction > 0"
                :d="slicePath(PIE_CX, PIE_CY, PIE_R, completedFraction, completedFraction + cancelledFraction)"
                fill="var(--destructive)"
                opacity="0.75"
                style="mix-blend-mode: multiply"
              />
              <text
                :x="PIE_CX"
                :y="PIE_CY + 5"
                text-anchor="bottom"
                font-size="14"
                font-weight="300"
                fill="var(--text)"
              >
                {{ Math.round(completedFraction * 100) }}%
              </text>
            </svg>
            <div class="space-y-4 text-[12px]">
              <div class="flex items-center gap-3">
                <span class="inline-block w-3 h-3 bg-chart-4" />
                <div>
                  <p class="text-(--text) font-medium">
                    {{ completedOrders }}
                  </p>
                  <p class="text-(--text-light) uppercase tracking-wide text-[10px]">
                    Completed
                  </p>
                </div>
              </div>
              <div class="flex items-center gap-3">
                <span class="inline-block w-3 h-3 border border-(--text-light)" />
                <div>
                  <p class="text-(--text) font-medium">
                    {{ pendingOrders }}
                  </p>
                  <p class="text-(--text-light) uppercase tracking-wide text-[10px]">
                    Pending
                  </p>
                </div>
              </div>
              <div class="flex items-center gap-3">
                <span class="inline-block w-3 h-3 bg-destructive" />
                <div>
                  <p class="text-(--text) font-medium">
                    {{ cancelOrders }}
                  </p>
                  <p class="text-(--text-light) uppercase tracking-wide text-[10px]">
                    Cancelled
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Quick actions -->
        <div
          class="stat-card p-8"
          style="animation-delay: 0.4s"
        >
          <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-6">
            Quick Actions
          </p>
          <div class="flex flex-col gap-0">
            <RouterLink
              to="/manager/orders"
              class="group flex items-center justify-between py-4 border-b border-(--text-light) hover:bg-(--card-hover) px-2 -mx-2 transition-colors"
            >
              <div class="flex items-center gap-3">
                <ShoppingBag class="w-4 h-4 text-(--text-muted)" />
                <span class="text-[13px] text-(--text-muted) group-hover:text-(--text) transition-colors tracking-wide">View all orders</span>
              </div>
              <ArrowRight class="w-3.5 h-3.5 text-(--text-light) group-hover:text-(--text-muted) transition-colors" />
            </RouterLink>

            <RouterLink
              to="/shop"
              class="group flex items-center justify-between py-4 border-b border-(--text-light) hover:bg-(--card-hover) px-2 -mx-2 transition-colors"
            >
              <div class="flex items-center gap-3">
                <Boxes class="w-4 h-4 text-(--text-muted)" />
                <span class="text-[13px] text-(--text-muted) group-hover:text-(--text) transition-colors tracking-wide">Manage inventory</span>
              </div>
              <ArrowRight class="w-3.5 h-3.5 text-(--text-light) group-hover:text-(--text-muted) transition-colors" />
            </RouterLink>

            <div class="group flex items-center justify-between py-4 border-b border-(--text-light) px-2 -mx-2 opacity-40 cursor-not-allowed">
              <div class="flex items-center gap-3">
                <UserCog class="w-4 h-4 text-(--text-muted)" />
                <span class="text-[13px] text-(--text-muted) tracking-wide">Manage employees</span>
              </div>
              <span class="text-[10px] text-(--text-light) uppercase tracking-widest">Soon</span>
            </div>

            <div class="group flex items-center justify-between py-4 px-2 -mx-2 opacity-40 cursor-not-allowed">
              <div class="flex items-center gap-3">
                <Users class="w-4 h-4 text-(--text-muted)" />
                <span class="text-[13px] text-(--text-muted) tracking-wide">Manage customers</span>
              </div>
              <span class="text-[10px] text-(--text-light) uppercase tracking-widest">Soon</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Recent Orders -->
      <div
        class="stat-card mt-0 border border-t-0 border-(--text-light)"
        style="animation-delay: 0.5s"
      >
        <div class="flex items-center justify-between px-8 py-5 border-b border-(--text-light)">
          <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light)">
            Recent Orders
          </p>
          <RouterLink
            to="/manager/orders"
            class="text-[11px] uppercase tracking-widest text-(--text-light) hover:text-(--text-muted) transition-colors flex items-center gap-1.5"
          >
            View all <ArrowRight class="w-3 h-3" />
          </RouterLink>
        </div>

        <!-- Header row -->
        <div class="grid grid-cols-[1fr_160px] px-8 py-2 border-b border-(--text-light)">
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Order ID</span>
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Status</span>
        </div>

        <div
          v-if="recentOrders.length === 0"
          class="px-8 py-6 text-[13px] text-(--text-light)"
        >
          No orders yet.
        </div>

        <div
          v-for="(order, i) in recentOrders"
          :key="order.orderID"
          class="grid grid-cols-[1fr_160px] px-8 py-4 border-b border-(--text-light) last:border-b-0 hover:bg-(--card-hover) transition-colors"
          :style="{ animationDelay: `${0.5 + i * 0.04}s` }"
        >
          <span class="text-[13px] text-(--text) tracking-wide font-light">#{{ order.orderID }}</span>
          <span
            class="text-[11px] uppercase tracking-widest"
            :class="order.orderStatus === 'Delivered' ? 'text-(--text-muted)' : 'text-(--text-light)'"
          >
            {{ order.orderStatus }}
          </span>
        </div>
      </div>
    </main>
  </div>
</template>

<style scoped>
.dashboard-heading {
  font-family: 'Playfair Display', serif;
  letter-spacing: -1.5px;
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.stat-card {
  animation: fadeUp 0.6s ease both;
}
</style>
