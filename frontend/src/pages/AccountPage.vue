<script setup lang="ts">
import {ref, computed} from 'vue'
import {Pencil, ShoppingBag} from 'lucide-vue-next'
import {Button} from '@/components/ui/button'
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog'
import {Input} from '@/components/ui/input'
import {Label} from '@/components/ui/label'
import {useRouter} from 'vue-router'
import {useAuthStore} from '@/stores/auth'
import {api} from '@/api/client'
import type {CustomerResponseDto} from '@/api/types/person'

const auth = useAuthStore()
const router = useRouter()

const person = computed(() => auth.person as CustomerResponseDto | null)

const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const passwordError = ref('')
const passwordDialogOpen = ref(false)

const newAddress = ref(person.value?.address ?? '')
const addressError = ref('')
const addressDialogOpen = ref(false)

async function handlePasswordUpdate() {
  passwordError.value = ''

  if (!oldPassword.value || !newPassword.value || !confirmPassword.value) {
    passwordError.value = 'Please fill in all fields.'
    return
  }

  if (newPassword.value !== confirmPassword.value) {
    passwordError.value = 'New passwords do not match.'
    return
  }

  if (oldPassword.value === newPassword.value) {
    passwordError.value = 'New password must be different from the current password.'
    return
  }

  try {
    // Update password
    await api(`/persons/${auth.personId}/password`, {
      method: 'PATCH',
      body: JSON.stringify({oldPassword: oldPassword.value, newPassword: newPassword.value}),
    })
    passwordDialogOpen.value = false
    oldPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''
  } catch (e) {
    passwordError.value = e instanceof Error ? e.message : 'Something went wrong. Please try again.'
  }
}

async function handleAddressUpdate() {
  addressError.value = ''

  if (!newAddress.value) {
    addressError.value = 'Please enter an address.'
    return
  }

  try {
    // Update address
    const updated = await api<CustomerResponseDto>(`/persons/${auth.personId}/address`, {
      method: 'PATCH',
      body: JSON.stringify({address: newAddress.value}),
    })
    auth.updatePerson(updated)
    addressDialogOpen.value = false
  } catch (e) {
    addressError.value = e instanceof Error ? e.message : 'Something went wrong. Please try again.'
  }
}

function getName(email: string | undefined) {
  if (!email) return ''
  return email.substring(0, email.indexOf('@')).replace('.', ' ')
}
</script>

<template>
  <main class="px-10 pt-16 pb-20 max-w-2xl mx-auto">
    <!-- Heading -->
    <h1
      class="account-heading fade-up text-[40px] lg:text-[46px] font-normal tracking-tight leading-tight mb-2"
      style="animation-delay: 0s"
    >
      <span class="text-(--text-muted)">Welcome back, <span class="text-(--text) capitalize"> {{ getName(person?.email ?? auth.person?.email) }}</span></span>
    </h1>
    <div class="mb-5 pb-6 border-b border-(--text-light)" />

    <div
      class="fade-up px-2 pb-7 space-y-6"
      style="animation-delay: 0.15s"
    >
      <div>
        <p class="text-[16px] uppercase tracking-[1px] text-(--text-light) mb-2">
          Email
        </p>
        <p class="text-[20px] font-light text-(--text-muted)">
          {{ person?.email ?? auth.person?.email }}
        </p>
      </div>

      <div>
        <p class="text-[16px] uppercase tracking-[1px] text-(--text-light) mb-2">
          Address
        </p>
        <div class="flex items-center gap-2">
          <p class="text-[20px] font-light text-(--text-muted)">
            {{ person?.address ?? 'N/A' }}
          </p>
          <Dialog v-model:open="addressDialogOpen">
            <DialogTrigger as-child>
              <button
                class="text-(--text-light) hover:text-(--text-muted) transition-colors"
                title="Change address"
              >
                <Pencil class="w-5 h-5" />
              </button>
            </DialogTrigger>
            <DialogContent class="rounded-sm bg-(--bg)">
              <DialogHeader>
                <DialogTitle>Update Address</DialogTitle>
              </DialogHeader>
              <div class="space-y-4">
                <div
                  v-if="addressError"
                  class="rounded-none bg-red-50 border border-red-200 p-3 text-sm text-red-800"
                >
                  {{ addressError }}
                </div>
                <div class="space-y-2">
                  <Label for="address">New Address</Label>
                  <Input
                    id="address"
                    v-model="newAddress"
                    class="rounded-none"
                    type="text"
                    placeholder=""
                  />
                </div>
              </div>
              <DialogFooter>
                <DialogClose as-child>
                  <Button
                    variant="outline"
                    class="rounded-none bg-transparent border-(--text-light)"
                  >
                    Cancel
                  </Button>
                </DialogClose>
                <Button
                  class="rounded-none bg-(--text)"
                  @click="handleAddressUpdate"
                >
                  Save Address
                </Button>
              </DialogFooter>
            </DialogContent>
          </Dialog>
        </div>
      </div>

      <div>
        <p class="text-[16px] uppercase tracking-[1px] text-(--text-light) mb-2">
          Loyalty Points
        </p>
        <div class="flex items-center gap-2">
          <p class="text-[20px] font-light font-medium text-(--button-hover)">
            {{ person?.loyaltyPoints ?? 0 }}
          </p>
          <button
            class="text-(--button-hover) hover:text-(--text-muted) transition-colors"
            title="Spend points in the store"
            @click="router.push({ name: 'shop' })"
          >
            <ShoppingBag class="w-5 h-5" />
          </button>
        </div>
      </div>

      <div class="pt-2">
        <Dialog v-model:open="passwordDialogOpen">
          <DialogTrigger as-child>
            <Button 
              size="sm" 
              class="rounded-none border-(--text-light) text-[16px] bg-transparent text-(--text-muted) hover:bg-(--card-hover) hover:border-(--text-muted)" 
              :variant="'outline'"
            >
              Change Password
            </Button>
          </DialogTrigger>
          <DialogContent class="rounded-sm bg-(--bg)">
            <DialogHeader>
              <DialogTitle>Change Password</DialogTitle>
            </DialogHeader>
            <div class="space-y-4">
              <div
                v-if="passwordError"
                class="rounded-md bg-red-50 border border-red-200 p-3 text-sm text-red-800"
              >
                {{ passwordError }}
              </div>
              <div class="space-y-2">
                <Label for="old-password">Current Password</Label>
                <Input
                  id="old-password"
                  v-model="oldPassword"
                  class="rounded-none"
                  type="password"
                  placeholder=""
                />
              </div>
              <div class="space-y-2">
                <Label for="new-password">New Password</Label>
                <Input
                  id="new-password"
                  v-model="newPassword"
                  class="rounded-none"
                  type="password"
                  placeholder=""
                />
              </div>
              <div class="space-y-2">
                <Label for="confirm-password">Confirm New Password</Label>
                <Input
                  id="confirm-password"
                  v-model="confirmPassword"
                  class="rounded-none"
                  type="password"
                  placeholder=""
                />
              </div>
            </div>
            <DialogFooter>
              <DialogClose as-child>
                <Button
                  variant="outline"
                  class="rounded-none bg-transparent border-(--text-light)"
                >
                  Cancel
                </Button>
              </DialogClose>
              <Button
                class="rounded-none bg-(--text)"
                @click="handlePasswordUpdate"
              >
                Save Password
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>
    </div>
  </main>
</template>

<style scoped>
.account-heading {
  font-family: 'Lexend Deca', sans-serif;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

.fade-up {
  animation: fadeUp 0.6s ease both;
}
</style>
