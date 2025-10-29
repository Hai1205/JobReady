"use client";

import { useCVStore } from "@/stores/cvStore";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Lightbulb, AlertCircle, XCircle, Check, X } from "lucide-react";
import AISuggestionCard from "./AISuggestionCard";

interface AISuggestionsListProps {
  onApplySuggestion?: (suggestion: IAISuggestion) => void;
  isApplying?: boolean;
}

export function AISuggestionsList({
  onApplySuggestion,
  isApplying = false,
}: AISuggestionsListProps) {
  const { aiSuggestions, handleApplySuggestion } = useCVStore();

  const handleApply = (suggestion: IAISuggestion) => {
    handleApplySuggestion(suggestion.id);
    if (onApplySuggestion) {
      onApplySuggestion(suggestion);
    }
  };

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Lightbulb className="h-5 w-5" />
          Gợi Ý AI ({aiSuggestions.length})
        </CardTitle>
        <CardDescription>
          {aiSuggestions.length === 0
            ? "No suggestions available yet"
            : "Review and apply suggestions to improve your CV"}
        </CardDescription>
      </CardHeader>
      <CardContent>
        {aiSuggestions.length === 0 ? (
          <div className="text-center text-muted-foreground py-8">
            <Lightbulb className="h-12 w-12 mx-auto mb-2 opacity-20" />
            <p>Analyze your CV to get AI-powered suggestions</p>
          </div>
        ) : (
          <ScrollArea className="h-[500px] pr-4">
            <div className="space-y-4">
              {aiSuggestions.map((suggestion) => (
                <AISuggestionCard
                  key={suggestion.id}
                  suggestion={suggestion}
                  isApplying={isApplying}
                  onApply={handleApply}
                  onDismiss={handleApplySuggestion}
                />
              ))}
            </div>
          </ScrollArea>
        )}
      </CardContent>
    </Card>
  );
}
