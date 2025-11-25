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

  // Parse suggestion text to extract Before and After content (or raw)
  const parseSuggestionParts = (suggestionText: string) => {
    if (!suggestionText) return { before: null, after: null, raw: null };

    const lines = suggestionText.split("\n");
    let beforeContent: string | null = null;
    let afterContent: string | null = null;

    // Find Before line
    const beforeLineIndex = lines.findIndex((line) =>
      line.trim().startsWith("Before:")
    );
    if (beforeLineIndex !== -1) {
      const beforeLine = lines[beforeLineIndex];
      beforeContent = beforeLine
        .replace(/^Before:\s*/i, "")
        .trim()
        .replace(/^['"]|['"]$/g, ""); // Remove quotes
    }

    // Find After line
    const afterLineIndex = lines.findIndex((line) =>
      line.trim().startsWith("After:")
    );
    if (afterLineIndex !== -1) {
      const afterLine = lines[afterLineIndex];
      afterContent = afterLine
        .replace(/^After:\s*/i, "")
        .trim()
        .replace(/^['"]|['"]$/g, ""); // Remove quotes
    }

    // If there is no explicit Before line, treat this as a special/raw suggestion
    // In that case we do NOT want to show a 'Sau' label — show raw text only.
    if (!beforeContent && !afterContent) {
      return { before: null, after: null, raw: suggestionText };
    }

    return { before: beforeContent, after: afterContent, raw: null };
  };

  const { before, after, raw } = parseSuggestionParts(suggestion.suggestion);

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

            {suggestion.suggestion && (
              <div className="bg-muted p-3 rounded-md space-y-2">
                {before && (
                  <div>
                    <p className="text-xs font-medium text-red-600 mb-1">
                      Trước:
                    </p>
                    <p className="text-sm font-mono text-black bg-red-200 p-2 rounded border-l-2 border-red-200">
                      {before}
                    </p>
                  </div>
                )}
                {after && (
                  <div>
                    <p className="text-xs font-medium text-green-600 mb-1">
                      Sau:
                    </p>
                    <p className="text-sm font-mono text-black bg-green-200 p-2 rounded border-l-2 border-green-200">
                      {after}
                    </p>
                  </div>
                )}
                {!before && !after && (
                  <p className="text-sm font-mono text-black bg-green-200 p-2 rounded border-l-2 border-green-200">{suggestion.suggestion}</p>
                )}
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
