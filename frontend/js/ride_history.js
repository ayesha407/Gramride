const currentUserRaw = localStorage.getItem("gramrideCurrentUser");
let currentUser;

try {
  currentUser = JSON.parse(currentUserRaw);
  if (!currentUser || !currentUser.id) throw new Error();
} catch {
  alert("User session expired. Please login again.");
  window.location.href = "login.html";
}

function loadRideHistory() {
  fetch(`http://localhost:8080/api/rides/user/${currentUser.id}`)
    .then(res => res.json())
    .then(rides => {
      const tbody = document.getElementById("rideHistoryTableBody");
      tbody.innerHTML = ""; // Clear table

      if (rides.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center text-gray-600 py-4">No rides found.</td></tr>`;
        return;
      }

      rides.forEach(ride => {
        const row = document.createElement("tr");

        row.innerHTML = `
          <td class="p-2 border">${ride.id}</td>
          <td class="p-2 border">${ride.pickupLocation}</td>
          <td class="p-2 border">${ride.dropLocation}</td>
          <td class="p-2 border">₹${ride.fare}</td>
          <td class="p-2 border">
            ${ride.vehicleType}<br/>
            <span class="text-xs font-semibold ${getStatusStyle(ride.status)}">${ride.status}</span>
            ${ride.status === "BOOKED" ? `<br/><button onclick="cancelRide(${ride.id})" class="mt-1 bg-red-600 text-white px-2 py-0.5 rounded text-xs hover:bg-red-700">Cancel</button>` : ""}
          </td>
        `;

        tbody.appendChild(row);
      });
    })
    .catch(() => {
      const tbody = document.getElementById("rideHistoryTableBody");
      tbody.innerHTML = `<tr><td colspan="5" class="text-center text-red-500 py-4">Failed to load ride history.</td></tr>`;
    });
}

function getStatusStyle(status) {
  switch (status) {
    case "BOOKED":
      return "text-yellow-700";
    case "COMPLETED":
      return "text-green-700";
    case "CANCELLED":
      return "text-red-700";
    default:
      return "text-gray-600";
  }
}

function cancelRide(rideId) {
  fetch(`http://localhost:8080/api/rides/${rideId}/status`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ status: "CANCELLED" })
  })
  .then(res => {
    if (res.ok) {
      alert("Ride cancelled successfully!");
      loadRideHistory(); // Refresh
    } else {
      alert("Failed to cancel ride.");
    }
  });
}

function logout() {
  localStorage.removeItem("gramrideCurrentUser");
  window.location.href = "login.html";
}

// Initial load
document.addEventListener("DOMContentLoaded", () => {
  loadRideHistory();
  setInterval(loadRideHistory, 10000);
});

