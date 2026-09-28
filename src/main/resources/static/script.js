const API = "";


/* =========================
   GENERAL FUNCTIONS
========================= */

function showSection(sectionId) {

    document.querySelectorAll(".section").forEach(section => {
        section.classList.remove("active");
    });

    document.getElementById(sectionId).classList.add("active");

    if (sectionId === "dashboardSection") {
        loadDashboard();
    }

    if (sectionId === "usersSection") {
        loadUsers();
    }

    if (sectionId === "recipesSection") {
        loadRecipes();
    }

    if (sectionId === "ingredientsSection") {
        loadIngredients();
    }

    if (sectionId === "mealPlansSection") {
        loadMealPlans();
    }
}


function showToast(message) {

    const toast = document.getElementById("toast");

    toast.textContent = message;
    toast.classList.add("show");

    setTimeout(() => {
        toast.classList.remove("show");
    }, 3000);
}


function escapeHtml(value) {

    if (value === null || value === undefined) {
        return "";
    }

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}


async function getJson(url, options = {}) {

    const response = await fetch(API + url, {
        headers: {
            "Content-Type": "application/json",
            ...(options.headers || {})
        },
        ...options
    });

    if (!response.ok) {

        let errorMessage = "Request failed";

        try {

            const errorData = await response.json();

            if (errorData.message) {
                errorMessage = errorData.message;
            }

            if (errorData.messages) {
                errorMessage = Object.values(errorData.messages).join(", ");
            }

        } catch (error) {
            // Ignore JSON parsing error
        }

        throw new Error(errorMessage);
    }

    if (response.status === 204) {
        return null;
    }

    return response.json();
}


/* =========================
   DASHBOARD
========================= */

async function loadDashboard() {

    try {

        const users = await getJson("/api/users");
        const recipes = await getJson("/api/recipes");
        const ingredients = await getJson("/api/ingredients");
        const mealPlans = await getJson("/api/meal-plans");

        document.getElementById("userCount").textContent =
            users.length;

        document.getElementById("recipeCount").textContent =
            recipes.length;

        document.getElementById("ingredientCount").textContent =
            ingredients.length;

        document.getElementById("mealPlanCount").textContent =
            mealPlans.length;

    } catch (error) {

        showToast(error.message);
    }
}


/* =========================
   USER CRUD
========================= */

async function loadUsers() {

    try {

        const users = await getJson("/api/users");

        const table = document.getElementById("usersTable");

        table.innerHTML = "";

        if (users.length === 0) {

            table.innerHTML = `
                <tr>
                    <td colspan="4">No users found.</td>
                </tr>
            `;

            return;
        }

        users.forEach(user => {

            table.innerHTML += `
                <tr>

                    <td>${escapeHtml(user.id)}</td>

                    <td>${escapeHtml(user.name)}</td>

                    <td>${escapeHtml(user.email)}</td>

                    <td>

                        <div class="action-buttons">

                            <button
                                class="primary-btn"
                                onclick="editUser(${user.id})">
                                Edit
                            </button>

                            <button
                                class="danger-btn"
                                onclick="deleteUser(${user.id})">
                                Delete
                            </button>

                        </div>

                    </td>

                </tr>
            `;
        });

    } catch (error) {

        showToast(error.message);
    }
}


async function saveUser() {

    const id = document.getElementById("userId").value;

    const name =
        document.getElementById("userName").value.trim();

    const email =
        document.getElementById("userEmail").value.trim();

    if (!name || !email) {

        showToast("Please enter name and email.");
        return;
    }

    const userData = {
        name: name,
        email: email
    };

    try {

        if (id) {

            await getJson(`/api/users/${id}`, {
                method: "PUT",
                body: JSON.stringify(userData)
            });

            showToast("User updated successfully.");

        } else {

            await getJson("/api/users", {
                method: "POST",
                body: JSON.stringify(userData)
            });

            showToast("User created successfully.");
        }

        clearUserForm();
        loadUsers();
        loadDashboard();

    } catch (error) {

        showToast(error.message);
    }
}


async function editUser(id) {

    try {

        const user = await getJson(`/api/users/${id}`);

        document.getElementById("userId").value = user.id;
        document.getElementById("userName").value = user.name;
        document.getElementById("userEmail").value = user.email;

        document.getElementById("userFormTitle").textContent =
            "Update User";

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });

    } catch (error) {

        showToast(error.message);
    }
}


