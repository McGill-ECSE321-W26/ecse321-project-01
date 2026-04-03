<script setup lang="ts">
import {ref} from 'vue'
import {Pencil} from 'lucide-vue-next'
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
import {useAuthStore} from '@/stores/auth'
import {api} from '@/api/client'
import type {CustomerResponseDto} from '@/api/types/person'

const auth = useAuthStore()

const person = ref<CustomerResponseDto | null>(auth.person as CustomerResponseDto | null)

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
    person.value = await api<CustomerResponseDto>(`/persons/${auth.personId}/address`, {
      method: 'PATCH',
      body: JSON.stringify({address: newAddress.value}),
    })
    addressDialogOpen.value = false
  } catch (e) {
    addressError.value = e instanceof Error ? e.message : 'Something went wrong. Please try again.'
  }
}
</script>

<template>
  <main class="px-10 pt-16 pb-20 max-w-2xl mx-auto">
    <!-- Heading -->
    <h1 class="account-heading fade-up text-[40px] lg:text-[52px] font-normal tracking-tight leading-tight mb-2" style="animation-delay: 0s">
      <span class="block text-(--text-muted)">Welcome <em class="text-(--text-light)">back</em>,</span>
      <span class="block text-(--text-light)">{{ person?.email ?? auth.person?.email }}</span>
    </h1>
    <div class="mb-10 pb-6 border-b border-(--text-light)" />

    <div class="fade-up border border-(--text-light) px-7 pt-6 pb-7 space-y-6" style="animation-delay: 0.15s">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-[12px] uppercase tracking-[0.2em] text-(--text-light) mb-2">
            Address
          </p>
          <p class="text-[15px] font-light text-(--text-muted)">
            {{ person?.address ?? 'N/A' }}
          </p>
        </div>
        <Dialog v-model:open="addressDialogOpen">
          <DialogTrigger as-child>
            <Button
              variant="ghost"
              size="icon"
            >
              <Pencil class="w-4 h-4" />
            </Button>
          </DialogTrigger>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Update Address</DialogTitle>
            </DialogHeader>
            <div class="space-y-4">
              <div
                v-if="addressError"
                class="rounded-md bg-red-50 border border-red-200 p-3 text-sm text-red-800"
              >
                {{ addressError }}
              </div>
              <div class="space-y-2">
                <Label for="address">New Address</Label>
                <Input
                  id="address"
                  v-model="newAddress"
                  type="text"
                  placeholder=""
                />
              </div>
            </div>
            <DialogFooter>
              <DialogClose as-child>
                <Button variant="outline">
                  Cancel
                </Button>
              </DialogClose>
              <Button @click="handleAddressUpdate">
                Save Address
              </Button>
            </DialogFooter>
          </DialogContent>
        </Dialog>
      </div>

      <div>
        <p class="text-[12px] uppercase tracking-[0.2em] text-(--text-light) mb-2">
          Loyalty Points
        </p>
        <p class="text-[15px] font-light text-(--text-muted)">
          {{ person?.loyaltyPoints ?? 0 }}
        </p>
      </div>

      <div class="pt-2">
        <Dialog v-model:open="passwordDialogOpen">
          <DialogTrigger as-child>
            <Button size="sm">
              Change Password
            </Button>
          </DialogTrigger>
          <DialogContent>
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
                  type="password"
                  placeholder=""
                />
              </div>
              <div class="space-y-2">
                <Label for="new-password">New Password</Label>
                <Input
                  id="new-password"
                  v-model="newPassword"
                  type="password"
                  placeholder=""
                />
              </div>
              <div class="space-y-2">
                <Label for="confirm-password">Confirm New Password</Label>
                <Input
                  id="confirm-password"
                  v-model="confirmPassword"
                  type="password"
                  placeholder=""
                />
              </div>
            </div>
            <DialogFooter>
              <DialogClose as-child>
                <Button variant="outline">
                  Cancel
                </Button>
              </DialogClose>
              <Button @click="handlePasswordUpdate">
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
  font-family: 'Playfair Display', serif;
  letter-spacing: -1.5px;
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(20px); }
  to   { opacity: 1; transform: translateY(0); }
}

.fade-up {
  animation: fadeUp 0.6s ease both;
}
</style>
