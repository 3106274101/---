<template>
  <div>
    <section v-for="(block, i) in blocks" :key="i">
      <div v-if="block.type === 'hero'" class="hero">
        <div class="wrap copy">
          <h1>{{ block.props.heading }}</h1>
          <p>{{ block.props.subtitle }}</p>
          <NuxtLink :to="localePath(block.props.ctaTo || '/inquiry')" class="btn">{{ block.props.cta || $t('cta') }}</NuxtLink>
        </div>
        <div class="hero-img" :style="{ backgroundImage: `url(${block.props.image})` }" />
      </div>
      <div v-else-if="block.type === 'trustBar'" class="trust">
        <div v-for="item in block.props.items" :key="item">{{ item }}</div>
      </div>
      <div v-else-if="block.type === 'productGrid'" class="section">
        <div class="wrap">
          <h2>{{ block.props.heading || $t('featured') }}</h2>
          <div class="grid-3">
            <NuxtLink v-for="p in products" :key="p.id" :to="localePath('/products/' + p.slug)" class="card">
              <img :src="p.coverUrl" :alt="p.name" width="400" height="180" />
              <div class="body">
                <h3>{{ p.name }}</h3>
                <p class="muted">{{ p.summary }}</p>
              </div>
            </NuxtLink>
          </div>
        </div>
      </div>
      <div v-else-if="block.type === 'solutions'" class="section" style="background:#f6f7f9">
        <div class="wrap">
          <h2>Solutions</h2>
          <div class="grid-3">
            <div v-for="s in block.props.items" :key="s.slug" class="card">
              <div class="body">
                <h3>{{ s.title }}</h3>
                <p class="muted">{{ s.text }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-else-if="block.type === 'factory'" class="section">
        <div class="wrap grid-3" style="grid-template-columns:1.2fr 0.8fr;align-items:center">
          <div>
            <h2>{{ block.props.heading }}</h2>
            <p>{{ block.props.text }}</p>
          </div>
          <img :src="block.props.image" :alt="block.props.heading" width="560" height="320" />
        </div>
      </div>
      <div v-else-if="block.type === 'faq'" class="section">
        <div class="wrap">
          <h2>FAQ</h2>
          <details v-for="f in block.props.items" :key="f.q">
            <summary><b>{{ f.q }}</b></summary>
            <p>{{ f.a }}</p>
          </details>
        </div>
      </div>
      <div v-else-if="block.type === 'cta'" class="section" style="background:#0b1f3a;color:#fff">
        <div class="wrap">
          <h2 style="color:#fff">{{ block.props.heading }}</h2>
          <NuxtLink :to="localePath('/inquiry')" class="btn">{{ block.props.cta || $t('cta') }}</NuxtLink>
        </div>
      </div>
      <div v-else-if="block.type === 'richText'" class="section">
        <div class="wrap prose" v-html="block.props.html" />
      </div>
      <div v-else-if="block.type === 'certificates'" class="section">
        <div class="wrap">
          <h2>Certificates</h2>
          <p>{{ (block.props.items || []).join(' · ') }}</p>
        </div>
      </div>
      <div v-else-if="block.type === 'inquiryForm'" class="section">
        <div class="wrap">
          <h2>{{ block.props.title || $t('cta') }}</h2>
          <InquiryForm />
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
defineProps<{ blocks: any[]; products?: any[] }>()
const localePath = useLocalePath()
</script>
