<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRouter } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

const email = ref('')
const password = ref('')
const address = ref('')
const error = ref('')
const loading = ref(false)

async function handleSubmit() {
  error.value = ''

  if (!email.value || !password.value || !address.value) {
    error.value = 'Please fill in all fields.'
    return
  }

  loading.value = true
  try {
    await auth.register(email.value, password.value, address.value)
    await router.push({path: '/login', query: {registered: 'true'}})
  }
  catch (e) {
    error.value = e instanceof Error ? e.message : 'Something went wrong. Please try again.'
  }
  finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="max-w-md mx-auto py-12 px-4">
    <Card>
      <CardHeader>
        <CardTitle class="text-3xl">
          Create Account
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div
          v-if="error"
          class="mb-4 rounded-md bg-red-50 border border-red-200 p-3 text-sm text-red-800"
        >
          {{ error }}
        </div>
        <form
          class="space-y-4"
          @submit.prevent="handleSubmit"
        >
          <div class="space-y-2">
            <Label for="email">Email</Label>
            <Input
              id="email"
              v-model="email"
              type="email"
              placeholder="you@example.com"
            />
          </div>
          <div class="space-y-2">
            <Label for="password">Password</Label>
            <Input
              id="password"
              v-model="password"
              type="password"
              placeholder="Min. 8 characters"
            />
          </div>
          <div class="space-y-2">
            <Label for="address">Address</Label>
            <Input
              id="address"
              v-model="address"
              type="text"
              placeholder="123 Main St"
            />
          </div>
          <Button
            type="submit"
            class="w-full"
            :disabled="loading"
          >
            {{ loading ? 'Creating account...' : 'Sign Up' }}
          </Button>
        </form>
      </CardContent>
      <CardFooter>
        <p class="text-sm text-muted-foreground">
          Already have an account?
          <RouterLink
            to="/login"
            class="underline text-foreground"
          >
            Log in
          </RouterLink>
        </p>
      </CardFooter>
    </Card>
  </div>
</template>
