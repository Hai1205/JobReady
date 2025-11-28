"use client";

import { useEffect, useState } from "react";
import { Card } from "@/components/ui/card";
import { useCVStore } from "@/stores/cvStore";
import { CVBuilderWizard } from "@/components/commons/cv-builder/CVBuilderWizard";
import { AIPanel } from "@/components/commons/cv-builder/AI-powered/AIPanel";
import { toast } from "react-toastify";
import DraggingOnPage from "@/components/commons/layout/DraggingOnPage";
import { useAIStore } from "@/stores/aiStore";

export default function CVBuilderPage() {
  const { handleSetCurrentStep } = useCVStore();
  const { reset: resetAIStore } = useAIStore();
  const [isDraggingOnPage, setIsDraggingOnPage] = useState(false);
  const [droppedFile, setDroppedFile] = useState<File | null>(null);
  const [accordionValue, setAccordionValue] = useState<string>("");

  useEffect(() => {
    handleSetCurrentStep(0);
  }, [handleSetCurrentStep]);

  // Reset AI suggestions when leaving the page
  useEffect(() => {
    return () => {
      resetAIStore();
    };
  }, [resetAIStore]);

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
        <DraggingOnPage
          title="Thả file JD vào đây"
          subtitle="để phân tích CV của bạn"
        />
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
