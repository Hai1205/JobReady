# 🚀 JobReady Frontend - Next.js CV Builder

Ứng dụng frontend JobReady với AI-powered CV builder, cung cấp trải nghiệm tạo và cải thiện CV chuyên nghiệp với sự hỗ trợ của trí tuệ nhân tạo.

## ✨ Tính năng chính

### 🎯 AI-Powered CV Features

- **Smart CV Import**: Upload và tự động phân tích CV từ PDF, DOCX, TXT
- **AI Analyze**: Phân tích CV và đưa ra suggestions cải thiện chi tiết
- **Job Matching**: So sánh CV với job description để tối ưu hóa ứng tuyển
- **Intelligent Suggestions**: AI-generated recommendations cho từng phần của CV
- **Real-time Improvements**: Cải thiện CV theo thời gian thực với AI guidance

### 🎨 CV Builder Interface

- **Modern UI**: Giao diện đẹp với Tailwind CSS và shadcn/ui components
- **Wizard Flow**: Quy trình tạo CV từng bước, dễ sử dụng
- **Live Preview**: Xem trước CV real-time khi chỉnh sửa
- **Responsive Design**: Tương thích với mọi thiết bị
- **Dark/Light Mode**: Chế độ sáng/tối

### 🔐 Authentication & Security

- **JWT Authentication**: Bảo mật với JSON Web Tokens
- **OAuth2 Integration**: Đăng nhập với Google, Facebook, GitHub
- **Protected Routes**: Bảo vệ các route nhạy cảm
- **Secure API Calls**: Axios interceptors với authentication

### 📱 User Experience

- **Intuitive Navigation**: Điều hướng dễ dàng với sidebar và breadcrumbs
- **Toast Notifications**: Thông báo real-time cho user actions
- **Loading States**: UX mượt mà với skeleton loading
- **Error Handling**: Xử lý lỗi graceful với user-friendly messages

## 🏗️ Kiến trúc & Tech Stack

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Next.js App   │    │   Zustand Store │    │   Axios Client  │
│   (React 18)    │◄──►│  State Mgmt     │◄──►│   API Calls     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │
         ▼                        ▼                        ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Tailwind CSS   │    │   shadcn/ui     │    │   TypeScript    │
│   Styling       │    │   Components    │    │   Type Safety   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Core Technologies

- **Next.js 14** - React framework với App Router
- **React 18** - UI library với concurrent features
- **TypeScript** - Type-safe JavaScript
- **Tailwind CSS** - Utility-first CSS framework
- **shadcn/ui** - Modern component library
- **Zustand** - Lightweight state management
- **Axios** - HTTP client với interceptors
- **React Hook Form** - Form handling
- **Lucide Icons** - Beautiful icon set

### State Management

- **Zustand Stores**: `authStore`, `cvStore`, `userStore`
- **Persistent State**: Local storage cho user preferences
- **Real-time Updates**: Optimistic updates cho better UX

## 🚀 Cách chạy

### Prerequisites

- Node.js 18+
- npm hoặc yarn
- Backend server đang chạy (localhost:8080)

### 1. Cài đặt Dependencies

```bash
cd client
npm install
# hoặc
yarn install
```

### 2. Cấu hình Environment

Tạo file `.env.local`:

```bash
# API Configuration
NEXT_PUBLIC_API_BASE_URL=http://localhost:8080

# OAuth2 Configuration (nếu sử dụng)
NEXT_PUBLIC_GOOGLE_CLIENT_ID=your-google-client-id
NEXT_PUBLIC_FACEBOOK_APP_ID=your-facebook-app-id
NEXT_PUBLIC_GITHUB_CLIENT_ID=your-github-client-id

# App Configuration
NEXT_PUBLIC_APP_NAME=JobReady
NEXT_PUBLIC_APP_VERSION=1.0.0
```

### 3. Chạy Development Server

```bash
npm run dev
# hoặc
yarn dev
```

Ứng dụng sẽ chạy tại: http://localhost:3000

### 4. Build cho Production

```bash
npm run build
npm start
# hoặc
yarn build
yarn start
```

## 📁 Cấu trúc Project

```
client/
├── app/                    # Next.js App Router
│   ├── globals.css        # Global styles
│   ├── layout.tsx         # Root layout
│   ├── page.tsx           # Home page
│   ├── auth/              # Authentication pages
│   ├── cv-builder/        # CV Builder pages
│   └── admin/             # Admin dashboard
├── components/            # Reusable components
│   ├── ui/                # shadcn/ui components
│   ├── cv-builder/        # CV-specific components
│   │   ├── AIFeaturesTab.tsx
│   │   ├── JobDescriptionImport.tsx
│   │   └── AISuggestionsList.tsx
│   └── admin/             # Admin components
├── hooks/                 # Custom React hooks
├── lib/                   # Utilities
│   ├── axiosInstance.ts   # Axios configuration
│   └── utils.ts           # Helper functions
├── stores/                # Zustand stores
│   ├── authStore.ts       # Authentication state
│   ├── cvStore.ts         # CV management state
│   └── userStore.ts       # User data state
├── types/                 # TypeScript definitions
│   └── interface.ts       # Type definitions
└── public/                # Static assets
```

## 🎯 Key Components

### CV Builder Components

#### `AIFeaturesTab.tsx`

- Tab chính cho AI features
- Tích hợp Job Description Import và Gợi Ý AI
- Quản lý state cho CV analyze

#### `JobDescriptionImport.tsx`

- Upload/paste job description
- Tích hợp với backend API để Phân Tích vs JD
- Hiển thị matching results

#### `AISuggestionsList.tsx`

