"use client";

import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Progress } from "@/components/ui/progress";
import { Separator } from "@/components/ui/separator";
import {
  Briefcase,
  Building2,
  TrendingUp,
  AlertCircle,
  CheckCircle2,
  XCircle,
} from "lucide-react";

interface JobDescriptionMatchResultProps {
  parsedJobDescription?: IJobDescriptionResult;
  matchScore?: number;
  missingKeywords?: string[];
  analyzeSummary?: string;
}

export function JobDescriptionMatchResult({
  parsedJobDescription,
  matchScore,
  missingKeywords,
  analyzeSummary,
}: JobDescriptionMatchResultProps) {
  if (!parsedJobDescription && matchScore === undefined) {
    return null;
  }

  const getScoreColor = (score: number) => {
    if (score >= 80) return "text-green-600";
    if (score >= 60) return "text-yellow-600";
    return "text-red-600";
  };

  const getScoreLabel = (score: number) => {
    if (score >= 80) return "Excellent Match";
    if (score >= 60) return "Good Match";
    if (score >= 40) return "Fair Match";
    return "Poor Match";
  };

  return (
    <div className="space-y-4">
      {/* Match Score Section */}
      {matchScore !== undefined && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <TrendingUp className="h-5 w-5" />
              Match Score
            </CardTitle>
            <CardDescription>
              How well your CV matches this job description
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <div
                  className={`text-4xl font-bold ${getScoreColor(matchScore)}`}
                >
                  {Math.round(matchScore)}%
                </div>
                <p className="text-sm text-muted-foreground mt-1">
                  {getScoreLabel(matchScore)}
                </p>
              </div>
              {matchScore >= 80 ? (
                <CheckCircle2 className="h-12 w-12 text-green-600" />
              ) : matchScore >= 60 ? (
                <AlertCircle className="h-12 w-12 text-yellow-600" />
              ) : (
                <XCircle className="h-12 w-12 text-red-600" />
              )}
            </div>
            <Progress value={matchScore} className="h-3" />
          </CardContent>
        </Card>
      )}

      {/* Missing Keywords */}
      {missingKeywords && missingKeywords.length > 0 && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <AlertCircle className="h-5 w-5 text-yellow-600" />
              Missing Keywords
            </CardTitle>
            <CardDescription>
              Important skills or keywords not found in your CV
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="flex flex-wrap gap-2">
              {missingKeywords.map((keyword, index) => (
                <Badge key={index} variant="destructive">
                  {keyword}
                </Badge>
              ))}
            </div>
          </CardContent>
        </Card>
      )}

      {/* Parsed Job Description */}
      {parsedJobDescription && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Briefcase className="h-5 w-5" />
              Job Details
            </CardTitle>
            <CardDescription>
              Parsed information from the job description
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* Job Title & Company */}
            <div className="space-y-2">
              {parsedJobDescription.jobTitle && (
                <div>
                  <h3 className="text-xl font-semibold">
                    {parsedJobDescription.jobTitle}
                  </h3>
                </div>
              )}
              {parsedJobDescription.company && (
                <div className="flex items-center gap-2 text-muted-foreground">
                  <Building2 className="h-4 w-4" />
                  <span>{parsedJobDescription.company}</span>
                </div>
              )}
            </div>

            {/* Job Meta Info */}
            {(parsedJobDescription.jobLevel ||
              parsedJobDescription.jobType ||
              parsedJobDescription.location ||
              parsedJobDescription.salary) && (
              <>
                <Separator />
                <div className="grid grid-cols-2 gap-3">
                  {parsedJobDescription.jobLevel && (
                    <div>
                      <p className="text-sm font-medium">Level</p>
                      <p className="text-sm text-muted-foreground">
                        {parsedJobDescription.jobLevel}
                      </p>
                    </div>
                  )}
                  {parsedJobDescription.jobType && (
                    <div>
                      <p className="text-sm font-medium">Type</p>
                      <p className="text-sm text-muted-foreground">
                        {parsedJobDescription.jobType}
                      </p>
                    </div>
                  )}
                  {parsedJobDescription.location && (
                    <div>
                      <p className="text-sm font-medium">Location</p>
                      <p className="text-sm text-muted-foreground">
                        {parsedJobDescription.location}
                      </p>
                    </div>
                  )}
                  {parsedJobDescription.salary && (
                    <div>
                      <p className="text-sm font-medium">Salary</p>
                      <p className="text-sm text-muted-foreground">
                        {parsedJobDescription.salary}
                      </p>
                    </div>
                  )}
                </div>
              </>
            )}

            {/* Required Skills */}
            {parsedJobDescription.requiredSkills &&
              parsedJobDescription.requiredSkills.length > 0 && (
                <>
                  <Separator />
                  <div>
                    <p className="text-sm font-medium mb-2">Required Skills</p>
                    <div className="flex flex-wrap gap-2">
                      {parsedJobDescription.requiredSkills.map(
                        (skill, index) => (
                          <Badge key={index} variant="default">
                            {skill}
                          </Badge>
                        )
                      )}
                    </div>
                  </div>
                </>
              )}

            {/* Preferred Skills */}
            {parsedJobDescription.preferredSkills &&
              parsedJobDescription.preferredSkills.length > 0 && (
                <>
                  <Separator />
                  <div>
                    <p className="text-sm font-medium mb-2">Preferred Skills</p>
                    <div className="flex flex-wrap gap-2">
                      {parsedJobDescription.preferredSkills.map(
                        (skill, index) => (
                          <Badge key={index} variant="secondary">
                            {skill}
                          </Badge>
                        )
                      )}
                    </div>
                  </div>
                </>
              )}

            {/* Responsibilities */}
            {parsedJobDescription.responsibilities &&
              parsedJobDescription.responsibilities.length > 0 && (
                <>
                  <Separator />
                  <div>
                    <p className="text-sm font-medium mb-2">Responsibilities</p>
                    <ul className="list-disc list-inside space-y-1 text-sm text-muted-foreground">
                      {parsedJobDescription.responsibilities.map(
                        (resp, index) => (
                          <li key={index}>{resp}</li>
                        )
                      )}
                    </ul>
                  </div>
                </>
              )}

            {/* Requirements */}
            {parsedJobDescription.requirements &&
              parsedJobDescription.requirements.length > 0 && (
                <>
                  <Separator />
                  <div>
                    <p className="text-sm font-medium mb-2">Requirements</p>
                    <ul className="list-disc list-inside space-y-1 text-sm text-muted-foreground">
                      {parsedJobDescription.requirements.map((req, index) => (
                        <li key={index}>{req}</li>
                      ))}
                    </ul>
                  </div>
                </>
              )}

            {/* Benefits */}
            {parsedJobDescription.benefits &&
              parsedJobDescription.benefits.length > 0 && (
                <>
                  <Separator />
                  <div>
                    <p className="text-sm font-medium mb-2">Benefits</p>
                    <ul className="list-disc list-inside space-y-1 text-sm text-muted-foreground">
                      {parsedJobDescription.benefits.map((benefit, index) => (
                        <li key={index}>{benefit}</li>
                      ))}
                    </ul>
                  </div>
                </>
              )}
          </CardContent>
        </Card>
      )}

      {/* AI Analysis Summary */}
      {analyzeSummary && (
        <Card>
          <CardHeader>
            <CardTitle>AI Analysis Summary</CardTitle>
            <CardDescription>
              Detailed analysis from AI about your CV match
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="prose prose-sm max-w-none">
              <p className="whitespace-pre-wrap text-sm">{analyzeSummary}</p>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  );
}
