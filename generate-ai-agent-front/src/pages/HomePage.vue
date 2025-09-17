<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { addApp, listMyAppVoByPage, listGoodAppVoByPage } from '@/api/appController'
import { getDeployUrl } from '@/config/env'
import AppCard from '@/components/AppCard.vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()

// 用户提示词
const userPrompt = ref('')
const creating = ref(false)

// 我的应用数据
const myApps = ref<API.AppVO[]>([])
const myAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

// 精选应用数据
const featuredApps = ref<API.AppVO[]>([])
const featuredAppsPage = reactive({
  current: 1,
  pageSize: 6,
  total: 0,
})

// 设置提示词
const setPrompt = (prompt: string) => {
  userPrompt.value = prompt
}

// 优化提示词功能已移除

// 创建应用
const createApp = async () => {
  if (!userPrompt.value.trim()) {
    message.warning('请输入应用描述')
    return
  }

  if (!loginUserStore.loginUser.id) {
    message.warning('请先登录')
    await router.push('/user/login')
    return
  }

  creating.value = true
  try {
    const res = await addApp({
      initPrompt: userPrompt.value.trim(),
    })

    if (res.data.code === 0 && res.data.data) {
      message.success('应用创建成功')
      // 跳转到对话页面，确保ID是字符串类型
      const appId = String(res.data.data)
      await router.push(`/app/chat/${appId}`)
    } else {
      message.error('创建失败：' + res.data.message)
    }
  } catch (error) {
    console.error('创建应用失败：', error)
    message.error('创建失败，请重试')
  } finally {
    creating.value = false
  }
}

// 加载我的应用
const loadMyApps = async () => {
  if (!loginUserStore.loginUser.id) {
    return
  }

  try {
    const res = await listMyAppVoByPage({
      pageNum: myAppsPage.current,
      pageSize: myAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })

    if (res.data.code === 0 && res.data.data) {
      myApps.value = res.data.data.records || []
      myAppsPage.total = res.data.data.totalRow || 0
    }
  } catch (error) {
    console.error('加载我的应用失败：', error)
  }
}

// 加载精选应用
const loadFeaturedApps = async () => {
  try {
    const res = await listGoodAppVoByPage({
      pageNum: featuredAppsPage.current,
      pageSize: featuredAppsPage.pageSize,
      sortField: 'createTime',
      sortOrder: 'desc',
    })

    if (res.data.code === 0 && res.data.data) {
      featuredApps.value = res.data.data.records || []
      featuredAppsPage.total = res.data.data.totalRow || 0
    }
  } catch (error) {
    console.error('加载精选应用失败：', error)
  }
}

// 查看对话
const viewChat = (appId: string | number | undefined) => {
  if (appId) {
    router.push(`/app/chat/${appId}?view=1`)
  }
}

// 查看作品
const viewWork = (app: API.AppVO) => {
  if (app.deployKey) {
    const url = getDeployUrl(app.deployKey)
    window.open(url, '_blank')
  }
}

// 格式化时间函数已移除，不再需要显示创建时间

// 页面加载时获取数据
onMounted(() => {
  loadMyApps()
  loadFeaturedApps()

  // 鼠标跟随光效
  const handleMouseMove = (e: MouseEvent) => {
    const { clientX, clientY } = e
    const { innerWidth, innerHeight } = window
    const x = (clientX / innerWidth) * 100
    const y = (clientY / innerHeight) * 100

    document.documentElement.style.setProperty('--mouse-x', `${x}%`)
    document.documentElement.style.setProperty('--mouse-y', `${y}%`)
  }

  document.addEventListener('mousemove', handleMouseMove)

  // 清理事件监听器
  return () => {
    document.removeEventListener('mousemove', handleMouseMove)
  }
})
</script>

