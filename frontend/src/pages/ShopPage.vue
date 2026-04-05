<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Button } from '@/components/ui/button'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { api } from '@/api/client'
import type { ClothingModelListResponseDto } from '@/api/types/clothing'

const activeCategory = ref('All')
const sortBy = ref('none')
const models = ref<ClothingModelListResponseDto[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

// Hashmap -> Key: clothingModelID (str) | Value: selected color index (int)
const selectedColorIndex = ref<Record<string, number>>({})

const categories = ['All', 'Tops', 'Bottoms', 'Dresses', 'Outerwear', 'Accessories'] as const

const sortOptions = [
  { label: 'None', value: 'none' },
  { label: 'Price: Low → High', value: 'price-asc' },
  { label: 'Price: High → Low', value: 'price-desc' },
] as const

const filteredModels = computed(() => {
  let result = models.value

  // Category filter
  if (activeCategory.value !== 'All') {
    result = result.filter(
      (p) => p.category === activeCategory.value,
    )
  }

  // Sort
  if (sortBy.value === 'price-asc') {
    result = [...result].sort((a, b) => a.price - b.price)
  } else if (sortBy.value === 'price-desc') {
    result = [...result].sort((a, b) => b.price - a.price)
  }

  return result
})

// Get image for a model based on chosen color
function getClothingImage(model: ClothingModelListResponseDto): string {
  // If no entry with that key exists yet, ?? 0 defaults to index 0
  const index = selectedColorIndex.value[model.clothingModelID] ?? 0
  return model.variants[index]?.imagePath ?? '' // blank image path if not provided
}

function selectColor(modelId: string, colorIndex: number) {
  selectedColorIndex.value[modelId] = colorIndex // populate hashmap with k/v entry
}

function isColorSelected(modelId: string, colorIndex: number): boolean {
  // look up selected color index from selectedColorIndex.value[modelId] defaulted to 0
  return (selectedColorIndex.value[modelId] ?? 0) === colorIndex // return true if color index match
}

function formatPrice(price: number): string {
  // Format to clean integer is no decimals, but fix to 2 places if decimals
  return Number.isInteger(price) ? `${price}` : price.toFixed(2)
}

onMounted(async () => {
  try {
    models.value = await api<ClothingModelListResponseDto[]>('/clothing')
  } catch (e: any) {
    error.value = e.message ?? 'Failed to load models'
  } finally {
    // Turn off spinner once data is fully loaded
    loading.value = false
  }
})

</script>

<template>
  <div class="min-h-screen">
    <!-- Header -->
    <div class="max-w-350 mx-auto px-5 md:px-10 pt-16 md:pt-24">
      <h1 class="shop-heading text-[32px] md:text-[42px] lg:text-[56px] font-normal tracking-tight leading-tight">
        The <span class="text-(--text-muted)">Complete</span> <em class="text-(--text-light)">Catalogue</em>
      </h1>
      <div class="flex items-center justify-between mt-5 pb-8 border-b border-(--text-light)">
        <p class="text-sm font-light text-(--text-muted)">
          Browse our full collection of clothing and accessories.
        </p>
        <span class="text-[13px] text-(--text-light) tracking-wide hidden sm:inline">
          Showing {{ filteredModels.length }} of {{ models.length }} items
        </span>
      </div>
    </div>

    <!-- Filter Bar -->
    <div class="max-w-350 mx-auto px-5 md:px-10 py-6 flex items-center gap-3 flex-wrap">
      <Button
        v-for="cat in categories"
        :key="cat"
        :variant="activeCategory === cat ? 'default' : 'outline'"
        size="sm"
        class="rounded-none border-(--text-light) text-[13px] tracking-wide"
        :class="
          activeCategory === cat
            ? 'bg-(--text) text-(--bg)'
            : 'bg-transparent text-(--text-muted) hover:bg-(--card-hover) hover:border-(--text-muted)'
        "
        @click="activeCategory = cat"
      >
        {{ cat }}
      </Button>

      <Select v-model="sortBy">
        <SelectTrigger
          class="ml-auto w-auto rounded-none border-(--text-light) text-[13px] text-(--text-muted) px-4 h-8 shadow-none focus-visible:ring-0 transition-all hover:bg-(--card-hover) hover:border-(--text-muted)"
        >
          <SelectValue placeholder="Sort by: None" />
        </SelectTrigger>
        <SelectContent
          class="rounded-none border-(--text-light) bg-(--bg)! text-(--text) shadow-sm"
        >
          <SelectItem
            v-for="opt in sortOptions"
            :key="opt.value"
            :value="opt.value"
            class="rounded-none text-[13px] text-(--text-muted) focus:bg-(--card-hover)! focus:text-(--text) data-[state=checked]:text-(--text) data-[state=checked]:font-medium"
          >
            {{ opt.label }}
          </SelectItem>
        </SelectContent>
      </Select>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="max-w-350 mx-auto px-5 md:px-10 pb-20 flex justify-center items-center py-32">
      <div class="loading-spinner" />
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="max-w-350 mx-auto px-5 md:px-10 pb-20 flex justify-center items-center py-32">
      <p class="text-sm text-red-400">{{ error }}</p>
    </div>

    <!-- Empty State -->
    <div v-else-if="filteredModels.length === 0" class="max-w-350 mx-auto px-5 md:px-10 pb-20 flex justify-center items-center py-32">
      <p class="text-sm text-(--text-muted)">No models found.</p>
    </div>

    <!-- Product Grid -->
    <div v-else class="max-w-350 mx-auto px-5 md:px-10 pb-20 grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5">
      <div
        v-for="(model, index) in filteredModels"
        :key="model.clothingModelID"
        class="model-card group cursor-pointer"
      >
        <!-- Image -->
        <div class="aspect-3/4 overflow-hidden relative bg-(--card-hover)">
          <img
            :src="getClothingImage(model)"
            :alt="model.name"
            loading="lazy"
            class="w-full h-full object-cover transition-transform duration-500 ease-[cubic-bezier(0.22,1,0.36,1)] group-hover:scale-105"
          >
        </div>

        <!-- Product Info -->
        <div class="pt-3.5 px-1">
          <p class="text-[11px] text-(--text-light) uppercase tracking-widest mb-1">
            {{ model.brand }}
          </p>
          <p class="text-sm text-(--text) leading-snug mb-1.5">
            {{ model.name }}
          </p>
          <p class="text-sm font-extrabold text-(--text)">
            ${{ formatPrice(model.price) }} CAD
          </p>
          <div class="flex gap-1.5 mt-2">
            <div
              v-for="(variant, color_idx) in model.variants"
              :key="color_idx"
              class="w-3.5 h-3.5 rounded-full border-[1.5px] cursor-pointer transition-colors"
              :class="isColorSelected(model.clothingModelID, color_idx)
                ? 'border-(--text) scale-110'
                : 'border-(--text-light) hover:border-(--text)'"
              :style="{ backgroundColor: variant.color }"
              :title="variant.color"
              @click="selectColor(model.clothingModelID, color_idx)"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.shop-heading {
  font-family: 'Playfair Display', serif;
  letter-spacing: -1.5px;
}

.loading-spinner {
  width: 32px;
  height: 32px;
  border: 2px solid var(--text-light);
  border-top-color: var(--text);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
