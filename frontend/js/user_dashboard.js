// ✅ Get current user from localStorage
const currentUser = JSON.parse(localStorage.getItem("gramrideCurrentUser"));

// 🚫 Redirect if not logged in or not a user
if (!currentUser || currentUser.role !== "user") {
  alert("Unauthorized access! Please log in.");
  window.location.href = "login.html";
} else {
  // ✅ Set username safely
  document.getElementById("username").textContent = currentUser.fullName || "User";
}

const userId = currentUser.id;

// 🛺 Load Ride History
function loadRideHistory() {
  fetch(`http://localhost:8080/api/rides/user/${userId}`)
    .then((response) => response.json())
    .then((rides) => {
      const historySection = document.getElementById("history");
      const historyHTML =
        rides.length > 0
          ? `<ul class="divide-y divide-gray-300 text-left">
              ${rides
                .map(
                  (ride) => `
                <li class="py-2">
                  <strong>From:</strong> ${ride.pickupLocation}<br>
                  <strong>To:</strong> ${ride.dropLocation}<br>
                  <strong>Vehicle:</strong> ${ride.vehicleType}<br>
                  <strong>Fare:</strong> ₹${ride.fare}<br>
                  <strong>Status:</strong> ${ride.status}
                </li>
              `
                )
                .join("")}
            </ul>`
          : `<p class="text-gray-600">No rides booked yet. Start booking now!</p>`;

      historySection.innerHTML = `
        <h3 class="text-xl font-semibold text-green-600 mb-4">Your Ride History</h3>
        ${historyHTML}
      `;
    })
    .catch((err) => {
      console.error("Failed to load ride history", err);
      document.getElementById("history").innerHTML =
        `<p class="text-red-600">Failed to load rides</p>`;
    });
}

loadRideHistory();

// 🔓 Logout
function logout() {
  localStorage.removeItem("gramrideCurrentUser");
  window.location.href = "login.html";
}

