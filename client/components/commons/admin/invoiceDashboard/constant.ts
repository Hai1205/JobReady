import { capitalizeFirstLetter } from "@/lib/utils";
import { EInvoiceStatus } from "@/types/enum";

export const invoiceStatus = Object.values(EInvoiceStatus).map(value => ({
  value,
  label: capitalizeFirstLetter(value),
}));

export type ExtendedInvoiceData = Omit<IInvoice, "status"> & {
    status: EInvoiceStatus;
};