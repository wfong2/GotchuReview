import SwiftUI

struct ContractorCardView: View {
    let contractor: Contractor

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(contractor.name)
                    .font(.headline)
                if contractor.reviewCount > 0 {
                    Image(systemName: "checkmark.seal.fill")
                        .foregroundColor(.green)
                        .font(.caption)
                }
                Spacer()
            }

            HStack(spacing: 4) {
                StarRatingView(rating: contractor.ratingOverall)
                Text(String(format: "%.1f", contractor.ratingOverall))
                    .font(.subheadline)
                    .foregroundColor(.secondary)
                Text("·")
                    .foregroundColor(.secondary)
                Text("\(contractor.reviewCount) \(contractor.reviewCount == 1 ? NSLocalizedString("review.singular", comment: "") : NSLocalizedString("review.plural", comment: ""))")
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }

            if !contractor.priceRangeDisplay.isEmpty {
                HStack {
                    Text(NSLocalizedString("contractor.typical", comment: ""))
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                    Text("$\(Int(contractor.pricingMedian))")
                        .font(.subheadline)
                        .fontWeight(.medium)
                    Text("·")
                        .foregroundColor(.secondary)
                    Text(contractor.zipCode)
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
            }
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(12)
        .shadow(color: .black.opacity(0.05), radius: 4, y: 2)
    }
}
