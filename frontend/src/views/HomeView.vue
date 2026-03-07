<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import api from '../api';
import BookCard from '../components/BookCard.vue';
import { Search, ChevronRight, BookOpen } from 'lucide-vue-next';

const router = useRouter();
const searchQuery = ref('');
const categories = ref([]);
const recommendedBooks = ref([]);
const isLoading = ref(true);

const handleSearch = () => {
  if (searchQuery.value.trim()) {
    router.push({ path: '/books', query: { search: searchQuery.value } });
  }
};

onMounted(async () => {
  try {
    // Fetch categories
    const categoriesRes = await api.get('/categories');
    categories.value = categoriesRes.data;

    // Fetch recommended books
    const booksRes = await api.get('/books?limit=4');
    recommendedBooks.value = booksRes.data.list;
  } catch (error) {
    console.error('Failed to fetch data', error);
  } finally {
    isLoading.value = false;
  }
});
</script>

<template>
  <div class="space-y-16">
    <!-- Hero Section -->
    <section class="relative bg-gradient-to-br from-primary to-blue-900 rounded-3xl p-8 md:p-16 text-white overflow-hidden shadow-2xl">
      <!-- Background pattern -->
      <div class="absolute inset-0 opacity-10 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAiIGhlaWdodD0iMjAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGNpcmNsZSBjeD0iMSIgY3k9IjEiIHI9IjEiIGZpbGw9IiNmZmYiLz48L3N2Zz4=')]"></div>
      
      <!-- Decorative circles -->
      <div class="absolute -top-24 -left-24 w-96 h-96 bg-blue-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob"></div>
      <div class="absolute -bottom-24 -right-24 w-96 h-96 bg-purple-500 rounded-full mix-blend-multiply filter blur-3xl opacity-20 animate-blob animation-delay-2000"></div>

      <div class="relative z-10 max-w-3xl mx-auto text-center space-y-8">
        <h1 class="text-4xl md:text-6xl font-bold tracking-tight leading-tight">
          发现你的下一本<span class="text-accent">好书</span>
        </h1>
        <p class="text-blue-100 text-lg md:text-xl max-w-2xl mx-auto font-light">
          海量图书资源，随时随地在线阅读。开启您的数字阅读之旅。
        </p>
        
        <div class="relative max-w-2xl mx-auto group">
          <div class="absolute -inset-1 bg-gradient-to-r from-blue-400 to-accent rounded-full blur opacity-25 group-hover:opacity-50 transition duration-200"></div>
          <div class="relative flex items-center">
            <input 
              v-model="searchQuery"
              @keyup.enter="handleSearch"
              type="text" 
              placeholder="搜索书名、作者或 ISBN..." 
              class="w-full pl-6 pr-14 py-4 md:py-5 rounded-full text-gray-800 bg-white focus:outline-none focus:ring-0 shadow-xl text-lg placeholder-gray-400"
            />
            <button 
              @click="handleSearch"
              class="absolute right-2 top-2 bottom-2 aspect-square bg-accent hover:bg-orange-600 text-white rounded-full transition-all duration-200 flex items-center justify-center shadow-md hover:scale-105"
            >
              <Search class="h-6 w-6" />
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- Categories -->
    <section class="container mx-auto">
      <div class="flex items-center justify-between mb-8">
        <h2 class="text-2xl font-bold text-gray-800 flex items-center">
          <span class="w-1 h-8 bg-primary rounded-full mr-3"></span>
          探索分类
        </h2>
      </div>
      
      <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-5 gap-6">
        <RouterLink 
          v-for="category in categories" 
          :key="category.id"
          :to="{ path: '/books', query: { categoryId: category.id } }"
          class="group bg-white p-6 rounded-2xl shadow-card hover:shadow-card-hover border border-gray-100 transition-all duration-300 text-center relative overflow-hidden"
        >
          <div class="absolute inset-0 bg-gradient-to-br from-primary/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-300"></div>
          <div class="w-12 h-12 mx-auto bg-blue-50 text-primary rounded-full flex items-center justify-center mb-4 group-hover:scale-110 group-hover:bg-primary group-hover:text-white transition-all duration-300">
            <BookOpen class="h-6 w-6" />
          </div>
          <h3 class="font-bold text-gray-700 group-hover:text-primary transition-colors text-lg">{{ category.name }}</h3>
        </RouterLink>
      </div>
    </section>

    <!-- Recommended Books -->
    <section class="container mx-auto">
      <div class="flex justify-between items-center mb-8">
        <h2 class="text-2xl font-bold text-gray-800 flex items-center">
          <span class="w-1 h-8 bg-accent rounded-full mr-3"></span>
          为你推荐
        </h2>
        <RouterLink to="/books" class="flex items-center text-primary hover:text-blue-700 font-medium group transition-colors px-4 py-2 rounded-full hover:bg-blue-50">
          查看全部
          <ChevronRight class="h-4 w-4 ml-1 transform group-hover:translate-x-1 transition-transform" />
        </RouterLink>
      </div>
      
      <div v-if="isLoading" class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
        <div v-for="i in 4" :key="i" class="bg-white aspect-[3/4] rounded-xl animate-pulse bg-gray-200"></div>
      </div>
      
      <div v-else class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-8">
        <BookCard v-for="book in recommendedBooks" :key="book.id" :book="book" />
      </div>
    </section>
  </div>
</template>

<style scoped>
.animate-blob {
  animation: blob 7s infinite;
}
.animation-delay-2000 {
  animation-delay: 2s;
}
@keyframes blob {
  0% { transform: translate(0px, 0px) scale(1); }
  33% { transform: translate(30px, -50px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
  100% { transform: translate(0px, 0px) scale(1); }
}
</style>
