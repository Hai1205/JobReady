"use client";

import { FileText } from "lucide-react";

export default function Footer() {
  return (
    <footer className="border-t border-border bg-card/50 backdrop-blur-sm py-8 mt-auto">
      <div className="container max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col md:flex-row justify-between items-center gap-4">
          <div className="flex items-center gap-2">
            <FileText className="h-5 w-5 text-primary" />
            <span className="text-sm font-medium text-muted-foreground">
              © 2025 JobReady. All rights reserved.
            </span>
          </div>
          <div className="flex items-center gap-6">
            <a
              href="/terms-of-service"
              className="text-sm font-medium text-muted-foreground hover:text-primary transition-colors"
            >
              Điều khoản sử dụng
            </a>
            <span className="text-muted-foreground/30">•</span>
            <a
              href="/privacy-policy"
              className="text-sm font-medium text-muted-foreground hover:text-primary transition-colors"
            >
              Chính sách bảo mật
            </a>
          </div>
        </div>
      </div>
    </footer>
  );
}
