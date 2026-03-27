<script setup lang="ts">
import { X } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'

defineProps<{
  open: boolean
}>()

const emit = defineEmits<{
  'update:open': [value: boolean]
}>()
</script>

<template>
  <div v-if="open">
    <div
      class="fixed inset-0 z-50 bg-black/50"
      @click="emit('update:open', false)"
    />
    <div
      class="fixed top-0 right-0 z-50 h-full w-64 border-l bg-sidebar text-sidebar-foreground"
      @click.stop
    >
      <div class="flex items-center justify-between p-4 border-b">
        <span class="font-semibold">Menu</span>
        <Button variant="ghost" size="icon" @click="emit('update:open', false)">
          <X class="size-5" />
        </Button>
      </div>
      <div class="p-4">
        <p class="text-muted-foreground">Sidebar content coming soon.</p>
      </div>
    </div>
  </div>
</template>
