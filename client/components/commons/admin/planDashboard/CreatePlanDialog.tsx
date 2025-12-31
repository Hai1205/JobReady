import { UserCog } from "lucide-react";
import { AdminDialog } from "../AdminDialog";
import PlanForm from "./PlanForm";

interface CreatePlanDialogProps {
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
  onChange: (field: keyof IPlan, value: string | string[]) => void;
  data: IPlan | null;
  onCreated: () => void;
}

const CreatePlanDialog = ({
  isOpen,
  onOpenChange,
  onChange,
  data,
  onCreated,
}: CreatePlanDialogProps) => {
  return (
    <AdminDialog<IPlan>
      isOpen={isOpen}
      onOpenChange={onOpenChange}
      title="Tạo gói mới"
      description="Tạo thông tin gói"
      icon={UserCog}
      onSubmit={onCreated}
      isCreateDialog={true}
      className="max-w-lg"
    >
      <PlanForm
        data={data as IPlan | null}
        onChange={(field, value) => onChange(field as keyof IPlan, value)}
      />
    </AdminDialog>
  );
};

export default CreatePlanDialog;
