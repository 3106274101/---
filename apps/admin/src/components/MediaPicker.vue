<template>
  <div class="media-picker">
    <el-input v-model="model" :placeholder="placeholder" clearable>
      <template #append>
        <el-button @click="open = true">媒体库</el-button>
      </template>
    </el-input>
    <img v-if="preview && model" :src="model" class="thumb" alt="" />
    <el-dialog v-model="open" title="选择素材" width="720px" append-to-body>
      <div class="picker-tools">
        <el-upload :http-request="upload" :show-file-list="false" accept="image/*">
          <el-button size="small" type="primary">上传图片</el-button>
        </el-upload>
        <el-button size="small" @click="load">刷新</el-button>
      </div>
      <div v-if="!list.length" class="empty-hint">媒体库是空的，先上传一张图。</div>
      <div class="picker-grid">
        <button v-for="item in images" :key="item.id" type="button" class="pick" @click="choose(item.url)">
          <img :src="item.url" :alt="item.originalName" />
          <span>{{ item.originalName }}</span>
        </button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../api/http'

const props = withDefaults(defineProps<{
  modelValue?: string
  placeholder?: string
  preview?: boolean
}>(), { placeholder: '图片 URL', preview: true })
const emit = defineEmits<{ 'update:modelValue': [string] }>()
const model = computed({
  get: () => props.modelValue || '',
  set: (v: string) => emit('update:modelValue', v)
})
const open = ref(false)
const list = ref<any[]>([])
const images = computed(() => list.value.filter((x) => String(x.mime || '').startsWith('image') || /\.(png|jpe?g|webp|gif)$/i.test(x.url || '')))

watch(open, (v) => { if (v) load() })

async function load() {
  const res: any = await http.get('/admin/media')
  list.value = res.data || []
}
async function upload(opt: any) {
  const fd = new FormData()
  fd.append('file', opt.file)
  fd.append('alt', opt.file.name)
  const res: any = await http.post('/admin/media/upload', fd)
  await load()
  if (res.data?.url) choose(res.data.url)
  else ElMessage.success('已上传')
}
function choose(url: string) {
  model.value = url
  open.value = false
}
</script>

<style scoped>
.thumb { margin-top: 8px; width: 160px; height: 100px; object-fit: cover; display: block; }
.picker-tools { display: flex; gap: 8px; margin-bottom: 12px; }
.picker-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  max-height: 420px;
  overflow: auto;
}
.pick {
  border: 1px solid var(--th-line);
  background: #fff;
  padding: 0;
  cursor: pointer;
  text-align: left;
}
.pick img { width: 100%; height: 90px; object-fit: cover; display: block; }
.pick span { display: block; padding: 6px 8px; font-size: 11px; color: var(--th-muted); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
