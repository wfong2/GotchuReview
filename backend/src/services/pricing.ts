import prisma from '../config/database';

function median(values: number[]): number {
  if (values.length === 0) return 0;
  const sorted = [...values].sort((a, b) => a - b);
  const mid = Math.floor(sorted.length / 2);
  return sorted.length % 2 === 0
    ? (sorted[mid - 1] + sorted[mid]) / 2
    : sorted[mid];
}

export async function recomputeContractorPricing(contractorId: string): Promise<void> {
  const invoices = await prisma.invoice.findMany({
    where: { contractorId },
  });

  if (invoices.length === 0) return;

  const totals = invoices.map((i) => i.totalAmount);
  const laborCosts = invoices.map((i) => i.laborCost).filter((v): v is number => v !== null);
  const materialsCosts = invoices.map((i) => i.materialsCost).filter((v): v is number => v !== null);

  const medianTotal = median(totals);
  const laborMedian = median(laborCosts);
  const materialsMedian = median(materialsCosts);
  const laborRatio = medianTotal > 0 && laborMedian > 0 ? laborMedian / medianTotal : 0;

  await prisma.contractor.update({
    where: { id: contractorId },
    data: {
      pricingMin: Math.min(...totals),
      pricingMax: Math.max(...totals),
      pricingMedian: medianTotal,
      pricingLaborMedian: laborMedian,
      pricingMaterialsMedian: materialsMedian,
      pricingLaborRatio: Math.round(laborRatio * 100) / 100,
    },
  });
}

export async function recomputeContractorRatings(contractorId: string): Promise<void> {
  const reviews = await prisma.review.findMany({
    where: { contractorId },
  });

  if (reviews.length === 0) return;

  const avg = (vals: number[]) => vals.reduce((a, b) => a + b, 0) / vals.length;

  const quality = avg(reviews.map((r) => r.ratingQuality));
  const communication = avg(reviews.map((r) => r.ratingCommunication));
  const timeliness = avg(reviews.map((r) => r.ratingTimeliness));
  const value = avg(reviews.map((r) => r.ratingValue));
  const overall = (quality + communication + timeliness + value) / 4;

  await prisma.contractor.update({
    where: { id: contractorId },
    data: {
      ratingOverall: Math.round(overall * 10) / 10,
      ratingQuality: Math.round(quality * 10) / 10,
      ratingCommunication: Math.round(communication * 10) / 10,
      ratingTimeliness: Math.round(timeliness * 10) / 10,
      ratingValue: Math.round(value * 10) / 10,
      reviewCount: reviews.length,
    },
  });
}
