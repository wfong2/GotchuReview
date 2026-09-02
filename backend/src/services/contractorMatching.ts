import Fuse from 'fuse.js';
import { Contractor } from '@prisma/client';
import prisma from '../config/database';

export interface MatchCandidate {
  contractor: Contractor;
  score: number;
}

export async function findMatchingContractors(
  name: string,
  city?: string,
  zipCode?: string,
  phone?: string
): Promise<MatchCandidate[]> {
  // Fetch candidates from the same general area
  const where: Record<string, unknown> = {};
  if (zipCode) {
    where.zipCode = zipCode;
  } else if (city) {
    where.city = { contains: city, mode: 'insensitive' };
  }

  const candidates = await prisma.contractor.findMany({
    where,
    take: 100,
  });

  if (candidates.length === 0) return [];

  // Use Fuse.js for fuzzy name matching
  const fuse = new Fuse(candidates, {
    keys: ['name', 'businessName', 'nameVariants'],
    threshold: 0.4,
    includeScore: true,
  });

  const results = fuse.search(name);

  let matches: MatchCandidate[] = results.map((r) => ({
    contractor: r.item,
    score: 1 - (r.score || 1),
  }));

  // Boost score if phone matches
  if (phone) {
    const normalizedPhone = phone.replace(/\D/g, '');
    matches = matches.map((m) => {
      const contractorPhone = m.contractor.phone.replace(/\D/g, '');
      if (contractorPhone && contractorPhone === normalizedPhone) {
        return { ...m, score: Math.min(m.score + 0.3, 1) };
      }
      return m;
    });
  }

  return matches.sort((a, b) => b.score - a.score).slice(0, 5);
}
