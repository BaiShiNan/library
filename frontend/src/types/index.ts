export interface User {
  id: number;
  email: string;
  name: string;
  role: 'USER' | 'ADMIN';
  avatarUrl?: string;
  createdAt: string;
  updatedAt: string;
}

export interface Book {
  id: number;
  title: string;
  author: string;
  isbn?: string;
  categoryId: number;
  description?: string;
  coverUrl?: string;
  fileUrl: string;
  rating: number;
  pageCount: number;
  publishDate?: string;
  createdAt: string;
}

export interface Category {
  id: number;
  name: string;
  description?: string;
  sortOrder: number;
  createdAt: string;
}

export interface ReadingHistory {
  id: number;
  userId: number;
  bookId: number;
  progress: number;
  lastReadAt: string;
  // Usually API returns book details populated or we fetch separately.
  // The API doc says "returns book list" for history, so likely it returns Book objects directly or a composite.
  // But let's stick to the base model first. The API doc says 3.2 returns `[ { id: 1, title: ... } ]`.
  // So it returns Book objects.
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface PaginatedResponse<T> {
  list: T[];
  total: number;
  pageSize: number;
  pageNum: number;
  pages: number;
  // PageHelper extra fields
  size: number;
  startRow: number;
  endRow: number;
  prePage: number;
  nextPage: number;
  isFirstPage: boolean;
  isLastPage: boolean;
  hasPreviousPage: boolean;
  hasNextPage: boolean;
  navigatePages: number;
  navigatepageNums: number[];
  navigateFirstPage: number;
  navigateLastPage: number;
}
