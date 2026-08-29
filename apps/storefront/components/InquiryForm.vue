<template>
  <form class="form" @submit.prevent="submit">
    <input class="hp" v-model="form.website" tabindex="-1" autocomplete="off" />
    <p v-if="form.productName" class="muted">{{ form.productName }}</p>
    <input v-model="form.name" required :placeholder="$t('form.name')" />
    <input v-model="form.company" :placeholder="$t('form.company')" />
    <input v-model="form.email" type="email" required :placeholder="$t('form.email')" />
    <input v-model="form.phone" :placeholder="$t('form.phone')" />
    <input v-model="form.whatsapp" placeholder="WhatsApp" />
    <input v-model="form.country" :placeholder="$t('form.country')" />
    <input v-model="form.quantity" :placeholder="$t('form.quantity')" />
    <textarea v-model="form.message" rows="5" :placeholder="$t('form.message')" />
    <label><input type="checkbox" v-model="agree" required /> {{ $t('form.agree') }}</label>
    <button class="btn" type="submit" :disabled="loading">{{ $t('form.submit') }}</button>
    <p v-if="done">{{ $t('form.ok') }}</p>
  </form>
</template>

<script setup lang="ts">
const props = defineProps<{ productId?: number; productName?: string }>()
const route = useRoute()
const { post } = useStoreApi()
const loading = ref(false)
const done = ref(false)
const agree = ref(false)
const qName = String(route.query.product || props.productName || '')
const qId = Number(route.query.productId || props.productId || 0) || props.productId
const form = reactive({
  name: '',
  company: '',
  email: '',
  phone: '',
  country: '',
  whatsapp: '',
  quantity: '',
  message: qName ? `I am interested in ${qName}. Please quote voltage, hose count and destination port.` : '',
  website: '',
  productId: qId,
  productName: qName,
  utm: {
    source: String(route.query.utm_source || ''),
    medium: String(route.query.utm_medium || ''),
    campaign: String(route.query.utm_campaign || ''),
    content: String(route.query.utm_content || ''),
    gclid: String(route.query.gclid || '')
  }
})

async function submit() {
  if (form.website) return
  loading.value = true
  try {
    await post('/inquiries', form)
    done.value = true
    try {
      const gtag = (window as any).gtag
      if (typeof gtag === 'function') {
        gtag('event', 'generate_lead', { product: form.productName || '', country: form.country })
      }
    } catch { /* no analytics */ }
  } finally {
    loading.value = false
  }
}
</script>
