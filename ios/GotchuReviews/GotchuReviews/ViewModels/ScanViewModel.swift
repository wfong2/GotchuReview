import SwiftUI
import AVFoundation

@MainActor
class ScanViewModel: ObservableObject {
    @Published var capturedImage: UIImage?
    @Published var extractionResult: ExtractionResponse?
    @Published var isExtracting = false
    @Published var errorMessage: String?

    // Review form state
    @Published var selectedContractorId: String?
    @Published var isNewContractor = false
    @Published var newContractorCategory: TradeCategory = .general
    @Published var ratingQuality = 0
    @Published var ratingCommunication = 0
    @Published var ratingTimeliness = 0
    @Published var ratingValue = 0
    @Published var reviewTitle = ""
    @Published var reviewBody = ""

    // Flow state
    @Published var flowStep: ScanFlowStep = .camera

    enum ScanFlowStep {
        case camera
        case extracting
        case extracted
        case review
        case submitted
    }

    func extractInvoice(imageData: Data) async {
        flowStep = .extracting
        isExtracting = true
        errorMessage = nil

        do {
            let result = try await APIClient.shared.extractInvoice(imageData: imageData)
            extractionResult = result

            // Auto-select top contractor match if confidence > 70%
            if let topMatch = result.contractorMatches.first, topMatch.confidence > 70 {
                selectedContractorId = topMatch.contractor.id
            }

            flowStep = .extracted
        } catch let error as APIError {
            if case .duplicateInvoice = error {
                errorMessage = NSLocalizedString("scan.duplicateInvoice", comment: "")
            } else {
                errorMessage = error.localizedDescription
            }
            flowStep = .camera
        } catch {
            errorMessage = error.localizedDescription
            flowStep = .camera
        }

        isExtracting = false
    }

    func confirmExtraction() {
        flowStep = .review
    }

    func rescan() {
        capturedImage = nil
        extractionResult = nil
        errorMessage = nil
        flowStep = .camera
    }

    var canSubmitReview: Bool {
        ratingQuality > 0 && ratingCommunication > 0 &&
        ratingTimeliness > 0 && ratingValue > 0 &&
        !reviewTitle.isEmpty && !reviewBody.isEmpty &&
        (selectedContractorId != nil || isNewContractor)
    }

    func submitReview() async -> Int? {
        guard let result = extractionResult else { return nil }

        let extracted = result.extracted

        let submission = APIClient.ReviewSubmission(
            contractorId: isNewContractor ? nil : selectedContractorId,
            newContractor: isNewContractor ? APIClient.NewContractor(
                name: extracted.contractorName,
                businessName: extracted.businessName,
                category: newContractorCategory.rawValue,
                phone: extracted.contractorPhone,
                email: extracted.contractorEmail,
                city: nil,
                state: nil,
                zipCode: extracted.zipCode
            ) : nil,
            invoiceData: APIClient.InvoiceData(
                totalAmount: extracted.totalAmount,
                currency: extracted.currency,
                laborCost: extracted.laborCost,
                materialsCost: extracted.materialsCost,
                hourlyRate: extracted.hourlyRate,
                projectDuration: extracted.projectDuration,
                invoiceDate: extracted.invoiceDate,
                description: extracted.description,
                zipCode: extracted.zipCode,
                lineItems: extracted.lineItems
            ),
            documentHash: result.documentHash,
            ratings: APIClient.Ratings(
                quality: ratingQuality,
                communication: ratingCommunication,
                timeliness: ratingTimeliness,
                value: ratingValue
            ),
            title: reviewTitle,
            body: reviewBody,
            workType: extracted.description,
            estimatedBreakdown: result.estimatedBreakdown
        )

        do {
            let response = try await APIClient.shared.submitReview(submission)
            flowStep = .submitted
            return response.creditBalance
        } catch {
            errorMessage = error.localizedDescription
            return nil
        }
    }

    func reset() {
        capturedImage = nil
        extractionResult = nil
        isExtracting = false
        errorMessage = nil
        selectedContractorId = nil
        isNewContractor = false
        newContractorCategory = .general
        ratingQuality = 0
        ratingCommunication = 0
        ratingTimeliness = 0
        ratingValue = 0
        reviewTitle = ""
        reviewBody = ""
        flowStep = .camera
    }
}
