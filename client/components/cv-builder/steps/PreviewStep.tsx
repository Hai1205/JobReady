"use client";

import React, { useEffect } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Mail, Phone, Download, Loader2 } from "lucide-react";
import { useCVStore } from "@/stores/cvStore";

export function PreviewStep() {
  const { currentCV, handleGeneratePDF } = useCVStore();
  const [avatarUrl, setAvatarUrl] = React.useState<string | null>(null);

  const [isLoading, setIsLoading] = React.useState(false);

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
    setIsLoading(true);
    await handleGeneratePDF(currentCV);
    setIsLoading(false);
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
          {isLoading ? (
            <>
              <Loader2 className="mr-2 h-4 w-4 animate-spin" />
              Đang tải...
            </>
          ) : (
            <>
              <Download className="h-5 w-5" />
              Tải CV (PDF)
            </>
          )}
        </Button>
      </div>

      {/* PDF Preview */}
      <Card className="overflow-hidden bg-white shadow-2xl border-border/50">
        {/* A4 Preview Container */}
        <div
          id="cv-preview-content"
          className="mx-auto max-w-[210mm] bg-white"
          style={{
            minHeight: "297mm",
            padding: "32px",
            boxShadow: "0 0 20px rgba(0,0,0,0.1)",
            fontFamily: "Arial, sans-serif",
            fontSize: "14px",
            lineHeight: "1.6",
            color: "#333",
          }}
        >
          {/* Header with Background */}
          <div
            style={{
              marginBottom: "32px",
              marginLeft: "-32px",
              marginRight: "-32px",
              marginTop: "-32px",
              backgroundColor: "#f3f4f6",
              padding: "24px 32px",
              borderBottom: "2px solid #e5e7eb",
            }}
          >
            <div
              style={{ display: "flex", gap: "24px", alignItems: "flex-start" }}
            >
              {/* Avatar */}
              <div style={{ flexShrink: 0 }}>
                {avatarUrl ? (
                  <img
                    src={avatarUrl}
                    alt="Avatar"
                    style={{
                      width: "96px",
                      height: "96px",
                      borderRadius: "50%",
                      objectFit: "cover",
                      border: "4px solid white",
                      boxShadow: "0 4px 6px rgba(0,0,0,0.1)",
                    }}
                  />
                ) : (
                  <div
                    style={{
                      width: "96px",
                      height: "96px",
                      borderRadius: "50%",
                      backgroundColor: "#1e3a8a",
                      color: "white",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      fontSize: "32px",
                      fontWeight: "bold",
                      border: "4px solid white",
                      boxShadow: "0 4px 6px rgba(0,0,0,0.1)",
                    }}
                  >
                    {currentCV.personalInfo.fullname
                      ? currentCV.personalInfo.fullname
                          .split(" ")
                          .map((n) => n[0])
                          .join("")
                          .toUpperCase()
                          .slice(0, 2)
                      : "CV"}
                  </div>
                )}
              </div>

              {/* Name and Info */}
              <div style={{ flex: 1 }}>
                <h1
                  style={{
                    fontSize: "36px",
                    fontWeight: "bold",
                    color: "#1e3a8a",
                    marginBottom: "8px",
                    marginTop: 0,
                  }}
                >
                  {currentCV.personalInfo.fullname || "Tên của bạn"}
                </h1>
                <p
                  style={{
                    fontSize: "18px",
                    color: "#4b5563",
                    fontWeight: "500",
                    marginBottom: "16px",
                    marginTop: 0,
                  }}
                >
                  Business Development Executive
                </p>
                <div
                  style={{
                    display: "grid",
                    gridTemplateColumns: "1fr 1fr",
                    gap: "8px",
                    fontSize: "13px",
                    color: "#374151",
                  }}
                >
                  {currentCV.personalInfo.phone && (
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "8px",
                      }}
                    >
                      <span style={{ fontWeight: "600" }}>Ngày sinh:</span>
                      <span>{currentCV.personalInfo.phone}</span>
                    </div>
                  )}
                  {currentCV.personalInfo.location && (
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "8px",
                      }}
                    >
                      <span style={{ fontWeight: "600" }}>Giới tính:</span>
                      <span>{currentCV.personalInfo.location}</span>
                    </div>
                  )}
                  {currentCV.personalInfo.phone && (
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "8px",
                      }}
                    >
                      <span>📞</span>
                      <span>{currentCV.personalInfo.phone}</span>
                    </div>
                  )}
                  {currentCV.personalInfo.email && (
                    <div
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "8px",
                      }}
                    >
                      <span>✉️</span>
                      <span
                        style={{
                          overflow: "hidden",
                          textOverflow: "ellipsis",
                          whiteSpace: "nowrap",
                        }}
                      >
                        {currentCV.personalInfo.email}
                      </span>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>

          <div style={{ marginTop: "24px" }}>
            {/* Career Objective */}
            {currentCV.personalInfo.summary && (
              <div style={{ marginBottom: "24px" }}>
                <h2
                  style={{
                    borderBottom: "2px solid #1e3a8a",
                    paddingBottom: "4px",
                    fontSize: "20px",
                    fontWeight: "bold",
                    color: "#1e3a8a",
                    marginBottom: "12px",
                    marginTop: 0,
                  }}
                >
                  MỤC TIÊU NGHỀ NGHIỆP
                </h2>
                <p
                  style={{
                    fontSize: "13px",
                    lineHeight: "1.8",
                    color: "#374151",
                    textAlign: "justify",
                    margin: 0,
                  }}
                >
                  {currentCV.personalInfo.summary}
                </p>
              </div>
            )}

            {/* Education */}
            {currentCV.educations.length > 0 && (
              <div style={{ marginBottom: "24px" }}>
                <h2
                  style={{
                    borderBottom: "2px solid #1e3a8a",
                    paddingBottom: "4px",
                    fontSize: "20px",
                    fontWeight: "bold",
                    color: "#1e3a8a",
                    marginBottom: "12px",
                    marginTop: 0,
                  }}
                >
                  HỌC VẤN
                </h2>
                <div>
                  {currentCV.educations.map((edu, index) => (
                    <div
                      key={edu.id}
                      style={{
                        marginBottom:
                          index < currentCV.educations.length - 1 ? "16px" : 0,
                      }}
                    >
                      <div
                        style={{
                          fontSize: "11px",
                          color: "#6b7280",
                          marginBottom: "4px",
                          fontWeight: "500",
                        }}
                      >
                        {edu.startDate} - {edu.endDate || "Hiện tại"}
                      </div>
                      <h3
                        style={{
                          fontWeight: "bold",
                          color: "#111827",
                          fontSize: "15px",
                          marginBottom: "4px",
                          marginTop: 0,
                        }}
                      >
                        {edu.school}
                      </h3>
                      <p
                        style={{
                          fontSize: "13px",
                          color: "#374151",
                          margin: 0,
                        }}
                      >
                        <span style={{ fontWeight: "600" }}>Chuyên ngành:</span>{" "}
                        {edu.degree}
                        {edu.field && ` - ${edu.field}`}
                      </p>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Work Experience */}
            {currentCV.experiences.length > 0 && (
              <div style={{ marginBottom: "24px" }}>
                <h2
                  style={{
                    borderBottom: "2px solid #1e3a8a",
                    paddingBottom: "4px",
                    fontSize: "20px",
                    fontWeight: "bold",
                    color: "#1e3a8a",
                    marginBottom: "12px",
                    marginTop: 0,
                  }}
                >
                  KINH NGHIỆM LÀM VIỆC
                </h2>
                <div>
                  {currentCV.experiences.map((exp, index) => (
                    <div
                      key={exp.id}
                      style={{
                        marginBottom:
                          index < currentCV.experiences.length - 1 ? "16px" : 0,
                      }}
                    >
                      <div
                        style={{
                          fontSize: "11px",
                          color: "#6b7280",
                          marginBottom: "4px",
                          fontWeight: "500",
                        }}
                      >
                        {exp.startDate} - {exp.endDate || "Hiện tại"}
                      </div>
                      <h3
                        style={{
                          fontWeight: "bold",
                          color: "#111827",
                          fontSize: "15px",
                          marginBottom: "4px",
                          marginTop: 0,
                        }}
                      >
                        {exp.company}
                      </h3>
                      <p
                        style={{
                          fontWeight: "600",
                          color: "#374151",
                          marginBottom: "8px",
                          marginTop: 0,
                          fontSize: "13px",
                        }}
                      >
                        {exp.position}
                      </p>
                      {exp.description && (
                        <p
                          style={{
                            fontSize: "13px",
                            color: "#374151",
                            lineHeight: "1.8",
                            textAlign: "justify",
                            margin: 0,
                          }}
                        >
                          {exp.description}
                        </p>
                      )}
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Skills */}
            {currentCV.skills.length > 0 && (
              <div style={{ marginBottom: "24px" }}>
                <h2
                  style={{
                    borderBottom: "2px solid #1e3a8a",
                    paddingBottom: "4px",
                    fontSize: "20px",
                    fontWeight: "bold",
                    color: "#1e3a8a",
                    marginBottom: "12px",
                    marginTop: 0,
                  }}
                >
                  KỸ NĂNG
                </h2>
                <ul
                  style={{
                    marginLeft: "20px",
                    paddingLeft: 0,
                    margin: 0,
                  }}
                >
                  {currentCV.skills.map((skill, index) => (
                    <li
                      key={index}
                      style={{
                        fontSize: "13px",
                        color: "#374151",
                        marginBottom:
                          index < currentCV.skills.length - 1 ? "6px" : 0,
                      }}
                    >
                      {skill}
                    </li>
                  ))}
                </ul>
              </div>
            )}
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
