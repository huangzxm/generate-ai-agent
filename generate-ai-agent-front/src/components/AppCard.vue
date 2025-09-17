<template>
  <div class="app-card" :class="{ 'app-card--featured': featured }">
    <div class="app-preview">
      <img v-if="app.cover" :src="app.cover" :alt="app.appName" />
      <div v-else class="app-placeholder">
        <span>🤖</span>
      </div>
      <div class="app-overlay">
        <a-space>
          <a-button type="primary" @click="handleViewChat">查看对话</a-button>
          <a-button v-if="app.deployKey" type="default" @click="handleViewWork">查看作品</a-button>
        </a-space>
      </div>
    </div>
    <div class="app-info">
      <div class="app-info-left">
        <a-avatar :src="app.user?.userAvatar" :size="40">
          {{ app.user?.userName?.charAt(0) || 'U' }}
        </a-avatar>
      </div>
      <div class="app-info-right">
        <h3 class="app-title">{{ app.appName || '未命名应用' }}</h3>
        <p class="app-author">
          {{ app.user?.userName || (featured ? '官方' : '未知用户') }}
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
interface Props {
  app: API.AppVO
  featured?: boolean
}

interface Emits {
  (e: 'view-chat', appId: string | number | undefined): void
  (e: 'view-work', app: API.AppVO): void
}

const props = withDefaults(defineProps<Props>(), {
  featured: false,
})

const emit = defineEmits<Emits>()

const handleViewChat = () => {
  emit('view-chat', props.app.id)
}

const handleViewWork = () => {
  emit('view-work', props.app)
}
</script>

<style scoped>
.app-card {
  background: var(--geek-gradient-card);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: var(--geek-shadow-md);
  backdrop-filter: blur(20px);
  border: 1px solid var(--geek-border);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
}

.app-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--geek-gradient-primary);
  opacity: 0;
  transition: opacity 0.3s ease;
  pointer-events: none;
  z-index: 0;
}

.app-card:hover::before {
  opacity: 0.05;
}

.app-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: var(--geek-shadow-lg);
  border-color: var(--geek-primary);
}

.app-card--featured {
  border: 2px solid var(--geek-primary);
  box-shadow: var(--geek-shadow-glow);
}

.app-card--featured::after {
  content: '⭐ FEATURED';
  position: absolute;
  top: 12px;
  right: 12px;
  background: var(--geek-gradient-primary);
  color: var(--geek-bg-primary);
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 600;
  font-family: 'JetBrains Mono', monospace;
  z-index: 3;
}

.app-preview {
  height: 200px;
  background: var(--geek-bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  position: relative;
  z-index: 1;
}

.app-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.9) contrast(1.1);
}

.app-placeholder {
  font-size: 48px;
  color: var(--geek-text-muted);
  filter: grayscale(1);
  opacity: 0.6;
}

.app-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(10, 14, 26, 0.8);
  backdrop-filter: blur(5px);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s ease;
  z-index: 2;
}

.app-card:hover .app-overlay {
  opacity: 1;
}

.app-overlay .ant-btn {
  background: var(--geek-gradient-primary);
  border: none;
  color: var(--geek-bg-primary);
  font-family: 'JetBrains Mono', monospace;
  font-size: 12px;
  font-weight: 600;
  border-radius: 6px;
  box-shadow: var(--geek-shadow-md);
  transition: all 0.3s ease;
}

.app-overlay .ant-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--geek-shadow-lg);
}

.app-overlay .ant-btn[type="default"] {
  background: var(--geek-bg-glass);
  color: var(--geek-text-primary);
  border: 1px solid var(--geek-border);
}

.app-info {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
  z-index: 1;
}

.app-info-left {
  flex-shrink: 0;
}

.app-info-left .ant-avatar {
  border: 2px solid var(--geek-border);
  box-shadow: 0 0 8px rgba(0, 212, 255, 0.2);
  background: var(--geek-bg-secondary);
  color: var(--geek-text-primary);
  font-family: 'JetBrains Mono', monospace;
  font-weight: 600;
}

.app-info-right {
  flex: 1;
  min-width: 0;
}

.app-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0 0 6px;
  color: var(--geek-text-primary);
  font-family: 'JetBrains Mono', monospace;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  text-shadow: 0 0 8px rgba(0, 212, 255, 0.1);
}

.app-author {
  font-size: 13px;
  color: var(--geek-text-muted);
  margin: 0;
  font-family: 'JetBrains Mono', monospace;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  opacity: 0.8;
}

.app-author::before {
  content: '// ';
  color: var(--geek-primary);
  opacity: 0.6;
}
</style>
