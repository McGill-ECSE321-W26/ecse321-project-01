<script setup lang="ts">
import { api } from '@/api/client'
import type { ClothingModelResponseDto, ClothingVariantResponseDto } from '@/api/types/clothing'
import { ref, onMounted, computed } from 'vue'
import { useRoute, RouterLink } from 'vue-router'
import { Button } from '@/components/ui/button'
import { ArrowLeft, ChevronDown } from 'lucide-vue-next'

const route = useRoute()

const loading = ref(true)
const error = ref<string | null>(null)

const modelId = computed(() => route.params.id as string)
const model = ref<ClothingModelResponseDto | null>(null)
const variants = ref<ClothingVariantResponseDto[]>([])
const currVariant = ref<ClothingVariantResponseDto | null>(null)
const detailsOpen = ref(false)

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
  // TODO
}

function toggleDetails() {
  detailsOpen.value = !detailsOpen.value
}


</script>

<template>
  <div class="min-h-screen">
    <!-- Back to shop button -->
    <div class="max-w-350 mx-auto px-5 md:px-10 py-6 pt-16 md:pt-24 flex items-center gap-3 flex-wrap">
      <RouterLink to="/shop">
        <Button
          size="sm"
          variant="outline"
          class="rounded-none border-(--text-light) text-[18px] tracking-wide bg-transparent text-(--text-light) hover:bg-(--card-hover) hover:border-(--text-muted) p-1"
        >
          <ArrowLeft :size="10" :strokeWidth="3" /> Back to shopping
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

    <!-- Show item -->
     <div
      v-else
      class="grid grid-cols-1 md:grid-cols-2 pb-10"
     >
      <!-- image -->
      <div class="aspect-[3/4] w-full overflow-hidden bg-(--card-hover)">
        <img
          :src="currVariant?.imagePath"
          :alt="model?.name"
          loading="lazy"
          class="w-full h-full object-cover"
        >
      </div>

      <!-- info -->
      <div class="pt-8 px-6">
        <!-- item name -->
        <p class="text-[40px]">
          {{ model?.name }}
        </p>
        <!-- colors -->
        
        <!-- sizes -->

        <!-- add to cart button -->
        <Button
          size="lg"
          class="rounded-none text-[20px] font-medium text-(--bg) py-6 bg-[#AE5A31] hover:bg-[#773f23]"
          @click="addToCart">
          Add to Cart &nbsp {{ model?.price }}$
        </Button>
        <!-- details -->
        <Button
          @click="toggleDetails">
          <span>Details</span>
          <ChevronDown class="transition-transform duration-200" :class="{ 'rotate-180': detailsOpen }"/>
        </Button>

        <div v-show="detailsOpen" class="mt-3 text-(--text-muted) leading-relaxed">
          {{ model?.description }}
        </div>
      </div>
     </div>

  </div>
</template>