async function deleteUser(id) {

    const confirmed =
        confirm("Are you sure you want to delete this user?");

    if (!confirmed) {
        return;
    }

    try {

        await getJson(`/api/users/${id}`, {
            method: "DELETE"
        });

        showToast("User deleted successfully.");

        loadUsers();
        loadDashboard();

    } catch (error) {

        showToast(error.message);
    }
}


function clearUserForm() {

    document.getElementById("userId").value = "";
    document.getElementById("userName").value = "";
    document.getElementById("userEmail").value = "";

    document.getElementById("userFormTitle").textContent =
        "Add User";
}


/* =========================
   RECIPE CRUD
========================= */

async function loadRecipes() {

    try {

        const recipes = await getJson("/api/recipes");

        renderRecipes(recipes);

    } catch (error) {

        showToast(error.message);
    }
}


function renderRecipes(recipes) {

    const table = document.getElementById("recipesTable");

    table.innerHTML = "";

    if (recipes.length === 0) {

        table.innerHTML = `
            <tr>
                <td colspan="7">No recipes found.</td>
            </tr>
        `;

        return;
    }

    recipes.forEach(recipe => {

        table.innerHTML += `
            <tr>

                <td>${escapeHtml(recipe.id)}</td>

                <td>${escapeHtml(recipe.name)}</td>

                <td>${escapeHtml(recipe.cuisine)}</td>

                <td>${escapeHtml(recipe.tag)}</td>

                <td>${escapeHtml(recipe.prepTime)} min</td>

                <td>
                    ${recipe.favorite ? "❤️ Yes" : "🤍 No"}
                </td>

                <td>

                    <div class="action-buttons">

                        <button
                            class="primary-btn"
                            onclick="editRecipe(${recipe.id})">
                            Edit
                        </button>

                        <button
                            class="favorite-btn"
                            onclick="toggleFavorite(${recipe.id})">
                            ❤️
                        </button>

                        <button
                            class="danger-btn"
                            onclick="deleteRecipe(${recipe.id})">
                            Delete
                        </button>

                    </div>

                </td>

            </tr>
        `;
    });
}


async function saveRecipe() {

    const id =
        document.getElementById("recipeId").value;

    const name =
        document.getElementById("recipeName").value.trim();

    const description =
        document.getElementById("recipeDescription").value.trim();

    const cuisine =
        document.getElementById("recipeCuisine").value.trim();

    const tag =
        document.getElementById("recipeTag").value.trim();

    const prepTime =
        Number(document.getElementById("recipePrepTime").value);

    const steps =
        document.getElementById("recipeSteps").value.trim();

    const userId =
        Number(document.getElementById("recipeUserId").value);

    if (
        !name ||
        !cuisine ||
        !tag ||
        !prepTime ||
        !steps ||
        !userId
    ) {

        showToast("Please fill all required recipe fields.");
        return;
    }

    const recipeData = {

        name: name,

        description: description,

        cuisine: cuisine,

        tag: tag,

        prepTime: prepTime,

        steps: steps,

        favorite: false,

        userId: userId
    };

    try {

        if (id) {

            await getJson(`/api/recipes/${id}`, {
                method: "PUT",
                body: JSON.stringify(recipeData)
            });

            showToast("Recipe updated successfully.");

        } else {

            await getJson("/api/recipes", {
                method: "POST",
                body: JSON.stringify(recipeData)
            });

            showToast("Recipe created successfully.");
        }

        clearRecipeForm();

        loadRecipes();
        loadDashboard();

    } catch (error) {

        showToast(error.message);
    }
}


async function editRecipe(id) {

    try {

        const recipe =
            await getJson(`/api/recipes/${id}`);

        document.getElementById("recipeId").value =
            recipe.id;

        document.getElementById("recipeName").value =
            recipe.name || "";

        document.getElementById("recipeDescription").value =
            recipe.description || "";

        document.getElementById("recipeCuisine").value =
            recipe.cuisine || "";

        document.getElementById("recipeTag").value =
            recipe.tag || "";

        document.getElementById("recipePrepTime").value =
            recipe.prepTime || "";

        document.getElementById("recipeSteps").value =
            recipe.steps || "";

        if (recipe.user && recipe.user.id) {

            document.getElementById("recipeUserId").value =
                recipe.user.id;
        }

        document.getElementById("recipeFormTitle").textContent =
            "Update Recipe";

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });

    } catch (error) {

        showToast(error.message);
    }
}


