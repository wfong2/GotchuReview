import OpenAI from 'openai';
import { config } from '../config';
import { VendorTemplateFeatures, computeVendorFingerprint } from './vendorFingerprint';

const openai = config.openaiApiKey
  ? new OpenAI({ apiKey: config.openaiApiKey })
  : null;

export interface ExtractedInvoiceData {
  contractorName: string;
  businessName: string;
  totalAmount: number;
  currency: string;
  laborCost: number | null;
  materialsCost: number | null;
  hourlyRate: number | null;
  projectDuration: string | null;
  invoiceDate: string | null;
  description: string;
  lineItems: Array<{
    description: string;
    amount: number;
    category: 'labor' | 'materials' | 'other';
  }>;
  contractorPhone: string | null;
  contractorEmail: string | null;
  contractorAddress: string | null;
  zipCode: string | null;
  vendorTemplate: VendorTemplateFeatures | null;
  vendorFingerprint: string | null;
}

const EXTRACTION_PROMPT = `You are an invoice data extraction assistant. Analyze this invoice image and extract the following information as JSON.

Return ONLY valid JSON with these fields:
{
  "contractorName": "Name of the contractor or business",
  "businessName": "Registered business name if different from contractor name",
  "totalAmount": 0.00,
  "currency": "USD",
  "laborCost": null or number,
  "materialsCost": null or number,
  "hourlyRate": null or number,
  "projectDuration": null or "string describing duration",
  "invoiceDate": null or "YYYY-MM-DD",
  "description": "Brief description of the work performed",
  "lineItems": [
    { "description": "item description", "amount": 0.00, "category": "labor|materials|other" }
  ],
  "contractorPhone": null or "phone number",
  "contractorEmail": null or "email",
  "contractorAddress": null or "full address",
  "zipCode": null or "zip code",
  "vendorTemplate": {
    "vendorNameNormalized": "lowercase company name, no punctuation",
    "fieldLabels": ["sorted", "lowercase", "labels", "found", "on", "invoice"],
    "sectionOrder": ["header", "billing-info", "line-items", "totals", "footer"],
    "invoiceNumberFormat": "pattern like INV-#### or #### or unknown",
    "logoPosition": "top-left | top-center | top-right | none",
    "fontCategory": "serif | sans-serif | mixed | monospace",
    "dominantColors": ["#hex1", "#hex2"],
    "tableStyle": "bordered | lines-only | no-borders | alternating-rows | none"
  }
}

Rules:
- Extract exact values from the invoice. Do not estimate or guess.
- If a field is not present on the invoice, use null.
- For lineItems, categorize each as "labor", "materials", or "other".
- If labor and materials are not separately itemized, set laborCost and materialsCost to null.
- Currency should be the 3-letter ISO code.
- vendorTemplate describes the TEMPLATE STRUCTURE, not the invoice content — these fields should be identical across different invoices from the same vendor.
- vendorNameNormalized: lowercase the company/business name, remove all punctuation and extra spaces.
- fieldLabels: list every visible field label on the invoice (e.g., "invoice #", "date", "bill to", "qty", "amount", "total"), lowercase and sorted alphabetically.
- sectionOrder: describe the top-to-bottom order of major sections using these terms: "header", "vendor-info", "billing-info", "invoice-meta", "line-items", "subtotals", "totals", "payment-info", "notes", "footer".
- invoiceNumberFormat: replace digits with #, letters with A, keep punctuation (e.g., "INV-####", "AA-######").
- dominantColors: the 2-3 most prominent brand/template colors as hex, sorted ascending.
- logoPosition: position of the company logo on the invoice.
- fontCategory: the primary font style used in the invoice.
- tableStyle: the style of the line items table.
- Return ONLY the JSON object, no markdown, no explanation.`;

function getMockExtractionData(): ExtractedInvoiceData {
  const vendorTemplate: VendorTemplateFeatures = {
    vendorNameNormalized: "bobs plumbing",
    fieldLabels: ["amount", "bill to", "date", "description", "invoice #", "qty", "total"],
    sectionOrder: ["header", "vendor-info", "billing-info", "invoice-meta", "line-items", "totals", "payment-info", "footer"],
    invoiceNumberFormat: "INV-####",
    logoPosition: "top-left",
    fontCategory: "sans-serif",
    dominantColors: ["#1a5276", "#2ecc71"],
    tableStyle: "bordered",
  };

  return {
    contractorName: "Bob Martinez",
    businessName: "Bob's Plumbing",
    totalAmount: 1250.00,
    currency: "USD",
    laborCost: 850.00,
    materialsCost: 400.00,
    hourlyRate: 85.00,
    projectDuration: "2 days",
    invoiceDate: "2024-11-15",
    description: "Kitchen sink replacement and garbage disposal installation",
    lineItems: [
      { description: "Remove old sink and disposal", amount: 250.00, category: "labor" },
      { description: "Install new undermount sink", amount: 350.00, category: "labor" },
      { description: "Install garbage disposal unit", amount: 250.00, category: "labor" },
      { description: "Undermount stainless steel sink", amount: 220.00, category: "materials" },
      { description: "InSinkErator garbage disposal", amount: 130.00, category: "materials" },
      { description: "Plumbing fittings and supplies", amount: 50.00, category: "materials" },
    ],
    contractorPhone: "(555) 234-5678",
    contractorEmail: "bob@bobsplumbing.com",
    contractorAddress: "1234 Pipe Lane, San Francisco, CA 94110",
    zipCode: "94110",
    vendorTemplate,
    vendorFingerprint: computeVendorFingerprint(vendorTemplate),
  };
}

export async function extractInvoiceData(imageBase64: string, mimeType: string = 'image/jpeg'): Promise<ExtractedInvoiceData> {
  if (!openai) {
    console.warn('[Invoice] No OpenAI API key — returning mock extraction data');
    await new Promise((resolve) => setTimeout(resolve, 1500));
    return getMockExtractionData();
  }

  const response = await openai.chat.completions.create({
    model: 'gpt-4o',
    messages: [
      {
        role: 'user',
        content: [
          { type: 'text', text: EXTRACTION_PROMPT },
          {
            type: 'image_url',
            image_url: {
              url: `data:${mimeType};base64,${imageBase64}`,
              detail: 'high',
            },
          },
        ],
      },
    ],
    max_tokens: 2000,
    temperature: 0,
  });

  const content = response.choices[0]?.message?.content;
  if (!content) {
    throw new Error('No response from OpenAI Vision API');
  }

  const jsonString = content.replace(/```json\n?/g, '').replace(/```\n?/g, '').trim();
  const data: ExtractedInvoiceData = JSON.parse(jsonString);

  if (data.vendorTemplate) {
    data.vendorFingerprint = computeVendorFingerprint(data.vendorTemplate);
  } else {
    data.vendorFingerprint = null;
  }

  return data;
}
