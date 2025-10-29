"use client";

import { Button } from "@/components/ui/button";
import { Progress } from "@/components/ui/progress";
import { PersonalInfoStep } from "./steps/PersonalInfoStep";
import { ExperienceStep } from "./steps/ExperienceStep";
import { SkillsStep } from "./steps/SkillsStep";
import { PreviewStep } from "./steps/PreviewStep";
import { ChevronLeft, ChevronRight, Loader2, Save } from "lucide-react";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Switch } from "@/components/ui/switch";
import { useCVStore } from "@/stores/cvStore";
import { useAuthStore } from "@/stores/authStore";
import { EducationStep } from "./steps/EducationStep";
import { EPrivacy } from "@/types/enum";
import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useCVModeStore } from "@/hooks/use-cv-mode";

const steps = [
  { id: 0, title: "Personal Info", component: PersonalInfoStep },
  { id: 1, title: "Experience", component: ExperienceStep },
  { id: 2, title: "Education", component: EducationStep },
  { id: 3, title: "Skills", component: SkillsStep },
  { id: 4, title: "Preview & Export", component: PreviewStep },
];

interface CVBuilderWizardProps {
  mode?: "create" | "update"; // Xác định rõ mode
}

export function CVBuilderWizard({ mode = "create" }: CVBuilderWizardProps) {
  const { userAuth } = useAuthStore();
  const { setMode } = useCVModeStore();
  const {
    isLoading,
    currentStep,
    handleSetCurrentStep,
    currentCVCreate,
    currentCVUpdate,
    updateCV,
    createCV,
    handleUpdateCVCreate,
    handleUpdateCVUpdate,
    handleSetCurrentCVCreate,
  } = useCVStore();

  const router = useRouter();

  // Set mode khi component mount
  useEffect(() => {
    setMode(mode);
  }, [mode, setMode]);

  // Initialize currentCVCreate if null for create mode
  useEffect(() => {
    if (mode === "create" && !currentCVCreate) {
      handleSetCurrentCVCreate({
        id: "",
        title: "Untitled CV",
        personalInfo: {
          fullname: "",
          email: "",
          phone: "",
          location: "",
          summary: "",
        },
        experiences: [],
        educations: [],
        skills: [],
        privacy: EPrivacy.PRIVATE,
      } as ICV);
    }
  }, [mode, currentCVCreate, handleSetCurrentCVCreate]);

  // Chọn CV dựa trên mode
  const currentCV = mode === "create" ? currentCVCreate : currentCVUpdate;

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
    if (!currentCV) return;

    if (currentCV.id) {
      await updateCV(
        currentCV.id,
        currentCV.title,
        currentCV.avatar as File,
        currentCV.personalInfo,
        currentCV.experiences,
        currentCV.educations,
        currentCV.skills,
        currentCV.privacy
      );
    } else {
      const res = await createCV(
        userAuth?.id || "",
        currentCV.title,
        currentCV.avatar as File,
        currentCV.personalInfo,
        currentCV.experiences,
        currentCV.educations,
        currentCV.skills,
        currentCV.privacy
      );

      // If API returns created CV, set it into store and redirect to its URL to avoid duplicate creates
      const createdCv = res?.data?.cv;
      if (createdCv && createdCv.id) {
        if (handleSetCurrentCVCreate) {
          handleSetCurrentCVCreate(createdCv);
        }
        
        router.push(`/cv-builder/${createdCv.id}`);
      }
    }
  };

  // Hàm update CV linh hoạt dựa trên mode
  const handleCVUpdate = (cvData: Partial<ICV>) => {
    if (mode === "update") {
      // Mode update: cập nhật currentCVUpdate
      handleUpdateCVUpdate(cvData);
    } else {
      // Mode create: cập nhật currentCVCreate
      handleUpdateCVCreate(cvData);
    }
  };

  return (
    <div className="flex flex-col gap-8">
      {/* Privacy & Tiêu đề */}
      {currentCV && (
        <div className="flex flex-col gap-4">
          {/* Privacy Toggle */}
          <div className="flex items-center justify-between rounded-lg border border-border p-4">
            <div className="space-y-0.5">
              <Label htmlFor="privacy-toggle" className="text-base">
                Chế độ riêng tư
              </Label>
              <p className="text-sm text-muted-foreground">
                {currentCV.privacy === EPrivacy.PUBLIC
                  ? "CV của bạn hiện công khai và có thể được tìm thấy bởi nhà tuyển dụng"
                  : "CV của bạn ở chế độ riêng tư, chỉ bạn mới có thể xem"}
              </p>
            </div>
            <Switch
              id="privacy-toggle"
              checked={currentCV.privacy === EPrivacy.PUBLIC}
              onCheckedChange={(checked) =>
                handleCVUpdate({
                  privacy: checked ? EPrivacy.PUBLIC : EPrivacy.PRIVATE,
                })
              }
            />
          </div>

          {/* Tiêu đề */}
          <div className="flex flex-col gap-2">
            <Label htmlFor="cv-title">Tiêu đề</Label>
            <Input
              id="cv-title"
              value={currentCV.title}
              onChange={(e) => handleCVUpdate({ title: e.target.value })}
              placeholder="e.g., Software Engineer CV"
              className="text-lg font-semibold"
            />
          </div>
        </div>
      )}

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
