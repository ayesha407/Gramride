const currentDriver = JSON.parse(localStorage.getItem("gramrideCurrentUser") || "{}");

if (!currentDriver || !currentDriver.id || currentDriver.role !== "DRIVER") {
  alert("⚠️ Please login as a driver.");
  window.location.href = "login.html";
}

function loadAssignedRides() {
  fetch(`http://localhost:8080/api/rides/driver/${currentDriver.id}`)
    .then(res => res.json())
    .then(rides => {
      const tbody = document.getElementById("driverRides");
      tbody.innerHTML = "";

      if (!rides || rides.length === 0) {
        tbody.innerHTML = "<tr><td colspan='5' class='text-center p-4 text-gray-500'>No assigned rides</td></tr>";
        return;
      }

      rides.forEach(ride => {
        const row = document.createElement("tr");
        row.innerHTML = `
          <td class="p-2 border">${ride.pickupLocation}</td>
          <td class="p-2 border">${ride.dropLocation}</td>
          <td class="p-2 border">₹${ride.fare}</td>
          <td class="p-2 border">${ride.status}</td>
          <td class="p-2 border">
            ${ride.status === "CONFIRMED" 
              ? `<button onclick="updateRideStatus(${ride.id}, 'ONGOING')" class="bg-blue-600 text-white px-2 py-1 rounded hover:bg-blue-700">Start</button>`
              : ride.status === "ONGOING"
              ? `<button onclick="updateRideStatus(${ride.id}, 'COMPLETED')" class="bg-green-600 text-white px-2 py-1 rounded hover:bg-green-700">Complete</button>`
              : `<span class="text-green-600 font-bold">✔ Done</span>`}

          </td>
        `;
        tbody.appendChild(row);
      });
    })
    .catch(err => {
      console.error("❌ Failed to fetch driver rides:", err);
      alert("Error loading assigned rides. Please try again later.");
    });
}

function updateRideStatus(rideId, status) {
  fetch(`http://localhost:8080/api/rides/${rideId}/status`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ status })
  })
  .then(res => {
    if (!res.ok) throw new Error("Status update failed");
    return res.text();
  })
  .then(() => loadAssignedRides())
  .catch(err => {
    console.error("❌ Error updating ride status:", err);
    alert("Failed to update ride status.");
  });
}

document.addEventListener("DOMContentLoaded", loadAssignedRides);