- Hiển thị AI-generated suggestions
- Apply/dismiss functionality
- Visual feedback cho user actions

#### `CVBuilderWizard.tsx`

- Multi-step CV creation wizard
- Form validation với React Hook Form
- Real-time preview

### State Management

#### `cvStore.ts`

```typescript
interface CVStore {
  // CV data
  currentCV: CV | null;
  cvList: CV[];

  // AI features
  jobDescription: string;
  aiSuggestions: AISuggestion[];
  isAnalyzing: boolean;

  // Actions
  analyzeCV: (cvId: string) => Promise<void>;
  analyzeCVWithJD: (cvId: string, jd: string) => Promise<void>;
  applySuggestion: (suggestionId: string) => Promise<void>;
}
```

#### `authStore.ts`

```typescript
interface AuthStore {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;

  login: (credentials: LoginData) => Promise<void>;
  logout: () => void;
  checkAuth: () => Promise<void>;
}
```

## 🔗 API Integration

### Axios Configuration

```typescript
// lib/axiosInstance.ts
const axiosInstance = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  timeout: 10000,
});

// Request interceptor cho JWT
axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor cho error handling
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Redirect to login
      window.location.href = "/auth/login";
    }
    return Promise.reject(error);
  }
);
```

### CV Service Integration

```typescript
// stores/cvStore.ts
const analyzeCV = async (cvId: string) => {
  set({ isAnalyzing: true });
  try {
    const response = await axiosInstance.post("/cv/analyze", {
      cvId,
      sections: ["experience", "skills", "education"],
    });

    const suggestions = response.data.suggestions;
    set({ aiSuggestions: suggestions, isAnalyzing: false });
  } catch (error) {
    set({ isAnalyzing: false });
    throw error;
  }
};
```

## 🎨 UI/UX Features

### Design System

- **Color Palette**: Consistent colors với CSS variables
- **Typography**: Readable fonts với proper hierarchy
- **Spacing**: Consistent spacing scale
- **Components**: Reusable UI components với shadcn/ui

### Responsive Design

- **Mobile-first**: Optimized cho mobile devices
- **Tablet Support**: Adaptive layouts
- **Desktop Enhancement**: Advanced features cho desktop

### Accessibility

- **Keyboard Navigation**: Full keyboard support
- **Screen Reader**: ARIA labels và semantic HTML
- **Focus Management**: Proper focus indicators
- **Color Contrast**: WCAG compliant colors

## 🧪 Testing

### Unit Tests

```bash
npm run test
# hoặc
yarn test
```

### E2E Tests (có thể thêm Cypress hoặc Playwright)

```bash
npm run test:e2e
```

### Linting

```bash
npm run lint
```

## 🚀 Deployment

### Vercel (Recommended)

1. Connect GitHub repository
2. Configure environment variables
3. Deploy automatically

### Docker

```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
```

### Manual Build

```bash
npm run build
npm run export  # Static export nếu cần
```

## 🔧 Development Scripts

```json
{
  "scripts": {
    "dev": "next dev",
    "build": "next build",
    "start": "next start",
    "lint": "next lint",
    "test": "jest",
    "test:watch": "jest --watch",
    "type-check": "tsc --noEmit"
  }
}
```

## 🚨 Troubleshooting

### Common Issues

#### Build Errors

- **Module not found**: `npm install` hoặc `yarn install`
- **Type errors**: Check TypeScript definitions
- **Environment variables**: Verify `.env.local` file

#### Runtime Errors

- **API connection failed**: Check backend server status
- **Authentication failed**: Verify JWT token validity
- **CORS errors**: Configure CORS trong backend

#### Performance Issues

- **Slow loading**: Enable compression và caching
- **Large bundle**: Code splitting và lazy loading
- **Memory leaks**: Check component cleanup

## 📊 Performance Optimization

### Code Splitting

- **Dynamic imports**: Lazy load components
- **Route-based splitting**: Automatic với Next.js

### Image Optimization

- **Next.js Image**: Automatic optimization
- **WebP format**: Modern image formats
- **Responsive images**: Different sizes cho devices

### Caching Strategies

- **Static generation**: ISR cho static pages
- **API caching**: React Query hoặc SWR
- **Browser caching**: Proper cache headers

## 🔮 Future Enhancements

### Planned Features

- [ ] **Real-time Collaboration**: Multiple users edit CV cùng lúc
- [ ] **CV Templates**: Pre-built templates với AI customization
- [ ] **Analytics Dashboard**: Track CV performance và improvements
- [ ] **Mobile App**: React Native version
- [ ] **Offline Support**: PWA capabilities
- [ ] **Multi-language**: Internationalization support

### Technical Improvements

- [ ] **Testing Coverage**: Comprehensive test suite
- [ ] **Performance Monitoring**: Real user monitoring
- [ ] **Error Tracking**: Sentry integration
- [ ] **CI/CD Pipeline**: Automated testing và deployment

## 📖 Documentation

- [API Documentation](../sever/README.md) - Backend API reference
- [Component Library](./components/README.md) - UI components guide
- [State Management](./stores/README.md) - Zustand stores documentation
- [Deployment Guide](./DEPLOYMENT.md) - Production deployment

## 🤝 Contributing

1. Fork repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

### Code Standards

- **TypeScript**: Strict type checking
- **ESLint**: Code quality enforcement
- **Prettier**: Consistent code formatting
- **Conventional Commits**: Standardized commit messages

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/your-repo/issues)
- **Discussions**: [GitHub Discussions](https://github.com/your-repo/discussions)
- **Email**: support@jobready.com

---

**Made with ❤️ by JobReady Team**
