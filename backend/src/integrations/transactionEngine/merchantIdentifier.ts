import { distance } from 'fastest-levenshtein';

export interface MerchantInfo {
  name: string;
  category: string;
  subcategory?: string;
  logo?: string;
  channel?: string;
}

// 50+ Indian merchant keyword → info mapping
export const MERCHANT_LOOKUP: Record<string, MerchantInfo> = {
  // Food Delivery
  SWIGGY:       { name: 'Swiggy',             category: 'Food',          subcategory: 'Food Delivery', logo: 'swiggy' },
  ZOMATO:       { name: 'Zomato',             category: 'Food',          subcategory: 'Food Delivery', logo: 'zomato' },
  DUNZO:        { name: 'Dunzo',              category: 'Food',          subcategory: 'Food Delivery' },
  INSTAMART:    { name: 'Swiggy Instamart',   category: 'Food',          subcategory: 'Groceries' },
  // Groceries
  BLINKIT:      { name: 'Blinkit',            category: 'Food',          subcategory: 'Groceries',    logo: 'blinkit' },
  ZEPTO:        { name: 'Zepto',              category: 'Food',          subcategory: 'Groceries' },
  BIGBASKET:    { name: 'BigBasket',          category: 'Food',          subcategory: 'Groceries',    logo: 'bigbasket' },
  DMART:        { name: 'D-Mart',             category: 'Food',          subcategory: 'Groceries' },
  // Restaurants
  DOMINOS:      { name: "Domino's",           category: 'Food',          subcategory: 'Restaurants' },
  PIZZAHUT:     { name: 'Pizza Hut',          category: 'Food',          subcategory: 'Restaurants' },
  MCDONALDS:    { name: "McDonald's",         category: 'Food',          subcategory: 'Restaurants' },
  KFC:          { name: 'KFC',               category: 'Food',          subcategory: 'Restaurants' },
  STARBUCKS:    { name: 'Starbucks',          category: 'Food',          subcategory: 'Restaurants' },
  CHAAYOS:      { name: 'Chaayos',            category: 'Food',          subcategory: 'Restaurants' },
  HALDIRAMS:    { name: "Haldiram's",         category: 'Food',          subcategory: 'Restaurants' },
  // E-Commerce / Shopping
  AMAZON:       { name: 'Amazon',             category: 'Shopping',                                   logo: 'amazon' },
  FLIPKART:     { name: 'Flipkart',           category: 'Shopping',                                   logo: 'flipkart' },
  MYNTRA:       { name: 'Myntra',             category: 'Shopping',      subcategory: 'Clothing',     logo: 'myntra' },
  AJIO:         { name: 'AJIO',               category: 'Shopping',      subcategory: 'Clothing' },
  NYKAA:        { name: 'Nykaa',              category: 'Shopping',      subcategory: 'Beauty',       logo: 'nykaa' },
  MEESHO:       { name: 'Meesho',             category: 'Shopping',                                   logo: 'meesho' },
  CROMA:        { name: 'Croma',              category: 'Shopping',      subcategory: 'Electronics' },
  RELIANCE:     { name: 'Reliance Retail',    category: 'Shopping' },
  // Transport
  OLA:          { name: 'Ola',               category: 'Transport',     subcategory: 'Cab/Ride Share', logo: 'ola' },
  UBER:         { name: 'Uber',              category: 'Transport',     subcategory: 'Cab/Ride Share', logo: 'uber' },
  RAPIDO:       { name: 'Rapido',            category: 'Transport',     subcategory: 'Cab/Ride Share' },
  IRCTC:        { name: 'IRCTC',             category: 'Transport',     subcategory: 'Public Transport' },
  REDBUS:       { name: 'RedBus',            category: 'Transport',     subcategory: 'Public Transport' },
  IOCL:         { name: 'Indian Oil',        category: 'Transport',     subcategory: 'Fuel' },
  BPCL:         { name: 'BPCL',             category: 'Transport',     subcategory: 'Fuel' },
  HPCL:         { name: 'HPCL',             category: 'Transport',     subcategory: 'Fuel' },
  // Travel
  INDIGO:       { name: 'IndiGo',            category: 'Travel',        subcategory: 'Flights',      logo: 'indigo' },
  AIRINDIA:     { name: 'Air India',         category: 'Travel',        subcategory: 'Flights' },
  SPICEJET:     { name: 'SpiceJet',          category: 'Travel',        subcategory: 'Flights' },
  MAKEMYTRIP:   { name: 'MakeMyTrip',        category: 'Travel',                                     logo: 'makemytrip' },
  CLEARTRIP:    { name: 'Cleartrip',         category: 'Travel' },
  YATRA:        { name: 'Yatra',             category: 'Travel' },
  // Entertainment / Streaming
  NETFLIX:      { name: 'Netflix',           category: 'Entertainment', subcategory: 'Streaming',    logo: 'netflix' },
  HOTSTAR:      { name: 'Disney+ Hotstar',   category: 'Entertainment', subcategory: 'Streaming' },
  PRIMEVIDEO:   { name: 'Amazon Prime Video',category: 'Entertainment', subcategory: 'Streaming' },
  SONYLIV:      { name: 'SonyLIV',           category: 'Entertainment', subcategory: 'Streaming' },
  SPOTIFY:      { name: 'Spotify',           category: 'Entertainment', subcategory: 'Streaming',    logo: 'spotify' },
  GAANA:        { name: 'Gaana',             category: 'Entertainment', subcategory: 'Streaming' },
  BOOKMYSHOW:   { name: 'BookMyShow',        category: 'Entertainment', subcategory: 'Movies',       logo: 'bookmyshow' },
  PVR:          { name: 'PVR Cinemas',       category: 'Entertainment', subcategory: 'Movies' },
  INOX:         { name: 'INOX',             category: 'Entertainment', subcategory: 'Movies' },
  STEAM:        { name: 'Steam',             category: 'Entertainment', subcategory: 'Gaming' },
  // Bills
  JIO:          { name: 'Reliance Jio',      category: 'Bills',         subcategory: 'Mobile',       logo: 'jio' },
  AIRTEL:       { name: 'Airtel',            category: 'Bills',         subcategory: 'Mobile',       logo: 'airtel' },
  VODAFONE:     { name: 'Vi (Vodafone)',     category: 'Bills',         subcategory: 'Mobile' },
  BSNL:         { name: 'BSNL',             category: 'Bills',         subcategory: 'Mobile' },
  ACT:          { name: 'ACT Fibernet',      category: 'Bills',         subcategory: 'Internet' },
  BESCOM:       { name: 'BESCOM',           category: 'Bills',         subcategory: 'Electricity' },
  MSEDCL:       { name: 'MSEDCL',           category: 'Bills',         subcategory: 'Electricity' },
  TATAPOWER:    { name: 'Tata Power',        category: 'Bills',         subcategory: 'Electricity' },
  // Health
  PHARMEASY:    { name: 'PharmEasy',         category: 'Health',        subcategory: 'Health',       logo: 'pharmeasy' },
  NETMEDS:      { name: 'Netmeds',           category: 'Health',        subcategory: 'Health' },
  APOLLO:       { name: 'Apollo',            category: 'Health' },
  '1MG':        { name: '1mg',              category: 'Health' },
  // Insurance & Investments
  LIC:          { name: 'LIC',              category: 'Insurance',                                   logo: 'lic' },
  HDFC_LIFE:    { name: 'HDFC Life',         category: 'Insurance' },
  ZERODHA:      { name: 'Zerodha',           category: 'Investments',                                 logo: 'zerodha' },
  GROWW:        { name: 'Groww',             category: 'Investments',                                 logo: 'groww' },
  UPSTOX:       { name: 'Upstox',            category: 'Investments' },
  // Education
  BYJUS:        { name: "BYJU'S",            category: 'Education' },
  UNACADEMY:    { name: 'Unacademy',         category: 'Education' },
  UDEMY:        { name: 'Udemy',             category: 'Education' },
  // Payment channels (no category — these are just channels)
  PAYTM:        { name: 'Paytm',             category: 'Other', channel: 'WALLET' },
  PHONEPE:      { name: 'PhonePe',           category: 'Other', channel: 'UPI' },
  GPAY:         { name: 'Google Pay',        category: 'Other', channel: 'UPI' },
  GOOGLEPAY:    { name: 'Google Pay',        category: 'Other', channel: 'UPI' },
};

