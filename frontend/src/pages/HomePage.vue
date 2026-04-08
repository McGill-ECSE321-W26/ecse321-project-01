<script setup lang="ts">
import { RouterLink } from 'vue-router'
import { ref, onMounted, onUnmounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import FooterBar from "@/components/FooterBar.vue";


// used to check if already logged in, if so, just have a single enter store button
const auth = useAuthStore()

const photos = [
  'https://i.pinimg.com/736x/64/e2/f6/64e2f6d1c4a1e07dad9b428d216a2ab9.jpg',
  'https://i.pinimg.com/736x/14/da/3b/14da3b0277e23929001c69ea54837de3.jpg',
  'https://i.pinimg.com/1200x/1a/87/42/1a874294dc85384c0722256b25f1d804.jpg',
  'https://i.pinimg.com/736x/78/5b/bb/785bbb5839ce398a28fbe9428958ca51.jpg',
  'https://i.pinimg.com/1200x/45/f6/53/45f65396896cb82992057131e29cd3cd.jpg',
]

interface PhotoStyle {
  width: string
  height: string
  top: string
  left: string
  zIndex: number
}

const photoStyles = ref<PhotoStyle[]>([])

function generateCollage() {
  const W = window.innerWidth
  const H = window.innerHeight

  // unique positions for images
  // is Dynamic and scales with window size, not hardcoded, yay :)
  const positions = [
    { left: 0,        top: 0,          width: W / 3,      height: H * 0.65 },
    { left: W / 3,    top: -H * 0.05,  width: W / 3 + 5,  height: H * 0.62 },
    { left: 2 * W/3,  top: -H * 0.05,  width: W / 3,      height: H * 0.62 },
    { left: 0,        top: H * 0.55,   width: W * 0.63,   height: H * 0.48 },
    { left: W * 0.50, top: H * 0.55,   width: W * 0.63,   height: H * 0.50 },
  ]

  // zIndices to layer images (no longer needed since we are not doing an overlapping collage anymore)
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
  document.body.classList.add('home-page')
  generateCollage()
  window.addEventListener('resize', generateCollage)
  window.addEventListener('wheel', (e) => {
    if (e.ctrlKey) e.preventDefault()
  }, { passive: false })
})

onUnmounted(() => {
  document.body.classList.remove('home-page')
  window.removeEventListener('resize', generateCollage)
})
</script>

<template>
  <div class="kloth-home">
    <div class="collage">
      // used to add images
      // move individual images in their frames to be aligned well when scaled
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

    // tint to improve visibility of buttons and logo
    <div class="tint" />

    // overlay, i.e logo and buttons for login and sign up
    <div class="overlay">
      <h1 class="brand-name">
        Kloth
      </h1>
      <p class="brand-slogan">
        Clothing for humans.
      </p>
      <div class="btn-group">
        <template v-if="auth.isAuthenticated && auth.role === 'Customer'">
          <RouterLink
            to="/shop"
            class="btn btn-white"
          >
            View Catalog
          </RouterLink>
        </template>
        <template v-else-if="auth.isAuthenticated && auth.role === 'Manager'">
          <RouterLink
            to="/manager"
            class="btn btn-white"
          >
            Manager Dashboard
          </RouterLink>
        </template><template v-else-if="auth.isAuthenticated && auth.role === 'Employee'">
          <RouterLink
            to="/employee/orders"
            class="btn btn-white"
          >
            Go to Employee Dashboard
          </RouterLink>
        </template>
        <template v-else>
          <RouterLink
            to="/register"
            class="btn btn-white"
          >
            Create Account
          </RouterLink>
          <RouterLink
            to="/login"
            class="btn btn-ghost"
          >
            Log in
          </RouterLink>
        </template>
      </div>
    </div>

    // footer on the bottom with useful info
    <FooterBar variant="dark" />
  </div>
</template>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700&display=swap');

.kloth-home {
  position: relative;
  width: 100%;
  height: 100vh;
  background: var(--bg);
  overflow: hidden;
}

.collage { position: absolute; inset: 0; }

.photo {
  position: absolute;
  overflow: hidden;
}

.photo img {
  width: 100%;
  height: 100%;
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
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  pointer-events: none;
}

.btn-group { pointer-events: all; }

.brand-name {
  font-family: 'Lexend Deca', serif;
  font-size: 64px;
  font-weight: 500;
  color: var(--bg);
  margin: 0;
  line-height: .5;
}

.brand-slogan {
  font-family: 'Lexend Deca';
  font-size: 20px;
  font-weight: 500;
  color: var(--bg);
  margin: 0 0 12px;
}

.btn-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  align-items: center;
}

.btn {
  width: 176px;
  padding: 10px 5px;
  font-size: 11px;
  letter-spacing: 2.5px;
  text-transform: uppercase;
  text-align: center;
  text-decoration: none;
  cursor: pointer;
  display: block;
}

.btn-white { background: var(--bg); color: var(--text); }
.btn-ghost { background: rgba(255,255,255,0.1); color: #fff; border: 1px solid rgba(255,255,255,0.5); backdrop-filter: blur(6px); }
.btn:hover { opacity: 0.85; }

</style>