<template>
  <div id="homePage">
    <div class="container">
      <!-- 网站标题和描述 -->
      <div class="hero-section">
        <div class="hero-badge">
          <span class="badge-text">// 极客专属</span>
        </div>
        <h1 class="hero-title">
          <span class="title-line">GeekAI</span>
          <span class="title-line">应用生成平台</span>
        </h1>
        <p class="hero-description">
          <span class="code-bracket">{</span>
          一行代码，无限可能
          <span class="code-bracket">}</span>
        </p>
        <div class="hero-stats">
          <div class="stat-item">
            <span class="stat-number">10K+</span>
            <span class="stat-label">应用生成</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-number">99.9%</span>
            <span class="stat-label">成功率</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-number">< 30s</span>
            <span class="stat-label">生成时间</span>
          </div>
        </div>
      </div>

      <!-- 用户提示词输入框 -->
      <div class="input-section">
        <a-textarea
          v-model:value="userPrompt"
          placeholder="帮我创建个人博客网站"
          :rows="4"
          :maxlength="1000"
          class="prompt-input"
        />
        <div class="input-actions">
          <a-button type="primary" size="large" @click="createApp" :loading="creating">
            <template #icon>
              <span>↑</span>
            </template>
          </a-button>
        </div>
      </div>

      <!-- 快捷按钮 -->
      <div class="quick-actions">
        <a-button
          type="default"
          @click="
            setPrompt(
              '创建一个现代化的个人博客网站，包含文章列表、详情页、分类标签、搜索功能、评论系统和个人简介页面。采用简洁的设计风格，支持响应式布局，文章支持Markdown格式，首页展示最新文章和热门推荐。',
            )
          "
          >个人博客网站</a-button
        >
        <a-button
          type="default"
          @click="
            setPrompt(
              '设计一个专业的企业官网，包含公司介绍、产品服务展示、新闻资讯、联系我们等页面。采用商务风格的设计，包含轮播图、产品展示卡片、团队介绍、客户案例展示，支持多语言切换和在线客服功能。',
            )
          "
          >企业官网</a-button
        >
        <a-button
          type="default"
          @click="
            setPrompt(
              '构建一个功能完整的在线商城，包含商品展示、购物车、用户注册登录、订单管理、支付结算等功能。设计现代化的商品卡片布局，支持商品搜索筛选、用户评价、优惠券系统和会员积分功能。',
            )
          "
          >在线商城</a-button
        >
        <a-button
          type="default"
          @click="
            setPrompt(
              '制作一个精美的作品展示网站，适合设计师、摄影师、艺术家等创作者。包含作品画廊、项目详情页、个人简历、联系方式等模块。采用瀑布流或网格布局展示作品，支持图片放大预览和作品分类筛选。',
            )
          "
          >作品展示网站</a-button
        >
      </div>

      <!-- 我的作品 -->
      <div class="section">
        <h2 class="section-title">
          <span class="section-icon">📁</span>
          <span class="section-text">./my-projects</span>
          <span class="section-cursor">_</span>
        </h2>
        <div class="app-grid">
          <AppCard
            v-for="app in myApps"
            :key="app.id"
            :app="app"
            @view-chat="viewChat"
            @view-work="viewWork"
          />
        </div>
        <div class="pagination-wrapper">
          <a-pagination
            v-model:current="myAppsPage.current"
            v-model:page-size="myAppsPage.pageSize"
            :total="myAppsPage.total"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个应用`"
            @change="loadMyApps"
          />
        </div>
      </div>

      <!-- 精选案例 -->
      <div class="section">
        <h2 class="section-title">
          <span class="section-icon">⭐</span>
          <span class="section-text">./featured-demos</span>
          <span class="section-cursor">_</span>
        </h2>
        <div class="featured-grid">
          <AppCard
            v-for="app in featuredApps"
            :key="app.id"
            :app="app"
            :featured="true"
            @view-chat="viewChat"
            @view-work="viewWork"
          />
        </div>
        <div class="pagination-wrapper">
          <a-pagination
            v-model:current="featuredAppsPage.current"
            v-model:page-size="featuredAppsPage.pageSize"
            :total="featuredAppsPage.total"
            :show-size-changer="false"
            :show-total="(total: number) => `共 ${total} 个案例`"
            @change="loadFeaturedApps"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
#homePage {
  width: 100%;
  margin: 0;
  padding: 0;
  min-height: 100vh;
  background: var(--geek-gradient-bg);
  position: relative;
  overflow: hidden;
}

/* 极客风格矩阵背景 */
#homePage::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image:
    linear-gradient(rgba(0, 212, 255, 0.1) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 212, 255, 0.1) 1px, transparent 1px),
    linear-gradient(rgba(91, 115, 255, 0.05) 1px, transparent 1px),
    linear-gradient(90deg, rgba(91, 115, 255, 0.05) 1px, transparent 1px);
  background-size:
    80px 80px,
    80px 80px,
    16px 16px,
    16px 16px;
  pointer-events: none;
  animation: matrixFlow 25s linear infinite;
}

/* 极客风格动态光效 */
#homePage::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(
      800px circle at var(--mouse-x, 50%) var(--mouse-y, 50%),
      rgba(0, 212, 255, 0.15) 0%,
      rgba(91, 115, 255, 0.1) 30%,
      rgba(156, 39, 176, 0.08) 60%,
      transparent 80%
    ),
    conic-gradient(from 0deg at 50% 50%, transparent 0deg, rgba(0, 212, 255, 0.05) 90deg, transparent 180deg, rgba(91, 115, 255, 0.05) 270deg, transparent 360deg);
  pointer-events: none;
  animation: geekGlow 12s ease-in-out infinite alternate;
}

@keyframes matrixFlow {
  0% {
    transform: translate(0, 0);
  }
  100% {
    transform: translate(80px, 80px);
  }
}

@keyframes geekGlow {
  0% {
    opacity: 0.4;
    transform: rotate(0deg) scale(1);
  }
  50% {
    opacity: 0.8;
    transform: rotate(180deg) scale(1.05);
  }
  100% {
    opacity: 0.6;
    transform: rotate(360deg) scale(1);
  }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
  z-index: 2;
  width: 100%;
  box-sizing: border-box;
}

/* 移除居中光束效果 */

/* 极客风格英雄区域 */
.hero-section {
  text-align: center;
  padding: 100px 0 80px;
  margin-bottom: 40px;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
    radial-gradient(ellipse 1000px 500px at center, rgba(0, 212, 255, 0.2) 0%, transparent 70%),
    conic-gradient(from 0deg at 50% 50%, transparent 0deg, rgba(91, 115, 255, 0.1) 120deg, transparent 240deg);
  animation: heroMatrix 15s ease-in-out infinite alternate;
}

@keyframes heroMatrix {
  0% {
    opacity: 0.7;
    transform: scale(1) rotate(0deg);
  }
  100% {
    opacity: 1;
    transform: scale(1.05) rotate(5deg);
  }
}

.hero-badge {
  display: inline-block;
  margin-bottom: 24px;
  padding: 8px 20px;
  background: var(--geek-bg-glass);
  border: 1px solid var(--geek-border);
  border-radius: 20px;
  backdrop-filter: blur(10px);
  position: relative;
  z-index: 3;
}

.badge-text {
  font-family: 'JetBrains Mono', monospace;
  font-size: 14px;
  color: var(--geek-primary);
  font-weight: 500;
}

.hero-title {
  font-size: 64px;
  font-weight: 800;
  margin: 0 0 32px;
  line-height: 1.1;
  font-family: 'JetBrains Mono', monospace;
  letter-spacing: -2px;
  position: relative;
  z-index: 3;
}

.title-line {
  display: block;
  background: var(--geek-gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  animation: titleGlow 4s ease-in-out infinite alternate;
  text-shadow: 0 0 30px rgba(0, 212, 255, 0.5);
}

@keyframes titleGlow {
  0% {
    filter: brightness(1) saturate(1);
  }
  100% {
    filter: brightness(1.2) saturate(1.3);
  }
}

.hero-description {
  font-size: 24px;
  margin: 0 0 48px;
  color: var(--geek-text-secondary);
  font-family: 'JetBrains Mono', monospace;
  font-weight: 400;
  position: relative;
  z-index: 3;
}

.code-bracket {
  color: var(--geek-primary);
  font-weight: 600;
  margin: 0 8px;
  animation: bracketPulse 2s ease-in-out infinite;
}

@keyframes bracketPulse {
  0%, 100% {
    opacity: 0.7;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.1);
  }
}

.hero-stats {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 32px;
  margin-top: 48px;
  position: relative;
  z-index: 3;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 32px;
  font-weight: 700;
  color: var(--geek-primary);
  font-family: 'JetBrains Mono', monospace;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.3);
}

.stat-label {
  display: block;
  font-size: 14px;
  color: var(--geek-text-muted);
  margin-top: 4px;
  font-family: 'JetBrains Mono', monospace;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: var(--geek-border);
  opacity: 0.5;
}

/* 极客风格输入区域 */
.input-section {
  position: relative;
  margin: 0 auto 32px;
  max-width: 900px;
}

.prompt-input {
  border-radius: 12px;
  border: 1px solid var(--geek-border);
  font-size: 16px;
  padding: 24px 80px 24px 24px;
  background: var(--geek-bg-glass);
  backdrop-filter: blur(20px);
  box-shadow: var(--geek-shadow-md);
  color: var(--geek-text-primary);
  font-family: 'JetBrains Mono', monospace;
  transition: all 0.3s ease;
  resize: none;
}

.prompt-input::placeholder {
  color: var(--geek-text-muted);
  font-style: italic;
}

.prompt-input:focus {
  border-color: var(--geek-primary);
  box-shadow: var(--geek-shadow-lg), 0 0 0 3px rgba(0, 212, 255, 0.1);
  transform: translateY(-2px);
  background: rgba(26, 31, 46, 0.9);
}

.input-actions {
  position: absolute;
  bottom: 16px;
  right: 16px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.input-actions .ant-btn {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: bold;
  background: var(--geek-gradient-primary);
  border: none;
  color: var(--geek-bg-primary);
  box-shadow: var(--geek-shadow-glow);
  transition: all 0.3s ease;
}

.input-actions .ant-btn:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: var(--geek-shadow-lg), 0 0 20px rgba(0, 212, 255, 0.4);
}

/* 极客风格快捷按钮 */
.quick-actions {
  display: flex;
  gap: 16px;
  justify-content: center;
  margin-bottom: 80px;
  flex-wrap: wrap;
}

.quick-actions .ant-btn {
  border-radius: 8px;
  padding: 12px 24px;
  height: auto;
  background: var(--geek-bg-glass);
  border: 1px solid var(--geek-border);
  color: var(--geek-text-secondary);
  backdrop-filter: blur(15px);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  font-family: 'JetBrains Mono', monospace;
  font-size: 13px;
  font-weight: 500;
}

.quick-actions .ant-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: var(--geek-gradient-primary);
  opacity: 0.1;
  transition: left 0.4s ease;
}

.quick-actions .ant-btn:hover::before {
  left: 100%;
}

.quick-actions .ant-btn:hover {
  background: var(--geek-bg-card);
  border-color: var(--geek-primary);
  color: var(--geek-primary);
  transform: translateY(-3px);
  box-shadow: var(--geek-shadow-md);
  text-shadow: 0 0 8px rgba(0, 212, 255, 0.3);
}

/* 极客风格区域标题 */
.section {
  margin-bottom: 80px;
}

.section-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 40px;
  color: var(--geek-text-primary);
  font-family: 'JetBrains Mono', monospace;
  display: flex;
  align-items: center;
  gap: 12px;
  position: relative;
}

.section-title::before {
  content: '';
  width: 4px;
  height: 100%;
  background: var(--geek-gradient-primary);
  border-radius: 2px;
  box-shadow: 0 0 8px rgba(0, 212, 255, 0.3);
}

.section-icon {
  font-size: 24px;
  filter: grayscale(1);
  opacity: 0.7;
}

.section-text {
  color: var(--geek-primary);
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.3);
}

.section-cursor {
  color: var(--geek-primary);
  animation: cursorBlink 1.5s infinite;
  font-weight: 400;
}

@keyframes cursorBlink {
  0%, 50% {
    opacity: 1;
  }
  51%, 100% {
    opacity: 0;
  }
}

/* 极客风格网格布局 */
.app-grid,
.featured-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 32px;
  margin-bottom: 48px;
}

/* 极客风格分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 48px;
}

.pagination-wrapper .ant-pagination {
  font-family: 'JetBrains Mono', monospace;
}

.pagination-wrapper .ant-pagination .ant-pagination-item {
  background: var(--geek-bg-glass);
  border: 1px solid var(--geek-border);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

.pagination-wrapper .ant-pagination .ant-pagination-item:hover {
  border-color: var(--geek-primary);
  box-shadow: 0 0 8px rgba(0, 212, 255, 0.2);
}

.pagination-wrapper .ant-pagination .ant-pagination-item-active {
  background: var(--geek-gradient-primary);
  border-color: var(--geek-primary);
  box-shadow: var(--geek-shadow-glow);
}

.pagination-wrapper .ant-pagination .ant-pagination-item-active a {
  color: var(--geek-bg-primary);
  font-weight: 600;
}

/* 极客风格响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 40px;
  }

  .hero-description {
    font-size: 18px;
  }

  .hero-stats {
    flex-direction: column;
    gap: 24px;
  }

  .stat-divider {
    width: 40px;
    height: 1px;
  }

  .app-grid,
  .featured-grid {
    grid-template-columns: 1fr;
    gap: 24px;
  }

  .quick-actions {
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }

  .section-title {
    font-size: 24px;
  }

  .input-section {
    max-width: 100%;
  }

  .prompt-input {
    padding: 20px 70px 20px 20px;
  }
}
</style>
