<script setup lang="ts">
import { useToastStore } from '@/stores/toast'
import { X, CheckCircle2, AlertCircle } from 'lucide-vue-next'

const toast = useToastStore()
</script>

<template>
  <Teleport to="body">
    <div class="fixed bottom-6 right-6 z-50 flex flex-col gap-2 pointer-events-none">
      <TransitionGroup name="toast">
        <div
          v-for="t in toast.toasts"
          :key="t.id"
          class="flex items-start gap-3 px-4 py-3.5 border pointer-events-auto min-w-65 max-w-90"
          :style="{
            backgroundColor: 'var(--bg)',
            borderColor: 'var(--text-light)',
            color: 'var(--text)',
          }"
        >
          <!-- type indicator -->
          <CheckCircle2
            v-if="t.type === 'success'"
            class="shrink-0 mt-0.5 w-4 h-4 text-green-600"
          />
          <AlertCircle
            v-else
            class="shrink-0 mt-0.5 w-4 h-4 text-red-400"
          />

          <span
            class="flex-1 text-[13px] leading-snug"
            :style="{ color: 'var(--text-muted)' }"
          >
            {{ t.message }}
          </span>

          <button
            class="shrink-0 transition-opacity cursor-pointer mt-0.5"
            :style="{ color: 'var(--text-light)' }"
            style="opacity: 0.7"
            @mouseenter="($event.target as HTMLElement).style.opacity = '1'"
            @mouseleave="($event.target as HTMLElement).style.opacity = '0.7'"
            @click="toast.dismiss(t.id)"
          >
            <X class="w-3 h-3" />
          </button>
        </div>
      </TransitionGroup>
    </div>
  </Teleport>
</template>

<style>
.toast-enter-active,
.toast-leave-active {
  transition: all 0.2s ease;
}
.toast-enter-from {
  opacity: 0;
  transform: translateY(8px);
}
.toast-leave-to {
  opacity: 0;
  transform: translateX(16px);
}
</style>
