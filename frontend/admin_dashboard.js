function logout() {
  localStorage.removeItem("gramrideCurrentUser");
  window.location.href = "login.html";
}

function showSection(sectionId, event) {
  document.querySelectorAll(".tab-section").forEach(sec => sec.classList.add("hidden"));
  document.querySelectorAll(".tab-btn").forEach(btn => btn.classList.remove("bg-green-600", "text-white"));
  document.getElementById(sectionId).classList.remove("hidden");
  event.target.classList.add("bg-green-600", "text-white");
}

// Load users
function filterUsersByRole(role) {
  let url = "http://localhost:8080/api/users/all";
  if (role !== "all") {
    url = `http://localhost:8080/api/users/by-role/${role}`;
  }

  fetch(url)
    .then(res => res.json())
    .then(users => {
      const tbody = document.getElementById("userTableBody");
      tbody.innerHTML = "";
      users.forEach(user => {
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

// Load all users on first load
filterUsersByRole("all");

// Load rides
fetch("http://localhost:8080/api/rides/all")
  .then(res => res.json())
  .then(rides => {
    const tbody = document.getElementById("rideTableBody");
    rides.forEach(ride => {
      const row = `<tr>
        <td class="p-2 border">${ride.id}</td>
        <td class="p-2 border">${ride.user?.fullName || 'N/A'}</td>
        <td class="p-2 border">${ride.pickupLocation}</td>
        <td class="p-2 border">${ride.dropLocation}</td>
        <td class="p-2 border">₹${ride.fare}</td>
        <td class="p-2 border">${ride.vehicleType}</td>
      </tr>`;
      tbody.innerHTML += row;
    });
  });

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

function addDriver() {
  const name = document.getElementById("driverName").value.trim();
  const email = document.getElementById("driverEmail").value.trim();
  const password = document.getElementById("driverPassword").value.trim();

  if (!name || !email || !password) {
    alert("Please fill all fields.");
    return;
  }

  fetch("http://localhost:8080/api/users/addDriver", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      fullName: name,
      email: email,
      password: password,
      role: "DRIVER",
      status: "ACTIVE"
    })
  })
    .then(res => res.json())
    .then(() => {
      alert("Driver added!");
      document.getElementById("driverName").value = "";
      document.getElementById("driverEmail").value = "";
      document.getElementById("driverPassword").value = "";
      loadDrivers();
    });
}

function suspendDriver(driverId) {
  fetch(`http://localhost:8080/api/users/suspend/${driverId}`, { method: "PUT" })
    .then(() => {
      alert("Driver suspended.");
      loadDrivers();
    });
}

// Load feedbacks
function loadFeedbacks() {
  fetch("http://localhost:8080/api/feedback")
    .then(res => res.json())
    .then(feedbacks => {
      const tbody = document.getElementById("feedbackTableBody");
      tbody.innerHTML = "";
      feedbacks.forEach(fb => {
        const stars = "★".repeat(fb.rating) + "☆".repeat(5 - fb.rating);
        const row = `
          <tr>
            <td class="p-2 border">${fb.rideId}</td>
            <td class="p-2 border">${fb.driverName || "N/A"}</td>
            <td class="p-2 border">${fb.feedbackType}</td>
            <td class="p-2 border">${fb.comments}</td>
            <td class="p-2 border text-yellow-500">${stars}</td>
          </tr>`;
        tbody.innerHTML += row;
      });
    });
}

// Initialize drivers and feedback on load
if (window.location.href.includes("admin_dashboard")) {
  loadDrivers();
  loadFeedbacks();
}
