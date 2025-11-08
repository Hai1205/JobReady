// import { useState, useCallback } from 'react';
// import { parseCV, convertToICV } from '@/lib/cvParser';
// import { enhanceCV, validateCV, getCompletenessScore } from '@/lib/cvValidator';
// import { toast } from 'react-toastify';

// interface UseCVParserResult {
//     isProcessing: boolean;
//     error: string | null;
//     parseFile: (file: File, userId: string, options?: ParseOptions) => Promise<ParseResult | null>;
// }

// interface ParseOptions {
//     enhance?: boolean;        // Auto-enhance data (format, clean)
//     validate?: boolean;       // Validate data
//     showWarnings?: boolean;   // Show warnings in toast
// }

// interface ParseResult {
//     cv: ICV;
//     score: number;
//     validation?: {
//         isValid: boolean;
//         errors: string[];
//         warnings: string[];
//     };
// }

// /**
//  * Hook to parse CV files on client-side with validation and enhancement
//  */
// export function useCVParser(): UseCVParserResult {
//     const [isProcessing, setIsProcessing] = useState(false);
//     const [error, setError] = useState<string | null>(null);

//     const parseFile = useCallback(async (
//         file: File,
//         userId: string,
//         options: ParseOptions = { enhance: true, validate: true, showWarnings: false }
//     ): Promise<ParseResult | null> => {
//         setIsProcessing(true);
//         setError(null);

//         try {
//             // Validate file type
//             const validTypes = [
//                 'application/pdf',
//                 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
//             ];

//             if (!validTypes.includes(file.type)) {
//                 throw new Error('Chỉ hỗ trợ file PDF hoặc DOCX');
//             }

//             // Validate file size (max 10MB)
//             const maxSize = 10 * 1024 * 1024;
//             if (file.size > maxSize) {
//                 throw new Error('File không được vượt quá 10MB');
//             }

//             // Parse the CV
//             const parsedCV = await parseCV(file);

//             // Convert to ICV format
//             let cv = convertToICV(parsedCV, userId);

//             // Enhance data if enabled
//             if (options.enhance) {
//                 cv = enhanceCV(cv);
//             }

//             // Validate data if enabled
//             let validation;
//             if (options.validate) {
//                 validation = validateCV(cv);

//                 // Show errors if any
//                 if (validation.errors.length > 0) {
//                     toast.warning(`Phát hiện ${validation.errors.length} lỗi: ${validation.errors[0]}`);
//                 }

//                 // Show warnings if enabled
//                 if (options.showWarnings && validation.warnings.length > 0) {
//                     toast.info(`${validation.warnings.length} cảnh báo - Vui lòng kiểm tra CV`);
//                 }
//             }

//             // Calculate completeness score
//             const score = getCompletenessScore(cv);

//             return {
//                 cv,
//                 score,
//                 validation
//             };
//         } catch (err) {
//             const errorMessage = err instanceof Error ? err.message : 'Đã xảy ra lỗi không xác định';
//             setError(errorMessage);
//             toast.error(errorMessage);
//             return null;
//         } finally {
//             setIsProcessing(false);
//         }
//     }, []);

//     return {
//         isProcessing,
//         error,
//         parseFile,
//     };
// }
