<script setup lang="ts">
import { RouterView } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
</script>

<template>
  <NavBar v-if="auth.isAuthenticated" />
  <main>
    <RouterView />
  </main>
</template>