<script setup lang="ts">
import { ref, onMounted, computed, watch, nextTick, markRaw } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../api';
import type { Book } from '../types';
import { ArrowLeft, Moon, Sun, ChevronLeft, ChevronRight } from 'lucide-vue-next';
import * as pdfjsLib from 'pdfjs-dist';

// Use a static URL for the worker. We need to copy the worker file to the public directory or use a CDN as a fallback.
// Since import ...?url can be tricky with some Vite versions/configs for node_modules.
// Let's try a direct public path approach if the import failed, or revert to a known working CDN for this specific version if local fails.
// But first, let's try to set it explicitly to standard import.
pdfjsLib.GlobalWorkerOptions.workerSrc = new URL('pdfjs-dist/build/pdf.worker.mjs', import.meta.url).toString();

const route = useRoute();
const router = useRouter();

const book = ref<Book | null>(null);
const isLoading = ref(true);
const error = ref<string | null>(null);
const textContent = ref<string>('');

// PDF State
const pdfDoc = ref<any>(null);
const pdfCanvas = ref<HTMLCanvasElement | null>(null);
const pdfScale = ref(1.5);

// Novel Reader State
const isDarkMode = ref(false);
const fontSize = ref(18);
const showControls = ref(true);

// Pagination
const pages = ref<string[]>([]);
const currentPage = ref(1);
const totalPdfPages = ref(0);
const PAGE_SIZE = 1500; // Approx characters per page

const fileType = computed(() => {
  if (!book.value?.fileUrl) return null;
  const url = book.value.fileUrl.toLowerCase();
  if (url.endsWith('.pdf')) return 'pdf';
  if (url.endsWith('.txt')) return 'txt';
  return 'other';
});

// Calculate pages when textContent changes (TXT only)
watch(textContent, (newText) => {
  if (!newText) {
    pages.value = [];
    return;
  }
  
  const result = [];
  let startIndex = 0;
  while (startIndex < newText.length) {
    let endIndex = startIndex + PAGE_SIZE;
    if (endIndex >= newText.length) {
      endIndex = newText.length;
    } else {
      const lastNewline = newText.lastIndexOf('\n', endIndex);
      if (lastNewline > startIndex && lastNewline > endIndex - 200) {
        endIndex = lastNewline + 1;
      } else {
        const lastSpace = newText.lastIndexOf(' ', endIndex);
        if (lastSpace > startIndex) {
          endIndex = lastSpace + 1;
        }
      }
    }
    result.push(newText.slice(startIndex, endIndex));
    startIndex = endIndex;
  }
  pages.value = result;
});

const handlePrevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--;
    if (fileType.value === 'pdf') {
        renderPdfPage(currentPage.value);
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
};

