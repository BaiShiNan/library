<script setup>
import { ref, onMounted } from 'vue';
import api from '../api';
import BookCard from '../components/BookCard.vue';
import { Heart, Search } from 'lucide-vue-next';

const favorites = ref([]);
const isLoading = ref(true);

const fetchFavorites = async () => {
  try {
    const res = await api.get('/user/favorites');
    favorites.value = res.data;
  } catch (error) {
    console.error('Failed to fetch favorites', error);
    favorites.value = [];
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  fetchFavorites();
});
</script>

<template>
  <div class="max-w-6xl mx-auto">
    <div class="flex items-center gap-4 mb-10">
      <div class="bg-red-50 p-4 rounded-2xl shadow-sm">
        <Heart class="h-8 w-8 text-red-500 fill-current" />
      </div>
      <div>
        <h1 class="text-3xl font-bold text-gray-900">我的收藏</h1>
        <p class="text-gray-500 mt-1">您收藏的所有图书都在这里</p>
      </div>
    </div>

    <div v-if="isLoading" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
      <div v-for="i in 4" :key="i" class="bg-white aspect-[3/4] rounded-xl animate-pulse bg-gray-100"></div>
    </div>

    <div v-else-if="favorites.length > 0" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
      <BookCard v-for="book in favorites" :key="book.id" :book="book" />
    </div>

    <div v-else class="text-center py-24 bg-white rounded-3xl border border-gray-100 shadow-sm">
      <div class="bg-gray-50 w-24 h-24 rounded-full flex items-center justify-center mx-auto mb-6">
        <Heart class="h-10 w-10 text-gray-300" />
      </div>
      <h3 class="text-xl font-bold text-gray-900 mb-2">暂无收藏</h3>
      <p class="text-gray-500 mb-8 max-w-md mx-auto">遇到喜欢的书就收藏起来吧，方便以后快速找到。</p>
      <RouterLink to="/books" class="inline-flex items-center px-6 py-3 bg-primary text-white rounded-full hover:bg-blue-800 transition shadow-md hover:shadow-lg font-medium">
        <Search class="h-4 w-4 mr-2" />
        去发现好书
      </RouterLink>
    </div>
  </div>
</template>
