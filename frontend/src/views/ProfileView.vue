<script setup>
import { ref, onMounted } from "vue";
import { useAuthStore } from "../stores/auth";
import api from "../api";
import BookCard from "../components/BookCard.vue";
import {
  User as UserIcon,
  Clock,
  Mail,
  Calendar,
  Edit2,
} from "lucide-vue-next";

const authStore = useAuthStore();
const history = ref([]);
const isLoading = ref(true);

const fetchHistory = async () => {
  try {
    const res = await api.get("/user/history");
    history.value = res.data;
  } catch (error) {
    console.error("Failed to fetch history", error);
    // Mock
    history.value = [];
  } finally {
    isLoading.value = false;
  }
};

onMounted(() => {
  if (!authStore.user) {
    authStore.fetchProfile();
  }
  fetchHistory();
});
</script>

<template>
  <div class="max-w-5xl mx-auto space-y-10">
    <!-- User Profile Card -->
    <div
      class="bg-white rounded-3xl shadow-sm border border-gray-100 p-8 md:p-10 flex flex-col md:flex-row items-center md:items-start gap-8 relative overflow-hidden"
    >
      <!-- Decorative background -->
      <div
        class="absolute top-0 right-0 w-64 h-64 bg-primary/5 rounded-full blur-3xl -translate-y-1/2 translate-x-1/2"
      ></div>

      <div class="relative group">
        <div
          class="w-32 h-32 md:w-40 md:h-40 bg-gray-50 rounded-full flex items-center justify-center text-gray-400 flex-shrink-0 border-4 border-white shadow-lg overflow-hidden"
        >
          <img
            v-if="authStore.user?.avatarUrl"
            :src="authStore.user.avatarUrl"
            alt="Avatar"
            class="w-full h-full object-cover"
          />
          <UserIcon v-else class="h-16 w-16 text-gray-300" />
        </div>
        <button
          class="absolute bottom-2 right-2 bg-white p-2 rounded-full shadow-md text-gray-600 hover:text-primary transition-colors border border-gray-100"
        >
          <Edit2 class="h-4 w-4" />
        </button>
      </div>

      <div class="flex-grow text-center md:text-left z-10">
        <h1 class="text-3xl md:text-4xl font-bold text-gray-900 mb-2">
          {{ authStore.user?.name || "用户" }}
        </h1>
        <p class="text-gray-500 mb-6 max-w-md mx-auto md:mx-0">
          欢迎回到您的个人中心，这里记录了您的阅读点滴。
        </p>

        <div
          class="flex flex-col md:flex-row gap-4 md:gap-8 justify-center md:justify-start"
        >
          <div class="flex items-center gap-3 bg-gray-50 px-4 py-2 rounded-xl">
            <div class="bg-blue-100 p-1.5 rounded-lg text-primary">
              <Mail class="h-4 w-4" />
            </div>
            <span class="text-gray-700 font-medium">{{
              authStore.user?.email
            }}</span>
          </div>
          <div
            class="flex items-center gap-3 bg-gray-50 px-4 py-2 rounded-xl"
            v-if="authStore.user?.createdAt"
          >
            <div class="bg-orange-100 p-1.5 rounded-lg text-accent">
              <Calendar class="h-4 w-4" />
            </div>
            <span class="text-gray-700 font-medium"
              >加入于
              {{
                new Date(authStore.user.createdAt).toLocaleDateString("zh-CN")
              }}</span
            >
          </div>
        </div>
      </div>
    </div>

    <!-- Reading History -->
    <div
      class="bg-white rounded-3xl shadow-sm border border-gray-100 p-8 md:p-10"
    >
      <div class="flex items-center gap-3 mb-8 pb-4 border-b border-gray-100">
        <div class="bg-primary/10 p-2 rounded-xl text-primary">
          <Clock class="h-6 w-6" />
        </div>
        <h2 class="text-2xl font-bold text-gray-800">阅读历史</h2>
      </div>

      <div
        v-if="isLoading"
        class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6"
      >
        <div
          v-for="i in 4"
          :key="i"
          class="bg-white aspect-[3/4] rounded-xl animate-pulse bg-gray-100"
        ></div>
      </div>

      <div
        v-else-if="history.length > 0"
        class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6"
      >
        <BookCard v-for="book in history" :key="book.id" :book="book" />
      </div>

      <div
        v-else
        class="text-center py-16 bg-gray-50 rounded-2xl border border-dashed border-gray-200"
      >
        <div
          class="bg-white w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4 shadow-sm"
        >
          <Clock class="h-8 w-8 text-gray-300" />
        </div>
        <h3 class="text-lg font-semibold text-gray-700 mb-1">暂无阅读记录</h3>
        <p class="text-gray-500">您还没有开始阅读任何书籍，快去书库看看吧。</p>
        <RouterLink
          to="/books"
          class="inline-block mt-4 text-primary font-medium hover:underline"
          >去浏览图书</RouterLink
        >
      </div>
    </div>
  </div>
</template>
