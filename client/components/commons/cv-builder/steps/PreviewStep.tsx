"use client";

import React, { useEffect, useMemo } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Download, Loader2 } from "lucide-react";
import { useCVStore } from "@/stores/cvStore";
import { generateCVHTML } from "../templates/templates";

export function PreviewStep() {
  const { currentCV, handleGeneratePDF } = useCVStore();

  const [isLoading, setIsLoading] = React.useState(false);

  // Convert File to base64 URL for display
  useEffect(() => {
    if (currentCV?.avatar && currentCV.avatar instanceof File) {
      const reader = new FileReader();
      reader.onloadend = () => {};
      reader.readAsDataURL(currentCV.avatar);
    }
  }, [currentCV?.avatar]);

  // Generate HTML from template
  const cvHtml = useMemo(() => {
    if (!currentCV) return "";
    return generateCVHTML(currentCV, currentCV.template);
  }, [currentCV]);

  // if (!currentCV) return null;

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

      {/* PDF Preview */}
      <Card className="overflow-hidden bg-white shadow-2xl border-border/50">
        {/* A4 Preview Container with HTML Template */}
        <div
          id="cv-preview-content"
          dangerouslySetInnerHTML={{ __html: cvHtml }}
        />
      </Card>

      {/* Action Buttons */}
      {/* <div className="flex gap-4">
        <Button onClick={generatePDF} size="lg" className="flex-1 gap-2">
          <Download className="h-5 w-5" />
          Tải CV (PDF)
        </Button>
        <Button variant="outline" size="lg" className="flex-1 gap-2">
          <FileText className="h-5 w-5" />
          Lưu Nháp
        </Button>
      </div> */}
    </div>
  );
}
