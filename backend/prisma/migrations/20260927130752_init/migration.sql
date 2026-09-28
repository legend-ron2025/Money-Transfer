-- CreateTable
CREATE TABLE "users" (
    "id" TEXT NOT NULL,
    "email" TEXT NOT NULL,
    "phone" TEXT,
    "passwordHash" TEXT NOT NULL,
    "name" TEXT,
    "avatar" TEXT,
    "isEmailVerified" BOOLEAN NOT NULL DEFAULT false,
    "isPhoneVerified" BOOLEAN NOT NULL DEFAULT false,
    "currency" TEXT NOT NULL DEFAULT 'INR',
    "language" TEXT NOT NULL DEFAULT 'en',
    "theme" TEXT NOT NULL DEFAULT 'system',
    "notificationsEnabled" BOOLEAN NOT NULL DEFAULT true,
    "biometricEnabled" BOOLEAN NOT NULL DEFAULT false,
    "autoSyncEnabled" BOOLEAN NOT NULL DEFAULT true,
    "syncFrequency" TEXT NOT NULL DEFAULT '15min',
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "users_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "user_devices" (
    "id" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "deviceId" TEXT NOT NULL,
    "platform" TEXT NOT NULL,
    "lastSeen" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "isTrusted" BOOLEAN NOT NULL DEFAULT false,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "user_devices_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "financial_institutions" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "logo" TEXT,
    "type" TEXT NOT NULL,
    "country" TEXT NOT NULL,
    "isActive" BOOLEAN NOT NULL DEFAULT true,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "financial_institutions_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "accounts" (
    "id" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "institutionId" TEXT NOT NULL,
    "accountType" TEXT NOT NULL,
    "accountSubType" TEXT,
    "maskedNumber" TEXT NOT NULL,
    "currency" TEXT NOT NULL DEFAULT 'INR',
    "balance" DECIMAL(19,4) NOT NULL,
    "availableBalance" DECIMAL(19,4),
    "creditLimit" DECIMAL(19,4),
    "isActive" BOOLEAN NOT NULL DEFAULT true,
    "lastSyncedAt" TIMESTAMP(3),
    "lastSuccessfulSync" TIMESTAMP(3),
    "syncStatus" TEXT NOT NULL DEFAULT 'PENDING',
    "syncError" TEXT,
    "consentId" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "accounts_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "transactions" (
    "id" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "accountId" TEXT NOT NULL,
    "externalId" TEXT,
    "amount" DECIMAL(19,4) NOT NULL,
    "currency" TEXT NOT NULL DEFAULT 'INR',
    "type" TEXT NOT NULL,
    "status" TEXT NOT NULL DEFAULT 'PENDING',
    "description" TEXT NOT NULL,
    "merchantName" TEXT,
    "merchantLogo" TEXT,
    "transactionDate" TIMESTAMP(3) NOT NULL,
    "postedDate" TIMESTAMP(3),
    "categoryId" TEXT,
    "subcategory" TEXT,
    "source" TEXT NOT NULL,
    "paymentChannel" TEXT,
    "upiReference" TEXT,
    "bankReference" TEXT,
    "isRecurring" BOOLEAN NOT NULL DEFAULT false,
    "isTransfer" BOOLEAN NOT NULL DEFAULT false,
    "transferPairId" TEXT,
    "confidence" DOUBLE PRECISION NOT NULL DEFAULT 0,
    "notes" TEXT,
    "tags" TEXT[],
    "locationLatitude" DOUBLE PRECISION,
    "locationLongitude" DOUBLE PRECISION,
    "locationAddress" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "transactions_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "categories" (
    "id" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "parentId" TEXT,
    "userId" TEXT,
    "icon" TEXT NOT NULL,
    "type" TEXT NOT NULL,
    "color" TEXT,
    "isSystem" BOOLEAN NOT NULL DEFAULT true,
    "sortOrder" INTEGER NOT NULL DEFAULT 0,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "categories_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "budgets" (
    "id" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "categoryId" TEXT NOT NULL,
    "amount" DECIMAL(19,4) NOT NULL,
    "period" TEXT NOT NULL,
    "startDate" TIMESTAMP(3) NOT NULL,
    "endDate" TIMESTAMP(3),
    "spentAmount" DECIMAL(19,4) NOT NULL DEFAULT 0,
    "alertThreshold" DOUBLE PRECISION NOT NULL DEFAULT 0.8,
    "isActive" BOOLEAN NOT NULL DEFAULT true,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "budgets_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "goals" (
    "id" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "name" TEXT NOT NULL,
    "description" TEXT,
    "targetAmount" DECIMAL(19,4) NOT NULL,
    "currentAmount" DECIMAL(19,4) NOT NULL DEFAULT 0,
    "currency" TEXT NOT NULL DEFAULT 'INR',
    "targetDate" TIMESTAMP(3) NOT NULL,
    "monthlyContribution" DECIMAL(19,4),
    "status" TEXT NOT NULL DEFAULT 'ACTIVE',
    "icon" TEXT,
    "color" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "goals_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "consents" (
    "id" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "providerId" TEXT NOT NULL,
    "providerName" TEXT NOT NULL,
    "providerLogo" TEXT,
    "consentId" TEXT NOT NULL,
    "status" TEXT NOT NULL DEFAULT 'PENDING',
    "grantedAt" TIMESTAMP(3) NOT NULL,
    "expiresAt" TIMESTAMP(3),
    "revokedAt" TIMESTAMP(3),
    "accounts" JSONB NOT NULL,
    "permissions" TEXT[],
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "updatedAt" TIMESTAMP(3) NOT NULL,

    CONSTRAINT "consents_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "notifications" (
    "id" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "type" TEXT NOT NULL,
    "title" TEXT NOT NULL,
    "body" TEXT NOT NULL,
    "data" JSONB,
    "isRead" BOOLEAN NOT NULL DEFAULT false,
    "priority" TEXT NOT NULL DEFAULT 'NORMAL',
    "actionUrl" TEXT,
    "imageUrl" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,
    "readAt" TIMESTAMP(3),

    CONSTRAINT "notifications_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "analytics_cache" (
    "id" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "cacheKey" TEXT NOT NULL,
    "data" JSONB NOT NULL,
    "period" TEXT NOT NULL,
    "expiresAt" TIMESTAMP(3) NOT NULL,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "analytics_cache_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "deleted_records" (
    "id" TEXT NOT NULL,
    "userId" TEXT NOT NULL,
    "entityType" TEXT NOT NULL,
    "entityId" TEXT NOT NULL,
    "deletedAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "deleted_records_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "audit_logs" (
    "id" TEXT NOT NULL,
    "userId" TEXT,
    "action" TEXT NOT NULL,
    "entityType" TEXT,
    "entityId" TEXT,
    "ipAddress" TEXT,
    "metadata" JSONB,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "audit_logs_pkey" PRIMARY KEY ("id")
);

-- CreateTable
CREATE TABLE "merchant_lookup" (
    "id" TEXT NOT NULL,
    "keyword" TEXT NOT NULL,
    "merchantName" TEXT NOT NULL,
    "categoryName" TEXT NOT NULL,
    "logo" TEXT,
    "paymentChannel" TEXT,
    "createdAt" TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT "merchant_lookup_pkey" PRIMARY KEY ("id")
);

-- CreateIndex
CREATE UNIQUE INDEX "users_email_key" ON "users"("email");

-- CreateIndex
CREATE UNIQUE INDEX "users_phone_key" ON "users"("phone");

-- CreateIndex
CREATE UNIQUE INDEX "user_devices_deviceId_key" ON "user_devices"("deviceId");

-- CreateIndex
CREATE INDEX "accounts_userId_idx" ON "accounts"("userId");

-- CreateIndex
CREATE INDEX "accounts_institutionId_idx" ON "accounts"("institutionId");

-- CreateIndex
CREATE INDEX "accounts_consentId_idx" ON "accounts"("consentId");

-- CreateIndex
CREATE INDEX "transactions_userId_idx" ON "transactions"("userId");

-- CreateIndex
CREATE INDEX "transactions_accountId_idx" ON "transactions"("accountId");

-- CreateIndex
CREATE INDEX "transactions_categoryId_idx" ON "transactions"("categoryId");

-- CreateIndex
CREATE INDEX "transactions_transactionDate_idx" ON "transactions"("transactionDate");

-- CreateIndex
CREATE INDEX "transactions_externalId_idx" ON "transactions"("externalId");

-- CreateIndex
CREATE INDEX "transactions_upiReference_idx" ON "transactions"("upiReference");

-- CreateIndex
CREATE INDEX "transactions_isTransfer_idx" ON "transactions"("isTransfer");

-- CreateIndex
CREATE INDEX "transactions_isRecurring_idx" ON "transactions"("isRecurring");

-- CreateIndex
CREATE INDEX "transactions_transferPairId_idx" ON "transactions"("transferPairId");

-- CreateIndex
CREATE INDEX "categories_parentId_idx" ON "categories"("parentId");

-- CreateIndex
CREATE INDEX "categories_userId_idx" ON "categories"("userId");

-- CreateIndex
CREATE INDEX "categories_type_idx" ON "categories"("type");

-- CreateIndex
CREATE INDEX "categories_sortOrder_idx" ON "categories"("sortOrder");

-- CreateIndex
CREATE INDEX "budgets_userId_idx" ON "budgets"("userId");

-- CreateIndex
CREATE INDEX "budgets_categoryId_idx" ON "budgets"("categoryId");

-- CreateIndex
CREATE INDEX "budgets_period_idx" ON "budgets"("period");

-- CreateIndex
CREATE INDEX "budgets_startDate_endDate_idx" ON "budgets"("startDate", "endDate");

-- CreateIndex
CREATE INDEX "goals_userId_idx" ON "goals"("userId");

-- CreateIndex
CREATE INDEX "goals_status_idx" ON "goals"("status");

-- CreateIndex
CREATE INDEX "goals_targetDate_idx" ON "goals"("targetDate");

-- CreateIndex
CREATE UNIQUE INDEX "consents_consentId_key" ON "consents"("consentId");

-- CreateIndex
CREATE INDEX "consents_userId_idx" ON "consents"("userId");

-- CreateIndex
CREATE INDEX "consents_providerId_idx" ON "consents"("providerId");

-- CreateIndex
CREATE INDEX "consents_consentId_idx" ON "consents"("consentId");

-- CreateIndex
CREATE INDEX "consents_status_idx" ON "consents"("status");

-- CreateIndex
CREATE INDEX "notifications_userId_idx" ON "notifications"("userId");

-- CreateIndex
CREATE INDEX "notifications_isRead_idx" ON "notifications"("isRead");

-- CreateIndex
CREATE INDEX "notifications_createdAt_idx" ON "notifications"("createdAt");

-- CreateIndex
CREATE INDEX "notifications_type_idx" ON "notifications"("type");

-- CreateIndex
CREATE INDEX "analytics_cache_expiresAt_idx" ON "analytics_cache"("expiresAt");

-- CreateIndex
CREATE UNIQUE INDEX "analytics_cache_userId_cacheKey_key" ON "analytics_cache"("userId", "cacheKey");

-- CreateIndex
CREATE INDEX "deleted_records_userId_idx" ON "deleted_records"("userId");

-- CreateIndex
CREATE INDEX "deleted_records_entityType_idx" ON "deleted_records"("entityType");

-- CreateIndex
CREATE INDEX "deleted_records_deletedAt_idx" ON "deleted_records"("deletedAt");

-- CreateIndex
CREATE INDEX "audit_logs_userId_idx" ON "audit_logs"("userId");

-- CreateIndex
CREATE INDEX "audit_logs_action_idx" ON "audit_logs"("action");

-- CreateIndex
CREATE INDEX "audit_logs_createdAt_idx" ON "audit_logs"("createdAt");

-- CreateIndex
CREATE UNIQUE INDEX "merchant_lookup_keyword_key" ON "merchant_lookup"("keyword");

-- CreateIndex
CREATE INDEX "merchant_lookup_keyword_idx" ON "merchant_lookup"("keyword");

-- AddForeignKey
ALTER TABLE "user_devices" ADD CONSTRAINT "user_devices_userId_fkey" FOREIGN KEY ("userId") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "accounts" ADD CONSTRAINT "accounts_userId_fkey" FOREIGN KEY ("userId") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "accounts" ADD CONSTRAINT "accounts_institutionId_fkey" FOREIGN KEY ("institutionId") REFERENCES "financial_institutions"("id") ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "transactions" ADD CONSTRAINT "transactions_userId_fkey" FOREIGN KEY ("userId") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "transactions" ADD CONSTRAINT "transactions_accountId_fkey" FOREIGN KEY ("accountId") REFERENCES "accounts"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "transactions" ADD CONSTRAINT "transactions_categoryId_fkey" FOREIGN KEY ("categoryId") REFERENCES "categories"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "categories" ADD CONSTRAINT "categories_parentId_fkey" FOREIGN KEY ("parentId") REFERENCES "categories"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "categories" ADD CONSTRAINT "categories_userId_fkey" FOREIGN KEY ("userId") REFERENCES "users"("id") ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "budgets" ADD CONSTRAINT "budgets_userId_fkey" FOREIGN KEY ("userId") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "budgets" ADD CONSTRAINT "budgets_categoryId_fkey" FOREIGN KEY ("categoryId") REFERENCES "categories"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "goals" ADD CONSTRAINT "goals_userId_fkey" FOREIGN KEY ("userId") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "consents" ADD CONSTRAINT "consents_userId_fkey" FOREIGN KEY ("userId") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "notifications" ADD CONSTRAINT "notifications_userId_fkey" FOREIGN KEY ("userId") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "analytics_cache" ADD CONSTRAINT "analytics_cache_userId_fkey" FOREIGN KEY ("userId") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "deleted_records" ADD CONSTRAINT "deleted_records_userId_fkey" FOREIGN KEY ("userId") REFERENCES "users"("id") ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE "audit_logs" ADD CONSTRAINT "audit_logs_userId_fkey" FOREIGN KEY ("userId") REFERENCES "users"("id") ON DELETE SET NULL ON UPDATE CASCADE;
