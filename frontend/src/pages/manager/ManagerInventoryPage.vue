<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {
  ArrowLeft, Pencil, Trash2, Plus, Package,
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
import { useToastStore } from '@/stores/toast'

const toast = useToastStore()
import type {
  ClothingModelListResponseDto,
  ClothingModelResponseDto,
  ClothingVariantResponseDto,
  ClothingCategory,
} from '@/api/types/clothing'

// ── Data ────────────────────────────────────────────────────────────────────
const models = ref<ClothingModelListResponseDto[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

// Filters
const activeCategory = ref<'All' | ClothingCategory>('All')
const categories = ['All', 'Tops', 'Bottoms', 'Dresses', 'Outerwear', 'Accessories'] as const
const categoryOptions = ['Tops', 'Bottoms', 'Dresses', 'Outerwear', 'Accessories'] as const

// Navigation
const currentView = ref<'list' | 'detail'>('list')
const selectedModel = ref<ClothingModelListResponseDto | null>(null)

// Detail panel — variants
const detailVariants = ref<ClothingVariantResponseDto[]>([])
const detailVariantsLoading = ref(false)

interface VariantEditForm {
  stockQuantity: number
  saving: boolean
  error: string | null
}
const variantForms = ref<Record<string, VariantEditForm>>({})

// Model CRUD
const showCreateModelDialog = ref(false)
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

// ── Detailed Edit Panel ─────────────────────────────────────────────────────────────
async function openEditDetails(model: ClothingModelListResponseDto) {
  selectedModel.value = model
  modelForm.value = {
    name: model.name,
    description: model.description,
    brand: model.brand,
    category: model.category,
    price: model.price,
  }
  modelError.value = null
  currentView.value = 'detail'
  detailVariantsLoading.value = true
  detailVariants.value = []
  variantForms.value = {}
  try {
    const loaded = await api<ClothingVariantResponseDto[]>(`/clothing/${model.clothingModelID}/variants`)
    detailVariants.value = loaded
    const forms: Record<string, VariantEditForm> = {}
    for (const v of loaded) {
      forms[v.clothingVariantID] = {
        stockQuantity: v.stockQuantity,
        saving: false,
        error: null,
      }
    }
    variantForms.value = forms
  } catch {
    // leave empty
  } finally {
    detailVariantsLoading.value = false
  }
}

// ── Model CRUD ──────────────────────────────────────────────────────────────
function openCreateModel() {
  modelForm.value = { name: '', description: '', brand: '', category: 'Tops', price: 0 }
  modelError.value = null
  showCreateModelDialog.value = true
}

function validateModel(): string | null {
  if (!modelForm.value.name.trim()) return 'Name is required.'
  if (!modelForm.value.brand.trim()) return 'Brand is required.'
  if (modelForm.value.price <= 0) return 'Price must be >= 0'
  return null
}

async function createModel() {
  const err = validateModel()
  if (err) { modelError.value = err; return }

  savingModel.value = true
  modelError.value = null
  try {
    await api<ClothingModelResponseDto>('/clothing', {
      method: 'POST',
      body: JSON.stringify(modelForm.value),
    })
    models.value = await api<ClothingModelListResponseDto[]>('/clothing')
    showCreateModelDialog.value = false
    toast.success('Model created successfully.')
  } catch (e: unknown) {
    modelError.value = e instanceof Error ? e.message : 'Failed to create model.'
    toast.error(modelError.value!)
  } finally {
    savingModel.value = false
  }
}

async function saveModel() {
  const err = validateModel()
  if (err) { modelError.value = err; return }

  savingModel.value = true
  modelError.value = null
  try {
    await api<ClothingModelResponseDto>(`/clothing/${selectedModel.value!.clothingModelID}`, {
      method: 'PUT',
      body: JSON.stringify(modelForm.value),
    })
    models.value = await api<ClothingModelListResponseDto[]>('/clothing')
    const updated = models.value.find(m => m.clothingModelID === selectedModel.value!.clothingModelID)
    if (updated) selectedModel.value = updated
    toast.success('Model saved successfully.')
  } catch (e: unknown) {
    modelError.value = e instanceof Error ? e.message : 'Failed to save model.'
    toast.error(modelError.value!)
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
    if (selectedModel.value?.clothingModelID === deletingModel.value.clothingModelID) {
      currentView.value = 'list'
      selectedModel.value = null
    }
    showDeleteModelDialog.value = false
    deletingModel.value = null
    toast.success('Model deleted.')
  } catch (e: unknown) {
    toast.error(e instanceof Error ? e.message : 'Failed to delete model.')
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

async function createVariant() {
  const err = validateVariant()
  if (err) { variantError.value = err; return }

  savingVariant.value = true
  variantError.value = null
  try {
    const modelId = selectedModel.value!.clothingModelID
    await api<ClothingVariantResponseDto>(`/clothing/${modelId}/variants`, {
      method: 'POST',
      body: JSON.stringify(variantForm.value),
    })
    const loaded = await api<ClothingVariantResponseDto[]>(`/clothing/${modelId}/variants`)
    detailVariants.value = loaded
    for (const v of loaded) {
      if (!variantForms.value[v.clothingVariantID]) {
        variantForms.value[v.clothingVariantID] = {
          stockQuantity: v.stockQuantity,
          saving: false,
          error: null,
        }
      }
    }
    models.value = await api<ClothingModelListResponseDto[]>('/clothing')
    showVariantDialog.value = false
    toast.success('Variant created successfully.')
  } catch (e: unknown) {
    variantError.value = e instanceof Error ? e.message : 'Failed to create variant.'
    toast.error(variantError.value!)
  } finally {
    savingVariant.value = false
  }
}

async function saveVariantEdit(variantId: string) {
  const form = variantForms.value[variantId]
  if (!form) return

  if (form.stockQuantity < 0) { form.error = 'Stock must be 0 or greater.'; return }

  form.saving = true
  form.error = null
  const modelId = selectedModel.value!.clothingModelID

  try {
    await api<ClothingVariantResponseDto>(
      `/clothing/${modelId}/variants/${variantId}`,
      { method: 'PATCH', body: JSON.stringify({ stockQuantity: form.stockQuantity }) },
    )
    const refreshed = await api<ClothingVariantResponseDto[]>(`/clothing/${modelId}/variants`)
    detailVariants.value = refreshed
    for (const v of refreshed) {
      if (variantForms.value[v.clothingVariantID]) {
        variantForms.value[v.clothingVariantID].stockQuantity = v.stockQuantity
      }
    }
    toast.success('Stock updated.')
  } catch (e: unknown) {
    form.error = e instanceof Error ? e.message : 'Failed to update stock.'
    toast.error(form.error)
  } finally {
    form.saving = false
  }
}

function openDeleteVariant(variant: ClothingVariantResponseDto) {
  deletingVariant.value = variant
  showDeleteVariantDialog.value = true
}

async function confirmDeleteVariant() {
  if (!deletingVariant.value) return
  deletingVariantLoading.value = true
  const modelId = selectedModel.value!.clothingModelID
  try {
    await api<void>(
      `/clothing/${modelId}/variants/${deletingVariant.value.clothingVariantID}`,
      { method: 'DELETE' },
    )
    detailVariants.value = detailVariants.value.filter(v => v.clothingVariantID !== deletingVariant.value!.clothingVariantID)
    const updatedForms = { ...variantForms.value }
    delete updatedForms[deletingVariant.value.clothingVariantID]
    variantForms.value = updatedForms
    models.value = await api<ClothingModelListResponseDto[]>('/clothing')
    showDeleteVariantDialog.value = false
    deletingVariant.value = null
    toast.success('Variant deleted.')
  } catch (e: unknown) {
    toast.error(e instanceof Error ? e.message : 'Failed to delete variant.')
  } finally {
    deletingVariantLoading.value = false
  }
}

// ── Helpers ─────────────────────────────────────────────────────────────────
function formatPrice(price: number): string {
  return Number.isInteger(price) ? `${price}` : price.toFixed(2)
}
</script>

<template>
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

    <!-- MAIN LIST VIEW (ALL clothing models : name, brand, category, price, stock) -->
    <div
      v-if="currentView === 'list'"
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
      <div class="grid grid-cols-[2fr_1.5fr_1fr_1fr_1fr_auto] px-6 py-2.5 border-b border-(--text-light)">
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Name</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Brand</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Category</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Price</span>
        <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Stock</span>
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
          class="grid grid-cols-[2fr_1.5fr_1fr_1fr_1fr_auto] px-6 py-4 border-b border-(--text-light) hover:bg-(--card-hover) transition-colors items-center row-card"
          :style="{ animationDelay: `${0.3 + i * 0.04}s` }"
        >
          <span class="text-[13px] text-(--text) font-light tracking-wide">{{ model.name }}</span>
          <span class="text-[13px] text-(--text) font-light">{{ model.brand }}</span>
          <span class="text-[11px] uppercase tracking-widest text-(--text-muted)">{{ model.category }}</span>
          <span class="text-[13px] text-(--text) font-light">${{ formatPrice(model.price) }}</span>
          <span class="text-[13px] text-(--text) font-light">{{ model.totalStockQuantity }}</span>
          <div class="flex items-center gap-2">
            <button
              class="p-1.5 border border-(--text-light) text-(--text-muted) hover:bg-(--card-hover) hover:text-(--text) transition-colors"
              title="Edit model"
              @click="openEditDetails(model)"
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
          </div>
        </div>
      </template>
    </div>

    <!-- DETAILED EDIT SPECIFIC CLOTHING MODEL  -->
    <div
      v-else-if="currentView === 'detail' && selectedModel"
      class="stat-card border border-(--text-light)"
      style="animation-delay: 0.3s"
    >
      <!-- Header -->
      <div class="flex items-center justify-between px-8 py-5 border-b border-(--text-light)">
        <button
          class="flex items-center gap-2 text-[11px] uppercase tracking-widest text-(--text-light) hover:text-(--text-muted) transition-colors"
          @click="currentView = 'list'"
        >
          <ArrowLeft class="w-3.5 h-3.5" /> Back to Inventory
        </button>
        <span class="text-[10px] uppercase tracking-[0.2em] text-(--text-light)">
          Editing: <strong class="text-(--text-muted)">{{ selectedModel.name }}</strong>
        </span>
      </div>

      <div class="px-8 pt-8 pb-8 space-y-10">

        <!-- 1 : Specific Model Details -->
        <section>
          <div class="flex items-center justify-between mb-5">
            <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light)">
              Model Details
            </p>
            <div class="flex items-center gap-2">
              <span class="text-[10px] uppercase tracking-[0.18em] text-(--text-light)">Total Stock:</span>
              <span class="text-[13px] font-light text-(--text)">{{ selectedModel.totalStockQuantity }}</span>
            </div>
          </div>
          <p
            v-if="modelError"
            class="text-[12px] text-red-400 mb-4"
          >
            {{ modelError }}
          </p>

          <div class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
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

          <div class="flex items-center justify-between mt-5 pt-5 border-t border-(--text-light)">
            <button
              class="flex items-center gap-1.5 p-1.5 border border-(--text-light) text-(--text-muted) hover:bg-destructive hover:text-(--bg) transition-colors"
              title="Delete model"
              @click="openDeleteModel(selectedModel)"
            >
              <Trash2 class="w-3.5 h-3.5" />
              <span class="text-[11px] uppercase tracking-widest pr-1">Delete Model</span>
            </button>
            <button
              :disabled="savingModel"
              class="text-[11px] uppercase tracking-widest border border-(--text-light) px-5 py-2 text-(--text) hover:bg-(--text) hover:text-(--bg) transition-colors disabled:opacity-40"
              @click="saveModel"
            >
              {{ savingModel ? 'Saving...' : 'Save Changes' }}
            </button>
          </div>
        </section>

        <!-- 2 : Variants -->
        <section>
          <div class="flex items-center justify-between mb-5">
            <div class="flex items-center gap-2">
              <Package class="w-3.5 h-3.5 text-(--text-light)" />
              <p class="text-[10px] uppercase tracking-[0.2em] text-(--text-light)">
                Variants ({{ detailVariants.length }})
              </p>
            </div>
            <button
              class="flex items-center gap-1.5 text-[11px] uppercase tracking-widest text-(--text) hover:text-(--text-muted) transition-colors"
              @click="openCreateVariant"
            >
              <Plus class="w-3 h-3" /> Add Variant
            </button>
          </div>

          <div
            v-if="detailVariantsLoading"
            class="py-8 text-[13px] text-(--text-light)"
          >
            Loading variants...
          </div>

          <div
            v-else-if="detailVariants.length === 0"
            class="py-8 text-[13px] text-(--text-light)"
          >
            No variants yet. Add one above.
          </div>

          <!-- Variant cards grid -->
          <div
            v-else
            class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4"
          >
            <div
              v-for="variant in detailVariants"
              :key="variant.clothingVariantID"
              class="border border-(--text-light) p-4 flex flex-col gap-3"
            >
              <!-- Image preview -->
              <img
                :src="variant.imagePath"
                :alt="`${variant.size} variant`"
                class="w-full h-36 object-cover border border-(--text-light) bg-(--card-hover)"
                @error="($event.target as HTMLImageElement).style.display = 'none'"
              >

              <!-- Size badge (read-only) -->
              <span class="text-[10px] uppercase tracking-widest border border-(--text-light) px-2 py-0.5 w-fit text-(--text-muted)">
                {{ variant.size }}
              </span>

              <!-- Color: display-only picker + hex code -->
              <div class="space-y-1.5">
                <Label class="text-[10px] uppercase tracking-widest text-(--text-light)">Color</Label>
                <div class="flex items-center gap-2">
                  <div
                    class="w-3.5 h-3.5 rounded-full border-[1.5px] border-(--text)"
                    :style="{ backgroundColor: variant.color }"
                    :title="variant.color"
                  />
                  <span class="text-[12px] font-mono text-(--text-muted)">
                    {{ variant.color }}
                  </span>
                </div>
              </div>

              <!-- Stock -->
              <div class="space-y-1.5">
                <Label class="text-[10px] uppercase tracking-widest text-(--text-light)">Stock</Label>
                <Input
                  v-model.number="variantForms[variant.clothingVariantID].stockQuantity"
                  type="number"
                  min="0"
                  class="rounded-none border-(--text-light) bg-transparent text-(--text) text-[13px] focus-visible:ring-0 focus-visible:border-(--text-muted)"
                />
              </div>

              <p
                v-if="variantForms[variant.clothingVariantID]?.error"
                class="text-[12px] text-red-400"
              >
                {{ variantForms[variant.clothingVariantID].error }}
              </p>

              <!-- Actions (Save/Delete) -->
              <div class="flex items-center gap-2 mt-auto pt-2 border-t border-(--text-light)">
                <button
                  :disabled="variantForms[variant.clothingVariantID]?.saving"
                  class="flex-1 text-[11px] uppercase tracking-widest border border-(--text-light) px-3 py-1.5 text-(--text) hover:bg-(--text) hover:text-(--bg) transition-colors disabled:opacity-40"
                  @click="saveVariantEdit(variant.clothingVariantID)"
                >
                  {{ variantForms[variant.clothingVariantID]?.saving ? 'Saving...' : 'Save' }}
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
        </section>
      </div>
    </div>
  </main>

  <!-- Create Model Dialog -->
  <Dialog v-model:open="showCreateModelDialog">
    <DialogContent class="rounded-none border border-(--text-light) bg-(--bg) text-(--text) shadow-sm max-w-md">
      <DialogHeader>
        <DialogTitle class="text-lg font-normal tracking-tight">
          Create Model
        </DialogTitle>
        <DialogDescription class="text-[13px] text-(--text-muted)">
          Add a new clothing model to the inventory.
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
            <Label class="text-[11px] uppercase tracking-widests text-(--text-light)">Category</Label>
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
            <Label class="text-[11px] uppercase tracking-widests text-(--text-light)">Price ($)</Label>
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
          @click="createModel"
        >
          {{ savingModel ? 'Creating...' : 'Create' }}
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
            <Label class="text-[11px] uppercase tracking-widests text-(--text-light)">Size</Label>
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
            <Label class="text-[11px] uppercase tracking-widests text-(--text-light)">Color</Label>
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
          <Label class="text-[11px] uppercase tracking-widests text-(--text-light)">Image Path</Label>
          <Input
            v-model="variantForm.imagePath"
            class="rounded-none border-(--text-light) bg-transparent text-(--text) text-[13px] focus-visible:ring-0 focus-visible:border-(--text-muted)"
            placeholder="e.g. /images/shirt-blue.jpg"
          />
        </div>

        <div class="space-y-1.5">
          <Label class="text-[11px] uppercase tracking-widests text-(--text-light)">Stock Quantity</Label>
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
          @click="createVariant"
        >
          {{ savingVariant ? 'Creating...' : 'Create Variant' }}
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
