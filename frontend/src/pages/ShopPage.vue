<script setup lang="ts">
import { ref } from 'vue'
import { Heart } from 'lucide-vue-next'
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
    <!-- Catalogue Header -->
    <div class="max-w-[1400px] mx-auto px-5 md:px-10 pt-8 md:pt-12">
      <h1 class="shop-heading text-[32px] md:text-[42px] lg:text-[56px] font-normal tracking-tight leading-tight">
        Full <em class="italic text-[var(--shop-text-muted)]">Catalogue</em>
      </h1>
      <div class="flex items-center justify-between mt-5 pb-8 border-b border-[var(--shop-border)]">
        <p class="text-sm font-light text-[var(--shop-text-muted)]">
          Browse our full collection of clothing and accessories.
        </p>
        <span class="text-[13px] text-[var(--shop-text-light)] tracking-wide hidden sm:inline">
          Showing {{ products.length }} of 48 items
        </span>
      </div>
    </div>

    <!-- Filter Bar -->
    <div class="max-w-[1400px] mx-auto px-5 md:px-10 py-6 flex items-center gap-3 flex-wrap">
      <Button
        v-for="cat in categories"
        :key="cat"
        :variant="activeCategory === cat ? 'default' : 'outline'"
        size="sm"
        class="rounded-none border-[var(--shop-border)] text-[13px] tracking-wide"
        :class="
          activeCategory === cat
            ? 'bg-[var(--shop-text)] text-[var(--shop-white)] border-[var(--shop-text)] hover:bg-[var(--shop-text)]/90'
            : 'bg-transparent text-[var(--shop-text-muted)] hover:bg-[var(--shop-card-hover)] hover:border-[var(--shop-text-muted)]'
        "
        @click="activeCategory = cat"
      >
        {{ cat }}
      </Button>

      <Select v-model="sortBy">
        <SelectTrigger
          class="ml-auto w-auto rounded-none border-[var(--shop-border)] bg-transparent text-[13px] text-[var(--shop-text-muted)] px-4 pr-8 h-8"
        >
          <SelectValue placeholder="Sort by: Newest" />
        </SelectTrigger>
        <SelectContent>
          <SelectItem
            v-for="opt in sortOptions"
            :key="opt.value"
            :value="opt.value"
          >
            {{ opt.label }}
          </SelectItem>
        </SelectContent>
      </Select>
    </div>

    <!-- Product Grid -->
    <div class="max-w-[1400px] mx-auto px-5 md:px-10 pb-20 grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5">
      <div
        v-for="(product, index) in products"
        :key="product.id"
        class="product-card group cursor-pointer"
        :style="{ animationDelay: `${index * 0.05}s` }"
      >
        <!-- Image -->
        <div class="aspect-[3/4] bg-[var(--shop-card-bg)] overflow-hidden relative">
          <!-- Tag -->
          <span
            v-if="product.tag === 'new'"
            class="absolute top-3 left-3 z-10 px-2.5 py-1 text-[10px] font-medium uppercase tracking-wider bg-[var(--shop-text)] text-[var(--shop-white)]"
          >
            New
          </span>
          <span
            v-else-if="product.tag === 'low'"
            class="absolute top-3 left-3 z-10 px-2.5 py-1 text-[10px] font-medium uppercase tracking-wider bg-[var(--shop-tag-bg)] text-[var(--shop-text-muted)]"
          >
            Low Stock
          </span>

          <!-- Wishlist -->
          <Button
            variant="ghost"
            size="icon-sm"
            class="absolute top-3 right-3 z-10 rounded-full bg-[rgba(255,255,250,0.85)] backdrop-blur-sm opacity-0 group-hover:opacity-100 transition-opacity hover:bg-[rgba(255,255,250,1)]"
          >
            <Heart class="size-4 stroke-[var(--shop-text)]" :stroke-width="1.5" />
          </Button>

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
          <p class="text-[11px] text-[var(--shop-text-light)] uppercase tracking-widest mb-1">
            {{ product.brand }}
          </p>
          <p class="text-sm text-[var(--shop-text)] leading-snug mb-1.5">
            {{ product.name }}
          </p>
          <p class="text-sm font-medium text-[var(--shop-text)]">
            ${{ product.price }}
          </p>
          <div class="flex gap-1.5 mt-2">
            <div
              v-for="(color, ci) in product.colors"
              :key="ci"
              class="w-3.5 h-3.5 rounded-full border-[1.5px] border-[var(--shop-border)] cursor-pointer hover:border-[var(--shop-text)] transition-colors"
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
  --shop-bg: #FFFCF2;
  --shop-bg-warm: #eae5db;
  --shop-text: #1a1a18;
  --shop-text-muted: #6b6860;
  --shop-text-light: #9a958b;
  --shop-card-bg: #edeae4;
  --shop-card-hover: #e4e0d8;
  --shop-white: #fffef9;
  --shop-border: #d4cfc5;
  --shop-tag-bg: #ddd8ce;

  background-color: var(--shop-bg);
  color: var(--shop-text);
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
