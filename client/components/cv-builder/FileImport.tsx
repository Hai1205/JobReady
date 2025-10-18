"use client";

import type React from "react";

import { useState } from "react";
import { Upload, FileText } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useCVStore } from "@/stores/cvStore";
import { toast } from "react-toastify";
import { useAuthStore } from "@/stores/authStore";

export function FileImport() {
  const { handleSetCurrentCV, importFile } = useCVStore();
  const { userAuth } = useAuthStore();

  const [isUploading, setIsUploading] = useState(false);

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    // Check file type
    const validTypes = [
      "application/pdf",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    ];
    if (!validTypes.includes(file.type)) {
      toast.error("Please upload a PDF or DOCX file");
      return;
    }

    if (!userAuth?.id) {
      toast.error("User not authenticated. Please log in.");
      return;
    }

    setIsUploading(true);
    const res = await importFile(userAuth?.id, file);
    try {
      const maybeResponse = (res as any).data;
      const responseData = maybeResponse?.data
        ? maybeResponse.data
        : maybeResponse;

      if (responseData?.cv) {
        handleSetCurrentCV(responseData.cv as ICV);
        toast.success("CV imported successfully");
      } else {
        toast.error((res as any)?.message || "Failed to import CV");
      }
    } catch (err) {
      console.error(err);
      toast.error("An error occurred while importing the CV");
    }

    setIsUploading(false);
  };

  return (
    <div className="rounded-lg border-2 border-dashed border-border bg-muted/50 p-8">
      <div className="flex flex-col items-center gap-4 text-center">
        <div className="flex h-16 w-16 items-center justify-center rounded-full bg-primary/10">
          <Upload className="h-8 w-8 text-primary" />
        </div>
        <div>
          <h3 className="text-lg font-semibold">Import Existing CV</h3>
          <p className="text-sm text-muted-foreground">
            Upload a PDF or DOCX file to edit your existing CV
          </p>
        </div>
        <label htmlFor="file-upload">
          <Button variant="outline" disabled={isUploading} asChild>
            <span>
              <FileText className="mr-2 h-4 w-4" />
              {isUploading ? "Uploading..." : "Choose File"}
            </span>
          </Button>
          <input
            id="file-upload"
            type="file"
            accept=".pdf,.docx"
            onChange={handleFileUpload}
            className="hidden"
          />
        </label>
      </div>
    </div>
  );
}
