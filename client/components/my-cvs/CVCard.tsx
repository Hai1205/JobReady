"use client";

import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { FileText, Calendar, Edit, Trash2, Download, Copy } from "lucide-react";
import { Badge } from "@/components/ui/badge";
import { formatDateAgo } from "@/lib/utils";

interface CVCardProps {
  cv: ICV;
  onEdit: (cv: ICV) => void;
  onDuplicate: (cv: ICV) => void;
  onDelete: (id: string) => void;
}

export default function CVCard({
  cv,
  onEdit,
  onDuplicate,
  onDelete,
}: CVCardProps) {
  return (
    <Card className="group relative overflow-hidden border-border/50 shadow-lg bg-gradient-to-br from-card to-card/80 backdrop-blur-sm transition-all duration-200 hover:shadow-xl hover:shadow-primary/20 hover:scale-[1.02]">
      <CardHeader>
        <div className="flex items-start justify-between">
          <div className="flex-1">
            <CardTitle className="line-clamp-1 group-hover:text-primary transition-colors">
              {cv.title}
            </CardTitle>
            <CardDescription className="mt-2 flex items-center gap-2">
              <Calendar className="h-3 w-3" />
              Cập nhật {formatDateAgo(cv.updatedAt || "")}
            </CardDescription>
          </div>
          <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-gradient-to-br from-primary/20 to-secondary/20">
            <FileText className="h-5 w-5 text-primary" />
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <div className="flex flex-col gap-4">
          <div className="flex flex-col gap-2">
            <p className="text-sm font-medium">
              {cv.personalInfo.fullname || "Chưa đặt tên"}
            </p>
            <p className="text-xs text-muted-foreground line-clamp-2">
              {cv.personalInfo.summary || "Chưa có tóm tắt"}
            </p>
          </div>

          <div className="flex flex-wrap gap-2">
            {cv.experiences.length > 0 && (
              <Badge variant="secondary" className="text-xs">
                {cv.experiences.length} Kinh nghiệm
              </Badge>
            )}
            {cv.educations.length > 0 && (
              <Badge variant="secondary" className="text-xs">
                {cv.educations.length} Học vấn
              </Badge>
            )}
            {cv.skills.length > 0 && (
              <Badge variant="secondary" className="text-xs">
                {cv.skills.length} Kỹ năng
              </Badge>
            )}
          </div>

          <div className="flex gap-2 pt-2 border-t border-border/50">
            <Button
              variant="outline"
              size="sm"
              onClick={() => onEdit(cv)}
              className="flex-1 hover:bg-primary/10 hover:text-primary hover:border-primary/50 transition-all duration-200"
            >
              <Edit className="mr-2 h-3 w-3" />
              Chỉnh sửa
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => onDuplicate(cv)}
              className="hover:bg-secondary/10 hover:text-secondary hover:border-secondary/50 transition-all duration-200"
            >
              <Copy className="h-3 w-3" />
            </Button>
            <Button
              variant="outline"
              size="sm"
              className="hover:bg-primary/10 hover:text-primary hover:border-primary/50 transition-all duration-200"
            >
              <Download className="h-3 w-3" />
            </Button>
            <Button
              variant="outline"
              size="sm"
              onClick={() => onDelete(cv.id)}
              className="hover:bg-destructive/10 hover:text-destructive hover:border-destructive/50 transition-all duration-200"
            >
              <Trash2 className="h-3 w-3" />
            </Button>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
