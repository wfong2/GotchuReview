import OpenAI from 'openai';
import { config } from '../config';

const openai = new OpenAI({ apiKey: config.openaiApiKey });

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
  "zipCode": null or "zip code"
}

Rules:
- Extract exact values from the invoice. Do not estimate or guess.
- If a field is not present on the invoice, use null.
- For lineItems, categorize each as "labor", "materials", or "other".
- If labor and materials are not separately itemized, set laborCost and materialsCost to null.
- Currency should be the 3-letter ISO code.
- Return ONLY the JSON object, no markdown, no explanation.`;

export async function extractInvoiceData(imageBase64: string): Promise<ExtractedInvoiceData> {
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
              url: `data:image/jpeg;base64,${imageBase64}`,
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

  return data;
}
