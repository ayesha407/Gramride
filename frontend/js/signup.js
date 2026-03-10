document.getElementById("signupForm").addEventListener("submit", function (e) {
  e.preventDefault();

  const fullName = document.getElementById("fullname").value.trim();
  const phone = document.getElementById("phone").value.trim();
  const email = document.getElementById("email").value.trim();
  const password = document.getElementById("password").value.trim();
  const role = document.getElementById("role").value.toUpperCase(); // Spring expects "USER", "DRIVER"

  const newUser = { fullName, phone, email, password, role };

 fetch("http://localhost:8080/api/users/signup", {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    body: JSON.stringify(newUser)
  })
  .then(response => {
    if (!response.ok) {
      return response.text().then(text => { throw new Error(text); });
    }
    return response.json();
  })
  .then(data => {
    alert("Signup successful!");
    window.location.href = "login.html";
  })
  .catch(error => {
    alert("Signup failed: " + error.message);
    console.error("Signup error:", error);
  });
});

