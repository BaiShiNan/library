<template>
  <div class="container mx-auto p-4">
    <h1 class="text-2xl font-bold mb-4">管理员控制台</h1>

    <div class="flex border-b mb-4">
      <button
        class="py-2 px-4 focus:outline-none transition-colors"
        :class="
          activeTab === 'books'
            ? 'border-b-2 border-primary font-bold text-primary'
            : 'text-gray-600 hover:text-primary'
        "
        @click="switchTab('books')"
      >
        图书管理
      </button>
      <button
        class="py-2 px-4 focus:outline-none transition-colors"
        :class="
          activeTab === 'users'
            ? 'border-b-2 border-primary font-bold text-primary'
            : 'text-gray-600 hover:text-primary'
        "
        @click="switchTab('users')"
      >
        用户管理
      </button>
    </div>

    <!-- Search & Actions Bar -->
    <div
      class="flex flex-col md:flex-row justify-between items-center mb-6 gap-4"
    >
      <div class="relative w-full md:w-96">
        <input
          v-model="searchQuery"
          @keyup.enter="handleSearch"
          type="text"
          :placeholder="
            activeTab === 'books' ? '搜索书名、作者...' : '搜索用户名、邮箱...'
          "
          class="w-full pl-4 pr-10 py-2 border rounded-lg focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none"
        />
        <button
          @click="handleSearch"
          class="absolute right-2 top-1/2 -translate-y-1/2 text-gray-400 hover:text-primary"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
            />
          </svg>
        </button>
      </div>

      <div class="flex gap-2 w-full md:w-auto">
        <button
          v-if="activeTab === 'books' && selectedBooks.length > 0"
          @click="batchDeleteBooks"
          class="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition-colors flex items-center gap-2"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-4 w-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
            />
          </svg>
          批量删除 ({{ selectedBooks.length }})
        </button>
        <button
          v-if="activeTab === 'users' && selectedUsers.length > 0"
          @click="batchDeleteUsers"
          class="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600 transition-colors flex items-center gap-2"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-4 w-4"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
            />
          </svg>
          批量删除 ({{ selectedUsers.length }})
        </button>
      </div>
    </div>

    <!-- Book Management -->
    <div v-if="activeTab === 'books'">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-xl font-bold text-gray-800">图书列表</h2>
        <button
          @click="showUploadForm = true"
          class="bg-primary text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors shadow-md flex items-center gap-2"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              stroke-linecap="round"
              stroke-linejoin="round"
              stroke-width="2"
              d="M12 4v16m8-8H4"
            />
          </svg>
          上传新书
        </button>
      </div>

      <!-- Upload Modal -->
      <div
        v-if="showUploadForm"
        class="fixed inset-0 bg-black/50 flex items-center justify-center z-50 p-4"
      >
        <div
          class="bg-white rounded-2xl shadow-xl w-full max-w-2xl max-h-[90vh] overflow-y-auto"
        >
          <div
            class="p-6 border-b border-gray-100 flex justify-between items-center sticky top-0 bg-white z-10"
          >
            <h2 class="text-xl font-bold text-gray-800">上传新书</h2>
            <button
              @click="showUploadForm = false"
              class="text-gray-400 hover:text-gray-600 transition-colors"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
              >
                <path
                  stroke-linecap="round"
                  stroke-linejoin="round"
                  stroke-width="2"
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            </button>
          </div>

          <div class="p-6">
            <form
              @submit.prevent="uploadBook"
              class="grid grid-cols-1 md:grid-cols-2 gap-6"
            >
              <div class="col-span-1">
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >标题</label
                >
                <input
                  v-model="newBook.title"
                  type="text"
                  required
                  class="w-full border rounded-lg p-2 focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none"
                />
              </div>
              <div class="col-span-1">
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >作者</label
                >
                <input
                  v-model="newBook.author"
                  type="text"
                  required
                  class="w-full border rounded-lg p-2 focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none"
                />
              </div>
              <div class="col-span-1">
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >ISBN</label
                >
                <input
                  v-model="newBook.isbn"
                  type="text"
                  required
                  class="w-full border rounded-lg p-2 focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none"
                />
              </div>
              <div class="col-span-1">
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >分类ID</label
                >
                <input
                  v-model="newBook.categoryId"
                  type="number"
                  required
                  class="w-full border rounded-lg p-2 focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none"
                />
              </div>
              <div class="col-span-1 md:col-span-2">
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >描述</label
                >
                <textarea
                  v-model="newBook.description"
                  required
                  class="w-full border rounded-lg p-2 focus:ring-2 focus:ring-primary/20 focus:border-primary outline-none h-24"
                ></textarea>
              </div>
              <div class="col-span-1">
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >封面图片</label
                >
                <input
                  type="file"
                  @change="handleCoverUpload"
                  accept="image/*"
                  class="w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-primary/10 file:text-primary hover:file:bg-primary/20"
                />
              </div>
              <div class="col-span-1">
                <label class="block text-sm font-medium text-gray-700 mb-1"
                  >图书文件 (PDF/EPUB/TXT)</label
                >
                <input
                  type="file"
                  @change="handleFileUpload"
                  accept=".pdf,.epub,.txt"
                  class="w-full text-sm text-gray-500 file:mr-4 file:py-2 file:px-4 file:rounded-full file:border-0 file:text-sm file:font-semibold file:bg-primary/10 file:text-primary hover:file:bg-primary/20"
                  required
                />
              </div>
              <div class="col-span-1 md:col-span-2 flex justify-end gap-3 mt-4">
                <button
                  type="button"
                  @click="showUploadForm = false"
                  class="px-6 py-2 border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
                >
                  取消
                </button>
                <button
                  type="submit"
                  class="bg-primary text-white px-6 py-2 rounded-lg hover:bg-blue-700 transition-colors shadow-md"
                >
                  上传图书
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>

      <div
        class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden"
      >
        <table class="min-w-full">
          <thead class="bg-gray-50 border-b border-gray-100">
            <tr>
              <th class="py-3 px-4 w-12 text-center">
                <input
                  type="checkbox"
                  @change="toggleAllBooks"
                  :checked="isAllBooksSelected"
                  class="rounded text-primary focus:ring-primary"
                />
              </th>
              <th
                class="py-3 px-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider w-16"
              >
                ID
              </th>
              <th
                class="py-3 px-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider w-1/3"
              >
                标题
              </th>
              <th
                class="py-3 px-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider w-1/4"
              >
                作者
              </th>
              <th
                class="py-3 px-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider"
              >
                ISBN
              </th>
              <th
                class="py-3 px-4 text-center text-xs font-semibold text-gray-500 uppercase tracking-wider w-24"
              >
                操作
              </th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr
              v-for="book in books"
              :key="book.id"
              class="hover:bg-gray-50 transition-colors"
            >
              <td class="py-3 px-4 text-center">
                <input
                  type="checkbox"
                  :value="book.id"
                  v-model="selectedBooks"
                  class="rounded text-primary focus:ring-primary"
                />
              </td>
              <td class="py-3 px-4 text-gray-500">{{ book.id }}</td>
              <td class="py-3 px-4 font-medium text-gray-900">
                {{ book.title }}
              </td>
              <td class="py-3 px-4 text-gray-600">{{ book.author }}</td>
              <td class="py-3 px-4 text-gray-500">{{ book.isbn }}</td>
              <td class="py-3 px-4 text-center">
                <button
                  @click="deleteBook(book.id)"
                  class="text-red-500 hover:text-red-700 bg-red-50 hover:bg-red-100 p-1.5 rounded-lg transition-colors"
                  title="删除"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-4 w-4"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                    />
                  </svg>
                </button>
              </td>
            </tr>
            <tr v-if="books.length === 0">
              <td colspan="6" class="py-8 text-center text-gray-500">
                暂无图书数据
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- User Management -->
    <div v-if="activeTab === 'users'">
      <div
        class="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden"
      >
        <table class="min-w-full">
          <thead class="bg-gray-50 border-b border-gray-100">
            <tr>
              <th class="py-3 px-4 w-12 text-center">
                <input
                  type="checkbox"
                  @change="toggleAllUsers"
                  :checked="isAllUsersSelected"
                  class="rounded text-primary focus:ring-primary"
                />
              </th>
              <th
                class="py-3 px-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider w-16"
              >
                ID
              </th>
              <th
                class="py-3 px-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider w-1/3"
              >
                邮箱
              </th>
              <th
                class="py-3 px-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider w-1/4"
              >
                用户名
              </th>
              <th
                class="py-3 px-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider"
              >
                角色
              </th>
              <th
                class="py-3 px-4 text-center text-xs font-semibold text-gray-500 uppercase tracking-wider w-24"
              >
                操作
              </th>
            </tr>
          </thead>
          <tbody class="divide-y divide-gray-100">
            <tr
              v-for="user in users"
              :key="user.id"
              class="hover:bg-gray-50 transition-colors"
            >
              <td class="py-3 px-4 text-center">
                <input
                  type="checkbox"
                  :value="user.id"
                  v-model="selectedUsers"
                  :disabled="user.role === 'ADMIN'"
                  class="rounded text-primary focus:ring-primary"
                />
              </td>
              <td class="py-3 px-4 text-gray-500">{{ user.id }}</td>
              <td class="py-3 px-4 font-medium text-gray-900">
                {{ user.email }}
              </td>
              <td class="py-3 px-4 text-gray-600">{{ user.name }}</td>
              <td class="py-3 px-4">
                <span
                  :class="
                    user.role === 'ADMIN'
                      ? 'bg-purple-100 text-purple-700'
                      : 'bg-gray-100 text-gray-600'
                  "
                  class="px-2 py-1 rounded-full text-xs font-medium"
                >
                  {{ user.role }}
                </span>
              </td>
              <td class="py-3 px-4 text-center">
                <button
                  v-if="user.role !== 'ADMIN'"
                  @click="deleteUser(user.id)"
                  class="text-red-500 hover:text-red-700 bg-red-50 hover:bg-red-100 p-1.5 rounded-lg transition-colors"
                  title="注销用户"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="h-4 w-4"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                  >
                    <path
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      stroke-width="2"
                      d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                    />
                  </svg>
                </button>
              </td>
            </tr>
            <tr v-if="users.length === 0">
              <td colspan="6" class="py-8 text-center text-gray-500">
                暂无用户数据
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from "vue";
import api from "../api";

