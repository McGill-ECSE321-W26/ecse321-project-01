<script setup lang="ts">
import { RouterLink } from 'vue-router'
</script>

<template>
  <footer class="footer-bar">
    <RouterLink to="/" class="footer-logo">Kloth</RouterLink>
    <div class="footer-links">
      <span>© 2026 Kloth. Clothing for humans.</span>
      <a href="#">About</a><span>|</span>
      <a href="#">Terms of Service</a><span>|</span>
      <a href="#">Privacy Policy</a>
    </div>
  </footer>
</template>

<style scoped>
.footer-bar {
  margin-top: auto;
  padding: 12px 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  border-top: 1px solid var(--text-light);
  font-size: 10px;
  color: var(--text-muted);
}

.footer-logo {
  font-family: 'Lexend Deca', serif;
  font-size: 40px;
  font-weight: 700;
  color: var(--text);
  text-decoration: none;
  letter-spacing: 2px;
}

.footer-links {
  display: flex;
  gap: 20px;
  font-size: 15px;
  font-weight: 700;
  color: var(--text-light);
}

.footer-links a {
  color: var(--text-light);
  text-decoration: none;
}

.footer-links a:hover {
  color: var(--text);
}
</style>