export interface MerchantMatch extends MerchantInfo {
  confidence: number;
  matchedKeyword: string;
}

export function identifyMerchant(
  description: string,
  extractedMerchant: string | null | undefined,
): MerchantMatch | null {
  const upperDesc = description.toUpperCase().replace(/[^A-Z0-9\s]/g, ' ');

  // 1. Exact keyword match in full description
  for (const [kw, info] of Object.entries(MERCHANT_LOOKUP)) {
    if (upperDesc.includes(kw)) {
      return { ...info, confidence: 0.95, matchedKeyword: kw };
    }
  }

  // 2. Fuzzy levenshtein on extracted UPI merchant name
  if (extractedMerchant && extractedMerchant.length >= 3) {
    const upper = extractedMerchant.toUpperCase().replace(/[^A-Z0-9\s]/g, '');
    let best: MerchantMatch | null = null;
    let bestDist = Infinity;

    for (const [kw, info] of Object.entries(MERCHANT_LOOKUP)) {
      const d = distance(upper, kw);
      const maxLen = Math.max(upper.length, kw.length);
      const threshold = Math.floor(maxLen * 0.35);
      if (d < bestDist && d <= threshold) {
        bestDist = d;
        const sim = 1 - d / maxLen;
        best = { ...info, confidence: Math.max(0.55, sim), matchedKeyword: kw };
      }
    }
    if (best) return best;

    // Return the cleaned UPI merchant with Unknown category at low confidence
    const formatted = extractedMerchant
      .split(/\s+/)
      .map(w => w.charAt(0).toUpperCase() + w.slice(1).toLowerCase())
      .join(' ');
    return {
      name: formatted,
      category: 'Other',
      confidence: 0.4,
      matchedKeyword: '',
    };
  }

  return null;
}
