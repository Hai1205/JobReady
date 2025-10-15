"use client";

import { useState } from "react";
import { useCVStore } from "@/stores/cvStore";
import { JobDescriptionImport } from "./JobDescriptionImport";
import { AISuggestionsList } from "./AISuggestionsList";
import { Button } from "@/components/ui/button";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Sparkles, FileUp, Loader2 } from "lucide-react";
import { toast } from "react-toastify";

interface AIFeaturesTabProps {
  cvId: string;
}

export function AIFeaturesTab({ cvId }: AIFeaturesTabProps) {
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [activeTab, setActiveTab] = useState("analyze");

  const { analyzeCV, handleSetAISuggestions, improveCV, currentCV } =
    useCVStore();

  const handleQuickAnalyze = async () => {
    if (!cvId) {
      toast.error("No CV selected");
      return;
    }

    setIsAnalyzing(true);
    try {
      const response = await analyzeCV(cvId);

      if (response.data) {
        const apiData = response.data as unknown as {
          data?: {
            suggestions?: IAISuggestion[];
            analysis?: string;
          };
        };

        const suggestions = apiData.data?.suggestions || [];
        handleSetAISuggestions(suggestions);

        toast.success(
          `Analysis complete! Found ${suggestions.length} suggestions`
        );
        setActiveTab("suggestions");
      } else {
        toast.error("Failed to analyze CV");
      }
    } catch (error) {
      console.error("Error analyzing CV:", error);
      toast.error("An error occurred during analysis");
    } finally {
      setIsAnalyzing(false);
    }
  };

  const handleApplySuggestion = async (suggestion: IAISuggestion) => {
    if (!currentCV) return;

    try {
      // Get the content from the CV based on the section
      let content = "";
      switch (suggestion.section.toLowerCase()) {
        case "summary":
          content = currentCV.personalInfo?.summary || "";
          break;
        case "experience":
          content = JSON.stringify(currentCV.experience);
          break;
        case "education":
          content = JSON.stringify(currentCV.education);
          break;
        case "skills":
          content = currentCV.skills?.join(", ") || "";
          break;
        default:
          content = suggestion.suggestion;
      }

      const response = await improveCV(cvId, suggestion.section, content);

      if (response.data) {
        const apiData = response.data as unknown as {
          data?: {
            improvedSection?: string;
          };
        };

        const improvedSection = apiData.data?.improvedSection;

        if (improvedSection) {
          toast.success("Suggestion applied! Review the improved section.");
          // Here you would update the CV with the improved content
          console.log("Improved section:", improvedSection);
        }
      }
    } catch (error) {
      console.error("Error applying suggestion:", error);
      toast.error("Failed to apply suggestion");
    }
  };

  return (
    <div className="space-y-6">
      <Card>
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Sparkles className="h-5 w-5" />
            AI-Powered CV Enhancement
          </CardTitle>
          <CardDescription>
            Use artificial intelligence to analyze, improve, and optimize your
            CV
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex gap-2">
            <Button
              onClick={handleQuickAnalyze}
              disabled={isAnalyzing}
              className="flex-1"
            >
              {isAnalyzing ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Analyzing...
                </>
              ) : (
                <>
                  <Sparkles className="mr-2 h-4 w-4" />
                  Quick Analyze
                </>
              )}
            </Button>
          </div>
        </CardContent>
      </Card>

      <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
        <TabsList className="grid w-full grid-cols-2">
          <TabsTrigger value="analyze">Job Match Analysis</TabsTrigger>
          <TabsTrigger value="suggestions">AI Suggestions</TabsTrigger>
        </TabsList>

        <TabsContent value="analyze" className="space-y-4">
          <JobDescriptionImport
            cvId={cvId}
            onAnalysisComplete={(suggestions) => {
              setActiveTab("suggestions");
            }}
          />
        </TabsContent>

        <TabsContent value="suggestions">
          <AISuggestionsList onApplySuggestion={handleApplySuggestion} />
        </TabsContent>
      </Tabs>
    </div>
  );
}
