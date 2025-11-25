import HeroSection from "@/components/commons/home/HeroSection";
import FeaturesSection from "@/components/commons/home/FeaturesSection";

export default function HomePage() {
  return (
    <div className="flex flex-col">
      <HeroSection />
      <FeaturesSection />
    </div>
  );
}
