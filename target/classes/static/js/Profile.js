document.getElementById("loginForm").addEventListener("submit", loginUser);

function loginUser(event) {

    event.preventDefault();

    const mail = document.getElementById("mail").value;
    const password = document.getElementById("password").value;

    fetch("/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        credentials: "include",
        body: JSON.stringify({
            mail: mail,
            password: password
        })
    })
        .then(response => response.text())
        .then(data => {

            if (data === "success") {
                window.location.href = "/dashboard.html";
            } else {
                document.getElementById("error").innerText =
                    "Forkert login";
            }

        });
}