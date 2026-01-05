"use client";

import { useState } from "react";
import { Check, Crown, Sparkles, Zap, ArrowRight } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { useAuthStore } from "@/stores/authStore";
import { mockPlans } from "@/services/mockData";
import { useRouter } from "next/navigation";
import { toast } from "react-toastify";

export default function PlanManagementTab() {
  const { userAuth } = useAuthStore();
  const router = useRouter();

  // Find current plan based on user's planType
  const currentPlan = mockPlans.find(
    (plan) => plan.type.toLowerCase() === userAuth?.planType?.toLowerCase()
  );

  const handleViewAllPlans = () => {
    router.push("/plans");
  };

  const getPlanIcon = (planType: string) => {
    switch (planType.toLowerCase()) {
      case "free":
        return <Sparkles className="h-5 w-5" />;
      case "pro":
        return <Zap className="h-5 w-5" />;
      case "ultra":
        return <Crown className="h-5 w-5" />;
      default:
        return <Sparkles className="h-5 w-5" />;
    }
  };

  const formatDate = (dateString?: string) => {
    if (!dateString) return "N/A";
    return new Date(dateString).toLocaleDateString("vi-VN", {
      day: "2-digit",
      month: "2-digit",
      year: "numeric",
    });
  };

  return (
    <div className="space-y-6">
      {/* Current Plan Card */}
      <div className="relative overflow-hidden rounded-xl border-2 border-primary/50 bg-linear-to-br from-primary/5 via-primary/10 to-secondary/10 p-6">
        <div className="absolute top-0 right-0 w-32 h-32 bg-primary/10 rounded-full blur-3xl" />
        <div className="absolute bottom-0 left-0 w-32 h-32 bg-secondary/10 rounded-full blur-3xl" />

        <div className="relative">
          <div className="flex items-start justify-between mb-4">
            <div className="flex items-center gap-3">
              <div className="p-3 rounded-xl bg-primary/10 text-primary">
                {getPlanIcon(currentPlan?.type || "free")}
              </div>
              <div>
                <div className="flex items-center gap-2">
                  <h3 className="text-xl font-bold">Plan Hiện Tại</h3>
                  {currentPlan?.isRecommended && (
                    <Badge className="bg-linear-to-r from-primary to-primary/80 text-white">
                      Recommended
                    </Badge>
                  )}
                </div>
                <p className="text-sm text-muted-foreground">
                  Bạn đang sử dụng plan{" "}
                  <span className="font-semibold text-foreground">
                    {currentPlan?.title || "Free"}
                  </span>
                </p>
              </div>
            </div>
            <div className="text-right">
              <div className="text-3xl font-bold text-primary">
                {currentPlan?.currency}
                {currentPlan?.price || 0}
              </div>
              <p className="text-sm text-muted-foreground">
                /{currentPlan?.period || "forever"}
              </p>
              {userAuth?.planExpiration && (
                <p className="text-xs text-muted-foreground mt-1">
                  Hết hạn: {formatDate(userAuth.planExpiration)}
                </p>
              )}
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-3 mt-6">
            {currentPlan?.features.slice(0, 4).map((feature, index) => (
              <div key={index} className="flex items-start gap-2 text-sm">
                <Check className="h-4 w-4 text-primary mt-0.5 shrink-0" />
                <span>{feature}</span>
              </div>
            ))}
          </div>

          {currentPlan && currentPlan.features.length > 4 && (
            <p className="text-sm text-muted-foreground mt-3">
              + {currentPlan.features.length - 4} tính năng khác
            </p>
          )}

          {/* Action Button */}
          <div className="mt-6">
            <Button onClick={handleViewAllPlans} className="w-full gap-2">
              Xem Tất Cả Các Gói
              <ArrowRight className="h-4 w-4" />
            </Button>
          </div>
        </div>
      </div>

      {/* Upgrade Options */}
      {/* <div>
        <div className="flex items-center justify-between mb-4">
          <div>
            <h3 className="text-lg font-semibold">Các Plan Khác</h3>
            <p className="text-sm text-muted-foreground">
              Nâng cấp hoặc thay đổi plan để phù hợp với nhu cầu
            </p>
          </div>
          <Button
            variant="outline"
            size="sm"
            onClick={handleViewAllPlans}
            className="gap-2"
          >
            Xem Tất Cả
            <ArrowRight className="h-4 w-4" />
          </Button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {mockPlans
            .filter((plan) => plan.id !== currentPlan?.id)
            .map((plan) => (
              <div
                key={plan.id}
                className="relative group rounded-lg border border-border/50 hover:border-primary/50 bg-card p-5 transition-all duration-300 hover:shadow-lg"
              >
                {plan.isPopular && (
                  <Badge className="absolute -top-2 -right-2 bg-yellow-500/10 text-yellow-600 dark:text-yellow-400 border-yellow-500/20">
                    Popular
                  </Badge>
                )}

                <div className="flex items-center gap-3 mb-3">
                  <div className="p-2 rounded-lg bg-primary/5 text-primary">
                    {getPlanIcon(plan.type)}
                  </div>
                  <div>
                    <h4 className="font-semibold">{plan.name}</h4>
                    <p className="text-sm text-muted-foreground">
                      {plan.currency}
                      {plan.price}/{plan.period}
                    </p>
                  </div>
                </div>

                <p className="text-sm text-muted-foreground mb-4 line-clamp-2">
                  {plan.description}
                </p>

                <div className="space-y-2 mb-4">
                  {plan.features.slice(0, 3).map((feature, index) => (
                    <div key={index} className="flex items-start gap-2 text-xs">
                      <Check className="h-3 w-3 text-primary mt-0.5 shrink-0" />
                      <span className="line-clamp-1">{feature}</span>
                    </div>
                  ))}
                  {plan.features.length > 3 && (
                    <p className="text-xs text-muted-foreground pl-5">
                      + {plan.features.length - 3} more...
                    </p>
                  )}
                </div>

                <Button
                  variant={plan.isRecommended ? "default" : "outline"}
                  size="sm"
                  className="w-full"
                  onClick={() => handleUpgrade(plan)}
                  disabled={isChanging}
                >
                  {plan.price > (currentPlan?.price || 0)
                    ? "Nâng Cấp"
                    : "Chuyển Plan"}
                </Button>
              </div>
            ))}
        </div>
      </div> */}

      {/* Benefits Section */}
      <div className="rounded-lg border border-border/50 bg-muted/30 p-5">
        <h4 className="font-semibold mb-3 flex items-center gap-2">
          <Sparkles className="h-4 w-4 text-primary" />
          Lợi Ích Khi Nâng Cấp
        </h4>
        <ul className="space-y-2 text-sm text-muted-foreground">
          <li className="flex items-start gap-2">
            <Check className="h-4 w-4 text-primary mt-0.5 shrink-0" />
            <span>Truy cập không giới hạn vào các mẫu CV chuyên nghiệp</span>
          </li>
          <li className="flex items-start gap-2">
            <Check className="h-4 w-4 text-primary mt-0.5 shrink-0" />
            <span>AI suggestions nâng cao giúp tối ưu hóa CV</span>
          </li>
          <li className="flex items-start gap-2">
            <Check className="h-4 w-4 text-primary mt-0.5 shrink-0" />
            <span>Hỗ trợ ưu tiên từ đội ngũ chăm sóc khách hàng</span>
          </li>
          <li className="flex items-start gap-2">
            <Check className="h-4 w-4 text-primary mt-0.5 shrink-0" />
            <span>Hủy bỏ bất cứ lúc nào, không ràng buộc dài hạn</span>
          </li>
        </ul>
      </div>
    </div>
  );
}
