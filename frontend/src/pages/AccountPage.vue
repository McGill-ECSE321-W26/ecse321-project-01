<script setup lang="ts">
import {onMounted, ref} from 'vue'
import {Pencil, UserCircle} from 'lucide-vue-next'
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
import {api, ApiError} from '@/api/client'
import type {PersonResponseDto} from '@/api/types/types.ts'

const auth = useAuthStore()

const person = ref<PersonResponseDto | null>(null)

const oldPassword = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const passwordError = ref('')
const passwordDialogOpen = ref(false)

const newAddress = ref('')
const addressError = ref('')
const addressDialogOpen = ref(false)

onMounted(async () => {
  if (auth.personId) {
    person.value = await api<PersonResponseDto>(`/persons/${auth.personId}`)
    newAddress.value = person.value.address ?? ''
  }
})

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
    await api<PersonResponseDto>(`/persons/${auth.personId}/password`, {
      method: 'PATCH',
      body: JSON.stringify({oldPassword: oldPassword.value, newPassword: newPassword.value}),
    })
    passwordDialogOpen.value = false
    oldPassword.value = ''
    newPassword.value = ''
    confirmPassword.value = ''
  } catch (e) {
    passwordError.value = e instanceof ApiError ? e.message : 'Something went wrong. Please try again.'
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
    person.value = await api<PersonResponseDto>(`/persons/${auth.personId}/address`, {
      method: 'PATCH',
      body: JSON.stringify({address: newAddress.value}),
    })
    addressDialogOpen.value = false
  } catch (e) {
    addressError.value = e instanceof ApiError ? e.message : 'Something went wrong. Please try again.'
  }
}
</script>

<template>
  <div class="max-w-2xl mx-auto py-6 px-4 space-y-6">
    <div class="flex items-center gap-3">
      <UserCircle class="w-10 h-10 text-(--text-muted)" />
      <h1 class="text-3xl font-bold">
        {{ person?.email ?? auth.person?.email }}
      </h1>
    </div>

    <div class="border border-(--card-hover) rounded-xl px-4 pt-3 pb-4 space-y-4">
      <div class="flex items-center justify-between">
        <div>
          <p class="text-xs text-(--text-muted) uppercase tracking-wide mb-1">
            Address
          </p>
          <p class="text-sm">
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
        <p class="text-xs text-(--text-muted) uppercase tracking-wide mb-1">
          Loyalty Points
        </p>
        <p class="text-sm">
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
  </div>
</template>
