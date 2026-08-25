<template>
  <div class="wrap section">
    <div class="contact-grid">
      <div>
        <h1>{{ $t('cta') }}</h1>
        <p>{{ $t('inquiryLead') }}</p>
        <ul>
          <li>Voltage: 110V / 220V / 380V</li>
          <li>Hose count and fuel types</li>
          <li>Destination port and OEM panel</li>
        </ul>
      </div>
      <InquiryForm />
    </div>
  </div>
</template>
<script setup lang="ts">
usePageSeo({ title: 'Get a Quote | FuelTech', description: 'Send an RFQ for fuel dispensers and accessories.', path: '/inquiry' })
</script>
