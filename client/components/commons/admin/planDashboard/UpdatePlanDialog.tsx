import { UserCog } from "lucide-react";
import { AdminDialog } from "../AdminDialog";
import PlanForm from "./PlanForm";

interface UpdatePlanDialogProps {
  isOpen: boolean;
  onOpenChange: (open: boolean) => void;
  onChange: (field: keyof IPlan, value: string | string[]) => void;
  data: IPlan | null;
  onUpdated: () => void;
}

const UpdatePlanDialog = ({
  isOpen,
  onOpenChange,
  onChange,
  data,
  onUpdated,
}: UpdatePlanDialogProps) => {
  return (
    <AdminDialog<IPlan>
      isOpen={isOpen}
      onOpenChange={onOpenChange}
      title="Chỉnh sửa người dùng"
      description="Cập nhật thông tin quản trị viên"
      icon={UserCog}
      onSubmit={onUpdated}
      isCreateDialog={false}
      className="max-w-lg"
    >
      <PlanForm
        data={data as IPlan | null}
        onChange={(field, value) => onChange(field as keyof IPlan, value)}
      />
    </AdminDialog>
  );
};

export default UpdatePlanDialog;
