<template>
  <form class="form" @submit.prevent="submit">
    <input class="hp" v-model="form.website" tabindex="-1" autocomplete="off" />
    <input v-model="form.name" required :placeholder="$t('form.name')" />
    <input v-model="form.company" :placeholder="$t('form.company')" />
    <input v-model="form.email" type="email" required :placeholder="$t('form.email')" />
    <input v-model="form.country" :placeholder="$t('form.country')" />
    <input v-model="form.quantity" :placeholder="$t('form.quantity')" />
    <textarea v-model="form.message" rows="5" :placeholder="$t('form.message')" />
    <label><input type="checkbox" v-model="agree" required /> I agree to the processing of this inquiry data.</label>
    <button class="btn" type="submit" :disabled="loading">{{ $t('form.submit') }}</button>
    <p v-if="done">{{ $t('form.ok') }}</p>
  </form>
</template>

<script setup lang="ts">
const props = defineProps<{ productId?: number }>()
const { post } = useStoreApi()
const loading = ref(false)
const done = ref(false)
const agree = ref(false)
const form = reactive({
  name: '', company: '', email: '', country: '', quantity: '', message: '', website: '', productId: props.productId
})

async function submit() {
  if (form.website) return
  loading.value = true
  try {
    await post('/inquiries', form)
    done.value = true
  } finally {
    loading.value = false
  }
}
</script>
