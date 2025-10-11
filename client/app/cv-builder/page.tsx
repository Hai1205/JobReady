"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { CVBuilderWizard } from "@/components/cv-builder/cv-builder-wizard";
import { AISuggestionsSidebar } from "@/components/cv-builder/ai-suggestions-sidebar";
import { Card } from "@/components/ui/card";
import { useAuthStore } from "@/stores/authStore";
import { useCVStore } from "@/stores/cvStore";

export default function CVBuilderPage() {
  const router = useRouter();
  const { userAuth } = useAuthStore();
  const { currentCV, handleSetCurrentCV, handleSetCurrentStep } = useCVStore();

  useEffect(() => {
    if (!userAuth) {
      router.push("/login");
      return;
    }

    // Initialize new CV if none exists
    if (!currentCV) {
      handleSetCurrentCV({
        id: crypto.randomUUID(),
        title: "Untitled CV",
        personalInfo: {
          fullName: "",
          email: "",
          phone: "",
          location: "",
          summary: "",
        },
        experience: [],
        education: [],
        skills: [],
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      });
      handleSetCurrentStep(0);
    }
  }, [userAuth, currentCV, router, handleSetCurrentCV, handleSetCurrentStep]);

  if (!userAuth) {
    return null;
  }

  return (
    <div className="container py-12">
      <div className="flex flex-col gap-8">
        <div>
          <h1 className="text-3xl font-bold">CV Builder</h1>
          <p className="text-muted-foreground">
            Create your professional CV step by step
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
