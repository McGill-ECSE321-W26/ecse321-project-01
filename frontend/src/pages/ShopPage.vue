<script setup lang="ts">
import { ref } from 'vue'
import { Button } from '@/components/ui/button'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { products, categories, sortOptions } from '@/data/products'

const activeCategory = ref('All')
const sortBy = ref('newest')
</script>

<template>
  <div class="shop-page min-h-screen">
    <!-- Header -->
    <div class="max-w-350 mx-auto px-5 md:px-10 pt-16 md:pt-24">
      <h1 class="shop-heading text-[32px] md:text-[42px] lg:text-[56px] font-normal tracking-tight leading-tight">
        The Complete <em class="italic text-(--text-muted)">Catalogue</em>
      </h1>
      <div class="flex items-center justify-between mt-5 pb-8 border-b border-border">
        <p class="text-sm font-light text-(--text-muted)">
          Browse our full collection of clothing and accessories.
        </p>
        <span class="text-[13px] text-(--text-light) tracking-wide hidden sm:inline">
          Showing {{ products.length }} of 48 items
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
        class="rounded-none border-border text-[13px] tracking-wide"
        :class="
          activeCategory === cat
            ? 'bg-(--text) text-(--bg) hover:bg-(--text)/90'
            : 'bg-transparent text-(--text-muted) hover:bg-(--card-hover) hover:border-(--text-muted)'
        "
        @click="activeCategory = cat"
      >
        {{ cat }}
      </Button>

      <Select v-model="sortBy">
        <SelectTrigger
          class="ml-auto w-auto rounded-none border-border bg-transparent text-[13px] text-(--text-muted) px-4 pr-8 h-8 shadow-none focus-visible:ring-0 focus-visible:border-(--text-muted) [&_svg]:text-(--text-light)!"
        >
          <SelectValue placeholder="Sort by: Newest" />
        </SelectTrigger>
        <SelectContent
          class="rounded-none border-[#d4cfc5] bg-[#FFFCF2]! text-[#252422] shadow-sm"
        >
          <SelectItem
            v-for="opt in sortOptions"
            :key="opt.value"
            :value="opt.value"
            class="rounded-none text-[13px] text-[#6b6860] focus:bg-[#e4e0d8]! focus:text-[#252422] data-[state=checked]:text-[#252422] data-[state=checked]:font-medium"
          >
            {{ opt.label }}
          </SelectItem>
        </SelectContent>
      </Select>
    </div>
    <!-- Product Grid -->
    <div class="max-w-350 mx-auto px-5 md:px-10 pb-20 grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5">
      <div
        v-for="(product, index) in products"
        :key="product.id"
        class="product-card group cursor-pointer"
        :style="{ animationDelay: `${index * 0.05}s` }"
      >
        <!-- Image -->
        <div class="aspect-3/4 overflow-hidden relative">
          <!-- Product Image -->
          <img
            :src="product.img"
            :alt="product.name"
            loading="lazy"
            class="w-full h-full object-cover transition-transform duration-500 ease-[cubic-bezier(0.22,1,0.36,1)] group-hover:scale-105"
          >

        </div>

        <!-- Product Info -->
        <div class="pt-3.5 px-1">
          <p class="text-[11px] text-(--text-light) uppercase tracking-widest mb-1">
            {{ product.brand }}
          </p>
          <p class="text-sm text-(--text) leading-snug mb-1.5">
            {{ product.name }}
          </p>
          <p class="text-sm font-medium text-(--text)">
            ${{ product.price }}
          </p>
          <div class="flex gap-1.5 mt-2">
            <div
              v-for="(color, ci) in product.colors"
              :key="ci"
              class="w-3.5 h-3.5 rounded-full border-[1.5px] border-border cursor-pointer hover:border-(--text) transition-colors"
              :style="{ backgroundColor: color }"
            />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.shop-page {
  --bg: #FFFCF2;
  --text: #252422;
  --text-muted: #6b6860;
  --text-light: #9a958b;
  --card-hover: #e4e0d8;
  --border: #d4cfc5;

  background-color: var(--bg);
  color: var(--text);
  font-family: 'Lexend Deca', sans-serif;
  -webkit-font-smoothing: antialiased;
}

.shop-heading {
  font-family: 'Playfair Display', serif;
  letter-spacing: -1.5px;
}

@keyframes fadeUp {
  from {
    opacity: 0;
    transform: translateY(24px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.product-card {
  animation: fadeUp 0.6s ease both;
}
</style>
