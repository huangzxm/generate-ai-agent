<template>
  <a-layout-footer class="footer">
    <div class="footer-content">
      <p class="copyright">
        <a
          href="https://github.com/huangzxm"
          target="_blank"
          rel="noopener noreferrer"
          class="author-link"
        >
          by huangxxinming
        </a>
      </p>
    </div>
  </a-layout-footer>
</template>

<script setup lang="ts">
// 无需额外的响应式数据
</script>

<style scoped>
.footer {
  background: var(--geek-bg-card);
  backdrop-filter: blur(20px);
  text-align: center;
  padding: 32px;
  margin-top: 60px;
  border-top: 1px solid var(--geek-border);
  position: relative;
  overflow: hidden;
}

.footer::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: var(--geek-gradient-primary);
  opacity: 0.6;
}

.footer-content {
  position: relative;
  z-index: 2;
}

.copyright {
  margin: 0;
  color: var(--geek-text-muted);
  font-size: 14px;
  font-family: 'JetBrains Mono', monospace;
}

.author-link {
  color: var(--geek-primary);
  font-weight: 600;
  transition: all 0.3s ease;
  text-decoration: none;
  position: relative;
}

.author-link::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: var(--geek-gradient-primary);
  transition: width 0.3s ease;
}

.author-link:hover {
  color: var(--geek-secondary);
  text-shadow: 0 0 8px var(--geek-primary);
}

.author-link:hover::after {
  width: 100%;
}
</style>
