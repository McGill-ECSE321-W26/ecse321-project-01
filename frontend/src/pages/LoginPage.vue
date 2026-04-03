<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink, useRouter, useRoute } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const email = ref('')
const password = ref('')
const role = ref('')
const error = ref('')
const loading = ref(false)

// Check query parameters from Register -> Login flow. Used for conditional rendering
const registered = route.query.registered === 'true'

async function handleSubmit() {
  error.value = ''

  if (!email.value || !password.value || !role.value) {
    error.value = 'Please fill in all fields.'
    return
  }

  loading.value = true
  try {
    await auth.login(email.value, password.value, role.value)
    await router.push('/shop')
  }
  catch (e) {
    error.value = e instanceof Error ? e.message : 'Something went wrong. Please try again.'
  }
  finally {
    loading.value = false // Guarantee loading = false so button is clickable
  }
}
</script>

<template>
  <div class="max-w-md mx-auto py-12 px-4">
    <Card>
      <CardHeader>
        <CardTitle class="text-3xl">
          Log In
        </CardTitle>
      </CardHeader>
      <CardContent>
        <div
          v-if="registered"
          class="mb-4 rounded-md bg-green-50 border border-green-200 p-3 text-sm text-green-800"
        >
          Account created successfully. Please log in.
        </div>
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
              placeholder="hello@example.com"
            />
          </div>
          <div class="space-y-2">
            <Label for="password">Password</Label>
            <Input
              id="password"
              v-model="password"
              type="password"
              placeholder="••••••••"
            />
          </div>
          <div class="space-y-2">
            <Label>Role</Label>
            <Select v-model="role">
              <SelectTrigger>
                <SelectValue placeholder="Select a role" />
              </SelectTrigger>
              <SelectContent>
                <SelectItem value="Customer">
                  Customer
                </SelectItem>
                <SelectItem value="Employee">
                  Employee
                </SelectItem>
                <SelectItem value="Manager">
                  Manager
                </SelectItem>
              </SelectContent>
            </Select>
          </div>
          <Button
            type="submit"
            class="w-full"
            :disabled="loading"
          >
            {{ loading ? 'Logging in...' : 'Log In' }}
          </Button>
        </form>
      </CardContent>
      <CardFooter>
        <p class="text-sm text-muted-foreground">
          Don't have an account?
          <RouterLink
            to="/register"
            class="underline text-foreground"
          >
            Sign up
          </RouterLink>
        </p>
      </CardFooter>
    </Card>
  </div>
</template>
