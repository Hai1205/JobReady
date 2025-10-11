import Link from "next/link";
import { Button } from "@/components/ui/button";
import { FileText, Sparkles, CheckCircle, Zap } from "lucide-react";

export default function HomePage() {
  return (
    <div className="flex flex-col">
      {/* Hero Section */}
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
            <Button
              size="lg"
              variant="outline"
              className="gap-2 bg-transparent"
            >
              <FileText className="h-5 w-5" />
              My CVs
            </Button>
          </Link>
        </div>
      </section>

      {/* Features Section */}
      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-24 md:py-32">
        <div className="grid gap-8 md:grid-cols-3">
          <div className="flex flex-col gap-4 rounded-lg border border-border bg-card p-6">
            <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-primary/10">
              <Sparkles className="h-6 w-6 text-primary" />
            </div>
            <h3 className="text-xl font-semibold">AI-Powered Suggestions</h3>
            <p className="text-muted-foreground leading-relaxed">
              Get real-time AI suggestions to improve your CV content, highlight
              key achievements, and optimize for ATS systems.
            </p>
          </div>

          <div className="flex flex-col gap-4 rounded-lg border border-border bg-card p-6">
            <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-secondary/10">
              <Zap className="h-6 w-6 text-secondary" />
            </div>
            <h3 className="text-xl font-semibold">Step-by-Step Builder</h3>
            <p className="text-muted-foreground leading-relaxed">
              Easy-to-use multi-step form guides you through creating a complete
              CV. Import existing CVs or start from scratch.
            </p>
          </div>

          <div className="flex flex-col gap-4 rounded-lg border border-border bg-card p-6">
            <div className="flex h-12 w-12 items-center justify-center rounded-lg bg-accent/10">
              <CheckCircle className="h-6 w-6 text-accent" />
            </div>
            <h3 className="text-xl font-semibold">Multiple CV Management</h3>
            <p className="text-muted-foreground leading-relaxed">
              Create and manage multiple CVs for different job applications.
              Keep everything organized in one place.
            </p>
          </div>
        </div>
      </section>
    </div>
  );
}
