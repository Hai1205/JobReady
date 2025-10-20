# Theme Colors - JobReady Admin Dashboard

## 🎨 Modern Black & White Color Palette

Webapp này sử dụng một bảng màu hiện đại với tone trắng-đen, kết hợp với các màu accent xanh slate/zinc chuyên nghiệp.

## 📊 Color Variables

### Light Mode

#### Primary Color - Professional Slate/Zinc

- **Primary**: `oklch(0.28 0.019 256.8)` - Xám xanh đậm chuyên nghiệp
- **Primary Foreground**: `oklch(0.985 0 0)` - Trắng

#### Secondary Color - Sophisticated Blue-gray

- **Secondary**: `oklch(0.53 0.046 257.4)` - Xanh xám hiện đại
- **Secondary Foreground**: `oklch(0.985 0 0)` - Trắng

### Dark Mode

#### Primary Color - Lighter Slate for Dark Backgrounds

- **Primary**: `oklch(0.70 0.069 240.27)` - Xanh slate sáng hơn
- **Primary Foreground**: `oklch(0.12 0.006 285.82)` - Đen xanh

#### Secondary Color - Sophisticated Blue-gray

- **Secondary**: `oklch(0.35 0.027 257.22)` - Xanh xám đậm
- **Secondary Foreground**: `oklch(0.97 0.002 285.93)` - Trắng

## 🎯 Sử dụng trong Components

### 1. AdminSidebar

- Gradient background từ card đến muted
- Active menu item: gradient từ primary đến secondary
- Hover effects với primary/secondary colors
- Logo sử dụng gradient primary-to-secondary

### 2. DashboardHeader

- Title với gradient text từ primary đến secondary
- Create button: gradient background với shadow effects
- Border accents sử dụng border/50 opacity

### 3. DataTable

- Header row: gradient từ primary/5 đến secondary/5
- Hover row: gradient tương tự
- Action buttons: primary/secondary colors
- Dropdown menu: backdrop blur với gradient accents

### 4. Cards & Stats

- Stat cards sử dụng gradient backgrounds:
  - Primary card: `from-primary to-primary/80`
  - Secondary card: `from-secondary to-secondary/80`
  - Chart colors từ theme variables
- Shadow effects với màu tương ứng: `shadow-primary/30`

### 5. Buttons & Inputs

- Primary buttons: gradient từ primary đến secondary
- Secondary buttons: gradient từ secondary/80 đến secondary
- Input focus: border-primary/50 với ring-primary/20
- Hover effects: scale transform + shadow enhancements

## 💡 Best Practices

### Gradients

```tsx
// Recommended gradient patterns
bg-gradient-to-r from-primary to-secondary
bg-gradient-to-br from-card to-card/80
bg-gradient-to-r from-primary/5 to-secondary/5
```

### Shadows

```tsx
// Layered shadows cho depth
shadow-lg shadow-primary/30
hover:shadow-xl hover:shadow-primary/40
```

### Borders

```tsx
// Subtle borders với opacity
border border-border/50
border-b border-border/30
```

### Hover & Focus States

```tsx
// Interactive states
hover:bg-primary/10
focus:border-primary/50 focus:ring-primary/20
group-hover:scale-110
transition-all duration-200
```

## 🚀 Applying to New Components

Khi tạo component mới:

1. **Background**: Sử dụng gradient từ `card` với opacity
2. **Text**: Sử dụng `foreground` cho text chính, `muted-foreground` cho text phụ
3. **Interactive elements**: Áp dụng `primary` hoặc `secondary` colors
4. **Borders**: Luôn sử dụng `border/50` để giữ sự tinh tế
5. **Shadows**: Thêm `shadow-{color}/30` cho depth
6. **Hover effects**: Kết hợp scale transform + shadow enhancements

## 📱 Responsive & Accessibility

- Tất cả màu sắc đã được tối ưu cho cả light và dark mode
- Contrast ratio đảm bảo accessibility standards
- Hover states rõ ràng cho UX tốt hơn
- Transition duration: 200ms cho smooth animations

## 🎨 Color Palette Reference

```css
/* Primary Palette */
primary-50:  oklch(0.95 0.01 256.8)
primary-100: oklch(0.90 0.015 256.8)
primary:     oklch(0.28 0.019 256.8)  /* Main */
primary-dark: oklch(0.20 0.019 256.8)

/* Secondary Palette */
secondary-50:  oklch(0.90 0.025 257.4)
secondary-100: oklch(0.80 0.035 257.4)
secondary:     oklch(0.53 0.046 257.4)  /* Main */
secondary-dark: oklch(0.40 0.046 257.4)
```

---

**Note**: Theme được cấu hình trong `app/globals.css` sử dụng CSS Custom Properties với OKLCH color space cho màu sắc hiện đại và chính xác.
