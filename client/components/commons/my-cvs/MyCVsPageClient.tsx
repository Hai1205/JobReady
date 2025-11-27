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
  const [isDraggingOnPage, setIsDraggingOnPage] = useState(false);
  const [importDialogOpen, setImportDialogOpen] = useState(false);
  const [droppedFile, setDroppedFile] = useState<File | null>(null);

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

  // Page-level drag and drop handlers
  const handlePageDragEnter = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.dataTransfer.types.includes("Files")) {
      setIsDraggingOnPage(true);
    }
  };

  const handlePageDragLeave = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    // Only hide overlay if leaving the container entirely
    if (e.currentTarget === e.target) {
      setIsDraggingOnPage(false);
    }
  };

  const handlePageDragOver = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
  };

  const handlePageDrop = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setIsDraggingOnPage(false);

    const file = e.dataTransfer.files?.[0];
    if (file && file.type === "application/pdf") {
      setDroppedFile(file);
      setImportDialogOpen(true);
    } else if (file) {
      toast.error("Chỉ chấp nhận file PDF!");
    }
  };

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
    <div
      className="min-h-screen flex items-center justify-center py-12 relative"
      onDragEnter={handlePageDragEnter}
      onDragLeave={handlePageDragLeave}
      onDragOver={handlePageDragOver}
      onDrop={handlePageDrop}
    >
      {/* Drag Overlay */}
      {isDraggingOnPage && (
        <div className="fixed inset-0 bg-primary/10 backdrop-blur-sm z-40 flex items-center justify-center pointer-events-none">
          <div className="bg-background border-2 border-dashed border-primary rounded-lg p-12 shadow-2xl">
            <div className="flex flex-col items-center gap-4">
              <div className="w-20 h-20 rounded-full bg-primary/20 flex items-center justify-center">
                <svg
                  className="w-10 h-10 text-primary"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12"
                  />
                </svg>
              </div>
              <div className="text-center">
                <p className="text-2xl font-bold text-primary mb-2">
                  Thả file PDF vào đây
                </p>
                <p className="text-sm text-muted-foreground">
                  để import CV của bạn
                </p>
              </div>
            </div>
          </div>
        </div>
      )}

      <div className="container max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex flex-col gap-8">
          <PageHeader
            onCreateNew={handleCreate}
            onImport={handleImport}
            externalFile={droppedFile}
            isImportDialogOpen={importDialogOpen}
            onImportDialogOpenChange={setImportDialogOpen}
          />

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
