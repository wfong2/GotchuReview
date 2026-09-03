import crypto from 'crypto';

export interface VendorTemplateFeatures {
  vendorNameNormalized: string;
  fieldLabels: string[];
  sectionOrder: string[];
  invoiceNumberFormat: string;
  logoPosition: 'top-left' | 'top-center' | 'top-right' | 'none';
  fontCategory: 'serif' | 'sans-serif' | 'mixed' | 'monospace';
  dominantColors: string[];
  tableStyle: 'bordered' | 'lines-only' | 'no-borders' | 'alternating-rows' | 'none';
}

export function computeVendorFingerprint(features: VendorTemplateFeatures): string {
  const normalized = {
    vendorName: features.vendorNameNormalized.toLowerCase().trim(),
    fieldLabels: features.fieldLabels.map((l) => l.toLowerCase().trim()).sort(),
    sectionOrder: features.sectionOrder.map((s) => s.toLowerCase().trim()),
    invoiceNumberFormat: features.invoiceNumberFormat.toLowerCase().trim(),
    logoPosition: features.logoPosition,
    fontCategory: features.fontCategory,
    dominantColors: features.dominantColors.map((c) => c.toLowerCase().trim()).sort(),
    tableStyle: features.tableStyle,
  };

  const payload = JSON.stringify(normalized);
  return 'vfp:' + crypto.createHash('sha256').update(payload).digest('hex');
}

export function computePartialFingerprint(features: VendorTemplateFeatures): string {
  const normalized = {
    fieldLabels: features.fieldLabels.map((l) => l.toLowerCase().trim()).sort(),
    sectionOrder: features.sectionOrder.map((s) => s.toLowerCase().trim()),
    invoiceNumberFormat: features.invoiceNumberFormat.toLowerCase().trim(),
    logoPosition: features.logoPosition,
    fontCategory: features.fontCategory,
    dominantColors: features.dominantColors.map((c) => c.toLowerCase().trim()).sort(),
    tableStyle: features.tableStyle,
  };

  const payload = JSON.stringify(normalized);
  return 'vfp-partial:' + crypto.createHash('sha256').update(payload).digest('hex');
}
