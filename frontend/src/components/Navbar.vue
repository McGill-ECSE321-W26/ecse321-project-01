<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink } from 'vue-router'
import { User, ShoppingCart, Menu, Store, Package } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import {
  Drawer,
  DrawerContent,
} from '@/components/ui/drawer'

const sidebarOpen = ref(false)
</script>

<template>
  <nav class="sticky top-0 z-50 w-full">
    <div class="flex justify-end items-center gap-4 px-8 h-16">
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
      <Drawer v-model:open="sidebarOpen" direction="right">
        <Button variant="ghost" size="icon" aria-label="Open menu" @click="sidebarOpen = true">
          <Menu class="size-5" />
        </Button>
        <DrawerContent>
          <nav class="flex flex-col gap-2 p-4">
            <Button variant="ghost" class="justify-start gap-3" as-child @click="sidebarOpen = false">
              <RouterLink to="/shop">
                <Store class="size-5" />
                Shop
              </RouterLink>
            </Button>
            <Button variant="ghost" class="justify-start gap-3" as-child @click="sidebarOpen = false">
              <RouterLink to="/orders">
                <Package class="size-5" />
                Orders
              </RouterLink>
            </Button>
            <Button variant="ghost" class="justify-start gap-3" as-child @click="sidebarOpen = false">
              <RouterLink to="/cart">
                <ShoppingCart class="size-5" />
                Cart
              </RouterLink>
            </Button>
            <Button variant="ghost" class="justify-start gap-3" as-child @click="sidebarOpen = false">
              <RouterLink to="/account">
                <User class="size-5" />
                Profile
              </RouterLink>
            </Button>
          </nav>
        </DrawerContent>
      </Drawer>
    </div>
  </nav>
</template>
