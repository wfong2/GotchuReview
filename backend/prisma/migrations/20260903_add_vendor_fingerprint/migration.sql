-- AlterTable
ALTER TABLE "contractors" ADD COLUMN "vendorFingerprints" TEXT[] DEFAULT ARRAY[]::TEXT[];

-- AlterTable
ALTER TABLE "invoices" ADD COLUMN "vendorFingerprint" TEXT;
ALTER TABLE "invoices" ADD COLUMN "vendorTemplateData" JSONB;
