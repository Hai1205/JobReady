"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import { Label } from "@/components/ui/label";
import { RadioGroup, RadioGroupItem } from "@/components/ui/radio-group";
import { Upload, Sparkles, Loader2 } from "lucide-react";
import { toast } from "react-toastify";
import { useAIStore } from "@/stores/aiStore";

interface CompactJobMatchProps {
  currentCV: ICV | null;
  onAnalysisComplete?: (
    suggestions: IAISuggestion[],
    matchScore?: number
  ) => void;
  isQuickAnalyzing?: boolean;
}

/**
 * CompactJobMatch - Compact version for sidebar
 */
export function CompactJobMatch({
  currentCV,
  onAnalysisComplete,
  isQuickAnalyzing = false,
}: CompactJobMatchProps) {
  const [jobDescription, setJobDescription] = useState("");
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [jdFile, setJdFile] = useState<File | null>(null);
  const [inputMethod, setInputMethod] = useState<"text" | "file">("text");

  const { analyzeCVWithJD, handleSetAISuggestions } = useAIStore();

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (!selectedFile) return;

    const validTypes = [
      "text/plain",
      "application/pdf",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    ];
    if (!validTypes.includes(selectedFile.type)) {
      toast.error("Chỉ hỗ trợ file TXT, PDF, DOCX");
      return;
    }

    setJdFile(selectedFile);

    if (selectedFile.type === "text/plain") {
      const reader = new FileReader();
      reader.onload = (event) => {
        const text = event.target?.result as string;
        setJobDescription(text);
      };
      reader.readAsText(selectedFile);
    }
  };

  const handleAnalyze = async () => {
    if (!jobDescription.trim() && !jdFile) {
      toast.error("Vui lòng nhập hoặc tải lên mô tả công việc");
      return;
    }

    if (!currentCV) {
      toast.error("Vui lòng điền thông tin CV trước");
      return;
    }

    setIsAnalyzing(true);

    try {
      const response = await analyzeCVWithJD(
        jobDescription,
        jdFile,
        "vi",
        currentCV.title,
        currentCV.personalInfo,
        currentCV.experiences,
        currentCV.educations,
        currentCV.skills
      );

      const backendData = response.data;

      // Extract suggestions - they can be at root level or inside analyze object
      const suggestions =
        backendData?.analyze?.suggestions || backendData?.suggestions || [];

      const score = backendData?.matchScore;
      const missing = backendData?.missingKeywords || [];

      console.log("CompactJobMatch - suggestions:", suggestions);

      // Store suggestions in CV store for AI Suggestions Sidebar
      handleSetAISuggestions(suggestions);

      // Show detailed success message
      const scoreText = score ? `${Math.round(score)}%` : "N/A";
      const suggestionsText =
        suggestions.length > 0
          ? `\n📋 ${suggestions.length} gợi ý cải thiện`
          : "";
      const missingText =
        missing.length > 0 ? `\n🔑 ${missing.length} từ khóa thiếu` : "";

      toast.success(
        `✅ Phân tích xong!\n🎯 Điểm khớp: ${scoreText}${suggestionsText}${missingText}\n\n💡 Xem tab "Gợi Ý"`,
        {
          autoClose: 4000,
        }
      );

      if (onAnalysisComplete) {
        onAnalysisComplete(suggestions, score);
      }
    } catch (error) {
      console.error("Error analyzing CV:", error);
      toast.error("Đã xảy ra lỗi khi phân tích");
    } finally {
      setIsAnalyzing(false);
    }
  };

  return (
    <div className="space-y-3">
      {/* Radio Group for Input Method */}
      <div>
        <Label className="text-xs mb-2 block">Chọn cách nhập JD:</Label>
        <RadioGroup
          value={inputMethod}
          onValueChange={(value) => setInputMethod(value as "text" | "file")}
          className="flex flex-col space-y-2"
        >
          <div className="flex items-center space-x-2">
            <RadioGroupItem value="text" id="text-method" />
            <Label
              htmlFor="text-method"
              className="text-xs font-normal cursor-pointer"
            >
              Nhập text mô tả công việc
            </Label>
          </div>
          <div className="flex items-center space-x-2">
            <RadioGroupItem value="file" id="file-method" />
            <Label
              htmlFor="file-method"
              className="text-xs font-normal cursor-pointer"
            >
              Tải file JD lên
            </Label>
          </div>
        </RadioGroup>
      </div>

      {/* Conditional Rendering Based on Selected Method */}
      {inputMethod === "file" ? (
        <div>
          <Label htmlFor="jd-file-compact" className="text-xs">
            Tải File JD
          </Label>
          <div className="mt-1">
            <input
              id="jd-file-compact"
              type="file"
              accept=".txt,.pdf,.docx"
              onChange={handleFileUpload}
              className="hidden"
            />
            <Button
              type="button"
              variant="outline"
              size="sm"
              onClick={() =>
                document.getElementById("jd-file-compact")?.click()
              }
              className="w-full"
            >
              <Upload className="mr-2 h-3 w-3" />
              {jdFile ? jdFile.name.slice(0, 20) + "..." : "Chọn File"}
            </Button>
          </div>
        </div>
      ) : (
        <div>
          <div className="flex items-center justify-between">
            <Label htmlFor="jd-text-compact" className="text-xs">
              Dán Mô Tả Công Việc
            </Label>
            <span className="text-xs text-muted-foreground">
              {jobDescription.length}/2000
            </span>
          </div>
          <Textarea
            id="jd-text-compact"
            placeholder="Paste job description here..."
            value={jobDescription}
            onChange={(e) => {
              // Truncate to 2000 characters if exceeded
              const truncatedValue =
                e.target.value.length > 2000
                  ? e.target.value.slice(0, 2000)
                  : e.target.value;
              setJobDescription(truncatedValue);
            }}
            className="mt-1 min-h-[120px] max-h-[400px] text-xs"
          />
        </div>
      )}

      <Button
        onClick={handleAnalyze}
        disabled={
          isAnalyzing ||
          isQuickAnalyzing ||
          (inputMethod === "text" && !jobDescription.trim()) ||
          (inputMethod === "file" && !jdFile)
        }
        className="w-full"
        size="sm"
      >
        {isAnalyzing ? (
          <>
            <Loader2 className="mr-2 h-3 w-3 animate-spin" />
            Đang phân tích...
          </>
        ) : (
          <>
            <Sparkles className="mr-2 h-3 w-3" />
            Phân tích
          </>
        )}
      </Button>
    </div>
  );
}
