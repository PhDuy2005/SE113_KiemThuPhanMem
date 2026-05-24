export interface Address {
  id: string;
  province: string;
  provinceCode?: string;
  ward: string;
  wardCode?: string;
  detail: string;
  isDefault: boolean;
}
