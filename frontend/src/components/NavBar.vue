<script setup>
import { ref } from "vue";
import { RouterLink, useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";
import {
  BookOpen,
  User,
  LogOut,
  Menu,
  X,
  Library,
  Heart,
} from "lucide-vue-next";

const authStore = useAuthStore();
const router = useRouter();
const isMenuOpen = ref(false);

const handleLogout = () => {
  authStore.logout();
  router.push("/login");
};

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value;
};
</script>

<template>
  <nav
    class="bg-white/90 backdrop-blur-md shadow-sm sticky top-0 z-50 border-b border-gray-100 transition-all duration-300"
  >
    <div class="container mx-auto px-4">
      <div class="flex justify-between items-center h-16">
        <!-- Logo -->
        <RouterLink to="/" class="flex items-center space-x-3 group">
          <div
            class="bg-primary/10 p-2 rounded-lg group-hover:bg-primary/20 transition-colors duration-300"
          >
            <BookOpen class="h-6 w-6 text-primary" />
          </div>
          <span
            class="font-bold text-xl tracking-tight text-gray-800 group-hover:text-primary transition-colors duration-300"
            >数字图书馆</span
          >
        </RouterLink>

        <!-- Desktop Navigation -->
        <div class="hidden md:flex items-center space-x-1">
          <RouterLink to="/" class="nav-item" active-class="active"
            >首页</RouterLink
          >
          <RouterLink to="/books" class="nav-item" active-class="active"
            >图书库</RouterLink
          >
          <RouterLink
            v-if="authStore.user?.role === 'ADMIN'"
            to="/admin"
            class="nav-item"
            active-class="active"
            >管理后台</RouterLink
          >

          <div class="h-6 w-px bg-gray-200 mx-4"></div>

          <div class="flex items-center space-x-3">
            <template v-if="authStore.isAuthenticated">
              <RouterLink to="/favorites" class="icon-btn" title="我的收藏">
                <Heart class="h-5 w-5" />
              </RouterLink>

              <div class="relative group">
                <RouterLink
                  to="/profile"
                  class="flex items-center space-x-2 px-3 py-2 rounded-full hover:bg-gray-100 transition-colors duration-200"
                >
                  <div
                    class="h-8 w-8 rounded-full bg-primary/10 flex items-center justify-center text-primary font-medium"
                  >
                    {{ authStore.user?.name?.charAt(0).toUpperCase() || "U" }}
                  </div>
                  <span
                    class="text-sm font-medium text-gray-700 group-hover:text-primary"
                    >{{ authStore.user?.name || "用户中心" }}</span
                  >
                </RouterLink>
              </div>

              <button
                @click="handleLogout"
                class="icon-btn text-gray-500 hover:text-red-500 hover:bg-red-50"
                title="退出登录"
              >
                <LogOut class="h-5 w-5" />
              </button>
            </template>
            <template v-else>
              <RouterLink
                to="/login"
                class="px-4 py-2 text-sm font-medium text-gray-600 hover:text-primary transition-colors"
                >登录</RouterLink
              >
              <RouterLink
                to="/register"
                class="px-5 py-2 text-sm font-medium text-white bg-primary rounded-full hover:bg-primary-light shadow-md hover:shadow-lg transform hover:-translate-y-0.5 transition-all duration-200"
              >
                注册账号
              </RouterLink>
            </template>
          </div>
        </div>

        <!-- Mobile Menu Button -->
        <div class="md:hidden flex items-center">
          <button
            @click="toggleMenu"
            class="p-2 text-gray-600 hover:text-primary hover:bg-gray-100 rounded-md transition-colors focus:outline-none"
          >
            <Menu v-if="!isMenuOpen" class="h-6 w-6" />
            <X v-else class="h-6 w-6" />
          </button>
        </div>
      </div>
    </div>

    <!-- Mobile Menu -->
    <transition
      enter-active-class="transition duration-200 ease-out"
      enter-from-class="transform -translate-y-4 opacity-0"
      enter-to-class="transform translate-y-0 opacity-100"
      leave-active-class="transition duration-150 ease-in"
      leave-from-class="transform translate-y-0 opacity-100"
      leave-to-class="transform -translate-y-4 opacity-0"
    >
      <div
        v-if="isMenuOpen"
        class="md:hidden bg-white border-t border-gray-100 py-4 shadow-lg absolute w-full left-0"
      >
        <div class="container mx-auto px-4 flex flex-col space-y-2">
          <RouterLink
            to="/"
            class="mobile-nav-item"
            active-class="bg-primary/5 text-primary"
            @click="isMenuOpen = false"
          >
            <Library class="h-5 w-5 mr-3" />
            首页
          </RouterLink>
          <RouterLink
            to="/books"
            class="mobile-nav-item"
            active-class="bg-primary/5 text-primary"
            @click="isMenuOpen = false"
          >
            <BookOpen class="h-5 w-5 mr-3" />
            图书库
          </RouterLink>
          <RouterLink
            v-if="authStore.user?.role === 'ADMIN'"
            to="/admin"
            class="mobile-nav-item"
            active-class="bg-primary/5 text-primary"
            @click="isMenuOpen = false"
          >
            <Menu class="h-5 w-5 mr-3" />
            管理后台
          </RouterLink>

          <div class="border-t border-gray-100 my-2 pt-2">
            <template v-if="authStore.isAuthenticated">
              <RouterLink
                to="/favorites"
                class="mobile-nav-item"
                active-class="bg-primary/5 text-primary"
                @click="isMenuOpen = false"
              >
                <Heart class="h-5 w-5 mr-3" />
                我的收藏
              </RouterLink>
              <RouterLink
                to="/profile"
                class="mobile-nav-item"
                active-class="bg-primary/5 text-primary"
                @click="isMenuOpen = false"
              >
                <User class="h-5 w-5 mr-3" />
                个人中心
              </RouterLink>
              <button
                @click="
                  handleLogout;
                  isMenuOpen = false;
                "
                class="mobile-nav-item w-full text-left text-red-500 hover:bg-red-50"
              >
                <LogOut class="h-5 w-5 mr-3" />
                退出登录
              </button>
            </template>
            <template v-else>
              <RouterLink
                to="/login"
                class="mobile-nav-item"
                @click="isMenuOpen = false"
                >登录</RouterLink
              >
              <RouterLink
                to="/register"
                class="mobile-nav-item text-primary font-bold"
                @click="isMenuOpen = false"
                >注册账号</RouterLink
              >
            </template>
          </div>
        </div>
      </div>
    </transition>
  </nav>
</template>

<style scoped>
.nav-item {
  @apply px-4 py-2 text-sm font-medium text-gray-600 rounded-full transition-all duration-200 hover:text-primary hover:bg-gray-50;
}
.nav-item.active {
  @apply text-primary bg-primary/5 font-semibold;
}
.icon-btn {
  @apply p-2 rounded-full text-gray-500 hover:text-primary hover:bg-gray-100 transition-all duration-200;
}
.mobile-nav-item {
  @apply flex items-center px-4 py-3 text-base font-medium text-gray-600 rounded-lg hover:bg-gray-50 hover:text-primary transition-colors;
}
</style>