async function deleteRecipe(id) {

    const confirmed =
        confirm("Are you sure you want to delete this recipe?");

    if (!confirmed) {
        return;
    }

    try {

        await getJson(`/api/recipes/${id}`, {
            method: "DELETE"
        });

        showToast("Recipe deleted successfully.");

        loadRecipes();
        loadDashboard();

    } catch (error) {

        showToast(error.message);
    }
}


async function toggleFavorite(id) {

    try {

        const recipe =
            await getJson(`/api/recipes/${id}/favorite`, {
                method: "PUT"
            });

        if (recipe.favorite) {

            showToast("Recipe added to favorites ❤️");

        } else {

            showToast("Recipe removed from favorites.");
        }

        loadRecipes();

    } catch (error) {

        showToast(error.message);
    }
}


function clearRecipeForm() {

    document.getElementById("recipeId").value = "";

    document.getElementById("recipeName").value = "";

    document.getElementById("recipeDescription").value = "";

    document.getElementById("recipeCuisine").value = "";

    document.getElementById("recipeTag").value = "";

    document.getElementById("recipePrepTime").value = "";

    document.getElementById("recipeSteps").value = "";

    document.getElementById("recipeUserId").value = "";

    document.getElementById("recipeFormTitle").textContent =
        "Add Recipe";
}


/* =========================
   RECIPE SEARCH
========================= */

async function searchRecipes() {

    const searchText =
        document.getElementById("recipeSearch").value.trim();

    if (!searchText) {

        loadRecipes();
        return;
    }

    try {

        let recipes = [];

        /*
         * Try ingredient search first.
         */

        try {

            recipes =
                await getJson(
                    `/api/recipes/search?ingredient=${encodeURIComponent(searchText)}`
                );

        } catch (error) {

            recipes = [];
        }

        /*
         * If ingredient search has no results,
         * try cuisine.
         */

        if (recipes.length === 0) {

            try {

                recipes =
                    await getJson(
                        `/api/recipes/search?cuisine=${encodeURIComponent(searchText)}`
                    );

            } catch (error) {

                recipes = [];
            }
        }

        /*
         * If cuisine search has no results,
         * try tag.
         */

        if (recipes.length === 0) {

            try {

                recipes =
                    await getJson(
                        `/api/recipes/search?tag=${encodeURIComponent(searchText)}`
                    );

            } catch (error) {

                recipes = [];
            }
        }

        renderRecipes(recipes);

        if (recipes.length === 0) {

            showToast("No recipes found.");
        }

    } catch (error) {

        showToast(error.message);
    }
}


/* =========================
   INGREDIENT CRUD
========================= */

async function loadIngredients() {

    try {

        const ingredients =
            await getJson("/api/ingredients");

        const table =
            document.getElementById("ingredientsTable");

        table.innerHTML = "";

        if (ingredients.length === 0) {

            table.innerHTML = `
                <tr>
                    <td colspan="6">No ingredients found.</td>
                </tr>
            `;

            return;
        }

        ingredients.forEach(ingredient => {

            const recipeId =
                ingredient.recipe
                    ? ingredient.recipe.id
                    : "";

            table.innerHTML += `
                <tr>

                    <td>${escapeHtml(ingredient.id)}</td>

                    <td>${escapeHtml(ingredient.name)}</td>

                    <td>${escapeHtml(ingredient.quantity)}</td>

                    <td>${escapeHtml(ingredient.unit)}</td>

                    <td>${escapeHtml(recipeId)}</td>

                    <td>

                        <div class="action-buttons">

                            <button
                                class="primary-btn"
                                onclick="editIngredient(${ingredient.id})">
                                Edit
                            </button>

                            <button
                                class="danger-btn"
                                onclick="deleteIngredient(${ingredient.id})">
                                Delete
                            </button>

                        </div>

                    </td>

                </tr>
            `;
        });

    } catch (error) {

        showToast(error.message);
    }
}


