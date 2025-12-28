"use client";

import { Check, ChevronDown, ChevronUp, Search, Type } from "lucide-react";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { ScrollArea } from "@/components/ui/scroll-area";
import { cn } from "@/lib/utils";
import { useState, useMemo } from "react";

interface Font {
  name: string;
  value: string;
  label: string;
  category: "serif" | "sans-serif" | "mono";
}

const fonts: Font[] = [
  {
    name: "inter",
    value: "Inter, sans-serif",
    label: "Inter",
    category: "sans-serif",
  },
  {
    name: "roboto",
    value: "Roboto, sans-serif",
    label: "Roboto",
    category: "sans-serif",
  },
  {
    name: "open-sans",
    value: "'Open Sans', sans-serif",
    label: "Open Sans",
    category: "sans-serif",
  },
  {
    name: "lato",
    value: "Lato, sans-serif",
    label: "Lato",
    category: "sans-serif",
  },
  {
    name: "montserrat",
    value: "Montserrat, sans-serif",
    label: "Montserrat",
    category: "sans-serif",
  },
  {
    name: "poppins",
    value: "Poppins, sans-serif",
    label: "Poppins",
    category: "sans-serif",
  },
  {
    name: "raleway",
    value: "Raleway, sans-serif",
    label: "Raleway",
    category: "sans-serif",
  },
  {
    name: "source-sans",
    value: "'Source Sans Pro', sans-serif",
    label: "Source Sans Pro",
    category: "sans-serif",
  },
  {
    name: "merriweather",
    value: "Merriweather, serif",
    label: "Merriweather",
    category: "serif",
  },
  {
    name: "playfair",
    value: "'Playfair Display', serif",
    label: "Playfair Display",
    category: "serif",
  },
  { name: "lora", value: "Lora, serif", label: "Lora", category: "serif" },
  {
    name: "pt-serif",
    value: "'PT Serif', serif",
    label: "PT Serif",
    category: "serif",
  },
  {
    name: "crimson",
    value: "'Crimson Text', serif",
    label: "Crimson Text",
    category: "serif",
  },
  {
    name: "roboto-mono",
    value: "'Roboto Mono', monospace",
    label: "Roboto Mono",
    category: "mono",
  },
  {
    name: "fira-code",
    value: "'Fira Code', monospace",
    label: "Fira Code",
    category: "mono",
  },
];

interface FontSelectorProps {
  selectedFont: string;
  onFontChange: (font: string) => void;
  isExpanded?: boolean;
  onExpandChange?: (expanded: boolean) => void;
}

export function FontSelector({
  selectedFont,
  onFontChange,
  isExpanded: controlledIsExpanded,
  onExpandChange,
}: FontSelectorProps) {
  const [internalIsExpanded, setInternalIsExpanded] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const selectedFontObj = fonts.find((f) => f.value === selectedFont);

  // Sử dụng controlled state nếu có, ngược lại dùng internal state
  const isExpanded = controlledIsExpanded ?? internalIsExpanded;
  const setIsExpanded = (value: boolean) => {
    if (onExpandChange) {
      onExpandChange(value);
    } else {
      setInternalIsExpanded(value);
    }
  };

  // Filter fonts based on search
  const filteredFonts = useMemo(() => {
    if (!searchQuery) return fonts;
    return fonts.filter(
      (font) =>
        font.label.toLowerCase().includes(searchQuery.toLowerCase()) ||
        font.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        font.category.toLowerCase().includes(searchQuery.toLowerCase())
    );
  }, [searchQuery]);

  const getCategoryLabel = (category: string) => {
    switch (category) {
      case "sans-serif":
        return "Sans Serif";
      case "serif":
        return "Serif";
      case "mono":
        return "Monospace";
      default:
        return category;
    }
  };

  return (
    <div className="space-y-3">
      <div className="flex items-center justify-between">
        <div>
          <Label className="text-base font-semibold">Font chữ</Label>
          <p className="text-sm text-muted-foreground mt-1">
            {isExpanded
              ? "Chọn font chữ sẽ được áp dụng cho CV của bạn"
              : selectedFontObj?.label || "Chọn font"}
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

      {/* Preview font hiện tại - always visible */}
      {!isExpanded && selectedFontObj && (
        <div className="flex items-center gap-3 p-3 rounded-lg border border-border bg-muted/30">
          <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
            <Type className="w-5 h-5 text-primary" />
          </div>
          <div>
            <p
              className="text-sm font-medium"
              style={{ fontFamily: selectedFont }}
            >
              {selectedFontObj.label}
            </p>
            <p className="text-xs text-muted-foreground">
              {getCategoryLabel(selectedFontObj.category)}
            </p>
          </div>
        </div>
      )}

      {/* Font grid - shown when expanded */}
      {isExpanded && (
        <div className="space-y-3">
          {/* Search box */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input
              type="text"
              placeholder="Tìm kiếm font..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-9"
            />
          </div>

          {/* Font grid with scroll */}
          <ScrollArea className="h-[280px] pr-2">
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-5 gap-3 animate-in fade-in-50 slide-in-from-top-2 duration-200">
              {filteredFonts.map((font) => (
                <button
                  key={font.name}
                  onClick={() => onFontChange(font.value)}
                  className={cn(
                    "relative flex flex-col items-center gap-2 p-3 rounded-lg border-2 transition-all hover:shadow-md",
                    selectedFont === font.value
                      ? "border-primary shadow-md"
                      : "border-border hover:border-muted-foreground"
                  )}
                >
                  <div className="w-12 h-12 rounded-md bg-muted flex items-center justify-center relative">
                    <span
                      className="text-lg font-semibold"
                      style={{ fontFamily: font.value }}
                    >
                      Aa
                    </span>
                    {selectedFont === font.value && (
                      <div className="absolute -top-1 -right-1 w-5 h-5 rounded-full bg-primary flex items-center justify-center">
                        <Check
                          className="w-3 h-3 text-primary-foreground"
                          strokeWidth={3}
                        />
                      </div>
                    )}
                  </div>
                  <span className="text-xs font-medium text-center leading-tight">
                    {font.label}
                  </span>
                </button>
              ))}
            </div>

            {/* No results message */}
            {filteredFonts.length === 0 && (
              <div className="text-center py-8 text-muted-foreground text-sm">
                Không tìm thấy font phù hợp
              </div>
            )}
          </ScrollArea>
        </div>
      )}
    </div>
  );
}
