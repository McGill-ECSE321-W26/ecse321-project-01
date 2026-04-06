<script setup lang="ts">
import { RouterView } from 'vue-router'
import NavBar from '@/components/NavBar.vue'
import ToastContainer from '@/components/ToastContainer.vue'
import FooterBar from "@/components/FooterBar.vue";
import { useAuthStore } from '@/stores/auth'
import { useRoute } from 'vue-router'

const auth = useAuthStore()
const route = useRoute()
</script>

<template>
  <NavBar v-if="auth.isAuthenticated" />
  <main>
    <RouterView />
  </main>
  <ToastContainer />
  <FooterBar v-if="!['/', '/login', '/register'].includes(route.path)" />
</template>


