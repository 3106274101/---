import axios from 'axios'

const http = axios.create({
  baseURL: '/api'
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('th_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  const tenantId = localStorage.getItem('th_tenant')
  if (tenantId) {
    config.headers['X-Tenant-Id'] = tenantId
  }
  const siteId = localStorage.getItem('th_site')
  if (siteId) {
    config.headers['X-Site-Id'] = siteId
  }
  config.headers['X-Locale'] = localStorage.getItem('th_locale') || 'en'
  return config
})

http.interceptors.response.use(
  (res) => res.data,
  (err) => {
    if (err.response?.status === 401 && !location.hash.includes('/login')) {
      localStorage.removeItem('th_token')
      location.href = '/login'
    }
    return Promise.reject(err.response?.data || err)
  }
)

export default http
