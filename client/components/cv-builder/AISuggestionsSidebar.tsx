"use client";

import { useState } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import {
  Sparkles,
  CheckCircle,
  AlertTriangle,
  Lightbulb,
  Loader2,
  Check,
} from "lucide-react";
import { ScrollArea } from "@/components/ui/scroll-area";
import { useCVStore } from "@/stores/cvStore";

export function AISuggestionsSidebar() {
  const {
    currentCV,
    aiSuggestions,
    handleApplySuggestion,
    analyzeCV,
  } = useCVStore();
  const [analyzing, setAnalyzing] = useState(false);

  const handleAnalyze = async () => {
    if (!currentCV) return;

    setAnalyzing(true);

    analyzeCV(currentCV.id);
  };

  const getSuggestionIcon = (type: string) => {
    switch (type) {
      case "improvement":
        return <Sparkles className="h-4 w-4" />;
      case "warning":
        return <AlertTriangle className="h-4 w-4" />;
      case "tip":
        return <Lightbulb className="h-4 w-4" />;
      default:
        return <CheckCircle className="h-4 w-4" />;
    }
  };

  const getSuggestionColor = (type: string) => {
    switch (type) {
      case "improvement":
        return "bg-blue-500/10 text-blue-500 border-blue-500/20";
      case "warning":
        return "bg-yellow-500/10 text-yellow-500 border-yellow-500/20";
      case "tip":
        return "bg-green-500/10 text-green-500 border-green-500/20";
      default:
        return "bg-muted text-muted-foreground";
    }
  };

  return (
    <Card className="flex h-fit flex-col gap-4 p-6 lg:sticky lg:top-24">
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-2">
          <Sparkles className="h-5 w-5 text-primary" />
          <h3 className="font-semibold">AI Suggestions</h3>
        </div>
        {aiSuggestions.length > 0 && (
          <Badge variant="secondary">
            {aiSuggestions.filter((s) => !s.applied).length} pending
          </Badge>
        )}
      </div>

      <Button
        onClick={handleAnalyze}
        disabled={analyzing || !currentCV}
        className="w-full"
      >
        {analyzing ? (
          <>
            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
            Analyzing...
          </>
        ) : (
          <>
            <Sparkles className="mr-2 h-4 w-4" />
            Analyze CV
          </>
        )}
      </Button>

      {aiSuggestions.length > 0 ? (
        <ScrollArea className="h-[600px] pr-4">
          <div className="flex flex-col gap-3">
            {aiSuggestions.map((suggestion) => (
              <div
                key={suggestion.id}
                className={`rounded-lg border p-4 transition-all ${
                  suggestion.applied ? "opacity-50" : ""
                } ${getSuggestionColor(suggestion.type)}`}
              >
                <div className="flex items-start gap-3">
                  <div className="mt-0.5">
                    {getSuggestionIcon(suggestion.type)}
                  </div>
                  <div className="flex-1">
                    <div className="mb-2 flex items-start justify-between gap-2">
                      <div>
                        <p className="text-sm font-medium">
                          {suggestion.section}
                        </p>
                        {suggestion.lineNumber && (
                          <p className="text-xs opacity-70">
                            Line {suggestion.lineNumber}
                          </p>
                        )}
                      </div>
                      {suggestion.applied && (
                        <Check className="h-4 w-4 text-green-500" />
                      )}
                    </div>
                    <p className="mb-2 text-sm font-medium">
                      {suggestion.message}
                    </p>
                    <p className="mb-3 text-xs opacity-80">
                      {suggestion.suggestion}
                    </p>
                    {!suggestion.applied && (
                      <Button
                        size="sm"
                        variant="outline"
                        onClick={() => handleApplySuggestion(suggestion.id)}
                        className="w-full"
                      >
                        Apply Suggestion
                      </Button>
                    )}
                  </div>
                </div>
              </div>
            ))}
          </div>
        </ScrollArea>
      ) : (
        <div className="flex flex-col items-center gap-2 rounded-lg border border-dashed border-border p-8 text-center">
          <Sparkles className="h-8 w-8 text-muted-foreground" />
          <p className="text-sm text-muted-foreground">
            Click "Analyze CV" to get AI-powered suggestions for improving your
            resume
          </p>
        </div>
      )}
    </Card>
  );
}
