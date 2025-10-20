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
import {
  Collapsible,
  CollapsibleContent,
  CollapsibleTrigger,
} from "@/components/ui/collapsible";
import { Sparkles, FileUp, Loader2, ChevronDown } from "lucide-react";
import { toast } from "react-toastify";

interface AIFeaturesTabProps {
  cvId: string;
}

export function AIFeaturesTab({ cvId }: AIFeaturesTabProps) {
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [isImproving, setIsImproving] = useState(false);
  const [activeTab, setActiveTab] = useState("analyze");
  const [analyzeRawText, setAnalyzeRawText] = useState<string>("");
  const [isRawTextOpen, setIsRawTextOpen] = useState(false);
  const [improvedContent, setImprovedContent] = useState<{
    section: string;
    content: string;
  } | null>(null);

  const {
    analyzeCV,
    handleSetAISuggestions,
    improveCV,
    currentCV,
    handleUpdateCV,
  } = useCVStore();

  const handleQuickAnalyze = async () => {
    if (!cvId) {
      toast.error("No CV selected");
      return;
    }

    setIsAnalyzing(true);
    try {
      const response = await analyzeCV(cvId);

      // Support both shapes: response.data may be the backend Response object
      // or the backend Response.data object directly depending on typings.
      const maybeResponse = (response as any).data;
      const responseData: any = maybeResponse?.data
        ? maybeResponse.data
        : maybeResponse;

      const suggestions = responseData?.suggestions || [];
      const analyzeText = responseData?.analyze || "";

      // Store raw analyze text
      if (analyzeText) {
        setAnalyzeRawText(analyzeText);
      }

      if (suggestions.length >= 0) {
        handleSetAISuggestions(suggestions);
        toast.success(
          `Analysis complete! Found ${suggestions.length} suggestions`
        );
        setActiveTab("suggestions");
      } else {
        toast.error((response as any)?.message || "Failed to Phân Tích");
      }
    } catch (error) {
      console.error("Error analyzing CV:", error);
      toast.error("An error occurred during analysis");
    } finally {
      setIsAnalyzing(false);
    }
  };

  const handleApplySuggestion = async (suggestion: any) => {
    if (!currentCV || !cvId) {
      toast.error("No CV loaded");
      return;
    }

    setIsImproving(true);
    try {
      // Get the content from the CV based on the section
      let content = "";
      switch (suggestion.section.toLowerCase()) {
        case "summary":
          content = currentCV.personalInfo?.summary || "";
          break;
        case "experiences":
          content = JSON.stringify(currentCV.experiences);
          break;
        case "educations":
          content = JSON.stringify(currentCV.educations);
          break;
        case "skills":
          content = currentCV.skills?.join(", ") || "";
          break;
        default:
          content = suggestion.suggestion;
      }

      const response = await improveCV(cvId, suggestion.section, content);

      const maybeResponse = (response as any).data;
      const responseData: any = maybeResponse?.data
        ? maybeResponse.data
        : maybeResponse;

      const improvedSection = responseData?.improvedSection;

      if (improvedSection) {
        // Store improved content for manual review
        setImprovedContent({
          section: suggestion.section,
          content: improvedSection,
        });
        toast.success(
          "Suggestion applied! Review and save the improved section."
        );
        console.log("Improved section:", improvedSection);
      } else {
        toast.error((response as any)?.message || "Failed to apply suggestion");
      }
    } catch (error) {
      console.error("Error applying suggestion:", error);
      toast.error("Failed to apply suggestion");
    } finally {
      setIsImproving(false);
    }
  };

  const handleSaveImprovedContent = () => {
    if (!improvedContent || !currentCV) return;

    const { section, content } = improvedContent;
    let applied = false;

    switch (section.toLowerCase()) {
      case "summary":
        handleUpdateCV({
          personalInfo: {
            ...currentCV.personalInfo,
            summary: content,
          },
        });
        applied = true;
        break;
      case "experiences":
        try {
          const parsedExperience = JSON.parse(content);
          handleUpdateCV({ experiences: parsedExperience });
          applied = true;
        } catch (error) {
          toast.error("Failed to parse experiences data");
          return;
        }
        break;
      case "educations":
        try {
          const parsedEducation = JSON.parse(content);
          handleUpdateCV({ educations: parsedEducation });
          applied = true;
        } catch (error) {
          toast.error("Failed to parse educations data");
          return;
        }
        break;
      case "skills":
        const skillsArray = content
          .split(",")
          .map((s) => s.trim())
          .filter((s) => s);
        handleUpdateCV({ skills: skillsArray });
        applied = true;
        break;
      default:
        toast.error(`Unknown section: ${section}`);
        return;
    }

    if (applied) {
      toast.success("Improved content applied to CV!");
      setImprovedContent(null);
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

          {/* Collapsible panel for raw AI analyze text */}
          {analyzeRawText && (
            <Collapsible
              open={isRawTextOpen}
              onOpenChange={setIsRawTextOpen}
              className="mt-4"
            >
              <CollapsibleTrigger asChild>
                <Button variant="outline" className="w-full justify-between">
                  <span>View Full AI Analysis</span>
                  <ChevronDown
                    className={`h-4 w-4 transition-transform ${
                      isRawTextOpen ? "rotate-180" : ""
                    }`}
                  />
                </Button>
              </CollapsibleTrigger>
              <CollapsibleContent className="mt-2">
                <Card>
                  <CardContent className="pt-4">
                    <div className="prose prose-sm max-w-none">
                      <pre className="whitespace-pre-wrap text-sm bg-muted p-4 rounded-md overflow-auto max-h-96">
                        {analyzeRawText}
                      </pre>
                    </div>
                  </CardContent>
                </Card>
              </CollapsibleContent>
            </Collapsible>
          )}
        </CardContent>
      </Card>

      <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
        <TabsList className="grid w-full grid-cols-2">
          <TabsTrigger value="analyze">Job Match Analysis</TabsTrigger>
          <TabsTrigger value="suggestions">Gợi Ý AI</TabsTrigger>
        </TabsList>

        <TabsContent value="analyze" className="space-y-4">
          <JobDescriptionImport
            cvId={cvId}
            onAnalysisComplete={(suggestions) => {
              setActiveTab("suggestions");
            }}
          />
        </TabsContent>

        <TabsContent value="suggestions" className="space-y-4">
          {/* Preview improved content before applying */}
          {improvedContent && (
            <Card className="border-green-200 bg-green-50">
              <CardHeader>
                <CardTitle className="text-lg">
                  Improved {improvedContent.section}
                </CardTitle>
                <CardDescription>
                  Review the AI-improved content and apply it to your CV
                </CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="p-4 bg-white rounded-md border">
                  <pre className="whitespace-pre-wrap text-sm">
                    {improvedContent.content}
                  </pre>
                </div>
                <div className="flex gap-2">
                  <Button
                    onClick={handleSaveImprovedContent}
                    className="flex-1"
                  >
                    Apply to CV
                  </Button>
                  <Button
                    onClick={() => setImprovedContent(null)}
                    variant="outline"
                    className="flex-1"
                  >
                    Discard
                  </Button>
                </div>
              </CardContent>
            </Card>
          )}

          <AISuggestionsList
            onApplySuggestion={handleApplySuggestion}
            isApplying={isImproving}
          />
        </TabsContent>
      </Tabs>
    </div>
  );
}