const activeTab = ref("books");
const showUploadForm = ref(false);
const searchQuery = ref("");

const books = ref([]);
const users = ref([]);

// Selection
const selectedBooks = ref([]);
const selectedUsers = ref([]);

const newBook = ref({
  title: "",
  author: "",
  isbn: "",
  categoryId: 1,
  description: "",
});
const coverFile = ref(null);
const bookFile = ref(null);

const switchTab = (tab) => {
  activeTab.value = tab;
  searchQuery.value = "";
  if (tab === "books") fetchBooks();
  else fetchUsers();
};

const handleSearch = () => {
  if (activeTab.value === "books") fetchBooks();
  else fetchUsers();
};

// Computed for Select All
const isAllBooksSelected = computed(() => {
  return (
    books.value.length > 0 && selectedBooks.value.length === books.value.length
  );
});

const isAllUsersSelected = computed(() => {
  const selectableUsers = users.value.filter((u) => u.role !== "ADMIN");
  return (
    selectableUsers.length > 0 &&
    selectedUsers.value.length === selectableUsers.length
  );
});

const toggleAllBooks = (e) => {
  const checked = e.target.checked;
  if (checked) {
    selectedBooks.value = books.value.map((b) => b.id);
  } else {
    selectedBooks.value = [];
  }
};

