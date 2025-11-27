"use client";

import { useState, useEffect } from "react";
import { Card } from "@/components/ui/card";
import { Sparkles } from "lucide-react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { AISuggestionsList } from "./AISuggestionsList";
import { AIToolsSidebar } from "./AIToolsSidebar";
import { useAIStore } from "@/stores/aiStore";
import { Badge } from "@/components/ui/badge";

interface AIPanelProps {
  externalFile?: File | null;
  onExternalFileProcessed?: () => void;
  accordionValue?: string;
  onAccordionChange?: (value: string) => void;
}

/**
 * AIPanel - Panel hiển thị tất cả tính năng AI ở sidebar
 * Bao gồm Quick Analyze, Job Match Analysis, và AI Suggestions
 */
export function AIPanel({
  externalFile,
  onExternalFileProcessed,
  accordionValue,
  onAccordionChange,
}: AIPanelProps) {
  const { aiSuggestions } = useAIStore();
  const [activeTab, setActiveTab] = useState("tools");
  const [previousSuggestionsCount, setPreviousSuggestionsCount] = useState(0);

  // Auto switch to suggestions tab when new suggestions arrive
  useEffect(() => {
    if (
      aiSuggestions.length > 0 &&
      aiSuggestions.length !== previousSuggestionsCount
    ) {
      setActiveTab("suggestions");
      setPreviousSuggestionsCount(aiSuggestions.length);
    }
  }, [aiSuggestions.length, previousSuggestionsCount]);

  return (
    <Card className="p-6 lg:sticky lg:top-24 h-fit">
      <div className="flex items-center gap-2 mb-4">
        <Sparkles className="h-5 w-5 text-primary" />
        <h3 className="font-semibold text-lg">Công Cụ AI</h3>
      </div>

      <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
        <TabsList className="grid w-full grid-cols-2">
          <TabsTrigger value="tools">Công Cụ</TabsTrigger>
          <TabsTrigger value="suggestions" className="relative">
            Gợi Ý
            {aiSuggestions.length > 0 && (
              <Badge
                variant="destructive"
                className="ml-2 h-5 min-w-5 px-1 text-xs"
              >
                {aiSuggestions.length}
              </Badge>
            )}
          </TabsTrigger>
        </TabsList>

        <TabsContent value="tools" className="mt-4">
          <AIToolsSidebar
            externalFile={externalFile}
            onExternalFileProcessed={onExternalFileProcessed}
            accordionValue={accordionValue}
            onAccordionChange={onAccordionChange}
          />
        </TabsContent>

        <TabsContent value="suggestions" className="mt-4">
          <AISuggestionsList />
        </TabsContent>
      </Tabs>
    </Card>
  );
}
