<template>
  <div class="wrap section">
    <div class="contact-grid">
      <div class="info-card">
        <h1>{{ $t('nav.contact') }}</h1>
        <p>{{ brand.tagline }}</p>
        <p><b>{{ brand.address }}</b></p>
        <p>{{ brand.email }}<br>{{ brand.phone }}</p>
        <p v-if="brand.whatsapp">WhatsApp {{ brand.whatsapp }}</p>
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
const { brand, pageTitle } = await useSiteBrand()
usePageSeo({
  title: pageTitle('Contact'),
  description: [brand.value.email, brand.value.phone, brand.value.address].filter(Boolean).join(' · '),
  path: '/contact'
})
</script>
