<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../api';
import type { Book } from '../types';
import { ArrowLeft } from 'lucide-vue-next';

const route = useRoute();
const router = useRouter();

const book = ref<Book | null>(null);
const isLoading = ref(true);
const error = ref<string | null>(null);

const fetchBook = async () => {
  try {
    const res = await api.get<Book>(`/books/${route.params.id}`);
    book.value = res.data;
  } catch (err) {
    console.error('Failed to fetch book', err);
    error.value = '无法加载图书信息';
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchBook();
});
</script>

<template>
  <div class="fixed inset-0 z-50 flex flex-col bg-gray-100">
    <!-- Top Bar -->
    <header class="h-16 flex items-center justify-between px-4 md:px-6 bg-white shadow-sm z-10">
      <div class="flex items-center gap-4">
        <button @click="router.back()" class="p-2 rounded-full hover:bg-gray-100 transition-colors">
          <ArrowLeft class="h-5 w-5 text-gray-600" />
        </button>
        <h1 class="font-bold text-lg truncate max-w-[200px] md:max-w-md text-gray-800">{{ book?.title || '加载中...' }}</h1>
      </div>
    </header>

    <!-- Reader Content -->
    <main class="flex-grow relative bg-gray-200 overflow-hidden">
      <div v-if="isLoading" class="flex items-center justify-center h-full">
        <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-primary"></div>
      </div>
      
      <div v-else-if="error" class="flex items-center justify-center h-full text-red-500">
        {{ error }}
      </div>

      <div v-else-if="book && book.fileUrl" class="w-full h-full">
        <!-- Use iframe to display PDF or other browser-supported files -->
        <iframe 
          :src="book.fileUrl" 
          class="w-full h-full border-none bg-white"
          title="Book Reader"
        ></iframe>
      </div>

      <div v-else class="flex flex-col items-center justify-center h-full text-gray-500">
        <p>暂无图书文件资源</p>
      </div>
    </main>
  </div>
</template>
