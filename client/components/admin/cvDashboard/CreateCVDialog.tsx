import { Loader2, Save } from "lucide-react";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Button } from "@/components/ui/button";

interface CreateCVDialogProps {
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
  onChange: (field: keyof ICV, value: string) => void;
  onCVCreated: () => void;
  data: ICV | null;
  isLoading: boolean;
}

const CreateCVDialog = ({
  isOpen,
  onOpenChange,
  onChange,
  onCVCreated,
  data,
  isLoading,
}: CreateCVDialogProps) => {
  return (
    <Dialog open={isOpen} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[525px] bg-white dark:bg-gray-800">
        <DialogHeader>
          <DialogTitle className="text-primary">Tạo mới</DialogTitle>

          <DialogDescription className="text-primary-200">
            Tạo một câu hỏi thường gặp mới.
          </DialogDescription>
        </DialogHeader>

        {/* <div className="grid gap-4 py-4">
          <div className="grid gap-4">
            <div className="grid gap-2">
              <Label htmlFor="create-question" className="text-primary">
                Câu hỏi
              </Label>

              <Input
                id="create-question"
                value={data?.question || ""}
                onChange={(e) => onChange("question", e.target.value)}
              />
            </div>
          </div>

          <div className="grid gap-4">
            <div className="grid gap-2">
              <Label htmlFor="create-answer" className="text-primary">
                Câu trả lời
              </Label>

              <Input
                id="create-answer"
                value={data?.answer || ""}
                onChange={(e) => onChange("answer", e.target.value)}
              />
            </div>
          </div>

          <div className="grid gap-2 mt-3">
            <Label htmlFor="create-category" className="text-primary">
              Danh mục
            </Label>

            <Select
              value={data?.category || CVCategory[0].value}
              onValueChange={(value: string) => onChange("category", value)}
            >
              <SelectTrigger id="create-category">
                <SelectValue placeholder="Select category" />
              </SelectTrigger>

              <SelectContent>
                {CVCategory.map((item: { value: string; label: string }) => (
                  <SelectItem key={item.value} value={item.value}>
                    {item.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>

          <div className="grid gap-2 mt-3">
            <Label htmlFor="create-status" className="text-primary">
              Trạng thái
            </Label>

            <Select
              value={data?.status || EStatus.ACTIVE}
              onValueChange={(value: string) => onChange("status", value)}
            >
              <SelectTrigger id="create-status">
                <SelectValue placeholder="Select status" />
              </SelectTrigger>

              <SelectContent>
                {CVStatus.map((item: { value: string; label: string }) => (
                  <SelectItem key={item.value} value={item.value}>
                    {item.label}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
        </div> */}

        <DialogFooter>
          <Button
            variant="outline"
            onClick={() => {
              onOpenChange(false);
            }}
            className="bg-gray-200 border-gray-300 text-gray-700 hover:bg-red-200 hover:text-red-600 hover:border-red-200 dark:bg-transparent dark:border-gray-700 dark:text-white dark:hover:bg-red-900 dark:hover:text-white"
          >
            Hủy
          </Button>

          <Button type="submit" disabled={isLoading} onClick={onCVCreated}>
            {isLoading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Đang tạo...
              </>
            ) : (
              <>
                <Save className="h-4 w-4" />
                Tạo
              </>
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default CreateCVDialog;
