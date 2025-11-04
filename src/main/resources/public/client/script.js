// --- Element References ---
const itemListEl = document.getElementById("itemList");
const searchInputEl = document.getElementById("searchInput");
const cartItemsEl = document.getElementById("cartItems");
const cartTotalEl = document.getElementById("cartTotal");
const purchaseButtonEl = document.getElementById("purchaseButton");
const purchaseStatusEl = document.getElementById("purchaseStatus");
const statusEl = document.getElementById("connection-status");

let cart = [];

const ws = new WebSocket(`ws://${window.location.host}/price-updates`);

ws.onopen = () => {
    console.log("WebSocket connected!");
    statusEl.textContent = "🟢 Real-time prices connected";
    statusEl.style.color = "green";
};

ws.onclose = () => {
    console.log("WebSocket disconnected.");
    statusEl.textContent = "🔴 Real-time prices disconnected";
    statusEl.style.color = "red";
};

ws.onerror = (err) => {
    console.error("WebSocket error:", err);
    statusEl.textContent = "🔴 WebSocket Error";
    statusEl.style.color = "red";
};

ws.onmessage = (event) => {
    console.log("Received update:", event.data);
    const updatedItem = JSON.parse(event.data);

    const priceEl = document.getElementById(`price-${updatedItem.id}`);
    
    if (priceEl) {
        priceEl.textContent = updatedItem.price.toFixed(2);
        
        // Also update the 'data-price' on the button
        const buttonEl = priceEl.closest(".item").querySelector(".add-to-cart");
        if (buttonEl) {
            buttonEl.dataset.price = updatedItem.price;
        }
        
        // Add a visual highlight
        const itemEl = priceEl.closest(".item");
        itemEl.classList.add("highlight");
        setTimeout(() => {
            itemEl.classList.remove("highlight");
        }, 1000);
    }
    
    // Check if the item is in the cart and update its price there too
    updateCartPrice(updatedItem.id, updatedItem.price);
};

// ===================================================
// 2. SEARCH / FILTERING
// ===================================================

searchInputEl.addEventListener("input", (e) => {
    const searchTerm = e.target.value;
    // We use your API to fetch filtered items
    fetch(`/api/v1/items?name=${encodeURIComponent(searchTerm)}`)
        .then(response => response.json())
        .then(items => {
            renderItems(items);
        })
        .catch(err => console.error("Error searching:", err));
});

// Helper function to re-render the item list
function renderItems(items) {
    itemListEl.innerHTML = ""; // Clear the list
    if (items.length === 0) {
        itemListEl.innerHTML = "<p>No items found.</p>";
        return;
    }
    
    items.forEach(item => {
        // Re-creating the HTML that Mustache made
        const itemHTML = `
            <div class="item" data-id="${item.id}">
                <h3>${item.name}</h3>
                <p>Category: ${item.category}</p>
                <p>Price: $<span class="price" id="price-${item.id}">${item.price.toFixed(2)}</span></p>
                <button class="add-to-cart" 
                        data-id="${item.id}" 
                        data-name="${item.name}" 
                        data-price="${item.price}">Add to Cart</button>
            </div>
        `;
        itemListEl.insertAdjacentHTML("beforeend", itemHTML);
    });
}

// ===================================================
// 3. SHOPPING CART LOGIC
// ===================================================

// Use event delegation for "Add to Cart" buttons
itemListEl.addEventListener("click", (e) => {
    if (e.target.classList.contains("add-to-cart")) {
        const button = e.target;
        const id = button.dataset.id;
        const name = button.dataset.name;
        // Parse price as a float
        const price = parseFloat(button.dataset.price); 
        
        addToCart(id, name, price);
    }
});

function addToCart(id, name, price) {
    // Check if item is already in cart
    const existingItem = cart.find(item => item.id === id);
    
    if (existingItem) {
        existingItem.quantity++;
    } else {
        cart.push({ id, name, price, quantity: 1 });
    }
    
    console.log("Cart updated:", cart);
    renderCart();
}

function updateCartPrice(itemId, newPrice) {
    const cartItem = cart.find(item => item.id === itemId);
    if (cartItem) {
        cartItem.price = newPrice;
        renderCart(); // Re-render to show new total
    }
}

function renderCart() {
    cartItemsEl.innerHTML = ""; // Clear cart list
    let total = 0;
    
    if (cart.length === 0) {
        cartItemsEl.innerHTML = "<li>Cart is empty.</li>";
        cartTotalEl.textContent = "0.00";
        return;
    }

    cart.forEach(item => {
        const itemTotal = item.price * item.quantity;
        total += itemTotal;
        
        const cartItemHTML = `
            <li>
                ${item.name} (x${item.quantity})
                <br>
                <strong>$${itemTotal.toFixed(2)}</strong>
            </li>
        `;
        cartItemsEl.insertAdjacentHTML("beforeend", cartItemHTML);
    });
    
    cartTotalEl.textContent = total.toFixed(2);
}

// ===================================================
// 4. CREATE PURCHASE LOGIC
// ===================================================

purchaseButtonEl.addEventListener("click", () => {
    if (cart.length === 0) {
        purchaseStatusEl.textContent = "Cart is empty.";
        return;
    }

    purchaseStatusEl.textContent = "Processing purchase...";
    
    // Send the cart array to a new API endpoint
    // NOTE: You must create this /api/v1/purchase endpoint!
    fetch("/api/v1/purchase", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(cart) 
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Purchase failed");
        }
        return response.json();
    })
    .then(data => {
        purchaseStatusEl.textContent = `Purchase successful! Order ID: ${data.orderId}`;
        cart = []; // Clear the cart
        renderCart(); // Re-render the empty cart
    })
    .catch(err => {
        purchaseStatusEl.textContent = "Purchase failed. Please try again.";
        console.error("Purchase error:", err);
    });
});