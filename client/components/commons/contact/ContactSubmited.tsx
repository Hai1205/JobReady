import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { CheckCircle } from "lucide-react";

interface ContactSubmitedProps {
  setIsSubmitted: (value: boolean) => void;
}

export default function ContactSubmited({ setIsSubmitted }: ContactSubmitedProps) {
  return (
    <div className="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center transition-colors">
      <Card className="max-w-md mx-4 bg-white dark:bg-gray-800 border-gray-200 dark:border-gray-700">
        <CardContent className="text-center pt-12 pb-8">
          <div className="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
            <CheckCircle className="w-8 h-8 text-green-600" />
          </div>
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white mb-2">
            Cảm ơn bạn đã liên hệ!
          </h2>
          <p className="text-gray-600 dark:text-gray-300 mb-6">
            Chúng tôi đã nhận được thông tin của bạn và sẽ liên hệ lại trong
            vòng 24 giờ.
          </p>
          <Button onClick={() => setIsSubmitted(false)}>
            Gửi thông tin khác
          </Button>
        </CardContent>
      </Card>
    </div>
  );
}
