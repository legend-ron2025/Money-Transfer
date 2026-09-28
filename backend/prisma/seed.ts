import { PrismaClient } from '@prisma/client';

const prisma = new PrismaClient();

async function main() {
  console.log('🌱 Seeding database...');

  // ── Financial Institutions ──────────────────────────────────────────────────
  const institutions = [
    { id: 'fi-hdfc',    name: 'HDFC Bank',             logo: 'hdfc',    type: 'BANK',   country: 'IN' },
    { id: 'fi-icici',   name: 'ICICI Bank',             logo: 'icici',   type: 'BANK',   country: 'IN' },
    { id: 'fi-sbi',     name: 'State Bank of India',    logo: 'sbi',     type: 'BANK',   country: 'IN' },
    { id: 'fi-axis',    name: 'Axis Bank',              logo: 'axis',    type: 'BANK',   country: 'IN' },
    { id: 'fi-kotak',   name: 'Kotak Mahindra Bank',    logo: 'kotak',   type: 'BANK',   country: 'IN' },
    { id: 'fi-yes',     name: 'Yes Bank',               logo: 'yesbank', type: 'BANK',   country: 'IN' },
    { id: 'fi-idfc',    name: 'IDFC First Bank',        logo: 'idfc',    type: 'BANK',   country: 'IN' },
    { id: 'fi-pnb',     name: 'Punjab National Bank',   logo: 'pnb',     type: 'BANK',   country: 'IN' },
    { id: 'fi-bob',     name: 'Bank of Baroda',         logo: 'bob',     type: 'BANK',   country: 'IN' },
    { id: 'fi-federal', name: 'Federal Bank',           logo: 'federal', type: 'BANK',   country: 'IN' },
    { id: 'fi-paytm',   name: 'Paytm Payments Bank',    logo: 'paytm',   type: 'WALLET', country: 'IN' },
    { id: 'fi-cash',    name: 'Cash',                   logo: 'cash',    type: 'CASH',   country: 'IN' },
    { id: 'fi-other',   name: 'Other',                  logo: 'other',   type: 'OTHER',  country: 'IN' },
  ];

  for (const inst of institutions) {
    await prisma.financialInstitution.upsert({
      where: { id: inst.id },
      update: inst,
      create: inst,
    });
  }
  console.log(`✅ Seeded ${institutions.length} financial institutions`);

  // ── Parent Categories ───────────────────────────────────────────────────────
  const parents = [
    // Income
    { id: 'cat-salary',      name: 'Salary',       icon: 'briefcase',       type: 'INCOME',   color: '#4CAF50', sortOrder: 1 },
    { id: 'cat-freelance',   name: 'Freelance',    icon: 'laptop',          type: 'INCOME',   color: '#4CAF50', sortOrder: 2 },
    { id: 'cat-business',    name: 'Business',     icon: 'store',           type: 'INCOME',   color: '#4CAF50', sortOrder: 3 },
    { id: 'cat-interest',    name: 'Interest',     icon: 'percent',         type: 'INCOME',   color: '#4CAF50', sortOrder: 4 },
    { id: 'cat-inv-inc',     name: 'Investment',   icon: 'trending_up',     type: 'INCOME',   color: '#4CAF50', sortOrder: 5 },
    { id: 'cat-cashback',    name: 'Cashback',     icon: 'card_giftcard',   type: 'INCOME',   color: '#4CAF50', sortOrder: 6 },
    { id: 'cat-other-inc',   name: 'Other Income', icon: 'add_circle',      type: 'INCOME',   color: '#4CAF50', sortOrder: 99 },
    // Expense
    { id: 'cat-food',        name: 'Food',         icon: 'restaurant',      type: 'EXPENSE',  color: '#FF9800', sortOrder: 1 },
    { id: 'cat-transport',   name: 'Transport',    icon: 'directions_car',  type: 'EXPENSE',  color: '#2196F3', sortOrder: 2 },
    { id: 'cat-shopping',    name: 'Shopping',     icon: 'shopping_bag',    type: 'EXPENSE',  color: '#9C27B0', sortOrder: 3 },
    { id: 'cat-bills',       name: 'Bills',        icon: 'receipt_long',    type: 'EXPENSE',  color: '#F44336', sortOrder: 4 },
    { id: 'cat-entertain',   name: 'Entertainment',icon: 'movie',           type: 'EXPENSE',  color: '#E91E63', sortOrder: 5 },
    { id: 'cat-health',      name: 'Health',       icon: 'local_hospital',  type: 'EXPENSE',  color: '#00BCD4', sortOrder: 6 },
    { id: 'cat-education',   name: 'Education',    icon: 'school',          type: 'EXPENSE',  color: '#795548', sortOrder: 7 },
    { id: 'cat-travel',      name: 'Travel',       icon: 'flight',          type: 'EXPENSE',  color: '#607D8B', sortOrder: 8 },
    { id: 'cat-insurance',   name: 'Insurance',    icon: 'security',        type: 'EXPENSE',  color: '#3F51B5', sortOrder: 9 },
    { id: 'cat-investments', name: 'Investments',  icon: 'trending_up',     type: 'EXPENSE',  color: '#673AB7', sortOrder: 10 },
    { id: 'cat-emi',         name: 'EMI/Loans',    icon: 'account_balance', type: 'EXPENSE',  color: '#FF5722', sortOrder: 11 },
    { id: 'cat-other',       name: 'Other',        icon: 'more_horiz',      type: 'EXPENSE',  color: '#9E9E9E', sortOrder: 99 },
    // Transfer
    { id: 'cat-transfer',    name: 'Transfer',     icon: 'swap_horiz',      type: 'TRANSFER', color: '#607D8B', sortOrder: 1 },
  ];

  for (const cat of parents) {
    await prisma.category.upsert({
      where: { id: cat.id },
      update: { ...cat, isSystem: true },
      create: { ...cat, isSystem: true, userId: null },
    });
  }

  // ── Child Categories ────────────────────────────────────────────────────────
  const children = [
    { id: 'cat-restaurants',     name: 'Restaurants',      parentId: 'cat-food',      icon: 'restaurant',      type: 'EXPENSE', sortOrder: 1 },
    { id: 'cat-food-delivery',   name: 'Food Delivery',    parentId: 'cat-food',      icon: 'delivery_dining', type: 'EXPENSE', sortOrder: 2 },
    { id: 'cat-groceries',       name: 'Groceries',        parentId: 'cat-food',      icon: 'shopping_cart',   type: 'EXPENSE', sortOrder: 3 },
    { id: 'cat-snacks',          name: 'Snacks',           parentId: 'cat-food',      icon: 'cookie',          type: 'EXPENSE', sortOrder: 4 },
    { id: 'cat-fuel',            name: 'Fuel',             parentId: 'cat-transport', icon: 'local_gas_station',type: 'EXPENSE', sortOrder: 1 },
    { id: 'cat-cab',             name: 'Cab/Ride Share',   parentId: 'cat-transport', icon: 'local_taxi',      type: 'EXPENSE', sortOrder: 2 },
    { id: 'cat-pub-transport',   name: 'Public Transport', parentId: 'cat-transport', icon: 'directions_bus',  type: 'EXPENSE', sortOrder: 3 },
    { id: 'cat-parking',         name: 'Parking',          parentId: 'cat-transport', icon: 'local_parking',   type: 'EXPENSE', sortOrder: 4 },
    { id: 'cat-clothing',        name: 'Clothing',         parentId: 'cat-shopping',  icon: 'checkroom',       type: 'EXPENSE', sortOrder: 1 },
    { id: 'cat-electronics',     name: 'Electronics',      parentId: 'cat-shopping',  icon: 'devices',         type: 'EXPENSE', sortOrder: 2 },
    { id: 'cat-beauty',          name: 'Beauty',           parentId: 'cat-shopping',  icon: 'face',            type: 'EXPENSE', sortOrder: 3 },
    { id: 'cat-electricity',     name: 'Electricity',      parentId: 'cat-bills',     icon: 'bolt',            type: 'EXPENSE', sortOrder: 1 },
    { id: 'cat-mobile',          name: 'Mobile',           parentId: 'cat-bills',     icon: 'phone_android',   type: 'EXPENSE', sortOrder: 2 },
    { id: 'cat-internet',        name: 'Internet',         parentId: 'cat-bills',     icon: 'wifi',            type: 'EXPENSE', sortOrder: 3 },
    { id: 'cat-water',           name: 'Water',            parentId: 'cat-bills',     icon: 'water_drop',      type: 'EXPENSE', sortOrder: 4 },
    { id: 'cat-streaming',       name: 'Streaming',        parentId: 'cat-entertain', icon: 'tv',              type: 'EXPENSE', sortOrder: 1 },
    { id: 'cat-gaming',          name: 'Gaming',           parentId: 'cat-entertain', icon: 'sports_esports',  type: 'EXPENSE', sortOrder: 2 },
    { id: 'cat-movies',          name: 'Movies',           parentId: 'cat-entertain', icon: 'movie',           type: 'EXPENSE', sortOrder: 3 },
    { id: 'cat-flights',         name: 'Flights',          parentId: 'cat-travel',    icon: 'flight',          type: 'EXPENSE', sortOrder: 1 },
    { id: 'cat-hotels',          name: 'Hotels',           parentId: 'cat-travel',    icon: 'hotel',           type: 'EXPENSE', sortOrder: 2 },
  ];

  for (const cat of children) {
    await prisma.category.upsert({
      where: { id: cat.id },
      update: { ...cat, isSystem: true, color: null },
      create: { ...cat, isSystem: true, color: null, userId: null },
    });
  }
  console.log(`✅ Seeded ${parents.length + children.length} categories`);

  // ── Merchant Lookup ─────────────────────────────────────────────────────────
  const merchants = [
    { keyword: 'SWIGGY',       merchantName: 'Swiggy',             categoryName: 'Food Delivery' },
    { keyword: 'ZOMATO',       merchantName: 'Zomato',             categoryName: 'Food Delivery' },
    { keyword: 'BLINKIT',      merchantName: 'Blinkit',            categoryName: 'Groceries'     },
    { keyword: 'BIGBASKET',    merchantName: 'BigBasket',          categoryName: 'Groceries'     },
    { keyword: 'ZEPTO',        merchantName: 'Zepto',              categoryName: 'Groceries'     },
    { keyword: 'AMAZON',       merchantName: 'Amazon',             categoryName: 'Shopping'      },
    { keyword: 'FLIPKART',     merchantName: 'Flipkart',           categoryName: 'Shopping'      },
    { keyword: 'MYNTRA',       merchantName: 'Myntra',             categoryName: 'Clothing'      },
    { keyword: 'NETFLIX',      merchantName: 'Netflix',            categoryName: 'Streaming'     },
    { keyword: 'HOTSTAR',      merchantName: 'Disney+ Hotstar',    categoryName: 'Streaming'     },
    { keyword: 'SPOTIFY',      merchantName: 'Spotify',            categoryName: 'Streaming'     },
    { keyword: 'OLA',          merchantName: 'Ola',                categoryName: 'Cab/Ride Share'},
    { keyword: 'UBER',         merchantName: 'Uber',               categoryName: 'Cab/Ride Share'},
    { keyword: 'RAPIDO',       merchantName: 'Rapido',             categoryName: 'Cab/Ride Share'},
    { keyword: 'IRCTC',        merchantName: 'IRCTC',              categoryName: 'Public Transport'},
    { keyword: 'INDIGO',       merchantName: 'IndiGo',             categoryName: 'Flights'       },
    { keyword: 'MAKEMYTRIP',   merchantName: 'MakeMyTrip',         categoryName: 'Travel'        },
    { keyword: 'BOOKMYSHOW',   merchantName: 'BookMyShow',         categoryName: 'Movies'        },
    { keyword: 'PHARMEASY',    merchantName: 'PharmEasy',          categoryName: 'Health'        },
    { keyword: 'ZERODHA',      merchantName: 'Zerodha',            categoryName: 'Investments'   },
    { keyword: 'GROWW',        merchantName: 'Groww',              categoryName: 'Investments'   },
    { keyword: 'JIO',          merchantName: 'Reliance Jio',       categoryName: 'Mobile'        },
    { keyword: 'AIRTEL',       merchantName: 'Airtel',             categoryName: 'Mobile'        },
    { keyword: 'LIC',          merchantName: 'LIC',                categoryName: 'Insurance'     },
    { keyword: 'DOMINOS',      merchantName: "Domino's",           categoryName: 'Restaurants'   },
    { keyword: 'MCDONALDS',    merchantName: "McDonald's",         categoryName: 'Restaurants'   },
    { keyword: 'KFC',          merchantName: 'KFC',                categoryName: 'Restaurants'   },
    { keyword: 'STARBUCKS',    merchantName: 'Starbucks',          categoryName: 'Restaurants'   },
    { keyword: 'IOCL',         merchantName: 'Indian Oil',         categoryName: 'Fuel'          },
    { keyword: 'BPCL',         merchantName: 'BPCL',               categoryName: 'Fuel'          },
  ];

  for (const m of merchants) {
    await prisma.merchantLookup.upsert({
      where: { keyword: m.keyword },
      update: m,
      create: m,
    });
  }
  console.log(`✅ Seeded ${merchants.length} merchant lookups`);
  console.log('🎉 Database seeded successfully!');
}

main()
  .catch(console.error)
  .finally(() => prisma.$disconnect());
