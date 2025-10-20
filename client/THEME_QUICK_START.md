# 🚀 Quick Start - Using the New Theme

## 📚 TL;DR

Webapp giờ đây có một theme màu hiện đại với:

- **Primary**: Slate/Zinc đậm (xám xanh chuyên nghiệp)
- **Secondary**: Blue-gray (xanh xám hiện đại)
- **Tone**: Trắng-đen với gradients tinh tế

## 🎨 How to Use Colors

### Basic Usage

```tsx
// Primary color
<button className="bg-primary text-primary-foreground">
  Button
</button>

// Secondary color
<button className="bg-secondary text-secondary-foreground">
  Button
</button>

// Gradient (recommended!)
<button className="bg-gradient-to-r from-primary to-secondary">
  Modern Button
</button>
```

### Common Patterns

#### 1. Cards with Gradient

```tsx
<Card className="border-border/50 shadow-lg bg-gradient-to-br from-card to-card/80 backdrop-blur-sm">
  {/* Content */}
</Card>
```

#### 2. Interactive Elements

```tsx
<div className="hover:bg-gradient-to-r hover:from-primary/5 hover:to-secondary/5 transition-all duration-200">
  {/* Hover-able content */}
</div>
```

#### 3. Buttons with Shadow

```tsx
<Button className="bg-gradient-to-r from-primary to-secondary shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40">
  Click me
</Button>
```

#### 4. Gradient Text

```tsx
<h1 className="bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
  Gradient Title
</h1>
```

## 🎯 Design Tokens

### Backgrounds

- `bg-card` - Card backgrounds
- `bg-muted` - Subtle sections
- `bg-accent` - Highlighted areas

### Text

- `text-foreground` - Primary text
- `text-muted-foreground` - Secondary text
- `text-primary` - Accent text

### Borders

- `border-border` - Standard borders
- `border-border/50` - Subtle borders (recommended)

### Shadows

- `shadow-lg shadow-primary/30` - Standard shadow
- `shadow-xl shadow-primary/40` - Enhanced shadow

## ⚡ Pro Tips

### 1. Use Opacity for Subtlety

```tsx
// Too strong
className = "bg-primary";

// Better - subtle
className = "bg-primary/5";
```

### 2. Layer Gradients

```tsx
// Background gradient
className = "bg-gradient-to-br from-card to-card/80";

// + Hover gradient
className = "hover:bg-gradient-to-r hover:from-primary/5 hover:to-secondary/5";
```

### 3. Add Transitions

```tsx
// Always add transitions!
className = "transition-all duration-200";
```

### 4. Combine with Scale

```tsx
// Hover with scale
className = "hover:scale-105 transition-all duration-200";
```

## 🎨 Complete Example

```tsx
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";

export function ExampleCard() {
  return (
    <Card className="border-border/50 shadow-lg bg-gradient-to-br from-card to-card/80 backdrop-blur-sm">
      <CardHeader className="border-b border-border/30">
        <CardTitle className="bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
          Modern Card Title
        </CardTitle>
      </CardHeader>

      <CardContent className="space-y-4">
        <p className="text-muted-foreground">
          This is a description using muted foreground color.
        </p>

        <div className="flex gap-3">
          <Button className="bg-gradient-to-r from-primary to-secondary shadow-lg shadow-primary/30 hover:shadow-xl hover:shadow-primary/40 hover:scale-105 transition-all duration-200">
            Primary Action
          </Button>

          <Button
            variant="secondary"
            className="hover:scale-105 transition-all duration-200"
          >
            Secondary
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}
```

## 🌓 Dark Mode

Theme tự động chuyển đổi! Không cần làm gì thêm:

```tsx
// Automatically adapts
<div className="bg-card text-foreground">
  Works in both light and dark mode!
</div>
```

## 📖 Reference

- **Full Color Guide**: `THEME_COLORS.md`
- **Update Summary**: `ADMIN_THEME_UPDATE.md`
- **Theme Config**: `app/globals.css`

## 💡 Questions?

Kiểm tra các component trong `components/admin/*` để xem examples thực tế!
