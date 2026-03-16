async function login(event) {

    event.preventDefault()

    const mail = document.getElementById("mail").value;
    const password = document.getElementById("password").value;

    const response = await fetch("/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            mail: mail,
            password: password
        })
    });

    if(response.ok){
        window.location.href = "/dashboard.html";
    } else {
        alert("Forkert login");
    }
}