<template>
  <div class="wrap section">
    <div class="contact-grid">
      <div class="info-card">
        <h1>{{ $t('nav.contact') }}</h1>
        <p>{{ brand.tagline }}</p>
        <p><b>{{ brand.address }}</b></p>
        <p>{{ brand.email }}<br>{{ brand.phone }}</p>
        <p>WhatsApp {{ brand.whatsapp }}</p>
        <p class="muted">{{ $t('replyHint') }}</p>
      </div>
      <div>
        <h2>{{ $t('cta') }}</h2>
        <InquiryForm />
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
const { get } = useStoreApi()
const { data } = await useAsyncData('ctx-contact', () => get('/context'))
const brand = computed(() => data.value?.brand || {})
usePageSeo({ title: 'Contact | ZhengHe Machinery', description: 'Cathy@machineryzh.com · +86 18567535165 · Xinxiang, Henan.', path: '/contact' })
</script>
