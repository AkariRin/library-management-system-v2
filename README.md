# Library Management System V2

## 1. 系统简介

Library Management System V2 是一个现代化的图书馆管理系统，采用前后端分离架构。该系统提供图书管理、用户认证、借阅管理等核心功能，支持管理员和普通用户两种角色。

**技术栈：**
- **后端：** Java + Spring Boot + Gradle
- **前端：** Vue 3 + TypeScript + Vite
- **数据库：** 支持关系型数据库
- **缓存：** Redis（会话管理）
- **容器化：** Docker + Docker Compose

---

## 2. 系统分析

### 2.1 用例图与主要流程

**主要用户角色：**
- **管理员：** 管理图书、用户、借阅记录等
- **普通用户：** 查看图书、借阅图书、管理个人信息

**主要业务流程：**
1. 用户认证流程：注册 → 登录 → 会话管理
2. 图书管理流程：创建 → 查询 → 编辑 → 删除
3. 借阅管理流程：查询可用图书 → 提交借阅申请 → 审批 → 归还管理

---

## 3. 系统设计

### 3.1 概要设计

#### 3.1.1 系统架构图

系统采用经典的三层架构：

```mermaid
graph TD
    A["🎨 前端层<br/>Vue 3 + TypeScript<br/>━━━━━━<br/>• UI Components<br/>• Vue Router<br/>• Pinia Store"] --HTTP/REST API--> B["🔌 API 层<br/>Spring Boot Controller<br/>━━━━━━<br/>• 请求处理<br/>• 参数验证<br/>• 异常处理"]
    B --> C["⚙️ 业务逻辑层<br/>Service<br/>━━━━━━<br/>• 业务规则实现<br/>• 权限控制<br/>• 事务管理"]
    C --> D["💾 数据持久层<br/>Repository + ORM<br/>━━━━━━<br/>• 数据访问<br/>• 缓存管理"]
    D --> E["🗄️ 数据库 + Redis 缓存"]
    
    style A fill:#e1f5ff
    style B fill:#f3e5f5
    style C fill:#f1f8e9
    style D fill:#fff3e0
    style E fill:#fce4ec
```

#### 3.1.2 核心类设计

**主要实体类：**
- `User` - 用户信息
- `Book` - 图书信息
- `BorrowRecord` - 借阅记录
- `Admin` - 管理员信息

**主要业务层类：**
- `AuthService` - 认证服务
- `UserService` - 用户管理服务
- `BookService` - 图书管理服务
- `BorrowService` - 借阅管理服务
- `AdminService` - 管理员服务

#### 3.1.3 主要业务时序图

**借阅图书时序：**

```mermaid
sequenceDiagram
    participant User as 用户
    participant Frontend as 前端
    participant Controller as Controller
    participant Service as Service
    participant Repository as Repository
    participant Database as 数据库
    
    User ->>+ Frontend: 1. 借书申请
    Frontend ->>+ Controller: 2. API 请求
    Controller ->>+ Service: 3. 校验权限
    Service ->>+ Repository: 4. 查询库存
    Repository ->>+ Database: 5. SELECT 查询
    Database -->>- Repository: 6. 返回结果
    Repository -->>- Service: 7. 库存数据
    Service ->>+ Repository: 8. 创建借阅记录
    Repository ->>+ Database: 9. INSERT 操作
    Database -->>- Repository: 10. 操作成功
    Repository -->>- Service: 11. 返回记录信息
    Service -->>- Controller: 12. 成功响应
    Controller -->>- Frontend: 13. 返回数据
    Frontend -->>- User: 14. 界面更新
```

### 3.2 数据库设计

#### 3.2.1 ER 图（实体关系模型）

```mermaid
erDiagram
    USERS ||--o{ BORROW_RECORDS : "借阅"
    BOOKS ||--o{ BORROW_RECORDS : "被借阅"
    
    USERS {
        int user_id PK
        string username UK
        string email UK
        string password
        datetime created_at
        datetime updated_at
    }
    
    BOOKS {
        int book_id PK
        string title
        string author
        string isbn UK
        int quantity
        int available
        datetime created_at
        datetime updated_at
    }
    
    BORROW_RECORDS {
        int borrow_id PK
        int user_id FK
        int book_id FK
        datetime borrow_date
        datetime return_date
        string status
        datetime created_at
        datetime updated_at
    }
    
    ADMIN_USERS {
        int admin_id PK
        string username UK
        string password
        datetime created_at
        datetime updated_at
    }
```

#### 3.2.2 主要数据表结构

**users 表：**
```sql
CREATE TABLE users (
  user_id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**books 表：**
```sql
CREATE TABLE books (
  book_id INT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(200) NOT NULL,
  author VARCHAR(100) NOT NULL,
  isbn VARCHAR(20) UNIQUE,
  quantity INT NOT NULL DEFAULT 0,
  available INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

**borrow_records 表：**
```sql
CREATE TABLE borrow_records (
  borrow_id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  book_id INT NOT NULL,
  borrow_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  return_date TIMESTAMP,
  status ENUM('borrowed', 'returned', 'overdue') DEFAULT 'borrowed',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (book_id) REFERENCES books(book_id)
);
```

**admin_users 表：**
```sql
CREATE TABLE admin_users (
  admin_id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

---

## 4. 总结

Library Management System V2 是一个功能完整、架构清晰的图书馆管理系统。通过采用现代化的技术栈（Spring Boot + Vue 3）和标准的三层架构设计，系统具有良好的可扩展性和可维护性。

**系统优势：**
- ✅ 前后端分离，易于独立开发和部署
- ✅ 采用 Redis 缓存提升性能
- ✅ Docker 容器化支持快速部署
- ✅ 完善的权限管理和安全认证
- ✅ 清晰的数据库设计和业务逻辑

**后续可扩展方向：**
- 图书推荐算法
- 积分兑换系统
- 消息通知服务
- 报表统计分析
- 性能监控和优化
