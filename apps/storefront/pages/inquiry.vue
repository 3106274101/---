<template>
  <div class="wrap section">
    <div class="contact-grid">
      <div>
        <h1>{{ $t('cta') }}</h1>
        <p>{{ copy('inquiryLead', 'inquiryLead') }}</p>
        <ul>
          <li v-for="hint in inquiryHints" :key="hint">{{ hint }}</li>
        </ul>
      </div>
      <InquiryForm />
    </div>
  </div>
</template>
<script setup lang="ts">
const { copy, inquiryHints, pageTitle } = await useSiteBrand()
usePageSeo({ title: pageTitle('Get a Quote'), description: copy('inquiryLead', 'inquiryLead'), path: '/inquiry' })
</script>
