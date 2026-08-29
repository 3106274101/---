<template>
  <div class="th-login">
    <div class="th-login-card">
      <div class="th-login-brand">
        <svg class="logo-mark" viewBox="0 0 32 32" fill="none" width="28" height="28">
          <path d="M16 3 28 10v12L16 29 4 22V10L16 3Z" stroke="#0a2540" stroke-width="1.7" />
          <path d="M16 3v26M4 10l12 7 12-7" stroke="#0d9488" stroke-width="1.7" />
        </svg>
        TRADEHUB
      </div>
      <h1>贸站通工作台</h1>
      <p>演示：超管 admin / admin123，老板 fueltech / fueltech123（新库还有 editor / sales）</p>
      <el-form @submit.prevent="submit">
        <el-form-item><el-input v-model="username" placeholder="用户名" size="large" /></el-form-item>
        <el-form-item><el-input v-model="password" type="password" placeholder="密码" show-password size="large" /></el-form-item>
        <el-button type="primary" size="large" style="width: 100%" :loading="loading" native-type="submit">登录</el-button>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'

const username = ref('admin')
const password = ref('admin123')
const loading = ref(false)
const router = useRouter()
const auth = useAuthStore()

async function submit() {
  loading.value = true
  try {
    await auth.login(username.value, password.value)
    router.push('/')
  } catch (e: any) {
    const msg = e.message || '登录失败'
    if (e.code === 423 || /locked/i.test(msg)) {
      ElMessage.error('账号已锁定 15 分钟，请稍后再试')
    } else {
      ElMessage.error(msg)
    }
  } finally {
    loading.value = false
  }
}
</script>
