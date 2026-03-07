<template>
  <div class="container mx-auto p-4">
    <h1 class="text-2xl font-bold mb-4">管理员控制台</h1>
    
    <div class="flex border-b mb-4">
      <button 
        class="py-2 px-4 focus:outline-none" 
        :class="{ 'border-b-2 border-blue-500 font-bold': activeTab === 'books' }"
        @click="activeTab = 'books'"
      >
        图书管理
      </button>
      <button 
        class="py-2 px-4 focus:outline-none" 
        :class="{ 'border-b-2 border-blue-500 font-bold': activeTab === 'users' }"
        @click="activeTab = 'users'"
      >
        用户管理
      </button>
    </div>

    <!-- Book Management -->
    <div v-if="activeTab === 'books'">
      <div class="mb-4 p-4 border rounded shadow">
        <h2 class="text-xl font-bold mb-2">上传新书</h2>
        <form @submit.prevent="uploadBook" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-700">标题</label>
            <input v-model="newBook.title" type="text" required class="mt-1 block w-full border rounded p-2" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">作者</label>
            <input v-model="newBook.author" type="text" required class="mt-1 block w-full border rounded p-2" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">ISBN</label>
            <input v-model="newBook.isbn" type="text" required class="mt-1 block w-full border rounded p-2" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">分类ID</label>
            <input v-model="newBook.categoryId" type="number" required class="mt-1 block w-full border rounded p-2" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">描述</label>
            <textarea v-model="newBook.description" required class="mt-1 block w-full border rounded p-2"></textarea>
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">封面图片</label>
            <input type="file" @change="handleCoverUpload" accept="image/*" class="mt-1 block w-full" />
          </div>
          <div>
            <label class="block text-sm font-medium text-gray-700">图书文件 (PDF/EPUB/TXT)</label>
            <input type="file" @change="handleFileUpload" accept=".pdf,.epub,.txt" class="mt-1 block w-full" required />
          </div>
          <button type="submit" class="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">上传图书</button>
        </form>
      </div>

      <div class="mt-8">
        <h2 class="text-xl font-bold mb-4">图书列表</h2>
        <table class="min-w-full bg-white border">
          <thead>
            <tr>
              <th class="py-2 px-4 border-b">ID</th>
              <th class="py-2 px-4 border-b">标题</th>
              <th class="py-2 px-4 border-b">作者</th>
              <th class="py-2 px-4 border-b">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="book in books" :key="book.id">
              <td class="py-2 px-4 border-b text-center">{{ book.id }}</td>
              <td class="py-2 px-4 border-b">{{ book.title }}</td>
              <td class="py-2 px-4 border-b">{{ book.author }}</td>
              <td class="py-2 px-4 border-b text-center">
                <button @click="deleteBook(book.id)" class="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 text-sm">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- User Management -->
    <div v-if="activeTab === 'users'">
      <h2 class="text-xl font-bold mb-4">用户列表</h2>
      <table class="min-w-full bg-white border">
        <thead>
          <tr>
            <th class="py-2 px-4 border-b">ID</th>
            <th class="py-2 px-4 border-b">邮箱</th>
            <th class="py-2 px-4 border-b">用户名</th>
            <th class="py-2 px-4 border-b">角色</th>
            <th class="py-2 px-4 border-b">操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td class="py-2 px-4 border-b text-center">{{ user.id }}</td>
            <td class="py-2 px-4 border-b">{{ user.email }}</td>
            <td class="py-2 px-4 border-b">{{ user.name }}</td>
            <td class="py-2 px-4 border-b">{{ user.role }}</td>
            <td class="py-2 px-4 border-b text-center">
              <button 
                v-if="user.role !== 'ADMIN'"
                @click="deleteUser(user.id)" 
                class="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 text-sm"
              >
                注销用户
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import api from '../api';
import type { Book, User } from '../types';

const activeTab = ref('books');
const books = ref<Book[]>([]);
const users = ref<User[]>([]);

const newBook = ref({
  title: '',
  author: '',
  isbn: '',
  categoryId: 1,
  description: '',
});
const coverFile = ref<File | null>(null);
const bookFile = ref<File | null>(null);

const handleCoverUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files[0]) {
    coverFile.value = target.files[0];
  }
};

const handleFileUpload = (event: Event) => {
  const target = event.target as HTMLInputElement;
  if (target.files && target.files[0]) {
    bookFile.value = target.files[0];
  }
};

const fetchBooks = async () => {
  try {
    const response = await api.get('/books', { params: { limit: 100 } });
    books.value = response.data.records;
  } catch (error) {
    console.error('Failed to fetch books', error);
  }
};

const fetchUsers = async () => {
  try {
    const response = await api.get('/admin/users', { params: { size: 100 } });
    users.value = response.data.records;
  } catch (error) {
    console.error('Failed to fetch users', error);
  }
};

const uploadBook = async () => {
  if (!bookFile.value) {
    alert('请上传图书文件');
    return;
  }

  const formData = new FormData();
  formData.append('title', newBook.value.title);
  formData.append('author', newBook.value.author);
  formData.append('isbn', newBook.value.isbn);
  formData.append('categoryId', String(newBook.value.categoryId));
  formData.append('description', newBook.value.description);
  if (coverFile.value) {
    formData.append('cover', coverFile.value);
  }
  formData.append('file', bookFile.value);

  try {
    await api.post('/admin/books', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    alert('图书上传成功');
    // Reset form
    newBook.value = { title: '', author: '', isbn: '', categoryId: 1, description: '' };
    coverFile.value = null;
    bookFile.value = null;
    // Refresh list
    fetchBooks();
  } catch (error: any) {
    console.error('Failed to upload book', error);
    // Display specific error message from backend if available
    if (error.response && error.response.data) {
      const data = error.response.data;
      if (typeof data === 'object') {
        // Try to find common error fields
        const msg = data.error || data.message || JSON.stringify(data);
        alert(`上传失败: ${msg}`);
      } else {
        alert(`上传失败: ${data}`);
      }
    } else {
      alert('上传失败');
    }
  }
};

const deleteBook = async (id: number) => {
  if (!confirm('确定要删除这本书吗？')) return;
  try {
    await api.delete(`/admin/books/${id}`);
    books.value = books.value.filter(b => b.id !== id);
  } catch (error) {
    console.error('Failed to delete book', error);
    alert('删除失败');
  }
};

const deleteUser = async (id: number) => {
  if (!confirm('确定要注销该用户吗？')) return;
  try {
    await api.delete(`/admin/users/${id}`);
    users.value = users.value.filter(u => u.id !== id);
  } catch (error) {
    console.error('Failed to delete user', error);
    alert('删除失败');
  }
};

onMounted(() => {
  fetchBooks();
  fetchUsers();
});
</script>
