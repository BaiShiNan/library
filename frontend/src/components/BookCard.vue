<script setup lang="ts">
import type { Book } from '../types';
import { Star, Book as BookIcon } from 'lucide-vue-next';
import { useRouter } from 'vue-router';

const props = defineProps<{
  book: Book;
}>();

const router = useRouter();

const goToDetail = () => {
  router.push(`/books/${props.book.id}`);
};
</script>

<template>
  <div 
    @click="goToDetail"
    class="group bg-white rounded-xl shadow-card hover:shadow-card-hover transition-all duration-300 overflow-hidden border border-gray-100 cursor-pointer flex flex-col h-full"
  >
    <!-- Cover Image -->
    <div class="relative aspect-[3/4] overflow-hidden bg-gray-100">
      <img 
        v-if="book.coverUrl" 
        :src="book.coverUrl" 
        :alt="book.title" 
        class="w-full h-full object-cover transform group-hover:scale-105 transition-transform duration-500"
        loading="lazy"
      />
      <div v-else class="w-full h-full flex flex-col items-center justify-center text-gray-300 bg-gray-50">
        <BookIcon class="w-12 h-12 mb-2" />
        <span class="text-xs">暂无封面</span>
      </div>
      
      <!-- Hover Overlay -->
      <div class="absolute inset-0 bg-black/0 group-hover:bg-black/5 transition-colors duration-300"></div>
    </div>

    <!-- Content -->
    <div class="p-4 flex flex-col flex-grow">
      <h3 class="font-bold text-gray-800 text-lg mb-1 line-clamp-2 leading-tight group-hover:text-primary transition-colors" :title="book.title">
        {{ book.title }}
      </h3>
      <p class="text-gray-500 text-sm mb-3 line-clamp-1">{{ book.author }}</p>
      
      <div class="mt-auto flex items-center justify-between pt-3 border-t border-gray-50">
        <div class="flex items-center space-x-1 text-yellow-500">
          <Star class="h-4 w-4 fill-current" />
          <span class="text-sm font-semibold text-gray-700">{{ book.rating }}</span>
        </div>
        <span class="text-xs text-gray-400 bg-gray-100 px-2 py-1 rounded-full">
          {{ book.pageCount }} 页
        </span>
      </div>
    </div>
  </div>
</template>
