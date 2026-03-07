<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../api';
import type { Book } from '../types';
import { useAuthStore } from '../stores/auth';
import { Star, BookOpen, Heart, Calendar, FileText, Share2, Info, ArrowLeft } from 'lucide-vue-next';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

const book = ref<Book | null>(null);
const isLoading = ref(true);
const isFavorite = ref(false); 

const fetchBook = async () => {
  try {
    const res = await api.get<Book>(`/books/${route.params.id}`);
    book.value = res.data;
  } catch (err: any) {
    console.error('Failed to fetch book', err);
    if (err.response) {
        if (err.response.status === 404) {
            error.value = '未找到该图书';
        } else {
            error.value = `加载失败: ${err.response.status} ${err.response.statusText}`;
        }
    } else {
        error.value = '无法连接到服务器';
    }
  } finally {
    isLoading.value = false;
  }
};

const handleRead = () => {
  if (!authStore.isAuthenticated) {
    router.push('/login');
    return;
  }
  router.push(`/read/${book.value?.id}`);
};

const toggleFavorite = async () => {
  if (!authStore.isAuthenticated) {
    router.push('/login');
    return;
  }
  try {
    await api.post(`/books/${book.value?.id}/favorite`);
    isFavorite.value = !isFavorite.value;
  } catch (error) {
    console.error('Failed to toggle favorite', error);
  }
};

const checkFavoriteStatus = async () => {
  if (!authStore.isAuthenticated) return;
  try {
    const res = await api.get<{ isFavorite: boolean }>(`/books/${route.params.id}/favorite`);
    isFavorite.value = res.data.isFavorite;
  } catch (error) {
    console.error('Failed to check favorite status', error);
  }
};

onMounted(() => {
  fetchBook();
  checkFavoriteStatus();
});
</script>

