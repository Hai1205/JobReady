# JobReady Frontend - Next.js CV Builder

Ứng dụng frontend JobReady với AI-powered CV builder, cung cấp trải nghiệm tạo và cải thiện CV chuyên nghiệp với sự hỗ trợ của trí tuệ nhân tạo.

## ✨ Tính năng chính

### AI-Powered CV Features

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
- **Dark/Light Mode**: Chế độ sáng/tối với next-themes

### Authentication & Security

- **JWT Authentication**: Bảo mật với JSON Web Tokens
- **Protected Routes**: Bảo vệ các route nhạy cảm với middleware
- **Role-based Access**: Phân quyền admin và user
- **Token Refresh**: Tự động refresh token với TokenRefresher component
- **Cookie Monitoring**: Theo dõi trạng thái authentication

### 📱 User Experience

- **Intuitive Navigation**: Điều hướng dễ dàng với sidebar và breadcrumbs
- **Toast Notifications**: Thông báo real-time với react-toastify
- **Loading States**: UX mượt mà với skeleton loading
- **Error Handling**: Xử lý lỗi graceful với user-friendly messages
- **Mobile Responsive**: Tối ưu cho mobile với responsive design

## 🏗️ Kiến trúc & Tech Stack

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Next.js 15    │    │   Zustand Store │    │   Axios Client  │
│   App Router    │◄──►│  State Mgmt     │◄──►│   API Calls     │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                        │                        │
         ▼                        ▼                        ▼
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│  Tailwind CSS   │    │   shadcn/ui     │    │   TypeScript    │
│   Styling       │    │   Components    │    │   Type Safety   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
```

### Core Technologies

- **Next.js 15.3.3** - React framework với App Router
- **React 18** - UI library với concurrent features
- **TypeScript 5** - Type-safe JavaScript
- **Tailwind CSS 4** - Utility-first CSS framework
- **shadcn/ui** - Modern component library với Radix UI
- **Zustand 5** - Lightweight state management
- **Axios 1.10** - HTTP client với interceptors
- **React Hook Form 7** - Form handling với validation
- **Lucide Icons** - Beautiful icon set
- **Framer Motion** - Animation library
- **React Toastify** - Toast notifications
- **Next Themes** - Theme management
- **Puppeteer** - PDF generation
- **Mammoth** - DOCX parsing
- **PDF.js** - PDF processing
- **File Saver** - File download
- **jsPDF** - PDF creation
- **html2canvas** - HTML to image conversion

### State Management

- **Zustand Stores**: `authStore`, `cvStore`, `userStore`, `aiStore`, `statsStore`
- **Persistent State**: Local storage cho user preferences
- **Real-time Updates**: Optimistic updates cho better UX
- **Immer**: Immutable state updates

## Cách chạy

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

# App Configuration
NEXT_PUBLIC_APP_NAME=JobReady
NEXT_PUBLIC_APP_VERSION=0.1.0
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
├── app/                          # Next.js App Router
│   ├── globals.css              # Global styles với Tailwind
│   ├── layout.tsx               # Root layout với providers
│   ├── page.tsx                 # Home page
│   ├── loading.tsx              # Loading UI
│   ├── not-found.tsx            # 404 page
│   ├── auth/                    # Authentication pages
│   │   ├── login/               # Login page
│   │   ├── register/            # Register page
│   │   ├── forgot-password/     # Password reset
│   │   ├── reset-password/      # Reset password
│   │   └── verification/        # Email verification
│   ├── cv-builder/              # CV Builder page
│   ├── my-cvs/                  # User's CV list
│   ├── settings/                # User settings
│   └── admin/                   # Admin dashboard
│       ├── cv-dashboard/        # CV management
│       └── user-dashboard/      # User management
├── components/                  # Reusable components
│   ├── ui/                      # shadcn/ui components (50+ components)
│   └── comons/                  # Custom components
│       ├── cv-builder/          # CV building components
│       │   ├── AIFeaturesTab.tsx
│       │   ├── JobDescriptionImport.tsx
│       │   ├── AISuggestionsList.tsx
│       │   └── CVBuilderWizard.tsx
│       ├── home/                # Home page components
│       ├── layout/              # Layout components
│       │   ├── ThemeProvider.tsx
│       │   ├── TokenRefresher.tsx
│       │   └── CookieMonitor.tsx
│       ├── navbar/              # Navigation components
│       ├── my-cvs/              # CV list components
│       └── settings/            # Settings components
├── hooks/                       # Custom React hooks
│   ├── use-cv-parser.ts         # CV parsing logic
│   ├── use-mobile.ts            # Mobile detection
│   └── use-toast.ts             # Toast management
├── lib/                         # Utilities
│   ├── axiosInstance.ts         # Axios configuration
│   ├── chromeFinder.ts          # Chrome detection for Puppeteer
│   ├── cvParser.ts              # CV parsing utilities
│   ├── cvValidator.ts           # CV validation logic
│   ├── initialStore.ts          # Initial store state
│   ├── suggestionApplier.ts     # AI suggestion application
│   └── utils.ts                 # General utilities
├── stores/                      # Zustand stores
│   ├── authStore.ts             # Authentication state
│   ├── cvStore.ts               # CV management state
│   ├── userStore.ts             # User data state
│   ├── aiStore.ts               # AI features state
│   └── statsStore.ts            # Statistics state
├── types/                       # TypeScript definitions
│   ├── enum.ts                  # Enums and constants
│   └── interface.ts             # Type interfaces
├── services/                    # Service layer
│   ├── constants.ts             # App constants
│   ├── mockData.ts              # Mock data for development
│   ├── pdfExportService.ts      # PDF export logic
│   └── pdfExportService.ts      # PDF export utilities
├── public/                      # Static assets
│   ├── images/                  # Image assets
│   └── svgs/                    # SVG icons
└── styles/                      # Additional styles
    ├── cv-preview.css           # CV preview styles
    └── globals.css              # Additional global styles
```

