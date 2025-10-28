"use client";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { ScrollArea } from "@/components/ui/scroll-area";
import { Badge } from "@/components/ui/badge";
import {
  User,
  Mail,
  Phone,
  MapPin,
  Briefcase,
  GraduationCap,
  Award,
  Calendar,
} from "lucide-react";

interface CVPreviewDialogProps {
  cv: ICV | null;
  open: boolean;
  onClose: () => void;
  onConfirm: (cv: ICV) => void;
}

export function CVPreviewDialog({
  cv,
  open,
  onClose,
  onConfirm,
}: CVPreviewDialogProps) {
  if (!cv) return null;

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent className="max-w-3xl max-h-[80vh]">
        <DialogHeader>
          <DialogTitle>Xem trước dữ liệu CV</DialogTitle>
          <DialogDescription>
            Kiểm tra thông tin được trích xuất từ file CV của bạn. Bạn có thể
            chỉnh sửa sau khi xác nhận.
          </DialogDescription>
        </DialogHeader>

        <ScrollArea className="h-[500px] pr-4">
          <div className="space-y-6">
            {/* Personal Info */}
            <div>
              <h3 className="text-lg font-semibold mb-3 flex items-center gap-2">
                <User className="h-5 w-5" />
                Thông tin cá nhân
              </h3>
              <div className="space-y-2 pl-7">
                <div className="flex items-center gap-2">
                  <User className="h-4 w-4 text-muted-foreground" />
                  <span className="font-medium">Họ tên:</span>
                  <span>{cv.personalInfo.fullname || "Chưa có thông tin"}</span>
                </div>
                <div className="flex items-center gap-2">
                  <Mail className="h-4 w-4 text-muted-foreground" />
                  <span className="font-medium">Email:</span>
                  <span>{cv.personalInfo.email || "Chưa có thông tin"}</span>
                </div>
                <div className="flex items-center gap-2">
                  <Phone className="h-4 w-4 text-muted-foreground" />
                  <span className="font-medium">Điện thoại:</span>
                  <span>{cv.personalInfo.phone || "Chưa có thông tin"}</span>
                </div>
                <div className="flex items-center gap-2">
                  <MapPin className="h-4 w-4 text-muted-foreground" />
                  <span className="font-medium">Địa chỉ:</span>
                  <span>{cv.personalInfo.location || "Chưa có thông tin"}</span>
                </div>
              </div>
            </div>

            {/* Experiences */}
            <div>
              <h3 className="text-lg font-semibold mb-3 flex items-center gap-2">
                <Briefcase className="h-5 w-5" />
                Kinh nghiệm làm việc ({cv.experiences.length})
              </h3>
              {cv.experiences.length > 0 ? (
                <div className="space-y-4 pl-7">
                  {cv.experiences.map((exp, index) => (
                    <div
                      key={exp.id || index}
                      className="border-l-2 border-primary pl-4"
                    >
                      <div className="font-medium">
                        {exp.position || "Chưa rõ vị trí"}
                      </div>
                      <div className="text-sm text-muted-foreground">
                        {exp.company || "Chưa rõ công ty"}
                      </div>
                      <div className="flex items-center gap-2 text-sm text-muted-foreground mt-1">
                        <Calendar className="h-3 w-3" />
                        {exp.startDate || "N/A"} - {exp.endDate || "Present"}
                      </div>
                      {exp.description && (
                        <p className="text-sm mt-2 line-clamp-3">
                          {exp.description}
                        </p>
                      )}
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-sm text-muted-foreground pl-7">
                  Chưa có thông tin
                </p>
              )}
            </div>

            {/* Education */}
            <div>
              <h3 className="text-lg font-semibold mb-3 flex items-center gap-2">
                <GraduationCap className="h-5 w-5" />
                Học vấn ({cv.educations.length})
              </h3>
              {cv.educations.length > 0 ? (
                <div className="space-y-4 pl-7">
                  {cv.educations.map((edu, index) => (
                    <div
                      key={edu.id || index}
                      className="border-l-2 border-primary pl-4"
                    >
                      <div className="font-medium">
                        {edu.school || "Chưa rõ trường"}
                      </div>
                      <div className="text-sm text-muted-foreground">
                        {edu.degree || "Chưa rõ bằng cấp"} -{" "}
                        {edu.field || "Chưa rõ ngành"}
                      </div>
                      <div className="flex items-center gap-2 text-sm text-muted-foreground mt-1">
                        <Calendar className="h-3 w-3" />
                        {edu.startDate || "N/A"} - {edu.endDate || "N/A"}
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-sm text-muted-foreground pl-7">
                  Chưa có thông tin
                </p>
              )}
            </div>

            {/* Skills */}
            <div>
              <h3 className="text-lg font-semibold mb-3 flex items-center gap-2">
                <Award className="h-5 w-5" />
                Kỹ năng ({cv.skills.length})
              </h3>
              {cv.skills.length > 0 ? (
                <div className="flex flex-wrap gap-2 pl-7">
                  {cv.skills.map((skill, index) => (
                    <Badge key={index} variant="secondary">
                      {skill}
                    </Badge>
                  ))}
                </div>
              ) : (
                <p className="text-sm text-muted-foreground pl-7">
                  Chưa có thông tin
                </p>
              )}
            </div>
          </div>
        </ScrollArea>

        <DialogFooter>
          <Button variant="outline" onClick={onClose}>
            Hủy bỏ
          </Button>
          <Button onClick={() => onConfirm(cv)}>Xác nhận & Chỉnh sửa</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