<template>
  <div v-if="isLoading" class="animate-pulse max-w-5xl mx-auto">
    <div class="h-96 bg-gray-200 rounded-2xl mb-8"></div>
    <div class="space-y-4">
      <div class="h-8 bg-gray-200 rounded w-1/3"></div>
      <div class="h-4 bg-gray-200 rounded w-full"></div>
      <div class="h-4 bg-gray-200 rounded w-full"></div>
    </div>
  </div>

  <div v-else-if="book" class="max-w-5xl mx-auto">
    <div class="mb-6">
      <button 
        @click="router.back()" 
        class="flex items-center gap-2 text-gray-600 hover:text-primary transition-colors px-4 py-2 rounded-full hover:bg-gray-100 font-medium"
      >
        <ArrowLeft class="h-5 w-5" />
        返回
      </button>
    </div>
    
    <div class="bg-white rounded-3xl shadow-xl overflow-hidden border border-gray-100">
      <div class="md:flex">
        <!-- Book Cover Section -->
        <div class="md:w-1/3 lg:w-1/3 bg-gray-50 p-8 lg:p-12 flex flex-col items-center justify-center border-r border-gray-100">
          <div class="shadow-2xl rounded-lg overflow-hidden w-48 md:w-full max-w-[260px] transform hover:scale-105 transition-transform duration-500">
            <img 
              v-if="book.coverUrl" 
              :src="book.coverUrl" 
              :alt="book.title" 
              class="w-full h-auto object-cover"
            />
            <div v-else class="w-full h-64 bg-gray-200 flex items-center justify-center text-gray-400">
              暂无封面
            </div>
          </div>
          
          <div class="mt-8 flex gap-4 w-full max-w-[260px]">
             <button 
              @click="toggleFavorite"
              class="flex-1 py-2 px-4 rounded-full border border-gray-200 text-gray-600 hover:bg-red-50 hover:text-red-500 hover:border-red-200 transition-colors flex items-center justify-center gap-2 text-sm font-medium"
              :class="{ 'text-red-500 bg-red-50 border-red-200': isFavorite }"
            >
              <Heart class="h-4 w-4" :class="{ 'fill-current': isFavorite }" />
              {{ isFavorite ? '已收藏' : '收藏' }}
            </button>
            <button class="p-2 rounded-full border border-gray-200 text-gray-600 hover:bg-gray-50 hover:text-primary transition-colors">
              <Share2 class="h-4 w-4" />
            </button>
          </div>
        </div>

        <!-- Book Info Section -->
        <div class="p-8 lg:p-12 md:w-2/3 lg:w-2/3 flex flex-col">
          <div class="mb-8">
            <div class="flex items-start justify-between">
              <div>
                <span class="inline-block px-3 py-1 rounded-full bg-blue-50 text-primary text-xs font-semibold tracking-wide uppercase mb-3">
                  {{ book.categoryId || '未分类' }}
                </span>
                <h1 class="text-3xl md:text-4xl font-bold text-gray-900 mb-2 leading-tight">{{ book.title }}</h1>
                <p class="text-xl text-gray-600 font-medium">{{ book.author }}</p>
              </div>
              <div class="flex flex-col items-end">
                <div class="flex items-center bg-yellow-50 px-3 py-1.5 rounded-lg border border-yellow-100">
                  <Star class="h-5 w-5 text-yellow-400 fill-current mr-1.5" />
                  <span class="font-bold text-gray-800 text-lg">{{ book.rating }}</span>
                </div>
                <span class="text-xs text-gray-400 mt-1">评分</span>
              </div>
            </div>
            
            <div class="grid grid-cols-2 md:grid-cols-3 gap-6 my-8 py-6 border-t border-b border-gray-100">
              <div class="flex items-center">
                <div class="p-2 rounded-lg bg-gray-50 text-gray-400 mr-3">
                  <FileText class="h-5 w-5" />
                </div>
                <div>
                  <p class="text-xs text-gray-400 uppercase tracking-wider">页数</p>
                  <p class="font-semibold text-gray-700">{{ book.pageCount }} 页</p>
                </div>
              </div>
              <div class="flex items-center" v-if="book.publishDate">
                <div class="p-2 rounded-lg bg-gray-50 text-gray-400 mr-3">
                  <Calendar class="h-5 w-5" />
                </div>
                <div>
                  <p class="text-xs text-gray-400 uppercase tracking-wider">出版年份</p>
                  <p class="font-semibold text-gray-700">{{ new Date(book.publishDate).getFullYear() }}</p>
                </div>
              </div>
              <div class="flex items-center" v-if="book.isbn">
                <div class="p-2 rounded-lg bg-gray-50 text-gray-400 mr-3">
                  <Info class="h-5 w-5" />
                </div>
                <div>
                  <p class="text-xs text-gray-400 uppercase tracking-wider">ISBN</p>
                  <p class="font-semibold text-gray-700">{{ book.isbn }}</p>
                </div>
              </div>
            </div>

            <div class="prose prose-blue max-w-none text-gray-600 leading-relaxed">
              <h3 class="text-lg font-bold text-gray-800 mb-3 flex items-center">
                <span class="w-1 h-6 bg-accent rounded-full mr-2"></span>
                内容简介
              </h3>
              <p>{{ book.description || '暂无简介' }}</p>
            </div>
          </div>

          <div class="mt-auto pt-8">
            <button 
              @click="handleRead"
              class="w-full bg-gradient-to-r from-primary to-blue-700 text-white py-4 px-8 rounded-xl hover:shadow-lg hover:shadow-blue-200 transition-all duration-300 flex items-center justify-center font-bold text-lg group"
            >
              <BookOpen class="h-6 w-6 mr-3 group-hover:scale-110 transition-transform" />
              开始阅读
            </button>
            <p class="text-center text-xs text-gray-400 mt-3 flex items-center justify-center">
              <span class="inline-block w-1.5 h-1.5 rounded-full bg-green-400 mr-2"></span>
              支持在线阅读 • 自动保存进度
            </p>
          </div>
        </div>
      </div>
    </div>
  </div>
  
  <div v-else class="text-center py-24 bg-white rounded-3xl shadow-sm border border-gray-100">
    <div class="bg-gray-50 w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-6">
      <BookOpen class="h-10 w-10 text-gray-400" />
    </div>
    <h2 class="text-2xl font-bold text-gray-800 mb-2">{{ error || '未找到该图书' }}</h2>
    <p class="text-gray-500 mb-8">抱歉，您访问的图书不存在或已被移除。</p>
    <button 
      @click="router.push('/books')"
      class="text-primary font-medium hover:underline"
    >
      返回图书库
    </button>
  </div>
</template>
