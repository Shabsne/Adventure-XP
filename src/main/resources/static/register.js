async function register(event){

    event.preventDefault();

    const name = document.getElementById("name").value;
    const mail = document.getElementById("mail").value;
    const password = document.getElementById("password").value;
    const birthDate = document.getElementById("birthDate").value;

    const response = await fetch("/register", {
        method: "POST",
        headers:{
            "Content-Type":"application/json"
        },
        body: JSON.stringify({
            name: name,
            mail: mail,
            password: password,
            birthDate: birthDate,
            role: "Custommer"
        })
    });

    const message = await response.text();

    if(response.ok){
        document.getElementById("message").innerText = "Bruger oprettet!";
        window.location.href="/login.html";
    }else if(response.status === 409){
        document.getElementById("message").innerText = message;
    }

    else{
        document.getElementById("message").innerText = "Noget gik galt";
    }
}