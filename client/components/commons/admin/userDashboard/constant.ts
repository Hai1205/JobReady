import { capitalizeFirstLetter } from "@/lib/utils";
import { EUserRole, EUserStatus, EPlanType } from "@/types/enum";

export const userStatus = Object.values(EUserStatus).map(value => ({
  value,
  label: capitalizeFirstLetter(value),
}));

export const userRole = Object.values(EUserRole).map(value => ({
  value,
  label: capitalizeFirstLetter(value),
}));

export const planTypes = Object.values(EPlanType).map(value => ({
  value,
  label: capitalizeFirstLetter(value),
}));

export type ExtendedUserData = Omit<IUser, "status"> & {
  status: EUserStatus;
  role: EUserRole;
  planType?: EPlanType;
  planExpiration?: string;
  password?: string;
};