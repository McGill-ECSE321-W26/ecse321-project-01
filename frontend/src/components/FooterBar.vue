<script setup lang="ts">
import { RouterLink, useRoute } from 'vue-router'

const route = useRoute()

const props = withDefaults(defineProps<{
  variant?: 'light' | 'dark'
}>(), {
  variant: 'light'
})
</script>

<template>
  <footer :class="['footer-bar', props.variant === 'dark' ? 'footer-dark' : 'footer-light']">
    <RouterLink
      v-if="route.path !== '/'"
      to="/"
      class="footer-logo"
    >
      Kloth
    </RouterLink>
    <div class="footer-links">
      <span>© 2026 Kloth. Clothing for humans.</span>
      <a href="https://github.com/McGill-ECSE321-W26/ecse321-project-01?tab=readme-ov-file">About</a><span>|</span>
      <a href="https://github.com/McGill-ECSE321-W26/ecse321-project-01?tab=readme-ov-file">Terms of Service</a><span>|</span>
      <a href="https://github.com/McGill-ECSE321-W26/ecse321-project-01?tab=readme-ov-file">Privacy Policy</a>
    </div>
  </footer>
</template>

<style scoped>
.footer-bar {
  padding: 10px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  z-index: 30;
}

.footer-logo {
  font-family: 'Lexend Deca', sans-serif;
  font-size: 40px;
  font-weight: 700;
  text-decoration: none;
  letter-spacing: 2px;
}

.footer-links {
  display: flex;
  gap: 20px;
  font-size: 15px;
}

/* dark variant */
.footer-dark {
  position: absolute;
  bottom: 0; left: 0; right: 0;
  color: rgba(255, 255, 255, 0.4);
}
.footer-dark .footer-logo { color: #fff; }
.footer-dark .footer-logo:hover { color: rgba(255, 255, 255, 0.7); }
.footer-dark .footer-links,
.footer-dark .footer-links a { color: rgba(255, 255, 255, 0.4); }
.footer-dark .footer-links a:hover { color: #fff; }

/* light variant */
.footer-light {
  margin-top: auto;
  border-top: 1px solid var(--text-light);
}
.footer-light .footer-logo { color: var(--text); }
.footer-light .footer-logo:hover { color: var(--text-muted); }
.footer-light .footer-links,
.footer-light .footer-links a { color: var(--text-light); }
.footer-light .footer-links a:hover { color: var(--text); }
</style>