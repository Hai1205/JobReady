"use client";

import { useEffect } from "react";
import { useRouter, useParams } from "next/navigation";
import { Card } from "@/components/ui/card";
import { useAuthStore } from "@/stores/authStore";
import { useCVStore } from "@/stores/cvStore";
import { CVBuilderWizard } from "@/components/comons/cv-builder/CVBuilderWizard";
import { AIPanel } from "@/components/comons/cv-builder/AI-powered/AIPanel";
import { mockCVs } from "@/services/mockData";

export default function CVBuilderPage() {
  const router = useRouter();
  const params = useParams();

  const { userAuth } = useAuthStore();
  const { handleSetCurrentCVUpdate, handleSetCurrentStep, getCV } =
    useCVStore();

  useEffect(() => {
    const fetchCurrentCV = async () => {
      const id = params.id as string;
      const response = await getCV(id);

      if (response.data && response.data.success && response.data.cv) {
        handleSetCurrentStep(0);
        handleSetCurrentCVUpdate(response.data.cv);
      }
    };

    fetchCurrentCV();
  }, [
    userAuth,
    router,
    params,
    handleSetCurrentCVUpdate,
    handleSetCurrentStep,
    getCV,
  ]);

  return (
    <div className="container mx-auto max-w-7xl py-12 px-4 sm:px-6 lg:px-8">
      <div className="flex flex-col gap-8">
        <div>
          <h1 className="text-3xl font-bold">Sửa CV</h1>
          <p className="text-muted-foreground">
            Sửa CV chuyên nghiệp của bạn từng bước một
          </p>
        </div>

        <div className="grid gap-6 lg:grid-cols-[1fr_400px]">
          <Card className="p-6">
            <CVBuilderWizard mode="update" />
          </Card>

          <AIPanel />
        </div>
      </div>
    </div>
  );
}
