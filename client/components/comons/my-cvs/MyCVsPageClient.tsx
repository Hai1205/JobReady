"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/authStore";
import { useCVStore } from "@/stores/cvStore";
import PageHeader from "@/components/comons/my-cvs/PageHeader";
import UserCVsSkeleton from "@/components/comons/layout/UserCVsSkeleton";
import UserCVsSection from "@/components/comons/my-cvs/UserCVsSection";
import TemplateCVsSection from "@/components/comons/my-cvs/TemplateCVsSection";
import ConfirmationDialog from "../layout/ConfirmationDialog";
import { useAIStore } from "@/stores/aiStore";

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
  } = useCVStore();

  const { importCV } = useAIStore();

  const router = useRouter();
  const [templateCVs, setTemplateCVs] = useState<ICV[]>([]);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [cvToDelete, setCvToDelete] = useState<string | null>(null);

  useEffect(() => {
    const loadData = async () => {
      if (userAuth) {
        await fetchUserCVsInBackground(userAuth.id);
      }
      await fetchAllCVsInBackground();
    };
    loadData();
  }, [userAuth]);

  useEffect(() => {
    // Filter public CVs from CVsTable
    const publicCvs = CVsTable.filter((cv) => cv.isVisibility === true);
    setTemplateCVs(publicCvs);
  }, [CVsTable]);

  const handleCreate = async () => {
    await createCV(userAuth?.id || "");
    router.push("/cv-builder");
  };

  const handleImport = async (file: File | null) => {
    try {
      const res = await importCV(userAuth?.id || "", file);
      console.log("Import CV result:", res);

      if (res.data && res.data.success) {
        console.log("Navigating to cv-builder...");
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
    setDeleteDialogOpen(false);
    setCvToDelete(null);
  };

  const handleDuplicate = (cvId: string) => {
    duplicateCV(cvId);
    router.push("/cv-builder");
  };

  const deleteDialogDescription =
    "Hành động này không thể hoàn tác. Điều này sẽ xóa vĩnh viễn CV của bạn và loại bỏ nó khỏi máy chủ của chúng tôi.";

  if (!userAuth) {
    return null;
  }

  // Show loading only when there's no cached data
  if (isLoadingUserCVs && userCVs.length === 0) {
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
      />
    </div>
  );
}
