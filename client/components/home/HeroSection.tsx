"use client";

import Link from "next/link";
import { Button } from "@/components/ui/button";
import { FileText, Sparkles } from "lucide-react";

export default function HeroSection() {
  return (
    <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex flex-col items-center justify-center gap-8 py-24 md:py-32">
      <div className="flex flex-col items-center gap-4 text-center">
        <h1 className="text-4xl font-bold tracking-tight sm:text-5xl md:text-6xl lg:text-7xl text-balance">
          Build Your Perfect CV with{" "}
          <span className="bg-gradient-to-r from-primary to-secondary bg-clip-text text-transparent">
            AI Assistance
          </span>
        </h1>
        <p className="max-w-[700px] text-lg text-muted-foreground text-balance md:text-xl leading-relaxed">
          Create professional, ATS-friendly resumes in minutes. Get AI-powered
          suggestions to improve your content and stand out from the crowd.
        </p>
      </div>

      <div className="flex flex-col gap-4 sm:flex-row">
        <Link href="/cv-builder">
          <Button size="lg" className="gap-2">
            <Sparkles className="h-5 w-5" />
            Start Building
          </Button>
        </Link>
        <Link href="/my-cvs">
          <Button size="lg" variant="outline" className="gap-2 bg-transparent">
            <FileText className="h-5 w-5" />
            My CVs
          </Button>
        </Link>
      </div>
    </section>
  );
}
