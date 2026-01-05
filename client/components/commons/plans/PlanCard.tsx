"use client";

import { Check } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";
import { useRouter } from "next/navigation";

interface PlanCardProps {
  plan: IPlan;
  isCurrentPlan: boolean;
  onSelect: (planId: string) => void;
}

export default function PlanCard({
  plan,
  isCurrentPlan,
  onSelect,
}: PlanCardProps) {
  const router = useRouter();
  const isFree = plan.type.toLowerCase() === "free";

  const handleSelectPlan = () => {
    if (isFree || isCurrentPlan) return;

    onSelect(plan.id);

    // Store payment info in localStorage
    localStorage.setItem(
      "paymentInfo",
      JSON.stringify({
        planId: plan.id,
        planTitle: plan.title,
        amount: plan.price,
      })
    );

    // Redirect to payment page
    router.push("/payment");
  };

  return (
    <Card
      className={cn(
        "relative flex flex-col transition-all duration-300 hover:shadow-2xl hover:-translate-y-1",
        plan.isPopular && "border-primary shadow-xl scale-105 md:scale-110",
        isCurrentPlan && "ring-2 ring-primary"
      )}
    >
      {/* Recommended Badge */}
      {plan.isRecommended && (
        <div className="absolute -top-4 left-1/2 -translate-x-1/2">
          <Badge className="bg-linear-to-r from-primary to-primary/80 text-white px-4 py-1 text-sm font-semibold shadow-lg">
            Recommended
          </Badge>
        </div>
      )}

      <CardHeader className="text-center pb-4">
        <div className="flex items-center justify-center gap-2 mb-2">
          <CardTitle className="text-2xl font-bold">{plan.title}</CardTitle>
          {/* Most Popular Badge - Inside Card */}
          {plan.isPopular && (
            <Badge
              variant="secondary"
              className="bg-yellow-500/10 text-yellow-600 dark:text-yellow-400 border-yellow-500/20 px-2 py-0.5 text-xs"
            >
              Most Popular
            </Badge>
          )}
        </div>

        {/* Current Plan Indicator */}
        {isCurrentPlan && (
          <Badge className="mx-auto mb-2 bg-green-500/10 text-green-600 dark:text-green-400 border-green-500/20">
            Gói hiện tại
          </Badge>
        )}

        <div className="mb-4">
          <div className="flex items-baseline justify-center gap-1">
            <span className="text-4xl font-bold">
              {plan.currency}
              {plan.price}
            </span>
            <span className="text-muted-foreground">/{plan.period}</span>
          </div>
        </div>
        <CardDescription className="text-base">
          {plan.description}
        </CardDescription>
      </CardHeader>

      <CardContent className="flex-1">
        <ul className="space-y-3">
          {plan.features.map((feature, index) => (
            <li key={index} className="flex items-start gap-3">
              <div className="rounded-full bg-primary/10 p-1 mt-0.5">
                <Check className="h-4 w-4 text-primary" />
              </div>
              <span
                className={cn(
                  "text-sm",
                  feature.toLowerCase().includes("everything") &&
                    "font-semibold"
                )}
              >
                {feature}
              </span>
            </li>
          ))}
        </ul>
      </CardContent>

      <CardFooter>
        {!isFree ? (
          <Button
            variant={plan.buttonVariant}
            className={cn(
              "w-full font-semibold",
              plan.isPopular &&
                "bg-linear-to-r from-primary to-primary/80 hover:from-primary/90 hover:to-primary/70",
              isCurrentPlan && "opacity-50 cursor-not-allowed"
            )}
            size="lg"
            onClick={handleSelectPlan}
            disabled={isCurrentPlan}
          >
            {isCurrentPlan ? "Đang sử dụng" : "Đăng ký gói"}
          </Button>
        ) : null}
      </CardFooter>
    </Card>
  );
}
