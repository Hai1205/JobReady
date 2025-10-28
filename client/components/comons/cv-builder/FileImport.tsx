"use client";

import { useState, useRef } from "react";
import { Upload, FileText } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useCVStore } from "@/stores/cvStore";
import { toast } from "react-toastify";
import { useAuthStore } from "@/stores/authStore";
import { useCVParser } from "@/hooks/use-cv-parser";
import { CVPreviewDialog } from "./CVPreviewDialog";

export function FileImport() {
  const { handleSetCurrentCV } = useCVStore();
  const { userAuth } = useAuthStore();
  const { isProcessing, parseFile } = useCVParser();

  const [previewCV, setPreviewCV] = useState<ICV | null>(null);
  const [showPreview, setShowPreview] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleButtonClick = () => {
    console.log("Button clicked!");
    fileInputRef.current?.click();
  };

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    console.log("file upload triggered");
    const file = e.target.files?.[0];
    console.log("Selected file:", file);

    if (!file) {
      console.log("No file selected");
      return;
    }

    if (!userAuth?.id) {
      toast.error("Bạn chưa đăng nhập. Vui lòng đăng nhập.");
      // return;
    }

    try {
      console.log("Starting to parse file...");
      const result = await parseFile(file, "userid", {
      // const result = await parseFile(file, userAuth.id, {
        enhance: true,
        validate: true,
        showWarnings: true,
      });
      console.log("Parse result:", result);

      if (result) {
        setPreviewCV(result.cv);
        setShowPreview(true);

        if (result.score < 50) {
          toast.warning(
            `CV của bạn có ${result.score}% hoàn thiện. Hãy bổ sung thêm thông tin!`
          );
        } else if (result.score < 80) {
          toast.info(`CV của bạn có ${result.score}% hoàn thiện. Khá tốt!`);
        } else {
          toast.success(`CV của bạn có ${result.score}% hoàn thiện. Rất tốt!`);
        }
      }
    } catch (error) {
      console.error("Error in handleFileUpload:", error);
    } finally {
      e.target.value = "";
    }
  };

  const handleConfirmCV = (cv: ICV) => {
    handleSetCurrentCV(cv);
    setShowPreview(false);
    toast.success(
      "Nhập CV thành công! Vui lòng kiểm tra và chỉnh sửa thông tin nếu cần."
    );
  };

  const handleClosePreview = () => {
    setShowPreview(false);
    setPreviewCV(null);
  };

  return (
    <>
      <div className="rounded-lg border-2 border-dashed border-border bg-muted/50 p-8">
        <div className="flex flex-col items-center gap-4 text-center">
          <div className="flex h-16 w-16 items-center justify-center rounded-full bg-primary/10">
            <Upload className="h-8 w-8 text-primary" />
          </div>
          <div>
            <h3 className="text-lg font-semibold">Nhập CV Hiện Có</h3>
            <p className="text-sm text-muted-foreground">
              Đăng tải file PDF hoặc DOCX để tự động phân tích và chỉnh sửa CV
              của bạn
            </p>
            <p className="text-xs mt-2 text-yellow-600">
              ⚠️ Lưu ý: Dữ liệu được phân tích tự động có thể chưa chính xác
              hoàn toàn. Vui lòng kiểm tra kỹ!
            </p>
          </div>

          <input
            ref={fileInputRef}
            id="file-upload"
            type="file"
            accept=".pdf,.docx"
            onChange={handleFileUpload}
            className="hidden"
          />

          <Button
            variant="outline"
            disabled={isProcessing}
            onClick={handleButtonClick}
            type="button"
          >
            <FileText className="mr-2 h-4 w-4" />
            {isProcessing ? "Đang xử lý..." : "Chọn File"}
          </Button>
        </div>
      </div>

      <CVPreviewDialog
        cv={previewCV}
        open={showPreview}
        onClose={handleClosePreview}
        onConfirm={handleConfirmCV}
      />
    </>
  );
}
