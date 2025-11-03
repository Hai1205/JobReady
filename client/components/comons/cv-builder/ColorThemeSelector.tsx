"use client";

import { Check, ChevronDown, ChevronUp, Search } from "lucide-react";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { cn } from "@/lib/utils";
import { useState, useMemo } from "react";

interface ColorTheme {
  name: string;
  value: string;
  label: string;
}

const colorThemes: ColorTheme[] = [
  { name: "blue", value: "#3498db", label: "Professional Blue" },
  { name: "emerald", value: "#10b981", label: "Fresh Emerald" },
  { name: "violet", value: "#8b5cf6", label: "Creative Violet" },
  { name: "rose", value: "#f43f5e", label: "Bold Rose" },
  { name: "amber", value: "#f59e0b", label: "Warm Amber" },
  { name: "cyan", value: "#06b6d4", label: "Modern Cyan" },
  { name: "indigo", value: "#6366f1", label: "Deep Indigo" },
  { name: "slate", value: "#64748b", label: "Classic Slate" },
  { name: "teal", value: "#14b8a6", label: "Tech Teal" },
  { name: "fuchsia", value: "#d946ef", label: "Vibrant Fuchsia" },
];

interface ColorThemeSelectorProps {
  selectedColor: string;
  onColorChange: (color: string) => void;
}

export function ColorThemeSelector({
  selectedColor,
  onColorChange,
}: ColorThemeSelectorProps) {
  const [isExpanded, setIsExpanded] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const selectedTheme = colorThemes.find((t) => t.value === selectedColor);

  // Filter colors based on search
  const filteredColors = useMemo(() => {
    if (!searchQuery) return colorThemes;
    return colorThemes.filter(
      (theme) =>
        theme.label.toLowerCase().includes(searchQuery.toLowerCase()) ||
        theme.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        theme.value.toLowerCase().includes(searchQuery.toLowerCase())
    );
  }, [searchQuery]);

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between">
        <div>
          <Label className="text-base font-semibold">Màu chủ đạo</Label>
          <p className="text-sm text-muted-foreground mt-1">
            {isExpanded
              ? "Chọn màu sẽ được áp dụng cho CV của bạn"
              : selectedTheme?.label || "Chọn màu"}
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

      {/* Preview màu hiện tại - always visible */}
      {!isExpanded && selectedTheme && (
        <div className="flex items-center gap-3 p-3 rounded-lg border border-border bg-muted/30">
          <div
            className="w-10 h-10 rounded-full shadow-sm ring-2 ring-primary ring-offset-2 ring-offset-background"
            style={{ backgroundColor: selectedColor }}
          />
          <div>
            <p className="text-sm font-medium">{selectedTheme.label}</p>
            <p className="text-xs text-muted-foreground font-mono">
              {selectedColor}
            </p>
          </div>
        </div>
      )}

      {/* Color grid - shown when expanded */}
      {isExpanded && (
        <div className="space-y-3">
          {/* Search box */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              type="text"
              placeholder="Tìm kiếm màu..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-9"
            />
          </div>

          {/* Color grid with fixed 2 rows + scroll */}
          <div className="max-h-[280px] overflow-y-auto pr-2 scrollbar-thin scrollbar-thumb-border scrollbar-track-transparent">
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-3 animate-in fade-in-50 slide-in-from-top-2 duration-200">
              {filteredColors.map((theme) => (
                <button
                  key={theme.name}
                  onClick={() => onColorChange(theme.value)}
                  className={cn(
                    "relative flex flex-col items-center gap-2 p-3 rounded-lg border-2 transition-all hover:shadow-md",
                    selectedColor === theme.value
                      ? "border-primary shadow-md"
                      : "border-border hover:border-muted-foreground"
                  )}
                >
                  <div
                    className={cn(
                      "w-12 h-12 rounded-full shadow-sm ring-2 ring-offset-2 ring-offset-background",
                      selectedColor === theme.value && "ring-primary"
                    )}
                    style={{ backgroundColor: theme.value }}
                  >
                    {selectedColor === theme.value && (
                      <div className="w-full h-full flex items-center justify-center">
                        <Check className="w-6 h-6 text-white" strokeWidth={3} />
                      </div>
                    )}
                  </div>
                  <span className="text-xs font-medium text-center leading-tight">
                    {theme.label}
                  </span>
                </button>
              ))}
            </div>

            {/* No results message */}
            {filteredColors.length === 0 && (
              <div className="text-center py-8 text-muted-foreground text-sm">
                Không tìm thấy màu phù hợp
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
