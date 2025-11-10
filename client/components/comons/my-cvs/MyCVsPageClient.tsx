"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { useAuthStore } from "@/stores/authStore";
import { useCVStore } from "@/stores/cvStore";
import PageHeader from "@/components/comons/my-cvs/PageHeader";
import DeleteConfirmationDialog from "@/components/comons/layout/DeleteConfirmationDialog";
import LoadingPage from "@/components/comons/layout/LoadingPage";
import UserCVsSection from "@/components/comons/my-cvs/UserCVsSection";
import TemplateCVsSection from "@/components/comons/my-cvs/TemplateCVsSection";

export default function MyCVsPageClient() {
  const { userAuth } = useAuthStore();
  const {
    handleSetCurrentStep,
    createCV,
    deleteCV,
    duplicateCV,
    getUserCVs,
    getAllCVs,
    handleGeneratePDF,
    handleSetCurrentCV,
    userCVs,
  } = useCVStore();

  const router = useRouter();
  const [loading, setLoading] = useState(true);
  const [templateCVs, setTemplateCVs] = useState<ICV[]>([]);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [cvToDelete, setCvToDelete] = useState<string | null>(null);

  useEffect(() => {
    const loadData = async () => {
      if (userAuth) {
        await getUserCVs(userAuth.id);
      }
      await loadTemplateCVs();
      setLoading(false);
    };
    loadData();
  }, [userAuth, router]);

  const loadTemplateCVs = async () => {
    const response = await getAllCVs();
    if (response?.data) {
      const allCvs = response.data.cvs || [];
      const publicCvs = allCvs.filter((cv) => cv.isVisibility === true);
      setTemplateCVs(publicCvs);
    }
  };

  const handleCreate = async () => {
    await createCV(userAuth?.id || "");
    handleSetCurrentStep(0);
    router.push("/cv-builder");
  };

  const handleEdit = (cv: ICV) => {
    handleSetCurrentStep(0);
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

  if (loading) {
    return <LoadingPage />;
  }

  return (
    <div className="min-h-screen flex items-center justify-center py-12">
      <div className="container max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col gap-8">
          <PageHeader onCreateNew={handleCreate} />

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

      <DeleteConfirmationDialog
        open={deleteDialogOpen}
        description={deleteDialogDescription}
        onOpenChange={setDeleteDialogOpen}
        onConfirm={handleDeleteConfirm}
      />
    </div>
  );
}
