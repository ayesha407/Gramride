function logout() {
  localStorage.removeItem("gramrideCurrentUser");
  window.location.href = "login.html";
}

function showSection(sectionId, event) {
  document.querySelectorAll(".tab-section").forEach(sec => sec.classList.add("hidden"));
  document.querySelectorAll(".tab-btn").forEach(btn => btn.classList.remove("bg-green-600", "text-white"));

  document.getElementById(sectionId).classList.remove("hidden");
  event.target.classList.add("bg-green-600", "text-white");

  if (sectionId === "drivers") loadDrivers();
  if (sectionId === "users") filterUsersByRole("all");
  if (sectionId === "feedback") loadFeedbacks();
}

// Load users
function filterUsersByRole(role) {
  fetch("http://localhost:8080/api/users/all")
    .then(res => res.json())
    .then(users => {
      const tbody = document.getElementById("userTableBody");
      tbody.innerHTML = "";

      users
        .filter(user => role === "all" || user.role === "USER")
        .forEach(user => {
          const row = `<tr>
            <td class="p-2 border">${user.id}</td>
            <td class="p-2 border">${user.fullName}</td>
            <td class="p-2 border">${user.email}</td>
            <td class="p-2 border">${user.role}</td>
          </tr>`;
          tbody.innerHTML += row;
        });
    });
}

// Load rides
function loadRides() {
  fetch("http://localhost:8080/api/rides")
    .then(res => {
      if (!res.ok) throw new Error("Failed to fetch rides");
      return res.json();
    })
    .then(rides => {
      const tbody = document.getElementById("rideTableBody");
      tbody.innerHTML = "";

      rides.forEach(ride => {
        const row = `
          <tr>
            <td class="p-2 border">${ride.id}</td>
            <td class="p-2 border">${ride.user?.fullName || 'N/A'}</td>
            <td class="p-2 border">${ride.pickupLocation}</td>
            <td class="p-2 border">${ride.dropLocation}</td>
            <td class="p-2 border">₹${ride.fare}</td>
            <td class="p-2 border">${ride.vehicleType}</td>
            <td class="p-2 border">${ride.rideCategory || 'N/A'}</td>

          </tr>`;
        tbody.innerHTML += row;
      });
    })
    .catch(error => {
      console.error("Failed to load rides:", error);
      document.getElementById("rideTableBody").innerHTML =
        `<tr><td colspan="6" class="p-4 text-red-600 text-center">Unable to load rides</td></tr>`;
    });
}

// Load drivers
function loadDrivers() {
  fetch("http://localhost:8080/api/users/drivers")
    .then(res => res.json())
    .then(drivers => {
      const tbody = document.getElementById("driverTableBody");
      tbody.innerHTML = "";

      drivers.forEach(driver => {
        const row = `
          <tr>
            <td class="p-2 border">${driver.id}</td>
            <td class="p-2 border">${driver.fullName}</td>
            <td class="p-2 border">${driver.email}</td>
            <td class="p-2 border">${driver.status}</td>
            <td class="p-2 border">
              <button onclick="suspendDriver(${driver.id})" class="text-red-600 underline">Suspend</button>
            </td>
          </tr>`;
        tbody.innerHTML += row;
      });
    });
}

// Add driver
function addDriver() {
  const name = document.getElementById("driverName").value.trim();
  const email = document.getElementById("driverEmail").value.trim();
  const phone = document.getElementById("driverPhone").value.trim();
  const password = document.getElementById("driverPassword").value.trim();

  if (!name || !email || !phone || !password) {
    alert("Please fill in all fields.");
    return;
  }

  fetch("http://localhost:8080/api/users/addDriver", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      fullName: name,
      email: email,
      phone: phone,
      password: password,
      role: "DRIVER",
      status: "ACTIVE"
    })
  })
    .then(res => {
      if (!res.ok) {
        return res.text().then(msg => { throw new Error(msg); });
      }
      return res.json();
    })
    .then(() => {
      alert(`✅ Driver added successfully!\n📧 ${email}\n📱 ${phone}\n🔑 ${password}`);
      loadDrivers();
      document.getElementById("driverName").value = "";
      document.getElementById("driverEmail").value = "";
      document.getElementById("driverPhone").value = "";
      document.getElementById("driverPassword").value = "";
    })
    .catch(err => {
      alert("❌ Failed to add driver: " + err.message);
    });
}

// Suspend driver
function suspendDriver(driverId) {
  fetch(`http://localhost:8080/api/users/suspend/${driverId}`, {
    method: "PUT"
  }).then(() => {
    alert("Driver suspended.");
    loadDrivers();
  });
}

// Load feedback with rating
function loadFeedbacks() {
  fetch("http://localhost:8080/api/feedback")
    .then(res => res.json())
    .then(feedbacks => {
      const tbody = document.getElementById("feedbackTableBody");
      tbody.innerHTML = "";

      feedbacks.forEach(fb => {
        const rideId = fb.ride?.id || "N/A";
        const driverName = fb.ride?.driver?.fullName || "N/A";
        const feedbackType = fb.feedbackType || "N/A";
        const comments = fb.comments || "-";
        const rating = fb.rating || 0;

        const row = `<tr class="hover:bg-gray-50">
          <td class="p-2 border">#${rideId}</td>
          <td class="p-2 border">${driverName}</td>
          <td class="p-2 border text-red-600 font-semibold">${feedbackType}</td>
          <td class="p-2 border">${comments}</td>
          <td class="p-2 border">${getStarsHTML(rating)}</td>
        </tr>`;
        tbody.innerHTML += row;
      });
    })
    .catch(err => {
      console.error("Failed to load feedback:", err);
    });
}

// Convert rating to stars
function getStarsHTML(rating) {
  let stars = "";
  for (let i = 1; i <= 5; i++) {
    stars += i <= rating ? "★" : "☆";
  }
  return `<span class="text-yellow-500">${stars}</span>`;
}

// Initial load
document.addEventListener("DOMContentLoaded", () => {
  filterUsersByRole("all");
  loadRides();
});

