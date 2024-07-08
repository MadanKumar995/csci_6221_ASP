
const csrfToken = document.getElementById("csrfToken").value;
const validateRoute = document.getElementById("validateRoute").value;


function login() {
    const username = document.getElementById("loginName").value;
    const password = document.getElementById("loginPass").value;
    fetch(validateRoute, {
        method: "POST",
        headers: {'Content-Type': 'application/json', 'Csrf-Token': csrfToken},
        body: JSON.stringify({username, password})
    }).then(res => res.json()).then(data => {
        console.log(data);
    });
}