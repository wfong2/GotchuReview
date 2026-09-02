import { CreditType } from '@prisma/client';
import prisma from '../config/database';

const CREDIT_AMOUNTS = {
  invoice_scan: 5,
  additional_invoice: 2,
  view_pricing: -1,
  view_review: -1,
  generate_report: -3,
} as const;

export async function awardCredits(
  userId: string,
  type: 'invoice_scan' | 'additional_invoice',
  description: string,
  relatedId?: string
): Promise<number> {
  const amount = CREDIT_AMOUNTS[type];

  await prisma.$transaction([
    prisma.creditTransaction.create({
      data: { userId, amount, type, description, relatedId },
    }),
    prisma.user.update({
      where: { id: userId },
      data: { creditBalance: { increment: amount } },
    }),
  ]);

  const user = await prisma.user.findUnique({ where: { id: userId } });
  return user?.creditBalance ?? 0;
}

export async function spendCredits(
  userId: string,
  type: 'view_pricing' | 'view_review' | 'generate_report',
  description: string,
  relatedId?: string
): Promise<number> {
  const amount = CREDIT_AMOUNTS[type];

  await prisma.$transaction([
    prisma.creditTransaction.create({
      data: { userId, amount, type, description, relatedId },
    }),
    prisma.user.update({
      where: { id: userId },
      data: { creditBalance: { increment: amount } }, // amount is negative
    }),
  ]);

  const user = await prisma.user.findUnique({ where: { id: userId } });
  return user?.creditBalance ?? 0;
}

export async function addPurchasedCredits(
  userId: string,
  amount: number,
  description: string
): Promise<number> {
  await prisma.$transaction([
    prisma.creditTransaction.create({
      data: { userId, amount, type: CreditType.purchase, description },
    }),
    prisma.user.update({
      where: { id: userId },
      data: { creditBalance: { increment: amount } },
    }),
  ]);

  const user = await prisma.user.findUnique({ where: { id: userId } });
  return user?.creditBalance ?? 0;
}
