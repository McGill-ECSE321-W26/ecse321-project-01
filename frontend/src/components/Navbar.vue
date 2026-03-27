<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink } from 'vue-router'
import { User, ShoppingCart, Menu } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import Sidebar from '@/components/Sidebar.vue'

const sidebarOpen = ref(false)
</script>

<template>
  <nav class="sticky top-0 z-50 w-full">
    <div class="flex justify-end items-center gap-4 px-4 h-14">
      <Button variant="ghost" size="icon" as-child>
        <RouterLink to="/account" aria-label="Account">
          <User class="size-5" />
        </RouterLink>
      </Button>
      <Button variant="ghost" size="icon" as-child>
        <RouterLink to="/cart" aria-label="Cart">
          <ShoppingCart class="size-5" />
        </RouterLink>
      </Button>
      <Button variant="ghost" size="icon" aria-label="Open menu" @click="sidebarOpen = true">
        <Menu class="size-5" />
      </Button>
    </div>
  </nav>
  <Sidebar v-model:open="sidebarOpen" />
</template>
