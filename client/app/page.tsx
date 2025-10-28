import HeroSection from "@/components/comons/home/HeroSection";
import FeaturesSection from "@/components/comons/home/FeaturesSection";

export default function HomePage() {
  return (
    <div className="flex flex-col">
      <HeroSection />
      <FeaturesSection />
    </div>
  );
}
