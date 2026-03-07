<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import api from '../api';
import BookCard from '../components/BookCard.vue';
import { Filter, X, ChevronLeft, ChevronRight, Search } from 'lucide-vue-next';

const route = useRoute();
const router = useRouter();

const books = ref([]);
const categories = ref([]);
const total = ref(0);
const currentPage = ref(1);
const totalPages = ref(1);
const isLoading = ref(true);

const selectedCategory = ref(route.query.categoryId || '');
const searchQuery = ref(route.query.search || '');

const fetchCategories = async () => {
  try {
    const res = await api.get('/categories');
    categories.value = res.data;
  } catch (error) {
    console.error('Failed to fetch categories', error);
    categories.value = [
      { id: 1, name: '文学', sortOrder: 1, createdAt: '' },
      { id: 2, name: '科技', sortOrder: 2, createdAt: '' },
      { id: 3, name: '历史', sortOrder: 3, createdAt: '' },
      { id: 4, name: '经济', sortOrder: 4, createdAt: '' },
      { id: 5, name: '艺术', sortOrder: 5, createdAt: '' },
    ];
  }
};

const fetchBooks = async () => {
  isLoading.value = true;
  try {
    const params = {
      page: currentPage.value,
      limit: 12,
    };
    if (selectedCategory.value) params.categoryId = selectedCategory.value;
    if (searchQuery.value) params.search = searchQuery.value;

    const res = await api.get('/books', { params });
    books.value = res.data.list;
    total.value = res.data.total;
    totalPages.value = res.data.pages;
    currentPage.value = res.data.pageNum;
  } catch (error) {
    console.error('Failed to fetch books', error);
  } finally {
    isLoading.value = false;
  }
};

const handleCategoryChange = (categoryId) => {
  selectedCategory.value = categoryId.toString();
  currentPage.value = 1;
  updateQueryParams();
};

