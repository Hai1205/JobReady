# Hướng dẫn sử dụng Pagination System

## 📚 Tổng quan

Hệ thống pagination đã được tích hợp hoàn chỉnh cho cả **Backend** (Spring Boot) và **Frontend** (Next.js). Hệ thống hỗ trợ:

- ✅ Pagination cho DataTable (admin tables)
- ✅ Pagination cho Grid Layout (CV cards)
- ✅ Custom hook để quản lý state
- ✅ Responsive và đẹp mắt
- ✅ Tương thích với backend Spring Data Page

---

## 🎨 Components

### 1. PaginationControls

Component cơ bản cho pagination trong table view.

**Location:** `components/commons/pagination/PaginationControls.tsx`

**Props:**

```typescript
{
  paginationData: PaginationData;  // Thông tin pagination
  onPageChange: (page: number) => void;  // Callback khi đổi trang
  className?: string;
  showFirstLast?: boolean;  // Hiển thị nút Đầu/Cuối
}
```

**Features:**

- Hiển thị số trang với ellipsis
- Nút Previous/Next
- Nút First/Last (optional)
- Hiển thị thông tin "Hiển thị X-Y trong tổng số Z"

---

### 2. GridPagination

Component pagination cho grid layout (CV cards).

**Location:** `components/commons/pagination/GridPagination.tsx`

**Props:**

```typescript
{
  paginationData: PaginationData;
  onPageChange: (page: number) => void;
  onPageSizeChange?: (size: number) => void;  // Callback thay đổi số item/page
  className?: string;
  showFirstLast?: boolean;
  showPageSizeSelector?: boolean;  // Hiển thị dropdown chọn số item
  pageSizeOptions?: number[];  // [8, 12, 16, 24, 32]
}
```

**Features:**

- Tất cả features của PaginationControls
- Dropdown chọn số items per page
- Layout 2 hàng: pagination controls + info/page size selector
- Styling gradient đẹp mắt

---

### 3. DataTable (Enhanced)

DataTable component đã được nâng cấp hỗ trợ pagination.

**Location:** `components/commons/admin/adminTable/DataTable.tsx`

**New Props:**

```typescript
{
  // ... existing props
  paginationData?: PaginationData;
  onPageChange?: (page: number) => void;
  showPagination?: boolean;  // Enable/disable pagination
}
```

---

## 🪝 Hooks

### usePagination Hook

Custom hook để quản lý pagination state.

**Location:** `hooks/use-pagination.ts`

**Usage:**

```typescript
const {
  paginationState, // { page, pageSize, totalPages, totalElements, hasNext, hasPrevious }
  paginationData, // Data cho component PaginationControls
  setPage, // Set trang hiện tại
  setPageSize, // Set số items per page
  nextPage, // Chuyển sang trang tiếp theo
  prevPage, // Quay về trang trước
  goToFirst, // Về trang đầu
  goToLast, // Đến trang cuối
  updateTotalPages, // Cập nhật tổng số trang
  updateTotalElements, // Cập nhật tổng số items
  setBackendResponse, // Cập nhật từ response của backend
} = usePagination({
  initialPage: 1,
  initialPageSize: 10,
  onPageChange: (page) => console.log(page),
  onPageSizeChange: (size) => console.log(size),
});
```

---

## 📡 Backend Integration

### Backend Response Format

Backend Spring Boot trả về `Page<T>` với format:

```typescript
interface IPageResponse<T> {
  content: T[]; // Dữ liệu của trang hiện tại
  totalElements: number; // Tổng số items
  totalPages: number; // Tổng số trang
  currentPage: number; // Trang hiện tại (0-based)
  pageSize: number; // Số items per page
  hasNext: boolean; // Có trang tiếp theo?
  hasPrevious: boolean; // Có trang trước?
  first: boolean; // Là trang đầu?
  last: boolean; // Là trang cuối?
}
```

### API Request

```typescript
const response = await axiosInstance.get<IPageResponse<ICV>>(
  `/cv-service/api/cvs/user/${userId}`,
  {
    params: {
      page: page - 1, // Convert to 0-based index
      size: size,
      sort: "createdAt,desc", // Optional sorting
    },
  }
);
```

### Update Pagination State

```typescript
setBackendResponse({
  totalPages: data.totalPages,
  totalElements: data.totalElements,
  currentPage: data.currentPage + 1, // Convert to 1-based
  pageSize: data.pageSize,
  hasNext: !data.last,
  hasPrevious: !data.first,
});
```

---

## 🎯 Usage Examples

### Example 1: CV Grid với Pagination

**File:** `UserCVsSectionWithPagination.example.tsx`

```typescript
const {
  paginationState,
  paginationData,
  setPage,
  setPageSize,
  setBackendResponse,
} = usePagination({
  initialPage: 1,
  initialPageSize: 12,
});

// Fetch data
const fetchCVs = async (page: number, size: number) => {
  const response = await axiosInstance.get(`/api/cvs`, {
    params: { page: page - 1, size },
  });

  setCvs(response.data.content);
  setBackendResponse({
    totalPages: response.data.totalPages,
    totalElements: response.data.totalElements,
    currentPage: response.data.currentPage + 1,
    pageSize: response.data.pageSize,
    hasNext: !response.data.last,
    hasPrevious: !response.data.first,
  });
};

useEffect(() => {
  fetchCVs(paginationState.page, paginationState.pageSize);
}, [paginationState.page, paginationState.pageSize]);

// Render
return (
  <>
    <div className="grid grid-cols-4 gap-6">
      {cvs.map((cv) => (
        <CVCard key={cv.id} cv={cv} />
      ))}
    </div>

    <GridPagination
      paginationData={paginationData}
      onPageChange={setPage}
      onPageSizeChange={setPageSize}
      pageSizeOptions={[8, 12, 16, 24]}
    />
  </>
);
```

