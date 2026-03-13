async function loadRoles(){

    const response = await fetch("/roles");
    const roles = await response.json();

    const roleSelect = document.getElementById("role");

    roles.forEach(role => {

        const option = document.createElement("option");
        option.value = role;
        option.text = role;

        roleSelect.appendChild(option);

    });
}

async function checkUser(){

    const response = await fetch("/me");

    if(!response.ok) return;

    const user = await response.json();

    if(user.role === "Admin"){

        document.getElementById("role").style.display = "block";
        loadRoles();
    }
}

checkUser();

async function register(){

    event.preventDefault();

    const roleElement = document.getElementById("role");

    let role = null;

    if(roleElement && roleElement.style.display !== "none"){
        role = roleElement.value;
    }

    const user = {
        name: document.getElementById("name").value,
        mail: document.getElementById("mail").value,
        password: document.getElementById("password").value,
        birthDate: document.getElementById("birthDate").value,
        role: role
    };

    const response = await fetch("/register", {
        method:"POST",
        headers:{
            "Content-Type":"application/json"
        },
        body: JSON.stringify(user)
    });

    if(response.ok){

        const me = await fetch("/me");

        if(me.ok){
            const currentUser = await me.json();

            if(currentUser.role === "Admin"){
                window.location.href = "/dashboard.html";
                return;
            }
        }

        window.location.href = "/login.html";

    } else {

        const text = await response.text();
        document.getElementById("message").innerText = text;

    }
}

