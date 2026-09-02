import Foundation

enum TradeCategory: String, CaseIterable, Identifiable {
    case plumber
    case electrician
    case roofer
    case painter
    case hvac
    case general
    case landscaper
    case carpenter
    case mason
    case flooring

    var id: String { rawValue }

    var displayName: String {
        switch self {
        case .plumber: return NSLocalizedString("category.plumber", comment: "")
        case .electrician: return NSLocalizedString("category.electrician", comment: "")
        case .roofer: return NSLocalizedString("category.roofer", comment: "")
        case .painter: return NSLocalizedString("category.painter", comment: "")
        case .hvac: return NSLocalizedString("category.hvac", comment: "")
        case .general: return NSLocalizedString("category.general", comment: "")
        case .landscaper: return NSLocalizedString("category.landscaper", comment: "")
        case .carpenter: return NSLocalizedString("category.carpenter", comment: "")
        case .mason: return NSLocalizedString("category.mason", comment: "")
        case .flooring: return NSLocalizedString("category.flooring", comment: "")
        }
    }

    var icon: String {
        switch self {
        case .plumber: return "wrench.and.screwdriver.fill"
        case .electrician: return "bolt.fill"
        case .roofer: return "house.fill"
        case .painter: return "paintbrush.fill"
        case .hvac: return "fan.fill"
        case .general: return "hammer.fill"
        case .landscaper: return "leaf.fill"
        case .carpenter: return "square.and.pencil"
        case .mason: return "building.columns.fill"
        case .flooring: return "square.grid.3x3.fill"
        }
    }
}
