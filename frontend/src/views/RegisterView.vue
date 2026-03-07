<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import api from "../api";
import { Mail, Lock, User, UserPlus, AlertCircle } from "lucide-vue-next";

const router = useRouter();

const name = ref("");
const email = ref("");
const password = ref("");
const confirmPassword = ref("");
const error = ref("");
const isLoading = ref(false);

const handleRegister = async () => {
  if (password.value !== confirmPassword.value) {
    error.value = "两次输入的密码不一致";
    return;
  }

  isLoading.value = true;
  error.value = "";

  try {
    await api.post("/auth/register", {
      name: name.value,
      email: email.value,
      password: password.value,
    });
    router.push("/login");
  } catch (err) {
    console.error("Registration failed", err);
    error.value = err.response?.data?.error || "注册失败，请稍后重试";
  } finally {
    isLoading.value = false;
  }
};
</script>

<template>
  <div class="max-w-md mx-auto mt-8 md:mt-16 mb-12">
    <div
      class="bg-white p-8 md:p-10 rounded-3xl shadow-xl border border-gray-100"
    >
      <div class="text-center mb-8">
        <div
          class="bg-primary/10 w-16 h-16 rounded-2xl flex items-center justify-center mx-auto mb-6 transform -rotate-3 hover:-rotate-6 transition-transform duration-300"
        >
          <UserPlus class="h-8 w-8 text-primary" />
        </div>
        <h1 class="text-3xl font-bold text-gray-900 mb-2">创建新账号</h1>
        <p class="text-gray-500">立即加入，开启您的阅读之旅</p>
      </div>

      <div
        v-if="error"
        class="mb-6 bg-red-50 border border-red-200 text-red-600 px-4 py-3 rounded-xl flex items-center text-sm animate-shake"
      >
        <AlertCircle class="h-5 w-5 mr-2 flex-shrink-0" />
        {{ error }}
      </div>

      <form @submit.prevent="handleRegister" class="space-y-5">
        <div class="space-y-1">
          <label class="block text-sm font-semibold text-gray-700 ml-1"
            >您的姓名</label
          >
          <div class="relative group">
            <User
              class="absolute left-4 top-3.5 h-5 w-5 text-gray-400 group-focus-within:text-primary transition-colors"
            />
            <input
              v-model="name"
              type="text"
              required
              class="w-full pl-12 pr-4 py-3 border border-gray-200 bg-gray-50 rounded-xl focus:ring-2 focus:ring-primary/20 focus:border-primary focus:bg-white transition-all duration-200 outline-none"
              placeholder="例如：张三"
            />
          </div>
        </div>

        <div class="space-y-1">
          <label class="block text-sm font-semibold text-gray-700 ml-1"
            >电子邮箱</label
          >
          <div class="relative group">
            <Mail
              class="absolute left-4 top-3.5 h-5 w-5 text-gray-400 group-focus-within:text-primary transition-colors"
            />
            <input
              v-model="email"
              type="email"
              required
              class="w-full pl-12 pr-4 py-3 border border-gray-200 bg-gray-50 rounded-xl focus:ring-2 focus:ring-primary/20 focus:border-primary focus:bg-white transition-all duration-200 outline-none"
              placeholder="您的邮箱地址"
            />
          </div>
        </div>

        <div class="space-y-1">
          <label class="block text-sm font-semibold text-gray-700 ml-1"
            >设置密码</label
          >
          <div class="relative group">
            <Lock
              class="absolute left-4 top-3.5 h-5 w-5 text-gray-400 group-focus-within:text-primary transition-colors"
            />
            <input
              v-model="password"
              type="password"
              required
              class="w-full pl-12 pr-4 py-3 border border-gray-200 bg-gray-50 rounded-xl focus:ring-2 focus:ring-primary/20 focus:border-primary focus:bg-white transition-all duration-200 outline-none"
              placeholder="••••••••"
            />
          </div>
        </div>

        <div class="space-y-1">
          <label class="block text-sm font-semibold text-gray-700 ml-1"
            >确认密码</label
          >
          <div class="relative group">
            <Lock
              class="absolute left-4 top-3.5 h-5 w-5 text-gray-400 group-focus-within:text-primary transition-colors"
            />
            <input
              v-model="confirmPassword"
              type="password"
              required
              class="w-full pl-12 pr-4 py-3 border border-gray-200 bg-gray-50 rounded-xl focus:ring-2 focus:ring-primary/20 focus:border-primary focus:bg-white transition-all duration-200 outline-none"
              placeholder="••••••••"
            />
          </div>
        </div>

        <button
          type="submit"
          :disabled="isLoading"
          class="w-full bg-primary text-white py-3.5 rounded-xl font-bold hover:bg-blue-800 transition-all duration-200 shadow-lg hover:shadow-blue-200 disabled:opacity-70 disabled:cursor-not-allowed flex items-center justify-center mt-6 group"
        >
          <span
            v-if="isLoading"
            class="h-5 w-5 border-2 border-white border-t-transparent rounded-full animate-spin mr-2"
          ></span>
          <span v-else class="flex items-center">
            注册账号
            <UserPlus
              class="h-4 w-4 ml-2 group-hover:translate-x-1 transition-transform"
            />
          </span>
        </button>
      </form>

      <div
        class="mt-8 pt-6 border-t border-gray-100 text-center text-sm text-gray-500"
      >
        已有账号？
        <RouterLink
          to="/login"
          class="text-primary font-bold hover:text-blue-700 hover:underline transition-all"
          >直接登录</RouterLink
        >
      </div>
    </div>
  </div>
</template>

<style scoped>
@keyframes shake {
  0%,
  100% {
    transform: translateX(0);
  }
  10%,
  30%,
  50%,
  70%,
  90% {
    transform: translateX(-4px);
  }
  20%,
  40%,
  60%,
  80% {
    transform: translateX(4px);
  }
}
.animate-shake {
  animation: shake 0.4s cubic-bezier(0.36, 0.07, 0.19, 0.97) both;
}
</style>
