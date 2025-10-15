"use client";

import { useCVStore } from "@/stores/cvStore";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Lightbulb, AlertCircle, XCircle, Check, X } from "lucide-react";

interface AISuggestionsListProps {
  onApplySuggestion?: (suggestion: IAISuggestion) => void;
}

export function AISuggestionsList({
  onApplySuggestion,
}: AISuggestionsListProps) {
  const { aiSuggestions, handleApplySuggestion } = useCVStore();

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

  const handleApply = (suggestion: IAISuggestion) => {
    handleApplySuggestion(suggestion.id);
    if (onApplySuggestion) {
      onApplySuggestion(suggestion);
    }
  };

  if (aiSuggestions.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>AI Suggestions</CardTitle>
          <CardDescription>No suggestions available yet</CardDescription>
        </CardHeader>
        <CardContent className="text-center text-muted-foreground py-8">
          <Lightbulb className="h-12 w-12 mx-auto mb-2 opacity-20" />
          <p>Analyze your CV to get AI-powered suggestions</p>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Lightbulb className="h-5 w-5" />
          AI Suggestions ({aiSuggestions.length})
        </CardTitle>
        <CardDescription>
          Review and apply suggestions to improve your CV
        </CardDescription>
      </CardHeader>
      <CardContent>
        <ScrollArea className="h-[500px] pr-4">
          <div className="space-y-4">
            {aiSuggestions.map((suggestion) => (
              <Card
                key={suggestion.id}
                className={suggestion.applied ? "opacity-50" : ""}
              >
                <CardContent className="pt-6">
                  <div className="flex items-start gap-3">
                    <div className="mt-1">
                      {getSuggestionIcon(suggestion.type)}
                    </div>
                    <div className="flex-1 space-y-2">
                      <div className="flex items-center gap-2 flex-wrap">
                        <Badge
                          variant={getSuggestionBadgeVariant(suggestion.type)}
                        >
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
                        <p className="font-medium text-sm">
                          {suggestion.message}
                        </p>
                        {suggestion.lineNumber && (
                          <p className="text-xs text-muted-foreground mt-1">
                            Line {suggestion.lineNumber}
                          </p>
                        )}
                      </div>

                      {suggestion.suggestion && (
                        <div className="bg-muted p-3 rounded-md">
                          <p className="text-sm font-mono">
                            {suggestion.suggestion}
                          </p>
                        </div>
                      )}

                      {!suggestion.applied && (
                        <div className="flex gap-2">
                          <Button
                            size="sm"
                            onClick={() => handleApply(suggestion)}
                            className="flex-1"
                          >
                            <Check className="h-3 w-3 mr-1" />
                            Apply
                          </Button>
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => handleApplySuggestion(suggestion.id)}
                          >
                            <X className="h-3 w-3 mr-1" />
                            Dismiss
                          </Button>
                        </div>
                      )}
                    </div>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </ScrollArea>
      </CardContent>
    </Card>
  );
}
