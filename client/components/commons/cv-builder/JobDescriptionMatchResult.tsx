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
    if (score >= 80) return "Khớp Xuất Sắc";
    if (score >= 60) return "Khớp Tốt";
    if (score >= 40) return "Khớp Khá";
    return "Khớp Kém";
  };

  return (
    <div className="space-y-4">
      {/* Match Score Section */}
      {matchScore !== undefined && (
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <TrendingUp className="h-5 w-5" />
              Điểm Khớp
            </CardTitle>
            <CardDescription>
              Mức độ CV của bạn khớp với mô tả công việc
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
              Từ Khóa Thiếu
            </CardTitle>
            <CardDescription>
              Kỹ năng hoặc từ khóa quan trọng không tìm thấy trong CV của bạn
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
              Chi Tiết Công Việc
            </CardTitle>
            <CardDescription>
              Thông tin được phân tích từ mô tả công việc
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* Job title & Company */}
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
                      <p className="text-sm font-medium">Cấp Độ</p>
                      <p className="text-sm text-muted-foreground">
                        {parsedJobDescription.jobLevel}
                      </p>
                    </div>
                  )}
                  {parsedJobDescription.jobType && (
                    <div>
                      <p className="text-sm font-medium">Loại</p>
                      <p className="text-sm text-muted-foreground">
                        {parsedJobDescription.jobType}
                      </p>
                    </div>
                  )}
                  {parsedJobDescription.location && (
                    <div>
                      <p className="text-sm font-medium">Địa Điểm</p>
                      <p className="text-sm text-muted-foreground">
                        {parsedJobDescription.location}
                      </p>
                    </div>
                  )}
                  {parsedJobDescription.salary && (
                    <div>
                      <p className="text-sm font-medium">Lương</p>
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
                    <p className="text-sm font-medium mb-2">Kỹ Năng Bắt Buộc</p>
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
                    <p className="text-sm font-medium mb-2">Kỹ Năng Ưu Tiên</p>
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
                    <p className="text-sm font-medium mb-2">Trách Nhiệm</p>
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
                    <p className="text-sm font-medium mb-2">Yêu Cầu</p>
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
                    <p className="text-sm font-medium mb-2">Phúc Lợi</p>
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
            <CardTitle>Tóm Tắt Phân Tích AI</CardTitle>
            <CardDescription>
              Phân tích chi tiết từ AI về mức độ khớp CV của bạn
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
