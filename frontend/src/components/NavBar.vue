<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { User, ShoppingCart, Menu, Store, Package, LogOut, AppWindowMac } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import {
  Drawer,
  DrawerContent,
} from '@/components/ui/drawer'
import { useAuthStore } from '@/stores/auth'

const sidebarOpen = ref(false)
const router = useRouter()
const auth = useAuthStore()

function handleLogout() {
  sidebarOpen.value = false
  auth.logout()
  router.push('/')
}
</script>

<template>
  <nav class="fixed top-0 right-0 z-50">
    <div class="flex justify-end items-center gap-4 px-8 h-16">
      <Button
        variant="ghost"
        size="icon"
        class="hover:bg-(--card-hover)"
        as-child
      >
        <RouterLink
          to="/account"
          aria-label="Account"
        >
          <User class="size-5" />
        </RouterLink>
      </Button>
      <Button
        variant="ghost"
        size="icon"
        class="hover:bg-(--card-hover)"
        as-child
      >
        <RouterLink
          to="/cart"
          aria-label="Cart"
        >
          <ShoppingCart class="size-5" />
        </RouterLink>
      </Button>
      <Drawer
        v-model:open="sidebarOpen"
        direction="right"
      >
        <Button
          variant="ghost"
          size="icon"
          class="hover:bg-(--card-hover)"
          aria-label="Open menu"
          @click="sidebarOpen = true"
        >
          <Menu class="size-5" />
        </Button>
        <DrawerContent>
          <nav class="flex flex-col gap-2 p-4">
            <Button
              variant="ghost"
              :class="['justify-start gap-3', (auth.role !== 'Manager')?'hidden':null]"
              as-child
              @click="sidebarOpen = false"
            >
              <RouterLink to="/manager">
                <AppWindowMac class="size-5" />
                Control Panel
              </RouterLink>
            </Button>
            <Button
              variant="ghost"
              class="justify-start gap-3"
              as-child
              @click="sidebarOpen = false"
            >
              <RouterLink to="/shop">
                <Store class="size-5" />
                Shop
              </RouterLink>
            </Button>
            <Button
              variant="ghost"
              class="justify-start gap-3"
              as-child
              @click="sidebarOpen = false"
            >
              <RouterLink to="/orders">
                <Package class="size-5" />
                Orders
              </RouterLink>
            </Button>
            <Button
              variant="ghost"
              class="justify-start gap-3"
              as-child
              @click="sidebarOpen = false"
            >
              <RouterLink to="/cart">
                <ShoppingCart class="size-5" />
                Cart
              </RouterLink>
            </Button>
            <Button
              variant="ghost"
              class="justify-start gap-3"
              as-child
              @click="sidebarOpen = false"
            >
              <RouterLink to="/account">
                <User class="size-5" />
                Profile
              </RouterLink>
            </Button>
            <hr class="my-2">
            <Button
              variant="ghost"
              class="justify-start gap-3 text-red-600 hover:text-red-700 hover:bg-red-50"
              @click="handleLogout"
            >
              <LogOut class="size-5" />
              Log Out
            </Button>
          </nav>
        </DrawerContent>
      </Drawer>
    </div>
  </nav>
</template>
