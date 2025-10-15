"use client";

import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Mail, Phone, Download, FileText } from "lucide-react";
import { useCVStore } from "@/stores/cvStore";
import jsPDF from "jspdf";
import { toast } from "react-toastify";

export function PreviewStep() {
  const { currentCV } = useCVStore();

  if (!currentCV) return null;

  const generatePDF = () => {
    if (!currentCV.personalInfo?.fullname) {
      toast.error("⚠️ Vui lòng điền TÊN trong Step 1!");
      return;
    }

    try {
      const doc = new jsPDF({
        orientation: "portrait",
        unit: "mm",
        format: "a4",
      });

      const W = doc.internal.pageSize.getWidth();
      const H = doc.internal.pageSize.getHeight();
      const M = 15;
      let Y = M;

      const plain = (text: string) =>
        text
          .normalize("NFD")
          .replace(/[\u0300-\u036f]/g, "")
          .replace(/đ/g, "d")
          .replace(/Đ/g, "D");

      const needSpace = (space: number) => {
        if (Y + space > H - M) {
          doc.addPage();
          Y = M;
        }
      };

      // ========== HEADER BACKGROUND ==========
      doc.setFillColor(245, 245, 245);
      doc.rect(0, 0, W, 70, "F");

      let textStartX = M;

      // ========== AVATAR (CIRCULAR) ==========
      if (currentCV.personalInfo.avatar) {
        try {
          const AV_SIZE = 40;
          const AV_X = M;
          const AV_Y = Y + 3;

          doc.addImage(
            currentCV.personalInfo.avatar,
            "JPEG",
            AV_X,
            AV_Y,
            AV_SIZE,
            AV_SIZE
          );

          doc.setDrawColor(0, 51, 153);
          doc.setLineWidth(2);
          doc.circle(AV_X + AV_SIZE / 2, AV_Y + AV_SIZE / 2, AV_SIZE / 2, "S");

          textStartX = M + AV_SIZE + 10;
        } catch (err) {
          textStartX = M;
        }
      }

      // ========== NAME & JOB TITLE ==========
      doc.setFont("helvetica", "bold");
      doc.setFontSize(24);
      doc.setTextColor(0, 51, 153);
      doc.text(plain(currentCV.personalInfo.fullname), textStartX, Y + 15);

      doc.setFont("helvetica", "normal");
      doc.setFontSize(13);
      doc.setTextColor(100, 100, 100);
      doc.text("Business Development Executive", textStartX, Y + 24);

      // ========== CONTACT INFO (2 COLUMNS) ==========
      doc.setFontSize(9);
      doc.setTextColor(70, 70, 70);
      let cy = Y + 34;

      // Row 1
      if (currentCV.personalInfo.phone) {
        doc.setFont("helvetica", "bold");
        doc.text("Ngay sinh:", textStartX, cy);
        doc.setFont("helvetica", "normal");
        doc.text(currentCV.personalInfo.phone, textStartX + 22, cy);
      }

      if (currentCV.personalInfo.location) {
        const col2X = textStartX + 80;
        doc.setFont("helvetica", "bold");
        doc.text("Gioi tinh:", col2X, cy);
        doc.setFont("helvetica", "normal");
        doc.text(plain(currentCV.personalInfo.location), col2X + 20, cy);
      }

      // Row 2
      cy += 5.5;
      if (currentCV.personalInfo.phone) {
        doc.text("0782748863", textStartX, cy);
      }

      if (currentCV.personalInfo.email) {
        const col2X = textStartX + 80;
        const emailLines = doc.splitTextToSize(
          currentCV.personalInfo.email,
          75
        );
        doc.text(emailLines[0], col2X, cy);
      }

      Y = 78;

      // ========== SECTION TITLE HELPER ==========
      const addSectionTitle = (title: string) => {
        needSpace(15);
        doc.setFont("helvetica", "bold");
        doc.setFontSize(13);
        doc.setTextColor(0, 51, 153);
        doc.text(plain(title), M, Y);
        doc.setDrawColor(0, 51, 153);
        doc.setLineWidth(0.8);
        doc.line(M, Y + 1.5, W - M, Y + 1.5);
        Y += 8;
      };

      // ========== CAREER OBJECTIVE ==========
      if (currentCV.personalInfo.summary) {
        addSectionTitle("MUC TIEU NGHE NGHIEP");
        doc.setFont("helvetica", "normal");
        doc.setFontSize(10);
        doc.setTextColor(60, 60, 60);
        const lines = doc.splitTextToSize(
          plain(currentCV.personalInfo.summary),
          W - 2 * M
        );
        lines.forEach((line: string) => {
          needSpace(5);
          doc.text(line, M, Y);
          Y += 5;
        });
        Y += 6;
      }

      // ========== EDUCATION ==========
      if (currentCV.education.length > 0) {
        addSectionTitle("HOC VAN");
        currentCV.education.forEach((edu) => {
          needSpace(20);
          doc.setFont("helvetica", "italic");
          doc.setFontSize(9);
          doc.setTextColor(130, 130, 130);
          doc.text(`${edu.startDate} - ${edu.endDate || "Hien tai"}`, M, Y);
          Y += 5;

          doc.setFont("helvetica", "bold");
          doc.setFontSize(11);
          doc.setTextColor(0, 0, 0);
          doc.text(plain(edu.school), M, Y);
          Y += 5.5;

          doc.setFont("helvetica", "normal");
          doc.setFontSize(10);
          doc.setTextColor(60, 60, 60);
          doc.text(
            plain(
              `Chuyen nganh: ${edu.degree}${edu.field ? ` - ${edu.field}` : ""}`
            ),
            M + 2,
            Y
          );
          Y += 5;

          doc.setFont("helvetica", "bold");
          doc.setFontSize(9);
          doc.text("Thanh tich noi bat:", M, Y);
          Y += 4.5;

          doc.setFont("helvetica", "normal");
          const achievements = [
            "Xep loai: Xuat sac (GPA: 3.7/4.0)",
            "Giai nhi Business Case Competition 2022",
          ];
          achievements.forEach((item) => {
            needSpace(4.5);
            doc.text(`• ${item}`, M + 2, Y);
            Y += 4.5;
          });
          Y += 4;
        });
      }

      // ========== EXPERIENCE ==========
      if (currentCV.experience.length > 0) {
        addSectionTitle("KINH NGHIEM LAM VIEC");
        currentCV.experience.forEach((exp) => {
          needSpace(25);
          doc.setFont("helvetica", "italic");
          doc.setFontSize(9);
          doc.setTextColor(130, 130, 130);
          doc.text(`${exp.startDate} - ${exp.endDate || "Hien tai"}`, M, Y);
          Y += 5;

          doc.setFont("helvetica", "bold");
          doc.setFontSize(11);
          doc.setTextColor(0, 0, 0);
          doc.text(plain(exp.company), M, Y);
          Y += 5.5;

          doc.setFont("helvetica", "bold");
          doc.setFontSize(10);
          doc.setTextColor(60, 60, 60);
          doc.text(plain(exp.position), M, Y);
          Y += 6;

          if (exp.description) {
            doc.setFont("helvetica", "normal");
            doc.setFontSize(9);
            const descLines = doc.splitTextToSize(
              plain(exp.description),
              W - 2 * M - 4
            );
            descLines.forEach((line: string) => {
              needSpace(4.5);
              doc.text(line, M + 2, Y);
              Y += 4.5;
            });
            Y += 2;
          }

          doc.setFont("helvetica", "bold");
          doc.setFontSize(9);
          doc.text("Thanh tich noi bat:", M, Y);
          Y += 4.5;

          doc.setFont("helvetica", "normal");
          const achievements = [
            "Dat hoac vuot chi tieu kinh doanh hang thang",
            "Tang doanh thu 20% trong 3 thang lien tiep",
            "Quan ly pipeline ban hang tri gia 80.000.000 VND",
          ];
          achievements.forEach((item) => {
            needSpace(4.5);
            doc.text(`• ${item}`, M + 2, Y);
            Y += 4.5;
          });
          Y += 6;
        });
      }

      // ========== SKILLS ==========
      if (currentCV.skills.length > 0) {
        addSectionTitle("KY NANG");
        doc.setFont("helvetica", "normal");
        doc.setFontSize(10);
        doc.setTextColor(60, 60, 60);
        currentCV.skills.forEach((skill) => {
          needSpace(5);
          doc.text(`• ${plain(skill)}`, M + 2, Y);
          Y += 5;
        });
        Y += 6;
      }

      // ========== ACTIVITIES ==========
      addSectionTitle("HOAT DONG");
      doc.setFont("helvetica", "normal");
      doc.setFontSize(10);
      doc.setTextColor(60, 60, 60);
      const activities = [
        "Tinh nguyen vien tai chuong trinh XYZ",
        "Thanh vien CLB ABC",
      ];
      activities.forEach((item) => {
        needSpace(5);
        doc.text(`• ${item}`, M + 2, Y);
        Y += 5;
      });

      // ========== SAVE PDF ==========
      const filename = `CV_${plain(currentCV.personalInfo.fullname).replace(
        /\s+/g,
        "_"
      )}.pdf`;
      doc.save(filename);
      toast.success("✅ Tải xuống CV thành công!");
    } catch (error) {
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
                  <AvatarImage
                    src={currentCV.personalInfo.avatar}
                    className="object-cover"
                  />
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
            {currentCV.education.length > 0 && (
              <div>
                <h2 className="border-b-2 border-blue-900 pb-1 text-xl font-bold text-blue-900 mb-3">
                  HỌC VẤN
                </h2>
                <div className="space-y-4">
                  {currentCV.education.map((edu) => (
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
            {currentCV.experience.length > 0 && (
              <div>
                <h2 className="border-b-2 border-blue-900 pb-1 text-xl font-bold text-blue-900 mb-3">
                  KINH NGHIỆM LÀM VIỆC
                </h2>
                <div className="space-y-4">
                  {currentCV.experience.map((exp) => (
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
