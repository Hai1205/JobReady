"use client";

import type React from "react";

import { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Plus, X } from "lucide-react";
import { useCVStore } from "@/stores/cvStore";

export function SkillsStep() {
  const { currentCV, handleUpdateCV } = useCVStore();
  const [skillInput, setSkillInput] = useState("");

  if (!currentCV) return null;

  const addSkill = () => {
    if (!skillInput.trim()) return;

    handleUpdateCV({
      skills: [...currentCV.skills, skillInput.trim()],
    });
    setSkillInput("");
  };

  const removeSkill = (index: number) => {
    handleUpdateCV({
      skills: currentCV.skills.filter((_, i) => i !== index),
    });
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      e.preventDefault();
      addSkill();
    }
  };

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h2 className="text-2xl font-bold">Skills</h2>
        <p className="text-muted-foreground">
          Add your technical and soft skills
        </p>
      </div>

      <div className="flex gap-2">
        <Input
          value={skillInput}
          onChange={(e) => setSkillInput(e.target.value)}
          onKeyDown={handleKeyPress}
          placeholder="Type a skill and press Enter or click Add"
        />

        <Button
          onClick={addSkill}
          size="sm"
          className="bg-primary text-primary-foreground shadow-md hover:shadow-lg transform hover:-translate-y-[1px] transition-all"
        >
          <Plus className="mr-2 h-4 w-4" />
          Add
        </Button>
      </div>

      {currentCV.skills.length > 0 ? (
        <div className="flex flex-wrap gap-2">
          {currentCV.skills.map((skill, index) => (
            <Badge
              key={index}
              variant="secondary"
              className="gap-2 px-3 py-1.5 text-sm"
            >
              {skill}
              <button
                onClick={() => removeSkill(index)}
                className="hover:text-destructive"
              >
                <X className="h-3 w-3" />
              </button>
            </Badge>
          ))}
        </div>
      ) : (
        <div className="rounded-lg border border-dashed border-border p-8 text-center">
          <p className="text-muted-foreground">
            No skills added yet. Start typing to add your skills.
          </p>
        </div>
      )}

      <div className="rounded-lg bg-muted p-4">
        <h3 className="mb-2 font-semibold">Suggestions:</h3>
        <div className="flex flex-wrap gap-2">
          {[
            "JavaScript",
            "React",
            "Node.js",
            "Python",
            "Communication",
            "Leadership",
            "Problem Solving",
          ].map((suggestion) => (
            <Button
              key={suggestion}
              size="sm"
              className="bg-primary/95 text-primary-foreground shadow-sm hover:shadow-md focus:ring-2 focus:ring-primary/40 transition"
              onClick={() => {
                if (!currentCV.skills.includes(suggestion)) {
                  handleUpdateCV({
                    skills: [...currentCV.skills, suggestion],
                  });
                }
              }}
            >
              <Plus className="mr-1 h-3 w-3" />
              {suggestion}
            </Button>
          ))}
        </div>
      </div>
    </div>
  );
}
