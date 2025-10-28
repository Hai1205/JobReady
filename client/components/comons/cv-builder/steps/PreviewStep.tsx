"use client";

import React, { useEffect } from "react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Download, Loader2 } from "lucide-react";
import { useCurrentCV } from "@/hooks/use-cv-mode";

export function PreviewStep() {
  const { currentCV, handleGeneratePDF } = useCurrentCV();

  const [isLoading, setIsLoading] = React.useState(false);

  // Convert File to base64 URL for display
  useEffect(() => {
    if (currentCV?.avatar && currentCV.avatar instanceof File) {
      const reader = new FileReader();
      reader.onloadend = () => {};
      reader.readAsDataURL(currentCV.avatar);
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
          className="mx-auto bg-white"
          style={{
            width: "210mm",
            minHeight: "297mm",
            padding: "40px",
            boxShadow: "0 0 20px rgba(0,0,0,0.1)",
            fontFamily: "'Times New Roman', Times, serif",
            fontSize: "13px",
            lineHeight: "1.5",
            color: "#000",
          }}
        >
          {/* Header Section */}
          <div
            style={{
              display: "flex",
              gap: "30px",
              alignItems: "flex-start",
              marginBottom: "30px",
              paddingBottom: "20px",
              borderBottom: "1px solid #ddd",
            }}
          >
            {/* Avatar */}
            <div style={{ flexShrink: 0 }}>
              {(currentCV.personalInfo.avatarUrl ||
                currentCV.personalInfo.avatarPublicId) && (
                <img
                  src={
                    currentCV.personalInfo.avatarUrl ||
                    currentCV.personalInfo.avatarPublicId ||
                    ""
                  }
                  alt="Avatar"
                  style={{
                    width: "120px",
                    height: "120px",
                    borderRadius: "50%",
                    objectFit: "cover",
                    border: "3px solid #4A90E2",
                  }}
                />
              )}
              {!currentCV.personalInfo.avatarUrl &&
                !currentCV.personalInfo.avatarPublicId && (
                  <div
                    style={{
                      width: "120px",
                      height: "120px",
                      borderRadius: "50%",
                      backgroundColor: "#4A90E2",
                      color: "white",
                      display: "flex",
                      alignItems: "center",
                      justifyContent: "center",
                      fontSize: "40px",
                      fontWeight: "bold",
                      border: "3px solid #4A90E2",
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

            {/* Personal Info */}
            <div style={{ flex: 1 }}>
              <h1
                style={{
                  fontSize: "32px",
                  fontWeight: "bold",
                  color: "#000",
                  marginBottom: "8px",
                  marginTop: 0,
                  textTransform: "uppercase",
                }}
              >
                {currentCV.personalInfo.fullname || "Họ và tên"}
              </h1>
              <p
                style={{
                  fontSize: "15px",
                  color: "#666",
                  fontStyle: "italic",
                  marginBottom: "15px",
                  marginTop: 0,
                }}
              >
                {currentCV.title || "Vị trí ứng tuyển"}
              </p>
              <div
                style={{
                  fontSize: "13px",
                  color: "#333",
                  lineHeight: "1.8",
                }}
              >
                {currentCV.personalInfo.email && (
                  <div style={{ marginBottom: "5px" }}>
                    <span style={{ fontWeight: "600" }}>Email: </span>
                    <span>{currentCV.personalInfo.email}</span>
                  </div>
                )}
                {currentCV.personalInfo.phone && (
                  <div style={{ marginBottom: "5px" }}>
                    <span style={{ fontWeight: "600" }}>Phone: </span>
                    <span>{currentCV.personalInfo.phone}</span>
                  </div>
                )}
                {currentCV.personalInfo.location && (
                  <div style={{ marginBottom: "5px" }}>
                    <span style={{ fontWeight: "600" }}>Location: </span>
                    <span>{currentCV.personalInfo.location}</span>
                  </div>
                )}
              </div>
            </div>
          </div>

          <div style={{ marginTop: "0" }}>
            {/* Career Objective */}
            {currentCV.personalInfo.summary && (
              <div style={{ marginBottom: "25px" }}>
                <h2
                  style={{
                    fontSize: "16px",
                    fontWeight: "bold",
                    color: "#000",
                    marginBottom: "10px",
                    marginTop: 0,
                    textTransform: "uppercase",
                    borderBottom: "2px solid #4A90E2",
                    paddingBottom: "5px",
                  }}
                >
                  Giới thiệu
                </h2>
                <p
                  style={{
                    fontSize: "13px",
                    lineHeight: "1.6",
                    color: "#333",
                    textAlign: "justify",
                    margin: 0,
                  }}
                >
                  {currentCV.personalInfo.summary}
                </p>
              </div>
            )}

            {/* Work Experience */}
            {currentCV.experiences.length > 0 && (
              <div style={{ marginBottom: "25px" }}>
                <h2
                  style={{
                    fontSize: "16px",
                    fontWeight: "bold",
                    color: "#000",
                    marginBottom: "10px",
                    marginTop: 0,
                    textTransform: "uppercase",
                    borderBottom: "2px solid #4A90E2",
                    paddingBottom: "5px",
                  }}
                >
                  Kinh nghiệm làm việc
                </h2>
                <div>
                  {currentCV.experiences.map((exp, index) => (
                    <div
                      key={index}
                      style={{
                        marginBottom:
                          index < currentCV.experiences.length - 1 ? "15px" : 0,
                        backgroundColor: "#f8f9fa",
                        padding: "12px 15px",
                        borderLeft: "3px solid #4A90E2",
                      }}
                    >
                      <h3
                        style={{
                          fontWeight: "bold",
                          color: "#000",
                          fontSize: "14px",
                          marginBottom: "5px",
                          marginTop: 0,
                        }}
                      >
                        {exp.position}
                      </h3>
                      <p
                        style={{
                          fontSize: "13px",
                          color: "#666",
                          marginBottom: "5px",
                          marginTop: 0,
                          fontStyle: "italic",
                        }}
                      >
                        {exp.company}
                      </p>
                      <p
                        style={{
                          fontSize: "12px",
                          color: "#999",
                          marginBottom: "8px",
                          marginTop: 0,
                        }}
                      >
                        {exp.startDate} - {exp.endDate || "Present"}
                      </p>
                      {exp.description && (
                        <p
                          style={{
                            fontSize: "13px",
                            color: "#333",
                            lineHeight: "1.6",
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

            {/* Education */}
            {currentCV.educations.length > 0 && (
              <div style={{ marginBottom: "25px" }}>
                <h2
                  style={{
                    fontSize: "16px",
                    fontWeight: "bold",
                    color: "#000",
                    marginBottom: "10px",
                    marginTop: 0,
                    textTransform: "uppercase",
                    borderBottom: "2px solid #4A90E2",
                    paddingBottom: "5px",
                  }}
                >
                  Học vấn
                </h2>
                <div>
                  {currentCV.educations.map((edu, index) => (
                    <div
                      key={index}
                      style={{
                        marginBottom:
                          index < currentCV.educations.length - 1 ? "15px" : 0,
                        backgroundColor: "#f8f9fa",
                        padding: "12px 15px",
                        borderLeft: "3px solid #4A90E2",
                      }}
                    >
                      <h3
                        style={{
                          fontWeight: "bold",
                          color: "#000",
                          fontSize: "14px",
                          marginBottom: "5px",
                          marginTop: 0,
                        }}
                      >
                        {edu.degree}
                      </h3>
                      <p
                        style={{
                          fontSize: "13px",
                          color: "#666",
                          marginBottom: "5px",
                          marginTop: 0,
                          fontStyle: "italic",
                        }}
                      >
                        {edu.school}
                      </p>
                      <p
                        style={{
                          fontSize: "12px",
                          color: "#999",
                          margin: 0,
                        }}
                      >
                        {edu.field && (
                          <span>
                            {edu.field}
                            {" • "}
                          </span>
                        )}
                        {edu.startDate} - {edu.endDate || "Present"}
                      </p>
                    </div>
                  ))}
                </div>
              </div>
            )}

            {/* Skills */}
            {currentCV.skills.length > 0 && (
              <div style={{ marginBottom: "25px" }}>
                <h2
                  style={{
                    fontSize: "16px",
                    fontWeight: "bold",
                    color: "#000",
                    marginBottom: "10px",
                    marginTop: 0,
                    textTransform: "uppercase",
                    borderBottom: "2px solid #4A90E2",
                    paddingBottom: "5px",
                  }}
                >
                  Kỹ năng
                </h2>
                <div
                  style={{
                    display: "flex",
                    flexWrap: "wrap",
                    gap: "8px",
                  }}
                >
                  {currentCV.skills.map((skill, index) => (
                    <span
                      key={index}
                      style={{
                        display: "inline-block",
                        padding: "6px 12px",
                        backgroundColor: "#E3F2FD",
                        color: "#1976D2",
                        borderRadius: "4px",
                        fontSize: "12px",
                        fontWeight: "500",
                      }}
                    >
                      {skill}
                    </span>
                  ))}
                </div>
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
