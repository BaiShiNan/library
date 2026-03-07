<script setup>
import { onMounted } from 'vue';
import { RouterView } from 'vue-router';
import NavBar from './components/NavBar.vue';
import { useAuthStore } from './stores/auth';

const authStore = useAuthStore();

onMounted(() => {
  if (authStore.isAuthenticated) {
    authStore.fetchProfile();
  }
});
</script>

<template>
  <div class="min-h-screen bg-secondary flex flex-col font-sans text-gray-800 antialiased">
    <NavBar />
    <main class="flex-grow container mx-auto px-4 py-8 md:py-12">
      <RouterView />
    </main>
    <footer class="bg-white border-t border-gray-200 py-8 text-center text-gray-500 text-sm shadow-inner">
      <div class="container mx-auto">
        <p>&copy; 2026 数字图书馆 Digital Library. 保留所有权利.</p>
        <p class="mt-2 text-xs text-gray-400">致力于为读者提供优质的在线阅读体验</p>
      </div>
    </footer>
  </div>
</template>
