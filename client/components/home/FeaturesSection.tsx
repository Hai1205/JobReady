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
            Create and manage multiple CVs for different job applications. Keep
            everything organized in one place.
          </p>
        </div>
      </div>
    </section>
  );
}