## Key Components

### CV Builder Components

#### `CVBuilderWizard.tsx`

- Multi-step CV creation wizard
- Form validation với React Hook Form và Zod
- Real-time preview với live updates
- Step navigation với progress indicator

#### `AIPanel.tsx`

- AI-powered features panel
- Job description import và analysis
- AI suggestions display và application
- Real-time CV improvement feedback

#### `AIFeaturesTab.tsx`

- Tab interface cho AI features
- Tích hợp Job Description Import
- Quản lý AI suggestions state

#### `JobDescriptionImport.tsx`

- Upload/paste job description
- Tích hợp với backend AI service
- Hiển thị matching results và suggestions

#### `AISuggestionsList.tsx`

- Hiển thị AI-generated suggestions
- Apply/dismiss functionality
- Visual feedback cho user actions
- Categorization của suggestions

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

  // Wizard state
  currentStep: number;
  totalSteps: number;

  // Actions
  createCV: (userId: string) => Promise<void>;
  updateCV: (cvId: string, data: Partial<CV>) => Promise<void>;
  analyzeCV: (cvId: string) => Promise<void>;
  analyzeCVWithJD: (cvId: string, jd: string) => Promise<void>;
  applySuggestion: (suggestionId: string) => Promise<void>;
  handleSetCurrentStep: (step: number) => void;
}
```

#### `authStore.ts`

```typescript
interface AuthStore {
  userAuth: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;

  login: (credentials: LoginData) => Promise<void>;
  register: (userData: RegisterData) => Promise<void>;
  logout: () => void;
  refreshToken: () => Promise<void>;
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
  headers: {
    "Content-Type": "application/json",
  },
});

