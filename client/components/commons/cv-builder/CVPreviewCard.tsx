"use client";

import React, { useEffect, useMemo } from "react";
import { Card } from "@/components/ui/card";
import { generateCVHTML } from "./templates/templates";

interface CVPreviewCardProps {
  currentCV: ICV | null;
  className?: string;
}

export function CVPreviewCard({ currentCV, className }: CVPreviewCardProps) {
  // Convert File to base64 if avatar là File
  useEffect(() => {
    if (currentCV?.avatar && currentCV.avatar instanceof File) {
      const reader = new FileReader();
      reader.onloadend = () => {};
      reader.readAsDataURL(currentCV.avatar);
    }
  }, [currentCV?.avatar]);

  // Render HTML
  const cvHtml = useMemo(() => {
    if (!currentCV) return "";
    return generateCVHTML(currentCV, currentCV.template);
  }, [currentCV]);

  return (
    <Card 
    className={`overflow-hidden bg-white shadow-2xl border-border/50 ${className}`}
    >
      <div
        id="cv-preview-content"
        dangerouslySetInnerHTML={{ __html: cvHtml }}
      />
    </Card>
  );
}