---

### Example 2: Admin DataTable với Pagination

**File:** `DataTableWithPagination.example.tsx`

```typescript
const { paginationState, paginationData, setPage, setBackendResponse } =
  usePagination({
    initialPage: 1,
    initialPageSize: 10,
  });

// Fetch data
const fetchUsers = async (page: number, size: number) => {
  const response = await axiosInstance.get(`/api/users`, {
    params: { page: page - 1, size },
  });

  setUsers(response.data.content);
  setBackendResponse({
    /* ... */
  });
};

// Render
return (
  <DataTable
    data={users}
    isLoading={isLoading}
    columns={columns}
    actions={actions}
    showPagination={true}
    paginationData={paginationData}
    onPageChange={setPage}
  />
);
```

---

## 🎨 Styling

Các component pagination sử dụng design system hiện tại:

- **Gradients:** `from-primary to-secondary`
- **Backdrop blur:** `backdrop-blur-sm`, `backdrop-blur-md`
- **Shadows:** `shadow-lg`, `shadow-xl` with `shadow-primary/...`
- **Rounded corners:** `rounded-xl`, `rounded-2xl`
- **Hover effects:** `hover:scale-105`, `hover:shadow-md`
- **Transitions:** `transition-all duration-200`

---

## 📝 Best Practices

1. **Always convert page index:**

   - Frontend: 1-based (page 1, 2, 3...)
   - Backend: 0-based (page 0, 1, 2...)
   - Convert khi gọi API: `page - 1`
   - Convert khi nhận response: `currentPage + 1`

2. **Re-fetch after mutations:**

   ```typescript
   const handleDelete = async (id: string) => {
     await deleteAPI(id);
     // Re-fetch to update list
     await fetchData(paginationState.page, paginationState.pageSize);
   };
   ```

3. **Scroll to top on page change:**

   ```typescript
   const handlePageChange = (page: number) => {
     setPage(page);
     window.scrollTo({ top: 0, behavior: "smooth" });
   };
   ```

4. **Choose appropriate page sizes:**

   - **Table view:** 10, 20, 50, 100
   - **Grid view:** 8, 12, 16, 24, 32 (multiples of grid columns)

5. **Handle empty states:**
   ```typescript
   {!isLoading && items.length === 0 && <EmptyState />}
   {!isLoading && items.length > 0 && (
     <>
       <ItemGrid items={items} />
       <GridPagination {...} />
     </>
   )}
   ```

---

## 🔧 Migration Guide

### Bước 1: Import dependencies

```typescript
import { usePagination } from "@/hooks/use-pagination";
import { GridPagination } from "@/components/commons/pagination/GridPagination";
// hoặc
import { PaginationControls } from "@/components/commons/pagination/PaginationControls";
```

### Bước 2: Setup pagination hook

```typescript
const {
  paginationState,
  paginationData,
  setPage,
  setPageSize,
  setBackendResponse,
} = usePagination({
  initialPage: 1,
  initialPageSize: 12,
});
```

### Bước 3: Update fetch function

```typescript
const fetchData = async (page: number, size: number) => {
  const response = await axiosInstance.get("/api/endpoint", {
    params: { page: page - 1, size },
  });

  setData(response.data.content);
  setBackendResponse({
    totalPages: response.data.totalPages,
    totalElements: response.data.totalElements,
    currentPage: response.data.currentPage + 1,
    pageSize: response.data.pageSize,
    hasNext: !response.data.last,
    hasPrevious: !response.data.first,
  });
};
```

### Bước 4: Add useEffect

```typescript
useEffect(() => {
  fetchData(paginationState.page, paginationState.pageSize);
}, [paginationState.page, paginationState.pageSize]);
```

### Bước 5: Add pagination component

```typescript
<GridPagination
  paginationData={paginationData}
  onPageChange={setPage}
  onPageSizeChange={setPageSize}
  showPageSizeSelector={true}
  pageSizeOptions={[8, 12, 16, 24]}
/>
```

---

## ✅ Checklist

Khi implement pagination cho một component mới:

- [ ] Import `usePagination` hook
- [ ] Import pagination component (`GridPagination` hoặc `PaginationControls`)
- [ ] Setup pagination hook với initial values
- [ ] Update API call để gửi page/size parameters
- [ ] Convert page index (1-based ↔ 0-based)
- [ ] Update pagination state từ backend response
- [ ] Add useEffect để re-fetch khi page/size thay đổi
- [ ] Add pagination component vào JSX
- [ ] Handle loading state
- [ ] Handle empty state
- [ ] Test với nhiều page sizes khác nhau
- [ ] Test navigation (first, previous, next, last)

---

## 🐛 Troubleshooting

### Pagination không hiển thị

- Kiểm tra `totalPages > 1`
- Kiểm tra `paginationData` được truyền đúng
- Kiểm tra `onPageChange` callback exists

### Dữ liệu không cập nhật khi đổi trang

- Kiểm tra `useEffect` dependencies có `paginationState.page`
- Kiểm tra API được gọi với đúng page parameter
- Kiểm tra `setBackendResponse` được gọi sau khi fetch

### Page index bị lệch

- **Luôn luôn** convert giữa 1-based (frontend) và 0-based (backend)
- Request: `page: page - 1`
- Response: `currentPage: data.currentPage + 1`

---

## 📞 Support

Nếu gặp vấn đề hoặc cần support, tham khảo:

- Example files: `*.example.tsx`
- Hook documentation: `hooks/use-pagination.ts`
- Component props: Check TypeScript interfaces

---

**Happy Coding! 🚀**
