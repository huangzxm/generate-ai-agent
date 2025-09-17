<template>
  <a-layout-header class="header">
    <a-row :wrap="false">
      <!-- 左侧：Logo和标题 -->
      <a-col flex="240px">
        <RouterLink to="/">
          <div class="header-left">
            <img class="logo" src="@/assets/geek-logo.svg" alt="GeekAI Logo" />
            <h1 class="site-title">
              <span class="title-geek">Geek</span><span class="title-ai">AI</span>
            </h1>
          </div>
        </RouterLink>
      </a-col>
      <!-- 中间：导航菜单 -->
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :items="menuItems"
          @click="handleMenuClick"
        />
      </a-col>
      <!-- 右侧：用户操作区域 -->
      <a-col>
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <a-space>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                {{ loginUserStore.loginUser.userName ?? '无名' }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, h, ref } from 'vue'
import { useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/loginUser.ts'
import { userLogout } from '@/api/userController.ts'
import { LogoutOutlined, HomeOutlined } from '@ant-design/icons-vue'

const loginUserStore = useLoginUserStore()
const router = useRouter()
// 当前选中菜单
const selectedKeys = ref<string[]>(['/'])
// 监听路由变化，更新当前选中菜单
router.afterEach((to, from, next) => {
  selectedKeys.value = [to.path]
})

// 菜单配置项
const originItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '主页',
    title: '主页',
  },
  {
    key: '/admin/userManage',
    label: '用户管理',
    title: '用户管理',
  },
  {
    key: '/admin/appManage',
    label: '应用管理',
    title: '应用管理',
  },
  {
    key: 'others',
    label: h('a', { href: 'https://github.com/huangzxm', target: '_blank' }, 'Github'),
    title: 'Github',
  },
]

// 过滤菜单项
const filterMenus = (menus = [] as MenuProps['items']) => {
  return menus?.filter((menu) => {
    const menuKey = menu?.key as string
    if (menuKey?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  })
}

// 展示在菜单的路由数组
const menuItems = computed<MenuProps['items']>(() => filterMenus(originItems))

// 处理菜单点击
const handleMenuClick: MenuProps['onClick'] = (e) => {
  const key = e.key as string
  selectedKeys.value = [key]
  // 跳转到对应页面
  if (key.startsWith('/')) {
    router.push(key)
  }
}

// 退出登录
const doLogout = async () => {
  const res = await userLogout()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
.header {
  background: var(--geek-bg-card);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid var(--geek-border);
  padding: 0 32px;
  box-shadow: var(--geek-shadow-sm);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  transition: all 0.3s ease;
}

.header-left:hover {
  transform: translateY(-1px);
}

.logo {
  height: 40px;
  width: 40px;
  filter: drop-shadow(0 0 8px rgba(0, 212, 255, 0.3));
  transition: all 0.3s ease;
}

.logo:hover {
  filter: drop-shadow(0 0 12px rgba(0, 212, 255, 0.5));
  transform: rotate(5deg);
}

.site-title {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  font-family: 'JetBrains Mono', monospace;
  letter-spacing: -0.5px;
}

.title-geek {
  background: var(--geek-gradient-primary);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.title-ai {
  color: var(--geek-text-primary);
  text-shadow: 0 0 10px var(--geek-primary);
}

.ant-menu-horizontal {
  border-bottom: none !important;
  background: transparent !important;
}

.user-login-status {
  color: var(--geek-text-primary);
}

.user-login-status .ant-avatar {
  border: 2px solid var(--geek-primary);
  box-shadow: 0 0 8px rgba(0, 212, 255, 0.3);
}

.user-login-status .ant-btn {
  background: var(--geek-gradient-primary);
  border: none;
  color: var(--geek-bg-primary);
  font-weight: 600;
  border-radius: 20px;
  padding: 4px 20px;
  height: auto;
  box-shadow: var(--geek-shadow-md);
  transition: all 0.3s ease;
}

.user-login-status .ant-btn:hover {
  transform: translateY(-2px);
  box-shadow: var(--geek-shadow-lg);
}
</style>
