"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Home, Search, ShoppingBag, ArrowLeft } from "lucide-react";

export default function NotFound() {
  return (
    <div className="min-h-screen bg-gradient-to-br from-background to-muted">
      <div className="container mx-auto px-4 py-16">
        <div className="flex flex-col items-center justify-center min-h-[60vh] text-center">
          <Card className="w-full max-w-2xl">
            <CardContent className="p-12">
              {/* 404 Number */}
              <div className="mb-8">
                <h1 className="text-8xl md:text-9xl font-serif font-bold text-primary mb-4">
                  404
                </h1>

                <div className="w-24 h-1 bg-gradient-to-r from-primary to-secondary mx-auto rounded-full"></div>
              </div>

              {/* Error Message */}
              <div className="mb-8">
                <h2 className="text-2xl md:text-3xl font-serif font-bold text-foreground mb-4">
                  Trang không tồn tại
                </h2>

                <p className="text-muted-foreground text-lg mb-2">
                  Xin lỗi, chúng tôi không thể tìm thấy trang bạn đang tìm kiếm.
                </p>

                <p className="text-muted-foreground">
                  Trang có thể đã bị xóa, đổi tên hoặc tạm thời không khả dụng.
                </p>
              </div>

              {/* Action Buttons */}
              <div className="flex flex-col sm:flex-row gap-4 justify-center items-center">
                <Button asChild size="lg" className="w-full sm:w-auto">
                  <Link href="/" className="flex items-center gap-2">
                    <Home className="h-4 w-4" />
                    Về trang chủ
                  </Link>
                </Button>

                <Button
                  asChild
                  variant="outline"
                  size="lg"
                  className="w-full sm:w-auto bg-transparent"
                >
                  <Link href="/products" className="flex items-center gap-2">
                    <ShoppingBag className="h-4 w-4" />
                    Xem sản phẩm
                  </Link>
                </Button>
              </div>

              {/* Additional Help */}
              <div className="mt-8 pt-8 border-t">
                <p className="text-sm text-muted-foreground mb-4">
                  Hoặc bạn có thể thử:
                </p>

                <div className="flex flex-wrap justify-center gap-4 text-sm">
                  <Link
                    href="/products"
                    className="text-primary hover:underline flex items-center gap-1"
                  >
                    <Search className="h-3 w-3" />
                    Tìm kiếm sản phẩm
                  </Link>

                  <Link
                    href="/favorites"
                    className="text-primary hover:underline"
                  >
                    Danh sách yêu thích
                  </Link>

                  <Link href="/cart" className="text-primary hover:underline">
                    Giỏ hàng
                  </Link>
                </div>
              </div>
            </CardContent>
          </Card>

          {/* Back Button */}
          <Button
            variant="ghost"
            size="sm"
            onClick={() => window.history.back()}
            className="mt-6 text-muted-foreground hover:text-foreground"
          >
            <ArrowLeft className="h-4 w-4 mr-2" />
            Quay lại trang trước
          </Button>
        </div>
      </div>
    </div>
  );
}
