<template>
  <div class="th-login">
    <div class="th-login-card">
      <h1>贸站通 TradeHub</h1>
      <p>统一管理所有外贸独立站 · 演示账号 admin / admin123</p>
      <el-form @submit.prevent="submit">
        <el-form-item><el-input v-model="username" placeholder="用户名" /></el-form-item>
        <el-form-item><el-input v-model="password" type="password" placeholder="密码" show-password /></el-form-item>
        <el-button type="primary" style="width: 100%; background: #0b1f3a; border: 0" :loading="loading" native-type="submit">登录</el-button>
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
    ElMessage.error(e.message || '登录失败')
  } finally {
    loading.value = false
  }
}
</script>
