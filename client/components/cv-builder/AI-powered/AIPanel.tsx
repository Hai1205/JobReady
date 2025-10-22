"use client";

import { Card } from "@/components/ui/card";
import { Sparkles } from "lucide-react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { AISuggestionsList } from "./AISuggestionsList";
import { AIToolsSidebar } from "./AIToolsSidebar";

/**
 * AIPanel - Panel hiển thị tất cả tính năng AI ở sidebar
 * Bao gồm Quick Analyze, Job Match Analysis, và AI Suggestions
 */
export function AIPanel() {
  return (
    <Card className="p-6 lg:sticky lg:top-24 h-fit">
      <div className="flex items-center gap-2 mb-4">
        <Sparkles className="h-5 w-5 text-primary" />
        <h3 className="font-semibold text-lg">Công Cụ AI</h3>
      </div>

      <Tabs defaultValue="tools" className="w-full">
        <TabsList className="grid w-full grid-cols-2">
          <TabsTrigger value="tools">Công Cụ</TabsTrigger>
          <TabsTrigger value="suggestions">Gợi Ý</TabsTrigger>
        </TabsList>

        <TabsContent value="tools" className="mt-4">
          <AIToolsSidebar />
        </TabsContent>

        <TabsContent value="suggestions" className="mt-4">
          <AISuggestionsList />
        </TabsContent>
      </Tabs>
    </Card>
  );
}
