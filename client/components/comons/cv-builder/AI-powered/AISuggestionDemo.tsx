/**
 * Demo component to test AI Suggestion Card UI
 * This shows how Before/After suggestions are displayed
 */

import React from "react";
import AISuggestionCard from "@/components/comons/cv-builder/AI-powered/AISuggestionCard";

const AISuggestionDemo = () => {
  // Mock suggestions based on the API response format
  const mockSuggestions: IAISuggestion[] = [
    {
      id: "b3a9f1e7-9909-4f52-9c47-c8cb9f6d435d",
      type: "error",
      section: "summary",
      lineNumber: undefined,
      message: "Summary chỉ có 1 câu, cần mở rộng hơn.",
      suggestion:
        "Before: 'Experienced software developer with 5+ years in full-stack development.'\nAfter: 'Software Developer with over 5 years of experience in full-stack development, seeking opportunities to leverage expertise in React, Node.js, and cloud technologies.'",
      applied: false,
    },
    {
      id: "cc1f3c73-9c52-4f65-bcbd-67e40d7de8ee",
      type: "warning",
      section: "experience",
      lineNumber: undefined,
      message: "Experience at TechCorp Inc. thiếu metrics định lượng.",
      suggestion:
        "Before: 'Lead development of web applications using React, Node.js, and AWS.'\nAfter: 'Led development of web applications using React, Node.js, and AWS, resulting in a 30% increase in user satisfaction and reduced deployment time by 20%.'",
      applied: false,
    },
    {
      id: "e8a039c5-482b-414f-af74-3703bb5d1c7f",
      type: "improvement",
      section: "skills",
      lineNumber: undefined,
      message: "Skills cần phân loại rõ ràng và thêm nhiều kỹ năng liên quan.",
      suggestion:
        "Phân loại kỹ năng: 'Technical Skills: JavaScript, React, Node.js. Soft Skills: Leadership, Mentoring, Problem-solving.'",
      applied: false,
    },
  ];

  const handleApply = (suggestion: IAISuggestion) => {
    console.log("Applied suggestion:", suggestion);
    alert(`Đã áp dụng gợi ý cho phần "${suggestion.section}"`);
  };

  const handleDismiss = (id: string) => {
    console.log("Dismissed suggestion:", id);
    alert(`Đã bỏ qua gợi ý ${id}`);
  };

  return (
    <div className="max-w-2xl mx-auto p-6 space-y-4">
      <h1 className="text-2xl font-bold mb-6">AI Suggestion Cards Demo</h1>
      <p className="text-muted-foreground mb-4">
        Demo hiển thị cách suggestion được chia thành Before/After để dễ quan
        sát.
      </p>

      {mockSuggestions.map((suggestion) => (
        <AISuggestionCard
          key={suggestion.id}
          suggestion={suggestion}
          isApplying={false}
          onApply={handleApply}
          onDismiss={handleDismiss}
        />
      ))}

      <div className="mt-8 p-4 bg-muted rounded-lg">
        <h3 className="font-semibold mb-2">📋 Test trong Console:</h3>
        <p className="text-sm text-muted-foreground">
          Mở Developer Tools và chạy:{" "}
          <code className="bg-background px-1 rounded">
            runSuggestionPartsTests()
          </code>
        </p>
      </div>
    </div>
  );
};

export default AISuggestionDemo;
