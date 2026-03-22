<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { Button } from '@/components/ui/button'
</script>

<template>
  <div class="max-w-md mx-auto py-12 px-4 text-center">
    <h1 class="text-6xl font-bold mb-4">404</h1>
    <p class="text-muted-foreground mb-6">Page not found.</p>
    <Button as-child>
      <RouterLink to="/">Go Home</RouterLink>
    </Button>
  </div>
</template>
