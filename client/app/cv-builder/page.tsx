"use client";

import { useEffect } from "react";
import { Card } from "@/components/ui/card";
import { useCVStore } from "@/stores/cvStore";
import { CVBuilderWizard } from "@/components/comons/cv-builder/CVBuilderWizard";
import { AIPanel } from "@/components/comons/cv-builder/AI-powered/AIPanel";

export default function CVBuilderPage() {
  const { handleSetCurrentStep } = useCVStore();

  useEffect(() => {
    handleSetCurrentStep(0);
  }, [handleSetCurrentStep]);

  return (
    <div className="container mx-auto max-w-7xl py-12 px-4 sm:px-6 lg:px-8">
      <div className="flex flex-col gap-8">
        <div>
          <h1 className="text-3xl font-bold">Tạo CV</h1>
          <p className="text-muted-foreground">
            Tạo CV chuyên nghiệp của bạn từng bước một
          </p>
        </div>

        <div className="grid gap-6 lg:grid-cols-[1fr_400px]">
          <Card className="p-6">
            <CVBuilderWizard />
          </Card>

          <AIPanel />
        </div>
      </div>
    </div>
  );
}
