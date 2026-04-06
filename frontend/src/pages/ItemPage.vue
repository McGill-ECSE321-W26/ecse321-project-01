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
const currVariant = computed(() => {
  return variants.value.find(v =>
    v.color === selectedColor.value &&
    v.size === selectedSize.value
  ) || null
})
const detailsOpen = ref(false)

const selectedColor = ref<string | null>(null)
const selectedSize = ref<string | null>(null)
const colors = computed(() => {
  return [...new Set(variants.value.map(v => v.color))]
})
const sizes = computed(() => {
  return [...new Set(variants.value.map(v => v.size))]
})


onMounted(async () => {
  try {
    model.value = await api<ClothingModelResponseDto>(`/clothing/${modelId.value}`)
    variants.value = await api<ClothingVariantResponseDto[]>(`/clothing/${modelId.value}/variants`)
    if (variants.value.length !== 0) {
      selectedColor.value = variants.value[0].color
      selectedSize.value = variants.value[0].size
    }
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

function selectColor(color: string) {
  selectedColor.value = color

  if (!isSizeAvailable(selectedSize.value!)) {
    const valid = variants.value.find(v => v.color === color)
    selectedSize.value = valid?.size || null
  }
}

function selectSize(size: string) {
  selectedSize.value = size

  if (!isColorAvailable(selectedColor.value!)) {
    const valid = variants.value.find(v => v.size === size)
    selectedColor.value = valid?.color || null
  }
}

function isColorSelected(color: string) {
  return (selectedColor.value !== null && selectedColor.value === color)
}

function isSizeSelected(size: string) {
  return (selectedSize.value !== null && selectedSize.value === size)
}

function isColorAvailable(color: string) {
  return variants.value.some(v =>
    v.color === color &&
    v.size === selectedSize.value
  )
}

function isSizeAvailable(size: string) {
  return variants.value.some(v =>
    v.size === size &&
    v.color === selectedColor.value
  )
}


</script>

<template>
  <div class="min-h-screen">
    <!-- Back to shop button -->
    <div class="max-w-350 mx-auto py-6 pt-6 md:pt-6 flex items-center gap-3 flex-wrap">
      <RouterLink to="/shop">
        <Button
          size="sm"
          variant="outline"
          class="rounded-none border-(--text-light) border-2 text-[18px] tracking-wide bg-transparent text-(--text-light) hover:bg-(--card-hover) hover:border-(--text-muted) p-1 cursor-pointer"
        >
          <ArrowLeft
            :size="10"
            :stroke-width="3"
          /> Back to shopping
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
      class="grid grid-cols-1 md:grid-cols-2 pb-15"
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
      <div class="flex flex-col items-start pt-8 px-6">
        <!-- item name -->
        <p class="text-[40px]">
          {{ model?.name }}
        </p>

        <!-- colors -->
        <div class="flex gap-1.5 mt-2 pb-3">
          <div
            v-for="color in colors"
            :key="color"
            class="w-5 h-5 rounded-full border-[1.5px] cursor-pointer transition-colors"
            :class="[
              isColorSelected(color)
                ? 'border-(--text) scale-110'
                : 'border-(--text-light) hover:border-(--text)',
              !isColorAvailable(color) && 'opacity-30 cursor-not-allowed'
            ]"
            :style="{ backgroundColor: color }"
            :title="color"
            @click="isColorAvailable(color) && selectColor(color)"
          />
        </div>

        <!-- sizes -->
        <div class="flex gap-1 mt-2 pb-3">
          <Button
            v-for="size in sizes"
            :key="size"
            :variant="isSizeSelected(size) ? 'default' : 'outline'"
            :disabled="!isSizeAvailable(size)"
            size="lg"
            class="rounded-none border-(--text-light) text-[18px] tracking-wide w-12.5 cursor-pointer"
            :class="[
              isSizeSelected(size)
                ? 'bg-(--text) text-(--bg)'
                : 'bg-transparent text-(--text-muted) hover:bg-(--card-hover) hover:border-(--text-muted)',
              !isSizeAvailable(size) && 'bg-(--card-hover) cursor-not-allowed'
            ]"
            @click="selectSize(size)"
          >
            {{ size }}
          </Button>
        </div>

        <!-- add to cart button -->
        <span class="pb-60"><Button
          size="lg"
          class="rounded-none cursor-pointer text-[20px] font-medium text-(--bg) py-6 bg-(--button-hover) hover:bg-[#773f23]"
          @click="addToCart"
        >
          Add to Cart &nbsp; {{ model?.price }}$
        </Button></span>

        <!-- details -->
        <Button
          size="sm"
          class="bg-transparent text-(--text) rounded-none cursor-pointer hover:bg-(--card-hover)"
          @click="toggleDetails"
        >
          <span>Details</span>
          <ChevronDown
            class="transition-transform duration-500"
            :class="{ 'rotate-180': detailsOpen }"
          />
        </Button>
        <div
          v-show="detailsOpen"
          class="mt-3 text-(--text-muted) leading-relaxed"
        >
          {{ model?.description }}
        </div>
      </div>
    </div>
  </div>
</template>