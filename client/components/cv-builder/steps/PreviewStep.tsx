"use client";

import React, { useEffect } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Mail, Phone, Download, FileText } from "lucide-react";
import { useCVStore } from "@/stores/cvStore";
import { toast } from "react-toastify";
import { PDFExportService } from "@/services/pdfExportService";

export function PreviewStep() {
  const { currentCV } = useCVStore();
  const [avatarUrl, setAvatarUrl] = React.useState<string | null>(null);

  // Convert File to base64 URL for display
  useEffect(() => {
    if (currentCV?.avatar && currentCV.avatar instanceof File) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setAvatarUrl(reader.result as string);
      };
      reader.readAsDataURL(currentCV.avatar);
    } else {
      setAvatarUrl(null);
    }
  }, [currentCV?.avatar]);

  if (!currentCV) return null;

  const generatePDF = async () => {
    if (!currentCV.personalInfo?.fullname) {
      toast.error("⚠️ Vui lòng điền TÊN trong Step 1!");
      return;
    }

    try {
      // Show loading toast
      const toastId = toast.loading("🔄 Đang tạo PDF...");

      // Generate filename
      const filename = `CV_${currentCV.personalInfo.fullname.replace(
        /\s+/g,
        "_"
      )}.pdf`;

      // Export using PDFShift API
      await PDFExportService.exportToPDF("cv-preview-content", filename);

      // Update toast
      toast.update(toastId, {
        render: "✅ Tải xuống CV thành công!",
        type: "success",
        isLoading: false,
        autoClose: 3000,
      });
    } catch (error) {
      console.error("PDF generation error:", error);
      toast.error(
        `❌ Lỗi tạo PDF: ${
          error instanceof Error ? error.message : "Unknown error"
        }`
      );
    }
  };

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-2xl font-bold">Xem Trước CV</h2>
          <p className="text-muted-foreground">
            Kiểm tra lại thông tin và tải xuống CV của bạn
          </p>
        </div>
        <Button onClick={generatePDF} size="lg" className="gap-2">
          <Download className="h-5 w-5" />
          Tải CV (PDF)
        </Button>
      </div>

      {/* PDF Preview */}
      <Card className="overflow-hidden bg-white shadow-2xl">
        {/* A4 Preview Container */}
        <div
          id="cv-preview-content"
          className="mx-auto max-w-[210mm] bg-white p-8"
          style={{
            minHeight: "297mm",
            boxShadow: "0 0 20px rgba(0,0,0,0.1)",
          }}
        >
          {/* Header with Background */}
          <div className="mb-8 -mx-8 -mt-8 bg-gray-100 px-8 py-6 border-b-2 border-gray-200">
            <div className="flex items-start gap-6">
              {/* Avatar */}
              <div className="flex-shrink-0">
                <Avatar className="h-24 w-24 border-4 border-white shadow-lg">
                  {avatarUrl && (
                    <AvatarImage src={avatarUrl} className="object-cover" />
                  )}
                  <AvatarFallback className="text-2xl font-bold bg-blue-900 text-white">
                    {currentCV.personalInfo.fullname
                      ? currentCV.personalInfo.fullname
                          .split(" ")
                          .map((n) => n[0])
                          .join("")
                          .toUpperCase()
                          .slice(0, 2)
                      : "CV"}
                  </AvatarFallback>
                </Avatar>
              </div>

              {/* Name and Info */}
              <div className="flex-1">
                <h1 className="text-4xl font-bold text-blue-900 mb-2">
                  {currentCV.personalInfo.fullname || "Tên của bạn"}
                </h1>
                <p className="text-lg text-gray-600 font-medium mb-4">
                  Business Development Executive
                </p>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-2 text-sm text-gray-700">
                  {currentCV.personalInfo.phone && (
                    <div className="flex items-center gap-2">
                      <span className="font-semibold">Ngày sinh:</span>{" "}
                      {currentCV.personalInfo.phone}
                    </div>
                  )}
                  {currentCV.personalInfo.location && (
                    <div className="flex items-center gap-2">
                      <span className="font-semibold">Giới tính:</span>{" "}
                      {currentCV.personalInfo.location}
                    </div>
                  )}
                  {currentCV.personalInfo.phone && (
                    <div className="flex items-center gap-2">
                      <Phone className="h-4 w-4 text-blue-900" />
                      <span>{currentCV.personalInfo.phone}</span>
                    </div>
                  )}
                  {currentCV.personalInfo.email && (
                    <div className="flex items-center gap-2">
                      <Mail className="h-4 w-4 text-blue-900" />
                      <span className="truncate">
                        {currentCV.personalInfo.email}
                      </span>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>

          <div className="space-y-6">
            {/* Career Objective */}
            {currentCV.personalInfo.summary && (
              <div>
                <h2 className="border-b-2 border-blue-900 pb-1 text-xl font-bold text-blue-900 mb-3">
                  MỤC TIÊU NGHỀ NGHIỆP
                </h2>
                <p className="text-sm leading-relaxed text-gray-700 text-justify">
                  {currentCV.personalInfo.summary}
                </p>
              </div>
            )}

            {/* Education */}
            {currentCV.educations.length > 0 && (
              <div>
                <h2 className="border-b-2 border-blue-900 pb-1 text-xl font-bold text-blue-900 mb-3">
                  HỌC VẤN
                </h2>
                <div className="space-y-4">
                  {currentCV.educations.map((edu) => (
                    <div key={edu.id} className="pl-0">
                      <div className="text-xs text-gray-500 mb-1 font-medium">
                        {edu.startDate} - {edu.endDate || "Hiện tại"}
                      </div>
                      <h3 className="font-bold text-gray-900 text-base mb-1">
                        {edu.school}
                      </h3>
                      <p className="text-sm text-gray-700">
                        <span className="font-semibold">Chuyên ngành:</span>{" "}
                        {edu.degree}
                        {edu.field && ` - ${edu.field}`}
                      </p>
                      {/* <div className="mt-2 text-sm text-gray-700">
                        <p className="font-semibold">Thành tích nổi bật:</p>
                        <ul className="ml-5 mt-1 list-disc space-y-1">
                          <li>Xếp loại: Xuất sắc (GPA: 3.7/4.0)</li>
                          <li>Giải nhì Business Case Competition 2022</li>
                        </ul>
                      </div> */}
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Work Experience */}
            {currentCV.experiences.length > 0 && (
              <div>
                <h2 className="border-b-2 border-blue-900 pb-1 text-xl font-bold text-blue-900 mb-3">
                  KINH NGHIỆM LÀM VIỆC
                </h2>
                <div className="space-y-4">
                  {currentCV.experiences.map((exp) => (
                    <div key={exp.id} className="pl-0">
                      <div className="text-xs text-gray-500 mb-1 font-medium">
                        {exp.startDate} - {exp.endDate || "Hiện tại"}
                      </div>
                      <h3 className="font-bold text-gray-900 text-base mb-1">
                        {exp.company}
                      </h3>
                      <p className="font-semibold text-gray-700 mb-2">
                        {exp.position}
                      </p>
                      {exp.description && (
                        <div className="text-sm text-gray-700 mb-2">
                          <p className="leading-relaxed text-justify">
                            {exp.description}
                          </p>
                        </div>
                      )}
                      {/* <div className="mt-2 text-sm text-gray-700">
                        <p className="font-semibold">Thành tích nổi bật:</p>
                        <ul className="ml-5 mt-1 list-disc space-y-1">
                          <li>Đạt hoặc vượt chỉ tiêu kinh doanh hàng tháng</li>
                          <li>Tăng doanh thu 20% trong 3 tháng liên tiếp</li>
                          <li>
                            Quản lý pipeline bán hàng trị giá 80.000.000 VNĐ
                          </li>
                        </ul>
                      </div> */}
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Skills */}
            {currentCV.skills.length > 0 && (
              <div>
                <h2 className="border-b-2 border-blue-900 pb-1 text-xl font-bold text-blue-900 mb-3">
                  KỸ NĂNG
                </h2>
                <ul className="ml-5 list-disc space-y-1.5 text-sm text-gray-700">
                  {currentCV.skills.map((skill, index) => (
                    <li key={index}>{skill}</li>
                  ))}
                </ul>
              </div>
            )}

            {/* Activities (Optional Section) */}
            {/* <div>
              <h2 className="border-b-2 border-blue-900 pb-1 text-xl font-bold text-blue-900 mb-3">
                HOẠT ĐỘNG
              </h2>
              <ul className="ml-5 list-disc space-y-1.5 text-sm text-gray-700">
                <li>Tình nguyện viên tại chương trình XYZ</li>
                <li>Thành viên CLB ABC</li>
              </ul>
            </div> */}
          </div>
        </div>
      </Card>

      {/* Action Buttons */}
      {/* <div className="flex gap-4">
        <Button onClick={generatePDF} size="lg" className="flex-1 gap-2">
          <Download className="h-5 w-5" />
          Tải CV (PDF)
        </Button>
        <Button variant="outline" size="lg" className="flex-1 gap-2">
          <FileText className="h-5 w-5" />
          Lưu Nháp
        </Button>
      </div> */}
    </div>
  );
}
