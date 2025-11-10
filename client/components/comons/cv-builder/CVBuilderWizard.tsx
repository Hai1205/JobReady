"use client";

import { Button } from "@/components/ui/button";
import { Progress } from "@/components/ui/progress";
import { PersonalInfoStep } from "./steps/PersonalInfoStep";
import { ExperienceStep } from "./steps/ExperienceStep";
import { SkillsStep } from "./steps/SkillsStep";
import { PreviewStep } from "./steps/PreviewStep";
import {
  ChevronLeft,
  ChevronRight,
  Loader2,
  Save,
  ChevronDown,
  ChevronUp,
} from "lucide-react";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { useCVStore } from "@/stores/cvStore";
import { useAuthStore } from "@/stores/authStore";
import { EducationStep } from "./steps/EducationStep";
import { useState } from "react";
import { ColorThemeSelector } from "./ColorThemeSelector";
import { TemplateSelector } from "./TemplateSelector";

const steps = [
  { id: 0, title: "Personal Info", component: PersonalInfoStep },
  { id: 1, title: "Experience", component: ExperienceStep },
  { id: 2, title: "Education", component: EducationStep },
  { id: 3, title: "Skills", component: SkillsStep },
  { id: 4, title: "Preview & Export", component: PreviewStep },
];

export function CVBuilderWizard() {
  const { userAuth } = useAuthStore();
  const {
    isLoading,
    currentStep,
    handleSetCurrentStep,
    currentCV,
    updateCV,
    handleUpdateCV,
  } = useCVStore();

  const [isCustomizationExpanded, setIsCustomizationExpanded] = useState(false);

  const CurrentStepComponent = steps[currentStep].component;

  const progress = ((currentStep + 1) / steps.length) * 100;

  const handleNext = () => {
    if (currentStep < steps.length - 1) {
      handleSetCurrentStep(currentStep + 1);
    }
  };

  const handlePrevious = () => {
    if (currentStep > 0) {
      handleSetCurrentStep(currentStep - 1);
    }
  };

  const handleSave = async () => {
    if (!currentCV || !userAuth) return;

    await updateCV(
      currentCV.id,
      currentCV.title,
      currentCV.avatar as File,
      currentCV.personalInfo,
      currentCV.experiences,
      currentCV.educations,
      currentCV.skills,
      currentCV.isVisibility,
      currentCV.color,
      currentCV.template
    );
  };

  const handleCVUpdate = (cvData: Partial<ICV>) => {
    handleUpdateCV(cvData);
  };

  return (
    <div className="flex flex-col gap-8">
      {/* Tiêu đề */}
      <div className="flex flex-col gap-2">
        <Label htmlFor="cv-title">Tiêu đề</Label>
        <Input
          id="cv-title"
          value={currentCV?.title}
          onChange={(e) => handleCVUpdate({ title: e.target.value })}
          placeholder="e.g., Software Engineer CV"
          className="text-lg font-semibold"
        />
      </div>

      {/* Visibility & Tiêu đề */}
      <div className="flex flex-col gap-4">
        {/* Visibility Toggle */}
        <div className="flex items-center justify-between rounded-lg border border-border p-4">
          <div className="space-y-0.5">
            <Label htmlFor="visibility-toggle" className="text-base">
              Chế độ hiển thị
            </Label>
            <p className="text-sm text-muted-foreground">
              {currentCV?.isVisibility
                ? "CV của bạn hiện công khai và có thể được tìm thấy bởi nhà tuyển dụng"
                : "CV của bạn ở chế độ riêng tư, chỉ bạn mới có thể xem"}
            </p>
          </div>
          <Switch
            id="visibility-toggle"
            checked={currentCV?.isVisibility || false}
            onCheckedChange={(checked) =>
              handleCVUpdate({
                isVisibility: checked,
              })
            }
          />
        </div>

        {/* CV Customization Section */}
        <div className="rounded-lg border border-border">
          {/* Header */}
          <div
            className="flex items-center justify-between p-4 cursor-pointer hover:bg-muted/50 transition-colors"
            onClick={() => setIsCustomizationExpanded(!isCustomizationExpanded)}
          >
            <div>
              <Label className="text-base font-semibold cursor-pointer">
                Tùy chỉnh giao diện CV
              </Label>
              <p className="text-sm text-muted-foreground mt-1">
                {isCustomizationExpanded
                  ? "Thu gọn để ẩn các tùy chọn màu sắc và template"
                  : `Màu: ${currentCV?.color || "#3498db"} | Template: ${
                      currentCV?.template || "modern"
                    }`}
              </p>
            </div>
            <Button type="button" variant="ghost" size="sm" className="gap-2">
              {isCustomizationExpanded ? (
                <>
                  Thu gọn <ChevronUp className="h-4 w-4" />
                </>
              ) : (
                <>
                  Mở rộng <ChevronDown className="h-4 w-4" />
                </>
              )}
            </Button>
          </div>

          {/* Content */}
          {isCustomizationExpanded && (
            <div className="border-t border-border p-4 space-y-4 animate-in fade-in-50 slide-in-from-top-2 duration-200">
              {/* Color Theme Selector */}
              <ColorThemeSelector
                selectedColor={currentCV?.color || "#3498db"}
                onColorChange={(color) => handleCVUpdate({ color })}
              />

              {/* Divider */}
              <div className="border-t border-border" />

              {/* Template Selector */}
              <TemplateSelector
                selectedTemplate={currentCV?.template || "modern"}
                onTemplateChange={(template) => handleCVUpdate({ template })}
              />
            </div>
          )}
        </div>
      </div>

      {/* File Import */}
      {/* {currentStep === 0 && (
        <div className="mb-4">
          <FileImport />
        </div>
      )} */}

      {/* Progress Bar */}
      <div className="flex flex-col gap-2">
        <div className="flex items-center justify-between">
          <span className="text-sm font-medium">
            Step {currentStep + 1} of {steps.length}: {steps[currentStep].title}
          </span>
          <span className="text-sm text-muted-foreground">
            {Math.round(progress)}% Complete
          </span>
        </div>
        <Progress value={progress} />
      </div>

      {/* Step Navigation */}
      <div className="flex items-center gap-2 overflow-x-auto pb-2">
        {steps.map((step, index) => (
          <button
            key={step.id}
            onClick={() => handleSetCurrentStep(index)}
            className={`flex items-center gap-2 rounded-lg px-4 py-2 text-sm font-medium transition-colors whitespace-nowrap ${
              index === currentStep
                ? "bg-primary text-primary-foreground"
                : index < currentStep
                ? "bg-secondary text-secondary-foreground"
                : "bg-muted text-muted-foreground"
            }`}
          >
            <span
              className={`flex h-6 w-6 items-center justify-center rounded-full text-xs ${
                index === currentStep
                  ? "bg-primary-foreground text-primary"
                  : index < currentStep
                  ? "bg-secondary-foreground text-secondary"
                  : "bg-muted-foreground text-muted"
              }`}
            >
              {index + 1}
            </span>
            {step.title}
          </button>
        ))}
      </div>

      {/* Current Step Content */}
      <div className="min-h-[400px]">
        <CurrentStepComponent />
      </div>

      {/* Navigation Buttons */}
      <div className="flex items-center justify-between border-t border-border pt-6">
        <Button
          variant="outline"
          onClick={handlePrevious}
          disabled={currentStep === 0}
        >
          <ChevronLeft className="mr-2 h-4 w-4" />
          Previous
        </Button>

        <div className="flex gap-2">
          {currentStep < steps.length - 1 ? (
            <Button onClick={handleNext}>
              Next
              <ChevronRight className="ml-2 h-4 w-4" />
            </Button>
          ) : (
            <Button onClick={handleSave}>
              {isLoading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Đang lưu...
                </>
              ) : (
                <>
                  <Save className="mr-2 h-4 w-4" />
                  Lưu
                </>
              )}
            </Button>
          )}
        </div>
      </div>
    </div>
  );
}
