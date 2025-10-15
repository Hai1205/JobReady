"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Textarea } from "@/components/ui/textarea";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Upload, Sparkles, Loader2 } from "lucide-react";
import { toast } from "react-toastify";
import { useCVStore } from "@/stores/cvStore";

interface JobDescriptionImportProps {
  cvId: string;
  onAnalysisComplete?: (
    suggestions: IAISuggestion[],
    matchScore?: number
  ) => void;
}

export function JobDescriptionImport({
  cvId,
  onAnalysisComplete,
}: JobDescriptionImportProps) {
  const [jobDescription, setJobDescription] = useState("");
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [file, setFile] = useState<File | null>(null);

  const { analyzeCVWithJD, handleSetAISuggestions, handleSetJobDescription } =
    useCVStore();

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFile = e.target.files?.[0];
    if (!selectedFile) return;

    // Check file type
    const validTypes = [
      "text/plain",
      "application/pdf",
      "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
    ];
    if (!validTypes.includes(selectedFile.type)) {
      toast.error("Please upload a TXT, PDF, or DOCX file");
      return;
    }

    setFile(selectedFile);

    // Read file content
    const reader = new FileReader();
    reader.onload = (event) => {
      const text = event.target?.result as string;
      setJobDescription(text);
      toast.success("Job description loaded from file");
    };
    reader.onerror = () => {
      toast.error("Error reading file");
    };

    if (selectedFile.type === "text/plain") {
      reader.readAsText(selectedFile);
    } else {
      // For PDF and DOCX, we would need a parser library
      // For now, just show a message
      toast.info(
        "Please paste the job description manually for PDF/DOCX files"
      );
    }
  };

  const handleAnalyze = async () => {
    if (!jobDescription.trim()) {
      toast.error("Please enter or upload a job description");
      return;
    }

    setIsAnalyzing(true);

    try {
      const response = await analyzeCVWithJD(cvId, jobDescription);

      if (response.data) {
        const apiData = response.data as unknown as {
          data?: {
            suggestions?: IAISuggestion[];
            matchScore?: number;
          };
        };

        const suggestions = apiData.data?.suggestions || [];
        const matchScore = apiData.data?.matchScore;

        handleSetAISuggestions(suggestions);
        handleSetJobDescription(jobDescription);

        toast.success(
          `Analysis complete! Match score: ${
            matchScore ? Math.round(matchScore) : "N/A"
          }%`
        );

        if (onAnalysisComplete) {
          onAnalysisComplete(suggestions, matchScore);
        }
      } else {
        toast.error("Failed to analyze CV with job description");
      }
    } catch (error) {
      console.error("Error analyzing CV:", error);
      toast.error("An error occurred during analysis");
    } finally {
      setIsAnalyzing(false);
    }
  };

  return (
    <Card className="w-full">
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <Sparkles className="h-5 w-5" />
          AI Job Match Analysis
        </CardTitle>
        <CardDescription>
          Upload or paste a job description to analyze how well your CV matches
          the requirements
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <div>
          <label
            htmlFor="jd-file-upload"
            className="block text-sm font-medium mb-2"
          >
            Upload Job Description (Optional)
          </label>
          <div className="flex items-center gap-2">
            <input
              id="jd-file-upload"
              type="file"
              accept=".txt,.pdf,.docx"
              onChange={handleFileUpload}
              className="hidden"
            />
            <Button
              type="button"
              variant="outline"
              onClick={() => document.getElementById("jd-file-upload")?.click()}
              className="w-full"
            >
              <Upload className="mr-2 h-4 w-4" />
              {file ? file.name : "Choose File"}
            </Button>
          </div>
        </div>

        <div>
          <label
            htmlFor="jd-textarea"
            className="block text-sm font-medium mb-2"
          >
            Job Description
          </label>
          <Textarea
            id="jd-textarea"
            placeholder="Paste the job description here..."
            value={jobDescription}
            onChange={(e) => setJobDescription(e.target.value)}
            className="min-h-[200px] resize-y"
          />
        </div>

        <Button
          onClick={handleAnalyze}
          disabled={isAnalyzing || !jobDescription.trim()}
          className="w-full"
        >
          {isAnalyzing ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Analyzing...
            </>
          ) : (
            <>
              <Sparkles className="mr-2 h-4 w-4" />
              Analyze Match
            </>
          )}
        </Button>
      </CardContent>
    </Card>
  );
}
