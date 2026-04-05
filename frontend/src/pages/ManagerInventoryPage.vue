<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import {
  ShoppingBag, Boxes, Users, UserCog, LayoutDashboard,
  ShieldCheck, ChevronDown, ChevronUp, Pencil, Trash2, Plus, Package,
} from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import {
  Select, SelectContent, SelectItem, SelectTrigger, SelectValue,
} from '@/components/ui/select'
import {
  Dialog, DialogContent, DialogHeader, DialogTitle,
  DialogDescription, DialogFooter, DialogClose,
} from '@/components/ui/dialog'
import { api } from '@/api/client'
import type {
  ClothingModelListResponseDto,
  ClothingModelResponseDto,
  ClothingVariantResponseDto,
  ClothingCategory,
} from '@/api/types/clothing'

const route = useRoute()

// ── Data ────────────────────────────────────────────────────────────────────
const models = ref<ClothingModelListResponseDto[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

// Filters
const activeCategory = ref<'All' | ClothingCategory>('All')
const categories = ['All', 'Tops', 'Bottoms', 'Dresses', 'Outerwear', 'Accessories'] as const
const categoryOptions = ['Tops', 'Bottoms', 'Dresses', 'Outerwear', 'Accessories'] as const

// Expanded model variants
const expandedModelId = ref<string | null>(null)
const variants = ref<ClothingVariantResponseDto[]>([])
const loadingVariants = ref(false)

// Model CRUD
const showModelDialog = ref(false)
const editingModel = ref<ClothingModelListResponseDto | null>(null)
const modelForm = ref({ name: '', description: '', brand: '', category: 'Tops' as ClothingCategory, price: 0 })
const savingModel = ref(false)
const modelError = ref<string | null>(null)

const showDeleteModelDialog = ref(false)
const deletingModel = ref<ClothingModelListResponseDto | null>(null)
const deletingModelLoading = ref(false)

// Variant CRUD
const showVariantDialog = ref(false)
const variantForm = ref({ size: 'S', color: '#000000', imagePath: '', stockQuantity: 0 })
const savingVariant = ref(false)
const variantError = ref<string | null>(null)

const showStockDialog = ref(false)
const editingVariant = ref<ClothingVariantResponseDto | null>(null)
const stockForm = ref({ stockQuantity: 0 })
const savingStock = ref(false)
const stockError = ref<string | null>(null)

const showDeleteVariantDialog = ref(false)
const deletingVariant = ref<ClothingVariantResponseDto | null>(null)
const deletingVariantLoading = ref(false)

// ── Computed ────────────────────────────────────────────────────────────────
const filteredModels = computed(() => {
  if (activeCategory.value === 'All') return models.value
  return models.value.filter(m => m.category === activeCategory.value)
})

const totalModels = computed(() => models.value.length)

// ── Data Loading ────────────────────────────────────────────────────────────
onMounted(async () => {
  try {
    models.value = await api<ClothingModelListResponseDto[]>('/clothing')
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : 'Failed to load inventory'
  } finally {
    loading.value = false
  }
})

async function toggleExpand(modelId: string) {
  if (expandedModelId.value === modelId) {
    expandedModelId.value = null
    return
  }
  loadingVariants.value = true
  try {
    variants.value = await api<ClothingVariantResponseDto[]>(`/clothing/${modelId}/variants`)
    expandedModelId.value = modelId
  } catch {
    // silently fail
  } finally {
    loadingVariants.value = false
  }
}

// ── Model CRUD ──────────────────────────────────────────────────────────────
function openCreateModel() {
  editingModel.value = null
  modelForm.value = { name: '', description: '', brand: '', category: 'Tops', price: 0 }
  modelError.value = null
  showModelDialog.value = true
}

function openEditModel(model: ClothingModelListResponseDto) {
  editingModel.value = model
  modelForm.value = {
    name: model.name,
    description: model.description,
    brand: model.brand,
    category: model.category,
    price: model.price,
  }
  modelError.value = null
  showModelDialog.value = true
}

function validateModel(): string | null {
  if (!modelForm.value.name.trim()) return 'Name is required.'
  if (!modelForm.value.brand.trim()) return 'Brand is required.'
  if (modelForm.value.price <= 0) return 'Price must be greater than 0.'
  return null
}

async function saveModel() {
  const err = validateModel()
  if (err) { modelError.value = err; return }

  savingModel.value = true
  modelError.value = null
  try {
    if (editingModel.value) {
      await api<ClothingModelResponseDto>(`/clothing/${editingModel.value.clothingModelID}`, {
        method: 'PUT',
        body: JSON.stringify(modelForm.value),
      })
    } else {
      await api<ClothingModelResponseDto>('/clothing', {
        method: 'POST',
        body: JSON.stringify(modelForm.value),
      })
    }
    models.value = await api<ClothingModelListResponseDto[]>('/clothing')
    showModelDialog.value = false
  } catch (e: unknown) {
    modelError.value = e instanceof Error ? e.message : 'Failed to save model.'
  } finally {
    savingModel.value = false
  }
}

function openDeleteModel(model: ClothingModelListResponseDto) {
  deletingModel.value = model
  showDeleteModelDialog.value = true
}

async function confirmDeleteModel() {
  if (!deletingModel.value) return
  deletingModelLoading.value = true
  try {
    await api<void>(`/clothing/${deletingModel.value.clothingModelID}`, { method: 'DELETE' })
    models.value = models.value.filter(m => m.clothingModelID !== deletingModel.value!.clothingModelID)
    if (expandedModelId.value === deletingModel.value.clothingModelID) {
      expandedModelId.value = null
    }
    showDeleteModelDialog.value = false
    deletingModel.value = null
  } catch {
    // silently fail
  } finally {
    deletingModelLoading.value = false
  }
}

// ── Variant CRUD ────────────────────────────────────────────────────────────
function openCreateVariant() {
  variantForm.value = { size: 'S', color: '#000000', imagePath: '', stockQuantity: 0 }
  variantError.value = null
  showVariantDialog.value = true
}

function validateVariant(): string | null {
  if (!/^#[0-9A-Fa-f]{6}$/.test(variantForm.value.color)) return 'Color must be a valid hex code (e.g. #FF5733).'
  if (!/\.(jpg|jpeg|png|gif|webp|svg)$/i.test(variantForm.value.imagePath)) return 'Image path must end with .jpg, .jpeg, .png, .gif, .webp, or .svg.'
  if (variantForm.value.stockQuantity < 0) return 'Stock quantity must be 0 or greater.'
  return null
}

async function saveVariant() {
  const err = validateVariant()
  if (err) { variantError.value = err; return }

  savingVariant.value = true
  variantError.value = null
  try {
    await api<ClothingVariantResponseDto>(`/clothing/${expandedModelId.value}/variants`, {
      method: 'POST',
      body: JSON.stringify(variantForm.value),
    })
    variants.value = await api<ClothingVariantResponseDto[]>(`/clothing/${expandedModelId.value}/variants`)
    models.value = await api<ClothingModelListResponseDto[]>('/clothing')
    showVariantDialog.value = false
  } catch (e: unknown) {
    variantError.value = e instanceof Error ? e.message : 'Failed to create variant.'
  } finally {
    savingVariant.value = false
  }
}

function openEditStock(variant: ClothingVariantResponseDto) {
  editingVariant.value = variant
  stockForm.value = { stockQuantity: variant.stockQuantity }
  stockError.value = null
  showStockDialog.value = true
}

async function saveStock() {
  if (stockForm.value.stockQuantity < 0) {
    stockError.value = 'Stock quantity must be 0 or greater.'
    return
  }
  savingStock.value = true
  stockError.value = null
  try {
    await api<ClothingVariantResponseDto>(
      `/clothing/${expandedModelId.value}/variants/${editingVariant.value!.clothingVariantID}`,
      { method: 'PATCH', body: JSON.stringify(stockForm.value) },
    )
    variants.value = await api<ClothingVariantResponseDto[]>(`/clothing/${expandedModelId.value}/variants`)
    showStockDialog.value = false
  } catch (e: unknown) {
    stockError.value = e instanceof Error ? e.message : 'Failed to update stock.'
  } finally {
    savingStock.value = false
  }
}

function openDeleteVariant(variant: ClothingVariantResponseDto) {
  deletingVariant.value = variant
  showDeleteVariantDialog.value = true
}

async function confirmDeleteVariant() {
  if (!deletingVariant.value) return
  deletingVariantLoading.value = true
  try {
    await api<void>(
      `/clothing/${expandedModelId.value}/variants/${deletingVariant.value.clothingVariantID}`,
      { method: 'DELETE' },
    )
    variants.value = variants.value.filter(v => v.clothingVariantID !== deletingVariant.value!.clothingVariantID)
    models.value = await api<ClothingModelListResponseDto[]>('/clothing')
    showDeleteVariantDialog.value = false
    deletingVariant.value = null
  } catch {
    // silently fail
  } finally {
    deletingVariantLoading.value = false
  }
}

// ── Helpers ─────────────────────────────────────────────────────────────────
function formatPrice(price: number): string {
  return Number.isInteger(price) ? `${price}` : price.toFixed(2)
}

// ── Sidebar ─────────────────────────────────────────────────────────────────
const navItems = [
  { key: 'dashboard', label: 'Dashboard', icon: LayoutDashboard, to: '/manager' },
  { key: 'orders', label: 'Orders', icon: ShoppingBag, to: '/manager/orders' },
  { key: 'inventory', label: 'Inventory', icon: Boxes, to: '/manager/inventory' },
  { key: 'customers', label: 'Customers', icon: Users, to: null },
  { key: 'employees', label: 'Employees', icon: UserCog, to: null },
]

const activeSection = computed(() => {
  if (route.path === '/manager') return 'dashboard'
  if (route.path.startsWith('/manager/orders')) return 'orders'
  if (route.path.startsWith('/manager/inventory')) return 'inventory'
  return ''
})
</script>

<template>
  <div class="flex min-h-screen bg-(--bg)">
    <!-- Sidebar -->
    <aside class="w-52 shrink-0 border-r border-(--text-light) flex flex-col pt-8 gap-1">
      <div class="flex flex-col items-center gap-2 pb-6 border-b border-(--text-light) px-4">
        <div class="w-12 h-12 border border-(--text-light) flex items-center justify-center">
          <ShieldCheck class="w-5 h-5 text-(--text-muted)" />
        </div>
        <span class="text-[10px] text-(--text-light) uppercase tracking-[0.2em]">Manager</span>
      </div>

      <nav class="flex flex-col gap-0 px-0 pt-2">
        <template
          v-for="item in navItems"
          :key="item.key"
        >
          <component
            :is="item.to ? RouterLink : 'button'"
            v-bind="item.to ? { to: item.to } : {}"
            class="flex items-center gap-3 px-5 py-3 text-[13px] tracking-wide border-b border-(--text-light) transition-colors w-full text-left"
            :class="
              activeSection === item.key
                ? 'bg-(--card-hover) text-(--text) font-medium'
                : 'text-(--text-muted) hover:bg-(--card-hover) hover:text-(--text)'
            "
          >
            <component
              :is="item.icon"
              class="w-4 h-4 shrink-0"
            />
            {{ item.label }}
          </component>
        </template>
      </nav>
    </aside>

    <!-- Main -->
    <main class="flex-1 px-10 pt-16 pb-20">
      <!-- Heading -->
      <h1 class="inventory-heading text-[40px] lg:text-[52px] font-normal tracking-tight leading-tight mb-2">
        <span class="text-(--text-muted)">Inventory</span>
        <em class="text-(--text-light)"> Management</em>
      </h1>
      <div class="flex items-center justify-between mb-10 pb-6 border-b border-(--text-light)">
        <p class="text-sm font-light text-(--text-muted)">
          Manage clothing models and their variants.
        </p>
        <span class="text-[12px] text-(--text-light) uppercase tracking-widest hidden sm:inline">Inventory</span>
      </div>

      <!-- Stats -->
      <div class="grid grid-cols-3 gap-0 border border-(--text-light) mb-10">
        <div
          class="stat-card p-7 border-r border-(--text-light)"
          style="animation-delay: 0s"
        >
          <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
            Total Models
          </p>
          <p class="text-[44px] font-light text-(--text) leading-none">
            {{ loading ? '—' : totalModels }}
          </p>
        </div>
        <div
          class="stat-card p-7 border-r border-(--text-light)"
          style="animation-delay: 0.1s"
        >
          <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
            Showing
          </p>
          <p class="text-[44px] font-light text-(--text) leading-none">
            {{ loading ? '—' : filteredModels.length }}
          </p>
        </div>
        <div
          class="stat-card p-7"
          style="animation-delay: 0.2s"
        >
          <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light) mb-3">
            Categories
          </p>
          <p class="text-[44px] font-light text-(--text) leading-none">
            {{ loading ? '—' : new Set(models.map(m => m.category)).size }}
          </p>
        </div>
      </div>

      <!-- Models Table -->
      <div
        class="stat-card border border-(--text-light)"
        style="animation-delay: 0.3s"
      >
        <!-- Filter tabs + Add button -->
        <div class="flex items-center border-b border-(--text-light)">
          <button
            v-for="cat in categories"
            :key="cat"
            class="px-5 py-3.5 text-[11px] uppercase tracking-widest border-r border-(--text-light) transition-colors"
            :class="activeCategory === cat ? 'bg-(--card-hover) text-(--text) font-medium' : 'text-(--text-muted) hover:bg-(--card-hover)'"
            @click="activeCategory = cat"
          >
            {{ cat }}
          </button>
          <button
            class="ml-auto flex items-center gap-2 px-5 py-3.5 text-[11px] uppercase tracking-widest text-(--text) hover:bg-(--card-hover) transition-colors"
            @click="openCreateModel"
          >
            <Plus class="w-3.5 h-3.5" />
            Add Model
          </button>
        </div>

        <!-- Table header -->
        <div class="grid grid-cols-[2fr_1.5fr_1fr_1fr_1.5fr] px-6 py-2.5 border-b border-(--text-light)">
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Name</span>
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Brand</span>
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Category</span>
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Price</span>
          <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Actions</span>
        </div>

        <!-- Loading -->
        <div
          v-if="loading"
          class="px-6 py-10 text-[13px] text-(--text-light)"
        >
          Loading inventory...
        </div>

        <!-- Error -->
        <div
          v-else-if="error"
          class="px-6 py-10 text-[13px] text-red-400"
        >
          {{ error }}
        </div>

        <!-- Empty -->
        <div
          v-else-if="filteredModels.length === 0"
          class="px-6 py-10 text-[13px] text-(--text-light)"
        >
          No models found.
        </div>

        <!-- Model Rows -->
        <template v-else>
          <div
            v-for="(model, i) in filteredModels"
            :key="model.clothingModelID"
          >
            <!-- Model Row -->
            <div
              class="grid grid-cols-[2fr_1.5fr_1fr_1fr_1.5fr] px-6 py-4 border-b border-(--text-light) hover:bg-(--card-hover) transition-colors items-center row-card"
              :style="{ animationDelay: `${0.3 + i * 0.04}s` }"
            >
              <span class="text-[13px] text-(--text) font-light tracking-wide">{{ model.name }}</span>
              <span class="text-[13px] text-(--text-muted) font-light">{{ model.brand }}</span>
              <span class="text-[11px] uppercase tracking-widest text-(--text-muted)">{{ model.category }}</span>
              <span class="text-[13px] text-(--text) font-light">${{ formatPrice(model.price) }}</span>
              <div class="flex items-center gap-2">
                <button
                  class="p-1.5 border border-(--text-light) text-(--text-muted) hover:bg-(--card-hover) hover:text-(--text) transition-colors"
                  title="Edit model"
                  @click="openEditModel(model)"
                >
                  <Pencil class="w-3.5 h-3.5" />
                </button>
                <button
                  class="p-1.5 border border-(--text-light) text-(--text-muted) hover:bg-destructive hover:text-(--bg) transition-colors"
                  title="Delete model"
                  @click="openDeleteModel(model)"
                >
                  <Trash2 class="w-3.5 h-3.5" />
                </button>
                <button
                  class="flex items-center gap-1 px-3 py-1.5 border border-(--text-light) text-[11px] uppercase tracking-widest text-(--text-muted) hover:bg-(--card-hover) hover:text-(--text) transition-colors"
                  @click="toggleExpand(model.clothingModelID)"
                >
                  Variants
                  <component
                    :is="expandedModelId === model.clothingModelID ? ChevronUp : ChevronDown"
                    class="w-3.5 h-3.5"
                  />
                </button>
              </div>
            </div>

            <!-- Expanded Variants -->
            <div
              v-if="expandedModelId === model.clothingModelID"
              class="bg-(--card-hover) border-b border-(--text-light)"
            >
              <!-- Variants header -->
              <div class="flex items-center px-10 py-3 border-b border-(--text-light)">
                <Package class="w-3.5 h-3.5 text-(--text-light) mr-2" />
                <span class="text-[10px] uppercase tracking-[0.2em] text-(--text-light)">
                  Variants for {{ model.name }}
                </span>
                <button
                  class="ml-auto flex items-center gap-1.5 text-[11px] uppercase tracking-widest text-(--text) hover:text-(--text-muted) transition-colors"
                  @click="openCreateVariant"
                >
                  <Plus class="w-3 h-3" />
                  Add Variant
                </button>
              </div>

              <!-- Variant column headers -->
              <div class="grid grid-cols-[1fr_1.5fr_2fr_1fr_1.5fr] px-10 py-2 border-b border-(--text-light)">
                <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Size</span>
                <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Color</span>
                <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Image</span>
                <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Stock</span>
                <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Actions</span>
              </div>

              <!-- Loading variants -->
              <div
                v-if="loadingVariants"
                class="px-10 py-6 text-[13px] text-(--text-light)"
              >
                Loading variants...
              </div>

              <!-- Empty variants -->
              <div
                v-else-if="variants.length === 0"
                class="px-10 py-6 text-[13px] text-(--text-light)"
              >
                No variants yet.
              </div>

              <!-- Variant rows -->
              <div
                v-for="variant in variants"
                v-else
                :key="variant.clothingVariantID"
                class="grid grid-cols-[1fr_1.5fr_2fr_1fr_1.5fr] px-10 py-3 border-b border-(--text-light) last:border-b-0 items-center"
              >
                <span class="text-[13px] text-(--text) font-light">{{ variant.size }}</span>
                <div class="flex items-center gap-2">
                  <div
                    class="w-4 h-4 rounded-full border border-(--text-light)"
                    :style="{ backgroundColor: variant.color }"
                  />
                  <span class="text-[12px] text-(--text-muted) font-mono">{{ variant.color }}</span>
                </div>
                <span class="text-[12px] text-(--text-muted) font-light truncate pr-2">{{ variant.imagePath }}</span>
                <span
                  class="text-[13px] font-light"
                  :class="variant.stockQuantity < 5 ? 'text-red-400' : 'text-(--text)'"
                >
                  {{ variant.stockQuantity }}
                </span>
                <div class="flex items-center gap-2">
                  <button
                    class="text-[11px] uppercase tracking-widest border border-(--text-light) px-3 py-1 text-(--text-muted) hover:bg-(--text) hover:text-(--bg) transition-colors"
                    @click="openEditStock(variant)"
                  >
                    Edit Stock
                  </button>
                  <button
                    class="p-1.5 border border-(--text-light) text-(--text-muted) hover:bg-destructive hover:text-(--bg) transition-colors"
                    title="Delete variant"
                    @click="openDeleteVariant(variant)"
                  >
                    <Trash2 class="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </main>

    <!-- Create/Edit Model Dialog -->
    <Dialog v-model:open="showModelDialog">
      <DialogContent class="rounded-none border border-(--text-light) bg-(--bg) text-(--text) shadow-sm max-w-md">
        <DialogHeader>
          <DialogTitle class="text-lg font-normal tracking-tight">
            {{ editingModel ? 'Edit Model' : 'Create Model' }}
          </DialogTitle>
          <DialogDescription class="text-[13px] text-(--text-muted)">
            {{ editingModel ? 'Update the clothing model details.' : 'Add a new clothing model to the inventory.' }}
          </DialogDescription>
        </DialogHeader>

        <div class="space-y-4 py-2">
          <p
            v-if="modelError"
            class="text-[12px] text-red-400"
          >
            {{ modelError }}
          </p>

          <div class="space-y-1.5">
            <Label class="text-[11px] uppercase tracking-widest text-(--text-light)">Name</Label>
            <Input
              v-model="modelForm.name"
              class="rounded-none border-(--text-light) bg-transparent text-(--text) text-[13px] focus-visible:ring-0 focus-visible:border-(--text-muted)"
              placeholder="e.g. Classic Oxford Shirt"
            />
          </div>

          <div class="space-y-1.5">
            <Label class="text-[11px] uppercase tracking-widest text-(--text-light)">Brand</Label>
            <Input
              v-model="modelForm.brand"
              class="rounded-none border-(--text-light) bg-transparent text-(--text) text-[13px] focus-visible:ring-0 focus-visible:border-(--text-muted)"
              placeholder="e.g. Ralph Lauren"
            />
          </div>

          <div class="space-y-1.5">
            <Label class="text-[11px] uppercase tracking-widest text-(--text-light)">Description</Label>
            <Input
              v-model="modelForm.description"
              class="rounded-none border-(--text-light) bg-transparent text-(--text) text-[13px] focus-visible:ring-0 focus-visible:border-(--text-muted)"
              placeholder="Optional description"
            />
          </div>

          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <Label class="text-[11px] uppercase tracking-widest text-(--text-light)">Category</Label>
              <Select v-model="modelForm.category">
                <SelectTrigger class="rounded-none border-(--text-light) bg-transparent text-(--text) text-[13px] focus-visible:ring-0 shadow-none">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent class="rounded-none border-(--text-light) bg-(--bg)! text-(--text) shadow-sm">
                  <SelectItem
                    v-for="cat in categoryOptions"
                    :key="cat"
                    :value="cat"
                    class="rounded-none text-[13px] text-(--text-muted) focus:bg-(--card-hover)! focus:text-(--text)"
                  >
                    {{ cat }}
                  </SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div class="space-y-1.5">
              <Label class="text-[11px] uppercase tracking-widest text-(--text-light)">Price ($)</Label>
              <Input
                v-model.number="modelForm.price"
                type="number"
                min="0"
                step="0.01"
                class="rounded-none border-(--text-light) bg-transparent text-(--text) text-[13px] focus-visible:ring-0 focus-visible:border-(--text-muted)"
              />
            </div>
          </div>
        </div>

        <DialogFooter class="gap-2">
          <DialogClose as-child>
            <Button
              variant="outline"
              class="rounded-none border-(--text-light) text-(--text-muted) hover:bg-(--card-hover) text-[12px] tracking-wide"
            >
              Cancel
            </Button>
          </DialogClose>
          <Button
            :disabled="savingModel"
            class="rounded-none bg-(--text) text-(--bg) hover:bg-(--text-muted) text-[12px] tracking-wide"
            @click="saveModel"
          >
            {{ savingModel ? 'Saving...' : (editingModel ? 'Update' : 'Create') }}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <!-- Delete Model Dialog -->
    <Dialog v-model:open="showDeleteModelDialog">
      <DialogContent class="rounded-none border border-(--text-light) bg-(--bg) text-(--text) shadow-sm max-w-sm">
        <DialogHeader>
          <DialogTitle class="text-lg font-normal tracking-tight">
            Delete Model
          </DialogTitle>
          <DialogDescription class="text-[13px] text-(--text-muted)">
            Are you sure you want to delete <strong class="text-(--text)">{{ deletingModel?.name }}</strong>?
            This will also remove all its variants.
          </DialogDescription>
        </DialogHeader>
        <DialogFooter class="gap-2">
          <DialogClose as-child>
            <Button
              variant="outline"
              class="rounded-none border-(--text-light) text-(--text-muted) hover:bg-(--card-hover) text-[12px] tracking-wide"
            >
              Cancel
            </Button>
          </DialogClose>
          <Button
            variant="destructive"
            :disabled="deletingModelLoading"
            class="rounded-none text-[12px] tracking-wide"
            @click="confirmDeleteModel"
          >
            {{ deletingModelLoading ? 'Deleting...' : 'Delete' }}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <!-- Create Variant Dialog -->
    <Dialog v-model:open="showVariantDialog">
      <DialogContent class="rounded-none border border-(--text-light) bg-(--bg) text-(--text) shadow-sm max-w-md">
        <DialogHeader>
          <DialogTitle class="text-lg font-normal tracking-tight">
            Add Variant
          </DialogTitle>
          <DialogDescription class="text-[13px] text-(--text-muted)">
            Create a new size/color variant for this model.
          </DialogDescription>
        </DialogHeader>

        <div class="space-y-4 py-2">
          <p
            v-if="variantError"
            class="text-[12px] text-red-400"
          >
            {{ variantError }}
          </p>

          <div class="grid grid-cols-2 gap-4">
            <div class="space-y-1.5">
              <Label class="text-[11px] uppercase tracking-widest text-(--text-light)">Size</Label>
              <Select v-model="variantForm.size">
                <SelectTrigger class="rounded-none border-(--text-light) bg-transparent text-(--text) text-[13px] focus-visible:ring-0 shadow-none">
                  <SelectValue />
                </SelectTrigger>
                <SelectContent class="rounded-none border-(--text-light) bg-(--bg)! text-(--text) shadow-sm">
                  <SelectItem
                    v-for="size in ['S', 'M', 'L', 'XL']"
                    :key="size"
                    :value="size"
                    class="rounded-none text-[13px] text-(--text-muted) focus:bg-(--card-hover)! focus:text-(--text)"
                  >
                    {{ size }}
                  </SelectItem>
                </SelectContent>
              </Select>
            </div>

            <div class="space-y-1.5">
              <Label class="text-[11px] uppercase tracking-widest text-(--text-light)">Color</Label>
              <div class="flex items-center gap-2">
                <Input
                  v-model="variantForm.color"
                  class="rounded-none border-(--text-light) bg-transparent text-(--text) text-[13px] font-mono focus-visible:ring-0 focus-visible:border-(--text-muted) flex-1"
                  placeholder="#000000"
                />
                <input
                  type="color"
                  :value="variantForm.color"
                  class="w-8 h-8 border border-(--text-light) bg-transparent cursor-pointer p-0"
                  @input="variantForm.color = ($event.target as HTMLInputElement).value"
                >
              </div>
            </div>
          </div>

          <div class="space-y-1.5">
            <Label class="text-[11px] uppercase tracking-widest text-(--text-light)">Image Path</Label>
            <Input
              v-model="variantForm.imagePath"
              class="rounded-none border-(--text-light) bg-transparent text-(--text) text-[13px] focus-visible:ring-0 focus-visible:border-(--text-muted)"
              placeholder="e.g. /images/shirt-blue.jpg"
            />
          </div>

          <div class="space-y-1.5">
            <Label class="text-[11px] uppercase tracking-widest text-(--text-light)">Stock Quantity</Label>
            <Input
              v-model.number="variantForm.stockQuantity"
              type="number"
              min="0"
              class="rounded-none border-(--text-light) bg-transparent text-(--text) text-[13px] focus-visible:ring-0 focus-visible:border-(--text-muted)"
            />
          </div>
        </div>

        <DialogFooter class="gap-2">
          <DialogClose as-child>
            <Button
              variant="outline"
              class="rounded-none border-(--text-light) text-(--text-muted) hover:bg-(--card-hover) text-[12px] tracking-wide"
            >
              Cancel
            </Button>
          </DialogClose>
          <Button
            :disabled="savingVariant"
            class="rounded-none bg-(--text) text-(--bg) hover:bg-(--text-muted) text-[12px] tracking-wide"
            @click="saveVariant"
          >
            {{ savingVariant ? 'Creating...' : 'Create Variant' }}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <!-- Edit Stock Dialog -->
    <Dialog v-model:open="showStockDialog">
      <DialogContent class="rounded-none border border-(--text-light) bg-(--bg) text-(--text) shadow-sm max-w-sm">
        <DialogHeader>
          <DialogTitle class="text-lg font-normal tracking-tight">
            Update Stock
          </DialogTitle>
          <DialogDescription class="text-[13px] text-(--text-muted)">
            Update stock quantity for {{ editingVariant?.size }} / {{ editingVariant?.color }}.
          </DialogDescription>
        </DialogHeader>

        <div class="space-y-4 py-2">
          <p
            v-if="stockError"
            class="text-[12px] text-red-400"
          >
            {{ stockError }}
          </p>

          <div class="space-y-1.5">
            <Label class="text-[11px] uppercase tracking-widest text-(--text-light)">Stock Quantity</Label>
            <Input
              v-model.number="stockForm.stockQuantity"
              type="number"
              min="0"
              class="rounded-none border-(--text-light) bg-transparent text-(--text) text-[13px] focus-visible:ring-0 focus-visible:border-(--text-muted)"
            />
          </div>
        </div>

        <DialogFooter class="gap-2">
          <DialogClose as-child>
            <Button
              variant="outline"
              class="rounded-none border-(--text-light) text-(--text-muted) hover:bg-(--card-hover) text-[12px] tracking-wide"
            >
              Cancel
            </Button>
          </DialogClose>
          <Button
            :disabled="savingStock"
            class="rounded-none bg-(--text) text-(--bg) hover:bg-(--text-muted) text-[12px] tracking-wide"
            @click="saveStock"
          >
            {{ savingStock ? 'Saving...' : 'Update Stock' }}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>

    <!-- Delete Variant Dialog -->
    <Dialog v-model:open="showDeleteVariantDialog">
      <DialogContent class="rounded-none border border-(--text-light) bg-(--bg) text-(--text) shadow-sm max-w-sm">
        <DialogHeader>
          <DialogTitle class="text-lg font-normal tracking-tight">
            Delete Variant
          </DialogTitle>
          <DialogDescription class="text-[13px] text-(--text-muted)">
            Are you sure you want to delete the <strong class="text-(--text)">{{ deletingVariant?.size }} / {{ deletingVariant?.color }}</strong> variant?
          </DialogDescription>
        </DialogHeader>
        <DialogFooter class="gap-2">
          <DialogClose as-child>
            <Button
              variant="outline"
              class="rounded-none border-(--text-light) text-(--text-muted) hover:bg-(--card-hover) text-[12px] tracking-wide"
            >
              Cancel
            </Button>
          </DialogClose>
          <Button
            variant="destructive"
            :disabled="deletingVariantLoading"
            class="rounded-none text-[12px] tracking-wide"
            @click="confirmDeleteVariant"
          >
            {{ deletingVariantLoading ? 'Deleting...' : 'Delete' }}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  </div>
</template>

<style scoped>
.inventory-heading {
  font-family: 'Playfair Display', serif;
  letter-spacing: -1.5px;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

.stat-card,
.row-card {
  animation: fadeUp 0.6s ease both;
}
</style>
