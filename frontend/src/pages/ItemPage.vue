<script setup lang="ts">
import { api } from '@/api/client'
import type { ClothingModelResponseDto, ClothingVariantResponseDto } from '@/api/types/clothing'
import { ref, onMounted, computed } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { Button } from '@/components/ui/button'
import { ArrowLeft } from 'lucide-vue-next'

const route = useRoute()

const loading = ref(true)
const error = ref<string | null>(null)

const modelId = computed(() => route.params.id as string)
const model = ref<ClothingModelResponseDto | null>(null)
const variants = ref<ClothingVariantResponseDto[]>([])
const currVariant = ref<ClothingVariantResponseDto | null>(null)

onMounted(async () => {
  try {
    model.value = await api<ClothingModelResponseDto>(`/clothing/${modelId.value}`)
    variants.value = await api<ClothingVariantResponseDto[]>(`/clothing/${modelId.value}/variants`)
    if (variants.value.length != 0) { currVariant.value = variants.value[0] }
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : 'Failed to load model'
  } finally {
    // Turn off spinner once data is fully loaded
    loading.value = false
  }
})

function addToCart() {

}


</script>

<template>
  <div class="min-h-screen">
    <!-- Back to shop button -->
    <div class="max-w-350 mx-auto px-5 md:px-10 py-6 pt-16 md:pt-24 flex items-center gap-3 flex-wrap">
      <RouterLink to="/shop">
        <Button
          size="lg"
          variant="outline"
          class="rounded-none border-(--text-light) text-[20px] tracking-wide bg-transparent text-(--text-muted) hover:bg-(--card-hover) hover:border-(--text-muted)"
        >
          <ArrowLeft :size="20" :strokeWidth="4" /> Back to shopping
        </Button>
      </RouterLink>
    </div>

    <!-- Loading State -->
    <div
      v-if="loading"
      class="max-w-350 mx-auto px-5 md:px-10 pb-20 flex justify-center items-center py-32"
    >
      <div class="loading-spinner" />
    </div>

    <!-- Error State -->
    <div
      v-else-if="error"
      class="max-w-350 mx-auto px-5 md:px-10 pb-20 flex justify-center items-center py-32"
    >
      <p class="text-sm text-red-400">
        {{ error }}
      </p>
    </div>

    <!-- No variants -->
    <div
      v-else-if="variants.length === 0"
      class="max-w-350 mx-auto px-5 md:px-10 pb-20 flex justify-center items-center py-32"
    >
      <p class="text-sm text-(--text-muted)">
        No variants found.
      </p>
    </div>

  </div>
</template>