<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { RouterLink, useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const email = ref('')
const password = ref('')
const role = ref('')
const error = ref('')
const loading = ref(false)
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
  } catch (e) {
    error.value = e instanceof Error ? e.message : 'Something went wrong. Please try again.'
  } finally {
    loading.value = false
  }
}

interface PhotoStyle {
  width: string; height: string; top: string; left: string; zIndex: number
}
const photoStyles = ref<PhotoStyle[]>([])
const photos = [
  'https://i.pinimg.com/736x/64/e2/f6/64e2f6d1c4a1e07dad9b428d216a2ab9.jpg',
  'https://i.pinimg.com/736x/14/da/3b/14da3b0277e23929001c69ea54837de3.jpg',
  'https://i.pinimg.com/1200x/1a/87/42/1a874294dc85384c0722256b25f1d804.jpg',
  'https://i.pinimg.com/736x/78/5b/bb/785bbb5839ce398a28fbe9428958ca51.jpg',
  'https://i.pinimg.com/1200x/45/f6/53/45f65396896cb82992057131e29cd3cd.jpg',
]

function generateCollage() {
  const W = window.innerWidth
  const H = window.innerHeight

  const positions = [
    { left: 0,      top: 0,          width: W / 3,    height: H * 0.65 },
    { left: W / 3,  top: -H * 0.05,  width: W / 3 + 5, height: H * 0.62 },
    { left: 2*W/3,  top: -H * 0.05,  width: W / 3,    height: H * 0.62 },
    { left: 0,      top: H * 0.55,   width: W * 0.63,  height: H * 0.48 },
    { left: W * 0.50, top: H * 0.55, width: W * 0.63,  height: H * 0.50 },
  ]

  const zIndices = [3, 2, 2, 4, 10]

  photoStyles.value = positions.map((pos, i) => ({
    width: `${Math.round(pos.width)}px`,
    height: `${Math.round(pos.height)}px`,
    left: `${Math.round(pos.left)}px`,
    top: `${Math.round(pos.top)}px`,
    zIndex: zIndices[i],
  }))
}

onMounted(() => {
  generateCollage()
  window.addEventListener('resize', generateCollage)
  window.addEventListener('wheel', (e) => {
    if (e.ctrlKey) e.preventDefault()
  }, { passive: false })
})
onUnmounted(() => {
  window.removeEventListener('resize', generateCollage)
})
</script>

<template>
  <div class="kloth-page">
    <div class="collage">
      <div
        v-for="(style, i) in photoStyles"
        :key="i"
        class="photo"
        :style="style"
      >
        <img
          :src="photos[i]"
          alt=""
          :style="{ objectPosition: i === 3 || i === 4 ? 'center 30%' : 'center 10%' }"
        >
      </div>
    </div>

    <div class="tint" />

    <div class="overlay">
      <div class="form-box">
        <div class="form-header">
          <RouterLink
            to="/"
            class="back-link"
          >
            ← Kloth
          </RouterLink>
          <h1 class="form-title">
            Log In
          </h1>
        </div>

        <div
          v-if="registered"
          class="alert alert-success"
        >
          Account created successfully. Please log in.
        </div>
        <div
          v-if="error"
          class="alert alert-error"
        >
          {{ error }}
        </div>

        <form
          class="form-fields"
          @submit.prevent="handleSubmit"
        >
          <div class="field">
            <label>Email</label>
            <input
              v-model="email"
              type="email"
              placeholder="hello@example.com"
            >
          </div>
          <div class="field">
            <label>Password</label>
            <input
              v-model="password"
              type="password"
              placeholder="••••••••"
            >
          </div>
          <div class="field">
            <label>Role</label>
            <select v-model="role">
              <option
                value=""
                disabled
              >
                Select a role
              </option>
              <option value="Customer">
                Customer
              </option>
              <option value="Employee">
                Employee
              </option>
              <option value="Manager">
                Manager
              </option>
            </select>
          </div>
          <button
            type="submit"
            class="btn-submit"
            :disabled="loading"
          >
            {{ loading ? 'Logging in...' : 'Log In' }}
          </button>
        </form>

        <p class="form-footer">
          Don't have an account?
          <RouterLink to="/register">
            Sign up
          </RouterLink>
        </p>
      </div>
    </div>

    <footer class="footer-bar">
      <RouterLink
        to="/"
        class="footer-logo"
      >
        Kloth
      </RouterLink>
      <div class="footer-links">
        <span>© 2026 Kloth. Clothing for humans.</span>
        <a href="#">About</a><span>|</span>
        <a href="#">Terms of Service</a><span>|</span>
        <a href="#">Privacy Policy</a>
      </div>
    </footer>
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700&family=Lato:wght@300;400&display=swap');

