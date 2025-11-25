"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/authStore";
import { useCVStore } from "@/stores/cvStore";
import PageHeader from "@/components/commons/my-cvs/PageHeader";
import UserCVsSkeleton from "@/components/commons/layout/UserCVsSkeleton";
import UserCVsSection from "@/components/commons/my-cvs/UserCVsSection";
import TemplateCVsSection from "@/components/commons/my-cvs/TemplateCVsSection";
import ConfirmationDialog from "../layout/ConfirmationDialog";
import { toast } from "react-toastify";

export default function MyCVsPageClient() {
  const { userAuth } = useAuthStore();
  const {
    createCV,
    deleteCV,
    duplicateCV,
    fetchUserCVsInBackground,
    fetchAllCVsInBackground,
    handleGeneratePDF,
    handleSetCurrentCV,
    userCVs,
    CVsTable,
    isLoadingUserCVs,
    importCV,
  } = useCVStore();

  const router = useRouter();
  const [templateCVs, setTemplateCVs] = useState<ICV[]>([]);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [cvToDelete, setCvToDelete] = useState<string | null>(null);

  useEffect(() => {
    if (userAuth) {
      fetchUserCVsInBackground(userAuth.id);
    }
    fetchAllCVsInBackground();
  }, [userAuth]);

  useEffect(() => {
    // Filter public CVs from CVsTable
    const publicCvs = CVsTable.filter((cv) => cv.isVisibility === true);
    setTemplateCVs(publicCvs);
  }, [CVsTable]);

  const handleCreate = async () => {
    router.push("/cv-builder");
    await createCV(userAuth?.id || "");
  };

  const handleImport = async (file: File | null) => {
    try {
      const res = await importCV(userAuth?.id || "", file);
      console.log("Import CV result:", res);

      if (res.data && res.data.success) {
        console.log("Navigating to cv-builder...");
        toast.success("Import CV thành công!");
        router.push("/cv-builder");
        return true;
      }

      console.log("Import failed or no CV data");
      return false;
    } catch (error) {
      console.error("Import error:", error);
      return false;
    }
  };

  const handleEdit = (cv: ICV) => {
    handleSetCurrentCV(cv);
    router.push(`/cv-builder`);
  };

  const handleDeleteClick = (cvId: string) => {
    setCvToDelete(cvId);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (!cvToDelete) return;

    await deleteCV(cvToDelete);
    toast.success("Xóa CV thành công!");
    setDeleteDialogOpen(false);
    setCvToDelete(null);
  };

  const handleDuplicate = (cvId: string) => {
    duplicateCV(cvId);
    router.push("/cv-builder");
  };

  const deleteDialogDescription =
    "Hành động này không thể hoàn tác. Điều này sẽ xóa vĩnh viễn CV của bạn và loại bỏ nó khỏi máy chủ của chúng tôi.";

  useEffect(() => {
    if (!userAuth) {
      router.push("/auth/login");
    }
  }, [userAuth]);

  // Show loading only when there's no cached data
  if (isLoadingUserCVs) {
    return <UserCVsSkeleton />;
  }

  return (
    <div className="min-h-screen flex items-center justify-center py-12">
      <div className="container max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col gap-8">
          <PageHeader onCreateNew={handleCreate} onImport={handleImport} />

          <UserCVsSection
            userCVs={userCVs}
            onCreateNew={handleCreate}
            onUpdate={handleEdit}
            onDuplicate={handleDuplicate}
            onDelete={handleDeleteClick}
          />

          <TemplateCVsSection
            templateCVs={templateCVs}
            handleDuplicate={handleDuplicate}
            onDownload={handleGeneratePDF}
          />
        </div>
      </div>

      <ConfirmationDialog
        open={deleteDialogOpen}
        description={deleteDialogDescription}
        onOpenChange={setDeleteDialogOpen}
        onConfirm={handleDeleteConfirm}
        isDestructive={true}
      />
    </div>
  );
}
