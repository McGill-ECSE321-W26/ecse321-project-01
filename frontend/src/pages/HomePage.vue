<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { Button } from '@/components/ui/button'
</script>

<template>
  <div class="max-w-4xl mx-auto py-12 px-4">
    <h1 class="text-4xl font-bold mb-4">Welcome to our amazing store!</h1>
    <p class="text-muted-foreground mb-8">Browse our collection of clothing items :)</p>

    <div class="flex gap-4">
      <Button as-child>
        <RouterLink to="/login">Log In</RouterLink>
      </Button>
      <Button as-child variant="secondary">
        <RouterLink to="/register">Sign Up</RouterLink>
      </Button>
    </div>
  </div>
</template>
