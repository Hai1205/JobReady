"use client";

import React from "react";
import { Button } from "@/components/ui/button";
import { Download, Loader2 } from "lucide-react";
import { useCVStore } from "@/stores/cvStore";
import { CVPreviewCard } from "../CVPreviewCard";

export function PreviewStep() {
  const { currentCV, handleGeneratePDF } = useCVStore();
  const [isLoading, setIsLoading] = React.useState(false);

  const generatePDF = async () => {
    if (!currentCV) return;
    setIsLoading(true);
    await handleGeneratePDF(currentCV);
    setIsLoading(false);
  };

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold">Xem Trước CV</h2>
          <p className="text-muted-foreground">
            Kiểm tra lại thông tin và tải xuống CV của bạn
          </p>
        </div>

        <Button onClick={generatePDF} size="lg" className="gap-2">
          {isLoading ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Đang tải...
            </>
          ) : (
            <>
              <Download className="h-5 w-5" />
              Tải CV (PDF)
            </>
          )}
        </Button>
      </div>

      {/* Preview Card */}
      <CVPreviewCard currentCV={currentCV} />
    </div>
  );
}
