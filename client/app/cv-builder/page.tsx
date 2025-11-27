"use client";

import { useEffect, useState } from "react";
import { Card } from "@/components/ui/card";
import { useCVStore } from "@/stores/cvStore";
import { CVBuilderWizard } from "@/components/commons/cv-builder/CVBuilderWizard";
import { AIPanel } from "@/components/commons/cv-builder/AI-powered/AIPanel";
import { toast } from "react-toastify";

export default function CVBuilderPage() {
  const { handleSetCurrentStep } = useCVStore();
  const [isDraggingOnPage, setIsDraggingOnPage] = useState(false);
  const [droppedFile, setDroppedFile] = useState<File | null>(null);
  const [accordionValue, setAccordionValue] = useState<string>("");

  useEffect(() => {
    handleSetCurrentStep(0);
  }, [handleSetCurrentStep]);

  // Page-level drag and drop handlers
  const handlePageDragEnter = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.dataTransfer.types.includes("Files")) {
      setIsDraggingOnPage(true);
    }
  };

  const handlePageDragLeave = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.currentTarget === e.target) {
      setIsDraggingOnPage(false);
    }
  };

  const handlePageDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
  };

  const handlePageDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDraggingOnPage(false);

    const file = e.dataTransfer.files?.[0];
    const validTypes = [
      "text/plain",
      "application/pdf",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    ];

    if (file && validTypes.includes(file.type)) {
      setDroppedFile(file);
      setAccordionValue("job-match");
    } else if (file) {
      toast.error("Chỉ hỗ trợ file TXT, PDF, DOCX!");
    }
  };

  const handleFileProcessed = () => {
    setDroppedFile(null);
  };

  return (
    <div
      className="container mx-auto max-w-7xl py-12 px-4 sm:px-6 lg:px-8 relative"
      onDragEnter={handlePageDragEnter}
      onDragLeave={handlePageDragLeave}
      onDragOver={handlePageDragOver}
      onDrop={handlePageDrop}
    >
      {/* Drag Overlay */}
      {isDraggingOnPage && (
        <div className="fixed inset-0 bg-primary/10 backdrop-blur-sm z-50 flex items-center justify-center pointer-events-none">
          <div className="bg-background border-2 border-dashed border-primary rounded-lg p-12 shadow-2xl">
            <div className="flex flex-col items-center gap-4">
              <div className="w-20 h-20 rounded-full bg-primary/20 flex items-center justify-center">
                <svg
                  className="w-10 h-10 text-primary"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"
                  />
                </svg>
              </div>
              <div className="text-center">
                <p className="text-2xl font-bold text-primary mb-2">
                  Thả file JD vào đây
                </p>
                <p className="text-sm text-muted-foreground">
                  để phân tích CV của bạn
                </p>
              </div>
            </div>
          </div>
        </div>
      )}

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

          <AIPanel
            externalFile={droppedFile}
            onExternalFileProcessed={handleFileProcessed}
            accordionValue={accordionValue}
            onAccordionChange={setAccordionValue}
          />
        </div>
      </div>
    </div>
  );
}
