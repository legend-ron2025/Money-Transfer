package com.moneytracker.domain.usecase

import com.moneytracker.domain.model.Category
import com.moneytracker.domain.repository.CategoryRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Maybe
import javax.inject.Inject

class GetCategoriesByTypeUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(type: Category.CategoryType): Flowable<List<Category>> {
        return categoryRepository.getCategoriesByType(type)
    }
}

class GetParentCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(type: Category.CategoryType): Flowable<List<Category>> {
        return categoryRepository.getParentCategoriesByType(type)
    }
}

class GetChildCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(parentId: String): Flowable<List<Category>> {
        return categoryRepository.getChildCategories(parentId)
    }
}

class GetAllCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flowable<List<Category>> {
        return categoryRepository.getAllCategories()
    }
}

class GetCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(categoryId: String): Maybe<Category> {
        return categoryRepository.getCategory(categoryId)
    }
}

class CreateCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(category: Category): Completable {
        return categoryRepository.insertCategory(category)
    }
}

class UpdateCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(category: Category): Completable {
        return categoryRepository.updateCategory(category)
    }
}

class DeleteCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(categoryId: String): Completable {
        return categoryRepository.deleteCustomCategory(categoryId)
    }
}

class InitializeDefaultCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Completable {
        return Completable.fromAction {
            val defaultCategories = DefaultCategories.getAll()
            categoryRepository.insertCategories(defaultCategories)
        }
    }
}

object DefaultCategories {
    fun getAll(): List<Category> {
        val incomeCategories = listOf(
            Category("income_salary", "Salary", null, "briefcase", Category.CategoryType.INCOME, "#4CAF50", true, 1),
            Category("income_freelance", "Freelance", null, "laptop", Category.CategoryType.INCOME, "#4CAF50", true, 2),
            Category("income_business", "Business", null, "store", Category.CategoryType.INCOME, "#4CAF50", true, 3),
            Category("income_interest", "Interest", null, "percent", Category.CategoryType.INCOME, "#4CAF50", true, 4),
            Category("income_investment", "Investment", null, "trending_up", Category.CategoryType.INCOME, "#4CAF50", true, 5),
            Category("income_cashback", "Cashback", null, "card_giftcard", Category.CategoryType.INCOME, "#4CAF50", true, 6),
            Category("income_other", "Other Income", null, "add_circle", Category.CategoryType.INCOME, "#4CAF50", true, 99)
        )

        val expenseCategories = listOf(
            // Food
            Category("expense_food", "Food", null, "restaurant", Category.CategoryType.EXPENSE, "#FF9800", true, 1,
                listOf(
                    Category("expense_food_restaurants", "Restaurants", "expense_food", "restaurant", Category.CategoryType.EXPENSE, null, true, 1),
                    Category("expense_food_delivery", "Food Delivery", "expense_food", "delivery_dining", Category.CategoryType.EXPENSE, null, true, 2),
                    Category("expense_food_groceries", "Groceries", "expense_food", "shopping_cart", Category.CategoryType.EXPENSE, null, true, 3),
                    Category("expense_food_snacks", "Snacks", "expense_food", "cookie", Category.CategoryType.EXPENSE, null, true, 4)
                )
            ),
            // Transport
            Category("expense_transport", "Transport", null, "directions_car", Category.CategoryType.EXPENSE, "#2196F3", true, 2,
                listOf(
                    Category("expense_transport_fuel", "Fuel", "expense_transport", "local_gas_station", Category.CategoryType.EXPENSE, null, true, 1),
                    Category("expense_transport_cab", "Cab/Ride Share", "expense_transport", "local_taxi", Category.CategoryType.EXPENSE, null, true, 2),
                    Category("expense_transport_public", "Public Transport", "expense_transport", "directions_bus", Category.CategoryType.EXPENSE, null, true, 3),
                    Category("expense_transport_parking", "Parking", "expense_transport", "local_parking", Category.CategoryType.EXPENSE, null, true, 4)
                )
            ),
            // Shopping
            Category("expense_shopping", "Shopping", null, "shopping_bag", Category.CategoryType.EXPENSE, "#9C27B0", true, 3,
                listOf(
                    Category("expense_shopping_clothing", "Clothing", "expense_shopping", "checkroom", Category.CategoryType.EXPENSE, null, true, 1),
                    Category("expense_shopping_electronics", "Electronics", "expense_shopping", "devices", Category.CategoryType.EXPENSE, null, true, 2),
                    Category("expense_shopping_other", "Other", "expense_shopping", "category", Category.CategoryType.EXPENSE, null, true, 99)
                )
            ),
            // Bills
            Category("expense_bills", "Bills", null, "receipt_long", Category.CategoryType.EXPENSE, "#F44336", true, 4,
                listOf(
                    Category("expense_bills_electricity", "Electricity", "expense_bills", "bolt", Category.CategoryType.EXPENSE, null, true, 1),
                    Category("expense_bills_mobile", "Mobile", "expense_bills", "phone_android", Category.CategoryType.EXPENSE, null, true, 2),
                    Category("expense_bills_internet", "Internet", "expense_bills", "wifi", Category.CategoryType.EXPENSE, null, true, 3),
                    Category("expense_bills_water", "Water", "expense_bills", "water_drop", Category.CategoryType.EXPENSE, null, true, 4)
                )
            ),
            // Entertainment
            Category("expense_entertainment", "Entertainment", null, "movie", Category.CategoryType.EXPENSE, "#E91E63", true, 5,
                listOf(
                    Category("expense_entertainment_streaming", "Streaming", "expense_entertainment", "tv", Category.CategoryType.EXPENSE, null, true, 1),
                    Category("expense_entertainment_gaming", "Gaming", "expense_entertainment", "sports_esports", Category.CategoryType.EXPENSE, null, true, 2),
                    Category("expense_entertainment_movies", "Movies", "expense_entertainment", "movie", Category.CategoryType.EXPENSE, null, true, 3)
                )
            ),
            // Other main categories
            Category("expense_health", "Health", null, "local_hospital", Category.CategoryType.EXPENSE, "#00BCD4", true, 6),
            Category("expense_education", "Education", null, "school", Category.CategoryType.EXPENSE, "#795548", true, 7),
            Category("expense_travel", "Travel", null, "flight", Category.CategoryType.EXPENSE, "#607D8B", true, 8),
            Category("expense_insurance", "Insurance", null, "security", Category.CategoryType.EXPENSE, "#3F51B5", true, 9),
            Category("expense_investments", "Investments", null, "trending_up", Category.CategoryType.EXPENSE, "#673AB7", true, 10),
            Category("expense_emi", "EMI/Loans", null, "account_balance", Category.CategoryType.EXPENSE, "#FF5722", true, 11),
            Category("expense_other", "Other", null, "more_horiz", Category.CategoryType.EXPENSE, "#9E9E9E", true, 99)
        )

        return incomeCategories + expenseCategories
    }
}