// Request interceptor cho JWT
axiosInstance.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
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
      // Redirect to login hoặc refresh token
      useAuthStore.getState().logout();
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
    toast.error("Không thể phân tích CV");
    throw error;
  }
};
```

## 🎨 UI/UX Features

### Design System

- **Color Palette**: Consistent colors với CSS variables
- **Typography**: Geist font family
- **Spacing**: Consistent spacing scale
- **Components**: 50+ reusable UI components từ shadcn/ui

### Responsive Design

- **Mobile-first**: Optimized cho mobile devices
- **Tablet Support**: Adaptive layouts
- **Desktop Enhancement**: Advanced features cho desktop

### Accessibility

- **Keyboard Navigation**: Full keyboard support
- **Screen Reader**: ARIA labels và semantic HTML
- **Focus Management**: Proper focus indicators
- **Color Contrast**: WCAG compliant colors

### Animations

- **Framer Motion**: Smooth animations và transitions
- **Loading States**: Skeleton loading cho better UX
- **Hover Effects**: Interactive feedback
- **Page Transitions**: Smooth navigation

## Authentication Flow

### Middleware Protection

```typescript
// middleware.ts
export function middleware(request: NextRequest) {
  // JWT token validation
  // Role-based access control
  // Mobile device detection
  // Route protection logic
}
```

### Token Management

- **Automatic Refresh**: TokenRefresher component
- **Cookie Monitoring**: CookieMonitor component
- **Secure Storage**: HTTP-only cookies cho production
- **Expiration Handling**: Automatic logout khi token hết hạn

## Testing

### Development Scripts

```json
{
  "scripts": {
    "dev": "next dev --turbopack -p 3000",
    "build": "next build",
    "start": "next start",
    "lint": "next lint"
  }
}
```

### Build Configuration

```javascript
// next.config.mjs
const nextConfig = {
  eslint: {
    ignoreDuringBuilds: true,
  },
  typescript: {
    ignoreBuildErrors: true,
  },
  images: {
    unoptimized: true,
  },
};
```

### E2E Tests (có thể thêm Cypress hoặc Playwright)

```bash
npm run test:e2e
```

### Linting

```bash
npm run lint
```

## Deployment

### Vercel (Recommended)

1. Connect GitHub repository
2. Configure environment variables
3. Deploy automatically với CI/CD

### Docker

```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production
COPY . .
RUN npm run build
EXPOSE 3000
CMD ["npm", "start"]
```

### Manual Build

```bash
npm run build
npm run start
```

## Development Features

### Turbopack

- **Fast Refresh**: Next.js 15 với Turbopack cho development
- **Hot Reload**: Instant updates without full reload
- **Type Checking**: Real-time TypeScript checking

### Development Tools

- **ESLint**: Code quality enforcement
- **TypeScript**: Strict type checking
- **Tailwind CSS**: Utility-first styling
- **shadcn/ui**: Component development

## 🚨 Troubleshooting

### Common Issues

#### Build Errors

- **Module not found**: `npm install` hoặc check imports
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
- **Route-based splitting**: Automatic với Next.js App Router

### Image Optimization

- **Next.js Image**: Automatic optimization (disabled for custom config)
- **WebP format**: Modern image formats
- **Responsive images**: Different sizes cho devices

### Caching Strategies

- **Static generation**: ISR cho static pages
- **API caching**: React Query hoặc Zustand persistence
- **Browser caching**: Proper cache headers

## 🔮 Future Enhancements

### Planned Features

- [ ] **Real-time Collaboration**: Multiple users edit CV cùng lúc
- [ ] **CV Templates**: Pre-built templates với AI customization
- [ ] **Analytics Dashboard**: Track CV performance và improvements
- [ ] **Mobile App**: React Native version
- [ ] **Offline Support**: PWA capabilities
- [ ] **Multi-language**: Internationalization support
- [ ] **Advanced AI**: More sophisticated AI suggestions
- [ ] **Integration APIs**: LinkedIn, Indeed, Glassdoor integration

### Technical Improvements

- [ ] **Testing Coverage**: Comprehensive test suite với Jest/Playwright
- [ ] **Performance Monitoring**: Real user monitoring
- [ ] **Error Tracking**: Sentry integration
- [ ] **CI/CD Pipeline**: Automated testing và deployment
- [ ] **Micro-frontends**: Modular architecture
- [ ] **GraphQL**: More efficient data fetching

## 📖 Documentation

- [Backend API Documentation](../server/README.md) - Backend services và APIs
- [shadcn/ui Documentation](https://ui.shadcn.com) - UI component library
- [Next.js Documentation](https://nextjs.org/docs) - Framework documentation
- [Zustand Documentation](https://zustand-demo.pmnd.rs) - State management

## 🤝 Contributing

1. Fork repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

### Code Standards

- **TypeScript**: Strict type checking enabled
- **ESLint**: Airbnb config với custom rules
- **Prettier**: Consistent code formatting
- **Conventional Commits**: Standardized commit messages
- **Component Structure**: Consistent component organization

## License

This project is licensed under the MIT License.

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/Hai1205/JobReady/issues)
- **Discussions**: [GitHub Discussions](https://github.com/Hai1205/JobReady/discussions)
- **Email**: support@jobready.com

---

**Made with ❤️ by JobReady Team**
