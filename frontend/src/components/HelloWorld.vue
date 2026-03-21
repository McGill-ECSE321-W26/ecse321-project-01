<script setup lang="ts">
defineProps<{
  msg?: string
}>()
</script>

<template>
  <div class="flex flex-col items-center justify-center min-h-screen px-4">
    <h1 class="text-4xl font-bold text-foreground mb-4">
      {{ msg ?? 'Welcome' }}
    </h1>
    <p class="text-lg text-muted-foreground">
      Get started by editing <code class="rounded bg-muted px-2 py-1 text-sm font-mono">src/App.vue</code>
    </p>
  </div>
</template>
