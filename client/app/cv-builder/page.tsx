"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { Card } from "@/components/ui/card";
import { useAuthStore } from "@/stores/authStore";
import { useCVStore } from "@/stores/cvStore";
import { CVBuilderWizard } from "@/components/cv-builder/CVBuilderWizard";
import { AISuggestionsSidebar } from "@/components/cv-builder/AI-powered/AISuggestionsSidebar";

export default function CVBuilderPage() {
  const router = useRouter();
  const { userAuth } = useAuthStore();
  const { currentCV, handleSetCurrentCV, handleSetCurrentStep } = useCVStore();

  useEffect(() => {
    // Initialize new CV if none exists
    if (!currentCV) {
      handleSetCurrentCV({
        id: crypto.randomUUID(),
        title: "Untitled CV",
        personalInfo: {
          fullname: "",
          email: "",
          phone: "",
          location: "",
          summary: "",
        },
        experiences: [],
        educations: [],
        skills: [],
      });
      handleSetCurrentStep(0);
    }
  }, [userAuth, currentCV, router, handleSetCurrentCV, handleSetCurrentStep]);

  return (
    <div className="container mx-auto max-w-7xl py-12 px-4 sm:px-6 lg:px-8">
      <div className="flex flex-col gap-8">
        <div>
          <h1 className="text-3xl font-bold">Tạo CV</h1>
          <p className="text-muted-foreground">
            Tạo CV chuyên nghiệp của bạn từng bước một
          </p>
        </div>

        <div className="grid gap-6 lg:grid-cols-[1fr_350px]">
          <Card className="p-6">
            <CVBuilderWizard />
          </Card>

          <AISuggestionsSidebar />
        </div>
      </div>
    </div>
  );
}
