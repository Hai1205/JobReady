# Debug Login Issue - Summary

## Vấn đề đã phát hiện:

1. **TokenRefresher tự động gọi RefreshToken ngay sau login**

   - Đã sửa: Skip initial refresh khi user mới login
   - TokenRefresher giờ chỉ chạy interval mà không gọi refresh ngay lập tức

2. **Zustand persist với COOKIE storage gây conflict**

   - Đã sửa: Thay đổi từ `EStorageType.COOKIE` sang `EStorageType.LOCAL`
   - Cookies thực sự (`access_token`, `refresh_token`) nên được quản lý bởi server
   - State Zustand (`userAuth`, `isAdmin`) được lưu trong localStorage

3. **State persistence gây re-render không cần thiết**

   - Đã thêm `partialize` trong persist config để loại bỏ `isLoading`, `error`, `status`, `message` khỏi persistence
   - Chỉ persist các state quan trọng như `userAuth` và `isAdmin`

4. **Navigation sau login**
   - Đã thêm `router.replace("/")` sau khi login thành công
   - Dùng `replace` thay vì `push` để tránh back button quay lại login page

## Các thay đổi đã thực hiện:

### 1. `client/stores/authStore.ts`

- Thay đổi storage type từ COOKIE sang LOCAL
- Login function giờ set state ngay lập tức

### 2. `client/components/TokenRefresher.tsx`

- Thêm `hasRunInitialRefresh` ref để skip initial refresh
- Không gọi `performRefresh()` ngay lập tức sau login

### 3. `client/lib/initialStore.ts`

- Thêm `partialize` để chỉ persist state quan trọng
- Loại bỏ `isLoading`, `error`, `status`, `message` khỏi persistence

### 4. `client/app/auth/login/page.tsx`

- Thêm navigation với `router.replace("/")` sau login thành công

## Kiểm tra tiếp theo:

1. Test login và xem console logs
2. Kiểm tra cookies trong DevTools (Application > Cookies)
3. Kiểm tra localStorage (Application > Local Storage)
4. Verify không có page reload sau login
5. Verify cookies không bị mất sau reload thủ công

## Cookies phải có:

- `access_token` (từ server, HttpOnly=false, SameSite=Lax)
- `refresh_token` (từ server, HttpOnly=false, SameSite=Lax)

## LocalStorage phải có:

- `auth-storage` (chứa userAuth và isAdmin)
