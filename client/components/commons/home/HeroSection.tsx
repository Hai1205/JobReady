"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import { FileText, Sparkles } from "lucide-react";
import { useAuthStore } from "@/stores/authStore";
import { useCVStore } from "@/stores/cvStore";
import { useRouter } from "next/navigation";
import Typewriter from "typewriter-effect";

export default function HeroSection() {
  const { userAuth } = useAuthStore();
  const { createCV } = useCVStore();

  const router = useRouter();

  const handleCreate = async () => {
    await createCV(userAuth);
    router.push("/cv-builder");
  };

  return (
    <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col items-center justify-center gap-8 py-24 md:py-32">
      <div className="flex flex-col items-center gap-4 text-center">
        <h1 className="text-4xl font-bold tracking-tight sm:text-5xl md:text-6xl lg:text-7xl text-balance">
          Xây Dựng CV Với
          <Typewriter
            options={{
              strings: [
                "Trợ Lý AI",
                "Công Nghệ AI",
                "AI Thông Minh",
                "AI Tiên Tiến",
              ],
              autoStart: true,
              loop: true,
              deleteSpeed: 50,
              wrapperClassName:
                "bg-gradient-to-br from-primary to-secondary bg-clip-text text-transparent",
            }}
          />
        </h1>
        <p className="max-w-[700px] text-lg text-muted-foreground text-balance md:text-xl leading-relaxed">
          Tạo CV chuyên nghiệp, thân thiện với hệ thống ATS chỉ trong vài phút.
          Nhận gợi ý từ AI để cải thiện nội dung và nổi bật giữa đám đông.
        </p>
      </div>

      <div className="flex flex-col gap-4 sm:flex-row">
        <Link href="/cv-builder">
          <Button size="lg" className="gap-2" onClick={handleCreate}>
            <Sparkles className="h-5 w-5" />
            Bắt Đầu Tạo
          </Button>
        </Link>
        <Link href="/my-cvs">
          <Button size="lg" variant="outline" className="gap-2 bg-transparent">
            <FileText className="h-5 w-5" />
            CV Của Tôi
          </Button>
        </Link>
      </div>
    </section>
  );
}