async function saveIngredient() {

    const id =
        document.getElementById("ingredientId").value;

    const name =
        document.getElementById("ingredientName").value.trim();

    const quantity =
        Number(document.getElementById("ingredientQuantity").value);

    const unit =
        document.getElementById("ingredientUnit").value.trim();

    const recipeId =
        Number(document.getElementById("ingredientRecipeId").value);

    if (!name || !quantity || !unit || !recipeId) {

        showToast("Please fill all ingredient fields.");
        return;
    }

    const ingredientData = {

        name: name,

        quantity: quantity,

        unit: unit,

        recipeId: recipeId
    };

    try {

        if (id) {

            await getJson(`/api/ingredients/${id}`, {
                method: "PUT",
                body: JSON.stringify(ingredientData)
            });

            showToast("Ingredient updated successfully.");

        } else {

            await getJson("/api/ingredients", {
                method: "POST",
                body: JSON.stringify(ingredientData)
            });

            showToast("Ingredient created successfully.");
        }

        clearIngredientForm();

        loadIngredients();
        loadDashboard();

    } catch (error) {

        showToast(error.message);
    }
}


async function editIngredient(id) {

    try {

        const ingredient =
            await getJson(`/api/ingredients/${id}`);

        document.getElementById("ingredientId").value =
            ingredient.id;

        document.getElementById("ingredientName").value =
            ingredient.name || "";

        document.getElementById("ingredientQuantity").value =
            ingredient.quantity || "";

        document.getElementById("ingredientUnit").value =
            ingredient.unit || "";

        if (ingredient.recipe) {

            document.getElementById("ingredientRecipeId").value =
                ingredient.recipe.id;
        }

        document.getElementById("ingredientFormTitle").textContent =
            "Update Ingredient";

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });

    } catch (error) {

        showToast(error.message);
    }
}


async function deleteIngredient(id) {

    const confirmed =
        confirm("Are you sure you want to delete this ingredient?");

    if (!confirmed) {
        return;
    }

    try {

        await getJson(`/api/ingredients/${id}`, {
            method: "DELETE"
        });

        showToast("Ingredient deleted successfully.");

        loadIngredients();
        loadDashboard();

    } catch (error) {

        showToast(error.message);
    }
}


function clearIngredientForm() {

    document.getElementById("ingredientId").value = "";

    document.getElementById("ingredientName").value = "";

    document.getElementById("ingredientQuantity").value = "";

    document.getElementById("ingredientUnit").value = "";

    document.getElementById("ingredientRecipeId").value = "";

    document.getElementById("ingredientFormTitle").textContent =
        "Add Ingredient";
}


/* =========================
   MEAL PLAN CRUD
========================= */

async function loadMealPlans() {

    try {

        const mealPlans =
            await getJson("/api/meal-plans");

        const table =
            document.getElementById("mealPlansTable");

        table.innerHTML = "";

        if (mealPlans.length === 0) {

            table.innerHTML = `
                <tr>
                    <td colspan="6">No meal plans found.</td>
                </tr>
            `;

            return;
        }

        mealPlans.forEach(mealPlan => {

            const recipeName =
                mealPlan.recipe
                    ? mealPlan.recipe.name
                    : "Unknown";

            const userId =
                mealPlan.user
                    ? mealPlan.user.id
                    : "";

            table.innerHTML += `
                <tr>

                    <td>${escapeHtml(mealPlan.id)}</td>

                    <td>${escapeHtml(mealPlan.mealDate)}</td>

                    <td>${escapeHtml(mealPlan.mealType)}</td>

                    <td>${escapeHtml(recipeName)}</td>

                    <td>${escapeHtml(userId)}</td>

                    <td>

                        <div class="action-buttons">

                            <button
                                class="primary-btn"
                                onclick="editMealPlan(${mealPlan.id})">
                                Edit
                            </button>

                            <button
                                class="danger-btn"
                                onclick="deleteMealPlan(${mealPlan.id})">
                                Delete
                            </button>

                        </div>

                    </td>

                </tr>
            `;
        });

    } catch (error) {

        showToast(error.message);
    }
}


