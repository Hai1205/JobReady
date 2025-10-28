import { useCVStore } from "@/stores/cvStore";
import { create } from "zustand";

// Store để lưu mode hiện tại (create hoặc update)
interface CVModeStore {
    mode: "create" | "update";
    setMode: (mode: "create" | "update") => void;
}

export const useCVModeStore = create<CVModeStore>((set) => ({
    mode: "create",
    setMode: (mode) => set({ mode }),
}));

/**
 * Hook để lấy CV và các hàm update phù hợp với mode hiện tại
 * Tự động chọn currentCVCreate hoặc currentCVUpdate dựa trên mode
 */
export function useCurrentCV() {
    const { mode } = useCVModeStore();
    const {
        currentCVCreate,
        currentCVUpdate,
        handleUpdateCVCreate,
        handleUpdateCVUpdate,
        handleUpdateCV: _handleUpdateCV, // Rename để tránh conflict
        ...rest
    } = useCVStore();

    // Chọn CV dựa trên mode
    const currentCV = mode === "create" ? currentCVCreate : currentCVUpdate;

    // Chọn hàm update dựa trên mode
    const handleUpdateCV = mode === "create" ? handleUpdateCVCreate : handleUpdateCVUpdate;

    return {
        currentCV,
        handleUpdateCV,
        mode,
        ...rest,
    };
}
