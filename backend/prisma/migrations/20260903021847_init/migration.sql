-- CreateEnum
CREATE TYPE "InvoiceSource" AS ENUM ('invoice', 'estimated_same_contractor');

-- CreateEnum
CREATE TYPE "CreditType" AS ENUM ('invoice_scan', 'additional_invoice', 'view_pricing', 'view_review', 'generate_report', 'purchase');

-- CreateTable
CREATE TABLE "users" (
    "id" TEXT NOT NULL,
    "googleEmail" TEXT NOT NULL,
    "displayName" TEXT NOT NULL,
    "firebaseUid" TEXT NOT NULL,
    "creditBalance" INTEGER NOT NULL DEFAULT 0,
    "notifyNewReview" BOOLEAN NOT NULL DEFAULT true,
    "notifyPriceUpdate" BOOLEAN NOT NULL DEFAULT true,
    "notifyInvoiceProcessed" BOOLEAN NOT NULL DEFAULT true,
    "notifyDraftReminder" BOOLEAN NOT NULL DEFAULT true,
    "prefZipCode" TEXT NOT NULL DEFAULT '',
    "prefCity" TEXT NOT NULL DEFAULT '',
    "prefState" TEXT NOT NULL DEFAULT '',
    "prefCountry" TEXT NOT NULL DEFAULT 'US',
    "fcmToken" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "users_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "contractors" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "businessName" TEXT NOT NULL DEFAULT '',
    "nameVariants" TEXT[] DEFAULT ARRAY[]::TEXT[],
    "category" TEXT NOT NULL,
    "phone" TEXT NOT NULL DEFAULT '',
    "email" TEXT NOT NULL DEFAULT '',
    "website" TEXT NOT NULL DEFAULT '',
    "city" TEXT NOT NULL DEFAULT '',
    "state" TEXT NOT NULL DEFAULT '',
    "zipCode" TEXT NOT NULL DEFAULT '',
    "country" TEXT NOT NULL DEFAULT 'US',
    "latitude" DOUBLE PRECISION,
    "longitude" DOUBLE PRECISION,
    "ratingOverall" DOUBLE PRECISION NOT NULL DEFAULT 0,
    "ratingQuality" DOUBLE PRECISION NOT NULL DEFAULT 0,
    "ratingCommunication" DOUBLE PRECISION NOT NULL DEFAULT 0,
    "ratingTimeliness" DOUBLE PRECISION NOT NULL DEFAULT 0,
    "ratingValue" DOUBLE PRECISION NOT NULL DEFAULT 0,
    "reviewCount" INTEGER NOT NULL DEFAULT 0,
    "pricingCurrency" TEXT NOT NULL DEFAULT 'USD',
    "pricingMin" DOUBLE PRECISION NOT NULL DEFAULT 0,
    "pricingMax" DOUBLE PRECISION NOT NULL DEFAULT 0,
    "pricingMedian" DOUBLE PRECISION NOT NULL DEFAULT 0,
    "pricingLaborMedian" DOUBLE PRECISION NOT NULL DEFAULT 0,
    "pricingMaterialsMedian" DOUBLE PRECISION NOT NULL DEFAULT 0,
    "pricingLaborRatio" DOUBLE PRECISION NOT NULL DEFAULT 0,
    "createdFromInvoiceId" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "contractors_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "reviews" (
    "id" TEXT NOT NULL,
    "contractorId" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "ratingQuality" INTEGER NOT NULL,
    "ratingCommunication" INTEGER NOT NULL,
    "ratingTimeliness" INTEGER NOT NULL,
    "ratingValue" INTEGER NOT NULL,
    "overallRating" DOUBLE PRECISION NOT NULL,
    "title" TEXT NOT NULL,
    "body" TEXT NOT NULL,
    "workType" TEXT NOT NULL,
    "isVerified" BOOLEAN NOT NULL DEFAULT false,
    "invoiceId" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "reviews_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "invoices" (
    "id" TEXT NOT NULL,
    "contractorId" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "documentHash" TEXT NOT NULL,
    "totalAmount" DOUBLE PRECISION NOT NULL,
    "currency" TEXT NOT NULL DEFAULT 'USD',
    "laborCost" DOUBLE PRECISION,
    "materialsCost" DOUBLE PRECISION,
    "hourlyRate" DOUBLE PRECISION,
    "projectDuration" TEXT,
    "invoiceDate" TIMESTAMP(3),
    "description" TEXT NOT NULL DEFAULT '',
    "zipCode" TEXT NOT NULL DEFAULT '',
    "lineItems" JSONB NOT NULL DEFAULT '[]',
    "source" "InvoiceSource" NOT NULL DEFAULT 'invoice',
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "invoices_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "credit_transactions" (
    "id" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "amount" INTEGER NOT NULL,
    "type" "CreditType" NOT NULL,
    "description" TEXT NOT NULL,
    "relatedId" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "credit_transactions_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "users_googleEmail_key" ON "users"("googleEmail");

-- CreateIndex
CREATE UNIQUE INDEX "users_firebaseUid_key" ON "users"("firebaseUid");

-- CreateIndex
CREATE INDEX "contractors_category_zipCode_idx" ON "contractors"("category", "zipCode");

-- CreateIndex
CREATE UNIQUE INDEX "reviews_invoiceId_key" ON "reviews"("invoiceId");

-- CreateIndex
CREATE INDEX "reviews_contractorId_createdAt_idx" ON "reviews"("contractorId", "createdAt" DESC);

-- CreateIndex
CREATE INDEX "reviews_userId_idx" ON "reviews"("userId");

-- CreateIndex
CREATE UNIQUE INDEX "invoices_documentHash_key" ON "invoices"("documentHash");

-- CreateIndex
CREATE INDEX "invoices_contractorId_idx" ON "invoices"("contractorId");

-- CreateIndex
CREATE INDEX "invoices_userId_idx" ON "invoices"("userId");

-- CreateIndex
CREATE INDEX "credit_transactions_userId_createdAt_idx" ON "credit_transactions"("userId", "createdAt" DESC);

-- AddForeignKey
ALTER TABLE "reviews" ADD CONSTRAINT "reviews_contractorId_fkey" FOREIGN KEY ("contractorId") REFERENCES "contractors"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "reviews" ADD CONSTRAINT "reviews_userId_fkey" FOREIGN KEY ("userId") REFERENCES "users"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "reviews" ADD CONSTRAINT "reviews_invoiceId_fkey" FOREIGN KEY ("invoiceId") REFERENCES "invoices"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "invoices" ADD CONSTRAINT "invoices_contractorId_fkey" FOREIGN KEY ("contractorId") REFERENCES "contractors"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "invoices" ADD CONSTRAINT "invoices_userId_fkey" FOREIGN KEY ("userId") REFERENCES "users"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "credit_transactions" ADD CONSTRAINT "credit_transactions_userId_fkey" FOREIGN KEY ("userId") REFERENCES "users"("id") ON DELETE RESTRICT ON UPDATE CASCADE;
