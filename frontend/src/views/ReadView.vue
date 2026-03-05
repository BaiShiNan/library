<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../api';
import type { Book } from '../types';
import { ArrowLeft, Moon, Sun, ChevronLeft, ChevronRight, Settings, Type } from 'lucide-vue-next';

const route = useRoute();
const router = useRouter();

const book = ref<Book | null>(null);
const isLoading = ref(true);
const currentPage = ref(1);
const totalPages = ref(100); // Mock
const fontSize = ref(18);
const isDarkMode = ref(false);
const showControls = ref(true);

const fetchBook = async () => {
  try {
    const res = await api.get<Book>(`/books/${route.params.id}`);
    book.value = res.data;
    if (book.value) totalPages.value = book.value.pageCount || 100;
  } catch (error) {
    console.error('Failed to fetch book', error);
    // Mock
    book.value = {
        id: String(route.params.id),
        title: '了不起的盖茨比',
        author: 'F. Scott Fitzgerald',
        category: '文学',
        coverUrl: '',
        fileUrl: '',
        rating: 4.5,
        pageCount: 180,
        createdAt: new Date(),
        isbn: '123456',
        description: '',
        publishDate: new Date()
    };
    totalPages.value = 180;
  } finally {
    isLoading.value = false;
  }
};

const handlePrevPage = () => {
  if (currentPage.value > 1) currentPage.value--;
};

const handleNextPage = () => {
  if (currentPage.value < totalPages.value) currentPage.value++;
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
  <div class="fixed inset-0 z-50 flex flex-col transition-colors duration-500 font-serif" :class="isDarkMode ? 'bg-[#1a1a1a] text-gray-300' : 'bg-[#f8f9fa] text-gray-800'">
    <!-- Top Bar -->
    <header 
      class="h-16 flex items-center justify-between px-4 md:px-6 shadow-sm transition-all duration-300 z-10"
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
      
      <div class="flex items-center space-x-1 md:space-x-2">
        <div class="flex items-center bg-opacity-10 bg-gray-500 rounded-lg p-1 mr-2">
          <button @click="decreaseFontSize" class="p-1.5 rounded hover:bg-opacity-20 hover:bg-gray-400 transition" title="减小字号">
            <span class="text-xs font-bold">A-</span>
          </button>
          <button @click="increaseFontSize" class="p-1.5 rounded hover:bg-opacity-20 hover:bg-gray-400 transition" title="增大字号">
            <span class="text-lg font-bold">A+</span>
          </button>
        </div>
        
        <button @click="toggleDarkMode" class="p-2 rounded-full hover:bg-opacity-10 hover:bg-gray-500 transition-colors" :title="isDarkMode ? '切换到亮色模式' : '切换到暗色模式'">
          <Sun v-if="isDarkMode" class="h-5 w-5" />
          <Moon v-else class="h-5 w-5" />
        </button>
      </div>
    </header>

    <!-- Reader Content -->
    <main 
      class="flex-grow overflow-y-auto relative scroll-smooth" 
      @click="showControls = !showControls"
    >
      <div class="max-w-3xl mx-auto px-6 py-12 md:py-20 min-h-full flex flex-col">
        <div v-if="isLoading" class="flex items-center justify-center h-full">
          <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
        </div>
        
        <div v-else class="prose max-w-none flex-grow transition-all duration-300 ease-in-out" :style="{ fontSize: `${fontSize}px`, lineHeight: '1.8' }">
          <div :class="isDarkMode ? 'prose-invert' : ''">
            <h2 class="text-center mb-16 font-bold opacity-80">第 {{ currentPage }} 章</h2>
            
            <div class="space-y-6 text-justify">
              <p>
                那是在去年夏天，我住在西卵。那是长岛的一个村镇，虽然这名字听起来有点古怪，其实它的地形并不怎么奇特。
                在这个细长的岛屿上，除了另外一些自然界的好奇现象以外，还有两个巨大的蛋形地带，在离纽约市二十英里处，
                彼此隔着一个叫做长岛海峡的小港湾，正对着伸进海里。
              </p>
              <p>
                它们并不是正圆形的——像鸡蛋一样，在接触地面的那一头是平的——但是由于它们的形状酷似，以及由于某种奇特的巧合，
                它们被称作“西卵”和“东卵”，这使得那些不熟悉这一带地理的人感到迷惑不解。
              </p>
              <p>
                我的房子就在西卵的顶端，离海峡只有五十码，夹在两座巨大的别墅中间，那两座别墅每季的租金都要一万二千到一万五千美元。
                我的房子就在右边那座别墅的阴影里，那座别墅是一座模仿诺曼底市政厅样式的庞然大物，一边有一座塔楼，在月光下显得白惨惨的，
                还有一座大理石游泳池，以及四十英亩草坪和花园。
              </p>
              <p>
                那就是盖茨比的公馆。或者更确切地说，是一座住着一位姓盖茨比的先生的公馆。
              </p>
              <p v-for="i in 5" :key="i">
                这里是模拟的章节内容文本，用于展示阅读器的排版效果。实际开发中，这里将显示从后端获取的真实图书章节内容。
                优秀的排版设计能够极大地提升用户的阅读体验，合适的字号、行高以及段间距都是关键因素。
                在这个数字图书馆项目中，我们致力于为用户打造最舒适的在线阅读环境。
              </p>
            </div>
            
            <div class="text-center mt-16 mb-8 opacity-50 text-sm">
              - 本章完 -
            </div>
          </div>
        </div>
      </div>
    </main>

    <!-- Bottom Bar -->
    <footer 
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
        第 {{ currentPage }} 页 / 共 {{ totalPages }} 页
      </div>
      
      <button 
        @click="handleNextPage" 
        :disabled="currentPage >= totalPages"
        class="flex items-center gap-2 px-4 py-2 rounded-full hover:bg-opacity-10 hover:bg-gray-500 transition disabled:opacity-30 disabled:cursor-not-allowed font-medium"
      >
        <span class="hidden md:inline">下一页</span>
        <ChevronRight class="h-5 w-5" />
      </button>
    </footer>
  </div>
</template>
