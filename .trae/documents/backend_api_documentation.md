# 后端 API 接口文档

## 1. 认证接口 (Authentication)

### 1.1 用户注册

*   **URL**: `/api/auth/register`
*   **Method**: `POST`
*   **Description**: 注册新用户。
*   **Request Body**:
    ```json
    {
      "email": "user@example.com",
      "password": "password123",
      "name": "User Name",
      "avatarUrl": "http://example.com/avatar.jpg" // 可选
    }
    ```
*   **Response Body (Success 200)**:
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiJ9...",
      "user": {
        "id": 1,
        "email": "user@example.com",
        "name": "User Name",
        "role": "USER",
        "avatarUrl": "http://example.com/avatar.jpg",
        "createdAt": "2023-10-27T10:00:00",
        "updatedAt": "2023-10-27T10:00:00"
      }
    }
    ```
*   **Response Body (Error 400)**:
    ```json
    {
      "error": "Email already exists"
    }
    ```

### 1.2 用户登录

*   **URL**: `/api/auth/login`
*   **Method**: `POST`
*   **Description**: 用户登录获取 Token。
*   **Request Body**:
    ```json
    {
      "email": "user@example.com",
      "password": "password123"
    }
    ```
*   **Response Body (Success 200)**:
    ```json
    {
      "token": "eyJhbGciOiJIUzI1NiJ9...",
      "user": {
        "id": 1,
        "email": "user@example.com",
        "name": "User Name",
        "role": "USER",
        "avatarUrl": "http://example.com/avatar.jpg",
        "createdAt": "2023-10-27T10:00:00",
        "updatedAt": "2023-10-27T10:00:00"
      }
    }
    ```
*   **Response Body (Error 401)**:
    ```json
    {
      "error": "User not found" // or "Invalid password" (though specific message depends on implementation security)
    }
    ```

---

## 2. 图书接口 (Books)

**注意**: 所有非 GET 请求需要 `Authorization: Bearer <token>` 头。

### 2.1 获取图书列表

*   **URL**: `/api/books`
*   **Method**: `GET`
*   **Description**: 获取图书列表，支持分页、分类筛选和搜索。
*   **Query Parameters**:
    *   `page`: (Optional) 页码，默认为 1。
    *   `limit`: (Optional) 每页数量，默认为 20。
    *   `categoryId`: (Optional) 分类 ID。
    *   `search`: (Optional) 搜索关键词（匹配标题或作者）。
*   **Response Body (Success 200)**:
    ```json
    {
      "records": [
        {
          "id": 1,
          "title": "Book Title",
          "author": "Author Name",
          "isbn": "1234567890",
          "categoryId": 1,
          "description": "Book description...",
          "coverUrl": "http://example.com/cover.jpg",
          "fileUrl": "http://example.com/book.pdf",
          "rating": 4.5,
          "pageCount": 300,
          "publishDate": "2023-01-01",
          "createdAt": "2023-10-27T10:00:00"
        }
      ],
      "total": 100,
      "size": 20,
      "current": 1,
      "pages": 5
    }
    ```

### 2.2 获取图书详情

*   **URL**: `/api/books/{id}`
*   **Method**: `GET`
*   **Description**: 根据 ID 获取图书详细信息。
*   **Path Parameters**:
    *   `id`: 图书 ID。
*   **Response Body (Success 200)**:
    ```json
    {
      "id": 1,
      "title": "Book Title",
      "author": "Author Name",
      "isbn": "1234567890",
      "categoryId": 1,
      "description": "Book description...",
      "coverUrl": "http://example.com/cover.jpg",
      "fileUrl": "http://example.com/book.pdf",
      "rating": 4.5,
      "pageCount": 300,
      "publishDate": "2023-01-01",
      "createdAt": "2023-10-27T10:00:00"
    }
    ```
*   **Response Body (Error 404)**: Not Found if book doesn't exist.

### 2.3 收藏/取消收藏图书

*   **URL**: `/api/books/{id}/favorite`
*   **Method**: `POST`
*   **Headers**: `Authorization: Bearer <token>`
*   **Description**: 切换图书的收藏状态（已收藏则取消，未收藏则添加）。
*   **Path Parameters**:
    *   `id`: 图书 ID。
*   **Response Body (Success 200)**:
    ```json
    "Added to favorites" // or "Removed from favorites"
    ```

---

## 3. 用户接口 (User)

**注意**: 所有请求需要 `Authorization: Bearer <token>` 头。

### 3.1 获取个人资料

*   **URL**: `/api/user/profile`
*   **Method**: `GET`
*   **Description**: 获取当前登录用户的详细信息。
*   **Response Body (Success 200)**:
    ```json
    {
      "id": 1,
      "email": "user@example.com",
      "name": "User Name",
      "role": "USER",
      "avatarUrl": "http://example.com/avatar.jpg",
      "createdAt": "2023-10-27T10:00:00",
      "updatedAt": "2023-10-27T10:00:00"
    }
    ```

### 3.2 获取阅读历史

*   **URL**: `/api/user/history`
*   **Method**: `GET`
*   **Description**: 获取当前用户的阅读历史记录（返回图书列表）。
*   **Response Body (Success 200)**:
    ```json
    [
      {
        "id": 1,
        "title": "Read Book Title",
        "author": "Author Name",
        // ... other book fields
      },
      // ... more books
    ]
    ```

### 3.3 获取收藏列表

*   **URL**: `/api/user/favorites`
*   **Method**: `GET`
*   **Description**: 获取当前用户收藏的图书列表。
*   **Response Body (Success 200)**:
    ```json
    [
      {
        "id": 2,
        "title": "Favorite Book Title",
        "author": "Author Name",
        // ... other book fields
      },
      // ... more books
    ]
    ```

---

## 4. 分类接口 (Categories)

### 4.1 获取所有分类

*   **URL**: `/api/categories`
*   **Method**: `GET`
*   **Description**: 获取所有图书分类列表。
*   **Response Body (Success 200)**:
    ```json
    [
      {
        "id": 1,
        "name": "文学",
        "description": "小说、诗歌、散文等文学作品",
        "sortOrder": 1,
        "createdAt": "2023-10-27T10:00:00"
      },
      {
        "id": 2,
        "name": "科技",
        "description": "计算机、工程、科学等科技类图书",
        "sortOrder": 2,
        "createdAt": "2023-10-27T10:00:00"
      }
      // ... more categories
    ]
    ```