const handlePageChange = (page) => {
  if (page < 1 || page > totalPages.value) return;
  currentPage.value = page;
  updateQueryParams();
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const updateQueryParams = () => {
  router.push({
    query: {
      ...route.query,
      categoryId: selectedCategory.value || undefined,
      page: currentPage.value,
    }
  });
};

const clearSearch = () => {
  searchQuery.value = '';
  currentPage.value = 1;
  router.push({ query: { ...route.query, search: undefined } });
};

watch(() => route.query, (newQuery) => {
  selectedCategory.value = newQuery.categoryId || '';
  searchQuery.value = newQuery.search || '';
  currentPage.value = Number(newQuery.page) || 1;
  fetchBooks();
});

onMounted(() => {
  fetchCategories();
  fetchBooks();
});
</script>

<template>
  <div class="flex flex-col lg:flex-row gap-8">
    <!-- Sidebar Filters -->
    <aside class="w-full lg:w-72 flex-shrink-0 space-y-8">
      <!-- Categories -->
      <div class="bg-white rounded-2xl shadow-card border border-gray-100 p-6 sticky top-24 transition-all duration-300">
        <div class="flex items-center gap-2 mb-6 pb-4 border-b border-gray-100">
          <div class="bg-blue-50 p-2 rounded-lg text-primary">
            <Filter class="h-5 w-5" />
          </div>
          <h2 class="text-lg font-bold text-gray-800">图书分类</h2>
        </div>
        
        <ul class="space-y-2">
          <li>
            <button 
              @click="handleCategoryChange('')"
              class="w-full text-left px-4 py-3 rounded-xl transition-all duration-200 flex items-center justify-between group"
              :class="!selectedCategory ? 'bg-primary text-white shadow-md' : 'text-gray-600 hover:bg-gray-50 hover:text-primary'"
            >
              <span class="font-medium">全部图书</span>
              <span v-if="!selectedCategory" class="bg-white/20 px-2 py-0.5 rounded text-xs">全部</span>
            </button>
          </li>
          <li v-for="category in categories" :key="category.id">
            <button 
              @click="handleCategoryChange(category.id)"
              class="w-full text-left px-4 py-3 rounded-xl transition-all duration-200 flex items-center justify-between group"
              :class="selectedCategory == String(category.id) ? 'bg-primary text-white shadow-md' : 'text-gray-600 hover:bg-gray-50 hover:text-primary'"
            >
              <span class="font-medium">{{ category.name }}</span>
            </button>
          </li>
        </ul>
      </div>
    </aside>

    <!-- Main Content -->
    <div class="flex-grow min-w-0">
      <!-- Header -->
      <div class="bg-white rounded-2xl shadow-sm border border-gray-100 p-6 mb-8 flex flex-col sm:flex-row justify-between items-center gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-800 flex items-center gap-2">
            <span v-if="searchQuery">"{{ searchQuery }}" 的搜索结果</span>
            <span v-else>所有图书</span>
          </h1>
          <p class="text-gray-500 text-sm mt-1">
            共找到 <span class="font-semibold text-primary">{{ total }}</span> 本相关图书
          </p>
        </div>
        
        <div v-if="searchQuery" class="flex items-center">
          <button 
            @click="clearSearch"
            class="flex items-center gap-2 text-sm text-gray-500 hover:text-red-500 bg-gray-50 hover:bg-red-50 px-3 py-1.5 rounded-full transition-colors"
          >
            <X class="h-4 w-4" />
            清除搜索
          </button>
        </div>
      </div>

      <!-- Loading State -->
      <div v-if="isLoading" class="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-6">
        <div v-for="i in 8" :key="i" class="bg-white aspect-[3/4] rounded-xl animate-pulse bg-gray-200"></div>
      </div>

      <!-- Book Grid -->
      <div v-else-if="books.length > 0" class="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-6">
        <BookCard v-for="book in books" :key="book.id" :book="book" />
      </div>

      <!-- Empty State -->
      <div v-else class="text-center py-24 bg-white rounded-2xl border border-gray-100 shadow-sm">
        <div class="bg-gray-50 w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-6">
          <Search class="h-10 w-10 text-gray-400" />
        </div>
        <h3 class="text-xl font-bold text-gray-800 mb-2">没有找到相关图书</h3>
        <p class="text-gray-500 mb-6 max-w-md mx-auto">尝试调整搜索关键词或清除筛选条件，浏览更多精彩内容。</p>
        <button 
          @click="handleCategoryChange(''); clearSearch()"
          class="inline-flex items-center px-6 py-3 bg-primary text-white rounded-full hover:bg-primary-light transition-colors font-medium shadow-md hover:shadow-lg"
        >
          查看所有图书
        </button>
      </div>

      <!-- Pagination -->
      <div v-if="totalPages > 1" class="mt-12 flex justify-center items-center gap-2">
        <button 
          @click="handlePageChange(currentPage - 1)"
          :disabled="currentPage === 1"
          class="w-10 h-10 rounded-full flex items-center justify-center border border-gray-200 bg-white text-gray-600 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          <ChevronLeft class="h-5 w-5" />
        </button>

        <div class="flex gap-2">
          <button 
            v-for="page in totalPages" 
            :key="page"
            @click="handlePageChange(page)"
            class="w-10 h-10 rounded-full flex items-center justify-center transition-all duration-200 font-medium"
            :class="currentPage === page ? 'bg-primary text-white shadow-md scale-110' : 'bg-white text-gray-600 hover:bg-gray-50 border border-gray-200'"
          >
            {{ page }}
          </button>
        </div>

        <button 
          @click="handlePageChange(currentPage + 1)"
          :disabled="currentPage === totalPages"
          class="w-10 h-10 rounded-full flex items-center justify-center border border-gray-200 bg-white text-gray-600 hover:bg-gray-50 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
        >
          <ChevronRight class="h-5 w-5" />
        </button>
      </div>
    </div>
  </div>
</template>