const handleNextPage = () => {
  const maxPage = fileType.value === 'pdf' ? totalPdfPages.value : pages.value.length;
  if (currentPage.value < maxPage) {
    currentPage.value++;
    if (fileType.value === 'pdf') {
        renderPdfPage(currentPage.value);
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
};

const fetchBook = async () => {
  try {
    const res = await api.get<Book>(`/books/${route.params.id}`);
    book.value = res.data;
    
    if (fileType.value === 'txt' && book.value?.fileUrl) {
      await fetchTextContent(book.value.fileUrl);
    } else if (fileType.value === 'pdf' && book.value?.fileUrl) {
      await initPdf(book.value.fileUrl);
    } else {
        isLoading.value = false;
    }
  } catch (err) {
    console.error('Failed to fetch book', err);
    error.value = '无法加载图书信息';
    isLoading.value = false;
  }
};

const initPdf = async (url: string) => {
    try {
        const loadingTask = pdfjsLib.getDocument(url);
        const pdf = await loadingTask.promise;
        // 使用 markRaw 避免 Vue 响应式代理 PDFDocumentProxy 对象
        // 这是一个复杂的对象，包含私有字段 (#pagesNumber)，被代理后会报错
        pdfDoc.value = markRaw(pdf);
        totalPdfPages.value = pdf.numPages;
        currentPage.value = 1;
        
        // 关键修复：必须先结束 loading 状态，Vue 才会渲染 v-else-if="fileType === 'pdf'" 分支中的 canvas 元素
        // 否则此时 canvas 元素根本不存在，pdfCanvas.value 为 null，导致首页渲染失败
        isLoading.value = false;
        
        await nextTick(); // 等待 DOM 更新，确保 canvas 已挂载
        await renderPdfPage(1);
    } catch (err) {
        console.error('Error loading PDF:', err);
        error.value = 'PDF 加载失败: ' + (err instanceof Error ? err.message : String(err));
        isLoading.value = false;
    }
};

const renderPdfPage = async (num: number) => {
    if (!pdfDoc.value || !pdfCanvas.value) return;
    
    try {
        const page = await pdfDoc.value.getPage(num);
        const viewport = page.getViewport({ scale: pdfScale.value });
        const canvas = pdfCanvas.value;
        const context = canvas.getContext('2d');
        
        canvas.height = viewport.height;
        canvas.width = viewport.width;
        
        const renderContext = {
            canvasContext: context!,
            viewport: viewport
        };
        
        await page.render(renderContext).promise;
    } catch (err) {
        console.error('Error rendering page:', err);
    }
};

const fetchTextContent = async (url: string) => {
  try {
    const response = await fetch(url);
    if (!response.ok) throw new Error('Failed to load text file');
    
    // 使用流式读取
    const reader = response.body?.getReader();
    // 使用 GBK 解码器尝试解决乱码问题 (如果文件是 GBK/GB18030)
    // 现代浏览器 TextDecoder 默认 utf-8，但中文 Windows txt 常见 gbk
    // 可以尝试先用 utf-8 解码，如果检测到乱码则切换，或者提供选项
    // 简单起见，这里默认尝试 gbk，或者我们可以尝试自动检测（虽然复杂）
    // 鉴于用户反馈乱码，很有可能是 GBK 编码的文件
    let decoder = new TextDecoder('gbk'); 
    
    // 尝试先读取一小段来检测编码 (简单启发式：如果有大量无法识别字符)
    // 但 TextDecoder('gbk') 也能解码 utf-8 兼容的 ascii，纯 utf-8 中文在 gbk 下会乱码
    // 更稳妥的方式是让 TextDecoder 默认 utf-8，失败再重试？
    // 由于用户明确提到乱码，我们先改回 utf-8 看看是否是之前的流式读取逻辑导致的（之前的代码是 utf-8）
    // 如果之前是 utf-8 乱码，那说明文件是 gbk。
    // 我们这里提供一个简单的切换机制或者尝试检测
    
    // 修正：我们先用 GBK 尝试，因为这是中文 TXT 最常见的乱码原因
    decoder = new TextDecoder('gb18030'); // gb18030 兼容 gbk 和 gb2312

    if (!reader) {
        // Fallback for browsers without stream support
        const text = await response.text();
        textContent.value = text;
        if (!text.trim()) error.value = '文件内容为空';
        isLoading.value = false;
        return;
    }

    // 读取第一块数据后立即显示，减少首屏等待
    const { done, value } = await reader.read();
    if (value) {
        textContent.value = decoder.decode(value, { stream: true });
        // 第一块读取完毕，立即取消 loading 状态，让用户看到内容
        isLoading.value = false;
    }

    if (done) {
        if (!textContent.value.trim()) error.value = '文件内容为空';
        return;
    }

    // 后台继续读取剩余内容
    readRemaining(reader, decoder);
    
  } catch (err) {
    console.error('Failed to load text content', err);
    error.value = '无法加载文本内容';
    isLoading.value = false;
  }
};

const readRemaining = async (reader: ReadableStreamDefaultReader<Uint8Array>, decoder: TextDecoder) => {
    try {
        while (true) {
            const { done, value } = await reader.read();
            if (done) break;
            // 追加内容
            textContent.value += decoder.decode(value, { stream: true });
        }
    } catch (err) {
        console.error('Error reading stream', err);
    }
};

const toggleDarkMode = () => {
  isDarkMode.value = !isDarkMode.value;
};

const increaseFontSize = () => {
  if (fontSize.value < 32) fontSize.value += 2;
};

const decreaseFontSize = () => {
  if (fontSize.value > 14) fontSize.value -= 2;
};

onMounted(() => {
  fetchBook();
});
</script>

<template>
  <div class="fixed inset-0 z-50 flex flex-col transition-colors duration-300" :class="isDarkMode ? 'bg-[#1a1a1a] text-gray-300' : 'bg-[#f8f9fa] text-gray-800'">
    <!-- Top Bar -->
    <header 
      class="h-16 flex items-center justify-between px-4 md:px-6 shadow-sm z-10 transition-all duration-300"
      :class="[
        isDarkMode ? 'bg-[#252525] border-gray-700 shadow-gray-900/50' : 'bg-white border-gray-200 shadow-gray-200/50',
        showControls ? 'translate-y-0 opacity-100' : '-translate-y-full opacity-0 pointer-events-none'
      ]"
    >
      <div class="flex items-center gap-4">
        <button @click="router.back()" class="p-2 rounded-full hover:bg-opacity-10 hover:bg-gray-500 transition-colors">
          <ArrowLeft class="h-5 w-5" />
        </button>
        <h1 class="font-bold text-lg truncate max-w-[200px] md:max-w-md">{{ book?.title || '加载中...' }}</h1>
      </div>
      
      <div v-if="fileType === 'txt'" class="flex items-center space-x-2">
        <div class="flex items-center bg-opacity-10 bg-gray-500 rounded-lg p-1 mr-2">
          <button @click="decreaseFontSize" class="p-1.5 rounded hover:bg-opacity-20 hover:bg-gray-400 transition" title="减小字号">
            <span class="text-xs font-bold">A-</span>
          </button>
          <button @click="increaseFontSize" class="p-1.5 rounded hover:bg-opacity-20 hover:bg-gray-400 transition" title="增大字号">
            <span class="text-lg font-bold">A+</span>
          </button>
        </div>
        
        <button @click="toggleDarkMode" class="p-2 rounded-full hover:bg-opacity-10 hover:bg-gray-500 transition-colors">
          <Sun v-if="isDarkMode" class="h-5 w-5" />
          <Moon v-else class="h-5 w-5" />
        </button>
      </div>
    </header>

    <!-- Reader Content -->
    <main 
      class="flex-grow relative overflow-hidden" 
      @click="showControls = !showControls"
    >
      <div v-if="isLoading" class="flex items-center justify-center h-full">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
      
      <div v-else-if="error" class="flex items-center justify-center h-full text-red-500">
        {{ error }}
      </div>

      <!-- PDF Reader (Canvas) -->
      <div v-else-if="fileType === 'pdf'" class="w-full h-full overflow-auto flex justify-center bg-gray-200 py-8">
        <div class="shadow-lg transition-all duration-300" :class="{ 'invert-colors': isDarkMode }">
            <canvas ref="pdfCanvas" class="block"></canvas>
        </div>
      </div>

      <!-- TXT Reader (Custom UI) -->
      <div v-else-if="fileType === 'txt'" class="h-full overflow-y-auto scroll-smooth">
        <div class="max-w-3xl mx-auto px-6 py-12 md:py-20 min-h-full flex flex-col">
          <div 
            class="prose max-w-none whitespace-pre-wrap text-justify leading-relaxed flex-grow"
            :class="isDarkMode ? 'prose-invert' : ''"
            :style="{ fontSize: `${fontSize}px` }"
          >
            {{ pages[currentPage - 1] }}
          </div>
          
          <div class="mt-8 flex items-center justify-between text-sm opacity-50 select-none">
             <span>{{ currentPage }} / {{ pages.length || 1 }}</span>
             <span>{{ (currentPage / (pages.length || 1) * 100).toFixed(1) }}%</span>
          </div>
        </div>
      </div>

      <!-- Fallback -->
      <div v-else class="flex flex-col items-center justify-center h-full text-gray-500">
        <p>不支持的文件格式</p>
        <a v-if="book?.fileUrl" :href="book.fileUrl" target="_blank" class="mt-4 text-blue-500 hover:underline">点击下载文件</a>
      </div>
    </main>
    <!-- Bottom Bar (Pagination) -->
    <footer 
      v-if="fileType === 'txt' || fileType === 'pdf'"
      class="h-16 flex items-center justify-between px-4 md:px-8 shadow-sm border-t transition-all duration-300 z-10"
      :class="[
        isDarkMode ? 'bg-[#252525] border-gray-700 shadow-gray-900/50' : 'bg-white border-gray-200 shadow-gray-200/50', 
        showControls ? 'translate-y-0 opacity-100' : 'translate-y-full opacity-0 pointer-events-none'
      ]"
    >
      <button 
        @click="handlePrevPage" 
        :disabled="currentPage <= 1"
        class="flex items-center gap-2 px-4 py-2 rounded-full hover:bg-opacity-10 hover:bg-gray-500 transition disabled:opacity-30 disabled:cursor-not-allowed font-medium"
      >
        <ChevronLeft class="h-5 w-5" />
        <span class="hidden md:inline">上一页</span>
      </button>
      
      <div class="text-sm font-medium opacity-80 font-sans">
        第 {{ currentPage }} 页 / 共 {{ fileType === 'pdf' ? totalPdfPages : (pages.length || 1) }} 页
      </div>
      
      <button 
        @click="handleNextPage" 
        :disabled="currentPage >= (fileType === 'pdf' ? totalPdfPages : pages.length)"
        class="flex items-center gap-2 px-4 py-2 rounded-full hover:bg-opacity-10 hover:bg-gray-500 transition disabled:opacity-30 disabled:cursor-not-allowed font-medium"
      >
        <span class="hidden md:inline">下一页</span>
        <ChevronRight class="h-5 w-5" />
      </button>
    </footer>
  </div>
</template>

<style scoped>
.invert-colors {
  filter: invert(1) hue-rotate(180deg) contrast(0.8);
}
</style>
