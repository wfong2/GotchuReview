import { PrismaClient, CreditType, InvoiceSource } from '@prisma/client';
import crypto from 'crypto';

const prisma = new PrismaClient();

async function main() {
  console.log('Seeding database...');

  // ─── Dev User ────────────────────────────────────────
  const devUser = await prisma.user.upsert({
    where: { firebaseUid: 'dev-test-uid-001' },
    update: {},
    create: {
      firebaseUid: 'dev-test-uid-001',
      googleEmail: 'dev@gotchureviews.com',
      displayName: 'Dev User',
      creditBalance: 50,
      prefZipCode: '94110',
      prefCity: 'San Francisco',
      prefState: 'CA',
    },
  });
  console.log(`  User: ${devUser.displayName} (${devUser.id})`);

  // ─── Contractors ─────────────────────────────────────
  const contractorData = [
    {
      name: "Bob Martinez",
      businessName: "Bob's Plumbing",
      category: "plumber",
      phone: "(555) 234-5678",
      email: "bob@bobsplumbing.com",
      city: "San Francisco",
      state: "CA",
      zipCode: "94110",
      ratingOverall: 4.5,
      ratingQuality: 5,
      ratingCommunication: 4,
      ratingTimeliness: 4,
      ratingValue: 5,
      reviewCount: 3,
      pricingMin: 350,
      pricingMax: 1800,
      pricingMedian: 1250,
      pricingLaborMedian: 850,
      pricingMaterialsMedian: 400,
      pricingLaborRatio: 0.68,
    },
    {
      name: "Joe Chen",
      businessName: "Joe's Pipes & Drains",
      category: "plumber",
      phone: "(555) 345-6789",
      email: "joe@joespipes.com",
      city: "San Francisco",
      state: "CA",
      zipCode: "94112",
      ratingOverall: 4.0,
      ratingQuality: 4,
      ratingCommunication: 4,
      ratingTimeliness: 4,
      ratingValue: 4,
      reviewCount: 0,
      pricingMin: 200,
      pricingMax: 900,
      pricingMedian: 550,
      pricingLaborMedian: 350,
      pricingMaterialsMedian: 200,
      pricingLaborRatio: 0.64,
    },
    {
      name: "Sarah Kim",
      businessName: "Quick Electric",
      category: "electrician",
      phone: "(555) 456-7890",
      email: "sarah@quickelectric.com",
      city: "San Francisco",
      state: "CA",
      zipCode: "94110",
      ratingOverall: 4.8,
      ratingQuality: 5,
      ratingCommunication: 5,
      ratingTimeliness: 4,
      ratingValue: 5,
      reviewCount: 0,
      pricingMin: 150,
      pricingMax: 2500,
      pricingMedian: 800,
      pricingLaborMedian: 600,
      pricingMaterialsMedian: 200,
      pricingLaborRatio: 0.75,
    },
    {
      name: "Mike Johnson",
      businessName: "ABC Roofing",
      category: "roofer",
      phone: "(555) 567-8901",
      email: "mike@abcroofing.com",
      city: "San Francisco",
      state: "CA",
      zipCode: "94110",
      ratingOverall: 4.2,
      ratingQuality: 4,
      ratingCommunication: 5,
      ratingTimeliness: 4,
      ratingValue: 4,
      reviewCount: 0,
      pricingMin: 500,
      pricingMax: 8000,
      pricingMedian: 3500,
      pricingLaborMedian: 2500,
      pricingMaterialsMedian: 1000,
      pricingLaborRatio: 0.71,
    },
    {
      name: "Lisa Garcia",
      businessName: "Fresh Coat Painters",
      category: "painter",
      phone: "(555) 678-9012",
      email: "lisa@freshcoat.com",
      city: "San Francisco",
      state: "CA",
      zipCode: "94110",
      ratingOverall: 4.6,
      ratingQuality: 5,
      ratingCommunication: 5,
      ratingTimeliness: 4,
      ratingValue: 4,
      reviewCount: 0,
      pricingMin: 300,
      pricingMax: 4000,
      pricingMedian: 1800,
      pricingLaborMedian: 1400,
      pricingMaterialsMedian: 400,
      pricingLaborRatio: 0.78,
    },
    {
      name: "David Park",
      businessName: "CoolAir HVAC",
      category: "hvac",
      phone: "(555) 789-0123",
      email: "david@coolairhvac.com",
      city: "San Francisco",
      state: "CA",
      zipCode: "94112",
      ratingOverall: 4.3,
      ratingQuality: 4,
      ratingCommunication: 4,
      ratingTimeliness: 5,
      ratingValue: 4,
      reviewCount: 0,
      pricingMin: 200,
      pricingMax: 6000,
      pricingMedian: 2200,
      pricingLaborMedian: 1500,
      pricingMaterialsMedian: 700,
      pricingLaborRatio: 0.68,
    },
    {
      name: "Carlos Rivera",
      businessName: "Green Thumb Landscaping",
      category: "landscaper",
      phone: "(555) 890-1234",
      email: "carlos@greenthumb.com",
      city: "San Francisco",
      state: "CA",
      zipCode: "94110",
      ratingOverall: 4.7,
      ratingQuality: 5,
      ratingCommunication: 5,
      ratingTimeliness: 4,
      ratingValue: 5,
      reviewCount: 0,
      pricingMin: 150,
      pricingMax: 5000,
      pricingMedian: 1200,
      pricingLaborMedian: 900,
      pricingMaterialsMedian: 300,
      pricingLaborRatio: 0.75,
    },
    {
      name: "Tom Wilson",
      businessName: "Wilson Woodworks",
      category: "carpenter",
      phone: "(555) 901-2345",
      email: "tom@wilsonwood.com",
      city: "San Francisco",
      state: "CA",
      zipCode: "94110",
      ratingOverall: 4.4,
      ratingQuality: 5,
      ratingCommunication: 4,
      ratingTimeliness: 4,
      ratingValue: 4,
      reviewCount: 0,
      pricingMin: 400,
      pricingMax: 7000,
      pricingMedian: 2800,
      pricingLaborMedian: 2100,
      pricingMaterialsMedian: 700,
      pricingLaborRatio: 0.75,
    },
    {
      name: "Alex Stone",
      businessName: "StoneWork Masonry",
      category: "mason",
      phone: "(555) 012-3456",
      email: "alex@stonework.com",
      city: "San Francisco",
      state: "CA",
      zipCode: "94112",
      ratingOverall: 4.1,
      ratingQuality: 4,
      ratingCommunication: 4,
      ratingTimeliness: 4,
      ratingValue: 4,
      reviewCount: 0,
      pricingMin: 600,
      pricingMax: 10000,
      pricingMedian: 4000,
      pricingLaborMedian: 3000,
      pricingMaterialsMedian: 1000,
      pricingLaborRatio: 0.75,
    },
    {
      name: "Nina Patel",
      businessName: "Perfect Floors",
      category: "flooring",
      phone: "(555) 123-4567",
      email: "nina@perfectfloors.com",
      city: "San Francisco",
      state: "CA",
      zipCode: "94110",
      ratingOverall: 4.9,
      ratingQuality: 5,
      ratingCommunication: 5,
      ratingTimeliness: 5,
      ratingValue: 5,
      reviewCount: 0,
      pricingMin: 800,
      pricingMax: 6000,
      pricingMedian: 3000,
      pricingLaborMedian: 2000,
      pricingMaterialsMedian: 1000,
      pricingLaborRatio: 0.67,
    },
  ];

  const contractors: Record<string, string> = {};
  for (const c of contractorData) {
    const created = await prisma.contractor.create({ data: c });
    contractors[c.businessName] = created.id;
    console.log(`  Contractor: ${c.businessName} (${c.category})`);
  }

  const bobId = contractors["Bob's Plumbing"];

  // ─── Invoices for Bob's Plumbing (tied to reviews) ───
  const invoice1 = await prisma.invoice.create({
    data: {
      contractorId: bobId,
      userId: devUser.id,
      documentHash: crypto.randomUUID(),
      totalAmount: 1250,
      laborCost: 850,
      materialsCost: 400,
      hourlyRate: 85,
      projectDuration: "2 days",
      invoiceDate: new Date("2024-09-15"),
      description: "Kitchen sink replacement and garbage disposal installation",
      zipCode: "94110",
      lineItems: [
        { description: "Remove old sink", amount: 250, category: "labor" },
        { description: "Install new sink", amount: 350, category: "labor" },
        { description: "Install garbage disposal", amount: 250, category: "labor" },
        { description: "Stainless steel sink", amount: 220, category: "materials" },
        { description: "Garbage disposal unit", amount: 130, category: "materials" },
        { description: "Plumbing fittings", amount: 50, category: "materials" },
      ],
    },
  });

  const invoice2 = await prisma.invoice.create({
    data: {
      contractorId: bobId,
      userId: devUser.id,
      documentHash: crypto.randomUUID(),
      totalAmount: 1800,
      laborCost: 1200,
      materialsCost: 600,
      hourlyRate: 85,
      projectDuration: "3 days",
      invoiceDate: new Date("2024-07-20"),
      description: "Bathroom remodel — new shower valve and tile work",
      zipCode: "94110",
      lineItems: [
        { description: "Demo old shower", amount: 400, category: "labor" },
        { description: "Install new valve", amount: 400, category: "labor" },
        { description: "Tile work", amount: 400, category: "labor" },
        { description: "Shower valve kit", amount: 280, category: "materials" },
        { description: "Tile and grout", amount: 320, category: "materials" },
      ],
    },
  });

  const invoice3 = await prisma.invoice.create({
    data: {
      contractorId: bobId,
      userId: devUser.id,
      documentHash: crypto.randomUUID(),
      totalAmount: 350,
      laborCost: 280,
      materialsCost: 70,
      hourlyRate: 85,
      projectDuration: "3 hours",
      invoiceDate: new Date("2024-05-10"),
      description: "Emergency pipe leak repair in basement",
      zipCode: "94110",
      lineItems: [
        { description: "Locate and repair leak", amount: 280, category: "labor" },
        { description: "Pipe fittings and sealant", amount: 70, category: "materials" },
      ],
    },
  });

  console.log(`  3 invoices for Bob's Plumbing`);

  // ─── Reviews on Bob's Plumbing ───────────────────────
  await prisma.review.create({
    data: {
      contractorId: bobId,
      userId: devUser.id,
      ratingQuality: 5,
      ratingCommunication: 4,
      ratingTimeliness: 5,
      ratingValue: 5,
      overallRating: 4.75,
      title: "Excellent kitchen sink replacement",
      body: "Bob replaced our kitchen sink and installed a new garbage disposal. Very professional, cleaned up after the job, and pricing was fair. Highly recommend for any plumbing work.",
      workType: "plumber",
      isVerified: true,
      invoiceId: invoice1.id,
    },
  });

  await prisma.review.create({
    data: {
      contractorId: bobId,
      userId: devUser.id,
      ratingQuality: 5,
      ratingCommunication: 4,
      ratingTimeliness: 3,
      ratingValue: 4,
      overallRating: 4.0,
      title: "Great bathroom work, slightly delayed",
      body: "Shower remodel turned out beautifully. The tile work is flawless. Took an extra day due to supply issues, but Bob kept us informed. Final result was worth the wait.",
      workType: "plumber",
      isVerified: true,
      invoiceId: invoice2.id,
    },
  });

  await prisma.review.create({
    data: {
      contractorId: bobId,
      userId: devUser.id,
      ratingQuality: 5,
      ratingCommunication: 5,
      ratingTimeliness: 5,
      ratingValue: 5,
      overallRating: 5.0,
      title: "Saved us from a flood!",
      body: "Called Bob for an emergency pipe leak at 8pm. He arrived within an hour and fixed the leak quickly. Very reasonable pricing for an emergency call. Can't thank him enough.",
      workType: "plumber",
      isVerified: true,
      invoiceId: invoice3.id,
    },
  });

  console.log(`  3 reviews for Bob's Plumbing`);

  // ─── Additional invoices for History tab ──────────────
  await prisma.invoice.create({
    data: {
      contractorId: contractors["Quick Electric"],
      userId: devUser.id,
      documentHash: crypto.randomUUID(),
      totalAmount: 680,
      laborCost: 480,
      materialsCost: 200,
      hourlyRate: 120,
      projectDuration: "4 hours",
      invoiceDate: new Date("2024-10-22"),
      description: "Panel upgrade and new 240V outlet for EV charger",
      zipCode: "94110",
      lineItems: [
        { description: "Panel inspection and upgrade", amount: 280, category: "labor" },
        { description: "Install 240V outlet", amount: 200, category: "labor" },
        { description: "Outlet and wiring", amount: 200, category: "materials" },
      ],
    },
  });

  await prisma.invoice.create({
    data: {
      contractorId: contractors["Fresh Coat Painters"],
      userId: devUser.id,
      documentHash: crypto.randomUUID(),
      totalAmount: 2400,
      laborCost: 1900,
      materialsCost: 500,
      invoiceDate: new Date("2024-08-05"),
      description: "Interior painting — living room, dining room, and hallway",
      zipCode: "94110",
      lineItems: [
        { description: "Prep and tape", amount: 400, category: "labor" },
        { description: "Prime walls", amount: 300, category: "labor" },
        { description: "Two coats paint", amount: 1200, category: "labor" },
        { description: "Premium paint (5 gal)", amount: 350, category: "materials" },
        { description: "Primer and supplies", amount: 150, category: "materials" },
      ],
    },
  });

  await prisma.invoice.create({
    data: {
      contractorId: contractors["Green Thumb Landscaping"],
      userId: devUser.id,
      documentHash: crypto.randomUUID(),
      totalAmount: 1500,
      laborCost: 1100,
      materialsCost: 400,
      invoiceDate: new Date("2024-06-12"),
      description: "Front yard redesign with drought-tolerant plants",
      zipCode: "94110",
      lineItems: [
        { description: "Remove existing plants", amount: 300, category: "labor" },
        { description: "Install drip irrigation", amount: 400, category: "labor" },
        { description: "Plant and mulch", amount: 400, category: "labor" },
        { description: "Plants and mulch", amount: 300, category: "materials" },
        { description: "Drip system parts", amount: 100, category: "materials" },
      ],
    },
  });

  await prisma.invoice.create({
    data: {
      contractorId: contractors["Wilson Woodworks"],
      userId: devUser.id,
      documentHash: crypto.randomUUID(),
      totalAmount: 3200,
      laborCost: 2400,
      materialsCost: 800,
      projectDuration: "5 days",
      invoiceDate: new Date("2023-12-10"),
      description: "Custom built-in bookshelf for home office",
      zipCode: "94110",
      lineItems: [
        { description: "Design and measurements", amount: 400, category: "labor" },
        { description: "Build shelving unit", amount: 1200, category: "labor" },
        { description: "Install and finish", amount: 800, category: "labor" },
        { description: "Hardwood lumber", amount: 600, category: "materials" },
        { description: "Hardware and finish", amount: 200, category: "materials" },
      ],
    },
  });

  console.log(`  4 additional invoices for History tab`);

  // ─── Credit Transactions ─────────────────────────────
  const creditEntries: { amount: number; type: CreditType; description: string }[] = [
    { amount: 50, type: 'purchase', description: "Welcome bonus — 50 free credits" },
    { amount: -5, type: 'invoice_scan', description: "Scanned invoice: Bob's Plumbing — kitchen sink" },
    { amount: -5, type: 'invoice_scan', description: "Scanned invoice: Bob's Plumbing — bathroom remodel" },
    { amount: -5, type: 'invoice_scan', description: "Scanned invoice: Bob's Plumbing — pipe leak repair" },
    { amount: -5, type: 'invoice_scan', description: "Scanned invoice: Quick Electric — panel upgrade" },
    { amount: -2, type: 'view_pricing', description: "Viewed pricing: Quick Electric" },
    { amount: -5, type: 'invoice_scan', description: "Scanned invoice: Fresh Coat Painters" },
    { amount: -5, type: 'invoice_scan', description: "Scanned invoice: Green Thumb Landscaping" },
    { amount: -5, type: 'invoice_scan', description: "Scanned invoice: Wilson Woodworks" },
  ];

  for (const entry of creditEntries) {
    await prisma.creditTransaction.create({
      data: { userId: devUser.id, ...entry },
    });
  }

  // Update final balance (50 - 5*7 - 2 = 13)
  await prisma.user.update({
    where: { id: devUser.id },
    data: { creditBalance: 13 },
  });

  console.log(`  ${creditEntries.length} credit transactions (balance: 13)`);
  console.log('Seeding complete!');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
