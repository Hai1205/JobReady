"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Upload, Loader2 } from "lucide-react";

interface ImportFileDialogProps {
  onImport: (file: File | null) => Promise<boolean>;
}

export default function ImportFileDialog({ onImport }: ImportFileDialogProps) {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file && file.type === "application/pdf") {
      setSelectedFile(file);
    }
  };

  const handleImportClick = async () => {
    if (!selectedFile) return;

    setIsDialogOpen(false);
    setIsLoading(true);
    try {
      await onImport(selectedFile);
    } finally {
      setIsLoading(false);
      setSelectedFile(null);
    }
  };

  return (
    <>
      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogTrigger asChild>
          <Button
            variant="outline"
            className="border-primary text-primary hover:bg-primary/10 shadow-md hover:shadow-lg transition-all duration-200 hover:scale-105"
          >
            <Upload className="mr-2 h-4 w-4" />
            Import File
          </Button>
        </DialogTrigger>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Import CV từ file PDF</DialogTitle>
            <DialogDescription>
              Chọn file PDF để import thông tin CV của bạn
            </DialogDescription>
          </DialogHeader>
          <div className="flex flex-col gap-4 py-4">
            <div className="flex items-center justify-center w-full">
              <label
                htmlFor="dropzone-file"
                className="flex flex-col items-center justify-center w-full h-64 border-2 border-dashed rounded-lg cursor-pointer bg-gray-50 dark:bg-slate-900/50 hover:bg-gray-100 dark:hover:bg-slate-800/50 border-gray-300 dark:border-slate-600 hover:border-primary dark:hover:border-slate-500 transition-colors"
              >
                <div className="flex flex-col items-center justify-center pt-5 pb-6">
                  <Upload className="w-10 h-10 mb-3 text-gray-400 dark:text-slate-400" />
                  <p className="mb-2 text-sm text-gray-500 dark:text-slate-300">
                    <span className="font-semibold">Click để chọn file</span>{" "}
                    hoặc kéo thả
                  </p>
                  <p className="text-xs text-gray-500 dark:text-slate-400">
                    Chỉ chấp nhận file PDF
                  </p>
                  {selectedFile && (
                    <p className="mt-2 text-sm text-primary font-medium">
                      {selectedFile.name}
                    </p>
                  )}
                </div>
                <input
                  id="dropzone-file"
                  type="file"
                  className="hidden"
                  accept="application/pdf"
                  onChange={handleFileChange}
                />
              </label>
            </div>
            <Button
              onClick={handleImportClick}
              disabled={!selectedFile || isLoading}
              className="w-full"
            >
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Đang xử lý...
                </>
              ) : (
                "Import CV"
              )}
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      {/* Loading Overlay */}
      {isLoading && (
        <div className="fixed inset-0 bg-black/50 backdrop-blur-sm z-50 flex items-center justify-center">
          <div className="flex flex-col items-center gap-4">
            <Loader2 className="h-12 w-12 animate-spin text-white" />
            <p className="text-white text-lg font-medium">Đang xử lý file...</p>
          </div>
        </div>
      )}
    </>
  );
}