async function saveMealPlan() {

    const id =
        document.getElementById("mealPlanId").value;

    const mealDate =
        document.getElementById("mealDate").value;

    const mealType =
        document.getElementById("mealType").value;

    const recipeId =
        Number(document.getElementById("mealRecipeId").value);

    const userId =
        Number(document.getElementById("mealUserId").value);

    if (!mealDate || !mealType || !recipeId || !userId) {

        showToast("Please fill all meal plan fields.");
        return;
    }

    const mealPlanData = {

        mealDate: mealDate,

        mealType: mealType,

        recipeId: recipeId,

        userId: userId
    };

    try {

        if (id) {

            await getJson(`/api/meal-plans/${id}`, {
                method: "PUT",
                body: JSON.stringify(mealPlanData)
            });

            showToast("Meal plan updated successfully.");

        } else {

            await getJson("/api/meal-plans", {
                method: "POST",
                body: JSON.stringify(mealPlanData)
            });

            showToast("Meal plan created successfully.");
        }

        clearMealPlanForm();

        loadMealPlans();
        loadDashboard();

    } catch (error) {

        showToast(error.message);
    }
}


async function editMealPlan(id) {

    try {

        const mealPlan =
            await getJson(`/api/meal-plans/${id}`);

        document.getElementById("mealPlanId").value =
            mealPlan.id;

        document.getElementById("mealDate").value =
            mealPlan.mealDate || "";

        document.getElementById("mealType").value =
            mealPlan.mealType || "";

        if (mealPlan.recipe) {

            document.getElementById("mealRecipeId").value =
                mealPlan.recipe.id;
        }

        if (mealPlan.user) {

            document.getElementById("mealUserId").value =
                mealPlan.user.id;
        }

        document.getElementById("mealPlanFormTitle").textContent =
            "Update Meal Plan";

        window.scrollTo({
            top: 0,
            behavior: "smooth"
        });

    } catch (error) {

        showToast(error.message);
    }
}


async function deleteMealPlan(id) {

    const confirmed =
        confirm("Are you sure you want to delete this meal plan?");

    if (!confirmed) {
        return;
    }

    try {

        await getJson(`/api/meal-plans/${id}`, {
            method: "DELETE"
        });

        showToast("Meal plan deleted successfully.");

        loadMealPlans();
        loadDashboard();

    } catch (error) {

        showToast(error.message);
    }
}


function clearMealPlanForm() {

    document.getElementById("mealPlanId").value = "";

    document.getElementById("mealDate").value = "";

    document.getElementById("mealType").value = "";

    document.getElementById("mealRecipeId").value = "";

    document.getElementById("mealUserId").value = "";

    document.getElementById("mealPlanFormTitle").textContent =
        "Add Meal Plan";
}


/* =========================
   SHOPPING LIST
========================= */

async function generateShoppingList() {

    const userId =
        document.getElementById("shoppingUserId").value;

    const startDate =
        document.getElementById("shoppingStartDate").value;

    const endDate =
        document.getElementById("shoppingEndDate").value;

    if (!userId || !startDate || !endDate) {

        showToast(
            "Please enter user ID, start date and end date."
        );

        return;
    }

    try {

        const list =
            await getJson(
                `/api/shopping-list?userId=${userId}&startDate=${startDate}&endDate=${endDate}`
            );

        const container =
            document.getElementById("shoppingList");

        container.innerHTML = "";

        if (list.length === 0) {

            container.innerHTML = `
                <div class="shopping-item">

                    <h3>No Items</h3>

                    <p>
                        No ingredients found for the selected dates.
                    </p>

                </div>
            `;

            return;
        }

        list.forEach(item => {

            container.innerHTML += `
                <div class="shopping-item">

                    <h3>
                        🥕 ${escapeHtml(item.ingredientName)}
                    </h3>

                    <p>
                        Required Quantity
                    </p>

                    <strong>
                        ${escapeHtml(item.quantity)}
                        ${escapeHtml(item.unit)}
                    </strong>

                </div>
            `;
        });

        showToast("Shopping list generated successfully.");

    } catch (error) {

        showToast(error.message);
    }
}


/* =========================
   INITIAL LOAD
========================= */

document.addEventListener("DOMContentLoaded", () => {

    loadDashboard();

    loadUsers();

    loadRecipes();

    loadIngredients();

    loadMealPlans();

});