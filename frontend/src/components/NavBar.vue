<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import { User, ShoppingCart, Menu, Store, Package, LogOut, AppWindowMac, ClipboardList } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import {
  Drawer,
  DrawerContent,
} from '@/components/ui/drawer'
import { useAuthStore } from '@/stores/auth'
import { useCartStore } from '@/stores/cart'

const sidebarOpen = ref(false)
const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const cart = useCartStore()

function handleLogout() {
  sidebarOpen.value = false
  auth.logout()
  router.push('/')
}

function backToShop() {
  router.push({name: 'shop'})
}

onMounted(() => {
  if (auth.role === 'Customer') {
    cart.fetchCart(auth.personId!)
  }
})

</script>

<template>
  <div
    v-if="route.name !== 'home'"
    class="flex justify-center pt-2 cursor-pointer"
    @click="backToShop"
  >
    <p class="font-medium text-[40px] md:text-[55px] ">
      Kloth
    </p>
  </div>
  <nav class="top-nav fixed top-0 right-0 z-50">
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
        v-if="auth.role === 'Customer'"
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
          <span
            v-if="cart.itemCount > 0"
            class="text-[15px]"
          >
            {{ cart.itemCount }}
          </span>
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
              :class="['justify-start gap-3 hover:bg-(--card-hover)', (auth.role !== 'Manager')?'hidden':null]"
              as-child
              @click="sidebarOpen = false"
            >
              <RouterLink to="/manager">
                <AppWindowMac class="size-5" />
                Control Panels
              </RouterLink>
            </Button>
            <Button
              variant="ghost"
              :class="['justify-start gap-3 hover:bg-(--card-hover)', (auth.role !== 'Employee')?'hidden':null]"
              as-child
              @click="sidebarOpen = false"
            >
              <RouterLink to="/employee/orders">
                <ClipboardList class="size-5" />
                Order Dashboard
              </RouterLink>
            </Button>
            <Button
              variant="ghost"
              class="justify-start gap-3 hover:bg-(--card-hover)"
              as-child
              @click="sidebarOpen = false"
            >
              <RouterLink to="/shop">
                <Store class="size-5" />
                Shop
              </RouterLink>
            </Button>
            <Button
              v-if="auth.role === 'Customer'"
              variant="ghost"
              class="justify-start gap-3 hover:bg-(--card-hover)"
              as-child
              @click="sidebarOpen = false"
            >
              <RouterLink to="/orders">
                <Package class="size-5" />
                Orders
              </RouterLink>
            </Button>
            <Button
              v-if="auth.role === 'Customer'"
              variant="ghost"
              class="justify-start gap-3 hover:bg-(--card-hover)"
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
              class="justify-start gap-3 hover:bg-(--card-hover)"
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