const toggleAllUsers = (e) => {
  const checked = e.target.checked;
  if (checked) {
    selectedUsers.value = users.value
      .filter((u) => u.role !== "ADMIN")
      .map((u) => u.id);
  } else {
    selectedUsers.value = [];
  }
};

const handleCoverUpload = (event) => {
  const target = event.target;
  if (target.files && target.files[0]) {
    coverFile.value = target.files[0];
  }
};

const handleFileUpload = (event) => {
  const target = event.target;
  if (target.files && target.files[0]) {
    bookFile.value = target.files[0];
  }
};

const fetchBooks = async () => {
  try {
    const params = { limit: 100 };
    if (searchQuery.value) params.search = searchQuery.value;
    const response = await api.get("/books", { params });
    books.value = response.data.list;
    selectedBooks.value = []; // Reset selection on refresh
  } catch (error) {
    console.error("Failed to fetch books", error);
  }
};

const fetchUsers = async () => {
  try {
    const params = { size: 100 };
    if (searchQuery.value) params.search = searchQuery.value;
    const response = await api.get("/admin/users", { params });
    users.value = response.data.list;
    selectedUsers.value = []; // Reset selection on refresh
  } catch (error) {
    console.error("Failed to fetch users", error);
  }
};

const uploadBook = async () => {
  if (!bookFile.value) {
    alert("请上传图书文件");
    return;
  }

  const formData = new FormData();
  formData.append("title", newBook.value.title);
  formData.append("author", newBook.value.author);
  formData.append("isbn", newBook.value.isbn);
  formData.append("categoryId", String(newBook.value.categoryId));
  formData.append("description", newBook.value.description);
  if (coverFile.value) {
    formData.append("cover", coverFile.value);
  }
  formData.append("file", bookFile.value);

  try {
    await api.post("/admin/books", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    alert("图书上传成功");
    // Reset form
    newBook.value = {
      title: "",
      author: "",
      isbn: "",
      categoryId: 1,
      description: "",
    };
    coverFile.value = null;
    bookFile.value = null;
    // Refresh list
    fetchBooks();
    showUploadForm.value = false;
  } catch (error) {
    console.error("Failed to upload book", error);
    if (error.response && error.response.data) {
      const data = error.response.data;
      if (typeof data === "object") {
        const msg = data.error || data.message || JSON.stringify(data);
        alert(`上传失败: ${msg}`);
      } else {
        alert(`上传失败: ${data}`);
      }
    } else {
      alert("上传失败");
    }
  }
};

const deleteBook = async (id) => {
  if (!confirm("确定要删除这本书吗？")) return;
  try {
    await api.delete(`/admin/books/${id}`);
    books.value = books.value.filter((b) => b.id !== id);
    selectedBooks.value = selectedBooks.value.filter((bid) => bid !== id);
  } catch (error) {
    console.error("Failed to delete book", error);
    const data = error.response?.data;
    alert(`删除失败: ${typeof data === "string" ? data : "系统错误"}`);
  }
};

const batchDeleteBooks = async () => {
  if (!confirm(`确定要删除选中的 ${selectedBooks.value.length} 本书吗？`))
    return;
  try {
    await api.post("/admin/books/batch-delete", { ids: selectedBooks.value });
    fetchBooks();
    selectedBooks.value = [];
  } catch (error) {
    console.error("Batch delete failed", error);
    const data = error.response?.data;
    alert(`批量删除失败: ${typeof data === "string" ? data : "系统错误"}`);
  }
};

const deleteUser = async (id) => {
  if (!confirm("确定要注销该用户吗？")) return;
  try {
    await api.delete(`/admin/users/${id}`);
    users.value = users.value.filter((u) => u.id !== id);
    selectedUsers.value = selectedUsers.value.filter((uid) => uid !== id);
  } catch (error) {
    console.error("Failed to delete user", error);
    const data = error.response?.data;
    alert(`删除失败: ${typeof data === "string" ? data : "系统错误"}`);
  }
};

const batchDeleteUsers = async () => {
  if (!confirm(`确定要注销选中的 ${selectedUsers.value.length} 个用户吗？`))
    return;
  try {
    await api.post("/admin/users/batch-delete", { ids: selectedUsers.value });
    fetchUsers();
    selectedUsers.value = [];
  } catch (error) {
    console.error("Batch delete failed", error);
    const data = error.response?.data;
    alert(`批量删除失败: ${typeof data === "string" ? data : "系统错误"}`);
  }
};

onMounted(() => {
  fetchBooks();
  fetchUsers();
});
</script>
