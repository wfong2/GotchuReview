import SwiftUI

struct ContractorDetailView: View {
    let contractorId: String
    @StateObject private var viewModel = ContractorDetailViewModel()

    var body: some View {
        ScrollView {
            if viewModel.isLoading {
                ProgressView()
                    .padding(.top, 60)
            } else if let contractor = viewModel.contractor {
                VStack(alignment: .leading, spacing: 20) {
                    // Header
                    VStack(alignment: .leading, spacing: 8) {
                        Text(contractor.businessName.isEmpty ? contractor.name : contractor.businessName)
                            .font(.title2)
                            .fontWeight(.bold)
                        Text("\(contractor.category.capitalized) · \(contractor.locationDisplay) \(contractor.zipCode)")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }

                    // Ratings
                    VStack(alignment: .leading, spacing: 8) {
                        HStack {
                            StarRatingView(rating: contractor.ratingOverall, starSize: 18)
                            Text(String(format: "%.1f", contractor.ratingOverall))
                                .font(.title3)
                                .fontWeight(.semibold)
                            Text(NSLocalizedString("contractor.overall", comment: ""))
                                .foregroundColor(.secondary)
                        }

                        RatingRow(label: NSLocalizedString("rating.quality", comment: ""), value: contractor.ratingQuality)
                        RatingRow(label: NSLocalizedString("rating.communication", comment: ""), value: contractor.ratingCommunication)
                        RatingRow(label: NSLocalizedString("rating.timeliness", comment: ""), value: contractor.ratingTimeliness)
                        RatingRow(label: NSLocalizedString("rating.value", comment: ""), value: contractor.ratingValue)

                        Text("\(contractor.reviewCount) \(NSLocalizedString("contractor.verifiedReviews", comment: ""))")
                            .font(.subheadline)
                            .foregroundColor(.secondary)
                    }

                    Divider()

                    // Pricing
                    if contractor.pricingMedian > 0 {
                        VStack(alignment: .leading, spacing: 8) {
                            Text(NSLocalizedString("contractor.pricing", comment: ""))
                                .font(.headline)

                            HStack {
                                Text(NSLocalizedString("contractor.typicalJob", comment: ""))
                                    .foregroundColor(.secondary)
                                Spacer()
                                Text("$\(Int(contractor.pricingMedian))")
                                    .fontWeight(.semibold)
                            }

                            if contractor.pricingLaborMedian > 0 {
                                HStack {
                                    Text(NSLocalizedString("contractor.labor", comment: ""))
                                        .foregroundColor(.secondary)
                                    Spacer()
                                    Text("$\(Int(contractor.pricingLaborMedian)) (\(Int(contractor.pricingLaborRatio * 100))%)")
                                }

                                HStack {
                                    Text(NSLocalizedString("contractor.materials", comment: ""))
                                        .foregroundColor(.secondary)
                                    Spacer()
                                    Text("$\(Int(contractor.pricingMaterialsMedian)) (\(Int((1 - contractor.pricingLaborRatio) * 100))%)")
                                }
                            }

                            HStack {
                                Text(NSLocalizedString("contractor.range", comment: ""))
                                    .foregroundColor(.secondary)
                                Spacer()
                                Text(contractor.priceRangeDisplay)
                            }

                            Text(String(format: NSLocalizedString("contractor.basedOn", comment: ""), contractor.reviewCount, contractor.zipCode))
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }

                        Divider()
                    }

                    // Reviews
                    VStack(alignment: .leading, spacing: 12) {
                        Text(NSLocalizedString("contractor.reviews", comment: ""))
                            .font(.headline)

                        ForEach(viewModel.reviews) { review in
                            ReviewCardView(review: review)
                        }
                    }
                }
                .padding()
            } else if let error = viewModel.errorMessage {
                Text(error)
                    .foregroundColor(.red)
                    .padding()
            }
        }
        .navigationBarTitleDisplayMode(.inline)
        .task {
            await viewModel.load(contractorId: contractorId)
        }
    }
}

struct RatingRow: View {
    let label: String
    let value: Double

    var body: some View {
        HStack {
            Text(label)
                .frame(width: 130, alignment: .leading)
                .font(.subheadline)
            StarRatingView(rating: value, starSize: 12)
            Text(String(format: "%.1f", value))
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
    }
}

struct ReviewCardView: View {
    let review: Review

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                StarRatingView(rating: review.overallRating)
                Text(review.title)
                    .font(.subheadline)
                    .fontWeight(.medium)
                if review.isVerified {
                    Image(systemName: "checkmark.seal.fill")
                        .foregroundColor(.green)
                        .font(.caption)
                }
            }

            Text(review.workType)
                .font(.caption)
                .foregroundColor(.secondary)

            if let invoice = review.invoice {
                HStack {
                    Text("$\(Int(invoice.totalAmount))")
                        .font(.subheadline)
                        .fontWeight(.medium)
                    if let date = invoice.invoiceDate {
                        Text("·")
                            .foregroundColor(.secondary)
                        Text(date.prefix(7).replacingOccurrences(of: "-", with: "/"))
                            .font(.caption)
                            .foregroundColor(.secondary)
                    }
                }
            }

            Text(review.body)
                .font(.subheadline)
                .lineLimit(3)

            if let invoice = review.invoice, invoice.source == "estimated_same_contractor" {
                Label(
                    NSLocalizedString("review.aiEstimated", comment: ""),
                    systemImage: "exclamationmark.triangle.fill"
                )
                .font(.caption2)
                .foregroundColor(.orange)
            }
        }
        .padding()
        .background(Color(.systemGray6))
        .cornerRadius(12)
    }
}
