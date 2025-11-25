"use client";

import { Check, Sparkles, ChevronDown, ChevronUp, Search } from "lucide-react";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";
import { useState, useMemo } from "react";
import { templates } from "./templates/templates";

interface TemplateSelectorProps {
  selectedTemplate: string;
  onTemplateChange: (template: string) => void;
}

export function TemplateSelector({
  selectedTemplate,
  onTemplateChange,
}: TemplateSelectorProps) {
  const [isExpanded, setIsExpanded] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const selectedTemp = templates.find((t) => t.id === selectedTemplate);

  const filteredTemplates = useMemo(() => {
    if (!searchQuery) return templates;
    return templates.filter(
      (template) =>
        template.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        template.description
          .toLowerCase()
          .includes(searchQuery.toLowerCase()) ||
        template.id.toLowerCase().includes(searchQuery.toLowerCase())
    );
  }, [searchQuery]);

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between">
        <div>
          <Label className="text-base font-semibold">Template CV</Label>
          <p className="text-sm text-muted-foreground mt-1">
            {isExpanded
              ? "Chọn template phù hợp với phong cách của bạn"
              : selectedTemp?.name || "Chọn template"}
          </p>
        </div>
        <Button
          type="button"
          variant="ghost"
          size="sm"
          onClick={() => setIsExpanded(!isExpanded)}
          className="gap-2"
        >
          {isExpanded ? (
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

      {/* Preview template hiện tại - always visible */}
      {!isExpanded && selectedTemp && (
        <div className="flex items-center gap-3 p-3 rounded-lg border-2 border-primary bg-primary/5">
          <div className="w-12 h-16 rounded bg-gradient-to-br from-primary/20 to-primary/10 border border-primary/30 flex items-center justify-center font-bold text-primary text-xl">
            {selectedTemp.preview}
          </div>
          <div className="flex-1">
            <div className="flex items-center gap-2">
              <p className="text-sm font-semibold">{selectedTemp.name}</p>
              {selectedTemp.isPremium && (
                <Badge variant="secondary" className="text-xs gap-1">
                  <Sparkles className="w-3 h-3" />
                  Premium
                </Badge>
              )}
            </div>
            <p className="text-xs text-muted-foreground mt-0.5">
              {selectedTemp.description}
            </p>
          </div>
        </div>
      )}

      {/* Template grid - shown when expanded */}
      {isExpanded && (
        <div className="space-y-3 animate-in fade-in-50 slide-in-from-top-2 duration-200">
          {/* Search Input */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              type="text"
              placeholder="Tìm kiếm template..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-9"
            />
          </div>

          {/* Scrollable grid container */}
          <div className="max-h-[420px] overflow-y-auto pr-1">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              {filteredTemplates.map((template) => (
                <button
                  key={template.id}
                  onClick={() => onTemplateChange(template.id)}
                  disabled={template.isPremium}
                  className={cn(
                    "relative flex flex-col p-4 rounded-lg border-2 transition-all text-left",
                    "hover:shadow-md disabled:opacity-50 disabled:cursor-not-allowed",
                    selectedTemplate === template.id
                      ? "border-primary shadow-md bg-primary/5"
                      : "border-border hover:border-muted-foreground"
                  )}
                >
                  {/* Premium badge */}
                  {template.isPremium && (
                    <Badge
                      variant="secondary"
                      className="absolute top-2 right-2 gap-1"
                    >
                      <Sparkles className="w-3 h-3" />
                      Premium
                    </Badge>
                  )}

                  {/* Selected indicator */}
                  {selectedTemplate === template.id && (
                    <div className="absolute top-2 left-2 w-6 h-6 rounded-full bg-primary flex items-center justify-center">
                      <Check
                        className="w-4 h-4 text-primary-foreground"
                        strokeWidth={3}
                      />
                    </div>
                  )}

                  {/* Template preview mockup */}
                  <div
                    className={cn(
                      "w-full aspect-[3/4] rounded-md mb-3 flex items-center justify-center text-4xl font-bold",
                      "bg-gradient-to-br from-muted to-muted/50 border border-border"
                    )}
                  >
                    {template.preview}
                  </div>

                  {/* Template info */}
                  <div className="space-y-1">
                    <h3 className="font-semibold text-base">{template.name}</h3>
                    <p className="text-xs text-muted-foreground leading-relaxed">
                      {template.description}
                    </p>
                  </div>
                </button>
              ))}
            </div>

            {/* No results message */}
            {filteredTemplates.length === 0 && (
              <div className="text-center py-8 text-muted-foreground text-sm">
                Không tìm thấy template phù hợp
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
