let selectedVehicle = null;

// Voice input using Web Speech API
function startVoice(field) {
  const recognition = new (window.SpeechRecognition || window.webkitSpeechRecognition)();
  recognition.lang = 'en-IN';

  recognition.start();

  recognition.onresult = function (event) {
    const result = event.results[0][0].transcript;
    document.getElementById(field).value = result;
  };

  recognition.onerror = function () {
    alert("🎤 Voice recognition failed. Please try again.");
  };
}

// Vehicle selection logic
window.addEventListener("DOMContentLoaded", () => {
  document.querySelectorAll(".vehicle-option").forEach((div) => {
    div.addEventListener("click", () => {
      document.querySelectorAll(".vehicle-option").forEach((d) =>
        d.classList.remove("bg-green-300")
      );
      div.classList.add("bg-green-300");
      selectedVehicle = div.dataset.vehicle;
    });
  });
});

// Book a Ride (with category)
async function bookRide() {
  const pickupInput = document.getElementById('pickup').value.trim();
  const dropInput = document.getElementById('drop').value.trim();
  const vehicle = selectedVehicle;
  const category = document.getElementById('rideCategory')?.value.trim();

  if (!pickupInput || !dropInput || !vehicle || !category) {
    alert("❗ Please fill all fields and select a vehicle and ride category.");
    return;
  }

  const currentUser = JSON.parse(localStorage.getItem("gramrideCurrentUser"));
  if (!currentUser) {
    alert("⚠️ Session expired. Please login again.");
    window.location.href = "login.html";
    return;
  }

  try {
    const rideRequest = {
      pickupLocation: pickupInput,
      dropLocation: dropInput,
      vehicleType: vehicle,
      rideCategory: category,
      userId: currentUser.id
    };

    const response = await fetch("http://localhost:8080/api/rides/book", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(rideRequest)
    });

    if (!response.ok) {
      throw new Error("Failed to book ride");
    }

    const ride = await response.json();
    localStorage.setItem("latestRide", JSON.stringify(ride));
    window.location.href = "ride_success.html";

  } catch (err) {
    console.error("Booking failed:", err);
    alert("❌ Ride booking failed. Please try again.");
  }
}

// Logout function
function logout() {
  localStorage.removeItem("gramrideCurrentUser");
  window.location.href = "login.html";
}

