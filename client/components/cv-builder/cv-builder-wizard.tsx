"use client";

import { Button } from "@/components/ui/button";
import { Progress } from "@/components/ui/progress";
import { PersonalInfoStep } from "./steps/personal-info-step";
import { ExperienceStep } from "./steps/experience-step";
import { EducationStep } from "./steps/education-step";
import { SkillsStep } from "./steps/skills-step";
import { ReviewStep } from "./steps/review-step";
import { FileImport } from "./file-import";
import { ChevronLeft, ChevronRight, Save } from "lucide-react";
import { useRouter } from "next/navigation";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { useCVStore } from "@/stores/cvStore";

const steps = [
  { id: 0, title: "Personal Info", component: PersonalInfoStep },
  { id: 1, title: "Experience", component: ExperienceStep },
  { id: 2, title: "Education", component: EducationStep },
  { id: 3, title: "Skills", component: SkillsStep },
  { id: 4, title: "Review", component: ReviewStep },
];

export function CVBuilderWizard() {
  const router = useRouter();
  const { currentStep, handleSetCurrentStep, currentCV, createCV, handleUpdateCV } =
    useCVStore();
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

    createCV(currentCV);
  };

  return (
    <div className="flex flex-col gap-8">
      {/* CV Title */}
      {currentCV && (
        <div className="flex flex-col gap-2">
          <Label htmlFor="cv-title">CV Title</Label>
          <Input
            id="cv-title"
            value={currentCV.title}
            onChange={(e) => handleUpdateCV({ title: e.target.value })}
            placeholder="e.g., Software Engineer CV"
            className="text-lg font-semibold"
          />
        </div>
      )}

      {/* File Import */}
      {currentStep === 0 && (
        <div className="mb-4">
          <FileImport />
        </div>
      )}

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
          <Button variant="outline" onClick={handleSave}>
            <Save className="mr-2 h-4 w-4" />
            Save Draft
          </Button>

          {currentStep < steps.length - 1 ? (
            <Button onClick={handleNext}>
              Next
              <ChevronRight className="ml-2 h-4 w-4" />
            </Button>
          ) : (
            <Button onClick={handleSave}>
              <Save className="mr-2 h-4 w-4" />
              Save & Finish
            </Button>
          )}
        </div>
      </div>
    </div>
  );
}
