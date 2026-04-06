<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink, RouterView, useRoute } from 'vue-router'
import {
  ShoppingBag, Boxes, Users, UserCog, LayoutDashboard, ShieldCheck,
} from 'lucide-vue-next'

const route = useRoute()

const activeSection = computed(() => {
  if (route.path === '/manager') return 'dashboard'
  if (route.path.startsWith('/manager/orders')) return 'orders'
  if (route.path.startsWith('/manager/customers')) return 'customers'
  return ''
})

const navItems = [
  { key: 'dashboard', label: 'Dashboard', icon: LayoutDashboard, to: '/manager' },
  { key: 'orders',    label: 'Orders',    icon: ShoppingBag,     to: '/manager/orders' },
  { key: 'inventory', label: 'Inventory', icon: Boxes,           to: null },
  { key: 'customers', label: 'Customers', icon: Users,           to: '/manager/customers' },
  { key: 'employees', label: 'Employees', icon: UserCog,         to: null },
]
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
          <!-- Active route link -->
          <RouterLink
            v-if="item.to"
            :to="item.to"
            class="flex items-center gap-3 px-5 py-3 text-[13px] tracking-wide border-b border-(--text-light) transition-colors"
            :class="
              activeSection === item.key
                ? 'bg-(--card-hover) text-(--text) font-medium'
                : 'text-(--text-muted) hover:bg-(--card-hover) hover:text-(--text)'
            "
          >
            <component
              :is="item.icon"
              class="w-4 h-4 shrink-0"
            />
            {{ item.label }}
          </RouterLink>

          <!-- Disabled / coming-soon item -->
          <div
            v-else
            class="flex items-center justify-between px-5 py-3 text-[13px] tracking-wide border-b border-(--text-light) opacity-35 cursor-not-allowed select-none"
          >
            <div class="flex items-center gap-3 text-(--text-muted)">
              <component
                :is="item.icon"
                class="w-4 h-4 shrink-0"
              />
              {{ item.label }}
            </div>
            <span class="text-[9px] uppercase tracking-widest text-(--text-light)">Soon</span>
          </div>
        </template>
      </nav>
    </aside>

    <!-- Page content -->
    <RouterView />
  </div>
</template>
