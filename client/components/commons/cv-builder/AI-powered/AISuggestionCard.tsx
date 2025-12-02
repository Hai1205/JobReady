import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { Check, X, Lightbulb, AlertCircle, XCircle } from "lucide-react";
import React from "react";

interface AISuggestionCardProps {
  suggestion: IAISuggestion;
  isApplying: boolean;
  onApply: (suggestion: IAISuggestion) => void;
  onDismiss: (id: string) => void;
}

export default function AISuggestionCard({
  suggestion,
  isApplying,
  onApply,
  onDismiss,
}: AISuggestionCardProps) {
  const getSuggestionIcon = (type: string) => {
    switch (type) {
      case "improvement":
        return <Lightbulb className="h-4 w-4 text-blue-500" />;
      case "warning":
        return <AlertCircle className="h-4 w-4 text-yellow-500" />;
      case "error":
        return <XCircle className="h-4 w-4 text-red-500" />;
      default:
        return <Lightbulb className="h-4 w-4 text-gray-500" />;
    }
  };

  const getSuggestionBadgeVariant = (
    type: string
  ): "default" | "secondary" | "destructive" | "outline" => {
    switch (type) {
      case "improvement":
        return "default";
      case "warning":
        return "secondary";
      case "error":
        return "destructive";
      default:
        return "outline";
    }
  };

  // Format data for display based on section type
  const formatDataForDisplay = (data: any, section: string) => {
    if (!data) return null;

    switch (section.toLowerCase()) {
      case "skills":
      case "skill":
      case "kỹ năng":
        return data.skills && Array.isArray(data.skills) ? data.skills : null;

      case "summary":
      case "personal info":
      case "personalinfo":
      case "thông tin cá nhân":
      case "title":
      case "tiêu đề":
      case "fullname":
      case "name":
      case "họ tên":
      case "email":
      case "phone":
      case "điện thoại":
      case "số điện thoại":
      case "location":
      case "địa chỉ":
      case "vị trí":
        return data.text || null;

      case "experience":
      case "experiences":
      case "kinh nghiệm":
      case "kinh nghiệm làm việc":
        return data.description || null;

      case "education":
      case "educations":
      case "học vấn":
        return data.field || data.degree || null;

      default:
        return null;
    }
  };

  const displayData = formatDataForDisplay(suggestion.data, suggestion.section);

  return (
    <Card className={suggestion.applied ? "opacity-50" : ""}>
      <CardContent className="pt-6">
        <div className="flex items-start gap-3">
          <div className="mt-1">{getSuggestionIcon(suggestion.type)}</div>
          <div className="flex-1 space-y-2">
            <div className="flex items-center gap-2 flex-wrap">
              <Badge variant={getSuggestionBadgeVariant(suggestion.type)}>
                {suggestion.type}
              </Badge>
              <Badge variant="outline">{suggestion.section}</Badge>
              {suggestion.applied && (
                <Badge variant="outline" className="text-green-600">
                  <Check className="h-3 w-3 mr-1" />
                  Applied
                </Badge>
              )}
            </div>

            <div>
              <p className="font-medium text-sm">{suggestion.message}</p>
              {suggestion.lineNumber && (
                <p className="text-xs text-muted-foreground mt-1">
                  Line {suggestion.lineNumber}
                </p>
              )}
            </div>

            {displayData && (
              <div className="bg-muted p-3 rounded-md space-y-2">
                <p className="text-xs font-medium text-green-600 mb-1">
                  Dữ liệu đề xuất:
                </p>
                {Array.isArray(displayData) ? (
                  // Display skills as chips
                  <div className="flex flex-wrap gap-2">
                    {displayData.map((skill, index) => (
                      <Badge
                        key={index}
                        variant="secondary"
                        className="bg-green-100 text-green-800 border-green-300"
                      >
                        {skill}
                      </Badge>
                    ))}
                  </div>
                ) : (
                  // Display text content
                  <p className="text-sm text-black bg-green-100 p-2 rounded border-l-4 border-green-500 whitespace-pre-wrap">
                    {displayData}
                  </p>
                )}
              </div>
            )}

            {!displayData && suggestion.suggestion && (
              // Fallback to old format if no data provided
              <div className="bg-muted p-3 rounded-md">
                <p className="text-xs font-medium text-blue-600 mb-1">Gợi ý:</p>
                <p className="text-sm text-black bg-blue-50 p-2 rounded border-l-2 border-blue-300">
                  {suggestion.suggestion}
                </p>
              </div>
            )}

            {!suggestion.applied && (
              <div className="flex gap-2">
                <Button
                  size="sm"
                  onClick={() => onApply(suggestion)}
                  className="flex-1"
                  disabled={isApplying}
                  title="Áp dụng gợi ý này vào CV của bạn"
                >
                  <Check className="h-3 w-3 mr-1" />
                  {isApplying ? "Đang áp dụng..." : "Áp dụng"}
                </Button>
                <Button
                  size="sm"
                  variant="outline"
                  onClick={() => onDismiss(suggestion.id)}
                  disabled={isApplying}
                  title="Bỏ qua gợi ý này"
                >
                  <X className="h-3 w-3 mr-1" />
                  Bỏ qua
                </Button>
              </div>
            )}
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
