"use client";

import { useState } from "react";
import { useAIStore } from "@/stores/aiStore";
import { useCVStore } from "@/stores/cvStore";
import { CompactJobMatch } from "./CompactJobMatch";
import { Button } from "@/components/ui/button";
import { Sparkles, Loader2 } from "lucide-react";
import { toast } from "react-toastify";
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from "@/components/ui/accordion";

/**
 * AIToolsSidebar - Compact sidebar version of AI tools
 * Includes Quick Analyze and Job Match Analysis in a compact format
 */
export function AIToolsSidebar() {
  const [matchScore, setMatchScore] = useState<number | undefined>(undefined);
  const [isAnalyzing, setIsAnalyzing] = useState(false);

  const {
    analyzeCV,
    handleSetAISuggestions,
    handleSetIsAnalyzing,
    isAnalyzing: globalIsAnalyzing,
  } = useAIStore();
  const { isLoading } = useCVStore();
  const { currentCV } = useCVStore();

  const handleQuickAnalyze = async () => {
    if (!currentCV) {
      toast.error("Vui lòng điền thông tin CV trước");
      return;
    }

    setIsAnalyzing(true);
    handleSetIsAnalyzing(true);
    try {
      const response = await analyzeCV(
        currentCV?.title,
        currentCV?.personalInfo,
        currentCV?.experiences,
        currentCV?.educations,
        currentCV?.skills
      );

      const maybeResponse = (response as any).data;
      const responseData: any = maybeResponse?.data
        ? maybeResponse.data
        : maybeResponse;
      console.log("AIToolsSidebar responseData:", responseData);

      // Extract suggestions - they can be at root level or inside analyze object
      const suggestions =
        responseData?.analyze?.suggestions || responseData?.suggestions || [];

      console.log("AIToolsSidebar extracted suggestions:", suggestions);

      if (suggestions.length > 0) {
        handleSetAISuggestions(suggestions);
        const score = responseData?.matchScore;
        setMatchScore(score);
        toast.success(
          `Phân tích hoàn tất! Tìm thấy ${suggestions.length} gợi ý`
        );
      } else {
        toast.error((response as any)?.message || "Phân tích thất bại");
      }
    } catch (error) {
      console.error("Error analyzing CV:", error);
      toast.error("Đã xảy ra lỗi khi phân tích");
    } finally {
      setIsAnalyzing(false);
      handleSetIsAnalyzing(false);
    }
  };

  return (
    <div className="space-y-4">
      {/* Match Score Display for Quick Analyze */}
      {matchScore !== undefined && (
        <div className="p-4 rounded-lg bg-muted border">
          <div className="flex items-center justify-between mb-2">
            <span className="text-sm font-medium">Điểm Tương Đồng:</span>
            <span
              className={`text-2xl font-bold ${
                matchScore >= 80
                  ? "text-green-600"
                  : matchScore >= 60
                  ? "text-yellow-600"
                  : "text-red-600"
              }`}
            >
              {Math.round(matchScore)}%
            </span>
          </div>
          <div className="h-2 bg-background rounded-full overflow-hidden">
            <div
              className={`h-full transition-all ${
                matchScore >= 80
                  ? "bg-green-600"
                  : matchScore >= 60
                  ? "bg-yellow-600"
                  : "bg-red-600"
              }`}
              style={{ width: `${matchScore}%` }}
            />
          </div>
        </div>
      )}

      {/* Quick Analyze Button */}
      <Button
        onClick={handleQuickAnalyze}
        disabled={globalIsAnalyzing || isLoading || !currentCV}
        className="w-full"
        size="lg"
      >
        {isAnalyzing ? (
          <>
            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
            Đang phân tích...
          </>
        ) : (
          <>
            <Sparkles className="mr-2 h-4 w-4" />
            Phân tích nhanh
          </>
        )}
      </Button>

      {/* Accordion for advanced features */}
      <Accordion type="single" collapsible className="w-full">
        <AccordionItem value="job-match">
          <AccordionTrigger className="text-sm font-medium">
            Phân Tích Với Mô Tả Công Việc
          </AccordionTrigger>
          <AccordionContent>
            <CompactJobMatch
              currentCV={currentCV}
              onAnalysisComplete={(suggestions) => {
                toast.success("Phân tích hoàn tất!");
              }}
            />
          </AccordionContent>
        </AccordionItem>
      </Accordion>

      {/* Info text */}
      <div className="rounded-lg bg-muted p-3 text-xs text-muted-foreground">
        <p className="mb-2">
          💡 <strong>Mẹo:</strong>
        </p>
        <ul className="list-inside list-disc space-y-1">
          <li>Phân tích CV để nhận gợi ý cải thiện</li>
          <li>So sánh với mô tả công việc để tối ưu</li>
          <li>Áp dụng gợi ý AI để tăng cơ hội</li>
        </ul>
      </div>
    </div>
  );
}
