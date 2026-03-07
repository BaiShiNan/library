import { defineStore } from "pinia";
import { ref, computed } from "vue";
import api from "../api";

export const useAuthStore = defineStore("auth", () => {
  const user = ref(null);
  const token = ref(localStorage.getItem("token"));

  const isAuthenticated = computed(() => !!token.value);

  function setAuth(data) {
    user.value = data.user;
    token.value = data.token;
    localStorage.setItem("token", data.token);
  }

  function logout() {
    user.value = null;
    token.value = null;
    localStorage.removeItem("token");
  }

  async function fetchProfile() {
    if (!token.value) return;
    try {
      const response = await api.get("/user/profile");
      user.value = response.data;
    } catch (error) {
      console.error("Failed to fetch profile", error);
      logout();
    }
  }

  return {
    user,
    token,
    isAuthenticated,
    setAuth,
    logout,
    fetchProfile,
  };
});
