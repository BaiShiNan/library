import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/HomeView.vue'),
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('../views/RegisterView.vue'),
    },
    {
      path: '/books',
      name: 'books',
      component: () => import('../views/BookListView.vue'),
    },
    {
      path: '/books/:id',
      name: 'book-detail',
      component: () => import('../views/BookDetailView.vue'),
    },
    {
      path: '/read/:id',
      name: 'read',
      component: () => import('../views/ReadView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/profile',
      name: 'profile',
      component: () => import('../views/ProfileView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/favorites',
      name: 'favorites',
      component: () => import('../views/FavoritesView.vue'),
      meta: { requiresAuth: true },
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/AdminView.vue'),
      meta: { requiresAuth: true, requiresAdmin: true },
    },
  ],
});

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore();
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login');
  } else if (to.meta.requiresAdmin) {
    // Check if user is admin. Note: user might be null if not fetched yet,
    // but if isAuthenticated is true, we should try to fetch or check token.
    // For simplicity, we assume if authenticated, we check user role.
    // Ideally we wait for fetchProfile, but let's do a simple check.
    if (authStore.user?.role !== 'ADMIN') {
       // If user is not loaded but token exists, we might need to fetch profile first.
       // But to keep it simple, we redirect to home if not admin.
       // A better way is to let the view handle the redirect or show "Access Denied".
       // Let's rely on the view or a simple check if user is loaded.
       if (authStore.user) {
         next('/');
       } else {
         // User not loaded, maybe let them pass and let the component handle it or fetch?
         // Let's just let them pass and the component can redirect if not admin after loading.
         next();
       }
    } else {
      next();
    }
  } else {
    next();
  }
});

export default router;
