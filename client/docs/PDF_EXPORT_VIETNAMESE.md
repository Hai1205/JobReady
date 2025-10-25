# Hướng Dẫn Sử Dụng Xuất PDF

Hàm `handleGeneratePDF` trong `cvStore` đã được nâng cấp để hỗ trợ xuất PDF từ nhiều nơi khác nhau trong ứng dụng.

## Tóm Tắt Thay Đổi

### Vấn Đề Trước Đây

- ❌ Hàm chỉ hoạt động khi element preview (`#cv-preview-content`) có trong DOM
- ❌ Không thể xuất PDF từ admin dashboard hoặc các trang khác
- ❌ Lỗi "Element with ID 'cv-preview-content' not found" khi gọi từ nơi không có preview

### Giải Pháp Mới

- ✅ Tự động phát hiện và chọn phương thức xuất phù hợp
- ✅ Hỗ trợ 3 chế độ: DOM-based, HTML generation, Custom HTML
- ✅ Hoạt động ở bất kỳ đâu trong ứng dụng
- ✅ Không cần thay đổi code ở các nơi đang dùng

## Cách Sử Dụng

### 1. Cách Cơ Bản (Khuyến Nghị)

Chỉ cần truyền object CV vào, hàm sẽ tự động xử lý:

```typescript
const { handleGeneratePDF } = useCVStore();

// Trong component của bạn
const handleExport = async (cv: ICV) => {
  await handleGeneratePDF(cv);
};
```

**Hàm tự động:**

- Nếu có preview DOM → dùng phương thức DOM (giữ nguyên 100% style)
- Nếu không có preview → tự động tạo HTML từ dữ liệu CV

### 2. Truyền HTML Tùy Chỉnh (Nâng Cao)

Nếu bạn muốn dùng template riêng:

```typescript
import { renderCVToHTMLAsync } from "@/components/cv-builder/CVRenderer";

const customHTML = await renderCVToHTMLAsync(cv);
await handleGeneratePDF(cv, customHTML);
```

## Ví Dụ Thực Tế

### Ví Dụ 1: Admin CV Dashboard (Đang Dùng)

```typescript
// File: app/admin/cv-dashboard/page.tsx
<CVTable
  CVs={filteredCVs}
  isLoading={isLoading}
  onDownload={(cv) => {
    handleGeneratePDF(cv); // ← Tự động tạo HTML
  }}
/>
```

### Ví Dụ 2: CV Builder Preview (Đang Dùng)

```typescript
// File: components/cv-builder/steps/PreviewStep.tsx
<Button onClick={() => handleGeneratePDF(currentCV)}>
  <Download className="mr-2 h-4 w-4" />
  Tải xuống PDF
</Button>
// ← Tự động dùng DOM vì có #cv-preview-content
```

### Ví Dụ 3: Các Nơi Khác

```typescript
// File: app/my-cvs/page.tsx
import { useCVStore } from "@/stores/cvStore";

export default function MyCVsPage() {
  const { handleGeneratePDF } = useCVStore();

  return (
    <div>
      {cvs.map((cv) => (
        <Button onClick={() => handleGeneratePDF(cv)}>Tải PDF</Button>
      ))}
    </div>
  );
}
```

## 3 Chế Độ Hoạt Động

### Chế Độ 1: DOM-based (Tự Động)

- **Khi nào:** Preview element có trong DOM
- **Ưu điểm:** Giữ nguyên 100% style từ preview
- **Dùng ở:** CV Builder - Preview Step

### Chế Độ 2: HTML Generation (Tự Động)

- **Khi nào:** Preview element không có trong DOM
- **Ưu điểm:** Hoạt động ở mọi nơi, không cần preview
- **Dùng ở:** Admin Dashboard, My CVs, v.v.

### Chế Độ 3: Custom HTML (Thủ Công)

- **Khi nào:** Bạn truyền HTML string vào tham số thứ 2
- **Ưu điểm:** Tùy chỉnh hoàn toàn template
- **Dùng ở:** Các tính năng đặc biệt (A/B testing, custom templates)

## Files Đã Thay Đổi

### 1. `client/stores/cvStore.ts`

```typescript
// Thêm tham số optional htmlContent
handleGeneratePDF: (cv: ICV, htmlContent?: string) => Promise<void>;

// Logic mới:
// 1. Nếu có htmlContent → dùng exportCustomHTML
// 2. Nếu có preview DOM → dùng exportToPDF
// 3. Nếu không → tự động tạo HTML và dùng exportCustomHTML
```

### 2. `client/components/cv-builder/CVRenderer.tsx` (Mới)

```typescript
// Component React để render CV
export const CVRenderer: React.FC<CVRendererProps>

// Hàm tạo HTML string từ CV data
export const generateCVHTML = (cv: ICV): string

// Phiên bản async
export const renderCVToHTMLAsync = async (cv: ICV): Promise<string>
```

### 3. Các File Khác

Không cần thay đổi! Code hiện tại vẫn hoạt động bình thường.

## Thông Báo Cho User

Hàm tự động hiển thị thông báo:

- 🔵 **"Đang tạo PDF..."** - Khi đang tạo từ dữ liệu
- ✅ **"Tải xuống CV thành công!"** - Khi thành công
- ❌ **"Lỗi tạo PDF: [chi tiết lỗi]"** - Khi có lỗi

## Xử Lý Lỗi

Hàm xử lý tất cả trường hợp:

- ❌ Gọi từ server-side → Hiện toast lỗi rõ ràng
- ❌ Tạo PDF thất bại → Hiện chi tiết lỗi
- ✅ Thành công → Tải file PDF xuống

## Performance

| Phương Thức     | Thời Gian | Ghi Chú                         |
| --------------- | --------- | ------------------------------- |
| DOM-based       | ~1-2s     | Bao gồm load ảnh, inline styles |
| HTML Generation | ~0.5-1s   | Tạo HTML trực tiếp              |

## Khắc Phục Sự Cố

### Lỗi "Element not found"

✅ **Đã sửa!** Không còn lỗi này nữa, hàm tự động chuyển sang tạo HTML.

### Ảnh không hiện trong PDF

- Đảm bảo avatar URL có thể truy cập công khai
- Nén ảnh trước khi upload (khuyến nghị < 500KB)

### Styling khác với preview

- DOM-based: Giống 100% với preview
- HTML generation: Dùng template chuẩn (có thể khác một chút)

## Testing

Để test đầy đủ, thử xuất PDF từ:

1. ✅ **CV Builder - Preview Step**

   - Vào trang tạo CV → Preview → Click "Tải xuống PDF"
   - Kiểm tra: Style phải giống 100% preview

2. ✅ **Admin CV Dashboard**

   - Vào Admin → CV Dashboard → Click icon download
   - Kiểm tra: PDF tải về thành công

3. ✅ **My CVs Page** (nếu có nút download)
   - Vào My CVs → Click download
   - Kiểm tra: PDF tải về thành công

## Kế Hoạch Tương Lai

Có thể nâng cấp thêm:

- [ ] Thanh progress cho quá trình xuất
- [ ] Hỗ trợ nhiều template CV
- [ ] Tạo PDF hoàn toàn ở client (không cần server)
- [ ] Xuất nhiều CV cùng lúc (batch)
- [ ] Preview PDF trước khi tải

## Support

Nếu gặp vấn đề:

1. Kiểm tra console log (có thông báo chi tiết)
2. Đảm bảo CV data có đầy đủ thông tin
3. Kiểm tra network tab (xem request đến `/api/export-cv`)
4. Xem file `PDF_EXPORT_USAGE.md` (tiếng Anh) để biết thêm chi tiết