.kloth-page {
  position: relative;
  width: 100%;
  height: 100vh;
  background: #f5f5f0;
  overflow: hidden;
  font-family: 'Lexend Deca', sans-serif;
}

.collage { position: absolute; inset: 0; }

.photo {
  position: absolute;
  overflow: hidden;
}

.photo img {
  width: 100%; height: 100%;
  object-fit: cover;
  display: block;
}

.tint {
  position: absolute;
  inset: 0;
  z-index: 10;
  background: rgba(0, 0, 0, 0.4);
  pointer-events: none;
}

.overlay {
  position: absolute;
  inset: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: none;
}

.form-box {
  pointer-events: all;
  background: #fff;
  border: 1px solid #e0e0e0;
  padding: 40px 48px;
  width: 100%;
  max-width: 400px;
  display: flex;
  flex-direction: column;
  gap: 20px;
  box-shadow: 0 8px 40px rgba(0,0,0,0.08);
}

.form-header { display: flex; flex-direction: column; gap: 6px; }

.back-link {
  font-size: 11px;
  letter-spacing: 2px;
  text-transform: uppercase;
  color: #999;
  text-decoration: none;
}
.back-link:hover { color: #111; }

.form-title {
  font-family: 'Lexend Deca', serif;
  font-size: 36px;
  font-weight: 700;
  color: #111;
  margin: 0;
}

.alert { font-size: 12px; padding: 10px 14px; }
.alert-success { background: #f0fdf4; border: 1px solid #bbf7d0; color: #166534; }
.alert-error   { background: #fef2f2; border: 1px solid #fecaca; color: #991b1b; }

.form-fields { display: flex; flex-direction: column; gap: 16px; }
.field { display: flex; flex-direction: column; gap: 6px; }

.field label {
  font-size: 11px;
  letter-spacing: 1.5px;
  text-transform: uppercase;
  color: #888;
}

.field input,
.field select {
  background: #fafafa;
  border: 1px solid #ddd;
  color: #111;
  padding: 10px 14px;
  font-size: 13px;
  font-family: 'Lexend Deca', sans-serif;
  outline: none;
  width: 100%;
  box-sizing: border-box;
}

.field input::placeholder { color: #bbb; }
.field input:focus,
.field select:focus { border-color: #111; }

.btn-submit {
  background: #111;
  color: #fff;
  border: none;
  padding: 12px;
  font-family: 'Lexend Deca', sans-serif;
  font-size: 11px;
  letter-spacing: 2.5px;
  text-transform: uppercase;
  cursor: pointer;
  width: 100%;
  margin-top: 4px;
}
.btn-submit:hover { background: #333; }
.btn-submit:disabled { opacity: 0.5; cursor: not-allowed; }

.form-footer {
  font-size: 12px;
  color: #999;
  text-align: center;
  margin: 0;
}
.form-footer a { color: #111; text-decoration: underline; }

.footer-bar {
  position: absolute;
  bottom: 0; left: 0; right: 0;
  z-index: 30;
  padding: 10px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  font-size: 15px;
  color: rgba(255, 255, 255, 0.4);
}

.footer-logo {
  font-family: 'Lexend Deca', serif;
  font-size: 40px;
  font-weight: 700;
  color: #fff;
  text-decoration: none;
  letter-spacing: 2px;
}

.footer-logo:hover { color: #fff; }

.footer-links {
  display: flex;
  gap: 20px;
  font-size: 15px;
  color: rgba(255,255,255,0.4);
}

.footer-links a { color: rgba(255,255,255,0.4); text-decoration: none; }
.footer-links a:hover { color: #fff; }
</style>