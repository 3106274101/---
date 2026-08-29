import { defineStore } from 'pinia'
import http from '../api/http'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('th_token') || '',
    user: JSON.parse(localStorage.getItem('th_user') || 'null') as any
  }),
  getters: {
    can: (state) => (perm: string) => {
      if (state.user?.superAdmin) return true
      return Array.isArray(state.user?.permissions) && state.user.permissions.includes(perm)
    }
  },
  actions: {
    async login(username: string, password: string) {
      const res: any = await http.post('/admin/auth/login', { username, password })
      this.token = res.data.token
      this.user = res.data.user
      localStorage.setItem('th_token', this.token)
      localStorage.setItem('th_user', JSON.stringify(this.user))
      if (this.user?.tenantId) {
        localStorage.setItem('th_tenant', String(this.user.tenantId))
      }
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem('th_token')
      localStorage.removeItem('th_user')
    }
  }
})
