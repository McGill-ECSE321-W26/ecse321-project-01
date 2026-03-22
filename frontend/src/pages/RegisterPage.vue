<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
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
        <form
          class="space-y-4"
          @submit.prevent
        >
          <div class="space-y-2">
            <Label for="email">Email</Label>
            <Input
              id="email"
              type="email"
              placeholder="you@example.com"
            />
          </div>
          <div class="space-y-2">
            <Label for="password">Password</Label>
            <Input
              id="password"
              type="password"
              placeholder="Min. 8 characters"
            />
          </div>
          <div class="space-y-2">
            <Label for="address">Address</Label>
            <Input
              id="address"
              type="text"
              placeholder="123 Main St"
            />
          </div>
          <Button
            type="submit"
            class="w-full"
          >
            Sign Up
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
