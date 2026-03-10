document.getElementById("loginForm").addEventListener("submit", function (e) {
  e.preventDefault();

  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value.trim();
  const roleInput = document.getElementById("role").value.trim().toUpperCase();

  if (!email || !password || !roleInput) {
    alert("Please fill in all fields.");
    return;
  }

  // 🔐 Admin login (handled on frontend only)
  if (roleInput === "ADMIN") {
    if (email === "admin@gramride.com" && password === "Admin@492") {
      const adminUser = {
        fullName: "Admin",
        email,
        role: "ADMIN",
      };
      localStorage.setItem("gramrideCurrentUser", JSON.stringify(adminUser));
      alert("✅ Admin login successful!");
      window.location.href = "admin_dashboard.html";
    } else {
      alert("❌ Invalid admin credentials!");
    }
    return;
  }

  // 🔐 User/Driver login via backend
  const loginData = {
    email,
    password,
    role: roleInput,
  };

  fetch("http://localhost:8080/api/users/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(loginData),
  })
    .then((response) => {
      if (!response.ok) {
        return response.text().then((text) => {
          throw new Error(text);
        });
      }
      return response.json();
    })
    .then((user) => {
      const userToStore = {
        id: user.id,
        fullName: user.fullName,
        email: user.email,
        role: user.role, // ✅ Store role exactly as backend returns (e.g., "DRIVER")
      };
      localStorage.setItem("gramrideCurrentUser", JSON.stringify(userToStore));

      alert("✅ Login successful!");

      if (userToStore.role === "USER") {
        window.location.href = "user_dashboard.html";
      } else if (userToStore.role === "DRIVER") {
        window.location.href = "driver_dashboard.html";
      } else {
        alert("❌ Unknown role. Please try again.");
      }
    })
    .catch((error) => {
      alert("❌ Login failed: " + error.message);
      console.error("Login error:", error);
    });
});
