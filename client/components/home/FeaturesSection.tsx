"use client";

import { CheckCircle, Sparkles, Zap } from "lucide-react";

export default function FeaturesSection() {
  return (
    <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24 md:py-32">
      <div className="grid gap-8 md:grid-cols-3">
        <div className="flex flex-col gap-4 rounded-lg border border-border bg-card p-6">
          <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
            <Sparkles className="h-6 w-6 text-primary" />
          </div>
          <h3 className="text-xl font-semibold">Gợi Ý Từ AI</h3>
          <p className="text-muted-foreground leading-relaxed">
            Nhận gợi ý thời gian thực từ AI để cải thiện nội dung CV, làm nổi
            bật thành tích quan trọng và tối ưu hóa cho hệ thống ATS.
          </p>
        </div>

        <div className="flex flex-col gap-4 rounded-lg border border-border bg-card p-6">
          <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-secondary/10">
            <Zap className="h-6 w-6 text-secondary" />
          </div>
          <h3 className="text-xl font-semibold">Trình Tạo Theo Bước</h3>
          <p className="text-muted-foreground leading-relaxed">
            Form đa bước dễ sử dụng hướng dẫn bạn tạo CV hoàn chỉnh. Có thể nhập
            CV hiện có hoặc bắt đầu từ đầu.
          </p>
        </div>

        <div className="flex flex-col gap-4 rounded-lg border border-border bg-card p-6">
          <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-accent/10">
            <CheckCircle className="h-6 w-6 text-accent" />
          </div>
          <h3 className="text-xl font-semibold">Quản Lý Nhiều CV</h3>
          <p className="text-muted-foreground leading-relaxed">
            Tạo và quản lý nhiều CV cho các ứng tuyển việc làm khác nhau. Giữ
            mọi thứ có tổ chức ở một nơi.
          </p>
        </div>
      </div>
    </section>
  );
}
