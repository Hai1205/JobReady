"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Upload, Sparkles, Loader2 } from "lucide-react";
import { toast } from "react-toastify";
import { useCVStore } from "@/stores/cvStore";
import { JobDescriptionMatchResult } from "./JobDescriptionMatchResult";

interface JobDescriptionImportProps {
  currentCV: ICV | null;
  onAnalysisComplete?: (
    suggestions: IAISuggestion[],
    matchScore?: number
  ) => void;
}

export function JobDescriptionImport({
  currentCV,
  onAnalysisComplete,
}: JobDescriptionImportProps) {
  const [jobDescription, setJobDescription] = useState("");
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [jdFile, setJdFile] = useState<File | null>(null);
  const [language, setLanguage] = useState<string>("vi");

  // State for analysis results
  const [matchScore, setMatchScore] = useState<number | undefined>(undefined);
  const [parsedJobDescription, setParsedJobDescription] = useState<
    IJobDescriptionResult | undefined
  >(undefined);
  const [missingKeywords, setMissingKeywords] = useState<string[]>([]);
  const [analyzeSummary, setAnalyzeSummary] = useState<string>("");

  const { analyzeCVWithJD, handleSetAISuggestions, handleSetJobDescription } =
    useCVStore();

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (!selectedFile) return;

    // Check file type
    const validTypes = [
      "text/plain",
      "application/pdf",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    ];
    if (!validTypes.includes(selectedFile.type)) {
      toast.error("Please upload a TXT, PDF, or DOCX file");
      return;
    }

    setJdFile(selectedFile);

    // For text files, preload content into textarea. For PDF/DOCX, backend will parse.
    if (selectedFile.type === "text/plain") {
      const reader = new FileReader();
      reader.onload = (event) => {
        const text = event.target?.result as string;
        setJobDescription(text);
        toast.success("Đã tải mô tả công việc từ file");
      };
      reader.onerror = () => {
        toast.error("Lỗi đọc file");
      };
      reader.readAsText(selectedFile);
    } else {
      toast.info(
        "File selected. The file will be uploaded and parsed by the server."
      );
    }
  };

  const handleAnalyze = async () => {
    if (!jobDescription.trim()) {
      if (!jdFile) {
        toast.error("Vui lòng nhập hoặc tải lên mô tả công việc");
        return;
      }
    }

    setIsAnalyzing(true);

    try {
      if (!currentCV) {
        toast.error("No CV available for analysis");
        return;
      }

      const response = await analyzeCVWithJD(
        jobDescription,
        jdFile,
        language,
        currentCV.title,
        currentCV.personalInfo,
        currentCV.experiences,
        currentCV.educations,
        currentCV.skills
      );

      const maybeResponse = (response as any).data;
      const responseData: IResponseData | undefined = maybeResponse?.data
        ? maybeResponse.data
        : maybeResponse;

      const suggestions = responseData?.suggestions || [];
      const score = responseData?.matchScore;
      const parsed = responseData?.parsedJobDescription;
      const missing = responseData?.missingKeywords || [];
      const summary = responseData?.analyze || "";

      // Update state with results
      setMatchScore(score);
      setParsedJobDescription(parsed);
      setMissingKeywords(missing);
      setAnalyzeSummary(summary);

      handleSetAISuggestions(suggestions);
      handleSetJobDescription(jobDescription || "");

      toast.success(
        `Analyze complete! Match score: ${score ? Math.round(score) : "N/A"}%`
      );

      if (onAnalysisComplete) {
        onAnalysisComplete(suggestions, score);
      }
    } catch (error) {
      console.error("Error analyzing CV:", error);
      toast.error("Đã xảy ra lỗi trong quá trình phân tích");
    } finally {
      setIsAnalyzing(false);
    }
  };

  return (
    <div className="space-y-4">
      <Card className="w-full">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Sparkles className="h-5 w-5" />
            Phân Tích Khớp Việc Bằng AI
          </CardTitle>
          <CardDescription>
            Tải lên hoặc dán mô tả công việc để phân tích mức độ CV của bạn khớp
            với yêu cầu
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div>
            <label
              htmlFor="jd-file-upload"
              className="block text-sm font-medium mb-2"
            >
              Tải Lên Mô Tả Công Việc (Tùy Chọn)
            </label>
            <div className="flex items-center gap-2">
              <input
                id="jd-file-upload"
                type="file"
                accept=".txt,.pdf,.docx"
                onChange={handleFileUpload}
                className="hidden"
              />
              <Button
                type="button"
                variant="outline"
                onClick={() =>
                  document.getElementById("jd-file-upload")?.click()
                }
                className="w-full"
              >
                <Upload className="mr-2 h-4 w-4" />
                {jdFile ? jdFile.name : "Chọn File"}
              </Button>
            </div>
          </div>

          <div>
            <label
              htmlFor="jd-textarea"
              className="block text-sm font-medium mb-2"
            >
              Mô Tả Công Việc
            </label>
            <div className="mb-2 flex items-center gap-2">
              <label className="text-sm">Ngôn Ngữ Xuất:</label>
              <select
                value={language}
                onChange={(e) => setLanguage(e.target.value)}
                className="rounded-md border px-2 py-1"
              >
                <option value="vi">Vietnamese</option>
                <option value="en">Tiếng Anh</option>
              </select>
            </div>
            <Textarea
              id="jd-textarea"
              placeholder="Dán mô tả công việc vào đây..."
              value={jobDescription}
              onChange={(e) => setJobDescription(e.target.value)}
              className="min-h-[200px] resize-y"
            />
          </div>

          <Button
            onClick={handleAnalyze}
            disabled={isAnalyzing || (!jobDescription.trim() && !jdFile)}
            className="w-full"
          >
            {isAnalyzing ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Đang Phân Tích...
              </>
            ) : (
              <>
                <Sparkles className="mr-2 h-4 w-4" />
                Phân Tích Độ Khớp
              </>
            )}
          </Button>
        </CardContent>
      </Card>

      {/* Display analysis results */}
      <JobDescriptionMatchResult
        parsedJobDescription={parsedJobDescription}
        matchScore={matchScore}
        missingKeywords={missingKeywords}
        analyzeSummary={analyzeSummary}
      />
    </div>
  );
}
